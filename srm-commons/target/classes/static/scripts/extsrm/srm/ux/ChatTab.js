/**
 * 审批沟通公共列表
 */
Ext.define("Ext.srm.ux.ChatTab", { 
    extend: 'Ext.grid.GridPanel', 
	alias : "widget.chatTab", 
    xtype:"chatTab",
    isGrid:true,
    submitValue:false,
	tabClassName:"chatTab",
	ui:'neu-detail-grid', 
	requires:[
	    'Ext.srm.ux.SrmPluploadButton', 
		'Ext.srm.ux.SrmPluploadButton',
		"Ext.srm.form.SrmFileIdField",
		"Ext.srm.form.SrmFileField"
    ], 
    paramsMapping:{},
	header:{
		height:40,
		hidden:true
	},
	forceFit:false,//默认自适应 
	/**
	 * @cfg {object/Ext.ux.data.JsonStore} store
	 * <pre>
	 * grid使用的store，若未设置store，则根据url自动创建store
	 * store参数可以是json配置 或 Ext.ux.data.JsonStore对象
	 * <code>
	 * store:{
     url : '',
     baseParams: {...},
     ...
     * }
	 * store:new Ext.ux.data.JsonStore({...})
	 * </code>
	 * </pre>
	 */
	store:null,

	emptyText:"暂无数据", 
	//-------------------------设置系统缺省属性------------------
	region:"center",
	bodyStyle:"width:100%",

	headerBorders:false,
	autoWidth:true,
	loadMask: true, //loadMask: {msg: 数据加载中...'},
	border:false,
	stripeRows: true,
	autoScroll: true,
	ableExporter:false,//是否可导出
	viewConfig:{
		enableTextSelection: true
	},  
    columnLines: true, 
	title: $("flowInfo.bpmMessage"),    
    vpShowTbar: true,
	autoUploadField: true,
    viewConfig : {
        forceFit : true,// 当行大小变化时始终填充满
        autoScroll : true
    },
    selModel: {
		type: 'uxcheckboxmodel',
    	mode:'SINGLE',
		allowDeselect:true,
		checkSelector:"x-grid-row-single-checker",
		headerWidth:40,
		injectCheckbox: 1 
    },  
    columns: [
	    {xtype: 'rownumberer',width:40},
        {Qheader: "id", header: "id", dataIndex: "chatId", hideable: false,hidden:true},
        {Qheader: "单据Id", header: "单据Id", dataIndex: "businessKey", hidden: true,hideable: false},
        {Qheader: "单据类型", header: "单据类型", dataIndex: "processKey", hidden: true,hideable: false},
        {Qheader: "发送人Id", header: "发送人Id", dataIndex: "senderId", hidden: true,hideable: false},
        {Qheader: "发送人", header: $("flowInfo.senderName"), dataIndex: "senderName", width:160},
		{Qheader: "接收人", header: $('flowInfo.receipentName'), dataIndex: "receipentName", width:160},
        {Qheader: "留言信息", header: $("flowInfo.message"), dataIndex: "message", width:200,
            renderer: function(v, m, r) {
				if (Ext.isEmpty(v)) {
					v = "";
				}
				m.tdAttr = "data-qtip='" + v + "'";
				return v;
			}
		},
        {Qheader: "附件", header: "附件", dataIndex: "fileId", hidden: true,hideable: false},
        {Qheader: "附件", header: "附件", dataIndex: "uploadFile4View", hidden: true,hideable: false},
        {Qheader: "附件", header: "附件", dataIndex: "fileName", hidden: true,hideable: false},
        {Qheader: "附件", header: $('porder.viewUploadFile'), dataIndex: "uploadFileGroupId", width:160,
         renderer: function(even, grid, record) {
 			var me = this;
 			var vm = me.external
            var renderId = grid.record.id + "_tab";
            if (null != even) {
                var obj = {
                    moduleId: vm.moduleId,
                    uploadFileGroupId: even
                };
                return Ext.defer(function() {
                    Ext.widget("button", {
                        renderTo: renderId,
                        text: $("label.ClickToDownload"),
                        ui: "blue-btn",
                        margin: "0 10 0 10",
                        width: 100,
                        height: 30,
                        handler: function() {
                            var win = new Ext.comm.FileDownLoad(obj);
                            win.show()
                        }
                    })
                }, 100),
                Ext.String.format('<div id="{0}"></div>', renderId)
            }
            return $("wx.null")
        }},
        {Qheader: "接收人Id", header: "接收人Id", dataIndex: "receipentId", hidden: true,hideable: false},
		{	Qheader: "回复",
            xtype: 'actioncolumn',
            header: $("flowInfo.reply"),
            menuText: $("flowInfo.reply"),
            width:160,
            items: [{
				text: $("flowInfo.reply"),
                iconCls: 'icon-edit',
                tooltip: $("flowInfo.reply"),
                handler: function(_grid, rowIndex, colIndex, item, e, record){
                	var me = this;
                    var chatTab = me.up("chatTab"); 
					chatTab.auditChatReply(chatTab, rowIndex, colIndex, item, e, record);
                }
            }]
        },
       {
            Qheader: "发送时间", header: $('label.sendTime'), dataIndex: "createTime", type: "date", width:160, 
            dateFormat: "Y-m-d H:i:s",
            renderer: Ext.util.Format.dateRenderer("Y-m-d H:i:s")
        }
    ],
    dockedItems:{
    	xtype: 'toolbar',
    	ui:'list-tbar',
    	dock:"top",
    	maxHeight:26,
    	items:[{
	        text: "添加",// 新建交互信息
	        iconCls: "icon-add",
	        name:"chatAdd",
	    	maxHeight:26,
			disabled:false,
			ui:'list-tbar-btn',
	        handler: function(_self){
	        	var grid = _self.findParentByType(Ext.grid.Panel);  
	        	grid.auditChatAddHandler(_self); 
	        }
	    }, {
	        text: "删除",// 删除交互信息
			ui:'list-tbar-btn',
	    	maxHeight:26,
	        iconCls: "icon-delete",
	        name:"chatDel",
			disabled:false,
	        handler: function(_self){
	        	var grid = _self.findParentByType(Ext.grid.Panel);  
	        	grid.auditChatDeleteHandler(_self); 
	        }
	    }]
    }, 
    /**
     * 数据源
     */
    store:Ext.create('Ext.ux.data.JsonStore', {
      idProperty: "chatId", 
      sort: "createTime",
      url: path_srment + "/sys/srmbpm/getauditchat" 
    }),
    /**
     * 监听
     * @type 
     */
    listeners: {
        activate: function() {
            var me = this,vm = me.external,store = me.getStore(),vpList = vm.getVp(),grid = vpList.grid,
            selected = grid.getSelectionModel().getSelection()[0],paramsMapping=me.paramsMapping|| {};
            var idProperty = vm.get("vp_idProperty");
			var prepaymentsId = selected.get(idProperty);  
			var createUserId = selected.get("createUserId") == typeof "undefined" ? selected.get(paramsMapping["userId"]) : selected.get("createUserId");
			var billTypeCode = vm.get("vp_billTypeCode");
            store.load({
                params: {
                    filter_EQ_businessKey : prepaymentsId,
                    filter_IN_processKey : billTypeCode,
                    createUserId:createUserId
                }
            })
        } 
    },
	/**
	 * 审核沟通添加方法
	 */
	auditChatAddHandler : function(_self) {
		try{ 
			var me = this,record = me.getSelectionModel().getSelection()[0],vm = me.external,vpList = vm.getVp(),editWin = vm.getEditWin(),
			grid = vpList.grid,selected = grid.getSelectionModel().getSelection()[0],paramsMapping = me.paramsMapping || {},params = {},billNo={};   
			var idProperty = vm.get("vp_idProperty");
			var prepaymentsId = selected.get(idProperty);  
			var userId = selected.get("createUserId");
			var renderToId = editWin.id;
			var certificationStatus = selected.get('certificationStatus');
			var billTypeCode = vm.get("vp_billTypeCode");
			if("ZSR,ZSP,ZSQ" == billTypeCode){
				if('REGISTRATION' == certificationStatus){
        			billTypeCode = 'ZSR';
        		}else if('POTENTIAL' == certificationStatus){
        			billTypeCode = "ZSP";
        		}else if('QUALIFIED' == certificationStatus){
        			billTypeCode = "ZSQ";
        		}
			}
			var moduleName = vm.get("moduleName");
			if(vm.get("vp_gridCfg") && vm.get("vp_gridCfg").billNoField ){
				billNo = selected.get(vm.get("vp_gridCfg").billNoField); 
			}
			if (editWin.hidden === true) { 
				renderToId = vpList.id;
			}  
			params = {
				model:{
					processKey : billTypeCode,
					businessKey : prepaymentsId,
					moduleName: moduleName,
					billNo:billNo
				}
			}
			//处理参数取值 
			var keys = Ext.Object.getAllKeys(paramsMapping);
			if(keys && keys.length > 0){
				Ext.Array.each(keys,function(key,index){ 
					(params.model)[key] = selected.get(paramsMapping[key]);  
				});
			}
			
			params = Ext.apply(params,me.paramsMappingFn(vm, params, record, "add") || {});
			
		    var config = {
	            "moduleId": renderToId,
	            "grid": me,
	            "params": params,
	            "extraParams":{
					"billId":prepaymentsId,
					"billTypeCode":billTypeCode,
					"type":"add",
					"userId":userId || params["userId"]
				},
				"handler":"add"
		    }  
			config = Ext.apply(config,me.configMappingFn(vm, params, record, "add") || {}); 
			var auditChatWin = Ext.create("Ext.srm.ux.AuditChat", config); 
			auditChatWin.show(); 
		}catch(e){
			console.log(e);
		}
	},
	/**
	 * 审核沟通删除方法
	 */
	auditChatDeleteHandler : function(_self) {
		try{
			var me = this,vm = me.external,vp = vm.getVp(),editWin = vm.getEditWin(),userId = vm.get("userId"); 
			var grid = _self.findParentByType(Ext.grid.GridPanel); 
			var store = grid.getStore();
			var renderToId = editWin.id;
			if (editWin.hidden === true) {
				renderToId = vp.moduleId;
			}
			var selected = grid.getSelectionModel().getSelection()[0];
			if (Ext.isEmpty(selected)) {
				Q.tips($("message.pleaseSelect"),"E");
				return false;
			}else if (userId != selected.get("senderId")) {
				Q.tips("只能删除自己发送的消息！","E");
				return false;
			}
			var url = path_srm + "/sys/srmbpm/delete";
			var params = {
				chatId : selected.get("chatId")				
			};
			Q.confirm($('message.delete.confirm'), {
		    	renderTo: renderToId,
		        ok : function() { 
					Ext.getBody().mask($("message.submit.wait"), $("message.submit.data"));
					Ext.Ajax.request({
						url : url,
						params : params,
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
		}catch(e){
			console.log(e);
		} 
	}, 
	/**
	 * 审核沟回复处理
	 * @param {} _grid
	 * @param {} rowIndex
	 * @param {} colIndex
	 * @param {} item
	 * @param {} e
	 * @param {} record 当前操作记录
	 */
	auditChatReply : function(_grid, rowIndex, colIndex, item, e, record) {
		try{
			var me = this,vm = _grid.external,vpList = vm.getVp(),editWin = vm.getEditWin(),paramsMapping = me.paramsMapping || {},
			grid = vpList.grid,selected = grid.getSelectionModel().getSelection()[0],params = {},billNo={};   
            if (!Ext.isEmpty(record)
					&& record.get("receipentId") != s_userid) {
				Q.tips("只能回复接收人为本人的消息!","E");
				return false;
			}
            var idProperty = vm.get("vp_idProperty"); 
            var prepaymentsId = selected.get(idProperty); 
            var createUserId = record.get("senderId");
            var parentId = record.get("chatId");
			var renderToId = editWin.id;
			var billTypeCode = vm.get("vp_billTypeCode");
			if("ZSR,ZSP,ZSQ" == billTypeCode){
				var certificationStatus = selected.get("certificationStatus");
				if('REGISTRATION' == certificationStatus){
        			billTypeCode = 'ZSR';
        		}else if('POTENTIAL' == certificationStatus){
        			billTypeCode = "ZSP";
        		}else if('QUALIFIED' == certificationStatus){
        			billTypeCode = "ZSQ";
        		}
			}
			var moduleName = vm.get("moduleName");
			if(vm.get("vp_gridCfg") && vm.get("vp_gridCfg").billNoField ){
				billNo = selected.get(vm.get("vp_gridCfg").billNoField); 
			}
			if (editWin.hidden === true) { 
				renderToId = vpList.id;
			}  
			params = {
				model:{
					processKey: billTypeCode,
					businessKey: prepaymentsId,
					moduleName: moduleName,
					billNo: billNo,
					parentId: parentId
				}
			}
			//处理参数取值 
			var keys = Ext.Object.getAllKeys(paramsMapping);
			if(keys && keys.length > 0){
				Ext.Array.each(keys,function(key,index){ 
					(params.model)[key] = selected.get(paramsMapping[key]);  
				});
			}
			params = Ext.apply(params,me.paramsMappingFn(vm, params, record, "reply") || {});
			
		    var config = {
	            "moduleId": renderToId,
	            "grid": me,
	            "params": params,
	            "extraParams":{
					"billId":prepaymentsId,
					"billTypeCode":billTypeCode,
					"type":"reply",
					"userId":createUserId
				},
				"handler":"reply"
		    }  
			config = Ext.apply(config,me.configMappingFn(vm, config, record, "reply") || {}); 
			var auditChatWin = Ext.create("Ext.srm.ux.AuditChat", config); 
			var auditChatForm = auditChatWin.formPanel.getForm();
			auditChatForm.findField("receipentId").setValue(record.get("senderId"));
			auditChatForm.findField("receipentName").setValue(record.get("senderName"));
			auditChatWin.show();  
		}catch(e){
			console.log(e);
		} 
	},
	/**
	 * chatTab 参数匹配处理
	 * @type 
	 */
	paramsMappingFn:Ext.emptyFn,
	
	/**
	 * AuditChat 配置参数处理
	 * @type 
	 */
	configMappingFn:Ext.emptyFn
});