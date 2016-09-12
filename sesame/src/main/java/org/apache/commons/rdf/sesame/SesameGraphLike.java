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
package org.apache.commons.rdf.sesame;

import java.util.Optional;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.GraphLike;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.TripleLike;
import org.openrdf.model.Model;
import org.openrdf.repository.Repository;

/**
 * Marker interface for Sesame implementations of GraphLike.
 * 
 * @see SesameGraph
 * 
 */
public interface SesameGraphLike<T extends TripleLike<BlankNodeOrIRI, IRI, RDFTerm>>
		extends GraphLike<T, BlankNodeOrIRI, IRI, RDFTerm>, AutoCloseable {

	/**
	 * Return the corresponding Sesame {@link Model}, if present.
	 * <p>
	 * The return value is {@link Optional#isPresent()} if this is backed by a
	 * Model.
	 * <p>
	 * Changes to the Model are reflected in both directions.
	 * 
	 * @return The corresponding Sesame Model.
	 */
	public Optional<Model> asModel();

	/**
	 * Return the corresponding Sesame {@link Repository}, if present.
	 * <p>
	 * The return value is {@link Optional#isPresent()} if this is backed by a
	 * Repository.
	 * <p>
	 * Changes to the Repository are reflected in both directions.
	 * 
	 * @return The corresponding Sesame Repository.
	 */
	public Optional<Repository> asRepository();

}
