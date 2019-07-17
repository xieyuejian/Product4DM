package com.huiju.srm.purchasing.entity;




public enum PurchasingRequisitionState{
	//0:新建;1:完成;
	NEW("新建"),//0 
    TOCONFIRM("待审核"), //1
    TONOPASS("审核不过"),//2
    TOPASS("发布"),//3  
    CLOSE("关闭 "),//4  
    CANCEL("取消");//5   
	
    private String stateDesc;
    private PurchasingRequisitionState(String stateDesc) {
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
    
 
    
	public Integer value() {
		return this.ordinal();
	}
   
	public String toString() {
		return String.valueOf(this.ordinal());
	}
}

