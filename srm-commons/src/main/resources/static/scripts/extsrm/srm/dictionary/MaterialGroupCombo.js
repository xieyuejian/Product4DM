/**
 * 生效、失效下拉
 */
Ext.define("Ext.srm.dictionary.MaterialGroupCombo", {
	extend: "Ext.srm.dictionary.SrmCombo",
	requires: ["Ext.srm.dictionary.MaterialGroupStore"],
	alias: "materialGroupCombo",
	xtype: "materialGroupCombo",
	queryMode: "local",
	triggerAction: "all",
	valueField: "materialGroupCode",
	displayField: "materialGroupName",
	value: "Y",
	storeId: "materialGroupStore",
	storeClassName: "Ext.srm.dictionary.MaterialGroupStore",
	constructor: function (B) {
		var A = this;
		var B = B || {};
		Ext.applyIf(B, {
			valueField: "materialGroupCode",
			displayField: "materialGroupName",
			fieldMapping: {}
		});
		this.callParent([B])
	}
});
