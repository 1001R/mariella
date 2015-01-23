package org.mariella.persistence.annotations.processing;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.mariella.persistence.annotations.Converter;
import org.mariella.persistence.annotations.Domain;
import org.mariella.persistence.mapping.AttributeAccessType;
import org.mariella.persistence.mapping.AttributeInfo;
import org.mariella.persistence.mapping.BasicAttributeInfo;
import org.mariella.persistence.mapping.ColumnInfo;
import org.mariella.persistence.mapping.GeneratedValueInfo;
import org.mariella.persistence.mapping.JoinColumnInfo;
import org.mariella.persistence.mapping.ManyToManyAttributeInfo;
import org.mariella.persistence.mapping.ManyToOneAttributeInfo;
import org.mariella.persistence.mapping.MappedClassInfo;
import org.mariella.persistence.mapping.OneToManyAttributeInfo;
import org.mariella.persistence.mapping.OneToOneAttributeInfo;
import org.mariella.persistence.mapping.OrderByInfo;
import org.mariella.persistence.mapping.ReflectionUtil;
import org.mariella.persistence.mapping.RelationAttributeInfo;
import org.mariella.persistence.mapping.SequenceGeneratorInfo;
import org.mariella.persistence.mapping.ToManyAttributeInfo;

public class MappedClassInfoAttributeInfosBuilder {
	
	OxyUnitInfoBuilder oxyUnitInfoBuilder;
	MappedClassInfo mappedClassInfo;
	IModelToDb translator;
	
	public MappedClassInfoAttributeInfosBuilder(OxyUnitInfoBuilder oxyUnitInfoBuilder, MappedClassInfo mappedClassInfo, IModelToDb translator) {
		this.oxyUnitInfoBuilder = oxyUnitInfoBuilder;
		this.mappedClassInfo = mappedClassInfo;
		this.translator = translator;
	}
	
	void buildAttributeInfos() throws Exception {
		mappedClassInfo.setAttributeInfos(new ArrayList<AttributeInfo>());

		// only consider annotations
		buildAttributeInfosFromAnnotations();

		// build defaults where no annotations are defined
		buildAttributeInfoDefaultsWhereNeeded();

		parseAttributeAnnotations();
	}

	private void parseAttributeAnnotations() throws Exception {
		for (Method method : mappedClassInfo.getClazz().getDeclaredMethods())
			parseAttributeAnnotations(method);
		for (Field field : mappedClassInfo.getClazz().getDeclaredFields())
			parseAttributeAnnotations(field);
	}
	


	private void parseAttributeAnnotations(AnnotatedElement ae) throws Exception {
		parseColumnAnnotation(ae);
		parseDomainAnnotation(ae);
		parseConverterAnnotation(ae);
		parseJoinColumnAnnotation(ae);
		parseSequenceGeneratorAnnotation(ae);
		parseTableGeneratorAnnotation(ae);
		parseGeneratedValueAnnotation(ae);
		parseOrderByAnnotation(ae);
	}

	private void parseColumnAnnotation(AnnotatedElement ae) throws Exception {
		if (!ae.isAnnotationPresent(Column.class))
			return;

		String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
		AttributeInfo attributeInfo = mappedClassInfo.getAttributeInfo(attributeName);
		if (!(attributeInfo instanceof BasicAttributeInfo))
			throw new Exception("the @Column annotation is only for base attributes");
		BasicAttributeInfo basicAttributeInfo = (BasicAttributeInfo) attributeInfo;

		Column column = ae.getAnnotation(Column.class); 
		
		ColumnInfo colInfo = new ColumnInfo();
		colInfo.setColumnDefinition(column.columnDefinition());
		colInfo.setInsertable(column.insertable());
		colInfo.setLength(column.length());
		colInfo.setName(translator.translate(column.name()));
		colInfo.setNullable(column.nullable());
		colInfo.setPrecision(column.precision());
		colInfo.setScale(column.scale());
		colInfo.setTable(translator.translate(column.table()));
		colInfo.setUnique(column.unique());
		colInfo.setUpdatable(column.updatable());
		basicAttributeInfo.setColumnInfo(colInfo);
	}

	private void parseDomainAnnotation(AnnotatedElement ae) throws Exception {
		if (!ae.isAnnotationPresent(Domain.class))
			return;

		String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
		AttributeInfo attributeInfo = mappedClassInfo.getAttributeInfo(attributeName);
		if (!(attributeInfo instanceof BasicAttributeInfo))
			throw new Exception("the @Domain annotation is only for base attributes");
		BasicAttributeInfo basicAttributeInfo = (BasicAttributeInfo) attributeInfo;

		Domain domain = ae.getAnnotation(Domain.class);
		basicAttributeInfo.setDomainName(domain.name());
	}

