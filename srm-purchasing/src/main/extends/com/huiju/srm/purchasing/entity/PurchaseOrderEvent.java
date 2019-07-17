package com.huiju.srm.purchasing.entity;

/**
 * 采购订单动作
 * 
 * @author zhuang.jq
 */
public enum PurchaseOrderEvent {

	TONEW("新建订单"), TOSAVE("保存订单"), TORELEASE("发布订单"), TOCONFIRM("订单提交审核"), TOPASS("订单审核通过"), TONOPASS("订单审核不过"), TOACCEPT("接受订单"), TOHOLD(
			"变更订单"), TOREJECT("拒绝订单"), TOOPEN("执行订单"), TOFIRMHOLD(
					"确认订单变更"), TOFIRMREJECT("确认订单拒绝"), TOCANCEL("取消订单"), TOCLOSE("订单完成"), TOREMOVE("删除订单"), TOSYN("同步订单"), TOREVOKE("撤销审批");
	private String eventDesc;

	private PurchaseOrderEvent(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public String getName() {
		return this.name();
	}

	@Override
	public String toString() {
		return this.getEventDesc();
	}
}
