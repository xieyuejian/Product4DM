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
 * 送货管理明细表
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdDeliveryDtl extends BaseEntity<Long> {
    protected static final long serialVersionUID = 1L;
    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "DeliveryDtl_PK")
    @TableGenerator(name = "DeliveryDtl_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "DeliveryDtl_PK", allocationSize = 1)
    @Column(name = "deliveryDtlId")
    protected Long deliveryDtlId;
    /** 送货管理id */
    @ManyToOne
    @JoinColumn(name = "deliveryId", referencedColumnName = "deliveryId")
    protected Delivery delivery;
    /** 订单id */
    @Column(name = "orderId")
    protected Long orderId;
    /** 取消按钮 */
    @Column(name = "cancelBtn")
    protected String cancelBtn;
    /** 取消标识（取消标识为“否”，“0”为否；取消标识为“是”，“1”为是） */
    @Column(name = "cancelFlag")
    protected String cancelFlag;
    /** 关闭按钮 */
    @Column(name = "closeBtn")
    protected String closeBtn;
    /** 关闭标识 */
    @Column(name = "closeFlag")
    protected String closeFlag;
    /** 采购订单号 */
    @Column(name = "purchaseOrderCode")
    protected String purchaseOrderCode;
    /** 行号 */
    @Column(name = "lineNumber")
    protected Long lineNumber;
    /** 行项目类别 */
    @Column(name = "lineItemTypes")
    protected String lineItemTypes;
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
    /** 订单数量 */
    @Column(name = "orderNumber")
    protected BigDecimal orderNumber = BigDecimal.ZERO;
    /** 已送数量 */
    @Column(name = "toSentNumber")
    protected BigDecimal toSentNumber = BigDecimal.ZERO;
    /** 已收货量 */
    @Column(name = "receivedNumber")
    protected BigDecimal receivedNumber = BigDecimal.ZERO;
    /** 退货量 */
    @Column(name = "returnNumber")
    protected BigDecimal returnNumber = BigDecimal.ZERO;
    /** 可送数量 */
    @Column(name = "canSentNumber")
    protected BigDecimal canSentNumber = BigDecimal.ZERO;
    /** 送货数量 */
    @Column(name = "deliveryNumber")
    protected BigDecimal deliveryNumber = BigDecimal.ZERO;
    /** 备注 */
    @Column(name = "remark")
    protected String remark;
    /** 排程单号 */
    @Column(name = "scheduleCode")
    protected String scheduleCode;
    /* 采购 **/
    @Column(name = "buyer")
    protected String buyer;
    /** 附件ID */
    @Column(name = "annex")
    protected String annex;
    /** 附件 */
    @Column(name = "annexView")
    protected String annexView;
    /** 点收数量 */
    @Column(name = "acceptQty")
    protected BigDecimal acceptQty = BigDecimal.ZERO;
    /** 数据来源 */
    @Column(name = "dataFrom")
    protected Long dataFrom;
    /** 订单明细ID */
    @Column(name = "orderDetailId")
    protected Long orderDetailId;
    /** 送货明细ID */
    @Column(name = "sendDetailId")
    protected Long sendDetailId;
    /** 库存编码 */
    @Column(name = "storageLocationCode")
    protected String storageLocationCode;
    /** 库存名称 */
    @Transient
    protected String storageLocationName;
    /** 是否收货完成 */
    @Transient
    protected String isFinish;
    /** 交货过量限度 */
    @Column(name = "overDeliveryLimit")
    protected BigDecimal overDeliveryLimit = BigDecimal.ZERO;
    /** 送货单已收货量 */
    @Column(name = "receivedQty")
    protected BigDecimal receivedQty = BigDecimal.ZERO;
    /** erp订单号 */
    @Column(name = "erpPurchaseOrderNo")
    protected String erpPurchaseOrderNo;
    
    public StdDeliveryDtl() {
    }

    public String getCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(String cancelFlag) {
        this.cancelFlag = cancelFlag;
    }

    public String getCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(String closeFlag) {
        this.closeFlag = closeFlag;
    }

    public Long getDeliveryDtlId() {
        return deliveryDtlId;
    }

    public void setDeliveryDtlId(Long deliveryDtlId) {
        this.deliveryDtlId = deliveryDtlId;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public String getCancelBtn() {
        return cancelBtn;
    }

    public void setCancelBtn(String cancelBtn) {
        this.cancelBtn = cancelBtn;
    }

    public String getCloseBtn() {
        return closeBtn;
    }

    public void setCloseBtn(String closeBtn) {
        this.closeBtn = closeBtn;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public Long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLineItemTypes() {
        return lineItemTypes;
    }

    public void setLineItemTypes(String lineItemTypes) {
        this.lineItemTypes = lineItemTypes;
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

    public BigDecimal getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(BigDecimal orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getToSentNumber() {
        return toSentNumber;
    }

    public void setToSentNumber(BigDecimal toSentNumber) {
        this.toSentNumber = toSentNumber;
    }

    public BigDecimal getReceivedNumber() {
        return receivedNumber;
    }

    public void setReceivedNumber(BigDecimal receivedNumber) {
        this.receivedNumber = receivedNumber;
    }

    public BigDecimal getReturnNumber() {
        return returnNumber;
    }

    public void setReturnNumber(BigDecimal returnNumber) {
        this.returnNumber = returnNumber;
    }

    public BigDecimal getCanSentNumber() {
        return canSentNumber;
    }

    public void setCanSentNumber(BigDecimal canSentNumber) {
        this.canSentNumber = canSentNumber;
    }

    public BigDecimal getDeliveryNumber() {
        return deliveryNumber;
    }

    public void setDeliveryNumber(BigDecimal deliveryNumber) {
        this.deliveryNumber = deliveryNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getScheduleCode() {
        return scheduleCode;
    }

    public void setScheduleCode(String scheduleCode) {
        this.scheduleCode = scheduleCode;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getAnnex() {
        return annex;
    }

    public void setAnnex(String annex) {
        this.annex = annex;
    }

    public BigDecimal getAcceptQty() {
        return acceptQty;
    }

    public void setAcceptQty(BigDecimal acceptQty) {
        this.acceptQty = acceptQty;
    }

    public Long getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(Long dataFrom) {
        this.dataFrom = dataFrom;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getSendDetailId() {
        return sendDetailId;
    }

    public void setSendDetailId(Long sendDetailId) {
        this.sendDetailId = sendDetailId;
    }

    public String getStorageLocationCode() {
        return storageLocationCode;
    }

    public void setStorageLocationCode(String storageLocationCode) {
        this.storageLocationCode = storageLocationCode;
    }

    public String getStorageLocationName() {
        return storageLocationName;
    }

    public void setStorageLocationName(String storageLocationName) {
        this.storageLocationName = storageLocationName;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public BigDecimal getOverDeliveryLimit() {
        return overDeliveryLimit;
    }

    public void setOverDeliveryLimit(BigDecimal overDeliveryLimit) {
        this.overDeliveryLimit = overDeliveryLimit;
    }

    public String getAnnexView() {
        return annexView;
    }

    public void setAnnexView(String annexView) {
        this.annexView = annexView;
    }

    public BigDecimal getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(BigDecimal receivedQty) {
        this.receivedQty = receivedQty;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deliveryDtlId == null) ? 0 : deliveryDtlId.hashCode());
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
        StdDeliveryDtl other = (StdDeliveryDtl) obj;
        if (deliveryDtlId == null) {
            if (other.deliveryDtlId != null)
                return false;
        } else if (!deliveryDtlId.equals(other.deliveryDtlId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StdDeliveryDtl [deliveryDtlId=" + deliveryDtlId + ", orderId=" + orderId + ", cancelBtn=" + cancelBtn
                + ", cancelFlag=" + cancelFlag + ", closeBtn=" + closeBtn + ", closeFlag=" + closeFlag
                + ", purchaseOrderCode=" + purchaseOrderCode + ", lineNumber=" + lineNumber + ", lineItemTypes="
                + lineItemTypes + ", materialCode=" + materialCode + ", materialName=" + materialName + ", unitCode="
                + unitCode + ", unitName=" + unitName + ", orderNumber=" + orderNumber + ", toSentNumber="
                + toSentNumber + ", receivedNumber=" + receivedNumber + ", returnNumber=" + returnNumber
                + ", canSentNumber=" + canSentNumber + ", deliveryNumber=" + deliveryNumber + ", remark=" + remark
                + ", scheduleCode=" + scheduleCode + ", buyer=" + buyer + ", annex=" + annex + ", acceptQty="
                + acceptQty + ", dataFrom=" + dataFrom + ", orderDetailId=" + orderDetailId + ", sendDetailId="
                + sendDetailId + ", storageLocationCode=" + storageLocationCode + ", storageLocationName="
                + storageLocationName + ", isFinish=" + isFinish + ", overDeliveryLimit=" + overDeliveryLimit + "]";
    }

	public String getErpPurchaseOrderNo() {
		return erpPurchaseOrderNo;
	}

	public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
		this.erpPurchaseOrderNo = erpPurchaseOrderNo;
	}

}
