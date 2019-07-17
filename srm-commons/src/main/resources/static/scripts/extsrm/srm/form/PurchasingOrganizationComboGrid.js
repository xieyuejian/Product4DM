/**
 * 采购组织模糊查询下拉框
 */
Ext.define("Ext.srm.form.PurchasingOrganizationComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.purchasingOrganizationComboGrid",
	xtype : "purchasingOrganizationComboGrid2", 
	alternateClassName : ["Ext.srm.form.PurchasingOrganizationComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + "/md/purchasingorganization/list",
	    fields: ['purchasingOrgCode', 'purchasingOrgName'],
        pageSize:10,
		autoLoad:false
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_purchasingOrgCode_OR_LIKE_purchasingOrgName",
    displayFields:[{
	  Qheader: "采购组织编码",
	  header: $("purchasingOrganization.purchasingOrgCode"),
	  dataIndex: 'purchasingOrgCode'
    },{
	  Qheader: "采购组织名称",
	  header: $("purchasingOrganization.purchasingOrgName"),
	  dataIndex: 'purchasingOrgName' 
    }], 
    valueField:"purchasingOrgCode",
    displayField:"purchasingOrgCode",
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.organization.PurchasingOrganizationSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.purchasingOrgCode":"purchasingOrgCode",
       "model.purchasingOrgName":"purchasingOrgName" 
    },
    minChars : 1
});


