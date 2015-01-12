/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.rdf.event;

import org.apache.commons.rdf.Triple;
import org.apache.commons.rdf.Graph;

/**
 * This class represent a addition event that occured on a
 * <code>TripleCollection</code>.
 *
 * @author rbn
 */
public class AddEvent extends GraphEvent {


    public AddEvent(Graph graph,  Triple triple) {
        super(graph, triple);
    }

}
