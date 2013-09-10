package org.mariella.persistence.annotations.processing;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

import javax.persistence.ColumnResult;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityResult;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.FieldResult;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.mariella.persistence.annotations.Cluster;
import org.mariella.persistence.annotations.DomainDefinition;
import org.mariella.persistence.annotations.DomainDefinitions;
import org.mariella.persistence.annotations.UpdateTable;
import org.mariella.persistence.annotations.processing.ClasspathBrowser.Entry;
import org.mariella.persistence.mapping.AttributeInfo;
import org.mariella.persistence.mapping.ClassInfo;
import org.mariella.persistence.mapping.ClusterInfo;
import org.mariella.persistence.mapping.ColumnResultInfo;
import org.mariella.persistence.mapping.DiscriminatorColumnInfo;
import org.mariella.persistence.mapping.DiscriminatorValueInfo;
import org.mariella.persistence.mapping.DomainDefinitionInfo;
import org.mariella.persistence.mapping.EmbeddableInfo;
import org.mariella.persistence.mapping.EntityInfo;
import org.mariella.persistence.mapping.EntityResultInfo;
import org.mariella.persistence.mapping.FieldResultInfo;
import org.mariella.persistence.mapping.InheritanceInfo;
import org.mariella.persistence.mapping.JoinColumnInfo;
import org.mariella.persistence.mapping.JoinTableInfo;
import org.mariella.persistence.mapping.MappedClassInfo;
import org.mariella.persistence.mapping.MappedSuperclassInfo;
import org.mariella.persistence.mapping.NamedNativeQueryInfo;
import org.mariella.persistence.mapping.OxyUnitInfo;
import org.mariella.persistence.mapping.PrimaryKeyJoinColumnInfo;
import org.mariella.persistence.mapping.QueryHintInfo;
import org.mariella.persistence.mapping.SequenceGeneratorInfo;
import org.mariella.persistence.mapping.SqlResultSetMappingInfo;
import org.mariella.persistence.mapping.TableTableInfo;
import org.mariella.persistence.mapping.ToManyAttributeInfo;
import org.mariella.persistence.mapping.UniqueConstraintInfo;
import org.mariella.persistence.mapping.UpdateTableInfo;
import org.osgi.framework.Bundle;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@SuppressWarnings("unchecked")
public class OxyUnitInfoBuilder {
	public final static String PERSISTENCE_XML_LOCATION = "META-INF/persistence.xml";

	private ClassLoader classLoader = null;
	private List<Bundle> bundles = null;

	List<OxyUnitInfo> oxyUnitInfos = new ArrayList<OxyUnitInfo>();
	Map<Class, EntityListenerClassInfoBuilder> classToEntityListenerClassInfoBuilder = new HashMap<Class, EntityListenerClassInfoBuilder>();
	Map<AttributeInfo, AnnotatedElement> attributeInfoToAnnotatedElementMap = new HashMap<AttributeInfo, AnnotatedElement>();

