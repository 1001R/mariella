package org.mariella.persistence.annotations.mapping_builder;

import java.io.Serializable;

public class DatabaseColumnInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private boolean nullable;
	private int type;
	private int length = -1;;
	private int scale = -1;
	
public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public int getType() {
	return type;
}

public void setType(int type) {
	this.type = type;
}

public int getLength() {
	return length;
}

public void setLength(int length) {
	this.length = length;
}

public int getScale() {
	return scale;
}

public void setScale(int scale) {
	this.scale = scale;
}

public boolean isNullable() {
	return nullable;
}

public void setNullable(boolean nullable) {
	this.nullable = nullable;
}

}
