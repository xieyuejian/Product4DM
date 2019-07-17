/**
 * @class {Cp.order.ProcessUserSelectWin}
 * @extend {Ext.ux.Window}
 * 审核流程授权界面弹出框
 * 
 * @param {object} cfg :
 * @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 * 
 */
Ext.define("Ext.comm.ProcessUserSelectWin", {
	extend: "Ext.ux.Window",
	constructor:function(cfg) {
		cfg = cfg || {};
		Ext.applyIf(cfg, {
			forceFit: true,//列表自适应grid用
			singleSelect: true,//列表单选grid用
			constrain: true,
			renderTo: cfg.moduleId
		});
		var grid = this.gridPanel = this.createGrid(cfg, cfg.singleSelect|| false);

		cfg = Ext.apply({
			title : $("bbs.user"),
			layout : "border",
			width : 750,
			height : 500,
			items : [ grid ],
			listeners : {
				"hide" : function() {
					grid.getSelectionModel().clearSelections();
				}
			}
		}, cfg);
		
		this.callParent([cfg]);

		if (Ext.isFunction(cfg.select)) {
			this.on("select", cfg.select);
		}
	},
	
	createGrid : function(cfg, singleSelect) {
		var win = this;
		var singleSelect = cfg.singleSelect;
		var queryBar = Ext.create("Ext.toolbar.Toolbar", { 
			style: {
                margin: '10px 0 10px 0',
                background: '#FFF',
                border: '1px'
            },
			items : [{
				xtype : "label",
				text : $("login.userName") + "/"+ $("correctiveNotice.name")
			},{
				xtype : "textfield",
				margin : "0 0 0 5",
				// 用户名或姓名
				emptyText : $("login.userName") + "/"+ $("correctiveNotice.name"),
				name : "searchParam",
				width :535
			},"->",
			{
				text : $("button.search"),// 查询
				iconCls : "icon_srm_search",
				margin : "0 0 0 0",
				style : "width:80px;height:30px;",
				ui:'blue-btn',
				handler : function() {
					var store = grid.getStore();
					searchValue = queryBar.query("textfield[name=searchParam]")[0].getValue();;
					store.proxy.extraParams.filter_LIKE_userCode_OR_userName = searchValue;
					store.load({
						params : {
							start : 0,
							limit : 14
						}
					});
				}
			} ]
		});
        var tBar = Ext.create("Ext.toolbar.Toolbar",{
	        dock : 'bottom',
	        style: {
                margin: '0',
                background: '#FFF',
                padding: '0px',
                border: '1px'
            },
			items : ["->",{
						text : $("label.return"),
						margin : "0 30 20 0",
						style : "width:80px;height:34px;",
						ui:'gray-btn',
						handler : function() {
							win.hide();
						}
					},{
						text : $("label.select"),
						margin : "0 0 20 0",
						style : "width:80px;height:34px;",
						ui:'blue-btn',
						handler : function() {
							var selectFlag = grid.doSelect(win,singleSelect);
		                    if(selectFlag && singleSelect){
		                        win.hide(); //单选时，"+labels.Select+"完成后自动隐藏
		                    }
						}
					}]
		});
		var grid = Ext.create("Ext.ux.grid.GridPanel",{
				pageSize:14,	
				border : true,
				ui:'small-grid',
				style: {
	                padding: '0px 20px 0px 20px',
	                border: '1px'
	            },
	            enableColumnHide: false,
	            sm : {
					singleSelect : singleSelect
				},
				store : {
					url : path_srm + "/sys/srmbpm/getuser4page",
					baseParams : cfg.baseParams || {}
				},
				cm : {
					defaultSortable : false,
					columns : [
						{header : $("login.userName"), dataIndex : 'userCode'},
						{header : $("correctiveNotice.name"), dataIndex : 'userName'} 
					]
				},
				dockedItems: [tBar,queryBar],
				listeners : {
					"itemdblclick" : function(g, i) {
						if (grid.doSelect(win, singleSelect)) {
//							win.hide();
						}
					}
				},
				// 选择
				doSelect : function(win, singleSelect) {
					var grid = this, sm = grid.getSelectionModel(), selections = sm.getSelection();
					if (selections.length < 1) {
						Q.tips($("message.pleaseSelect" + cfg.type + "UserBeforeSubmit"),'E');//请先选择需要--的用户！
						return false;
					}
					var userNames = "",userIds = "";
					Q.each(selections, function(r, i){
						if(!Ext.isEmpty(r.get("userName")) && !Ext.isEmpty(r.get("userId"))){
							if((i+1) != selections.length){
								userNames += r.get("userName")+"、";
								userIds += r.get("userId")+",";
							}else{
								userNames += r.get("userName");
								userIds += r.get("userId");
							}
						}
					})
					//数据提交的遮罩
					var mk = new Ext.LoadMask({
						msg : $("message."+cfg.type+"Authorization.submitTips"),
						target : grid
					});
					//确定选择XX用户（选择的用户名称）授权为当前审核节点用户？
					Q.confirm($("message."+cfg.type+"User.confirm").replace("{0}",userNames), {
						renderTo:grid.id,//增加背景遮罩
						ok: function(){
							mk.show();
							Ext.Ajax.request({
					     	     url: path_srm + "/sys/srmbpm/" + cfg.url, 
					             params:{
					            	 "filter_IN_userId": userIds,
					            	 "filter_processKey": cfg.processKey,
					            	 "filter_dataKeyId": cfg.dataKeyId,
					            	 "filter_billNo": cfg.billNo,
					            	 "filter_moduleName": cfg.moduleName
					             },
					             success: function(response){ 
					 	                var json = Ext.decode(response.responseText); 
					 					if(false === json.success){//grid.moduleName+"删除失败！未知系统异常！
					 						Q.tips(json.info ||$("message."+cfg.type+"Authorization.failure"));
					 	                }else {
					 	                	Q.tips($("message."+cfg.type+"Authorization.success"));
					 	                	win.hide();
					 	                }
					             },
					 			failure: function(response){//grid.moduleName+"删除失败！请检查与服务器的连接是否正常，或稍候再试！ 
					                var json = Ext.decode(response.responseText);  
					 				Ext.getBody().unmask();
					 				if(json && json.data){
					 					Q.error(json.data || $("message."+cfg.type+"Authorization.failure"));//数据加载失败！
					 				}else{
					 					Q.error($("message."+cfg.type+"Authorization.failure")+"<br/><br/>"+$("message.system.disconnect"));//数据加载失败！请检查与服务器的连接是否正常，或稍候再试。
					 				}
					             },
					             callback: function(){
					            	 mk.hide();
					             }
					         }); 
						}
					}); 
				}
			});
		return grid;
	}
});