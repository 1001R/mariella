package at.hts.persistence.test.junit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import oracle.jdbc.driver.OracleDriver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import at.hts.persistence.loader.ClusterLoader;
import at.hts.persistence.loader.LoaderContext;
import at.hts.persistence.persistor.ClusterDescription;
import at.hts.persistence.persistor.DatabaseAccess;
import at.hts.persistence.persistor.Persistor;
import at.hts.persistence.query.QueryBuilderListener;
import at.hts.persistence.runtime.Modifiable;
import at.hts.persistence.runtime.ModificationTracker;
import at.hts.persistence.runtime.RIListener;
import at.hts.persistence.test.model.Adresse;
import at.hts.persistence.test.model.Identity;
import at.hts.persistence.test.model.LieferAdresse;
import at.hts.persistence.test.model.Person;
import at.hts.persistence.test.model.TestSchemaDescription;

public class ModificationTest {
	private ModificationTracker modificationTracker;
	private RIListener riListener;
	
	private TestSchemaDescription schemaDescription;
	private Connection connection;
	
	private DatabaseAccess idGenerator = new DatabaseAccess() {
		public long generateId() {
			try {
				PreparedStatement ps = getConnection().prepareStatement("select idsequence.nextval from dual");
				try {
					ResultSet rs = ps.executeQuery();
					try {
						rs.next();
						return rs.getLong(1);
					} finally {
						rs.close();
					}
				} finally {
					ps.close();
				}
			} catch(SQLException e) {
				throw new RuntimeException(e);
			}
		}
		public Connection getConnection() {
			return connection;
		}
	};
	
@Before
public void setUp() throws Exception {
	schemaDescription = new TestSchemaDescription();
	
	modificationTracker = new ModificationTracker();
	riListener = new RIListener(new TestSchemaDescription());
	modificationTracker.addListener(riListener);
	
	DriverManager.registerDriver(new OracleDriver());
	connection = DriverManager.getConnection("jdbc:oracle:thin:@vievmsdrsld1.eu.boehringer.com:1521:htssd", "aim", "aim");
	connection.setAutoCommit(false);
}

@Test
public void test() {
	Person markus = new Person();
	modificationTracker.addNewParticipant(markus);
	Person martin = new Person();
	modificationTracker.addNewParticipant(martin);
	Adresse adresse = new LieferAdresse();
	modificationTracker.addNewParticipant(adresse);
	
	markus.setPersonIdentity(new Identity("markus", 37));
	martin.setPersonIdentity(new Identity("martin", 40));
	adresse.setStrasse("dr. boehringer gasse");
	markus.getAdressen().add(adresse);
	adresse.setPerson(martin);
}

@Test
public void testPersistor() throws Exception {
	PreparedStatement ps; 
	
	ps = connection.prepareStatement("delete from privatadresse");
	ps.executeUpdate();
	ps.close();

	ps = connection.prepareStatement("delete from adresse");
	ps.executeUpdate();
	ps.close();

	ps = connection.prepareStatement("delete from person");
	ps.executeUpdate();
	ps.close();

	Persistor p;

	Adresse csd = new LieferAdresse();
	csd.setStrasse("Amalienstrasse 68");
	modificationTracker.addNewParticipant(csd);
	
	Person markus = new Person();
	markus.setPersonIdentity(new Identity("markus", 37));
	modificationTracker.addNewParticipant(markus);
	markus.getPrivatAdressen().add(csd);

	Person martin = new Person();
	martin.setPersonIdentity(new Identity("martin", 40));
	modificationTracker.addNewParticipant(martin);

	p = new Persistor(schemaDescription.getSchemaMapping(), idGenerator, modificationTracker);
	p.persist();
	
	Adresse bia = new LieferAdresse();
	modificationTracker.addNewParticipant(bia);
	bia.setStrasse("dr. boehringer gasse");
	markus.getAdressen().add(bia);
	bia.setPerson(martin);
	markus.setContactPerson(martin);
	
	p = new Persistor(schemaDescription.getSchemaMapping(), idGenerator, modificationTracker);
	try {
		p.persist();
		connection.commit();
	} catch(Exception e) {
		connection.rollback();
		throw e;
	}
}

@Test
public void testLoader() throws Exception {
	ClusterDescription cd = new ClusterDescription();
	cd.setRootDescription(schemaDescription.getClassDescription(Person.class.getName()));
	cd.setPathExpressions(new String[] { "root", "root.contactPerson", "root.contactPerson.adressen"  } );
	
	ClusterLoader clusterLoader = new ClusterLoader(schemaDescription.getSchemaMapping(), cd);
	LoaderContext loaderContext = new LoaderContext(modificationTracker);
	List<Modifiable> result = (List<Modifiable>)clusterLoader.load(connection, loaderContext, QueryBuilderListener.Default);
	Assert.assertEquals(result.size(), 2);
	Person aim = (Person)result.get(0);
	Person ms = (Person)result.get(1);
	Assert.assertEquals(aim.getPersonIdentity().getName(), "markus");
	Assert.assertEquals(aim.getPersonIdentity().getAge(), 37);
	Assert.assertEquals(aim.getAdressen().size(), 0);
	Assert.assertNull(aim.getContactPersonFor());
	Assert.assertEquals(aim.getContactPerson(), ms);
	Assert.assertEquals(ms.getContactPersonFor(), aim);
	Assert.assertEquals(ms.getPersonIdentity().getName(), "martin");
	Assert.assertEquals(ms.getPersonIdentity().getAge(), 40);
	Assert.assertEquals(ms.getAdressen().size(), 1);
	Assert.assertEquals(ms.getAdressen().get(0).getStrasse(), "dr. boehringer gasse");
	
	ms.getAdressen().remove(0);
	Persistor p = new Persistor(schemaDescription.getSchemaMapping(), idGenerator, modificationTracker);
	try {
		p.persist();
		connection.commit();
	} catch(Exception e) {
		connection.rollback();
		throw e;
	}
}

@Test
public void testLoader2() throws Exception {
	ClusterDescription cd = new ClusterDescription();
	cd.setRootDescription(schemaDescription.getClassDescription(Person.class.getName()));
	cd.setPathExpressions(new String[] { "root", "root.privatAdressen" } );
	
	ClusterLoader clusterLoader = new ClusterLoader(schemaDescription.getSchemaMapping(), cd);
	LoaderContext loaderContext = new LoaderContext(modificationTracker);
	List<Modifiable> result = (List<Modifiable>)clusterLoader.load(connection, loaderContext, QueryBuilderListener.Default);
	Assert.assertEquals(result.size(), 2);
	Person aim = (Person)result.get(0);
	aim.getPrivatAdressen().remove(0);
	aim.setPersonIdentity(new Identity("markus", 19));
	Persistor p = new Persistor(schemaDescription.getSchemaMapping(), idGenerator, modificationTracker);
	try {
		p.persist();
		connection.commit();
	} catch(Exception e) {
		connection.rollback();
		throw e;
	}
}

@After
public void tearDown() throws Exception {
	modificationTracker.removeListener(riListener);
	modificationTracker.dispose();
	connection.close();
}

}
