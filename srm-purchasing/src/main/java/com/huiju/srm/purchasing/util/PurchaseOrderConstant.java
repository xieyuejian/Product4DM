package com.huiju.srm.purchasing.util;

/**
 * 采购订单常量
 * 
 * @author cwq
 * 
 */
public abstract class PurchaseOrderConstant {

	/** 采购订单同步接口 **/
	public final static String GROOVY_SYNC = "CP0301";
	/** 采购订单供应商默认接受 **/
	public final static String GROOVY_ACCEPT = "CP0302";
	/** 供应商查看采购订单金额标识 **/
	public final static String GROOVY_VENDORVIEW = "CP0303";
	/** 采购订单流程顺序 **/
	public final static String GROOVY_CHECKFIRST = "CP0304";
	/** 是否启用货源清单 **/
	public final static String GROOVY_SOURCELIST = "CP0305";
	/** 是否可以修改订单明细价格 **/
	public final static String GROOVY_EDITPRICE = "CP0306";

	/** 管控点：是 **/
	public final static String GROOVY_YES = "1";
	/** 管控点：否 **/
	public final static String GROOVY_NO = "0";
	/** 是 **/
	public final static String YES = "1";
	/** 否 **/
	public final static String NO = "0";

}
