package org.mariella.sample.app.binding;

import java.util.Arrays;
import java.util.List;

import org.mariella.rcp.databinding.ComboViewerLabelProviderExtension;
import org.mariella.rcp.databinding.PassingConverterBuilder;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.ValueSetExtension;

public class MaritalStatusDomainFactory extends SampleBindingDomainFactory {

public static final Character MARRIED_VALUE = 'm';
public static final Character SINGLE_VALUE = 's';
public static final Character WIDOW_VALUE = 'w';
public static final Character DIVORCEE_VALUE = 'd';

public static final Character[] AVAILABLE_VALUES = new Character[] {
	MARRIED_VALUE,
	SINGLE_VALUE,
	WIDOW_VALUE,
	DIVORCEE_VALUE
};

@Override
VBindingDomain createDomain() {
	return new VBindingDomain(
			DomainSymbols.MaritalStatus, 
			Character.class, new PassingConverterBuilder(),
			// we need one ValueSetExtension for the input of the ComboViewer:
			new ValueSetExtension() {
				public List getValueSet() {
					return Arrays.asList(AVAILABLE_VALUES);
				}
			},
			// and one extension responsible for providing the ILabelProvider to the ComboViewer:
			new ComboViewerLabelProviderExtension(
					"this", 		// "this" property refers to the Character value itself
					DomainSymbols.MaritalStatusAsText		// We reuse the MaritalStatusAsText domain for the label provider  
			));
}

}
