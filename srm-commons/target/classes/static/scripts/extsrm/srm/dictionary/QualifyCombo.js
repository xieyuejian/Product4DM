/**
 * 合格、不合格下拉
 */
Ext.define("Ext.srm.dictionary.QualifyCombo", {
	extend: "Ext.srm.dictionary.SrmCombo",
	requires: ["Ext.srm.dictionary.QualifyArrayStore"],
	alias: "qualifyCombo",
	xtype: "qualifyCombo",
	queryMode: "local",
	triggerAction: "all",
	valueField: "value",
	displayField: "text",
	value: "Y",
	storeId: "qualifyArrayStore",
	storeClassName: "Ext.srm.dictionary.QualifyArrayStore",
	constructor: function (B) {
		var A = this;
		var B = B || {};
		Ext.applyIf(B, {
			valueField: "value",
			displayField: "text",
			fieldMapping: {}
		});
		this.callParent([B])
	}
});
