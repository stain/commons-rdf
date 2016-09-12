/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.sesame.impl;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.sesame.SesameGraph;
import org.apache.commons.rdf.sesame.SesameTriple;
import info.aduna.iteration.Iterations;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;

public class RepositoryGraphImpl extends AbstractRepositoryGraphLike<Triple> implements Graph, SesameGraph {

	private final Resource[] contextFilter;

	public RepositoryGraphImpl(Repository repository, boolean includeInferred, boolean unionGraph) {
		super(repository, includeInferred);
		if (unionGraph) {
			// no context filter aka any context
			this.contextFilter = new Resource[] { };
		} else {
			// default context: null
			this.contextFilter = new Resource[] { null };
		}
	}

	public RepositoryGraphImpl(Repository repository, boolean includeInferred, Resource... contextFilter) {
		super(repository, includeInferred);
		this.contextFilter = contextFilter;
	}


	@Override
	public void add(Triple tripleLike) {
		Statement statement = sesameTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(statement, contextFilter);
			conn.commit();
		}
	}


	@Override
	public boolean contains(Triple tripleLike) {
		Statement statement = sesameTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(statement, includeInferred, contextFilter);
		}
	}

	@Override
	public void remove(Triple tripleLike) {
		Statement statement = sesameTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(statement, contextFilter);
			conn.commit();
		}
	}

	@Override
	public void clear() {
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.clear(contextFilter);
			conn.commit();
		}
	}

	@Override
	public long size() {
		try (RepositoryConnection conn = getRepositoryConnection()) {
			if (! includeInferred && contextFilter.length == 0) { 
				return conn.size();
			} else {
				return stream().count();
			}
		}
	}

	
	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(subj, pred, obj, contextFilter);
			conn.commit();
		}
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(subj, pred, obj, includeInferred, contextFilter);
		}
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(subj, pred, obj, contextFilter);
			conn.commit();
		}
	}

	@Override
	public Stream<SesameTriple> stream() {
		return stream(null, null, null);
	}
	
	@Override
	public Stream<SesameTriple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		RepositoryConnection conn = getRepositoryConnection();
		// FIXME: Is it OK that we don't close the connection?
		RepositoryResult<Statement> statements = conn.getStatements(subj, pred, obj, includeInferred, contextFilter);
		return Iterations.stream(statements).map(this::asTripleLike);
	}
	
	@Override
	protected SesameTriple asTripleLike(Statement statement) {
		return sesameTermFactory.asTriple(statement);
	}

	public Optional<Resource[]> getContextFilter() {
		// Make sure we clone
		return Optional.ofNullable(contextFilter).map(f -> f.clone());		
	}
	
}
