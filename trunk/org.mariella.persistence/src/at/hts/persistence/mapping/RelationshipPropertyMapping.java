package at.hts.persistence.mapping;

import at.hts.persistence.query.SubSelectBuilder;
import at.hts.persistence.query.TableReference;
import at.hts.persistence.schema.RelationshipPropertyDescription;

public abstract class RelationshipPropertyMapping extends PropertyMapping {

public RelationshipPropertyMapping(ClassMapping classMapping, RelationshipPropertyDescription propertyDescription) {
	super(classMapping, propertyDescription);
}

public RelationshipPropertyDescription getPropertyDescription() {
	return (RelationshipPropertyDescription)super.getPropertyDescription();
}

@Override
public ClassMapping getClassMapping() {
	return (ClassMapping)super.getClassMapping();
}

public RelationshipPropertyMapping getReversePropertyMapping() {
	return (RelationshipPropertyMapping)getReferencedClassMapping().getPropertyMapping(getPropertyDescription().getReversePropertyDescription());
}

public ClassMapping getReferencedClassMapping() {
	return getClassMapping().getSchemaMapping().getClassMapping(getPropertyDescription().getReferencedClassDescription().getClassName());
}

public abstract TableReference join(SubSelectBuilder subSelectBuilder, TableReference myTableReference);
protected abstract TableReference joinReverse(SubSelectBuilder subSelectBuilder, TableReference referencedTableReference);

}
