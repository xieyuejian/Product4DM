/**
 * 工厂模糊查询下拉框
 */
Ext.define("Ext.srm.form.CompanyComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.companyComboGrid",
	xtype : "companyComboGrid", 
	alternateClassName : ["Ext.srm.form.CompanyComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + "/md/company/list",
	    fields: ['companyCode', 'companyName'],
        pageSize:10,
		autoLoad:true
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_companyCode_OR_LIKE_companyName",
    displayFields : [{
		Qheader : "公司编码",
		header : $("company.code"),
		dataIndex : "companyCode"
	}, {
		Qheader : "公司名称",
		header : $("company.name"),
		dataIndex : "companyName"
	}], 
    valueField:"companyCode",
    displayField:"companyCode", 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.organization.CompanySelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.companyCode":"companyCode",
       "model.companyName":"companyName" 
    }
});


