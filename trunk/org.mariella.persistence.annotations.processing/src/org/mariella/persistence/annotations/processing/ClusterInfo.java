package org.mariella.persistence.annotations.processing;

import org.mariella.persistence.annotations.Cluster;

@SuppressWarnings("unchecked")
public class ClusterInfo {

private Class clusterClass;
private String rootEntityName;
private Cluster cluster;

public Class getClusterClass() {
	return clusterClass;
}

public String[] getPathExpressions() {
	return cluster.pathExpressions();
}

public String getRootEntityName() {
	return rootEntityName;
}

public String getName() {
	String name = cluster.name();
	if (name.length() > 0) return name;
	
	return clusterClass.getName();
}

void setClusterClass(Class clusterClass) {
	this.clusterClass = clusterClass;
}

void setCluster(Cluster cluster) {
	this.cluster = cluster;
}

void setRootEntityName(String rootEntityName) {
	this.rootEntityName  = rootEntityName;
}

}
