package org.mariella.persistence.springtest.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.mariella.oxygen.basic_core.OxyObjectPool;

public class MergingInputStream extends ObjectInputStream {
	private OxyObjectPool target;
	
public MergingInputStream(InputStream in) throws IOException {
	super(in);
	enableResolveObject(true);
}

@Override
protected Object resolveObject(Object obj) throws IOException {
	return null;
}

}
