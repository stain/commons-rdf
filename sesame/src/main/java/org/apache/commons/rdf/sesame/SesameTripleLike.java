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

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.TripleLike;
import org.openrdf.model.Statement;

/**
 * Marker interface for Sesame implementations of TripleLike statements.
 * <p>
 * This is backed by a {@link Statement} retrievable with {@link #asStatement()}.
 * 
 * @see SesameTriple
 * @see SesameQuad
 */
public interface SesameTripleLike extends TripleLike<BlankNodeOrIRI, IRI, RDFTerm> {
	
	/**
	 * Return the corresponding Sesame {@link Statement}.
	 * 
	 * @return The corresponding Sesame Statement.
	 */
	public Statement asStatement();
}
