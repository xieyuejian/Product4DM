/**
 * @class {Cp.order.MaterialMasterPriceSelectWin}
 * @extend {Ext.ux.Window}
 * 价格主数据选择弹出框
 * 
 * @param {object} cfg :
 *  @param {Boolean}    cfg.singleSelect 是否单选，缺省为多选
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 *  @param {Function}   cfg.select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *               单选时参数：(grid, record)
 *               多选时参数：(grid, selections)
 */ 

Ext.define('Cp.order.MaterialMasterPriceSelectWin', { 
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
            title: $('selectwin.materialMasterPrice'),
            layout: 'border',
            width: 800,
            height: 500,
            items: [grid],
            listeners: {
                'hide': function() {
                    grid.getSelectionModel().clearSelections();
                }
            }
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
//            style: {
//                margin: '10px 0 10px 0',
//                background: '#FFF',
//                border: '0px',
//            },
            style: "background-color:transparent",
            margin: '10px 0px 0px 0px',
            broder: '0px',
            items: [{
                xtype: 'label',
                text: $('materialMasterPrice.materialCode'),
                style: {
                    margin: '0 6px 0 0'
                }
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
                    cfg.baseParams.filter_LIKE_materialCode = materialCode;
                    store.load({params: {start: 0, limit: 20 } }); 
                }
            }]
        });

        var bBar = Ext.create('Ext.toolbar.Toolbar', {
            dock: 'bottom',
//            style: {
//                margin: '0',
//                background: '#FFF',
//                padding: '0px',
//                border: '1px'
//            },
            style: {
                margin: '0px 0px 0px 20px',
                border: '0px'
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
            } ]
        });
    
        var grid = new Ext.ux.grid.GridPanel({
            pageSize: cfg.pageSize || 20,
            border: true,
            ui: 'small-grid',
//            style: {
//                padding: '0px 30px 0px 30px',
//                border: '1px'
//            },
            ui: 'small-grid',
            style: {
                padding: '0px 20px 10px 20px',
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
                url: cfg.url || path_srm + '/cp/purchaseorder/findmaterialmasterpricedetail',
                listeners: {
                    'beforeload': function(s, opt) {
                        Ext.apply(s.proxy.extraParams, cfg.baseParams);
                    }
                }
            },
            cm: {
                defaultSortable:false,
                columns:cfg.columns || [ 
                    {Qheader:'物料编码',header:$('materialMasterPrice.materialCode'),dataIndex:'materialCode',width:150},
                    {Qheader:'物料名称',header:$('materialMasterPrice.materialName'),dataIndex:'materialName',minWidth:250},
                    {
                        Qheader:'记录类别',
                        header:$('materialMasterPrice.recordType'),
                        dataIndex:'recordType',
                        renderer:function(v){
                            if(!Ext.isEmpty(v)){ 
                                var index = viewModel.getStore('lineItemTypeStore').find('itemCode',v);
                                if(index > -1){
                                    var record = viewModel.getStore('lineItemTypeStore').getAt(index);
                                    return record.get('itemName');
                                }
                            } 
                            return v;
                        }
                    },
                    {Qheader:'工厂编码',header:$('materialMasterPrice.plantCode'),dataIndex:'plantCode'},
                    {
                        header : $('label.notTaxPrice'),
                        dataIndex : 'nonTaxPrice',
                        align : 'right',
                        renderer : function(v) {
                            return Ext.util.Format.number(v, '0.00');
                        }
                    },
                    {Qheader:'订单单位',header:$('materialMasterPriceApply.orderUnitCode'),dataIndex:'orderElementaryUnitCode'},
                    {Qheader:'价格单位',header:$('label.priceUnit'),dataIndex:'priceUnit'},
                    
                    {Qheader:'细单ID',dataIndex:'materialMasterPriceDtlId',hidden:true},
                    {Qheader:'主单ID',dataIndex:'materialMasterPriceId',hidden:true},
                    {Qheader:'价格ID',dataIndex:'materialLadderPriceDtlId',hidden:true},
                    {Qheader:'转换单位ID',dataIndex:'materialUnitConversionDtlId',hidden:true},
                    {Qheader:'过量交货限度%',dataIndex:'excessDeliveryLimit',hidden:true},
                    {Qheader:'交货不足限度%',dataIndex:'deliveryLimit',hidden:true},
                    {Qheader:'基本单位(订单单位与基本单位转换关系)',dataIndex:'elementaryUnit',hidden:true},
                    {Qheader:'订单单位(订单单位与基本单位转换关系)',dataIndex:'orderElementaryUnit',hidden:true},
                    {Qheader:'订单单位(订单单位与定价单位转换关系)',dataIndex:'orderPricingUnit',hidden:true},
                    {Qheader:'定价单位(订单单位与定价单位转换关系)',dataIndex:'pricingUnit',hidden:true},
                    {Qheader:'基本单位(订单单位与基本单位转换关系)',dataIndex:'elementaryUnitCode',hidden:true},
                    {Qheader:'订单单位(订单单位与定价单位转换关系)',dataIndex:'orderPricingUnitCode',hidden:true},
                    {Qheader:'定价单位(订单单位与定价单位转换关系)',dataIndex:'pricingUnitCode',hidden:true},
                    {Qheader:'税率编码',dataIndex:'taxRateCode',hidden:true},
                    {Qheader:'计划天数',dataIndex:'plannedDays',hidden:true},
                    {Qheader:'jitFlag',dataIndex:'jitFlag',hidden:true},
                    {Qheader:'stockLocationCode',dataIndex:'stockLocationCode',hidden:true},
                    {Qheader:'plantName',dataIndex:'plantName',hidden:true},
                    {Qheader:'供应商编码',header:$('materialMasterPrice.vendorCode'),dataIndex:'vendorCode',hidden:true},
                    {Qheader:'供应商编码',header: $('materialMasterPrice.vendorCode'),dataIndex:'vendorErpCode',width:120},
                    {Qheader:'供应商名称',header:$('materialMasterPrice.vendorName'),dataIndex:'vendorName',minWidth:200}
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
                    win.fireEvent('select', grid, selections[0]);
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
