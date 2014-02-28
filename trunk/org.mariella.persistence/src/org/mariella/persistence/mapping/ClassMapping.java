package org.mariella.persistence.mapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.DeleteByPrimaryKeyStatementBuilder;
import org.mariella.persistence.database.SingleRowPreparedStatementBuilder;
import org.mariella.persistence.database.Table;
import org.mariella.persistence.database.UpdateStatementBuilder;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.PrimaryInsertStatementBuilder;
import org.mariella.persistence.persistor.PropertyChooser;
import org.mariella.persistence.persistor.Row;
import org.mariella.persistence.query.JoinBuilder;
import org.mariella.persistence.query.JoinBuilderImpl;
import org.mariella.persistence.query.JoinedTable;
import org.mariella.persistence.query.SelectItem;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.runtime.ModificationInfo;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.PropertyDescription;

public abstract class ClassMapping extends AbstractClassMapping {
	private PrimaryKey primaryKey;
	
	protected Table primaryTable;
	protected Table primaryUpdateTable;

	private final Map<PropertyDescription, PropertyMapping> hierarchyPropertyMappingMap = new HashMap<PropertyDescription, PropertyMapping>();
	private final List<PropertyMapping> hierarchyPropertyMappings = new ArrayList<PropertyMapping>();
	private final List<PhysicalPropertyMapping> hierarchyPhysicalPropertyMappingList = new ArrayList<PhysicalPropertyMapping>();

	private List<ClassMapping> immediateChildren;
	private List<ClassMapping> allChildren;
	
public ClassMapping(SchemaMapping schemaMapping, ClassDescription classDescription) {
	super(schemaMapping, classDescription);
}

public Table getPrimaryTable() {
	return primaryTable;
}

public Table getPrimaryUpdateTable() {
	return primaryUpdateTable;
}

public void setPrimaryTable(Table primaryTable) {
	this.primaryTable = primaryTable;
}

public void setPrimaryUpdateTable(Table primaryUpdateTable) {
	this.primaryUpdateTable = primaryUpdateTable;
}

public Table getMainUpdateTable() {
	return primaryUpdateTable;
}

public List<PropertyMapping> getHierarchyPropertyMappings(){
	return hierarchyPropertyMappings;
}

public List<PhysicalPropertyMapping> getHierarchyPhysicalPropertyMappingList(){
	return hierarchyPhysicalPropertyMappingList;
}

public PropertyMapping getPropertyMappingInHierarchy(PropertyDescription propertyDescription) {
	return hierarchyPropertyMappingMap.get(propertyDescription);
}

public List<ClassMapping> getImmediateChildren() {
	return immediateChildren;
}

public List<ClassMapping> getAllChildren() {
	return allChildren;
}

public PrimaryKey getPrimaryKey() {
	return primaryKey;
}

public ColumnMapping getColumnMapping(Column column) {
	for(PhysicalPropertyMapping pm : physicalPropertyMappingList) {
		if(pm instanceof ColumnMapping && (((ColumnMapping)pm).getReadColumn() == column) || ((ColumnMapping)pm).getUpdateColumn() == column) {
			return (ColumnMapping)pm;
		}
	}
	return null;
}

public boolean isChildOf(ClassMapping classMapping) {
	ClassMapping cm = getSuperClassMapping();
	while(cm != null && cm != classMapping) {
		cm = cm.getSuperClassMapping();
	}
	return cm != null;
}

@Override
public void initialize(ClassMappingInitializationContext context) {
	super.initialize(context);

	immediateChildren = new ArrayList<ClassMapping>();
	allChildren = new ArrayList<ClassMapping>();
	
	for(ClassMapping classMapping : getSchemaMapping().getClassMappings()) {
		if(classMapping.isChildOf(this)) {
			allChildren.add(classMapping);
			if(classMapping.getSuperClassMapping() == this) {
				immediateChildren.add(classMapping);
			}
		}
	}

	if(getSuperClassMapping() == null) {
		if(getClassDescription().getIdentityPropertyDescriptions() == null) {
			throw new IllegalStateException("No @Id specified for class " + getClassDescription().getClassName());
		}
		List<ColumnMapping> columnMappings = new ArrayList<ColumnMapping>();
		for(PhysicalPropertyMapping propertyMapping : getPhysicalPropertyMappingList()) {
			if(getClassDescription().isId(propertyMapping.getPropertyDescription())) {
				columnMappings.add((ColumnMapping)propertyMapping);	
			}
		}
		primaryKey = new PrimaryKey(this, columnMappings.toArray(new ColumnMapping[columnMappings.size()]));
	} else {
		primaryKey = getSuperClassMapping().getPrimaryKey();
	}
}	

public void postInitialize(ClassMappingInitializationContext context) {
	super.postInitialize(context);
	for(PropertyMapping pm : getPropertyMappings()) {
		hierarchyPropertyMappings.add(pm);
		hierarchyPropertyMappingMap.put(pm.getPropertyDescription(), pm);
	}
	for(PhysicalPropertyMapping pm : getPhysicalPropertyMappingList()) {
		hierarchyPhysicalPropertyMappingList.add(pm);
	}

	for(ClassMapping child : getImmediateChildren()) {
		context.ensureInitialized(child);
		for(PropertyMapping pm : child.getHierarchyPropertyMappings()) {
			if(!hierarchyPropertyMappings.contains(pm)) {
				hierarchyPropertyMappings.add(pm);
				hierarchyPropertyMappingMap.put(pm.getPropertyDescription(), pm);
			}
		}
		for(PhysicalPropertyMapping pm : child.getHierarchyPhysicalPropertyMappingList()) {
			if(!hierarchyPhysicalPropertyMappingList.contains(pm)) {
				hierarchyPhysicalPropertyMappingList.add(pm);
			}
		}
	}
}

public boolean isLeaf() {
	return getAllChildren().isEmpty();
}

public abstract Object getDiscriminatorValue();
 
public Object getDiscriminatorValue(ResultSetReader reader) throws SQLException {
	return reader.getResultSet().getString(reader.getCurrentColumnIndex());
}

public void addObjectColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference, PropertyChooser propertyChooser) {
	if(!isLeaf()) {
		addDiscriminatorColumn(subSelectBuilder, tableReference);
	}
	for(PhysicalPropertyMapping pm : getHierarchyPhysicalPropertyMappingList()) {
		if(propertyChooser.wants(pm.getPropertyDescription())) {
			pm.addColumns(subSelectBuilder, tableReference);
		}
	}
}

