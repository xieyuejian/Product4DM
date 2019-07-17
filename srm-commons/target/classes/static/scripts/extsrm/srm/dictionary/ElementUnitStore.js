Ext.define("Ext.srm.dictionary.ElementUnitStore", {
			extend : "Ext.ux.data.JsonStore",
			alias : "elementUnitStore",
			xtype : "elementUnitStore",
			storeId : "elementUnitStore",
			fields : ["unitCode", "unitName"],
			autoLoad : true,
			constructor : function(A) {
				var A = A || {};
				this.proxy = {
					url : path_masterdata + "/md/unit/getall",
					type : "ajax"
				};
				this.callParent([A])
			}
		});