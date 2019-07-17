Ext.define("Ext.srm.dictionary.CurrencyStore", {
			extend : "Ext.ux.data.JsonStore",
			alias : "currencyStore",
			xtype : "currencyStore",
			storeId : "currencyStore",
			fields : ["currencyCode", "currencyName"],
			autoLoad : true,
			constructor : function(A) {
				var A = A || {};
				this.proxy = {
					url : path_masterdata + "/md/currency/getall",
					type : "ajax"
				};
				this.callParent([A])
			}
		});