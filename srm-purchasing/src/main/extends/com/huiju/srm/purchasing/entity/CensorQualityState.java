package com.huiju.srm.purchasing.entity;

/**
 * 
 * @author bairu
 *
 */
public enum CensorQualityState {

	TOCHECK("待检"),
	CHECKING("检验中"),
	CHECKED("检验完成"),
	CANCEL("取消");

	private String stateDesc;//状态描述
	private CensorQualityState(String stateDesc) {
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
    @Override
    public String toString() {
    	return String.valueOf(this.getIndex());
    }
}
