package org.mariella.persistence.annotations.processing;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import javax.persistence.JoinTable;

public class JoinTableInfo {
/*
@Target({METHOD, FIELD}) @Retention(RUNTIME)
public @interface JoinTable {
String name() default "";
String catalog() default "";
String schema() default "";
JoinColumn[] joinColumns() default {};
JoinColumn[] inverseJoinColumns() default {};
UniqueConstraint[] uniqueConstraints() default {};
}
 */

	JoinTable annotation;
	@SuppressWarnings("unchecked")
	private List<JoinColumnInfo> joinColumnInfos = Collections.EMPTY_LIST;
	@SuppressWarnings("unchecked")
	private List<JoinColumnInfo> inverseJoinColumnInfos = Collections.EMPTY_LIST;
	@SuppressWarnings("unchecked")
	private List<UniqueConstraintInfo> uniqueConstraintInfos = Collections.EMPTY_LIST;

public String getName() {
	return annotation.name();
}

public String getCatalog() {
	return annotation.catalog();
}

public String getSchema() {
	return annotation.schema();
}

public List<JoinColumnInfo> getJoinColumnInfos() {
	return joinColumnInfos;
}

void setJoinColumnInfos(List<JoinColumnInfo> joinColumnInfos) {
	this.joinColumnInfos = joinColumnInfos;
}

void setAnnotation(JoinTable anno) {
	this.annotation = anno;
}

public List<UniqueConstraintInfo> getUniqueConstraintInfos() {
	return uniqueConstraintInfos;
}

void setUniqueConstraintInfos(List<UniqueConstraintInfo> uniqueConstraintInfos) {
	this.uniqueConstraintInfos = uniqueConstraintInfos;
}

public void debugPrint(PrintStream out) {
	out.print(" @JoinTable ");
	out.print(getName());
	for (JoinColumnInfo info : getJoinColumnInfos()) {
		info.debugPrint(out);
	}
	for (UniqueConstraintInfo info : getUniqueConstraintInfos()) {
		info.debugPrint(out);
	}
		
}

public List<JoinColumnInfo> getInverseJoinColumnInfos() {
	return inverseJoinColumnInfos;
}

void setInverseJoinColumnInfos(List<JoinColumnInfo> inverseJoinColumnInfos) {
	this.inverseJoinColumnInfos = inverseJoinColumnInfos;
}
	
}
