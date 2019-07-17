package com.huiju.srm.commons.ws.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class Message implements Serializable {

	private static final long serialVersionUID = -8204124664128300071L;
	public static final String I = "I";
	public static final String W = "W";
	public static final String E = "E";
	public static final String S = "S";
	protected String message;
	protected String type;
	protected final MessageContent content = new MessageContent();

	public Message() {
	}

	public Message(String message, String type) {
		this(message, type, Collections.emptyMap());
	}

	public Message(String message, String type, Map<String, String> content) {
		this.message = message;
		this.type = type;
		if ((content != null) && (!content.isEmpty())) {
			this.content.putAll(content);
		}
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, String> getContent() {
		return this.content;
	}

	public void setContent(Map<String, String> content) {
		this.content.clear();
		this.content.putAll(content);
	}

	public void addContent(String key, String value) {
		this.content.put(key, value);
	}

	public void appendContent(String key, String value) {
		this.content.append(key, value);
	}

	public void addAllContent(Map<String, String> content) {
		if ((content != null) && (!content.isEmpty())) {
			this.content.putAll(content);
		}
	}

	public String toString() {
		return "{type:" + this.type + ", message:" + this.message + ", content:" + this.content + "}";
	}
}
