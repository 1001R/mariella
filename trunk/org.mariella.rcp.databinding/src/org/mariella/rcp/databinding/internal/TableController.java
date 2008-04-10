package org.mariella.rcp.databinding.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.mariella.rcp.databinding.SelectionManagementExtension;
import org.mariella.rcp.databinding.TableViewerColumnEditExtension;
import org.mariella.rcp.databinding.TableViewerColumnExtension;
import org.mariella.rcp.databinding.TableViewerColumnFontExtension;
import org.mariella.rcp.databinding.TableViewerColumnImageExtension;
import org.mariella.rcp.databinding.TableViewerColumnLabelDecoratorExtension;
import org.mariella.rcp.databinding.TableViewerColumnToolTipExtension;
import org.mariella.rcp.databinding.TableViewerEditExtension;
import org.mariella.rcp.databinding.TableViewerElementChangeListenerExtension;
import org.mariella.rcp.databinding.VBinding;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingFactory;
import org.mariella.rcp.databinding.VBindingSelection;

public class TableController implements ITableLabelProvider, ITableFontProvider {

private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
private IObservableValue selectionHolder;
private TableControllerCursor tableCursor;
private ControlEditor cursorEditor;
private TableViewer tableViewer;
public VTableViewerObservableList targetObservable;
private TableLayout tableLayout;
private List<TableViewerColumnExtension> columnExtensions = new ArrayList<TableViewerColumnExtension>();
private Map<String,TableViewerColumnImageExtension> imageExtensionMap = new HashMap<String, TableViewerColumnImageExtension>();
private Map<String,TableViewerColumnFontExtension> fontExtensionMap = new HashMap<String, TableViewerColumnFontExtension>();
private Map<String,TableViewerColumnToolTipExtension> toolTipExtensionMap = new HashMap<String, TableViewerColumnToolTipExtension>();
private Map<String,TableViewerColumnEditExtension> editExtensionMap = new HashMap<String, TableViewerColumnEditExtension>();
private Map<String, TableViewerColumnLabelDecoratorExtension> labelDecoratorExtensionMap = new HashMap<String, TableViewerColumnLabelDecoratorExtension>();
private Map<String,Composite> editCompositeMap = new HashMap<String,Composite>();
private Map<String,Control> editControlMap = new HashMap<String,Control>();
private VBindingContext bindingContext;
private boolean editable = true;
private boolean hookElementChangeListeners = false;
private List<Runnable> onExtensionInstalledCommands = new ArrayList<Runnable>();

public static TableController createTableController(VBindingContext dbc, TableViewer tableViewer) {
	TableController controller = new TableController();
	controller.bindingContext = dbc;
	dbc.tableControllerMap.put(tableViewer, controller);
	controller.setTableViewer(tableViewer);
	return controller;
}

private void setTableViewer(TableViewer tableViewer) {
	this.tableViewer = tableViewer;
	tableLayout = new TableLayout();
	tableViewer.getTable().setLayout(tableLayout);
	tableViewer.getTable().setHeaderVisible(true);
	tableViewer.setLabelProvider(this);
}

public void install(TableViewerColumnExtension columnExtension, VBinding binding) {
	if (columnExtension.getDomain() == null)
		columnExtension.setDomain(binding.getBindingContext().getDataBindingFactory().getDomainRegistry().getDomain(columnExtension.getDomainSymbol()));
	columnExtensions.add(columnExtension);
	tableLayout.addColumnData(new ColumnWeightData(columnExtension.getWeight()));
	TableColumn tableCol = new TableColumn(tableViewer.getTable(), SWT.ITALIC);
	tableCol.setText(columnExtension.getHeaderText());
}

public void install(TableViewerEditExtension tableViewerEditExtension) {
	if ((tableViewer.getTable().getStyle() & SWT.SINGLE) == 0)
		throw new IllegalStateException("Table must have the SWT.SINGLE style");
	if ((tableViewer.getTable().getStyle() & SWT.MULTI) != 0)
		throw new IllegalStateException("Table must not have the SWT.MULTI style");
	if ((tableViewer.getTable().getStyle() & SWT.FULL_SELECTION) == 0)
		throw new IllegalStateException("Table must have the SWT.FULL_SELECTION style");
	
	selectionHolder = RcpObservables.observeSingleSelection(bindingContext, tableViewer);
	tableCursor = new TableControllerCursor(bindingContext, this, tableViewer, SWT.NONE);
	cursorEditor = new ControlEditor(tableCursor);
	cursorEditor.grabHorizontal=true;
	cursorEditor.grabVertical=true;
	
	tableCursor.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (!editable) return;
			for (Control c : editCompositeMap.values()) c.setVisible(false);

			TableItem row = tableCursor.getRow();
			int rowIndex = tableViewer.getTable().indexOf(row);
			tableViewer.getTable().select(rowIndex);
			int colIndex = tableCursor.getColumn();
			String propertyPath = columnExtensions.get(colIndex).getPropertyPath();
			final Control editControl = editCompositeMap.get(propertyPath);
			if (editControl == null) return;
			
			editControl.setVisible(true);
			cursorEditor.setEditor(editControl);
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					editControl.setFocus();
				}
			});
			
		}
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	});
	
	final Color selectionColor1 = new Color(Display.getCurrent(), 255, 255, 80);
	final Color selectionColor2 = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	
	final Table table = tableViewer.getTable();
	table.addListener(SWT.EraseItem, new Listener() {
		public void handleEvent(Event event) {
			if ((event.detail & SWT.SELECTED) == 0) return; // item not selected
			int clientWidth = table.getClientArea().width;
			GC gc = event.gc;
			Color oldBackground = gc.getBackground();
			gc.setForeground(selectionColor1);
			gc.setBackground(selectionColor2);
			gc.fillGradientRectangle(0, event.y, clientWidth, event.height, false);
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(oldBackground);
			event.detail &= ~SWT.SELECTED;		
		}
	});
}

