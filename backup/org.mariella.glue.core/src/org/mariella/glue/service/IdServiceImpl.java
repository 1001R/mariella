package org.mariella.glue.service;

public class IdServiceImpl extends ScreeningServiceImpl implements IdService {
	private int nextId = 0;
	
public int generateId() {
	return nextId++;
}

}
