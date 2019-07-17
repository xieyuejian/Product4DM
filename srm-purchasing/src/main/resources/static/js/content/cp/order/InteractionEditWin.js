/**
 * @class {Cp.order.InteractionEditWin}
 * @extend {Ext.ux.Window}
 * 订单交互编辑窗体
 * 
 * @param {object} cfg :
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 */

Ext.define('Cp.order.InteractionEditWin', {
    extend: 'Ext.ux.Window',
    constructor: function(cfg) {
        var win = this;
        cfg = cfg || {};

        var formPanel = this.formPanel = this.createFormPanel(cfg);
        if(cfg.isView){
            this.setFormValues(cfg.selected,cfg.vp,formPanel);    
        }
        
        cfg = Ext.apply({
            title: $('priceQuote.interaction'),
            width: 500,
            height: 360,
            closeAction: 'hide',
            style: 'padding-top:10px;padding-left:15px;',
            constrain: true,
            renderTo: cfg.moduleId,
            items: [formPanel]
        }, cfg);

        this.callParent([cfg]);
        
        if (!Ext.isEmpty(cfg.select) && Ext.isFunction(cfg.select)) {
            this.on('select', cfg.select);
        }
    },
    createFormPanel: function(cfg) {
        var vp = cfg.vp;
        var isView = cfg.isView;
        var win = this;

        var formPanel = Ext.create('Ext.ux.form.FormPanel', {
            layout: "column", 
            autoScroll: true,
            bodyStyle:"padding:10px", 
			width : 500,
            defaults: {
                labelWidth: 104,
                columnWidth: 1,
                xtype: "textfield" ,
                border: false
            },
            items: [{
                name: 'model.bbsContent',
                xtype: 'textarea',
                width: '100%',
                height: 200,
                allowBlank: false,
                hideLabel: true,
                validator: 'maxlength',
                maxLength: 500
            },{
				xtype : 'srmpluploadbutton',
				name : 'uploadBtn',
				text : $('label.Annex'),
				fileViewField:"uploadFile4View",
				fileGroupIdField:"model.uploadFileGroupId",
				style:{
					"margin":"0px 0px 0px 14px"
				},
				columnWidth:0.2
			},{ 
				name : "uploadFile4View",
				xtype:"srmfilefield",
				columnWidth:0.7
			},{
				QfieldLabel : "附件",
				xtype : "srmfileidfield", 
				name : "model.uploadFileGroupId",
				fileViewField:"uploadFile4View"
			}],
            buttons: [{
                text: $('bbs.publish'),
                style: 'width:80px;height:34px;',
                ui: "blue-btn",
                hidden: isView,
                handler: function() {
                    win.submitBBS(cfg);
                }
            }, {
                text: $('bbs.close'),
                tyle: 'width:80px;height:34px;',
                ui: "gray-btn",
                handler: function() {
                    win.destroy();
                }
            }]

        });

        return formPanel;
    },
    setFormValues: function(record,vp,formpanel) {
        var form = formpanel.getForm();
        form.findField('model.bbsContent').setReadOnly(true);
        form.findField('model.bbsContent').setValue(record.get('bbsContent'));
        form.findField('model.uploadFileGroupId').setValue(record.get('uploadFileGroupId'));
    },
    submitBBS: function(cfg) {
        var win = this;
        var vp = cfg.vp;
        var formPanel = win.query('form')[0];
        if (formPanel.isValid()) {
            var form = formPanel.getForm();
            var bbsContent = form.findField('model.bbsContent').getValue().replace(/\r/g, '').replace(/\n/g, '<br/>'); 
            var fileGroupId = form.findField('model.uploadFileGroupId').getValue();
            var params = {
                'model': {
                    'billId': vp.grid.getSelectionModel().getSelection()[0].get('purchaseOrderId'),
                    'billTypeCode': 'CGD',
                    'bbsContent': bbsContent,
                    'uploadFileGroupId': fileGroupId
                }
            };
            Ext.getBody().mask();

            Ext.Ajax.request({
                url: path_console + '/sys/billbbs/save',
                headers:{
	           		"Content-Type":"application/json; charset=utf-8" 
	        	},
                params: Ext.encode(params),
                success: function(resp, opt) {
                    Q.tips($('message.operator.success'));
                    win.destroy();
                },
                failure: function(resp, opt) {
                    Q.tips($('message.operator.failure'), 'E');
                },
                callback: function() {
                    var lastOptions = grid.getStore().lastOptions;
                    Ext.apply(lastOptions, {
                    });
                    grid.getStore().reload(lastOptions);
                    Ext.getBody().unmask();
                }
            });
        } else {
            Q.tips(String.format($('bbs.content.max'), 'E'));
        }
    }
});