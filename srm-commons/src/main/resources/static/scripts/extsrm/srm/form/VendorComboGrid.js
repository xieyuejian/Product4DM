/**
 * 供应商模糊查询下拉框
 */
Ext.define("Ext.srm.form.VendorComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.vendorComboGrid",
	xtype : "vendorComboGrid", 
	alternateClassName : ["Ext.srm.form.VendorComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url:  path_srm + "/sl/vendorm/find4select",
	    fields: ['vendorErpCode', 'vendorName', 'vendorCode', 'vendorId'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_vendorErpCode_OR_LIKE_vendorName",
    displayFields : [{
		header : $("vendor.code"),
		dataIndex : 'vendorErpCode'
	}, {
		header : $("vendor.name"),
		dataIndex : 'vendorName'
	}], 
    valueField:"vendorErpCode",
    displayField:"vendorErpCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Sl.masterdata.VendorSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{
       "model.vendorId":"vendorId",
       "model.vendorErpCode":"vendorErpCode",
       "model.vendorCode":"vendorCode",
       "model.vendorName":"vendorName" 
    },
    minChars : 1
});


