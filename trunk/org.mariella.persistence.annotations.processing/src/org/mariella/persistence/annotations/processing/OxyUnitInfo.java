package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

@SuppressWarnings("unchecked")
public class OxyUnitInfo implements PersistenceUnitInfo {

	List<URL> jarFileUrls = new ArrayList<URL>(); 
	List<String> managedClassNames = new ArrayList<String>();
	String persistenceUnitName = null;
	URL persistenceUnitRootUrl = null;
	Properties properties = new Properties();
	Map<Class,ClassInfo> classToInfoMap = new HashMap<Class,ClassInfo>();
	List<ClassInfo> hierarchyOrderedClassInfos = new ArrayList<ClassInfo>();
	List<ClusterInfo> clusterInfos = new ArrayList<ClusterInfo>();
	List<SqlResultSetMappingInfo> sqlResultSetMappingInfos = new ArrayList<SqlResultSetMappingInfo>();
	List<NamedNativeQueryInfo> namedNativeQueryInfos = new ArrayList<NamedNativeQueryInfo>();
	List<SequenceGeneratorInfo> sequenceGeneratorInfos = new ArrayList<SequenceGeneratorInfo>();
	List<TableGeneratorInfo> tableGeneratorInfos = new ArrayList<TableGeneratorInfo>();
	List<DomainDefinitionInfo> domainDefinitionInfos = new ArrayList<DomainDefinitionInfo>();
	
public Collection<ClassInfo> getClassInfos() {
	return classToInfoMap.values();
}


public void addTransformer(ClassTransformer transformer) {
	throw new UnsupportedOperationException();
}

public boolean excludeUnlistedClasses() {
	throw new UnsupportedOperationException();
}

public ClassLoader getClassLoader() {
	throw new UnsupportedOperationException();
}

public List<URL> getJarFileUrls() {
	return jarFileUrls;
}

public DataSource getJtaDataSource() {
	throw new UnsupportedOperationException();
}

public List<String> getManagedClassNames() {
	return managedClassNames;
}

public List<String> getMappingFileNames() {
	throw new UnsupportedOperationException();
}

public ClassLoader getNewTempClassLoader() {
	throw new UnsupportedOperationException();
}

public DataSource getNonJtaDataSource() {
	throw new UnsupportedOperationException();
}

public String getPersistenceProviderClassName() {
	throw new UnsupportedOperationException();
}

public String getPersistenceUnitName() {
	return persistenceUnitName;
}

public URL getPersistenceUnitRootUrl() {
	return persistenceUnitRootUrl;
}

public Properties getProperties() {
	return properties;
}

public PersistenceUnitTransactionType getTransactionType() {
	throw new UnsupportedOperationException();
}

public String toString() {
	return "OxyUnitInfo name: " + getPersistenceUnitName();
}

public void debugPrint(PrintStream out) {
	out.println("======= START PERSISTENCE UNIT INFO ========");
	out.println("[persistenceUnitRootURL: " + persistenceUnitRootUrl + "]");
	for (ClassInfo info : classToInfoMap.values()) {
		out.println();
		info.debugPrint(out);
	}
	out.println("======= END PERSISTENCE UNIT INFO ========");
}


public List<ClusterInfo> getClusterInfos() {
	return clusterInfos;
}


public List<SqlResultSetMappingInfo> getSqlResultSetMappingInfos() {
	return sqlResultSetMappingInfos;
}


public List<NamedNativeQueryInfo> getNamedNativeQueryInfos() {
	return namedNativeQueryInfos;
}

public ClassInfo getClassInfo(Class clazz) {
	return classToInfoMap.get(clazz);
}


public List<SequenceGeneratorInfo> getSequenceGeneratorInfos() {
	return sequenceGeneratorInfos;
}


public List<TableGeneratorInfo> getTableGeneratorInfos() {
	return tableGeneratorInfos;
}


public List<DomainDefinitionInfo> getDomainDefinitionInfos() {
	return domainDefinitionInfos;
}

}
