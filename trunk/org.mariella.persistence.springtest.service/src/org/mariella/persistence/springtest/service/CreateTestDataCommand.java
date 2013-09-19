package org.mariella.persistence.springtest.service;

import org.mariella.oxygen.basic_core.OxyObjectPool;


public class CreateTestDataCommand extends Command<Void> {

	private static final long serialVersionUID = 1L;

	public CreateTestDataCommand(OxyObjectPool objectPool) {
		super(objectPool);
	}

}
