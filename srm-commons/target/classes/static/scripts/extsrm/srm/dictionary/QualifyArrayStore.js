/**
 * 合格、不合格store
 */
Ext.define("Ext.srm.dictionary.QualifyArrayStore", {
	extend: "Ext.data.ArrayStore",
	alias: "qualifyArrayStore",
	xtype: "qualifyArrayStore",
	storeId: "qualifyArrayStore",
	fields: ["value", "text"],
	data: [["Y", $("dict.qualified")], ["N", $("dict.unqualified")]],
	autoLoad: true,
	constructor: function (A) {
		var A = A || {};
		this.callParent([A])
	}
});
