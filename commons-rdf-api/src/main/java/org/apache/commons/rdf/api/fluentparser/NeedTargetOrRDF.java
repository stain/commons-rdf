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
package org.apache.commons.rdf.api.fluentparser;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.ParserTarget;

public interface NeedTargetOrRDF extends _NeedTargetOrRDF,Buildable {
	NeedTargetOrRDF build();	
	<V> NeedTargetOrRDF option(Option<V> option, V value);
}
interface _NeedTargetOrRDF extends _NeedTarget {
    OptionalTarget<Dataset> rdf(RDF rdf);
}

interface _NeedTarget {
    NeedSourceOrBase<Dataset> target(Dataset dataset);

    NeedSourceOrBase<Graph> target(Graph graph);

    <T> NeedSourceOrBase<T> target(ParserTarget<T> target);
}