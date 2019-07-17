/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huiju.srm.purchasing.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.ws.utils.Key;

/**
 * <pre>
 * 
 * ebeln      采购订单                  purchaseOrderNo          SAP采购订单号
 * ebelp      采购订单行项目            sapPurchaseOrderItem     采购细单行项目号
 * mjahr      物料凭证年度              materialCertificateYear      物料凭证年度
 * mblnr      物料凭证                  materialCertificateCode      物料凭证编号
 * zeile      物料凭证行项目            materialCertificateItem        物料凭证中的项目
 * matnr      物料号                    materialCode             物料编码
 * werks      工厂                      plantCode                SAP工厂代码
 * bwart      移动类型                  acceptReturnFlag                     收、退货标识 
 * bldat      凭证日期                  certificateDate              凭证日期
 * budat      过账日期                  postingDate                 过账日期
 * menge      数量                      qtyReceive                      订单单位数量
 * meins      订单单位                  unitCode                     订单单位
 * peinh      PO定价单位数量               fixPriceQty              定价单位数量
 * bprme      订单价格单位              fixPriceUnitCode         定价单位
 * dmbtr      按本位币计的金额          amountMoney              凭证货币金额(未含税)
 * waers      币种                      currencyCode             币种
 * mwskz      税码                      taxCode                  税代码
 * insmk      库存类型                  stockType                库存类型
 *            送货单明细行ID            shoppingNoticeDetailId   送货单明细行ID
 * sobkz      特殊库存标识              specialWhseFlag          特殊库存标识
 * lgort      库存地                    storeLocalCode           库存地点
 * lifnr      供应商或债权人的帐号      vendorBusinessCode       SAP供应商代码
 * ekorg      采购组织                  businessCode             SAP采购组织代码
 * MENGE      库存单位数量              stockQty                 库存单位数量
 * MEINS      库存单位                  stockUnit                库存单位
 * SJAHR      物料凭证年度-冲销         omaterialCertificateYear   原物料凭证年度
 * SMBLN      物料凭证编号-冲销         omaterialCertificateCode   原物料凭证编号
 * SMBLP      物料凭证行项目-冲销       omaterialCertificateItem     原物料凭证中的项目
 * prueflos   检验批编号                                         检验批编号
 * charg      入库批次                  enterWarehouseBatch      入库批次
 * ekgrp      采购组                  purchasingGroupCode      SAP采购组编码
 * 
 * </pre>
 * 
 * @author wuxii@foxmail.com
 */
public class ReceivingNoteDto implements Serializable {

	private static final long serialVersionUID = 248156217607779720L;
	@Key(name = "采购订单号")
	@Length(max = 50, message = "采购订单号长度超出限制{max}")
	private String purchaseOrderNo;// SAP采购订单号

	/**
	 * 应该改为rowId对应sap的采购订单明细行的行号
	 */
	@Key(name = "采购订单行号")
	private Long rowId;// SAP采购细单行项目号

	@NotBlank(message = "物料凭证年度不能为空")
	// @FormatDate(pattern = "yyyy", message = "物料凭证年度格式不正确,正确格式[yyyy]")
	@Length(max = 4, message = "物料凭证年度长度超出限制{max}")
	private String materialCertificateYear;// 物料凭证年度

	@NotBlank(message = "物料凭证编号不能为空")
	@Length(max = 50, message = "物料凭证编号长度超出限制{max}")
	private String materialCertificateCode;// 物料凭证编号

	@NotNull(message = "物料凭证中的项目不能为空")
	private String materialCertificateItem;// 物料凭证中的项目

	@NotBlank(message = "物料编码不能为空")
	@Length(max = 50, message = "物料编码长度超出限制{max}")
	private String materialCode;// 物料编码

	@NotBlank(message = "SAP工厂代码不能为空")
	@Length(max = 50, message = "SAP工厂代码长度超出限制{max}")
	private String plantCode;// SAP工厂代码

	@NotNull(message = "收、退货标识不能为空")
	private Long acceptReturnFlag;// 收、退货标识

	@NotBlank(message = "凭证日期不能为空")
	// @FormatDate(pattern = "yyyyMMdd", message = "凭证日期格式不正确,正确格式[yyyyMMdd]")
	private String certificateDate;// 凭证日期 格式yyyyMMdd

	@NotBlank(message = "过账日期不能为空")
	// @FormatDate(pattern = "yyyyMMdd", message = "过账日期格式不正确,正确格式[yyyyMMdd]")
	private String postingDate;// 过账日期 yyyyMMdd

	@NotNull(message = "订单单位数量不能为空")
	private BigDecimal qtyReceive;

	@NotBlank(message = "订单单位不能为空")
	@Length(max = 3, message = "订单单位长度超出限制{max}")
	private String unitCode;// 订单单位

	/**
	 * PO定价单位数量
	 */
	@NotNull(message = "PO定价单位数量不能为空")
	private BigDecimal fixPriceQty;

