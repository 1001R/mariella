package org.mariella.persistence.annotations.processing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.IdClass;

// TODO finally check if each EntityInfo has an id attribute
@SuppressWarnings("unchecked")
public class EntityInfo extends MappedClassInfo {

	private Class compositeIdClass;
	private TableInfo tableInfo;

@Override
void buildAttributeInfos() throws Exception {
	// TODO Auto-generated method stub
	super.buildAttributeInfos();
	buildAdoptedAttributeInfos();
	IdClass idClass = (IdClass)getClazz().getAnnotation(IdClass.class);
	if (idClass != null)
		compositeIdClass = idClass.value();
}

private void buildAdoptedAttributeInfos() throws Exception {
	MappedClassInfo info = this.getSuperclassInfo();
	while (info instanceof MappedSuperclassInfo) {
		buildAdoptedAttributeInfos(info);
		info = info.getSuperclassInfo();
	}
}

private void buildAdoptedAttributeInfos(MappedClassInfo info) {
	for (AttributeInfo attrInfo : info.getAttributeInfos()) {
		AttributeInfo adopted = attrInfo.copyForAdoption();
		adopted.setParentClassInfo(this);
		addAttributeInfo(adopted);
	}
}

public String getName() {
	String name = ((Entity)getAnnotation()).name();
	if (name.length() > 0)
		return name;
	return clazz.getSimpleName();
}

public EntityInfo getSuperEntityInfo() {
	MappedClassInfo superInfo = getSuperclassInfo();
	while (superInfo != null) {
		if (superInfo instanceof EntityInfo)
			return (EntityInfo)superInfo;
		superInfo = superInfo.getSuperclassInfo();
	}
	return null;
}

public Class getCompositeIdClass() {
	return compositeIdClass;
}

public List<BasicAttributeInfo>getBasicAttributeInfos() {
	List<BasicAttributeInfo> result = new ArrayList<BasicAttributeInfo>();
	for (AttributeInfo ai : getAttributeInfos()) {
		if (ai instanceof BasicAttributeInfo)
			result.add((BasicAttributeInfo)ai);
	}
	return result;
}

public BasicAttributeInfo getAttributeInfoHavingColName(String referencedColumnName) {
	for (AttributeInfo ai : getBasicAttributeInfos()) {
		if (ai instanceof BasicAttributeInfo) {
			if (((BasicAttributeInfo)ai).getColumnInfo() != null && ((BasicAttributeInfo)ai).getColumnInfo().getName().equals(referencedColumnName))
				return (BasicAttributeInfo)ai;
		}
	}
	if (getSuperEntityInfo() != null)
		return getSuperEntityInfo().getAttributeInfoHavingColName(referencedColumnName);
	return null;
}

public TableInfo getTableInfo() {
	return tableInfo;
}

void setTableInfo(TableInfo tableInfo) {
	this.tableInfo = tableInfo;
}

}
