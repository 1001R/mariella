package org.mariella.persistence.mapping;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.database.Column;
import org.mariella.persistence.database.StringConverter;
import org.mariella.persistence.query.ColumnReference;
import org.mariella.persistence.query.ColumnReferenceImpl;
import org.mariella.persistence.query.SelectItem;
import org.mariella.persistence.query.SubSelectBuilder;
import org.mariella.persistence.query.TableReference;

public class HierarchySubSelect implements TableReference {
	private List<SubSelectBuilder> subSelectBuilders = new ArrayList<SubSelectBuilder>();
	private Map<Column, Column> virtualTableColumnMap = new HashMap<Column, Column>();
	private String alias;
	private Column discriminiatorColum;
	private boolean referenced = false;

@Override
public boolean isReferenced() {
	return referenced;
}

public HierarchySubSelect() {
	super();
}
	
public void addSubSelectBuilder(SubSelectBuilder subSelectBuilder) {
	subSelectBuilders.add(subSelectBuilder);
}

public Column registerDiscriminatorColumn(Column discriminator) {
	if(discriminiatorColum == null) {
		discriminiatorColum = new Column("D", discriminator.getType(), false, discriminator.getConverter());
	}
	virtualTableColumnMap.put(discriminator, discriminiatorColum);
	return discriminiatorColum;
}

public Column registerAnonymousDiscriminator() {
	if(this.discriminiatorColum == null) {
		this.discriminiatorColum = new Column("D", Types.VARCHAR, false, StringConverter.Singleton);
	}
	return this.discriminiatorColum;
}

public void selectColumn(SubSelectBuilder subSelectBuilder, TableReference tableReference, Column readColumn) {
	String alias;
	Column virtualColumn = virtualTableColumnMap.get(readColumn);
	
	if(virtualColumn != null) {
		alias = virtualColumn.getName();
	} else {
		alias = subSelectBuilder.createSelectItemAlias(readColumn.getName());
		Column column = new Column(alias, readColumn.getType(), readColumn.isNullable(), readColumn.getConverter());
		virtualTableColumnMap.put(readColumn, column);
	}
	SelectItem selectItem = subSelectBuilder.addSelectItem(new ColumnReferenceImpl(tableReference, readColumn));
	selectItem.setAlias(alias);
}

public void selectDummy(SubSelectBuilder subSelectBuilder, TableReference tableReference, Column readColumn) {
	String alias;
	Column virtualColumn = virtualTableColumnMap.get(readColumn);
	
	if(virtualColumn != null) {
		alias = virtualColumn.getName();
		virtualTableColumnMap.put(readColumn, virtualColumn);
	} else {
		alias = subSelectBuilder.createSelectItemAlias(readColumn.getName());
		Column column = new Column(alias, readColumn.getType(), readColumn.isNullable(), readColumn.getConverter());
		virtualTableColumnMap.put(readColumn, column);
	}
	SelectItem selectItem = subSelectBuilder.addSelectItem(readColumn.getConverter().createDummy());
	selectItem.setAlias(alias);
}

@Override
public ColumnReference createUnreferencedColumnReference(Column column) {
	Column virtualColumn = virtualTableColumnMap.get(column);
	if(virtualColumn == null) {
		throw new IllegalArgumentException();
	} else {
		return new ColumnReferenceImpl(this, virtualColumn);
	}
}

@Override
public ColumnReference createColumnReference(Column column) {
	referenced = true;
	return createUnreferencedColumnReference(column);
}

@Override
public ColumnReference createColumnReferenceForRelationship(Column foreignKeyColumn) {
	return createColumnReference(foreignKeyColumn);
}

@Override
public boolean canCreateColumnReference(Column column) {
	return virtualTableColumnMap.containsKey(column);
}

public void setAlias(String alias) {
	this.alias = alias;
}

@Override
public String getAlias() {
	return alias;
}

@Override
public void printFromClause(StringBuilder b) {
	b.append("(");
	boolean first = true;
	for(SubSelectBuilder subSelectBuilder : subSelectBuilders) {
		if(first) {
			first = false;
		} else {
			b.append(" union all ");	
		}
		subSelectBuilder.getSubSelect().printSql(b);
	}
	b.append(") ");
	b.append(alias);	
}

@Override
public void printSql(StringBuilder b) {
	b.append(alias);
}

}
