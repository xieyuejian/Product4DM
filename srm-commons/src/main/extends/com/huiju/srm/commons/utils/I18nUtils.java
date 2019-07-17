package com.huiju.srm.commons.utils;

import java.util.Locale;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.huiju.module.i18n.MessageBundle;

/**
 * 获取国际化资源工具类<br/>
 * 供ejb及报表调用国际化资源使用 <br/>
 * 调用实例:I18nUtils.getText("button.new");
 * 
 * @author linjx
 */
public class I18nUtils {

	/**
	 * 根据资源编码获取资源值
	 * 
	 * @param key 资源key
	 * @return 资源值
	 */
	public static String getText(String key) {
		return MessageBundle.getInstance()
				.getText(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getLocale(), key);
	}

	/**
	 * 根据资源编码获取资源值
	 * 
	 * @param key 资源编码
	 * @param locale 语言
	 * @return 资源值
	 */
	public static String getText(String key, Locale locale) {
		return MessageBundle.getInstance().getText(locale, key);
	}

}
