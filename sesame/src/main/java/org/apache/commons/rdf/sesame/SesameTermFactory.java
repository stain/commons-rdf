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

import java.util.UUID;

// To avoid confusion, avoid importing 
// classes that are in both
// commons.rdf and openrdf.model (e.g. IRI)
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.sesame.impl.BlankNodeImpl;
import org.apache.commons.rdf.sesame.impl.IRIImpl;
import org.apache.commons.rdf.sesame.impl.LiteralImpl;
import org.apache.commons.rdf.sesame.impl.ModelGraphImpl;
import org.apache.commons.rdf.sesame.impl.QuadImpl;
import org.apache.commons.rdf.sesame.impl.RepositoryDatasetImpl;
import org.apache.commons.rdf.sesame.impl.RepositoryGraphImpl;
import org.apache.commons.rdf.sesame.impl.TripleImpl;
import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.repository.Repository;

/**
 * Sesame implementation of RDFTermFactory
 * <p>
 * The {@link #SesameTermFactory()} constructor uses a {@link SimpleValueFactory}
 * to create corresponding Sesame {@link Value} instances. Alternatively, this
 * factory can be constructed with a different {@link ValueFactory} using
 * {@link #SesameTermFactory(ValueFactory)}.
 * <p>
 * {@link #asRDFTerm(Value)} can be used to convert any Sesame {@link Value} to
 * an RDFTerm. Note that adapted {@link BNode}s are considered equal if they are
 * converted with the same {@link SesameTermFactory} instance and have the same
 * {@link BNode#getID()}.
 * <p>
 * {@link #createGraph()} creates a new Graph backed by {@link LinkedHashModel}.
 * To use other models, see {@link #asRDFTermGraph(Model)}.
 * <p>
 * To adapt a Sesame {@link Repository} as a {@link Dataset} or {@link Graph},
 * use {@link #asRDFTermDataset(Repository)} or
 * {@link #asRDFTermGraph(Repository)}.
 * <p>
 * {@link #asTriple(Statement)} can be used to convert a Sesame {@link Statement}
 * to a Commons RDF {@link Triple}, and equivalent {@link #asQuad(Statement)} to
 * convert a {@link Quad}.
 * <p>
 * To convert any {@link Triple} or {@link Quad} to to Sesame {@link Statement},
 * use {@link #asStatement(TripleLike)}. This recognises previously converted
 * {@link SesameTriple}s and {@link SesameQuad}s without re-converting their
 * {@link SesameTripleLike#asStatement()}.
 * <p>
 * Likewise, {@link #asValue(RDFTerm)} can be used to convert any Commons RDF
 * {@link RDFTerm} to a corresponding Sesame {@link Value}. This recognises
 * previously converted {@link SesameTerm}s without re-converting their
 * {@link SesameTerm#asValue()}.
 * <p>
 * For the purpose of {@link BlankNode} equivalence, this factory contains an
 * internal {@link UUID} salt that is used by adapter methods like
 * {@link #asQuad(Statement)}, {@link #asTriple(Statement)},
 * {@link #asRDFTerm(Value)} as well as {@link #createBlankNode(String)}. As
 * Sesame {@link BNode} instances from multiple repositories or models may have
 * the same {@link BNode#getID()}, converting them with the above methods might
 * cause accidental {@link BlankNode} equivalence. Note that the {@link Graph}
 * and {@link Dataset} adapter methods like
 * {@link #asRDFTermDataset(Repository)} and {@link #asRDFTermGraph(Model)}
 * therefore uses a unique {@link SesameTermFactory} internally. An alternative
 * is to use the static methods {@link #asRDFTerm(Value, UUID)},
 * {@link #asQuad(Statement, UUID)} or {@link #asTriple(Statement, UUID)} with
 * a provided {@link UUID} salt.
 * 
 */
public class SesameTermFactory implements RDFTermFactory {

