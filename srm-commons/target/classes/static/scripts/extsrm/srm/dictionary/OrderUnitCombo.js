Ext.define("Ext.srm.dictionary.OrderUnitCombo", {
			extend : "Ext.srm.dictionary.SrmCombo",
			alias : "orderUnitCombo",
			xtype : "orderUnitCombo",
			triggerAction : "all",
			valueField : "unitCode",
			displayField : "unitName",
			innerTpl : true,
			displayValue : "unitName",
			storeId : "orderUnitStore",
			storeClassName : "Ext.srm.dictionary.OrderUnitStore",
			constructor : function(B) {
				var A = this;
				var B = B || {};
				Ext.applyIf(B, {
							valueField : "unitCode",
							displayField : "unitName",
							displayValue : "unitName",
							innerTpl : true,
							fieldMapping : Ext.isEmpty(B.column) ? {
								"model.unitCode" : "unitCode",
								"model.unitName" : "unitName"
							} : {
								"unitCode" : "unitCode",
								"unitName" : "unitName"
							}
						});
				this.callParent([B])
			}
		});