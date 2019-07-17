package com.huiju.srm.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huiju.module.data.jpa.utils.DataUtils;

/**
 * 常用工具类
 * 
 * @author WANGLQ
 * 
 */
public class CommonUtil {

	/**
	 * 日期格式化工具(yyyy-MM-dd HH:mm:ss)
	 */
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 日期格式化工具(yyyy-MM-dd)
	 */
	public static final SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 将实体转换成工作流可以识别的变量集HashMap;</br>
	 * 日期将被转换成Long对象，目前仅支持yyyy-MM-dd
	 * HH:mm:ss和yyyy-MM-dd格式字符串以及Date和Calendar类型的对象;<br>
	 * 子对象无法转换，请使用参数excludes排除;<br>
	 * 
	 * @param object 实体对象
	 * @param excludes 排除字段
	 * @return
	 */
	public static HashMap<String, Object> toMap(Object object, String[] excludes) {
		// (object, excludes)
		String json = DataUtils.toJson(object, new SerializerFeature[] { SerializerFeature.WriteMapNullValue }, excludes);
		HashMap<String, Object> map = new HashMap<String, Object>(JSON.parseObject(json));// new
																							// HashMap<String,
																							// Object>(JSONObject.fromObject(json));
		for (String key : map.keySet()) {
			Object o = map.get(key);
			if (o == null) {
				continue;
			}
			// 日期在Activiti中不能直接比较，转成Long
			if (o.getClass().equals(Date.class)) {
				Date v = (Date) o;
				map.put(key, v.getTime());
			} else if (o.getClass().equals(Calendar.class)) {
				Calendar v = (Calendar) o;
				map.put(key, v.getTimeInMillis());
			} else if (o.getClass().equals(String.class)) {
				String v = o.toString();
				if (v.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
					try {
						Date date = sdfDay.parse(v);
						map.put(key, date.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (v.matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}")) {
					try {
						Date date = sdf.parse(v);
						map.put(key, date.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return map;
	}

	/**
	 * 复制一个对象
	 * 
	 * @param t 对象
	 * @return 新的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyObject(T t) {
		SerializerFeature feature = SerializerFeature.DisableCircularReferenceDetect;
		String jsonStr = JSON.toJSONString(t, feature);
		T newT = (T) JSON.parseObject(jsonStr, t.getClass());
		return newT;
	}

	/**
	 * 复制一个对象集合
	 * 
	 * @param t 集合
	 * @return 新的集合对象
	 */
	public static <T> List<T> copyObject(List<T> t) {
		List<T> newList = new ArrayList<T>();
		for (T t2 : t) {
			T newT = CommonUtil.copyObject(t2);
			newList.add(newT);
		}
		return newList;
	}

	/**
	 * 复制一个对象到另一个对象
	 * 
	 * @param t 源
	 * @param e 目
	 * @param excludes 排除不复制的字段
	 * @return 目
	 */
	@SuppressWarnings("unchecked")
	public static <E, T> E copyObject(T t, E e, String... excludes) {
		// SerializerFeature feature =
		// SerializerFeature.DisableCircularReferenceDetect;
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
		String jsonStr = null;
		if (excludes != null && excludes.length > 0) {
			List<String> excludesList = Arrays.asList(excludes);
			filter.getExcludes().addAll(excludesList);
			jsonStr = JSON.toJSONString(t, filter);
			System.out.println(jsonStr);
		} else {
			jsonStr = JSON.toJSONString(t);
		}
		E newT = (E) JSON.parseObject(jsonStr, e.getClass());
		return newT;
	}

	/**
	 * 复制一个对象到另一个对象
	 * 
	 * @param t 源
	 * @param e 目
	 * @return 目
	 */
	public static <E, T> E copyObject(T t, E e) {
		return copyObject(t, e, new String[] {});
	}

	/**
	 * 把Calendar类型的日期值转换为字符串
	 *
	 * @param cl Calendar类型的日期值
	 * @param format 日期格式，如：yyyy-MM-dd HH:mm:ss
	 * @return
	 *
	 * @author：yu8home
	 * @date：2017年1月5日 上午11:16:01
	 */
	public static String parseStringFromCalendar(Calendar cl, String format) {
		String retStr = "";
		if (cl != null) {
			try {
				SimpleDateFormat df = new SimpleDateFormat(format);
				retStr = df.format(cl.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return retStr;
	}

	/**
	 * 字符串前补0
	 * 
	 * @param formatStr 表达式（"%18s"）
	 * @param str 字符串
	 * @return 格式化后值
	 */
	public static String strToAddZero(String formatStr, String str) {
		// TODO Auto-generated method stub
		if (str != null && !"".equals(str)) {
			return String.format(formatStr, str).replaceAll(" ", "0");
		} else {
			return null;
		}

	}

	/**
	 * 字符串去0
	 * 
	 * @param str 字符串
	 * @return 格式化后值
	 */
	public static String removeZero(String str) {
		// TODO Auto-generated method stub
		if (str == null) {
			return null;
		} else {
			return str.replaceAll("^(0+)", "");
		}

	}

	/**
	 * 字符串转日期(yyyy-MM-dd)
	 * 
	 * @param dateStr 日期字符串
	 * @return
	 */
	public static Calendar strToDate(String dateStr) {
		// TODO Auto-generated method stub
		Date date = null;
		try {
			date = sdfDay.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 字符串转日期(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param dateStr 日期字符串
	 * @return
	 */
	public static Calendar strToDateTime(String dateStr) {
		// TODO Auto-generated method stub
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
}
