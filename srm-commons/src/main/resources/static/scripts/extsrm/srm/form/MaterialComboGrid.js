/**
 * 物料模糊查询下拉框
 */
Ext.define("Ext.srm.form.MaterialComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.materialComboGrid",
	xtype : "materialComboGrid", 
	alternateClassName : ["Ext.srm.form.MaterialComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + "/md/material/list",
	    fields: ['materialCode', 'materialName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_materialCode_OR_LIKE_materialName",
    displayFields : [{
		header : $("materialInfo.code"),
		dataIndex : 'materialCode' 
	}, {
		header : $("materialInfo.name"),
		dataIndex : 'materialName' 
	}], 
    valueField:"materialCode",
    displayField:"materialCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.material.MaterialSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.materialCode":"materialCode",
       "model.materialName":"materialName" 
    }
});


