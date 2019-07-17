Ext.define("Ext.srm.dictionary.DictionaryStore", {
	extend : "Ext.ux.data.JsonStore",
	alias : "dictionaryStore",
	xtype : "dictionaryStore",
	fields : [ "itemCode", "itemName" ],
	autoLoad : true,
	constructor : function(A) {
		var A = A || {};
		this.proxy = {
			url : path_masterdata + "/md/datadict/getall",
			type : "ajax",
			extraParams : {
				"groupCode" : A.groupCode
			}
		};
		this.storeId = A.groupCode + "Store";
		this.callParent([ A ])
	}
});