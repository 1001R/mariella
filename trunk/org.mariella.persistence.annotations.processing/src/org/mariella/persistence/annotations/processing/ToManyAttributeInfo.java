package org.mariella.persistence.annotations.processing;


@SuppressWarnings("unchecked")
public abstract class ToManyAttributeInfo extends RelationAttributeInfo {

@Override
Class readTargetEntityByReflection() {
	return (Class)ReflectionUtil.readCollectionElementType(ReflectionUtil.readType(getAnnotatedElement()));
}


}
