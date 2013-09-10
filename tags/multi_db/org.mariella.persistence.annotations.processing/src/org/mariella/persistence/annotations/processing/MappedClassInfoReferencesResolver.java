package org.mariella.persistence.annotations.processing;

import org.mariella.persistence.mapping.AttributeInfo;
import org.mariella.persistence.mapping.EntityListenerClassInfo;
import org.mariella.persistence.mapping.LifecycleEventInfo;
import org.mariella.persistence.mapping.ManyToManyAttributeInfo;
import org.mariella.persistence.mapping.ManyToOneAttributeInfo;
import org.mariella.persistence.mapping.MappedClassInfo;
import org.mariella.persistence.mapping.OneToManyAttributeInfo;
import org.mariella.persistence.mapping.OneToOneAttributeInfo;
import org.mariella.persistence.mapping.RelationAttributeInfo;

public class MappedClassInfoReferencesResolver {
	
	OxyUnitInfoBuilder oxyUnitInfoBuilder;
	MappedClassInfo mappedClassInfo;
	
	MappedClassInfoReferencesResolver(OxyUnitInfoBuilder oxyUnitInfoBuilder, MappedClassInfo mappedClassInfo) {
		this.oxyUnitInfoBuilder = oxyUnitInfoBuilder;
		this.mappedClassInfo = mappedClassInfo;
	}
	
	public void resolveReferences() {
		for (AttributeInfo attrInfo : mappedClassInfo.getAttributeInfos()) {
			if (attrInfo instanceof RelationAttributeInfo) {
				createRelationAttributeInfoReferencesResolver((RelationAttributeInfo)attrInfo).resolveReferences();
			}
		}
		buildAdoptedEntityListenerClassInfos();
		buildAdoptedLifecycleEventInfos();
	}

	private RelationAttributeInfoReferencesResolver createRelationAttributeInfoReferencesResolver(RelationAttributeInfo attrInfo) {
		if (attrInfo instanceof ManyToManyAttributeInfo)
			return new ManyToManyAttributeInfoReferencesResolver(oxyUnitInfoBuilder, attrInfo);
		if (attrInfo instanceof OneToManyAttributeInfo)
			return new OneToManyAttributeInfoReferencesResolver(oxyUnitInfoBuilder, attrInfo);
		if (attrInfo instanceof ManyToOneAttributeInfo)
			return new ManyToOneAttributeInfoReferencesResolver(oxyUnitInfoBuilder, attrInfo);
		if (attrInfo instanceof OneToOneAttributeInfo)
			return new OneToOneAttributeInfoReferencesResolver(oxyUnitInfoBuilder, attrInfo);
		return null;
	}

	private void buildAdoptedEntityListenerClassInfos() {
		if (mappedClassInfo.isExcludeSuperclassListeners())
			return;
		
		MappedClassInfo info = mappedClassInfo.getSuperclassInfo();
		
		if (info != null) {
			buildAdoptedEntityListenerClassInfos(info);
		}
	}
	

	private void buildAdoptedEntityListenerClassInfos(MappedClassInfo info) {
		for (EntityListenerClassInfo ci : info.getEntityListenerClassInfos()) {
			mappedClassInfo.getEntityListenerClassInfos().add(ci);
		}
	}

	private void buildAdoptedLifecycleEventInfos() {
		MappedClassInfo info = mappedClassInfo.getSuperclassInfo();
		if (info != null) {
			buildAdoptedLifecycleEventInfos(info);
		}
	}

	private void buildAdoptedLifecycleEventInfos(MappedClassInfo info) {
		for (LifecycleEventInfo lc : info.getLifecycleEventInfos()) {
			if (!mappedClassInfo.containsLifecycleEventInfo(lc))
				mappedClassInfo.getLifecycleEventInfos().add(lc);
		}
	}


}
