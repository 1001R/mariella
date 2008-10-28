package org.mariella.persistence.mapping;

import java.util.Collection;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.Table;
import org.mariella.persistence.query.BinaryCondition;
import org.mariella.persistence.query.ColumnReference;
import org.mariella.persistence.query.JoinType;
import org.mariella.persistence.query.JoinedTable;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;
import org.mariella.persistence.schema.PropertyDescription;
import org.mariella.persistence.schema.RelationshipPropertyDescription;
import org.mariella.persistence.util.Util;


public abstract class RelationshipAsTablePropertyMapping extends RelationshipPropertyMapping {
	protected Table table;
	protected Column foreignKeyToOwner;
	protected Column foreignKeyToContent;
	
public RelationshipAsTablePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription, String tableName, String foreignKeyToOwner, String foreignKeyToContent) {
	super(classMapping, (RelationshipPropertyDescription)propertyDescription);
	this.table = classMapping.getSchemaMapping().getSchema().getTable(tableName);
	this.foreignKeyToOwner = table.getColumn(foreignKeyToOwner);
	Util.assertTrue(foreignKeyToOwner != null, "Unknown column");
	this.foreignKeyToContent = table.getColumn(foreignKeyToContent);
	Util.assertTrue(foreignKeyToContent != null, "Unknown column");
}

public RelationshipAsTablePropertyMapping(ClassMapping classMapping, PropertyDescription propertyDescription) {
	super(classMapping, (RelationshipPropertyDescription)propertyDescription);
}

@Override
public TableReference join(SubSelectBuilder subSelectBuilder, TableReference myTableReference) {
	JoinedTable joinedTable = subSelectBuilder.join(table);
	subSelectBuilder.and(
		BinaryCondition.eq(
			new ColumnReference(joinedTable, foreignKeyToOwner), 
			new ColumnReference(myTableReference, getClassMapping().getIdMapping().getColumn()), 
			JoinType.leftouter
		)
	);
	
	TableReference joinedContentTable = getReferencedClassMapping().join(subSelectBuilder);
	subSelectBuilder.and(
		BinaryCondition.eq(
			new ColumnReference(joinedTable, foreignKeyToContent),
			new ColumnReference(joinedContentTable, getReferencedClassMapping().getIdMapping().getColumn())
		)
	);
	return joinedContentTable;
}

@Override
public TableReference joinReverse(SubSelectBuilder subSelectBuilder, TableReference contentTableReference) {
	JoinedTable joinedTable = subSelectBuilder.join(table);
	subSelectBuilder.and(
		BinaryCondition.eq(
			new ColumnReference(joinedTable, foreignKeyToContent), 
			new ColumnReference(contentTableReference, getReferencedClassMapping().getIdMapping().getColumn()), 
			JoinType.leftouter
		)
	);
	
	TableReference joinedOwnerTable = getClassMapping().join(subSelectBuilder);
	subSelectBuilder.and(
		BinaryCondition.eq(
			new ColumnReference(joinedTable, foreignKeyToOwner),
			new ColumnReference(joinedOwnerTable, getClassMapping().getIdMapping().getColumn())
		)
	);
	return joinedOwnerTable;
}

public Table getTable() {
	return table;
}

public void setTable(Table table) {
	this.table = table;
}

public Column getForeignKeyToOwner() {
	return foreignKeyToOwner;
}

public void setForeignKeyToOwner(Column foreignKeyToOwner) {
	this.foreignKeyToOwner = foreignKeyToOwner;
}

public Column getForeignKeyToContent() {
	return foreignKeyToContent;
}

public void setForeignKeyToContent(Column foreignKeyToContent) {
	this.foreignKeyToContent = foreignKeyToContent;
}

@Override
public void collectUsedTables(Collection<Table> collection) {
	collection.add(table);
}

@Override
public void collectUsedColumns(Collection<Column> collection) {
	collection.add(foreignKeyToOwner);
	collection.add(foreignKeyToContent);
}
}
