package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;

public class EntityListenerClassInfo extends ClassInfo {

@Override
public String getName() {
	return clazz.getName();
}

@Override
public void debugPrint(PrintStream out) {
	out.println(clazz.getSimpleName() + " (EntityListener)");
	debugPrintLifecycleEventInfos(out);
}

}
