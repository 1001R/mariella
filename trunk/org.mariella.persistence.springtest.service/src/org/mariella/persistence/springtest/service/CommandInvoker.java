package org.mariella.persistence.springtest.service;

import java.io.IOException;
import java.io.ObjectOutputStream;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.oxygen.remoting.common.Invoker;


public class CommandInvoker extends Invoker<Command<?>> {

public CommandInvoker(ClassResolver classResolver) {
	super(classResolver);
}

public void invoke(ObjectOutputStream outputStream) throws IOException {
	outputStream.writeObject(getObjectPool());
	outputStream.writeObject(getCommand());
}

}
