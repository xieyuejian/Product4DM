/**
 * 采购组模糊查询下拉框
 */
Ext.define("Ext.srm.form.PurchasingGroupComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.purchasingGroupComboGrid",
	xtype : "purchasingGroupComboGrid", 
	alternateClassName : ["Ext.srm.form.PurchasingGroupComboGrid"], 
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + "/md/purchasinggroup/list",
	    fields: ['purchasingGroupCode', 'purchasingGroupName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_purchasingGroupCode_OR_LIKE_purchasingGroupName",
    displayFields:[{
		Qheader: "采购组编码",
		header: $("purchasingGroup.purchasingGroupCode"),
		dataIndex: 'purchasingGroupCode'
	}, {
		Qheader: "采购组名称",
		header: $("purchasingGroup.purchasingGroupName"),
		dataIndex: 'purchasingGroupName'
	}], 
    valueField:"purchasingGroupCode",
    displayField:"purchasingGroupCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.organization.PurchasingGroupSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.purchasingGroupCode":"purchasingGroupCode",
       "model.purchasingGroupName":"purchasingGroupName" 
    }
});


