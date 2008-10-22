package at.hts.persistence.mapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.Converter;
import at.hts.persistence.persistor.Row;
import at.hts.persistence.query.BinaryCondition;
import at.hts.persistence.query.ColumnReference;
import at.hts.persistence.query.Expression;
import at.hts.persistence.query.InCondition;
import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.util.Util;

public class HierarchyInTableClassMapping extends SingleTableClassMapping {
	private Column discriminatorColumn;
	private Object discriminatorValue;
	private Collection<HierarchyInTableClassMapping> allChildren  = new ArrayList<HierarchyInTableClassMapping>();
	private HierarchyInTableClassMapping containingClassMapping;
	
public HierarchyInTableClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription, String tableName, String discriminatorColumnName, Object discriminatorValue) {
	super(schemaMapping, classDescription, tableName);
	this.discriminatorColumn = primaryTable.getColumn(discriminatorColumnName);
	Util.assertTrue(discriminatorColumn != null, "Unknown discriminator column!");
	this.discriminatorValue = discriminatorValue;
	containingClassMapping = null;
}

public HierarchyInTableClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription, Object discriminatorValue) {
	super(schemaMapping, classDescription);
	if(classDescription.getSuperClassDescription() != null) {
		ClassMapping superClassMapping = schemaMapping.getClassMapping(classDescription.getSuperClassDescription().getClassName());
		if(superClassMapping != null && superClassMapping instanceof HierarchyInTableClassMapping) {
			containingClassMapping = (HierarchyInTableClassMapping)superClassMapping;
			primaryTable = superClassMapping.getPrimaryTable();
			physicalPropertyMappingList = new ArrayList<PhysicalPropertyMapping>();
			propertyMappings = new HashMap<PropertyDescription, PropertyMapping>();
			discriminatorColumn = ((HierarchyInTableClassMapping)superClassMapping).getDiscriminatorColum();
			this.discriminatorValue = discriminatorValue;
			containingClassMapping.addContainedClassMapping(this);
		}
	}
	Util.assertTrue(primaryTable != null, "No mapping found for superclass");
}

public HierarchyInTableClassMapping getContainingClassMapping() {
	return containingClassMapping;
}

public boolean isRoot() {
	return containingClassMapping == null;
}

private void addContainedClassMapping(HierarchyInTableClassMapping classMapping) {
	allChildren.add(classMapping);
	if(containingClassMapping != null) {
		containingClassMapping.addContainedClassMapping(classMapping);
	}
}

public Column getDiscriminatorColum() {
	return discriminatorColumn;
}

public Object getDiscriminatorValue() {
	return discriminatorValue;
}

public void collectUsedColumns(Collection<Column> collection) {
	super.collectUsedColumns(collection);
	if(!collection.contains(discriminatorColumn)) {
		collection.add(discriminatorColumn);
	}
}

@Override
@SuppressWarnings("unchecked")
public TableReference join(SubSelectBuilder subSelectBuilder) {
	TableReference tr = super.join(subSelectBuilder);
	if(!isRoot()) {
		if(allChildren.isEmpty()) {
			subSelectBuilder.and(
				BinaryCondition.eq(
					new ColumnReference(tr, getDiscriminatorColum()),
					((Converter<Object>)getDiscriminatorColum().getConverter()).createLiteral(getDiscriminatorValue())
				)
			);
		} else {
			List<Expression> in = new ArrayList<Expression>();
			for(HierarchyInTableClassMapping child : allChildren) {
				if(!child.getClassDescription().isAbstract()) {
					in.add(((Converter<Object>)getDiscriminatorColum().getConverter()).createLiteral(child.getDiscriminatorValue()));
				}
			}
			subSelectBuilder.and(
				new InCondition(
					new ColumnReference(tr, getDiscriminatorColum()),
					in
				)
			);
		}
	}
	return tr;
}

public HierarchyInTableClassMapping getClassMappingForDiscriminatorValue(Object value) {
	if(discriminatorValue != null && discriminatorValue.equals(value)) {
		return this;
	} else {
		for(HierarchyInTableClassMapping child : allChildren) {
			if(!child.getClassDescription().isAbstract() && child.getDiscriminatorValue().equals(value)) {
				return child;
			}
		}
		return null;
	}
}

@Override
public void addObjectColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference) {
	subSelectBuilder.addSelectItem(tableReference, discriminatorColumn);
	super.addObjectColumns(subSelectBuilder, tableReference);
}

@Override
public Object createObject(ResultSetReader reader, ObjectFactory factory, boolean wantsObjects) throws SQLException {
	if(wantsObjects) {
		Object value = discriminatorColumn.getObject(reader.getResultSet(), reader.getCurrentColumnIndex());
		reader.setCurrentColumnIndex(reader.getCurrentColumnIndex() + 1);
		HierarchyInTableClassMapping effectiveMapping = getClassMappingForDiscriminatorValue(value);
		return effectiveMapping.primitiveCreateObject(reader, factory, wantsObjects);
	} else {
		return primitiveCreateObject(reader, factory, wantsObjects);
	}
}

protected Object primitiveCreateObject(ResultSetReader reader, ObjectFactory factory, boolean wantsObjects) throws SQLException { 
	return super.createObject(reader, factory, wantsObjects);
}


@Override
public Row createPrimaryRow() {
	Row row = super.createPrimaryRow();
	row.setProperty(discriminatorColumn, discriminatorValue);
	return row;
}

}
