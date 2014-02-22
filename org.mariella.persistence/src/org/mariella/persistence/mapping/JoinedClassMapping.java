package org.mariella.persistence.mapping;

import java.util.Collection;
import java.util.List;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.DeleteJoinedTableByPrimaryKeyStatementBuilder;
import org.mariella.persistence.database.JoinedUpsertStatementBuilder;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.database.Table;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;
import org.mariella.persistence.query.BinaryCondition;
import org.mariella.persistence.query.JoinBuilder;
import org.mariella.persistence.query.JoinedSecondaryTable;
import org.mariella.persistence.query.SecondaryTableJoinBuilder;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.PropertyDescription;

public class JoinedClassMapping extends SelectableHierarchyClassMapping {
	private PrimaryKeyJoinColumns primaryKeyJoinColumns = new PrimaryKeyJoinColumns();

	private Table joinTable;
	private Table joinUpdateTable;

public JoinedClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super(schemaMapping, classDescription);
}

public PrimaryKeyJoinColumns getPrimaryKeyJoinColumns() {
	return primaryKeyJoinColumns;
}

public Table getJoinTable() {
	return joinTable;
}

public void setJoinTable(Table joinTable) {
	this.joinTable = joinTable;
}

public Table getJoinUpdateTable() {
	return joinUpdateTable;
}

public void setJoinUpdateTable(Table joinUpdateTable) {
	this.joinUpdateTable = joinUpdateTable;
}

@Override
public Table getMainUpdateTable() {
	return joinUpdateTable != null ? joinUpdateTable : super.getMainUpdateTable();
}

@Override
public void initialize(ClassMappingInitializationContext context) {
	super.initialize(context);
	for(PrimaryKeyJoinColumn primaryKeyJoinColumn : primaryKeyJoinColumns.getPrimaryKeyJoinColumns()) {
		primaryKeyJoinColumn.setPrimaryKeyProperty(getPrimaryKeyPropertyDescription(primaryKeyJoinColumn.getPrimaryTableColumn()));
	}
}

private PropertyDescription getPrimaryKeyPropertyDescription(Column primaryKeyColumn) {
	for(ColumnMapping columnMapping : getPrimaryKey().getColumnMappings()) {
		if(columnMapping.getReadColumn() == primaryKeyColumn || columnMapping.getUpdateColumn() == primaryKeyColumn) {
			return columnMapping.getPropertyDescription();
		}
	}
	throw new IllegalStateException("No primary key property found for column " + primaryKeyColumn);
}

@Override
protected boolean shouldBeContainedBy(ClassMapping classMapping) {
	return classMapping instanceof JoinedClassMapping;
}

@Override
protected JoinBuilder primitiveCreateJoinBuilder(SubSelectBuilder subSelectBuilder, List<SelectableHierarchyClassMapping> affectedChildren, List<SelectableHierarchyClassMapping> selectedChildren) {
	if(isSelectableHierarchyRoot()) {
		JoinedClassMappingJoinBuilder joinBuilder = new JoinedClassMappingJoinBuilder();
		JoinBuilder primaryJoinBuilder = super.primitiveCreateJoinBuilder(subSelectBuilder, affectedChildren, selectedChildren);
		joinBuilder.addJoinBuilder(primaryJoinBuilder);
		updateJoinBuilder(subSelectBuilder, joinBuilder);
		for(SelectableHierarchyClassMapping child : affectedChildren) {
			((JoinedClassMapping)child).updateJoinBuilder(subSelectBuilder, joinBuilder);
		}
		return joinBuilder;
	} else {
		affectedChildren.add(0, this);
		return ((SelectableHierarchyClassMapping)getSuperClassMapping()).primitiveCreateJoinBuilder(subSelectBuilder, affectedChildren, selectedChildren);
	}
}

