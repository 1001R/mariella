package org.mariella.persistence.mapping;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("unchecked")
public abstract class ClassInfo {
private OxyUnitInfo oxyUnitInfo;
private Class clazz;
private List<LifecycleEventInfo> lifecycleEventInfos = new ArrayList<LifecycleEventInfo>();

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

public void setOxyUnitInfo(OxyUnitInfo oxyUnitInfo) {
	this.oxyUnitInfo = oxyUnitInfo;
}

public Class getClazz() {
	return clazz;
}

public void setClazz(Class clazz) {
	this.clazz = clazz;
}

public List<LifecycleEventInfo> getLifecycleEventInfos() {
	return lifecycleEventInfos;
}

public boolean isAbstract() {
	return Modifier.isAbstract(clazz.getModifiers());
}

public void setLifecycleEventInfos(List<LifecycleEventInfo> lifecycleEventInfos) {
	this.lifecycleEventInfos = lifecycleEventInfos;
}

}
