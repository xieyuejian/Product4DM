/**
 * 生效、失效store
 */
Ext.define("Ext.srm.dictionary.ValidArrayStore", {
	extend: "Ext.data.ArrayStore",
	alias: "validArrayStore",
	xtype: "validArrayStore",
	storeId: "validArrayStore",
	fields: ["value", "text"],
	data: [["Y", $("label.valid")], ["N", $("label.invalid")]],
	autoLoad: true,
	constructor: function (A) {
		var A = A || {};
		this.callParent([A])
	}
});
