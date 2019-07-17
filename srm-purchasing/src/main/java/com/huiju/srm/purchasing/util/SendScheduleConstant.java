package com.huiju.srm.purchasing.util;

/**
 * 送货排程常量
 * 
 * @author cwq
 * 
 */
public abstract class SendScheduleConstant {
	/** 排程需要供方确认 **/
	public final static String GROOVY_VCONFIRM = "CP0401";
	/** 排程允许选择多个订单 **/
	public final static String GROOVY_MULTIORDER = "CP0402";
	/** 订单明细需全部排程 **/
	public final static String GROOVY_FULLSCHEDULE = "CP0403";
	/** 排程变更后是否需要供方确认 **/
	public final static String GROOVY_CHANGEVCONFIRM = "CP0404";
	/** 排程单接口启用 **/
	public final static String GROOVY_SYNC = "CP0405";

	/** 管控点：是 **/
	public final static String GROOVY_YES = "1";
	/** 管控点：否 **/
	public final static String GROOVY_NO = "0";
	/** 是 **/
	public final static String YES = "1";
	/** 否 **/
	public final static String NO = "0";

}
