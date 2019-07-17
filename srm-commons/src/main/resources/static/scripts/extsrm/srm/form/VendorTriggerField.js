/**
 * @class Ext.srm.form.VendorTriggerField
 * @extends Ext.srm.form.SrmTriggerField 
 *  供应商弹出选择
 * 
 *     @example
 *      Ext.create('Ext.ux.form.FormPanel',{
 *          frame:true,
 *		    buttonAlign : 'center',
 *		    layout:'fit',
 *          width:400,
 *		    renderTo:Ext.getBody(),
 *		    items:[{
 *	                QfieldLabel: "供应商编码",
 *	                fieldLabel: $("materialMasterPriceApply.vendorCode") + "<font color = 'red'>*</font>",
 *	                name: "model.vendorErpCode",
 *	                hiddenName: "model.vendorErpCode",
 *	                xtype: "vendortrigger", 
 *	                allowBlank: false, 
 *	                editable:true,
 *	                keypressParam:"filter_EQ_vendorErpCode",
 * 	                baseParamsTree: {
 *                       certificationStatus: "QUALIFIED"
 *                   },
 *                   baseParams: {
 *                       "filter_IN_certificationStatus": "QUALIFIED"
 *                   },
 *                   fieldMapping:{
 *                       "model.vendorCode":"vendorCode",
 *                       "model.vendorErpCode":"vendorErpCode",
 *                       "model.vendorName":"vendorName"
 *                   },
 *	                listeners: {
 *	                	"triggerbeforeshow": "vendorErpCodeTriggerbeforeshow",
 *	                	"triggerselect": "vendorErpCodeTriggerselect",
 *	                	"triggerbaseparams": "vendorErpCodeTriggerbaseparams", 
 *	                    'clear': "vendorErpCodeClear"  
 *	                }
 *	      }]
 *		 });
 *   
 */ 
Ext.define('Ext.srm.form.VendorTriggerField', {
	extend: 'Ext.srm.form.SrmTriggerField',
	alias: 'vendortrigger',
	xtype: 'vendortrigger',
	alternateClassName: ['Ext.srm.form.VendorTriggerField'],  
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
    	"model.vendorCode":"vendorCode",
    	"model.vendorName":"vendorName" 
    }, 
    constructor:function(conf){
       var me = this;  
       var conf = conf || {};  
       this.callParent([conf]);
    } 
});