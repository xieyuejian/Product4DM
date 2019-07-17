/**
 * 生效、失效store
 */
Ext.define("Ext.srm.dictionary.MaterialGroupStore", {
	extend: "Ext.ux.data.JsonStore",
	alias: "materialGroupStore",
	xtype: "materialGroupStore",
	storeId: "materialGroupStore",
	fields: ["materialGroupCode", "materialGroupName","materialGroupId"],
	autoLoad: true,
	constructor: function (A) {
		var A = A || {};
		this.proxy = {
			url: path_masterdata + "/md/materialgroup/getall?status=1",
			type: "ajax"
		};
		this.callParent([A])
	}
});
