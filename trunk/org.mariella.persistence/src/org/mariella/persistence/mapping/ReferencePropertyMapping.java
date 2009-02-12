package org.mariella.persistence.mapping;

import java.util.Collection;
import java.util.List;

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

public class ReferencePropertyMapping extends RelationshipPropertyMapping {
	private List<JoinColumn> joinColumns = null;
	
public ReferencePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription) {
	super(classMapping, (ReferencePropertyDescription)propertyDescription);
}

public void setJoinColumns(List<JoinColumn> joinColumns) {
	this.joinColumns = joinColumns;
}

@Override 
public ReferencePropertyDescription getPropertyDescription() {
	return (ReferencePropertyDescription)super.getPropertyDescription();
}

public TableReference join(SubSelectBuilder subSelectBuilder, TableReference myTableReference) {
	if(joinColumns != null) {
		TableReference referencedTableReference = getReferencedClassMapping().join(subSelectBuilder);
		for(JoinColumn joinColumn : joinColumns) {
			subSelectBuilder.and(
				BinaryCondition.eq(
					new ColumnReference(myTableReference, joinColumn.getMyColumn()),
					new ColumnReference(referencedTableReference, joinColumn.getReferencedColumn()),
					joinColumn.getMyColumn().isNullable() ? JoinType.leftouter : JoinType.inner
				)
			);
		}
		return referencedTableReference;
	} else {
		return getReversePropertyMapping().joinReverse(subSelectBuilder, myTableReference);
	}
}

@Override
protected TableReference joinReverse(SubSelectBuilder subSelectBuilder, TableReference referencedTableReference) {
	if(joinColumns == null) {
		throw new UnsupportedOperationException();
	} else {
		TableReference ownerTableReference = getClassMapping().join(subSelectBuilder);
		for(JoinColumn joinColumn : joinColumns) {
			subSelectBuilder.and(
				BinaryCondition.eq(
						new ColumnReference(ownerTableReference, joinColumn.getMyColumn()),
						new ColumnReference(referencedTableReference, joinColumn.getReferencedColumn()),
						joinColumn.getMyColumn().isNullable() ? JoinType.leftouter : JoinType.inner
				)
			);
		}
		return ownerTableReference;
	}
}

@Override
protected void persist(ObjectPersistor persistor, Object value) {
	throw new UnsupportedOperationException();
}

@Override
public void insert(ObjectPersistor persistor, Object value) {
	persist(persistor, value, true);
}

@Override
public void update(ObjectPersistor persistor, Object value) {
	persist(persistor, value, false);
}

protected void persist(ObjectPersistor persistor, Object value, boolean isInsert) {
	if(joinColumns != null) {
		for(JoinColumn joinColumn : joinColumns) {
			if(isInsert && joinColumn.isInsertable() || !isInsert && joinColumn.isUpdatable()) {
				Object relatedValue = null;
				if(value != null) {
					ColumnMapping cm = getReferencedClassMapping().getColumnMapping(joinColumn.getReferencedColumn());
					relatedValue = ModifiableAccessor.Singleton.getValue(value, cm.getPropertyDescription());
				}
				persistor.getPrimaryPreparedStatementBuilder().getRow().setProperty(joinColumn.getMyColumn(), relatedValue);
			}
		}
	}
}



@Override
public void collectUsedColumns(Collection<Column> collection) {
	if(joinColumns != null) {
		for(JoinColumn joinColumn : joinColumns) {
			collection.add(joinColumn.getMyColumn());
		}
	}
}

}
