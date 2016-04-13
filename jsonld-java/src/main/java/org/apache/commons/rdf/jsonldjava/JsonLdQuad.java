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
package org.apache.commons.rdf.jsonldjava;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

import com.github.jsonldjava.core.RDFDataset;

final class JsonLdQuad implements org.apache.commons.rdf.api.Quad {
	private final RDFDataset.Quad quad;
	private String blankNodePrefix;

	private static JsonLdRDFTermFactory rdfTermFactory = new JsonLdRDFTermFactory();
	
	JsonLdQuad(RDFDataset.Quad quad, String blankNodePrefix) {
		this.quad = quad;
		this.blankNodePrefix = blankNodePrefix;			
	}

	@Override
	public BlankNodeOrIRI getSubject() {
		return (BlankNodeOrIRI) rdfTermFactory.asTerm(quad.getSubject(), blankNodePrefix);
	}

	@Override
	public IRI getPredicate() {
		return (IRI) rdfTermFactory.asTerm(quad.getPredicate(), blankNodePrefix);
	}

	@Override
	public RDFTerm getObject() {
		return rdfTermFactory.asTerm(quad.getObject(), blankNodePrefix);
	}

	@Override
	public Optional<BlankNodeOrIRI> getGraphName() {
		return Optional.ofNullable(quad.getGraph())
				.map(n -> (BlankNodeOrIRI)rdfTermFactory.asTerm(n, blankNodePrefix));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof org.apache.commons.rdf.api.Quad)) {
			return false;
		}
		org.apache.commons.rdf.api.Quad other = (org.apache.commons.rdf.api.Quad) obj;
		return getGraphName().equals(other.getGraphName())
				&& getSubject().equals(other.getSubject()) 
				&& getPredicate().equals(other.getPredicate()) 
				&& getObject().equals(other.getObject());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSubject(), getPredicate(), getObject(), getGraphName());
	}
	
	@Override
	public Triple asTriple() {
		return new JsonLdTriple(quad, blankNodePrefix);
	}
}