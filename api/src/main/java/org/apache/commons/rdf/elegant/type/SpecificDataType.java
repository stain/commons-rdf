package org.apache.commons.rdf.elegant.type;

import org.apache.commons.rdf.elegant.DataType;
import org.apache.commons.rdf.elegant.IRI;
import org.apache.commons.rdf.elegant.impl.SimpleIRI;

public class SpecificDataType extends AbstractDataType implements DataType {


	public SpecificDataType(String iriString) {
		this(new SimpleIRI(iriString)); 
	}
	
	public SpecificDataType(IRI iri) {
		super(iri);
	}

}
