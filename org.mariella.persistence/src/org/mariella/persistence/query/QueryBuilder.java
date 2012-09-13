package org.mariella.persistence.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.mapping.ColumnMapping;
import org.mariella.persistence.mapping.RelationshipPropertyMapping;
import org.mariella.persistence.mapping.SchemaMapping;
import org.mariella.persistence.query.JoinBuilder.JoinType;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.PropertyDescription;
import org.mariella.persistence.schema.RelationshipPropertyDescription;
import org.mariella.persistence.schema.ScalarPropertyDescription;
import org.mariella.persistence.util.Util;


public class QueryBuilder extends SubSelectBuilder {
	protected class PathExpressionParser extends DefaultPathExpressionVisitor {
		private JoinType joinType;
		
		public ClassMapping currentClassMapping = null;
		public StringBuilder currentPathExpression = null;
		public TableReference currentTableReference = null;
		public PropertyDescription currentPropertyDescription = null;
		
		public JoinType getJoinType() {
			return joinType;
		}
		
		public void setJoinType(JoinType joinType) {
			this.joinType = joinType;
		}
		
		@Override
		public ClassDescription root(String token) {
			currentClassMapping = aliasToClassMappingMap.get(token);
			if(currentClassMapping == null) {
				throw new IllegalArgumentException(token + " is unknown (has not been joined).");
			}
			currentPathExpression = new StringBuilder();
			currentPathExpression.append(token);
			currentTableReference = namespaceProvider.getNamespace(token).getTableReference(token);
			currentPropertyDescription = null;
			for(QueryBuilderListener listener : listeners) {
				listener.pathExpressionJoined(QueryBuilder.this, token, currentClassMapping, currentTableReference);
			}
			return currentClassMapping.getClassDescription();
		}
		
		@Override
		public void property(ClassDescription classDescription, PropertyDescription propertyDescription) {
			currentPathExpression.append('.');
			currentPathExpression.append(propertyDescription.getPropertyDescriptor().getName());
			if(propertyDescription instanceof RelationshipPropertyDescription) {
				String currentPathExpressionString = currentPathExpression.toString();
				RelationshipPropertyMapping rpm = (RelationshipPropertyMapping)currentClassMapping.getPropertyMappingInHierarchy(propertyDescription);
				IQueryBuilderNamespace ns = namespaceProvider.getNamespace(currentPathExpressionString); 
				if(ns.hasTableReference(currentPathExpressionString)) {
					currentTableReference = namespaceProvider.getNamespace(currentPathExpressionString).getTableReference(currentPathExpressionString);
				} else {
					JoinBuilder joinBuilder = rpm.createJoinBuilder(QueryBuilder.this, currentTableReference);
					if(joinType != null) {
						joinBuilder.setJoinType(joinType);
					}
					for(QueryBuilderListener listener : listeners) {
						listener.aboutToJoinRelationship(QueryBuilder.this, currentPathExpressionString, rpm, joinBuilder);
					}
					joinBuilder.createJoin();
					currentTableReference = joinBuilder.getJoinedTableReference();
					ns.register(currentPathExpressionString, currentTableReference);
					for(QueryBuilderListener listener : listeners) {
						listener.pathExpressionJoined(QueryBuilder.this, currentPathExpressionString, rpm.getReferencedClassMapping(), currentTableReference);
					}
				}
				currentClassMapping = rpm.getReferencedClassMapping();
			}
			currentPropertyDescription = propertyDescription;
		}
	};
	
	protected final SchemaMapping schemaMapping;
	protected final List<QueryBuilderListener> listeners = new ArrayList<QueryBuilderListener>();
	protected final Map<String, ClassMapping> aliasToClassMappingMap = new HashMap<String, ClassMapping>();
	
	protected final QueryBuilderNamespaceProvider namespaceProvider;
	
public QueryBuilder(SchemaMapping schemaMapping, QueryBuilderNamespaceProvider namespaceProvider) {
	super();
	this.schemaMapping = schemaMapping;
	this.namespaceProvider = namespaceProvider;
}

public QueryBuilder(SchemaMapping schemaMapping) {
	super();
	this.schemaMapping = schemaMapping;
	this.namespaceProvider = new QueryBuilderNamespaceProvider() {
		private IQueryBuilderNamespace namespace = new QueryBuilderNamespace();
		
		public IQueryBuilderNamespace getNamespace(String pathExpression) {
			return namespace;
		}
	};
}

public void addListener(QueryBuilderListener listener) {
	listeners.add(listener);
}

public void removeListener(QueryBuilderListener listener) {
	listeners.remove(listener);
}

public TableReference join(ClassDescription classDescription, String alias) {
	ClassMapping classMapping = schemaMapping.getClassMapping(classDescription.getClassName());
	aliasToClassMappingMap.put(alias, classMapping);
	JoinBuilder joinBuilder = classMapping.createJoinBuilder(this);
	joinBuilder.createJoin();
	TableReference tableReference = joinBuilder.getJoinedTableReference();
	namespaceProvider.getNamespace(alias).register(alias, tableReference);
	return tableReference;
}

public ClassMapping getClassMapping(String alias) {
	return aliasToClassMappingMap.get(alias);
}

public TableReference join(String pathExpression) {
	PathExpressionParser parser = new PathExpressionParser();
	new PathExpression(schemaMapping.getSchemaDescription(), pathExpression).visit(parser);
	Util.assertTrue(
		parser.currentPropertyDescription == null || parser.currentPropertyDescription instanceof RelationshipPropertyDescription, 
		"only relationship properties may be joined!"
	);
	return parser.currentTableReference;
}

public TableReference join(String pathExpression, JoinType joinType) {
	PathExpressionParser parser = new PathExpressionParser();
	parser.setJoinType(joinType);
	new PathExpression(schemaMapping.getSchemaDescription(), pathExpression).visit(parser);
	Util.assertTrue(
		parser.currentPropertyDescription == null || parser.currentPropertyDescription instanceof RelationshipPropertyDescription, 
		"only relationship properties may be joined!"
	);
	return parser.currentTableReference;
}

public SelectItem addSelectItem(String pathExpression) {
	return addSelectItem(createColumnReference(pathExpression));
}

public ColumnReference createColumnReference(String pathExpression) {
	PathExpressionParser parser = new PathExpressionParser();
	new PathExpression(schemaMapping.getSchemaDescription(), pathExpression).visit(parser);
	Util.assertTrue(
			parser.currentPropertyDescription instanceof ScalarPropertyDescription, 
			"only scalar properties are supported!"
		);
	ColumnMapping ppm = (ColumnMapping)parser.currentClassMapping.getPropertyMappingInHierarchy(parser.currentPropertyDescription);
	return parser.currentTableReference.createColumnReference(ppm.getReadColumn());
}	
	
}
