package org.mariella.cat.ui.editcontext;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.mariella.cat.common.SafeList;
import org.mariella.cat.ui.controls.radioset.RadioSet;


public abstract class RefreshContext {

	private class RefreshRunner implements Runnable {
		private boolean queued = false;
		private RefreshTarget refreshTarget = new RefreshTarget();
		private Display display;
		private RefreshRunner(Display display) {
			this.display = display;
		}
		@Override
		public void run() {
			try {
				update();
			} finally {
				queued = false;
			}
		}
		public void update() {
			if (refreshTarget.isRefreshNeeded() && !display.isDisposed()) {
				try {
					refresh(refreshTarget);
				} finally {
					refreshTarget.reset();
				}
			}
		}
		public void markDirty() {
			if (!queued) {
				queued = true;
				display.asyncExec(this);
			}
		}
	}

	private static interface IReleaseable {
		void release();
	}
	
	public static enum ChangeOp {
		ADD,
		REMOVE,
		PROPERTY
	}
	
	
	private RefreshRunner refreshRunner;
	private boolean refreshing = false;
	private List<IReleaseable> releasables = new ArrayList<IReleaseable>(3);
	private SafeList<IRefreshContextExt> extensions = new SafeList<IRefreshContextExt>();
	
	protected abstract void needRefresh(RefreshTarget refreshTarget, ChangeOp operation, Object element, String propertyName, Object value);
	
	void setDisplay(Display display) {
		if (display == null) {
			refreshRunner = null;
		} else if (refreshRunner == null || refreshRunner.display != display) {
			refreshRunner = new RefreshRunner(display);
		}
	}

	public void release() {
		if (refreshRunner != null) {
			refreshRunner.refreshTarget.reset();
		}
		refreshRunner = null;
		for (IReleaseable releaseable : releasables) {
			releaseable.release();
		}
	}

	public void handleModelChange(ChangeOp operation, Object element, String propertyName, Object value) {
		if (refreshRunner != null && !refreshRunner.refreshTarget.fullRefresh) {
			needRefresh(refreshRunner.refreshTarget, operation, element, propertyName, value);
			for (IRefreshContextExt extension : extensions) {
				extension.needRefresh(refreshRunner.refreshTarget, operation, element, propertyName, value);
			}
			if (refreshRunner.refreshTarget.isRefreshNeeded()) {
				refreshRunner.markDirty();
			}
		}
	}

	public final void markDirty(boolean fullRefresh, Object... elements) {
		if (refreshRunner != null) {
			refreshRunner.refreshTarget.mergeFullRefresh(fullRefresh);
			refreshRunner.refreshTarget.addElements(elements);
			refreshRunner.markDirty();
		}
	}
	
	public void update() {
		refreshRunner.update();
	}
	
	public final void refresh(boolean fullRefresh, Object... elements) {
		markDirty(fullRefresh, elements);
		update();
	}

	private void refresh(RefreshTarget refreshTarget) {
		if (refreshTarget == null) {
			throw new NullPointerException("null refreshTarget not allowed!");
		}
		boolean refreshingMarker = refreshing;
		refreshing = true;
		try {
			doRefresh(refreshTarget);
			for (IRefreshContextExt extension : extensions) {
				extension.doRefresh(refreshTarget);
			}
		} finally {
			refreshing = refreshingMarker;
		}
	}

	protected abstract void doRefresh(RefreshTarget refreshTarget);

	public final boolean isRefreshing() {
		return refreshing;
	}

	public abstract boolean canRefresh(Object control);

	public void updateUiValue(StyledText styledText, String modelValue) {
		if (canRefresh(styledText)) {
			modelValue = (modelValue == null ? "" : modelValue);
			String uiValue = styledText.getText();
			if (!modelValue.equals(uiValue)) {
				styledText.setText(modelValue);
			}
		}
	}

	public void updateUiValue(StyledText styledText, Object modelValue, Format format) {
		updateUiValue(styledText, modelValue == null ? null : format.format(modelValue));
	}

