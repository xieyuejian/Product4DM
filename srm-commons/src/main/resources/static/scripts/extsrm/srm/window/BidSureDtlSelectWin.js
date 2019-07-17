/**
 *@class {Ext.srm.window.BidSureSelectWin}
 *@extend {Ext.ux.Window}
 *定标单信息选择框选择弹出窗口
 */
Ext.define("Ext.srm.window.BidSureDtlSelectWin", {
    extend: "Ext.ux.Window",
    alias: "bidSureDtlSelectWin", 
    constructor: function (cfg) {
        var cfg = cfg || {};
        var singleSelect = false !== cfg.singleSelect;
        var grid = this.gridPanel = this.createGrid(cfg, singleSelect); 
        cfg = Ext.apply({
            title: "定标明细",
            layout: "border",
            width: 750,
            height: 400,
            items: [grid], 
            renderTo : cfg.moduleId,
		    constrain:true,
            listeners: {
                "hide": function () {
                    grid.getSelectionModel().clearSelections();
                }
            }
        }, cfg); 

        this.callParent([cfg]);

        if (Ext.isFunction(cfg.select)) {
            this.on("select", cfg.select);
        }
    },

    /**
     * 创建grid
     * @param cfg 配置项
     * @param singleSelect 是否单选
     * @returns {Ext.ux.grid.GridPanel} gridPanel
     */
    createGrid: function (cfg, singleSelect) {
        var win = this;
        var grid = this.gridPanel = Ext.create("Ext.ux.grid.GridPanel",{
            border: true,
            sm: {singleSelect: singleSelect},
            viewConfig: {forceFit: true, stripeRows: true},
            store: {
                url: path_srment + "/bd/bid/BidSure_getDetailJson.action",
                baseParams: cfg.baseParams || {}
            },
            cm: {
                defaultSortable: false,
                columns: [
		            //{Qheader: "定标单号", header: $("bidSure.bidSureNo"), dataIndex: "bidSure.bidSureNo", width: 150},
		            {Qheader: "ID", header: $("bidSureDtl.bidSureDtlId"), dataIndex: "bidSureDtlId", disabled: true},
		            {Qheader: "物料编码", header: $("bidSureDtl.materialCode"), dataIndex: "materialCode"},
                    {Qheader: "物料名称", header: $("bidSureDtl.materialName"), dataIndex: "materialName"},
                    {Qheader: "工厂编码", header: $("bidSureDtl.plantCode"), dataIndex: "plantCode"},
                    {Qheader: "物料单位", header: $("bidSureDtl.unitCode"), dataIndex: "unitCode"},
                    {Qheader: "工厂名称", header: $("bidSureDtl.plantName"), dataIndex: "plantName"},
                    {Qheader: "币种", header: $("bidSureDtl.currencyCode"), dataIndex: "currencyCode"},
                    {Qheader: "税率", header: $("bidSureDtl.taxRate"), dataIndex: "taxRate"},
                    {Qheader: "税率编码", header: $("bidSureDtl.taxRateCode"), dataIndex: "taxRateCode"},
                    {Qheader: "付款条件说明", header: $("bidSureDtl.paymentTerm"), dataIndex: "paymentTerm"},
                    {Qheader: "供应商编码", header: $("bidSureDtl.vendorErpCode"), dataIndex: "vendorErpCode"},
                    {Qheader: "供应商名称", header: $("bidSureDtl.vendorName"), dataIndex: "vendorName"},
                    {Qheader: "采购数量", header: $("bidSureDtl.quality"), dataIndex: "quality", align: "right"},
                    {Qheader: "分配数量", header: $("bidSureDtl.allotNum"), dataIndex: "allotNum", align: "right"},
                    {Qheader: "含税单价", header: $("bidMatDtl.taxPrice"), dataIndex: "factoryPrice"},
                    {Qheader: "未税单价", header: $("bidMatDtl.notTaxPrice"), dataIndex: "factoryNotPrice"},
                    {Qheader: "交货日期", header: $("label.deliveryDate"), dataIndex: "deliveryDate",type: "date", dateFormat: "Y-m-d", renderer: Ext.util.Format.dateRenderer("Y-m-d")}
		        ]
            },
            tbar: [{
                text: $("button.select"),
                iconCls: "icon-save",
                handler: function (_self) {
                    var selectFlag = grid.doSelect(win, singleSelect);
                    if (selectFlag) {
                        var store = grid.getStore();
                        _self.ownerCt.find("name", "materialCode")[0].setValue("");
                        store.proxy.extraParams.filter_LIKE_materialCode = "";
                        store.load({params: {start: 0, limit: 20}});
                        win.hide();
                    }
                }
            }, {
                text: $("button.return"),
                iconCls: "icon-return",
                handler: function (_self) {
                    var store = grid.getStore();
                    _self.ownerCt.find("name", "materialCode")[0].setValue("");
                    store.proxy.extraParams.filter_LIKE_materialCode = "";
                    store.removeAll();
                    win.hide();
                }
            }, "->", {
                xtype: "label",
                text: $("bidSureDtl.materialCode")//"专家编码" + "："
            }, {
                name: "materialCode",
                xtype: "textfield",
                width: 100
            }, {
                text: $("button.search"),
                iconCls: "icon-search",
                handler: function (_self) {
                    var store = grid.getStore();
                    var materialCode = _self.ownerCt.find("name", "materialCode")[0].getValue();
                    store.proxy.extraParams.filter_LIKE_materialCode = materialCode;
                    store.load({params: {start: 0, limit: 20}});
                }
            }],
            listeners: {
                "rowdblclick": function (g, i) {
                    if (grid.doSelect(win, singleSelect)) {
                        var store = grid.getStore();
                        store.proxy.extraParams.filter_LIKE_materialCode = "";
                        store.removeAll();
                        win.hide();
                    }
                }
            },
            //private 自定义
            //选择
            doSelect: function (win, singleSelect) {
                var grid = this, sm = grid.getSelectionModel(), selections = sm.getSelection();
                if (selections.length < 1) {
                    Q.tips($("message.pleaseSelect"), "E");
                    return false;
                }
                if (singleSelect) {
                    win.fireEvent("select", grid, selections[0]);
                } else {
                    win.fireEvent("select", grid, selections);
                }

                Q.tips($("message.selectSuccess"));
                return true;
            }
        });
        return grid;
    }
});