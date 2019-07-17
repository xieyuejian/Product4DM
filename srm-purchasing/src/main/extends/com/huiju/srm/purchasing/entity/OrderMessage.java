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
 * 订单审核日志表
 * 
 * @author zhuang.jq
 */
@Entity
@Table(name = "d_cp_message")
public class OrderMessage extends BaseEntity<String> {
	private static final long serialVersionUID = 334059748711537547L;

	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "BUSINESS_KEY")
	private String processKey;
	@Column(name = "PROCESS_KEY")
	private String businessKey;
	@Column(name = "message")
	private String message;
	@Column(name = "ROLE_ID")
	private String roleName;
	@Column(name = "ROLE_NAME")
	private String userName;
	@Column(name = "USER_ID")
	private String roleId;
	@Column(name = "USER_NAME")
	private String userId;
	@Column(name = "RESULT")
	private String result;
	@Column(name = "TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar time;
	@Column(name = "UPLOADGROUPID")
	private String uploadGroupId;

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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Calendar getTime() {
		return time;
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

	public String getUploadGroupId() {
		return uploadGroupId;
	}

	public void setUploadGroupId(String uploadGroupId) {
		this.uploadGroupId = uploadGroupId;
	}
}
