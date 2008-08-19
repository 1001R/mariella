package org.mariella.glue.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.mariella.glue.service.Context;
import org.mariella.glue.service.Entity;
import org.mariella.rcp.problems.ProblemManager;
import org.mariella.rcp.problems.ProblemResource;
import org.mariella.rcp.problems.ProblemsPlugin;
import org.mariella.rcp.problems.ProblemsProvider;
import org.mariella.rcp.resources.AbstractEditorAwareVResourceManager;
import org.mariella.rcp.resources.VResource;
import org.mariella.rcp.resources.VResourceRef;
import org.mariella.rcp.resources.VResourceSaveException;

public class EntityAdapterResourceManager <T extends Entity> extends AbstractEditorAwareVResourceManager implements ProblemsProvider {
	private final UIRegistration<T> uiRegistration;
	
public EntityAdapterResourceManager(UIRegistration<T> uiRegistration) {
	super();
	this.uiRegistration = uiRegistration;
	setEditorId(uiRegistration.getEditorId());
	setElementFactoryId(EntityAdapterElementFactory.ID);
}

public UIRegistration<T> getUIRegistration() {
	return uiRegistration;
}

protected ScreeningProblemScanner createProblemScanner(ProblemManager problemManager, EntityAdapter<T> model) {
	return getUIRegistration().createProblemScanner(problemManager, model);
}

@Override
public List<ProblemResource> getProblemResources() {
	Collection<VResource> loaded = getResources();
	List<ProblemResource> resources = new ArrayList<ProblemResource>();
	for(VResource resource : loaded) {
		resources.add(new ScreeningProblemResource(getUIRegistration(), resource.getRef(), "problem"));
	}
	return resources;
}

@Override
public void initialize() {
	ProblemsPlugin.getProblemManager().addProvider(this);
}

@Override
public IEditorInput getEditorInput(VResource resource) {
	return (IEditorInput)resource;
}

@Override
protected VResourceRef instanciateRef() {
	return new EntityAdapterRef();
}

@Override
protected VResource implementBuildNewResource() {
	Context context = getUIRegistration().getPersistence().createContext();
	T entity = getUIRegistration().getService().create(context);
	EntityAdapter<T> adapter = getUIRegistration().createAdapter(context, entity);
	return adapter;
}

@SuppressWarnings("unchecked")
public EntityAdapter<T> getModelForIdentity(Object identity) {
	EntityReference er = new EntityReference(uiRegistration.getEntityClass().getName(), identity);
	return (EntityAdapter<T>)getResource(getRefForPersistentId(er));
}

@Override
protected VResource implementBuildResource(Object persistentId) {
	Context context = getUIRegistration().getPersistence().createContext();
	T entity = getUIRegistration().getService().loadForEditing(context, ((EntityReference)persistentId).getIdentity(), false);
	EntityAdapter<T>  adapter = getUIRegistration().createAdapter(context, entity);
	return adapter;
}

@Override
@SuppressWarnings("unchecked")
protected Object implementSaveResource(VResource resource) throws VResourceSaveException {
	EntityAdapter<T> adapter = (EntityAdapter<T>)resource;
	ProblemsPlugin.getProblemManager().checkResourceErrors(new ScreeningProblemResource(adapter.getUIRegistration(), resource.getRef(), resource.getName()));
	getUIRegistration().getService().save(adapter.getEntity(), adapter.getHtsContext());
	return adapter.createEntityReference();
}

@SuppressWarnings("unchecked")
public void addProblems(ProblemManager problemMgr, ProblemResource problemResource) {
	EntityAdapter<T> adapter = (EntityAdapter<T>)getResource(((ScreeningProblemResource)problemResource).getRef());
	getUIRegistration().createProblemScanner(problemMgr, adapter).scanForProblems();
}

@Override
@SuppressWarnings("unchecked")
protected void implementRemoveResource(VResource resource) throws VResourceSaveException {
	EntityAdapter<T> model = (EntityAdapter<T>)resource;
	try {
		Context context = getUIRegistration().getPersistence().createContext();
		T entity = getUIRegistration().getService().loadForEditing(context,model.getEntity().getId(), false);
		getUIRegistration().getService().delete(context, entity);
	} catch(Exception e) {
		throw new VResourceSaveException(e);
	}
}

}
