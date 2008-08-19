package org.mariella.glue.ui;

import org.eclipse.swt.graphics.Image;
import org.mariella.glue.service.Context;
import org.mariella.glue.service.Persistence;
import org.mariella.glue.service.Entity;
import org.mariella.glue.service.EntityService;
import org.mariella.rcp.databinding.VBindingFactory;
import org.mariella.rcp.problems.ProblemManager;

public interface UIRegistration <T extends Entity> {
	
public Persistence getPersistence();
public VBindingFactory getDataBindingFactory();
public Class<?> getEntityClass();

public String getLabel();

public String getEditorId();
public EntityAdapterResourceManager<T> getResourceManager();
public EntityService<T> getService();
public Image getImage();

public void delete(Long id);

public ScreeningProblemScanner createProblemScanner(ProblemManager problemManager, EntityAdapter<T> model);
public EntityAdapter<T> createAdapter(Context context, T entity);

public void openEditor(Object identity);
}
