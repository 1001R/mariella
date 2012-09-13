package org.mariella.persistence.annotations.processing;


public class MappedSuperclassInfo extends MappedClassInfo {

public String getName() {
	return getClazz().getName();
}

}
