Ext.define("Ext.srm.dictionary.YNCombo",{extend:"Ext.srm.dictionary.SrmCombo",requires:["Ext.srm.dictionary.YNArrayStore"],alias:"ynCombo",xtype:"ynCombo",queryMode:"local",triggerAction:"all",valueField:"itemCode",displayField:"itemName",value:"Y",storeId:"ynStore",storeClassName:"Ext.srm.dictionary.YNArrayStore",constructor:function(B){var A=this;var B=B||{};Ext.applyIf(B,{valueField:"itemCode",displayField:"itemName",fieldMapping:{}});this.callParent([B])}});