/**
 * @class Ext.srm.form.UserTriggerField
 * @extends Ext.srm.form.SrmTriggerField
 * 用户渲染弹出框
 * See the example: 
 * 
 *       @example
 *       Ext.create('Ext.ux.form.FormPanel',{
 *          frame:true,
 *		    buttonAlign : 'center',
 *		    layout:'fit',
 *          width:400,
 *		    renderTo:Ext.getBody(),
 *		    items:[{
 *	                QfieldLabel: "供应商编码",
 *	                fieldLabel: $("materialMasterPriceApply.vendorCode") + "<font color = 'red'>*</font>",
 *	                name: "model.userCode",
 *	                hiddenName: "model.userCode",
 *	                xtype: "usertrigger", 
 *	                allowBlank: false, 
 *	                editable:true,
 *	                keypressParam:" ",
 * 	                baseParamsTree: { 
 *                   },
 *                   baseParams: { 
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
Ext.define('Ext.srm.form.UserTriggerField', {
	extend: 'Ext.srm.form.SrmTriggerField',
	alias: 'usertrigger',
	xtype: 'usertrigger',
	alternateClassName: ['Ext.srm.form.UserTriggerField'],  
    /**
     * 请求初始参数 
     */
    baseParams:{
        "filter_EQ_status": 1
    }, 
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Ext.srm.window.UserSelectWin",
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{
        "model.userCode":"userCode",
        "model.userName":"userName"
    }, 
    constructor:function(conf){
       var me = this;  
       var conf = conf || {};  
       this.callParent([conf]);
    } 
});