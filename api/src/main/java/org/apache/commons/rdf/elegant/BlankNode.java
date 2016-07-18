package org.apache.commons.rdf.elegant;

public interface BlankNode {
	String uniqueReference();
	
	public class Fake implements BlankNode {
		@Override
		public String uniqueReference() {
			return "4dd27e59-7917-4438-b939-f8cd2d3ad7a1";
		}		
	}
	
}
