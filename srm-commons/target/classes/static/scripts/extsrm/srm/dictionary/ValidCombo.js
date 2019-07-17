/**
 * 生效、失效下拉
 */
Ext.define("Ext.srm.dictionary.ValidCombo", {
	extend: "Ext.srm.dictionary.SrmCombo",
	requires: ["Ext.srm.dictionary.ValidArrayStore"],
	alias: "validCombo",
	xtype: "validCombo",
	queryMode: "local",
	triggerAction: "all",
	valueField: "value",
	displayField: "text",
	value: "Y",
	storeId: "validArrayStore",
	storeClassName: "Ext.srm.dictionary.ValidArrayStore",
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
