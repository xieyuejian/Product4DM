Ext.define("Ext.srm.dictionary.CompanyStore", {
    extend: "Ext.ux.data.JsonStore",
    alias: "companyStore",
    xtype: "companyStore",
    storeId: "companyStore",
    fields: ["companyCode", "companyName"],
    autoLoad: true,
    constructor: function(A) {
        var A = A || {};
        this.proxy = {
            url: path_masterdata + "/md/company/getall",
            type: "ajax"
        };
        this.callParent([A])
    }
});