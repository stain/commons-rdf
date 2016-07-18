package org.apache.commons.rdf.elegant.impl;

import org.apache.commons.rdf.elegant.BlankNode;
import org.apache.commons.rdf.elegant.IRI;
import org.apache.commons.rdf.elegant.Literal;
import org.apache.commons.rdf.elegant.Term;

public abstract class AbstractTerm implements Term {

	@Override
	public boolean isIri() {
		return false;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}

	@Override
	public boolean isBlankNode() {
		return false;
	}

	@Override
	public IRI iri() throws IllegalStateException {
		throw new IllegalStateException("This term is not an iri");
	}

	@Override
	public Literal literal() throws IllegalStateException {
		throw new IllegalStateException("This term is not literal");
	}

	@Override
	public BlankNode blankNode() throws IllegalStateException {
		throw new IllegalStateException("This term is not blanknode");
	}

	@Override
	public String ntriplesString() {
		if (isIri())
			return "<" + iri().iriString() + ">";
		if (isBlankNode()) 
			return "_:" + blankNode().uniqueReference();
		if (isLiteral()) 
			return "\"" + literal()	 + "\"";
		throw new IllegalStateException("This term is not a iri, literal or blanknode");
	}

}
