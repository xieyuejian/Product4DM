Ext.define("Ext.srm.dictionary.PurchasingGroupStore", {
			extend : "Ext.ux.data.JsonStore",
			alias : "purchasingGroupStore",
			xtype : "purchasingGroupStore",
			storeId : "purchasingGroupStore",
			fields : ["purchasingGroupCode", "purchasingGroupName"],
			autoLoad : true,
			constructor : function(A) {
				var A = A || {};
				this.proxy = {
					url : path_masterdata + "/md/purchasinggroup/getall",
					type : "ajax"
				};
				this.callParent([A])
			}
		});