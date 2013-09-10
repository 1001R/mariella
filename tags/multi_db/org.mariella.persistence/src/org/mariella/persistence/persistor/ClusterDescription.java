package org.mariella.persistence.persistor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.schema.ClassDescription;

public class ClusterDescription implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private ClassDescription rootDescription;
	private String[] pathExpressions;
	private Map<String, PropertyChooser> propertyChoosers = new HashMap<String, PropertyChooser>();
public ClusterDescription() {
	super();
}

public ClusterDescription(ClassDescription rootDescription, String...pathExpressions) {
	super();
	this.rootDescription = rootDescription;
	this.pathExpressions = pathExpressions;
}

public ClassDescription getRootDescription() {
	return rootDescription;
}

public void setRootDescription(ClassDescription rootDescription) {
	this.rootDescription = rootDescription;
}

public String[] getPathExpressions() {
	return pathExpressions;
}

public void setPathExpressions(String[] pathExpressions) {
	this.pathExpressions = pathExpressions;
}

public PropertyChooser getPropertyChooser(String pathExpression) {
	if(propertyChoosers.containsKey(pathExpression)) {
		return propertyChoosers.get(pathExpression);
	} else {
		return PropertyChooser.All;
	}
}

public void setPropertyChooser(String pathExpression, PropertyChooser propertyChooser) {
	propertyChoosers.put(pathExpression, propertyChooser);
}

public String[] getChildPathExpressions(String parentPathExpression) {
	List<String> children = new ArrayList<String>();
	for(String pathExpression : pathExpressions) {
		if(pathExpression.length() > parentPathExpression.length() && pathExpression.startsWith(parentPathExpression)) {
			String child = pathExpression.substring(parentPathExpression.length() + 1);
			int idx = child.indexOf('.');
			if(idx > -1) {
				child = child.substring(0, idx);
			}
			if(!children.contains(child)) {
				children.add(child);
			}
		}
	}
	return children.toArray(new String[children.size()]);
}

}
