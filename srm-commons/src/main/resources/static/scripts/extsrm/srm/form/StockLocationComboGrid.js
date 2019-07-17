/**
 * 库存地点模糊查询下拉框
 */
Ext.define("Ext.srm.form.StockLocationComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.stockLocationComboGrid",
	xtype : "stockLocationComboGrid", 
	alternateClassName : ["Ext.srm.form.StockLocationComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + "/md/stocklocation/list",
	    fields: ['stockLocationCode', 'stockLocationName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_stockLocationCode_OR_LIKE_stockLocationName",
    displayFields : [{
		Qheader : "库存地点编码",
		header : $("stockLocation.stockLocationCode"),
		dataIndex : "stockLocationCode"
	}, {
		Qheader : "库存地点名称",
		header : $("stockLocation.stockLocationName"),
		dataIndex : "stockLocationName"
	}], 
    valueField:"stockLocationCode",
    displayField:"stockLocationCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.organization.StockLocationSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.stockLocationCode":"stockLocationCode",
       "model.stockLocationName":"stockLocationName" 
    }
});


