/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.apache.jena.commons;

import org.apache.commons.rdf.api.Graph ;
import org.apache.jena.riot.system.StreamRDF ;

public class ToGraph implements StreamRDF {

    private Graph graph;

    public ToGraph(Graph graph) {
        this.graph = graph ;
    }

    @Override
    public void start() {}

    @Override
    public void triple(org.apache.jena.graph.Triple triple) {
        graph.add(JCR_Factory.fromJena(triple));
    }

    @Override
    public void quad(org.apache.jena.sparql.core.Quad quad) {
        throw new UnsupportedOperationException() ;
    }

    @Override
    public void base(String base) {}

    @Override
    public void prefix(String prefix, String iri) {}

    @Override
    public void finish() {}
    
}

