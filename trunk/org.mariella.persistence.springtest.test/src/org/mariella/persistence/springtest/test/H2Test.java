package org.mariella.persistence.springtest.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.h2.Driver;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mariella.persistence.springtest.service.SpringTestService;

public class H2Test {
	
@BeforeClass
public static void initialize() throws SQLException, IOException {
	DriverManager.registerDriver(new Driver());
	Connection connection = DriverManager.getConnection("jdbc:h2:~/test");
	new FileExecutor(H2Test.class.getResourceAsStream("/h2/drop.sql"), connection, false).execute();
	new FileExecutor(H2Test.class.getResourceAsStream("/h2/create.sql"), connection, true).execute();
	connection.close();
}

@AfterClass
public static void dispose() throws SQLException {
}

@Test
public void test() throws SQLException {
	SpringTestService.service.test();
}




}
