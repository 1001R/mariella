package org.mariella.persistence.annotations.processing.test;

import java.util.List;

import org.junit.Test;
import org.mariella.persistence.annotations.processing.OxyUnitInfo;
import org.mariella.persistence.annotations.processing.OxyUnitInfoBuilder;

public class ParseAnnotationsTest {
	
@Test
public void parseAnnotations() throws Exception {
	OxyUnitInfoBuilder b = new OxyUnitInfoBuilder();
	b.setBundle(Activator.getDefault().getBundle());
	b.build();
	
	List<OxyUnitInfo> infos = b.getOxyUnitInfos();
	for (OxyUnitInfo info : infos) {
		info.debugPrint(System.out);
	}
}

}
