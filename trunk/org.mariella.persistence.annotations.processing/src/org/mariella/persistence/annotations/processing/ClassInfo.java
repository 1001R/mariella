package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;


@SuppressWarnings("unchecked")
public abstract class ClassInfo {
OxyUnitInfo oxyUnitInfo;
Class clazz;
List<LifecycleEventInfo> lifecycleEventInfos = new ArrayList<LifecycleEventInfo>();


void buildLifecycleEventInfos() {
	for (Method method : clazz.getDeclaredMethods()) 
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
	lifecycleEventInfos.add(info);
}

protected void debugPrintLifecycleEventInfos(PrintStream out) {
	if (lifecycleEventInfos.size() == 0) return;
	
	out.print("\tLifecycleEventInfos: ");
	for (Iterator<LifecycleEventInfo> i=lifecycleEventInfos.iterator(); i.hasNext();) {
		LifecycleEventInfo info = i.next();
		out.print(info.getMethod());
		if (i.hasNext())
			out.print(", ");
	}
	out.println();
}

public abstract String getName();

public abstract void debugPrint(PrintStream out);

public OxyUnitInfo getOxyUnitInfo() {
	return oxyUnitInfo;
}

void setOxyUnitInfo(OxyUnitInfo oxyUnitInfo) {
	this.oxyUnitInfo = oxyUnitInfo;
}

public Class getClazz() {
	return clazz;
}

void setClazz(Class clazz) {
	this.clazz = clazz;
}

void resolveReferences() {
}

public List<LifecycleEventInfo> getLifecycleEventInfos() {
	return lifecycleEventInfos;
}

public boolean isAbstract() {
	return Modifier.isAbstract(clazz.getModifiers());
}

}
