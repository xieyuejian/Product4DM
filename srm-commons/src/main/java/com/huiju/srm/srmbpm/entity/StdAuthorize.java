package com.huiju.srm.srmbpm.entity;

import java.util.Calendar;

import javax.persistence.*;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 *
 * 授权单实体类
 * 
 * @author hongwl
 *
 * @date 2019年4月16日
 */
@MappedSuperclass
public class StdAuthorize extends BaseEntity<Long> {
	private static final long serialVersionUID = 334059748711537547L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Authorize_PK")
	@TableGenerator(name = "Authorize_PK",
			// 主键生成策略的表
			table = "s_pkgenerator",
			// 主键生成策略的表的字段
			pkColumnName = "PkGeneratorName",
			// 主键生成策略值的字段
			valueColumnName = "PkGeneratorValue",
			// 主键生成策略的值，序列的名字
			pkColumnValue = "Authorize_PK", allocationSize = 1)
	@Column(name = "AUTHORIZEID")
	private Long authorizeId;
	/** 授权编码 */
	@Column(name = "AUTHORIZENO")
	protected String authorizeNo;
	/** 单据类型 */
	@Column(name = "BILLTYPE")
	protected String billTypes;
	/** 单据类型id */
	@Column(name = "BILLCODE")
	protected String billId;
	/** 生效时间 */
	@Column(name = "EFFECTIVETIME")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar effectiveTime;
	/** 失效时间 */
	@Column(name = "EXPIRYTIME")
	@Temporal(TemporalType.TIMESTAMP)
	protected Calendar expiryTime;
	/** 授权理由 */
	@Column(name = "AUTHORIZEREASON")
	protected String authorityReason;
	/** 授权人id */
	@Column(name = "AUTHORIZEPERSONID")
	protected Long authorizePersonId;
	/** 授权人姓名 */
	@Column(name = "AUTHORIZEPERSONNAME")
	protected String authorizePersonName;
	/** 被授权人id */
	@Column(name = "AUTHORIZETOPERSONID")
	protected Long authorizeToPersonId;
	/** 被授权人姓名 */
	@Column(name = "AUTHORIZETOPERSONNAME")
	protected String authorizeToPersonName;
	/** 申请状态 */
	@Column(name = "STATUS")
	protected AuthorizeState status;
	@Transient
	private String[] billIds;

	public Long getAuthorizeId() {
		return authorizeId;
	}

	public void setAuthorizeId(Long authorizeId) {
		this.authorizeId = authorizeId;
	}

	public String getAuthorizeNo() {
		return authorizeNo;
	}

	public void setAuthorizeNo(String authorizeNo) {
		this.authorizeNo = authorizeNo;
	}

	public String getBillTypes() {
		return billTypes;
	}

	public void setBillTypes(String billTypes) {
		this.billTypes = billTypes;
	}

	public Calendar getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Calendar effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Calendar getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Calendar expiryTime) {
		this.expiryTime = expiryTime;
	}

	public String getAuthorityReason() {
		return authorityReason;
	}

	public void setAuthorityReason(String authorityReason) {
		this.authorityReason = authorityReason;
	}

	public Long getAuthorizePersonId() {
		return authorizePersonId;
	}

	public void setAuthorizePersonId(Long authorizePersonId) {
		this.authorizePersonId = authorizePersonId;
	}

	public String getAuthorizePersonName() {
		return authorizePersonName;
	}

	public void setAuthorizePersonName(String authorizePersonName) {
		this.authorizePersonName = authorizePersonName;
	}

	public Long getAuthorizeToPersonId() {
		return authorizeToPersonId;
	}

	public void setAuthorizeToPersonId(Long authorizeToPersonId) {
		this.authorizeToPersonId = authorizeToPersonId;
	}

	public String getAuthorizeToPersonName() {
		return authorizeToPersonName;
	}

	public void setAuthorizeToPersonName(String authorizeToPersonName) {
		this.authorizeToPersonName = authorizeToPersonName;
	}

	public AuthorizeState getStatus() {
		return status;
	}

	public void setStatus(AuthorizeState status) {
		this.status = status;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String[] getBillIds() {
		return billIds;
	}

	public void setBillIds(String[] billIds) {
		this.billIds = billIds;
	}

}
