package org.mariella.glue.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.mariella.glue.service.Context;
import org.mariella.glue.service.Entity;
import org.mariella.glue.service.EntityService;
import org.mariella.rcp.databinding.VBindingFactory;
import org.mariella.rcp.problems.ProblemManager;

public interface UIRegistration <T extends Entity> {
	
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
public void closeEditor(IWorkbenchPage page, long id, boolean save);
}
