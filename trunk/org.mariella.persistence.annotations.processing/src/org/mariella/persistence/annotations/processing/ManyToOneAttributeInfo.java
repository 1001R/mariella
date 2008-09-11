package org.mariella.persistence.annotations.processing;

import javax.persistence.ManyToOne;

@SuppressWarnings("unchecked")
public class ManyToOneAttributeInfo extends ToOneAttributeInfo {

@Override
String getAnnotatedMappedBy() {
	return null;
}

@Override
Class getAnnotatedTargetEntity() {
	ManyToOne anno = getAnnotatedElement().getAnnotation(ManyToOne.class);
	return anno.targetEntity();
}

@Override
AttributeInfo copyForAdoption() {
	ManyToOneAttributeInfo copy = new ManyToOneAttributeInfo();
	initializeAdoptionCopy(copy);
	return copy;
}

@Override
public boolean isOptionalOrNullable() {
	ManyToOne anno = getAnnotatedElement().getAnnotation(ManyToOne.class);
	return anno.optional();
}

}
