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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.mariella.persistence.annotations.Cluster;
import org.mariella.persistence.annotations.DomainDefinition;
import org.mariella.persistence.annotations.DomainDefinitions;
import org.osgi.framework.Bundle;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@SuppressWarnings("unchecked")
public class OxyUnitInfoBuilder {
	public final static String PERSISTENCE_XML_LOCATION = "META-INF/persistence.xml";
	
	class EntityListenerClassInfoBuilder {
		Class listenerClazz;
		List<MappedClassInfo> usingMappedClassInfos = new ArrayList<MappedClassInfo>();
		OxyUnitInfo oxyUnitInfo;
		void build() {
			EntityListenerClassInfo info = new EntityListenerClassInfo();
			info.setClazz(listenerClazz);
			info.setOxyUnitInfo(oxyUnitInfo);
			info.buildLifecycleEventInfos();
			oxyUnitInfo.classToInfoMap.put(listenerClazz, info);
			for (MappedClassInfo mappedClassInfo : usingMappedClassInfos) {
				mappedClassInfo.getEntityListenerClassInfos().add(info);
			}
		}
	}

	ClassLoader classLoader = null;
	Bundle bundle = null;

	List<OxyUnitInfo> oxyUnitInfos = new ArrayList<OxyUnitInfo>();
	Map<Class, EntityListenerClassInfoBuilder> classToEntityListenerClassInfoBuilder = new HashMap<Class, EntityListenerClassInfoBuilder>();

	public void build() throws Exception {
		parsePersistenceUnits();
		for (OxyUnitInfo info : oxyUnitInfos) {
			parseAnnotations(info);
		}
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
		
		for (ClassInfo ci : oxyUnitInfo.hierarchyOrderedClassInfos) {
			ci.resolveReferences();
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
		info.setDomainDefinition(def);
		oxyUnitInfo.domainDefinitionInfos.add(info);
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
		info.setNamedNativeQuery(query);
		oxyUnitInfo.namedNativeQueryInfos.add(info);
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
		SqlResultSetMappingInfo info = new SqlResultSetMappingInfo();
		info.setResultSetMapping(mapping);
		oxyUnitInfo.sqlResultSetMappingInfos.add(info);
	}

	private void parseOxyAnnotations(OxyUnitInfo oxyUnitInfo, List<Class> clusterClasses) {
		for (Class clazz : clusterClasses) {
			Cluster cluster = ((AnnotatedElement)clazz).getAnnotation(Cluster.class);
			ClusterInfo clusterInfo = new ClusterInfo();
			clusterInfo.setCluster(cluster);
			clusterInfo.setClusterClass(clazz);
			Type clusterTypeArg = (Type)ReflectionUtil.readTypeArgumentsOfClass(clazz);
			if (!(clusterTypeArg instanceof Class))
				throw new IllegalStateException("Root type of cluster " + clazz + " is not a class " + clusterTypeArg);
			
			ClassInfo classInfo = oxyUnitInfo.classToInfoMap.get((Class)clusterTypeArg);
			if (classInfo == null || !(classInfo instanceof EntityInfo))
				throw new IllegalStateException("Root type of cluster " + clazz + " is not an Entity " + clusterTypeArg);
			clusterInfo.setRootEntityName(classInfo.getName());
			oxyUnitInfo.clusterInfos.add(clusterInfo);
		}
		
	}

	private void parseEmbeddables(OxyUnitInfo oxyUnitInfo, List<Class> classes) throws Exception {
		for (Class clazz : classes) {
			parseEmbeddable(oxyUnitInfo, clazz);
		}

		for (Class clazz: classes) {
			ClassInfo ci = oxyUnitInfo.classToInfoMap.get(clazz);
			((EmbeddableInfo)ci).buildAttributeInfos();
		}
	}

	private void parseEmbeddable(OxyUnitInfo oxyUnitInfo, Class clazz) {
		EmbeddableInfo info = new EmbeddableInfo();
		info.setAnnotation(clazz.getAnnotation(Embeddable.class));
		info.setClazz(clazz);
		info.setOxyUnitInfo(oxyUnitInfo);
		oxyUnitInfo.classToInfoMap.put(clazz, info);
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
			ClassInfo ci = oxyUnitInfo.classToInfoMap.get(clazz);
			if (ci instanceof MappedClassInfo)
				((MappedClassInfo)ci).buildHierarchyInfo();
		}
		for (Class clazz: annotatedClasses) {
			ClassInfo ci = oxyUnitInfo.classToInfoMap.get(clazz);
			if (ci instanceof MappedClassInfo)
				((MappedClassInfo)ci).buildAttributeInfos();
		}
		for (Class clazz: annotatedClasses) {
			ClassInfo ci = oxyUnitInfo.classToInfoMap.get(clazz);
			ci.buildLifecycleEventInfos();
		}
		for (Class clazz : annotatedClasses) {
			ClassInfo ci = oxyUnitInfo.classToInfoMap.get(clazz);
			if (ci instanceof MappedClassInfo)
				((MappedClassInfo)ci).mergeOverridenAttributes();
		}
	}
	
