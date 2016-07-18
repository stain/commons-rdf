package org.apache.commons.rdf.elegant;

public interface Term {
	boolean isIri();
	boolean isLiteral();
	boolean isBlankNode();
	
	IRI iri() throws IllegalStateException;
	Literal literal() throws IllegalStateException;
	BlankNode blankNode() throws IllegalStateException;
	String ntriplesString();
	
	public class Fake implements Term {
		@Override
		public boolean isIri() {
			return true;
		}
		@Override
		public boolean isLiteral() {
			return false;
		}
		@Override
		public boolean isBlankNode() {
			return false;
		}
		@Override
		public IRI iri() throws IllegalStateException {
			return new IRI.Fake();
		}
		@Override
		public Literal literal() throws IllegalStateException {
			throw new IllegalStateException();
		}
		@Override
		public BlankNode blankNode() throws IllegalStateException {
			throw new IllegalStateException();
		}
		@Override
		public String ntriplesString() {
			return "<http://example.com/>";
		}
	}	
}
