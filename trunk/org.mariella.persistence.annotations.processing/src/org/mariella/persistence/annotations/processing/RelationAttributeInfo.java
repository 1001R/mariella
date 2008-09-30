package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;


@SuppressWarnings("unchecked")
public abstract class RelationAttributeInfo extends AttributeInfo {

	EntityInfo relatedEntityInfo;
	RelationAttributeInfo reverseAttributeInfo;

@Override
void initializeAdoptionCopy(AttributeInfo copy) {
	super.initializeAdoptionCopy(copy);
	
	((RelationAttributeInfo)copy).relatedEntityInfo = relatedEntityInfo;
	// reverseAttributeInfo is built after adoption... 
}

@Override
void resolveReferences() {
	super.resolveReferences();
	
	Class targetEntity = getAnnotatedTargetEntity();
	if (targetEntity == null || targetEntity == void.class)
		targetEntity = readTargetEntityByReflection();
	relatedEntityInfo = (EntityInfo)getParentClassInfo().getOxyUnitInfo().classToInfoMap.get(targetEntity.getName());
	if (relatedEntityInfo == null)
		throw new IllegalStateException("Could not determine relatedEntityInfo for relation attribute " + this);
	String mappedBy = getAnnotatedMappedBy();
	if (mappedBy != null && mappedBy.length() > 0) {
		try {
			reverseAttributeInfo = (RelationAttributeInfo)relatedEntityInfo.getAttributeInfo(mappedBy);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Entity " + getParentClassInfo().getName() + " has invalid mappedBy value " + mappedBy + " for attribute " + getName());
		}
		reverseAttributeInfo.reverseAttributeInfo = this;
	}
}

abstract Class readTargetEntityByReflection();

abstract String getAnnotatedMappedBy();

abstract Class getAnnotatedTargetEntity();

public EntityInfo getRelatedEntityInfo() {
	return relatedEntityInfo;
}

void setRelatedEntityInfo(EntityInfo relatedEntityInfo) {
	this.relatedEntityInfo = relatedEntityInfo;
}

public RelationAttributeInfo getReverseAttributeInfo() {
	return reverseAttributeInfo;
}

void setReverseAttributeInfo(RelationAttributeInfo reverseAttributeInfo) {
	this.reverseAttributeInfo = reverseAttributeInfo;
}

@Override
void debugPrintAttributes(PrintStream out) {
	super.debugPrintAttributes(out);
	if (reverseAttributeInfo != null)
		out.print(" reverseAttributeInfo=" + reverseAttributeInfo.getName());
}

@Override
void debugPrintTypeInfo(PrintStream out) {
	out.print("<" + relatedEntityInfo.getName() + ">");
}

}
