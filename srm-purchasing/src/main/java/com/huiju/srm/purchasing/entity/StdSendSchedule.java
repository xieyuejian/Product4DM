package com.huiju.srm.purchasing.entity;

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
 * 送货排程主单
 * 
 * @author zhuang.jq
 */
@MappedSuperclass
public class StdSendSchedule extends BaseEntity<Long> {
    private static final long serialVersionUID = -2459667978502535034L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SendSchedule_PK")
    @TableGenerator(name = "SendSchedule_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "SendSchedule_PK", allocationSize = 1)
    /** 排程Id */
    @Column(name = "sendScheduleId")
    protected Long sendScheduleId;

    /** 排程单号 */
    @Column(name = "sendScheduleNo")
    protected String sendScheduleNo;

    /** 客户端编码 */
    @Column(name = "clientCode")
    protected String clientCode;

    /** 采购组织编码 */
    @Column(name = "purchasingOrgCode")
    protected String purchasingOrgCode;

    /** 采购组织名称 */
    @Column(name = "purchasingOrgName")
    protected String purchasingOrgName;

    /** 供应商编码 */
    @Column(name = "vendorCode")
    protected String vendorCode;

    /** erp供应商 */
    @Column(name = "vendorErpCode")
    protected String vendorErpCode;

    /** 供应商名称 */
    @Column(name = "vendorName")
    protected String vendorName;

    /** 单据状态 0:新建;1:发布、2:拒绝3:执行4:取消5:完成 */
    @Column(name = "sendScheduleState")
    protected SendScheduleState sendScheduleState;

    /** 同步sap状态 0:未同步2:同步中1:已同步3:同步失败 */
    @Column(name = "erpSynState")
    protected Integer erpSynState;

    /** Sap返回信息 */
    @Column(name = "erpReturnMsg")
    protected String erpReturnMsg;

    /** 拒绝原因 */
    @Column(name = "refuseReason")
    protected String refuseReason;

    @Column(name = "sendScheduleDate")
    /** 排程日期 */
    @Temporal(TemporalType.TIMESTAMP)
    protected Calendar sendScheduleDate;

    /** 中间表对象 */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "sendSchedule", orphanRemoval = true)
    protected List<SendScheduleCommon> sendScheduleCommons;

    @Transient
    protected String returnValue;// 返回值

    @Column(name = "procesKey")
    protected String procesKey;// 流程Key

    public Long getSendScheduleId() {
        return sendScheduleId;
    }

    public void setSendScheduleId(Long sendScheduleId) {
        this.sendScheduleId = sendScheduleId;
    }

    public String getSendScheduleNo() {
        return sendScheduleNo;
    }

    public void setSendScheduleNo(String sendScheduleNo) {
        this.sendScheduleNo = sendScheduleNo;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
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

    public SendScheduleState getSendScheduleState() {
        return sendScheduleState;
    }

    public void setSendScheduleState(SendScheduleState sendscheduleState) {
        this.sendScheduleState = sendscheduleState;
    }

    public String getErpReturnMsg() {
        return erpReturnMsg;
    }

    public void setErpReturnMsg(String erpReturnMsg) {
        this.erpReturnMsg = erpReturnMsg;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public Calendar getSendScheduleDate() {
        return sendScheduleDate;
    }

    public void setSendScheduleDate(Calendar sendScheduleDate) {
        this.sendScheduleDate = sendScheduleDate;
    }

    public List<SendScheduleCommon> getSendScheduleCommons() {
        return sendScheduleCommons;
    }

    public void setSendScheduleCommons(List<SendScheduleCommon> sendScheduleCommons) {
        this.sendScheduleCommons = sendScheduleCommons;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public String getProcesKey() {
        return procesKey;
    }

    public void setProcesKey(String procesKey) {
        this.procesKey = procesKey;
    }

    public Integer getErpSynState() {
        return erpSynState;
    }

    public void setErpSynState(Integer erpSynState) {
        this.erpSynState = erpSynState;
    }

    public String getVendorErpCode() {
        return vendorErpCode;
    }

    public void setVendorErpCode(String vendorErpCode) {
        this.vendorErpCode = vendorErpCode;
    }
}