public abstract SelectItem addDiscriminatorColumn(SubSelectBuilder subSelectBuilder, TableReference tableReference);
public abstract void registerDiscriminator(HierarchySubSelect hierarchySubSelect);

public void addIdentityColumns(SubSelectBuilder subSelectBuilder, TableReference tableReference) {
	if(!isLeaf()) {
		addDiscriminatorColumn(subSelectBuilder, tableReference);
	}
	primaryKey.addColumns(subSelectBuilder, tableReference);
}

public JoinBuilder createJoinBuilder(SubSelectBuilder subSelectBuilder) {
	if(needsSubSelect()) {
		JoinBuilderImpl joinBuilder = new JoinBuilderImpl(subSelectBuilder);
		HierarchySubSelect hierarchySubSelect = new HierarchySubSelect();
		createJoinBuilderSubSelect(hierarchySubSelect, getHierarchyPropertyMappings());
		hierarchySubSelect.setAlias(subSelectBuilder.createTableAlias("A"));
		subSelectBuilder.addJoinedTableReference(hierarchySubSelect);
		joinBuilder.setJoinedTableReference(hierarchySubSelect);
		return joinBuilder;
	} else {
		return primitiveCreateJoinBuilder(subSelectBuilder);
	}
}

protected JoinBuilder primitiveCreateJoinBuilder(SubSelectBuilder subSelectBuilder) {
	JoinBuilderImpl joinBuilder = new JoinBuilderImpl(subSelectBuilder);
	JoinedTable joinedTable = subSelectBuilder.createJoinedTable(getPrimaryTable());
	joinBuilder.setJoinedTableReference(joinedTable);
	return joinBuilder;
}

