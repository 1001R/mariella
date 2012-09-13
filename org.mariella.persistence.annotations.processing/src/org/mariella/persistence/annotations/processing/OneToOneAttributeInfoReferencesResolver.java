package org.mariella.persistence.annotations.processing;

import javax.persistence.OneToOne;

import org.mariella.persistence.mapping.RelationAttributeInfo;

public class OneToOneAttributeInfoReferencesResolver extends ToOneAttributeInfoReferencesResolver {

	public OneToOneAttributeInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, RelationAttributeInfo attrInfo) {
		super(oxyUnitInfoBuilder, attrInfo);
	}

	@Override
	String getAnnotatedMappedBy() {
		OneToOne anno = getAnnotatedElement().getAnnotation(OneToOne.class);
		return anno.mappedBy();
	}

	@Override
	Class<?> getAnnotatedTargetEntity() {
		OneToOne anno = getAnnotatedElement().getAnnotation(OneToOne.class);
		return anno.targetEntity();
	}

}
