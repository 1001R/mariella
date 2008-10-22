package org.mariella.persistence.annotations.mapping_builder;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.InheritanceType;

import org.eclipse.core.runtime.Assert;
import org.mariella.persistence.annotations.processing.AttributeInfo;
import org.mariella.persistence.annotations.processing.BasicAttributeInfo;
import org.mariella.persistence.annotations.processing.ClassInfo;
import org.mariella.persistence.annotations.processing.ColumnInfo;
import org.mariella.persistence.annotations.processing.DiscriminatorColumnInfo;
import org.mariella.persistence.annotations.processing.EntityInfo;
import org.mariella.persistence.annotations.processing.JoinColumnInfo;
import org.mariella.persistence.annotations.processing.ManyToManyAttributeInfo;
import org.mariella.persistence.annotations.processing.ManyToOneAttributeInfo;
import org.mariella.persistence.annotations.processing.OneToManyAttributeInfo;
import org.mariella.persistence.annotations.processing.OneToOneAttributeInfo;
import org.mariella.persistence.annotations.processing.OxyUnitInfo;
import org.mariella.persistence.annotations.processing.RelationAttributeInfo;
import org.mariella.persistence.annotations.processing.TableInfo;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Converter;
import at.hts.persistence.database.StringConverter;
import at.hts.persistence.database.Table;
import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.CollectionAsTablePropertyMapping;
import at.hts.persistence.mapping.CollectionPropertyMapping;
import at.hts.persistence.mapping.ColumnMapping;
import at.hts.persistence.mapping.HierarchyInTableClassMapping;
import at.hts.persistence.mapping.ReferencePropertyMapping;
import at.hts.persistence.mapping.SingleTableClassMapping;
import at.hts.persistence.runtime.Introspector;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.CollectionPropertyDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.ReferencePropertyDescription;
import at.hts.persistence.schema.RelationshipPropertyDescription;
import at.hts.persistence.schema.ScalarPropertyDescription;

public class PersistenceBuilder {
	private final OxyUnitInfo unitInfo;
	private final PersistenceInfo persistenceInfo;
	private final DatabaseInfoProvider databaseInfoProvider;
	private final Map<TableInfo, DatabaseTableInfo> tableInfos = new HashMap<TableInfo, DatabaseTableInfo>();
	private final Map<TableInfo, Table> tables = new HashMap<TableInfo, Table>();
	
