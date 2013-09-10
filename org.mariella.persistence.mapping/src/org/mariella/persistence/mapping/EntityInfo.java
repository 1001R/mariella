package org.mariella.persistence.mapping;

import java.util.ArrayList;
import java.util.List;

// TODO finally check if each EntityInfo has an id attribute
@SuppressWarnings("unchecked")
public class EntityInfo extends MappedClassInfo {

	private Class compositeIdClass;
	private TableInfo tableInfo;
	private UpdateTableInfo updateTableInfo;
	private DiscriminatorColumnInfo discriminatorColumnInfo;
	private DiscriminatorValueInfo discriminatorValueInfo;
	private List<PrimaryKeyJoinColumnInfo> primaryKeyJoinColumnInfos = new ArrayList<PrimaryKeyJoinColumnInfo>();

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

public void setTableInfo(TableInfo tableInfo) {
	this.tableInfo = tableInfo;
}

public UpdateTableInfo getUpdateTableInfo() {
	return updateTableInfo;
}

public void setUpdateTableInfo(UpdateTableInfo updateTableInfo) {
	this.updateTableInfo = updateTableInfo;
}

public DiscriminatorColumnInfo getDiscriminatorColumnInfo() {
	return discriminatorColumnInfo;
}

public void setDiscriminatorColumnInfo(DiscriminatorColumnInfo discriminatorColumnInfo) {
	this.discriminatorColumnInfo = discriminatorColumnInfo;
}

public DiscriminatorValueInfo getDiscriminatorValueInfo() {
	return discriminatorValueInfo;
}

public void setDiscriminatorValueInfo(
		DiscriminatorValueInfo discriminatorValueInfo) {
	this.discriminatorValueInfo = discriminatorValueInfo;
}

public List<PrimaryKeyJoinColumnInfo> getPrimaryKeyJoinColumnInfos() {
	return primaryKeyJoinColumnInfos;
}

public void setPrimaryKeyJoinColumnInfos(List<PrimaryKeyJoinColumnInfo> primaryKeyJoinColumnInfos) {
	this.primaryKeyJoinColumnInfos = primaryKeyJoinColumnInfos;
}

public void setCompositeIdClass(Class compositeIdClass) {
	this.compositeIdClass = compositeIdClass;
}

}
