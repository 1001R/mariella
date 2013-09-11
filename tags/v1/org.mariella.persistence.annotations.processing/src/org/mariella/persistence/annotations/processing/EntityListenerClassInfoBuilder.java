package org.mariella.persistence.annotations.processing;

import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.mapping.EntityListenerClassInfo;
import org.mariella.persistence.mapping.MappedClassInfo;
import org.mariella.persistence.mapping.OxyUnitInfo;

public class EntityListenerClassInfoBuilder {
	Class<?> listenerClazz;
	List<MappedClassInfo> usingMappedClassInfos = new ArrayList<MappedClassInfo>();
	OxyUnitInfo oxyUnitInfo;
	
	public EntityListenerClassInfoBuilder(Class<?> listenerClazz, OxyUnitInfo oxyUnitInfo) {
		this.listenerClazz = listenerClazz;
		this.oxyUnitInfo = oxyUnitInfo;
	}
	
	void build() {
		EntityListenerClassInfo info = new EntityListenerClassInfo();
		info.setClazz(listenerClazz);
		info.setOxyUnitInfo(oxyUnitInfo);
		
		new ClassInfoLifecycleEventInfosBuilder(info).buildLifecycleEventInfos();
		
		oxyUnitInfo.getClassToInfoMap().put(listenerClazz.getName(), info);
		for (MappedClassInfo mappedClassInfo : usingMappedClassInfos) {
			mappedClassInfo.getEntityListenerClassInfos().add(info);
		}
	}

}
