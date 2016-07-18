package org.apache.commons.rdf.elegant.type;

import org.apache.commons.rdf.elegant.DataType;
import org.apache.commons.rdf.elegant.IRI;

public abstract class AbstractDataType implements DataType {

	private IRI iri;
	
	public AbstractDataType(IRI iri) {
		this.iri = iri;
	}

	@Override
	public boolean isLangString() {
		return false;
	}

	@Override
	public int hashCode() {
		return iri().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DataType && 
				iri().equals( ((DataType)obj).iri() );
	}

	@Override
	public IRI iri() {
		return iri;
	}

	
}
