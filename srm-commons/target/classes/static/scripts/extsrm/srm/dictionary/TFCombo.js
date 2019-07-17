/**
 * @class  Ext.srm.dictionary.TFCombo
 * 是/否 下拉框组件
 */
Ext.define("Ext.srm.dictionary.TFCombo", {
	extend : "Ext.srm.dictionary.SrmCombo",
	requires: ["Ext.srm.dictionary.TFArrayStore"],
	alias : "tfCombo",
	xtype : "tfCombo",
	queryMode: 'local',
	triggerAction : "all",
	valueField : "itemCode",
	displayField : "itemName", 
	innerTpl:true,
	value:true, 
	storeId:"deStore",
    storeClassName:"Ext.srm.dictionary.TFArrayStore",    
	constructor : function(conf) {
		var me = this;  
		var conf = conf || {};
		Ext.applyIf(conf,{ 
			valueField : 'itemCode',
			displayField : 'itemName',
			displayValue : 'itemName',
		    innerTpl:true,
		    fieldMapping:Ext.isEmpty(conf.column)?{
						    "model.itemCode":"itemCode",
						    "model.itemName":"itemName"
						}:{
						    "itemCode":"itemCode",
						    "itemName":"itemName"
						}
		}); 
		this.callParent([conf]);  
	}
});