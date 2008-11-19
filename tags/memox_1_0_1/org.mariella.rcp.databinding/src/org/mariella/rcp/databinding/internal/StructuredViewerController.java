package org.mariella.rcp.databinding.internal;

import java.util.Collection;

public abstract class StructuredViewerController {

public abstract boolean hookElementChangeListeners();

public abstract void extensionsInstalled();

public abstract Collection<String> getPropertyPathes();

}