	@NotBlank(message = "定价单位不能为空")
	@Length(max = 3, message = "定价单位长度超出限制{max}")
	private String fixPriceUnitCode;// 定价单位

	@NotNull(message = "凭证货币金额不能为空")
	private BigDecimal amountMoney;// 凭证货币金额(未含税)

	@NotBlank(message = "币种不能为空")
	@Length(max = 5, message = "币种长度超出限制{max}")
	private String currencyCode;// 币种

	@NotBlank(message = "税代码不能为空")
	@Length(max = 2, message = "税代码长度超出限制{max}")
	private String taxCode;// 税代码

	private String stockType;// 库存类型

	private Long shoppingNoticeDetailId;// 送货单明细行ID

	@Length(max = 1, message = "特殊库存标识长度超出限制{max}")
	private String specialWhseFlag;// 特殊库存标识

	@Length(max = 50, message = "库存地点长度超出限制{max}")
	private String storeLocalCode;// 库存地点

	@NotBlank(message = "SAP供应商代码不能为空")
	@Length(max = 50, message = "SAP供应商代码长度超出限制{max}")
	private String vendorErpCode;// SAP供应商代码

	@NotBlank(message = "SAP采购组织代码不能为空")
	@Length(max = 50, message = "SAP采购组织代码长度超出限制{max}")
	private String businessCode;// SAP采购组织代码

	// @FormatDate(pattern = "yyyy", message = "原物料凭证年度格式不正确,正确格式[yyyy]")
	@Length(max = 4, message = "原物料凭证年度次长度超出限制{max}")
	private String omaterialCertificateYear;// 原物料凭证年度

	@Length(max = 50, message = "原物料凭证编号长度超出限制{max}")
	private String omaterialCertificateCode;// 原物料凭证编号

	private String omaterialCertificateItem;// 原物料凭证中的项目

	@Length(max = 10, message = "SAP采购组编码长度超出限制{max}")
	private String purchasingGroupCode;// SAP 采购组编码

	@Length(max = 2, message = "SAPGR-Bsd IV长度超出限制{max}")
	private String rgBsd;// SAP GR-Bsd IV标识 用于判断预制发票同步sap时是否合并明细

	@NotNull(message = "SKU数量不能为空")
	private BigDecimal skuQty;// SKU数量‘

	@NotBlank(message = "SKU单位不能为空")
	@Length(max = 20, message = "SKU单位长度超出限制{max}")
	private String skuUnitCode;// SKU单位

	@Length(max = 50, message = "检验批编号  长度超出限制{max}")
	private String manualCheckBatch;// 检验批编号

	@Length(max = 10, message = "入库批次长度超出限制{max}")
	private String enterWarehouseBatch;// 入库批次

	@Length(max = 20, message = "供应商批次长度超出限制{max}")
	private String vendorBatch; // 供应商批次

	public String getManualCheckBatch() {
		return manualCheckBatch;
	}

	public void setManualCheckBatch(String manualCheckBatch) {
		this.manualCheckBatch = manualCheckBatch;
	}

	public String getEnterWarehouseBatch() {
		return enterWarehouseBatch;
	}

	public void setEnterWarehouseBatch(String enterWarehouseBatch) {
		this.enterWarehouseBatch = enterWarehouseBatch;
	}

	public String getVendorBatch() {
		return vendorBatch;
	}

	public void setVendorBatch(String vendorBatch) {
		this.vendorBatch = vendorBatch;
	}

	/**
	 * 去零方法
	 */
	public void removeZero() {
		this.materialCode = removeZero(this.materialCode);
		this.vendorErpCode = removeZero(this.vendorErpCode);
	}

	/**
	 * 去零
	 * 
	 * @return 返回去零后的值
	 */
	private String removeZero(String sourceData) {
		if (StringUtils.isBlank(sourceData)) {
			return sourceData;
		}

		boolean flag = true;
		while (flag) {
			flag = sourceData.startsWith("0");
			if (flag) {
				sourceData = sourceData.substring(1, sourceData.length());
			}
		}

		return sourceData;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}

