package org.mariella.rcp.databinding;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Text;
import org.mariella.rcp.databinding.internal.AsyncActionWrapper;
import org.mariella.rcp.databinding.internal.BindingDomainExtensionDependendBinding;
import org.mariella.rcp.databinding.internal.ButtonContributionItem;
import org.mariella.rcp.databinding.internal.EnabledObservableValue;
import org.mariella.rcp.databinding.internal.EnabledObservableValueFactory;
import org.mariella.rcp.databinding.internal.EnabledStateModelObservableValue;
import org.mariella.rcp.databinding.internal.ModelObservables;
import org.mariella.rcp.databinding.internal.RcpObservables;
import org.mariella.rcp.databinding.internal.TableController;
import org.mariella.rcp.databinding.internal.VCompoundValidator;
import org.mariella.rcp.databinding.internal.VStatusLineManagerErrorMsgAdapter;
import org.mariella.rcp.databinding.internal.VTableViewerObservableList;
import org.mariella.rcp.databinding.internal.VTargetObservable;
import org.mariella.rcp.databinding.internal.VUpdateValueStrategy;
import org.mariella.rcp.databinding.internal.VisibleStateModelObservableValue;


public class VDataBindingFactory {

public interface Callback {
	void bindingCreated(VBinding binding);

	/**
	 * Implementors must copy the binding domain to extend it.
	 * Never modify the passed domain directly.
	 * 
	 * @param binding	implementors may read target observable if suitable for modification
	 * @param domain
	 * @return the modified copy of the domain or the original (when not modified)
	 */
	VBindingDomain extendBindingDomain(VBinding binding, VBindingDomain domain);
}
	
public static class DefaultBean {
public void addPropertyChangeListener(PropertyChangeListener l) {}
public void removePropertyChangeListener(PropertyChangeListener l) {}
}

private VBindingDomainRegistry domainRegistry;
private List<Callback> callbacks = new ArrayList<Callback>();

public VDataBindingFactory(VBindingDomainRegistry registry) {
	this.domainRegistry = registry;
}

public VDataBindingContext createDataBindingContext() {
	return new VDataBindingContext(this);
}

public void addContextExtension(VDataBindingContext dbc, DataBindingContextExtension extension) {
	extension.install(dbc);
}

public IObservableValue createPropertyObservable(VDataBindingContext dbc, Object bean, String propertyPath) {
	return ModelObservables.observeValue(bean, propertyPath, null);
}

public VBinding createSingleSelectionBinding(VDataBindingContext dbc, StructuredViewer structuredViewer, Object bean, String propertyPath, Object domainSymbol) {
	VBindingDomain domain = (VBindingDomain)domainRegistry.getDomain(domainSymbol);
	return createSingleSelectionBinding(dbc, structuredViewer, bean, propertyPath, domain);
}

public VBinding createSingleSelectionBinding(VDataBindingContext dbc, StructuredViewer structuredViewer, Object bean, String propertyPath, VBindingDomain domain) {
	VBinding binding = dbc.bindValue(RcpObservables.observeSingleSelection(dbc, structuredViewer), 
			ModelObservables.observeValue(bean, propertyPath, domain.getType()), 
			createTargetTextToModel(dbc, domain),  
			createModelToTargetText(dbc, domain),
			domain);
	
	completeBindingCreation(binding, domain);
	return binding;
}

public VBinding createComboViewerBinding(VDataBindingContext dbc, ComboViewer comboViewer, Object bean, String propertyPath, Object domainSymbol) {
	VBindingDomain domain = (VBindingDomain)domainRegistry.getDomain(domainSymbol);
	return createComboViewerBinding(dbc, comboViewer, bean, propertyPath, domain);
}

public VBinding createComboViewerBinding(VDataBindingContext dbc, ComboViewer comboViewer, Object bean, String propertyPath, VBindingDomain domain) {
	VBinding binding = dbc.bindValue(RcpObservables.observeComboViewer(dbc, comboViewer), 
			ModelObservables.observeValue(bean, propertyPath, domain.getType()), 
			createTargetTextToModel(dbc, domain),  
			createModelToTargetText(dbc, domain),
			domain);
	
	completeBindingCreation(binding, domain);
	return binding;
}

public VBinding createButtonBinding(VDataBindingContext dbc, Button button, Object bean, String propertyPath, Object domainSymbol) {
	VBindingDomain domain = (VBindingDomain)domainRegistry.getDomain(domainSymbol);
	return createButtonBinding(dbc, button, bean, propertyPath, domain);
}

public VBinding createButtonBinding(VDataBindingContext dbc, Button button, Object bean, String propertyPath, VBindingDomain domain) {
	ISWTObservableValue swtObservable = RcpObservables.observeButton(dbc, button);
	VBinding binding = dbc.bindValue(
			swtObservable,
			ModelObservables.observeValue(bean, propertyPath, domain.getType()), 
			new UpdateValueStrategy(),  
			new UpdateValueStrategy(),
			domain);
	
	completeBindingCreation(binding, domain);
	
	return binding;
}

public VBinding createControlVisibleBinding(VDataBindingContext dbc, Control control, Composite parentToRedraw, Object bean, String propertyPath) {
	return createControlVisibleBinding(dbc, control, parentToRedraw, bean, propertyPath, null);
}

public VBinding createControlVisibleBinding(VDataBindingContext dbc, Control control, Composite parentToRedraw, Object bean, String propertyPath, VisibleCallback callback) {
	ISWTObservableValue visibleObservable = RcpObservables.observeControlVisible(dbc, control, parentToRedraw);
	VBindingDomain domain = new VBindingDomain("visible", Boolean.class);
	final VisibleStateModelObservableValue stateModel = new VisibleStateModelObservableValue(callback, bean, propertyPath);
	VBinding binding = dbc.bindValue(
			visibleObservable,
			stateModel, 
			new UpdateValueStrategy(),  
			new UpdateValueStrategy(),
			domain);
	
	completeBindingCreation(binding, domain);
	
	return binding;
}

public VBinding[] createRadioSetBindings(VDataBindingContext dbc, Button[] buttons, Object[] values, Object bean, String propertyPath, Object domainSymbol) {
	VBindingDomain domain = (VBindingDomain)domainRegistry.getDomain(domainSymbol);
	return createRadioSetBindings(dbc, buttons, values, bean, propertyPath, domain);
}

public VBinding[] createRadioSetBindings(VDataBindingContext dbc, Button[] buttons, Object[] values, Object bean, String propertyPath, VBindingDomain domain) {
	VBinding[] bindings = new VBinding[buttons.length];
	// for each button/value we create a ValueMatchObservable which is suitable for a button binding (boolean)
	for (int i=0; i<buttons.length; i++) {
		Button button = buttons[i];
		Object value = values[i]; 
		ISWTObservableValue swtObservable = RcpObservables.observeRadioButton(dbc, button, value);
		
		VBinding binding = dbc.bindValue(
				swtObservable,
				ModelObservables.observeValue(bean, propertyPath, domain.getType()), 
				new UpdateValueStrategy(),  
				new UpdateValueStrategy(),
				domain);
		bindings[i] = binding;
		completeBindingCreation(binding, domain);
	}
	return bindings;
}

public VBinding createTextBinding(VDataBindingContext dbc, TextViewer textViewer, Object bean, String propertyPath, Object domainSymbol, TextDataBindingDetails details) {
	VBindingDomain domain = domainRegistry.getDomain(domainSymbol);
	return createTextBinding(dbc, textViewer, bean, propertyPath, domain, details);
}

public VBinding createTextBinding(VDataBindingContext dbc, TextViewer textViewer, Object bean, String propertyPath, VBindingDomain domain, TextDataBindingDetails details) {
	if (details == null)
		details = new TextDataBindingDetails();
	ISWTObservableValue swtObservable = RcpObservables.observeText(dbc, textViewer, details.eventType);
	VUpdateValueStrategy textToModel = createTargetTextToModel(dbc, domain);
	textToModel.swtObservable = swtObservable;
	details.statusDecorator.initializeFor((Control)swtObservable.getWidget());
	VBinding binding = dbc.bindValue(
			swtObservable,
			ModelObservables.observeValue(bean, propertyPath, domain.getType()), 
			textToModel,  
			createModelToTargetText(dbc, domain),
			domain);
	dbc.swtObservableStatusDecoratorMap.put(swtObservable, details.statusDecorator);
	
	completeBindingCreation(binding, domain);
	
	return binding;
}

public VBinding createTextBinding(VDataBindingContext dbc, Text text, Object bean, String propertyPath, Object domainSymbol, TextDataBindingDetails details) {
	VBindingDomain domain = domainRegistry.getDomain(domainSymbol);
	return createTextBinding(dbc, text, bean, propertyPath, domain, details);
}

public VBinding createTextBinding(VDataBindingContext dbc, Text text, Object bean, String propertyPath, VBindingDomain domain, TextDataBindingDetails details) {
	if (details == null)
		details = new TextDataBindingDetails();
	ISWTObservableValue swtObservable = RcpObservables.observeText(dbc, text, details.eventType);
	VUpdateValueStrategy textToModel = createTargetTextToModel(dbc, domain);
	textToModel.swtObservable = swtObservable;
	details.statusDecorator.initializeFor((Control)swtObservable.getWidget());
	VBinding binding = dbc.bindValue(
			swtObservable,
			ModelObservables.observeValue(bean, propertyPath, domain.getType()), 
			textToModel,  
			createModelToTargetText(dbc, domain),
			domain);
	dbc.swtObservableStatusDecoratorMap.put(swtObservable, details.statusDecorator);
	
	completeBindingCreation(binding, domain);
	
	return binding;
}

public VBinding createDateTimeBinding(VDataBindingContext dbc, DateTime dateTime, Object bean, String propertyPath, Object domainSymbol) {
	VBindingDomain domain = domainRegistry.getDomain(domainSymbol);
	return createDateTimeBinding(dbc, dateTime, bean, propertyPath, domain);
}

public VBinding createDateTimeBinding(VDataBindingContext dbc, DateTime dateTime, Object bean, String propertyPath, VBindingDomain domain) {
	ISWTObservableValue swtObservable = RcpObservables.observeDateTime(dbc, dateTime);
	VUpdateValueStrategy textToModel = createTargetTextToModel(dbc, domain);
	textToModel.swtObservable = swtObservable;
	VBinding binding = dbc.bindValue(
			swtObservable,
			ModelObservables.observeValue(bean, propertyPath, domain.getType()), 
			textToModel,  
			createModelToTargetText(dbc, domain),
			domain);
	
	completeBindingCreation(binding, domain);
	
	return binding;
}


public VBinding createTableViewerListBinding(VDataBindingContext dbc, TableViewer tableViewer, Object bean, String propertyPath, Object domainSymbol) {
	VBindingDomain domain = (VBindingDomain)domainRegistry.getDomain(domainSymbol);
	return createTableViewerListBinding(dbc, tableViewer, bean, propertyPath, domain);
}

public IObservableList createObservableList(VDataBindingContext dbc) {
	return ModelObservables.createObservableList(dbc);
}

public IObservableList createObservableList(VDataBindingContext dbc, List wrapped) {
	return ModelObservables.createObservableList(dbc, wrapped);
}

public IObservableValue observeSingleSelection(VDataBindingContext dbc, StructuredViewer viewer) {
	return RcpObservables.observeSingleSelection(dbc, viewer);
}

public IObservableValue observeSingleSelection(VDataBindingContext dbc, StructuredViewer viewer, Class targetType) {
	return RcpObservables.observeSingleSelection(dbc, viewer, targetType);
}

public VBinding createTableViewerListBinding(VDataBindingContext dbc, TableViewer tableViewer, Object bean, String propertyPath, VBindingDomain domain) {
	tableViewer.setContentProvider(new IStructuredContentProvider() {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		public void dispose() {}
		public Object[] getElements(Object inputElement) {
			if (inputElement == null) 
				return new Object[0];
			return ((Collection)inputElement).toArray();
		}
	});
	TableController tableController = TableController.createTableController(dbc, tableViewer);

	IObservableList targetObservableList = RcpObservables.observeTableViewer(dbc, tableViewer, domain.getType());
	((VTableViewerObservableList)targetObservableList).setTableController(tableController);
	tableController.targetObservable = (VTableViewerObservableList)targetObservableList;
	IObservableList modelObservableList = ModelObservables.observeList(dbc, bean, propertyPath, domain.getType());
	VBinding binding = dbc.bindList(
			targetObservableList, 
			modelObservableList, 
			new UpdateListStrategy(), 
			new UpdateListStrategy(), 
			domain);
	completeBindingCreation(binding, domain);

	return binding;
}

private void completeBindingCreation(VBinding binding, VBindingDomain domain) {
	for (Callback callback : callbacks) {
		domain = callback.extendBindingDomain(binding, domain);
	}
	
	if (domain.getExtensions() == null) return;
	// first install DomainContextExtension
	for (VBindingDomainExtension extension : domain.getExtensions())
		if (extension instanceof DomainContextExtension)
			extension.install(binding);
	
	for (VBindingDomainExtension extension : domain.getExtensions())
		if (!(extension instanceof DomainContextExtension))
			extension.install(binding);
	if (binding.getBinding() instanceof BindingDomainExtensionDependendBinding)
		((BindingDomainExtensionDependendBinding)binding.getBinding()).extensionsInstalled();
	
	for (Callback callback : callbacks) {
		callback.bindingCreated(binding);
	}
	
	if (binding.getBinding().getTarget() instanceof VTargetObservable)
		((VTargetObservable)binding.getBinding().getTarget()).extensionsInstalled();
}

public void createStatusBarErrorBinding(VDataBindingContext dbc, IStatusLineManager mgr) {
	dbc.bindValue(new VStatusLineManagerErrorMsgAdapter(mgr), 
			new AggregateValidationStatus(dbc.getDataBindingContext().getBindings(), AggregateValidationStatus.MERGED), 
			null, null,
			null);
}

private VUpdateValueStrategy createTargetTextToModel(VDataBindingContext dbc, VBindingDomain domain) {
	VUpdateValueStrategy strategy = new VUpdateValueStrategy(dbc);
	strategy.setConverter(domain.getConverterBuilder().buildToModelConverter(domain));
	IValidator beforeConvertToModel = domain.getConverterBuilder().buildBeforeSetModelValidator(domain);
	if (beforeConvertToModel != null)
		strategy.setBeforeSetValidator(beforeConvertToModel);
	if (domain.getAfterConvertValidators() != null)
		strategy.setAfterConvertValidator(createValidator(domain.getAfterConvertValidators()));
	return strategy;
}

/**
 * For model to text conversions, we do not use validators.
 * 
 * @param domainSymbol
 * @return
 */
private VUpdateValueStrategy createModelToTargetText(VDataBindingContext dbc, VBindingDomain domain) {
	VUpdateValueStrategy strategy = new VUpdateValueStrategy(dbc);
	strategy.setConverter(domain.getConverterBuilder().buildFromModelConverter(domain));
	return strategy;
}

private IValidator createValidator(IValidator[] validators) {
	if (validators.length == 1)
		return validators[0];
	return new VCompoundValidator(validators);
}

public VBindingDomainRegistry getDomainRegistry() {
	return domainRegistry;
}

VBinding createEnabledBinding(VDataBindingContext dbc, EnabledObservableValueFactory targetFactory, EnabledCallback enabledCallback) {
	return createEnabledBinding(dbc, targetFactory, new DefaultBean(), enabledCallback);
}

VBinding createEnabledBinding(VDataBindingContext dbc, EnabledObservableValueFactory targetFactory, Object bean, final EnabledCallback enabledCallback, String ...propertyPathes) {
	EnabledObservableValue target = targetFactory.createEnabledObservableValue();
	VBindingDomain domain = new VBindingDomain("enabled", Boolean.class);
	final EnabledStateModelObservableValue model = new EnabledStateModelObservableValue(enabledCallback, bean, propertyPathes);
	VBinding binding = dbc.bindValue(
			target,
			model, 
			new UpdateValueStrategy(),  
			new UpdateValueStrategy(),
			domain);
	
	enabledCallback.install(model);
	
	binding.addDisposeListener(new VBinding.DisposeListener() {
		public void disposed(VBinding binding) {
			enabledCallback.uninstall(model);
		}
	});
	
	completeBindingCreation(binding, domain);
	
	return binding;	
}

public VBinding createActionBinding(VDataBindingContext dbc, Button button, Action action, VBindingDomainExtension ...extensions) {
	new ButtonContributionItem(button, new AsyncActionWrapper(action));
	return createActionBinding(dbc, action, extensions);
}

@Deprecated
public VBinding createActionBinding(VDataBindingContext dbc, Action action, VBindingDomainExtension ...extensions) {
	IObservableValue actionObservable = RcpObservables.observeAction(dbc, action);
	VBindingDomain domain = new VBindingDomain("action", DefaultBean.class, extensions);
	VBinding binding = dbc.bindValue(
			actionObservable,
			ModelObservables.observeValue(new DefaultBean(), "this", domain.getType()), 
			new UpdateValueStrategy(),  
			new UpdateValueStrategy(),
			domain);
	
	completeBindingCreation(binding, domain);
	
	return binding;
}

public VBindingDomain copyExtend(Object domainSymbol, VBindingDomainExtension ... extensions) {
	VBindingDomain domain = new VBindingDomain(getDomainRegistry().getDomain(domainSymbol), extensions);
	return domain;
}

public VBindingDomain copyExtend(VBindingDomain domain, VBindingDomainExtension ... extensions) {
	VBindingDomain extended= new VBindingDomain(domain, extensions);
	return extended;
}

public void addCallback(Callback l) {
	callbacks.add(l);
}

public void removeCallback(Callback l) {
	callbacks.remove(l);
}

}
