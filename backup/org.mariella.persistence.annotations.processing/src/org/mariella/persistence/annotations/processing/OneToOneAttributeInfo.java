package org.mariella.persistence.annotations.processing;

import javax.persistence.OneToOne;


@SuppressWarnings("unchecked")
public class OneToOneAttributeInfo extends ToOneAttributeInfo {

@Override
public AttributeInfo copyForAdoption() {
	OneToOneAttributeInfo copy = new OneToOneAttributeInfo();
	initializeAdoptionCopy(copy);
	return copy;
}

@Override
String getAnnotatedMappedBy() {
	OneToOne anno = getAnnotatedElement().getAnnotation(OneToOne.class);
	return anno.mappedBy();
}

@Override
Class getAnnotatedTargetEntity() {
	OneToOne anno = getAnnotatedElement().getAnnotation(OneToOne.class);
	return anno.targetEntity();
}

@Override
public boolean isOptionalOrNullable() {
	OneToOne anno = getAnnotatedElement().getAnnotation(OneToOne.class);
	return anno.optional();
}

}
