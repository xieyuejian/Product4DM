Ext.define("Ext.srm.window.MaterialSelectWin", {
    extend: "Ext.ux.Window",
    requires: ["Ext.ux.tree.TreePanel"],
    constructor: function(B) {
        B = B || {};
        Ext.applyIf(B, {
            forceFit: true,
            singleSelect: true,
            constrain: true,
            renderTo: B.moduleId
        });
        var C = this.gridPanel = this.createGrid(B);
        var A = this.createTree(C, B);
        B = Ext.apply({
            title: $("materialInfo.title"),
            layout: "border",
            width: 800,
            height: 500,
            items: [A, C],
            listeners: {
                "hide": function() {
                    C.getSelectionModel().clearSelections()
                }
            }
        }, B);
        this.callParent([B]);
        if (Ext.isFunction(B.select)) {
            this.on("select", B.select)
        }
    },
    createTree: function(C, B) {
        var A = Ext.create("Ext.ux.tree.TreePanel", {
            margin: "15px 5px 74px 30px",
            width: 140,
            text: $("materialInfo.group"),
            url: path_masterdata + "/md/materialgroup/gettree?node={0}",
            treeType: TreeType.INFO,
            header: {
                hidden: true
            },
            nodeName: "filter_LIKE_materialGroup_codes",
            grid: C
        });
        A.getStore().on("beforeload", function(E, D) {
            E.proxy.extraParams = B.baseParamsTree || {}
        });
        return A
    },
    createGrid: function(D) {
        var C = this;
        var F = D.singleSelect;
        var A = Ext.create("Ext.toolbar.Toolbar", {
            style: "background-color:transparent",
            margin: "15px 0 10px -6",
            broder: "0px",
            items: [{
                xtype: "label",
                margin: "0 6 0 0",
                text: $("materialInfo.code")
            }, {
                name: "materialCode",
                margin: "0 10 0 0",
                xtype: "textfield",
                width: 150
            }, "->", {
                xtype: "label",
                margin: "0 6 0 28",
                text: $("materialInfo.name")
            }, {
                name: "materialName",
                margin: "0 10 0 0",
                xtype: "textfield",
                width: 150
            }, "->", {
                text: $("button.search"),
                iconCls: "icon_srm_search",
                style: "width:80px;height:30px;",
                margin: "0 0 0 10",
                ui: "blue-btn",
                handler: function(G) {
                    var H = B.getStore();
                    var J = B.getTopToolbar().query("textfield[name=materialCode]")[0].getValue();
                    var I = B.getTopToolbar().query("textfield[name=materialName]")[0].getValue();
                    H.proxy.extraParams.filter_LIKE_materialCode = J;
                    H.proxy.extraParams.filter_LIKE_materialName = I;
                    H.load({
                        params: {
                            start: 0,
                            limit: 20
                        }
                    })
                }
            }]
        });
        var E = Ext.create("Ext.toolbar.Toolbar", {
            dock: "bottom",
            style: {
                margin: "10px 0 10px 0",
                border: "0px"
            },
            items: ["->", {
                text: $("label.return"),
                margin: "0 30 10 0",
                style: "width:80px;height:34px;",
                ui: "gray-btn",
                handler: function() {
                    C.hide()
                }
            }, {
                text: $("label.select"),
                margin: "0 0 10 0",
                style: "width:80px;height:34px;",
                ui: "blue-btn",
                handler: function() {
                    var G = B.doSelect(C, F);
                    if (G && F) {
                        C.hide()
                    }
                }
            }]
        });
        
        
        var columns = [
        	{
                header: $("material.materialId"),
                dataIndex: "materialId",
                disabled: true
            }, {
                header: $("materialInfo.code"),
                dataIndex: "materialCode",
                width: 80
            }, {
                header: $("materialInfo.name"),
                dataIndex: "materialName"
            }, {
                header: $("materialInfo.brand"),
                dataIndex: "materialBrand",
                disabled: true
            }, {
                header: $("materialInfo.materialDesc"),
                dataIndex: "materialDesc",
                disabled: true
            }, {
                header: $("materialInfo.baseUnit"),
                dataIndex: "baseUnitCode",
                width: 80
            }, {
                header: $("materialInfo.type"),
                dataIndex: "materialType",
                sortable: false,
                renderer: function(H, I, G) {
                    return H ? H.materialTypeName : ""
                }
            }, {
                header: $("materialInfo.group"),
                dataIndex: "materialGroup",
                sortable: false,
                renderer: function(H, I, G) {
                    return H ? H.materialGroupName : ""
                }
            }
        ];
        
        if(undefined != D.columns && D.columns.length > 0){
        	
        	columns = D.columns;
        	
        }
        
        
        
        var B = Ext.create("Ext.ux.grid.GridPanel", {
            border: false,
            pageSize: 14,
            ui: "small-grid",
            style: {
                padding: "0px 30px 0px 5px",
                border: "1px"
            },
            sm: {
                singleSelect: F
            },
            pageSize: 14,
            store: {
                url: D.url || path_masterdata + "/md/material/findbymaterialgroup",
                baseParams: D.baseParams || {}
            },
            cm: {
                defaultSortable: false,
                columns: columns
            },
            dockedItems: [E, A],
            listeners: {
                "itemdblclick": function(G, H) {
                    if (B.doSelect(C, F)) {
                        C.hide()
                    }
                }
            },
            doSelect: function(I, K) {
                var H = this,
                    J = H.getSelectionModel(),
                    G = J.getSelection();
                if (G.length < 1) {
                    Q.tips($("message.pleaseSelect"), "E");
                    return false
                }
                if (K) {
                    I.fireEvent("select", H, G[0])
                } else {
                    I.fireEvent("select", H, G)
                }
                Q.tips($("message.selectSuccess"));
                return true
            }
        });
        return B
    }
});