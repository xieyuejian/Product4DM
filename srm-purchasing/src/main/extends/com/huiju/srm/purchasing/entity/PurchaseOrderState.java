package com.huiju.srm.purchasing.entity;

/**
 * 订单状态
 * 
 * @author zhuang.jq
 */
public enum PurchaseOrderState {

	NEW("porder.purchaseOrderStateNew"), // 0 新建
	RELEASE("porder.purchaseOrderStateRelease"), // 1 发布
	OPEN("porder.purchaseOrderStateOpen"), // 2 执行
	CLOSE("porder.purchaseOrderStateClose"), // 3 关闭
	CANCEL("porder.purchaseOrderStateCancel");// 4 取消

	private String desc;

	private PurchaseOrderState(String desc) {
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
