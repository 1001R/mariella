package org.mariella.oxygen.runtime.impl;


import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.mariella.oxygen.runtime.core.OxyQuery;

/**
 * @author aim
 */
public abstract class OxyQueryImpl implements OxyQuery {
	protected boolean detachedResults;
	
	protected int firstResult = 0;
	protected int maxResults = -1;
	
public Query setFirstResult(int i) {
	firstResult = i;
	return this;
}

public Query setFlushMode(FlushModeType flushmodetype) {
	throw new UnsupportedOperationException();
}

public Query setHint(String s, Object obj) {
	throw new UnsupportedOperationException();
}

public Query setMaxResults(int i) {
	if(i < 0) {
		throw new IllegalArgumentException();
	}
	maxResults = i;
	return this;
}

public boolean isDetachedResults() {
	return detachedResults;
}

public void setDetachedResults(boolean detachedResults) {
	this.detachedResults = detachedResults;
}


}