	private void buildClassInfo(OxyUnitInfo oxyUnitInfo, Class clazz) {
		MappedClassInfo info;
		if (clazz.isAnnotationPresent(Entity.class)) { 
			info = new EntityInfo();
			info.setAnnotation(clazz.getAnnotation(Entity.class));
			if (clazz.isAnnotationPresent(Table.class)) {
				((EntityInfo)info).setTableInfo(new TableInfo());
				((EntityInfo)info).getTableInfo().setTable(((AnnotatedElement)clazz).getAnnotation(Table.class));
			}
		} else if (clazz.isAnnotationPresent(MappedSuperclass.class)) { 
			info = new MappedSuperclassInfo();
			info.setAnnotation(clazz.getAnnotation(MappedSuperclass.class));
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
		
		oxyUnitInfo.classToInfoMap.put(clazz, info);
		oxyUnitInfo.hierarchyOrderedClassInfos.add(info);
		
		if (clazz.isAnnotationPresent(Inheritance.class)) {
			if (!(info instanceof MappedClassInfo))
				throw new IllegalArgumentException("@Inheritance annotation can only be assigned to classes that have either @MappedSuperclass or @Entity annotations");
			((MappedClassInfo)info).setInheritanceInfo(new InheritanceInfo());
			((MappedClassInfo)info).getInheritanceInfo().setInheritance(((AnnotatedElement)clazz).getAnnotation(Inheritance.class));
		}

		if (clazz.isAnnotationPresent(SequenceGenerator.class)) {
			SequenceGeneratorInfo sqinfo = new SequenceGeneratorInfo();
			sqinfo.setSeqGenerator(((AnnotatedElement)clazz).getAnnotation(SequenceGenerator.class));
			oxyUnitInfo.sequenceGeneratorInfos.add(sqinfo);
		}

		if (clazz.isAnnotationPresent(TableGenerator.class)) {
			TableGeneratorInfo tinfo = new TableGeneratorInfo();
			tinfo.setTableGenerator(((AnnotatedElement)clazz).getAnnotation(TableGenerator.class));
			oxyUnitInfo.tableGeneratorInfos.add(tinfo);
		}
}
	
	private void buildEntityListenerClassInfoBuilder(OxyUnitInfo oxyUnitInfo, Class listenerClazz, MappedClassInfo info) {
		EntityListenerClassInfoBuilder builder = classToEntityListenerClassInfoBuilder.get(listenerClazz);
		if (builder == null) {
			builder = new EntityListenerClassInfoBuilder();
			builder.listenerClazz = listenerClazz;
			builder.oxyUnitInfo = oxyUnitInfo;
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
			// TODO include Embeddables here?
			if ( superClass.isAnnotationPresent( MappedSuperclass.class )) {
				newOrderedClasses.add( 0, superClass );
				break;
			}
			superClass = superClass.getSuperclass();
		}
	}

	private Map<Class,List<Class>> readAnnotatedClasses(OxyUnitInfo oxyUnitInfo, Class ... annotationClasses) throws Exception {
		// get all class-files
		ClasspathBrowser browser = ClasspathBrowser.getBrowser(oxyUnitInfo.getPersistenceUnitRootUrl());
		Map<Class,List<Class>> result = new HashMap<Class, List<Class>>();
		for (Class annoClass : annotationClasses) {
			result.put(annoClass, new ArrayList<Class>());
		}
		
		for (InputStream is : browser.entries) {
			DataInputStream dstream = new DataInputStream(is);
			ClassFile cf = null;
			try {
				cf = new ClassFile(dstream);
			} finally {
				dstream.close();
				is.close();
			}
			AnnotationsAttribute visible = (AnnotationsAttribute) cf.getAttribute( AnnotationsAttribute.visibleTag );
			if ( visible != null ) {
				for (Class annoClass : annotationClasses) {
					javassist.bytecode.annotation.Annotation anno = visible.getAnnotation(annoClass.getName());
					if (anno != null) {
						List<Class> list = result.get(annoClass);
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
		Enumeration<URL> persistenceXmlResources = bundle != null ? bundle.getResources(PERSISTENCE_XML_LOCATION) :
			classLoader.getResources(PERSISTENCE_XML_LOCATION);
		while (persistenceXmlResources.hasMoreElements()) {
			URL xmlRes = persistenceXmlResources.nextElement();
			parsePersistenceUnit(xmlRes);
		}
	}
	
	private void parsePersistenceUnit(URL xmlRes) throws Exception {
		InputStream persistenceXmlIs = xmlRes.openStream();
		if (persistenceXmlIs == null)
			throw new Exception("Could not find " + PERSISTENCE_XML_LOCATION );
		
		PersistenceXmlHandler handler = new PersistenceXmlHandler(); 
		handler.oxyUnitInfo = new OxyUnitInfo();
		URL rootUrl;
		if (bundle == null) {
			URL xmlFileURL = classLoader.getResource(PERSISTENCE_XML_LOCATION);
			File file=new File(urlDecode(xmlFileURL.getFile()));
			File rootFile = file.getParentFile().getParentFile();
			if (rootFile.getPath().endsWith("!")) {
				// TODO huuuaaahhhh ... better way to find out if this is path in a jar file?
				// remove '!'
				rootFile = new File(rootFile.getPath().substring(0,rootFile.getPath().length()-1));
				// if in a jar file, the rootFile already contains the "file:" protocol
				rootUrl = new URL(rootFile.getPath());
			} else {
				rootUrl = new URL("file", null, rootFile.getPath());
			}
		} else {
			rootUrl = bundle.getResource("/");
		}
		
		handler.oxyUnitInfo.persistenceUnitRootUrl = rootUrl;
		
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

	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

}
