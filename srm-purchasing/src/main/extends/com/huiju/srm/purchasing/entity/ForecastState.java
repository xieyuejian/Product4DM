package com.huiju.srm.purchasing.entity;

/**
 * 
 * @author bairx
 *
 */
public enum ForecastState {

	NEW("新建"),
	TOCONFIRM("待审核"),
	TONOPASS("审核不过"),
    TOPASS("完成");
//	CLOSE("完成");

	private String stateDesc;//状态描述
	private ForecastState(String stateDesc) {
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
