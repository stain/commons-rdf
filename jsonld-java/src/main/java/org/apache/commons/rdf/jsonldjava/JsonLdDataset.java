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
package org.apache.commons.rdf.jsonldjava;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
//import org.apache.commons.rdf.api.Quad;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;

public class JsonLdDataset implements Dataset {
	
	/** 
	 * Used by {@link #bnodePrefix()} to get a unique UUID per JVM run
	 */
	private static UUID SALT = UUID.randomUUID();
	
	/**
	 * The underlying JSON-LD {@link RDFDataset}.
	 */
	private RDFDataset rdfDataSet;

	public RDFDataset getRdfDataSet() {
		return rdfDataSet;
	}

	private JsonLdRDFTermFactory rdfTermFactory;

	public JsonLdDataset() {
		this(new RDFDataset());
	}
	public JsonLdDataset(RDFDataset rdfDataset) {
		this.rdfDataSet = rdfDataset;	
	}

	@Override
	public void add(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		String subjectStr;
		if (subject instanceof BlankNode) {
			// FIXME: Should use BlankNode#uniqueReference()
			subjectStr = subject.ntriplesString();
		} else if (subject instanceof IRI){
			subjectStr = ((IRI)subject).getIRIString();
		} else { 
			throw new IllegalStateException("Subject was neither IRI or BlankNode: " + subject);
		}
		
		String predicateStr = predicate.getIRIString();
		
		if (object instanceof Literal) {
			Literal literal = (Literal) object;
			rdfDataSet.addQuad(subjectStr, predicateStr, 
					literal.getLexicalForm(), 
					literal.getDatatype().getIRIString(), 
					literal.getLanguageTag().orElse(null));			
		} else if (object instanceof BlankNode) {
			rdfDataSet.addQuad(subjectStr, predicateStr, object.ntriplesString());
		} else if (object instanceof IRI) { 
			rdfDataSet.addQuad(subjectStr, predicateStr, ((IRI)object).getIRIString());
		} else { 
			throw new IllegalStateException("Object was neither IRI, BlankNode nor Literal: " + object);
		}				
	}

	@Override
	public void add(org.apache.commons.rdf.api.Quad quad) {		
		// Quad q = asJsonLdQuad(Quad);
		// rdfDataSet.addQuad(q);
		add(quad.getGraphName().orElse(null), 
			quad.getSubject(), 
			quad.getPredicate(), 
			quad.getObject());
	}

	@Override
	public void clear() {
		rdfDataSet.clear();
	}

	@Override
	public void close() {
		// Drop the memory reference, but don't clear it
		rdfDataSet = null;		
	}
	
	@Override
	public boolean contains(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return getQuads(graphName, subject, predicate, object).findAny().isPresent();
	}

	@Override
	public boolean contains(org.apache.commons.rdf.api.Quad quad) {
		return getQuads().anyMatch(Predicate.isEqual(quad));
	}

	public RDFTermFactory getContext() {
		// Note: This does not need to be synchronized, it's OK 
		// if you get a few accidental copies as the
		// same bnodePrefix() is passed to each
		if (rdfTermFactory == null) {
			rdfTermFactory = new JsonLdRDFTermFactory(bnodePrefix());
		}
		return rdfTermFactory;
	}

	@Override
	public Stream<JsonLdQuad> getQuads() {
		return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).
				flatMap(List<RDFDataset.Quad>::parallelStream).map(this::asJsonLdQuad);
	}

	@Override
	public Stream<JsonLdQuad> getQuads(Optional<BlankNodeOrIRI> graphName, 
			BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		// RDFDataSet has no optimizations to help us, so we'll dispatch to filter()
        return getQuads().filter(t -> {
            if (graphName != null && !t.getGraphName().equals(graphName)) {
                return false;
            }        	
            if (subject != null && !t.getSubject().equals(subject)) {
                return false;
            }
            if (predicate != null && !t.getPredicate().equals(predicate)) {
                return false;
            }
            if (object != null && !t.getObject().equals(object)) {
                return false;
            }
            return true;
        });
	}

	@Override
	public void remove(Optional<BlankNodeOrIRI> graphName, 
			BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {		
		Predicate<JsonLdQuad> filter = quadFilter(graphName, subject, predicate, object);
		rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).map(t -> t.removeIf(filter));
	}

	@Override
	public void remove(org.apache.commons.rdf.api.Quad quad) {
		remove(quad.getSubject(), quad.getPredicate(), quad.getObject());
	}

	@Override
	public long size() {
		// Summarize graph.size() for all graphs
		return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).collect(Collectors.summingLong(List::size));
	}

	private Node asJsonLdNode(RDFTerm term) {
		if (term instanceof IRI) {
			return new RDFDataset.IRI( ((IRI)term).getIRIString() );
		}
		if (term instanceof BlankNode) {
			
			String uniqueReference = ((BlankNode)term).uniqueReference();
			if (uniqueReference.startsWith(bnodePrefix())) {
				// one of our own
				// TODO: Retrieve the original BlankNode
				return new RDFDataset.BlankNode(term.ntriplesString());
			} 
			return new RDFDataset.BlankNode( "_:" + uniqueReference );
		}
		if (term instanceof Literal) {
			Literal literal = (Literal) term;
			return new RDFDataset.Literal(literal.getLexicalForm(), literal.getDatatype().getIRIString(), 
					literal.getLanguageTag().orElse(null));
		}
		throw new IllegalArgumentException("RDFTerm not instanceof IRI, BlankNode or Literal: " + term);
	}

	private JsonLdQuad asJsonLdQuad(final RDFDataset.Quad quad) {
		return new JsonLdQuad(quad, bnodePrefix());
	}

	public String bnodePrefix() {
		return "urn:uuid:" + SALT + "#" +  "g"+ System.identityHashCode(rdfDataSet);
	}

	private Predicate<JsonLdQuad> quadFilter(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Optional<Node> graphNameNode = graphName.map(this::asJsonLdNode);
		Optional<Node> subjectNode = Optional.ofNullable(subject).map(this::asJsonLdNode);
		Optional<Node> predicateNode = Optional.ofNullable(predicate).map(this::asJsonLdNode);
		Optional<Node> objectNode = Optional.ofNullable(object).map(this::asJsonLdNode);
		
		return q -> {
			// TODO: Compare graphName (including default graph)
			
		    if (subjectNode.isPresent() && subjectNode.get().compareTo(q.getSubject()) != 0) {
		        return false;
		    }
		    if (predicateNode.isPresent() && predicateNode.get().compareTo(q.getPredicate()) != 0) {	          
		        return false;
		    }
		    if (objectNode.isPresent() && objectNode.get().compareTo(q.getObject()) != 0) {
		        return false;
		    }
		    return true;			
		};
	}
}
