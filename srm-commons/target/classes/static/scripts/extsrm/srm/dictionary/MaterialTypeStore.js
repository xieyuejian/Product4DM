/**
 * 物料类型store
 */
Ext.define("Ext.srm.dictionary.MaterialTypeStore", {
	extend: "Ext.ux.data.JsonStore",
	alias: "materialTypeStore",
	xtype: "materialTypeStore",
	storeId: "materialTypeStore",
	fields: ["materialTypeCode", "materialTypeName", "materialTypeId"],
	autoLoad: true,
	constructor: function (A) {
		var A = A || {};
		this.proxy = {
			url: path_masterdata + "/md/materialtype/getall?status=1",
			type: "ajax"
		};
		this.callParent([A])
	}
});
