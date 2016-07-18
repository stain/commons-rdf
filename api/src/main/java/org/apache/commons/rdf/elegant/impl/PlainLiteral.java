package org.apache.commons.rdf.elegant.impl;

import java.util.Optional;

import org.apache.commons.rdf.elegant.DataType;
import org.apache.commons.rdf.elegant.Language;
import org.apache.commons.rdf.elegant.Literal;
import org.apache.commons.rdf.elegant.type.LangStringDataType;

public class PlainLiteral implements Literal {
	private final String plainLiteral;
	private DataType dataType;

	public PlainLiteral(String plainLiteral) {
		this.plainLiteral = plainLiteral;
		this.dataType = new LangStringDataType();
	}

	public PlainLiteral(String plainLiteral, DataType datatype) {
		this.plainLiteral = plainLiteral;
		this.dataType = datatype;
	}
	
	
	@Override
	public DataType dataType() {
		return dataType;
	}

	@Override
	public Optional<Language> language() {
		return Optional.empty();
	}

	@Override
	public String lexicalForm() {
		return plainLiteral;
	}
}
