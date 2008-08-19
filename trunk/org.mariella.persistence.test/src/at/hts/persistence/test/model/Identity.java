package at.hts.persistence.test.model;


public class Identity {
	private String name;
	private int age;
	
public Identity(String name, int age) {
	super();
	this.name = name;
	this.age = age;
}

public Identity() {
	super();
}

public String getName() {
	return name;
}

public int getAge() {
	return age;
}

public void setName(String name) {
	this.name = name;
}

public void setAge(int age) {
	this.age = age;
}

}
