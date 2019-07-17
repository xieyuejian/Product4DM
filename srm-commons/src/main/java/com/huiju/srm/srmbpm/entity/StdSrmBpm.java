package com.huiju.srm.srmbpm.entity;

import com.huiju.module.data.common.BaseDTO;

/**
 * bpm的实体
 * 
 * @author wangmx
 *
 */
public class StdSrmBpm extends BaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String billId;
	public String billNo;
	public Long createUserId;
	public String createUserCode;
	public String createUserName;
	public String moduleCode;
	public String moduleName;

	public String getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(String createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/*
	 * @Override public String toString() { return "Bpm [billId=" + billId +
	 * ", billNo=" + billNo + ", createUserId=" + createUserId +
	 * ", createUserName=" + createUserName + ", moduleCode=" + moduleCode +
	 * ", moduleName=" + moduleName + "]"; }
	 */

}