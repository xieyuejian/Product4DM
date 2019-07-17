/**
 * 审核功能添加附件
 */
Ext.define('Ext.srm.ux.AuditChat', { 
    extend:'Ext.ux.Window', 
	alias : "widget.auditChat", 
    xtype:"auditChat",
    requires: [ 
		'Ext.srm.ux.SrmPluploadButton',
		"Ext.srm.form.SrmFileIdField",
		"Ext.srm.form.SrmFileField"
    ],
    constructor: function(cfg) {
        var win = this;
        cfg = cfg || {}; 

        var formPanel = this.formPanel = win.createFormPanel(cfg);
        
        var cfg = Ext.apply({
            title: $('flowInfo.bpmMessage'),
            layout: 'border',
            width: 485,
            height: 340, 
            items: [formPanel],
            renderTo:cfg.moduleId, 
            buttons : [{	
				text : $("button.submit"), 
				ui : 'blue-btn',
				handler : function() {
					var isValid = win.formPanel.form.isValid();
					if (!isValid) {
						Q.tips($("message.data.incomplete"),"E");
						return false;
					}
					var params = cfg.params;
					var formValues = win.formPanel.getForm().getValues();
					Ext.apply(params.model, formValues);
					win.submitForm(cfg.grid, params);
					win.formPanel.getForm().reset();
					win.hide();
				}
			},{	
				text : $("button.return"), 
				ui : 'gray-btn',
				handler : function() {
					win.formPanel.getForm().reset();
					win.hide();
				}
			}] 
        }, cfg);
        
        this.callParent([cfg]);
    },
    /**
     * 
     * @param {} cfg
     * @return {}
     */
    createFormPanel: function(cfg) {
        var win = this;
        /**
		 * 用户store
		 */
		var userStore = Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_srm + '/sys/srmbpm/getauditperson',
				extraParams:cfg.extraParams,
				type : 'ajax'
			},
			fields : [ 'userId', 'userName' ],
			autoLoad : true,
			listeners: {
				load : function(grid,record,json){
					if(record.length==0){
						Q.tips($('auditChat.NotSend'),"E");
					}
				}
			}
		})
        var formPanel = Ext.create("Ext.ux.form.FormPanel", {
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
			items :
				[{
					xtype : "uxcombo",  
					labelWidth: 80,
					fieldLabel : $('flowInfo.to'),// 发送给
					name : "receipentId",
					hiddenName : "receipentId",
					allowBlank : false,
					valueField : 'userId',
					displayField : 'userName',
					store : userStore,
					editable : true,
					listeners : {
						select : function(combo, record){
							var form = this.findParentByType(Ext.form.Panel).form;
							form.findField('receipentName').setValue(record.get('userName'));
							form.findField('receipentId').setValue(record.get('userId'));
						},
						clear : function(){
							var form = this.findParentByType(Ext.form.Panel).form;
							form.findField('receipentId').reset();
							form.findField('receipentName').reset();
						}
					}
				},{
					name : 'receipentName',// 接收人姓名
					hidden : true
				},{
					name : "message",
					xtype : "textarea", 
					height : 150,
					allowBlank : false,
					validator : "maxlength",
					emptyText : $('max.length.text').replace('{0}', 150),
					maxLength : 150
				},{
					xtype : 'srmpluploadbutton',
					name : 'uploadBtn',
					text : $('label.Annex'),
					fileViewField:"uploadFile4View",
					fileGroupIdField:"uploadFileGroupId",
					style:{
						"margin":"0px 0px 0px 14px"
					},
					columnWidth:0.2
				},{ 
					name : "uploadFile4View",
					xtype:"srmfilefield",
					columnWidth:0.8
				},{
					QfieldLabel : "附件",
					xtype : "srmfileidfield", 
					name : "uploadFileGroupId",
					fileViewField:"uploadFile4View"
				}]
		});
        return formPanel;
    }, 
    /**
     * 提交处理
     * @param {} grid
     * @param {} params
     */
    submitForm:function(grid, params){
    	var me = this;
		Ext.getBody().mask($("message.submit.wait"), $("message.submit.data"));
		Ext.Ajax.request({
			url : path_srm + "/sys/srmbpm/" + me.handler,
			headers:{
           		"Content-Type":"application/json; charset=utf-8" 
        	},
            params: Ext.encode(params),
			success : function(response) {
				var json = Ext.decode(response.responseText);
				if (false === json.success) {
					Q.error("<font color='red'>" + json.msg
							|| $("message.operator.failure")
							+ "<br/><br/>"
							+ $("message.system.error")
							+ "</font>");
					return;
				}
				Q.tips("<font color='blue'>"
						+ $("message.operator.success")
						+ "</font>");
				grid.getStore().reload(); 
			},
			failure : function(response) {
				Q.error("<font color='red'>"
						+ $("message.operator.failure")
						+ "<br/><br/>"
						+ $("message.system.disconnect")
						+ "</font>"); 
			},
			callback : function() {
				Ext.getBody().unmask();
			}
		});
	}
});
