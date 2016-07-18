package org.apache.commons.rdf.elegant.impl;

import org.apache.commons.rdf.elegant.BlankNode;
import org.apache.commons.rdf.elegant.IRI;
import org.apache.commons.rdf.elegant.Literal;
import org.apache.commons.rdf.elegant.Term;
import org.apache.commons.rdf.elegant.Triple;

public class SimpleTriple implements Triple {

	public SimpleTriple(Term subject, Term predicate, Term object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	
	public SimpleTriple(BlankNode subject, IRI predicate, Literal object) {
		this.subject = new BlankNodeTerm(subject);
		this.predicate = new IRITerm(predicate);
		this.object = new LiteralTerm(object);
	}
	
	private final Term subject;
	private final Term predicate;
	private final Term object;

	@Override
	public Term subject() {
		if (! (subject.isIri() || subject.isBlankNode())) {
			throw new IllegalStateException("Subject must be a iri or blanknode: " + subject);
		}
		return subject;
	}

	@Override
	public Term predicate() {
		if (! predicate.isIri()) {
			throw new IllegalStateException("Predicate must be a iri" + predicate);
		}		
		return predicate;		
	}

	@Override
	public Term object() {
		if (! (object.isIri() || object.isBlankNode() || object.isLiteral())) {
			throw new IllegalStateException("Object must be a iri, blankNode or literal");
		}
		return object;
	}

}
