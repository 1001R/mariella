package at.hts.persistence.mapping;

import java.util.Collection;

import at.hts.persistence.database.Column;
import at.hts.persistence.persistor.ObjectPersistor;
import at.hts.persistence.query.BinaryCondition;
import at.hts.persistence.query.ColumnReference;
import at.hts.persistence.query.JoinType;
import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.runtime.ModifiableAccessor;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.ReferencePropertyDescription;
import at.hts.persistence.util.Util;

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
	if(foreignKeyColumn != null) {
		Object relatedIdentity = null;
		if(value != null) {
			relatedIdentity = ModifiableAccessor.Singleton.getValue(value, getPropertyDescription().getReferencedClassDescription().getId());
		}
		persistor.getPrimaryPreparedStatementBuilder().getRow().setProperty(getForeignKeyColumn(), relatedIdentity);
	}
}

@Override
public void collectUsedColumns(Collection<Column> collection) {
	if(foreignKeyColumn != null) {
		collection.add(foreignKeyColumn);
	}
}
}
