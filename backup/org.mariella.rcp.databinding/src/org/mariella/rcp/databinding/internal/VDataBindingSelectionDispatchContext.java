package org.mariella.rcp.databinding.internal;

import java.util.Iterator;
import java.util.Stack;

import org.eclipse.swt.widgets.Display;

public class VDataBindingSelectionDispatchContext {

public Object[] selectionPath;
public int selectionPathOffset = -1;
Stack<Integer> markers = new Stack<Integer>();
public boolean dispatched = false;

public Iterator<VDataBindingSelectionDispatcher> dispatcherChain;

public void invokeNextDispatcher(boolean async) {
	if (!dispatcherChain.hasNext()) return;
	final VDataBindingSelectionDispatcher next = dispatcherChain.next();
	Runnable dispatchBlock = new Runnable() {
		public void run() {
			next.dispatchSelection(VDataBindingSelectionDispatchContext.this);
		}
	};
	if (!async)
		dispatchBlock.run();
	else
		Display.getCurrent().asyncExec(dispatchBlock);
	
}

public Object nextPathToken() {
	selectionPathOffset++;
	if (selectionPathOffset < selectionPath.length)
		return selectionPath[selectionPathOffset];
	return null;
}

public boolean matchesPath(Object[] matchPath) {
	for (int i=selectionPathOffset, m = 0; m<matchPath.length && i<selectionPath.length; i++, m++)
		if (!selectionPath[i].equals(matchPath[m]))	return false;
	return true;
}

public void markOffset() {
	markers.push(selectionPathOffset);
}

public void resetOffset() {
	selectionPathOffset = markers.pop();
}

public void incrementOffset(int increment) {
	selectionPathOffset += increment;
}

public boolean hasNextPathToken() {
	return selectionPathOffset + 1 < selectionPath.length;
}

}
