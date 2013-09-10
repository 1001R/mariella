package org.mariella.persistence.persistor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.mariella.persistence.schema.PropertyDescription;

public interface PropertyChooser extends Serializable {
	static class AllChooser implements PropertyChooser {
		private static final long serialVersionUID = 1L;

		public boolean wants(PropertyDescription propertyDescription) {
			return true;
		}
	}
	
	
	public static PropertyChooser All = new AllChooser();
	
	public static class Include implements PropertyChooser {
		private static final long serialVersionUID = 1L;
		
		private final Collection<String> propertyNames;
		
		public Include(String...propertyNames) {
			super();
			this.propertyNames = new HashSet<String>(Arrays.asList(propertyNames));
		}
		public boolean wants(PropertyDescription propertyDescription) {
			return propertyNames.contains(propertyDescription.getPropertyDescriptor().getName());
		}
	};

	public static class Exclude implements PropertyChooser {
		private static final long serialVersionUID = 1L;
		
		private final Collection<String> propertyNames;
		
		public Exclude(String...propertyNames) {
			super();
			this.propertyNames = new HashSet<String>(Arrays.asList(propertyNames));
		}
		public boolean wants(PropertyDescription propertyDescription) {
			return !propertyNames.contains(propertyDescription.getPropertyDescriptor().getName());
		}
	};

	
public boolean wants(PropertyDescription propertyDescription);
}
