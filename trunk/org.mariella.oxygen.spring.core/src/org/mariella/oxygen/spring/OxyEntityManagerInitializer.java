package org.mariella.oxygen.spring;

import org.mariella.oxygen.runtime.core.OxyServerEntityManager;

public interface OxyEntityManagerInitializer {

void initializeEntityManager(OxyServerEntityManager entityManager);

}
