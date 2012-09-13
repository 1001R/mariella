package org.mariella.persistence.annotations.processing;

import javax.persistence.NamedNativeQuery;

@SuppressWarnings("unchecked")
public class NamedNativeQueryInfo {

private NamedNativeQuery namedNativeQuery;

public String getName() {
	return namedNativeQuery.name();
}

public String getQuery() {
	return namedNativeQuery.query();
}

public Class getResultClass() {
	return namedNativeQuery.resultClass();
}

public QueryHintInfo[] getQueryHintInfos() {
	QueryHintInfo[] result = new QueryHintInfo[namedNativeQuery.hints().length];
	for (int i=0; i<namedNativeQuery.hints().length;i++) {
		result[i] = new QueryHintInfo();
		result[i].setQueryHint(namedNativeQuery.hints()[i]);
	}
	return result;
}

public String getSqlResultSetMappingName() {
	return namedNativeQuery.resultSetMapping();
}

void setNamedNativeQuery(NamedNativeQuery namedNativeQuery) {
	this.namedNativeQuery = namedNativeQuery;
}

}