	/**
	 * 
	 * Adapt a Sesame {@link Value} as a Commons RDF {@link RDFTerm}.
	 * <p>
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.openrdf.model.BNode} is converted to a
	 * {@link org.apache.commons.rdf.api.BlankNode}, a
	 * {@link org.openrdf.model.IRI} is converted to a
	 * {@link org.apache.commons.rdf.api.IRI} and a
	 * {@link org.openrdf.model.Literal}. is converted to a
	 * {@link org.apache.commons.rdf.api.Literal}
	 * 
	 * @param value The Sesame {@link Value} to convert.
	 * @param salt
	 *            A {@link UUID} salt to use for uniquely mapping any
	 *            {@link BNode}s. The salt should typically be the same for
	 *            multiple statements in the same {@link Repository} or
	 *            {@link Model} to ensure {@link BlankNode#equals(Object)} and
	 *            {@link BlankNode#uniqueReference()} works as intended.
	 * @return A {@link RDFTerm} that corresponds to the Sesame value
	 * @throws IllegalArgumentException if the value is not a BNode, Literal or IRI 
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Value> SesameTerm<T> asRDFTerm(final T value, UUID salt) {
		if (value instanceof BNode) {
			return (SesameTerm<T>) new BlankNodeImpl((BNode) value, salt);
		}
		if (value instanceof org.openrdf.model.Literal) {
			return (SesameTerm<T>) new LiteralImpl((org.openrdf.model.Literal) value);
		}
		if (value instanceof org.openrdf.model.IRI) {
			return (SesameTerm<T>) new IRIImpl((org.openrdf.model.IRI) value);
		}
		throw new IllegalArgumentException("Value is not a BNode, Literal or IRI: " + value.getClass());
	}

	/**
	 * Adapt a Sesame {@link Statement} as a Commons RDF {@link Triple}.
	 * 
	 * @param statement
	 *            The statement to convert
	 * @param salt
	 *            A {@link UUID} salt to use for uniquely mapping any
	 *            {@link BNode}s. The salt should typically be the same for
	 *            multiple statements in the same {@link Repository} or
	 *            {@link Model} to ensure {@link BlankNode#equals(Object)} and
	 *            {@link BlankNode#uniqueReference()} works as intended.
	 * @return A {@link Triple} that corresponds to the Sesame statement
	 */
	public static SesameTriple asTriple(final Statement statement, UUID salt) {
		return new TripleImpl(statement, salt);
	}

	private UUID salt = UUID.randomUUID();

	private final ValueFactory valueFactory;

	public SesameTermFactory() {
		this.valueFactory = SimpleValueFactory.getInstance();
	}

	public SesameTermFactory(ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}

	/**
	 * Adapt a Sesame {@link Statement} as a Commons RDF {@link Quad}.
	 * <p>
	 * For the purpose of {@link BlankNode} equivalence, this 
	 * method will use an internal salt UUID that is unique per instance of 
	 * {@link SesameTermFactory}. 
	 * <p>
	 * <strong>NOTE:</strong> If combining Sesame {@link Statement}s
	 * multiple repositories or models, then their {@link BNode}s 
	 * may have the same {@link BNode#getID()}, which with this method 
	 * would become equivalent according to {@link BlankNode#equals(Object)} and
	 * {@link BlankNode#uniqueReference()}, 
	 * unless a separate {@link SesameTermFactory}
	 * instance is used per Sesame repository/model.  
	 * 
	 * @see #asQuad(Statement, UUID)
	 * @param statement
	 *            The statement to convert
	 * @return A {@link SesameQuad} that is equivalent to the statement
	 */
	public SesameQuad asQuad(final Statement statement) {
		return new QuadImpl(statement, salt);
	}

