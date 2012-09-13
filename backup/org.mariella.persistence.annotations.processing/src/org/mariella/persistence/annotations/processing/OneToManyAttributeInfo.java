package org.mariella.persistence.annotations.processing;

import javax.persistence.OneToMany;


@SuppressWarnings("unchecked")
public class OneToManyAttributeInfo extends ToManyAttributeInfo {

@Override
public AttributeInfo copyForAdoption() {
	OneToManyAttributeInfo copy = new OneToManyAttributeInfo();
	initializeAdoptionCopy(copy);
	return copy;
}

@Override
String getAnnotatedMappedBy() {
	OneToMany anno = getAnnotatedElement().getAnnotation(OneToMany.class);
	return anno.mappedBy();
}

@Override
Class getAnnotatedTargetEntity() {
	OneToMany anno = getAnnotatedElement().getAnnotation(OneToMany.class);
	return anno.targetEntity();
}

@Override
public boolean isOptionalOrNullable() {
	return true;
}

}
