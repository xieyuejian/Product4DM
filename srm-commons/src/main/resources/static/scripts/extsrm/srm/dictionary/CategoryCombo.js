/**
 * 附件类型下拉
 */
Ext.define("Ext.srm.dictionary.CategoryCombo", {
	extend: "Ext.srm.dictionary.SrmCombo",
	requires: ["Ext.srm.dictionary.CategoryStore"],
	alias: "categoryCombo",
	xtype: "categoryCombo",
	queryMode: "local",
	triggerAction: "all",
	valueField: "itemCode",
	displayField: "itemName",
	value: "Y",
	storeId: "categoryStore",
	storeClassName: "Ext.srm.dictionary.CategoryStore",
	constructor: function (B) {
		var A = this;
		var B = B || {};
		Ext.applyIf(B, {
			valueField: "itemCode",
			displayField: "itemName",
			fieldMapping: {}
		});
		this.callParent([B])
	}
});
