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

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * <pre>
 * 采购预测
 * </pre>
 * 
 * @author bairx
 * @version 1.0 时间 2019/3/30
 */
@MappedSuperclass
public class StdForecast extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/** 采购预测ID */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Forecast_PK")
	@TableGenerator(name = "Forecast_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "Forecast_PK", allocationSize = 1)
	@Column(name = "forecastId")
	protected Long forecastId;
	/** 采购预测单号 */
	@Column(name = "forecastNo")
	protected String forecastNo;
	/** 采购组织编码 */
	@Column(name = "purchasingOrgCode")
	protected String purchasingOrgCode;
	/** 采购组织名称 */
	@Column(name = "purchasingOrgName")
	protected String purchasingOrgName;
	/** 采购预测时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "forecastMainDate")
	protected Calendar forecastMainDate;
	/** 状态 */
	@Column(name = "forecastMainState")
	protected ForecastState status;
	/** 修改时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifyTime")
	protected Calendar modifyTime;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "forecast")
	protected List<ForecastDtl> forecastDtls;

	/** 客户端编码 */
	@Column(name = "clientCode")
	protected String clientCode;

	/** 数据来源-srm：手工创建，erp：ERP导入 */
	@Column(name = "createType")
	protected String createType;

	public String getCreateType() {
		return createType;
	}

	public void setCreateType(String createType) {
		this.createType = createType;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public List<ForecastDtl> getForecastDtls() {
		return forecastDtls;
	}

	public void setForecastDtls(List<ForecastDtl> forecastDtls) {
		this.forecastDtls = forecastDtls;
	}

	public Long getForecastId() {
		return forecastId;
	}

	public void setForecastId(Long forecastId) {
		this.forecastId = forecastId;
	}

	public String getForecastNo() {
		return forecastNo;
	}

	public void setForecastNo(String forecastNo) {
		this.forecastNo = forecastNo;
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

	public Calendar getForecastMainDate() {
		return forecastMainDate;
	}

	public void setForecastMainDate(Calendar forecastMainDate) {
		this.forecastMainDate = forecastMainDate;
	}

	public ForecastState getStatus() {
		return status;
	}

	public void setStatus(ForecastState status) {
		this.status = status;
	}

	public Calendar getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}

	public Long getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(Long modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public String getModifyUserName() {
		return modifyUserName;
	}

	public void setModifyUserName(String modifyUserName) {
		this.modifyUserName = modifyUserName;
	}

	public Calendar getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Calendar modifyTime) {
		this.modifyTime = modifyTime;
	}

	public StdForecast() {

	}

	public StdForecast(Long forecastId) {
		this.forecastId = forecastId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((forecastId == null) ? 0 : forecastId.hashCode());
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
		StdForecast other = (StdForecast) obj;
		if (forecastId == null) {
			if (other.forecastId != null)
				return false;
		} else if (!forecastId.equals(other.forecastId))
			return false;
		return true;
	}

}
