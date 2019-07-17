package com.huiju.srm.commons.entity;

/**
 * 快递状态
 * 
 * @author hongwl
 */
public enum ExpressState {
	ONTHEWAY("porder.purchaseOrderStateNew"), // 0 在途中
	COLLECTED("porder.purchaseOrderStateRelease"), // 1 已揽收
	DIFFICULT("porder.purchaseOrderStateOpen"), // 2疑难
	RECEIVED("porder.purchaseOrderStateClose"), // 3 已签收
	REFUND("porder.purchaseOrderStateClose"), // 4 退签
	CITYDELIVERY("porder.purchaseOrderStateClose"), // 5 同城派送中
	TRANSFER("porder.purchaseOrderStateClose"), // 6 转单
	RETURN("porder.purchaseOrderStateCancel");// 7 退回

	private String desc;

	private ExpressState(String desc) {
		this.desc = desc;
	}

	public String desc() {
		return desc;
	}

	public Integer value() {
		return this.ordinal();
	}

	public String toString() {
		return String.valueOf(this.ordinal());
	}

	public int getIndex() {
		return this.ordinal();
	}

	public String getName() {
		return this.name();
	}

	public String getStateDesc() {
		return this.desc();
	}

}
