package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
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

@Override
public void override(AttributeInfo overriddenAttrInfo) {
	if (joinColumnInfos == null) {
		joinColumnInfos = ((ToOneAttributeInfo)overriddenAttrInfo).joinColumnInfos;
	}
}

@Override
void debugPrintAttributes(PrintStream out) {
	super.debugPrintAttributes(out);
	if (joinColumnInfos != null) {
		for (JoinColumnInfo joinColInfo : joinColumnInfos) {
			joinColInfo.debugPrint(out);
		}
	}
		
}


}
