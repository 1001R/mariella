package org.mariella.persistence.springtest.service;

import org.mariella.persistence.schema.SchemaDescription;


public class LoadSchemaDescriptionCommand extends Command<SchemaDescription> {

	private static final long serialVersionUID = 1L;

	public LoadSchemaDescriptionCommand() {
		super(null);
	}

}
