/**
 * 单位编码
 */
Ext.define("Ext.srm.window.UnitCodeSelectWin", {
	extend : "Ext.ux.Window", 
	constructor : function(conf) {
		var conf = conf || {};
		conf.baseParams.filter_EQ_status = 1;//过滤单位启用的计量单位
		var singleSelect = Ext.isEmpty(conf.singleSelect) ? true : conf.singleSelect;
		var gridPanel = this.gridPanel = this.createGrid(conf, singleSelect);
		conf = Ext.apply({
			title : "单位选择",
			layout : "border",
			width : 750,
			height : 400,
			items : [gridPanel],
			renderTo : conf.renderTo,
			constrain : true,
			listeners : {
				"hide" : function() {
					gridPanel.getSelectionModel().clearSelections()
				}
			}
		}, conf);
		this.callParent([conf]);
		if (Ext.isFunction(conf.select)) {
			this.on("select", conf.select);
		}
	}, 
	createGrid : function(conf, singleSelect) {
		var win = this;
		var gridPanel = Ext.create("Ext.ux.grid.GridPanel", {
			border : true,
			sm : {
				singleSelect : singleSelect
			},
			viewConfig : {
				forceFit : false,
				stripeRows : true
			},
			store : {
				url : path_masterdata + "/md/unit/list",
				baseParams : conf.baseParams || {}
			},
			cm : {
				defaultSortable : false,
				columns : [{
							Qheader : "id", 
							dataIndex : "unitId",
							disabled : true
						}, {
							header : "单位编码", 
							dataIndex : "unitCode" 
						}, {
							header : "单位名称", 
							dataIndex : "unitName" 
						}, {
							header : "状态", 
							dataIndex : "status" ,
							renderer : function(v, m, r) {
								if (Ext.isEmpty(v)) {
									return '';
								}
								return v == '1' ? $("dict.yes") : $("dict.no");
							}
						}]
			},
			tbar : [{
						text : $("button.select"),
						iconCls : "icon-save",
						handler : function(_self) {
							var result = gridPanel.doSelect(win, gridPanel);
							if (result) {
								var store = gridPanel.getStore();
						    	_self.ownerCt.find("name", "unitCode")[0].reset();
							    _self.ownerCt.find("name", "unitName")[0].reset(); 
								store.proxy.extraParams={
									"filter_EQ_status": "1"//生效
								};
								store.load({
									params : {
										start : 0,
										limit : 20
									}
								});
								win.hide()
							}
						}
					}, {
						text : $("button.return"),
						iconCls : "icon-return",
						handler : function(_self) {
							var store = gridPanel.getStore();
							_self.ownerCt.find("name", "unitCode")[0].reset();
							_self.ownerCt.find("name", "unitName")[0].reset(); 
							store.proxy.extraParams = {};
							store.removeAll();
							me.hide()
						}
					}, "->", {
						xtype : "label",
						text : "单位编码"
					}, {
						name : "unitCode",
						xtype : "textfield",
						width : 100
					}, {
						xtype : "label",
						text : "单位名称"
					}, {
						name : "unitName",
						xtype : "textfield",
						width : 100
					}, {
						text : $("button.search"),
						iconCls : "icon-search",
						handler : function(_self) {
							var store = gridPanel.getStore();
							store.proxy.extraParams = {
								"filter_EQ_status": "1"//生效
							};
							var unitCode = _self.ownerCt.find("name", "unitCode")[0].getValue(),
								unitName = _self.ownerCt.find("name", "unitName")[0].getValue();
							if(!Ext.isEmpty(unitCode)){
								store.proxy.extraParams.filter_EQ_unitCode = unitCode;
							}
							if(!Ext.isEmpty(unitName)){
								store.proxy.extraParams.filter_LIKE_unitName = unitName;
							}  
							store.load({
								params : {
									start : 0,
									limit : 20
								}
							});
						}
					}],
			listeners : {
				"rowdblclick" : function(g, r, e) {
					if (gridPanel.doSelect(win, singleSelect)) { 
						win.hide()
					}
				}
			},
			doSelect : function(_me, singleSelect) { 
				var grid = this, selModel = grid.getSelectionModel(), records = selModel.getSelection();
				if (records.length < 1) {
					Q.tips($("message.pleaseSelect"), "E");
					return false
				} 
				if (singleSelect) { 
					win.fireEvent("select", gridPanel, records[0])
				} else {
					win.fireEvent("select", gridPanel, records)
				}
				Q.tips($("message.selectSuccess"));
				return true
			}
		});
		return gridPanel;
	}
});