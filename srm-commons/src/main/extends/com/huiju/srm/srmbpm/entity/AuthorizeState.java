package com.huiju.srm.srmbpm.entity;

/**
 */
public enum AuthorizeState {

	NEW("新建"), TOCONFIRM("待审核"), TONOPASS("审核不过"), TOPASS("生效"), CANCEL("关闭");

	private String stateDesc;

	private AuthorizeState(String stateDesc) {
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
		AuthorizeState[] names = values();
		if (index != null) {
			return names[index].stateDesc;
		}
		return "";
	}

	@Override
	public String toString() {
		// return this.getStateDesc();
		return String.valueOf(this.getIndex());
	}
}
