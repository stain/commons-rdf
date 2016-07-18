package org.apache.commons.rdf.elegant.impl;

import java.net.URI;

import org.apache.commons.rdf.elegant.IRI;

public class SimpleIRI implements IRI {

	private final String iriString;

	public SimpleIRI(IRITerm iriTerm) {
		this(iriTerm.iri().iriString());
	}
	
	public SimpleIRI(IRI iri) {
		this(iri.iriString());
	}
	
	public SimpleIRI(URI uri) {
		this(uri.toString());
	}
	
	public SimpleIRI(String iriString) {
		this.iriString = iriString;
	}

	@Override
	public String iriString() {
		return iriString;
	}

}