protected void createJoinBuilderSubSelect(final HierarchySubSelect hierarchySubSelect, List<PropertyMapping> propertyMappingList) {
	final SubSelectBuilder subSelectBuilder = new SubSelectBuilder();
	JoinBuilder joinBuilder = primitiveCreateJoinBuilder(subSelectBuilder);
	joinBuilder.createJoin();
	
	final TableReference joinedTableReference = joinBuilder.getJoinedTableReference();
	
	ColumnVisitor addColumnsCallback = new ColumnVisitor() {
		@Override
		public void visit(Column column) {
			hierarchySubSelect.selectColumn(subSelectBuilder, joinedTableReference, column);
		}
	};

	ColumnVisitor addDummiesCallback = new ColumnVisitor() {
		@Override
		public void visit(Column column) {
			hierarchySubSelect.selectDummy(subSelectBuilder, joinedTableReference, column);
		}
	};
	
	registerDiscriminator(hierarchySubSelect);
	SelectItem selectItem = addDiscriminatorColumn(subSelectBuilder, joinedTableReference);
	selectItem.setAlias("D");
	
	for(PropertyMapping pm : propertyMappingList) {
		if(maySelectForHierarchy(pm)) {
			pm.visitColumns(addColumnsCallback);
		} else {
			pm.visitColumns(addDummiesCallback);
		}
	}
	hierarchySubSelect.addSubSelectBuilder(subSelectBuilder);
	
	for(ClassMapping child : immediateChildren) {
		child.collectJoinBuilderSubSelects(hierarchySubSelect, propertyMappingList);
	}
}

protected abstract boolean maySelectForHierarchy(PropertyMapping propertyMapping);

protected abstract void collectJoinBuilderSubSelects(HierarchySubSelect hierarchySubSelect, List<PropertyMapping> propertyMappingList);

protected boolean needsSubSelect() {
	return !isLeaf();
}

protected boolean needsSubSelect(ClassMapping childMapping) {
	return true;
}

public Object createObject(ResultSetReader reader, ObjectFactory factory, boolean wantsObjects, PropertyChooser propertyChooser) throws SQLException {
	if(isLeaf()) {
		return createObject(reader, factory, wantsObjects, getHierarchyPhysicalPropertyMappingList(), propertyChooser);
	} else {
		Object value = getDiscriminatorValue(reader);
		if(value == null) {
			return null;
		} else {
			reader.setCurrentColumnIndex(reader.getCurrentColumnIndex() + 1);
			ClassMapping effectiveMapping = getClassMappingForDiscriminatorValue(value);
			return effectiveMapping.createObject(reader, factory, wantsObjects, getHierarchyPhysicalPropertyMappingList(), propertyChooser);
		}
	}
}

public Object createObject(ResultSetReader reader, ObjectFactory factory, boolean wantsObjects, List<PhysicalPropertyMapping> physicalPropertyMappings, PropertyChooser propertyChooser) throws SQLException {
	int columnIndex = reader.getCurrentColumnIndex();
	int idIndex = reader.getCurrentColumnIndex();
	if(wantsObjects) {
		idIndex += primaryKey.getIndex(physicalPropertyMappings);
	} 
	reader.setCurrentColumnIndex(idIndex);
	Object identity = primaryKey.getIdentity(reader, factory, getClassDescription());
	Object entity = identity == null ? null : factory.getObject(this, identity);
	if(identity == null) {
		reader.setCurrentColumnIndex(reader.getCurrentColumnIndex() + (wantsObjects ? physicalPropertyMappings.size() : 1));
	} else {
		boolean update;
		if(entity != null ) {
			update = true;
		} else {
			update = false;
			entity = factory.createObject(this, identity);
		}
		if(wantsObjects) {
			reader.setCurrentColumnIndex(columnIndex);
			for(PhysicalPropertyMapping pm : physicalPropertyMappings) {
				if(propertyChooser.wants(pm.getPropertyDescription())) {
					if(primaryKey.contains(pm) || !getPhysicalPropertyMappingList().contains(pm)) {
						pm.advance(reader);
					} else {
						Object value = pm.getObject(reader, factory);
						if(update) {
							factory.updateValue(entity, pm.getPropertyDescription(), value);
						} else {
							factory.setValue(entity, pm.getPropertyDescription(), value);
						}
					}
				}
			}
		}
	}
	
	return entity;
}

