package org.mariella.rcp.databinding;

public interface ConversionCallback {

Object getObjectForText(Object domainContext, String text);

String getTextForObject(Object domainContext, Object entity);

}
