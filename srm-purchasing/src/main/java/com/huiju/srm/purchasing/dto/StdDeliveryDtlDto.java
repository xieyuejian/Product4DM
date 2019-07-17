package com.huiju.srm.purchasing.dto;

import java.math.BigDecimal;

/**
 * @author zhuang.jq
 */
public class StdDeliveryDtlDto {
	// 送货单明细行号
	protected Long deliveryDtlId;
	// 送货数量
	protected BigDecimal deliveryNumber;
	// 采购订单号
	protected String purchaseOrderCode;
	// 行号
	protected Long lineNumber;
	// 送货单号
	protected String deliveryCode;

	public Long getDeliveryDtlId() {
		return deliveryDtlId;
	}

	public void setDeliveryDtlId(Long deliveryDtlId) {
		this.deliveryDtlId = deliveryDtlId;
	}

	public BigDecimal getDeliveryNumber() {
		return deliveryNumber;
	}

	public void setDeliveryNumber(BigDecimal deliveryNumber) {
		this.deliveryNumber = deliveryNumber;
	}

	public String getPurchaseOrderCode() {
		return purchaseOrderCode;
	}

	public void setPurchaseOrderCode(String purchaseOrderCode) {
		this.purchaseOrderCode = purchaseOrderCode;
	}

	public Long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

}
