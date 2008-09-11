package org.mariella.persistence.annotations.processing;

import java.lang.reflect.Method;

public class LifecycleEventInfo {
private Method method;
private LifecycleEventType eventType;

public LifecycleEventType getEventType() {
	return eventType;
}

void setEventType(LifecycleEventType eventType) {
	this.eventType = eventType;
}

public Method getMethod() {
	return method;
}

void setMethod(Method method) {
	this.method = method;
}

}