	public Long getRowId() {
		return rowId;
	}

	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}

	public String getMaterialCertificateYear() {
		return materialCertificateYear;
	}

	public void setMaterialCertificateYear(String materialCertificateYear) {
		this.materialCertificateYear = materialCertificateYear;
	}

	public String getMaterialCertificateCode() {
		return materialCertificateCode;
	}

	public void setMaterialCertificateCode(String materialCertificateCode) {
		this.materialCertificateCode = materialCertificateCode;
	}

	public String getMaterialCode() {
		return materialCode;
	}

	public void setMaterialCode(String materialCode) {
		this.materialCode = materialCode;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public Long getAcceptReturnFlag() {
		return acceptReturnFlag;
	}

	public void setAcceptReturnFlag(Long acceptReturnFlag) {
		this.acceptReturnFlag = acceptReturnFlag;
	}

	public String getCertificateDate() {
		return certificateDate;
	}

	public void setCertificateDate(String certificateDate) {
		this.certificateDate = certificateDate;
	}

	public String getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getFixPriceUnitCode() {
		return fixPriceUnitCode;
	}

	public void setFixPriceUnitCode(String fixPriceUnitCode) {
		this.fixPriceUnitCode = fixPriceUnitCode;
	}

	public BigDecimal getAmountMoney() {
		return amountMoney;
	}

	public void setAmountMoney(BigDecimal amountMoney) {
		this.amountMoney = amountMoney;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public Long getShoppingNoticeDetailId() {
		return shoppingNoticeDetailId;
	}

	public void setShoppingNoticeDetailId(Long shoppingNoticeDetailId) {
		this.shoppingNoticeDetailId = shoppingNoticeDetailId;
	}

	public String getSpecialWhseFlag() {
		return specialWhseFlag;
	}

	public void setSpecialWhseFlag(String specialWhseFlag) {
		this.specialWhseFlag = specialWhseFlag;
	}

	public String getStoreLocalCode() {
		return storeLocalCode;
	}

	public void setStoreLocalCode(String storeLocalCode) {
		this.storeLocalCode = storeLocalCode;
	}

	public String getVendorErpCode() {
		return vendorErpCode;
	}

	public void setVendorErpCode(String vendorErpCode) {
		this.vendorErpCode = vendorErpCode;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getOmaterialCertificateYear() {
		return omaterialCertificateYear;
	}

	public void setOmaterialCertificateYear(String omaterialCertificateYear) {
		this.omaterialCertificateYear = omaterialCertificateYear;
	}

	public String getOmaterialCertificateCode() {
		return omaterialCertificateCode;
	}

	public void setOmaterialCertificateCode(String omaterialCertificateCode) {
		this.omaterialCertificateCode = omaterialCertificateCode;
	}

	public String getMaterialCertificateItem() {
		return materialCertificateItem;
	}

	public void setMaterialCertificateItem(String materialCertificateItem) {
		this.materialCertificateItem = materialCertificateItem;
	}

	public String getOmaterialCertificateItem() {
		return omaterialCertificateItem;
	}

	public void setOmaterialCertificateItem(String omaterialCertificateItem) {
		this.omaterialCertificateItem = omaterialCertificateItem;
	}

	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}

	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public BigDecimal getFixPriceQty() {
		return fixPriceQty;
	}

	public void setFixPriceQty(BigDecimal fixPriceQty) {
		this.fixPriceQty = fixPriceQty;
	}

	public String getPurchasingGroupCode() {
		return purchasingGroupCode;
	}

	public void setPurchasingGroupCode(String purchasingGroupCode) {
		this.purchasingGroupCode = purchasingGroupCode;
	}

	public String getRgBsd() {
		return rgBsd;
	}

	public void setRgBsd(String rgBsd) {
		this.rgBsd = rgBsd;
	}

	public BigDecimal getSkuQty() {
		return skuQty;
	}

	public void setSkuQty(BigDecimal skuQty) {
		this.skuQty = skuQty;
	}

	public String getSkuUnitCode() {
		return skuUnitCode;
	}

	public void setSkuUnitCode(String skuUnitCode) {
		this.skuUnitCode = skuUnitCode;
	}

	@Override
	public String toString() {
		return "ReceivingNoteDto [purchaseOrderNo=" + purchaseOrderNo + ", rowId=" + rowId + ", materialCertificateYear="
				+ materialCertificateYear + ", materialCertificateCode=" + materialCertificateCode + ", materialCertificateItem="
				+ materialCertificateItem + ", materialCode=" + materialCode + ", plantCode=" + plantCode + ", acceptReturnFlag="
				+ acceptReturnFlag + ", certificateDate=" + certificateDate + ", postingDate=" + postingDate + ", qtyReceive=" + qtyReceive
				+ ", unitCode=" + unitCode + ", fixPriceQty=" + fixPriceQty + ", fixPriceUnitCode=" + fixPriceUnitCode + ", amountMoney="
				+ amountMoney + ", currencyCode=" + currencyCode + ", taxCode=" + taxCode + ", stockType=" + stockType
				+ ", shoppingNoticeDetailId=" + shoppingNoticeDetailId + ", specialWhseFlag=" + specialWhseFlag + ", storeLocalCode="
				+ storeLocalCode + ", vendorErpCode=" + vendorErpCode + ", businessCode=" + businessCode + ", omaterialCertificateYear="
				+ omaterialCertificateYear + ", omaterialCertificateCode=" + omaterialCertificateCode + ", omaterialCertificateItem="
				+ omaterialCertificateItem + ", purchasingGroupCode=" + purchasingGroupCode + ", rgBsd=" + rgBsd + ", skuQty=" + skuQty
				+ ", skuUnitCode=" + skuUnitCode + "]";
	}

}
