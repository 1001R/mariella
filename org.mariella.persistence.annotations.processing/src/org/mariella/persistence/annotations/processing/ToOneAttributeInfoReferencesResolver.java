package org.mariella.persistence.annotations.processing;

import org.mariella.persistence.mapping.ReflectionUtil;
import org.mariella.persistence.mapping.RelationAttributeInfo;

public abstract class ToOneAttributeInfoReferencesResolver extends
		RelationAttributeInfoReferencesResolver {

	public ToOneAttributeInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, RelationAttributeInfo attrInfo) {
		super(oxyUnitInfoBuilder, attrInfo);
	}
	
	@Override
	Class<?> readTargetEntityByReflection() {
		return (Class<?>)ReflectionUtil.readType(getAnnotatedElement());
	}


}
