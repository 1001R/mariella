package org.mariella.oxygen.basic_impl;

import java.lang.reflect.Method;

public abstract class EntityListener {
	private Method[] prePersist;
	private Method[] postPersist;
	
	private Method[] preUpdate;
	private Method[] postUpdate;
	
	private Method[] preRemove;
	private Method[] postRemove;
	
	private Method[] postLoad;

protected abstract void perform(Method[] method, Object entity);

public void postLoad(Object entity) {
	perform(postLoad, entity);
}

public void prePersist(Object entity) {
	perform(prePersist, entity);
}

public void postPersist(Object entity) {
	perform(postPersist, entity);
}

public void preUpdate(Object entity) {
	perform(preUpdate, entity);
}

public void postUpdate(Object entity) {
	perform(postUpdate, entity);
}

public void preRemove(Object entity) {
	perform(preRemove, entity);
}

public void postRemove(Object entity) {
	perform(postRemove, entity);
}

public Method[] getPostLoad() {
	return postLoad;
}

public void setPostLoad(Method[] postLoad) {
	this.postLoad = postLoad;
}

public Method[] getPostPersist() {
	return postPersist;
}

public void setPostPersist(Method[] postPersist) {
	this.postPersist = postPersist;
}

public Method[] getPostRemove() {
	return postRemove;
}

public void setPostRemove(Method[] postRemove) {
	this.postRemove = postRemove;
}

public Method[] getPostUpdate() {
	return postUpdate;
}

public void setPostUpdate(Method[] postUpdate) {
	this.postUpdate = postUpdate;
}

public Method[] getPrePersist() {
	return prePersist;
}

public void setPrePersist(Method[] prePersist) {
	this.prePersist = prePersist;
}

public Method[] getPreRemove() {
	return preRemove;
}

public void setPreRemove(Method[] preRemove) {
	this.preRemove = preRemove;
}

public Method[] getPreUpdate() {
	return preUpdate;
}

public void setPreUpdate(Method[] preUpdate) {
	this.preUpdate = preUpdate;
}

}
