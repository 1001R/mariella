package org.mariella.persistence.annotations.processing;

import javax.persistence.ManyToMany;

@SuppressWarnings("unchecked")
public class ManyToManyAttributeInfo extends ToManyAttributeInfo {

@Override
public AttributeInfo copyForAdoption() {
	ManyToManyAttributeInfo copy = new ManyToManyAttributeInfo();
	initializeAdoptionCopy(copy);
	return copy;
}

@Override
String getAnnotatedMappedBy() {
	ManyToMany anno = getAnnotatedElement().getAnnotation(ManyToMany.class);
	return anno.mappedBy();
}

@Override
Class getAnnotatedTargetEntity() {
	ManyToMany anno = getAnnotatedElement().getAnnotation(ManyToMany.class);
	return anno.targetEntity();
}

@Override
public boolean isOptionalOrNullable() {
	return true;
}


}
