package at.hts.persistence.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.hts.persistence.loader.LoadingPolicyStatementBuilder.NamespaceProvider;
import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.ColumnMapping;
import at.hts.persistence.mapping.RelationshipPropertyMapping;
import at.hts.persistence.mapping.SchemaMapping;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.RelationshipPropertyDescription;
import at.hts.persistence.schema.ScalarPropertyDescription;
import at.hts.persistence.util.Util;

public class QueryBuilder extends SubSelectBuilder {
	protected class PathExpressionParser extends DefaultPathExpressionVisitor {
		public ClassMapping currentClassMapping = null;
		public StringBuilder currentPathExpression = null;
		public TableReference currentTableReference = null;
		public PropertyDescription currentPropertyDescription = null;
		
		@Override
		public ClassDescription root(String token) {
			currentClassMapping = aliasToClassMappingMap.get(token);
			currentPathExpression = new StringBuilder();
			currentPathExpression.append(token);
			currentTableReference = namespaceProvider.getNamespace(token).pathExpressionToTableReferenceMap.get(token);
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
				RelationshipPropertyMapping rpm = (RelationshipPropertyMapping)currentClassMapping.getPropertyMapping(propertyDescription);
				QueryBuilderNamespace ns = namespaceProvider.getNamespace(currentPathExpressionString); 
				if(ns.pathExpressionToTableReferenceMap.containsKey(currentPathExpressionString)) {
					currentTableReference = namespaceProvider.getNamespace(currentPathExpressionString).pathExpressionToTableReferenceMap.get(currentPathExpressionString);
				} else {
					currentTableReference = rpm.join(QueryBuilder.this, currentTableReference);
					ns.pathExpressionToTableReferenceMap.put(currentPathExpressionString, currentTableReference);
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
	
public QueryBuilder(SchemaMapping schemaMapping, NamespaceProvider namespaceProvider) {
	super();
	this.schemaMapping = schemaMapping;
	this.namespaceProvider = namespaceProvider;
}

public QueryBuilder(SchemaMapping schemaMapping) {
	super();
	this.schemaMapping = schemaMapping;
	this.namespaceProvider = QueryBuilderNamespaceProvider.Default;
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
	TableReference tableReference = classMapping.join(this);
	namespaceProvider.getNamespace(alias).pathExpressionToTableReferenceMap.put(alias, tableReference);
	return tableReference;
}

public TableReference join(String pathExpression) {
	PathExpressionParser parser = new PathExpressionParser();
	new PathExpression(pathExpression).visit(parser);
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
	new PathExpression(pathExpression).visit(parser);
	Util.assertTrue(
			parser.currentPropertyDescription instanceof ScalarPropertyDescription, 
			"only scalar properties are supported!"
		);
	ColumnMapping ppm = (ColumnMapping)parser.currentClassMapping.getPropertyMapping(parser.currentPropertyDescription);
	return new ColumnReference(parser.currentTableReference, ppm.getColumn());
}	
	
}
