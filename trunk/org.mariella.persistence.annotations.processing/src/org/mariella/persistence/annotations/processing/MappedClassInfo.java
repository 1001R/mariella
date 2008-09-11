package org.mariella.persistence.annotations.processing;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.mariella.persistence.annotations.Domain;

@SuppressWarnings("unchecked")
public abstract class MappedClassInfo extends ClassInfo {

private MappedClassInfo superclassInfo;
private List<MappedClassInfo> subclassInfos = new ArrayList<MappedClassInfo>();
private List<AttributeInfo> attributeInfos;
private Map<String, AttributeInfo> nameToAttributeInfoMap = new HashMap<String, AttributeInfo>();
private InheritanceInfo inheritanceInfo;
private Annotation annotation;
List<EntityListenerClassInfo> entityListenerClassInfos = new ArrayList<EntityListenerClassInfo>();


void buildHierarchyInfo() {
	superclassInfo = null;
	Class curSuper = clazz.getSuperclass();
	while (curSuper != Object.class && superclassInfo == null) {
		superclassInfo = (MappedClassInfo) oxyUnitInfo.classToInfoMap.get(curSuper);
		curSuper = curSuper.getSuperclass();
	}
	if (superclassInfo != null) {
		superclassInfo.subclassInfos.add(this);
	}
}

void buildAttributeInfos() throws Exception {
	attributeInfos = new ArrayList<AttributeInfo>();

	// only consider annotations
	buildAttributeInfosFromAnnotations();

	// build defaults where no annotations are defined
	buildAttributeInfoDefaultsWhereNeeded();

	parseAttributeAnnotations();
}

private void parseAttributeAnnotations() throws Exception {
	for (Method method : clazz.getDeclaredMethods())
		parseAttributeAnnotations(method);
	for (Field field : clazz.getDeclaredFields())
		parseAttributeAnnotations(field);
}

private void parseAttributeAnnotations(AnnotatedElement ae) throws Exception {
	parseColumnAnnotation(ae);
	parseDomainAnnotation(ae);
	parseJoinColumnAnnotation(ae);
	parseSequenceGeneratorAnnotation(ae);
	parseTableGeneratorAnnotation(ae);
	parseGeneratedValueAnnotation(ae);
}

private void parseGeneratedValueAnnotation(AnnotatedElement ae) throws Exception {
	if (!(ae.isAnnotationPresent(GeneratedValue.class)))
		return;

	String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
	AttributeInfo attributeInfo = getAttributeInfo(attributeName);
	if (!(attributeInfo instanceof BasicAttributeInfo))
		throw new Exception("the @GeneratedValue annotation is only for base attributes");
	BasicAttributeInfo basicAttributeInfo = (BasicAttributeInfo) attributeInfo;

	GeneratedValueInfo info = new GeneratedValueInfo();
	info.setGeneratedValue(ae.getAnnotation(GeneratedValue.class));
	basicAttributeInfo.setGeneratedValueInfo(info);
}

private void parseSequenceGeneratorAnnotation(AnnotatedElement ae) throws Exception {
	if (!(ae.isAnnotationPresent(SequenceGenerator.class)))
		return;

	SequenceGeneratorInfo sqinfo = new SequenceGeneratorInfo();
	sqinfo.setSeqGenerator(ae.getAnnotation(SequenceGenerator.class));
	oxyUnitInfo.sequenceGeneratorInfos.add(sqinfo);
}

private void parseTableGeneratorAnnotation(AnnotatedElement ae) throws Exception {
	if (!(ae.isAnnotationPresent(TableGenerator.class)))
		return;

	TableGeneratorInfo sqinfo = new TableGeneratorInfo();
	sqinfo.setTableGenerator(ae.getAnnotation(TableGenerator.class));
	oxyUnitInfo.tableGeneratorInfos.add(sqinfo);
}

private void parseJoinColumnAnnotation(AnnotatedElement ae) throws Exception {
	if (!(ae.isAnnotationPresent(JoinColumn.class) || ae.isAnnotationPresent(JoinColumns.class)))
		return;

	List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
	if (ae.isAnnotationPresent(JoinColumn.class))
		joinColumns.add(ae.getAnnotation(JoinColumn.class));
	else
		joinColumns.addAll(Arrays.asList(ae.getAnnotation(JoinColumns.class).value()));

	List<JoinColumnInfo> joinColumnInfos = new ArrayList<JoinColumnInfo>();
	for (JoinColumn col : joinColumns) {
		JoinColumnInfo info = new JoinColumnInfo();
		info.setJoinColumn(col);
		joinColumnInfos.add(info);
	}

	String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
	AttributeInfo attributeInfo = getAttributeInfo(attributeName);
	if (!(attributeInfo instanceof ToOneAttributeInfo))
		throw new Exception("the @JoinColumn annotation is only for attributes having @ManyToOne or @OneToOne annotations");

	((ToOneAttributeInfo) attributeInfo).setJoinColumnInfos(joinColumnInfos);
}

private void parseColumnAnnotation(AnnotatedElement ae) throws Exception {
	if (!ae.isAnnotationPresent(Column.class))
		return;

	String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
	AttributeInfo attributeInfo = getAttributeInfo(attributeName);
	if (!(attributeInfo instanceof BasicAttributeInfo))
		throw new Exception("the @Column annotation is only for base attributes");
	BasicAttributeInfo basicAttributeInfo = (BasicAttributeInfo) attributeInfo;

	ColumnInfo colInfo = new ColumnInfo();
	colInfo.setColumn(ae.getAnnotation(Column.class));
	basicAttributeInfo.setColumnInfo(colInfo);
}

private void parseDomainAnnotation(AnnotatedElement ae) throws Exception {
	if (!ae.isAnnotationPresent(Domain.class))
		return;

	String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
	AttributeInfo attributeInfo = getAttributeInfo(attributeName);
	if (!(attributeInfo instanceof BasicAttributeInfo))
		throw new Exception("the @Domain annotation is only for base attributes");
	BasicAttributeInfo basicAttributeInfo = (BasicAttributeInfo) attributeInfo;

	Domain domain = ae.getAnnotation(Domain.class);
	basicAttributeInfo.setDomainName(domain.name());
}

private void buildAttributeInfoDefaultsWhereNeeded() throws Exception {
	Map<String, String> propertyToFieldName = new HashMap<String, String>();
	for (Field field : clazz.getDeclaredFields()) {
		if (isTransient(field))
			continue;
		if (hasAttributeInfo(field.getName()))
			continue;
		propertyToFieldName.put(ReflectionUtil.buildPropertyName(field.getName()), field.getName());
	}
	for (PropertyDescriptor prop : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
		if (prop.getReadMethod() != null && prop.getReadMethod().getDeclaringClass() != clazz)
			continue;
		Method reader = prop.getReadMethod();
		if (reader == null)
			throw new IllegalStateException("Could not determine read method for property named " + prop.getName() + " in class " + clazz);
		if (isTransient(reader))
			continue;
		if (hasAttributeInfo(prop.getName()))
			continue;
		propertyToFieldName.put(prop.getName(), null);
	}
	for (Map.Entry<String, String> entry : propertyToFieldName.entrySet()) {
		buildDefaultAttributeInfo(entry.getKey(), entry.getValue());
	}
}

private void buildAttributeInfosFromAnnotations() {
	for (Method method : clazz.getDeclaredMethods())
		buildAttributeInfoFromAnnotations(method);
	for (Field field : clazz.getDeclaredFields())
		buildAttributeInfoFromAnnotations(field);
}

private void buildDefaultAttributeInfo(String propName, String fieldName) throws Exception {
	if (fieldName != null) {
		Field field = ReflectionUtil.getField(clazz, fieldName);
		if (field != null && ReflectionUtil.hasAnyEJB3Annotations(field)) {
			buildDefaultAttributeInfo(field, field.getType());
			return;
		}
	}
	PropertyDescriptor prop = ReflectionUtil.getPropertyDescriptor(clazz, propName);
	if (prop == null) {
		// property under expected propertyname not exist... do it like
		// hibernate and ignore it
		return;
	}
	buildDefaultAttributeInfo(prop.getReadMethod(), prop.getPropertyType());
}

private void buildDefaultAttributeInfo(AnnotatedElement ae, Class<?> type) {
	AttributeInfo attrInfo = new BasicAttributeInfo();
	attrInfo.setParentClassInfo(this);
	attrInfo.setAnnotatedElement(ae);
	addAttributeInfo(attrInfo);
}


@Override 
void resolveReferences() {
	for (AttributeInfo attrInfo : attributeInfos)
		attrInfo.resolveReferences();
	buildAdoptedEntityListenerClassInfos();
	buildAdoptedLifecycleEventInfos();
}

private void buildAdoptedEntityListenerClassInfos() {
	if (getClazz().isAnnotationPresent(ExcludeSuperclassListeners.class))
		return;
	
	MappedClassInfo info = this.getSuperclassInfo();
	
	if (info != null) {
		buildAdoptedEntityListenerClassInfos(info);
		info = info.getSuperclassInfo();
	}
}

private void buildAdoptedEntityListenerClassInfos(MappedClassInfo info) {
	for (EntityListenerClassInfo ci : info.entityListenerClassInfos) {
		entityListenerClassInfos.add(ci);
	}
}

private void buildAdoptedLifecycleEventInfos() {
	MappedClassInfo info = this.getSuperclassInfo();
	if (info != null) {
		buildAdoptedLifecycleEventInfos(info);
		info = info.getSuperclassInfo();
	}
}

private void buildAdoptedLifecycleEventInfos(MappedClassInfo info) {
	for (LifecycleEventInfo lc : info.lifecycleEventInfos) {
		if (!containsLifecycleEventInfo(lc))
			lifecycleEventInfos.add(lc);
	}
}


private boolean containsLifecycleEventInfo(LifecycleEventInfo info) {
	for (LifecycleEventInfo each : lifecycleEventInfos) {
		if (each.getEventType().equals(info.getEventType()) && each.getMethod().getName().equals(info.getMethod().getName()))
			return true;
	}
	return false;
}

private void buildAttributeInfoFromAnnotations(AnnotatedElement ae) {
	if (ae.isAnnotationPresent(Transient.class))
		return;

	AttributeInfo attrInfo;
	// strategy: JSR220 says, all non-transient attributes are persistent
	// first handle relations (look for annotations or relations to entities)
	// TODO is this really complete? i guess not
	if (ae.isAnnotationPresent(ManyToOne.class)) {
		attrInfo = new ManyToOneAttributeInfo();
	} else if (ae.isAnnotationPresent(OneToOne.class)) {
		attrInfo = new OneToOneAttributeInfo();
	} else if (ae.isAnnotationPresent(OneToMany.class)) {
		attrInfo = new OneToManyAttributeInfo();
	} else if (ae.isAnnotationPresent(ManyToMany.class)) {
		attrInfo = new ManyToManyAttributeInfo();
	} else if (ae.isAnnotationPresent(Basic.class)) {
		attrInfo = new BasicAttributeInfo();
	} else if (ae.isAnnotationPresent(Id.class)) {
		attrInfo = new BasicAttributeInfo();
	} else if (ae.isAnnotationPresent(Column.class)) {
		attrInfo = new BasicAttributeInfo();
	} else
		return;

	attrInfo.setParentClassInfo(this);
	attrInfo.setAnnotatedElement(ae);
	addAttributeInfo(attrInfo);
}

Class getClassMemberType(AnnotatedElement ae) {
	if (ae instanceof Field)
		return (Class) ((Field) ae).getGenericType();
	else
		return (Class) ((Method) ae).getGenericReturnType();
}

public List<AttributeInfo> getAttributeInfos() {
	return attributeInfos;
}

public boolean hasAttributeInfo(String name) {
	return nameToAttributeInfoMap.containsKey(name);
}

public AttributeInfo getAttributeInfo(String name) {
	AttributeInfo result = nameToAttributeInfoMap.get(name);
	if (result == null)
		throw new IllegalArgumentException("No AttributeInfo named " + name + " found in " + getName());
	return result;
}

public MappedClassInfo getSuperclassInfo() {
	return superclassInfo;
}

void addAttributeInfo(AttributeInfo info) {
	attributeInfos.add(info);
	nameToAttributeInfoMap.put(info.getName(), info);
}

public void debugPrint(PrintStream out) {
	if (this instanceof MappedSuperclassInfo)
		out.print("[");
	out.print(clazz.getSimpleName());
	if (superclassInfo != null) {
		out.print(" extends ");
		out.print(superclassInfo);
	}
	if (this instanceof MappedSuperclassInfo)
		out.print("]");
	out.println();
	for (AttributeInfo ai : attributeInfos)
		ai.debugPrint(out);
	debugPrintLifecycleEventInfos(out);
	if (entityListenerClassInfos.size() > 0) {
		out.print("\tEntityListenerInfos: ");
		for (Iterator<EntityListenerClassInfo> i = entityListenerClassInfos.iterator(); i.hasNext();) {
			EntityListenerClassInfo info = i.next();
			out.print(info.getName());
			if (i.hasNext())
				out.print(", ");
		}
	}
	out.println();
}

public String toString() {
	return clazz == null ? super.toString() : clazz.getSimpleName();
}

private boolean isTransient(AnnotatedElement ae) {
	if (ae instanceof Field && Modifier.isTransient(((Field) ae).getModifiers()))
		return true;
	return ae.isAnnotationPresent(Transient.class);
}

public InheritanceInfo getInheritanceInfo() {
	return inheritanceInfo;
}

void setInheritanceInfo(InheritanceInfo inheritanceInfo) {
	this.inheritanceInfo = inheritanceInfo;
}

public List<MappedClassInfo> getSubclassInfos() {
	return subclassInfos;
}

public boolean hasSubclassEntities() {
	for (MappedClassInfo sub : getSubclassInfos()) {
		if (sub instanceof EntityInfo)
			return true;
		boolean next = sub.hasSubclassEntities();
		if (next)
			return true;
	}
	return false;
}

public Annotation getAnnotation() {
	return annotation;
}

void setAnnotation(Annotation annotation) {
	this.annotation = annotation;
}

public List<EntityListenerClassInfo> getEntityListenerClassInfos() {
	return entityListenerClassInfos;
}

void setEntityListenerClassInfos(List<EntityListenerClassInfo> entityListenerClassInfos) {
	this.entityListenerClassInfos = entityListenerClassInfos;
}

}
