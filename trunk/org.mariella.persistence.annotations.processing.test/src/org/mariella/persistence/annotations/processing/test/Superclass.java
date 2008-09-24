package org.mariella.persistence.annotations.processing.test;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.mariella.persistence.annotations.Converter;
import org.mariella.persistence.annotations.Domain;

@Entity(name="Superclass")
@Inheritance(
		strategy=InheritanceType.JOINED
)
@Table(name="SUPERCLASS")
public class Superclass extends UnmappedSuperclass implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Timestamp creation;
	private Timestamp lastUpdate;
	transient boolean loaded = false;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

@Column(name="ID", nullable=false)
@Domain(name="Id")
@Id
@GeneratedValue(strategy=GenerationType.TABLE, generator="IdGenerator")
@TableGenerator(
		name="IdGenerator",
		table="IDGENERATOR",
		pkColumnName="GEN_KEY",
		valueColumnName="GEN_VALUE",
		allocationSize=50)
@Converter(name="IntConverter")
public Integer getId() {
	return id;
}

@Column(name="CREATION", nullable=true)
@Converter(name="TimestampConverter")
public Timestamp getCreation() {
	return creation;
}

@Column(name="LASTUPDATE", nullable=true)
@Converter(name="TimestampConverter")
public Timestamp getLastUpdate() {
	return lastUpdate;
}

public void setId(Integer id) {
	this.id = id;
}

public void setCreation(Timestamp creation) {
	this.creation = creation;
}

public void setLastUpdate(Timestamp lastUpdate) {
	this.lastUpdate = lastUpdate;
}

@Transient
public PropertyChangeSupport getPropertyChangeSupport() {
	return propertyChangeSupport;
}

}
