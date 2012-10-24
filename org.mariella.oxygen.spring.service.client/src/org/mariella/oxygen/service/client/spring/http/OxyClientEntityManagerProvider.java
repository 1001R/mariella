package org.mariella.oxygen.service.client.spring.http;

import org.aopalliance.intercept.MethodInvocation;
import org.mariella.oxygen.basic_core.OxyEntityManager;

public interface OxyClientEntityManagerProvider {

	OxyEntityManager getEntityManager(MethodInvocation methodInvocation);
	
}
