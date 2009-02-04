package org.mariella.persistence.mapping;

import java.util.Collection;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.query.BinaryCondition;
import org.mariella.persistence.query.ColumnReference;
import org.mariella.persistence.query.JoinType;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.schema.PropertyDescription;
import org.mariella.persistence.schema.ReferencePropertyDescription;
import org.mariella.persistence.util.Util;


public class ReferencePropertyMapping extends RelationshipPropertyMapping {
	private final Column foreignKeyColumn;
	
public ReferencePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription, String foreignKeyColumnName) {
	super(classMapping, (ReferencePropertyDescription)propertyDescription);
	foreignKeyColumn = classMapping.getPrimaryTable().getColumn(foreignKeyColumnName);
	Util.assertTrue(foreignKeyColumn != null, "Unknown column");
}

public ReferencePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription) {
	super(classMapping, (ReferencePropertyDescription)propertyDescription);
	foreignKeyColumn = null;
}

public Column getForeignKeyColumn() {
	return foreignKeyColumn;
}

@Override
public ReferencePropertyDescription getPropertyDescription() {
	return (ReferencePropertyDescription)super.getPropertyDescription();
}

public TableReference join(SubSelectBuilder subSelectBuilder, TableReference myTableReference) {
	if(getForeignKeyColumn() != null) {
		TableReference referencedTableReference = getReferencedClassMapping().join(subSelectBuilder);
		subSelectBuilder.and(
			BinaryCondition.eq(
				new ColumnReference(myTableReference, getForeignKeyColumn()),
				new ColumnReference(referencedTableReference, getReferencedClassMapping().getIdMapping().getColumn()),
				getForeignKeyColumn().isNullable() ? JoinType.leftouter : JoinType.inner
			)
		);
		return referencedTableReference;
	} else {
		return getReversePropertyMapping().joinReverse(subSelectBuilder, myTableReference);
	}
}

@Override
protected TableReference joinReverse(SubSelectBuilder subSelectBuilder, TableReference referencedTableReference) {
	if(getForeignKeyColumn() == null) {
		throw new UnsupportedOperationException();
	} else {
		TableReference ownerTableReference = getClassMapping().join(subSelectBuilder);
		subSelectBuilder.and(
			BinaryCondition.eq(
					new ColumnReference(ownerTableReference, getForeignKeyColumn()),
					new ColumnReference(referencedTableReference, getReferencedClassMapping().getIdMapping().getColumn()),
					getForeignKeyColumn().isNullable() ? JoinType.leftouter : JoinType.inner
			)
		);
		return ownerTableReference;
	}
}

@Override
public void persist(ObjectPersistor persistor, Object value) {
	if(foreignKeyColumn != null && getPropertyDescription().isUpdateForeignKeys()) {
		Object relatedIdentity = null;
		if(value != null) {
			relatedIdentity = ModifiableAccessor.Singleton.getValue(value, getPropertyDescription().getReferencedClassDescription().getId());
		}
		persistor.getPrimaryPreparedStatementBuilder().getRow().setProperty(getForeignKeyColumn(), relatedIdentity);
	}
}

@Override
public void collectUsedColumns(Collection<Column> collection) {
	if(foreignKeyColumn != null && getPropertyDescription().isUpdateForeignKeys()) {
		collection.add(foreignKeyColumn);
	}
}
}
