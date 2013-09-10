package org.mariella.persistence.annotations.processing;

import javax.persistence.ManyToOne;

import org.mariella.persistence.mapping.RelationAttributeInfo;

public class ManyToOneAttributeInfoReferencesResolver extends ToOneAttributeInfoReferencesResolver {

	public ManyToOneAttributeInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, RelationAttributeInfo attrInfo) {
		super(oxyUnitInfoBuilder, attrInfo);
	}

	@Override
	String getAnnotatedMappedBy() {
		return null;
	}

	@Override
	Class<?> getAnnotatedTargetEntity() {
		ManyToOne anno = getAnnotatedElement().getAnnotation(ManyToOne.class);
		return anno.targetEntity();
	}

}
