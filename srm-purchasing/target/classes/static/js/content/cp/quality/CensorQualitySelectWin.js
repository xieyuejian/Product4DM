/**
 * @class {Cp.quality.CensorQualitySelectWin}
 * @extend {Ext.ux.Window}
 * 质检数据选择弹出框
 * 
 * @param {object} cfg :
 *  @param {Boolean}    cfg.singleSelect 是否单选，缺省为多选
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 *  @param {Function}   cfg.select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *               单选时参数：(grid, record)
 *               多选时参数：(grid, selections)
 */ 

Ext.define('Cp.quality.CensorQualitySelectWin', { 
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
            title: $('moduleCode.ZJD'),
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
                    cfg.baseParams.filter_LIKE_deliveryCode = deliveryCode;
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
                url: cfg.url || path_srment + '/cp/censorqualityresult/list',
                dir : cfg.dir || 'desc',
                sort : cfg.sort || 'censorqualityNo',
                listeners: {
                    'beforeload': function(s, opt) {
                        Ext.apply(s.proxy.extraParams, cfg.baseParams);
                    }
                }
            },
            cm: {
                defaultSortable:false,
                columns:cfg.columns || [
        			{Qheader:'检验批号',header:$('censorQuality.censorqualityNo'),dataIndex:'censorqualityNo',width:150},
        			{Qheader:'供应商编码',header:$('censorQuality.vendorCode'),dataIndex:'vendorErpCode',width:150},
        			{Qheader:'供应商名称',header:$('censorQuality.vendorName'),dataIndex:'vendorName',width:250},
        			{Qheader:'物料编码',header:$('censorQuality.materialCode'),dataIndex:'materialCode',width:150},
        			{Qheader:'物料名称',header:$('censorQuality.materialName'),dataIndex:'materialName',width:250},
        			{Qheader:'质检结果名称',header:$('censorQuality.resultCode'),dataIndex:'resultName',width:130},
        			
        			{Qheader:'送检量',header:$('censorQuality.censorQty'),dataIndex:'censorQty',width:130,renderer : 
        				function(v){
	        				if (!Ext.isEmpty(v)) {
	        					return Ext.util.Format.number(v, '0.00');
	        				}
							return '';
						}
        			},
           			{Qheader:'已质检合格量',header:$('censorQuality.checkQualifiedQty'),dataIndex:'checkQualifiedQty',width:130,renderer :  
        				function(v){
	        				if (!Ext.isEmpty(v)) {
	        					return Ext.util.Format.number(v, '0.00');
	        				}
							return '';
						}
        			},
        			{Qheader:'已质检不合格量',header:$('censorQuality.checkUnqualifiedQty'),dataIndex:'checkUnqualifiedQty',width:130,renderer :  
        				function(v){
	        				if (!Ext.isEmpty(v)) {
	        					return Ext.util.Format.number(v, '0.00');
	        				}
							return '';
						}
        			},
        			{Qheader:'已质检让步接收量',header:$('censorQuality.checkReceiveQty'),dataIndex:'checkReceiveQty',width:130,renderer :  
        				function(v){
	        				if (!Ext.isEmpty(v)) {
	        					return Ext.util.Format.number(v, '0.00');
	        				}
							return '';
						}
        			},
        			{Qheader:'质检时间',header:$('censorQuality.qualityTime'),dataIndex:'qualityTime',type:'date',width:150,renderer:
        				function(value){
	        				if (!Ext.isEmpty(value)) {
	        					return Ext.util.Format.date(value, 'Y-m-d');
	        				}
	        				
	        				return '';
        				}
        			},
        			
        			//hidden
        			{Qheader:'质检状态',header:$('censorQuality.status'),dataIndex:'status',width:130,renderer:'rendererStatus',hidden:true},
        			{Qheader:'送检时间',header:$('censorQuality.inspectionTime'),dataIndex:'inspectionTime',type:'date',dateFormat:'Y-m-d H:i:s',width:150,renderer:'rendererDateTime',hidden:true},               
        			{Qheader:'单位',header:$('censorQuality.unit'),dataIndex:'unit',width:130,hidden:true},
        			{Qheader:'收货单号',header:$('censorQuality.shoppingNoticeNo'),dataIndex:'receivingNoteNo',width:150,hidden:true},
        			{Qheader:'采购订单号',header:$('censorQuality.purchaseOrderNo'),dataIndex:'purchaseOrderNo',width:150,hidden:true},
        			{Qheader:'行号',header:$('censorQuality.rowIds'),dataIndex:'rowIds',width:130,hidden:true},
        			{Qheader:'凭证年度',header:$('censorQuality.voucherYear'),dataIndex:'voucherYear',width:130,hidden:true},
        			{Qheader:'凭证编号',header:$('censorQuality.voucherNo'),dataIndex:'voucherNo',width:150,hidden:true},
        			{Qheader:'凭证行项目号',header:$('censorQuality.voucherProNo'),dataIndex:'voucherProNo',width:130,hidden:true},
        			{Qheader:'采购组织编码',header:$('censorQuality.purchasingOrgCode'),dataIndex:'purchasingOrgCode',width:130,hidden:true},
        			{Qheader:'采购组织名称',header:$('censorQuality.purchasingOrgName'),dataIndex:'purchasingOrgName',width:180,hidden:true},
        			{Qheader:'备注',header:$('censorQuality.remark'),dataIndex:'remark',width:130,hidden:true},
        			{Qheader:'同步状态',header:$('censorQuality.erpSyn'),dataIndex:'erpSyn',width:130,renderer: 'renderSynStatus',hidden:true},
        			{Qheader:'送检质检单ID',header:$('censorQuality.censorqualityId'),dataIndex:'censorqualityId',hidden:true},
        			{Qheader:'不合格量',header:$('censorQuality.unqualifiedQty'),dataIndex:'unqualifiedQty',hidden:true},
        			{Qheader:'让步接收量',header:$('censorQuality.receiveQty'),dataIndex:'receiveQty',hidden:true},
        			{Qheader:'合格量',header:$('censorQuality.qualifiedQty'),dataIndex:'qualifiedQty',hidden:true},
        			{Qheader:'质检结果代码',header:$('censorQuality.resultCode'),dataIndex:'resultCode',hidden:true},
        			{Qheader:'可检量',header:$('censorQuality.canCheckQty'),dataIndex:'canCheckQty',hidden:true},
        			{Qheader:'工厂编码',header:$('censorQuality.plantCode'),dataIndex:'plantCode',hidden:true},
        			{Qheader:'工厂名称',header:$('censorQuality.plantName'),dataIndex:'plantName',hidden:true},
        			{Qheader:'库存地点编码',header:$('censorQuality.stockCode'),dataIndex:'stockCode',hidden:true},
        			{Qheader:'库存地点编码',header:$('censorQuality.stockCode'),dataIndex:'stockName',hidden:true},
        			{Qheader:'附件',header:$('censorQuality.uploadFileGroupId'),dataIndex:'uploadFileGroupId',hidden:true},
        			{Qheader:'同步信息',header:$('censorQuality.erpReturnMsg'),dataIndex:'erpReturnMsg',hidden:true},
        			{Qheader:'送检人员id',header:$('censorQuality.inspectorId'),dataIndex:'inspectorId',hidden:true},
        			{Qheader:'送检人员名称',header:$('censorQuality.inspectorName'),dataIndex:'inspectorName',hidden:true},
        			{Qheader:'质检人员id',header:$('censorQuality.qualitorId'),dataIndex:'qualitorId',hidden:true},
        			{Qheader:'质检人员名称',header:$('censorQuality.qualitorName'),dataIndex:'qualitorName',hidden:true},
        			{Qheader:'客户端编码',header:$('censorQuality.clientCode'),dataIndex:'clientCode',hidden:true}
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
