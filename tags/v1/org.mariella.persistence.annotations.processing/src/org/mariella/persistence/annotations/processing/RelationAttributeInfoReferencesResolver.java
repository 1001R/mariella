package org.mariella.persistence.annotations.processing;

import java.lang.reflect.AnnotatedElement;

import org.mariella.persistence.mapping.EntityInfo;
import org.mariella.persistence.mapping.RelationAttributeInfo;

public abstract class RelationAttributeInfoReferencesResolver {

	OxyUnitInfoBuilder oxyUnitInfoBuilder;
	RelationAttributeInfo relationAttributeInfo;

	public RelationAttributeInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, RelationAttributeInfo attrInfo) {
		this.oxyUnitInfoBuilder = oxyUnitInfoBuilder;
		this.relationAttributeInfo = attrInfo;
	}

	public void resolveReferences() {
		Class<?> targetEntity = getAnnotatedTargetEntity();
		if (targetEntity == null || targetEntity == void.class)
			targetEntity = readTargetEntityByReflection();
		relationAttributeInfo.setRelatedEntityInfo((EntityInfo)relationAttributeInfo.getParentClassInfo().getOxyUnitInfo().getClassToInfoMap().get(targetEntity.getName()));
		if (relationAttributeInfo.getRelatedEntityInfo() == null)
			throw new IllegalStateException("Could not determine relatedEntityInfo for relation attribute " + relationAttributeInfo.getParentClassInfo().getName() + "." + relationAttributeInfo.getName());
		String mappedBy = getAnnotatedMappedBy();
		if (mappedBy != null && mappedBy.length() > 0) {
			try {
				relationAttributeInfo.setReverseAttributeInfo((RelationAttributeInfo)relationAttributeInfo.getRelatedEntityInfo().getAttributeInfo(mappedBy));
			} catch (IllegalArgumentException e) {
				throw new IllegalStateException("Entity " + relationAttributeInfo.getParentClassInfo().getName() + " has invalid mappedBy value " + mappedBy + " for attribute " + relationAttributeInfo.toString());
			}
			relationAttributeInfo.getReverseAttributeInfo().setReverseAttributeInfo(relationAttributeInfo);
		}

	}

	abstract Class<?> readTargetEntityByReflection();

	abstract Class<?> getAnnotatedTargetEntity();

	abstract String getAnnotatedMappedBy();

	AnnotatedElement getAnnotatedElement() {
		AnnotatedElement ae = oxyUnitInfoBuilder.attributeInfoToAnnotatedElementMap.get(relationAttributeInfo);
		return ae;
	}

}
