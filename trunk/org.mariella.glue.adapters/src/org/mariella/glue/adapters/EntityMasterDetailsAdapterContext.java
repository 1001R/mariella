package org.mariella.glue.adapters;

import org.mariella.glue.service.Entity;

public interface EntityMasterDetailsAdapterContext<E extends Entity> extends GlueAdapterContext {

Class<E> getEntityClass();

}
