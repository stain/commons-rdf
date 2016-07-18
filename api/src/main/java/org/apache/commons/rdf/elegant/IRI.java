package org.apache.commons.rdf.elegant;

public interface IRI {
	public String iriString();

	public class Fake implements IRI {
		@Override
		public String iriString() {
			return "http://example.com/";
		}
		@Override
		public int hashCode() {
			return iriString().hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof IRI) && iriString().equals(((IRI) obj).iriString());
		}
	}
}
