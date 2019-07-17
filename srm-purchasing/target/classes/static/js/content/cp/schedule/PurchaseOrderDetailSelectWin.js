/**
 * 采购订单明细信息 - for Q版本(ext-3.3+) //引用本选择列表无需为权限添加资源
 * 
 * @param {object}
 *            cfg {boolean} singleSelect 是否单选，缺省为多选 {object} baseParams 查询条件
 *            {function} select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *            单选时参数：(grid, record) 多选时参数：(grid, selections)
 */
Ext.define('Cp.schedule.PurchaseOrderDetailSelectWin', {
	extend : 'Ext.ux.Window',
	constructor : function(cfg, viewModel) {
		var win = this;
		cfg = cfg || {};
		cfg = Ext.apply({
					forceFit : true,
					singleSelect : true,
					constrain : true,
					renderTo : cfg.moduleId
				}, cfg);

		var grid = this.gridPanel = this.createGrid(cfg, viewModel);

		var cfg = Ext.apply({
					title : $('purchaseOrderDetail.title'),
					layout : 'border',
					width : 800,
					height : 500,
					items : [grid],
					listeners : {
						'hide' : function() {
							grid.getSelectionModel().clearSelections();
							grid.store.removeAll();
							this.url = "";
							if (!this.hidden) {
								this.hide();// 如果窗口状态为不隐藏时，隐藏它
							}
						}
					}
				}, cfg);

		this.callParent([cfg]);

		if (!Ext.isEmpty(cfg.select) && Ext.isFunction(cfg.select)) {
			this.on('select', cfg.select);
		}
	},
	createGrid : function(cfg, viewModel) {
		var win = this;
		var singleSelect = cfg.singleSelect;
		var vp = viewModel.getVp();

		var queryBar = Ext.create("Ext.toolbar.Toolbar", {
			// style:{
			// margin: '20px 0 15px 0',
			// background:'#FFF',
			// padding:'0px',
			// broder:'0px',
			// },
			style : "background-color:transparent;border-style: none",
			margin : '10px 0 10px 0',
			items : [{
						xtype : "label",
						text : $("porder.purchaseOrderNo")
					}, {
						name : "purchaseOrderNo",
						xtype : "textfield",
						width : 140
						// style:{margin:'0px 0px
					// 0px 6px'}
				}	, {
						xtype : "label",
						text : $("materialInfo.code")
						// style:{margin:'0px 0px
					// 0px 40px'}
				}	, {
						name : "materialCode",
						xtype : "textfield",
						width : 140
						// style:{margin:'0px 0px
					// 0px 6px'}
				}	, {
						xtype : "label",
						text : $("materialInfo.name")
						// style:{margin:'0px 0px
					// 0px 40px'}
				}	, {
						name : "materialName",
						xtype : "textfield",
						width : 140
						// style:{margin:'0px 0px
					// 0px 6px'}
				}	, "->", {
						text : $('button.search'),
						iconCls : "icon-search",
						style : "width:80px;height:30px;",
						// width : 80,
						// height : 30,
						margin : "0",
						ui : 'blue-btn',
						handler : function(_self) {
							var store = grid.getStore();
							var purchaseOrderNo = queryBar
									.query("textfield[name=purchaseOrderNo]")[0]
									.getValue();
							store.proxy.extraParams.filter_LIKE_purchaseOrder_erpPurchaseOrderNo = purchaseOrderNo;
							var materialName = queryBar
									.query("textfield[name=materialName]")[0]
									.getValue();
							store.proxy.extraParams.filter_LIKE_materialName = materialName;
							var materialCode = queryBar
									.query("textfield[name=materialCode]")[0]
									.getValue();
							store.proxy.extraParams.filter_LIKE_materialCode = materialCode;
							store.proxy.extraParams.start = 0
							store.proxy.extraParams.limit = 20;
							store.load();
						}
					}]
		});
		var bbar = Ext.create("Ext.toolbar.Toolbar", {
					dock : 'bottom',
					// style:{
					// margin: '0',
					// background:'#FFF',
					// padding:'0px',
					// border:'1px'
					// },
					style : {
						margin : '0px 0px 0px 20px',
						border : '0px'
					},
					items : ["->", {
								text : $("label.return"),
								margin : "5 20 5 0",
								style : "width:66px;height:26px;",
								ui : 'gray-btn',
								handler : function() {
									win.hide();
								}
							}, {
								text : $("label.select"),
								ui : 'blue-btn',
								margin : "5 0 5 0",
								style : "width:66px;height:26px;",
								handler : function() {
									var selectFlag = grid.doSelect(win,
											singleSelect);
									if (selectFlag) {
										win.hide();
									}
								}
							}]
				});
		var grid = new Ext.ux.grid.GridPanel({
			pageSize : cfg.pageSize || 20,
			border : false,
			// height : 322,
			ui : 'small-grid',
			// style:{padding:'0px 30px 0px 30px',
			// border:'1px'},
			style : {
				padding : '0px 20px 0px 20px',
				border : '1px'
			},
			enableColumnHide : false,
			sm : {
				singleSelect : singleSelect
			},
			// autoExpandColumn:"materialName",
			store : {
				url : path_srm + "/cp/sendschedule/getpurchaseorderdetail",
				baseParams : cfg.baseParams || {}
			},
			// pageSize:20,
			forceFit : false,
			cm : {
				defaultSortable : false,
				columns : [{
							Qheader : "采购订单号",
							header : $("porder.purchaseOrderNo"),
							dataIndex : 'purchaseOrder.purchaseOrderNo',
							width : 150,
							hidden : true
						}, {
							Qheader : "sap采购订单号",
							header : $("porder.purchaseOrderNo"),
							dataIndex : "purchaseOrder.erpPurchaseOrderNo",
							width : 150
						}, {
							header : $("label.rowNo"),
							dataIndex : 'rowIds',
							width : 50
							/* ,align:"right" */}, {
							header : $("vendor.code"),
							dataIndex : 'purchaseOrder.vendorCode',
							width : 100,
							hidden : true
						}, {
							header : $("vendor.name"),
							dataIndex : 'purchaseOrder.vendorName',
							width : 160,
							hidden : true
						}, {
							header : $("censorQuality.materialId"),
							dataIndex : 'materialId',
							width : 100,
							hidden : true
						}, {
							header : $("materialInfo.code"),
							dataIndex : 'materialCode',
							width : 100
						}, {
							header : $("materialInfo.name"),
							dataIndex : 'materialName',
							width : 160
						}, {
							header : $("unit.unitName"),
							dataIndex : 'unitCode',
							width : 120
						}, {
							header : $("unit.unitName"),
							dataIndex : 'unitName',
							hidden : true
						}, {
							header : $("sendscheduledetail.sendQty1"),
							dataIndex : 'vendorQty',
							width : 100,
							align : "right",
							renderer : function(value) {
								if (Ext.isEmpty(value))
									value = 0;
								return Ext.util.Format.number(value, '0.000')
							}
						}, {
							header : $("porder.vendorTime"),
							dataIndex : 'vendorTime',
							width : 160,
							renderer : function(value) {
								if (!Ext.isEmpty(value)) {
									value = value.replace(/\d{2}:\d{2}:\d{2}$/,
											"");
								}
								return value
							}
						}, {
							header : $("plant.code"),
							dataIndex : 'plantCode',
							width : 160,
							hidden : true,
							renderer : function(v) {
								var plantStore = viewModel
										.getStore('plantStore');
								var index = plantStore.find("plantcode", v);
								if (index > -1) {
									var me = this;
									var name = plantStore.getAt(index)
											.get(	'plantname');
									return name;
								} else {
									return "";
								}
							}
						}, {
							header : $("plant.code"),
							dataIndex : 'plantCode'
						}, {
							header : $("label.storageLocation"),
							dataIndex : 'storeLocal',
							width : 80,
							sortable : false,
							renderer : function(value, m, record) {
								var locationname;
								var store = viewModel
										.getStore('storageLocationStore');

								store.findBy(function(r) {
											if (r.get("locationcode") == value
													&& record.get("plantCode") == r
															.get("plantcode")) {
												locationname = r
														.get("locationname");
												return false;
											}
										});

								return locationname || value;
							}
						},
						// 可送数量 = 订单量-点收量(已接收量)-在途量+退货
						{
							header : $("porder.sendableQty"),
							dataIndex : 'scheduledQty',
							width : 100,
							align : "right",
							renderer : function(value, metaData, record,
									rowIndex, colIndex, store) {
								// 可送数量=订单量-收货量-在途量+退货量
								var canSendNum = Ext.util.Format
										.number(
												(record.get('vendorQty')
														+ record.get('qtyQuit')
														- record
																.get('qtyArrive') - record
														.get('qtyOnline')),
												'0.000');

								// return 24;
								return canSendNum;
							},
							sortable : false
						}, {
							header : $("porderDtl.qtyOnline"),
							dataIndex : 'qtyOnline',
							width : 100,
							align : "right",
							renderer : function(value) {
								if (Ext.isEmpty(value))
									value = 0;
								return Ext.util.Format.number(value, '0.000')
							}
						}, {
							header : $("porder.lineItemType"),
							dataIndex : 'lineItemTypeCode',
							hidden : true
						},
						// 显示 不回传
						{
							header : $("shoppingnoticedetail.receiveQty"),
							dataIndex : 'qtyArrive',
							width : 80,
							align : "right",
							renderer : function(value) {
								if (Ext.isEmpty(value))
									value = 0;
								return Ext.util.Format.number(value, '0.000')
							},
							hidden : true
						}, {
							header : $("shoppingnoticedetail.returnGoodsQty"),
							dataIndex : 'qtyQuit',
							width : 80,
							align : "right",
							renderer : function(value) {
								if (Ext.isEmpty(value))
									value = 0;
								return Ext.util.Format.number(value, '0.000')
							},
							hidden : true
						}, {
							header : $("shoppingnoticedetail.deliveryQty"),
							dataIndex : 'qtySend',
							width : 80,
							align : "right",
							renderer : function(value) {
								if (Ext.isEmpty(value))
									value = 0;
								return Ext.util.Format.number(value, '0.000')
							},
							hidden : true
						}, {
							header : $("label.notTaxPrice"),
							dataIndex : 'buyerPrice',
							hidden : true
						},// 未税价
						{
							header : $("sendschedulecommon.scheduleQty"),
							dataIndex : 'qtyOnline',
							width : 80,
							align : "right",
							renderer : function(value) {

								if (Ext.isEmpty(value))
									value = 0;
								return Ext.util.Format.number(value, '0.000')
							},
							hidden : true
						}, {
							header : $("porder.purchaseOrderId"),
							dataIndex : 'purchaseOrder.purchaseOrderId',
							width : 100,
							hidden : true
						}, {
							header : $("porder.buyer"),
							dataIndex : 'purchaseOrder.buyer',
							hidden : true
						}, {
							header : $("porder.buyerId"),
							dataIndex : 'purchaseOrder.buyerId',
							hidden : true
						}, {
							Qheader : "供应商erp编码",
							header : $("vendor.vendorErpCode"),
							dataIndex : "purchaseOrder.vendorErpCode",
							hidden : true
						}, {
							Qheader : "公司名称",
							header : $("company.name"),
							dataIndex : "purchaseOrder.companyCode",
							hidden : true
						}, {
							header : $("porderDtl.purchaseOrderDetailId = 订单明细id"),
							dataIndex : 'purchaseOrderDetailId',
							hidden : true
						}]
			},
			dockedItems : [bbar, queryBar],
			listeners : {
				"itemdblclick" : function(g, i) {
					if (grid.doSelect(win, singleSelect)) {
						win.hide();
					}
				}
			},
			// private 自定义
			// 选择
			doSelect : function(win, singleSelect) {

				var grid = this, sm = grid.getSelectionModel(), selections = sm
						.getSelection();
				if (selections.length < 1) {
					Q.tips($("message.pleaseSelect"), "E");
					return false;
				}
				var code = "";
				var flag = false
				// 勾选不同采购订单号明细，点击【选择】，弹出提示“只允许选择一个采购订单进行排程！”
				Q.each(selections, function(r, i) {
							if (i == 0) {
								code = r.get('purchaseOrder.purchaseOrderNo');
							} else if (i > 0 && code != r.get('purchaseOrder.purchaseOrderNo')) {
								flag = true;
								return false
							}
						});

				var isMulti = vp.editWin.formPanel.form.findField("model.isMulti").getValue();

				if (flag && "1" != isMulti) {
					Q.tips($("porderDtl.message.warnMsg3"), "E");
					return false;
				} else {
					// 如果排程明细有记录则判断选中的行记录是否是同一个采购订单号
					var gridOutStore = win.gridOut.getStore();
					if (!Ext.isEmpty(gridOutStore)
							&& gridOutStore.getCount() > 0) {
						Q.each(selections, function(r, i) {
							var index = gridOutStore.find('purchaseOrderNo',r.get('purchaseOrder.purchaseOrderNo'));
							if (index < 0) {
								flag = true;
								return false;
							}
						});
					}
				}

				var form = vp.editWin.formPanel.getForm();
				// 排程允许选择多个订单
				var isMulti = form.findField('model.isMulti').getValue();

				if (isMulti != "1" && flag) {
					Q.tips($("porderDtl.message.warnMsg4"), "E");
					return false;
				}

				if (singleSelect) {
					win.fireEvent("select", grid, selections[0]);
				} else {
					win.fireEvent("select", grid, selections);
				}

				Q.tips($("message.selectSuccess"));
				return true;
			}
		});
		return grid;
	}
});