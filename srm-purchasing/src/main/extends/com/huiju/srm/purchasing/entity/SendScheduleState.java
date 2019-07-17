package com.huiju.srm.purchasing.entity;

/**
 * 排程状态
 * 
 * @author zhuang.jq
 */

public enum SendScheduleState {
	NEW("新建"), // 0
	CONFIRM("待审核"), // 1
	NOPASS("审核不过"), // 2
	PASS("审核通过"), // 3
	RELEASE("发布"), // 4
	REFUSE("拒绝"), // 5
	OPEN("执行"), // 6
	CANCEL("取消"), // 7
	CLOSE("完成");// 8
	private String stateDesc;

	private SendScheduleState(String stateDesc) {
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
