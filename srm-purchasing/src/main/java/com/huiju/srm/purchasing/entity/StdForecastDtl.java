package com.huiju.srm.purchasing.entity;

import java.math.BigDecimal;
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
 * <pre>
 * 采购预测细表
 * </pre>
 * 
 * @author wz
 * @version 1.0 时间 2016/8/3
 */
@MappedSuperclass
public class StdForecastDtl extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/** 采购预测细单ID */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ForecastDtl_PK")
	@TableGenerator(name = "ForecastDtl_PK", table = "s_pkgenerator", pkColumnName = "PkGeneratorName", valueColumnName = "PkGeneratorValue", pkColumnValue = "ForecastDtl_PK", allocationSize = 1)
	@Column(name = "forecastDtlId")
	protected Long forecastDtlId;
	@ManyToOne
	@JoinColumn(name = "forecastId", referencedColumnName = "forecastId", nullable = false)
	protected Forecast forecast;

	/** 物料编码 */
	@Column(name = "materialCode")
	protected String materialCode;
	/** 物料名称 */
	@Column(name = "materialName")
	protected String materialName;
	/** 采购预测需求时间 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "forecastMainDate")
	protected Calendar forecastMainDate;
	/** 预测数量 */
	@Column(name = "forecastNum")
	protected BigDecimal forecastNum;
	/** 单位 */
	@Column(name = "unitName")
	protected String unitName;
	/** 工厂编码 */
	@Column(name = "plantCode")
	protected String plantCode;
	/** 供应商编码 */
	@Column(name = "vendorCode")
	protected String vendorCode;
	/** 供应商Erp编码 */
	@Column(name = "vendorErpCode")
	protected String vendorErpCode;
	/** 供应商名称 */
	@Column(name = "vendorName")
	protected String vendorName;
	/** 工厂编码 */
	@Column(name = "plantName")
	protected String plantName;

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public Long getForecastDtlId() {
		return forecastDtlId;
	}

	public void setForecastDtlId(Long forecastDtlId) {
		this.forecastDtlId = forecastDtlId;
	}

	public Forecast getForecast() {
		return forecast;
	}

	public void setForecast(Forecast forecast) {
		this.forecast = forecast;
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

	public Calendar getForecastMainDate() {
		return forecastMainDate;
	}

	public void setForecastMainDate(Calendar forecastMainDate) {
		this.forecastMainDate = forecastMainDate;
	}

	public BigDecimal getForecastNum() {
		return forecastNum;
	}

	public void setForecastNum(BigDecimal forecastNum) {
		this.forecastNum = forecastNum;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((forecastDtlId == null) ? 0 : forecastDtlId.hashCode());
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
		StdForecastDtl other = (StdForecastDtl) obj;
		if (forecastDtlId == null) {
			if (other.forecastDtlId != null)
				return false;
		} else if (!forecastDtlId.equals(other.forecastDtlId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StdForecastDtl [forecastDtlId=" + forecastDtlId + ", materialCode=" + materialCode + ", materialName=" + materialName
				+ ", forecastMainDate=" + forecastMainDate + ", forecastNum=" + forecastNum + ", unitName=" + unitName + ", plantCode="
				+ plantCode + ", vendorCode=" + vendorCode + ", vendorErpCode=" + vendorErpCode + ", vendorName=" + vendorName + "]";
	}

}
