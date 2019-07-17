Ext.define("Ext.srm.dictionary.CompanyCombo", {
    extend: "Ext.srm.dictionary.SrmCombo",
    alias: "companyCombo",
    xtype: "companyCombo",
    triggerAction: "all",
    valueField: "companyCode",
    displayField: "companyName",
    displayValue: "companyName",
    innerTpl: true,
    parentObj: null,
    parentXtype: "",
    storeId: "companyStore",
    storeClassName: "Ext.srm.dictionary.CompanyStore",
    constructor: function(B) {
        var A = this;
        var B = B || {};
        Ext.applyIf(B, {
            valueField: "companyCode",
            displayField: "companyName",
            displayValue: "companyName",
            innerTpl: true,
            fieldMapping: Ext.isEmpty(B.column) ? {
                "model.companyCode": "companyCode",
                "model.companyName": "companyName"
            } : {
                "companyCode": "companyCode",
                "companyName": "companyName"
            }
        });
        this.callParent([B])
    }
});