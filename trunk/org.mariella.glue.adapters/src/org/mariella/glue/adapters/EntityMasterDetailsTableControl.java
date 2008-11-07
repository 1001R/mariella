package org.mariella.glue.adapters;

import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;
import org.mariella.glue.service.Entity;
import org.mariella.rcp.ControlFactory;
import org.mariella.rcp.adapters.WritableMasterDetailsTableControl;
import org.mariella.rcp.databinding.LongConverterBuilder;
import org.mariella.rcp.databinding.TableViewerColumnExtension;
import org.mariella.rcp.databinding.TableViewerEditExtension;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.VBindingDomainExtension;

public abstract class EntityMasterDetailsTableControl<A extends EntityMasterDetailsAdapter<E>, E extends Entity> extends WritableMasterDetailsTableControl<A, E> {

public EntityMasterDetailsTableControl(Composite parent, int style, A adapter, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style, adapter, controlFactory, bindingContext);
}

public EntityMasterDetailsTableControl(Composite parent, int style, IObservableValue adapterObservable, ControlFactory controlFactory, VBindingContext bindingContext) {
	super(parent, style, adapterObservable, controlFactory, bindingContext);
}

@Override
protected void addTableBindingDomainExtensions(List<VBindingDomainExtension> extensions) {
	extensions.add(new TableViewerEditExtension());
	extensions.add(new TableViewerColumnExtension("id", 
			createIdBindingDomain(),
			"Id",
			10));
}

protected VBindingDomain createIdBindingDomain() {
	return new VBindingDomain("id", 
			getEntityMasterDetailsAdapterContext().getEntityClass(),
			new LongConverterBuilder("#############0"));
}

@SuppressWarnings("unchecked")
protected EntityMasterDetailsAdapterContext< E> getEntityMasterDetailsAdapterContext() {
	return (EntityMasterDetailsAdapterContext<E>) getAdapter().getAdapterContext();
}

}
