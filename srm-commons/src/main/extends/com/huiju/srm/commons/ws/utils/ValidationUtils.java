package com.huiju.srm.commons.ws.utils;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.poi.ss.formula.functions.T;

import com.huiju.module.util.StringUtils;

public class ValidationUtils {

	/**
	 * 使用hibernate的注解来进行验证
	 * 
	 */
	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	/**
	 * 校验方法
	 * 
	 * @param obj
	 * @return
	 */
	public static String validate(T obj) {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
		// 抛出检验异常
		if (constraintViolations.size() > 0) {
			return constraintViolations.iterator().next().getMessage();
		}

		return "";
	}

	/**
	 * 验证方法
	 * 
	 * @param object
	 *            要验证的对象
	 * @param visitor
	 *            验证自定义方法
	 * @param groups
	 *            只验证标记的类
	 * @return
	 */
	public static String getViolationMessage(Object object, ValidVisitor visitor, Class<?>... groups) {
		StringBuilder buf = new StringBuilder();

		Iterator<ConstraintViolation<Object>> it = validator.validate(object, groups).iterator();
		while (it.hasNext()) {
			buf.append(it.next().getMessage());
			// buf.append(((ConstraintViolation) it.next()).getMessage());
			if (it.hasNext()) {
				buf.append(", ");
			}
		}
		if (visitor != null) {
			String message = visitor.valid(object);
			if (!StringUtils.isBlank(message)) {
				buf.append(", ").append(message);
			}
		}
		return buf.length() == 0 ? null : buf.toString();
	}

	public static String isValid(Object obj, ValidVisitor visitor, Class<?>... groups) {
		if (obj == null) {
			return "校验对象不能为空";
		}

		String message = getViolationMessage(obj, visitor, groups);
		return message;
	}

	public static String isValid(Object obj, Class<?>... groups) {
		return isValid(obj, null, groups);
	}
}
