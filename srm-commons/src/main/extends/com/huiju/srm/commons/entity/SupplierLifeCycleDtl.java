package com.huiju.srm.commons.entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 供应商生命周期配置
 */
@MappedSuperclass
public class SupplierLifeCycleDtl extends BaseEntity<Long> {
private static final long serialVersionUID = 3075133691557657227L;
	/**id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "SupplierLifeCycleDtl_PK")
    @TableGenerator(name = "SupplierLifeCycleDtl_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "SupplierLifeCycleDtl_PK",
    allocationSize = 1)
	@Column(name = "supplierLifeCycleDtlId")
	protected Long supplierLifeCycleDtlId;
	/**id*/
	@Column(name = "supplierLifeCycleId")
	protected Long supplierLifeCycleId;
	/**供应商主数据接口同步*/
	@Column(name = "supplierMasterSyn")
	protected String supplierMasterSyn;
	/**冻结解冻来源*/
	@Column(name = "frozenSource")
	protected String frozenSource;
	/**等级变更来源*/
	@Column(name = "gradeChangeSource")
	protected String gradeChangeSource;
	/**供应商整改来源*/
	@Column(name = "rectifySource")
	protected String rectifySource;
	/**供应商引入认证流程*/
	@Column(name = "authenticationPricess")
	protected String authenticationPricess;
	/**供应商引入是否参考实地评鉴*/
	@Column(name = "reference1")
	protected String reference1;
	/**供应商引入是否参考物料认证*/
	@Column(name = "reference2")
	protected String reference2;
	/**样品申请是否需要供应商确认*/
	@Column(name = "sampleComfirme")
	protected String sampleComfirme;
	/**是否需要绩效考核*/
	@Column(name = "isAssessment")
	protected String isAssessment;
	/**是否按照物料组考核供应商*/
	@Column(name = "materialAssessment")
	protected String materialAssessment;
	/**控制点所属*/
	@Column(name = "belong")
	protected String belong;
	/**id*/
	/*@Column(name = "belongId")
	protected Long belongId;*/
	
	public Long getSupplierLifeCycleDtlId() {
		return supplierLifeCycleDtlId;
	}
	public void setSupplierLifeCycleDtlId(Long supplierLifeCycleDtlId) {
		this.supplierLifeCycleDtlId = supplierLifeCycleDtlId;
	}
	public Long getSupplierLifeCycleId() {
		return supplierLifeCycleId;
	}
	public void setSupplierLifeCycleId(Long supplierLifeCycleId) {
		this.supplierLifeCycleId = supplierLifeCycleId;
	}
	public String getSupplierMasterSyn() {
		return supplierMasterSyn;
	}
	public void setSupplierMasterSyn(String supplierMasterSyn) {
		this.supplierMasterSyn = supplierMasterSyn;
	}
	public String getFrozenSource() {
		return frozenSource;
	}
	public void setFrozenSource(String frozenSource) {
		this.frozenSource = frozenSource;
	}
	public String getGradeChangeSource() {
		return gradeChangeSource;
	}
	public void setGradeChangeSource(String gradeChangeSource) {
		this.gradeChangeSource = gradeChangeSource;
	}
	public String getRectifySource() {
		return rectifySource;
	}
	public void setRectifySource(String rectifySource) {
		this.rectifySource = rectifySource;
	}
	public String getAuthenticationPricess() {
		return authenticationPricess;
	}
	public void setAuthenticationPricess(String authenticationPricess) {
		this.authenticationPricess = authenticationPricess;
	}
	public String getReference1() {
		return reference1;
	}
	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}
	public String getReference2() {
		return reference2;
	}
	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}
	public String getSampleComfirme() {
		return sampleComfirme;
	}
	public void setSampleComfirme(String sampleComfirme) {
		this.sampleComfirme = sampleComfirme;
	}
	public String getIsAssessment() {
		return isAssessment;
	}
	public void setIsAssessment(String isAssessment) {
		this.isAssessment = isAssessment;
	}
	public String getMaterialAssessment() {
		return materialAssessment;
	}
	public void setMaterialAssessment(String materialAssessment) {
		this.materialAssessment = materialAssessment;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
	 
	public SupplierLifeCycleDtl() {
	
	}
	public SupplierLifeCycleDtl(Long supplierLifeCycleDtlId ) {
		this.supplierLifeCycleDtlId = supplierLifeCycleDtlId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((supplierLifeCycleDtlId == null) ? 0 : supplierLifeCycleDtlId.hashCode());
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
		SupplierLifeCycleDtl other = (SupplierLifeCycleDtl) obj;
		if (supplierLifeCycleDtlId == null) {
			if (other.supplierLifeCycleDtlId != null)
				return false;
		} else if (!supplierLifeCycleDtlId.equals(other.supplierLifeCycleDtlId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdSupplierLifeCycleDtl[supplierLifeCycleDtlId=" + supplierLifeCycleDtlId + ",supplierLifeCycleId=" + supplierLifeCycleId + ",supplierMasterSyn=" + supplierMasterSyn + ",frozenSource=" + frozenSource + ",gradeChangeSource=" + gradeChangeSource + ",rectifySource=" + rectifySource + ",authenticationPricess=" + authenticationPricess + ",reference1=" + reference1 + ",reference2=" + reference2 + ",sampleComfirme=" + sampleComfirme + ",isAssessment=" + isAssessment + ",materialAssessment=" + materialAssessment + ",belong=" + belong + "]";
	}

}
