package com.huiju.srm.commons.ws.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.huiju.module.core.Member;
import com.huiju.module.core.MemberAccess;
import com.huiju.module.util.ReflectionUtils;

public class ServiceUtils {

	public static void sortMember(Member[] keyAccessMembers) {
		Arrays.sort(keyAccessMembers, new Comparator<Member>() {
			public int compare(Member o1, Member o2) {
				Key a1 = ServiceUtils.getKeyAnnotation(o1);
				Key a2 = ServiceUtils.getKeyAnnotation(o2);

				return a1.ordinal() == a2.ordinal() ? 0
						: a1.ordinal() < a2.ordinal() ? -1 : (a1 == null) && (a2 == null) ? 0 : a2 == null ? 1 : a1 == null ? -1 : 1;
			}

		});
	}

	public static Member[] getKeyMembers(Class<?> targetClass) {
		List<Member> result = Lists.newArrayList();
		for (Method method : targetClass.getMethods()) {
			Key ann = (Key) method.getAnnotation(Key.class);
			if ((ann != null) && (ReflectionUtils.isReadMethod(method))) {
				result.add(MemberAccess.access(targetClass, method));
			}
		}
		for (Field field : targetClass.getDeclaredFields()) {
			Key ann = (Key) field.getAnnotation(Key.class);
			if (ann != null) {
				result.add(MemberAccess.access(targetClass, field));
			}
		}
		return (Member[]) result.toArray(new Member[result.size()]);
	}

	public static Key getKeyAnnotation(Member member) {
		Method method = member.getReadMethod();
		Key ann = null;
		if (method != null) {
			ann = (Key) method.getAnnotation(Key.class);
			if (ann == null) {
				Field field = member.getField();
				if (field != null) {
					ann = (Key) field.getAnnotation(Key.class);
				}
			}
		}
		return ann;
	}

	public static String getKeyName(Member member) {
		Key ann = getKeyAnnotation(member);
		return ann != null ? ann.name() : member.getName();
	}
}
