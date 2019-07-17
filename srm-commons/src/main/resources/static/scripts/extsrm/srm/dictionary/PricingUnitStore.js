Ext.define("Ext.srm.dictionary.PricingUnitStore", {
			extend : "Ext.ux.data.JsonStore",
			alias : "pricingUnitStore",
			xtype : "pricingUnitStore",
			storeId : "pricingUnitStore",
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