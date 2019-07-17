package com.huiju.srm.purchasing.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 采购订单实体类
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdPurchaseOrderDetail extends BaseEntity<Long> {
	private static final long serialVersionUID = 4889299609913708626L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PurchaseOrderDetail_PK")
	@TableGenerator(name = "PurchaseOrderDetail_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "PurchaseOrderDetail_PK", allocationSize = 10)
	/** 订单明细id */
	@Column(name = "purchaseOrderDetailId")
	protected Long purchaseOrderDetailId;

	/** 订单主单对象 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "purchaseOrderId", referencedColumnName = "purchaseOrderId")
	protected PurchaseOrder purchaseOrder;

	/** 行号 */
	@Column(name = "rowIds")
	protected Integer rowIds;

	/** 价格_采购方 */
	@Column(name = "buyerPrice")
	protected BigDecimal buyerPrice;

	/** 数量_采购方 */
	@Column(name = "buyerQty")
	protected BigDecimal buyerQty;

	/** 送货日期_采购方 */
	@Column(name = "buyerTime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar buyerTime;

	/** 价格_供应商 */
	@Column(name = "vendorPrice")
	protected BigDecimal vendorPrice;

	/** 数量_供应商 */
	@Column(name = "vendorQty")
	protected BigDecimal vendorQty;

	/** 送货日期_供应商 */
	@Column(name = "vendorTime")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar vendorTime;

	/** 送货数量 */
	@Column(name = "qtySend")
	protected BigDecimal qtySend;

	/** 在途数量 */
	@Column(name = "qtyOnline")
	protected BigDecimal qtyOnline;

	/** 到货数量 */
	@Column(name = "qtyArrive")
	protected BigDecimal qtyArrive;

	/** 检验数量 */
	@Column(name = "qtyCheck")
	protected BigDecimal qtyCheck;

	/** 合格数量 */
	@Column(name = "qtyAccord")
	protected BigDecimal qtyAccord;

	/** 不合格数量 */
	@Column(name = "qtyNaccord")
	protected BigDecimal qtyNaccord;

	/** 入库数量 */
	@Column(name = "qtyStore")
	protected BigDecimal qtyStore;

	/** 退货数量 */
	@Column(name = "qtyQuit")
	protected BigDecimal qtyQuit;

	/** 备注 */
	@Column(name = "remark")
	protected String remark;

	/** 送货(库存)地点 */
	@Column(name = "deliverystoreLocal")
	protected String deliverystoreLocal;

	/** 行金额 */
	@Column(name = "lineItemValAmt")
	protected BigDecimal lineItemValAmt;

	/** 物料编码 */
	@Column(name = "materialCode")
	protected String materialCode;

	/** 物料名称 */
	@Column(name = "materialName")
	protected String materialName;

	/** 单位代码 */
	@Column(name = "unitCode")
	protected String unitCode;

	/** 单位名称 */
	@Column(name = "unitName")
	protected String unitName;

	/** 已排程数量 */
	@Column(name = "scheduledQty")
	protected BigDecimal scheduledQty;

	/** 未已排程数量（可排程） */
	@Column(name = "unScheduledQty")
	protected BigDecimal unScheduledQty;

	/** 行项目类别编码 */
	@Column(name = "lineItemTypeCode")
	protected String lineItemTypeCode;

	/** 工厂代码 */
	@Column(name = "plantCode")
	protected String plantCode;

	/** 工厂名称 */
	@Column(name = "plantName")
	protected String plantName;

	/** 物料组编码 */
	@Column(name = "materialGroupCode")
	protected String materialGroupCode;

	/** 物料组名称 */
	@Column(name = "materialGroupName")
	protected String materialGroupName;

	/** 价格单位代码 */
	@Column(name = "priceUnitCode")
	protected String priceUnitCode;

	/** 价格单位名称 */
	@Column(name = "priceUnitName")
	protected String priceUnitName;

	/** 库存地点 */
	@Column(name = "storeLocal")
	protected String storeLocal;

	/** 是否退货0否1是 */
	@Column(name = "isReturn")
	protected Integer isReturn;

	/** 是否免费0否1是 */
	@Column(name = "isFree")
	protected Integer isFree;

	/** 过量交货限度 */
	@Column(name = "overDeliveryLimit")
	protected BigDecimal overDeliveryLimit;

	/** 删除标识 */
	@Column(name = "deleteFlag")
	protected Integer deleteFlag;

	/** 关闭标识 */
	@Column(name = "closeFlag")
	protected Integer closeFlag;

	/** 税率编码 */
	@Column(name = "taxRateCode")
	protected String taxRateCode;

	/** 交货不足限度 */
	@Column(name = "shortDeliveryLimit")
	protected BigDecimal shortDeliveryLimit;

	/** 未送数量 */
	@Column(name = "unCount")
	protected BigDecimal unCount;

	/** srm的行号 */
	@Column(name = "srmRowids")
	protected Integer srmRowids;

	/** ERP返回信息 */
	@Column(name = "erpReturnMsg")
	protected String erpReturnMsg;

	/** ERP同步标识0未同步1已同步 */
	@Column(name = "erpSynState")
	protected Integer erpSynState;

	/** 紧急标识 */
	@Column(name = "emergencyFlag")
	protected Integer emergencyFlag;

	/** 排程标识 */
	@Column(name = "scheduleFlag")
	protected Integer scheduleFlag;

	/** 数据来源 */
	@Column(name = "sourceCode")
	protected Long sourceCode;

	/** 操作标识 */
	@Column(name = "operate")
	protected String operate;

	/** 库存类型 A非限制/X质检/S冻结 */
	@Column(name = "stockType")
	protected String stockType;

	/** 已收货量 */
	@Column(name = "receiveQty")
	protected BigDecimal receiveQty = BigDecimal.ZERO;

	/** 可送数量 */
	@Column(name = "canSendQty")
	protected BigDecimal canSendQty;

	/** 价格主数据ID */
	@Column(name = "materialMasterPriceId")
	protected Long materialMasterPriceId;

	/** 细单ID */
	@Column(name = "materialMasterPriceDtlId")
	protected Long materialMasterPriceDtlId;

	/** 价格ID */
	@Column(name = "materialLadderPriceDtlId")
	protected Long materialLadderPriceDtlId;

	/** 研发项目号 */
	@Column(name = "pdProjectNumber")
	protected String pdProjectNumber;

	/** 科目分配类别 */
	@Column(name = "accountAllocationTypeCode")
	protected String accountAllocationTypeCode;

	/** 资产号 */
	@Column(name = "assetNumber")
	protected String assetNumber;

	/** 成本中心 */
	@Column(name = "costCenter")
	protected String costCenter;

	/** 总账科目 */
	@Column(name = "generalLedgerSubject")
	protected String generalLedgerSubject;

	/** 双单位转换集合 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "purchaseOrderDetail", orphanRemoval = true)
	protected List<PurchaseDualUnitConversion> purchaseDualUnitConversions = new ArrayList<PurchaseDualUnitConversion>();

	/** 定价条件集合 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "purchaseOrderDetail", orphanRemoval = true)
	protected List<PurchaseOrderPricing> purchaseOrderPricings = new ArrayList<PurchaseOrderPricing>();

	/** 供应商是否具有查看金额权限 */
	@Transient
	protected Integer isVendorView;

	/** 送货排程集合 */
	@Transient
	protected List<SendScheduleDetailVo> sendScheduleDetails = new ArrayList<SendScheduleDetailVo>();

	/** 采购申请明细归集ID */
	@Column(name = "purchasingRequisitionColId")
	protected Long purchasingRequisitionColId;

	/** 双单位转换临时字段 */
	@Transient
	protected String unitConversionInfo;
	/** 定价条件临时字段 */
	@Transient
	protected String pricingInfo;

	/** 定价单位 */
	@Transient
	protected String pricingUnit;

	/** 订单可送货量达到下容差标识, 默认N：否，Y：是 */
	@Column(name = "isAchieveLimit")
	protected String isAchieveLimit;

	public String getIsAchieveLimit() {
		return isAchieveLimit;
	}

	public void setIsAchieveLimit(String isAchieveLimit) {
		this.isAchieveLimit = isAchieveLimit;
	}

	public String getPricingUnit() {
		return pricingUnit;
	}

	public void setPricingUnit(String pricingUnit) {
		this.pricingUnit = pricingUnit;
	}

	public String getPdProjectNumber() {
		return pdProjectNumber;
	}

	public void setPdProjectNumber(String pdProjectNumber) {
		this.pdProjectNumber = pdProjectNumber;
	}

	public BigDecimal getReceiveQty() {
		return receiveQty;
	}

	public void setReceiveQty(BigDecimal receiveQty) {
		this.receiveQty = receiveQty;
	}

	public BigDecimal getCanSendQty() {
		return canSendQty;
	}

	public void setCanSendQty(BigDecimal canSendQty) {
		this.canSendQty = canSendQty;
	}

	public Long getPurchaseOrderDetailId() {
		return purchaseOrderDetailId;
	}

	public void setPurchaseOrderDetailId(Long purchaseOrderDetailId) {
		this.purchaseOrderDetailId = purchaseOrderDetailId;
	}

	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public Integer getRowIds() {
		return rowIds;
	}

	public void setRowIds(Integer rowIds) {
		this.rowIds = rowIds;
	}

	public BigDecimal getBuyerPrice() {
		return buyerPrice;
	}

	public void setBuyerPrice(BigDecimal buyerPrice) {
		this.buyerPrice = buyerPrice;
	}

	public BigDecimal getBuyerQty() {
		return buyerQty;
	}

	public void setBuyerQty(BigDecimal buyerQty) {
		this.buyerQty = buyerQty;
	}

	public Calendar getBuyerTime() {
		return buyerTime;
	}

	public void setBuyerTime(Calendar buyerTime) {
		this.buyerTime = buyerTime;
	}

	public BigDecimal getVendorPrice() {
		return vendorPrice;
	}

	public void setVendorPrice(BigDecimal vendorPrice) {
		this.vendorPrice = vendorPrice;
	}

	public BigDecimal getVendorQty() {
		return vendorQty;
	}

	public void setVendorQty(BigDecimal vendorQty) {
		this.vendorQty = vendorQty;
	}

	public Calendar getVendorTime() {
		return vendorTime;
	}

	public void setVendorTime(Calendar vendorTime) {
		this.vendorTime = vendorTime;
	}

	public BigDecimal getQtySend() {
		return qtySend;
	}

	public void setQtySend(BigDecimal qtySend) {
		this.qtySend = qtySend;
	}

	public BigDecimal getQtyOnline() {
		return qtyOnline;
	}

	public void setQtyOnline(BigDecimal qtyOnline) {
		this.qtyOnline = qtyOnline;
	}

	public BigDecimal getQtyArrive() {
		return qtyArrive;
	}

	public void setQtyArrive(BigDecimal qtyArrive) {
		this.qtyArrive = qtyArrive;
	}

	public BigDecimal getQtyCheck() {
		return qtyCheck;
	}

	public void setQtyCheck(BigDecimal qtyCheck) {
		this.qtyCheck = qtyCheck;
	}

	public BigDecimal getQtyAccord() {
		return qtyAccord;
	}

	public void setQtyAccord(BigDecimal qtyAccord) {
		this.qtyAccord = qtyAccord;
	}

	public BigDecimal getQtyNaccord() {
		return qtyNaccord;
	}

	public void setQtyNaccord(BigDecimal qtyNaccord) {
		this.qtyNaccord = qtyNaccord;
	}

	public BigDecimal getQtyStore() {
		return qtyStore;
	}

	public void setQtyStore(BigDecimal qtyStore) {
		this.qtyStore = qtyStore;
	}

	public BigDecimal getQtyQuit() {
		return qtyQuit;
	}

	public void setQtyQuit(BigDecimal qtyQuit) {
		this.qtyQuit = qtyQuit;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDeliverystoreLocal() {
		return deliverystoreLocal;
	}

	public void setDeliverystoreLocal(String deliverystoreLocal) {
		this.deliverystoreLocal = deliverystoreLocal;
	}

	public BigDecimal getLineItemValAmt() {
		return lineItemValAmt;
	}

	public void setLineItemValAmt(BigDecimal lineItemValAmt) {
		this.lineItemValAmt = lineItemValAmt;
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

	public BigDecimal getScheduledQty() {
		return scheduledQty;
	}

	public void setScheduledQty(BigDecimal scheduledQty) {
		this.scheduledQty = scheduledQty;
	}

	public String getLineItemTypeCode() {
		return lineItemTypeCode;
	}

	public void setLineItemTypeCode(String lineItemTypeCode) {
		this.lineItemTypeCode = lineItemTypeCode;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public String getMaterialGroupCode() {
		return materialGroupCode;
	}

	public void setMaterialGroupCode(String materialGroupCode) {
		this.materialGroupCode = materialGroupCode;
	}

	public String getMaterialGroupName() {
		return materialGroupName;
	}

	public void setMaterialGroupName(String materialGroupName) {
		this.materialGroupName = materialGroupName;
	}

	public String getPriceUnitCode() {
		return priceUnitCode;
	}

	public void setPriceUnitCode(String priceUnitCode) {
		this.priceUnitCode = priceUnitCode;
	}

	public String getPriceUnitName() {
		return priceUnitName;
	}

	public void setPriceUnitName(String priceUnitName) {
		this.priceUnitName = priceUnitName;
	}

	public String getStoreLocal() {
		return storeLocal;
	}

	public void setStoreLocal(String storeLocal) {
		this.storeLocal = storeLocal;
	}

	public Integer getIsReturn() {
		return isReturn;
	}

	public void setIsReturn(Integer isReturn) {
		this.isReturn = isReturn;
	}

	public Integer getIsFree() {
		return isFree;
	}

	public void setIsFree(Integer isFree) {
		this.isFree = isFree;
	}

	public BigDecimal getOverDeliveryLimit() {
		return overDeliveryLimit;
	}

	public void setOverDeliveryLimit(BigDecimal overDeliveryLimit) {
		this.overDeliveryLimit = overDeliveryLimit;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Integer getCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(Integer closeFlag) {
		this.closeFlag = closeFlag;
	}

	public String getTaxRateCode() {
		return taxRateCode;
	}

	public void setTaxRateCode(String taxRateCode) {
		this.taxRateCode = taxRateCode;
	}

	public BigDecimal getShortDeliveryLimit() {
		return shortDeliveryLimit;
	}

	public void setShortDeliveryLimit(BigDecimal shortDeliveryLimit) {
		this.shortDeliveryLimit = shortDeliveryLimit;
	}

	public BigDecimal getUnCount() {
		return unCount;
	}

	public void setUnCount(BigDecimal unCount) {
		this.unCount = unCount;
	}

	public Integer getSrmRowids() {
		return srmRowids;
	}

	public void setSrmRowids(Integer srmRowids) {
		this.srmRowids = srmRowids;
	}

	public String getErpReturnMsg() {
		return erpReturnMsg;
	}

	public void setErpReturnMsg(String erpReturnMsg) {
		this.erpReturnMsg = erpReturnMsg;
	}

	public Integer getErpSynState() {
		return erpSynState;
	}

	public void setErpSynState(Integer erpSynState) {
		this.erpSynState = erpSynState;
	}

	public Integer getEmergencyFlag() {
		return emergencyFlag;
	}

	public void setEmergencyFlag(Integer emergencyFlag) {
		this.emergencyFlag = emergencyFlag;
	}

	public Integer getScheduleFlag() {
		return scheduleFlag;
	}

	public void setScheduleFlag(Integer scheduleFlag) {
		this.scheduleFlag = scheduleFlag;
	}

	public Long getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(Long sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public List<PurchaseDualUnitConversion> getPurchaseDualUnitConversions() {
		return purchaseDualUnitConversions;
	}

	public void setPurchaseDualUnitConversions(List<PurchaseDualUnitConversion> purchaseDualUnitConversions) {
		this.purchaseDualUnitConversions = purchaseDualUnitConversions;
	}

	public List<PurchaseOrderPricing> getPurchaseOrderPricings() {
		return purchaseOrderPricings;
	}

	public void setPurchaseOrderPricings(List<PurchaseOrderPricing> purchaseOrderPricings) {
		this.purchaseOrderPricings = purchaseOrderPricings;
	}

	public Long getMaterialMasterPriceId() {
		return materialMasterPriceId;
	}

	public void setMaterialMasterPriceId(Long materialMasterPriceId) {
		this.materialMasterPriceId = materialMasterPriceId;
	}

	public Long getMaterialMasterPriceDtlId() {
		return materialMasterPriceDtlId;
	}

	public void setMaterialMasterPriceDtlId(Long materialMasterPriceDtlId) {
		this.materialMasterPriceDtlId = materialMasterPriceDtlId;
	}

	public Long getMaterialLadderPriceDtlId() {
		return materialLadderPriceDtlId;
	}

	public void setMaterialLadderPriceDtlId(Long materialLadderPriceDtlId) {
		this.materialLadderPriceDtlId = materialLadderPriceDtlId;
	}

	public String getAccountAllocationTypeCode() {
		return accountAllocationTypeCode;
	}

	public void setAccountAllocationTypeCode(String accountAllocationTypeCode) {
		this.accountAllocationTypeCode = accountAllocationTypeCode;
	}

	public String getAssetNumber() {
		return assetNumber;
	}

	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getGeneralLedgerSubject() {
		return generalLedgerSubject;
	}

	public void setGeneralLedgerSubject(String generalLedgerSubject) {
		this.generalLedgerSubject = generalLedgerSubject;
	}

	public BigDecimal getUnScheduledQty() {
		return unScheduledQty;
	}

	public void setUnScheduledQty(BigDecimal unScheduledQty) {
		this.unScheduledQty = unScheduledQty;
	}

	public Integer getIsVendorView() {
		return isVendorView;
	}

	public void setIsVendorView(Integer isVendorView) {
		this.isVendorView = isVendorView;
	}

	public Long getPurchasingRequisitionColId() {
		return purchasingRequisitionColId;
	}

	public void setPurchasingRequisitionColId(Long purchasingRequisitionColId) {
		this.purchasingRequisitionColId = purchasingRequisitionColId;
	}

	public List<SendScheduleDetailVo> getSendScheduleDetails() {
		return sendScheduleDetails;
	}

	public void setSendScheduleDetails(List<SendScheduleDetailVo> sendScheduleDetails) {
		this.sendScheduleDetails = sendScheduleDetails;
	}

	public String getUnitConversionInfo() {
		return unitConversionInfo;
	}

	public void setUnitConversionInfo(String unitConversionInfo) {
		this.unitConversionInfo = unitConversionInfo;
	}

	public String getPricingInfo() {
		return pricingInfo;
	}

	public void setPricingInfo(String pricingInfo) {
		this.pricingInfo = pricingInfo;
	}

}