	public void updateUiValue(Text text, String modelValue) {
		if (canRefresh(text)) {
			modelValue = (modelValue == null ? "" : modelValue);
			String uiValue = text.getText();
			if (!modelValue.equals(uiValue)) {
				text.setText(modelValue);
			}
		}
	}

	public void updateUiValue(Text text, Object modelValue, Format format) {
		updateUiValue(text, modelValue == null ? null : format.format(modelValue));
	}

	public <T> void updateUiValue(RadioSet<T> radioSet, T value) {
		radioSet.select(value);
	}

	public void updateUiValue(Button button, Boolean selected) {
		boolean selectedValue = selected != null && selected;
		if (button.getSelection() != selectedValue) {
			button.setSelection(selectedValue);
		}
	}

	public void updateUiValue(ComboViewer viewer, Object value) {
		if (canRefresh(viewer.getControl())) {
			viewer.setSelection(value == null ? StructuredSelection.EMPTY : new StructuredSelection(value));
		}
	}

	public void updateUiEnabled(Control control, boolean enabled) {
		if (control.isEnabled() != enabled) {
			control.setEnabled(enabled);
		}
	}

	public void updateUiEnabled(Viewer viwer, boolean enabled) {
		if (viwer.getControl().isEnabled() != enabled) {
			viwer.getControl().setEnabled(enabled);
		}
	}

	public void updateUiEnabled(RadioSet<?> radioSet, boolean enabled) {
		radioSet.setEnabled(enabled);
	}

	public boolean updateUiVisible(Control control, boolean visible) {
		if (control.getVisible() != visible) {
			control.setVisible(visible);
			return true;
		} else {
			return false;
		}
	}

	public boolean updateUiVisible(Viewer viewer, boolean visible) {
		if (viewer.getControl().getVisible() != visible) {
			viewer.getControl().setVisible(visible);
			return true;
		} else {
			return false;
		}
	}

	public boolean updateUiVisible(TableColumn column, boolean visible) {
		if (visible && column.getWidth() == 0 && !column.getResizable()) {
			Integer lastWidth = (Integer) column.getData("lastWidth");
			Boolean wasResizable = (Boolean) column.getData("wasResizable");
			column.setWidth(lastWidth == null ? 50 : lastWidth);
			column.setResizable(wasResizable == null ? Boolean.TRUE : wasResizable);
			return true;
		} else if (!visible && column.getWidth() > 0) {
			column.setData("lastWidth", column.getWidth());
			column.setData("wasResizable", column.getResizable());
			column.setResizable(false);
			column.setWidth(0);
			return true;
		} else {
			return false;
		}
	}

	public boolean updateUiVisible(TableViewerColumn column, boolean visible) {
		return updateUiVisible(column.getColumn(), visible);
	}

	public void updateUiEditable(StyledText control, boolean editable) {
		if (control.getEditable() != editable) {
			control.setEditable(editable);
		}
	}

	public void updateUiEditable(Text control, boolean editable) {
		if (control.getEditable() != editable) {
			control.setEditable(editable);
		}
	}

	public void refreshViewer(Viewer viewer) {
		if (canRefresh(viewer.getControl())) {
			viewer.refresh();
		}
	}
	
	public void refreshOnSelectionChanged(final ISelectionProvider selectionProvider, final boolean fullRefresh, final Object... hints) {
		class SelectionListener implements ISelectionChangedListener, IReleaseable {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				markDirty(fullRefresh, hints);
			}
			@Override
			public void release() {
				selectionProvider.removeSelectionChangedListener(this);
			}
		}
		SelectionListener selectionListener = new SelectionListener();
		releasables.add(selectionListener);
		selectionProvider.addSelectionChangedListener(selectionListener);
	}

	public final void addExtension(IRefreshContextExt extension) {
		extensions.add(extension);
	}
	
	public final void removeExtension(IRefreshContextExt extension) {
		extensions.remove(extension);
	}
	
}
