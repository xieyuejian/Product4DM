/**
 * @class Ext.srm.dictionary.TFArrayStore
 * 是/否 数字字典
 * data:[[true,$("dict.yes")],[false,$("dict.no")]]
 */
Ext.define("Ext.srm.dictionary.TFArrayStore", {
	extend : "Ext.data.ArrayStore",
	alias : "tfStore",
	xtype : "tfStore",
	storeId: 'tfStore',
	fields:[ "itemCode","itemName"],  
	data:[[true,$("dict.yes")],[false,$("dict.no")]],
	constructor:function(conf){  
		var conf = conf || {};
		this.callParent([conf]);
	}
});