package com.huiju.srm.purchasing.entity;

public enum SendScheduleEvent {
	TONEW("新建"), // 0
	TOCONFIRM("提交审核"), // 1
	TONOPASS("审核不过"), // 2
	TOPASS("审核通过"), // 3
	TORELEASE("发布"), // 4
	TOREFUSE("拒绝"), // 5
	TOOPEN("执行"), // 6
	TOCANCEL("取消"), // 7
	TOCLOSE("完成"), // 8
	TOSAVE("保存"), // 9
	TOHOLD("变更"), // 10
	TOACCEPT("接受"), // 11
	TOREMOVE("删除"), // 12
	TOSYN("同步");// 13

	private String eventeDesc;

	private SendScheduleEvent(String eventeDesc) {
		this.eventeDesc = eventeDesc;
	}

	public void setEventDesc(String eventeDesc) {
		this.eventeDesc = eventeDesc;
	}

	public String getName() {
		return this.name();
	}

	public int getIndex() {
		return this.ordinal();
	}

	@Override
	public String toString() {
		return String.valueOf(this.getIndex());
	}

	public String getEventDesc() {
		return eventeDesc;
	}

}
