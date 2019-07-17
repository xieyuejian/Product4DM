Ext.define("Ext.srm.window.UserSelectWin", {
	extend : "Ext.ux.Window",
	alias : "UserListSelectWin",
	constructor : function(B) {
		var B = B || {};
		var C = false !== B.singleSelect;
		var A = this.gridPanel = this.createGrid(B, C);
		B = Ext.apply({
			title : $("UserList"),
			layout : "border",
			width : 750,
			height : 400,
			items : [A],
			renderTo : B.renderTo,
			constrain : true,
			listeners : {
				"hide" : function() {
					A.getSelectionModel().clearSelections()
				}
			}
		}, B);
		this.callParent([B]);
		if (Ext.isFunction(B.select)) {
			this.on("select", B.select)
		}
	},
	createGrid : function(C, D) {
		var B = this;
		var A = this.gridPanel = Ext.create("Ext.ux.grid.GridPanel", {
			border : true,
			sm : {
				singleSelect : D
			},
			viewConfig : {
				forceFit : true,
				stripeRows : true
			},
			store : {
				url : path_console + "/sys/user/list",
				baseParams : C.baseParams || {}
			},
			cm : {
				defaultSortable : false,
				columns : [{
					Qheader : "ID",
					header : $("user.id"),
					dataIndex : "id",
					disabled : true
				}, {
					Qheader : "用户ID",
					header : $("user.userId"),
					dataIndex : "userId",
					disabled : true
				}, {
					Qheader : "用户名",
					header : $("user.userCode"),
					dataIndex : "userCode"
				}, {
					Qheader : "姓名",
					header : $("user.userName"),
					dataIndex : "userName"
				}, {
					Qheader : "联系电话",
					header : $("user.tel"),
					dataIndex : "tel"
				}, {
					Qheader : "邮箱",
					header : $("user.email"),
					dataIndex : "email"
				}, {
					Qheader : "身份证号",
					header : $("user.idcard"),
					dataIndex : "idCard"
				}]
			},
			tbar : [{
				text : $("button.select"),
				iconCls : "icon-save",
				handler : function(E) {
					var G = A.doSelect(B, D);
					if (G) {
						var F = A.getStore();
						E.ownerCt.find("name", "userName")[0].setValue("");
						E.ownerCt.find("name", "userCode")[0].setValue("");
						F.proxy.extraParams.filter_LIKE_userName = "";
						F.proxy.extraParams.filter_LIKE_userCode = "";
						F.load({
							params : {
								start : 0,
								limit : 20
							}
						});
						B.hide()
					}
				}
			}, {
				text : $("button.return"),
				iconCls : "icon-return",
				handler : function(E) {
					var F = A.getStore();
					E.ownerCt.find("name", "userName")[0].setValue("");
					E.ownerCt.find("name", "userCode")[0].setValue("");
					F.proxy.extraParams.filter_LIKE_userName = "";
					F.proxy.extraParams.filter_LIKE_userCode = "";
					F.load({
						params : {
							start : 0,
							limit : 20
						}
					});
					B.hide()
				}
			}, "->", {
				xtype : "label",
				text : $("user.userCode")
			}, {
				name : "userCode",
				xtype : "textfield",
				width : 100
			}, "-", {
				xtype : "label",
				text : $("user.userName")
			}, {
				name : "userName",
				xtype : "textfield",
				width : 100
			}, {
				text : $("button.search"),
				iconCls : "icon-search",
				handler : function(E) {
					var F = A.getStore();
					var G = E.ownerCt.find("name", "userName")[0].getValue();
					var H = E.ownerCt.find("name", "userCode")[0].getValue();
					F.proxy.extraParams.filter_LIKE_userName = G;
					F.proxy.extraParams.filter_LIKE_userCode = H;
					F.load({
						params : {
							start : 0,
							limit : 20
						}
					})
				}
			}],
			listeners : {
				"rowdblclick" : function(E, F) {
					if (A.doSelect(B, D)) {
						var G = A.getStore();
						G.proxy.extraParams.filter_LIKE_userName = "";
						G.proxy.extraParams.filter_LIKE_userCode = "";
						G.load({
							params : {
								start : 0,
								limit : 20
							}
						});
						B.hide()
					}
				}
			},
			doSelect : function(G, I) {
				var F = this,
					H = F.getSelectionModel(),
					E = H.getSelection();
				if (E.length < 1) {
					Q.tips($("message.pleaseSelect"), "E");
					return false
				}
				if (I) {
					G.fireEvent("select", F, E[0])
				} else {
					G.fireEvent("select", F, E)
				}
				Q.tips($("message.selectSuccess"));
				return true
			}
		});
		return A
	}
});