package at.hts.persistence.test.model;

import java.sql.Types;

import at.hts.persistence.database.Column;
import at.hts.persistence.database.IntegerConverter;
import at.hts.persistence.database.LongConverter;
import at.hts.persistence.database.Schema;
import at.hts.persistence.database.StringConverter;
import at.hts.persistence.database.Table;
import at.hts.persistence.mapping.EmbeddedClassMapping;
import at.hts.persistence.mapping.EmbeddedPropertyMapping;
import at.hts.persistence.mapping.HierarchyInTableClassMapping;
import at.hts.persistence.mapping.SingleTableClassMapping;
import at.hts.persistence.mapping.CollectionAsTablePropertyMapping;
import at.hts.persistence.mapping.CollectionPropertyMapping;
import at.hts.persistence.mapping.ColumnMapping;
import at.hts.persistence.mapping.ReferencePropertyMapping;
import at.hts.persistence.mapping.RelationshipAsTablePropertyMapping;
import at.hts.persistence.mapping.SchemaMapping;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.CollectionPropertyDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.ReferencePropertyDescription;
import at.hts.persistence.schema.ScalarPropertyDescription;
import at.hts.persistence.schema.SchemaDescription;

public class TestSchemaDescription extends SchemaDescription {
	private SchemaMapping schemaMapping;
	private Schema schema;
	
public TestSchemaDescription() {
	super();
	
	ClassDescription cd;
	
	ClassDescription scd = new ClassDescription(this, Superclass.class.getName());
	ScalarPropertyDescription id = new ScalarPropertyDescription(scd, getPropertyDescriptor(Superclass.class, "id")); 
	scd.addPropertyDescription(id);
	scd.setId(id);
	addClassDescription(scd);

	cd = new ClassDescription(this, Identity.class.getName());
	cd.addPropertyDescription(new ScalarPropertyDescription(cd, getPropertyDescriptor(Identity.class, "name")));
	cd.addPropertyDescription(new ScalarPropertyDescription(cd, getPropertyDescriptor(Identity.class, "age")));
	addClassDescription(cd);
	
	cd = new ClassDescription(this, scd, Person.class.getName());
	cd.addPropertyDescription(new ScalarPropertyDescription(cd, getPropertyDescriptor(Person.class, "personIdentity")));
	cd.addPropertyDescription(new CollectionPropertyDescription(cd, getPropertyDescriptor(Person.class, "adressen"), Adresse.class.getName(), "person"));
	cd.addPropertyDescription(new CollectionPropertyDescription(cd, getPropertyDescriptor(Person.class, "privatAdressen"), Adresse.class.getName()));
	cd.addPropertyDescription(new ReferencePropertyDescription(cd, getPropertyDescriptor(Person.class, "contactPerson"), "contactPersonFor"));
	cd.addPropertyDescription(new ReferencePropertyDescription(cd, getPropertyDescriptor(Person.class, "contactPersonFor"), "contactPerson"));
	addClassDescription(cd);
	
	ClassDescription adresse = new ClassDescription(this, scd, Adresse.class.getName());
	adresse.addPropertyDescription(new ScalarPropertyDescription(adresse, getPropertyDescriptor(Adresse.class, "strasse")));
	adresse.addPropertyDescription(new ReferencePropertyDescription(adresse, getPropertyDescriptor(Adresse.class, "person"), "adressen"));
	addClassDescription(adresse);

	cd = new ClassDescription(this, adresse, LieferAdresse.class.getName());
	addClassDescription(cd);

	cd = new ClassDescription(this, adresse, PrivatAdresse.class.getName());
	addClassDescription(cd);

	schemaMapping = new SchemaMapping(this, getSchema());
	SingleTableClassMapping cm; 
	PropertyDescription pd;
	ReferencePropertyMapping rpm;
	CollectionPropertyMapping cpm;
	RelationshipAsTablePropertyMapping rtm;

	EmbeddedClassMapping idMapping = new EmbeddedClassMapping(schemaMapping, getClassDescription(Identity.class.getName()));
	pd = idMapping.getClassDescription().getPropertyDescription("name");
	idMapping.setPropertyMapping(pd, new ColumnMapping(idMapping, pd, schemaMapping.getSchema().getTable("PERSON").getColumn("NAME")));
	pd = idMapping.getClassDescription().getPropertyDescription("age");
	idMapping.setPropertyMapping(pd, new ColumnMapping(idMapping, pd, schemaMapping.getSchema().getTable("PERSON").getColumn("AGE")));
	
	cm = new SingleTableClassMapping(schemaMapping, getClassDescription(Person.class.getName()), "PERSON");
	schemaMapping.setClassMapping(cm.getClassDescription().getClassName(), cm);
	pd = cm.getClassDescription().getPropertyDescription("id"); 
	cm.setPropertyMapping(pd, new ColumnMapping(cm, pd, "ID"));
	pd = cm.getClassDescription().getPropertyDescription("personIdentity");
	cm.setPropertyMapping(pd, new EmbeddedPropertyMapping(cm, idMapping, pd));
	pd = cm.getClassDescription().getPropertyDescription("contactPerson");
	rpm = new ReferencePropertyMapping(cm, pd, "CONTACT_PERSON_ID");
	cm.setPropertyMapping(pd, rpm);
	pd = cm.getClassDescription().getPropertyDescription("contactPersonFor");
	rpm = new ReferencePropertyMapping(cm, pd);
	cm.setPropertyMapping(pd, rpm);
	pd = cm.getClassDescription().getPropertyDescription("adressen");
	cpm = new CollectionPropertyMapping(cm, pd);
	cm.setPropertyMapping(pd, cpm);
	pd = cm.getClassDescription().getPropertyDescription("privatAdressen");
	rtm = new CollectionAsTablePropertyMapping(cm, pd, "PRIVATADRESSE", "PERSON_ID", "ADRESSE_ID");
	cm.setPropertyMapping(pd, rtm);
	
	cm = new HierarchyInTableClassMapping(schemaMapping, getClassDescription(Adresse.class.getName()), "ADRESSE", "ART", null);
	schemaMapping.setClassMapping(cm.getClassDescription().getClassName(), cm);
	pd = cm.getClassDescription().getPropertyDescription("id"); 
	cm.setPropertyMapping(pd, new ColumnMapping(cm, pd, "ID"));
	pd = cm.getClassDescription().getPropertyDescription("strasse"); 
	cm.setPropertyMapping(pd, new ColumnMapping(cm, pd, "STRASSE"));
	pd = cm.getClassDescription().getPropertyDescription("person");
	rpm = new ReferencePropertyMapping(cm, pd, "PERSON_ID");
	cm.setPropertyMapping(pd, rpm);

	cm = new HierarchyInTableClassMapping(schemaMapping, getClassDescription(LieferAdresse.class.getName()), "L");
	schemaMapping.setClassMapping(cm.getClassDescription().getClassName(), cm);

	cm = new HierarchyInTableClassMapping(schemaMapping, getClassDescription(PrivatAdresse.class.getName()), "P");
	schemaMapping.setClassMapping(cm.getClassDescription().getClassName(), cm);
}

public SchemaMapping getSchemaMapping() {
	return schemaMapping;
}

private Schema getSchema() {
	if(schema == null) {
		Table table;

		schema = new Schema();
		
		table = new Table("PERSON");
		table.addPrimaryKeyColumn(new Column("ID", Types.INTEGER, false, LongConverter.Singleton));
		table.addColumn(new Column("NAME", Types.VARCHAR, false, StringConverter.Singleton));
		table.addColumn(new Column("AGE", Types.INTEGER, false, IntegerConverter.Singleton));
		table.addColumn(new Column("CONTACT_PERSON_ID", Types.INTEGER, true, LongConverter.Singleton));
		schema.addTable(table);

		table = new Table("ADRESSE");
		table.addPrimaryKeyColumn(new Column("ID", Types.INTEGER, false, LongConverter.Singleton));
		table.addColumn(new Column("ART", Types.VARCHAR, false, StringConverter.Singleton));
		table.addColumn(new Column("STRASSE", Types.VARCHAR, false, StringConverter.Singleton));
		table.addColumn(new Column("PERSON_ID", Types.INTEGER, false, LongConverter.Singleton));
		schema.addTable(table);

		table = new Table("PRIVATADRESSE");
		table.addPrimaryKeyColumn(new Column("ADRESSE_ID", Types.INTEGER, false, LongConverter.Singleton));
		table.addPrimaryKeyColumn(new Column("PERSON_ID", Types.INTEGER, false, LongConverter.Singleton));
		schema.addTable(table);
}
	return schema;
}

}
