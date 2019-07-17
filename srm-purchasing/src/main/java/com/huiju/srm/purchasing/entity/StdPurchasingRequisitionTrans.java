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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * <pre>采购申请转单</pre>
 * @author bairx
 * @version 1.0 时间 2019/3/30
 */
@MappedSuperclass
public class StdPurchasingRequisitionTrans extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/**采购申请转单id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PurchasingRequisitionTrans_PK")
    @TableGenerator(name = "PurchasingRequisitionTrans_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "PurchasingRequisitionTrans_PK",
    allocationSize = 1)
	@Column(name = "purchaseRequisitionTransId")
    @NotFound(action=NotFoundAction.IGNORE)
	protected Long purchaseRequisitionTransId;
	/**采购申请单号*/
	@Column(name = "purchasingRequisitionNo")
	protected String purchasingRequisitionNo;
	/**采购组织编码*/
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;
	/**采购组织名称*/
	@Column(name = "purchasingOrgName")
	protected String purchasingOrgName;
	/**供应商编码*/
	@Column(name = "vendorCode")
	protected String vendorCode;
	/**供应商erp编码*/
	@Column(name = "vendorErpCode")
	protected String vendorErpCode;
	/**供应商名称*/
	@Column(name = "vendorName")
	protected String vendorName;
	/**公司编码*/
	@Column(name = "companyCode")
	protected String companyCode;
	/**公司名称*/
	@Column(name = "companyName")
	protected String companyName;
	/**采购类别编码*/
	@Column(name = "purchaseType")
	protected String purchaseType;
	/**采购类别名称*/
	@Column(name = "purchaseTypeName")
	protected String purchaseTypeName;
	/**可转单数量*/
	@Column(name = "transferQuantity")
	protected BigDecimal transferQuantity;
	/**已分配数量*/
	@Column(name = "assignedQuantity")
	protected BigDecimal assignedQuantity;
	/**价格*/
	@Column(name = "price")
	protected BigDecimal price;
	/**是否已转单*/
	@Column(name = "isTransfered")
	protected String isTransfered;
	/**税率编码*/
	@Column(name = "taxrateCode")
	protected String taxrateCode;
	/**税率值*/
	@Column(name = "taxrateValue")
	protected BigDecimal taxrateValue;
	/**采购申请明细归集*/
	@ManyToOne
	@JoinColumn(name="purchasingRequisitionColId", referencedColumnName="purchasingRequisitionColId")
    @NotFound(action=NotFoundAction.IGNORE)
	protected PurchasingRequisitionCollection purchasingRequisitionCollection;

	public String getPurchasingRequisitionNo() {
		return purchasingRequisitionNo;
	}
	public void setPurchasingRequisitionNo(String purchasingRequisitionNo) {
		this.purchasingRequisitionNo = purchasingRequisitionNo;
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
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getPurchaseType() {
		return purchaseType;
	}
	public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
	}
	public String getPurchaseTypeName() {
		return purchaseTypeName;
	}
	public void setPurchaseTypeName(String purchaseTypeName) {
		this.purchaseTypeName = purchaseTypeName;
	}
	public BigDecimal getTransferQuantity() {
		return transferQuantity;
	}
	public void setTransferQuantity(BigDecimal transferQuantity) {
		this.transferQuantity = transferQuantity;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getIsTransfered() {
		return isTransfered;
	}
	public void setIsTransfered(String isTransfered) {
		this.isTransfered = isTransfered;
	}
	public PurchasingRequisitionCollection getPurchasingRequisitionCollection() {
		return purchasingRequisitionCollection;
	}
	public void setPurchasingRequisitionCollection(PurchasingRequisitionCollection purchasingRequisitionCollection) {
		this.purchasingRequisitionCollection = purchasingRequisitionCollection;
	}
	public Long getPurchaseRequisitionTransId() {
		return purchaseRequisitionTransId;
	}
	public void setPurchaseRequisitionTransId(Long purchaseRequisitionTransId) {
		this.purchaseRequisitionTransId = purchaseRequisitionTransId;
	}
	public String getTaxrateCode() {
		return taxrateCode;
	}
	public void setTaxrateCode(String taxrateCode) {
		this.taxrateCode = taxrateCode;
	}
	public BigDecimal getTaxrateValue() {
		return taxrateValue;
	}
	public void setTaxrateValue(BigDecimal taxrateValue) {
		this.taxrateValue = taxrateValue;
	}
	public String getVendorErpCode() {
		return vendorErpCode;
	}
	public void setVendorErpCode(String vendorErpCode) {
		this.vendorErpCode = vendorErpCode;
	}
	public BigDecimal getAssignedQuantity() {
		return assignedQuantity;
	}
	public void setAssignedQuantity(BigDecimal assignedQuantity) {
		this.assignedQuantity = assignedQuantity;
	}
	
}
