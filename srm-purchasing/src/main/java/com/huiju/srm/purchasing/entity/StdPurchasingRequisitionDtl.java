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
 * <pre>采购申请明细</pre>
 * @author bairx 
 * @version 1.0 时间 2019/3/30 
 */
@MappedSuperclass
public class StdPurchasingRequisitionDtl extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/**采购申请明细id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PurchasingRequisitionDtl_PK")
    @TableGenerator(name = "PurchasingRequisitionDtl_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "PurchasingRequisitionDtl_PK",
    allocationSize = 1)
	@Column(name = "purchasingRequisitionDtlId")
	protected Long purchasingRequisitionDtlId;
	/**采购申请id*/
	@ManyToOne
	@JoinColumn(name="purchasingRequisitionId", referencedColumnName="purchasingRequisitionId")
	protected PurchasingRequisition purchasingRequisition;
	/**行号*/
	@Column(name = "rowNo")
	protected Long rowNo;
	/**工厂名称*/
	@Column(name = "plantName")
	protected String plantName;
	/**工厂编码*/
	@Column(name = "plantCode")
	protected String plantCode;
	/**物料编码*/
	@Column(name = "materialCode")
	protected String materialCode;
	/**物料名称*/
	@Column(name = "materialName")
	protected String materialName;
	/**基本单位编码*/
	@Column(name = "unitCode")
	protected String unitCode;
	/**基本单位名称*/
	@Column(name = "unitName")
	protected String unitName;
	/**采购组编码*/
	@Column(name = "purchasingGroupCode")
	protected String purchasingGroupCode;
	/**采购组名称*/
	@Column(name = "purchasingGroupName")
	protected String purchasingGroupName;
	/**需求日期*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "demandDate")
	protected Calendar demandDate;
	/**需求量*/
	@Column(name = "quantityDemanded")
	protected BigDecimal quantityDemanded;
	/**备注*/
	@Column(name = "remark")
	protected String remark;
	/**来源(1:正式;2:临时)*/
	@Column(name = "source")
	protected String source;
	



	
	public Long getPurchasingRequisitionDtlId() {
		return purchasingRequisitionDtlId;
	}
	public void setPurchasingRequisitionDtlId(Long purchasingRequisitionDtlId) {
		this.purchasingRequisitionDtlId = purchasingRequisitionDtlId;
	}

	public PurchasingRequisition getPurchasingRequisition() {
		return purchasingRequisition;
	}
	public void setPurchasingRequisition(PurchasingRequisition purchasingRequisition) {
		this.purchasingRequisition = purchasingRequisition;
	}
	public Long getRowNo() {
		return rowNo;
	}
	public void setRowNo(Long rowNo) {
		this.rowNo = rowNo;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getPlantCode() {
		return plantCode;
	}
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
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
	public Calendar getDemandDate() {
		return demandDate;
	}
	public void setDemandDate(Calendar demandDate) {
		this.demandDate = demandDate;
	}
	public BigDecimal getQuantityDemanded() {
		return quantityDemanded;
	}
	public void setQuantityDemanded(BigDecimal quantityDemanded) {
		this.quantityDemanded = quantityDemanded;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	} 
	
	public StdPurchasingRequisitionDtl() {
	
	}
	public StdPurchasingRequisitionDtl(Long purchasingRequisitionDtlId ) {
		this.purchasingRequisitionDtlId = purchasingRequisitionDtlId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((purchasingRequisitionDtlId == null) ? 0 : purchasingRequisitionDtlId.hashCode());
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
		PurchasingRequisitionDtl other = (PurchasingRequisitionDtl) obj;
		if (purchasingRequisitionDtlId == null) {
			if (other.purchasingRequisitionDtlId != null)
				return false;
		} else if (!purchasingRequisitionDtlId.equals(other.purchasingRequisitionDtlId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "PurchasingRequisitionDtl[purchasingRequisitionDtlId=" + purchasingRequisitionDtlId + ",rowNo=" + rowNo + ",plantName=" + plantName + ",plantCode=" + plantCode + ",materialCode=" + materialCode + ",materialName=" + materialName + ",unitCode=" + unitCode + ",unitName=" + unitName + ",purchasingGroupCode=" + purchasingGroupCode + ",purchasingGroupName=" + purchasingGroupName + ",demandDate=" + demandDate + ",quantityDemanded=" + quantityDemanded + ",remark=" + remark + ",source=" + source + "]";
	
	}

}
