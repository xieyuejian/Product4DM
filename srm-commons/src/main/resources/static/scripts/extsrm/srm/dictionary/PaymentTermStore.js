Ext.define("Ext.srm.dictionary.PaymentTermStore", {
    extend: "Ext.ux.data.JsonStore",
    alias: "paymentTermStore",
    xtype: "paymentTermStore",
    storeId: "paymentTermStore",
    fields: ["paymentTermCode", "paymentTermName"],
    autoLoad: true,
    constructor: function(A) {
        var A = A || {};
        this.proxy = {
            url:path_masterdata + "/md/paymentterm/getall",
            type: "ajax"
        };
        this.callParent([A])
    }
});