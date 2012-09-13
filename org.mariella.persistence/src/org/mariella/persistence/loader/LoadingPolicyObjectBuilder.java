package org.mariella.persistence.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.mapping.AbstractClassMapping;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.ObjectFactory;
import org.mariella.persistence.mapping.RelationshipPropertyMapping;
import org.mariella.persistence.mapping.ResultSetReader;
import org.mariella.persistence.query.DefaultPathExpressionVisitor;
import org.mariella.persistence.query.PathExpression;
import org.mariella.persistence.runtime.MariellaPersistence;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.PropertyDescription;
import org.mariella.persistence.schema.RelationshipPropertyDescription;


public class LoadingPolicyObjectBuilder {

	private final class LoadingPolicyObjectFactory implements ObjectFactory {
		private final boolean isUpdate;

		public LoadingPolicyObjectFactory(boolean isUpdate) {
			super();
			this.isUpdate = isUpdate;
		}

		public Object createObject(ClassMapping classMapping, Object identity) {
			return loaderContext.createModifiable(classMapping, identity);
		}

		public Object createEmbeddableObject(AbstractClassMapping classMapping) {
			return loaderContext.createEmbeddable(classMapping);
		}

		public Object getObject(ClassMapping classMapping, Object identity) {
			return loaderContext.getModifiable(identity);
		}

		public Object getValue(Object receiver, PropertyDescription propertyDescription) {
			return ModifiableAccessor.Singleton.getValue(receiver, propertyDescription);
		}

		public void setValue(Object receiver, PropertyDescription propertyDescription, Object value) {
			ModifiableAccessor.Singleton.setValue(receiver, propertyDescription, value);
		}

		public void updateValue(Object receiver, PropertyDescription propertyDescription, Object value) {
			if(isUpdate) {
				setValue(receiver, propertyDescription, value);
			}
		}
	}

	protected final ResultSetReader resultSetReader = new ResultSetReader() {
		public int getCurrentColumnIndex() {
			return currentIndex;
		}
		public ResultSet getResultSet() {
			return resultSet;
		}
		public void setCurrentColumnIndex(int index) {
			currentIndex = index;
		}
	};

	protected final ObjectFactory objectFactory;

	private final LoadingPolicy loadingPolicy;
	private final LoaderContext loaderContext;
	private final ResultSet resultSet;

	private int currentIndex;
	private ClassMapping currentClassMapping;
	private Object modifiable;


public LoadingPolicyObjectBuilder(LoadingPolicy loadingPolicy, LoaderContext loaderContext, ResultSet resultSet) {
	super();
	this.loadingPolicy = loadingPolicy;
	this.loaderContext = loaderContext;
	this.resultSet = resultSet;
	this.objectFactory = new LoadingPolicyObjectFactory(loaderContext.isUpdate());
}

public List<?> createObjects() {
	try {
		MariellaPersistence.logger.fine("### creating objects for loading policy: " + loadingPolicy.getPathExpression());
		final List<Object> result = new ArrayList<Object>();
		while(resultSet.next()) {
			currentIndex = 1;
			new PathExpression(loadingPolicy.getLoader().getSchemaMapping().getSchemaDescription(), loadingPolicy.getPathExpression()).visit(
				new DefaultPathExpressionVisitor() {
					StringBuilder currentPathExpression;

					public ClassDescription root(String token) {
						currentPathExpression = new StringBuilder();
						currentPathExpression.append(token);
						MariellaPersistence.logger.fine("currentPathExpression: " + currentPathExpression);
						currentClassMapping = loadingPolicy.getLoader().getSchemaMapping().getClassMapping(loadingPolicy.getLoader().getClusterDescription().getRootDescription().getClassName());
						MariellaPersistence.logger.fine("currentClassMapping: " + currentClassMapping.getClassDescription().getClassName());
						try {
							Object object = loadingPolicy.createObject(resultSetReader, currentClassMapping, objectFactory, currentPathExpression.toString().length() == loadingPolicy.getPathExpression().length());
							setModifiable(object);
						} catch(SQLException e) {
							throw new RuntimeException(e);
						}
						if(modifiable != null && !result.contains(modifiable)) {
							loaderContext.newObject(loadingPolicy.getLoader().getClusterDescription(), currentPathExpression.toString(), getClassDescription(modifiable), modifiable);
							result.add(modifiable);
						}
						return loadingPolicy.getLoader().getClusterDescription().getRootDescription();
					}

					@Override
					public void property(ClassDescription classDescription,	PropertyDescription propertyDescription) {
						if(modifiable != null) {
							currentPathExpression.append('.');
							currentPathExpression.append(propertyDescription.getPropertyDescriptor().getName());
							MariellaPersistence.logger.fine("currentPathExpression: " + currentPathExpression);
							currentClassMapping = ((RelationshipPropertyMapping)currentClassMapping.getPropertyMappingInHierarchy(propertyDescription)).getReferencedClassMapping();
							MariellaPersistence.logger.fine("currentClassMapping: " + currentClassMapping.getClassDescription().getClassName());
							try {
								Object value = loadingPolicy.createObject(resultSetReader, currentClassMapping, objectFactory, currentPathExpression.toString().length() == loadingPolicy.getPathExpression().length());
								if(value != null) {
									loaderContext.newObject(loadingPolicy.getLoader().getClusterDescription(), currentPathExpression.toString(), getClassDescription(value), value);
								}
								MariellaPersistence.logger.fine("adding to relationship: " + propertyDescription.getPropertyDescriptor().getName() + "\tvalue: " + (value == null ? "null" : currentClassMapping.getClassDescription().getId(value)));
								if(getClassDescription(modifiable).getPropertyDescriptions().contains(propertyDescription)) {
									loaderContext.addToRelationship(modifiable, (RelationshipPropertyDescription)propertyDescription, value);
								}
								setModifiable(value);
							} catch(SQLException e) {
								throw new RuntimeException(e);
							}
						}
					}

					private void setModifiable(Object object) {
						// 368008
						LoadingPolicyObjectBuilder.this.modifiable = object;
						if(modifiable == null) {
							MariellaPersistence.logger.fine("setModifiable to null ");
						} else {
							MariellaPersistence.logger.fine("setModifiable instance of: " + object.getClass().getName() + " id: " + currentClassMapping.getClassDescription().getId(object));
						}
					}
				}
			);
		}
		return result;
	} catch(SQLException e) {
		throw new RuntimeException(e);
	}
}

private ClassDescription getClassDescription(Object modifiable) {
	return loadingPolicy.getLoader().getSchemaMapping().getSchemaDescription().getClassDescription(modifiable.getClass().getName());
}

}


