Ext.define("Ext.srm.dictionary.PricingUnitCombo", {
			extend : "Ext.srm.dictionary.SrmCombo",
			alias : "pricingUnitCombo",
			xtype : "pricingUnitCombo",
			triggerAction : "all",
			valueField : "unitCode",
			displayField : "unitName",
			innerTpl : true,
			displayValue : "unitName",
			storeId : "pricingUnitStore",
			storeClassName : "Ext.srm.dictionary.PricingUnitStore",
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