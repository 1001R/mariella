package org.mariella.glue.annotations.processing.test;

import org.mariella.glue.annotations.BindingDomain;

public class Person {
	
	private String name;
	
	@BindingDomain(name="PersonAge")
	private Integer age;

@BindingDomain(name="PersonName")
public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public Integer getAge() {
	return age;
}

public void setAge(Integer age) {
	this.age = age;
}

}
