package org.mariella.persistence.annotations.processing;

import javax.persistence.OneToMany;

import org.mariella.persistence.mapping.RelationAttributeInfo;

public class OneToManyAttributeInfoReferencesResolver extends ToManyAttributeInfoReferencesResolver {

	public OneToManyAttributeInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, RelationAttributeInfo attrInfo) {
		super(oxyUnitInfoBuilder, attrInfo);
	}

	@Override
	String getAnnotatedMappedBy() {
		OneToMany anno = getAnnotatedElement().getAnnotation(OneToMany.class);
		return anno.mappedBy();
	}

	@Override
	Class<?> getAnnotatedTargetEntity() {
		OneToMany anno = getAnnotatedElement().getAnnotation(OneToMany.class);
		return anno.targetEntity();
	}

}
