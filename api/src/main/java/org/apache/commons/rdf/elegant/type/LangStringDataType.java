package org.apache.commons.rdf.elegant.type;

import org.apache.commons.rdf.elegant.DataType;
import org.apache.commons.rdf.elegant.IRI;
import org.apache.commons.rdf.elegant.impl.SimpleIRI;

public class LangStringDataType extends AbstractDataType implements DataType {
	
	private static final String LANG_STRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";

	public LangStringDataType() {
		this(new SimpleIRI(LANG_STRING));		
	}
	
	public LangStringDataType(IRI iri) {
		super(iri);		
	}
	
	@Override
	public boolean isLangString() {
		return true;
	}

	@Override
	public IRI iri() {
		IRI iri = super.iri();
		if (!iri.iriString().equals(LANG_STRING)) {
			throw new IllegalStateException("Expected rdfs:langString datatype: " + iri.iriString());
		}
		return iri;
	}
	
	
	
}
