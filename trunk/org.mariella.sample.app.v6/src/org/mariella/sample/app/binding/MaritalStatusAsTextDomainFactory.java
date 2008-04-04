package org.mariella.sample.app.binding;

import org.mariella.rcp.databinding.CallbackConverterBuilder;
import org.mariella.rcp.databinding.ConversionCallback;
import org.mariella.rcp.databinding.VBindingDomain;

public class MaritalStatusAsTextDomainFactory extends SampleBindingDomainFactory {

@Override
VBindingDomain createDomain() {
	return new VBindingDomain(
			DomainSymbols.MaritalStatusAsText, 
			Character.class, 
			new CallbackConverterBuilder(new ConversionCallback() {
				public String getTextForObject(Object domainContext, Object entity) {
					if (MaritalStatusDomainFactory.MARRIED_VALUE.equals(entity)) return "married";
					if (MaritalStatusDomainFactory.SINGLE_VALUE.equals(entity)) return "single";
					if (MaritalStatusDomainFactory.WIDOW_VALUE.equals(entity)) return "widow";
					if (MaritalStatusDomainFactory.DIVORCEE_VALUE.equals(entity)) return "divorcee";
					return "";
				}
				public Object getObjectForText(Object domainContext, String text) {
					throw new UnsupportedOperationException();
				}
			}));
}

}
