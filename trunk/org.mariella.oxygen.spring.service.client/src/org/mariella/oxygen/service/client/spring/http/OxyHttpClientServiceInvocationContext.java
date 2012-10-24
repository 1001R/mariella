package org.mariella.oxygen.service.client.spring.http;

import org.mariella.oxygen.basic_core.OxyEntityManager;

public interface OxyHttpClientServiceInvocationContext {

OxyEntityManager getEntityManager();

ClassLoader getClassLoader();

}
