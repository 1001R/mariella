package org.mariella.oxygen.basic_core;

import org.mariella.persistence.schema.PropertyDescription;

/**
 * EntityListeners are intercepting the read and write access to an entities property.
 * There are also callback methods for an entities lifecycle.
 * 
 * Applications should not use this interface since its specific to oxygen. Listening
 * to an entities lifecycle should be realized using the EntityListener mechanism specified
 * in the JSR 220.
 * 
 * @author aim
 *
 * @see EntityFacade
 * @see EntityManager
 */
public interface EntityObserver {

/**
 * is called after the entity was removed from the entity manager 
 */
public void removed();

/**
 * is called after the entities state has been flushed by the entity manager 
 */
public void flushed();


/**
 * is called before an entities property is read
 * @param attributeDescription
 * @throws Exception
 */
public void aboutToReadAttribute(PropertyDescription pd) throws Exception;

/**
 * is called before an entities property is modified
 * @param attributeDescription
 * @throws Exception
 */
public void aboutToModifyAttribute(PropertyDescription pd) throws Exception;

/**
 * is called after an entities property is modified
 * @param attributeDescription
 * @throws Exception
 */
public void attributeModified(PropertyDescription pd) throws Exception;
}