package org.mariella.persistence.annotations.mapping_builder;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.parser.Entity;

import org.mariella.persistence.annotations.processing.AttributeInfo;
import org.mariella.persistence.annotations.processing.BasicAttributeInfo;
import org.mariella.persistence.annotations.processing.ClassInfo;
import org.mariella.persistence.annotations.processing.EntityInfo;
import org.mariella.persistence.annotations.processing.ManyToManyAttributeInfo;
import org.mariella.persistence.annotations.processing.ManyToOneAttributeInfo;
import org.mariella.persistence.annotations.processing.OneToManyAttributeInfo;
import org.mariella.persistence.annotations.processing.OneToOneAttributeInfo;
import org.mariella.persistence.annotations.processing.OxyUnitInfo;
import org.mariella.persistence.annotations.processing.RelationAttributeInfo;
import org.mariella.persistence.annotations.processing.TableInfo;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Converter;
import at.hts.persistence.database.Table;
import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.CollectionPropertyMapping;
import at.hts.persistence.mapping.ColumnMapping;
import at.hts.persistence.mapping.SingleTableClassMapping;
import at.hts.persistence.runtime.Introspector;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.CollectionPropertyDescription;
import at.hts.persistence.schema.ScalarPropertyDescription;

public class PersistenceBuilder {
	private final OxyUnitInfo unitInfo;
	private final PersistenceInfo persistenceInfo;
	private final DatabaseInfoProvider databaseInfoProvider;
	private final Map<TableInfo, DatabaseTableInfo> tableInfos = new HashMap<TableInfo, DatabaseTableInfo>();
	private final Map<TableInfo, Table> tables = new HashMap<TableInfo, Table>();
	
public PersistenceBuilder(OxyUnitInfo unitInfo, DatabaseInfoProvider databaseInfoProvider) {
	super();
	this.unitInfo = unitInfo;
	persistenceInfo = new PersistenceInfo();
	this.databaseInfoProvider = databaseInfoProvider;
}

public PersistenceInfo getPersistenceInfo() {
	return persistenceInfo;
}

public DatabaseInfoProvider getColumnInfoProvider() {
	return databaseInfoProvider;
}

public void build() {
	for(ClassInfo classInfo : unitInfo.getClassInfos()) {
		if(classInfo instanceof EntityInfo) {
			buildEntity((EntityInfo)classInfo);
		}
	}
}

protected Table getTable(TableInfo tableInfo) {
	Table table = tables.get(tableInfo);
	if(table == null) {
		DatabaseTableInfo dti = databaseInfoProvider.getTableInfo(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName());
		if(dti == null) {
			throw new RuntimeException("Table " + tableInfo.getName() + " has not been found!");
		}
		tableInfos.put(tableInfo, dti);
		table = new Table(dti.getName());
		persistenceInfo.getSchema().addTable(table);
		tables.put(tableInfo, table);
	}
	return table;
}

protected Column getColumn(TableInfo tableInfo, String columnName, ScalarPropertyDescription spd) {
	Table table = getTable(tableInfo);
	DatabaseTableInfo dti = tableInfos.get(tableInfo);
	DatabaseColumnInfo dci = dti.getColumnInfo(columnName);
	if(dci == null) {
		throw new RuntimeException("Column " + table.getName() + "." + columnName + " does not exist!");
	}
	Column column = table.getColumn(dci.getName());
	if(column == null) { 
		Converter<?> converter = getConverter(dti, dci, spd);
		column = new Column(dci.getName(), dci.getType(), dci.isNullable(), converter);
		if(dti.getPrimaryKey().contains(dci)) {
			table.addPrimaryKeyColumn(column);
		} else {
			table.addColumn(column);
		}
	}
	return column;
}

protected Converter<?> getConverter(DatabaseTableInfo dti, DatabaseColumnInfo dci, ScalarPropertyDescription spd) {
	return null;
}


protected TableInfo getTableInfo(EntityInfo entityInfo) {
	if(entityInfo.getTableInfo() != null) {
		return entityInfo.getTableInfo();
	} else  {
		EntityInfo currentEntityInfo = entityInfo.getSuperEntityInfo();
		while(currentEntityInfo != null) {
			if(currentEntityInfo.getTableInfo() != null) {
				return currentEntityInfo.getTableInfo();
			}
			currentEntityInfo = currentEntityInfo.getSuperEntityInfo();
		}
	}
	return null;
}

protected void buildEntity(EntityInfo entityInfo) {
	TableInfo tableInfo = getTableInfo(entityInfo);
	Table table = getTable(tableInfo); 
	
	ClassDescription cd = persistenceInfo.getSchemaDescription().getClassDescription(entityInfo.getClazz().getName());
	if(cd != null) {
		throw new IllegalStateException();
	}
	
	ClassDescription scd = null;
	if(entityInfo.getSuperEntityInfo() != null) {
		scd = persistenceInfo.getSchemaDescription().getClassDescription(entityInfo.getSuperclassInfo().getClazz().getName());
	}
	if(scd == null) {
		cd = new ClassDescription(persistenceInfo.getSchemaDescription(), entityInfo.getClazz().getName());
	} else {
		cd = new ClassDescription(persistenceInfo.getSchemaDescription(), scd, entityInfo.getClazz().getName());
	}
	persistenceInfo.getSchemaDescription().addClassDescription(cd);
	
	ClassMapping cm;
	cm = new SingleTableClassMapping(persistenceInfo.getSchemaMapping(), cd, table.getName());
	persistenceInfo.getSchemaMapping().setClassMapping(cd.getClassName(), cm);
	
	for(AttributeInfo attributeInfo : entityInfo.getAttributeInfos()) {
		if(attributeInfo instanceof BasicAttributeInfo) {
			buildBasicAttribute(entityInfo, cm, (BasicAttributeInfo)attributeInfo);
		} else {
			buildRelationAttributeInfo(entityInfo, cm, (RelationAttributeInfo)attributeInfo);
		}
	}
}

protected void buildBasicAttribute(EntityInfo entityInfo, ClassMapping classMapping, BasicAttributeInfo attributeInfo) {
	if(attributeInfo.getColumnInfo() != null) {
		if(classMapping.getClassDescription().getPropertyDescription(attributeInfo.getName()) != null) {
			throw new IllegalStateException();
		}
		PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
		ScalarPropertyDescription spd = new ScalarPropertyDescription(classMapping.getClassDescription(), propertyDescriptor);
		
		Column column = getColumn(entityInfo.getTableInfo(), attributeInfo.getColumnInfo().getName(), spd);
		ColumnMapping columnMapping = new ColumnMapping(classMapping, spd, column);
		
		classMapping.setPropertyMapping(spd, columnMapping);
	} else {
		System.out.println("No column info for attribute " + classMapping.getClassDescription().getClassName() + "." + attributeInfo.getName());
	}
}

protected void buildRelationAttributeInfo(EntityInfo entityInfo, ClassMapping classMapping, RelationAttributeInfo attributeInfo) {
	if(attributeInfo instanceof ManyToManyAttributeInfo) {
		buildManyToManyAttributeInfo(entityInfo, classMapping, (ManyToManyAttributeInfo)attributeInfo);
	} else if(attributeInfo instanceof OneToManyAttributeInfo) {
		buildOneToManyAttributeInfo(entityInfo, classMapping, (OneToManyAttributeInfo)attributeInfo);
	}  else if(attributeInfo instanceof OneToOneAttributeInfo) {
		buildOneToOneAttributeInfo(entityInfo, classMapping, (OneToOneAttributeInfo)attributeInfo);
	} else if(attributeInfo instanceof ManyToOneAttributeInfo) {
		buildManyToOneAttributeInfo(entityInfo, classMapping, (ManyToOneAttributeInfo)attributeInfo);
	}
}

protected void buildManyToManyAttributeInfo(EntityInfo entityInfo, ClassMapping classMapping, ManyToManyAttributeInfo attributeInfo) {
	throw new IllegalStateException("Many to many relationships are not supported!");
}

protected void buildOneToManyAttributeInfo(EntityInfo entityInfo, ClassMapping classMapping, OneToManyAttributeInfo attributeInfo) {
	PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
	Class<?> referencedClass = attributeInfo.getRelatedEntityInfo().getClazz();

	CollectionPropertyDescription pd;
	if(attributeInfo.getReverseAttributeInfo() != null) {
		pd = new CollectionPropertyDescription(classMapping.getClassDescription(), propertyDescriptor, referencedClass.getName(), attributeInfo.getReverseAttributeInfo().getName());
	} else {
		pd = new CollectionPropertyDescription(classMapping.getClassDescription(), propertyDescriptor, referencedClass.getName());
	}
	classMapping.getClassDescription().addPropertyDescription(pd);
	CollectionPropertyMapping pm = new CollectionPropertyMapping(classMapping, pd);
	classMapping.setPropertyMapping(pd, pm);
}

protected void buildOneToOneAttributeInfo(EntityInfo entityInfo, ClassMapping classMapping, OneToOneAttributeInfo attributeInfo) {
	PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
	Class<?> referencedClass = attributeInfo.getRelatedEntityInfo().getClazz();
}

protected void buildManyToOneAttributeInfo(EntityInfo entityInfo, ClassMapping classMapping, ManyToOneAttributeInfo attributeInfo) {
	PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
	Class<?> referencedClass = attributeInfo.getRelatedEntityInfo().getClazz();

}

}
