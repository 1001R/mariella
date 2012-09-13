package org.mariella.glue.ui;

import java.util.HashMap;
import java.util.Map;

import org.mariella.glue.service.Entity;



@SuppressWarnings("unchecked")
public class UIRegistry {
	public static UIRegistry Singleton = new UIRegistry();
	
	private Map<String, UIRegistration> registry = new HashMap<String, UIRegistration>();
	
public void register(Class entityClass, UIRegistration registration) {
	registry.put(entityClass.getName(), registration);
}

public <T extends Entity> UIRegistration<T> get(String entityClassName) {
	return registry.get(entityClassName);
}

public <T extends Entity> UIRegistration<T> get(Class<?> entityClass) {
	return get(entityClass.getName());
}
}
