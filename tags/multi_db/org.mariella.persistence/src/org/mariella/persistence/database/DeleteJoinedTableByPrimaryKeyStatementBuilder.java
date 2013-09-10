package org.mariella.persistence.database;

import org.mariella.persistence.mapping.JoinedClassMapping;
import org.mariella.persistence.mapping.PrimaryKeyJoinColumn;
import org.mariella.persistence.persistor.ObjectPersistor;
import org.mariella.persistence.persistor.Row;

public class DeleteJoinedTableByPrimaryKeyStatementBuilder extends DeleteByPrimaryKeyStatementBuilder {
	private final ObjectPersistor objectPersistor;
	private final JoinedClassMapping classMapping;

public DeleteJoinedTableByPrimaryKeyStatementBuilder(ObjectPersistor objectPersistor, JoinedClassMapping classMapping, Row row) {
	super(row);
	this.objectPersistor = objectPersistor;
	this.classMapping = classMapping;
}

@Override
public void initialize() {
	super.initialize();
	for(PrimaryKeyJoinColumn primaryKeyJoinColumn : classMapping.getPrimaryKeyJoinColumns().getPrimaryKeyJoinColumns()) {
		Object value = objectPersistor.getModifiableAccessor().getValue(objectPersistor.getModificationInfo().getObject(), primaryKeyJoinColumn.getPrimaryKeyProperty());
		getRow().setProperty(primaryKeyJoinColumn.getJoinTableColumn(), value);
	}
}

}