/**
 * Returns true if PropertyChangeListeners has to be installed
 * for each element of the table.
 * 
 *  For performance reasons, non-editable tables do not provide this feature.
 * 
 * @return
 */
boolean hookElementChangeListeners() {
	return hookElementChangeListeners || cursorEditor != null;	// either if explicitly said with TableViewerElementChangeListenerExtension or if edit extension installed
}

public boolean isEditable(int columnIndex) {
	String propertyPath = columnExtensions.get(columnIndex).getPropertyPath();
	return editCompositeMap.get(propertyPath) != null;
}

public boolean blockDefaultTraversing(int columnIndex) {
	String propertyPath = columnExtensions.get(columnIndex).getPropertyPath();
	Control editControl = editControlMap.get(propertyPath);
	List<VTargetObservable> observables = bindingContext.getObservablesFor(editControl);
	for (VTargetObservable observable : observables)
		if (observable.blockDefaultTraversing())
			return true;
	return false;
}

public void install(TableViewerColumnEditExtension columnEditExtension) {
	editExtensionMap.put(columnEditExtension.getPropertyPath(), columnEditExtension);
	final Composite editControlComposite = new Composite(tableCursor, SWT.NONE);
	editControlComposite.setLayout(new FormLayout());
	// add callback to associate all created SelectionAwareObservables with a proper GetContextSelectionCallback
	VBindingFactory.Callback factoryCallback = new VBindingFactory.Callback() {
		public void bindingCreated(final VBinding binding) {
			onExtensionInstalledCommands.add(new Runnable() {
				public void run() {
					SelectionManagementExtension ext = binding.getDomain().getExtension(SelectionManagementExtension.class);
					if (ext != null) {
						((SelectionAwareObservable)binding.getBinding().getTarget()).setContextSelectionCallback(new GetContextSelectionCallback() {
							public VBindingSelection getContextSelection() {
								return targetObservable.getSelection();
							}
						});
					}
				}
			});
		}
		public VBindingDomain extendBindingDomain(VBinding binding, VBindingDomain domain) {
			return domain;
		}
	};
	bindingContext.getDataBindingFactory().addCallback(factoryCallback);
	final Control editControl = columnEditExtension.getCallback().createEditControl(selectionHolder, editControlComposite);
	bindingContext.getDataBindingFactory().removeCallback(factoryCallback);
	FormData formData = new FormData();
	formData.top= new FormAttachment(0);
	formData.left = new FormAttachment(0);
	formData.bottom = new FormAttachment(100);
	formData.right = new FormAttachment(100);
	editControl.setLayoutData(formData);
	editCompositeMap.put(columnEditExtension.getPropertyPath(), editControlComposite);
	editControlMap.put(columnEditExtension.getPropertyPath(), editControl);
	editControlComposite.setVisible(false);
	tableCursor.attachTraverseListener(editControl);
	editControl.addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent e) {
		}
		public void focusLost(FocusEvent e) {
			tableViewer.refresh(selectionHolder.getValue());
			tableViewer.getTable().redraw();
			
			Runnable focusLostBlock = new Runnable() {
				public void run() {
					if (!editControl.isDisposed() && isTableFocusOut(editControl)) {
						// if focus was lost to something out of the table, we hide the edit control
						editControlComposite.setVisible(false);
						editControlComposite.setSize(0,0);
						tableCursor.setRowColumn(null, null, false);
					}
				}

			}; 
			
			Display.getCurrent().asyncExec(focusLostBlock);
		}
	});
}

boolean isTableFocusOut(Control focusLostTrigger) {
	Control newFocusControl = Display.getCurrent().getFocusControl();
	return newFocusControl != null && !newFocusControl.isDisposed() && 
			!isTableChild(tableViewer.getTable(), newFocusControl) && newFocusControl.getShell() == focusLostTrigger.getShell();

}

private boolean isTableChild(Table table, Control focusControl) {
	if (focusControl == null) return false;
	Control parent = focusControl;
	while (parent != null) {
		if (parent == table) return true;
		parent = parent.getParent();
	}
	return false;
}

