Ext.define("Ext.srm.dictionary.TaxRateStore", {
	extend : "Ext.ux.data.JsonStore",
	alias : "taxRateStore",
	xtype : "taxRateStore",
	storeId : "taxRateStore",
	fields : [ "taxRateCode", "taxRateName", "taxRateValue" ],
	autoLoad : true,
	constructor : function(A) {
		var A = A || {};
		this.proxy = {
			url : path_masterdata + "/md/taxrate/getall",
			type : "ajax",
			actionMethods: {
                    read: "GET"
                }
		};
		this.callParent([ A ])
	}
});