package com.huiju.srm.purchasing.entity;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 送货排程明细
 * 
 * @author CWQ date 2016年8月9日 09:34:15
 */
@MappedSuperclass
public class StdSendScheduleDetail extends BaseEntity<Long> {
	private static final long serialVersionUID = 9005288631888284052L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "SendScheduleDetail_PK")
	@TableGenerator(name = "SendScheduleDetail_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "SendScheduleDetail_PK", allocationSize = 1)
	/** 排程细单ID */
	@Column(name = "sendScheduleDetailId")
	protected Long sendScheduleDetailId;

	@ManyToOne(optional = false)
	/** 排程中间表ID */
	@JoinColumn(name = "sendScheduleCommonId", referencedColumnName = "sendScheduleCommonId")
	protected SendScheduleCommon sendScheduleCommon;

	/** 订单明细ID */
	@Column(name = "purchaseOrderDetailId")
	protected Long purchaseOrderDetailId;

	/** 采购组织编码 */
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;

	/** 采购组织名称 */
	@Column(name = "purchasingOrgName")
	protected String purchasingOrgName;

	/** 来自主单排程单号编码 */
	@Column(name = "sendScheduleNo")
	protected String sendScheduleNo;

	/** 来自主单采购订单号编码 */
	@Column(name = "purchaseOrderNo")
	protected String purchaseOrderNo;

	/** sap采购订单号 */
	@Column(name = "erpPurchaseOrderNo")
	protected String erpPurchaseOrderNo;

	/** 行号 */
	@Column(name = "rowNo")
	protected Long rowNo;

	/** 订单明细行号 */
	@Column(name = "rowIds")
	protected Integer rowIds;

	/** 供应商编码 */
	@Column(name = "vendorCode")
	protected String vendorCode;

	/** 供应商名称 */
	@Column(name = "vendorName")
	protected String vendorName;

	/** 送货标识 */
	@Column(name = "sendFlag")
	protected Integer sendFlag;

	/** 物料ID */
	@Column(name = "materialId")
	protected Long materialId;

	/** 物料编码 */
	@Column(name = "materialCode")
	protected String materialCode;

	/** 物料名称 */
	@Column(name = "materialName")
	protected String materialName;

	/** 单位编码 */
	@Column(name = "unitCode")
	protected String unitCode;

	/** 单位名称 */
	@Column(name = "unitName")
	protected String unitName;

	@Column(name = "scheduleTime")
	/** 需求时间 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar scheduleTime;

	/** 需求数量 */
	@Column(name = "scheduleQty")
	protected BigDecimal scheduleQty;

	/** 订单数量 */
	@Column(name = "sendQty")
	protected BigDecimal sendQty;

	/** 送货量 */
	@Column(name = "deliveryQty")
	protected BigDecimal deliveryQty;

	/** 在途量 */
	@Column(name = "onWayQty")
	protected BigDecimal onWayQty;

	/** 收货量 */
	@Column(name = "receiptQty")
	protected BigDecimal receiptQty;

	/** 退货量 */
	@Column(name = "returnGoodsQty")
	protected BigDecimal returnGoodsQty;

	/** 可送货量 */
	@Column(name = "canSendQty")
	protected BigDecimal canSendQty;

	/** 工厂编码 */
	@Column(name = "plantCode")
	protected String plantCode;

	/** 工厂名称 */
	@Column(name = "factoryName")
	protected String factoryName;

	/** 库存地点 */
	@Column(name = "stockLocal")
	protected String stockLocal;

	/** 行项目类别 */
	@Column(name = "lineItemTypeCode")
	protected String lineItemTypeCode;

	/** 未税价 */
	@Column(name = "taxPrice")
	protected BigDecimal taxPrice;

	@Column(name = "oldScheduleTime")
	/** 旧的需求时间 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar oldScheduleTime;

	/** 旧的需求数量 */
	@Column(name = "oldScheduleQty")
	protected BigDecimal oldScheduleQty;

	public Long getSendScheduleDetailId() {
		return sendScheduleDetailId;
	}

	public void setSendScheduleDetailId(Long sendScheduleDetailId) {
		this.sendScheduleDetailId = sendScheduleDetailId;
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

	public Long getPurchaseOrderDetailId() {
		return purchaseOrderDetailId;
	}

	public void setPurchaseOrderDetailId(Long purchaseOrderDetailId) {
		this.purchaseOrderDetailId = purchaseOrderDetailId;
	}

	public SendScheduleCommon getSendScheduleCommon() {
		return sendScheduleCommon;
	}

	public void setSendScheduleCommon(SendScheduleCommon sendScheduleCommon) {
		this.sendScheduleCommon = sendScheduleCommon;
	}

	public String getErpPurchaseOrderNo() {
		return erpPurchaseOrderNo;
	}

	public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
		this.erpPurchaseOrderNo = erpPurchaseOrderNo;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public String getPurchasingOrgName() {
		return purchasingOrgName;
	}

	public void setPurchasingOrgName(String purchasingOrgName) {
		this.purchasingOrgName = purchasingOrgName;
	}

	/**
	 * @return the oldScheduleTime
	 */
	public Calendar getOldScheduleTime() {
		return oldScheduleTime;
	}

	/**
	 * @param oldScheduleTime the oldScheduleTime to set
	 */
	public void setOldScheduleTime(Calendar oldScheduleTime) {
		this.oldScheduleTime = oldScheduleTime;
	}

	/**
	 * @return the oldScheduleQty
	 */
	public BigDecimal getOldScheduleQty() {
		return oldScheduleQty;
	}

	/**
	 * @param oldScheduleQty the oldScheduleQty to set
	 */
	public void setOldScheduleQty(BigDecimal oldScheduleQty) {
		this.oldScheduleQty = oldScheduleQty;
	}

}
