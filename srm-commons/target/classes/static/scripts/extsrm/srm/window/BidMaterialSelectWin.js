/**
 *@class {Ext.srm.window.BidSureSelectWin}
 *@extend {Ext.ux.Window}
 *定标单信息选择框选择弹出窗口
 */
Ext.define("Ext.srm.window.BidMaterialSelectWin", {
    extend: "Ext.ux.Window",
    alias: "bidMaterialSelectWin", 
    constructor: function (cfg) {
        var cfg = cfg || {};
        var singleSelect = false !== cfg.singleSelect;
        var grid = this.gridPanel = this.createGrid(cfg, singleSelect); 
        cfg = Ext.apply({
            title: $("bid.bidMatDtl"),
            layout: "border",
            width: 450,
            height: 400,
            items: [grid],
            renderTo: cfg.renderTo,
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
                url: path_srment + "/bidding/bid/findbidmatdtlall",
                baseParams: cfg.baseParams || {}
            },
            cm: {
                defaultSortable: false,
                columns: [
		            {
                        Qheader: "物料编号",
                        header: $("bidMatDtl.materialCode"),
                        dataIndex: "materialCode",
                        width: 120
                    },
                    {
                        Qheader: "物料名称", 
                        header: $("bidMatDtl.materialName"),
                        dataIndex: "materialName",
                        width: 150
                    },{
                        Qheader: "付款条件", 
                        header: $("inquiry.paymentTerm"),
                        dataIndex: "bid.paymentTermCode",
                        width: 150,
                        hidden:true
                    },
                    {
                        Qheader: "付款条件", 
                        header: $("inquiry.paymentTerm"),
                        dataIndex: "bid.paymentTermName",
                        width: 150,
                        hidden:true
                    },{
                        Qheader: "招标物料", 
                        header: $("bidMatDtl.bidMatId"),
                        dataIndex: "bidMatId",
                        hidden:true
                    }
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
                text: $("bidMatDtl.materialCode")//"专家编码" + "："
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