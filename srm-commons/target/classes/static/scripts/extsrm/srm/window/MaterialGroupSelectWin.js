/**
 *@class {Md.material.MaterialGroupSelectWin}
 *@extend {Ext.ux.Window}
 *物料组信息选择框选择弹出窗口
 */
Ext.define("Md.material.MaterialGroupSelectWin", {
    extend: "Ext.ux.Window",
    alias: "materialGroupSelectWin", 
    constructor: function (cfg) {
        var cfg = cfg || {};
        var singleSelect = false !== cfg.singleSelect;
        var grid = this.gridPanel = this.createGrid(cfg, singleSelect); 
        cfg = Ext.apply({
            title: $("materialGroup.materialGroupSelectWin"),
            layout: "border",
            width: 700,
            height: 500,
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
        //查询工具条
        var queryBar = Ext.create("Ext.toolbar.Toolbar", {
            style: "background-color:transparent",
            margin: '15px 0 10px -6',
            broder: '0px',
            items: [ 
            {
                xtype: "label",
                margin: "0 6 0 0",
                 text: $("materialGroup.code")//"物料组编码" + "："
            }, {
                name: "materialGroupCode",
                margin: "0 10 0 0",
                xtype: "textfield",
                width: 150
            }, "->", {
                xtype: "label",
                margin: "0 6 0 28",
                text: $("materialGroup.name")//"物料组编码" + "："
            }, {
                name: "materialGroupName",
                margin: "0 10 0 0",
                xtype: "textfield",
                width: 150
            }, "->", {
                text: $("button.search"),
                iconCls: "icon_srm_search",
                style: "width:80px;height:30px;",
                margin: "0 0 0 10",
                ui: "blue-btn",
                handler: function() {
                    var store = grid.getStore();
                    var materialGroupCode = grid.getTopToolbar().query("textfield[name=materialGroupCode]")[0].getValue();
                    store.proxy.extraParams.filter_LIKE_materialGroupCode = materialGroupCode; 
                    var materialGroupName =  grid.getTopToolbar().query("textfield[name=materialGroupName]")[0].getValue();
                    store.proxy.extraParams.filter_LIKE_materialGroupName = materialGroupName;
                    store.load({params: {start: 0, limit: 20}});
                }
            }]
        });
        //底部按钮
        var tBar = Ext.create("Ext.toolbar.Toolbar", {
            dock: 'bottom',
            style: {
                margin: '10px 0 10px 0',
                border: '0px'
            },
            items: ["->", {
                text: $("label.return"),
                margin: "0 30 10 0",
                style: "width:80px;height:34px;",
                ui: 'gray-btn',
                handler: function() {
                    var store = grid.getStore();
                    grid.getTopToolbar().query("textfield[name=materialGroupCode]")[0].setValue("");
                    store.proxy.extraParams.filter_LIKE_materialGroupCode = "";
                    grid.getTopToolbar().query("textfield[name=materialGroupName]")[0].setValue("");
                    store.proxy.extraParams.filter_LIKE_materialGroupName = "";
                    store.removeAll();
                    win.hide();                    
                }
            }, {
                text: $("label.select"),
                margin: "0 0 10 0",
                style: "width:80px;height:34px;",
                ui: 'blue-btn',
                handler: function() {
                    var selectFlag = grid.doSelect(win, singleSelect);
                    if (selectFlag) {
                        var store = grid.getStore();
                        grid.getTopToolbar().query("textfield[name=materialGroupCode]")[0].setValue("");
                        store.proxy.extraParams.filter_LIKE_materialGroupCode = "";
                        grid.getTopToolbar().query("textfield[name=materialGroupName]")[0].setValue("");
                        store.proxy.extraParams.filter_LIKE_materialGroupName = "";
                        store.load({params: {start: 0, limit: 20}});
                        win.hide();
                    }                    
                }
            }]
        });
        
        var grid = this.gridPanel = Ext.create("Ext.ux.grid.GridPanel",{
            border: false,
            pageSize:14,
            ui:'small-grid',
            style:{padding:'0px 30px 0px 30px', border:'1px'},
            sm: {singleSelect: singleSelect},
            store: {
                url: path_masterdata + "/md/materialgroup/getall?isPage=1",
                baseParams: cfg.baseParams || {}
            },
            cm: {
                defaultSortable: false,
                columns: [ 
                    {header: $("materialGroup.ID"), dataIndex : "materialGroupId", disabled: true},
		            {Qheader: "物料组编码", header: $("materialGroup.code"), dataIndex: 'materialGroupCode', width: 120 },
                    {Qheader: "物料组名称", header: $("materialGroup.name"), dataIndex: 'materialGroupName', width: 150 } 
		        ]
            },
            dockedItems: [tBar,queryBar],
            listeners: {
                "rowdblclick": function (g, i) {
                    if (grid.doSelect(win, singleSelect)) {
                        var store = grid.getStore();
                        store.proxy.extraParams.filter_LIKE_materialGroupCode = "";
                        store.proxy.extraParams.filter_LIKE_materialGroupName = "";
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