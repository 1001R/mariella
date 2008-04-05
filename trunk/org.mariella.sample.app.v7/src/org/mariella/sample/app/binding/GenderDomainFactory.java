package org.mariella.sample.app.binding;

import org.mariella.rcp.databinding.PassingConverterBuilder;
import org.mariella.rcp.databinding.RadioSetExtension;
import org.mariella.rcp.databinding.VBindingDomain;

public class GenderDomainFactory extends SampleBindingDomainFactory {

public static final Character FEMALE_VALUE = 'f';
public static final Character MALE_VALUE = 'm';

public static final String FEMALE_LABEL = "female";
public static final String MALE_LABEL = "male";

public static final Character[] AVAILABLE_VALUES = new Character[] {
	FEMALE_VALUE,
	MALE_VALUE,
};

VBindingDomain createDomain() {
	VBindingDomain domain = new VBindingDomain(
			DomainSymbols.Gender, 
			Character.class,
			new PassingConverterBuilder());

	domain.setExtensions(new RadioSetExtension(
			new Object[] {FEMALE_VALUE, FEMALE_LABEL},
			new Object[] {MALE_VALUE, MALE_LABEL}
			));
	
	return domain;
}


}
