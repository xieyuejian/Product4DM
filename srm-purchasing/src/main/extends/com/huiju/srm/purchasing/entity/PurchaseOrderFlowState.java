package com.huiju.srm.purchasing.entity;

/**
 * 订单流程状态
 * 
 * @author zhuang.jq
 */

public enum PurchaseOrderFlowState {

	CONFIRM("state.confirm"), NOPASS("state.nopass"), PASS("state.pass");
	private String desc;

	private PurchaseOrderFlowState(String desc) {
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
