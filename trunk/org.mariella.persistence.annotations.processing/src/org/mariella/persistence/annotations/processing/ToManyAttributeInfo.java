package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.UniqueConstraint;


@SuppressWarnings("unchecked")
public abstract class ToManyAttributeInfo extends RelationAttributeInfo {
	
	private JoinTableInfo joinTableInfo;
	private boolean fetchedJoinTableInfo = false;

public ToManyAttributeInfo() {
}	
	
@Override
Class readTargetEntityByReflection() {
	return (Class)ReflectionUtil.readCollectionElementType(ReflectionUtil.readType(getAnnotatedElement()));
}

@Override
void initializeAdoptionCopy(AttributeInfo copy) {
	super.initializeAdoptionCopy(copy);
	((ToManyAttributeInfo)copy).setJoinTableInfo(joinTableInfo);
}

@Override
public void overrideWith(AttributeInfo overriddenAttrInfo) {
	super.overrideWith(overriddenAttrInfo);
	if (this.joinTableInfo == null)
		this.joinTableInfo = ((ToManyAttributeInfo)overriddenAttrInfo).getJoinTableInfo();
}

public JoinTableInfo getJoinTableInfo() {
	if (fetchedJoinTableInfo) return joinTableInfo;
	
	joinTableInfo = buildJoinTableInfo();
	fetchedJoinTableInfo = true;
	return joinTableInfo;
}

private JoinTableInfo buildJoinTableInfo() {
	if (!getAnnotatedElement().isAnnotationPresent(JoinTable.class)) 
		return null;
	
	JoinTableInfo info = new JoinTableInfo();
	info.setAnnotation(getAnnotatedElement().getAnnotation(JoinTable.class));
	info.setUniqueConstraintInfos(buildUniqueContraintInfos(info.annotation.uniqueConstraints()));
	info.setJoinColumnInfos(buildJoinColumnInfos(info.annotation.joinColumns()));
	info.setInverseJoinColumnInfos(buildJoinColumnInfos(info.annotation.inverseJoinColumns()));
	return info;
}

private List<JoinColumnInfo> buildJoinColumnInfos(JoinColumn[] joinColumns) {
	List<JoinColumnInfo> infos = new ArrayList<JoinColumnInfo>();
	for (JoinColumn joinCol : joinColumns) {
		JoinColumnInfo info = new JoinColumnInfo();
		info.setJoinColumn(joinCol);
		infos.add(info);
	}
	return infos;
}

private List<UniqueConstraintInfo> buildUniqueContraintInfos(UniqueConstraint[] uniqueConstraints) {
	List<UniqueConstraintInfo> infos = new ArrayList<UniqueConstraintInfo>();
	for (UniqueConstraint con : uniqueConstraints) {
		UniqueConstraintInfo info = new UniqueConstraintInfo();
		info.setUniqueConstraint(con);
		infos.add(info);
	}
	return infos;
}

public void setJoinTableInfo(JoinTableInfo joinTableInfo) {
	this.joinTableInfo = joinTableInfo;
}

@Override
void debugPrintAttributes(PrintStream out) {
	super.debugPrintAttributes(out);
	if (getJoinTableInfo() != null)
		getJoinTableInfo().debugPrint(out);
}


}
