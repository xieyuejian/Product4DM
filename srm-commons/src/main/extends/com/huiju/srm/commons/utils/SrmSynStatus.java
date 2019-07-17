package com.huiju.srm.commons.utils;

/**
 * SRM ERP同步状态枚举
 * 
 * @author zhengjf
 * 
 */
public enum SrmSynStatus {

	SYNCHRONIZEDNOT("未同步"), // 0
	SYNCHRONIZING("同步中"), // 1
	SYNSUCCESS("已同步"), // 2
	SYNFAILED("同步失败"), SYNNONEED("不同步");// 3

	private String stateDesc;

	private SrmSynStatus(String stateDesc) {
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
		SrmSynStatus[] names = values();
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
