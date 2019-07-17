package com.huiju.srm.purchasing.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.huiju.module.data.jpa.entity.BaseEntity;
import com.huiju.srm.commons.utils.SrmSynStatus;

/**
 * 送货管理数据表 基准类
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "v_cp_delivery")
public class DeliveryView extends BaseEntity<Long> {

	private static final long serialVersionUID = 3166419172896261357L;

	/** id */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Delivery_PK")
	@TableGenerator(name = "Delivery_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "Delivery_PK", allocationSize = 1)
	@Column(name = "deliveryId")
	protected Long deliveryId;
	/** 送货单号 */
	@Column(name = "deliveryCode")
	protected String deliveryCode;
	/** 送货日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deliveryDate")
	protected Calendar deliveryDate;
	/** 单据状态 */
	@Column(name = "status")
	protected DeliveryState status;
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
	/** 工厂编码 */
	@Column(name = "plantCode")
	protected String plantCode;
	/** 工厂名称 */
	@Column(name = "plantName")
	protected String plantName;
	/** 库存地点编码 */
	@Column(name = "storageLocationCode")
	protected String storageLocationCode;
	/** 库存地点名称 */
	@Column(name = "storageLocationName")
	protected String storageLocationName;
	/** 送货方式 */
	@Column(name = "deliveryTypes")
	protected String deliveryTypes;
	/** 快递单号 */
	@Column(name = "trackingNumber")
	protected String trackingNumber;
	/** 送达日期 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "serviceDate")
	protected Calendar serviceDate;
	/** 同步状态 */
	@Column(name = "synchronizeStatus")
	protected SrmSynStatus synchronizeStatus;
	/** ERP返回信息 */
	/*
	 * @Column(name = "erpReturnMsg") protected String erpReturnMsg;
	 */
	/** 客户端编码 */
	@Column(name = "clientCode")
	protected String clientCode;
	/** 收货人 */
	@Transient
	protected String consignee;
	/** 收货时间 */
	@Transient
	protected Calendar receivingTime;

	public Long getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Long deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public Calendar getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Calendar deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public DeliveryState getStatus() {
		return status;
	}

	public void setStatus(DeliveryState status) {
		this.status = status;
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

	public String getVendorErpCode() {
		return vendorErpCode;
	}

	public void setVendorErpCode(String vendorErpCode) {
		this.vendorErpCode = vendorErpCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
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

	public String getDeliveryTypes() {
		return deliveryTypes;
	}

	public void setDeliveryTypes(String deliveryTypes) {
		this.deliveryTypes = deliveryTypes;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public Calendar getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Calendar serviceDate) {
		this.serviceDate = serviceDate;
	}

	public SrmSynStatus getSynchronizeStatus() {
		return synchronizeStatus;
	}

	public void setSynchronizeStatus(SrmSynStatus synchronizeStatus) {
		this.synchronizeStatus = synchronizeStatus;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

	public Calendar getReceivingTime() {
		return receivingTime;
	}

	public void setReceivingTime(Calendar receivingTime) {
		this.receivingTime = receivingTime;
	}

}
