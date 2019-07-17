package com.huiju.srm.commons.ws.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.validation.groups.Default;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Maps;
import com.huiju.module.core.Member;
import com.huiju.module.json.Json;
import com.huiju.module.util.Assert;
import com.huiju.module.util.Exceptions;
import com.huiju.module.util.StringUtils;

public class ServiceSupport {
	protected static final String MAPPING_LOCATION = "mapping.xml";
	public static final String MESSAGE_ERROR = "操作失败";
	public static final String MESSAGE_SUCCESS = "操作成功";
	public static final String MESSAGE_FAILED = "系统异常";
	protected boolean extractContent = true;
	private Map<Class<?>, Member[]> keyAccessMemberCache = Maps.newHashMap();

	protected Message success() {
		return success("操作成功", Maps.newHashMap());
	}

	protected Message success(String message) {
		return success(message, Maps.newHashMap());
	}

	protected Message success(Map<String, String> content) {
		return success("操作成功", content);
	}

	protected Message success(String message, Map<String, String> content) {
		return buildMessage(message, "S", content);
	}

	protected Message error() {
		return error("操作失败", Maps.newHashMap());
	}

	protected Message error(Exception ex) {
		return error("操作失败", ex);
	}

	protected Message error(String message, Exception ex) {
		return error(message, ex, Maps.newHashMap());
	}

	protected Message error(String message) {
		return error(message, Maps.newHashMap());
	}

	protected Message error(Map<String, String> content) {
		return error("操作失败", content);
	}

	protected Message error(String message, Exception ex, Map<String, String> content) {
		append(ex.getClass().getName(), Exceptions.getAllMessage(ex), content);
		return error(message, content);
	}

	protected Message error(String message, Map<String, String> content) {
		return buildMessage(message, "E", content);
	}

	protected Message failed() {
		return failed("系统异常");
	}

	protected Message failed(String message) {
		return failed(message, Maps.newHashMap());
	}

	protected Message failed(Map<String, String> content) {
		return failed("系统异常", content);
	}

	protected Message failed(Exception ex) {
		return failed("系统异常", ex, Maps.newHashMap());
	}

	protected Message failed(Exception ex, Map<String, String> content) {
		return failed("系统异常", ex, content);
	}

	protected Message failed(String message, Exception ex, Map<String, String> content) {
		append(ex.getClass().getName(), Exceptions.getAllMessage(ex), content);
		return failed(message, content);
	}

	protected Message failed(String message, Map<String, String> content) {
		return buildMessage(message, "E", content);
	}

	private void append(String key, String value, Map<String, String> content) {
		String msg = (String) content.get(key);
		if (StringUtils.isBlank(msg)) {
			content.put(key, value);
		} else {
			content.put(key, String.format("%s, %s", new Object[] { msg, value }));
		}
	}

	private Message buildMessage(String message, String type, Map<String, String> content) {
		message = message.trim();
		if ((message.startsWith("{")) && (message.endsWith("}"))) {
			message = getText(message.substring(1, message.length() - 1));
		}
		if (!this.extractContent) {
			return new Message(message, type, content);
		}
		return new Message(extractContentMessage(new StringBuilder(message), content), type);
	}

	protected String getText(String name) {
		return name;
	}

	protected String extractContentMessage(StringBuilder buf, Map<String, String> content) {
		if (buf == null) {
			buf = new StringBuilder();
		}
		if ((content == null) || (content.isEmpty())) {
			return buf.toString();
		}
		if ((buf.lastIndexOf("\n") != buf.length()) && (buf.lastIndexOf("/") != buf.length())) {
			buf.append("/");
		}
		Iterator<Map.Entry<String, String>> it = content.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			buf.append("{").append((String) entry.getKey()).append(":").append((String) entry.getValue()).append("}");
			if (it.hasNext()) {
				buf.append("/");
			}
		}
		return buf.toString();
	}

	protected boolean isValid(Collection<?> objs, MessageContent content) {
		return isValid(objs, content, (ValidVisitor) null, new Class[0]);
	}

	protected boolean isValid(Collection<?> objs, MessageContent content, Class<?>... groups) {
		return isValid(objs, content, null, groups);
	}

	protected boolean isValid(Collection<?> objs, MessageContent content, ValidVisitor visitor, Class<?>... groups) {
		boolean flag = true;
		for (Object object : objs) {
			flag = (flag) && (isValid(object, content, visitor, groups));
		}
		return flag;
	}

	protected boolean isValid(Object obj, MessageContent content) {
		return isValid(obj, content, new Class[] { Default.class });
	}

	protected boolean isValid(Object obj, MessageContent content, Class<?>... groups) {
		return isValid(obj, content, null, groups);
	}

	protected boolean isValid(Object obj, MessageContent content, ValidVisitor visitor, Class<?>... groups) {
		Assert.notNull(content, "message content must not be null");
		if (obj == null) {
			content.append("NULL", "input is null");
			return false;
		}

		String message = ValidationUtils.getViolationMessage(obj, visitor, groups);
		if (StringUtils.isNotBlank(message)) {
			String key = getKey(obj);
			content.append(key, message);
			return false;
		}
		return true;
	}

	protected MessageContent createContent() {
		return new MessageContent();
	}

	protected String getKey(Object obj) {
		Map<String, Object> keys = Maps.newHashMap();
		Member[] members = getKeyMember(obj.getClass());
		for (Member member : members) {
			keys.put(ServiceUtils.getKeyName(member), member.get(obj));
		}
		return Json.toJson(keys, new SerializerFeature[] { SerializerFeature.WriteMapNullValue });
	}

	protected Member[] getKeyMember(Class<?> targetClass) {
		Member[] members = (Member[]) this.keyAccessMemberCache.get(targetClass);
		if (members == null) {
			members = ServiceUtils.getKeyMembers(targetClass);
			ServiceUtils.sortMember(members);
			this.keyAccessMemberCache.put(targetClass, members);
		}
		return members;
	}

}
