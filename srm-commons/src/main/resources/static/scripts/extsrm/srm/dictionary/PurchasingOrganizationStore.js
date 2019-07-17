Ext.define("Ext.srm.dictionary.PurchasingOrganizationStore", {
	extend : "Ext.ux.data.JsonStore",
	alias : "purchasingOrganizationStore",
	xtype : "purchasingOrganizationStore",
	storeId : "purchasingOrganizationStore",
	fields : ["purchasingOrgCode", "purchasingOrgName"],
	autoLoad : true,
	constructor : function(A) {
		var A = A || {};
		this.proxy = {
			url :path_masterdata + "/md/purchasingorganization/getall",
			type : "ajax"
		};
		this.callParent([A])
	}
});