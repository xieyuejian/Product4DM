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
 * <pre>采购申请</pre>
 * @author bairx
 * @version 1.0 时间 2019/3/30 
 */
@MappedSuperclass
public class StdPurchasingRequisition extends BaseEntity<Long> {
	private static final long serialVersionUID = 3075133691557657227L;
	/**采购申请id*/
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "PurchasingRequisition_PK")
    @TableGenerator(name = "PurchasingRequisition_PK",
    table = "s_pkgenerator",
    pkColumnName = "PkGeneratorName",
    valueColumnName = "PkGeneratorValue",
    pkColumnValue = "PurchasingRequisition_PK",
    allocationSize = 1)
	@Column(name = "purchasingRequisitionId")
	protected Long purchasingRequisitionId;
	/**采购申请编码*/
	@Column(name = "purchasingRequisitionNo")
	protected String purchasingRequisitionNo;
	/**客户端编码*/
	@Column(name = "clientCode")
	protected String clientCode;
	/**单据状态(0:新建;1:待审核;2:审核不过;3:发布;4:关闭;5:取消)*/
	@Column(name = "status")
	protected PurchasingRequisitionState status;
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
	/**申请时间*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "applicantTime")
	protected Calendar applicantTime;
	/**是否被使用*/
	@Column(name = "isUsed")
	protected String isUsed;
	/**备注*/
	@Column(name = "remark")
	protected String remark;
	/**创建人id*/
	@Column(name = "createUserId")
	protected Long createUserId;
	/**创建人姓名*/
	@Column(name = "createUserName")
	protected String createUserName;  
	/**创建时间*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createTime")
	protected Calendar createTime;
	/**修改人id*/
	@Column(name = "modifyUserId")
	protected Long modifyUserId;
	/**修改人姓名*/
	@Column(name = "modifyUserName")
	protected String modifyUserName; 
	/**修改时间*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modifyTime")
	protected Calendar modifyTime;
	
	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY, mappedBy = "purchasingRequisition",orphanRemoval = true)
    protected List<PurchasingRequisitionDtl> purchasingRequisitionDtls;
	
	public List<PurchasingRequisitionDtl> getPurchasingRequisitionDtls() {
		return purchasingRequisitionDtls;
	}
	public void setPurchasingRequisitionDtls(List<PurchasingRequisitionDtl> purchasingRequisitionDtls) {
		this.purchasingRequisitionDtls = purchasingRequisitionDtls;
	}
	
	public Long getPurchasingRequisitionId() {
		return purchasingRequisitionId;
	}
	public void setPurchasingRequisitionId(Long purchasingRequisitionId) {
		this.purchasingRequisitionId = purchasingRequisitionId;
	}
	public String getPurchasingRequisitionNo() {
		return purchasingRequisitionNo;
	}
	public void setPurchasingRequisitionNo(String purchasingRequisitionNo) {
		this.purchasingRequisitionNo = purchasingRequisitionNo;
	}
	public String getClientCode() {
		return clientCode;
	}
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	 
	public PurchasingRequisitionState getStatus() {
		return status;
	}
	public void setStatus(PurchasingRequisitionState status) {
		this.status = status;
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
	public Calendar getApplicantTime() {
		return applicantTime;
	}
	public void setApplicantTime(Calendar applicantTime) {
		this.applicantTime = applicantTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
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
	
	public Calendar getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}
	public Calendar getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Calendar modifyTime) {
		this.modifyTime = modifyTime;
	} 
	public String getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}
	
	public StdPurchasingRequisition() {
	
	}
	public StdPurchasingRequisition(Long purchasingRequisitionId ) {
		this.purchasingRequisitionId = purchasingRequisitionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((purchasingRequisitionId == null) ? 0 : purchasingRequisitionId.hashCode());
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
		StdPurchasingRequisition other = (StdPurchasingRequisition) obj;
		if (purchasingRequisitionId == null) {
			if (other.purchasingRequisitionId != null)
				return false;
		} else if (!purchasingRequisitionId.equals(other.purchasingRequisitionId))
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "StdPurchasingRequisition[purchasingRequisitionId=" + purchasingRequisitionId + ",purchasingRequisitionNo=" + purchasingRequisitionNo + ",clientCode=" + clientCode + ",status=" + status + ",companyCode=" + companyCode + ",companyName=" + companyName + ",applicantCode=" + applicantCode + ",applicantName=" + applicantName + ",applicantTime=" + applicantTime + ",remark=" + remark + ",createUserId=" + createUserId + ",createUserName=" + createUserName + ",modifyUserId=" + modifyUserId + ",modifyUserName=" + modifyUserName + "]";
	
	}

}
