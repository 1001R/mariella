package org.mariella.oxygen.basic_impl;


public interface EntityListenerRegistry {
	public interface EntityListenerVisitor {
		public void visit(EntityListener listener);
	}


public void visitListeners(Object entity, EntityListenerVisitor visitor);
}
