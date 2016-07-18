package org.apache.commons.rdf.elegant;

public interface Quad extends Triple {
	Term graphName();
	
	public class Fake implements Quad {
		@Override
		public Term subject() {
			return new Term.Fake();
		}
		@Override
		public Term predicate() {
			return new Term.Fake();
		}
		@Override
		public Term object() {
			return new Term.Fake();
		}	
		@Override
		public Term graphName() {
			return new Term.Fake();
		}		
	}
}
