Ext.define("Ext.srm.dictionary.PlantByMaterialCombo", {
	extend : "Ext.srm.dictionary.SrmCombo",
	alias : "plantByMaterialCombo",
	xtype : "plantByMaterialCombo",
	triggerAction : "all",
	valueField : "plantCode",
	displayField : "plantName",
	innerTpl : true,
	displayValue : "plantName",
	storeId : "plantByMaterialStore",
	storeClassName : "Ext.srm.dictionary.PlantByMaterialStore",
	constructor : function(B) {
		var A = this;
		var B = B || {};
		Ext.applyIf(B, {
			valueField : "plantCode",
			displayField : "plantName",
			displayValue : "plantName",
			innerTpl : true,
			fieldMapping : Ext.isEmpty(B.column) ? {
				"model.plantCode" : "plantCode",
				"model.plantName" : "plantName"
			} : {
				"plantCode" : "plantCode",
				"plantName" : "plantName"
			}
		});
		this.callParent([ B ])
	}
});