package org.mariella.glue.service;

import java.sql.Timestamp;

public abstract class LostUpdateEntityServiceImpl <T extends LostUpdateEntity> extends EntityServiceImpl<T> {

@Override
public T create(Context context) {
	T entity = super.create(context);
	entity.setCreateUser(context.getUserName());
	entity.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
	return entity;
}

public void save(T entity, Context context) {
	entity.setUpdateUser(context.getUserName());
	entity.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
	super.save(entity, context);
}

}
