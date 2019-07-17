Ext.define("Ext.srm.dictionary.PaymentTermCombo", {
    extend: "Ext.srm.dictionary.SrmCombo",
    alias: "paymentTermCombo",
    xtype: "paymentTermCombo",
    triggerAction: "all",
    valueField: "paymentTermCode",
    displayField: "paymentTermName",
    displayValue: "paymentTermName",
    innerTpl: true,
    parentObj: null,
    parentXtype: "",
    storeId: "paymentTermStore",
    storeClassName: "Ext.srm.dictionary.PaymentTermStore",
    constructor: function(B) {
        var A = this;
        var B = B || {};
        Ext.applyIf(B, {
            valueField: "paymentTermCode",
            displayField: "paymentTermName",
            displayValue: "paymentTermName",
            innerTpl: true,
            fieldMapping: Ext.isEmpty(B.column) ? {
                "model.paymentTermCode": "paymentTermCode",
                "model.paymentTermName": "paymentTermName"
            } : {
                "paymentTermCode": "paymentTermCode",
                "paymentTermName": "paymentTermName"
            }
        });
        this.callParent([B])
    }
});