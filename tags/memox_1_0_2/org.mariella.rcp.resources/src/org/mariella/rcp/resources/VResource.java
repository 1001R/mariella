package org.mariella.rcp.resources;


public interface VResource extends VResourceRefHolder {
	
VResourceRef getRef();

void setRef(VResourceRef ref);

String getName();

void dispose();

}
