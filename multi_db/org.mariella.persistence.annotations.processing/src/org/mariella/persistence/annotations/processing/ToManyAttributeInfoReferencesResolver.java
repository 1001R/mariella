package org.mariella.persistence.annotations.processing;

import org.mariella.persistence.mapping.ReflectionUtil;
import org.mariella.persistence.mapping.RelationAttributeInfo;

public abstract class ToManyAttributeInfoReferencesResolver extends
		RelationAttributeInfoReferencesResolver {

	public ToManyAttributeInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, RelationAttributeInfo attrInfo) {
		super(oxyUnitInfoBuilder, attrInfo);
	}

	@Override
	Class<?> readTargetEntityByReflection() {
		return (Class<?>)ReflectionUtil.readCollectionElementType(ReflectionUtil.readType(getAnnotatedElement()));
	}

}
