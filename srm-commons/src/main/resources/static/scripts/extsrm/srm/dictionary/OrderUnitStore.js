Ext.define("Ext.srm.dictionary.OrderUnitStore", {
			extend : "Ext.ux.data.JsonStore",
			alias : "orderUnitStore",
			xtype : "orderUnitStore",
			storeId : "orderUnitStore",
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