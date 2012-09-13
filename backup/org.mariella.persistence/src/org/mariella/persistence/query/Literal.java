package org.mariella.persistence.query;

import org.mariella.persistence.database.Converter;

public abstract class Literal<T> implements Expression {
	protected final Converter<T> converter;
	protected final T value;

public Literal(Converter<T> converter, T value) {
	this.converter = converter;
	this.value = value;
}

public void printSql(StringBuilder b) {
	b.append(converter.toString(value));
}

}
