package com.huiju.srm.purchasing.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.huiju.module.data.jpa.entity.BaseEntity;

/**
 * 邮件提交审核记录
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_mailsubmitauditlog")
public class MaiLSubmitAuditLog extends BaseEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "BUSINESSKEY")
	private String processKey;
	@Column(name = "PROCESSKEY")
	private String businessKey;
	@Column(name = "CREATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar createTime;
	@Column(name = "USERID")
	private Long userId;
	@Column(name = "USERNAME")
	private String userName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessKey() {
		return processKey;
	}

	public void setProcessKey(String processKey) {
		this.processKey = processKey;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public Calendar getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