	/**
	 * Adapt a Sesame {@link Statement} as a Commons RDF {@link Quad}.
	 *
	 * @see #asQuad(Statement)
	 * @param statement
	 *            The statement to convert
	 * @param salt
	 *            A {@link UUID} salt to use for uniquely mapping any
	 *            {@link BNode}s. The salt should typically be the same for
	 *            multiple statements in the same {@link Repository} or
	 *            {@link Model} to ensure {@link BlankNode#equals(Object)} and
	 *            {@link BlankNode#uniqueReference()} works as intended.
	 * @return A {@link SesameQuad} that is equivalent to the statement
	 */
	public static SesameQuad asQuad(final Statement statement, UUID salt) {
		return new QuadImpl(statement, salt);
	}

	
	/**
	 * 
	 * Adapt a Sesame {@link Value} as a Commons RDF {@link RDFTerm}.
	 * <p>
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.openrdf.model.BNode} is converted to a
	 * {@link org.apache.commons.rdf.api.BlankNode}, a
	 * {@link org.openrdf.model.IRI} is converted to a
	 * {@link org.apache.commons.rdf.api.IRI} and a
	 * {@link org.openrdf.model.Literal}. is converted to a
	 * {@link org.apache.commons.rdf.api.Literal}
	 * <p>
	 * For the purpose of {@link BlankNode} equivalence, this 
	 * method will use an internal salt UUID that is unique per instance of 
	 * {@link SesameTermFactory}. 
	 * <p>
	 * <strong>NOTE:</strong> If combining Sesame values from
	 * multiple repositories or models, then their {@link BNode}s 
	 * may have the same {@link BNode#getID()}, which with this method 
	 * would become equivalent according to {@link BlankNode#equals(Object)} and
	 * {@link BlankNode#uniqueReference()}, 
	 * unless a separate {@link SesameTermFactory}
	 * instance is used per Sesame repository/model.  
	 * 
	 * @param value The Sesame {@link Value} to convert.
	 * @return A {@link RDFTerm} that corresponds to the Sesame value
	 * @throws IllegalArgumentException if the value is not a BNode, Literal or IRI 
	 */
	public <T extends Value> SesameTerm<T> asRDFTerm(T value) {
		return asRDFTerm(value, salt);
	}

	/**
	 * Adapt an Sesame {@link Repository} as a Commons RDF {@link Dataset}.
	 * <p>
	 * Changes to the dataset are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            Sesame {@link Repository} to connect to.
	 * @return A {@link Dataset} backed by the Sesame repository.
	 */
	public SesameDataset asRDFTermDataset(Repository repository) {
		return new RepositoryDatasetImpl(repository);
	}

	/**
	 * Adapt an Sesame {@link Repository} as a Commons RDF {@link Dataset}.
	 * <p>
	 * Changes to the dataset are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            Sesame {@link Repository} to connect to.
	 * @param includeInferred
	 *            If true, any inferred quads are included in the dataset
	 * @return A {@link Dataset} backed by the Sesame repository.
	 */
	public SesameDataset asRDFTermDataset(Repository repository, boolean includeInferred) {
		return new RepositoryDatasetImpl(repository, includeInferred);
	}
	
	/**
	 * Adapt an Sesame {@link Model} as a Commons RDF {@link Graph}.
	 * <p>
	 * Changes to the graph are reflected in the model, and vice versa.
	 * 
	 * @param model
	 *            Sesame {@link Model} to adapt.
	 * @return Adapted {@link Graph}.
	 */
	public SesameGraph asRDFTermGraph(Model model) {
		return new ModelGraphImpl(model);
	}

	/**
	 * Adapt an Sesame {@link Repository} as a Commons RDF {@link Graph}.
	 * <p>
	 * The graph will include triples in any contexts (e.g. the union graph).
	 * <p>
	 * Changes to the graph are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            Sesame {@link Repository} to connect to.
	 * @return A {@link Graph} backed by the Sesame repository.
	 */
	public SesameGraph asRDFTermGraph(Repository repository) {
		return new RepositoryGraphImpl(repository, false, true);
	}