public void install(TableViewerColumnImageExtension imageExtension) {
	imageExtensionMap.put(imageExtension.getPropertyPath(), imageExtension);
}

public void install(TableViewerColumnFontExtension fontExtension) {
	fontExtensionMap.put(fontExtension.getPropertyPath(), fontExtension);
}

public void install(TableViewerColumnLabelDecoratorExtension decoratorExtension) {
	labelDecoratorExtensionMap .put(decoratorExtension.getPropertyPath(), decoratorExtension);
}


public Image getColumnImage(Object element, int columnIndex) {
	TableViewerColumnExtension ext = columnExtensions.get(columnIndex);
	Object value = readValue(element, ext);
	TableViewerColumnImageExtension imgExt = imageExtensionMap.get(ext.getPropertyPath());
	if (imgExt != null)
		return getImage(imgExt.getImageCallback().getImageDescriptor(element, value));
	return null;
}

public Font getFont(Object element, int columnIndex) {
	TableViewerColumnExtension ext = columnExtensions.get(columnIndex);
	Object value = readValue(element, ext);
	TableViewerColumnFontExtension fontExt = fontExtensionMap.get(ext.getPropertyPath());
	if (fontExt != null)
		return fontExt.getFontCallback().getFont(element, value);
	return null;
}

public String getColumnToolTip(Object element, int columnIndex) {
	TableViewerColumnExtension ext = columnExtensions.get(columnIndex);
	Object value = readValue(element, ext);
	TableViewerColumnToolTipExtension toolTipExt = toolTipExtensionMap.get(ext.getPropertyPath());
	if (toolTipExt != null)
		return toolTipExt.getToolTipCallback().getToolTipText(element, value);
	return null;
}

Object getColumnValue(Object element, int columnIndex) {
	TableViewerColumnExtension ext = columnExtensions.get(columnIndex);
	Object value = readValue(element, ext);
	return value;
}

private Image getImage(ImageDescriptor imageDescriptor) {
	if (imageDescriptor == null) return null;
	return (Image) resourceManager.get(imageDescriptor);
}

// TODO optimize
public String getColumnText(Object element, int columnIndex) {
	TableViewerColumnExtension ext = columnExtensions.get(columnIndex);
	Object value = readValue(element, ext);
	Object converted = ext.getDomain().getConverterBuilder().buildFromModelConverter(ext.getDomain()).convert(value);
	String string =  (converted == null ? "" : converted.toString());
	TableViewerColumnLabelDecoratorExtension decoratorExt = labelDecoratorExtensionMap.get(ext.getPropertyPath());
	if (decoratorExt == null) return string;
	return decoratorExt.getLabelDecoratorCallback().decorateLabel(element, string);
}


private Object readValue(Object element, TableViewerColumnExtension ext) {
	PropertyPathSupport propertyPathSupport = new PropertyPathSupport();
	propertyPathSupport.object = element;
	propertyPathSupport.propertyPath = ext.getPropertyPath();
	propertyPathSupport.initialize();
	return propertyPathSupport.implementDoGetValue();
}

public void dispose() {}

public boolean isLabelProperty(Object element, String property) {
	return false;
}

public void addListener(ILabelProviderListener listener) {}

public void removeListener(ILabelProviderListener listener) {}

public void dispatchSelection(int rowIndex, VDataBindingSelectionDispatchContext ctx) {
	String propertyPath = (String)ctx.nextPathToken();
	if (propertyPath == null) return;
	
	int colIndex = getColumnIndex(propertyPath);
	tableCursor.setRowColumn(rowIndex, colIndex, true);
}

private int getColumnIndex(String propertyPath) {
	int index = 0;
	for (TableViewerColumnExtension ext : columnExtensions) {
		if (ext.getPropertyPath().equals(propertyPath))
			return index;
		index++;
	}
	return -1;
}

public void install(TableViewerColumnToolTipExtension extension) {
	toolTipExtensionMap.put(extension.getPropertyPath(), extension);
	new TableViewerToolTipHandler(this, tableViewer);
	
}

public void setEditable(boolean editable) {
	for (Control control : editCompositeMap.values()) {
		if (!editable) {
			control.setVisible(false);
			control.setSize(0,0);
			cursorEditor.setEditor(null);
		}
		control.setEnabled(editable);
	}
	tableCursor.setVisible(editable);
	tableCursor.redraw();
	this.editable = editable;
}

public Collection<String> getPropertyPathes() {
	Set<String> propertyPathes = new HashSet<String>(columnExtensions.size());
	for (TableViewerColumnExtension ext : columnExtensions)
		propertyPathes.add(ext.getPropertyPath());
	return propertyPathes;
}

public void install(TableViewerElementChangeListenerExtension elementChangeListenerExtension) {
	hookElementChangeListeners = true;
}

public void extensionsInstalled() {
	for (Runnable r : onExtensionInstalledCommands)
		r.run();
}



}
