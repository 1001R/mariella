package org.mariella.rcp.table.edit;

import java.util.Set;

import org.eclipse.swt.dnd.Transfer;

public abstract class TableDropHandler {

/**
 * Reciever is responsible to return true if it is responsible for the targetElement / dropObject.
 * 
 * @param element
 * @return
 */
public abstract boolean isResponsibleFor(Object targetElement, Object dropObject);

/**
 * Receiver is responsible for modifying the targetElement with data from the dropObject.
 * 
 * @param targetElement
 * @param dropObject
 */
public abstract void handleDrop(Object targetElement, Object dropObject);

/**
 * Invoked after all handleDrop(...) is invoked  for all handlers and the targetElement has its new state.  
 * Receivers  can do additionally refresh stuff (handling dependencies).
 * 
 * @param targetElement
 * @param dropObject
 */
public abstract void handlePostDrop(Object targetElement, Object dropObject);

/**
 * Receivers can return a new Element when the drop is done over an empty part of the table.
 * 
 * @return
 */
public abstract Object handleDropElementOnEmptySelection(Object dropObject);

/**
 * Receivers are responsible for adding those Transfer objects, for which they are responsible for
 * 
 * @param transfers
 */
public abstract void collectTransfers(Set<Transfer> transfers);

}
