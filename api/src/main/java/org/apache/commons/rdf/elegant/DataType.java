package org.apache.commons.rdf.elegant;

public interface DataType {

	public boolean isLangString();
	public IRI iri();

	public class Fake implements DataType {
		@Override
		public boolean isLangString() {
			return true;
		}
		@Override
		public IRI iri() {
			return new IRI.Fake();
		}
	}	
	
}
