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

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.sesame.impl.ModelGraphImpl;
import org.apache.commons.rdf.sesame.impl.RepositoryGraphImpl;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;


/**
 * Marker interface for Sesame implementations of Graph.
 * 
 * @see ModelGraphImpl
 * @see RepositoryGraphImpl
 */
public interface SesameGraph extends Graph, SesameGraphLike<Triple> {
	
	/**
	 * Return a copy of the context filter, if present.
	 * <p>
	 * If {@link Optional#isPresent()}, the filter determines which
	 * <em>contexts</em> in the corresponding Sesame {@link Model} or
	 * {@link Repository} that this graph reflect. Modifications to the graph
	 * (e.g. {@link #add(Triple)} will be performed for all the specified
	 * contexts, while retrieval (e.g. {@link #contains(Triple)}) will succeed
	 * if the triple is in at least one of the specified contexts.
	 * <p>
	 * The context filter array may contain <code>null</code>, indicating the
	 * default context (the <em>default graph</em> in RDF datasets).
	 * <p>
	 * If the context filter is {@link Optional#empty()}, then this is a
	 * <em>union graph</em> which triples reflecting statements in any contexts.
	 * Triples added to the graph will be added in the default context
	 * <code>null</code>.
	 * <p>
	 * The filter array is a copy, and thus any modifications are not reflected
	 * in the SesameGraph.
	 * 
	 * @return The context filter as an array of {@link Resource}s, or
	 *         {@link Optional#empty()} indicating the union graph (any
	 *         context).
	 */
	public Optional<Resource[]> getContextFilter();
	
}
