/**
 * 库存地点store
 */
Ext.define("Ext.srm.dictionary.StockLocationStore", {
    extend: "Ext.ux.data.JsonStore",
    alias: "stockLocationStore",
    xtype: "stockLocationStore",
    storeId: "stockLocationStore",
    fields: ["stockLocationCode", "stockLocationName"],
    autoLoad: true,
    constructor: function(A) {
        var A = A || {};
        this.proxy = {
            url: path_masterdata + "/md/stocklocation/getall",
            type: "ajax"
        };
        this.callParent([A])
    }
});