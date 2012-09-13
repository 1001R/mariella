package org.mariella.persistence.annotations.processing;

import javax.persistence.IdClass;

import org.mariella.persistence.mapping.AttributeInfo;
import org.mariella.persistence.mapping.EntityInfo;
import org.mariella.persistence.mapping.MappedClassInfo;
import org.mariella.persistence.mapping.MappedSuperclassInfo;

public class EntityInfoAttributeInfosBuilder extends
		MappedClassInfoAttributeInfosBuilder {

	public EntityInfoAttributeInfosBuilder(OxyUnitInfoBuilder oxyUnitInfoBuilder, MappedClassInfo mappedClassInfo) {
		super(oxyUnitInfoBuilder, mappedClassInfo);
	}

	@SuppressWarnings("unchecked")
	@Override
	void buildAttributeInfos() throws Exception {
		// TODO Auto-generated method stub
		super.buildAttributeInfos();
		buildAdoptedAttributeInfos();
		IdClass idClass = (IdClass)mappedClassInfo.getClazz().getAnnotation(IdClass.class);
		if (idClass != null)
			((EntityInfo)mappedClassInfo).setCompositeIdClass(idClass.value());
	}
	
	private void buildAdoptedAttributeInfos() throws Exception {
		MappedClassInfo info = mappedClassInfo.getSuperclassInfo();
		while (info instanceof MappedSuperclassInfo) {
			buildAdoptedAttributeInfos(info);
			info = info.getSuperclassInfo();
		}
	}

	private void buildAdoptedAttributeInfos(MappedClassInfo info) {
		for (AttributeInfo attrInfo : info.getAttributeInfos()) {
			AttributeInfo adopted = attrInfo.copyForAdoption();
			oxyUnitInfoBuilder.attributeInfoToAnnotatedElementMap.put(adopted, oxyUnitInfoBuilder.attributeInfoToAnnotatedElementMap.get(attrInfo));
			adopted.setParentClassInfo(mappedClassInfo);
			mappedClassInfo.addAttributeInfo(adopted);
		}
	}

}
