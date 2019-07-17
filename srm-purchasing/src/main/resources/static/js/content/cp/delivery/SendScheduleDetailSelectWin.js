/**
 * @class {Cp.delivery.SendScheduleDetailSelectWin}
 * @extend {Ext.ux.Window}
 * 送货排程明细选择
 * 
 * @param {object} cfg :
 *  @param {Boolean}    cfg.singleSelect 是否单选，缺省为多选
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 *  @param {Function}   cfg.select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *               单选时参数：(grid, record)
 *               多选时参数：(grid, selections)
 */
Ext.define('Cp.delivery.SendScheduleDetailSelectWin', {
    extend: 'Ext.ux.Window',
    constructor: function(cfg) {
        cfg = cfg || {};
        Ext.applyIf(cfg, {
			forceFit: true,//列表自适应grid用
			singleSelect: true,//列表单选grid用
			constrain: true,
			renderTo: cfg.moduleId
		});
        var singleSelect = false !== cfg.singleSelect;
        var gridOut = this.gridOut = cfg.gridOut;
        var grid = this.grid = this.createGrid(cfg, singleSelect);

        var cfg = Ext.apply({
            title: $('d_po_sendscheduledetail'),
            layout: 'border',
            width: 800,
            height: 400,
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
/*            style: {
                margin: '10px 0 10px 0',
                background: '#FFF',
                border: '0px',
            },*/
            style: "background-color:transparent;border-style: none",
            margin: '10px 0 10px 0',
            
            items: [{
    			xtype : "label",
    			text : $("sendscheduledetail.scheduleTime"),
    		},{
                name: 'filter_GE_scheduleTime',
                xtype: 'datetimefield',
                width : '21%',
                format: 'Y-m-d H:i:s'
            }, {
    			xtype : "label",
    			text : $("label.to"),
    		}, {
                name: 'filter_LE_scheduleTime',
                xtype: 'datetimefield',
                width : '21%',
                format: 'Y-m-d H:i:s'
            }, {
    			xtype : "label",
    			text : $("materialInfo.code"),
    		}, {
                xtype : "textfield",
                width : '20%',
                name: 'filter_LIKE_materialCode'
            }, {
                text: $('button.search'),
                ui: 'blue-btn',
                iconCls : "icon-search",
                style: "width:80px;height:30px;",
//                margin : "0 0 0 10",
//                width:80,
//    			height:30,
                handler: function() {
                	var store = grid.getStore();
                	var startTime = tBar.query("datefield[name=filter_GE_scheduleTime]")[0].getRawValue();
                	var endTime = tBar.query("datefield[name=filter_LE_scheduleTime]")[0].getRawValue();
                	var materialCode = tBar.query("textfield[name=filter_LIKE_materialCode]")[0].getValue();
    				store.proxy.extraParams.filter_GE_scheduleTime = startTime;
    				store.proxy.extraParams.filter_LE_scheduleTime = endTime;
    				store.proxy.extraParams.filter_LIKE_materialCode = materialCode;
    				store.proxy.extraParams.filter_LIKE_purchaseOrderNo = {};
    				store.proxy.extraParams.filter_LIKE_materialName = {};
    				store.proxy.extraParams.filter_LIKE_sendScheduleNo = {};
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
//                width:80,
//    			height:30,
//                margin : "0 30 0 10",
                style: "width:80px;height:30px;",
                handler: function() {
                    var win = new Cp.delivery.SendScheduleDetailSearchWin(),
                        store = grid.getStore();
                    win.on('search', function(data) {
                    	var startTime = tBar.query("datefield[name=filter_GE_scheduleTime]")[0].getRawValue();
                    	var endTime = tBar.query("datefield[name=filter_LE_scheduleTime]")[0].getRawValue();
                    	var materialCode = tBar.query("textfield[name=filter_LIKE_materialCode]")[0].getValue();
        				store.proxy.extraParams.filter_GE_scheduleTime = startTime;
        				store.proxy.extraParams.filter_LE_scheduleTime = endTime;
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
/*            style: {
                margin: '10px 0 10px 0',
                background: '#FFF',
                border: '1px'
            },*/
            style: {
                margin: '0px 0px 0px 20px',
                border: '0px'
            },
            items: ['->', {
                text: $('button.return'),
                ui: 'gray-btn',
//                style: {
//                    padding: '8px 15px 8px 15px',
//                    margin: '0 12px 0 12px'
//                },
                style : "width:66px;height:26px;",
                handler: function() {
                    win.close();
                }
            }, {
                text: $('button.select'),
                ui: 'blue-btn',
//                style: {
//                    padding: '8px 15px 8px 15px',
//                    margin: '0 0 0 12px'
//                },
                style : "width:66px;height:26px;",
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
            viewConfig: {
                forceFit: false,
                stripeRows: true
            },
            store: {
                url: path_srm + '/cp/delivery/getssdetail',
                baseParams: cfg.baseParams || {},
                autoLoad: true
            },
            cm: {
                defaultSortable: false,
                columns: [{
                        Qheader: '排程细单ID',
                        header: $('sendscheduledetail.sendScheduleDetailId'),
                        dataIndex: 'sendScheduleDetailId',
                        disabled: true
                    }, {
                        Qheader: '排程中间表ID',
                        header: $('sendschedulecommon.sendScheduleCommonId'),
                        dataIndex: 'sendScheduleCommonId',
                        disabled: true
                    }, {
                        Qheader: '采购细单ID',
                        header: $('sendscheduledetail.purchaseOrderDetailId'),
                        dataIndex: 'purchaseOrderDetailId',
                        disabled: true
                    }, {
                        Qheader: 'SAP采购订单号编码',
                        header: $('porder.purchaseOrderNo'),
                        dataIndex: 'erpPurchaseOrderNo',
                        disabled: true
                    }, {
                        Qheader: '需求时间',
                        header: $('sendscheduledetail.scheduleTime'),
                        dataIndex: 'scheduleTime',
                        type: 'date',
                        dateFormat: 'Y-m-d H:i:s',
                        width: 160,
                        renderer: function(v, m, r) { // 日期加背景处理记得看本质
                            v = Ext.util.Format.dateRenderer('Y-m-d H:i:s')(v);
                            return v;
                        }
                    }, {
                        Qheader: '物料编码',
                        header: $('materialInfo.code'),
                        dataIndex: 'materialCode'
                    }, {
                        Qheader: '物料名称',
                        header: $('materialInfo.name'),
                        dataIndex: 'materialName'
                    }, {
                        Qheader: '可送货量',
                        header: $('porder.sendableQty'),
                        dataIndex: 'canSendQty',
                        align: 'right',
                        renderer: function(value, metaData, record) {
                            // 可送数量=订单量-收货量-在途量+退货量
                            var canSendNum = Ext.util.Format.number((record.get('scheduleQty') + record.get('returnGoodsQty') - Math.abs(record.get('receiptQty')) - Math
                                .abs(record.get('onWayQty'))), '0.000');
                            // 查看该条订单明细是否是之前存在这条送货单中，若存在，则可送数量=之前送货单中的可送数量
                            /*
                             * var index =
                             * cacheStore.find('purchaseOrderDetailId',
                             * record.get('purchaseOrderDetailId'));
                             * if(index>-1){ canSendNum =
                             * Ext.util.Format.number(cacheStore.getAt(index).get('qtyCanSend'),'0.000'); }
                             * if(record.get('qtySend')!=0 ){ canSendNum = '<font
                             * color='red'>'+canSendNum+'</font>' }
                             */
                            return canSendNum;
                        }
                    }, {
                        Qheader: '来自主单排程单号编码',
                        header: $('sendschedul.sendScheduleNo'),
                        dataIndex: 'sendScheduleNo',
                        width: 150
                    }, {
                        Qheader: '来自主单采购订单号编码',
                        header: $('porder.purchaseOrderNo'),
                        dataIndex: 'purchaseOrderNo',
                        width: 150
                    }, {
                        Qheader: '行号',
                        header: $('label.rowNo'),
                        dataIndex: 'rowIds'
                    }, {
                        Qheader: '单位编码',
                        header: $('shoppingnoticedetail.unitCode'),
                        dataIndex: 'unitCode'
                    }, {
                        Qheader: '单位名称',
                        header: $('sendscheduledetail.unitName'),
                        dataIndex: 'unitName',
                        disabled: true
                    }, {
                        Qheader: '订单数量',
                        header: '订单数量',
                        dataIndex: 'sendQty',
                        renderer: function(value) {
                            if (Ext.isEmpty(value))
                                value = 0;
                            return Ext.util.Format.number(value, '0.000')
                        }
                    }, {
                        Qheader: '需求数量',
                        header: $('sendscheduledetail.scheduleQty'),
                        dataIndex: 'scheduleQty',
                        align: 'right',
                        renderer: function(v, m, r) {
                            return Ext.util.Format.number(v, '0.000');
                        }
                    }, {
                        Qheader: '送货量',
                        header: $('sendscheduledetail.deliveryQty'),
                        dataIndex: 'deliveryQty',
                        xtype: 'hidden',
                        renderer: function(value) {
                            if (Ext.isEmpty(value))
                                value = 0;
                            return Ext.util.Format.number(value, '0.000')
                        },
                    }, {
                        Qheader: '收货量',
                        header: $('sendscheduledetail.receiptQty'),
                        dataIndex: 'receiptQty',
                        align: 'right',
                        renderer: function(value) {
                            if (Ext.isEmpty(value))
                                value = 0;
                            return Ext.util.Format.number(value, '0.000')
                        }
                    }, {
                        Qheader: '退货量',
                        header: $('shoppingnoticedetail.returnGoodsQty'),
                        dataIndex: 'returnGoodsQty',
                        align: 'right',
                        renderer: function(value) {
                            if (Ext.isEmpty(value))
                                value = 0;
                            return Ext.util.Format.number(value, '0.000')
                        }
                    }, {
                        Qheader: '在途量',
                        header: $('sendscheduledetail.onWayQty'),
                        dataIndex: 'onWayQty',
                        renderer: function(value) {
                            if (Ext.isEmpty(value))
                                value = 0;
                            return Ext.util.Format.number(value, '0.000')
                        },
                        disabled: true
                    }, {
                        Qheader: '采购组织编码',
                        header: $('purchasingOrg.code'),
                        dataIndex: 'purchasingOrgCode',
                    }, {
                        Qheader: '工厂编码',
                        header: $('contract.dtl.plantCode'),
                        dataIndex: 'plantCode',
                        disabled: true
                    }, {
                        Qheader: '工厂名称',
                        header: $('sendscheduledetail.factoryName'),
                        dataIndex: 'factoryName',
                        disabled: true
                    }, {
                        Qheader: '库存地点',
                        header: $('label.storageLocation'),
                        dataIndex: 'stockLocal'
                    },

                    {
                        Qheader: '行项目类型',
                        header: $('sendscheduledetail.lineItemTypeCode'),
                        dataIndex: 'lineItemTypeCode',
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
                        dataIndex: 'createName',
                        disabled: true
                    }, {
                        Qheader: '交货过量限度',
                        dataIndex: 'overDeliveryLimit',
                        disabled: true
                    }
                ]
            },
            dockedItems: [tBar, bBar],
            listeners: {
                'itemdblclick': function(g, i) {
                    if (grid.doSelect(win, singleSelect)) {
                        win.close();
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
                    Q.tips($('message.pleaseSelect'),"E");
                    return false;
                }
                var flag = true;
                Q.each(selections, function(v, i) {
                    var store = win.gridOut.getStore();
                    var index = store.find('orderId', v.get('sendscheduleCommon.sendScheduleCommonId'));
                    if (index > -1) {
                        flag = false;
                    }
                });

                if (flag) {
                    if (singleSelect) {
                        win.fireEvent('select', grid, selections[0]);
                    } else {
                        win.fireEvent('select', grid, selections);
                    }

                    Q.tips($('message.selectSuccess'));
                    return true;
                } else {
                    Q.tips($('shoppingnoticedetail.tip.msg2') + '！'); // 同张送货单不能存在同一个排程明细行项目
                    return false;
                }
            }
        });
        return grid;
    }
});