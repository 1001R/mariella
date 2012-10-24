package org.mariella.oxygen.service.server.spring;

import java.util.HashMap;
import java.util.Map;

public class DefaultServiceExecutionContext implements ServiceExecutionContext {
Map<Object,Object> transactionCache = new HashMap<Object, Object>();


public void clearTransactionCache() {
	transactionCache.clear();
}

public Map<Object, Object> getTransactionCache() {
	return transactionCache;
}

}
