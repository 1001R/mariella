package org.mariella.rcp.beans;

public interface PropertyAccessor {

Object readValue(Object target);

void writeValue(Object target, Object value);

boolean canRead(Object target);

boolean canWrite(Object target);

}
