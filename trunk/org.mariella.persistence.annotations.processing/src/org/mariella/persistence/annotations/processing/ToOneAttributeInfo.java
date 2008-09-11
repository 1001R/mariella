package org.mariella.persistence.annotations.processing;

import java.util.Collections;
import java.util.List;


@SuppressWarnings("unchecked")
public abstract class ToOneAttributeInfo extends RelationAttributeInfo {

private List<JoinColumnInfo> joinColumnInfos = Collections.EMPTY_LIST;

@Override
Class readTargetEntityByReflection() {
	return (Class)ReflectionUtil.readType(getAnnotatedElement());
}

@Override
void initializeAdoptionCopy(AttributeInfo copy) {
	super.initializeAdoptionCopy(copy);
	((ToOneAttributeInfo)copy).joinColumnInfos = joinColumnInfos;
}

public List<JoinColumnInfo> getJoinColumnInfos() {
	return joinColumnInfos;
}

void setJoinColumnInfos(List<JoinColumnInfo> joinColumnInfos) {
	this.joinColumnInfos = joinColumnInfos;
}

}
