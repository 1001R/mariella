package org.mariella.persistence.mapping;

import java.util.List;

import org.mariella.persistence.database.UpdateStatementBuilder;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;
import org.mariella.persistence.query.BinaryCondition;
import org.mariella.persistence.query.JoinBuilder;
import org.mariella.persistence.query.JoinBuilder.JoinType;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.runtime.CollectionModificationInfo;
import org.mariella.persistence.schema.RelationshipPropertyDescription;

public class CollectionWithoutReferencePropertyMapping extends RelationshipPropertyMapping {
	private List<JoinColumn> joinColumns = null;
	
public CollectionWithoutReferencePropertyMapping(ClassMapping classMapping, RelationshipPropertyDescription propertyDescription) {
	super(classMapping, propertyDescription);
}

public List<JoinColumn> getJoinColumns() {
	return joinColumns;
}

public void setJoinColumns(List<JoinColumn> joinColumns) {
	this.joinColumns = joinColumns;
}

@Override
public JoinBuilder createJoinBuilder(SubSelectBuilder subSelectBuilder, TableReference myTableReference) {
	if(joinColumns == null) {
		throw new IllegalStateException();
	} else {
		JoinBuilder joinBuilder = getReferencedClassMapping().createJoinBuilder(subSelectBuilder);
		for(JoinColumn joinColumn : joinColumns) {
			joinBuilder.getConditionBuilder(joinColumn.getReferencedReadColumn()).and(
				BinaryCondition.eq(
					myTableReference.createColumnReferenceForRelationship(joinColumn.getMyReadColumn()),
					joinBuilder.getJoinedTableReference().createColumnReference(joinColumn.getReferencedReadColumn())
				)
			);
			joinBuilder.setJoinType(JoinType.leftouter);
		}
		return joinBuilder;
	} 
}

@Override
protected JoinBuilder createReverseJoinBuilder(SubSelectBuilder subSelectBuilder, TableReference referencedTableReference) {
	throw new UnsupportedOperationException();
}

@Override
protected void persistPrimary(ObjectPersistor persistor, Object value) {
}

@Override
public void persistSecondary(ObjectPersistor persistor, Object value) {
	CollectionModificationInfo cmi = persistor.getModificationInfo().getCollectionModificationInfo(getPropertyDescription().getPropertyDescriptor().getName());
	if(cmi != null) {
		for(Object removed: cmi.getRemoved()) {
			ObjectPersistor relatedPersistor = persistor.getPersistor().getObjectPersistor(removed);
			Row row = createReferencedRow(relatedPersistor, removed);
			for(JoinColumn joinColumn : joinColumns) {
				row.setProperty(joinColumn.getReferencedUpdateColumn(), null);
			}
			persistor.getPreparedStatementBuilders().add(new UpdateStatementBuilder(row));
		}
		for(Object added : cmi.getAdded()) {
			ObjectPersistor relatedPersistor = persistor.getPersistor().getObjectPersistor(added);
			Row row = createReferencedRow(relatedPersistor, added);
			for(JoinColumn joinColumn : joinColumns) {
				ColumnMapping cm = getClassMapping().getColumnMapping(joinColumn.getMyReadColumn());
				Object myValue = persistor.getModifiableAccessor().getValue(persistor.getModificationInfo().getObject(), cm.getPropertyDescription());
				row.setProperty(joinColumn.getReferencedUpdateColumn(), myValue);
			}
			persistor.getPreparedStatementBuilders().add(new UpdateStatementBuilder(row));
		}
	}
}

private Row createReferencedRow(ObjectPersistor relatedPersistor, Object object) {
	Row row = new Row(relatedPersistor.getClassMapping().getPrimaryUpdateTable());
	for(ColumnMapping columnMapping : relatedPersistor.getClassMapping().getPrimaryKey().getColumnMappings()) {
		Object value = relatedPersistor.getModifiableAccessor().getValue(object, columnMapping.getPropertyDescription());
		row.setProperty(columnMapping.getUpdateColumn(), value);
	}
	return row;
}
 
@Override
public void visitColumns(ColumnVisitor visitor) {
	for(JoinColumn joinColumn : joinColumns) {
		visitor.visit(joinColumn.getReferencedReadColumn());
	}
}

}
