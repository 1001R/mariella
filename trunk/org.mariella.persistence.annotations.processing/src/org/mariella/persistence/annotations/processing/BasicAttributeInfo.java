package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.lang.reflect.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

@SuppressWarnings("unchecked")
public class BasicAttributeInfo extends AttributeInfo {

	private Type cachedType = null;
	private ColumnInfo columnInfo = null;
	private String domainName = null;
	private String converterName = null;
	private GeneratedValueInfo generatedValueInfo = null;

	
public BasicAttributeInfo() {
}

public boolean isId() {
	return getAnnotatedElement().isAnnotationPresent(Id.class) || getAnnotatedElement().isAnnotationPresent(EmbeddedId.class);
}

@Override
BasicAttributeInfo copyForAdoption() {
	BasicAttributeInfo copy = new BasicAttributeInfo();
	initializeAdoptionCopy(copy);
	return copy;
}

@Override
void initializeAdoptionCopy(AttributeInfo copy) {
	super.initializeAdoptionCopy(copy);
	((BasicAttributeInfo)copy).cachedType = cachedType;
	((BasicAttributeInfo)copy).columnInfo = columnInfo;
	((BasicAttributeInfo)copy).domainName = domainName;
	((BasicAttributeInfo)copy).generatedValueInfo = generatedValueInfo;
	((BasicAttributeInfo)copy).converterName = converterName;
}

void debugPrintAttributes(PrintStream out) {
	super.debugPrintAttributes(out);
	if (isId()) out.print(" @id");
	if (converterName != null) {
		out.print(" @converter=");
		out.print(converterName);
	}
	if (columnInfo != null) {
		columnInfo.debugPrint(out);
	}
}

void debugPrintTypeInfo(PrintStream out) {
	out.print("<" + ((Class)getType()).getSimpleName() + ">");
}

@Override
public boolean isOptionalOrNullable() {
	if (isId()) return false;
	
	Basic basic = getAnnotatedElement().getAnnotation(Basic.class);
	if (basic != null && !basic.optional()) return false;
	Column col = getAnnotatedElement().getAnnotation(Column.class);
	if (col == null) return !isId();
	return col.nullable();
}

public Type getType() {
	if (cachedType != null) return cachedType;
	
	cachedType = ReflectionUtil.readType(getAnnotatedElement());
	return cachedType;
}

public ColumnInfo getColumnInfo() {
	return columnInfo;
}

void setColumnInfo(ColumnInfo columnInfo) {
	this.columnInfo = columnInfo;
}

void setGeneratedValueInfo(GeneratedValueInfo generatedValueInfo) {
	this.generatedValueInfo = generatedValueInfo;
}

public GeneratedValueInfo getGeneratedValueInfo() {
	return generatedValueInfo;
}

public String getDomainName() {
	return domainName;
}

void setDomainName(String domainName) {
	this.domainName = domainName;
}

public String getConverterName() {
	return converterName;
}

void setConverterName(String converterName) {
	this.converterName = converterName;
}

@Override
public void override(AttributeInfo overriddenAttrInfo) {
	if (columnInfo == null) {
		columnInfo = ((BasicAttributeInfo)overriddenAttrInfo).columnInfo;
	}
	if (domainName == null) {
		domainName = ((BasicAttributeInfo)overriddenAttrInfo).domainName;
	}
	if (generatedValueInfo == null) {
		generatedValueInfo = ((BasicAttributeInfo)overriddenAttrInfo).generatedValueInfo;
	}
	if (converterName == null) {
		converterName = ((BasicAttributeInfo)overriddenAttrInfo).converterName;
	}
}

}
