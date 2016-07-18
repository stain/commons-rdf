package org.apache.commons.rdf.elegant.type;

import org.apache.commons.rdf.elegant.DataType;
import org.apache.commons.rdf.elegant.IRI;
import org.apache.commons.rdf.elegant.impl.SimpleIRI;

public class StringDataType extends AbstractDataType implements DataType {
	
	private static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";

	public StringDataType() {
		this(new SimpleIRI(XSD_STRING));		
	}
	
	public StringDataType(IRI iri) {
		super(iri);		
	}

	@Override
	public IRI iri() {
		IRI iri = super.iri();
		if (!iri.iriString().equals(XSD_STRING)) {
			throw new IllegalStateException("Expected xsd:string datatype: " + iri.iriString());
		}
		return iri;
	}

}
