package com.huiju.srm.purchasing.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 采购订单细单定价条件关系体类
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdPurchaseOrderPricing extends BaseEntity<Long> {
	private static final long serialVersionUID = -8390985527280625417L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PurchaseOrderPricing_PK")
	@TableGenerator(name = "PurchaseOrderPricing_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "PurchaseOrderPricing_PK", allocationSize = 10)
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "purchaseOrderPricing_seq")
	// @SequenceGenerator(name = "purchaseOrderPricing_seq", sequenceName =
	// "seq_purchaseOrderPricing", initialValue = 0, allocationSize = 1)
	/** id */
	@Column(name = "purchaseOrderPricingId")
	protected Long purchaseOrderPricingId;

	/** 订单明细对象 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "purchaseOrderDetailId", referencedColumnName = "purchaseOrderDetailId")
	protected PurchaseOrderDetail purchaseOrderDetail;

	/** 条件类型 */
	@Column(name = "purchaseOrderPricingTypeCode")
	protected String purchaseOrderPricingTypeCode;

	/** 条件类型描述 */
	@Column(name = "purchaseOrderPricingTypeName")
	protected String purchaseOrderPricingTypeName;

	/** 价格 */
	@Column(name = "pricingQty")
	protected BigDecimal pricingQty;

	/** 价格单位 */
	@Column(name = "priceUnit")
	protected Long priceUnit;

	/** 金额 */
	@Column(name = "amount")
	protected BigDecimal amount;

	/** 条件行号 */
	@Column(name = "purchaseOrderPricingRowId")
	protected Integer purchaseOrderPricingRowId;

	/** SAP采购细单行项目号 */
	@Column(name = "rowIds")
	protected Long rowIds;

	/** 币种 */
	@Column(name = "curType")
	protected String curType;

	/** 采购员是否可以修改金额,0不行，1可以 */
	@Transient
	protected String isEditPrice;

	public Long getPurchaseOrderPricingId() {
		return purchaseOrderPricingId;
	}

	public void setPurchaseOrderPricingId(Long purchaseOrderPricingId) {
		this.purchaseOrderPricingId = purchaseOrderPricingId;
	}

	public PurchaseOrderDetail getPurchaseOrderDetail() {
		return purchaseOrderDetail;
	}

	public void setPurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail) {
		this.purchaseOrderDetail = purchaseOrderDetail;
	}

	public String getPurchaseOrderPricingTypeCode() {
		return purchaseOrderPricingTypeCode;
	}

	public void setPurchaseOrderPricingTypeCode(String purchaseOrderPricingTypeCode) {
		this.purchaseOrderPricingTypeCode = purchaseOrderPricingTypeCode;
	}

	public String getPurchaseOrderPricingTypeName() {
		return purchaseOrderPricingTypeName;
	}

	public void setPurchaseOrderPricingTypeName(String purchaseOrderPricingTypeName) {
		this.purchaseOrderPricingTypeName = purchaseOrderPricingTypeName;
	}

	public BigDecimal getPricingQty() {
		return pricingQty;
	}

	public void setPricingQty(BigDecimal pricingQty) {
		this.pricingQty = pricingQty;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getPurchaseOrderPricingRowId() {
		return purchaseOrderPricingRowId;
	}

	public void setPurchaseOrderPricingRowId(Integer purchaseOrderPricingRowId) {
		this.purchaseOrderPricingRowId = purchaseOrderPricingRowId;
	}

	public String getIsEditPrice() {
		return isEditPrice;
	}

	public void setIsEditPrice(String isEditPrice) {
		this.isEditPrice = isEditPrice;
	}

	public Long getPriceUnit() {
		return priceUnit;
	}

	public void setPriceUnit(Long priceUnit) {
		this.priceUnit = priceUnit;
	}

	public String getCurType() {
		return curType;
	}

	public void setCurType(String curType) {
		this.curType = curType;
	}

	public Long getRowIds() {
		return rowIds;
	}

	public void setRowIds(Long rowIds) {
		this.rowIds = rowIds;
	}

}
