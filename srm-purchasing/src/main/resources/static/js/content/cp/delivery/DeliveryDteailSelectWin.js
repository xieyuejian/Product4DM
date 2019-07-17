/**
 * @class {Cp.delivery.DeliveryDteailSelectWin}
 * @extend {Ext.ux.Window}
 * 送货数据选择弹出框
 * 
 * @param {object} cfg :
 *  @param {Boolean}    cfg.singleSelect 是否单选，缺省为多选
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 *  @param {Function}   cfg.select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *               单选时参数：(grid, record)
 *               多选时参数：(grid, selections)
 */ 

Ext.define('Cp.delivery.DeliveryDteailSelectWin', { 
    extend:'Ext.ux.Window',
    constructor: function(cfg) {
        var win = this;
        cfg = cfg || {};
        cfg = Ext.apply({
            forceFit: true,
            singleSelect: true,
            constrain: true,
            renderTo: cfg.moduleId            
        }, cfg);

        var grid = this.gridPanel = this.createGrid(cfg);

        var cfg = Ext.apply({
            title: $('delivery'),
            layout: 'border',
            width: 800,
            height: 500,
            items: [grid],
            listeners: {
                'hide': function() {
                    grid.getSelectionModel().clearSelections();
                }
            },
        }, cfg);
        
        this.callParent([cfg]);

        if (!Ext.isEmpty(cfg.select) && Ext.isFunction(cfg.select)) {
            this.on('select', cfg.select);
        }
    },
    createGrid: function(cfg) {
        var singleSelect = cfg.singleSelect;
        var win = this;
        var viewModel = cfg.viewModel;

        var tBar = Ext.create('Ext.toolbar.Toolbar', {
            dock: 'top',
            style: {
                margin: '10px 0 10px 0',
                background: '#FFF',
                border: '0px',
            },
            items: [{
                xtype: 'label',
                text: $('shoppingNotice.shoppingNoticeNo'),
                style: {
                    margin: '0 6px 0 0'
                },
            },{
                name: 'deliveryCode',
                xtype: 'textfield',
                style: {
                    margin: '0 20px 0 0'
                },
                width: 190
            },  {
                xtype: 'label',
                text: $('materialMasterPrice.materialCode'),
                style: {
                    margin: '0 6px 0 0'
                },
            }, {
                name: 'materialCode',
                xtype: 'textfield',
                style: {
                    margin: '0 20px 0 0'
                },
                width: 190
            }, {
                text: $('button.search'),
                iconCls: 'icon-search',
                width: 80,
                height: 30,
                margin: '0',
                ui: 'blue-btn',
                handler: function(_self) {
                    var store = grid.getStore();
                    var materialCode = _self.ownerCt.find('name', 'materialCode')[0].getValue();
                    var deliveryCode = _self.ownerCt.find('name', 'deliveryCode')[0].getValue();
                    cfg.baseParams.filter_LIKE_materialCode = materialCode;
                    cfg.baseParams.filter_LIKE_delivery_deliveryCode = deliveryCode;
                    store.load({params: {start: 0, limit: 20 } }); 
                }
            }]
        });

        var bBar = Ext.create('Ext.toolbar.Toolbar', {
            dock: 'bottom',
            style: {
                margin: '0',
                background: '#FFF',
                padding: '0px',
                border: '1px'
            },
            items: ['->', {
                text: $('label.return'),
                margin: '20 30 20 0',
                style: 'width:80px;height:34px;',
                ui: 'gray-btn',
                handler: function() {
                    win.hide();
                }
            }, {
                text: $('label.select'),
                ui: 'blue-btn',
                margin: '20 0 20 0',
                style: 'width:80px;height:34px;',
                handler: function() {
                    var selectFlag = grid.doSelect(win, singleSelect);
                    if (selectFlag && singleSelect) {
                        win.hide(); //单选时，'+labels.Select+'完成后自动隐藏
                    }
                }
            }, ]
        });
    
        var grid = new Ext.ux.grid.GridPanel({
            pageSize: cfg.pageSize || 20,
            border: true,
            ui: 'small-grid',
            style: {
                padding: '0px 30px 0px 30px',
                border: '1px'
            },
            enableColumnHide: false,
            sm: {
                singleSelect: singleSelect
            },
            viewConfig: {
                forceFit: cfg.forceFit
            },
            store: {
                url: cfg.url || path_srm + '/cp/deliverydetail/list',
                dir : cfg.dir || 'desc',
                sort : cfg.sort || 'delivery.deliveryCode',
                listeners: {
                    'beforeload': function(s, opt) {
                        Ext.apply(s.proxy.extraParams, cfg.baseParams);
                    }
                }
            },
            cm: {
                defaultSortable:false,
                columns:cfg.columns || [
                	{Qheader: '送货单编码', header: $('shoppingNotice.shoppingNoticeNo'), dataIndex: 'delivery.deliveryCode', width: 150},
        			{Qheader: '送货单的供应商编码', header: $('vendor.code'), dataIndex: 'delivery.vendorCode'},
        			{Qheader: '送货单的供应商名称', header: $('vendor.name'), dataIndex: 'delivery.vendorName', width: 250 },
        			{Qheader: '送货明细的物料编码', header: $('materialInfo.code'), dataIndex: 'materialCode', width: 120 },
        			{Qheader: '送货明细的物料名称', header: $('materialInfo.name'), dataIndex: 'materialName', width: 250 },
        			{Qheader: '送货明细的送货数量', header: $('sendscheduledetail.deliveryQty'), dataIndex: 'deliveryNumber', align: 'right', renderer: 
        				function(v){
	        				if (!Ext.isEmpty(v)) {
	        					return Ext.util.Format.number(v, '0.00');
	        				}
							return '';
						}
	    			},
        			{Qheader: '送货明细的收货数量', header: $('sendscheduledetail.receiptQty'), dataIndex: 'receivedNumber', align: 'right', renderer: 
        				function(v){
	        				if (!Ext.isEmpty(v)) {
	        					return Ext.util.Format.number(v, '0.00');
	        				}
							return '';
						}
	    			},
        			//hidden
        			{Qheader: '送货单明细ID', header: $('shoppingnoticedetail.shoppingNoticeDetailId'), dataIndex: 'delivery.deliveryDtlId', width: 120 ,hidden:true},
        			{Qheader: '送货单状态(待收货、部分收货、收货完成)', header: $('sendscheduledetail.sendFlag'), dataIndex: 'delivery.status', width: 120, renderer: 'rendererStatus',hidden:true},
        			{Qheader: '送货明细的采购订单号', header: $('porder.purchaseOrderNo'), dataIndex: 'purchaseOrderCode', width: 150 ,hidden:true},
        			{Qheader: '送货明细的行号', header: $('label.rowNo'), dataIndex: 'lineNumber', width: 120 ,hidden:true},
        			{Qheader: '送货明细的单位编码', header: $('deliveryDtl.unitCode'), dataIndex: 'unitCode', width: 120 ,hidden:true},
        			{Qheader: '送货单的送货单号', header: $('delivery.deliveryCode'), dataIndex: 'delivery.deliveryCode', width: 150, renderer: 'rendererNo',hidden:true},//gridDeliveryCodeRenderer
        			{Qheader: '送货单的采购组织编码', header: $('purchasingOrg.code'), dataIndex: 'delivery.purchasingOrgCode',hidden:true},
        			{Qheader: '送货单的采购组织名称', header: $('delivery.purchasingOrgName'), dataIndex: 'delivery.purchasingOrgName', width: 150 ,hidden:true},
        			{Qheader: '送货单的工厂，显示工厂名称', header: $('plant.title'), dataIndex: 'delivery.plantName', width: 200 ,hidden:true},
        			{Qheader: '送货单的库存地点，显示库存地点名称', header: $('label.storageLocation'), dataIndex: 'delivery.storageLocationName', width: 130 ,hidden:true},
        			{Qheader: '送货单的送货日期，显示格式yyyy-mm-dd', header: $('delivery.deliveryDate'), dataIndex: 'delivery.deliveryDate', type: 'date', dateFormat: 'Y-m-d H:i:s', renderer: 'rendererDateTime',hidden:true}, 
        			{Qheader: '送货单的送货方式： 1:自送;2:快递3:托运4:自提 ', header: $('delivery.deliveryTypes'), dataIndex: 'delivery.deliveryTypes', renderer: 'gridDeliveryTypesRenderer',hidden:true},
        			{Qheader: '送货单的送达日期，显示格式yyyy-mm-dd', header: $('delivery.serviceDate'), dataIndex: 'delivery.serviceDate', type: 'date', dateFormat: 'Y-m-d H:i:s', renderer: 'rendererDateTime',hidden:true},
        			{Qheader: '送货明细的排程单号', header: $('sendschedul.sendScheduleNo'), dataIndex: 'scheduleCode', width: 150 ,hidden:true},
        			{Qheader: '送货明细的行项目类别', header: $('deliveryDtl.lineItemTypes'), dataIndex: 'lineItemTypes', renderer: 'gridDtlLineItemTypesRenderer',hidden:true},
        			{Qheader: '送货明细的关闭标识', header: '关闭标识', dataIndex: 'closeFlag', renderer: 'gridDtlFlagRenderer',hidden:true}
        		]
            },
            dockedItems: [tBar, bBar],
            listeners: {
                'itemdblclick': function(g, i) {
                    if (grid.doSelect(win, singleSelect)) {
                        win.hide(); //双击'+labels.Select+'时，'+labels.Select+'完成后自动隐藏
                    }
                }
            },
            //private 自定义
            //'+labels.Select+'
            doSelect: function(win, singleSelect) {
                var grid = this,
                    sm = grid.getSelectionModel(),
                    selections = sm.getSelection();
                if (selections.length < 1) {
                    Q.tips($('message.pleaseSelect'), 'E');
                    return false;
                }
                if (singleSelect) {
                    win.fireEvent('select', grid, selections);
                } else {
                    win.fireEvent('select', grid, selections);
                }
                Q.tips($('message.selectSuccess'));
                return true;
            }
        });

        return grid;
    }
});
