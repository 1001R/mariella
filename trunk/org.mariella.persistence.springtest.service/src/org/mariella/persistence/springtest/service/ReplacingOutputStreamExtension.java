package org.mariella.persistence.springtest.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ReplacingOutputStreamExtension extends ObjectOutputStream {

public ReplacingOutputStreamExtension(OutputStream out) throws IOException {
	super(out);
	enableReplaceObject(true);
}

@Override
protected Object replaceObject(Object obj) throws IOException {
	return super.replaceObject(obj);
}

}
