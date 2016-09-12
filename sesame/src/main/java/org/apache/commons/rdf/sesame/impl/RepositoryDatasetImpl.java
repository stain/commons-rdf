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
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.sesame.SesameDataset;
import org.apache.commons.rdf.sesame.SesameQuad;
import info.aduna.iteration.Iterations;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;

public class RepositoryDatasetImpl extends AbstractRepositoryGraphLike<Quad> implements SesameDataset, Dataset {

	public RepositoryDatasetImpl(Repository repository, boolean includeInferred) {
		super(repository, includeInferred);
	}

	public RepositoryDatasetImpl(Repository repository) {
		super(repository);
	}


	@Override
	public void add(Quad tripleLike) {
		Statement statement = sesameTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(statement);
			conn.commit();
		}
	}


	@Override
	public boolean contains(Quad tripleLike) {
		Statement statement = sesameTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(statement, includeInferred);
		}
	}

	@Override
	public void remove(Quad tripleLike) {
		Statement statement = sesameTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(statement);
			conn.commit();
		}
	}

	@Override
	public void clear() {
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.clear();
			conn.commit();
		}
	}

	@Override
	public long size() {
		if (includeInferred) { 
			// We'll need to count them all
			return stream().count();
		} 
		// else: Ask directly
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.size();
		}
	}

	
	@Override
	public void add(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource context = (Resource) sesameTermFactory.asValue(graphName);
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(subj, pred, obj,  context);
			conn.commit();
		}
	}

	@Override
	public boolean contains(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		Resource[] contexts = asContexts(graphName);
		
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(subj, pred, obj, includeInferred, contexts);
		}
	}

	private Resource[] asContexts(Optional<BlankNodeOrIRI> graphName) {
		Resource[] contexts;
		if (graphName == null) {
			// no contexts == any contexts
			 contexts = new Resource[0];
		} else {	
			BlankNodeOrIRI g = graphName.orElse(null);
			Resource context = (Resource) sesameTermFactory.asValue(g);
			contexts = new Resource[] { context };
		}
		return contexts;
	}

	@Override
	public void remove(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		Resource[] contexts = asContexts(graphName);

		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(subj, pred, obj, contexts);
			conn.commit();
		}
	}

	@Override
	public Stream<SesameQuad> stream() {
		return stream(null, null, null, null);
	}
	
	@Override
	public Stream<SesameQuad> stream(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) sesameTermFactory.asValue(subject);
		org.openrdf.model.IRI pred = (org.openrdf.model.IRI) sesameTermFactory.asValue(predicate);
		Value obj = sesameTermFactory.asValue(object);
		Resource[] contexts = asContexts(graphName);
		
		RepositoryConnection conn = getRepositoryConnection();
		// NOTE: connection will be closed outside by the Iterations.stream()
		RepositoryResult<Statement> statements = conn.getStatements(subj, pred, obj, includeInferred, contexts);
		return Iterations.stream(statements).map(this::asTripleLike);
	}

	@Override
	protected SesameQuad asTripleLike(Statement s) {
		return sesameTermFactory.asQuad(s);
	}

	@Override
	public Graph getGraph() {
		// default context only
		return new RepositoryGraphImpl(repository, includeInferred, (Resource)null);		
	}

	@Override
	public Optional<Graph> getGraph(BlankNodeOrIRI graphName) {
		// NOTE: May be null to indicate default context
		Resource context = (Resource) sesameTermFactory.asValue(graphName);		
		return Optional.of(new RepositoryGraphImpl(repository, includeInferred, context));		
	}

	@Override
	public Stream<BlankNodeOrIRI> getGraphNames() {
		RepositoryConnection conn = getRepositoryConnection();
		RepositoryResult<Resource> contexts = conn.getContextIDs();
		// NOTE: connection will be closed outside by the Iterations.stream()
		return Iterations.stream(contexts).map(g -> (BlankNodeOrIRI) sesameTermFactory.asRDFTerm(g));
	}
	
}
