package org.mariella.persistence.annotations.processing;

import javax.persistence.QueryHint;

public class QueryHintInfo {

private QueryHint queryHint;

public String getName() {
	return queryHint.name();
}

public String getValue() {
	return queryHint.value();
}

void setQueryHint(QueryHint queryHint) {
	this.queryHint = queryHint;
}

}
