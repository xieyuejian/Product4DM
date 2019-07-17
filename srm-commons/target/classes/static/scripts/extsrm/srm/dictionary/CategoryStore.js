/**
 * 附件类型store
 */
Ext.define("Ext.srm.dictionary.CategoryStore", {
	extend: "Ext.ux.data.JsonStore",
	alias: "categoryStore",
	xtype: "categoryStore",
	storeId: "categoryStore",
	fields: ["itemCode", "itemName"],
	autoLoad: true,
	constructor: function (A) {
		var A = A || {};
		this.proxy = {
			url: path_masterdata + "/md/datadict/getall?groupCode=evalUationFileType",
			type: "ajax"
		};
		this.callParent([A])
	}
});
