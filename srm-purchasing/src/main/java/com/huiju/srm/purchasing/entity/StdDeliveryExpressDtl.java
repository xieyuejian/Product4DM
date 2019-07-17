package com.huiju.srm.purchasing.entity;

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

import com.huiju.module.data.jpa.entity.BaseEntity;
import com.huiju.srm.commons.entity.ExpressState;

/**
 * 送货管理快递明细，对接快递100
 * 
 * @author hongwl
 */
@MappedSuperclass
public class StdDeliveryExpressDtl extends BaseEntity<Long> {
	protected static final long serialVersionUID = 1L;
	/** id */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "DeliveryExpressDtl_PK")
	@TableGenerator(name = "DeliveryExpressDtl_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "DeliveryExpressDtl_PK", allocationSize = 1)
	/** 快递明细id */
	@Column(name = "deliveryExpressDtlId")
	protected Long deliveryExpressDtlId;
	/** 送货管理id */
	@ManyToOne
	@JoinColumn(name = "deliveryId", referencedColumnName = "deliveryId")
	protected Delivery delivery;
	/** 快递公司编码 */
	@Column(name = "expressCompanyCode")
	protected String expressCompanyCode;
	/** 快递公司名称 */
	@Column(name = "expressCompanyName")
	protected String expressCompanyName;
	/** 快递单号 */
	@Column(name = "expressNo")
	protected String expressNo;
	/** 物流详情 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "deliveryExpressDtl", orphanRemoval = true)
	protected List<LogisticsDtlDtl> logisticsDtlDtls;
	// 以下为调用快递100（单号归属公司智能判断接口） api接口设计字段
	/** 接口调用返回状态 0：成功 1：失败 */
	@Column(name = "status")
	protected String status;
	/** 接口调用消息 */
	@Column(name = "message")
	protected String message;
	// 以下为调用快递100（实时查询接口--物流信息） api接口设计字段
	/** 快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态 */
	@Column(name = "state")
	protected ExpressState state;
	/** 接口调用消息 */
	@Column(name = "logisticsMessage")
	protected String logisticsMessage;
	/** 接口调用返回状态 */
	@Column(name = "logisticsStatus")
	protected String logisticsStatus;
	/** 每次调用接口的时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "invokeTime")
	protected Calendar invokeTime;

	public ExpressState getState() {
		return state;
	}

	public void setState(ExpressState state) {
		this.state = state;
	}

	public String getLogisticsMessage() {
		return logisticsMessage;
	}

	public void setLogisticsMessage(String logisticsMessage) {
		this.logisticsMessage = logisticsMessage;
	}

	public String getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(String logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	public Calendar getInvokeTime() {
		return invokeTime;
	}

	public void setInvokeTime(Calendar invokeTime) {
		this.invokeTime = invokeTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getDeliveryExpressDtlId() {
		return deliveryExpressDtlId;
	}

	public void setDeliveryExpressDtlId(Long deliveryExpressDtlId) {
		this.deliveryExpressDtlId = deliveryExpressDtlId;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public String getExpressCompanyCode() {
		return expressCompanyCode;
	}

	public void setExpressCompanyCode(String expressCompanyCode) {
		this.expressCompanyCode = expressCompanyCode;
	}

	public String getExpressCompanyName() {
		return expressCompanyName;
	}

	public void setExpressCompanyName(String expressCompanyName) {
		this.expressCompanyName = expressCompanyName;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public List<LogisticsDtlDtl> getLogisticsDtlDtls() {
		return logisticsDtlDtls;
	}

	public void setLogisticsDtlDtls(List<LogisticsDtlDtl> logisticsDtlDtls) {
		this.logisticsDtlDtls = logisticsDtlDtls;
	}
}
