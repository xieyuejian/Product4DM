/**
 * @class {Cp.delivery.PurcahseOrderDetailSearchWin}
 * @extend {Ext.ux.Window}
 * 采购订单明细查询框
 * 
 * @param {object} cfg :
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 */
Ext.define('Cp.delivery.PurcahseOrderDetailSearchWin', {
    extend: 'Ext.ux.Window',
    constructor: function(cfg) {
        //查询表单
        var formPanel = this.createFormPanel();
        this.formPanel = formPanel;
        var cfg = Ext.apply({
            title: $('button.search'),
            layout: 'border',
            items: formPanel,
            width: 500,
            height: 240,
            listeners: {
                'hide': function() {
                	var form = formPanel.form;
                	form.reset();
                    this.close();
                }
            }
        });

        this.callParent([cfg]);
    },
    //创建维护表单
    createFormPanel: function() {
        var win = this;
        var formPanel = Ext.create('Ext.form.FormPanel', {
            region: 'center',
            labelWidth: 100,
            layout: 'column',
            border: true,
            bodyStyle: 'padding:10px',
            defaults: {
                columnWidth: 1,
                layout: 'form',
                border: false
            },
            items: [{
                defaults: {
                    xtype: 'textfield',
                    anchor: '90%'
                },
                columnWidth: 1,
                items: [/*{
                    fieldLabel: '确认交货日期',
                    name: 'filter_GE_vendorTime',
                    xtype: 'datefield',
                    format: 'Y-m-d'
                }, {
                    fieldLabel: '物料编码',
                    name: 'filter_LIKE_materialCode'
                }, */{
                    fieldLabel: '采购订单号',
                    name: 'filter_LIKE_purchaseOrder_erpPurchaseOrderNo'
                },{
                    fieldLabel: $('materialInfo.name'),
                    name: 'filter_LIKE_materialName'
                }]
            }/*, {
                defaults: {
                    xtype: 'textfield',
                    anchor: '90%'
                },
                columnWidth: .5,
                items: [{
                    fieldLabel: '至',
                    name: 'filter_LE_vendorTime',
                    xtype: 'datefield',
                    format: 'Y-m-d'
                }, {
                    fieldLabel: $('materialInfo.name'),
                    name: 'filter_LIKE_materialName'
                }]
            }*/],
            buttons: [{
                text: $('button.return'),
                ui: 'gray-btn',
                margin : "20 30 20 0",
				style : "width:80px;height:34px;",
                handler: function() {
                    win.close();
                }
            }, {
                text: $('button.search'),
                ui: 'blue-btn',
                margin : "20 20 20 0",
				style : "width:80px;height:34px;",
                id: 'purchaseOrderDetail_searchid',
                handler: function() {
                    win.searchData();
                    if (!Ext.isEmpty(Ext.getCmp('purchaseOrderDetail_clearSearchid'))) {
                        Ext.getCmp('purchaseOrderDetail_clearSearchid').show();
                    }
                }
            }]
        });

        return formPanel;
    },

    //查询
    searchData: function() {
        var form = this.formPanel.form;
        if (!form.isValid()) {
            return;
        }
        if (false === this.fireEvent('search', form.getValues())) {
            return;
        }
        this.close();
    }
});