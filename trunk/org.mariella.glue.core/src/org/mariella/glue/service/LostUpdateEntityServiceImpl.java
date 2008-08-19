package org.mariella.glue.service;

import java.sql.Timestamp;

public abstract class LostUpdateEntityServiceImpl <T extends LostUpdateEntity> extends EntityServiceImpl<T> {

@Override
public T create(Context context) {
	T entity = super.create(context);
	entity.setCreateUser("aim");
	entity.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
	return entity;
}
}
