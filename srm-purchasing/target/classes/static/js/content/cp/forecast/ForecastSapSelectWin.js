/**
 * @class {Cp.forecast.ForecastSapSelectWin}
 * @extend {Ext.ux.Window}
 * 采购预测选择弹出框
 * 
 * @param {object} cfg :
 *  @param {Boolean}    cfg.singleSelect 是否单选，缺省为多选
 *  @param {String}     cfg.moduleId 调用弹出框的菜单对应id
 *  @param {Function}   cfg.select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *               单选时参数：(grid, record)
 *               多选时参数：(grid, selections)
 */ 

Ext.define('Cp.forecast.ForecastSapSelectWin', { 
    extend:'Ext.ux.Window',
    constructor: function(cfg) {
		var win = this;
		cfg = cfg || {};

		var formPanel = this.formPanel = this.createFormPanel(cfg);
		var gridPanel = this.gridPanel = this.createGridDetail(cfg);

		var cfg = Ext.apply({
			constrain: true,
			renderTo: cfg.moduleId,
			title: $('button.search'),
			Qtitle: '查询',
			width: 700,
			height: 580,
			resizable: false,
			layout: 'border',
			maximized: true,
			border: false,
			style: 'margin-top: -2px;',
			items: [formPanel, gridPanel],
            renderTo:cfg.renderTo,
			buttons: [{
				text: $('button.return'),
				Qtext: '返回',
				ui: 'gray-btn',
				handler: function() {
					win.close();
				}
			}]
		}, cfg);

		this.callParent([cfg]);

		if (!Ext.isEmpty(cfg.select) && Ext.isFunction(cfg.select)) {
			this.on('select', cfg.select);
		}
    },
    loadData:function(data){
    	var win = this;
    	var form = win.formPanel.form;
    	form.findField('purchasingOrgCode').setValue(data.purchasingOrgCode);
    	form.findField('purchasingOrgName').setValue(data.purchasingOrgName);
    },
	createFormPanel: function(cfg) {

		var winPc = this;
		var formPanel = new Ext.form.FormPanel({
			layout: 'column',
			border: true,
			bodyStyle: 'padding: 15px;border-width: 1px 0px 1px 0px;',
			height: 250,
			region: 'north',
			labelWidth: 180,
			autoScroll: true,
			defaults: {
				layout: 'form',
				columnWidth: .48,
				border: false,
				defaults: {
					xtype: 'textfield',
					anchor: '-20'
				}
			},
			items: [{
				items: [{
					fieldLabel: $('purchasingOrg.code'),
					QfieldLabel: '采购组织编码',
					name: 'purchasingOrgCode',
					hideLabel: true,
					xtype: 'textfield',
					hidden: true,
					readOnly: true
				}, {
					fieldLabel: $('purchasingOrg.title') + "<font color='red'>*</font>",
					QfieldLabel: '采购组织',
					xtype: 'textfield',
					name: 'purchasingOrgName',
					readOnly: true
				}, {
					fieldLabel: $('purchasingGroup.code') + "<font color='red'>*</font>",
					QfieldLabel: '采购组',
					xtype: 'textfield',
					name: 'purchasingGroupCode',
					hiddenName: 'purchasingGroupCode',
					xtype: 'uxcombo',
					triggerAction: 'all',
					resizable: true,
					editable: true,
					clearable: true,
					allowBlank: false,
					valueField: 'purchasingGroupCode',
					displayField: 'purchasingGroupName',
					displayValue: 'purchasingGroupCode',
					innerTpl: true,
					store: cfg.purchasingGroupStore
				}, {
					fieldLabel: $('forecast.forecastMainDate') +"<font color='red'>*</font>",
					QfieldLabel: '采购预测时间',
					xtype: 'datefield',
					format: 'Y-m-d',
					value: new Date(),
					//allowBlank: false,
					editable: true,
					name: 'forecastMainStartDate'
				}, {
					fieldLabel: $('forecastDtl.forecastMainDate')+ "<font color='red'>*</font>",
					QfieldLabel: '采购预测需求时间',
					xtype: 'datefield',
					format: 'Y-m-d',
					value: new Date(),
					editable: true,
					name: 'forecastStartDate'
				}]
			}, {
				items: [{
					fieldLabel: $('forecastDtl.plantCode') + "<font color='red'>*</font>",
					QfieldLabel: '工厂',
					name: 'plantCode',
					hiddenName: 'plantCode',
					xtype: 'textfield',
					//xtype: 'uxcombo',
					triggerAction: 'all',
					editable: true,
					//emptyText:$('message.pleaseSelect'),
					allowBlank: false,
					/*valueField: 'plantCode',
					displayField: 'plantName',
					displayValue: 'plantCode',*/
					innerTpl: true
					//store: cfg.plantStore
				}, {
					fieldLabel: $('materialInfo.code'),
					QfieldLabel: '物料代码',
					xtype: 'textfield',
					name: 'materialCode'
				}, {
					fieldLabel: $('label.to') +"<font color='red'>*</font>",
					xtype: 'datefield',
					format: 'Y-m-d',
					editable: true,
					name: 'forecastMainEndDate'
				}, {
					fieldLabel: $('label.to')+ "<font color='red'>*</font>",
					xtype: 'datefield',
					format: 'Y-m-d',
					editable: true,
					name: 'forecastEndDate'
				}]
			}],
			buttons: [{
				text: $('button.search'),
				ui: 'blue-btn',
				handler: function() {
					var isValid = formPanel.form.isValid();
					if (!isValid) {
						return false;
					}
					var store = winPc.gridPanel.getStore();
					
					var forecastMainStartDate= formPanel.form.findField('forecastMainStartDate').value;
					var	forecastMainEndDate= formPanel.form.findField('forecastMainEndDate').value;
					var	forecastStartDate= formPanel.form.findField('forecastStartDate').value;
					var	forecastEndDate=formPanel.form.findField('forecastEndDate').value;
					store.proxy.extraParams = {
						'purchasingOrgCode': formPanel.form.findField('purchasingOrgCode').value,
						'purchasingGroupCode': formPanel.form.findField('purchasingGroupCode').value,
						'materialCode': formPanel.form.findField('materialCode').value,
						'plantCode': formPanel.form.findField('plantCode').value,
						'forecastMainStartDate': forecastMainStartDate==null?"":Ext.Date.format(forecastMainStartDate,"Y-m-d"),
						'forecastMainEndDate': forecastMainEndDate==null?"":Ext.Date.format(forecastMainEndDate,"Y-m-d"),
						'forecastStartDate': forecastStartDate==null?"":Ext.Date.format(forecastStartDate,"Y-m-d"),
						'forecastEndDate': forecastEndDate==null?"":Ext.Date.format(forecastEndDate,"Y-m-d")
					};
					store.load()
				}
			}]
		});
		return formPanel;
	},
	createGridDetail: function(cfg) {
		var win = this;
		var formPanel = this.formPanel;

		var grid = new Ext.ux.grid.EditorGridPanel({
			region: 'center',
			Qtitle: 'SAP采购申请信息',
			title: $('forecastMain.forecastMainSAP'),
			pageSize: 0,
			sm: true,
			style: {
				'background': 'none',
				'border-right': '0px solid',
				'border-top': '#99BBFF 1px solid',
				'border-left': '0px solid',
				'border-bottom': '#99BBFF 1px solid'
			},
			height: 300,
			viewConfig: {
				forceFit: false,
				autoScroll: true
			},
			cm: {
				defaultSortable: false,
				// defaults:{ menuDisabled:true},
				columns: [{
					Qheader: '采购预测细单ID',
					header: $('forecastDtl.forecastDtlId'),
					dataIndex: 'forecastDtlId',
					disabled: true
				}, {
					Qheader: '采购预测ID',
					header: $('forecastDtl.forecastId'),
					dataIndex: 'forecast_forecastId',
					disabled: true
				}, {
					header: $('forecast.purchasingOrgCode'),
					Qheader: '采购组织代码',
					hidden:true,
					dataIndex: 'purchasingOrgCode'
				}, {
					header: $('forecast.purchasingOrgName'),
					Qheader: '采购组织名称',
					hidden:true,
					dataIndex: 'purchasingOrgName'
				}, {
					header: $('forecastDtl.plantCode'),
					Qheader: '工厂编码',
					dataIndex: 'plantCode'
				}, {
					header: $('forecast.vendorBusinessCode'),
					Qheader: '供应商ERP码',
					dataIndex: 'vendorErpCode'
				}, {
					header: $('forecast.vendorBusinessCode'),
					Qheader: '供应商编码',
					hidden:true,
					dataIndex: 'vendorCode'
				}, {
					header: $('vendor.name'),
					Qheader: '供应商名称',
					dataIndex: 'vendorName'
				}, {
					header: $('materialInfo.code'),
					Qheader: '物料编码',
					dataIndex: 'materialCode'
				}, {
					header: $('materialInfo.name'),
					Qheader: '物料名称',
					dataIndex: 'materialName'
				}, {
					header: $('label.unit'),
					Qheader: '单位',
					dataIndex: 'unitName'
				}, {
					header: $('forecast.deliveryDate'),
					Qheader: '预测需求日期',
					dataIndex: 'forecastMainDate',
					type: 'date',
					dateFormat: 'Y-m-d',
					renderer: function(value) {
						var fn = Ext.util.Format.dateRenderer('Y-m-d');
						if(null != value && '' != value){
							return fn(value);
						}
						
						return '';
					}
				}, {
					header: $('forecast.dqty'),
					Qheader: '预测数量',
					dataIndex: 'forecastNum',
					align: 'right',
					renderer: function(value) {
						if (!Ext.isEmpty(value)) {
							return Ext.util.Format.number(value, '0,000.000');
						}
						return '';
					}
				}]
			},
			store: {
				autoLoad: false,
				url: path_srm + '/cp/forecast/purchasingapplysearch'
			},
			tbar: [{
				text: $('label.select'),
				iconCls: 'icon-save',
				handler: function(_self) {
					var selectids = grid.getSelectionModel().getSelection();

					if (selectids.length <= 0) {
						Q.tips($('forecastMain.message.pleaseChoosePurchaseApplication'), 'E');
						return;
					}

					var store = cfg.grid.getStore();

					if (0 < store.getCount()) {
						store.removeAll();
					}

					Ext.each(selectids, function(r) {
						store.add(r.data);
					});

					cfg.grid.getView().refresh();
					win.close();
				}
			}]
		});
		return grid;
	}
});
