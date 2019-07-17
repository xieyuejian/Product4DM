﻿/**
 * @class {Cp.order.PurchaseOrderController}
 * @extend {Ext.ux.app.ViewController} 采购订单控制层
 */
Ext.define('Cp.order.PurchaseOrderController', {
	extend: 'Ext.srm.app.ViewController',
	alias: 'controller.purchaseOrderController',

	/**
	 * 临时数据缓存，用于保存明细
	 * 
	 * @param {Array} purchaseOrderDetails
	 */
	purchaseOrderDetails: [],

	/**
	 * 临时数据缓存，用于保存价格主数据明细
	 * 
	 * @param {Array} removedMaterialMasterPriceIds
	 */
	removedMaterialMasterPriceIds: [],

	/**
	 * @method gridStoreBeforeLoad 列表加载前事件
	 */
	gridStoreBeforeLoad: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		if (vp.searchFlag && undefined != vp.searchWin) {
			var params = vp.searchWin.formPanel.form.getValues();
			params.initStates = vp.grid.store.proxy.extraParams.initStates;
			params.start = vp.grid.store.proxy.extraParams.start;
			params.limit = vp.grid.store.proxy.extraParams.limit;
			params.sort = vp.grid.store.proxy.extraParams.sort;
			params.dir = vp.grid.store.proxy.extraParams.dir;
			params.methodName = viewModel.get('methodName');
			vp.grid.store.proxy.extraParams = params;
			vp.searchFlag = false;
		}
	},

	/**
	 * @method gridStoreLoad 列表加载事件
	 */
	gridStoreLoad: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (undefined != vp.searchWin) {
			vp.searchWin.formPanel.form.reset();
		}
	},

	/**
	 * @method interactionGridStoreBeforeLoad
	 * @param {Ext.data.store} store 当前加载的store 采购订单交互列表列表加载前事件
	 */
	interactionGridStoreBeforeLoad: function(store) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var filter = {
			billTypeCode: "CGD",
			billId: vp.grid.getSelectionModel().getSelection()[0].get('purchaseOrderId')
		};
		store.proxy.extraParams.filter_EQ_undefined = null;
		Ext.apply(store.proxy.extraParams, filter);
	},


	/**
	 * @method gridOrderStateRenderer 订单状态渲染
	 * @param {Object} value 当前列值
	 * @return {String} 要呈现的HTML字符串
	 */
	gridOrderStateRenderer: function(value) {
		switch (value) {
			case 'NEW':
				return $('state.new');
			case 'RELEASE':
				return $('state.release');
			case 'OPEN':
				return $('porder.purchaseOrderStateOpen');
			case 'CLOSE':
				return $('label.close');
			case 'CANCEL':
				return "<font color='#bbbbbb'>" + $('state.cancel') + "</font>";
			default:
				return '';
		}
	},

	/**
	 * @method gridOrderFlowStateRenderer 订单审核状态渲染
	 * @param {Object} value 当前列值
	 * @return {String} 要呈现的HTML字符串
	 */
	gridOrderFlowStateRenderer: function(value) {
		switch (value) {
			case 'CONFIRM':
				return "<font color='#ff8800'>" + $('state.confirm') + "</font>";
			case 'NOPASS':
				return "<font color='#ee4444'>" + $('state.nopass') + "</font>";
			case 'PASS':
				return $('state.pass');
			default:
				return '---';
		}
	},

	/**
	 * @method gridOrderCheckStateRenderer 订单确认状态渲染
	 * @param {Object} value 当前列值
	 * @return {String} 要呈现的HTML字符串
	 */
	gridOrderCheckStateRenderer: function(value) {
		switch (value) {
			case 'CONFIRM':
				return "<font color='#ff8800'>" + $('porder.purchaseOrderCheckStateCheck') + "</font>";
			case 'ACCEPT':
				return $('porder.purchaseOrderCheckStateAccept');
			case 'HOLD':
				return "<font color='#2d6299'>" + $('porder.purchaseOrderCheckStateHold') + "</font>";
			case 'FIRMHOLD':
				return $('porder.purchaseOrderCheckStateFirmhold');
			case 'REJECT':
				return "<font color='#ee4444'>" + $('button.reject') + "</font>";
			case 'FIRMREJECT':
				return $('porder.purchaseOrderCheckStateFirmreject');
			default:
				return '---';
		}
	},

	/**
	 * @method gridErpSynStateRenderer 订单同步状态渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前记录行
	 * @return {String} 要呈现的HTML字符串
	 */
	gridErpSynStateRenderer: function(value, metaData, record) {
		var msg = record.get('erpReturnMsg');
		metaData.tdAttr = "data-qtip='" + msg + "'";

		switch (value) {
			case 0:
				return "<font color='#bbbbbb'>" + $('erpSyn.nosyn') + "</font>"; // 灰色
			case 1:
				return "<font color='#444444'>" + $('erpSyn.synsuccess') + "</font>"; // 绿色
			case 2:
				return "<font color='#ff8800'>" + $('erpSyn.onsyn') + "</font>"; // 蓝色
			case 3:
				return "<font color='#ee4444'>" + $('erpSyn.synfail') + "</font>"; // 红色
			case -1:
				return "<font color='#444444'>" + $('erpSyn.noneed') + "</font>"; // 红色
			default:
				return '';
		}
	},

	/**
	 * @method gridCreateTypeRenderer 订单创建类型渲染
	 * @param {Object} value 当前列值
	 * @return {String} 要呈现的HTML字符串
	 */
	gridCreateTypeRenderer: function(value) {
		switch (value) {
			case 'FromInput':
				return $('porder.input');
			case 'FromErp':
				return $('porder.erpGenerated');
			case 'FromBatchLimit':
				return $('sourcing.batchLimit');
			default:
				return value;
		}
	},
	/**
	 * @method vpCheckHandler 列表界面撤销审批按钮方法
	 */
	vpCheckHandler: function() {
	
		var me = this;
		var viewModel = me.getViewModel();
		var grid = viewModel.getVp().grid;
		var selected = grid.getSelectionModel().getSelection();

		if (0 == selected.length) {
			Q.tips($('priceInquiry.select.msg'), 'E');
		} else {
			Q.confirm($('porder.comfirmToRevocationCheck'), function(btn) {
				if (btn == 'ok') {
					Ext.Ajax.request({
						url: path_srm + '/cp/purchaseorder/revocationcheck',
						success: function(response) {
							var json = Ext.decode(response.responseText);
							if (false === json.success) {
								Q.tips(json.msg || $('message.submit.failure'), 'E');
								return;
							} else if (true === json.success) {
								Q.tips($('porder.revocationCheckSuccess'));
							}
							grid.getStore().reload();
						},
						failure: function() {
							grid.getStore().reload();
						},
						method: 'POST',
						params: {
							id: selected[0].get('purchaseOrderId')
						}
					});
				}
			});
		}
	},

	/**
	 * @method vpSynErpHandler 列表界面同步erp按钮方法
	 */
	vpSynErpHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var grid = viewModel.getVp().grid;
		var selected = grid.getSelectionModel().getSelection();
		var dealUrl = viewModel.get('dealUrl');

		if (0 == selected.length) {
			Q.tips($('priceInquiry.select.msg'), 'E');
		} else {
			Q.confirm($('message.sync.confirm'), function(btn) {
				if (btn == 'ok') {
					Ext.Ajax.request({
						url: dealUrl + '/syncerp',
						timeout: 120000,
						params: {
							id: selected[0].get('purchaseOrderId')
						},
						success: function(response) {
							var json = Ext.decode(response.responseText);
							if (false === json.success) {
								Q.tips($('message.submit.failure'), 'E');
								return;
							}

							Q.tips($('porder.sycnStarted'));
							grid.getStore().reload();
						},
						failure: function() {
							grid.getStore().reload();
						}
					});
				}
			});
		}
	},

	/**
	 * @method vpExportHandler 列表界面导出按钮方法
	 */
	vpExportHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var grid = viewModel.getVp().grid;
		var selection = grid.getSelectionModel().getSelection();

		if (0 == selection.length) {
			Q.tips($('porder.selectToExport'), 'E');
			return false;
		} else {
			var purchaseOrderId = selection[0].get('purchaseOrderId');
			Ext.UxFile.fileDown(this.getViewModel().get('dealUrl')+"/export?jasperFile=PurchaseOrder&isDownLoad=true&reportFileType=pdf&filter_purchaseOrderId=" + purchaseOrderId,"订单.pdf",null);
		}
	},

	/**
	 * @method vpDownloadHandler 列表界面下载按钮方法
	 */
	vpDownloadHandler: function() {
		Ext.UxFile.fileDown(this.getViewModel().get('dealUrl')+"/download?templateFile=PurchaseOrders.xls","订单导入模板.xls",null);
	},
	

	/**
	 * @method vpImportHandler 列表界面下载按钮方法
	 */
	vpImportHandler: function(_self) {
		try{
            var me = this; 
            var viewModel = me.getViewModel();
    		var vp = viewModel.getVp();
            var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid 
            var opt = {
                url: viewModel.get("dealUrl"), 
                method: "/batchimport",
                importSuccess:function(op, op1){//导入成功后回调
                	vp.grid.getStore().reload();
                }
            };//配置项
            var renderTo = grid.id;//渲染载体
            //文件工具类调用
            Ext.UxFile.fileImport(opt, renderTo); 
        }catch(e){
            console.log(e);
	    } 
	},

	/**
	 * @method dealState
	 * @param {Object} self 当前点击对象 状态处理方法
	 */
	dealState: function(self) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var name = self.name;
		var text = self.text;
		var id = null;

		if (!vp.editWin.hidden) {
			id = vp.editWin.formPanel.form.findField('model.purchaseOrderId').getValue();
		}

		if ('TOPASS' == name) {
			if (vp.editWin.hidden) {
			 vp.flowApprove(vp,text,name);
			} else {
			  vp.flowApprove(vp,text,name,vp.editWin);
			}
		} else if ('TONOPASS' == name || 'TOREJECT' == name) {
			if (vp.editWin.hidden) {
			 vp.flowApprove(vp,text,name);
			} else {
				    vp.flowApprove(vp,text,name,vp.editWin);
			}
		} else if ('TOFIRMHOLD' == name || 'TOFIRMREJECT' == name || 'TOOPEN' == name || 'TOCLOSE' == name || 'TOCANCEL' == name) {
		if (vp.editWin.hidden) {
				vp.dealstate(name, "[" + text + "]", true, true, vp.id, false);
			} else {
				vp.dealstate(name, "[" + text + "]", true, true, vp.editWin.id, true, id);
			}
		} else if ('TOACCEPT' == name || 'TORELEASE' == name) { // TOACCEPT接受订单
				// TORELEASE发布订单
		if (vp.editWin.hidden) {
				vp.dealstate(name, "[" + text + "]", false, true, vp.id, false);
			} else {
				vp.dealstate(name, "[" + text + "]", false, true, vp.editWin.id, true, id);
			}
		}
	},

	/**
	 * @method getInteractionGrid 获取交互组件
	 * @return {Ext.grid.Panel} detailGrid
	 */
	getInteractionGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.getCompByTabClassName('interactionGrids');
	},

	/**
	 * @method getDetailGrid 获取编辑界面订单明细列表方法
	 * @return {Ext.grid.Panel} detailGrid
	 */
	getDetailGrid: function() {
		var vp = this.getViewModel().getVp();

		if (true == vp.editWin.hidden) {
			return vp.getCompByTabClassName('purchaseOrderDetails')
		}

		return vp.editWin.getCompByTabClassName('purchaseOrderDetails');
	},

	/**
	 * @method getPricingGrid 获取编辑界面订单明细的定价列表方法
	 * @return {Ext.grid.Panel} pricingGrid
	 */
	getPricingGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('purchaseOrderPricings');
	},

	/**
	 * @method getQuantityPanel 获取编辑界面订单明细的双单位表单方法
	 * @return {Ext.grid.Panel} quantityPanel
	 */
	getQuantityPanel: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('purchaseDualUnitConversions');
	},

	/**
	 * @method toHold 加载变更订单界面方法
	 */
	toHold: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var record = vp.grid.getSelectionModel().getSelection()[0];
		var recordId = viewModel.get("recordId");
		if (undefined == record) {
			record = Ext.create('Ext.data.Model', {
				'purchaseOrderId': recordId
			});

			record.store = vp.grid.store;
		}

		var win = vp.editWin;
		win.resetWin()
		win.configVar.editFlag = true; // 编辑
		win.holdFlag = true; // 变更
		win.on('submit', function() {
			// 启用文件上传按钮
			win.holdFlag = false; // 清除变更
			win.hide();
		});
		
		win.setFormValue(record, 'edit');

		vp.editWin.url = viewModel.get('dealUrl') + '/dealhold';
		// 只允许变更交货日期和价格
		var form = win.formPanel.form;
		var fields = win.formPanel.items.items;
		for (var i = 0; i < fields.length; i++) {
			if (!Ext.isEmpty(fields[i].setReadOnly) && Ext.isFunction(fields[i].setReadOnly)) {
				fields[i].setReadOnly(true);
			}
		}
		
		//设置细单按钮不能编辑
		win.setDisabledChildTopToolbar(false);

		// 保存提交按钮，变为确认变更按钮
		var btns = win.formPanel.getTopToolbar().items.items;
		for (var i = 0; i < btns.length; i++) {
			if (btns[i].text == $('button.save')) {
				btns[i].hide();
			} else if (btns[i].text == $('button.submit')) {
				btns[i].setText($('porder.purchaseOrderCheckStateFirmhold'));
			}
		}

		win.show();
	},

	/**
	 * @method vpRowdblclick 列表双击事件
	 * @return {Boolean} 默认返回false
	 */
	vpRowdblclick: function() {
		return false;
	},

	/**
	 * @method setFormValueAfter
	 * @param {Ext.form.Panel} formPanel 主单表单对象 设置表单后触发的方法
	 */
	setFormValueAfter: function(formPanel) {
		console.log("setFormValueAfter");
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var tbar = vp.editWin.formPanel.getTopToolbar();
		var id = vp.editWin.formPanel.form.findField('model.purchaseOrderId').getValue();

		if (null != id && "" != id) {
			var items = vp.editWin.formPanel.items.items;
			Ext.each(items, function(item) {
				if (!Ext.isEmpty(item.setReadOnly) && Ext.isFunction(item.setReadOnly)) {
					item.setReadOnly(true);
				}
			});
		}
        console.log(vp.editWin.editFlag);
		// 设置操作按钮显隐
		if (!vp.editWin.editFlag) {
			me.setEditBtnShowHide(id, tbar);
		}

		// 设置上传附件按钮
		var uploadBtn = vp.editWin.formPanel.query('button[name=upload]')[0];
		var btnType = vp.editWin.btnType;
		var holdFlag = vp.editWin.holdFlag;

		if (('add' == btnType || 'edit' == btnType) && (Ext.isEmpty(holdFlag) || holdFlag == false)) {
			uploadBtn.setDisabled(false);
		} else {
			uploadBtn.setDisabled(true);
		}

	},

	/**
	 * @method setEditBtnShowHide 设置编辑按钮显隐
	 * @param {Integer} id 单据id
	 * @param {Ext.Toolbar} tbar 编辑界面顶部按钮对象
	 */
	setEditBtnShowHide: function(id, tbar) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var purchaseOrderFlowState=form.findField('model.purchaseOrderFlowState').getValue();
	    Ext.each(tbar.items.items, function(btn) {
			if ('return' != btn.name && 'expand' != btn.name && 'collapse' != btn.name && !btn.hidden) {
				btn.hide();
			}
		});
      
		// 根据id判断可变状态
		Ext.Ajax.request({
			url: viewModel.get('dealUrl') + '/getevents',
			method: 'post',
			params: {
				id: id,
				btnStateFlag: '1'
			},
			async: false,
			success: function(result) {
				var json = Ext.decode(result.responseText);
				if (Ext.isEmpty(json)) {
					viewModel.editWin.addOtherBtn.forEach(function(btn) {
						tbar.query('button[name=' + btn.name + ']')[0].hide();
					});
					return;
				}
			       viewModel.editWin.addOtherBtn.forEach(function(btn) {
				  btn = tbar.query('button[name=' + btn.name + ']')[0];
					if (typeof(btn) != "undefined") {
						if ('return' != btn.name) {
							btn.hide();
							Ext.each(json, function(r, j) {
								if (-1 < btn.name.indexOf(r)) {
									btn.show();
									return false;
								}
							});
					    }
					    
					}
					
				});
				Ext.each(json, function(r, j) {
					var button = tbar.query("button[name='"+r+"']")[0];
					if(button){
						button.show();
					}
				});
			}
		});
		if(purchaseOrderFlowState=="CONFIRM"&&-1 == s_roleTypes.indexOf('V')){
		    tbar.query('button[name=grant]')[0].show();
	    }

	},

	/**
	 * @method vpInstanceAfert 窗体实例化之后
	 */
	vpInstanceAfter: function() {
		try{
			var me = this;
			var viewModel = me.getViewModel();
			var vp = viewModel.getVp();
			var form = vp.editWin.formPanel.getForm();
			var grant=vp.grid.getTopToolbar().find("name","grant")[0];
			var check=vp.grid.getTopToolbar().find("name","check")[0];
			var prompttrial=vp.grid.getTopToolbar().find("name","prompttrial")[0];
			if(grant!=null){
				grant.setDisabled(true);
		    }
		    if(check!=null){
		        check.setDisabled(true);
		    }
		    if(prompttrial!=null){
		       prompttrial.setDisabled(true);
		    }
			var gridStore=vp.grid.store;
		    gridStore.on("load",function(){
		    if(grant!=null){
				grant.setDisabled(true);
		    }
		    if(check!=null){
		        check.setDisabled(true);
		    }
		    if(prompttrial!=null){
		       prompttrial.setDisabled(true);
		    }
		    });
			var grid = this.getInteractionGrid();
		    grid.on('select', function() {
				var record = grid.getSelection()[0];
				var deleteBtn = grid.getTopToolbar().find('name', 'delete')[0];
				deleteBtn.setDisabled(true);
				if (record.get('createUserId') == s_userid) {
					deleteBtn.setDisabled(false);
				}
			});
			grid.getStore().on("beforeload",function(s){
				me.interactionGridStoreBeforeLoad(s);
			});
	
			me.vpInit();
		}
		catch(e){
			console.log(e);
		}
	},

	/**
	 * @method countPrice 依次调用计算数量，计算定价条件金额，计算订单明细金额方法
	 */
	countPrice: function() {
		var me = this;
		me.countQuantity(); // 根据订单单位和定价单位、SKU的转换关系，计算定价单位数量和SKU数量
		me.countPricingAmount();
		me.countItemAmount();
		me.countTotalAmout();
	},

	/**
	 * @method countQuantity 根据订单单位和定价单位、SKU的转换关系，计算定价单位数量和SKU数量
	 */
	countQuantity: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		// 获取选中的订单明细
		var detailGrid = me.getDetailGrid();
		var selecteds = detailGrid.getSelectionModel().getSelection();
		if (selecteds.length != 1) {
			return;
		}
		var record = selecteds[0];
		// 获取转换关系
		var quantityPanel = me.getQuantityPanel();
		var quantityForm = quantityPanel.getForm();
		var formValues = quantityForm.getValues();
		// 手动触发存入缓存的方法
		var fields = quantityPanel.items.items;
		Ext.each(fields, function(field) {
			if ('fieldset' != field.xtype) {
				if (field.getName() == 'purchaseOrderQtyId') {
					return true;
				}
				field.fireEvent('change', field, field.getValue(), '');
			}
		});

		var r = selecteds[0];
		var qty = r.get('buyerQty');
		// 订单单位
		var convertMolecular2 = formValues['convertMolecular2'];
		// 定价单位
		var convertDenominator2 = formValues['convertDenominator2'];
		// 基本单位
		var convertDenominator = formValues['convertDenominator'];
		// 订单单位
		var convertMolecular = formValues['convertMolecular'];
		
		//订单定价数量 = 订单数量*定价单位/订单单位（qty*convertDenominator2/convertMolecular2）
		var pricingQty = convertDenominator2 / convertMolecular2 * qty;
		// SKU数量 = 订单数量 * 基本单位/订单单位（qty*convertDenominator/convertMolecular）
		var skuQty = convertDenominator / convertMolecular * qty;
		quantityForm.findField('pricingQty').setValue(pricingQty);
		quantityForm.findField('skuQty').setValue(skuQty);
		
		// 更新细单sourceField
		var json = record.get("unitConversionInfo");
		var data = JSON.parse(json);
		data[0].pricingQty = pricingQty;
		data[0].skuQty = skuQty;
		record.set("unitConversionInfo", JSON.stringify(data));

		// 手动触发存入缓存的方法
		Ext.each(fields, function(field) {
			if ('fieldset' != field.xtype) {
				if (field.getName() == 'purchaseOrderQtyId') {
					return true;
				}
				field.fireEvent('change', field, field.getValue(), '');
			}
		});
		// 写入订单备注
		quantityForm.findField('comment').setValue('<font color="red">' + $('porderDtl.message.warnMsg2') + '</font>');
	},

	/**
	 * @method countPricingAmount 计算定价条件金额
	 */
	countPricingAmount: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var pricingGrid = me.getPricingGrid();
		var detailGrid = me.getDetailGrid();

		var selecteds = detailGrid.getSelectionModel().getSelection();

		if (0 == pricingGrid.store.getCount() || 1 != selecteds.length) {
			return;
		}

		var selected = selecteds[0];
		var pricing = pricingGrid.getStore().getAt(0);
		var recordType = selected.get('lineItemTypeCode');
		// 数量标签
		var quantityForm = vp.editWin.query('form[name=quantityPanel]')[0].getForm();
		// 获取定价单位数量
		var pricingQty = quantityForm.findField('pricingQty').getValue();
		var amount = 0;

		if (2 == recordType) {
			pricing.set('pricingQty', Ext.util.Format.number(0, '0.00'));
		}
		// 获取当前行定价
		var pricingPrice = pricing.get('pricingQty');
		// 获取当前行价格单位
		var priceUnit = pricing.get('priceUnit');

		if (!Ext.isEmpty(pricingQty) && !Ext.isEmpty(pricingPrice) && !Ext.isEmpty(priceUnit)) {
			amount = pricingQty * pricingPrice / priceUnit;
		}

		pricing.set('amount', amount);
		
		// 更新细单sourceField
		var json = selected.get("pricingInfo");
		var data = JSON.parse(json);
		data[0].priceUnit = pricing.get('priceUnit');
		data[0].pricingQty = pricing.get('pricingQty');
		data[0].amount = pricing.get('amount');
		selected.set("pricingInfo", JSON.stringify(data));
		pricingGrid.getView().refresh();
	},

	/**
	 * @method countItemAmount 计算行金额和价格
	 */
	countItemAmount: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var detailGrid = me.getDetailGrid();
		var pricingGrid = me.getPricingGrid();
		var selecteds = detailGrid.getSelectionModel().getSelection();
		if (selecteds.length != 1) {
			return;
		}

		var selected = selecteds[0];
		var quantityForm = vp.editWin.query('form[name=quantityPanel]')[0].getForm();
		var pricingAmount = 0;
		var priceUnit = 0;
		var isFree = 0;

		// 计算行金额
		if (0 < pricingGrid.store.getCount()) {
			var pricing = pricingGrid.getStore().getAt(0);
			pricingAmount = pricing.get('amount');
			priceUnit = pricing.get('priceUnit');
		}

		selected.set('lineItemValAmt', Ext.util.Format.number(pricingAmount, '0.00'));
		// 计算价格
		var buyerPrice = pricingAmount / quantityForm.findField('pricingQty').getValue() * priceUnit;
		selected.set('buyerPrice', Ext.util.Format.number(buyerPrice, '0.00'));

	},

	/**
	 * @method countTotalAmout 计算总金额
	 */
	countTotalAmout: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var totalAmount = 0;
		var form = vp.editWin.formPanel.getForm()
		var store = me.getDetailGrid().getStore();

		store.each(function(r) {
			var deleteFlag = r.get('deleteFlag');
			var lineItemValAmt = r.get('lineItemValAmt');
			if (deleteFlag == 0 && !Ext.isEmpty(lineItemValAmt)) {
				totalAmount += parseFloat(r.get('lineItemValAmt'));
			}
		});

		form.findField('model.totalAmount').setValue(Ext.util.Format.number(totalAmount, '0.0000'));
	},

	/**
	 * @method setMaterialLadderPrice
	 * @param {Stirng} plantCode 工厂编码 加载价格主数据
	 */
	setMaterialLadderPrice: function(plantCode) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var mainData = vp.editWin.formPanel.getForm().getValues(); // 获取主单数据
		var record = me.getDetailGrid().getSelectionModel().getSelection()[0]; // 获取选中的细单

		if (Ext.isEmpty(record.get('materialCode')) || Ext.isEmpty(record.get('buyerQty')) || '' == plantCode) {
			return false;
		}

		var searchParams = {
			filter_EQ_materialMasterPrice_plantCode: plantCode,
			filter_EQ_materialMasterPrice_materialCode: record.get('materialCode'),
			filter_EQ_materialMasterPrice_vendorErpCode: mainData['model.vendorErpCode'],
			filter_EQ_materialMasterPrice_purchasingOrgCode: mainData['model.purchasingOrgCode'],
			filter_EQ_materialMasterPrice_recordType: record.get('lineItemTypeCode'),
			filter_LE_materialLadderPriceDtls_startNum: record.get('buyerQty')
		}

		var lineItemTypeCode = record.get('lineItemTypeCode');

		if ('2' != lineItemTypeCode) {
			Ext.Ajax.request({
				url: viewModel.get('dealUrl') + '/findmaterialladderprice',
				method: 'post',
				async: false,
				params: searchParams,
				success: function(result) {
					var pricingGrid = me.getPricingGrid();
					var record = pricingGrid.store.getAt(0);
					if ('' != result.responseText && '-1' != result.responseText) {
						record.set('pricingQty', result.responseText);
					}else{// 当没有匹配到价格主数据时，设置定价为空
						record.set('pricingQty', null);
					}
					// 计算数量、定价、价格
					me.countPrice();
				},
				failure: function() {}
			});
		}
	},

	/**
	 * @method companyStore 公司store加载事件
	 * @param {Ext.data.Store} store 当前加载后的store对象
	 */
	companyStoreLoad: function(store) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (store.getCount() == 1 && !Ext.isEmpty(vp)) {
			var form = vp.editWin.formPanel.getForm();
			var field = form.findField('model.companyCode');
			field.setValue(store.getAt(0).get('companyCode'));
			field.fireEvent('select', field, store.getAt(0));
		}
	},

	/**
	 * @method plantStoreLoad 工厂store加载事件
	 * @param {Ext.data.Store} store 当前加载后的store对象
	 * @param {Ext.data.Model} record 当前行记录
	 */
	plantStoreLoad: function(store, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		if (store.getCount() == 1 && !Ext.isEmpty(vp)) {
			var record = me.getDetailGrid().getSelectionModel().getSelection()[0];
			var r = store.getAt(0);
			record.set('plantCode', r.get('plantCode'));
			record.set('plantName', r.get('plantName'));
			me.setMaterialPlantInfo(record.get('materialCode'),record.get('plantCode'),record);
			me.setMaterialLadderPrice(r.get('plantCode'));
		}
	},

	/**
	 * @method storLocStoreLoad 库存地点store加载事件
	 * @param {Ext.data.Store} store 当前加载后的store对象
	 */
	storLocStoreLoad: function(store) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if(!Ext.isEmpty(store)){
			if (store.getCount() == 1 && !Ext.isEmpty(vp)) {
				var record = me.getDetailGrid().getSelectionModel().getSelection()[0];
				var r = store.getAt(0);
				record.set('storeLocal', r.get('stockLocationCode'));
			}
		}
	},

	/**
	 * @method addRowSelectEvent 添加额外的行选择事件，处理强制状态变更中可变更的状态
	 */
	addRowSelectEvent: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var toolBar =  vp.grid.getTopToolbar();
         var menu=null;
        toolBar.items.each(function(i,n){
        
	     if(i.name=='changeState'){
                  menu = i.menu;
            }
        });
        if(menu!=null){
		vp.grid.getSelectionModel().on('selectionchange', function(model, ri, r) {
			// 选中单行时才可操作
			var selections = this.getSelection();
			if (selections.length != 1) {
				return;
			}
			var selected = selections[0];
			// 先将菜单项设为隐藏
			var items = menu.items;
			items.each(function(item) {
				item.hide();
			});
			
			// 根据id判断可变状态
			Ext.Ajax.request({
				url: viewModel.get('dealUrl') + '/getevents',
				method: 'post',
				params: {
					id: selected.data.purchaseOrderId,
					btnStateFlag: '1'
				},
				success: function(result) {
					var json = Ext.decode(result.responseText);
					if (Ext.isEmpty(json)) {
						menu.items.each(function(i){
							if(i.name == 'unstate'){
							 	i.show();
							}
						});
						return;
					}
					
					var hasItem = false;
					Ext.each(json, function(r, j) {
						menu.items.each(function(i){
							if(i.name == r){
								hasItem = true;
							 	i.show();
							}
						});
					});
					
					if (!hasItem) {
						menu.items.each(function(i){
							if(i.name == 'unstate'){
							 	i.show();
							}
						});
					}
				}
			});

		});
        }
	},

	/**
	 * @method formCollapseExpand 主单收缩功能
	 * @param {Ext.form.Panel} formPanel主单对象
	 */
	formCollapseExpand: function(formPanel) {
		// 主单增加收缩功能
		var tbar = formPanel.getTopToolbar();
		var tbarItems = tbar.items.items;
		if (tbarItems[tbarItems.length - 1].name != 'expand') {
			tbar.add('->');
			tbar.add({
				name: 'collapse',
				width: 75,
				hidden: true,
				buttonAlign: 'right',
				iconCls: 'icon_srm_show1',
				text: $('porder.shrink'),
				handler: function() {
					formPanel.setHeight(40);
					tbar.query('button[name=expand]')[0].show();
					this.hide();
				}
			});
			tbar.add({
				name: 'expand',
				width: 75,
				hidden: true,
				iconCls: 'icon_srm_show2',
				buttonAlign: 'right',
				text: $('porder.expand'),
				handler: function() {
					formPanel.body.dom.style.display = 'block';
					formPanel.setHeight(150);
					tbar.query('button[name=collapse]')[0].show();
					this.hide();
				}
			});
		}

		tbar.query('button[name=collapse]')[0].show();
	},

	/**
	 * @method editWinShow 编辑窗口初始化方法
	 */
	editWinShow: function() {
    	var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var formPanel = vp.editWin.formPanel;
		var grid = vp.grid;

		// 主单收缩功能
		me.formCollapseExpand(formPanel);

		me.removedMaterialMasterPriceIds = [];

		// 审核不过的编辑操作
		if ('edit' == vp.editWin.btnType) {
			var record = grid.getSelectionModel().getSelection()[0];
			var purchaseOrderFlowState = record.get('purchaseOrderFlowState');
			var tbar = formPanel.getTopToolbar();
			if ('NOPASS' == purchaseOrderFlowState) {
				tbar.query('button[name=save]')[0].hide();
			}
		}

		// 恢复部分只读字段为可编辑
		var form = formPanel.getForm();
		if (!vp.editWin.editFlag) {
			form.findField('model.vendorErpCode').setReadOnly(false);
			form.findField('model.purchasingOrgCode').setReadOnly(false);
		}

		// 初始化缓存
		me.purchaseOrderDetails = [];
		
		// 添加额外的行选择事件
		me.addRowSelectEvent();
	},
	

	/**
	 * @method vpInit 主界面初始化方法
	 */
	vpInit: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		// 初始化行选中事件，主要处理状态变更菜单项的显示和隐藏
		me.addRowSelectEvent();
		// 隐藏待审核单据过滤按钮
		
	},

	/**
	 * @method getBy 根据input字段值value，获取store中output字段值
	 * @param {Object} store 数据源
	 * @param {String} output 输出的字段名称对应的值
	 * @param {String} input 根据该字符名称的值进行对比
	 * @param {String} value 用于对比的值
	 * @return {String} 返回对应字段的值
	 * @private
	 */
	getBy: function(store, output, input, value) {
		for (var i = 0; i < store.getCount(); i++) {
			var r = store.getAt(i);
			if (r.data[input] == value) {
				return r.data[output];
			}
		}
	},

	/**
	 * @method findCurrencyRate 根据货币编码查找汇率
	 * @param {String} vendorCode 供应商编码
	 * @param {String} PurchasingOrgCode 采购组织
	 */
	findCurrencyRate: function(vendorCode, purchasingOrgCode, origCurrencyCode) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		
		if('' ==  vendorCode || '' == purchasingOrgCode){
			return false;
		}
		/*if('RMB' == origCurrencyCode){
			form.findField('model.currencyRate').setValue(1);
			return;
		}*/
		Ext.Ajax.request({
			url: path_srm + '/cp/purchaseorder/findexchangerate',
			method: 'post',
			async: false,
			params: {
				vendorCode: vendorCode,
				purchasingOrgCode: purchasingOrgCode,
				origCurrencyCode: origCurrencyCode
			},
			success: function(result) {
				var json = Ext.decode(result.responseText);

				if (true == json.success) {
					form.findField('model.currencyRate').setValue(json.data.split('-')[2]);
					form.findField('model.currencyCode').setValue(json.data.split('-')[0]);
				} else {
					form.findField('model.currencyRate').reset();
					form.findField('model.currencyCode').reset();
					Q.tips(json.msg, 'E');
				}
			},
			failure: function() {}
		});
	},

	/**
	 * @method quantityUnitInfo 设置双单位转换相关信息
	 */
	quantityUnitInfo: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var grid = me.getDetailGrid();
		var selected = grid.getSelectionModel().getSelection();
		if (selected.length != 1) {
			return;
		}
		selected = selected[0];
		var unitCode = selected.get('unitCode');
		// 双单位转换panel
		var quantityPanel = me.getQuantityPanel();
		// 双单位转换form
		var quantityForm = quantityPanel.getForm();
		quantityPanel.query('textfield[name=orderDetailUnit]')[0].setValue(unitCode);
		quantityPanel.query('textfield[name=pricingUnit]')[0].setValue(unitCode);
		quantityPanel.query('textfield[name=unitCode]')[0].setValue(unitCode);
		quantityPanel.query('textfield[name=orderDetailUnit2]')[0].setValue(unitCode);
		quantityForm.findField('convertMolecular2').setValue(1);
		quantityForm.findField('convertMolecular').setValue(1);
		quantityForm.findField('convertDenominator2').setValue(1);
		quantityForm.findField('convertDenominator').setValue(1);
		selected.set('unitName', me.getBy(viewModel.getStore('unitStore'), 'unitName', 'unitCode', unitCode));
		// 计算数量、定价、价格
		me.countPrice();
	},

	/**
	 * @method closeOrderDetail 关闭订单明细
	 * @param {Ext.Toolbar} btn 所点击按钮对象
	 * @param {Integer} detailId 明细Id
	 * @param {Integer} closeFlag 关闭标识
	 */
	closeOrderDetail: function(btn, detailId, closeFlag) {
		var me = this;
		var detailGrid = me.getDetailGrid();
		var index = detailGrid.getStore().find('purchaseOrderDetailId', detailId);
		var record = detailGrid.getStore().getAt(index);
		var vm = me.getView();
		var operate = '关闭';
		if(closeFlag == 0){
			operate = '取消关闭';
		}

		var msg = closeFlag == 1 ? $('purchaseOrder.thisPurchaseOrderDetail') : $('purchaseOrder.thisUNPurchaseOrderDetail');

		var okFn = function() {
			vm.mask($('message.submit.wait'));
			Ext.Ajax.request({
				url: path_srm + '/cp/purchaseorderdetail/close',
				params: {
					id: detailId,
					closeFlag: closeFlag,
					operate:operate
				},
				success: function(response) {
					vm.unmask();
					var json = Ext.decode(response.responseText);
					if (false === json.success) {
						Q.tips(json.msg || $('message.operator.failure') + "<br/><br/>" + $('message.system.error'), 'E');
						return;
					}
					me.getDetailGrid().getStore().reload();
					Q.tips($('message.operator.success'));
				},
				failure: function(response) {
					Q.tips($('message.operator.failure'), 'E');
					vm.unmask();
				},
				callback: function() {
					vm.unmask();
				}
			});
		}

		// 设置窗体在最前面
		// Q.msgbox.alwaysOnTop = true;
		Q.confirm(msg, {
			renderTo: vm.id,
			ok: okFn
		});
	},

	/**
	 * @method formPurchasingOrgCodeSelect 表单采购组织选中事件
	 * @param {Ext.form.field.ComboBox} combo 当前对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formPurchasingOrgCodeSelect: function(field,value,selected) {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.purchasingOrgName').setValue(selected.get('purchasingOrgName'));
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var companyStore = viewModel.getStore('companyStore');

		var index = viewModel.getStore('purchasingOrgStore').find('purchasingOrgCode', value);
		companyStore.removeAll();
        form.findField('model.companyCode').reset("");
		if (-1 == index) {
			combo.fireEvent('clear');
		} else {
			form.findField('model.companyCode').fireEvent('clear');
			form.findField('model.vendorErpCode').fireEvent('clear');
			companyStore.proxy.extraParams.filter_EQ_purchasingOrgCode = value;
			companyStore.load({
				callback:function(records){
					form.findField('model.companyCode').reset("");
					form.findField('model.companyName').reset("")
					if(companyStore.totalCount ==1 ){
						form.findField('model.companyCode').setValue(companyStore.data.items[0].get('companyCode'));
						form.findField('model.companyName').setValue(companyStore.data.items[0].get('companyName'));
					}
				}
			});
		}
	},

	/**
	 * @method formPurchasingOrgCodeChange 表单采购组织改变事件
	 * @param {Object} combo 当前对象
	 * @param {String} value 改变后的值
	 * @param {String} oldValue 改变之前的值
	 */
	formPurchasingOrgCodeChange: function(combo, value, oldValue) {
		if (combo.readOnly) { // 如果是只读，则不让下拉，直接返回
			return;
		}
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var companyStore = viewModel.getStore('companyStore');
		form.findField('model.companyCode').reset();
		form.findField('model.companyName').reset();
		var index = viewModel.getStore('purchasingOrgStore').find('purchasingOrgCode', value);
		companyStore.removeAll();
		form.findField('model.companyCode').fireEvent('clear');
		form.findField('model.vendorErpCode').fireEvent('clear');
		if (-1 == index) {
			combo.fireEvent('clear');
		} else {
			
			companyStore.proxy.extraParams.filter_EQ_purchasingOrgCode = value;
			companyStore.load({
				callback:function(records){
					form.findField('model.companyCode').reset("");
					form.findField('model.companyName').reset("")
					if(companyStore.totalCount ==1 ){
						form.findField('model.companyCode').setValue(companyStore.data.items[0].get('companyCode'));
						form.findField('model.companyName').setValue(companyStore.data.items[0].get('companyName'));
					}
				}
			});
		}
	},

	/**
	 * @method formPurchasingOrgCodeClear 表单采购组织清空
	 */
	formPurchasingOrgCodeClear: function() {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();

		viewModel.getStore('companyStore').removeAll();
		form.findField('model.companyCode').fireEvent('clear');
		form.findField('model.vendorErpCode').fireEvent('clear');
		form.findField('model.purchasingOrgCode').setValue('');
		form.findField('model.purchasingOrgName').setValue('');
	},

	/**
	 * @method formPurchasingGroupCodeSelect 表单采购组选中事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formPurchasingGroupCodeSelect: function(field,value,selected) {
		var vp = this.getViewModel().getVp();
    	var form = vp.editWin.formPanel.getForm();
    	form.findField('model.purchasingGroupName').setValue(selected.get('purchasingGroupName'));
    
	},
	
	 /**
	  * @method formCompanyCodeChange 表单公司改变事件
	 */
	 	formCompanyCodeChange:function(){
			var vp=this.getViewModel().getVp();
			var form=vp.editWin.formPanel.getForm();
			var  CompanyCode=form.findField("model.companyCode").getValue();
			var  CompanyStore = this.getViewModel().getStore('companyStore');
			CompanyStore.clearFilter();
		    if(null!=CompanyCode){
		    	CompanyStore.proxy.extraParams.filter_LIKE_companyCode=CompanyCode;
			}
		    CompanyStore.load();
		 	},
 
	/**
	 * @method formCompanyCodeSelect 表单公司选中事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formCompanyCodeSelect: function(combo, record) {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.companyName').setValue(record.get('companyName'));},

	/**
	 * @method formCompanyCodeClear 表单公司清空
	 */
	formCompanyCodeClear: function() {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.companyName').reset();
	},

	/**
	 * @method formCompanyCodeFocus 表单公司获取焦点
	 */
	formCompanyCodeBeforeexpand: function() {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
		if (null == purchasingOrgCode || Ext.isEmpty(purchasingOrgCode)) {
			Q.tips($('message.pleaseSelectPurchasingOrgCode'), 'E');
			return false;
		}
	},
	
	/**
	 * @method formVendorErpCodeClear 表单供应商清空
	 */
	formVendorErpCodeClear: function() {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.vendorCode').reset();
		form.findField('model.vendorName').reset();
		form.findField('model.vendorErpCode').reset();
		form.findField('model.currencyCode').reset();
		form.findField('model.currencyRate').reset();
		form.findField('taxRateCode').reset();
	},

	/**
	 * @method formVendorErpCodeTrigger
	 * @param {Ext.field.Field} field 当前field对象 表单供应商点击触发事件
	 */
	formVendorErpCodeTrigger: function(field) {
		if (field.readOnly) { // 如果是只读，则不让下拉，直接返回
			return;
		} else {
			this.showVendorSelectWin();
		}
	},

	/**
	 * @method showVendorSelectWin 供应商选择窗体显示
	 * @return {Object} 供应商选择窗体
	 */
	showVendorSelectWin: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();

		if (Ext.isEmpty(purchasingOrgCode)) {
			Q.tips($('message.pleaseSelectPurchasingOrgCode'), 'E');
			return;
		}

		var vm = this.getView();
		var selectWin = new Sl.masterdata.VendorSelectWin({
			singleSelect: true,
			moduleId: vm.id,
			baseParams: {
				filter_IN_certificationStatus: 'QUALIFIED',
				purchasingOrgCode: purchasingOrgCode
			},
			baseParamsTree: {
				certificationStatus: 'QUALIFIED'
			},
			select: function(g, r) {
				var form = vp.editWin.formPanel.getForm();
				form.findField('model.vendorCode').setValue(r.get('vendorCode'));
				form.findField('model.vendorErpCode').setValue(r.get('vendorErpCode'));
				form.findField('model.vendorName').setValue(r.get('vendorName'));
				form.findField('taxRateCode').setValue(r.get('taxCode')); // 税率编码

				//初始化货币编码
				var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
				var vendorCode = r.get('vendorCode');
				var vendorPorgDtlStore = viewModel.getStore('vendorPorgDtlStore');
				vendorPorgDtlStore.proxy.extraParams.filter_EQ_vendor_vendorCode = vendorCode;
				vendorPorgDtlStore.proxy.extraParams.filter_EQ_purchasingOrgCode = purchasingOrgCode;
				vendorPorgDtlStore.load(function() {
					if (vendorPorgDtlStore.getCount() >= 1) {
						var currencyCode = vendorPorgDtlStore.getAt(0).get("currencyCode");
						// 加载汇率
						me.findCurrencyRate(vendorCode, purchasingOrgCode, currencyCode);
						form.findField("model.currencyCode").setValue(currencyCode);
					}
				});

				selectWin.close();
			}
		});

		selectWin.show();
	},

	/**
	 * @method formVendorErpCodeBlur 供应商编码失去焦点事件
	 * 
	 */
	formVendorErpCodeBlur: function(_self){
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var vendorCode = _self.value;
		if(vendorCode != ""){
			Ext.Ajax.request({
				url: path_srm + "/cp/purchaseorder/findvendor",
				method: 'POST',
				params: {
					filter_EQ_vendorErpCode : vendorCode
				},
				success: function(response) {
					var json = Ext.decode(response.responseText);
					if(json != ""){
						form.findField('model.vendorCode').setValue(json.vendorCode);
						form.findField('model.vendorErpCode').setValue(json.vendorErpCode);
						form.findField('model.vendorName').setValue(json.vendorName);
						form.findField('taxRateCode').setValue(json.taxCode); // 税率编码
						//初始化货币编码
						var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
						var vendorCode = json.vendorCode;
						var vendorPorgDtlStore = viewModel.getStore('vendorPorgDtlStore');
						vendorPorgDtlStore.proxy.extraParams.filter_EQ_vendor_vendorCode = vendorCode;
						vendorPorgDtlStore.proxy.extraParams.filter_EQ_purchasingOrgCode = purchasingOrgCode;
						vendorPorgDtlStore.load(function() {
							if (vendorPorgDtlStore.getCount() >= 1) {
								var currencyCode = vendorPorgDtlStore.getAt(0).get("currencyCode");
								// 加载汇率
								me.findCurrencyRate(vendorCode, purchasingOrgCode, currencyCode);
								form.findField("model.currencyCode").setValue(currencyCode);
							}
						});
					}else{
						Q.tips($('message.vendorNotExit'),'E');
						form.findField('model.vendorErpCode').setValue("");
						form.findField('model.vendorName').setValue("");
						return;
					}
				},
				failure: function(resp, opt) {
					Q.tips($('message.operator.failure'), 'E');
				}
			});
		}
	},
	
	/**
	 * @method formCurrencyCodeSelect 货币选中事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	currencyCodeSelect: function(combo, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var vendorCode = form.findField('model.vendorCode').getValue();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
		
		form.findField('model.currencyName').setValue(record.get('currencyName'));
		form.findField('model.currencyCode').setValue(record.get('currencyCode'));
		me.findCurrencyRate(vendorCode, purchasingOrgCode, record.get('currencyCode'));
	},

	/**
	 * @method formUploadFileHandler 表单上传附件
	 */
	formUploadFileHandler: function() {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var uploadFileGroupId = vp.editWin.formPanel.form.findField('model.uploadFileGroupId').getValue();
		vp.openUploadWindows(null, uploadFileGroupId, 'uploadFile4View', vp.editWin.formPanel.form, null);
	},

	/**
	 * @method gridDtlCloseFlagRenderer 订单明细关闭标识渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlCloseFlagRenderer: function(value, metaData) {
		var viewModel = this.getViewModel();
		return this.getBy(viewModel.getStore('flagStore'), 'display', 'value', value);
	},

	/**
	 * @method gridDtlLineItemTypeCodeRenderer 订单明细行类型渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlLineItemTypeCodeRenderer: function(value, metaData, record) {
		var viewModel = this.getViewModel();
		// this.setBgColor(metaData, record);
		return this.getBy(viewModel.getStore('lineItemTypeStore'), 'itemName', 'itemCode', value);
	},

	/**
	 * @method gridDtlUnitCodeSelect 订单明细单位选中事件
	 * @param {Object} combo 当前控件对象
	 * @param {Ext.data.Model} record 选中的记录
	 */
	gridDtlUnitCodeSelect: function(combo, record) {
		var record = this.getDetailGrid().getSelectionModel().getSelection()[0];
		record.set('unitName', record.get('unitName'));
	},

	/**
	 * @method gridDtlPlantCodeFocus 订单明细工厂聚焦事件
	 */
	gridDtlPlantCodeFocus: function() {
		var me = this;
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var record = this.getDetailGrid().getSelectionModel().getSelection()[0];
		var materialCode = record.get('materialCode');
		var purchasingOrgCode = vp.editWin.formPanel.form.findField('model.purchasingOrgCode').getValue();
		var params = {};

		// 没有物料编码从采购组织工厂关系取
		if (Ext.isEmpty(materialCode)) {
			params.purchasingOrgCode = purchasingOrgCode;
		} else {
			params = {
				materialCode: materialCode,
				purchasingOrgCode: purchasingOrgCode
			};
		}
		var plantStore = viewModel.getStore('plantStore');
		plantStore.proxy.extraParams = params;

		plantStore.load(function() {
			me.plantStoreLoad(plantStore, record);
		});

	},

	/**
	 * @method gridDtlStockTypeRenderer 订单明细库存类型渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlStockTypeRenderer: function(value, metaData, record) {
		var viewModel = this.getViewModel();
		this.setBgColor(metaData, record);
		return this.getBy(viewModel.getStore('stockTypeStore'), 'itemName', 'itemCode', value);
	},

	/**
	 * @method gridDtlPlantCodeSelect 订单明细工厂选中事件
	 * @param {Object} combo 当前控件对象
	 * @param {Ext.data.Model} record 选中的记录
	 */
	gridDtlPlantCodeSelect: function(combo, record) {
		var me = this;
		if (combo.originalValue != combo.value) {
			var detailRecord = this.getDetailGrid().getSelectionModel().getSelection()[0];
			detailRecord.set('plantCode', record.get('plantCode'));
			detailRecord.set('plantName', record.get('plantName'));
			detailRecord.set('storeLocal', "");
			me.setMaterialPlantInfo(detailRecord.get('materialCode'),record.get('plantCode'),detailRecord);
			me.setMaterialLadderPrice(record.get('plantCode'));
		}
	},
	
	/**
	 * 获取物料工厂视图信息
	 * @param {} materialCode 物料编码
	 * @param {} plantCode 工厂编码
	 * @param {} record 当前编码行
	 */
	setMaterialPlantInfo: function(materialCode,plantCode,record){
		try{
			var me = this;
			Ext.Ajax.request({
                url: path_srment + "/cp/purchaseapply/findpurchasinggroup",
                params: {
                    'materialCode': materialCode,
                    'plantCode': plantCode
                },
                success: function(resp) {
                    var data = Ext.decode(resp.responseText);
                    if (0 < data.length) {
                        record.set('stockType', data[0].qualityCheck);
                    }
                }
            });
			
		}catch(e){
			console.log(e);
		}
		
	},

	/**
	 * @method gridDtlStoreLocalFocus
	 * @param {Object} combo 当前对象 订单明细库存地点聚焦事件
	 */
	gridDtlStoreLocalFocus: function(combo) {
		var viewModel = this.getViewModel();
		var record = this.getDetailGrid().getSelectionModel().getSelection()[0];
		var plantCode = record.data.plantCode;

		if (Ext.isEmpty(plantCode)) {
			Q.tips($('porder.msg.selectPlantFirst'), 'E');
			return;
		}

		var storLocStore = viewModel.getStore('storLocStore');
		storLocStore.proxy.extraParams.filter_EQ_plantCode = plantCode;
		storLocStore.load();
		this.storLocStoreLoad();
	},

	/**
	 * @method gridDtlIsReturnRenderer 订单明细是否退货标识渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlIsReturnRenderer: function(value, metaData, record) {
		var viewModel = this.getViewModel();
		if (!Ext.isEmpty(record.get('rowIds')) && record.get('erpSynState') == 1) {
			this.setBgColor(metaData, record);
		}
		return this.getBy(viewModel.getStore('flagStore'), 'display', 'value', value);
	},

	/**
	 * @method gridPurchaseOrderTypeRenderer 订单订单类型渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridPurchaseOrderTypeRenderer: function(value, metaData, record) {
		var viewModel = this.getViewModel();
		return this.getBy(viewModel.getStore('purchasingOrderTypeStore'), 'itemName', 'itemCode', value);
	},

	/**
	 * @method gridDtlCloseOrCancelCloseHandler 订单关闭\取消关闭按钮事件
	 */
	gridDtlCloseOrCancelCloseHandler: function() {
		var me = this;
		var grid = me.getDetailGrid();
		var selecteds = grid.getSelectionModel().getSelection();

		if (0 == selecteds.length) {
			Q.tips($('receiptBill.pleaseSelect'), 'E');
			return false;
		}

		var detailId = selecteds[0].get('purchaseOrderDetailId');
		var closeFlag = selecteds[0].get('closeFlag');

		if(1 == closeFlag){
			//当订单的关闭标识为是，点击取消关闭时，需要判断该订单明细的收货量-退货量是否大于等于订单量*(1+过量交货限度），如果大于等于，则提示“该订单明细已经交货完成，不允许取消关闭”
			var val1 = selecteds[0].get('qtyArrive')-selecteds[0].get('qtyQuit');
			var val2 =  Math.round(selecteds[0].get('buyerQty')*(1+selecteds[0].get('overDeliveryLimit')/100)*100)/100;
			if (val1 >= val2 && 0 != val1) {
				Q.tips($('receiptBill.orderAlreadyCompleteDeliveryNotAllowedCancelClose'), 'E');
				return false;
			}
		}else {
			//当订单的关闭标识为否时，点击关闭时，需要判断该订单明细的在途量是否为0，如果不为0，则提示"该订单明细的在途量不为0，不允许关闭"
			if(selecteds[0].get('qtyOnline') != 0){
				Q.tips($('receiptBill.orderDetailsQtyOnlineNotZeroNotAllowedClose'), 'E');
				return false;
			}
		}
		
		closeFlag = closeFlag == 1 ? 0 : 1;
		me.closeOrderDetail(me, detailId, closeFlag);
	},

	/**
	 * @method gridDtlEdit 采购订单明细编辑事件
	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
	 * @param {Object} content 具有以下属性的编辑上下文
	 * @param {Ext.grid.Panel} content.content 所编辑的表格对象
	 * @param {Ext.data.Model} content.record 正在编辑的记录
	 * @param {String} content.field 正在编辑的字段名称
	 * @param {Mixed} content.value 字段当前值
	 * @param {HTMLElement} content.row 正在编辑的行html元素
	 * @param {Ext.grid.column.Column} content.column 正在编辑的列
	 * @param {Number} content.rowIdx 正在编辑的行序列
	 * @param {Number} content.colIdx 正在编辑的列序列.
	 */
	gridDtlEdit: function(editor, content) {
		var me = this;
		// 获取当前行
		var record = content.record;
		// 表格
		var grid = content.grid;
		// 物料编码
		var materialCode = record.get('materialCode');

		if (content.field == 'isFree') {
			// 免费订单行金额必须为0

			if (1 == content.value) {
				var pricingGrid = me.getPricingGrid();
				if (0 == pricingGrid.store.getCount()) {
					return;
				}
				var pricing = pricingGrid.getStore().getAt(0);
				pricing.set('pricingQty', 0);
			}

			// 免费 --> 不免费,有料号,数量不为空
			if (!Ext.isEmpty(materialCode) && 0 == content.value && 1 == content.originalValue && '' != record.get('buyerQty')) {
				me.setMaterialLadderPrice(record.get('plantCode'));
			}

			// 计算数量、定价、价格
			me.countPrice();
		}

		// 编辑数量
		if (content.field == 'buyerQty' || content.field == 'lineItemTypeCode') {
			// 计算数量、定价、价格
			me.countPrice();
			var isFree = 0;
			// 如果行金额为0，则免费标识置为是
			if (2 != record.get('lineItemTypeCode') && 0 == record.get('lineItemValAmt')) {
				isFree = 1;
			}
			record.set('isFree', isFree);
			
		}

		// 编辑数据进行价格主数据加载
		if (!Ext.isEmpty(materialCode) && content.field == 'buyerQty' && 1 != record.get('isFree')) {
			me.setMaterialLadderPrice(record.get('plantCode'));
		}

		// 编辑删除标志
		if (content.field == 'deleteFlag') {
			// 计算数量、定价、价格
			me.countTotalAmout();
		}

		// 编辑单位
		if (content.field == 'unitCode' && null != content.value) {
			// 设置双单位转换
			me.quantityUnitInfo();
		}

		// 编辑交货日期
		if (content.field == 'buyerTime') {
			// 设置双单位转换
			record.set('vendorTime', content.value);
		}

		grid.getStore().resumeEvents();
		grid.resumeEvents();
		//grid.getView().refresh();
	},

	/**
	 * @method gridDtlBeforeedit 采购订单明细编辑前事件
	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
	 * @param {Object} content 具有以下属性的编辑上下文
	 * @param {Ext.grid.Panel} content.content 所编辑的表格对象
	 * @param {Ext.data.Model} content.record 正在编辑的记录
	 * @param {String} content.field 正在编辑的字段名称
	 * @param {Mixed} content.value 字段当前值
	 * @param {HTMLElement} content.row 正在编辑的行html元素
	 * @param {Ext.grid.column.Column} content.column 正在编辑的列
	 * @param {Number} content.rowIdx 正在编辑的行序列
	 * @param {Number} content.colIdx 正在编辑的列序列.
	 * @param {Boolean} context.cancel 将此设置为“TRUE”取消编辑或从处理程序返回false。
	 * @param {Mixed} context.originalValue 编辑前的值
	 */
	gridDtlBeforeedit: function(editor, content) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var record = content.record;
		var field = content.field;
		var erpSynState = vp.editWin.formPanel.form.findField('model.erpSynState').getValue();

		if ('view' == vp.editWin.viewType) {
			content.cancel = true;
			return;
		}

		if (vp.editWin.holdFlag) {
			if (field != 'vendorTime') {
				content.cancel = true;
				return;
			}
		} else {
			if (field == 'vendorTime') {
				content.cancel = true;
				return;
			}
		}
		// 物料编码不为空的情况下，物料名称不允许编辑
		if (!Ext.isEmpty(record.get('materialCode'))) {

			// 通过价格主数据选取的订单明细，以下字段从价格主数据自动带出，设置为只读：行项目类别、物料编码、物料名称、单位、税率编码
			var array = ['lineItemTypeCode', 'materialCode', 'materialName', 'unitCode', 'unitName', 'taxRateCode'];

			for (var i = 0, len = array.length; i < len; i++) {
				if (field == array[i]) {
					Q.tips($('porder.materialCodeIsNotBlankTips'), 'E');
					content.cancel = true;
					return;
				}
			}
		}

		// 没有同步SAP的或不是来源于SAP的数据，不允许修改删除、退货标识
		if (field == 'deleteFlag' || field == 'closeFlag') {
			if (!Ext.isEmpty(record.get('rowIds')) && erpSynState == 1) {
				return true;
			} else {
				return false;
			}
		}

		// 价格主数据免费标识不可以修改
		if (field == 'isFree' && 2 == record.get('sourceCode')) {
			return;
		}

		// 判断是否
		return vp.editWin.beforeedit();
	},

	/**
	 * @method gridDtlSelectionchange 采购订单明细列表选中改变事件
	 * @param {Ext.selection.Model} sm 选中组件对象
	 * @param {Ext.data.Model[]} selected 选中的记录
	 */
	gridDtlSelectionchange: function() {
		var me = this;

		var grid = me.getDetailGrid();
		var selected = grid.getSelectionModel().getSelection();
		// 删除按钮控制
		var tbar = grid.getTopToolbar();
		if (Ext.isEmpty(tbar)) {
			return;
		}
		var deleteBtn = tbar.query('button[name=deleteBtn]')[0];
		var closeBtn = tbar.query('button[name=closeBtn]')[0];
		var cancelCloseBtn = tbar.query('button[name=cancelCloseBtn]')[0];
		// 判断是否有要编辑的明细，已提交的明细不允许删除，只能修改删除标识
		var hasId = false;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (vp.editWin.holdFlag) {
			hasId = true;
		}

		var form = vp.editWin.formPanel.getForm();
		var purchaseOrderFlowState = form.findField('model.purchaseOrderFlowState').getValue();
		var erpSynState = form.findField('model.erpSynState').getValue();
		var state = form.findField('model.purchaseOrderState').getValue();

		// 关闭按钮、取消关闭按钮的设置
		if (!Ext.isEmpty(selected)) {
			if ('add' == vp.editWin.btnType || 'edit' == vp.editWin.btnType) {
				closeBtn.setDisabled(true);
				cancelCloseBtn.setDisabled(true);
			} else {
				var closeFlag = selected[0].get('closeFlag');
				if (-1 == s_roleTypes.indexOf('V')) {
					if (('OPEN' == state || 'CLOSE' == state ) && selected[0].get('deleteFlag') == 0) {
						if (closeFlag == 1) {
							closeBtn.setDisabled(true);
							cancelCloseBtn.setDisabled(false);
						} else {
							closeBtn.setDisabled(false);
							cancelCloseBtn.setDisabled(true);
						}
					} else {
						closeBtn.setDisabled(true);
						cancelCloseBtn.setDisabled(true);
					}
				}
			}
		}

		// 审核不过的已同步订单，编辑时，订单明细不能直接删除，[删除]按钮应该反灰
		if ('view' == vp.editWin.btnType || selected.length == 0 || (purchaseOrderFlowState == "NOPASS" && erpSynState == '1' && !Ext.isEmpty(selected[0].get("purchaseOrderDetailId")))) {
			deleteBtn.disable();
		} else {
			deleteBtn.enable();
		}
		if (selected.length == 0) {
			return;
		}
	},

	/**
	 * @method existSrmRowId 判断行号是否已经存在
	 * @param {Integer} srmRowId 需要进行判断的行号
	 * @return {Boolean} 存在返回true，否则false
	 */
	existSrmRowId: function(srmRowId) {
		var grid = this.getDetailGrid();
		var index = grid.store.find('srmRowids', srmRowId);
		if (-1 < index) {
			return true;

		}
		return false;
	},
	/**
	 * @method getSrmRowIdList 获取当前订单最后一行的行号
	 * @return {String} 当前订单最后一行的行号
	 */
	getMaxSrmRowId: function() {
		var grid = this.getDetailGrid();
		var store = grid.store;
		var count = store.getCount();
		if(count==0){
			return "0";
		}
		var r = store.getAt(store.getCount()-1);
		return r.get("srmRowids");
	},

	/**
	 * @method resetSrmRowId 重新计算SRM行号
	 */
	resetSrmRowId: function() {
		var me = this;
		var grid = me.getDetailGrid();
		var store = grid.getStore();
		// 先清除未提交过的行的行号，重新生成
		for (var j = 0; j < store.getCount(); j++) {
			var r = store.getAt(j);
			if (!Ext.isEmpty(r.get('purchaseOrderDetailId'))) {
				return true;
			} else {
				r.set('srmRowids', '');
			}
		}
		for (var j = 0; j < store.getCount(); j++) {
			var r = store.getAt(j);
			var srmRowId = 0;
			for (var i = 1; i < 10000; i++) {
				if (!Ext.isEmpty(r.get('purchaseOrderDetailId'))) {
					return true;
				}
				srmRowId = i * 10;
				if (!me.existSrmRowId(srmRowId)) {
					r.set('srmRowids', srmRowId);
					break;
				}
			}
		}
	},

	/**
	 * @method addDetailManual 手动添加物料明细
	 */
	addDetailManual: function() {
		this.buildNewDetail('', '');
	},

	/**
	 * @method buildNewDetail 构建采购订单明细
	 * @param {String} plantCode 工厂
	 * @param {locationcode} plantCode 库存地址
	 */
	buildNewDetail: function(plantCode, locationcode) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		// 添加物料明细
		var grid = me.getDetailGrid();
		var mainForm = vp.editWin.formPanel.getForm();
		var taxRateCode = mainForm.findField('taxRateCode').getValue();
		var count = grid.store.getCount();
		
		
		//拼装临时字段数据
		var unitData = [];
		var formdata = {
			"purchaseOrderQtyId" : "",
			"orderDetailUnit" : "",
			"pricingUnit" : "",
			"unitCode" : "",
			"orderDetailUnit2" : "",
			"convertMolecular2" : "",
			"convertMolecular" : "",
			"convertDenominator2" : "",
			"convertDenominator" : "",
			"pricingQty" : "",
			"skuQty" : "",
			"comment": "<font color=\"red\">" + $('porderDtl.message.warnMsg2') + "</font>"
		};
		unitData.push(formdata);
		unitData = Ext.JSON.encode(unitData);

		var pricingType = viewModel.getStore('pricingConditionTypeStore').getAt(0).data;
		var pricingData = {
			purchaseOrderPricingTypeCode: pricingType.itemCode,
			purchaseOrderPricingTypeName: pricingType.itemName,
			priceUnit: 1,
			pricingQty: 0,
			purchaseOrderPricingRowId: 1
		};
		var pricingDataArr = [];
		pricingDataArr.push(pricingData);
		pricingDataArr = Ext.JSON.encode(pricingDataArr);
		var curDate = new Date();
		var data = {
				unitConversionInfo: unitData,
				pricingInfo: pricingDataArr,
				sourceCode: 1, // 来源于手动创建
				srmRowids: (count + 1) * 10,
				scheduleFlag: 0,
				emergencyFlag: 0,
				taxRateCode: taxRateCode,
				plantCode: plantCode,
				storeLocal: locationcode,
				closeFlag: 0,
				deleteFlag: 0,
				isReturn: 0,
				isFree: 0,
				lineItemTypeCode: 0,
				overDeliveryLimit: 0,
				shortDeliveryLimit: 0,
				stockType: 'A',
			    buyerTime: curDate,
				vendorTime: curDate
			};
		vp.editWin.addDetail(grid, data);
		me.resetSrmRowId();
		grid.getSelectionModel().select(grid.store.getCount() - 1);
	},

	/**
	 * @method showMaterialMasterPriceWin 显示价格主数据窗体
	 */
	showMaterialMasterPriceWin: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		// 供应商编码
		var mainData = vp.editWin.formPanel.getForm().getValues();
		var vendorErpCode = mainData['model.vendorErpCode'];
		var currencyCode = mainData['model.currencyCode'];
		var purchasingOrgCode = mainData['model.purchasingOrgCode'];
		var params = {
			filter_EQ_materialMasterPrice_vendorErpCode: vendorErpCode, // 供应商编码
			filter_EQ_materialMasterPrice_purchasingOrgCode: purchasingOrgCode,
			filter_EQ_currencyCode: currencyCode
		};

		var ids = '';
		var records = me.getDetailGrid().getStore().getRange();

		for (var i = 0; i < records.length; i++) {
			var r = records[i];
			var id = r.get('materialMasterPriceId');
			if (!Ext.isEmpty(id)) {
				ids += r.get('materialMasterPriceId') + ',';
			}
		}

		for (var i = 0; i < me.removedMaterialMasterPriceIds.length; i++) {
			var id = me.removedMaterialMasterPriceIds[i];
			if (!Ext.isEmpty(id)) {
				ids += id + ',';
			}
		}

		if (ids.length > 0) {
			ids = ids.substring(0, ids.length - 1);
		}

		var vm = this.getView();
		var win = new Cp.order.MaterialMasterPriceSelectWin({
			singleSelect: false,
			baseParams: params,
			viewModel: viewModel,
			moduleId: vm.id,
			select: function(grid, rs) {
				me.buildNewDetail4MaterialMasterP(rs);
				win.hide();
			},
			listeners: {
				hide: function() {
					win.hide();
				}
			}
		});

		win.show();
	},

	/**
	 * @method gridDtlAddHandler 订单明细添加按钮操作方法,数据来源：价格主数据
	 */
	gridDtlAddPriceHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var items = vp.editWin.formPanel.items.items;
		var isValidFlag = false; // 是否存在未校验的主单
		Ext.each(items, function(field) {
			if (!Ext.isEmpty(field.isValid) && Ext.isFunction(field.isValid)) {
				if (!field.isValid()) {
					isValidFlag = true;
				}
			}
		});
		if (!isValidFlag) {
			me.showMaterialMasterPriceWin();
		} else {
			Q.tips($('porder.inputMainInfo'), 'E');
		}
	},

	/**
	 * @method gridDtlAddHandler 订单明细添加明细按钮操作方法，数据来源：手动新建
	 */
	gridDtlAddManualHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var items = vp.editWin.formPanel.items.items;
		var isValidFlag = false; // 是否存在未校验的主单
		Ext.each(items, function(field) {
			if (!Ext.isEmpty(field.isValid) && Ext.isFunction(field.isValid)) {
				if (!field.isValid()) {
					isValidFlag = true;
				}
			}
		});

		if (!isValidFlag) {
			me.addDetailManual();
			me.resetSrmRowId();
		} else {
			Q.tips($('porder.inputMainInfo'), 'E');
		}

	},

	/**
	 * @method gridDtlImportHandler 订单明细导入按钮操作方法
	 */
	gridDtlImportHandler: function(_self) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var items = vp.editWin.formPanel.items.items;
		var isValidFlag = false; // 是否存在未校验的主单
		Ext.each(items, function(field) {
			if (!Ext.isEmpty(field.isValid) && Ext.isFunction(field.isValid)) {
				if (!field.isValid()) {
					isValidFlag = true;
				}
			}
		});

		if (!isValidFlag) {
			var mainData = vp.editWin.formPanel.getForm().getValues();
			//获取当前行号集合
			var maxSrmRowId = me.getMaxSrmRowId();
			var urlParams = '?filter_EQ_materialMasterPrice_vendorErpCode=' + mainData['model.vendorErpCode'] 
			+ '&filter_EQ_materialMasterPrice_purchasingOrgCode=' + mainData['model.purchasingOrgCode'] 
			+ '&filter_EQ_currencyCode=' + mainData['model.currencyCode']
			+ '&srmRowIds='+maxSrmRowId;
			var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid 
            var opt = {
                url:viewModel.get("dealUrl"), // + '/importexcel' + urlParams
                method: "/importexcel"+ urlParams,
                importSuccess:function(op, op1){//导入成功后回调
                	if(!op1.data){
                		return;
                	}
        		    me.buildNewDetail4MaterialMasterPriceForImport(op1.data);
                }
            };//配置项
            var renderTo = vp.editWin.id;//渲染载体
            //文件工具类调用
            Ext.UxFile.fileImport(opt, renderTo); 
		} else {
			Q.tips($('porder.inputMainInfo'), 'E');
		}

	},
	
	/**
	 * @method buildNewDetail4MaterialMasterPriceForImport 导入订单明细-构建价格主数据明细到订单明细中
	 * @param {Ext.data.Model[]} records 选中给的采购订单明细
	 */
	buildNewDetail4MaterialMasterPriceForImport: function(records) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		// 添加物料明细
		var form = vp.editWin.formPanel.getForm();
		var grid = me.getDetailGrid();
		var store = grid.getStore();
		var mainData = vp.editWin.formPanel.getForm().getValues();
		var purchasingOrgCode = mainData['model.purchasingOrgCode'];

		var arr = [];
		
		vp.editWin.addDetail(grid, records);
		me.countTotalAmout();
	},
	/**
	 * @method buildNewDetail4MaterialMasterP 构建价格主数据明细到订单明细中
	 * @param {Ext.data.Model[]} records 选中给的采购订单明细
	 */
	buildNewDetail4MaterialMasterP: function(records) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		
		// 添加物料明细
		var form = vp.editWin.formPanel.getForm();
		var grid = me.getDetailGrid();
		var store = grid.getStore();
		var mainData = vp.editWin.formPanel.getForm().getValues();
		var purchasingOrgCode = mainData['model.purchasingOrgCode'];
		
		for (var i = 0, len = records.length; i < len; i++) {
			var record = records[i];
			if (undefined != record.data) {
				record = record.data;
			}
			var buyerTime;
			if (Ext.isEmpty(record.buyerTime)) {
				buyerTime = new Date();
				if (null != record.plannedDays && undefined != record.plannedDays && !isNaN(record.plannedDays)) {
					buyerTime.setDate(buyerTime.getDate() + parseInt(record.plannedDays));
				}
			} else {
				buyerTime = new Date(record.buyerTime);
			}
			var jitFlag = 0;
			
			if (undefined != record.jitFlag && null != record.jitFlag && "1" == record.jitFlag) {
				jitFlag = 1;
			}
			
			var buyerQty = record.buyerQty;
			if (undefined == buyerQty) {
				buyerQty = '';
			}
			
			
			// 根据订单单位和定价单位、SKU的转换关系，计算定价单位数量和SKU数量
			var qty = buyerQty;
			// 订单单位
			var orderDetailUnit = record.orderElementaryUnit;
			// 订价单位
			var pricingUnit = record.pricingUnit;
			// sku
			var sku = record.elementaryUnit;
			if (isNaN(qty) || isNaN(orderDetailUnit) || isNaN(pricingUnit) || isNaN(sku)) {
				return;
			}
			// 订价单位数量=订价单位/订单单位*订单数量
			var pricingQty = pricingUnit / orderDetailUnit * qty;
			// sku数量=sku/订单单位*订单数量
			var skuQty = sku / orderDetailUnit * qty;
			
			var recordType = record.recordType;
			// 获取当前行定价
			var pricingPrice = record.nonTaxPrice;
			// 获取当前行价格单位
			var priceUnit = record.priceUnit;
			var amount = 0;
			if (2 == recordType) {
				pricingPrice= 0;
			}
			if (!Ext.isEmpty(pricingQty) && !Ext.isEmpty(pricingPrice) && !Ext.isEmpty(priceUnit)) {
				amount = pricingQty * pricingPrice / priceUnit;
			}
			
			// 计算数量、定价、价格
			var lineItemTypeCode = record.recordType;
			// 计算行金额
			var pricingAmount = amount;
			var priceUnit = priceUnit;
			var lineItemValAmt = pricingAmount;
			// 计算价格
			var buyerPrice = pricingAmount / pricingQty * priceUnit;
			
			var unitName = me.getBy(viewModel.getStore('unitStore'), 'unitName', 'unitCode', record.elementaryUnitCode);
			//拼装临时字段数据
			var unitData = [];
			var formdata = {
					"purchaseOrderQtyId" : "",
					"orderDetailUnit" : record.orderPricingUnitCode,
					"pricingUnit" : record.pricingUnitCode,
					"unitCode" : record.elementaryUnitCode,
					"orderDetailUnit2" : record.orderElementaryUnitCode,
					"convertMolecular2" : record.orderPricingUnit,
					"convertMolecular" : record.orderElementaryUnit,
					"convertDenominator2" : record.pricingUnit,
					"convertDenominator" : record.elementaryUnit,
					"pricingQty" : pricingQty,
					"skuQty" : skuQty
			};
			unitData.push(formdata);
			unitData = Ext.JSON.encode(unitData);
			
			// 属于寄售数据
			if ('2' == record.recordType) {
				record.nonTaxPrice = 0;
			}
			var pricingType = viewModel.getStore('pricingConditionTypeStore').getAt(0).data;
			var pricingDataArr = [];
			var pricingData = {
					purchaseOrderPricingTypeCode: pricingType.itemCode,
					purchaseOrderPricingTypeName: pricingType.itemName,
					priceUnit: record.priceUnit,
					purchaseOrderPricingRowId: 1,
					pricingQty: record.nonTaxPrice,
					amount: amount
			};
			pricingDataArr.push(pricingData);
			pricingDataArr = Ext.JSON.encode(pricingDataArr);
			
			var count = grid.store.getCount();
			var data = {
					unitConversionInfo: unitData,
					pricingInfo: pricingDataArr,
					lineItemTypeCode: record.recordType,
					srmRowids: (count + 1) * 10,
					materialCode: record.materialCode,
					materialName: record.materialName,
					unitCode: record.orderElementaryUnitCode,
					unitName: unitName,
					plantCode: record.plantCode,
					plantName: record.plantName,
					overDeliveryLimit: record.excessDeliveryLimit,
					shortDeliveryLimit: record.deliveryLimit,
					materialMasterPriceId: record.materialMasterPriceId, // 主单ID
					materialMasterPriceDtlId: record.materialMasterPriceDtlId, // 细单ID
					materialLadderPriceDtlId: record.materialLadderPriceDtlId, // 价格ID
					taxRateCode: record.taxRateCode,
					buyerTime: buyerTime,
					vendorTime: buyerTime,
					stockType: record.qualityCheck,
					storeLocal: record.stockLocationCode,
					sourceCode: 2, // 来源于价格主数据
					scheduleFlag: jitFlag,
					emergencyFlag: 0,
					closeFlag: 0,
					deleteFlag: 0,
					isReturn: 0,
					isFree: 0,
					buyerQty: buyerQty,
					buyerPrice: buyerPrice,
					lineItemValAmt: lineItemValAmt,
					pricingUnit:record.pricingUnitCode
			};
			
			vp.editWin.addDetail(grid, data);
			//me.resetSrmRowId();
			grid.getSelectionModel().select(grid.store.getCount() - 1);
			
		}
		
		
		me.resetSrmRowId();
		// 计算数量、定价、价格
		me.countItemAmount();
		me.countTotalAmout();
	},

	/**
	 * @method gridDtlDownloadHandler 订单明细下载模版
	 */
	gridDtlDownloadHandler: function() {
		Ext.UxFile.fileDown(this.getViewModel().get('dealUrl')+"/download?templateFile=PurchaseOrderMatertals.xls&fileName=订单明细导入模板","订单明细导入模板.xls",null);
		//window.open(this.getViewModel().get('dealUrl') + '/download?templateFile=PurchaseOrderMatertals.xls&fileName=订单明细导入模板');
	},

	/**
	 * @method gridDtlDeleteHandler 订单明细删除
	 */
	gridDtlDeleteHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var sm = me.getDetailGrid().getSelectionModel();
		var selections = sm.getSelection();
		if (0 == selections.length) {
			Q.tips($('message.delete.select'), 'E');
			return false;
		}

		// 已同步SAP的明细不能删除
		var hasRowId = false;
		for (var i = 0; i < selections.length; i++) {
			var r = selections[i];
			if (1 == r.get('erpSynState')) {
				hasRowId = true;
				break;
			}
		}
		if (hasRowId) {
			Q.tips($('porder.changeDeletionFlag'), 'E');
			return;
		}

		var vm = this.getView();
		Q.confirm($('message.delete.confirm'), {
			renderTo: vm.id,
			ok: function() {
				// 删除明细时同时恢复定价/定标明细的数量
				for (var i = 0; i < selections.length; i++) {
					var json = selections[i].data;
					if (!Ext.isEmpty(json.purchaseOrderDetailId)) {
						me.purchaseOrderDetails.push({
							buyerQty: json.buyerQty
						});

						if (!Ext.isEmpty(json.removedMaterialMasterPriceIds)) {
							me.removedMaterialMasterPriceIds.push(json.materialMasterPriceId);
						}
					}
				}
				vp.editWin.deleteDetail(me.getDetailGrid());
				me.resetSrmRowId();
				me.countTotalAmout();
			}
		});
	},

	/**
	 * @method gridPricingDtlEdit 采购订单明细价格条件编辑事件
	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
	 * @param {Object} content 具有以下属性的编辑上下文
	 * @param {Ext.grid.Panel} content.content 所编辑的表格对象
	 * @param {Ext.data.Model} content.record 正在编辑的记录
	 * @param {String} content.field 正在编辑的字段名称
	 * @param {Mixed} content.value 字段当前值
	 * @param {HTMLElement} content.row 正在编辑的行html元素
	 * @param {Ext.grid.column.Column} content.column 正在编辑的列
	 * @param {Number} content.rowIdx 正在编辑的行序列
	 * @param {Number} content.colIdx 正在编辑的列序列.
	 */
	gridPricingDtlEdit: function(editor, content) {
		var me = this;
		if (content.field == 'pricingQty' && 0 < content.value) {
			var selecteds = me.getDetailGrid().getSelectionModel().getSelection();
			selecteds[0].set('isFree', 0);
		}

		if (content.field == 'pricingQty' || content.field == 'priceUnit') {
			me.countPrice();
			return;
		}
	},

	/**
	 * @method gridPricingDtlBeforeedit 采购订单明细价格条件编辑前事件
	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
	 * @param {Object} content 具有以下属性的编辑上下文
	 * @param {Ext.grid.Panel} content.content 所编辑的表格对象
	 * @param {Ext.data.Model} content.record 正在编辑的记录
	 * @param {String} content.field 正在编辑的字段名称
	 * @param {Mixed} content.value 字段当前值
	 * @param {HTMLElement} content.row 正在编辑的行html元素
	 * @param {Ext.grid.column.Column} content.column 正在编辑的列
	 * @param {Number} content.rowIdx 正在编辑的行序列
	 * @param {Number} content.colIdx 正在编辑的列序列.
	 * @param {Boolean} context.cancel 将此设置为“TRUE”取消编辑或从处理程序返回false。
	 * @param {Mixed} context.originalValue 编辑前的值
	 */
	gridPricingDtlBeforeedit: function(editor, content) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		// 判断是否
		var record = me.getDetailGrid().getSelectionModel().getSelection()[0];
		if (undefined != record.get('materialMasterPriceId') && '' != record.get('materialMasterPriceId')) {
			content.cancel = true;
			return false;
		}

		if (vp.editWin.holdFlag) {
			content.cancel = true;
			return false;
		}
		return vp.editWin.beforeedit();
	},

	/**
	 * @method pricingGridDtlTypeCodeRenderer 采购订单明细定价条件类型编码渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @return {String} 要呈现的HTML字符串
	 */
	pricingGridDtlTypeCodeRenderer: function(value, metaData, record) {
		var me = this;
		var viewModel = me.getViewModel();

		me.setBgColor(metaData, record);
		return me.getBy(viewModel.getStore('pricingConditionTypeStore'), 'itemName', 'itemCode', value);
	},

	/**
	 * @method pricingGridDtlPricingQtyRenderer 采购订单明细定价条件价格渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行
	 * @return {String} 要呈现的HTML字符串
	 */
	pricingGridDtlPricingQtyRenderer: function(value, metaData, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		me.setBgColor(metaData, record);
		var record = me.getDetailGrid().getSelectionModel().getSelection()[0];

		// TOOD 未知
		if (Ext.isEmpty(record)) {
			record = vp.getCompByTabClassName('purchaseOrderDetails').getSelectionModel().getSelection()[0];
		}

		if (0 == record.get('isVendorView')) {
			return '';
		}
		return Ext.util.Format.number(value, '0.00');
	},

	/**
	 * @method pricingGridDtlAmountRenderer 采购订单明细定价条件行价格渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行
	 * @return {String} 要呈现的HTML字符串
	 */
	pricingGridDtlAmountRenderer: function(value, metaData, record) {
		var me = this;
		var viewModel = me.getViewModel();
		me.setBgColor(metaData, record);

		var record = me.getDetailGrid().getSelectionModel().getSelection()[0];
		if (Ext.isEmpty(record)) {
			record = vp.getCompByTabClassName('purchaseOrderDetails').getSelectionModel().getSelection()[0];
		}
		if (0 == record.get('isVendorView')) {
			return '';
		}
		return Ext.util.Format.number(value, '0.00');
	},

	/**
	 * @method editWinHide 编辑窗体隐藏事件
	 */
	editWinHide: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var editWin = viewModel.getEditWin();

		editWin.editFlag = false;
		delete editWin.viewType;

		// 展开主单
		var formPanel = editWin.formPanel;
		var tbar = formPanel.getTopToolbar();
		formPanel.body.dom.style.display = 'block';
		formPanel.setHeight(150);
		tbar.query('button[name=collapse]')[0].show();
		tbar.query('button[name=expand]')[0].hide();
	},


	/**
	 * @method gridEditBgColorRenderer 列表编辑背景色渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridEditBgColorRenderer: function(value, metaData, record) {
		var me = this;
		if(Ext.isEmpty(value)){
			return'0.00';
		}
		
		return Ext.util.Format.number(value,'0.00');
	},

	/**
	 * @method interactionGridAddHandler 订单交互创建
	 */
	interactionGridAddHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var record = vp.grid.getSelection()[0];
		var status = record.get('purchaseOrderState');
		if(status == 'NEW' || status === 'CANCEL'){
			Q.tips($('purchaseOrder.interact.message.valid1'),"E");
			return false;
		}
		var win = new Cp.order.InteractionEditWin({
			isView: false,
			moduleId:  me.getViewModel().getVp().id,
			vp: this.getViewModel().getVp()
		});

		win.show();
	},

	/**
	 * @method interactionGridAddHandler 订单交互创建
	 */
	interactionGridViewHandler: function() {
		var me = this;
		var grid = me.getInteractionGrid();
		var selecteds = grid.getSelectionModel().getSelection();

		if (selecteds.length != 1) {
			Q.tips($('message.pleaseSelect'), 'E');
			return;
		}

		var win = new Cp.order.InteractionEditWin({
			isView: true,
			moduleId: me.getViewModel().getVp().id,
			vp: this.getViewModel().getVp(),
			selected: selecteds[0]
		});
		win.show();
	},

	/**
	 * @method interactionGridDeleteHandler 订单删除创建
	 */
	interactionGridDeleteHandler: function() {
		var me = this;
		var grid = me.getInteractionGrid();
		var selected = grid.getSelectionModel().getSelection();
		if (selected.length != 1) {
			Q.tips($('message.pleaseSelect'), 'E');
			return;
		}
		selected = selected[0];

		Q.confirm($('message.delete.confirm'), function(btn) {
			if (btn == 'ok') {
				Ext.Ajax.request({
					url: path_console + '/sys/billbbs/delete',
					method: 'POST',
					params: {
						id: selected.get('billBbsId')
					},
					success: function(resp, opt) {
						Q.tips($('message.operator.success'));
						grid.getStore().reload();
					},
					success: function(response) {
						var json = Ext.decode(response.responseText);
						if (false === json.success) {
							Q.tips(json.msg || $('message.delete.failure'), 'E');
							return;
						} else if (true === json.success) {
							Q.tips($('message.delete.success'));
						}
						grid.getStore().reload();
					},
					failure: function(resp, opt) {
						Q.tips($('message.operator.failure'), 'E');
					}
				});
			}
		});

	},
	
	/**
	 * @method gridEditBgColorRenderer 列表编辑交货时间字段背景色渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlBuyerTimeRenderer: function(value, metaData, record) {
		this.setBgColor(metaData, record);

		if (value && typeof value == 'string') {
			return value.substring(0, 10);
		} else if (value && typeof value == 'object') {
			return Ext.util.Format.date(value, 'Y-m-d');
		}

		return value;
	},

	/**
	 * @method gridEditBgColorRenderer 列表编辑确认交货时间字段背景色渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlVendorTimeRenderer: function(value, metaData, record) {
		var showValue = value;
		this.setBgColor(metaData, record);

		if (value && typeof value == 'string') {
			showValue = value.substring(0, 10);
		} else if (value && typeof value == 'object') {
			showValue = Ext.util.Format.date(value, 'Y-m-d');
		} else {
			return showValue;
		}

		if (value != record.get('buyerTime')) {
			showValue = Q.color(showValue);
		}

		return showValue;
	},

	/**
	 * @method setBgColor 设置背景列表的验证出错时的背景颜色 setBgColor Function
	 * 
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 */
	setBgColor: function(metaData, record) {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var dataIndex = metaData.column.dataIndex;
		var rendererColor = metaData.column.rendererColor;

		if ('view' == vp.editWin.btnType || undefined == vp.editWin.btnType) {
			return;
		}

		if (vp.editWin.holdFlag == true && dataIndex != 'vendorTime') {
			return;
		}
		if (vp.editWin.editFlag) {
			viewModel.getEditWin().setBgColor(metaData, record, dataIndex, rendererColor);
		} else {
			return;
		}
	},

	/**
	 * @method setEditWinValues 其他模块设置订单编辑界面的值
	 * 
	 * @param {Object} editWin 订单编辑窗口
	 * @param {Object} record 订单主单记录
	 * @param {Object} detailRecords 订单明细记录
	 */
	setEditWinValues: function(editWin, record, detailRecords, pricingType) {
		var win = this;
		var form = editWin.formPanel.getForm();
		var viewModel = win.getViewModel();
		// 禁止触发改变事件
		Ext.each(form.monitor.items.items, function(v, i) {
			form.findField(v.name).suspendEvent("change");
		})
		 var purchaseGroupStore = viewModel.getStore('purchaseGroupStore');
	      if(purchaseGroupStore.getCount() == 1){
	      	var record = purchaseGroupStore.getAt(0);
	    	form.findField('model.purchasingGroupCode').setValue(record.get('purchasingGroupCode'));
			form.findField('model.purchasingGroupName').setValue(record.get('purchasingGroupName'));
	      }
        var vendorErpCode = form.findField('model.vendorErpCode').getValue();
		
		if(!Ext.isEmpty(vendorErpCode)){
			Ext.Ajax.request({
				url: path_srm + "/cp/purchaseorder/findvendor",
				method: 'POST',
				params: {
					filter_EQ_vendorErpCode :  vendorErpCode
				},
				success: function(response) {
					var json = Ext.decode(response.responseText);
					if(json != ""){
						form.findField('taxRateCode').setValue(json.taxCode); // 税率编码
						//初始化货币编码
						var purchasingOrgCode = record.get("purchasingOrgCode");
						var vendorCode = json.vendorCode;
						var vendorPorgDtlStore = viewModel.getStore('vendorPorgDtlStore');
						vendorPorgDtlStore.proxy.extraParams.filter_EQ_vendor_vendorCode = vendorCode;
						vendorPorgDtlStore.proxy.extraParams.filter_EQ_purchasingOrgCode = purchasingOrgCode;
						vendorPorgDtlStore.load(function() {
							if (vendorPorgDtlStore.getCount() >= 1) {
								var currencyCode = vendorPorgDtlStore.getAt(0).get("currencyCode");
								// 加载汇率
								win.findCurrencyRate(vendorCode, purchasingOrgCode, currencyCode);
								form.findField("model.currencyCode").setValue(currencyCode);
							}
						});
					}else{
						Q.tips($('message.vendorNotExit'),'E');
						form.findField('model.vendorErpCode').setValue("");
						form.findField('model.vendorName').setValue("");
						return;
					}
				},
				failure: function(resp, opt) {
					Q.tips($('message.operator.failure'), 'E');
				}
			});
		}

		// 设置订单明细
		var delGrid = editWin.getCompByTabClassName("purchaseOrderDetails");
		var arr = [];
		Ext.each(detailRecords, function(r, i) {
			// 设置双单位转换
			var unitData = [];
			var formdata = {
				"purchaseOrderQtyId" : '',
				"orderDetailUnit" : r.data.purchasingRequisitionCollection.unitCode,
				"pricingUnit" : r.data.purchasingRequisitionCollection.unitCode,
				"unitCode" : r.data.purchasingRequisitionCollection.unitCode,
				"orderDetailUnit2" : r.data.purchasingRequisitionCollection.unitCode,
				"convertMolecular2" : 1,
				"convertMolecular" : 1,
				"convertDenominator2" : 1,
				"convertDenominator" : 1
			};
			unitData.push(formdata);
			unitData = Ext.JSON.encode(unitData);

			// 设置价格明细
			// 属于寄售数据
			if ('2' == r.data.purchaseType) {
				r.data.price = 0;
			}
			var pricingDataArr = [];
			var amount = parseFloat(r.data.purchasingRequisitionCollection.transferQuantity) * parseFloat(r.data.price);
			var pricingData = {
				purchaseOrderPricingTypeCode: pricingType.getAt(0).data.itemCode,
				purchaseOrderPricingTypeName: pricingType.getAt(0).data.itemName,
				priceUnit: 1,
				purchaseOrderPricingRowId: 1,
				pricingQty: r.data.price,
				amount: Ext.util.Format.number(amount, '0.00')
			};
			pricingDataArr.push(pricingData);
			pricingDataArr = Ext.JSON.encode(pricingDataArr);

			var data = new Ext.data.Record({
				srmRowids: (i + 1) * 10,
				sourceCode: 3, // 来源于采购申请
				unitConversionInfo: unitData,
				pricingInfo: pricingDataArr,
				lineItemTypeCode: r.data.purchaseType,
				materialCode: r.data.purchasingRequisitionCollection.materialCode,
				materialName: r.data.purchasingRequisitionCollection.materialName,
				unitCode: r.data.purchasingRequisitionCollection.unitCode,
				unitName: r.data.purchasingRequisitionCollection.unitName,
				plantCode: r.data.purchasingRequisitionCollection.plantCode,
				plantName: r.data.purchasingRequisitionCollection.plantName,
				overDeliveryLimit: 0,
				shortDeliveryLimit: 0,
				materialMasterPriceId: '', // 主单ID
				materialMasterPriceDtlId: '', // 细单ID
				materialLadderPriceDtlId: '', // 价格ID
				taxRateCode: r.data.taxrateCode,
				buyerTime: r.get("purchasingRequisitionCollection.demandDate"),
				vendorTime: r.get("purchasingRequisitionCollection.demandDate"),
				storeLocal: null,
				scheduleFlag: 0,
				emergencyFlag: 0,
				closeFlag: 0,
				deleteFlag: 0,
				isReturn: 0,
				isFree: 0,
				stockType: '',
				buyerQty: r.get("transferQuantity"),
				stockType: 'A',
				purchasingRequisitionColId: r.data.purchaseRequisitionTransId,
				pricingUnit: r.data.purchasingRequisitionCollection.unitCode,
				buyerPrice: Ext.util.Format.number(parseFloat(r.data.price), '0.00'),
				lineItemValAmt: Ext.util.Format.number(amount, '0.00')
					// 采购申请明细转单ID
			});
			arr.push(data);
			
		});
		if(delGrid.getStore().getCount()>0){
		 delGrid.getStore().removeAll();
		}
		editWin.addDetail(delGrid, arr);
		//设置订单类型和货币编码为可编辑 防止插入细单导致主单无法编辑
		form.findField("model.purchasingOrgCode").setReadOnly(true);
	    form.findField("model.companyCode").setReadOnly(true);
        form.findField("model.purchaseOrderType").setReadOnly(false);
        form.findField("model.currencyCode").setReadOnly(false);
		// 计算数量、定价、价格
//		win.countTotalAmout();
        win.countPrice();
		return editWin;
	},
	/**
	 * @method countPriceTrans 其他模块跳转设置值：依次调用计算数量，计算定价条件金额，计算订单明细金额方法
	 * @param {Object} editWin 订单编辑界面
	 * @param {Object} detailGrid 订单明细表格对象
	 * @param {Object} pricingGrid 订单明细的明细价格条件表格对象
	 */
	countPriceTrans: function(editWin, detailGrid, pricingGrid) {
		this.countQuantityTrans(editWin, detailGrid, pricingGrid);
		this.countPricingAmountTrans(editWin, detailGrid, pricingGrid);
		this.countItemAmountTrans(editWin, detailGrid, pricingGrid);
		this.countTotalAmoutTrans(editWin, detailGrid, pricingGrid);
	},
	/**
	 * 
	 * @method countQuantityTrans 根据订单单位和定价单位、SKU的转换关系，计算定价单位数量和SKU数量
	 * @param {Object} editWin 订单编辑界面
	 * @param {Object} detailGrid 订单明细表格对象
	 * @param {Object} pricingGrid 订单明细的明细价格条件表格对象
	 */
	countQuantityTrans: function(editWin, detailGrid, pricingGrid) {
		var selecteds = detailGrid.getSelectionModel().getSelection();
		if (selecteds.length != 1) {
			return;
		}
		// 获取转换关系
		var quantityPanel = editWin.query('form[name=quantityPanel]')[0];
		var quantityForm = quantityPanel.getForm();
		var formValues = quantityForm.getValues();
		// 手动触发存入缓存的方法
		var fields = quantityPanel.items.items;
		Ext.each(fields, function(field) {
			if ('fieldset' != field.xtype) {
				if (field.getName() == 'purchaseOrderQtyId') {
					return true;
				}
				field.fireEvent('change', field, field.getValue(), '');
			}
		});

		var r = selecteds[0];
		var qty = r.get('buyerQty');
		// 订单单位
		var orderDetailUnit = formValues['convertMolecular'];
		// 订价单位
		var pricingUnit = formValues['convertDenominator2'];
		// sku
		var sku = formValues['convertDenominator'];
		if (isNaN(qty) || isNaN(orderDetailUnit) || isNaN(pricingUnit) || isNaN(sku)) {
			return;
		}
		// 订价单位数量=订价单位/订单单位*订单数量
		var pricingQty = pricingUnit / orderDetailUnit * qty;
		// sku数量=sku/订单单位*订单数量
		var skuQty = sku / orderDetailUnit * qty;
		quantityForm.findField('pricingQty').setValue(pricingQty);
		quantityForm.findField('skuQty').setValue(skuQty);
		// 手动触发存入缓存的方法
		Ext.each(fields, function(field) {
			if ('fieldset' != field.xtype) {
				if (field.getName() == 'purchaseOrderQtyId') {
					return true;
				}
				field.fireEvent('change', field, field.getValue(), '');
			}
		});
	},
	/**
	 * 
	 * @method countTotalAmoutTrans 计算总金额
	 * @param {Object} editWin 订单编辑界面
	 * @param {Object} detailGrid 订单明细表格对象
	 * @param {Object} pricingGrid 订单明细的明细价格条件表格对象
	 */
	countTotalAmoutTrans: function(editWin, detailGrid, pricingGrid) {
		var totalAmount = 0;
		var form = editWin.formPanel.getForm()
		var store = detailGrid.getStore();

		store.each(function(r) {
			var deleteFlag = r.get('deleteFlag');
			var lineItemValAmt = r.get('lineItemValAmt');
			if (deleteFlag == 0 && !Ext.isEmpty(lineItemValAmt)) {
				totalAmount += parseFloat(r.get('lineItemValAmt'));
			}
		});
		form.findField('model.totalAmount').setValue(Ext.util.Format.number(totalAmount, '0.0000'));
	},

	/**
	 * @method countItemAmountTrans 计算行金额和价格
	 * @param {Object} editWin 订单编辑界面
	 * @param {Object} detailGrid 订单明细表格对象
	 * @param {Object} pricingGrid 订单明细的明细价格条件表格对象
	 */
	countItemAmountTrans: function(editWin, detailGrid, pricingGrid) {
		var selecteds = detailGrid.getSelectionModel().getSelection();
		if (selecteds.length != 1) {
			return;
		}
		var selected = selecteds[0];
		var quantityForm = editWin.query('form[name=quantityPanel]')[0].getForm();
		var pricingAmount = 0;
		var priceUnit = 0;
		var lineItemTypeCode = selected.get('lineItemTypeCode');
		var isFree = 0;
		// 计算行金额
		if (0 < pricingGrid.store.getCount()) {
			var pricing = pricingGrid.getStore().getAt(0);
			pricingAmount = pricing.get('amount');
			priceUnit = pricing.get('priceUnit');
		}
		selected.set('lineItemValAmt', Ext.util.Format.number(pricingAmount, '0.00'));
		// 计算价格
		var buyerPrice = pricingAmount / quantityForm.findField('pricingQty').getValue() * priceUnit;
		selected.set('buyerPrice', Ext.util.Format.number(buyerPrice, '0.00'));
		// 如果行金额为0，则免费标识置为是
		if (2 != lineItemTypeCode && 0 == pricingAmount) {
			isFree = 1;
		}
		selected.set('isFree', isFree);
	},

	/**
	 * @method countPricingAmountTrans 计算定价条件金额
	 * @param {Object} editWin 订单编辑界面
	 * @param {Object} detailGrid 订单明细表格对象
	 * @param {Object} pricingGrid 订单明细的明细价格条件表格对象
	 */
	countPricingAmountTrans: function(editWin, detailGrid, pricingGrid) {
		var selecteds = detailGrid.getSelectionModel().getSelection();
		if (0 == pricingGrid.store.getCount() || 1 != selecteds.length) {
			return;
		}
		var selected = selecteds[0];
		var pricing = pricingGrid.getStore().getAt(0);
		var recordType = selected.get('lineItemTypeCode');
		// 获取当前行定价
		var pricingPrice = pricing.get('pricingQty');
		// 获取当前行价格单位
		var priceUnit = pricing.get('priceUnit');
		// 数量标签
		var quantityForm = editWin.query('form[name=quantityPanel]')[0].getForm();
		// 获取定价单位数量
		var pricingQty = quantityForm.findField('pricingQty').getValue();
		var amount = 0;
		// 寄售
		if (2 == recordType) {
			pricing.set('pricingQty', Ext.util.Format.number(0, '0.00'));
		}
		if (!Ext.isEmpty(pricingQty) && !Ext.isEmpty(pricingPrice) && !Ext.isEmpty(priceUnit)) {
			amount = pricingQty * pricingPrice / priceUnit;
		}
		pricing.set('amount', amount);
		pricingGrid.getView().refresh();
	},


	/**
	 * @method addAfter 新增订单前执行方法，设置关闭、取消关闭按钮不可用
	 */
	addAfter: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = me.getDetailGrid();
		var form = vp.editWin.formPanel.getForm();
		var tbar = grid.getTopToolbar();
		var closeBtn = tbar.query('button[name=closeBtn]')[0];
		var cancelCloseBtn = tbar.query('button[name=cancelCloseBtn]')[0];
		closeBtn.setDisabled(true);
		cancelCloseBtn.setDisabled(true);
		
		// 清除定价明细表单
		var pricingGrid = me.getPricingGrid();
		var store = pricingGrid.getStore();
		store.removeAll();
		// 双单位转换panel
		var quantityPanel = me.getQuantityPanel();
	    var quantityForm = quantityPanel.getForm();
	    quantityForm.reset();
	    var purchasingOrgStore = viewModel.getStore('purchasingOrgStore');
	    if(purchasingOrgStore.getCount() == 1){
	    	var record = purchasingOrgStore.getAt(0);
	    	form.findField('model.purchasingOrgCode').setValue(record.get('purchasingOrgCode'));
			form.findField('model.purchasingOrgName').setValue(record.get('purchasingOrgName'));
	   		var companyStore = viewModel.getStore('companyStore');
	   		companyStore.proxy.extraParams.filter_EQ_purchasingOrgCode = record.get('purchasingOrgCode');
			companyStore.load({
				callback:function(records){
					form.findField('model.companyCode').reset("");
					form.findField('model.companyName').reset("")
					if(companyStore.totalCount ==1 ){
						form.findField('model.companyCode').setValue(companyStore.data.items[0].get('companyCode'));
						form.findField('model.companyName').setValue(companyStore.data.items[0].get('companyName'));
					}
				}
			});
	    }
	    
	    var purchaseGroupStore = viewModel.getStore('purchaseGroupStore');
	    if(purchaseGroupStore.getCount() == 1){
	    	var record = purchaseGroupStore.getAt(0);
	    	form.findField('model.purchasingGroupCode').setValue(record.get('purchasingGroupCode'));
			form.findField('model.purchasingGroupName').setValue(record.get('purchasingGroupName'));
	    }
	    
	},

	/**
     *  点击下载、删除附件处理
     *  @params  _self 本身
     *  @params  {Object} operations 操作
     *        eg.{ 
     *               "delete":true,//是否允许删除操作
     *               "download":true,//是否允许下载操作
     *         } 
     *  @params  {Object} operationsInfo 操作提示信息
     *        eg.{ 
     *               "delete":{"type":"tips","msg":"xxxxx"},//删除操作提醒信息
     *               "download":{"type":"confirm","msg":"xxxxx"}//下载操作提醒信息
     *         } 
     *  @params  { } me 编辑窗口
     */
    operation:function(_self , operations, operationsInfo){
        try{ 
            operations["delete"] = true;
            operationsInfo["delete"] = {};
            operationsInfo["delete"]["type"] = "tips";
            operationsInfo["delete"]["msg"] = "无权限";
        }catch(e){
            console.log(e);
        }
    },
	/**货币字段*/
	vendorErpCodeSetValueAfter:function(_self,fieldselect,parentObj,grid,parentType){
 		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var vendorCode = _self.value;
		if(vendorCode != ""){
			Ext.Ajax.request({
				url: path_srm + "/cp/purchaseorder/findvendor",
				method: 'POST',
				params: {
					filter_EQ_vendorErpCode : vendorCode
				},
				success: function(response) {
					var json = Ext.decode(response.responseText);
					if(json != ""){
						form.findField('model.vendorCode').setValue(json.vendorCode);
						form.findField('model.vendorErpCode').setValue(json.vendorErpCode);
						form.findField('model.vendorName').setValue(json.vendorName);
						form.findField('taxRateCode').setValue(json.taxCode); // 税率编码
						//初始化货币编码
						var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
						var vendorCode = json.vendorCode;
						var vendorPorgDtlStore = viewModel.getStore('vendorPorgDtlStore');
						vendorPorgDtlStore.proxy.extraParams.filter_EQ_vendor_vendorCode = vendorCode;
						vendorPorgDtlStore.proxy.extraParams.filter_EQ_purchasingOrgCode = purchasingOrgCode;
						vendorPorgDtlStore.load(function() {
							if (vendorPorgDtlStore.getCount() >= 1) {
								var currencyCode = vendorPorgDtlStore.getAt(0).get("currencyCode");
								// 加载汇率
								me.findCurrencyRate(vendorCode, purchasingOrgCode, currencyCode);
								form.findField("model.currencyCode").setValue(currencyCode);
							}
						});
					}else{
						Q.tips($('message.vendorNotExit'),'E');
						form.findField('model.vendorErpCode').setValue("");
						form.findField('model.vendorName').setValue("");
						return;
					}
				},
				failure: function(resp, opt) {
					Q.tips($('message.operator.failure'), 'E');
				}
			});
		}

 	},
 /**
	 * 供应商过滤采购组织
	 * @param {} _self
	 * @param {} baseParams
	 * @param {} parentObj
	 */
	vendorErpCodeTriggerBaseParams : function(_self, baseParams, parentObj) {
		try{
			var viewModel = this.getViewModel()
			var form = viewModel.getEditWin().formPanel.getForm();
			var purchasingOrgCode = form.findField("model.purchasingOrgCode").getValue();// 采购组织
			baseParams.purchasingOrgCode = purchasingOrgCode;
			baseParams.filter_IN_certificationStatus = "QUALIFIED";
			baseParams.filter_EQ_stopFlag = 0;
			baseParams.filter_EQ_erpSynState = 1;
		} catch(e) {
            console.log(e);
        }
	},
	/**审核按钮控制权限*/
	vpClickAfter:function(){
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var selected = vp.grid.getSelection();
		var grant=vp.grid.getTopToolbar().find("name","grant")[0];
		var check=vp.grid.getTopToolbar().find("name","revokeaudit")[0];
		var prompttrial=vp.grid.getTopToolbar().find("name","prompttrial")[0];
		if(selected[0].get("purchaseOrderFlowState")=="CONFIRM"){
			if(grant!=null){
				grant.setDisabled(false);
			}
			if(prompttrial!=null){
				prompttrial.setDisabled(false);
			}
			if(check!=null){
			   	check.setDisabled(false);
			  	if(selected[0].get("createUserId") != s_userId){
		       		check.setDisabled(true);
		       	}
		        else{
		       		check.setDisabled(false);
		       	}
	     	}
	 	} else{
			if(grant!=null){
				grant.setDisabled(true);
			}
			if(check!=null){
				check.setDisabled(true);
			}
			if(prompttrial!=null){
				prompttrial.setDisabled(true);
			}
		}
	},
	/**采购申请转单处理*/
    vpAfterRender: function () {
    	 var me = this;
    	 var viewModel = me.getViewModel();
    	 viewModel.getEditWin().formPanel.getForm().findField("model.purchasingOrgCode").getStore().load();
    	 if(viewModel.get("recordId") instanceof Object){
    	 	me.testFn(viewModel.get("recordId"))
    	 	return viewModel.getVp().customAddBtn(function(editWin,formPanel){
    	 		formPanel.getForm().findField("model.purchasingOrgCode").setReadOnly(true);
	       		formPanel.getForm().findField("model.companyCode").setReadOnly(true);
    	 	},null);
			//return me.testFn(viewModel.get("recordId"));
    	 	
    	 }
         return 'view';
    },
    testFn:function(recordId,formPanel){
    	
    	  var me = this;
		  var viewModel = me.getViewModel();
		  
		  console.info(viewModel);
		  
		  var editWin = viewModel.getEditWin();
          var formPanel =editWin.formPanel;
	      var form = editWin.formPanel.getForm();
	      
		  form.findField("model.companyCode").setValue(viewModel.get("recordId").data.companyCode);
		  form.findField("model.companyName").setValue(viewModel.get("recordId").data.companyName);
		  form.findField("model.vendorCode").setValue(viewModel.get("recordId").data.vendorCode);
		  form.findField("model.vendorErpCode").setValue(viewModel.get("recordId").data.vendorErpCode);
		  form.findField("model.vendorName").setValue(viewModel.get("recordId").data.vendorName);
		  form.findField("model.purchasingOrgCode").getStore().load();
		  form.findField("model.purchasingOrgCode").setValue(viewModel.get("recordId").data.purchasingOrgCode);
		  form.findField("model.purchasingOrgName").setValue(viewModel.get("recordId").data.purchasingOrgName);
		  //设置表单可编辑
		   form.findField("model.purchaseOrderType").setReadOnly(false);
	       form.findField("model.currencyCode").setReadOnly(false);
           var pricingType= me.pricingTypeInit();
           if(pricingType.getAt(0) == null){
				pricingType.load(function(){
					 me.setEditWinValues(editWin, viewModel.get("recordId"), viewModel.get("recordId").delSelects,pricingType);
				});
		   }else{
			   me.setEditWinValues(editWin, viewModel.get("recordId"), viewModel.get("recordId").delSelects,pricingType);
		   }
    },
    /*初始化定价条件*/
    pricingTypeInit:function(){
       var me = this;
	   var viewModel = me.getViewModel();
	   var pricingConditionTypeStore = viewModel.getStore('pricingConditionTypeStore');
       return pricingConditionTypeStore.load();
       
    },
    /**
     * 查看按钮点击后处理方法
     * @param {Object} grid 列表grid
     * @param {Object} win 编辑窗口
     */
    viewAfter: function (grid, win) {
    	var me = this;
    	var viewModel = me.getViewModel();
        var form = viewModel.getEditWin().formPanel.getForm();
        var billTypeCode = viewModel.get("vp_billTypeCode");
        var key = grid.key;
        // 获取审核明细
        var comment = viewModel.getEditWin().getCompByTabClassName('dealComments');
        var commentStore = comment.getStore();
        commentStore.proxy.extraParams={businessKey: key,processKey:billTypeCode};
        commentStore.reload();
        
        // 获取操作日志
        var operationLog = viewModel.getEditWin().getCompByTabClassName('operationLog');
        var logStore = operationLog.getStore();
        logStore.proxy.extraParams = {
        	 "filter_EQ_bizkey": key,
			 "filter_IN_module": billTypeCode,
			 limit : 20,
			 start : 0
        };
        logStore.reload();
    },
    dealMsgRenderer: function(t, r, a) {
    	var me = this;
        var o = r.record.id + "_tab";
        if (null != t) {
            var s = {
                moduleId: me.getViewModel().moduleId,
                uploadFileGroupId: t
            };
            return Ext.defer(function() {
                Ext.widget("button", {
                    renderTo: o,
                    text: $("label.ClickToDownload"),
                    ui: "blue-btn",
                    margin: "0 10 0 10",
                    width: 100,
                    height: 30,
                    handler: function() {
                        var e = new Ext.comm.FileDownLoad(s);
                        e.show()
                    }
                })
            }, 100),
            Ext.String.format('<div id="{0}"></div>', o)
        }
        return $("wx.null")
    },
    /**
     *  请求前事件
     *  _self 本身 
     *  baseParams 请求前参数 
     *  parentObj 父类
     */
    companyCodeTriggerbaseparams:function(_self,baseParams,parentObj){
        try{
           var me = this;
           var vm = me.getViewModel();
           var editWin = vm.getEditWin();
           var form = editWin.formPanel.getForm();
           var purchasingOrgCode = form.findField("model.purchasingOrgCode").getValue();
           //新增请求参数
           baseParams.filter_EQ_purchasingOrgCode = purchasingOrgCode;
        }catch(e){
            console.log(e);
        }
    }
 });