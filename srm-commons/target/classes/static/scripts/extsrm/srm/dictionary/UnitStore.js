Ext.define("Ext.srm.dictionary.UnitStore", {
			extend : "Ext.ux.data.JsonStore",
			alias : "unitStore",
			xtype : "unitStore",
			storeId : "unitStore",
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