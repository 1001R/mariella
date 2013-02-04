package org.mariella.cat.ui.editcontext;

import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;

import org.mariella.cat.core.validation.Message;
import org.mariella.cat.core.validation.Messages;


public abstract class FormattedTextEditSupport<T> extends EditSupportAdapter<String> {

	private Format format;
	private Object messageKey;
	private String parseErrorMessage = "Invalid value: ''{0}''";
	
	public FormattedTextEditSupport() {
		this(null, null, null);
	}
	
	public FormattedTextEditSupport(Format format) {
		this(format, null, null);
	}
	
	public FormattedTextEditSupport(Format format, Object messageKey, String parseErrorMessage) {
		this.format = format;
		this.messageKey = messageKey;
		if (parseErrorMessage != null) {
			this.parseErrorMessage = parseErrorMessage;
		}
	}

	public void setFormat(Format format) {
		this.format = format;
	}
	
	public Format getFormat() {
		return format;
	}
	
	public void setMessageKey(Object messageKey) {
		this.messageKey = messageKey;
	}
	
	public Object getMessageKey() {
		return messageKey;
	}
	
	public void setParseErrorMessage(String parseErrorMessage) {
		this.parseErrorMessage = parseErrorMessage;
	}
	
	public String getParseErrorMessage() {
		return parseErrorMessage;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void uiValueChanged(EditContext editContext, String uiValue, Object source, Messages messages) {
		ParsePosition pp = new ParsePosition(0);
		T modelValue = (T) format.parseObject(uiValue, pp);
		if (pp.getErrorIndex() >= 0 || pp.getIndex() < uiValue.length()) {
			onParseError(editContext, uiValue, source, messages);
		} else {
			setModelValue(modelValue);
		}
	}
	
	protected abstract void setModelValue(T modelValue);
	
	protected void onParseError(EditContext editContext, String uiValue, Object source, Messages messages) {
		messages.put(messageKey == null ? source : messageKey, Message.error(MessageFormat.format(parseErrorMessage, uiValue)));
	}

}
