Ext.define("Ext.srm.dictionary.SexCombo",{extend:"Ext.srm.dictionary.SrmCombo",alias:"sexCombo",xtype:"sexCombo",triggerAction:"all",value:"M",valueField:"itemCode",displayField:"itemName",storeId:"sexStore",storeClassName:"Ext.srm.dictionary.SexArrayStore",constructor:function(B){var A=this;var B=B||{};Ext.applyIf(B,{valueField:"itemCode",displayField:"itemName",fieldMapping:{}});this.callParent([B])}});