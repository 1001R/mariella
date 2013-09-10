package org.mariella.oxygen.spring;

import java.util.HashMap;
import java.util.Map;

public class OxyDataSourceEntityManagerProviderContext {

private static Map<Thread, OxyEntityManagerInitializer> threadToEntityManagerInitializerMap = new HashMap<Thread, OxyEntityManagerInitializer>();

public static void putOxyEntityManagerInitializerForCurrentThread(OxyEntityManagerInitializer initializer) {
	threadToEntityManagerInitializerMap.put(Thread.currentThread(), initializer);
}

public static OxyEntityManagerInitializer getOxyEntityManagerInitializerForCurrentThread() {
	return threadToEntityManagerInitializerMap.get(Thread.currentThread());
}

public static void removeOxyEntityManagerInitializerForCurrentThread() {
	threadToEntityManagerInitializerMap.remove(Thread.currentThread());
}



}
