Ext.define("Ext.srm.dictionary.EvalTypeCombo",{extend:"Ext.srm.dictionary.SrmCombo",alias:"evalTypeCombo",xtype:"evalTypeCombo",triggerAction:"all",valueField:"evalTypeCode",displayField:"evalTypeName",displayValue:"evalTypeCode",innerTpl:true,parentObj:null,parentXtype:"",storeId:"evalTypeStore",storeClassName:"Ext.srm.dictionary.EvalTypeStore",constructor:function(B){var A=this;var B=B||{};Ext.applyIf(B,{valueField:"evalTypeCode",displayField:"evalTypeName",displayValue:"evalTypeCode",innerTpl:true,fieldMapping:Ext.isEmpty(B.column)?{"model.evalTypeCode":"evalTypeCode","model.evalTypeName":"evalTypeName"}:{"evalTypeCode":"evalTypeCode","evalTypeName":"evalTypeName"}});this.callParent([B])}});