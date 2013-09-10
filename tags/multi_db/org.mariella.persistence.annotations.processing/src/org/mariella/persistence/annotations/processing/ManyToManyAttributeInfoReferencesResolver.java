package org.mariella.persistence.annotations.processing;

import javax.persistence.ManyToMany;

import org.mariella.persistence.mapping.RelationAttributeInfo;

public class ManyToManyAttributeInfoReferencesResolver extends
		ToManyAttributeInfoReferencesResolver {

	public ManyToManyAttributeInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, RelationAttributeInfo attrInfo) {
		super(oxyUnitInfoBuilder, attrInfo);
	}

	@Override
	String getAnnotatedMappedBy() {
		ManyToMany anno = getAnnotatedElement().getAnnotation(ManyToMany.class);
		return anno.mappedBy();
	}

	@Override
	Class<?> getAnnotatedTargetEntity() {
		ManyToMany anno = getAnnotatedElement().getAnnotation(ManyToMany.class);
		return anno.targetEntity();
	}

}
