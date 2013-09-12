package org.mariella.persistence.springtest.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.remoting.common.Invoker;


public class CommandInvoker extends Invoker<Command<?>> {

public CommandInvoker(ClassResolver classResolver) {
	super(classResolver);
}

public void invoke() throws IOException, ClassNotFoundException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(bos);
	
	oos.writeObject(getObjectPool());
	super.invoke(oos);
	
	ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())) {
		protected java.lang.Class<?> resolveClass(java.io.ObjectStreamClass desc) throws IOException ,ClassNotFoundException {
			return getClassResolver().resolveClass(desc.getName());	
		}
	};
	
	InvokableCommand invokableCommand = new InvokableCommand();
	invokableCommand.invoke(ois);
	
	ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
	ObjectOutputStream oos2 = new ObjectOutputStream(bos2);
	invokableCommand.writeResult(oos2);
	
	ObjectInputStream rois = new ObjectInputStream(new ByteArrayInputStream(bos2.toByteArray())) {
		protected java.lang.Class<?> resolveClass(java.io.ObjectStreamClass desc) throws IOException ,ClassNotFoundException {
			return getClassResolver().resolveClass(desc.getName());	
		}
	};
	
	readResult(rois);
}

}
