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
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.sesame.RDF4JGraph;
import org.apache.commons.rdf.sesame.RDF4JTermFactory;
import org.apache.commons.rdf.sesame.RDF4JTriple;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;

public final class ModelGraphImpl implements RDF4JGraph {
	
	private Model model;
	private RDF4JTermFactory sesameTermFactory;

	public ModelGraphImpl(Model model) {
		this.model = model;	
		this.sesameTermFactory = new RDF4JTermFactory();
	}

	public ModelGraphImpl(Model model, RDF4JTermFactory sesameTermFactory) {
		this.model = model;	
		this.sesameTermFactory = sesameTermFactory;
	}
	
	@Override
	public void add(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
		model.add(
				(Resource)sesameTermFactory.asValue(subject), 
				(org.openrdf.model.IRI)sesameTermFactory.asValue(predicate), 
				sesameTermFactory.asValue(object));				
	}
	
	@Override
	public void add(Triple triple) {
		model.add(sesameTermFactory.asStatement(triple));
	}

	public Optional<Model> asModel() { 
		return Optional.of(model);
	}

	@Override
	public Optional<Repository> asRepository() {
		return Optional.empty();
	}
	
	@Override
	public void clear() {
		model.clear();
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
		return model.contains(
				(Resource)sesameTermFactory.asValue(subject), 
				(org.openrdf.model.IRI)sesameTermFactory.asValue(predicate), 
				sesameTermFactory.asValue(object));
	}

	@Override
	public boolean contains(Triple triple) {
		return model.contains(sesameTermFactory.asStatement(triple));
	}

	@Override
	public void remove(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
		model.remove(
				(Resource)sesameTermFactory.asValue(subject), 
				(org.openrdf.model.IRI)sesameTermFactory.asValue(predicate), 
				sesameTermFactory.asValue(object));		
	}

	@Override
	public void remove(Triple triple) { 
		model.remove(sesameTermFactory.asStatement(triple));
	}

	@Override
	public long size() {
		int size = model.size();
		if (size < Integer.MAX_VALUE) {
			return size;
		} else {
			// TODO: Check if this can really happen with RDF4J models
			// Collection.size() can't help us, we'll have to count
			return model.parallelStream().count();
		}				
	}

	@Override
	public Stream<RDF4JTriple> stream() {
		return model.parallelStream().map(sesameTermFactory::asTriple);
	}

	@Override
	public Stream<RDF4JTriple> stream(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
		return model.filter(
				(Resource)sesameTermFactory.asValue(subject), 
				(org.openrdf.model.IRI)sesameTermFactory.asValue(predicate), 
				sesameTermFactory.asValue(object)).parallelStream()
			.map(sesameTermFactory::asTriple);
	}
	
	@Override
	public Optional<Resource[]> getContextFilter() {
		// ModelGraph always do the unionGraph
		return Optional.empty();
		// TODO: Should we support contextFilter like in RepositoryGraphImpl?
	}
	
}