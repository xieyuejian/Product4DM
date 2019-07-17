Ext.define("Ext.srm.window.BidEvalSelectWin", {
	extend : "Ext.ux.Window",
	alias : "bidEvalSelectWin",
	constructor : function(B) {
		var B = B || {};
		var C = false !== B.singleSelect;
		var A = this.gridPanel = this.createGrid(B, C);
		B = Ext.apply({
					title : $("biddingSure.biddingEval"),
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
				forceFit : false,
				stripeRows : true
			},
			store : {
				url : path_srm + "/bidding/bideval/list",
				baseParams : C.baseParams || {}
			},
			cm : {
				defaultSortable : false,
				columns : [{
							Qheader : "评标单ID",
							header : $("bidEval.bidEvalId"),
							dataIndex : "biddingEvalId",
							disabled : true
						}, {
							Qheader : "招标单ID",
							header : $("bidEval.bidId"),
							dataIndex : "bid.bidId",
							disabled : true
						}, {
							Qheader : "评标单单号",
							header : $("bidEval.bidEvalNo"),
							dataIndex : "bidEvalNo",
							width : 170
						}, {
							Qheader : "招标单单号",
							header : $("bid.bidNo"),
							dataIndex : "bid.bidNo",
							width : 150
						}, {
							Qheader : "项目名称",
							header : $("bid.projectName"),
							dataIndex : "bid.projectName",
							width: 170,
		                    renderer:function(v,m){
		                        m.tdAttr = "data-qtip='" + v + "'"; 
		                        return v;
		                    } 
						},{
							Qheader : "采购组织名称",
							header : $("bid.purchasingOrgName"),
							dataIndex : "bid.purchasingOrgName",
							width : 150,
		                    renderer:function(v,m){
		                        m.tdAttr = "data-qtip='" + v + "'"; 
		                        return v;
		                    } 
						}, {
							Qheader: "物料组", 
							header: $("materialGroup.title"), 
							dataIndex: "bid.materialGroupName",
							width : 150,
		                    renderer:function(v,m){
		                        m.tdAttr = "data-qtip='" + v + "'"; 
		                        return v;
		                    } 
						}, {
							Qheader : "信息记录类别名称",
							header : $("bid.infoRecordCategoryName"),
							dataIndex : "bid.infoRecordCategoryName",
							width : 150
						}, {
							Qheader: "采购组织编码", 
							header: $("bid.purchasingOrgCode"), 
							dataIndex: "bid.purchasingOrgCode", 
							hidden: true
						}, {
							Qheader: "物资类型编码", 
							header: $("bid.materialGroupCode"), 
							dataIndex: "bid.materialGroupCode", 
							hidden: true
						}, {
							Qheader : "实际开标时间",
							header : $("bid.popenTime"),
							dataIndex : "bid.popenTime",
							type : "date",
							dateFormat : "Y-m-d H:i:s",
							renderer : Ext.util.Format.dateRenderer("Y-m-d H:i:s"),
							hidden : true
						}, {
							Qheader : "实际开标时间",
							header : $("bid.openTime"),
							dataIndex : "bid.openTime",
							type : "date",
							dateFormat : "Y-m-d H:i:s",
							renderer : Ext.util.Format.dateRenderer("Y-m-d H:i:s"),
							hidden : true
						}, {
							Qheader : "决标方式",
							header : $("bid.bidSureMode"),
							dataIndex : "bid.bidSureMode",
							hidden : true
						}, {
							Qheader : "评分方法",
							header : $("bid.scoreMethod"),
							dataIndex : "bid.scoreMethod",
							hidden : true
						}, {
							Qheader : "发票类型",
							header : $("bid.invoiceType"),
							dataIndex : "bid.invoiceType",
							hidden : true
						}, {
							Qheader : "付款条件说明",
							header : $("bid.paymentTermName"),
							dataIndex : "bid.paymentTermName",
							hidden : true
						}, {
							Qheader : "付款方式",
							header : $("bid.payMentName"),
							dataIndex : "bid.payMentName",
							hidden : true
						}, {
							Qheader : "中标供应商保证金处理方式",
							header : $("bid.depositDealMode"),
							dataIndex : "bid.depositDealMode",
							hidden : true
						},{
							Qheader : "价格有效期至",
							header : $("bid.priceValidity"),
							dataIndex : "bid.priceValidity",
							type : "date",
							dateFormat : "Y-m-d H:i:s",
							renderer : Ext.util.Format.dateRenderer("Y-m-d H:i:s"),
							hidden : true
						}, {
			                QfieldLabel: "是否入库",
			                fieldLabel: $("project.isWareHousing"),
			                name: "bid.isWarehousing",
			                hidden: true
			            }]
			},
			tbar : [{
						text : $("button.select"),
						iconCls : "icon-save",
						handler : function(E) {
							var G = A.doSelect(B, D);
							if (G) {
								var F = A.getStore();
								E.ownerCt.find("name", "bidEvalNo")[0].setValue("");
								E.ownerCt.find("name", "bidNo")[0].setValue("");
								E.ownerCt.find("name", "projectName")[0].setValue("");
								F.proxy.extraParams={
									"filter_EQ_bid_status": "TOSURE"//待定标
								};
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
							E.ownerCt.find("name", "bidEvalNo")[0].setValue("");
							E.ownerCt.find("name", "bidNo")[0].setValue("");
							E.ownerCt.find("name", "projectName")[0].setValue("");
							F.proxy.extraParams = {};
							F.removeAll();
							B.hide()
						}
					}, "->", {
						xtype : "label",
						text : $("bidEval.bidEvalNo")
					}, {
						name : "bidEvalNo",
						xtype : "textfield",
						width : 100
					}, {
						xtype : "label",
						text : $("bid.bidNo")
					}, {
						name : "bidNo",
						xtype : "textfield",
						width : 100
					}, {
						xtype : "label",
						text : $("bid.projectName")
					}, {
						name : "projectName",
						xtype : "textfield",
						width : 100
					}, {
						text : $("button.search"),
						iconCls : "icon-search",
						handler : function(E) {
							var F = A.getStore();
							F.proxy.extraParams={
								"filter_EQ_bid_status": "TOSURE"//待定标
							};
							var G = E.ownerCt.find("name", "bidEvalNo")[0].getValue(),
								bidNo = E.ownerCt.find("name", "bidNo")[0].getValue(),
								projectName = E.ownerCt.find("name", "projectName")[0].getValue();
							if(!Ext.isEmpty(G)){
								F.proxy.extraParams.filter_EQ_bidEvalNo = G;
							}
							if(!Ext.isEmpty(bidNo)){
								F.proxy.extraParams.filter_EQ_bid_bidNo = bidNo;
							}
							if(!Ext.isEmpty(projectName)){
								F.proxy.extraParams.filter_LIKE_bid_projectName = projectName;
							}
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
						G.proxy.extraParams = {};
						G.removeAll();
						B.hide()
					}
				}
			},
			doSelect : function(G, I) {
				var F = this, H = F.getSelectionModel(), E = H.getSelection();
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