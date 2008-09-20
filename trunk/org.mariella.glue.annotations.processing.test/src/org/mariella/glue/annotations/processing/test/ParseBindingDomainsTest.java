package org.mariella.glue.annotations.processing.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mariella.glue.annotations.processing.AnnotationsProcessingPlugin;
import org.mariella.glue.annotations.processing.BindingDomainInfo;

public class ParseBindingDomainsTest {

@Test
public void testIt() {
	List<BindingDomainInfo> domainInfos = AnnotationsProcessingPlugin.getDefault().fetchDomainInfos(Activator.getDefault().getBundle());
	Assert.assertEquals(2, domainInfos.size());
	Assert.assertTrue(hasBindingDomain(domainInfos, Person.class, "name", "PersonName"));
	Assert.assertTrue(hasBindingDomain(domainInfos, Person.class, "age", "PersonAge"));
}

private boolean hasBindingDomain(List<BindingDomainInfo> domainInfos, Class<Person> clazz, String attrName, String domainName) {
	for (BindingDomainInfo info : domainInfos) {
		if (info.getDeclaringClass().getName().equals(clazz.getName()) && info.getAttributeName().equals(attrName) && info.getBindingDomain().name().equals(domainName)) {
			return true;
		}
	}
	return false;
}
	
}
