package org.mariella.persistence.springtest.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CommandInvoker {

public void invoke(Command command) {
	try {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		os.writeObject(command);
		os.close();
		
		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
	} catch(IOException e) {
		throw new RuntimeException(e);
	}
}
	
}
