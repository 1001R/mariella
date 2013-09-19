package org.mariella.persistence.springtest.test;

import java.sql.SQLException;

import org.eclipse.core.runtime.Assert;
import org.junit.AfterClass;
import org.junit.Test;
import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.basic_impl.OxyClientEntityManagerImpl;
import org.mariella.oxygen.remoting.common.RemotingClassResolver;
import org.mariella.persistence.schema.SchemaDescription;
import org.mariella.persistence.springtest.model.Person;
import org.mariella.persistence.springtest.service.TestCommandInvoker;
import org.mariella.persistence.springtest.service.LoadExtendedPersonCommand;
import org.mariella.persistence.springtest.service.LoadPersonCommand;
import org.mariella.persistence.springtest.service.SpringTestService;

public class H2Test {
	

@AfterClass
public static void dispose() throws SQLException {
}


@Test
public void test() throws Exception {
	SpringTestService.service.test();
	
	OxyEntityManager clientEntityManager;
	SchemaDescription schemaDescr = SpringTestService.service.getSchemaDescription();
	org.mariella.oxygen.basic_core.ClassResolver classResolver = new RemotingClassResolver();
	clientEntityManager = new OxyClientEntityManagerImpl(schemaDescr, classResolver);
	OxyObjectPool pool = clientEntityManager.getObjectPool();
	TestCommandInvoker invoker;
	
	// entitymgr, pool, ...
	LoadPersonCommand command = new LoadPersonCommand(pool);
	command.setId("P1");
	
	invoker = new TestCommandInvoker(new RemotingClassResolver());
	invoker.setObjectPool(command.getObjectPool());
	invoker.setCommand(command);
	invoker.invoke();
	Person person = (Person)invoker.getResult();
	
	Assert.isNotNull(person);
	Assert.isTrue(person.getAddresses().size() == 1);
	Assert.isTrue(person.getFriends().size() == 0);
	
	LoadExtendedPersonCommand command2 = new LoadExtendedPersonCommand(pool);
	command2.setPerson(person);
	
	invoker = new TestCommandInvoker(new RemotingClassResolver());
	invoker.setSendObjectPool(false);
	invoker.setObjectPool(command2.getObjectPool());
	invoker.setCommand(command2);
	invoker.invoke();
	
	Assert.isTrue(person.getFriends().size() == 1);
}


}
