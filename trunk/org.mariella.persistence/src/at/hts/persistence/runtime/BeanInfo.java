package at.hts.persistence.runtime;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class BeanInfo {
	private Map<String, PropertyDescriptor> propertyDescriptors = null;
	
public BeanInfo(Class<?> cls) {
	super();
	propertyDescriptors = new HashMap<String, PropertyDescriptor>();
	try {
		PropertyDescriptor pds[] =  java.beans.Introspector.getBeanInfo(cls).getPropertyDescriptors();
		for(PropertyDescriptor pd : pds) {
			propertyDescriptors.put(pd.getName(), pd);
		}
	} catch(IntrospectionException e) {
		throw new RuntimeException(e);
	}
}

public PropertyDescriptor getPropertyDescriptor(String propertyName) {
	return propertyDescriptors.get(propertyName);
}

}
