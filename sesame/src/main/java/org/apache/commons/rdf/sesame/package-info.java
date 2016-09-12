/*
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
/**
 * Commons RDF integration with <a href="http://sesame.org/">Sesame</a>.
 * <p>
 * Use the {@link org.apache.commons.rdf.sesame.SesameTermFactory} to convert
 * between Commons RDF and Sesame types, for instance
 * {@link org.apache.commons.rdf.sesame.SesameTermFactory#asQuad(org.openrdf.model.Statement)}
 * converts a Sesame {@link org.openrdf.model.Statement} to a
 * {@link org.apache.commons.rdf.api.Quad}. Converted RDF terms implement the
 * {@link org.apache.commons.rdf.sesame.SesameTerm} interface, and converted
 * statements the {@link org.apache.commons.rdf.sesame.SesameTripleLike}
 * interface, which provide convenience access to the underlying Sesame
 * implementations.
 * <p>
 * Sesame {@link org.openrdf.model.Model}s and
 * {@link org.openrdf.repository.Repository} instances can be adapted to
 * Commons RDF {@link org.apache.commons.rdf.api.Graph} and
 * {@link org.apache.commons.rdf.api.Dataset}, e.g. using
 * {@link org.apache.commons.rdf.sesame.SesameTermFactory#asRDFTermGraph(org.openrdf.model.Model)}
 * or
 * {@link org.apache.commons.rdf.sesame.SesameTermFactory#asRDFTermDataset(org.openrdf.repository.Repository)}.
 * The returned adapted graph/dataset is directly mapped, so changes are
 * propagated both ways. For convenience, the marker interface
 * {@link org.apache.commons.rdf.sesame.SesameGraph} and
 * {@link org.apache.commons.rdf.sesame.SesameDataset} provide access to the
 * underlying Sesame implementations.
 * <p>
 * The {@link org.apache.commons.rdf.sesame.SesameParserBuilder} can be used to
 * parse RDF files using RDF4j. It should be most efficient if used with
 * {@link org.apache.commons.rdf.sesame.SesameParserBuilder#target(org.apache.commons.rdf.api.Dataset)}
 * and an adapted {@link org.apache.commons.rdf.sesame.SesameDataset}, or
 * {@link org.apache.commons.rdf.sesame.SesameParserBuilder#target(org.apache.commons.rdf.api.Graph)}
 * and a an adapted {@link org.apache.commons.rdf.sesame.SesameGraph}
 * 
 *
 */
package org.apache.commons.rdf.sesame;
