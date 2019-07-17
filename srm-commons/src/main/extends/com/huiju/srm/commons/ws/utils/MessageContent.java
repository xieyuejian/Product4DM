package com.huiju.srm.commons.ws.utils;

import java.util.HashMap;

import com.huiju.module.util.StringUtils;

public class MessageContent extends HashMap<String, String> {
	private static final long serialVersionUID = 8111278858572162151L;

	public void append(String key, String value) {
		String msg = (String) get(key);
		if (StringUtils.isBlank(msg)) {
			put(key, value);
		} else {
			put(key, String.format("%s, %s", new Object[] { msg, value }));
		}
	}
}
