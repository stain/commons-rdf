/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.sesame;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.simple.AbstractRDFParserBuilder;
import org.openrdf.model.Model;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.repository.util.RDFLoader;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.AbstractRDFHandler;

/**
 * RDF4J-based parser.
 * <p>
 * This can handle the RDF syntaxes {@link RDFSyntax#JSONLD},
 * {@link RDFSyntax#NQUADS}, {@link RDFSyntax#NTRIPLES},
 * {@link RDFSyntax#RDFXML}, {@link RDFSyntax#TRIG} and {@link RDFSyntax#TURTLE}
 * - additional syntaxes can be supported by including the corresponding
 * <em>sesame-rio-*</em> module on the classpath.
 *
 */
public class RDF4JParserBuilder extends AbstractRDFParserBuilder<RDF4JParserBuilder> implements RDFParserBuilder {

	private final class AddToQuadConsumer extends AbstractRDFHandler {
		private final Consumer<Quad> quadTarget;

		private AddToQuadConsumer(Consumer<Quad> quadTarget) {
			this.quadTarget = quadTarget;
		}

		public void handleStatement(org.openrdf.model.Statement st)
				throws org.openrdf.rio.RDFHandlerException {
			// TODO: if getRdfTermFactory() is a non-sesame factory, should
			// we use factory.createQuad() instead?
			// Unsure what is the promise of setting getRdfTermFactory() --
			// does it go all the way down to creating BlankNode, IRI and
			// Literal?
			quadTarget.accept(sesameTermFactory.asQuad(st));
			// Performance note:
			// Graph/Quad.add should pick up again our
			// RDF4JGraphLike.asStatement()
			// and avoid double conversion.
			// Additionally the RDF4JQuad and RDF4JTriple implementations
			// are lazily converting subj/obj/pred/graph.s
		}
	}

	private final static class AddToModel extends AbstractRDFHandler {
		private final Model model;

		public AddToModel(Model model) {
			this.model = model;
		}

		public void handleStatement(org.openrdf.model.Statement st)
				throws org.openrdf.rio.RDFHandlerException {
			model.add(st);
		}

		@Override
		public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
			model.setNamespace(prefix, uri);
		}
	}

	private RDF4JTermFactory sesameTermFactory;

	@Override
	protected RDF4JTermFactory createRDFTermFactory() {
		return new RDF4JTermFactory();
	}

	@Override
	protected RDF4JParserBuilder prepareForParsing() throws IOException, IllegalStateException {
		RDF4JParserBuilder c = prepareForParsing();
		// Ensure we have an RDF4JTermFactory for conversion.
		// We'll make a new one if user has provided a non-RDF4J factory
		c.sesameTermFactory = (RDF4JTermFactory) getRdfTermFactory().filter(RDF4JTermFactory.class::isInstance)
				.orElseGet(c::createRDFTermFactory);
		return c;
	}

	@Override
	protected void parseSynchronusly() throws IOException {		
		Optional<RDFFormat> formatByMimeType = getContentType().flatMap(Rio::getParserFormatForMIMEType);
		String base = getBase().map(IRI::getIRIString).orElse(null);
				
		ParserConfig parserConfig = new ParserConfig();
		// TODO: Should we need to set anything?
		RDFLoader loader = new RDFLoader(parserConfig, sesameTermFactory.getValueFactory());
		RDFHandler rdfHandler = makeRDFHandler();		
		if (getSourceFile().isPresent()) {			
			// NOTE: While we could have used  
			// loader.load(sourcePath.toFile()
			// if the path fs provider == FileSystems.getDefault(), 			
			// that RDFLoader method does not use absolute path
			// as the base URI, so to be consistent 
			// we'll always do it with our own input stream
			//
			// That means we may have to guess format by extensions:			
			Optional<RDFFormat> formatByFilename = getSourceFile().map(Path::getFileName).map(Path::toString)
					.flatMap(Rio::getParserFormatForFileName);
			// TODO: for the excited.. what about the extension after following symlinks? 
			
			RDFFormat format = formatByMimeType.orElse(formatByFilename.orElse(null));
			try (InputStream in = Files.newInputStream(getSourceFile().get())) {
				loader.load(in, base, format, rdfHandler);
			}
		} else if (getSourceIri().isPresent()) {
			try {
				// TODO: Handle international IRIs properly
				// (Unicode support for for hostname, path and query)
				URL url = new URL(getSourceIri().get().getIRIString());
				// TODO: This probably does not support https:// -> http:// redirections
				loader.load(url, base, formatByMimeType.orElse(null), makeRDFHandler());
			} catch (MalformedURLException ex) {
				throw new IOException("Can't handle source URL: " + getSourceIri().get(), ex);
			}			
		}
		// must be getSourceInputStream then, this is guaranteed by super.checkSource(); 		
		loader.load(getSourceInputStream().get(), base, formatByMimeType.orElse(null), rdfHandler);
	}

	protected RDFHandler makeRDFHandler() {

		// TODO: Can we join the below DF4JDataset and RDF4JGraph cases
		// using RDF4JGraphLike<TripleLike<BlankNodeOrIRI,IRI,RDFTerm>>
		// or will that need tricky generics types?

		if (getTargetDataset().filter(RDF4JDataset.class::isInstance).isPresent()) {
			// One of us, we can add them as Statements directly
			RDF4JDataset dataset = (RDF4JDataset) getTargetDataset().get();
			if (dataset.asRepository().isPresent()) {
				return new RDFInserter(dataset.asRepository().get().getConnection());
			}
			if (dataset.asModel().isPresent()) {
				Model model = dataset.asModel().get();
				return new AddToModel(model);
			}
			// Not backed by Repository or Model?
			// Third-party RDF4JDataset subclass, so we'll fall through to the
			// getTarget() handling further down
		} else if (getTargetGraph().filter(RDF4JGraph.class::isInstance).isPresent()) {
			RDF4JGraph graph = (RDF4JGraph) getTargetGraph().get();

			if (graph.asRepository().isPresent()) {
				RDFInserter inserter = new RDFInserter(graph.asRepository().get().getConnection());
				graph.getContextFilter().ifPresent(inserter::enforceContext);
				return inserter;
			}
			if (graph.asModel().isPresent() && graph.getContextFilter().isPresent()) {
				Model model = graph.asModel().get();
				return new AddToModel(model);
			}
			// else - fall through
		}

		// Fall thorough: let target() consume our converted quads.
		return new AddToQuadConsumer(getTarget());
	}

}
