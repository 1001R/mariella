package org.mariella.persistence.annotations.processing;



public class EmbeddableInfo extends MappedClassInfo {

@Override
public String getName() {
	return clazz.getSimpleName();
}

}
