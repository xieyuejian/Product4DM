package com.huiju.srm.purchasing.entity;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 收货单 entity
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdReceivingNote extends BaseEntity<Long> implements Cloneable {

	private static final long serialVersionUID = 3075133691557657227L;

	/** 收货单ID */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ReceivingNote_PK")
	@TableGenerator(name = "ReceivingNote_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "ReceivingNote_PK", allocationSize = 1)
	@Column(name = "grnId")
	protected Long receivingNoteId;
	/** 收货单编号 */
	@Column(name = "grnNo")

	protected String receivingNoteNo;
	/** 客户端编号 */
	@Column(name = "clientCode")
	protected String clientCode;

	/** 送货单编号 */
	@Column(name = "shoppingNoticeNo")
	protected String shoppingNoticeNo;

	/** 送货单细单ID */
	@Column(name = "shoppingNoticeDetailId")
	protected Long shoppingNoticeDetailId;

	/** 送货明细行号 */
	@Column(name = "shoppingNoticeRowId")
	protected Long shoppingNoticeRowId;

	/** 采购订单号 */
	@Column(name = "purchaseOrderNo")
	protected String purchaseOrderNo;

	/** 采购订单行ID（采购订单行项目 */
	@Column(name = "purchaseOrderDetailId")
	protected Long purchaseOrderDetailId;

	/** 采购组织编码/对账单明细用到的采购组织编码sap系统中的代码 */
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;

	/** 采购组织名称 */
	@Column(name = "purchasingOrgName")
	protected String purchasingOrgName;

	/** 采购组编码 */
	@Column(name = "purchasingGroupCode")
	protected String purchasingGroupCode;

	/** 采购组名称 */
	@Column(name = "purchasingGroupName")
	protected String purchasingGroupName;

	/** 供应商代码对账单明细用到的供应商编码 */
	@Column(name = "vendorCode")
	protected String vendorCode;

	/** 供应商名称 */
	@Column(name = "vendorName")
	protected String vendorName;

	/** 供应商sap业务代码 */
	@Column(name = "vendorErpCode")
	protected String vendorErpCode;

	/** 物料Id */
	@Column(name = "materialId")
	protected Long materialId;

	/** 物料编码 */
	@Column(name = "materialCode")
	protected String materialCode;

	/** 物料名称 */
	@Column(name = "materialName")
	protected String materialName;

	/** 数量 对账单明细用到的数量 订单单位数量 */
	@Column(name = "quantity")
	protected BigDecimal qtyReceive;

	/** 单位编码 对账单明细用到的单位 订单单位 */
	@Column(name = "unitCode")
	protected String unitCode;

	/** 收退货标识101:收货102:退货 */
	@Column(name = "acceptReturnFlag")
	protected Long acceptReturnFlag;

	/** 物料凭证年度 */
	@Column(name = "materialCertificateYear")
	protected String materialCertificateYear;

	/** 物料凭证编号 */
	@Column(name = "materialCertificateCode")
	protected String materialCertificateCode;

	/** 物料凭证中的项目 */
	@Column(name = "materialCertificateItem")
	protected String materialCertificateItem;

	/** 物料凭证信息（凭证年度-凭证编码-凭证行号） */
	@Column(name = "materialCertificate")
	protected String materialCertificate;

	/** 金额（未含税）对应对账单明细的产品未税金额 */
	@Column(name = "amountnoTax")
	protected BigDecimal amountnoTax;

	/** 货币码 */
	@Column(name = "currencyCode")
	protected String currencyCode;

	/** 税代码 */
	@Column(name = "taxCode")
	protected String taxCode;

	/** 税率 */
	@Column(name = "taxRate")
	protected BigDecimal taxRate;

	/** 特殊库存标志(分包:O/寄售:K/标准:S;) */
	@Column(name = "specialwhseFlag")
	protected String specialwhseFlag;

	/** 凭证日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "certificateDate")
	protected Calendar certificateDate;

	/** 过帐日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "postingDate")
	protected Calendar postingDate;

	/** sap工厂编码 */
	@Column(name = "plantCode")
	protected String plantCode;

	/** sap采购订单号 对账单明细用到的采购订单号 */
	@Column(name = "erpPurchaseOrderNo")
	protected String erpPurchaseOrderNo;

	/** SAP采购订单行项目 对账单明细用到的采购订单行号 */
	@Column(name = "purchaseOrderItem")
	protected String purchaseOrderItem;

	/** 价格 */
	@Column(name = "price")
	protected BigDecimal price;

	/** 含税总金额 */
	@Column(name = "totalAmountAndTax")
	protected BigDecimal totalAmountAndTax;

	/** 总税额 */
	@Column(name = "totalTax")
	protected BigDecimal totalTax;

	/** 库存类型 A非限制/X质检/S冻结 */
	@Column(name = "stockType")
	protected String stockType;

	/** PO定价单位数量 */
	@Column(name = "fixPriceQty")
	protected BigDecimal fixPriceQty;

	/** PO定价单位 */
	@Column(name = "fixPriceUnitCode")
	protected String fixPriceUnitCode;

	/** 库存地编码 */
	@Column(name = "storeLocalCode")
	protected String storeLocalCode;

	/** 可对账数量（定价单位） */
	@Column(name = "invoiceQty")
	protected BigDecimal invoiceQty;

	/** 可对账数量（订单单位） */
	@Column(name = "reconciliableQty")
	protected BigDecimal reconciliableQty;

	/** 库存单位数量 */
	@Column(name = "stockQty")
	protected BigDecimal stockQty;

	/** 库存单位 */
	@Column(name = "stockUnit")
	protected String stockUnit;

	/** 订单单位已冲销量 */
	@Column(name = "orderUnitQty")
	protected BigDecimal orderUnitQty = BigDecimal.ZERO;
	/** 定价单位已冲销量 */
	@Column(name = "pricingUnitQty")
	protected BigDecimal pricingUnitQty = BigDecimal.ZERO;

	/** 原物料凭证年度 */
	@Column(name = "omaterialCertificateYear")
	protected String omaterialCertificateYear;

	/** 原物料凭证编号 */
	@Column(name = "omaterialCertificateCode")
	protected String omaterialCertificateCode;

	/** 原物料凭证行号 */
	@Column(name = "omaterialCertificateItem")
	protected String omaterialCertificateItem;

	/** 可冲销数量(订单单位) */
	@Column(name = "canChargeOffNum")
	protected BigDecimal canChargeOffNum;

	/** 定价/订单单位转换系数 */
	@Column(name = "exchangeRate")
	protected BigDecimal exchangeRate;

	/** 来源于：0来源于SRM1，来源于SAP */
	@Column(name = "origin")
	protected Integer origin;

	/** 质检状态 */
	@Column(name = "status")
	protected CensorQualityState status;

	/** SAP GR-Bsd IV标识 用于判断预制发票同步sap时是否合并明细 "X"是空否 */
	@Column(name = "rgBsd")
	protected String rgBsd;

	/** 未对账：0，已对账：1 */
	@Column(name = "receiptBillFlag")
	protected Integer receiptBillFlag;

	/** 已开票,未开票 */
	@Column(name = "invoiceFlag")
	protected Integer invoiceFlag;

	public Long getReceivingNoteId() {
		return receivingNoteId;
	}

	public void setReceivingNoteId(Long receivingNoteId) {
		this.receivingNoteId = receivingNoteId;
	}

	public String getReceivingNoteNo() {
		return receivingNoteNo;
	}

	public void setReceivingNoteNo(String receivingNoteNo) {
		this.receivingNoteNo = receivingNoteNo;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getShoppingNoticeNo() {
		return shoppingNoticeNo;
	}

	public void setShoppingNoticeNo(String shoppingNoticeNo) {
		this.shoppingNoticeNo = shoppingNoticeNo;
	}

	public Long getShoppingNoticeDetailId() {
		return shoppingNoticeDetailId;
	}

	public void setShoppingNoticeDetailId(Long shoppingNoticeDetailId) {
		this.shoppingNoticeDetailId = shoppingNoticeDetailId;
	}

	public Long getShoppingNoticeRowId() {
		return shoppingNoticeRowId;
	}

	public void setShoppingNoticeRowId(Long shoppingNoticeRowId) {
		this.shoppingNoticeRowId = shoppingNoticeRowId;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
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

	public String getPurchasingOrgName() {
		return purchasingOrgName;
	}

	public void setPurchasingOrgName(String purchasingOrgName) {
		this.purchasingOrgName = purchasingOrgName;
	}

	public String getPurchasingGroupCode() {
		return purchasingGroupCode;
	}

	public void setPurchasingGroupCode(String purchasingGroupCode) {
		this.purchasingGroupCode = purchasingGroupCode;
	}

	public String getPurchasingGroupName() {
		return purchasingGroupName;
	}

	public void setPurchasingGroupName(String purchasingGroupName) {
		this.purchasingGroupName = purchasingGroupName;
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

	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}

	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public Long getAcceptReturnFlag() {
		return acceptReturnFlag;
	}

	public void setAcceptReturnFlag(Long acceptReturnFlag) {
		this.acceptReturnFlag = acceptReturnFlag;
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

	public String getMaterialCertificateItem() {
		return materialCertificateItem;
	}

	public void setMaterialCertificateItem(String materialCertificateItem) {
		this.materialCertificateItem = materialCertificateItem;
	}

	public BigDecimal getAmountnoTax() {
		return amountnoTax;
	}

	public void setAmountnoTax(BigDecimal amountnoTax) {
		this.amountnoTax = amountnoTax;
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

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public String getSpecialwhseFlag() {
		return specialwhseFlag;
	}

	public void setSpecialwhseFlag(String specialwhseFlag) {
		this.specialwhseFlag = specialwhseFlag;
	}

	public Calendar getCertificateDate() {
		return certificateDate;
	}

	public void setCertificateDate(Calendar certificateDate) {
		this.certificateDate = certificateDate;
	}

	public Calendar getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Calendar postingDate) {
		this.postingDate = postingDate;
	}

	public String getErpPurchaseOrderNo() {
		return erpPurchaseOrderNo;
	}

	public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
		this.erpPurchaseOrderNo = erpPurchaseOrderNo;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getTotalAmountAndTax() {
		return totalAmountAndTax;
	}

	public void setTotalAmountAndTax(BigDecimal totalAmountAndTax) {
		this.totalAmountAndTax = totalAmountAndTax;
	}

	public BigDecimal getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public BigDecimal getFixPriceQty() {
		return fixPriceQty;
	}

	public void setFixPriceQty(BigDecimal fixPriceQty) {
		this.fixPriceQty = fixPriceQty;
	}

	public String getFixPriceUnitCode() {
		return fixPriceUnitCode;
	}

	public void setFixPriceUnitCode(String fixPriceUnitCode) {
		this.fixPriceUnitCode = fixPriceUnitCode;
	}

	public String getStoreLocalCode() {
		return storeLocalCode;
	}

	public void setStoreLocalCode(String storeLocalCode) {
		this.storeLocalCode = storeLocalCode;
	}

	public BigDecimal getInvoiceQty() {
		return invoiceQty;
	}

	public void setInvoiceQty(BigDecimal invoiceQty) {
		this.invoiceQty = invoiceQty;
	}

	public BigDecimal getStockQty() {
		return stockQty;
	}

	public void setStockQty(BigDecimal stockQty) {
		this.stockQty = stockQty;
	}

	public String getStockUnit() {
		return stockUnit;
	}

	public void setStockUnit(String stockUnit) {
		this.stockUnit = stockUnit;
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

	public String getOmaterialCertificateItem() {
		return omaterialCertificateItem;
	}

	public void setOmaterialCertificateItem(String omaterialCertificateItem) {
		this.omaterialCertificateItem = omaterialCertificateItem;
	}

	public String getVendorErpCode() {
		return vendorErpCode;
	}

	public void setVendorErpCode(String vendorErpCode) {
		this.vendorErpCode = vendorErpCode;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public StdReceivingNote() {

	}

	public StdReceivingNote(Long receivingNoteId) {
		this.receivingNoteId = receivingNoteId;
	}

	public String getRgBsd() {
		return rgBsd;
	}

	public void setRgBsd(String rgBsd) {
		this.rgBsd = rgBsd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receivingNoteId == null) ? 0 : receivingNoteId.hashCode());
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
		StdReceivingNote other = (StdReceivingNote) obj;
		if (receivingNoteId == null) {
			if (other.receivingNoteId != null)
				return false;
		} else if (!receivingNoteId.equals(other.receivingNoteId))
			return false;
		return true;
	}

	@Override
	public Object clone() {
		try {
			return super.clone(); // call protected method
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public BigDecimal getCanChargeOffNum() {
		return canChargeOffNum;
	}

	public void setCanChargeOffNum(BigDecimal canChargeOffNum) {
		this.canChargeOffNum = canChargeOffNum;
	}

	public Integer getOrigin() {
		return origin;
	}

	public void setOrigin(Integer origin) {
		this.origin = origin;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getReconciliableQty() {
		return reconciliableQty;
	}

	public void setReconciliableQty(BigDecimal reconciliableQty) {
		this.reconciliableQty = reconciliableQty;
	}

	public BigDecimal getOrderUnitQty() {
		return orderUnitQty;
	}

	public void setOrderUnitQty(BigDecimal orderUnitQty) {
		this.orderUnitQty = orderUnitQty;
	}

	public BigDecimal getPricingUnitQty() {
		return pricingUnitQty;
	}

	public void setPricingUnitQty(BigDecimal pricingUnitQty) {
		this.pricingUnitQty = pricingUnitQty;
	}

	public String getMaterialCertificate() {
		return materialCertificate;
	}

	public void setMaterialCertificate(String materialCertificate) {
		this.materialCertificate = materialCertificate;
	}

	public String getPurchaseOrderItem() {
		return purchaseOrderItem;
	}

	public void setPurchaseOrderItem(String purchaseOrderItem) {
		this.purchaseOrderItem = purchaseOrderItem;
	}

	public CensorQualityState getStatus() {
		return status;
	}

	public void setStatus(CensorQualityState status) {
		this.status = status;
	}

	public Integer getReceiptBillFlag() {
		return receiptBillFlag;
	}

	public void setReceiptBillFlag(Integer receiptBillFlag) {
		this.receiptBillFlag = receiptBillFlag;
	}

	public Integer getInvoiceFlag() {
		return invoiceFlag;
	}

	public void setInvoiceFlag(Integer invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}

}
