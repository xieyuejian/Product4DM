package com.huiju.srm.purchasing.entity;

/**
 * 供应商确认状态
 * 
 * @author zhuang.jq
 */

public enum PurchaseOrderCheckState {

	CONFIRM("porder.purchaseOrderCheckStateCheck"), // 待审核
	ACCEPT("porder.purchaseOrderCheckStateAccept"), // 接受 V
	HOLD("porder.purchaseOrderCheckStateHold"), // 变更 V
	FIRMHOLD("porder.purchaseOrderCheckStateFirmhold"), // 确认变更 B
	REJECT("porder.purchaseOrderCheckStateReject"), // 拒绝 V
	FIRMREJECT("porder.purchaseOrderCheckStateFirmreject");// 确认拒绝 B
	private String desc;

	private PurchaseOrderCheckState(String desc) {
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
