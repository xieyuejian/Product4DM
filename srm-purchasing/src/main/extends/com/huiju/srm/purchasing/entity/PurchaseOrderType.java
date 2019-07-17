package com.huiju.srm.purchasing.entity;

/**
 * 订单类型
 * 
 * @author zhuang.jq
 */
public enum PurchaseOrderType {

	FromInput("录入"), FromPlan("采购计划生成"), FromPrice("价格审批单生成"), FromErp("ERP生成"), FromBatchLimit("批量导入");
	private String stateDesc;

	private PurchaseOrderType(String stateDesc) {
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

	@Override
	public String toString() {
		return this.getStateDesc();
	}
}
