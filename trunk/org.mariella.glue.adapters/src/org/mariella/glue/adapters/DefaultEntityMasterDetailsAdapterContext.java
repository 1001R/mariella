package org.mariella.glue.adapters;

import org.mariella.glue.service.Context;
import org.mariella.glue.service.Entity;
import org.mariella.rcp.databinding.VBindingContext;

public class DefaultEntityMasterDetailsAdapterContext<E extends Entity> extends DefaultGlueAdapterContext implements EntityMasterDetailsAdapterContext<E>{

	private Class<E> entityClass;
	
public DefaultEntityMasterDetailsAdapterContext(VBindingContext bindingContext, Context glueContext, Class<E> entityClass) {
	super(bindingContext, glueContext);
	this.entityClass = entityClass;
}

@Override
public Class<E> getEntityClass() {
	return entityClass;
}

}
