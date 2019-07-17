package com.huiju.srm.purchasing.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 送货管理物流详情细细单，对接快递100
 * 
 * @author hongwl
 */
@MappedSuperclass
public class StdLogisticsDtlDtl extends BaseEntity<Long> {
	protected static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LogisticsDtlDtl_PK")
	@TableGenerator(name = "LogisticsDtlDtl_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "LogisticsDtlDtl_PK", allocationSize = 1)
	/** 物流详情id */
	@Column(name = "logisticsDtlDtlId")
	protected Long logisticsDtlDtlId;
	/** 快递信息id */
	@ManyToOne
	@JoinColumn(name = "deliveryExpressDtlId", referencedColumnName = "deliveryExpressDtlId")
	protected DeliveryExpressDtl deliveryExpressDtl;
	/** 更新时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updateTime")
	protected Calendar updateTime;
	/** 内容 */
	@Column(name = "contant")
	protected String contant;

	public Long getLogisticsDtlDtlId() {
		return logisticsDtlDtlId;
	}

	public void setLogisticsDtlDtlId(Long logisticsDtlDtlId) {
		this.logisticsDtlDtlId = logisticsDtlDtlId;
	}

	public DeliveryExpressDtl getDeliveryExpressDtl() {
		return deliveryExpressDtl;
	}

	public void setDeliveryExpressDtl(DeliveryExpressDtl deliveryExpressDtl) {
		this.deliveryExpressDtl = deliveryExpressDtl;
	}

	public Calendar getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Calendar updateTime) {
		this.updateTime = updateTime;
	}

	public String getContant() {
		return contant;
	}

	public void setContant(String contant) {
		this.contant = contant;
	}
}
