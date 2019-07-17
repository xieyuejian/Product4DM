/**
 * @class {Cp.delivery.PurcahseOrderDetailSelectWin}
 * @extend {Ext.ux.Window}
 * 采购订单明细选择
 * 
 * @param {object} cfg :
 *  @param {Boolean}    cfg.singleSelect 是否单选，缺省为多选
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 *  @param {Function}   cfg.select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *               单选时参数：(grid, record)
 *               多选时参数：(grid, selections)
 */
Ext.define('Cp.delivery.PurcahseOrderDetailSelectWin', {
    extend: 'Ext.ux.Window',
    constructor: function(cfg) {
        cfg = cfg || {};
        var singleSelect = false !== cfg.singleSelect;
        var gridOut = this.gridOut = cfg.gridOut;
        var grid = this.grid = this.createGrid(cfg, singleSelect);

        var cfg = Ext.apply({
            title: $('purchaseOrderDetail.title'),
            layout: 'border',
            width: 800,
            height: 400,
            constrain: true,
            renderTo: cfg.moduleId,
            items: [grid]
        }, cfg);


        this.callParent([cfg]);

        if (!Ext.isEmpty(cfg.select) && Ext.isFunction(cfg.select)) {
            this.on('select', cfg.select);
        }
    },
    createGrid: function(cfg, singleSelect) {
        var win = this;
        var tBar = Ext.create('Ext.toolbar.Toolbar', {
            dock: 'top',
/*            style:{
            	margin: '10px 0 10px 0',
            	background:'#FFF',
            	padding:'0px',
            	broder:'0px',
            },*/
            style: "background-color:transparent;border-style: none",
            margin: '10px 0 10px 0',
            
            items: [{
    			xtype : "label",
    			text : $("porder.vendorTime")
    		},{
                name: 'filter_GE_vendorTime',
                xtype: 'datefield',
                width : '18%',
                format: 'Y-m-d'
            }, {
    			xtype : "label",
    			text : $("label.to")
    		}, {
                name: 'filter_LE_vendorTime',
                xtype: 'datefield',
                width : '18%',
                format: 'Y-m-d'
            }, {
    			xtype : "label",
    			text : $("materialInfo.code")
    		},{
                xtype : "textfield",
                width : '20%',
                name: 'filter_LIKE_materialCode'
            }, {
                text: $('button.search'),
                ui: 'blue-btn',
                iconCls : "icon-search",
                style: "width:80px;height:30px;",
//                margin : "0 10 0 10",
//                width:80,
//    			height:30,
                handler: function() {
                	var store = grid.getStore();
                	var startTime = tBar.query("datefield[name=filter_GE_vendorTime]")[0].getRawValue();
                	var endTime = tBar.query("datefield[name=filter_LE_vendorTime]")[0].getRawValue();
                	var materialCode = tBar.query("textfield[name=filter_LIKE_materialCode]")[0].getValue();
    				store.proxy.extraParams.filter_GE_vendorTime = startTime;
    				store.proxy.extraParams.filter_LE_vendorTime = endTime;
    				store.proxy.extraParams.filter_LIKE_materialCode = materialCode;
    				store.proxy.extraParams.filter_LIKE_purchaseOrder_purchaseOrderNo = {};
    				store.proxy.extraParams.filter_LIKE_materialName = {};
                    store.load({
                        params: {
                            start: 0,
                            limit: 20
                        }
                    });
                }
            }, {
                text: $('button.moreData'),
                iconCls: 'icon-view',
                ui: 'blue-btn',
                build: true,
                style: "width:80px;height:30px;",
//                width:80,
//    			height:30,
//                margin : "0 30 0 20",
                /* power.search */
                handler: function() {
                    var win = new Cp.delivery.PurcahseOrderDetailSearchWin();
                    var store = grid.getStore();
                    win.on('search', function(data) {
                    	var startTime = tBar.query("datefield[name=filter_GE_vendorTime]")[0].getRawValue();
                    	var endTime = tBar.query("datefield[name=filter_LE_vendorTime]")[0].getRawValue();
                    	var materialCode = tBar.query("textfield[name=filter_LIKE_materialCode]")[0].getValue();
        				store.proxy.extraParams.filter_GE_vendorTime = startTime;
        				store.proxy.extraParams.filter_LE_vendorTime = endTime;
        				store.proxy.extraParams.filter_LIKE_materialCode = materialCode;
                        Ext.apply(store.proxy.extraParams, data);
                        store.load({
                            params: {
                                start: 0,
                                limit: 20
                            }
                        });
                    });
                    win.show();
                    this.handler = function() {
                        win.show();
                    }
                }
            }]
        });

        var bBar = Ext.create('Ext.toolbar.Toolbar', {
            dock: 'bottom',
//            style: {
//                margin: '10px 0 10px 0',
//                background: '#FFF',
//                border: '1px'
//            },
            style: {
                margin: '0px 0px 0px 20px',
                border: '0px'
            },
            items: ['->', {
                text: $('button.return'),
                ui: 'gray-btn',
                margin : "5 20 5 0",
                style : "width:66px;height:26px;",
//                style: {
//                    padding: '8px 15px 8px 15px',
//                    margin: '0 12px 0 12px'
//                },
                handler: function() {
                    win.close();
                }
            }, {
                text: $('button.select'),
                ui: 'blue-btn',
                margin : "5 20 5 0",
                style : "width:66px;height:26px;",
//                style: {
//                    padding: '8px 15px 8px 15px',
//                    margin: '0 0 0 12px'
//                },
                handler: function() {
                    var selectFlag = grid.doSelect(win, singleSelect);
                    if (selectFlag && singleSelect) {
                        win.close(); //单选时，'+labels.Select+'完成后自动隐藏
                    }
                }
            }]
        });

        var grid = this.gridPanel = new Ext.ux.grid.GridPanel({
            border: true,
            ui: 'small-grid',
            style: {
                padding: '0px 20px 0px 20px',
                border: '1px'
            },
            sm: {
                singleSelect: singleSelect
            },
            pageSize: 20,
            forceFit: false,
            viewConfig: {
                stripeRows: true
            },
            store: {
                url: path_srm + '/cp/delivery/getpoddetail',
                baseParams: cfg.baseParams || {},
                autoLoad: true
            },
            cm: {
                defaultSortable: false,
                columns: [{
                    Qheader: 'ID',
                    header: $('porder.purchaseOrderDetailId'),
                    dataIndex: 'purchaseOrderDetailId',
                    disabled: true
                }, {
                    Qheader: '确认交货日期',
                    header: $('porder.vendorTime'),
                    dataIndex: 'vendorTime',
                    width: 150,
                    renderer: function(value) {
                        if (!Ext.isEmpty(value)) {
                            value = value.replace(/\d{2}:\d{2}:\d{2}$/, '');
                        }
                        return value
                    }
                }, {
                    Qheader: '物料编码',
                    header: $('materialInfo.code'),
                    dataIndex: 'materialCode',
                    width: 100
                }, {
                    Qheader: '物料名称',
                    header: $('materialInfo.name'),
                    dataIndex: 'materialName',
                    width: 160
                }, {
                    Qheader: '可送数量',
                    header: $('deliveryDtl.canSentNumber'),
                    dataIndex: 'canSendQty',
                    width: 120,
                    align: 'right',
                    renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                        var canSendNum = Ext.util.Format.number((record.get('vendorQty') + record.get('qtyQuit') - Math.abs(record.get('qtyArrive')) - Math.abs(record
                            .get('qtyOnline'))), '0.000');
                        return canSendNum;
                    }
                }, {
                    Qheader: '采购订单号',
                    header: $('porder.purchaseOrderNo'),
                    dataIndex: 'purchaseOrder.purchaseOrderNo',
                    width: 150,
                    hidden:true
                }, {
                    Qheader: 'erp采购订单号',
                    header: $('porder.purchaseOrderNo')/*$('receivingnote.sapPurchaseOrderNo')*/,
                    dataIndex: 'purchaseOrder.erpPurchaseOrderNo',
                    width: 150
                }, {
                    Qheader: '行号',
                    header: $('label.rowNo'),
                    dataIndex: 'rowIds',
                    width: 100
                }, {
                    Qheader: '计量单位编码',
                    header: $('deliveryDtl.unitCode'),
                    dataIndex: 'unitCode',
                    //xtype:'hidden',
                    width: 100
                }, {
                    Qheader: '计量单位名称',
                    header: $('unit.unitName'),
                    dataIndex: 'unitName',
                    xtype:'hidden',
                    width: 120
                }, {
                    Qheader: '订单数量',
                    header: $('sendscheduledetail.sendQty1'),
                    dataIndex: 'vendorQty',
                    width: 100,
                    align: 'right',
                    renderer: function(value) {
                        if (Ext.isEmpty(value)) value = 0;
                        return Ext.util.Format.number(value, '0.000');
                    }
                }, {
                    Qheader: '已送数量',
                    header: $('deliveryDtl.toSentNumber'),
                    xtype: 'hidden',
                    dataIndex: 'qtySend',
                    width: 120,
                    align: 'right',
                    renderer: function(value) {
                        if (Ext.isEmpty(value))
                            value = 0;
                        return Ext.util.Format.number(value, '0.000')
                    }
                }, {
                    Qheader: '到货数量',
                    header: $('shoppingnoticedetail.receiveQty'),
                    dataIndex: 'qtyArrive',
                    width: 120,
                    align: 'right',
                    renderer: function(value) {
                        if (Ext.isEmpty(value))
                            value = 0;
                        return Ext.util.Format.number(value, '0.000')
                    }
                }, {
                    Qheader: '退货量',
                    header: $('shoppingnoticedetail.returnGoodsQty'),
                    dataIndex: 'qtyQuit',
                    width: 100,
                    align: 'right',
                    renderer: function(value) {
                        if (Ext.isEmpty(value))
                            value = 0;
                        return Ext.util.Format.number(value, '0.000')
                    }
                }, { // 在途数量
                    header: $('porderDtl.qtyOnline'),
                    dataIndex: 'qtyOnline',
                    width: 80,
                    align: 'right',
                    renderer: function(value) {
                        if (Ext.isEmpty(value))
                            value = 0;
                        return Ext.util.Format.number(value, '0.000')
                    },
                    hidden: true
                }, {
                    Qheader: '采购组织编码',
                    header: $('purchasingOrg.code'),
                    dataIndex: 'purchaseOrder.purchasingOrgCode',
                    xtype:'hidden',
                    width: 120
                }, {
                    Qheader: '采购组织名称',
                    header: $('purchasingOrg.name'),
                    dataIndex: 'purchaseOrder.purchasingOrgName',
                    width: 160
                }, {
                    Qheader: '工厂编码',
                    header: $('plant.code'),
                    dataIndex: 'plantCode',
                    xtype:'hidden',
                    width: 100
                }, {
                    Qheader: '工厂名称',
                    header: $('plant.name'),
                    dataIndex: 'plantName',
                    width: 200
                }, {
                    Qheader: '库存地点名称',
                    header: $('label.storageLocation'),
                    dataIndex: 'storeLocal',
                    width: 160
                }, {
                    Qheader: '行项目类别编码',
                    header: $('porder.lineItemType'),
                    dataIndex: 'lineItemTypeCode',
                    width: 100,
                    renderer: function(v, m, r) {
                        var value = r.get('lineItemTypeCode');
                        var index = cfg.recordTypeStore.find('itemCode', value);
                        if (index > -1) {
                            return cfg.recordTypeStore.getAt(index).get('itemName');
                        } else {
                            return v;
                        }
                    }
                }, {
                    Qheader: '交货过量限度',
                    dataIndex: 'overDeliveryLimit',
                    disabled: true
                }, {
                    dataIndex: 'purchaseOrder.createName',
                    disabled: true
                }]
            },
            dockedItems: [tBar, bBar],
            listeners: {
                'itemdblclick': function(g, i) {
                    if (grid.doSelect(win, singleSelect)) {
                        win.hide();
                    }
                }
            },
            //private 自定义
            //选择
            doSelect: function(win, singleSelect) {
                var grid = this,
                    sm = grid.getSelectionModel(),
                    selections = sm.getSelection();
                if (selections.length < 1) {
                    Q.tips('<font color="red">' + $('message.pleaseSelect') + '</font>');
                    return false;
                }
                var flag = true;
                Q.each(selections, function(v, i) {
                    var store = win.gridOut.getStore();
                    var index = store.find('orderDetailId', v.get('purchaseOrderDetailId'));
                    if (index > -1) {
                        flag = false;
                    }
                });

                // 同张送货单不能存在同一个排程明细行项目
                if (flag) {
                    if (singleSelect) {
                        win.fireEvent('select', grid, selections[0]);
                    } else {
                        win.fireEvent('select', grid, selections);
                    }

                    Q.tips('<font color="blue">' + $('message.selectSuccess') + '</font>');
                    return true;
                } else {
                    Q.tips($('shoppingnoticedetail.tip.msg2') + '！');
                    return false;
                }
            }
        });
        return grid;
    }
});