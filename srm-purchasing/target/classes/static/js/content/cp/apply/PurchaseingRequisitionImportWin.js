/**
 * @class {Cp.apply.PurchaseingRequisitionImportWin}
 * @extend {Ext.ux.Window}
 * 采购申请erp导入
 *
 * @param {object} cfg :
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 */
Ext.define('Cp.apply.PurchaseingRequisitionImportWin', {
    extend: 'Ext.ux.Window',
    constructor: function(cfg) {
        cfg = cfg || {};
        var formPanel = this.formPanel = this.createFormPanel(cfg);
        var cfg = Ext.apply({
            title: $("button.import"),
            layout: 'border',
            width: 500,
            height: 288,
            items: formPanel,
            listeners: {
                hide: function() {
                    Ext.getBody().unmask();
                },
                show: function() {
                    Ext.getBody().mask();
                }
            }
        }, cfg);
        this.callParent([cfg]);
    },
    importData: function(cfg) {
        var win = this;
        var form = this.formPanel.form;
        var viewModel = cfg.viewModel;
        var materialCode = form.findField("materialCode").getValue();
        var plantCode = form.findField("plantCode").getValue();
        var demandDateFrom = form.findField("demandDateFrom").getValue();
        var demandDateTo = form.findField("demandDateTo").getValue();
        if (Ext.isEmpty(plantCode)||Ext.isEmpty(demandDateFrom)||Ext.isEmpty(demandDateTo)) {
            Q.tips($("message.importNotBlank"), "E");
            return false;
        }
        var params = [];
        params.push(materialCode);
        params.push(plantCode);
        params.push(demandDateFrom == null ? "" : Ext.Date.format(demandDateFrom, "Y-m-d"));
        params.push(demandDateTo == null ? "" : Ext.Date.format(demandDateTo, "Y-m-d"));
        Ext.getBody().mask();
        Ext.Ajax.request({
            url: viewModel.get('dealUrl') + "/importfromsap",
            method: 'POST',
            params: {
                params: params
            },
            success: function(response) {
                var json = Ext.decode(response.responseText);
                if (false === json.success) { // grid.moduleName+"删除失败！未知系统异常！
                    Q.tips(json.data || $("message.import.failure") + "<br/>" + $("message.system.error"), "E");
                    return;
                }
                win.hide();
                Q.tips($("message.import.success"));
                viewModel.getVp().editWin.fireEvent("submit");
                viewModel.getVp().grid.getStore().reload();
                Ext.getBody().unmask();
            },
            failure: function(response) { // grid.moduleName+"删除失败！请检查与服务器的连接是否正常，或稍候再试！
            	win.hide();
                Q.tips($("message.import.failure") + "<br/>" + $("message.system.disconnect"), "E");
            },
            callback: function() {
            	win.hide();
            	Ext.getBody().unmask();
            }
        });
    },
    createFormPanel: function(cfg) {
        var win = this;
        var viewModel = cfg.viewModel;
        var formPanel = Ext.create("Ext.ux.form.FormPanel", {
            region: "center",
            labelWidth: 100,
            autoScroll: true,
            anchor: "100% 95%",
            layout: "column",
            border: true,
            bodyStyle: "padding:20px 30px 0px",
            defaults: {
                xtype: "textfield",
                border: false,
                labelWidth: 70,
                style: {
                    padding: '0px'
                },
                margin: '0 0 10 0',
                columnWidth: 1
            },
            items: [{
                QfieldLabel: "物料编码",
                fieldLabel: $("purchasingRequisitionDtl.materialCode"),
                name: 'materialCode',
                allowBlank: true
            }, {
                QfieldLabel: "工厂编码",
                fieldLabel: $("purchasingRequisitionDtl.plant") + "<font color='red'>*</font>",
                name: "plantCode",
                hiddenName: 'model.plantCode',
                xtype: 'uxcombo',
                store: viewModel.getStore('plantStore'),
                valueField: 'plantCode',
                displayField: 'plantName',
                displayValue: 'plantCode',
                innerTpl: true,
                allowBlank: false,
                listeners: {
                    "afterrender": function() {
                        var store = viewModel.getStore('plantStore');
                        if (store.getCount() == 1) {
                            var form = formPanel.getForm();
                            var record = store.getAt(0);
                            form.findField("plantCode").setValue(record.get("plantCode"));
                        }
                    }
                }
            }, {
                QfieldLabel: "需求日期",
                fieldLabel: $("purchasingRequisitionDtl.demandDate") + "<font color='red'>*</font>",
                name: "demandDateFrom",
                xtype: "datefield",
                format: "Y-m-d"
            }, {
                QfieldLabel: "至",
                fieldLabel: $("label.to") + "<font color='red'>*</font>",
                name: "demandDateTo",
                xtype: "datefield",
                format: "Y-m-d",
                margin: '0'
            }],
            buttons: ["->", {
                text: $("button.return"),
                ui: 'gray-btn',
                margin: '0 30 28 0',
                handler: function() {
                    win.hide();
                }
            }, {
                text: $("button.import"),
                ui: 'blue-btn',
                margin: '0 30 28 0',
                handler: function() {
                    win.importData(cfg);
                }
            }]
        });
        return formPanel;
    }
});