protected ClassMapping getClassMappingForDiscriminatorValue(Object value) {
	if(getDiscriminatorValue() != null && getDiscriminatorValue().equals(value)) {
		return this;
	} else {
		for(ClassMapping child : getAllChildren()) {
			if(!child.getClassDescription().isAbstract() && child.getDiscriminatorValue().equals(value)) {
				return child;
			}
		}
		return null;
	}
}

public Row createPrimaryRow() {
	return new Row(getPrimaryUpdateTable());
}

public String toString() {
	return getClassDescription().toString() + " (" + getPrimaryTable().getName() + ")"; 
}

public void collectUsedTables(Collection<Table> collection) {
	if(!collection.contains(primaryTable)) {
		collection.add(primaryTable);
	}
	if(!collection.contains(primaryUpdateTable)) {
		collection.add(primaryUpdateTable);
	}
	collectUserTablesFromProperties(collection);
}

protected void collectUserTablesFromProperties(Collection<Table> collection) {
	for(PropertyMapping pm : getPropertyMappings()) {
		pm.collectUsedTables(collection);
	}
}

public void collectUsedColumns(Collection<Column> collection) {
	for(PropertyMapping pm : getPropertyMappings()) {
		pm.collectUsedColumns(collection);
	}
}

public SingleRowPreparedStatementBuilder getPrimaryPreparedStatementBuilder(ObjectPersistor objectPersistor, PropertyMapping propertyMapping) {
	return objectPersistor.getPrimaryPreparedStatementBuilder(getPrimaryUpdateTable());
}

public void createInitialPrimaryPreparedStatementBuilders(ObjectPersistor objectPersistor) {
	ModificationInfo.Status status = objectPersistor.getModificationInfo().getStatus();
	if(status == ModificationInfo.Status.New || status == ModificationInfo.Status.Removed) {
		objectPersistor.getPrimaryPreparedStatementBuilder(getPrimaryUpdateTable());
	}
}

public SingleRowPreparedStatementBuilder createPrimaryPreparedStatementBuilder(ObjectPersistor objectPersistor, Object key) {
	if(key == primaryUpdateTable) {
		SingleRowPreparedStatementBuilder primaryPreparedStatementBuilder;
		Row row = createPrimaryRow();
		if(objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.New) {
			primaryPreparedStatementBuilder = new PrimaryInsertStatementBuilder(objectPersistor, row);
		} else if(objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.Removed) {
			primaryPreparedStatementBuilder = new DeleteByPrimaryKeyStatementBuilder(row);
		} else if(objectPersistor.getModificationInfo().getStatus() == ModificationInfo.Status.Modified) {
			primaryPreparedStatementBuilder = new UpdateStatementBuilder(row);
		} else {
			throw new IllegalStateException();
		}
		return primaryPreparedStatementBuilder;
	} else {
		throw new UnsupportedOperationException();
	}
}

public void initializePrimaryPreparedStatementBuilder(ObjectPersistor objectPersistor, Object key, SingleRowPreparedStatementBuilder primaryPreparedStatementBuilder) {
	if(key == primaryUpdateTable) {
		ModificationInfo.Status status = objectPersistor.getModificationInfo().getStatus();
		if(status == ModificationInfo.Status.Removed || status == ModificationInfo.Status.Modified) {
			persistPrimaryKey(objectPersistor);
		}
	} else {
		throw new UnsupportedOperationException();
	}
}

private void persistPrimaryKey(ObjectPersistor objectPersistor) {
	for(ColumnMapping columnMapping : getPrimaryKey().getColumnMappings()) {
		Object value = ModifiableAccessor.Singleton.getValue(objectPersistor.getModificationInfo().getObject(), columnMapping.getPropertyDescription());
		columnMapping.persistPrimary(objectPersistor, value);
	}
}

}

