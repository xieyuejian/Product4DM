package com.huiju.srm.purchasing.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 同步sapvo
 * 
 * @author CWQ
 */
public class StdSendScheduleDetailVo implements Serializable {
	private static final long serialVersionUID = 3930916482773908553L;

	/** 采购组织编码 */
	protected String purchasingOrgCode;

	/** 采购组织名称 */
	protected String purchasingOrgName;

	/** 来自主单排程单号编码 */
	protected String sendScheduleNo;

	/** 来自主单采购订单号编码 */
	protected String purchaseOrderNo;

	/** sap采购订单号 */
	protected String erpPurchaseOrderNo;

	/** 行号 */
	protected Long rowNo;

	/** 订单明细行号 */
	protected Integer rowIds;

	/** 供应商编码 */
	protected String vendorCode;

	/** 供应商名称 */
	protected String vendorName;

	/** 送货标识 */
	protected Integer sendFlag;

	/** 物料ID */
	protected Long materialId;

	/** 物料编码 */
	protected String materialCode;

	/** 物料名称 */
	protected String materialName;

	/** 单位编码 */
	protected String unitCode;

	/** 单位名称 */
	protected String unitName;

	/** 需求时间 */
	protected Calendar scheduleTime;

	/** 需求数量 */
	protected BigDecimal scheduleQty;

	/** 订单数量 */
	protected BigDecimal sendQty;

	/** 送货量 */
	protected BigDecimal deliveryQty;

	/** 在途量 */
	protected BigDecimal onWayQty;

	/** 收货量 */
	protected BigDecimal receiptQty;

	/** 退货量 */
	protected BigDecimal returnGoodsQty;

	/** 可送货量 */
	protected BigDecimal canSendQty;

	/** 工厂编码 */
	protected String plantCode;

	/** 工厂名称 */
	protected String factoryName;

	/** 库存地点 */
	protected String stockLocal;

	/** 行项目类别 */
	protected String lineItemTypeCode;

	/** 未税价 */
	protected BigDecimal taxPrice;

	/** 旧的需求时间 */
	protected Calendar oldScheduleTime;

	/** 旧的需求数量 */
	protected BigDecimal oldScheduleQty;

	public String getPurchasingOrgCode() {
		return purchasingOrgCode;
	}

	public void setPurchasingOrgCode(String purchasingOrgCode) {
		this.purchasingOrgCode = purchasingOrgCode;
	}

	public String getPurchasingOrgName() {
		return purchasingOrgName;
	}

	public void setPurchasingOrgName(String purchasingOrgName) {
		this.purchasingOrgName = purchasingOrgName;
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

	public Calendar getOldScheduleTime() {
		return oldScheduleTime;
	}

	public void setOldScheduleTime(Calendar oldScheduleTime) {
		this.oldScheduleTime = oldScheduleTime;
	}

	public BigDecimal getOldScheduleQty() {
		return oldScheduleQty;
	}

	public void setOldScheduleQty(BigDecimal oldScheduleQty) {
		this.oldScheduleQty = oldScheduleQty;
	}

}
