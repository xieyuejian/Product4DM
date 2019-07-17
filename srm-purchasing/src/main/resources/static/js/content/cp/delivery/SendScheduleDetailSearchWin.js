/**
 * @class {Cp.delivery.SendScheduleDetailSearchWin}
 * @extend {Ext.ux.Window}
 * 送货排程明细查询框
 * 
 * @param {object} cfg :
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 */
Ext.define('Cp.delivery.SendScheduleDetailSearchWin', {
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
            height: 250,
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
                    fieldLabel: '需求时间',
                    fieldLabel: $('sendscheduledetail.scheduleTime'),
                    name: 'filter_GE_scheduleTime',
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s'
                }, {
                    fieldLabel: '物料编码',
                    fieldLabel: $('materialInfo.code'),
                    name: 'filter_LIKE_materialCode'
                }, */{
                    QfieldLabel: '订单号',
                    fieldLabel: $('porder.purchaseOrderNo'),
                    name: 'filter_LIKE_purchaseOrderNo'
                }, {
                    fieldLabel: $('materialInfo.name'),
                    name: 'filter_LIKE_materialName'
                }, {
                    QfieldLabel: '排程号',
                    fieldLabel: $('sendschedul.sendScheduleNo'),
                    name: 'filter_LIKE_sendScheduleNo'
                }]
            }/*, {
                defaults: {
                    xtype: 'textfield',
                    anchor: '90%'
                },
                columnWidth: .5,
                items: [{
                    fieldLabel: $('label.to'),
                    name: 'filter_LE_scheduleTime',
                    xtype: 'datetimefield',
                    format: 'Y-m-d H:i:s'
                }, {
                    fieldLabel: $('materialInfo.name'),
                    name: 'filter_LIKE_materialName'
                }, {
                    QfieldLabel: '排程号',
                    fieldLabel: $('sendschedul.sendScheduleNo'),
                    name: 'filter_LIKE_sendScheduleNo'
                }]
            }*/],
            buttons: [{
                text: $('button.return'),
                margin : "20 30 20 0",
                ui: 'gray-btn',
				style : "width:80px;height:34px;",
                handler: function() {
                    win.close();
                }
            }, {
                text: $('button.search'),
                id: 'sendscheduleDetail_searchid',
                ui: 'blue-btn',
                margin : "20 20 20 0",
				style : "width:80px;height:34px;",
                handler: function() {
                    win.searchData();
                    if (!Ext.isEmpty(Ext.getCmp('sendscheduleDetail_clearSearchid'))) {
                        Ext.getCmp('sendscheduleDetail_clearSearchid').show();
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
    },
});