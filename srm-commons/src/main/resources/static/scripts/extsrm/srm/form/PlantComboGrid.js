/**
 * 工厂模糊查询下拉框
 */
Ext.define("Ext.srm.form.PlantComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.plantComboGrid",
	xtype : "plantComboGrid", 
	alternateClassName : ["Ext.srm.form.PlantComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + "/md/plant/list",
	    fields: ['plantCode', 'plantName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_plantCode_OR_LIKE_plantName",
    displayFields : [{
		Qheader : "工厂编码",
		header : $("plant.plantCode"),
		dataIndex : "plantCode"
	}, {
		Qheader : "工厂名称",
		header : $("plant.plantName"),
		dataIndex : "plantName"
	}], 
    valueField:"plantCode",
    displayField:"plantCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.organization.PlantSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.plantCode":"plantCode",
       "model.plantName":"plantName" 
    }
});


