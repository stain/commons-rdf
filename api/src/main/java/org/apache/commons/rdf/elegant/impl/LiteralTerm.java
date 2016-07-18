package org.apache.commons.rdf.elegant.impl;

import org.apache.commons.rdf.elegant.Literal;
import org.apache.commons.rdf.elegant.Term;

public class LiteralTerm extends AbstractTerm implements Term {

	private final Literal literal;

	public LiteralTerm(String plainLiteral) {
		this.literal = new PlainLiteral(plainLiteral);
	}
	
	public LiteralTerm(Literal literal) {
		this.literal = literal;
	}

	@Override
	public boolean isLiteral() {
		return true;
	}
	
	@Override
	public Literal literal() {
		return literal;
	}
	
}
