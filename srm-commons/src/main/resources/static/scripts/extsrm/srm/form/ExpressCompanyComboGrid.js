/**
 * 快递公司模糊查询下拉框
 */
Ext.define("Ext.srm.form.ExpressCompanyComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.expressCompanyComboGrid",
	xtype : "expressCompanyComboGrid", 
	alternateClassName : ["Ext.srm.form.ExpressCompanyComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + '/md/datadict/getall?groupCode=expressCompany',
	    fields: ['itemCode', 'itemName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_itemCode_OR_LIKE_itemName",
    displayFields : [{
	    Qheader : "快递公司编码",
		header : $("delivery.expressCompanyCode"),
		dataIndex : "itemCode"
	}, {
		Qheader : "快递公司名称",
		header : $("delivery.expressCompanyName"),
		dataIndex : "itemName",
		tipable : true
	}], 
    valueField:"itemCode",
    displayField:"itemName", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Ext.srm.window.ExpressCompanySelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.expressCompanyCode":"itemCode",
       "model.expressCompanyName":"itemName" 
    }
});


