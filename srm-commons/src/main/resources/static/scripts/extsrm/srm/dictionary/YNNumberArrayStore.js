Ext.define("Ext.srm.dictionary.YNNumberArrayStore", {
    extend: "Ext.data.ArrayStore",
    alias: "ynNumberStore",
    xtype: "ynNumberStore",
    storeId: "ynNumberStore",
    fields: ["itemCode", "itemName"],
    data: [
        ["0", $("dict.no")],
        ["1", $("dict.yes")]
    ],
    constructor: function(A) {
        var A = A || {};
        this.callParent([A])
    }
});