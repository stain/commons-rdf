package org.apache.commons.rdf.elegant.impl;

import org.apache.commons.rdf.elegant.IRI;
import org.apache.commons.rdf.elegant.Term;

public class IRITerm extends AbstractTerm implements Term {
	private final IRI iri;

	public IRITerm(String iriString) {
		this(new SimpleIRI(iriString));
	}
	
	public IRITerm(IRI iri) {
		this.iri = iri;
	}
	
	@Override
	public boolean isIri() {
		return true;
	}
	
	@Override
	public IRI iri() {
		return iri;
	};
	
}
