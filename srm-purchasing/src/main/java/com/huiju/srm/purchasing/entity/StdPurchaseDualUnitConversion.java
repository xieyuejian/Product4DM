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

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 采购订单细单双单位转换关系类
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdPurchaseDualUnitConversion extends BaseEntity<Long> {
	private static final long serialVersionUID = 5573852609277762940L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PurchaseDualUnitConversion_PK")
	@TableGenerator(name = "PurchaseDualUnitConversion_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "PurchaseDualUnitConversion_PK", allocationSize = 10)
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "purchaseDualUnitConversion_seq")
	// @SequenceGenerator(name = "purchaseDualUnitConversion_seq", sequenceName
	// = "seq_purchaseDualUnitConversion", initialValue = 0, allocationSize = 1)
	/** 双单位转换id */
	@Column(name = "purchaseOrderQtyId")
	protected Long purchaseOrderQtyId;

	/** 订单明细对象 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "purchaseOrderDetailId", referencedColumnName = "purchaseOrderDetailId")
	protected PurchaseOrderDetail purchaseOrderDetail;

	/** 转换分子1-->订单单位 */
	@Column(name = "convertMolecular")
	protected BigDecimal convertMolecular;

	/** 转换分母1-->基本单位（SKU） */
	@Column(name = "convertDenominator")
	protected BigDecimal convertDenominator;

	/** 采购订单单位 */
	@Column(name = "orderDetailUnit")
	protected String orderDetailUnit;

	/** 订单单位 */
	@Column(name = "orderDetailUnit2")
	protected String orderDetailUnit2;

	/** 基本单位 */
	@Column(name = "unitCode")
	protected String unitCode;

	/** 转换分子2--->订单单位 */
	@Column(name = "convertMolecular2")
	protected BigDecimal convertMolecular2;

	/** 转换分母2---->定价单位 */
	@Column(name = "convertDenominator2")
	protected BigDecimal convertDenominator2;

	/** 订单定价单位 */
	@Column(name = "pricingUnit")
	protected String pricingUnit;

	/** 订单定价单位数量 */
	@Column(name = "pricingQty")
	protected BigDecimal pricingQty;

	/** PO QTY IN SKU */
	@Column(name = "skuQty")
	protected BigDecimal skuQty;

	public Long getPurchaseOrderQtyId() {
		return purchaseOrderQtyId;
	}

	public void setPurchaseOrderQtyId(Long purchaseOrderQtyId) {
		this.purchaseOrderQtyId = purchaseOrderQtyId;
	}

	public PurchaseOrderDetail getPurchaseOrderDetail() {
		return purchaseOrderDetail;
	}

	public void setPurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail) {
		this.purchaseOrderDetail = purchaseOrderDetail;
	}

	public BigDecimal getConvertMolecular() {
		return convertMolecular;
	}

	public void setConvertMolecular(BigDecimal convertMolecular) {
		this.convertMolecular = convertMolecular;
	}

	public String getOrderDetailUnit() {
		return orderDetailUnit;
	}

	public void setOrderDetailUnit(String orderDetailUnit) {
		this.orderDetailUnit = orderDetailUnit;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public BigDecimal getConvertMolecular2() {
		return convertMolecular2;
	}

	public void setConvertMolecular2(BigDecimal convertMolecular2) {
		this.convertMolecular2 = convertMolecular2;
	}

	public BigDecimal getConvertDenominator2() {
		return convertDenominator2;
	}

	public void setConvertDenominator2(BigDecimal convertDenominator2) {
		this.convertDenominator2 = convertDenominator2;
	}

	public String getPricingUnit() {
		return pricingUnit;
	}

	public void setPricingUnit(String pricingUnit) {
		this.pricingUnit = pricingUnit;
	}

	public BigDecimal getPricingQty() {
		return pricingQty;
	}

	public void setPricingQty(BigDecimal pricingQty) {
		this.pricingQty = pricingQty;
	}

	public BigDecimal getSkuQty() {
		return skuQty;
	}

	public void setSkuQty(BigDecimal skuQty) {
		this.skuQty = skuQty;
	}

	public BigDecimal getConvertDenominator() {
		return convertDenominator;
	}

	public void setConvertDenominator(BigDecimal convertDenominator) {
		this.convertDenominator = convertDenominator;
	}

	public String getOrderDetailUnit2() {
		return orderDetailUnit2;
	}

	public void setOrderDetailUnit2(String orderDetailUnit2) {
		this.orderDetailUnit2 = orderDetailUnit2;
	}
}
