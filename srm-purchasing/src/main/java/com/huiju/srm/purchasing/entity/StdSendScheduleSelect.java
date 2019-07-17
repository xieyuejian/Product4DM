package com.huiju.srm.purchasing.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class StdSendScheduleSelect implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "sendScheduleDetailId")
	protected Long sendScheduleDetailId;// 排程细单ID

	@Column(name = "sendScheduleCommonId")
	protected Long sendScheduleCommonId;// 排程中间表ID

	@Column(name = "purchaseOrderDetailId")
	protected Long purchaseOrderDetailId;// 订单明细ID

	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;// 采购组织编码

	@Column(name = "sendScheduleNo")
	protected String sendScheduleNo;// 来自主单排程单号编码

	@Column(name = "purchaseOrderNo")
	protected String purchaseOrderNo;// 来自主单采购订单号编码

	@Column(name = "erpPurchaseOrderNo")
	protected String erpPurchaseOrderNo;// sap采购订单号

	@Column(name = "rowNo")
	protected Long rowNo;// 行号

	@Column(name = "rowIds")
	protected Integer rowIds;// 订单明细行号

	@Column(name = "vendorCode")
	protected String vendorCode;// 供应商编码

	@Column(name = "vendorName")
	protected String vendorName;// 供应商名称

	@Column(name = "sendFlag")
	protected Integer sendFlag;// 送货标识

	@Column(name = "materialId")
	protected Long materialId;// 物料ID

	@Column(name = "materialCode")
	protected String materialCode;// 物料编码

	@Column(name = "materialName")
	protected String materialName;// 物料名称

	@Column(name = "unitCode")
	protected String unitCode;// 单位编码

	@Column(name = "unitName")
	protected String unitName;// 单位名称

	@Column(name = "scheduleTime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar scheduleTime;// 需求时间

	@Column(name = "scheduleQty")
	protected BigDecimal scheduleQty;// 需求数量

	@Column(name = "sendQty")
	protected BigDecimal sendQty;// 订单数量

	@Column(name = "deliveryQty")
	protected BigDecimal deliveryQty;// 送货量

	@Column(name = "onWayQty")
	protected BigDecimal onWayQty;// 在途量

	@Column(name = "receiptQty")
	protected BigDecimal receiptQty;// 收货量

	@Column(name = "returnGoodsQty")
	protected BigDecimal returnGoodsQty;// 退货量

	@Column(name = "canSendQty")
	protected BigDecimal canSendQty;// 可送货量

	@Column(name = "plantCode")
	protected String plantCode;// 工厂编码

	@Column(name = "factoryName")
	protected String factoryName;// 工厂名称

	@Column(name = "stockLocal")
	protected String stockLocal;// 库存地点

	@Column(name = "lineItemTypeCode")
	protected String lineItemTypeCode;// 行项目类别

	@Column(name = "taxPrice")
	protected BigDecimal taxPrice;// 未税价

	@Column(name = "createUserName")
	protected String createUserName;

	@Column(name = "overDeliveryLimit")
	protected BigDecimal overDeliveryLimit;

	public Long getSendScheduleDetailId() {
		return sendScheduleDetailId;
	}

	public void setSendScheduleDetailId(Long sendScheduleDetailId) {
		this.sendScheduleDetailId = sendScheduleDetailId;
	}

	public Long getSendScheduleCommonId() {
		return sendScheduleCommonId;
	}

	public void setSendScheduleCommonId(Long sendScheduleCommonId) {
		this.sendScheduleCommonId = sendScheduleCommonId;
	}

	public Long getPurchaseOrderDetailId() {
		return purchaseOrderDetailId;
	}

	public void setPurchaseOrderDetailId(Long purchaseOrderDetailId) {
		this.purchaseOrderDetailId = purchaseOrderDetailId;
	}

	public String getPurchasingOrgCode() {
		return purchasingOrgCode;
	}

	public void setPurchasingOrgCode(String purchasingOrgCode) {
		this.purchasingOrgCode = purchasingOrgCode;
	}

	public String getSendScheduleNo() {
		return sendScheduleNo;
	}

	public void setSendScheduleNo(String sendScheduleNo) {
		this.sendScheduleNo = sendScheduleNo;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public String getErpPurchaseOrderNo() {
		return erpPurchaseOrderNo;
	}

	public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
		this.erpPurchaseOrderNo = erpPurchaseOrderNo;
	}

	public Long getRowNo() {
		return rowNo;
	}

	public void setRowNo(Long rowNo) {
		this.rowNo = rowNo;
	}

	public Integer getRowIds() {
		return rowIds;
	}

	public void setRowIds(Integer rowIds) {
		this.rowIds = rowIds;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public Integer getSendFlag() {
		return sendFlag;
	}

	public void setSendFlag(Integer sendFlag) {
		this.sendFlag = sendFlag;
	}

	public Long getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Calendar getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Calendar scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public BigDecimal getScheduleQty() {
		return scheduleQty;
	}

	public void setScheduleQty(BigDecimal scheduleQty) {
		this.scheduleQty = scheduleQty;
	}

	public BigDecimal getSendQty() {
		return sendQty;
	}

	public void setSendQty(BigDecimal sendQty) {
		this.sendQty = sendQty;
	}

	public BigDecimal getDeliveryQty() {
		return deliveryQty;
	}

	public void setDeliveryQty(BigDecimal deliveryQty) {
		this.deliveryQty = deliveryQty;
	}

	public BigDecimal getOnWayQty() {
		return onWayQty;
	}

	public void setOnWayQty(BigDecimal onWayQty) {
		this.onWayQty = onWayQty;
	}

	public BigDecimal getReceiptQty() {
		return receiptQty;
	}

	public void setReceiptQty(BigDecimal receiptQty) {
		this.receiptQty = receiptQty;
	}

	public BigDecimal getReturnGoodsQty() {
		return returnGoodsQty;
	}

	public void setReturnGoodsQty(BigDecimal returnGoodsQty) {
		this.returnGoodsQty = returnGoodsQty;
	}

	public BigDecimal getCanSendQty() {
		return canSendQty;
	}

	public void setCanSendQty(BigDecimal canSendQty) {
		this.canSendQty = canSendQty;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public String getStockLocal() {
		return stockLocal;
	}

	public void setStockLocal(String stockLocal) {
		this.stockLocal = stockLocal;
	}

	public String getLineItemTypeCode() {
		return lineItemTypeCode;
	}

	public void setLineItemTypeCode(String lineItemTypeCode) {
		this.lineItemTypeCode = lineItemTypeCode;
	}

	public BigDecimal getTaxPrice() {
		return taxPrice;
	}

	public void setTaxPrice(BigDecimal taxPrice) {
		this.taxPrice = taxPrice;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateName(String createName) {
		this.createUserName = createName;
	}

	public BigDecimal getOverDeliveryLimit() {
		return overDeliveryLimit;
	}

	public void setOverDeliveryLimit(BigDecimal overDeliveryLimit) {
		this.overDeliveryLimit = overDeliveryLimit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sendScheduleDetailId == null) ? 0 : sendScheduleDetailId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StdSendScheduleSelect other = (StdSendScheduleSelect) obj;
		if (sendScheduleDetailId == null) {
			if (other.sendScheduleDetailId != null)
				return false;
		} else if (!sendScheduleDetailId.equals(other.sendScheduleDetailId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StdSendScheduleSelect [sendScheduleDetailId=" + sendScheduleDetailId + ", sendScheduleCommonId=" + sendScheduleCommonId
				+ ", purchaseOrderDetailId=" + purchaseOrderDetailId + ", purchasingOrgCode=" + purchasingOrgCode + ", sendScheduleNo="
				+ sendScheduleNo + ", purchaseOrderNo=" + purchaseOrderNo + ", erpPurchaseOrderNo=" + erpPurchaseOrderNo + ", rowNo="
				+ rowNo + ", rowIds=" + rowIds + ", vendorCode=" + vendorCode + ", vendorName=" + vendorName + ", sendFlag=" + sendFlag
				+ ", materialId=" + materialId + ", materialCode=" + materialCode + ", materialName=" + materialName + ", unitCode="
				+ unitCode + ", unitName=" + unitName + ", scheduleTime=" + scheduleTime + ", scheduleQty=" + scheduleQty + ", sendQty="
				+ sendQty + ", deliveryQty=" + deliveryQty + ", onWayQty=" + onWayQty + ", receiptQty=" + receiptQty + ", returnGoodsQty="
				+ returnGoodsQty + ", canSendQty=" + canSendQty + ", plantCode=" + plantCode + ", factoryName=" + factoryName
				+ ", stockLocal=" + stockLocal + ", lineItemTypeCode=" + lineItemTypeCode + ", taxPrice=" + taxPrice + ", createUserName="
				+ createUserName + ", overDeliveryLimit=" + overDeliveryLimit + "]";
	}

}
