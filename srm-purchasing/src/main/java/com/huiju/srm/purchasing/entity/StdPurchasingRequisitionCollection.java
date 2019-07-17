package com.huiju.srm.purchasing.entity;
import java.math.BigDecimal;
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
 * <pre>采购申请明细归集</pre>
 * @author bairx 
 * @version 1.0 时间 2019/3/30 
 */
@MappedSuperclass
public class StdPurchasingRequisitionCollection extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/**采购申请明细归集id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PurchasingRequisitionCollection_PK")
    @TableGenerator(name = "PurchasingRequisitionCollection_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "PurchasingRequisitionCollection_PK",
    allocationSize = 1)
	@Column(name = "purchasingRequisitionColId")
	protected Long purchasingRequisitionColId;
	/**采购申请单号*/
	@Column(name = "purchasingRequisitionNo")
	protected String purchasingRequisitionNo;
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
	/**转移数量*/
	@Column(name = "transferQuantity")
	protected BigDecimal transferQuantity;
	/**已转单数量*/
	@Column(name = "transferedQuantity")
	protected BigDecimal transferedQuantity;
	/**备注*/
	@Column(name = "remark")
	protected String remark;
	/**来源(1:srm;2:sap)*/
	@Column(name = "source")
	protected String source;
	/**公司编码*/
	@Column(name = "companyCode")
	protected String companyCode;
	/**公司名称*/
	@Column(name = "companyName")
	protected String companyName;
	/**申请人编码*/
	@Column(name = "applicantCode")
	protected String applicantCode;
	/**申请人姓名*/
	@Column(name = "applicantName")
	protected String applicantName;
	/**可转单数量*/
	@Column(name = "canTransferQuantity")
	protected BigDecimal canTransferQuantity;
	/**分配状态*/
	@Column(name = "configState")
	protected String configState;
  
	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY, mappedBy = "purchasingRequisitionCollection",orphanRemoval = true)
    protected List<PurchasingRequisitionTrans> purchasingRequisitionTrans;
	
	public Long getPurchasingRequisitionColId() {
		return purchasingRequisitionColId;
	}
	public void setPurchasingRequisitionColId(Long purchasingRequisitionColId) {
		this.purchasingRequisitionColId = purchasingRequisitionColId;
	}
	public String getPurchasingRequisitionNo() {
		return purchasingRequisitionNo;
	}
	public void setPurchasingRequisitionNo(String purchasingRequisitionNo) {
		this.purchasingRequisitionNo = purchasingRequisitionNo;
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
	public BigDecimal getTransferQuantity() {
		return transferQuantity;
	}
	public void setTransferQuantity(BigDecimal transferQuantity) {
		this.transferQuantity = transferQuantity;
	}
	public BigDecimal getTransferedQuantity() {
		return transferedQuantity;
	}
	public void setTransferedQuantity(BigDecimal transferedQuantity) {
		this.transferedQuantity = transferedQuantity;
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
	


	
	
	public StdPurchasingRequisitionCollection() {
	
	}
	public StdPurchasingRequisitionCollection(Long purchasingRequisitionColId ) {
		this.purchasingRequisitionColId = purchasingRequisitionColId;
	}

	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getApplicantCode() {
		return applicantCode;
	}
	public void setApplicantCode(String applicantCode) {
		this.applicantCode = applicantCode;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((purchasingRequisitionColId == null) ? 0 : purchasingRequisitionColId.hashCode());
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
		StdPurchasingRequisitionCollection other = (StdPurchasingRequisitionCollection) obj;
		if (purchasingRequisitionColId == null) {
			if (other.purchasingRequisitionColId != null)
				return false;
		} else if (!purchasingRequisitionColId.equals(other.purchasingRequisitionColId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdPurchasingRequisitionCollection[purchasingRequisitionCollectionId=" + purchasingRequisitionColId + ",purchasingRequisitionNo=" + purchasingRequisitionNo + ",rowNo=" + rowNo + ",plantName=" + plantName + ",plantCode=" + plantCode + ",materialCode=" + materialCode + ",materialName=" + materialName + ",unitCode=" + unitCode + ",unitName=" + unitName + ",purchasingGroupCode=" + purchasingGroupCode + ",purchasingGroupName=" + purchasingGroupName + ",demandDate=" + demandDate + ",quantityDemanded=" + quantityDemanded + ",transferQuantity=" + transferQuantity + ",transferedQuantity=" + transferedQuantity + ",remark=" + remark + ",source=" + source + ",companyCode="+companyCode +",companyName="+companyName +",applicantCode="+applicantCode + ",applicantName="+applicantName  +"]";
	
	}
	public String getConfigState() {
		return configState;
	}
	public void setConfigState(String configState) {
		this.configState = configState;
	}
	public List<PurchasingRequisitionTrans> getPurchasingRequisitionTrans() {
		return purchasingRequisitionTrans;
	}
	public void setPurchasingRequisitionTrans(List<PurchasingRequisitionTrans> purchasingRequisitionTrans) {
		this.purchasingRequisitionTrans = purchasingRequisitionTrans;
	}
	public BigDecimal getCanTransferQuantity() {
		return canTransferQuantity;
	}
	public void setCanTransferQuantity(BigDecimal canTransferQuantity) {
		this.canTransferQuantity = canTransferQuantity;
	}

}
