package at.hts.persistence.schema;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class SchemaDescription {
	private final Map<String, ClassDescription> classDescriptions = new HashMap<String, ClassDescription>();
	
public ClassDescription getClassDescription(String className) {
	return classDescriptions.get(className);
}

public void addClassDescription(ClassDescription classDescription) {
	classDescriptions.put(classDescription.getClassName(), classDescription);
}

public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
	try {
		BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
		for(PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			if(pd.getName().equals(propertyName)) {
				return pd;
			}
		}
	} catch(IntrospectionException e) {
	}
	return null;
}

}
