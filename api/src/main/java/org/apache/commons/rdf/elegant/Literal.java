package org.apache.commons.rdf.elegant;

import java.util.Optional;

public interface Literal {
	Optional<Language> language();
	DataType dataType();
	String lexicalForm();
	
	public class Fake implements Literal {
		@Override
		public Optional<Language> language() {
			return Optional.empty();
		}
		@Override
		public DataType dataType() {
			return new DataType.Fake();
		}
		@Override
		public String lexicalForm() {
			return "Hello^^<http://example.com/>";
		}		
	}
}
