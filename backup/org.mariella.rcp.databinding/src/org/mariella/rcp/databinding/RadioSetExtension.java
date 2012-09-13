package org.mariella.rcp.databinding;

public class RadioSetExtension implements VBindingDomainExtension {

Object[][] valuesAndLabels;

public RadioSetExtension(Object[] ... valuesAndLabels) {
	this.valuesAndLabels = valuesAndLabels;
}

public void install(VBinding binding) {}

}
