package com.huiju.srm.purchasing.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 采购订单实体类 扩展类
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdPurchaseOrder extends BaseEntity<Long> {
	private static final long serialVersionUID = -8839593392625740350L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "purchaseorder_pk")
	@TableGenerator(name = "purchaseorder_pk", table = "s_pkgenerator", pkColumnName = "pkgeneratorname", valueColumnName = "pkgeneratorvalue", pkColumnValue = "purchaseorder_pk", allocationSize = 10)
	// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	// "purchaseOrder_seq")
	// @SequenceGenerator(name = "purchaseOrder_seq", sequenceName =
	// "seq_purchaseOrder", initialValue = 0, allocationSize = 1)

	/** ID */
	@Column(name = "purchaseOrderId")
	protected Long purchaseOrderId;

	/** SRM采购订单号 */
	@Column(name = "purchaseOrderNo")
	protected String purchaseOrderNo;

	/** 订单类型 */
	@Column(name = "purchaseOrderType")
	protected String purchaseOrderType;

	/** SAP采购订单号 */
	@Column(name = "erpPurchaseOrderNo")
	protected String erpPurchaseOrderNo;

	/** 订单时间 */
	@Column(name = "purchaseOrderTime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar purchaseOrderTime;
	
	/** 订单发布时间 */
	@Column(name = "orderReleaseTime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar orderReleaseTime;

	/** 采购组织编码 */
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;

	/** 采购组织名称 */
	@Column(name = "purchasingOrgName")
	protected String purchasingOrgName;

	/** 供应商编码 */
	@Column(name = "vendorCode")
	protected String vendorCode;

	/** 供应商名称 */
	@Column(name = "vendorName")
	protected String vendorName;

	/** 公司编码 */
	@Column(name = "companyCode")
	protected String companyCode;

	/** 公司名称 */
	@Column(name = "companyName")
	protected String companyName;

	/** 货币编码 */
	@Column(name = "currencyCode")
	protected String currencyCode;

	/** 货币名称 */
	@Column(name = "currencyName")
	protected String currencyName;

	/** 货币汇率 */
	@Column(name = "currencyRate")
	protected BigDecimal currencyRate;

	/** 订单金额 */
	@Column(name = "totalAmount")
	protected BigDecimal totalAmount;

	/** 附件ID */
	@Column(name = "uploadFileGroupId")
	protected Long uploadFileGroupId;

	/** 订单状态 */
	@Column(name = "purchaseOrderState")
	protected PurchaseOrderState purchaseOrderState;

	/** 审核状态 */
	@Column(name = "purchaseOrderFlowState")
	protected PurchaseOrderFlowState purchaseOrderFlowState;

	/** 流程状态 */
	@Column(name = "purchaseOrderCheckState")
	protected PurchaseOrderCheckState purchaseOrderCheckState;

	/** ERP同步标识,0未同步,1已同步 */
	@Column(name = "erpSynState")
	protected Integer erpSynState;

	/** 创建方式 */
	@Column(name = "createType")
	protected PurchaseOrderType createType;

	/** 供应商查看标识,标识供应商是否已经查看过采购订单 */
	@Column(name = "viewFlag")
	protected Integer viewFlag;

	/** ERP返回信息 */
	@Column(name = "erpReturnMsg")
	protected String erpReturnMsg;

	/** 先确认后审核标识：1是先确认，0是先审核 */
	@Column(name = "checkFirst")
	protected Integer checkFirst;

	/** 客户端编码 */
	@Column(name = "clientCode")
	protected String clientCode;

	/** 备注 */
	@Column(name = "remark")
	protected String remark;

	/** 采购组编码 */
	@Column(name = "purchasingGroupCode")
	protected String purchasingGroupCode;

	/** 采购组名称 */
	@Column(name = "purchasingGroupName")
	protected String purchasingGroupName;

	/** 供应商ERP编码 */
	@Column(name = "vendorErpCode")
	protected String vendorErpCode;

	/** 供应商是否具有查看金额权限 */
	@Column(name = "isVendorView")
	protected Integer isVendorView;

	/** 集合 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "purchaseOrder", orphanRemoval = true)
	protected List<PurchaseOrderDetail> purchaseOrderDetails;

	/** 是否是撤销审批的数据 0 不是，1是 */
	@Column(name = "isRevocationCheck")
	protected Integer isRevocationCheck;

	/** 1可以关闭，其他不行 */
	@Transient
	protected Integer closeHandleFlag;

	/** 供应商确认时间 */
	@Column(name = "vendorConfirmTime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar vendorConfirmTime;

	/** 国际贸易条件 */
	@Column(name = "internationlTradeTerm")
	protected String internationlTradeTerm;

	/** 国际贸易条件说明 */
	@Column(name = "internationlTradeRemark")
	protected String internationlTradeRemark;

	/** 税率编码 */
	@Column(name = "taxRateCode")
	protected String taxRateCode;

	/** 返回值，临时字段 */
	@Transient
	protected String returnValue;

	@Transient
	protected Boolean srmSyncSap;

	public Calendar getOrderReleaseTime() {
		return orderReleaseTime;
	}

	public void setOrderReleaseTime(Calendar orderReleaseTime) {
		this.orderReleaseTime = orderReleaseTime;
	}

	public PurchaseOrderType getCreateType() {
		return createType;
	}

	public void setCreateType(PurchaseOrderType createType) {
		this.createType = createType;
	}

	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}

	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
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

	public Long getUploadFileGroupId() {
		return uploadFileGroupId;
	}

	public void setUploadFileGroupId(Long uploadFileGroupId) {
		this.uploadFileGroupId = uploadFileGroupId;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public BigDecimal getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(BigDecimal currencyRate) {
		this.currencyRate = currencyRate;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getViewFlag() {
		return viewFlag;
	}

	public void setViewFlag(Integer viewFlag) {
		this.viewFlag = viewFlag;
	}

	public String getErpReturnMsg() {
		return erpReturnMsg;
	}

	public void setErpReturnMsg(String erpReturnMsg) {
		this.erpReturnMsg = erpReturnMsg;
	}

	public Integer getCheckFirst() {
		return checkFirst;
	}

	public void setCheckFirst(Integer checkFirst) {
		this.checkFirst = checkFirst;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<PurchaseOrderDetail> getPurchaseOrderDetails() {
		return purchaseOrderDetails;
	}

	public void setPurchaseOrderDetails(List<PurchaseOrderDetail> purchaseOrderDetails) {
		this.purchaseOrderDetails = purchaseOrderDetails;
	}

	public Integer getErpSynState() {
		return erpSynState;
	}

	public void setErpSynState(Integer erpSynState) {
		this.erpSynState = erpSynState;
	}

	public PurchaseOrderState getPurchaseOrderState() {
		return purchaseOrderState;
	}

	public void setPurchaseOrderState(PurchaseOrderState purchaseOrderState) {
		this.purchaseOrderState = purchaseOrderState;
	}

	public PurchaseOrderFlowState getPurchaseOrderFlowState() {
		return purchaseOrderFlowState;
	}

	public void setPurchaseOrderFlowState(PurchaseOrderFlowState purchaseOrderFlowState) {
		this.purchaseOrderFlowState = purchaseOrderFlowState;
	}

	public PurchaseOrderCheckState getPurchaseOrderCheckState() {
		return purchaseOrderCheckState;
	}

	public void setPurchaseOrderCheckState(PurchaseOrderCheckState purchaseOrderCheckState) {
		this.purchaseOrderCheckState = purchaseOrderCheckState;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public String getVendorErpCode() {
		return vendorErpCode;
	}

	public void setVendorErpCode(String vendorErpCode) {
		this.vendorErpCode = vendorErpCode;
	}

	public Integer getIsVendorView() {
		return isVendorView;
	}

	public void setIsVendorView(Integer isVendorView) {
		this.isVendorView = isVendorView;
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

	public String getPurchaseOrderType() {
		return purchaseOrderType;
	}

	public void setPurchaseOrderType(String purchaseOrderType) {
		this.purchaseOrderType = purchaseOrderType;
	}

	public String getErpPurchaseOrderNo() {
		return erpPurchaseOrderNo;
	}

	public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
		this.erpPurchaseOrderNo = erpPurchaseOrderNo;
	}

	public Integer getIsRevocationCheck() {
		return isRevocationCheck;
	}

	public void setIsRevocationCheck(Integer isRevocationCheck) {
		this.isRevocationCheck = isRevocationCheck;
	}

	public Integer getCloseHandleFlag() {
		return closeHandleFlag;
	}

	public void setCloseHandleFlag(Integer closeHandleFlag) {
		this.closeHandleFlag = closeHandleFlag;
	}

	public Calendar getVendorConfirmTime() {
		return vendorConfirmTime;
	}

	public void setVendorConfirmTime(Calendar vendorConfirmTime) {
		this.vendorConfirmTime = vendorConfirmTime;
	}

	public String getTaxRateCode() {
		return taxRateCode;
	}

	public void setTaxRateCode(String taxRateCode) {
		this.taxRateCode = taxRateCode;
	}

	public String getInternationlTradeTerm() {
		return internationlTradeTerm;
	}

	public void setInternationlTradeTerm(String internationlTradeTerm) {
		this.internationlTradeTerm = internationlTradeTerm;
	}

	public String getInternationlTradeRemark() {
		return internationlTradeRemark;
	}

	public void setInternationlTradeRemark(String internationlTradeRemark) {
		this.internationlTradeRemark = internationlTradeRemark;
	}

	public Calendar getPurchaseOrderTime() {
		return purchaseOrderTime;
	}

	public void setPurchaseOrderTime(Calendar purchaseOrderTime) {
		this.purchaseOrderTime = purchaseOrderTime;
	}

	public Boolean getSrmSyncSap() {
		return srmSyncSap;
	}

	public void setSrmSyncSap(Boolean srmSyncSap) {
		this.srmSyncSap = srmSyncSap;
	}

}
