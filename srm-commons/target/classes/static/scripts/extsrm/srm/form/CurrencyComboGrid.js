/**
 * 货币模糊查询下拉框
 */
Ext.define("Ext.srm.form.CurrencyComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.currencyComboGrid",
	xtype : "currencyComboGrid", 
	alternateClassName : ["Ext.srm.form.CurrencyComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url:  path_masterdata + "/md/currency/list",
	    fields: ['currencyCode', 'currencyName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_currencyCode_OR_LIKE_currencyName",
    displayFields : [{
	    Qheader : "货币编码",
		header : $("currency.currencyCode"),
		dataIndex : "currencyCode"
	}, {
		Qheader : "货币名称",
		header : $("currency.currencyName"),
		dataIndex : "currencyName",
		tipable : true
	}], 
    valueField:"currencyCode",
    displayField:"currencyCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.busiparams.CurrencySelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.currencyCode":"currencyCode",
       "model.currencyName":"currencyName" 
    }
});


