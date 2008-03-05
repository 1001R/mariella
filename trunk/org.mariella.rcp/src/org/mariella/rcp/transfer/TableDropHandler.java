package org.mariella.rcp.transfer;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.dnd.Transfer;

public abstract class TableDropHandler {

/**
 * Reciever is responsible to return true if it is responsible for the targetElement / dropObject.
 * 
 * @param element
 * @return
 */
public abstract boolean isResponsibleFor(Object dropObject);

/**
 * Receiver is responsible for doing the drop job and to return a list of new/modified elements.
 * 
 * @param dropObject
 */
public abstract List handleDrop(Object dropObject);

/**
 * Invoked after all handleDrop(...) is invoked  for all handlers.  
 * Receivers  can do additionally refresh stuff (handling dependencies).
 * 
 * @param dropObject
 */
public abstract void handlePostDrop(Object dropObject);

/**
 * Receivers are responsible for adding those Transfer objects, for which they are responsible for
 * 
 * @param transfers
 */
public abstract void collectTransfers(Set<Transfer> transfers);

}
