package org.mariella.persistence.springtest.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mariella.persistence.runtime.TrackedList;

@Entity
@Table(name="PERSON")
public class Person extends Superclass implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private List<Address> addresses = new TrackedList<>(propertyChangeSupport, "addresses");
	
	private List<Person> friends = new TrackedList<Person>(propertyChangeSupport, "friends");
	private List<Person> friendOf = new TrackedList<Person>(propertyChangeSupport, "friendOf");
	
@Column(name="NAME")
public String getName() {
	return name;
}

public void setName(String name) {
	String old = this.name;
	this.name = name;
	propertyChangeSupport.firePropertyChange("name", old, name);
}

@OneToMany(mappedBy="person")
public List<Address> getAddresses() {
	return addresses;
}

@ManyToMany
@JoinTable(
	name="FRIEND",
	joinColumns = @JoinColumn(name="MY_ID", referencedColumnName="ID"),
	inverseJoinColumns = @JoinColumn(name="FRIEND_ID", referencedColumnName="ID")
)
public List<Person> getFriends() {
	return friends;
}

@ManyToMany(mappedBy="friends")
public List<Person> getFriendOf() {
	return friendOf;
}

}
