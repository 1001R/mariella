package org.mariella.cat.core.validation;

public class Message {
	
	public static enum Type {
		ERROR,
		WARNING,
		INFO
	}

	private Type type;
	private String text;
	
	public Message(Type type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public Type getType() {
		return type;
	}

	public String getText() {
		return text;
	}
	
	public static Message error(String text) {
		return new Message(Type.ERROR, text);
	}
	
	public static Message info(String text) {
		return new Message(Type.INFO, text);
	}

	public static Message warning(String text) {
		return new Message(Type.WARNING, text);
	}
}
