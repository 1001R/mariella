package org.mariella.cat.ui.controls.radioset;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class RadioSet<T> {

	public static class RadioElement<T> {
		private Button button;
		private T value;

		private RadioElement(Button button, T value) {
			this.button = button;
			this.value = value;
		}

		public Button getButton() {
			return button;
		}

		public T getValue() {
			return value;
		}
	}

	public static interface IRadioSetListener<T> {
		void elementSelected(RadioElement<T> radioElement);
	}

	private List<RadioElement<T>> radioElements = new ArrayList<RadioElement<T>>(5);
	private List<IRadioSetListener<T>> listeners = new ArrayList<IRadioSetListener<T>>(3);
	private boolean selecting = false;

	private Listener buttonListener = new Listener() {
		public void handleEvent(Event event) {
			if (event.type == SWT.Selection) {
				select((Button) event.widget);
			} else if (event.type == SWT.Traverse) {
				if (event.detail == SWT.TRAVERSE_ARROW_NEXT) {
					for (int i = 0; i < radioElements.size()-1; i++) {
						if (event.widget == radioElements.get(i).button) {
							radioElements.get(i+1).button.forceFocus();
						}
					}
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = true;
				} else if (event.detail == SWT.TRAVERSE_ARROW_PREVIOUS) {
					for (int i = 1; i < radioElements.size(); i++) {
						if (event.widget == radioElements.get(i).button) {
							radioElements.get(i-1).button.forceFocus();
						}
					}
					event.detail = SWT.TRAVERSE_NONE;
					event.doit = true;
				}
			}
		}
	};

	public RadioSet() {
	}

	public RadioSet(Button button, T value) {
		add(button, value);
	}

	public RadioSet<T> add(Button button, T value) {
		radioElements.add(new RadioElement<T>(button, value));
		button.addListener(SWT.Selection, buttonListener);
		button.addListener(SWT.Traverse, buttonListener);
		return this;
	}

	public void addRadioSetListener(IRadioSetListener<T> listener) {
		listeners.add(listener);
	}

	public void removeRadioSetListener(IRadioSetListener<T> listener) {
		listeners.remove(listener);
	}

	public RadioElement<T> getRadioElement(T value) {
		for (RadioElement<T> radioElement : radioElements) {
			if (radioElement.value == value || (radioElement.value != null && radioElement.value.equals(value))) {
				return radioElement;
			}
		}
		return null;
	}

	public RadioElement<T> getRadioElement(Button button) {
		for (RadioElement<T> radioElement : radioElements) {
			if (radioElement.button.equals(button)) {
				return radioElement;
			}
		}
		return null;
	}

	public void select(T value) {
		if (!selecting) {
			selecting = true;
			try {
				RadioElement<T> selectedElement = null;
				for (RadioElement<T> radioElement : radioElements) {
					if (radioElement.value == value || (radioElement.value != null && radioElement.value.equals(value))) {
						radioElement.button.setSelection(true);
						selectedElement = radioElement;
					} else {
						radioElement.button.setSelection(false);
					}
				}
				adjustTabOrder(selectedElement);
				fireSelection(selectedElement);
			} finally {
				selecting = false;
			}
		}
	}

	public void select(Button button) {
		if (!selecting) {
			selecting = true;
			try {
				RadioElement<T> selectedElement = null;
				for (RadioElement<T> radioElement : radioElements) {
					if (radioElement.button == button) {
						radioElement.button.setSelection(true);
						selectedElement = radioElement;
					} else {
						radioElement.button.setSelection(false);
					}
				}
				adjustTabOrder(selectedElement);
				fireSelection(selectedElement);
			} finally {
				selecting = false;
			}
		}
	}

	private void adjustTabOrder(RadioElement<T> selectedElement) {
		Control[] tabList = selectedElement.button.getParent().getTabList();
		List<Control> newTabList = new ArrayList<Control>(tabList.length);
		boolean adjusted = false;
		boolean changed = false;
		for (Control c : tabList) {
			if (c instanceof Button && getRadioElement((Button) c) != null) {
				if (!adjusted) {
					changed = selectedElement.button != c;
					newTabList.add(selectedElement.button);
					adjusted = true;
				} else {
					changed = true;
				}
			} else {
				newTabList.add(c);
			}
		}
		if (changed) {
			selectedElement.button.getParent().setTabList(newTabList.toArray(new Control[newTabList.size()]));
		}
	}
	
	private void fireSelection(RadioElement<T> selectedElement) {
		for (IRadioSetListener<T> listener : listeners) {
			listener.elementSelected(selectedElement);
		}
	}

	public boolean isEnabled() {
		for (RadioElement<T> radioElement : radioElements) {
			if (radioElement.button.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public void setEnabled(boolean enabled) {
		for (RadioElement<T> radioElement : radioElements) {
			if (radioElement.button.getEnabled() != enabled) {
				radioElement.button.setEnabled(enabled);
			}
		}
	}

}
