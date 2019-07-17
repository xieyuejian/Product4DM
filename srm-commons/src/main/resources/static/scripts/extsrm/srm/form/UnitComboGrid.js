/**
 * 计量单位模糊查询下拉框
 */
Ext.define("Ext.srm.form.UnitComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.unitComboGrid",
	xtype : "unitComboGrid", 
	alternateClassName : ["Ext.srm.form.UnitComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url:  path_masterdata + "/md/unit/list",
	    fields: ['unitCode', 'unitName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_unitCode_OR_LIKE_unitName",
    displayFields : [{
		Qheader : "单位代码",
		header : $("unit.unitCode"),
		dataIndex : "unitCode"
	}, {
		Qheader : "单位名称",
		header : $("unit.unitName"),
		dataIndex : "unitName"
	}], 
    valueField:"unitCode",
    displayField:"unitCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.busiparams.UnitSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.unitCode":"unitCode",
       "model.unitName":"unitName" 
    }
});