	public void build() {
		try {
			parsePersistenceUnits();
			for (OxyUnitInfo info : oxyUnitInfos) {
				parseAnnotations(info);
			}
			for (OxyUnitInfo info : oxyUnitInfos) {
				buildJoinTableInfos(info);
			}
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void buildJoinTableInfos(OxyUnitInfo info) {
		for (ClassInfo classInfo : info.getClassInfos()) {
			if (classInfo instanceof MappedClassInfo) {
				for (AttributeInfo attributeInfo : ((MappedClassInfo)classInfo).getAttributeInfos()) {
					if (attributeInfo instanceof ToManyAttributeInfo) {
						ToManyAttributeInfo toMany = (ToManyAttributeInfo)attributeInfo;
						buildJoinTableInfos(toMany);
					}
				}
			}
		}
	}

	private void buildJoinTableInfos(ToManyAttributeInfo toMany) {
		if (toMany.getJoinTableInfo() != null)
			return;

		AnnotatedElement ae = attributeInfoToAnnotatedElementMap.get(toMany);
		if (ae == null)
			throw new IllegalStateException();

		if (!ae.isAnnotationPresent(javax.persistence.JoinTable.class))
			return;

		javax.persistence.JoinTable joinTable = ae.getAnnotation(javax.persistence.JoinTable.class);

		JoinTableInfo info = new JoinTableInfo();
		info.setCatalog(joinTable.catalog());
		info.setName(joinTable.name());
		info.setSchema(joinTable.schema());
		info.setUniqueConstraintInfos(buildUniqueContraintInfos(joinTable.uniqueConstraints()));
		info.setJoinColumnInfos(buildJoinColumnInfos(joinTable.joinColumns()));
		info.setInverseJoinColumnInfos(buildJoinColumnInfos(joinTable.inverseJoinColumns()));
		toMany.setJoinTableInfo(info);
	}

	private List<JoinColumnInfo> buildJoinColumnInfos(JoinColumn[] joinColumns) {
		List<JoinColumnInfo> infos = new ArrayList<JoinColumnInfo>();
		for (JoinColumn joinCol : joinColumns) {
			infos.add(new JoinColumnInfoBuilder(joinCol).buildJoinColumnInfo());
		}
		return infos;
	}

	private List<UniqueConstraintInfo> buildUniqueContraintInfos(javax.persistence.UniqueConstraint[] uniqueConstraints) {
		List<UniqueConstraintInfo> infos = new ArrayList<UniqueConstraintInfo>();
		for (UniqueConstraint con : uniqueConstraints) {
			infos.add(new UniqueConstraintInfoBuilder(con).buildUniqueConstraintInfo());
		}
		return infos;
	}


	private void parseAnnotations(OxyUnitInfo oxyUnitInfo) throws Exception {
		Map<Class,List<Class>> annotationToClassesMap = readAnnotatedClasses(
				oxyUnitInfo,
				Entity.class,
				Embeddable.class,
				Cluster.class,
				SqlResultSetMapping.class,
				SqlResultSetMappings.class,
				NamedNativeQuery.class,
				NamedNativeQueries.class,
				DomainDefinitions.class,
				DomainDefinition.class);
		parseEmbeddables(oxyUnitInfo, annotationToClassesMap.get(Embeddable.class));
		parseEntities(oxyUnitInfo, annotationToClassesMap.get(Entity.class));
		processEntityListenerClassInfoBuilders();

		for (ClassInfo ci : oxyUnitInfo.getHierarchyOrderedClassInfos()) {
			if (ci instanceof MappedClassInfo) {
				new MappedClassInfoReferencesResolver(this, (MappedClassInfo)ci).resolveReferences();
			}
		}

		parseOxyAnnotations(oxyUnitInfo, annotationToClassesMap.get(Cluster.class));
		parseSqlResultSetMappingInstances(oxyUnitInfo, annotationToClassesMap.get(SqlResultSetMapping.class));
		parseSqlResultSetMappings(oxyUnitInfo, annotationToClassesMap.get(SqlResultSetMappings.class));
		parseNamedNativeQueryInstances(oxyUnitInfo, annotationToClassesMap.get(NamedNativeQuery.class));
		parseNamedNativeQueries(oxyUnitInfo, annotationToClassesMap.get(NamedNativeQueries.class));
		parseDomainDefinitionInstances(oxyUnitInfo, annotationToClassesMap.get(DomainDefinition.class));
		parseDomainDefinitions(oxyUnitInfo, annotationToClassesMap.get(DomainDefinitions.class));
	}

	private void parseDomainDefinitions(OxyUnitInfo oxyUnitInfo, List<Class> classes) {
		for (Class clazz : classes) {
			DomainDefinitions defs = ((AnnotatedElement)clazz).getAnnotation(DomainDefinitions.class);
			for (int i=0; i<defs.value().length; i++) {
				buildDomainDefinitionInfo(oxyUnitInfo, defs.value()[i]);
			}
		}
	}

	private void parseDomainDefinitionInstances(OxyUnitInfo oxyUnitInfo, List<Class> resultSetClasses)  {
		for (Class clazz : resultSetClasses) {
			DomainDefinition def = ((AnnotatedElement)clazz).getAnnotation(DomainDefinition.class);
			buildDomainDefinitionInfo(oxyUnitInfo, def);
		}
	}

	private void buildDomainDefinitionInfo(OxyUnitInfo oxyUnitInfo, DomainDefinition def) {
		DomainDefinitionInfo info = new DomainDefinitionInfo();
		info.setLength(def.length());
		info.setName(def.name());
		info.setPrecision(def.precision());
		info.setScale(def.scale());
		info.setSqlType(def.sqlType());
		oxyUnitInfo.getDomainDefinitionInfos().add(info);
	}

	private void parseNamedNativeQueries(OxyUnitInfo oxyUnitInfo, List<Class> classes) {
		for (Class clazz : classes) {
			NamedNativeQueries queries = ((AnnotatedElement)clazz).getAnnotation(NamedNativeQueries.class);
			for (int i=0; i<queries.value().length; i++) {
				buildNamedNativeQueryInfo(oxyUnitInfo, queries.value()[i]);
			}
		}
	}

	private void parseNamedNativeQueryInstances(OxyUnitInfo oxyUnitInfo, List<Class> resultSetClasses)  {
		for (Class clazz : resultSetClasses) {
			NamedNativeQuery mapping = ((AnnotatedElement)clazz).getAnnotation(NamedNativeQuery.class);
			buildNamedNativeQueryInfo(oxyUnitInfo, mapping);
		}
	}

	private void buildNamedNativeQueryInfo(OxyUnitInfo oxyUnitInfo, NamedNativeQuery query) {
		NamedNativeQueryInfo info = new NamedNativeQueryInfo();

		QueryHintInfo[] queryHintInfos = new QueryHintInfo[query.hints().length];
		for (int i=0; i<query.hints().length;i++) {
			queryHintInfos[i] = buildQueryHintInfo(query.hints()[i]);
		}
		info.setName(query.name());
		info.setQuery(query.query());
		info.setQueryHintInfos(queryHintInfos);
		info.setResultClass(query.resultClass());
		info.setSqlResultSetMappingName(query.resultSetMapping());

		oxyUnitInfo.getNamedNativeQueryInfos().add(info);
	}

	private QueryHintInfo buildQueryHintInfo(QueryHint queryHint) {
		QueryHintInfo info = new QueryHintInfo();
		info.setName(queryHint.name());
		info.setValue(queryHint.value());
		return info;
	}

	private void parseSqlResultSetMappings(OxyUnitInfo oxyUnitInfo, List<Class> classes) {
		for (Class clazz : classes) {
			SqlResultSetMappings mappings = ((AnnotatedElement)clazz).getAnnotation(SqlResultSetMappings.class);
			for (int i=0; i<mappings.value().length; i++) {
				buildSqlResultSetMappingInfo(oxyUnitInfo, mappings.value()[i]);
			}
		}
	}

	private void parseSqlResultSetMappingInstances(OxyUnitInfo oxyUnitInfo, List<Class> resultSetClasses)  {
		for (Class clazz : resultSetClasses) {
			SqlResultSetMapping mapping = ((AnnotatedElement)clazz).getAnnotation(SqlResultSetMapping.class);
			buildSqlResultSetMappingInfo(oxyUnitInfo, mapping);
		}
	}

	private void buildSqlResultSetMappingInfo(OxyUnitInfo oxyUnitInfo, SqlResultSetMapping mapping) {

		EntityResultInfo[] entityResultInfos = new EntityResultInfo[mapping.entities().length];
		for (int i=0; i<mapping.entities().length;i++) {
			entityResultInfos[i] = buildEntityResultInfo(mapping.entities()[i]);
		}

		ColumnResultInfo[] columnResultInfos = new ColumnResultInfo[mapping.columns().length];
		for (int i=0; i<mapping.columns().length;i++) {
			columnResultInfos[i] = buildColumnResultInfo(mapping.columns()[i]);
		}


		SqlResultSetMappingInfo info = new SqlResultSetMappingInfo();
		info.setColumnResultInfos(columnResultInfos);
		info.setEntityResultInfos(entityResultInfos);
		info.setName(mapping.name());
		oxyUnitInfo.getSqlResultSetMappingInfos().add(info);
	}

	private ColumnResultInfo buildColumnResultInfo(ColumnResult columnResult) {
		ColumnResultInfo info = new ColumnResultInfo();
		info.setName(columnResult.name());
		return info;
	}

	private EntityResultInfo buildEntityResultInfo(EntityResult entityResult) {
		EntityResultInfo info = new EntityResultInfo();

		FieldResultInfo[] fieldResultInfos = new FieldResultInfo[entityResult.fields().length];
		for (int i=0; i<entityResult.fields().length;i++) {
			fieldResultInfos[i] = buildFieldResultInfo(entityResult.fields()[i]);
		}
		info.setFieldResultInfos(fieldResultInfos);

		return info;
	}

	private FieldResultInfo buildFieldResultInfo(FieldResult fieldResult) {
		FieldResultInfo info = new FieldResultInfo();
		info.setColumn(fieldResult.column());
		info.setName(fieldResult.name());
		return info;
	}

	private void parseOxyAnnotations(OxyUnitInfo oxyUnitInfo, List<Class> clusterClasses) {
		for (Class clazz : clusterClasses) {
			Cluster cluster = ((AnnotatedElement)clazz).getAnnotation(Cluster.class);
			ClusterInfo clusterInfo = new ClusterInfo();
			clusterInfo.setName(cluster.name());
			clusterInfo.setPathExpressions(cluster.pathExpressions());
			clusterInfo.setClusterClass(clazz);
			Type clusterTypeArg = (Type)ReflectionUtil.readTypeArgumentsOfClass(clazz);
			if (!(clusterTypeArg instanceof Class))
				throw new IllegalStateException("Root type of cluster " + clazz + " is not a class " + clusterTypeArg);

			ClassInfo classInfo = oxyUnitInfo.getClassToInfoMap().get(((Class)clusterTypeArg).getName());
			if (classInfo == null || !(classInfo instanceof EntityInfo))
				throw new IllegalStateException("Root type of cluster " + clazz + " is not an Entity " + clusterTypeArg);
			clusterInfo.setRootEntityName(classInfo.getName());
			oxyUnitInfo.getClusterInfos().add(clusterInfo);
		}

	}

	private void parseEmbeddables(OxyUnitInfo oxyUnitInfo, List<Class> classes) throws Exception {
		for (Class clazz : classes) {
			parseEmbeddable(oxyUnitInfo, clazz);
		}

		for (Class clazz: classes) {
			ClassInfo ci = oxyUnitInfo.getClassToInfoMap().get(clazz.getName());
			createMappedClassInfoAttributeInfosBuilder(ci).buildAttributeInfos();
		}
	}

	private void parseEmbeddable(OxyUnitInfo oxyUnitInfo, Class clazz) {
		EmbeddableInfo info = new EmbeddableInfo();
		info.setClazz(clazz);
		info.setOxyUnitInfo(oxyUnitInfo);
		oxyUnitInfo.getClassToInfoMap().put(clazz.getName(), info);
	}

	private void parseEntities(OxyUnitInfo oxyUnitInfo, List<Class> entityClasses) throws Exception {
		entityClasses = addMappedSuperclasses(entityClasses);
		entityClasses = orderHierarchy(entityClasses);
		buildClassInfos(oxyUnitInfo, entityClasses);
	}

	private void processEntityListenerClassInfoBuilders() {
		for (EntityListenerClassInfoBuilder builder : classToEntityListenerClassInfoBuilder.values()) {
			builder.build();
		}
	}

	private void buildClassInfos(OxyUnitInfo oxyUnitInfo, List<Class> annotatedClasses) throws Exception {
		for (Class clazz : annotatedClasses) {
			buildClassInfo(oxyUnitInfo, clazz);
		}
		for (Class clazz: annotatedClasses) {
			ClassInfo ci = oxyUnitInfo.getClassToInfoMap().get(clazz.getName());
			if (ci instanceof MappedClassInfo)
				new MappedClassInfoHierarchyBuilder((MappedClassInfo)ci).buildHierarchyInfo();
		}
		for (Class clazz: annotatedClasses) {
			ClassInfo ci = oxyUnitInfo.getClassToInfoMap().get(clazz.getName());
			if (ci instanceof MappedClassInfo) {
				createMappedClassInfoAttributeInfosBuilder(ci).buildAttributeInfos();
			}
		}
		for (Class clazz: annotatedClasses) {
			ClassInfo ci = oxyUnitInfo.getClassToInfoMap().get(clazz.getName());
			new ClassInfoLifecycleEventInfosBuilder(ci).buildLifecycleEventInfos();
		}
		for (Class clazz : annotatedClasses) {
			ClassInfo ci = oxyUnitInfo.getClassToInfoMap().get(clazz.getName());
			if (ci instanceof MappedClassInfo)
				((MappedClassInfo)ci).mergeOverridenAttributes();
		}
	}

	private MappedClassInfoAttributeInfosBuilder createMappedClassInfoAttributeInfosBuilder(ClassInfo ci) {
		if (ci instanceof EntityInfo)
			return new EntityInfoAttributeInfosBuilder(this, (EntityInfo)ci);

		return new MappedClassInfoAttributeInfosBuilder(this, (MappedClassInfo)ci);
	}

	private void buildClassInfo(OxyUnitInfo oxyUnitInfo, Class clazz) {
		MappedClassInfo info;
		if (clazz.isAnnotationPresent(Entity.class)) {
			info = new EntityInfo();
			Entity annotation = ((AnnotatedElement)clazz).getAnnotation(Entity.class);
			info.setName("".equals(annotation.name()) ? null : annotation.name());
			info.setExcludeSuperclassListeners(clazz.isAnnotationPresent(ExcludeSuperclassListeners.class));
			if (clazz.isAnnotationPresent(Table.class)) {
				Table table = ((AnnotatedElement)clazz).getAnnotation(Table.class);

				TableTableInfo tti = new TableTableInfo();
				tti.setCatalog(table.catalog());
				tti.setName(table.name());
				tti.setSchema(table.schema());
				((EntityInfo)info).setTableInfo(tti);
			}
			if (clazz.isAnnotationPresent(UpdateTable.class)) {
				UpdateTable table = ((AnnotatedElement)clazz).getAnnotation(UpdateTable.class);

				UpdateTableInfo uti = new UpdateTableInfo();
				uti.setCatalog(table.catalog());
				uti.setName(table.name());
				uti.setSchema(table.schema());
				((EntityInfo)info).setUpdateTableInfo(uti);
			}
		} else if (clazz.isAnnotationPresent(MappedSuperclass.class)) {
			info = new MappedSuperclassInfo();
		} else
			throw new RuntimeException("Not a valid class: " + clazz);


		if (clazz.isAnnotationPresent(EntityListeners.class)) {
			EntityListeners entityListeners = ((AnnotatedElement)clazz).getAnnotation(EntityListeners.class);
			for (Class lclazz : entityListeners.value()) {
				buildEntityListenerClassInfoBuilder(oxyUnitInfo, lclazz, info);
			}
		}


		info.setOxyUnitInfo(oxyUnitInfo);
		info.setClazz(clazz);

		oxyUnitInfo.getClassToInfoMap().put(clazz.getName(), info);
		oxyUnitInfo.getHierarchyOrderedClassInfos().add(info);

		if (clazz.isAnnotationPresent(Inheritance.class)) {
			if (!(info instanceof MappedClassInfo))
				throw new IllegalArgumentException("@Inheritance annotation can only be assigned to classes that have either @MappedSuperclass or @Entity annotations");
			((MappedClassInfo)info).setInheritanceInfo(new InheritanceInfo());
			((MappedClassInfo)info).getInheritanceInfo().setStrategy(((AnnotatedElement)clazz).getAnnotation(Inheritance.class).strategy());
		}

		if (clazz.isAnnotationPresent(DiscriminatorColumn.class)) {
			if (!(info instanceof EntityInfo))
				throw new IllegalArgumentException("@DiscriminatorColumn annotation can only be assigned to classes that have an @Entity annotation");
			DiscriminatorColumn discrCol = ((AnnotatedElement)clazz).getAnnotation(DiscriminatorColumn.class);
			DiscriminatorColumnInfo discrColumnInfo = new DiscriminatorColumnInfo();
			discrColumnInfo.setColumnDefinition(discrCol.columnDefinition());
			discrColumnInfo.setDiscriminatorType(discrCol.discriminatorType());
			discrColumnInfo.setLength(discrCol.length());
			discrColumnInfo.setName(discrCol.name());

			((EntityInfo)info).setDiscriminatorColumnInfo(discrColumnInfo);
		}

		if (clazz.isAnnotationPresent(DiscriminatorValue.class)) {
			if (!(info instanceof EntityInfo))
				throw new IllegalArgumentException("@DiscriminatorValue annotation can only be assigned to classes that have an @Entity annotation");
			DiscriminatorValue discrValue = ((AnnotatedElement)clazz).getAnnotation(DiscriminatorValue.class);
			DiscriminatorValueInfo discrValueInfo = new DiscriminatorValueInfo();
			discrValueInfo.setValue(discrValue.value());
			((EntityInfo)info).setDiscriminatorValueInfo(discrValueInfo);
		}

		if (clazz.isAnnotationPresent(PrimaryKeyJoinColumns.class)) {
			if (!(info instanceof EntityInfo))
				throw new IllegalArgumentException("@PrimaryKeyJoinColumns annotation can only be assigned to classes that have an @Entity annotation");
			if(clazz.isAnnotationPresent(PrimaryKeyJoinColumn.class)) {
				throw new IllegalArgumentException("@PrimaryKeyJoinColumns and @PrimaryKeyJoinColumn annotations must not be used together!");
			}
			PrimaryKeyJoinColumns primaryKeyJoinColumns = ((AnnotatedElement)clazz).getAnnotation(PrimaryKeyJoinColumns.class);
			for(PrimaryKeyJoinColumn primaryKeyJoinColumn : primaryKeyJoinColumns.value()) {
				((EntityInfo)info).getPrimaryKeyJoinColumnInfos().add(buildPrimaryKeyJoinColumnInfo(primaryKeyJoinColumn));
			}
		} else if(clazz.isAnnotationPresent(PrimaryKeyJoinColumn.class)) {
			((EntityInfo)info).getPrimaryKeyJoinColumnInfos().add(buildPrimaryKeyJoinColumnInfo(((AnnotatedElement)clazz).getAnnotation(PrimaryKeyJoinColumn.class)));
		}

		if (clazz.isAnnotationPresent(SequenceGenerator.class)) {
			SequenceGenerator generator = ((AnnotatedElement)clazz).getAnnotation(SequenceGenerator.class);

			SequenceGeneratorInfo sqinfo = new SequenceGeneratorInfo();
			sqinfo.setAllocationSize(generator.allocationSize());
			sqinfo.setInitialValue(generator.initialValue());
			sqinfo.setName(generator.name());
			sqinfo.setSequenceName(generator.sequenceName());
			oxyUnitInfo.getSequenceGeneratorInfos().add(sqinfo);
		}

		if (clazz.isAnnotationPresent(TableGenerator.class)) {
			new TableGeneratorInfoBuilder(((AnnotatedElement)clazz).getAnnotation(TableGenerator.class), oxyUnitInfo).buildInfo();
		}
}

	private PrimaryKeyJoinColumnInfo buildPrimaryKeyJoinColumnInfo(PrimaryKeyJoinColumn primaryKeyJoinColumn) {
		PrimaryKeyJoinColumnInfo pkInfo = new PrimaryKeyJoinColumnInfo();
		pkInfo.setColumnDefinition(primaryKeyJoinColumn.columnDefinition());
		pkInfo.setName(primaryKeyJoinColumn.name());
		pkInfo.setReferencedColumnName(primaryKeyJoinColumn.referencedColumnName());
		return pkInfo;
	}

	private void buildEntityListenerClassInfoBuilder(OxyUnitInfo oxyUnitInfo, Class listenerClazz, MappedClassInfo info) {
		EntityListenerClassInfoBuilder builder = classToEntityListenerClassInfoBuilder.get(listenerClazz);
		if (builder == null) {
			builder = new EntityListenerClassInfoBuilder(listenerClazz, oxyUnitInfo);
			classToEntityListenerClassInfoBuilder.put(listenerClazz, builder);
		}
		builder.usingMappedClassInfos.add(info);
	}

	private List<Class> orderHierarchy(List<Class> original) {
		List<Class> copy = new ArrayList<Class>( original );
		List<Class> newList = new ArrayList<Class>();
		while ( copy.size() > 0 ) {
			Class clazz = copy.get( 0 );
			orderHierarchy( copy, newList, original, clazz );
		}
		return newList;
	}

	private void orderHierarchy(List<Class> copy, List<Class> newList, List<Class> original, Class clazz) {
		if ( Object.class.equals( clazz ) ) return;
		//process superclass first
		orderHierarchy( copy, newList, original, clazz.getSuperclass() );
		if ( original.contains( clazz ) ) {
			if ( !newList.contains( clazz ) ) {
				newList.add( clazz );
			}
			copy.remove( clazz );
		}
	}

	private List<Class> addMappedSuperclasses(List<Class> orderedClasses) {
		List<Class> newOrderedClasses = new ArrayList<Class>( orderedClasses );
		for ( Class clazz : orderedClasses ) {
			Class superClazz = clazz.getSuperclass();
			if ( ! newOrderedClasses.contains( superClazz ) ) {
				addMappedSuperclasses( clazz, newOrderedClasses );
			}
		}
		return newOrderedClasses;
	}

	private void addMappedSuperclasses(Class clazz, List<Class> newOrderedClasses) {
		Class superClass = clazz.getSuperclass();
		while (superClass != null) {
			if ( superClass.isAnnotationPresent( MappedSuperclass.class )) {
				newOrderedClasses.add( 0, superClass );
			}
			superClass = superClass.getSuperclass();
		}
	}

	private Map<Class,List<Class>> readAnnotatedClasses(OxyUnitInfo oxyUnitInfo, Class ... annotationClasses) throws Exception {
		// get all class-files
//		ClasspathBrowser browser = ClasspathBrowser.getBrowser(oxyUnitInfo.getPersistenceUnitRootUrl(), bundle);
		Map<Class,List<Class>> result = new HashMap<Class, List<Class>>();
		for (Class annoClass : annotationClasses) {
			result.put(annoClass, new ArrayList<Class>());
		}
		List<Entry> entries;
		List<Bundle> bundles = getBundles();
		if (bundles != null) {
			entries = ClasspathBrowser.resolveBundleEntries(bundles);
		} else {
			entries = ClasspathBrowser.readEntries(oxyUnitInfo.getPersistenceUnitRootUrl());
		}
		for (Entry entry : entries) {
			DataInputStream dstream = new DataInputStream(entry.getInputStream());
			ClassFile cf = null;
			//System.out.println("Parsing " + entry.getName());
			try {
				cf = new ClassFile(dstream);
			} catch (Exception e) {
				throw new RuntimeException("Error during parsing class " + entry.getName(), e);
			} finally {
				dstream.close();
				entry.getInputStream().close();
			}
			AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag );
			if ( visible != null ) {
				for (Class annoClass : annotationClasses) {
					javassist.bytecode.annotation.Annotation anno = visible.getAnnotation(annoClass.getName());
					if (anno != null) {
						List<Class> list = result.get(annoClass);
						Bundle bundle = entry.getBundle();
						if (bundle != null) {
							list.add(bundle.loadClass(cf.getName()));
						} else {
							list.add(Class.forName(cf.getName()));
						}
					}
				}
			}
		}
		return result;
	}


	private void parsePersistenceUnits() throws Exception {
		List<URL> urls = new ArrayList<URL>();
		List<Bundle> bundles = getBundles();
		if (bundles != null) {
			for (Bundle bundle : bundles) {
				URL url = bundle.getResource(PERSISTENCE_XML_LOCATION);
				if (url != null) {
					urls.add(url);
				}
			}
		} else {
			urls.add(classLoader.getResource(PERSISTENCE_XML_LOCATION));
		}
		if (urls.isEmpty()) {
			throw new Exception("No " + PERSISTENCE_XML_LOCATION + " found.");
		}
		for (URL url : urls) {
			parsePersistenceUnit(url);
		}
	}

	private void parsePersistenceUnit(URL xmlRes) throws Exception {
		InputStream persistenceXmlIs = xmlRes.openStream();
		if (persistenceXmlIs == null)
			throw new Exception("Could not find " + PERSISTENCE_XML_LOCATION );

		PersistenceXmlHandler handler = new PersistenceXmlHandler();
		handler.oxyUnitInfo = new OxyUnitInfo();
		List<Bundle> bundles = getBundles();
		if (bundles == null) {
			URL xmlFileURL = classLoader.getResource(PERSISTENCE_XML_LOCATION);
			File file=new File(urlDecode(xmlFileURL.getFile()));
			File rootFile = file.getParentFile().getParentFile();
			URL rootUrl;
			if (rootFile.getPath().endsWith("!")) {
				// TODO huuuaaahhhh ... better way to find out if this is path in a jar file?
				// remove '!'
				rootFile = new File(rootFile.getPath().substring(0,rootFile.getPath().length()-1));
				// if in a jar file, the rootFile already contains the "file:" protocol
				rootUrl = new URL(rootFile.getPath());
			} else {
				rootUrl = new URL("file", null, rootFile.getPath());
			}
			handler.oxyUnitInfo.setPersistenceUnitRootUrl(rootUrl);
		}

		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(persistenceXmlIs));

		oxyUnitInfos.add(handler.oxyUnitInfo);
	}

	private String urlDecode(String file) throws UnsupportedEncodingException {
		String decoded = URLDecoder.decode(file, "UTF-8");
		return decoded;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public List<OxyUnitInfo> getOxyUnitInfos() {
		return oxyUnitInfos;
	}

	public List<Bundle> getBundles() {
		if (bundles == null) {
			bundles = new ArrayList<Bundle>(5);
			IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.mariella.persistence.persistenceBundles");
			for (IConfigurationElement configElement : configElements) {
				if (configElement.getName().equals("bundle")) {
					String bundleId = configElement.getAttribute("bundleId");
					Bundle bundle = Platform.getBundle(bundleId);
					if (bundle == null) {
						// boom
					}
					bundles.add(bundle);
				}
			}
		}
		return bundles.isEmpty() ? null : bundles;
	}

//	public void setBundles(List<Bundle> bundles) {
//		this.bundles = bundles;
//	}

}