	/**
	 * Adapt an Sesame {@link Repository} as a Commons RDF {@link Graph}.
	 * <p>
	 * The graph will include triples in any contexts (e.g. the union graph).
	 * <p>
	 * Changes to the graph are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            Sesame {@link Repository} to connect to.
	 * @param includeInferred
	 *            If true, any inferred triples are included in the graph
	 * @param unionGraph
	 *            If true, triples from any context is included in the graph,
	 *            otherwise only triples in the default context
	 *            <code>null</code>.
	 * @return A {@link Graph} backed by the Sesame repository.
	 */
	public SesameGraph asRDFTermGraph(Repository repository, boolean includeInferred, boolean unionGraph) {
		return new RepositoryGraphImpl(repository, includeInferred, unionGraph);
	}

	/**
	 * Adapt a Commons RDF {@link Triple} or {@link Quad} as a Sesame
	 * {@link Statement}.
	 * <p>
	 * If the <code>tripleLike</code> argument is an {@link SesameTriple} or
	 * a {@link SesameQuad}, then its {@link SesameTripleLike#asStatement()} is
	 * returned as-is. Note that this means that a {@link SesameTriple} would
	 * preserve its {@link Statement#getContext()}, and that any 
	 * {@link BlankNode}s would be deemed equivalent in Sesame
	 * if they have the same {@link BNode#getID()}.
	 * 
	 * @param tripleLike
	 *            A {@link Triple} or {@link Quad} to adapt
	 * @return A corresponding {@link Statement}
	 */
	public Statement asStatement(TripleLike<BlankNodeOrIRI, org.apache.commons.rdf.api.IRI, RDFTerm> tripleLike) {
		if (tripleLike instanceof SesameTripleLike) {
			// Return original statement - this covers both SesameQuad and
			// SesameTriple
			SesameTripleLike sesameTriple = (SesameTripleLike) tripleLike;
			return sesameTriple.asStatement();
		}

		org.openrdf.model.Resource subject = (org.openrdf.model.Resource) asValue(tripleLike.getSubject());
		org.openrdf.model.IRI predicate = (org.openrdf.model.IRI) asValue(tripleLike.getPredicate());
		Value object = asValue(tripleLike.getObject());

		org.openrdf.model.Resource context = null;
		if (tripleLike instanceof Quad) {
			Quad quad = (Quad) tripleLike;
			context = (org.openrdf.model.Resource) asValue(quad.getGraphName().orElse(null));
		}

		return getValueFactory().createStatement(subject, predicate, object, context);
	}

	/**
	 * Adapt a Sesame {@link Statement} as a Commons RDF {@link Triple}.
	 * <p>
	 * For the purpose of {@link BlankNode} equivalence, this 
	 * method will use an internal salt UUID that is unique per instance of 
	 * {@link SesameTermFactory}. 
	 * <p>
	 * <strong>NOTE:</strong> If combining Sesame statements from
	 * multiple repositories or models, then their {@link BNode}s 
	 * may have the same {@link BNode#getID()}, which with this method 
	 * would become equivalent according to {@link BlankNode#equals(Object)} and
	 * {@link BlankNode#uniqueReference()}, 
	 * unless a separate {@link SesameTermFactory}
	 * instance is used per Sesame repository/model.
	 * 
	 * @param statement
	 * @return A {@link SesameTriple} that is equivalent to the statement
	 */
	public SesameTriple asTriple(final Statement statement) {
		return new TripleImpl(statement, salt);
	}

