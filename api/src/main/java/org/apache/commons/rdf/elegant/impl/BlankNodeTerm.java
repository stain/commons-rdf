package org.apache.commons.rdf.elegant.impl;

import org.apache.commons.rdf.elegant.BlankNode;
import org.apache.commons.rdf.elegant.Term;

public class BlankNodeTerm extends AbstractTerm implements Term  {

	private final BlankNode blankNode;
	
	public BlankNodeTerm() {
		this(new SimpleBlankNode());
	}
	
	public BlankNodeTerm(BlankNode blankNode) {
		this.blankNode = blankNode;
	}
	
	@Override
	public boolean isBlankNode() {
		return true;
	}

	@Override
	public BlankNode blankNode() {
		return blankNode;
	}

}
