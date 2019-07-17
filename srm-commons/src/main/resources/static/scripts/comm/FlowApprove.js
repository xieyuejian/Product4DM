
/**
 * 审核功能添加附件
 */
Ext.define('Ext.comm.FlowApprove', { 
    extend:'Ext.ux.Window',
    alias: "flowApprove",
    requires: ['Ext.ux.button.UploadButton',
    		  "Ext.srm.ux.SrmPluploadButton",
              "Ext.srm.form.SrmFileField",
              "Ext.srm.form.SrmFileIdField",
              "Ext.srm.ux.SrmFileColumn"
    ],
    constructor: function(cfg) {
        var win = this;
        cfg = cfg || {};
        var renderTo = "undefined" == typeof cfg.win ? cfg.me.id : cfg.win.id;
        cfg = Ext.apply({
            renderTo: renderTo  
        }, cfg);

        var formPanel = this.formPanel = win.createFormPanel(cfg);
        
        var cfg = Ext.apply({
            title: $('message.prompt'),
            layout: 'border',
            width: 500,
            height: 300,
            items: [formPanel],
            renderTo:cfg.moduleId,
            listeners: {
                'hide': function() {
                	
                }
            },
            alwaysOnTop:true
        }, cfg);
        
        this.callParent([cfg]);
    },
    createFormPanel: function(cfg) {
        var win = this;
        var me = cfg.me;
        var billKey = "";
        var moduleCode = "";
        if(Ext.isEmpty(cfg.billKey)){
        	
        	//从消息通知进入的审核动作
        	if(undefined == me.grid.getSelection()[0]){
        		var name = 'model.' + me.grid.store.idProperty;
        		billKey = cfg.win.vpWin.editWin.formPanel.form.findField(name).getValue();
        	}else{
        		billKey = me.grid.getSelection()[0].get(me.grid.store.idProperty);
        	}
        	
        	moduleCode = me.getVp().billTypeCode;
        	
        	if(0 < moduleCode.indexOf(',')){
        		var certificationStatus = me.grid.getSelection()[0].get('certificationStatus');
        		
        		if(undefined == certificationStatus){
        			certificationStatus = cfg.win.vpWin.editWin.formPanel.form.findField('model.certificationStatus').getValue();
        		}
        		
        		if('REGISTRATION' == certificationStatus){
        			moduleCode = 'ZSR';
        		}else if('POTENTIAL' == certificationStatus){
        			moduleCode = "ZSP";
        		}else if('QUALIFIED' == certificationStatus){
        			moduleCode = "ZSR";
        		}
        	}
        	
        }else{
        	billKey = cfg.billKey;
        	moduleCode = cfg.moduleCode;
        }
        
        var formPanel = Ext.create("Ext.ux.form.FormPanel", {
            region: "center",
            overflowX: "hidden",
            overflowY: "auto", 
            labelWidth: 100,
            layout: "column",
            border: true,
            defaults: {
                columnWidth: 1,
                border: false,
                xtype: "textfield"
            },
            items: [ 
		        {
	                xtype: 'label',
	                text: $('message.operator.confirm').replace('{0}',cfg.btnText),
	                margin: '10px 0px 5px 20px'
	            },{
	                xtype: 'label',
	                text: cfg.btnName == "TOPASS" ? $('message.canInput') + $('message.topassInput') : 
	                								$('message.mustInput') + $('message.tonopassInput'),
	                margin: '5px 0px 10px 20px'
	            },
	            {
	            	name: 'message',
	                xtype:'textarea',
	                height:50,
	                allowBlank:cfg.btnName == "TOPASS" ? true : false, 
	                width: 480,
	                margin:"0px 0px 0px 5px"
	            },{
	                name: "upload",
	                xtype: "srmpluploadbutton", 
	                text: "附件上传",
	                fileGroupIdField:"model.uploadFileGroupId",//根据自己配置附件组id存储字段
	                fileViewField:"fileViewField",//根据自己配置附件渲染字段,
	                moduleCode: moduleCode,
	                billKey: billKey, 
	                margin:"0px 0px 0px 20px",
	                listeners:{
	                    "uploadsuccess":function(xtype, _self, data, record){
					        try{ 
					           //alert("上传成功后");
					        }catch(e){
					            console.log(e);
					        }
					    }
	                }
	            },{ 
	                xtype: "srmfilefield", 
	                name: "fileViewField",
	                columnWidth: 0.80, 
	                listeners:{
	                   "operation": function(_self , operations, operationsInfo){
					        try{ 
					            operations["delete"] = true;// 是否允许删除
					        }catch(e){
					            console.log(e);
					        }
					    }
	                },
	                style:{
	                	margin: '0px 0px 0px 20px'
	                } 
	            }, {
	                QfieldLabel: "附件",
	                xtype: "srmfileidfield",
	                hidden:true,
	                fieldLabel: $("censorQuality.uploadFileGroupId"),
	                fileViewField:"fileViewField",//附件渲染字段
	                name: "model.uploadFileGroupId"
	            }],
            buttons:[
            	{
					text : $("label.return"), 
					ui:'gray-btn',
					handler : function() {
						win.close();
					}
				}/*,
				{
					xtype : 'uploadButton',
					text : $("label.Annex"),
					margin : "0 30 20 0",
					style : "width:80px;height:34px;",
					handler : function() {
						var uploadFileGroupId = formPanel.form.findField('model.uploadFileGroupId').getValue();
						if(moduleCode == "HT" || moduleCode == "YZD"){
							var fileWin = me.openUploadWindows('', uploadFileGroupId, 'uploadFile4View', formPanel.getForm(), null,null,null,formPanel.id, moduleCode,billKey);
						}else{
							var fileWin = me.openUploadWindows(null, uploadFileGroupId, 'uploadFile4View', formPanel.getForm(), null,null,null,moduleCode,billKey);
						}
					}
				}*/,{
					text : cfg.btnText,
					name:cfg.btnName, 
					ui:'blue-btn',
					handler : function() {
						win.submitForm(cfg,formPanel,moduleCode,billKey);
					}
				}
				
            ]
        });
        return formPanel;
    },
    
    submitForm:function(cfg,formPanel,moduleCode,billKey){
    	var win = this;
    	var me = cfg.me;
    	var url = "";
    	
    	if(!Ext.isEmpty(cfg.url)){
    		url = cfg.url;
    	}else{
    		url = me.dealUrl + (cfg.dealMethod ? ("/" + cfg.dealMethod) : "/dealstatus");
    	}
    	
    	if(Ext.isEmpty(billKey)){
    		 billKey = me.grid.getSelection()[0].get(me.grid.store.idProperty);
    	}
    	
    	if(cfg.btnName != "TOPASS" && Ext.isEmpty(formPanel.form.findField("message").getValue())){
    		Q.tips($('message.noPassViewCanNotBeEmpty'), "E");
    		return false;
    	}else{
    		win.hide();
    		var waitWin = Ext.create("Ext.window.MessageBox");
    		
	        waitWin.wait($("message.submit.wait"), $("message.submit.data"), {
	            renderTo: cfg.renderTo,
	            alwaysOnTop:true
	        });
	        
	        Ext.Ajax.request({
	        	url: url,
	        	params: {
	                "id": billKey,
	                "billState": cfg.btnName,
	                "message": formPanel.form.findField('message').getValue()
	            },
	            method: "POST",
	            success: function(d) {
	                var json = Ext.decode(d.responseText);
	                var flag = json.success;
	                if (flag) {
                		me.grid.getStore().reload();
	                	if(!Ext.isEmpty(cfg.win)){
							cfg.win.hide();
						}
	                	
	                	win.close();
	                } else {
	                    var msg = json.info || $("message.submit.failure");
	                    Q.error(msg)
	                    win.show();
	                }
	            },
	            callback: function() {
	            	waitWin.hide()
	            }
	        });
    	}
    }
    
});
