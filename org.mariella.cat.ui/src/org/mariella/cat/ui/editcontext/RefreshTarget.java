package org.mariella.cat.ui.editcontext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class RefreshTarget {
	boolean fullRefresh = false;
	private List<Object> elements = null;

	RefreshTarget() {
	}

	public RefreshTarget(boolean fullRefresh, Object... elements) {
		this.fullRefresh = fullRefresh;
		if (elements.length > 0) {
			this.elements = new ArrayList<Object>(Arrays.asList(elements));
		}
	}

	void reset() {
		fullRefresh = false;
		elements = null;
	}

	boolean isRefreshNeeded() {
		return fullRefresh || elements != null;
	}

	public void fullRefresh() {
		this.fullRefresh = true;
	}

	public void mergeFullRefresh(boolean wantsFullRefresh) {
		this.fullRefresh |= wantsFullRefresh;
	}

	public boolean isFullRefresh() {
		return fullRefresh;
	}

	public void addElement(Object element) {
		if (element == null) {
			throw new NullPointerException("null-refresh element not allowed!");
		} else if (elements == null) {
			elements = new ArrayList<Object>(3);
			elements.add(element);
		} else if (!elements.contains(element)) {
			elements.add(element);
		}
	}

	public void addElements(Object... elements) {
		for (Object hint : elements) {
			addElement(hint);
		}
	}

	public boolean isElementSet(Object element) {
		return elements != null && elements.contains(element);
	}

	public Object[] getElements() {
		return elements == null ? new Object[0] : elements.toArray();
	}
}