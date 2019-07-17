package com.huiju.srm.purchasing.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 价格主数据视图
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdMaterialMasterPriceOrderDtlView implements Serializable {
	private static final long serialVersionUID = 334059748711537547L;

	@Id
	/** 价格ID */
	@Column(name = "materialLadderPriceDtlId")
	protected Long materialLadderPriceDtlId;

	/** 供应商编码 */
	@Column(name = "vendorCode")
	protected String vendorCode;

	/** 供应erp商编码 */
	@Column(name = "vendorErpCode")
	protected String vendorErpCode;

	/** 供应商名称 */
	@Column(name = "vendorName")
	protected String vendorName;

	/** 物料编码 */
	@Column(name = "materialCode")
	protected String materialCode;

	/** 供应商编码 */
	@Column(name = "materialName")
	protected String materialName;

	/** 记录类别 */
	@Column(name = "recordType")
	protected String recordType;

	/** 记录类别 */
	@Column(name = "recordTypeName")
	protected String recordTypeName;

	/** 工厂编码 */
	@Column(name = "plantCode")
	protected String plantCode;

	/** 工厂名称 */
	@Column(name = "plantName")
	protected String plantName;

	/** 未税价格 */
	@Column(name = "nonTaxPrice")
	protected BigDecimal nonTaxPrice;

	/** 价格单位 */
	@Column(name = "priceUnit")
	protected BigDecimal priceUnit;

	/** 细单ID */
	@Column(name = "materialMasterPriceDtlId")
	protected Long materialMasterPriceDtlId;

	/** 主单ID */
	@Column(name = "materialMasterPriceId")
	protected Long materialMasterPriceId;

	/** 过量交货限度% */
	@Column(name = "excessDeliveryLimit")
	protected BigDecimal excessDeliveryLimit;

	/** 交货不足限度% */
	@Column(name = "deliveryLimit")
	protected BigDecimal deliveryLimit;

	/** 转换单位ID */
	@Column(name = "materialUnitConversionDtlId")
	protected Long materialUnitConversionDtlId;

	/** 基本单位(订单单位与基本单位转换关系) */
	@Column(name = "elementaryUnit")
	protected BigDecimal elementaryUnit;

	/** 订单单位(订单单位与基本单位转换关系) */
	@Column(name = "orderElementaryUnit")
	protected BigDecimal orderElementaryUnit;

	/** 订单单位(订单单位与定价单位转换关系) */
	@Column(name = "orderPricingUnit")
	protected BigDecimal orderPricingUnit;

	/** 定价单位(订单单位与定价单位转换关系) */
	@Column(name = "pricingUnit")
	protected BigDecimal pricingUnit;

	/** 基本单位(订单单位与基本单位转换关系) */
	@Column(name = "elementaryUnitCode")
	protected String elementaryUnitCode;

	/** 订单单位(订单单位与基本单位转换关系) */
	@Column(name = "orderElementaryUnitCode")
	protected String orderElementaryUnitCode;

	/** 订单单位(订单单位与定价单位转换关系) */
	@Column(name = "orderPricingUnitCode")
	protected String orderPricingUnitCode;

	/** 定价单位(订单单位与定价单位转换关系) */
	@Column(name = "pricingUnitCode")
	protected String pricingUnitCode;

	/** 税率编码 */
	@Column(name = "taxRateCode")
	protected String taxRateCode;

	/** 计划天数 */
	@Column(name = "plannedDays")
	protected BigDecimal plannedDays;;

	/** 物料JIT标识，是否排程 */
	@Column(name = "jitFlag")
	protected String jitFlag;

	/** 货币编码 */
	@Column(name = "currencyCode")
	protected String currencyCode;

	/** 采购组织编码 */
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;

	@Column(name = "effectiveDate")
	/** 有效开始日期 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar effectiveDate;

	@Column(name = "expirationDate")
	/** 有效截止时间 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar expirationDate;

	/** 质检标识 */
	@Column(name = "qualityCheck")
	// @Transient
	protected String qualityCheck;
	/** 库存地点 */
	@Column(name = "storLocCode")
	// @Transient
	protected String storLocCode;

	/** 数量 */
	@Transient
	protected BigDecimal buyerQty;

	/** 是否退货0否1是 */
	@Transient
	protected Integer isReturn;

	/** 送货日期_采购方 */
	@Transient
	protected Calendar buyerTime;

	/** 备注 */
	@Transient
	protected String remark;

	/** 单位编码 */
	@Transient
	protected String unitCode;

	/** 库存地址 */
	@Transient
	protected String stockLocationCode;

	/** 库存类型 **/
	@Transient
	protected String itemCode;

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
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

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public BigDecimal getNonTaxPrice() {
		return nonTaxPrice;
	}

	public void setNonTaxPrice(BigDecimal nonTaxPrice) {
		this.nonTaxPrice = nonTaxPrice;
	}

	public BigDecimal getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(BigDecimal priceUnit) {
		this.priceUnit = priceUnit;
	}

	public Long getMaterialMasterPriceOrderDtlViewId() {
		return materialMasterPriceDtlId;
	}

	public void setMaterialMasterPriceOrderDtlViewId(Long materialMasterPriceDtlId) {
		this.materialMasterPriceDtlId = materialMasterPriceDtlId;
	}

	public Long getMaterialLadderPriceDtlId() {
		return materialLadderPriceDtlId;
	}

	public void setMaterialLadderPriceDtlId(Long materialLadderPriceDtlId) {
		this.materialLadderPriceDtlId = materialLadderPriceDtlId;
	}

	public Long getMaterialMasterPriceId() {
		return materialMasterPriceId;
	}

	public void setMaterialMasterPriceId(Long materialMasterPriceId) {
		this.materialMasterPriceId = materialMasterPriceId;
	}

	public BigDecimal getExcessDeliveryLimit() {
		return excessDeliveryLimit;
	}

	public void setExcessDeliveryLimit(BigDecimal excessDeliveryLimit) {
		this.excessDeliveryLimit = excessDeliveryLimit;
	}

	public BigDecimal getDeliveryLimit() {
		return deliveryLimit;
	}

	public void setDeliveryLimit(BigDecimal deliveryLimit) {
		this.deliveryLimit = deliveryLimit;
	}

	public Long getMaterialUnitConversionDtlId() {
		return materialUnitConversionDtlId;
	}

	public void setMaterialUnitConversionDtlId(Long materialUnitConversionDtlId) {
		this.materialUnitConversionDtlId = materialUnitConversionDtlId;
	}

	public BigDecimal getElementaryUnit() {
		return elementaryUnit;
	}

	public void setElementaryUnit(BigDecimal elementaryUnit) {
		this.elementaryUnit = elementaryUnit;
	}

	public BigDecimal getOrderElementaryUnit() {
		return orderElementaryUnit;
	}

	public void setOrderElementaryUnit(BigDecimal orderElementaryUnit) {
		this.orderElementaryUnit = orderElementaryUnit;
	}

	public BigDecimal getOrderPricingUnit() {
		return orderPricingUnit;
	}

	public void setOrderPricingUnit(BigDecimal orderPricingUnit) {
		this.orderPricingUnit = orderPricingUnit;
	}

	public BigDecimal getPricingUnit() {
		return pricingUnit;
	}

	public void setPricingUnit(BigDecimal pricingUnit) {
		this.pricingUnit = pricingUnit;
	}

	public String getElementaryUnitCode() {
		return elementaryUnitCode;
	}

	public void setElementaryUnitCode(String elementaryUnitCode) {
		this.elementaryUnitCode = elementaryUnitCode;
	}

	public String getOrderElementaryUnitCode() {
		return orderElementaryUnitCode;
	}

	public void setOrderElementaryUnitCode(String orderElementaryUnitCode) {
		this.orderElementaryUnitCode = orderElementaryUnitCode;
	}

	public String getOrderPricingUnitCode() {
		return orderPricingUnitCode;
	}

	public void setOrderPricingUnitCode(String orderPricingUnitCode) {
		this.orderPricingUnitCode = orderPricingUnitCode;
	}

	public String getPricingUnitCode() {
		return pricingUnitCode;
	}

	public void setPricingUnitCode(String pricingUnitCode) {
		this.pricingUnitCode = pricingUnitCode;
	}

	public String getTaxRateCode() {
		return taxRateCode;
	}

	public void setTaxRateCode(String taxRateCode) {
		this.taxRateCode = taxRateCode;
	}

	public BigDecimal getPlannedDays() {
		return plannedDays;
	}

	public void setPlannedDays(BigDecimal plannedDays) {
		this.plannedDays = plannedDays;
	}

	public String getJitFlag() {
		return jitFlag;
	}

	public void setJitFlag(String jitFlag) {
		this.jitFlag = jitFlag;
	}

	public BigDecimal getBuyerQty() {
		return buyerQty;
	}

	public void setBuyerQty(BigDecimal buyerQty) {
		this.buyerQty = buyerQty;
	}

	public Integer getIsReturn() {
		return isReturn;
	}

	public void setIsReturn(Integer isReturn) {
		this.isReturn = isReturn;
	}

	public Calendar getBuyerTime() {
		return buyerTime;
	}

	public void setBuyerTime(Calendar buyerTime) {
		this.buyerTime = buyerTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public Long getMaterialMasterPriceDtlId() {
		return materialMasterPriceDtlId;
	}

	public void setMaterialMasterPriceDtlId(Long materialMasterPriceDtlId) {
		this.materialMasterPriceDtlId = materialMasterPriceDtlId;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPurchasingOrgCode() {
		return purchasingOrgCode;
	}

	public void setPurchasingOrgCode(String purchasingOrgCode) {
		this.purchasingOrgCode = purchasingOrgCode;
	}

	public Calendar getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Calendar effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Calendar getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Calendar expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getStockLocationCode() {
		return stockLocationCode;
	}

	public void setStockLocationCode(String stockLocationCode) {
		this.stockLocationCode = stockLocationCode;
	}

	public String getRecordTypeName() {
		return recordTypeName;
	}

	public void setRecordTypeName(String recordTypeName) {
		this.recordTypeName = recordTypeName;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public String getVendorErpCode() {
		return vendorErpCode;
	}

	public void setVendorErpCode(String vendorErpCode) {
		this.vendorErpCode = vendorErpCode;
	}

	public String getQualityCheck() {
		return qualityCheck;
	}

	public void setQualityCheck(String qualityCheck) {
		this.qualityCheck = qualityCheck;
	}

	public String getStorLocCode() {
		return storLocCode;
	}

	public void setStorLocCode(String storLocCode) {
		this.storLocCode = storLocCode;
	}
}
