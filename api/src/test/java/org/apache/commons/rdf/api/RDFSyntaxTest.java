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
package org.apache.commons.rdf.api;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class RDFSyntaxTest {

	@Test
	public void mediaType() throws Exception {
		assertEquals("application/ld+json", RDFSyntax.Standard.JSONLD.getMediaType());
		assertEquals("application/n-quads", RDFSyntax.Standard.NQUADS.getMediaType());
		assertEquals("application/n-triples", RDFSyntax.Standard.NTRIPLES.getMediaType());
		assertEquals("text/html", RDFSyntax.Standard.RDFA_HTML.getMediaType());
		assertEquals("application/xhtml+xml", RDFSyntax.Standard.RDFA_XHTML.getMediaType());
		assertEquals("application/rdf+xml", RDFSyntax.Standard.RDFXML.getMediaType());
		assertEquals("application/trig", RDFSyntax.Standard.TRIG.getMediaType());
		assertEquals("text/turtle", RDFSyntax.Standard.TURTLE.getMediaType());
	}

	@Test
	public void byMediaType() throws Exception {
		assertEquals(RDFSyntax.Standard.JSONLD, RDFSyntax.Standard.byMediaType("application/ld+json").get());
		assertEquals(RDFSyntax.Standard.NQUADS, RDFSyntax.Standard.byMediaType("application/n-quads").get());
		assertEquals(RDFSyntax.Standard.NTRIPLES, RDFSyntax.Standard.byMediaType("application/n-triples").get());
		assertEquals(RDFSyntax.Standard.RDFA_HTML, RDFSyntax.Standard.byMediaType("text/html").get());
		assertEquals(RDFSyntax.Standard.RDFA_XHTML, RDFSyntax.Standard.byMediaType("application/xhtml+xml").get());
		assertEquals(RDFSyntax.Standard.RDFXML, RDFSyntax.Standard.byMediaType("application/rdf+xml").get());
		assertEquals(RDFSyntax.Standard.TRIG, RDFSyntax.Standard.byMediaType("application/trig").get());
		assertEquals(RDFSyntax.Standard.TURTLE, RDFSyntax.Standard.byMediaType("text/turtle").get());
	}

	@Test
	public void name() throws Exception {
		assertEquals("JSON-LD 1.0", RDFSyntax.Standard.JSONLD.toString());
		assertEquals("RDF 1.1 Turtle", RDFSyntax.Standard.TURTLE.toString());
	}

	@Test
	public void byMediaTypeUnknown() throws Exception {
		assertEquals(Optional.empty(), RDFSyntax.Standard.byMediaType("application/octet-stream"));
	}

	@Test
	public void byMediaTypeLowerCase() throws Exception {
		assertEquals(RDFSyntax.Standard.JSONLD, RDFSyntax.Standard.byMediaType("APPLICATION/ld+JSON").get());
	}

	@Test
	public void byMediaTypeContentType() throws Exception {
		assertEquals(RDFSyntax.Standard.TURTLE, RDFSyntax.Standard.byMediaType("text/turtle; charset=\"UTF-8\"").get());
		assertEquals(RDFSyntax.Standard.TURTLE, RDFSyntax.Standard.byMediaType("text/turtle ; charset=\"UTF-8\"").get());
		assertEquals(RDFSyntax.Standard.TURTLE, RDFSyntax.Standard.byMediaType("text/turtle, text/plain").get());
		assertEquals(Optional.empty(), RDFSyntax.Standard.byMediaType(" text/turtle"));
	}

}
