package org.mariella.persistence.annotations.processing;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.mariella.persistence.mapping.ClassInfo;
import org.mariella.persistence.mapping.LifecycleEventInfo;
import org.mariella.persistence.mapping.LifecycleEventType;

public class ClassInfoLifecycleEventInfosBuilder {

	ClassInfo classInfo;
	
	public ClassInfoLifecycleEventInfosBuilder(ClassInfo classInfo) {
		this.classInfo = classInfo;
	}
	
	void buildLifecycleEventInfos() {
		for (Method method : classInfo.getClazz().getDeclaredMethods()) 
			parseLifecycleEventAnnotations(method);
	}

	private void parseLifecycleEventAnnotations(AnnotatedElement method) {
		if (method.isAnnotationPresent(PrePersist.class))
			addLifecycleEventInfo(method, LifecycleEventType.PrePersist);
		if (method.isAnnotationPresent(PostPersist.class))
			addLifecycleEventInfo(method, LifecycleEventType.PostPersist);
		if (method.isAnnotationPresent(PreRemove.class))
			addLifecycleEventInfo(method, LifecycleEventType.PreRemove);
		if (method.isAnnotationPresent(PostRemove.class))
			addLifecycleEventInfo(method, LifecycleEventType.PostRemove);
		if (method.isAnnotationPresent(PreUpdate.class))
			addLifecycleEventInfo(method, LifecycleEventType.PreUpdate);
		if (method.isAnnotationPresent(PostUpdate.class))
			addLifecycleEventInfo(method, LifecycleEventType.PostUpdate);
		if (method.isAnnotationPresent(PostLoad.class))
			addLifecycleEventInfo(method, LifecycleEventType.PostLoad);
	}

	private void addLifecycleEventInfo(AnnotatedElement method, LifecycleEventType eventType) {
		LifecycleEventInfo info = new LifecycleEventInfo();
		info.setEventType(eventType);
		info.setMethod((Method)method);
		classInfo.getLifecycleEventInfos().add(info);
	}

}
