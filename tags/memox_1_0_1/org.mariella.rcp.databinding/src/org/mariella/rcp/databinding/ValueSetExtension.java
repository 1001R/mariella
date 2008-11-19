package org.mariella.rcp.databinding;

import java.util.List;

import org.mariella.rcp.databinding.internal.ValueSetAwareObservable;

public abstract class ValueSetExtension implements VBindingDomainExtension {

public void install(VBinding binding) {
	((ValueSetAwareObservable)binding.getBinding().getTarget()).installValueSetExtension(this);
}

public abstract List getValueSet();

}
