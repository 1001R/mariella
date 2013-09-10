package org.mariella.oxygen.service.client.spring.http;


import java.util.HashMap;
import java.util.Map;

import org.mariella.oxygen.basic_core.OxyEntityManager;

public class OxyClientServiceExecutionContext {

static Map<Thread,OxyEntityManager> threadToEntityManagerMap = new HashMap<Thread, OxyEntityManager>();
static Map<Thread,Integer> threadToStacksizeMap = new HashMap<Thread, Integer>();

public static void putOxyEntityManagerForCurrentThread(OxyEntityManager entityManager) {
	if (entityManager == null)
		throw new IllegalArgumentException();
	
	//System.out.println("Put OxyEntityManager for " + Thread.currentThread());
	threadToEntityManagerMap.put(Thread.currentThread(), entityManager);
	Integer size = threadToStacksizeMap.get(Thread.currentThread());
	if (size == null)
		threadToStacksizeMap.put(Thread.currentThread(), 1);
	else
		threadToStacksizeMap.put(Thread.currentThread(), size+1);
}

public static OxyEntityManager getOxyEntityManagerForCurrentThread() {
	OxyEntityManager oxyEntityMgr = threadToEntityManagerMap.get(Thread.currentThread());
	if (oxyEntityMgr == null)
		throw new IllegalStateException("No OxyEntityManager given for thread " + Thread.currentThread());
	return oxyEntityMgr;
}

public static void removeOxyEntityManagerForCurrentThread() {
	//System.out.println("Remove OxyEntityManager for " + Thread.currentThread());
	Integer size = threadToStacksizeMap.get(Thread.currentThread());
	if (size == null)
		throw new IllegalStateException();
	size = size -1;
	if (size == 0) {
		threadToEntityManagerMap.remove(Thread.currentThread());
	} else {
		threadToStacksizeMap.put(Thread.currentThread(), size);
	}
}


}
