package com.huiju.srm.purchasing.entity;

/**
 * 送货状态
 * 
 * @author zhuang.jq
 */
public enum DeliveryState {

	NEW("新建"), WAIT("待收货"), RECEIVING("收货中"), CLOSE("收货完成"), CANCEL("取消");

	private String stateDesc;

	private DeliveryState(String stateDesc) {
		this.stateDesc = stateDesc;
	}

	public String getStateDesc() {
		return stateDesc;
	}

	public void setStateDesc(String stateDesc) {
		this.stateDesc = stateDesc;
	}

	public String getName() {
		return this.name();
	}

	public int getIndex() {
		return this.ordinal();
	}

	@Override
	public String toString() {
		// return this.getStateDesc();
		return String.valueOf(this.getIndex());
	}
}