	private ConverterRegistry converterRegistry = new ConverterRegistryImpl();
	
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

public ConverterRegistry getConverterRegistry() {
	return converterRegistry;
}

public void setConverterRegistry(ConverterRegistry converterRegistry) {
	this.converterRegistry = converterRegistry;
}

public void build() {
	for(ClassInfo classInfo : unitInfo.getHierarchyOrderedClassInfos()) {
		if(classInfo instanceof EntityInfo) {
			buildEntityDescription((EntityInfo)classInfo);
		}
	}

	for(ClassInfo classInfo : unitInfo.getHierarchyOrderedClassInfos()) {
		if(classInfo instanceof EntityInfo) {
			buildEntityRelationshipDescriptions((EntityInfo)classInfo);
		}
	}

	for(ClassInfo classInfo : unitInfo.getHierarchyOrderedClassInfos()) {
		if(classInfo instanceof EntityInfo) {
			ClassDescription cd = persistenceInfo.getSchemaDescription().getClassDescription(classInfo.getClazz().getName());
			cd.setAbstract(classInfo.isAbstract());
			cd.initialize();
		}
	}

	for(ClassInfo classInfo : unitInfo.getHierarchyOrderedClassInfos()) {
		if(classInfo instanceof EntityInfo) {
			buildEntityMapping((EntityInfo)classInfo);
		}
	}

	for(ClassInfo classInfo : unitInfo.getHierarchyOrderedClassInfos()) {
		if(classInfo instanceof EntityInfo) {
			buildEntityRelationshipMappings((EntityInfo)classInfo);
		}
	}
}

protected Table getTable(TableInfo tableInfo) {
	Table table = tables.get(tableInfo);
	if(table == null) {
		table = getTable(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName());
		DatabaseTableInfo dti = databaseInfoProvider.getTableInfo(tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName());
		System.out.println("Loaded info from DB: " + dti.getName());
		tableInfos.put(tableInfo, dti);
		tables.put(tableInfo, table);
	}
	return table;
}

private Table getTable(String catalog, String schema, String name) {
	Table table = persistenceInfo.getSchema().getTable(name);
	if(table == null) {
		DatabaseTableInfo dti = databaseInfoProvider.getTableInfo(catalog, schema, name);
		if(dti == null) {
			throw new RuntimeException("Table " + name + " has not been found!");
		}
		table = new Table(dti.getCatalog(), dti.getSchema(), dti.getName());
		persistenceInfo.getSchema().addTable(table);
	}
	return table;
}

protected Column getColumn(TableInfo tableInfo, BasicAttributeInfo attributeInfo, ScalarPropertyDescription spd) {
	Table table = getTable(tableInfo);
	DatabaseTableInfo dti = tableInfos.get(tableInfo);
	DatabaseColumnInfo dci = dti.getColumnInfo(attributeInfo.getColumnInfo().getName());
	Assert.isNotNull(dci, "No database column info for column " + attributeInfo.getColumnInfo().getName());
	Converter<?> converter;
	if(attributeInfo.getConverterName() != null) {
		converter = converterRegistry.getNamedConverter(attributeInfo.getConverterName());
	} else {
		converter = converterRegistry.getConverterForColumn(spd, dti, dci);
	}
	return getColumn(table, attributeInfo.getColumnInfo().getName(), converter);
}

protected Column getColumn(TableInfo tableInfo, ColumnInfo columnInfo, Converter<?> converter) {
	Table table = getTable(tableInfo);
	@SuppressWarnings("unused")
	DatabaseTableInfo dti = tableInfos.get(tableInfo);
	return getColumn(table, columnInfo.getName(), converter);
}

protected Column getColumn(TableInfo tableInfo, DiscriminatorColumnInfo discriminatorColumnInfo) {
	Table table = getTable(tableInfo);
	return getColumn(table, discriminatorColumnInfo.getName(), StringConverter.Singleton);
}

protected Column getColumn(Table table, String columnName, Converter<?> converter) {
	DatabaseTableInfo dti = databaseInfoProvider.getTableInfo(table.getCatalog(), table.getSchema(), table.getName());
	DatabaseColumnInfo dci = dti.getColumnInfo(columnName);
	if(dci == null) {
		throw new RuntimeException("Column " + table.getName() + "." + columnName + " does not exist!");
	}
	Column column = table.getColumn(dci.getName());
	if(column == null) {
		column = new Column(dci.getName(), dci.getType(), dci.isNullable(), converter);
		if(dti.getPrimaryKey().contains(dci)) {
			table.addPrimaryKeyColumn(column);
		} else {
			table.addColumn(column);
		}
	}
	return column;
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

protected void buildEntityDescription(EntityInfo entityInfo) {
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

	for(AttributeInfo attributeInfo : entityInfo.getAttributeInfos()) {
		if(attributeInfo instanceof BasicAttributeInfo) {
			buildBasicAttributeDescription(entityInfo, cd, (BasicAttributeInfo)attributeInfo);
		}
	}
}

protected void buildEntityRelationshipDescriptions(EntityInfo entityInfo) {
	ClassDescription cd = persistenceInfo.getSchemaDescription().getClassDescription(entityInfo.getClazz().getName());
	for(AttributeInfo attributeInfo : entityInfo.getAttributeInfos()) {
		if(!(attributeInfo instanceof BasicAttributeInfo)) {
			buildRelationAttributeDescription(entityInfo, cd, (RelationAttributeInfo)attributeInfo);
		}
	}
}

protected void buildEntityMapping(EntityInfo entityInfo) {
	ClassDescription cd = persistenceInfo.getSchemaDescription().getClassDescription(entityInfo.getClazz().getName());
	
	TableInfo tableInfo = getTableInfo(entityInfo);
	Table table = getTable(tableInfo); 
	
	ClassMapping cm;
	if(entityInfo.getInheritanceInfo() != null) {
		if(entityInfo.getInheritanceInfo().getStrategy() == InheritanceType.JOINED) {
			Column discriminatorColumn = null;
			if(entityInfo.getDiscriminatorColumnInfo() != null) {
				discriminatorColumn = getColumn(tableInfo, entityInfo.getDiscriminatorColumnInfo());
			}
			
			String discriminatorValue = null;
			if(entityInfo.getDiscriminatorValueInfo() != null) {
				discriminatorValue = entityInfo.getDiscriminatorValueInfo().getValue();
			}
			
			if(discriminatorColumn != null) {
				cm = new HierarchyInTableClassMapping(persistenceInfo.getSchemaMapping(), cd, table.getName(), discriminatorColumn.getName(), discriminatorValue);
			} else {
				cm = new HierarchyInTableClassMapping(persistenceInfo.getSchemaMapping(), cd, discriminatorValue);
			}
		} else {
			throw new IllegalStateException("Inheritance strategy " + entityInfo.getInheritanceInfo().getStrategy().toString() + " is not supported!");
		}
	} else {
		cm = new SingleTableClassMapping(persistenceInfo.getSchemaMapping(), cd, table.getName());
	}
	persistenceInfo.getSchemaMapping().setClassMapping(cd.getClassName(), cm);
	
	for(PropertyDescription pd : cd.getPropertyDescriptions()) {
		if(pd instanceof ScalarPropertyDescription) {
			BasicAttributeInfo attributeInfo = (BasicAttributeInfo)getAttributeInfo(cd, pd.getPropertyDescriptor().getName());
			buildBasicAttributeMapping(entityInfo, cm, attributeInfo);
		}
	}
}

protected void buildEntityRelationshipMappings(EntityInfo entityInfo) {
	ClassMapping cm = persistenceInfo.getSchemaMapping().getClassMapping(entityInfo.getClazz().getName());
	for(PropertyDescription pd : cm.getClassDescription().getPropertyDescriptions()) {
		if(pd instanceof RelationshipPropertyDescription) {
			RelationAttributeInfo attributeInfo = (RelationAttributeInfo)getAttributeInfo(cm.getClassDescription(), pd.getPropertyDescriptor().getName());
			buildRelationAttributeMapping(entityInfo, cm, (RelationAttributeInfo)attributeInfo);
		}
	}
}

protected void buildBasicAttributeDescription(EntityInfo entityInfo, ClassDescription classDescription, BasicAttributeInfo attributeInfo) {
	if(attributeInfo.getColumnInfo() != null) {
		if(classDescription.getPropertyDescription(attributeInfo.getName()) != null) {
			throw new IllegalStateException();
		}
		PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
		ScalarPropertyDescription spd = new ScalarPropertyDescription(classDescription, propertyDescriptor);
		if(attributeInfo.isId()) {
			classDescription.setId(spd);
		}
		classDescription.addPropertyDescription(spd);
	} else {
		System.out.println("No column info for attribute " + classDescription.getClassName() + "." + attributeInfo.getName());
	}
}


protected void buildBasicAttributeMapping(EntityInfo entityInfo, ClassMapping classMapping, BasicAttributeInfo attributeInfo) {
	ScalarPropertyDescription spd = (ScalarPropertyDescription)classMapping.getClassDescription().getPropertyDescription(attributeInfo.getName());
	Column column = getColumn(getTableInfo(entityInfo), attributeInfo, spd);
	ColumnMapping columnMapping = new ColumnMapping(classMapping, spd, column);
	classMapping.setPropertyMapping(spd, columnMapping);
}

protected void buildRelationAttributeMapping(EntityInfo entityInfo, ClassMapping classMapping, RelationAttributeInfo attributeInfo) {
	if(attributeInfo instanceof ManyToManyAttributeInfo) {
		buildManyToManyAttributeMapping(entityInfo, classMapping, (ManyToManyAttributeInfo)attributeInfo);
	} else if(attributeInfo instanceof OneToManyAttributeInfo) {
		buildOneToManyAttributeMapping(entityInfo, classMapping, (OneToManyAttributeInfo)attributeInfo);
	}  else if(attributeInfo instanceof OneToOneAttributeInfo) {
		buildOneToOneAttributeMapping(entityInfo, classMapping, (OneToOneAttributeInfo)attributeInfo);
	} else if(attributeInfo instanceof ManyToOneAttributeInfo) {
		buildManyToOneAttributeMapping(entityInfo, classMapping, (ManyToOneAttributeInfo)attributeInfo);
	}
}

protected void buildRelationAttributeDescription(EntityInfo entityInfo, ClassDescription classDescription, RelationAttributeInfo attributeInfo) {
	if(attributeInfo instanceof ManyToManyAttributeInfo) {
		buildManyToManyAttributeDescription(entityInfo, classDescription, (ManyToManyAttributeInfo)attributeInfo);
	} else if(attributeInfo instanceof OneToManyAttributeInfo) {
		buildOneToManyAttributeDescription(entityInfo, classDescription, (OneToManyAttributeInfo)attributeInfo);
	}  else if(attributeInfo instanceof OneToOneAttributeInfo) {
		buildOneToOneAttributeDescription(entityInfo, classDescription, (OneToOneAttributeInfo)attributeInfo);
	} else if(attributeInfo instanceof ManyToOneAttributeInfo) {
		buildManyToOneAttributeDescription(entityInfo, classDescription, (ManyToOneAttributeInfo)attributeInfo);
	}
}

protected void buildManyToManyAttributeDescription(EntityInfo entityInfo, ClassDescription classDescription, ManyToManyAttributeInfo attributeInfo) {
	throw new IllegalStateException("Many to many relationships are not supported!");
}

protected void buildManyToManyAttributeMapping(EntityInfo entityInfo, ClassMapping classMapping, ManyToManyAttributeInfo attributeInfo) {
	throw new IllegalStateException("Many to many relationships are not supported!");
}

protected void buildOneToManyAttributeDescription(EntityInfo entityInfo, ClassDescription classDescription, OneToManyAttributeInfo attributeInfo) {
	PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
	Class<?> referencedClass = attributeInfo.getRelatedEntityInfo().getClazz();
	
	CollectionPropertyDescription pd;
	if(attributeInfo.getReverseAttributeInfo() != null) {
		pd = new CollectionPropertyDescription(classDescription, propertyDescriptor, referencedClass.getName(), attributeInfo.getReverseAttributeInfo().getName());
	} else {
		pd = new CollectionPropertyDescription(classDescription, propertyDescriptor, referencedClass.getName());
	}
	classDescription.addPropertyDescription(pd);
}

protected void buildOneToManyAttributeMapping(EntityInfo entityInfo, ClassMapping classMapping, OneToManyAttributeInfo attributeInfo) {
	CollectionPropertyDescription pd = (CollectionPropertyDescription)classMapping.getClassDescription().getPropertyDescription(attributeInfo.getName());
	Class<?> referencedClass = attributeInfo.getRelatedEntityInfo().getClazz();
	ClassMapping referencedClassMapping = classMapping.getSchemaMapping().getClassMapping(referencedClass.getName());
	
	if(attributeInfo.getJoinTableInfo() != null) {
		Table joinTable = getTable(attributeInfo.getJoinTableInfo().getCatalog(), attributeInfo.getJoinTableInfo().getSchema(), attributeInfo.getJoinTableInfo().getName());
		if(attributeInfo.getJoinTableInfo().getJoinColumnInfos().size() != 1) {
			throw new IllegalStateException("One one join column is allowed per join table " + attributeInfo.getJoinTableInfo() + ")!");
		}
		JoinColumnInfo jci = attributeInfo.getJoinTableInfo().getJoinColumnInfos().get(0);
		Column joinColumn = getColumn(joinTable, jci.getName(), classMapping.getIdMapping().getColumn().getConverter());

		if(attributeInfo.getJoinTableInfo().getInverseJoinColumnInfos().size() != 1) {
			throw new IllegalStateException("One one inverse join column is allowed per join table " + attributeInfo.getJoinTableInfo() + ")!");
		}
		JoinColumnInfo ijci = attributeInfo.getJoinTableInfo().getInverseJoinColumnInfos().get(0);
		Column inverseJoinColumn = getColumn(joinTable, ijci.getName(), referencedClassMapping.getIdMapping().getColumn().getConverter());
		
		CollectionAsTablePropertyMapping pm = new CollectionAsTablePropertyMapping(
														classMapping, 
														pd, 
														joinTable.getName(),
														joinColumn.getName(),
														inverseJoinColumn.getName()
												);		
		classMapping.setPropertyMapping(pd, pm);
	} else if(attributeInfo.getReverseAttributeInfo() != null) {
		CollectionPropertyMapping pm = new CollectionPropertyMapping(classMapping, pd);
		classMapping.setPropertyMapping(pd, pm);
	} else {
		throw new IllegalStateException();
	}
}

protected void buildOneToOneAttributeDescription(EntityInfo entityInfo, ClassDescription classDescription, OneToOneAttributeInfo attributeInfo) {
	PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
	
	ReferencePropertyDescription pd;
	if(attributeInfo.getReverseAttributeInfo() != null) {
		pd = new ReferencePropertyDescription(classDescription, propertyDescriptor, attributeInfo.getReverseAttributeInfo().getName());
	} else {
		pd = new ReferencePropertyDescription(classDescription, propertyDescriptor);
	}
	classDescription.addPropertyDescription(pd);
}

protected void buildOneToOneAttributeMapping(EntityInfo entityInfo, ClassMapping classMapping, OneToOneAttributeInfo attributeInfo) {
	Class<?> referencedClass = attributeInfo.getRelatedEntityInfo().getClazz();
	ClassMapping referencedClassMapping = classMapping.getSchemaMapping().getClassMapping(referencedClass.getName());
	
	ReferencePropertyDescription pd = (ReferencePropertyDescription)classMapping.getClassDescription().getPropertyDescription(attributeInfo.getName());
	
	if(attributeInfo.getJoinColumnInfos() != null && attributeInfo.getJoinColumnInfos().size() > 0) {
		if(attributeInfo.getJoinColumnInfos().size() != 1) {
			throw new IllegalStateException("One one join column is allowed (" + attributeInfo.toString() + ")!");
		}
		JoinColumnInfo jci = attributeInfo.getJoinColumnInfos().get(0);
		Column joinColumn = getColumn(classMapping.getPrimaryTable(), jci.getName(), referencedClassMapping.getIdMapping().getColumn().getConverter());
		ReferencePropertyMapping pm = new ReferencePropertyMapping(classMapping, pd, joinColumn.getName());
		classMapping.setPropertyMapping(pd, pm);
	} else {
		ReferencePropertyMapping pm = new ReferencePropertyMapping(classMapping, pd);
		classMapping.setPropertyMapping(pd, pm);
	}
}

protected void buildManyToOneAttributeDescription(EntityInfo entityInfo, ClassDescription classDescription, ManyToOneAttributeInfo attributeInfo) {
	PropertyDescriptor propertyDescriptor = Introspector.Singleton.getBeanInfo(entityInfo.getClazz()).getPropertyDescriptor(attributeInfo.getName());
	
	ReferencePropertyDescription pd;
	if(attributeInfo.getReverseAttributeInfo() != null) {
		pd = new ReferencePropertyDescription(classDescription, propertyDescriptor, attributeInfo.getReverseAttributeInfo().getName());
	} else {
		pd = new ReferencePropertyDescription(classDescription, propertyDescriptor);
	}
	classDescription.addPropertyDescription(pd);
}

protected void buildManyToOneAttributeMapping(EntityInfo entityInfo, ClassMapping classMapping, ManyToOneAttributeInfo attributeInfo) {
	Class<?> referencedClass = attributeInfo.getRelatedEntityInfo().getClazz();
	ClassMapping referencedClassMapping = classMapping.getSchemaMapping().getClassMapping(referencedClass.getName());
	
	ReferencePropertyDescription pd = (ReferencePropertyDescription )classMapping.getClassDescription().getPropertyDescription(attributeInfo.getName());
	
	if(attributeInfo.getJoinColumnInfos() != null && attributeInfo.getJoinColumnInfos().size() > 0) {
		if(attributeInfo.getJoinColumnInfos().size() != 1) {
			throw new IllegalStateException("One one join column is allowed (" + attributeInfo.toString() + ")!");
		}
		JoinColumnInfo jci = attributeInfo.getJoinColumnInfos().get(0);
		Column joinColumn = getColumn(classMapping.getPrimaryTable(), jci.getName(), referencedClassMapping.getIdMapping().getColumn().getConverter());
		ReferencePropertyMapping pm = new ReferencePropertyMapping(classMapping, pd, joinColumn.getName());
		classMapping.setPropertyMapping(pd, pm);
	} else {
		ReferencePropertyMapping pm = new ReferencePropertyMapping(classMapping, pd);
		classMapping.setPropertyMapping(pd, pm);
	}
}

private AttributeInfo getAttributeInfo(ClassDescription cd, String attributeName) {
	ClassInfo classInfo = unitInfo.getClassInfo(cd.getClassName());
	if(classInfo instanceof EntityInfo) {
		return ((EntityInfo)classInfo).lookupAttributeInfo(attributeName);
	}
	return null;
}

}
