package org.mariella.adapters.glue;

import org.mariella.rcp.adapters.AbstractAdapter;
import org.mariella.rcp.adapters.Adapter;

public abstract class AbstractGlueAdapter extends AbstractAdapter implements GlueAdapter {

public AbstractGlueAdapter() {
	super();
}

public AbstractGlueAdapter(Adapter parent) {
	super(parent);
}

public AbstractGlueAdapter(GlueAdapterContext context) {
	super(context);
}

@Override
public GlueAdapterContext getGlueAdapterContext() {
	return (GlueAdapterContext)getAdapterContext();
}
}
