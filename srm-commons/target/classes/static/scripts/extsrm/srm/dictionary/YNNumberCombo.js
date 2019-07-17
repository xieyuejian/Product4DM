Ext.define("Ext.srm.dictionary.YNNumberCombo", {
    extend: "Ext.srm.dictionary.SrmCombo",
    requires: ["Ext.srm.dictionary.YNNumberArrayStore"],
    alias: "ynNumberCombo",
    xtype: "ynNumberCombo",
    queryMode: "local",
    triggerAction: "all",
    valueField: "itemCode",
    displayField: "itemName",
    value: "0",
    storeId: "ynNumberStore",
    storeClassName: "Ext.srm.dictionary.YNNumberArrayStore",
    constructor: function(B) {
        var A = this;
        var B = B || {};
        Ext.applyIf(B, {
            valueField: "itemCode",
            displayField: "itemName",
            fieldMapping: {}
        });
        this.callParent([B])
    }
});