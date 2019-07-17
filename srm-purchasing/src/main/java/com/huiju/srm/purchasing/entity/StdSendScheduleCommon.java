package com.huiju.srm.purchasing.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Basic;
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
import javax.persistence.Transient;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 送货排程采购订单中间表
 * 
 * @author CWQ
 *
 */
@MappedSuperclass
public class StdSendScheduleCommon extends BaseEntity<Long> {
    private static final long serialVersionUID = 334059748711537547L;;

    /** 排程中间表ID */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SendScheduleCommon_PK")
    @TableGenerator(name = "SendScheduleCommon_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "SendScheduleCommon_PK", allocationSize = 1)
    @Column(name = "sendScheduleCommonId")
    protected Long sendScheduleCommonId;

    /** 排程主表ID */
    @ManyToOne(optional = false)
    @JoinColumn(name = "sendScheduleId", referencedColumnName = "sendScheduleId")
    protected SendSchedule sendSchedule;

    /** 采购细单ID */
    @Basic(optional = false)
    @Column(name = "purchaseOrderDetailId")
    protected Long purchaseOrderDetailId;

    /** 明细 */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sendScheduleCommon", orphanRemoval = true)
    protected List<SendScheduleDetail> sendScheduleDetails;

    /** 订单ID */
    @Transient
    protected Long purchaseOrderId;

    /** 采购员ID */
    @Transient
    protected Long buyerId;

    /** 采购员名称 */
    @Transient
    protected String buyerName;

    /** 采购员编码 */
    @Transient
    protected String buyerCode;

    /** 来自主单采购订单号编码 */
    @Transient
    protected String purchaseOrderNo;

    /** 订单明细行号 */
    @Transient
    protected Integer rowIds;

    /** 行号 */
    @Transient
    protected Long rowNo;

    /** 供应商编码 */
    @Transient
    protected String vendorCode;

    /** 供应商名称 */
    @Transient
    protected String vendorName;

    /** 物料ID */
    @Transient
    protected Long materialId;

    /** 物料编码 */
    @Transient
    protected String materialCode;

    /** 物料名称 */
    @Transient
    protected String materialName;

    /** 单位编码 */
    @Transient
    protected String unitCode;

    /** 单位名称 */
    @Transient
    protected String unitName;

    /** 订单数量 */
    @Transient
    protected BigDecimal sendQty;

    /** 送货量 */
    @Transient
    protected BigDecimal deliveryQty;

    /** 在途量 */
    @Transient
    protected BigDecimal onWayQty;

    /** 收货量 */
    @Transient
    protected BigDecimal receiptQty;

    /** 退货量 */
    @Transient
    protected BigDecimal returnGoodsQty;

    /** 可送货量 */
    @Transient
    protected BigDecimal canSendQty;

    /** 已排程数量 */
    @Transient
    protected BigDecimal scheduleQty;

    /** 库存地点 */
    @Transient
    protected String stockLocal;

    /** 工厂编码 */
    @Transient
    protected String factoryCode;

    /** 工厂名称 */
    @Transient
    protected String factoryName;

    /** 库存 */
    @Transient
    protected BigDecimal stockQty;

    /** 行项目类别 */
    @Transient
    protected String lineItemTypeCode;

    /** 需求日期 */
    @Transient
    protected Calendar vendorTime;

    /** sap采购订单号 */
    @Transient
    protected String erpPurchaseOrderNo;

    /** SAP供应商代码 */
    @Transient
    protected String vendorErpCode;

    /** 公司编码 */
    @Transient
    protected String companyCode;

    /** 未税价 */
    @Transient
    protected BigDecimal taxPrice;

    public Long getSendScheduleCommonId() {
        return sendScheduleCommonId;
    }

    public void setSendScheduleCommonId(Long stdSendScheduleCommonId) {
        this.sendScheduleCommonId = stdSendScheduleCommonId;
    }

    public SendSchedule getSendSchedule() {
        return sendSchedule;
    }

    public void setSendSchedule(SendSchedule sendSchedule) {
        this.sendSchedule = sendSchedule;
    }

    public List<SendScheduleDetail> getSendScheduleDetails() {
        return sendScheduleDetails;
    }

    public void setSendScheduleDetails(List<SendScheduleDetail> sendScheduleDetails) {
        this.sendScheduleDetails = sendScheduleDetails;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public Integer getRowIds() {
        return rowIds;
    }

    public void setRowIds(Integer rowIds) {
        this.rowIds = rowIds;
    }

    public Long getRowNo() {
        return rowNo;
    }

    public void setRowNo(Long rowNo) {
        this.rowNo = rowNo;
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

    public BigDecimal getScheduleQty() {
        return scheduleQty;
    }

    public void setScheduleQty(BigDecimal scheduleQty) {
        this.scheduleQty = scheduleQty;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public BigDecimal getStockQty() {
        return stockQty;
    }

    public void setStockQty(BigDecimal stockQty) {
        this.stockQty = stockQty;
    }

    public String getLineItemTypeCode() {
        return lineItemTypeCode;
    }

    public void setLineItemTypeCode(String lineItemTypeCode) {
        this.lineItemTypeCode = lineItemTypeCode;
    }

    public Calendar getVendorTime() {
        return vendorTime;
    }

    public void setVendorTime(Calendar vendorTime) {
        this.vendorTime = vendorTime;
    }

    public String getErpPurchaseOrderNo() {
        return erpPurchaseOrderNo;
    }

    public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
        this.erpPurchaseOrderNo = erpPurchaseOrderNo;
    }

    public String getVendorErpCode() {
        return vendorErpCode;
    }

    public void setVendorErpCode(String vendorErpCode) {
        this.vendorErpCode = vendorErpCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
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

    public String getStockLocal() {
        return stockLocal;
    }

    public void setStockLocal(String stockLocal) {
        this.stockLocal = stockLocal;
    }

}
