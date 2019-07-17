Ext.define("Ext.srm.dictionary.PlantByMaterialStore", {
	extend : "Ext.ux.data.JsonStore",
	alias : "plantByMaterialStore",
	xtype : "plantByMaterialStore",
	storeId : "plantByMaterialStore",
	fields : [ "plantCode", "plantName" ] , 
    autoLoad : false,
	constructor : function(A) {
		var A = A || {};
		this.proxy = {
			url : path_srment
					+ "/mmd/material/Material_findPlantByMaterial.action",
			type : "ajax"  
		}; 
		A.autoLoad = Ext.isEmpty(A.autoLoad) ? this.autoLoad : A.autoLoad;
		this.callParent([ A ])
	}
});