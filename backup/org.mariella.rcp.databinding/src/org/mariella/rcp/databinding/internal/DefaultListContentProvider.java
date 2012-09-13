package org.mariella.rcp.databinding.internal;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DefaultListContentProvider implements IStructuredContentProvider {

public Object[] getElements(Object inputElement) {
	return ((List)inputElement).toArray();
}

public void dispose() {}

public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

}