private void updateJoinBuilder(SubSelectBuilder subSelectBuilder, JoinedClassMappingJoinBuilder joinBuilder) {
	if(!isSelectableHierarchyRoot()) {
		if(joinTable != null) {
			SecondaryTableJoinBuilder joinTableJoinBuilder = new SecondaryTableJoinBuilder(subSelectBuilder);
			JoinedSecondaryTable joinedTable = new JoinedSecondaryTable();
			joinedTable.setTable(joinTable);
			subSelectBuilder.addJoinedTable(joinedTable);
			joinTableJoinBuilder.setSecondaryTable(joinedTable);
			for(PrimaryKeyJoinColumn primaryKeyJoinColumn : primaryKeyJoinColumns.getPrimaryKeyJoinColumns()) {
				joinTableJoinBuilder.getConditionBuilder(primaryKeyJoinColumn.getPrimaryTableColumn()).and(
					BinaryCondition.eq(
						joinBuilder.getPrimaryJoinBuilder().getJoinedTableReference().createColumnReference(primaryKeyJoinColumn.getPrimaryTableColumn()),
						joinedTable.createUnreferencedColumnReference(primaryKeyJoinColumn.getJoinTableColumn())
					)
				);
			}
			joinBuilder.addJoinBuilder(joinTableJoinBuilder);
		} else if(!getClassDescription().isAbstract()) {
			throw new IllegalStateException();
		}
	}
}

@Override
public SingleRowPreparedStatementBuilder getPrimaryPreparedStatementBuilder(ObjectPersistor objectPersistor, PropertyMapping propertyMapping) {
	if(propertyMapping.getPropertyDescription().getClassDescription() == getClassDescription()) {
		if(isSelectableHierarchyRoot()) {
			return super.getPrimaryPreparedStatementBuilder(objectPersistor, propertyMapping);
		} else {
			return objectPersistor.getPrimaryPreparedStatementBuilder(joinUpdateTable);
		}
	} else {
		return getSuperClassMapping().getPrimaryPreparedStatementBuilder(objectPersistor, propertyMapping);
	}
}

@Override
public void createInitialPrimaryPreparedStatementBuilders(ObjectPersistor objectPersistor) {
	if(isSelectableHierarchyRoot()) {
		super.createInitialPrimaryPreparedStatementBuilders(objectPersistor);
	} else {
		if(objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.Removed) {
			objectPersistor.getPrimaryPreparedStatementBuilder(joinUpdateTable);
		}
		getSuperClassMapping().createInitialPrimaryPreparedStatementBuilders(objectPersistor);
		if(objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.New) {
			objectPersistor.getPrimaryPreparedStatementBuilder(joinUpdateTable);
		}
	}
}

@Override
public SingleRowPreparedStatementBuilder createPrimaryPreparedStatementBuilder(ObjectPersistor objectPersistor, Object key, Object discriminatorValue) {
	if(isSelectableHierarchyRoot()) {
		return super.createPrimaryPreparedStatementBuilder(objectPersistor, key, discriminatorValue);
	} else {
		if(key == joinUpdateTable) {
			SingleRowPreparedStatementBuilder primaryPreparedStatementBuilder;
			Row row = new Row(joinUpdateTable);
			if(objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.New || objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.Modified) {
				primaryPreparedStatementBuilder = new JoinedUpsertStatementBuilder(objectPersistor, this, row);
			} else if(objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.Removed) {
				primaryPreparedStatementBuilder = new DeleteJoinedTableByPrimaryKeyStatementBuilder(objectPersistor, this, row);
			} else {
				throw new IllegalStateException();
			}
			return primaryPreparedStatementBuilder;
		} else {
			return ((SelectableHierarchyClassMapping)getSuperClassMapping()).createPrimaryPreparedStatementBuilder(objectPersistor, key, discriminatorValue);
		}
	}
}

@Override
public void initializePrimaryPreparedStatementBuilder(ObjectPersistor objectPersistor, Object key, SingleRowPreparedStatementBuilder primaryPreparedStatementBuilder) {
	if(isSelectableHierarchyRoot()) {
		super.initializePrimaryPreparedStatementBuilder(objectPersistor, key, primaryPreparedStatementBuilder);
	} else if(key != joinUpdateTable) {
		getSuperClassMapping().initializePrimaryPreparedStatementBuilder(objectPersistor, key, primaryPreparedStatementBuilder);
	}
}

public String toString() {
	return getClassDescription().toString() + "(" + (joinTable != null ? joinTable.toString() : getPrimaryTable().toString()) +")";
}

@Override
public void collectUsedTables(Collection<Table> collection) {
	if(isSelectableHierarchyRoot()) {
		super.collectUsedTables(collection);
	} else {
		if(joinTable != null && !collection.contains(joinTable)) {
			collection.add(joinTable);
		}
		if(joinUpdateTable != null && !collection.contains(joinUpdateTable)) {
			collection.add(joinUpdateTable);
		}
		collectUserTablesFromProperties(collection);
	}
}

}