	private void parseConverterAnnotation(AnnotatedElement ae) throws Exception {
		if (!ae.isAnnotationPresent(Converter.class))
			return;

		String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
		AttributeInfo attributeInfo = mappedClassInfo.getAttributeInfo(attributeName);
		if (!(attributeInfo instanceof BasicAttributeInfo))
			throw new Exception("the @Converter annotation is only for base attributes");
		BasicAttributeInfo basicAttributeInfo = (BasicAttributeInfo) attributeInfo;

		Converter converter = ae.getAnnotation(Converter.class);
		basicAttributeInfo.setConverterName(converter.name());
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
			joinColumnInfos.add(new JoinColumnInfoBuilder(col, translator).buildJoinColumnInfo());
		}

		String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
		AttributeInfo attributeInfo = mappedClassInfo.getAttributeInfo(attributeName);
		if (!(attributeInfo instanceof RelationAttributeInfo))
			throw new Exception("the @JoinColumn annotation is only for relationship attributes; attribute: " + mappedClassInfo.toString() + attributeInfo.toString());

		((RelationAttributeInfo) attributeInfo).setJoinColumnInfos(joinColumnInfos);
	}

	private void parseSequenceGeneratorAnnotation(AnnotatedElement ae) throws Exception {
		if (!(ae.isAnnotationPresent(SequenceGenerator.class)))
			return;

		SequenceGenerator gen = ae.getAnnotation(SequenceGenerator.class);
		
		SequenceGeneratorInfo sqinfo = new SequenceGeneratorInfo();
		sqinfo.setAllocationSize(gen.allocationSize());
		sqinfo.setInitialValue(gen.initialValue());
		sqinfo.setName(gen.name());
		sqinfo.setSequenceName(gen.sequenceName());
		mappedClassInfo.getOxyUnitInfo().getSequenceGeneratorInfos().add(sqinfo);
	}


	private void parseTableGeneratorAnnotation(AnnotatedElement ae) throws Exception {
		if (!(ae.isAnnotationPresent(TableGenerator.class)))
			return;


		TableGenerator tableGenerator = ae.getAnnotation(TableGenerator.class);

		new TableGeneratorInfoBuilder(tableGenerator, mappedClassInfo.getOxyUnitInfo(), translator).buildInfo();
	}

	private void parseGeneratedValueAnnotation(AnnotatedElement ae) throws Exception {
		if (!(ae.isAnnotationPresent(GeneratedValue.class)))
			return;

		String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
		AttributeInfo attributeInfo = mappedClassInfo.getAttributeInfo(attributeName);
		if (!(attributeInfo instanceof BasicAttributeInfo))
			throw new Exception("the @GeneratedValue annotation is only for base attributes");
		BasicAttributeInfo basicAttributeInfo = (BasicAttributeInfo) attributeInfo;

		
		GeneratedValue generatedValue = ae.getAnnotation(GeneratedValue.class);
		
		GeneratedValueInfo info = new GeneratedValueInfo();
		info.setGenerator(generatedValue.generator());
		info.setStrategy(generatedValue.strategy());
		
		basicAttributeInfo.setGeneratedValueInfo(info);
	}


	private void parseOrderByAnnotation(AnnotatedElement ae) throws Exception {
		if (!(ae.isAnnotationPresent(OrderBy.class)))
			return;

		String attributeName = ReflectionUtil.readFieldOrPropertyName(ae);
		AttributeInfo attributeInfo = mappedClassInfo.getAttributeInfo(attributeName);
		if (!(attributeInfo instanceof ToManyAttributeInfo))
			throw new Exception("the @GeneratedValue annotation is only for @OneToMany and @ManyToMany attributes");
		ToManyAttributeInfo toManyAttributeInfo = (ToManyAttributeInfo) attributeInfo;

		OrderBy orderBy = ae.getAnnotation(OrderBy.class);
		
		OrderByInfo info = new OrderByInfo();
		info.setOrderBy(translator.translate(orderBy.value()));
		toManyAttributeInfo.setOrderByInfo(info);

	}


	private void buildAttributeInfosFromAnnotations() {
		for (Method method : mappedClassInfo.getClazz().getDeclaredMethods())
			buildAttributeInfoFromAnnotations(method);
		for (Field field : mappedClassInfo.getClazz().getDeclaredFields())
			buildAttributeInfoFromAnnotations(field);
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

		oxyUnitInfoBuilder.attributeInfoToAnnotatedElementMap.put(attrInfo, ae);
		attrInfo.setParentClassInfo(mappedClassInfo);
		
		applyAttributeInfoProperties(attrInfo, ae);
		mappedClassInfo.addAttributeInfo(attrInfo);
	}

	private void applyAttributeInfoProperties(AttributeInfo attrInfo, AnnotatedElement ae) {
		attrInfo.setAccessType(ae instanceof Field ? AttributeAccessType.FIELD : AttributeAccessType.PROPERTY);
		attrInfo.setName(ReflectionUtil.readFieldOrPropertyName(ae));
		if (attrInfo instanceof BasicAttributeInfo) {
			((BasicAttributeInfo)attrInfo).setId(isId(ae));
			((BasicAttributeInfo)attrInfo).setOptionalOrNullable(isOptionalOrNullable(ae));
			((BasicAttributeInfo)attrInfo).setType(ReflectionUtil.readType(ae));
		} else if (attrInfo instanceof ManyToOneAttributeInfo) {
			ManyToOne anno = ae.getAnnotation(ManyToOne.class);
			((ManyToOneAttributeInfo)attrInfo).setOptionalOrNullable(anno.optional());
		} else if (attrInfo instanceof OneToOneAttributeInfo) {
			OneToOne anno = ae.getAnnotation(OneToOne.class);
			((OneToOneAttributeInfo)attrInfo).setOptionalOrNullable(anno.optional());
		}
	}

	private boolean isId(AnnotatedElement ae) {
		return ae.isAnnotationPresent(Id.class) || ae.isAnnotationPresent(javax.persistence.EmbeddedId.class);
	}

	private boolean isOptionalOrNullable(AnnotatedElement ae) {
		if (isId(ae))
			return false;
		
		Basic basic = ae.getAnnotation(Basic.class);
		if (basic != null && !basic.optional()) return false;
		Column col = ae.getAnnotation(Column.class);
		if (col == null) 
			return !isId(ae);
		return col.nullable();
	}

	private void buildAttributeInfoDefaultsWhereNeeded() throws Exception {
		Map<String, String> propertyToFieldName = new HashMap<String, String>();
		Set<String> transientFields = new HashSet<String>();
		for (Field field : mappedClassInfo.getClazz().getDeclaredFields()) {
			if (isTransient(field)) {
				transientFields.add(field.getName());
				continue;
			}
			if (mappedClassInfo.hasAttributeInfo(field.getName()))
				continue;
			propertyToFieldName.put(ReflectionUtil.buildPropertyName(field.getName()), field.getName());
		}
		for (PropertyDescriptor prop : Introspector.getBeanInfo(mappedClassInfo.getClazz()).getPropertyDescriptors()) {
			if (transientFields.contains(prop.getName()))
				continue;
			if (prop.getReadMethod() != null && prop.getReadMethod().getDeclaringClass() != mappedClassInfo.getClazz())
				continue;
			Method reader = prop.getReadMethod();
			if (reader == null)
				throw new IllegalStateException("Could not determine read method for property named " + prop.getName() + " in class " + mappedClassInfo.getClazz());
			if (isTransient(reader)) {
				propertyToFieldName.remove(prop.getName());
				continue;
			} if (mappedClassInfo.hasAttributeInfo(prop.getName()))
				continue;
			propertyToFieldName.put(prop.getName(), null);
		}
		for (Map.Entry<String, String> entry : propertyToFieldName.entrySet()) {
			buildDefaultAttributeInfo(entry.getKey(), entry.getValue());
		}
	}

	private boolean isTransient(AnnotatedElement ae) {
		if (ae instanceof Field && Modifier.isTransient(((Field) ae).getModifiers()))
			return true;
		return ae.isAnnotationPresent(Transient.class);
	}

	private void buildDefaultAttributeInfo(String propName, String fieldName) throws Exception {
		if (fieldName != null) {
			Field field = ReflectionUtil.getField(mappedClassInfo.getClazz(), fieldName);
			if (field != null && ReflectionUtil.hasAnyEJB3Annotations(field)) {
				buildDefaultAttributeInfo(field, field.getType());
				return;
			}
		}
		PropertyDescriptor prop = ReflectionUtil.getPropertyDescriptor(mappedClassInfo.getClazz(), propName);
		if (prop == null) {
			// property under expected propertyname not exist... do it like
			// hibernate and ignore it
			return;
		}
		buildDefaultAttributeInfo(prop.getReadMethod(), prop.getPropertyType());
	}


	private void buildDefaultAttributeInfo(AnnotatedElement ae, Class<?> type) {
		AttributeInfo attrInfo = new BasicAttributeInfo();
		attrInfo.setParentClassInfo(mappedClassInfo);
		applyAttributeInfoProperties(attrInfo, ae);
		mappedClassInfo.addAttributeInfo(attrInfo);
	}

}
