Ext.define("Ext.srm.dictionary.ElementUnitCombo", {
			extend : "Ext.srm.dictionary.SrmCombo",
			alias : "elementUnitCombo",
			xtype : "elementUnitCombo",
			triggerAction : "all",
			valueField : "unitCode",
			displayField : "unitName",
			innerTpl : true,
			displayValue : "unitName",
			storeId : "elementUnitStore",
			storeClassName : "Ext.srm.dictionary.ElementUnitStore",
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