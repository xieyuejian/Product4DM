package com.huiju.srm.commons.utils;

/**
 * SRM 公共右键操作状态枚举
 * 
 * @author xufq
 * 
 */
public enum SrmCommEvent {

	TOCONFIRM("提交审核"), // 0
	TOPASS("同意"), // 1
	TONOPASS("驳回");// 3

	private String stateDesc;

	private SrmCommEvent(String stateDesc) {
		this.stateDesc = stateDesc;
	}

	public String getStateDesc() {
		return stateDesc;
	}

	public void setStateDesc(String stateDesc) {
		this.stateDesc = stateDesc;
	}

	public String getName() {
		return this.name();
	}

	public int getIndex() {
		return this.ordinal();
	}

	public static String getDescByIndex(Integer index) {
		SrmCommEvent[] names = values();
		if (index != null) {
			return names[index].stateDesc;
		}
		return "";
	}

	public Integer value() {
		return this.ordinal();
	}

	public String toString() {
		return String.valueOf(this.ordinal());
	}

}
