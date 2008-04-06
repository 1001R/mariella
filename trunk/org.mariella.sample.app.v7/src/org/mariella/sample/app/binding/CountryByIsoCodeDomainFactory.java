package org.mariella.sample.app.binding;

import java.util.Iterator;

import org.mariella.rcp.databinding.CallbackConverterBuilder;
import org.mariella.rcp.databinding.ConversionCallback;
import org.mariella.rcp.databinding.VBindingDomain;
import org.mariella.rcp.databinding.contentassist.CallbackContentAssistProcessor;
import org.mariella.rcp.databinding.contentassist.ContentAssistantExtension;
import org.mariella.rcp.databinding.contentassist.VContentAssistProcessor;
import org.mariella.rcp.databinding.contentassist.CallbackContentAssistProcessor.ProposalsBuilder;
import org.mariella.sample.app.Activator;
import org.mariella.sample.core.Country;
import org.mariella.sample.core.SampleCorePlugin;

public class CountryByIsoCodeDomainFactory extends SampleBindingDomainFactory {

// to keep the domain flexible, delegate fetching of matching Countries to the context
public interface Context {
Iterator<Country> getAvailableCountries(String startingWithIsoCode);
Country getCountry(String isoCode);
}

// if no context is given, this default implementation is used
public static class DefaultContext implements Context {
@Override
public Iterator<Country> getAvailableCountries(String startingWithIsoCode) {	
	return SampleCorePlugin.getCoreService().getCountriesStartingWithIsoCode(startingWithIsoCode).iterator();
}
@Override
public Country getCountry(String isoCode) {
	return SampleCorePlugin.getCoreService().getCountry(isoCode);
}
}

@Override
VBindingDomain createDomain() {
	VBindingDomain domain = new VBindingDomain(
			DomainSymbols.CountryByIsoCode, 
			Country.class,
			new CallbackConverterBuilder(new ConversionCallback() {
				public String getTextForObject(Object domainContext, Object entity) {
					if (entity == null) return "";
					return ((Country)entity).getIsoCode();
				}
			
				public Object getObjectForText(Object domainContext, String text) {
					Context context = getContext((Context)domainContext);
					Country country = context.getCountry(text);
					return country;
				}
			}));
	
	domain.setExtensions(new ContentAssistantExtension() {
		public VContentAssistProcessor createContentAssistProcessor(final Object domainContext) {
			return new CallbackContentAssistProcessor(new CallbackContentAssistProcessor.Callback() {
				@Override
				public void addEntries(ProposalsBuilder builder, String word) {
					Context context = getContext((Context)domainContext);
					Iterator<Country> countries = context.getAvailableCountries(word);
					while (countries.hasNext()) {
						Country country = countries.next();
						builder.setWord(country == null ? "not given" : country.getIsoCode());
						builder.setImage(country == null ? null : Activator.getImage("icons/country.png"));
						builder.addProposal();
					}
				}
			});
		}
	});
	addDefaultTextViewerExtensions(domain);
	return domain;
}

Context getContext(Context explicitContext) {
	if (explicitContext != null) return explicitContext;
	return new DefaultContext();
}

}
