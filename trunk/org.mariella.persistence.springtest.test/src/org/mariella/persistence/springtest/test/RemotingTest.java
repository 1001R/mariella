package org.mariella.persistence.springtest.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.junit.Test;
import org.mariella.oxygen.basic_core.OxyEntityManager;
import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.oxygen.basic_impl.OxyClientEntityManagerImpl;
import org.mariella.oxygen.remoting.common.InputStreamAndLength;
import org.mariella.oxygen.remoting.common.RemotingClassResolver;
import org.mariella.oxygen.remoting.http.client.RufusHttpClientService;
import org.mariella.persistence.schema.SchemaDescription;
import org.mariella.persistence.springtest.model.Person;
import org.mariella.persistence.springtest.service.CommandInvoker;
import org.mariella.persistence.springtest.service.CreateTestDataCommand;
import org.mariella.persistence.springtest.service.LoadPersonCommand;
import org.mariella.persistence.springtest.service.LoadSchemaDescriptionCommand;
import org.mariella.persistence.springtest.service.LoadSomeContentCommand;
import org.mariella.persistence.springtest.service.PostSomeContentCommand;
import org.mariella.persistence.springtest.service.SetupDBCommand;

public class RemotingTest {

	@Test
	public void test() throws Exception {
		URL url = new URL("http://localhost:8087/at.rufus.server/springTestServlet");
		RufusHttpClientService clientService = new RufusHttpClientService();

		org.mariella.oxygen.basic_core.ClassResolver classResolver = new RemotingClassResolver();

		CommandInvoker invoker;
		
		// setup db schema, no object pool
		invoker = new CommandInvoker(new RemotingClassResolver());
		invoker.setCommand(new SetupDBCommand());
		clientService.invoke(invoker, url, false);

		// load schema description, no object pool
		invoker = new CommandInvoker(new RemotingClassResolver());
		invoker.setCommand(new LoadSchemaDescriptionCommand());
		clientService.invoke(invoker, url, false);
		SchemaDescription schemaDescr = (SchemaDescription)invoker.getResult();
		schemaDescr.afterDeserialization(getClass().getClassLoader());
		
		// create object pool with schema description
		OxyEntityManager clientEntityManager;
		clientEntityManager = new OxyClientEntityManagerImpl(schemaDescr, classResolver);
		OxyObjectPool pool = clientEntityManager.getObjectPool();

		// pool is available
		
		// create test data
		invoker = new CommandInvoker(new RemotingClassResolver());
		invoker.setCommand(new CreateTestDataCommand(pool));
		clientService.invoke(invoker, url, false);

		// load person
		LoadPersonCommand command = new LoadPersonCommand(pool);
		command.setId("P1");

		invoker = new CommandInvoker(new RemotingClassResolver());
		invoker.setObjectPool(command.getObjectPool());
		invoker.setCommand(command);


		clientService.invoke(invoker, url, false);
		Person person = (Person)invoker.getResult();
		
		Assert.isNotNull(person);
		Assert.isTrue(person.getAddresses().size() == 1);
		Assert.isTrue(person.getFriends().size() == 0);

		LoadSomeContentCommand loadCommand = new LoadSomeContentCommand();
		invoker = new CommandInvoker(new RemotingClassResolver());
		invoker.setCommand(loadCommand);
		clientService.invoke(invoker, url, false);
		InputStream resultStream = (InputStream)invoker.getResult();
		try {
			String content = readStringContent(resultStream);
			Assert.isNotNull(resultStream);
			Assert.isTrue(content.equals("ABER HALLO"));
		} finally {
			resultStream.close();
		}
		
		PostSomeContentCommand postCommand = new PostSomeContentCommand();
		invoker = new CommandInvoker(new RemotingClassResolver());
		invoker.setPostedContent(getSomeInputStreamAndLength());
		invoker.setCommand(postCommand);
		clientService.invoke(invoker, url, false);
		Assert.isNotNull(invoker.getResult());
		Assert.isTrue(invoker.getResult().equals("ABER HALLO"));
		invoker.getResult();
	}

	private InputStreamAndLength getSomeInputStreamAndLength() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter w = new PrintWriter(new OutputStreamWriter(bos));
		w.write("ABER HALLO");
		w.close();
		return new InputStreamAndLength(new ByteArrayInputStream(bos.toByteArray()), "ABER HALLO".length());
	}

	private String readStringContent(InputStream result) throws IOException {
		InputStreamReader reader = new InputStreamReader(result);
		String s = "";
		while (true) {
			int ch = reader.read();
			if (ch == -1)
				break;
			s += (char)ch;
		}
		return s;
	}
}
