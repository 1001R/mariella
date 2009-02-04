package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.mariella.persistence.annotations.ForeignKeyUpdateStrategy;


@SuppressWarnings("unchecked")
public abstract class ToOneAttributeInfo extends RelationAttributeInfo {

private List<JoinColumnInfo> joinColumnInfos = Collections.EMPTY_LIST;
private ForeignKeyUpdateStrategy foreignKeyUpdateStrategy = null;

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
public void overrideWith(AttributeInfo overriddenAttrInfo) {
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

public ForeignKeyUpdateStrategy getForeignKeyUpdateStrategy() {
	return foreignKeyUpdateStrategy;
}

void setForeignKeyUpdateStrategy(
		ForeignKeyUpdateStrategy foreignKeyUpdateStrategy) {
	this.foreignKeyUpdateStrategy = foreignKeyUpdateStrategy;
}

public boolean isUpdateForeignKeys() {
	if (foreignKeyUpdateStrategy == null)
		return true;
	return foreignKeyUpdateStrategy.update();
}

}
