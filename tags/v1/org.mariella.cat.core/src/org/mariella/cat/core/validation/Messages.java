package org.mariella.cat.core.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mariella.cat.core.validation.Message.Type;


public class Messages implements Iterable<Message> {

	private Map<Object, List<Message>> messageMap = new HashMap<Object, List<Message>>();
	private List<IMessageChangedListener> changeListeners = new ArrayList<IMessageChangedListener>(3);
	private boolean processChangeListeners = false;

	public Messages() {
	}

	public void addMessageChangedListener(IMessageChangedListener listener) {
		if (processChangeListeners) {
			this.changeListeners = new ArrayList<IMessageChangedListener>(this.changeListeners);
		}
		this.changeListeners.add(listener);
	}

	public void removeMessageChangedListener(IMessageChangedListener listener) {
		if (processChangeListeners) {
			this.changeListeners = new ArrayList<IMessageChangedListener>(this.changeListeners);
		}
		this.changeListeners.remove(listener);
	}

	public boolean hasMessages() {
		return !messageMap.isEmpty();
	}

	public boolean hasMessages(Type type) {
		for (Message message : this) {
			if (message.getType() == type) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMessages(Object source) {
		return messageMap.containsKey(source);
	}
	
	public Message.Type getMostSevereMessageType(Object source) {
		List<Message> messages = messageMap.get(source);
		if (messages == null || messages.isEmpty()) {
			return null;
		} else {
			Message.Type mostSevere = null;
			for (Message msg : messages) {
				if (mostSevere == null || msg.getType().ordinal() < mostSevere.ordinal()) {
					mostSevere = msg.getType();
				}
				if (mostSevere == Type.ERROR) {
					break;
				}
			}
			return mostSevere;
		}
	}
	
	public Message.Type getMostSevereMessageType(Object... sources) {
		Message.Type mostSevere = null;
		for (Object source : sources) {
			Message.Type mostSevereForSource = getMostSevereMessageType(source);
			if (mostSevere == null || (mostSevereForSource != null && mostSevereForSource.ordinal() < mostSevere.ordinal())) {
				mostSevere = mostSevereForSource;
			}
			if (mostSevere == Type.ERROR) {
				break;
			}
		}
		return mostSevere;
	}
	
	public List<Message> getMessagesOfType(Message.Type type) {
		List<Message> messagesOfType = new ArrayList<Message>();
		for (List<Message> messages : messageMap.values()) {
			for (Message msg : messages) {
				if (msg.getType() == type) {
					messagesOfType.add(msg);
				}
			}
		}
		return messagesOfType;
	}

	public void put(Object source, Message message) {
		if (message == null) {
			clear(source);
		} else {
			messageMap.put(source, new ArrayList<Message>(Arrays.asList(message)));
			fireMessagesChanged();
		}
	}

	public void put(Object source, Message... messages) {
		if (messages.length == 0) {
			clear(source);
		} else {
			messageMap.put(source, new ArrayList<Message>(Arrays.asList(messages)));
			fireMessagesChanged();
		}
	}

	public void add(Object source, Message message) {
		List<Message> messages = messageMap.get(source);
		if (messages == null) {
			messages = new ArrayList<Message>(3);
			messageMap.put(source, messages);
		}
		messages.add(message);
		fireMessagesChanged();
	}

	public void clear(Object source) {
		List<Message> messages = messageMap.remove(source);
		if (messages != null && !messages.isEmpty()) {
			fireMessagesChanged();
		}
	}

	@Override
	public Iterator<Message> iterator() {
		return new Iterator<Message>() {
			private Iterator<List<Message>> mapValueIt = messageMap.values().iterator();
			private Iterator<Message> msgIterator;
			@Override
			public boolean hasNext() {
				while ((msgIterator == null || !msgIterator.hasNext()) && mapValueIt.hasNext()) {
					msgIterator = mapValueIt.next().iterator();
				}
				return msgIterator != null && msgIterator.hasNext();
			}
			@Override
			public Message next() {
				return msgIterator.next();
			}
			@Override
			public void remove() {
				msgIterator.remove();
				fireMessagesChanged();
			}
		};
	}

	protected void fireMessagesChanged() {
		processChangeListeners = true;
		try {
			for (IMessageChangedListener listener : changeListeners) {
				listener.messagesChanged(this);
			}
		} finally {
			processChangeListeners = false;
		}
	}

}
