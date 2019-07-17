/**
 * 计量单位模糊查询下拉框
 */
Ext.define("Ext.srm.form.TaxRateComboGrid", {
	extend : "Ext.srm.form.SrmComboGrid",
	alias : "widget.taxRateComboGrid",
	xtype : "taxRateComboGrid", 
	alternateClassName : ["Ext.srm.form.TaxRateComboGrid"],  
	store: Ext.create("Ext.ux.data.JsonStore",{
		url: path_masterdata + "/md/taxrate/getall",
	    fields: ['taxRateCode', "taxRateName", "taxRateValue"],
        pageSize:10,
		autoLoad:false,
		actionMethods: {read: "GET"}
    }), 
    pageSize:10,
    queryParam:"filter_LIKE_taxRateCode_OR_LIKE_taxRateName",
    displayFields : [{
		Qheader : "税率编码",
		header : $("taxRate.taxRateCode"),
		dataIndex : "taxRateCode"
	}, {
		Qheader : "税率名称",
		header : $("taxRate.taxRateName"),
		dataIndex : "taxRateName"
	}], 
    valueField:"taxRateCode",
    displayField:"taxRateCode", 
    /**
     * 创建弹出窗口类名 
     */
  //  selectWinClassName:"Md.busiparams.TaxRateSelectWin", 
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{ 
       "model.taxRateCode":"taxRateCode",
       "model.taxRateName":"taxRateName" 
    }
});


