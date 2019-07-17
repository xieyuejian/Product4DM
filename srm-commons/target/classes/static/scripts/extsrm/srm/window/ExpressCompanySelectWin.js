/**
 * @class {Cp.delivery.ExpressCompanySelectWin}
 * @extend {Ext.ux.Window}
 * 快递公司弹出框
 * 
 * @param {object} cfg :
 *  @param {Boolean}    cfg.singleSelect 是否单选，缺省为多选
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 *  @param {Function}   cfg.select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *               单选时参数：(grid, record)
 *               多选时参数：(grid, selections)
 */ 

Ext.define('Ext.srm.window.ExpressCompanySelectWin', { 
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
            title: $('delivery.expressCompanyName'),
            layout: 'border',
            width: 400,
            height: 450,
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
            style: "background-color:transparent",
            margin: '10px 0px 10px 0px',
            broder: '0px',
            items: [{
                xtype: 'label',
                text: $('delivery.expressCompanyCode'),
                style: {
                    margin: '0 6px 0 0'
                }
            }, {
                name: 'itemCode',
                xtype: 'textfield',
                style: {
                    margin: '0 20px 10px 0'
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
                    var materialCode = _self.ownerCt.find('name', 'itemCode')[0].getValue();
                    cfg.baseParams.filter_LIKE_itemCode_OR_LIKE_itemName = materialCode;
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
                margin: '5 30 10 0',
                style: 'width:60px;height:34px;',
                ui: 'gray-btn',
                handler: function() {
                    win.hide();
                }
            }, {
                text: $('label.select'),
                ui: 'blue-btn',
                margin: '5 0 10 0',
                style: 'width:90px;height:34px;',
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
                url: cfg.url || path_masterdata + '/md/datadict/getall?groupCode=expressCompany',
                listeners: {
                    'beforeload': function(s, opt) {
                        Ext.apply(s.proxy.extraParams, cfg.baseParams);
                    }
                }
            },
            cm: {
                defaultSortable:false,
                columns:cfg.columns || [ 
                    {Qheader:'快递公司编码',header:$('delivery.expressCompanyCode'),dataIndex:'itemCode',width: 100},
                    {Qheader:'快递公司名称',header:$('delivery.expressCompanyName'),dataIndex:'itemName',width: 200}
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
