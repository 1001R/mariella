package org.mariella.cat.ui.editcontext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.mariella.cat.core.validation.Messages;
import org.mariella.cat.ui.controls.radioset.RadioSet;
import org.mariella.cat.ui.controls.radioset.RadioSet.RadioElement;


public abstract class EditContext extends RefreshContext {

	private static class AttachedObject {
		private Object object;
		private int numberOfAttaches;
		private AttachedObject(Object object) {
			this.object = object;
			numberOfAttaches = 1;
		}
	}
	
	private Messages messages;
	
	private List<AttachedObject> attachedTo = new ArrayList<AttachedObject>(5);
	private Control control;
	
	private List<Object> editingControls = new ArrayList<Object>(3);
	private List<IEditable> editables = new ArrayList<IEditable>(5);

	private DisposeListener disposeListener = new DisposeListener() {
		@Override
		public void widgetDisposed(DisposeEvent e) {
			release();
		}
	};
	
	private PropertyChangeListener changeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			handleModelChange(ChangeOp.PROPERTY, evt.getSource(), evt.getPropertyName(), evt.getNewValue());
		}
	};
	
	public EditContext() {
		this(null);
	}

	public EditContext(Messages messages) {
		this.messages = (messages == null ? new Messages() : messages);
		setDisplay(Display.getCurrent());
	}
	
	public void release() {
		super.release();
		while (!attachedTo.isEmpty()) {
			AttachedObject ao = attachedTo.remove(attachedTo.size()-1);
			hookDetachFrom(ao.object);
		}
	}

	public void attachToUi(Control control) {
		if (this.control != null) {
			this.control.removeDisposeListener(disposeListener);
		}
		this.control = control;
		if (control != null) {
			control.addDisposeListener(disposeListener);
			setDisplay(control.getDisplay());
		} else {
			setDisplay(null);
		}
	}

	public Messages getMessages() {
		return messages;
	}

	private AttachedObject findAttachedObject(Object object) {
		for (AttachedObject ao : attachedTo) {
			if (ao.object.equals(object)) {
				return ao;
			}
		}
		return null;
	}
	
	public void attachTo(List<?> observables) {
		for (Object observable : observables) {
			attachTo(observable);
		}
	}

	public void detachFrom(List<?> observables) {
		for (Object observable : observables) {
			detachFrom(observable);
		}
	}
	
	public final void attachTo(Object observableElement) {
		AttachedObject ao = findAttachedObject(observableElement);
		if (ao != null) {
			ao.numberOfAttaches++;
		} else {
			if (hookAttachTo(observableElement)) {
				attachedTo.add(new AttachedObject(observableElement));
			}
		}
	}
	
	protected boolean hookAttachTo(Object observableElement) {
		return addPropertyChangeListener(observableElement, changeListener);
	}

	public final void detachFrom(Object observableElement) {
		AttachedObject ao = findAttachedObject(observableElement);
		if (ao != null) {
			ao.numberOfAttaches--;
			if (ao.numberOfAttaches <= 0) {
				hookDetachFrom(observableElement);
				attachedTo.remove(ao);
			}
		}
	}
	
	protected boolean hookDetachFrom(Object observableElement) {
		return removePropertyChangeListener(observableElement, changeListener);
	}

	private boolean addPropertyChangeListener(Object object, PropertyChangeListener listener) {
		try {
			Method m = object.getClass().getMethod("addPropertyChangeListener", new Class<?>[] { PropertyChangeListener.class });
			if (m != null) {
				m.invoke(object, new Object[] { listener });
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}

	private boolean removePropertyChangeListener(Object object, PropertyChangeListener listener) {
		try {
			Method m = object.getClass().getMethod("removePropertyChangeListener", new Class<?>[] { PropertyChangeListener.class });
			if (m != null) {
				m.invoke(object, new Object[] { listener });
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}
	
	
	public void dispose() {
		for (IEditable editable : editables) {
			editable.dispose();
		}
	}

	@Override
	public boolean canRefresh(Object control) {
		return !editingControls.contains(control);
	}

	public final void validate() {
		for (IEditable editable : editables) {
			editable.validate();
		}
		doValidation();
	}

	protected void doValidation() {
	}

	public final void addEditControl(Object editControl) {
		editingControls.add(editControl);
	}

	public final Object removeEditControl(Object editControl) {
		return editingControls.remove(editControl);
	}
	
	public final void addEditable(IEditable editable) {
		editables.add(editable);
	}
	
	public void install(final StyledText styledText, final IEditSupport<String> editSupport) {
		Listener listener = new Listener() {
			private String uiValueWhenStartEditing = null;
			private boolean editingStarted = false;
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Modify) {
					if (!isRefreshing()) {
						if (!editingStarted) {
							editSupport.startEditing(EditContext.this, styledText, messages);
							if (styledText.isFocusControl()) {
								addEditControl(styledText);
								editingStarted = true;
							}
						}
						editSupport.uiValueChanged(EditContext.this, styledText.getText(), styledText, messages);
					}
				} else if (event.type == SWT.FocusIn) {
					uiValueWhenStartEditing = styledText.getText();
					editingStarted = false;
				} else if (event.type == SWT.FocusOut) {
					if (editingStarted) {
						removeEditControl(styledText);
						editSupport.endEditing(EditContext.this, styledText, messages);
						editingStarted = false;
					}
				} else if (event.type == SWT.Traverse && event.character == SWT.ESC) {
					if (editSupport.cancelEditing(EditContext.this, styledText, messages)) {
						styledText.setText(uiValueWhenStartEditing);
						event.detail = SWT.TRAVERSE_NONE;
						event.doit = true;
					}
				}
			}
		};
		addEditable(new EditableControl(this, editSupport, styledText, listener, SWT.Modify, SWT.FocusIn, SWT.FocusOut, SWT.Traverse));
	}

	public void install(final Text text, final IEditSupport<String> editSupport) {
		Listener listener = new Listener() {
			private String uiValueWhenStartEditing = null;
			private boolean editingStarted = false;
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Modify) {
					if (!isRefreshing()) {
						if (!editingStarted) {
							editSupport.startEditing(EditContext.this, text, messages);
							if (text.isFocusControl()) {
								addEditControl(text);
								editingStarted = true;
							}
						}
						editSupport.uiValueChanged(EditContext.this, text.getText(), text, messages);
					}
				} else if (event.type == SWT.FocusIn) {
					uiValueWhenStartEditing = text.getText();
					editingStarted = false;
				} else if (event.type == SWT.FocusOut) {
					if (editingStarted) {
						removeEditControl(text);
						editSupport.endEditing(EditContext.this, text, messages);
						editingStarted = false;
					}
				} else if (event.type == SWT.Traverse && event.character == SWT.ESC) {
					if (editSupport.cancelEditing(EditContext.this, text, messages)) {
						text.setText(uiValueWhenStartEditing);
						event.detail = SWT.TRAVERSE_NONE;
						event.doit = true;
					}
				}
			}
		};
		addEditable(new EditableControl(this, editSupport, text, listener, SWT.Modify, SWT.FocusIn, SWT.FocusOut, SWT.Traverse));
	}

	public <T> void install(final ComboViewer viewer, final IEditSupport<T> editSupport) {
		class ComboListener implements Listener, ISelectionChangedListener {
			private boolean editingStarted = false;
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!isRefreshing()) {
					addEditControl(viewer.getControl());
					try {
						if (!editingStarted) {
							editSupport.startEditing(EditContext.this, viewer.getControl(), messages);
							editingStarted = true;
						}
						editSupport.uiValueChanged(EditContext.this, (T) ((IStructuredSelection) event.getSelection()).getFirstElement(), viewer.getControl(), messages);
					} finally {
						removeEditControl(viewer.getControl());
					}
				}
			}

			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.FocusOut) {
					if (editingStarted) {
						editSupport.endEditing(EditContext.this, viewer.getControl(), messages);
						editingStarted = false;
					}
				}
			}
		}
		ComboListener listener = new ComboListener();
		addEditable(new EditableViewer(this, editSupport, viewer, listener, listener, SWT.Selection, SWT.FocusOut));
	}

	public void install(final Button checkboxButton, final IEditSupport<Boolean> editSupport) {
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.Selection && !isRefreshing()) {
					editSupport.startEditing(EditContext.this, checkboxButton, messages);
					editSupport.uiValueChanged(EditContext.this, checkboxButton.getSelection(), checkboxButton, messages);
					editSupport.endEditing(EditContext.this, checkboxButton, messages);
				}
			}
		};
		addEditable(new EditableControl(this, editSupport, checkboxButton, listener, SWT.Selection));
	}
	
	public <T> void install(final RadioSet<T> radioSet,final IEditSupport<T> editSupport) {
		RadioSet.IRadioSetListener<T> listener = new RadioSet.IRadioSetListener<T>() {
			@Override
			public void elementSelected(RadioElement<T> radioElement) {
				if (!isRefreshing()) {
					editSupport.startEditing(EditContext.this, radioSet, getMessages());
					editSupport.uiValueChanged(EditContext.this, radioElement.getValue(), radioSet, getMessages());
					editSupport.endEditing(EditContext.this, radioSet, getMessages());
				}
			}
		};
		addEditable(new EditableRadioSet<T>(this, editSupport, radioSet, listener));
	}

}
