package org.mariella.oxygen.service.server.spring;

import java.util.Map;

public interface ServiceExecutionContext {

Map<Object, Object> getTransactionCache();
void clearTransactionCache();

}
