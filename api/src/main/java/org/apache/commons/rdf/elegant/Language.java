package org.apache.commons.rdf.elegant;

public interface Language {
	public String languageTag();
	
	public class Fake implements Language {
		@Override
		public String languageTag() {
			return "en";
		}	
	}
}
