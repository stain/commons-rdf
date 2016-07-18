package org.apache.commons.rdf.elegant.impl;

import java.util.UUID;

import org.apache.commons.rdf.elegant.BlankNode;

public class SimpleBlankNode implements BlankNode {
	
	private final UUID uuid;  
	
	public SimpleBlankNode() {
		this(UUID.randomUUID());
	}
	
	public SimpleBlankNode(String uuid) {
		this.uuid = UUID.fromString(uuid);
	}
	
	public SimpleBlankNode(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String uniqueReference() {
		return uuid.toString();
	}

}
