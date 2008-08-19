package at.hts.persistence.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import at.hts.persistence.mapping.AbstractClassMapping;
import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.ObjectFactory;
import at.hts.persistence.mapping.RelationshipPropertyMapping;
import at.hts.persistence.mapping.ResultSetReader;
import at.hts.persistence.query.DefaultPathExpressionVisitor;
import at.hts.persistence.query.PathExpression;
import at.hts.persistence.runtime.ModifiableAccessor;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.RelationshipPropertyDescription;

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
		final List<Object> result = new ArrayList<Object>();
		while(resultSet.next()) {
			currentIndex = 1;
			new PathExpression(loadingPolicy.getPathExpression()).visit(
				new DefaultPathExpressionVisitor() {
					StringBuilder currentPathExpression;
					
					public ClassDescription root(String token) {
						currentPathExpression = new StringBuilder();
						currentPathExpression.append(token);
						currentClassMapping = loadingPolicy.getLoader().getSchemaMapping().getClassMapping(loadingPolicy.getLoader().getClusterDescription().getRootDescription().getClassName());
						try {
							modifiable = currentClassMapping.createObject(resultSetReader, objectFactory, currentPathExpression.toString().length() == loadingPolicy.getPathExpression().length());
						} catch(SQLException e) {
							throw new RuntimeException(e);
						}
						if(modifiable != null && !result.contains(modifiable)) {
							result.add(modifiable);
						}
						return loadingPolicy.getLoader().getClusterDescription().getRootDescription();
					}
					
					@Override
					public void property(ClassDescription classDescription,	PropertyDescription propertyDescription) {
						if(modifiable != null) {
							currentPathExpression.append('.');
							currentPathExpression.append(propertyDescription.getPropertyDescriptor().getName());
							currentClassMapping = ((RelationshipPropertyMapping)currentClassMapping.getPropertyMapping(propertyDescription)).getReferencedClassMapping();
							try {
								Object value = currentClassMapping.createObject(resultSetReader, objectFactory, currentPathExpression.toString().length() == loadingPolicy.getPathExpression().length());
								if(value != null) {
									loaderContext.addToRelationship(modifiable, (RelationshipPropertyDescription)propertyDescription, value);
									modifiable = value;
								}
							} catch(SQLException e) {
								throw new RuntimeException(e);
							}
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
	
}


