package org.mariella.persistence.annotations.processing.test;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.mariella.persistence.annotations.Converter;

@Embeddable
public class Underpants {
	
	private String color;
	private int size;
	
@Column(name="COLOR",length=20)
@Converter(name="StringConverter")
public String getColor() {
	return color;
}

public void setColor(String color) {
	this.color = color;
}

@Column(name="SIZE")
@Converter(name="IntConverter")
public int getSize() {
	return size;
}

public void setSize(int size) {
	this.size = size;
}

}
