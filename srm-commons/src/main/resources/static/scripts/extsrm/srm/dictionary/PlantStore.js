Ext.define("Ext.srm.dictionary.PlantStore", {
			extend : "Ext.ux.data.JsonStore",
			alias : "plantStore",
			xtype : "plantStore",
			storeId : "plantStore",
			fields : ["plantCode", "plantName"],
			autoLoad : true,
			constructor : function(A) {
				var A = A || {};
				this.proxy = {
					url :path_masterdata + "/md/plant/getall",
					type : "ajax"
				};
				this.callParent([A])
			}
		});