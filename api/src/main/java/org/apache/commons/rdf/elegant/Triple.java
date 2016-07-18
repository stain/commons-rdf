package org.apache.commons.rdf.elegant;

public interface Triple {
	Term subject();
	Term predicate();
	Term object();
	
	public class Fake implements Triple {
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
	}
}