	/**
	 * Adapt a Commons RDF {@link RDFTerm} as a Sesame {@link Value}.
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.apache.commons.rdf.api.BlankNode} is converted to a
	 * {@link org.openrdf.model.BNode}, a
	 * {@link org.apache.commons.rdf.api.IRI} is converted to a
	 * {@link org.openrdf.model.IRI} and a
	 * {@link org.apache.commons.rdf.api.Literal} is converted to a
	 * {@link org.openrdf.model.Literal}.
	 * <p>
	 * If the provided {@link RDFTerm} is <code>null</code>, then the returned
	 * value is <code>null</code>.
	 * <p>
	 * If the provided term is an instance of {@link SesameTerm}, then the
	 * {@link SesameTerm#asValue()} is returned without any conversion. Note that
	 * this could mean that a {@link Value} from a different kind of
	 * {@link ValueFactory} could be returned.
	 * 
	 * @param term
	 *            RDFTerm to adapt to Sesame Value
	 * @return Adapted Sesame {@link Value}
	 */
	public Value asValue(RDFTerm term) {
		if (term == null) {
			return null;
		}
		if (term instanceof SesameTerm) {
			// One of our own - avoid converting again.
			// (This is crucial to avoid double-escaping in BlankNode)
			return ((SesameTerm<?>) term).asValue();
		}
		if (term instanceof org.apache.commons.rdf.api.IRI) {
			org.apache.commons.rdf.api.IRI iri = (org.apache.commons.rdf.api.IRI) term;
			return getValueFactory().createIRI(iri.getIRIString());
		}
		if (term instanceof org.apache.commons.rdf.api.Literal) {
			org.apache.commons.rdf.api.Literal literal = (org.apache.commons.rdf.api.Literal) term;
			String label = literal.getLexicalForm();
			if (literal.getLanguageTag().isPresent()) {
				String lang = literal.getLanguageTag().get();
				return getValueFactory().createLiteral(label, lang);
			}
			org.openrdf.model.IRI dataType = (org.openrdf.model.IRI) asValue(literal.getDatatype());
			return getValueFactory().createLiteral(label, dataType);
		}
		if (term instanceof BlankNode) {
			// This is where it gets tricky to support round trips!
			BlankNode blankNode = (BlankNode) term;
			// FIXME: The uniqueReference might not be a valid BlankNode
			// identifier..
			// does it have to be in Sesame?
			return getValueFactory().createBNode(blankNode.uniqueReference());
		}
		throw new IllegalArgumentException("RDFTerm was not an IRI, Literal or BlankNode: " + term.getClass());
	}

	@Override
	public SesameBlankNode createBlankNode() throws UnsupportedOperationException {
		BNode bnode = getValueFactory().createBNode();
		return (SesameBlankNode) asRDFTerm(bnode);
	}

	@Override
	public SesameBlankNode createBlankNode(String name) throws UnsupportedOperationException {
		BNode bnode = getValueFactory().createBNode(name);
		return (SesameBlankNode) asRDFTerm(bnode);
	}

	@Override
	public SesameGraph createGraph() throws UnsupportedOperationException {
		return asRDFTermGraph(new LinkedHashModel());
	}

	@Override
	public SesameIRI createIRI(String iri) throws IllegalArgumentException, UnsupportedOperationException {
		return (SesameIRI) asRDFTerm(getValueFactory().createIRI(iri));
	}

	@Override
	public SesameLiteral createLiteral(String lexicalForm)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.openrdf.model.Literal lit = getValueFactory().createLiteral(lexicalForm);
		return (SesameLiteral) asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, org.apache.commons.rdf.api.IRI dataType)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.openrdf.model.IRI iri = getValueFactory().createIRI(dataType.getIRIString());
		org.openrdf.model.Literal lit = getValueFactory().createLiteral(lexicalForm, iri);
		return (org.apache.commons.rdf.api.Literal) asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, String languageTag)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.openrdf.model.Literal lit = getValueFactory().createLiteral(lexicalForm, languageTag);
		return (org.apache.commons.rdf.api.Literal) asRDFTerm(lit);
	}

	@Override
	public SesameTriple createTriple(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		final Statement statement = getValueFactory().createStatement(
				(org.openrdf.model.Resource) asValue(subject),
				(org.openrdf.model.IRI) asValue(predicate), 
				asValue(object));
		return asTriple(statement);
	}

	@Override
	public Quad createQuad(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		final Statement statement = getValueFactory().createStatement(
				(org.openrdf.model.Resource) asValue(subject),
				(org.openrdf.model.IRI) asValue(predicate), 
				asValue(object), 
				(org.openrdf.model.Resource)asValue(graphName));
		return asQuad(statement);
	}
	
	public ValueFactory getValueFactory() {
		return valueFactory;
	}

}
