/**
 * @class {Cp.delivery.DeliveryController} 
 * @extend {Ext.ux.app.ViewController}
 * 送货管理控制类
 */
Ext.define('Cp.delivery.DeliveryController', {
	//extend: 'Ext.ux.app.ViewController',
	extend: 'Ext.srm.app.ViewController',
	alias: 'viewmodel.deliveryController',

	/**
	 * @method vpAfterRender
	 * 窗体加载后渲染
	 */
	vpAfterRender: function() {
		return "view";
	},
	
	
	/**
	 * @method searchWinSearch
	 * 查询窗体查询事件
	 */
	searchWinSearch: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		vp.searchFlag = true;
	},

	/**
	 * @method gridStoreBeforeLoad
	 * 列表加载前事件
	 */
	gridStoreBeforeLoad: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		if (vp.searchFlag && undefined != vp.searchWin) {
			var params = vp.searchWin.formPanel.form.getValues();
			vp.grid.store.proxy.extraParams = params;
			if (playListMode == "undeal") {
				vp.grid.store.proxy.extraParams.billFlag = 'undeal';
			}
			vp.searchFlag = false;
		}
		    	
	},

	/**
	 * @method gridStoreLoad
	 * 列表加载事件
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
	 * @method gridDeliveryTypesRenderer
	 * 送货类型状态渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDeliveryTypesRenderer: function(value, metaDatam, record) {
		var me = this;
		var viewModel = me.getViewModel();

		var index = viewModel.getStore('deliveryTypeStore').find('itemCode', value);
		if (index > -1) {
			return viewModel.getStore('deliveryTypeStore').getAt(index).get('itemName');
		} else {
			return value;
		}
	},

	/**
	 * @method vpCancleHandler
	 * 列表界面ERP导入按钮方法
	 */
	vpCancleHandler: function() {
		var vp = this.getViewModel().getVp();
		var grid = vp.grid;
		var selections = grid.getSelectionModel().getSelection();

		if (selections.length != 1) {
			Q.tips($('comm.pleaseSelect'), 'E'); //'请选择一条信息!'
			return;
		}

		var id = selections[0].get('deliveryId');

		Q.confirm($('message.cancel.confirm'), {
			renderTo:vp.id,
			ok: function() {
				Ext.getBody().mask();
				Ext.Ajax.request({
					url: path_srm + '/cp/delivery/cancel',
					params: {
						'id': id
					},
					success: function(response) {
						var json = Ext.decode(response.responseText);
						if (false === json.success) { // grid.moduleName+'删除失败！未知系统异常！
							Q.error(json.info || $('message.refuse.failure') + '<br/><br/>' + $('message.system.error'));
							return;
						}
						Q.tips('<font color="blue">' + $('message.cancel.success') + '</font>');
						grid.getStore().reload();
						grid.getSelectionModel().clearSelections();
					},
					failure: function(response) {
						Q.error($('message.cancel.failure') + '<br/><br/>' + $('message.system.disconnect'));
					},
					callback: function() {
						Ext.getBody().unmask();
					}
				});
			}
		});
	},

	/**
	 * @method vpInstanceAfter
	 * 窗体实例化之后
	 */
	vpInstanceAfter: function() {
		var me = this;
		window.closeFunction = function(id){
			me.closeFunction(id);
		} 
		window.cancelFunction = function(id){
			me.cancelFunction(id);
		}
		window.canceldetail = power['canceldetail'];
		window.closedetail = power['closedetail'];
		console.log('vpInstanceAfter');	
		
	},

	/**
	 * @method vpExportHandler
	 * 列表界面导出按钮方法
	 */
	vpExportHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = vp.grid;
		var selections = grid.getSelectionModel().getSelection();
		if (selections.length != 1) {
			Q.tips($('comm.pleaseSelect'), 'E'); //'请选择一条信息!'
			return;
		}
		var id = selections[0].get('deliveryId');
		var baseParams = grid.getStore().proxy.extraParams;
		baseParams.id = id;
		var url = viewModel.get('dealUrl') + '/export?reportFileType=PDF&' + Ext.urlEncode(baseParams);
		//window.open(url);
		Ext.UxFile.fileDown(url,"送货单.pdf",null);

	},

	/**
	 * @method vpSynerpHandler
	 * 列表界面同步按钮方法
	 */
	vpSynerpHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = vp.grid;
		var selectids = grid.getSelectionModel().getSelection();

		if (selectids.length != 1) {
			Q.tips($('message.synerp.select'), 'E');
			return;
		}

		var id = selectids[0].data.deliveryId;

		Q.confirm($('message.erpSyn.confirm'), {
			renderTo:vp.id,
			ok: function() {
				Ext.getBody().mask();
				Ext.Ajax.request({
					url: path_srm + '/cp/delivery/syncerp',
					params: {
						'id': id
					},
					success: function(response) {
						var json = Ext.decode(response.responseText);
						if (json.success == false) {
							Q.error(json.info || $('message.erpSyn.synfailure') + '<br/><br/>' + $('message.system.error'));
							return;
						}
						Q.tips('<font color="blue">' + $('message.erpSyn.synsuccess') + '</font>');
						if (!Ext.isEmpty(grid)) {
							grid.getStore().reload();
							grid.getSelectionModel().clearSelections();
						} else {
							vp.editWin.resetWin();
							vp.editWin.hide();
							vp.editWin.fireEvent('sumbit');
						}
					},
					failure: function(response) { // grid.moduleName+'删除失败！请检查与服务器的连接是否正常，或稍候再试！
						Q.error($('message.erpSyn.synfailure') + '<br/><br/>' + $('message.system.disconnect'));
					},
					callback: function() {
						Ext.getBody().unmask();
					}
				});
			}
		});
	},

	/**
	 * @method formPurchasingOrgCodeTrigger
	 * 表单采购组织获取焦点事件
	 * @param {Ext.form.field.ComboBox} combo 当前对象
	 */
	formPurchasingOrgCodeTrigger: function(_self,baseParams,parentObj){
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (-1 < s_roleTypes.indexOf('V')) {
			//供应商编辑或者新建时，选择供应商所属的采购组织
			var purchasingOrgCodes = [];
			var store = viewModel.getStore('purchasingOrgByVendorStore');
			store.each(function(record,indx){
				purchasingOrgCodes.push(record.get('purchasingOrgCode'));
			})
			baseParams.filter_IN_purchasingOrgCode = purchasingOrgCodes;
		}
	},

	/**
	 * @method formPurchasingOrgCodeFocus
	 * 表单采购组织获取焦点事件
	 * @param {Ext.form.field.ComboBox} combo 当前对象
	 */
	formPurchasingOrgCodeFocus: function(combo) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var vendorCode = form.findField("model.vendorCode").getValue();
		//if (vendorCode != null && vendorCode != "") {
		if (-1 != s_roleTypes.indexOf('V')) {
			//供应商编辑或者新建时，选择供应商所属的采购组织
			var store = combo.getStore();
			store.removeAll();
			store.add(viewModel.getStore('vendorStore').getRange());
		}
	},

	/**
	 * @method formPurchasingOrgCodeSelect
	 * 表单采购组织选中事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formPurchasingOrgCodeSelect: function(field,record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var oldValue = form.findField('model.purchasingOrgCode').getValue();
		
		form.findField('model.purchasingOrgCode').setValue(record.get('purchasingOrgCode'));
		form.findField('model.purchasingOrgName').setValue(record.get('purchasingOrgName'));

		viewModel.getStore('plantStore').proxy.extraParams = {
			'filter_EQ_purchasingOrgCode': record.get('purchasingOrgCode')
		};
		
		viewModel.getStore('plantStore').load(function(){
			if(viewModel.getStore('plantStore').getCount() == 1){
				form.findField('model.plantCode').setValue(viewModel.getStore('plantStore').getAt(0).get("plantCode"));
				form.findField('model.plantName').setValue(viewModel.getStore('plantStore').getAt(0).get("plantName"));
				
				viewModel.getStore('stockLocationStore').proxy.extraParams = {
					'filter_EQ_plantCode': viewModel.getStore('plantStore').getAt(0).get("plantCode")
				};
				viewModel.getStore('stockLocationStore').load(function(){
					if(viewModel.getStore('stockLocationStore').getCount() == 1){
						form.findField('model.storageLocationCode').setValue(viewModel.getStore('stockLocationStore').getAt(0).get("stockLocationCode"));
						form.findField('model.storageLocationName').setValue(viewModel.getStore('stockLocationStore').getAt(0).get("stockLocationName"));
					}
				});
			}
		});
	},

	/**
	 * @method formPurchasingOrgCodeBeforeselect
	 * 表单采购组织选择前事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formPurchasingOrgCodeBeforeselect: function(field,record){
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var gridPanel = me.getDetailGrid();
		var oldValue = field.getValue();
		var newValue =viewModel.getStore("formPurchasingOrgCodeBeforeselect");

		if (oldValue != null && oldValue != newValue) {
			if (gridPanel.getStore().getCount() > 0) {
				// 提示 重新选择采购组织将清空库存地点，工厂信息和明细列表，是否修改
				Q.confirm($('shoppingnotice.tip.msg') + '?', function(button) {
					if (button == 'ok') {
						form.findField('model.purchasingOrgName').setValue(record.get('purchasingOrgName'));
						gridPanel.getStore().removeAll(); // 清空细单
						gridPanel.getView().refresh(); // 刷新视图
						form.findField('model.plantCode').reset();
						form.findField('model.plantName').reset();
						form.findField('model.storageLocationCode').reset();
						form.findField('model.storageLocationName').reset();
					}
				});
				return false;
			} else {
				form.findField('model.purchasingOrgName').setValue(record.get('purchasingOrgName'));
				gridPanel.getStore().removeAll();
				gridPanel.getView().refresh();
				form.findField('model.storageLocationCode').reset();
				form.findField('model.storageLocationName').reset();
				form.findField('model.plantCode').reset();
				form.findField('model.plantName').reset();
			}
		}
	},
	
	/**
	 * @method formPurchasingOrgCodeClear
	 * 表单采购组织清除事件
	 */
	formPurchasingOrgCodeClear: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var gridPanel = me.getDetailGrid();
		gridPanel.getStore().removeAll();
		gridPanel.getView().refresh();
		
		if (-1 == s_roleTypes.indexOf('V')) {
			form.findField('model.vendorCode').reset();
			form.findField('model.vendorName').reset();
		}
		
		form.findField('model.purchasingOrgName').reset();
		form.findField('model.storageLocationCode').reset();
		form.findField('model.storageLocationName').reset();
		form.findField('model.plantCode').reset();
		form.findField('model.plantName').reset();
		viewModel.getStore('stockLocationStore').removeAll();
		viewModel.getStore('plantStore').removeAll();
	},

	/**
	 * @method formVendorCodeTrigger
	 * 表单供应商触发事件
	 * @param {Ext.field.Field} field 当前field对象
	 */
	formVendorCodeTrigger: function(field) {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();

		if (Ext.isEmpty(purchasingOrgCode)) {
			Q.tips($('message.pleaseSelectPurchasingOrgCode'), 'E');
			return;
		}

		if (field.disabled == false) {
			var vm = this.getView();
			var selectWin = new Sl.masterdata.VendorSelectWin({
				singleSelect: true,
				model:true,
				moduleId:vm.id,
				baseParams: {
					filter_IN_certificationStatus: 'QUALIFIED', //合格供应商
					purchasingOrgCode:purchasingOrgCode
				},
				baseParamsTree: {
					certificationStatus: 'QUALIFIED' //合格供应商
				},
				select: function(g, r) {
					var form = vp.editWin.formPanel.getForm();
					form.findField('model.vendorErpCode').setValue(r.get('vendorErpCode'));
					form.findField('model.vendorCode').setValue(r.get('vendorCode'));
					form.findField('model.vendorName').setValue(r.get('vendorName'));
				}
			});
			selectWin.show();
		}
	},
	
	/**
	 * @method formVendorCodeClear
	 * 表单供应商清除事件
	 */
	formVendorCodeClear: function(field) {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.vendorCode').reset();
		form.findField('model.vendorName').reset();
	},

	/**
	 * @method formPlantCodeSelect
	 * 表单工厂选择事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formPlantCodeSelect: function(field,record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.plantCode').setValue(record.get('plantCode'));
		form.findField('model.plantName').setValue(record.get('plantName'));
		form.findField('model.storageLocationName').reset();
		form.findField('model.storageLocationCode').reset();
		
		var plantCode = record.get('plantCode');
		viewModel.getStore('stockLocationStore').proxy.extraParams = {
			'filter_EQ_plantCode': plantCode
		};
		viewModel.getStore('stockLocationStore').load(function(){
			if(viewModel.getStore('stockLocationStore').getCount() == 1){
				form.findField('model.storageLocationCode').setValue(viewModel.getStore('stockLocationStore').getAt(0).get("stockLocationCode"));
				form.findField('model.storageLocationName').setValue(viewModel.getStore('stockLocationStore').getAt(0).get("stockLocationName"));
			}
		});
	},


	/**
	 * @method formPlantCodeBeforeselect
	 * 表单工厂选择前事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formPlantCodeBeforeselect: function(combo, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var storeLocalCode = form.findField('model.storeLocalCode');
		var gridPanel = me.getDetailGrid();
		var oldValue = combo.getValue();
		var newValue = record.get('plantCode');
		if (oldValue != newValue&&oldValue!=null) {
			if (gridPanel.getStore().getCount() > 0) {
				Q.confirm($('shoppingnotice.tip.msg1') + '?', {
					ok: function() {
						form.findField('model.plantCode').setValue(record.get('plantCode'));
						gridPanel.getStore().removeAll();
						gridPanel.getView().refresh();

						if (storeLocalCode != null) {
							form.findField('model.storeLocalCode').reset();
							form.findField('model.storeLocalName').reset();
						}

					}
				});
				return false;
			} else {
				form.findField('model.plantCode').setValue(record.get('plantCode'));
				gridPanel.getStore().removeAll();
				gridPanel.getView().refresh();
				if (storeLocalCode != null) {
					form.findField('model.storageLocationCode').reset();
					form.findField('model.storageLocationName').reset();
				}

			}
		}else{
			form.findField('model.plantCode').setValue(record.get('plantCode'));
		}
	},
	
	/**
	 * @method formPlantCodeClear
	 * 表单工厂清除事件
	 */
	formPlantCodeClear: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var gridPanel = me.getDetailGrid();
		gridPanel.getStore().removeAll();
		gridPanel.getView().refresh();
		viewModel.getStore('stockLocationStore').removeAll();
		form.findField('model.plantName').reset();
		form.findField('model.storageLocationCode').reset();
		form.findField('model.storageLocationName').reset();
	},

	/**
	 * @method formStockLocationCodeSelect
	 * 表单库存选择事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formStockLocationCodeSelect: function(combo, record) {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.storageLocationCode').setValue(record.get('stockLocationCode'));
		form.findField('model.storageLocationName').setValue(record.get('stockLocationName'));
	},
	
	/**
	 * @method formStockLocationCodeClear
	 * 表单库存清除事件
	 */
	formStockLocationCodeClear: function() {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.storageLocationName').reset();
		form.findField('model.storageLocationCode').reset();
	},

	/**
	 * @method formDeliveryTypesSelect
	 * 表单送货类型选择事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Ext.data.Model} record 所选中的记录
	 */
	formDeliveryTypesSelect: function(combo, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var value = record.get('itemCode');
		var trackingNumber = vp.editWin.formPanel.getForm().findField('model.trackingNumber');
		var serviceDate = vp.editWin.formPanel.getForm().findField('model.serviceDate');

		combo.setValue(record.get('itemCode'));

		if ((value == '2' || value == '3') && Ext.isEmpty(trackingNumber.getValue())) {
			trackingNumber.validator = function() {
				return $('shoppingnotice.validator.msg') + '!'; // 快递或托运，快递信息必填
			};
			trackingNumber.validator();
			serviceDate.validator = function() {
				return true;
			};
			serviceDate.validator();
		} else if (value == '1' && Ext.isEmpty(serviceDate.getValue())) {
			trackingNumber.validator = function() {
				return true;
			};
			trackingNumber.validator();
			serviceDate.validator = function() {
				return '送达日期必填!'; // 送达日期必填
			};
			serviceDate.validator();
		} else {
			trackingNumber.validator = function() {
				return true;
			};
			trackingNumber.validator();

			serviceDate.validator = function() {
				return true;
			};
			serviceDate.validator();
		}
	},


	/**
	 * @method formTrackingNumberChange
	 * 快递单号改变事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Object} value 改变后的值
	 * @param {Object} oldValue 改变之前的值
	 */
	formTrackingNumberChange: function(combo, value, oldValue) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var deliveryTypes = form.findField('model.deliveryTypes').getValue();

		if (Ext.isEmpty(value) && (deliveryTypes == '2' || deliveryTypes == '3')) {
			combo.validator = function() {
				return $('shoppingnotice.validator.msg') + '!'; // 快递或托运，快递信息必填
			};
			combo.validator();
		} else {
			combo.validator = function() {
				return true;
			};
			combo.validator();
		}
	},

	/**
	 * @method formServiceDateChange
	 * 送达日期改变事件
	 * @param {Ext.form.field.ComboBox} combo 当前field对象
	 * @param {Object} value 改变后的值
	 * @param {Object} oldValue 改变之前的值
	 */
	formServiceDateChange: function(combo, value) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		combo.validator = function() {
			return true;
		};
		combo.validator();
		var deliveryDate = form.findField('model.deliveryDate').getValue();
		if (!Ext.isEmpty(value) && (!Ext.isEmpty(deliveryDate)) && value < deliveryDate) {
			Q.tips("送达日期必须大于送货日期！","E");
			combo.reset();
		}
	},

	/**
	 * @method gridDtlEdit
	 * 送货管理明细编辑事件
	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
	 * @param {Object} content 具有以下属性的编辑上下文
	 *   @param {Ext.grid.Panel} content.content 所编辑的表格对象
	 *   @param {Ext.data.Model} content.record 正在编辑的记录
	 *   @param {String} content.field 正在编辑的字段名称
	 *   @param {Mixed} content.value 字段当前值
	 *   @param {HTMLElement} content.row 正在编辑的行html元素
	 *   @param {Ext.grid.column.Column} content.column 正在编辑的列
	 *   @param {Number} content.rowIdx 正在编辑的行序列
	 *   @param {Number} content.colIdx 正在编辑的列序列.
	 */
	gridDtlEdit: function(editor, content) {
		var me = this;
		//获取当前行
		var record = content.record;
		//表格
		var grid = content.grid;

		if (content.column.dataIndex == 'deliveryNumber') {
			var deliveryNumber = Number(record.get('deliveryNumber'));
			var orderNumber = Number(record.get('orderNumber'));
			// 填写的数量不能超过（可送货量+订单量*交货过量限度）
			var limitNumber = Number(record.get('canSentNumber')) +
				Number(orderNumber * Number(record.get('overDeliveryLimit')) / 100);
			if (deliveryNumber > limitNumber) {
				Q.tips($('shoppingnoticedetail.tip.msg1')); // '送货数量不允许超过....'
				record.set('deliveryNumber', null);
			}
		}
	},
	/**
	 * @method expressDtlEdit
	 * 快递公司明细编辑时间
	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
	 * @param {Object} content 具有以下属性的编辑上下文
	 *   @param {Ext.grid.Panel} content.content 所编辑的表格对象
	 *   @param {Ext.data.Model} content.record 正在编辑的记录
	 *   @param {String} content.field 正在编辑的字段名称
	 *   @param {Mixed} content.value 字段当前值
	 *   @param {HTMLElement} content.row 正在编辑的行html元素
	 *   @param {Ext.grid.column.Column} content.column 正在编辑的列
	 *   @param {Number} content.rowIdx 正在编辑的行序列
	 *   @param {Number} content.colIdx 正在编辑的列序列.
	 */
	expressDtlEdit: function(editor, content) {
		var me = this;
		var field = content.field;
		var grid = content.grid;
		var record = content.record;
		var value = content.value;

		if(field == "expressNo"){// 如果编辑的是单号，调用快递100接口
			Ext.Ajax.request({
				url: path_srm + '/cp/delivery/getexpresscompanybyno',
				params: {
					'expressNo': value
				},
				success: function(response) {
					var json = Ext.decode(response.responseText);
					console.log(json);
					record.set("expressCompanyCode", json.expressCompanyCode);
					record.set("expressCompanyName", json.expressCompanyName);
					record.set("message", json.message);
					record.set("status", json.status);
					grid.getView().refresh();
				},
				failure: function(response) {
					Q.error($('message.cancel.failure') + '<br/><br/>' + $('message.system.disconnect'));
				},
				callback: function() {
					
				}
			});
		}
	},


	/**
	 * @method gridDtlBeforeedit
	 * 送货管理明细编辑前事件
	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
	 * @param {Object} content 具有以下属性的编辑上下文
	 *   @param {Ext.grid.Panel} content.content 所编辑的表格对象
	 *   @param {Ext.data.Model} content.record 正在编辑的记录
	 *   @param {String} content.field 正在编辑的字段名称
	 *   @param {Mixed} content.value 字段当前值
	 *   @param {HTMLElement} content.row 正在编辑的行html元素
	 *   @param {Ext.grid.column.Column} content.column 正在编辑的列
	 *   @param {Number} content.rowIdx 正在编辑的行序列
	 *   @param {Number} content.colIdx 正在编辑的列序列.
	 *   @param {Boolean} context.cancel 将此设置为“TRUE”取消编辑或从处理程序返回false。
	 *   @param {Mixed} context.originalValue 编辑前的值
	 */
	gridDtlBeforeedit: function(editor, content) {
		var vp = this.getViewModel().getVp();
		return vp.editWin.beforeedit();
	},

	/**
	 * @method gridDtlAddSourceOrderHandler
	 * 送货管理添加按钮操作方法,数据来源：采购订单
	 */
	gridDtlAddSourceOrderHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var grid = me.getDetailGrid(); //查找出所属的父Grid
		var formField = vp.editWin.formPanel.getForm();
		if (!vp.editWin.formPanel.form.isValid()) {
			Q.tips("<font color='red'>" + $("message.pleaseSetNoBlankInfo") + "！</font>");
			return;
		}
		var purchasingOrgCode = formField.findField('model.purchasingOrgCode').getValue();
		var plantCode = formField.findField('model.plantCode').getValue();

		me.showPurchaseOrderDetailSelectWin();
	},
	
	/**
	 * @method expressGridAddHandle 添加快递信息
	 */
    expressGridAddHandle: function () {
    	try {
			var me = this;
			var viewModel = me.getViewModel();
			var vp = viewModel.getVp();
			var grid = vp.editWin.getCompByTabClassName('deliveryExpressDtls');;
			var isValidFlag = me.gridvalid();
			if (isValidFlag) {
				Q.tips($('porder.inputMainInfo'), 'E');
				return false;
			}
			var data = {
				expressCompanyCode: "",
				expressCompanyName: "",
				expressNo: ""
			};
			vp.editWin.addDetail(grid, data);
			grid.getView().refresh();
		} catch (e) {
			if (!Ext.isIE) {
				console.log(e)
			}
		}
    },
    
    /**
	 * @method expressGridDeleteHandle 删除快递信息
	 */
    expressGridDeleteHandle:function (_self) {
    	try {
			var grid = _self.findParentByType(Ext.grid.GridPanel); // 查找出所属的父Grid
			this.getViewModel().getEditWin().deleteDetail(grid);
		} catch (e) {
			if (!Ext.isIE) {
				console.log(e)
			}
		}
    },
    /**
	 * @method gridvalid 校验主单是否已填
	 */
    gridvalid: function () {
    	try {
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
			return isValidFlag;
		} catch (e) {
			if (!Ext.isIE) {
				console.log(e)
			}
		} 
    },

	/**
	 * @method gridDtlAddSourceScheduleHandler
	 * 送货管理添加按钮操作方法,数据来源：订单排程
	 */
	gridDtlAddSourceScheduleHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var grid = me.getDetailGrid(); //查找出所属的父Grid
		var formField = vp.editWin.formPanel.getForm();
		if (!vp.editWin.formPanel.form.isValid()) {
			Q.tips("<font color='red'>" + $("message.pleaseSetNoBlankInfo") + "！</font>");
			return;
		}
		var purchasingOrgCode = formField.findField('model.purchasingOrgCode').getValue();
		var plantCode = formField.findField('model.plantCode').getValue();

		me.showSendScheduleSelectWin();
	},

	/**
	 * @method gridDtlDeleteHandler
	 * 删除明细方法
	 */
	gridDtlDeleteHandler: function() {
		var me = this;
		var vp = me.getViewModel().getVp();
		var grid = me.getDetailGrid(); // 查找出所属的父Grid
		vp.editWin.deleteDetail(grid);
	},

	/**
	 * @method gridDtlUploadHandler
	 * 上传附件
	 */
	gridDtlUploadHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = me.getViewModel().getVp();
		var grid = me.getDetailGrid(); // 查找出所属的父Grid
		var url = {
			downLoadUrl: path_srm + "/fs/file/download?"
		};
		vp.editWin.uploadDoc(grid, 'annex', url);
	},
	/**
	 * @method gridDtlUploadHandler
	 * 上传附件前事件
	 */
	beforebrowseshow:function(_self,op){
        try{ 
            var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid
            var selectids = grid.getSelectionModel().getSelection();
            if (selectids.length <= 0) { // 请选择+grid.moduleName
                Q.tips("请先选择要上传的记录", 'E');
                return false;
            } else if (selectids.length > 1) { // 同时只能编辑一条信息！
                Q.tips($("同时只能编辑一条信息！"), 'E');
                return false;
            } 
            return true;
        }catch(e){
            console.log(e);
        }
    },
	/**
	 * @method gridDtlLoadValueBefore
	 * 当前组件加载值之前后调用
	 */
	gridDtlLoadValueBefore: function() {
		var gridPanel = this.getDetailGrid();
		var column = gridPanel.columns;

		//新建或者编辑状态下隐藏取消按钮和关闭按钮列
		if ('add' == vp.editWin.btnType || 'edit' == vp.editWin.btnType) {
			column[1].hide();
			column[2].hide();
		} else {
			column[1].show();
			column[2].show();
		}
	},


	/**
	 * @method gridDtlCancelBtnRenderer
	 * 取消按钮渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlCancelBtnRenderer: function(value, metaDatam, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		
		if (record.data == undefined) {
			var status = 'NEW';
		} else {
			var status = record.data.delivery.status;
		}
		//当满足送货细单的收货数量为0+送货细单的取消标识为否+送货细单的关闭标识为否的条件，则显示出取消按钮，不满足条件则不显示该按钮
		// 送货单状态为“收货完成”的送货单明细不允许进行取消和关闭操作 xieyj 20180321
		var cancelFlag = (record.get('cancelFlag') == '0' && (record.get('receivedQty') == 0 || record.get('receivedQty') == null) && record.get('closeFlag') == '0' && (status != 'NEW' || status != 'CLOSE'));
		var deliveryDtlId = record.get('deliveryDtlId');
		
		if (cancelFlag && window.canceldetail) {
			return "<button onclick='cancelFunction(" + deliveryDtlId + ")'>取消 </button>";
		} else {
			return '';
		}
	},

	/**
	 * @method gridDtlCloseBtnRenderer
	 * 关闭按钮渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlCloseBtnRenderer: function(value, metaDatam, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		
		if (record.data == undefined) {
			var status = 'NEW';
		} else {
			var status = record.data.delivery.status;
		}
		// 当满足送货细单的收货数量不为0+送货细单的关闭标识为否+送货细单的取消标识为否的条件，则显示出关闭按钮，不满足条件则不显示该按钮
		// 送货单状态为“收货完成”的送货单明细不允许进行取消和关闭操作 xieyj 20180321
		var closeFlag = (record.get('cancelFlag') == '0' && record.get('receivedQty') != 0 && record.get('closeFlag') == '0' && status != 'NEW' && (status != 'WAIT' || status != 'CLOSE'));
		var deliveryDtlId = record.get('deliveryDtlId');

		if (closeFlag && window.closedetail) {
			return "<button onclick='closeFunction(" + deliveryDtlId + ")'>关闭 </button>";
		} else {
			return '';
		}
	},

	/**
	 * @method gridDtlLineItemTypesRenderer
	 * 明细行记录类型渲染渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlLineItemTypesRenderer: function(value, metaDatam, record) {
		var viewModel = this.getViewModel();
		var value = record.get('lineItemTypes');
		var index = viewModel.getStore('recordTypeStore').find('itemCode', value);
		if (index > -1) {
			return viewModel.getStore('recordTypeStore').getAt(index).get('itemName');
		} else {
			return value;
		}
	},




	/**
	 * @method gridDtlFlagRenderer
	 * 是否字段渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlFlagRenderer: function(value, metaData) {
		var viewModel = this.getViewModel();
		if (!Ext.isEmpty(value)) {
			var index = viewModel.getStore('yesAndNoStore').find('value', value);
			if (index != -1) {
				var record = viewModel.getStore('yesAndNoStore').getAt(index);
				return record.get('text');
			}
		}
	},

	/**
	 * @method addAfter
	 * 点击添加按钮之后的事件
	 */
	addAfter: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var vendorStore = viewModel.getStore('vendorStore');
		var form = vp.editWin.formPanel.getForm();
		
		vendorStore.load({
			callback: function(records, operation, success) {
				var count = vendorStore.getCount();
				if (count == 1 && vendorStore.getAt(0).get('vendorCode') != undefined) {
					var record = vendorStore.getAt(0);
					var form = vp.editWin.formPanel.getForm();
					form.findField('model.vendorCode').setValue(record.get('vendorCode'));
					form.findField('model.vendorErpCode').setValue(record.get('vendorErpCode'));
					form.findField('model.vendorName').setValue(record.get('vendorName'));
					form.findField('model.vendorCode').setReadOnly(true);
					form.findField('model.vendorErpCode').setReadOnly(true);
				}
			}
		});

		viewModel.getStore('plantStore').removeAll();
		viewModel.getStore('stockLocationStore').removeAll();
		
		var a = vendorStore.getCount();
		var b = 0;
		var record;
		
		if (a == 1 && vendorStore.getAt(0).get('vendorCode') != undefined) {
			b = viewModel.getStore('purchasingOrgByVendorStore').getCount();
			record = viewModel.getStore('purchasingOrgByVendorStore').getAt(0);
		} else {
			b = viewModel.getStore('purchasingOrgStore').getCount();
			record = viewModel.getStore('purchasingOrgStore').getAt(0);
		}
		
		if (b == 1 && record.get('purchasingOrgCode') != undefined) {
			form.findField('model.purchasingOrgCode').setValue(record.get('purchasingOrgCode'));
			form.findField('model.purchasingOrgName').setValue(record.get('purchasingOrgName'));
		}
		
		//加载工厂信息
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
		
		if('' != purchasingOrgCode && null != purchasingOrgCode){
			//重新加载工厂，库存
			viewModel.getStore('plantStore').proxy.extraParams = {
				'filter_EQ_purchasingOrgCode': record.get('purchasingOrgCode')
			};
			viewModel.getStore('plantStore').load(function(){
				if(viewModel.getStore('plantStore').getCount() == 1){
					form.findField('model.plantCode').setValue(viewModel.getStore('plantStore').getAt(0).get("plantCode"));
					form.findField('model.plantName').setValue(viewModel.getStore('plantStore').getAt(0).get("plantName"));
					
					viewModel.getStore('stockLocationStore').proxy.extraParams = {
						'filter_EQ_plantCode': viewModel.getStore('plantStore').getAt(0).get("plantCode")
					};
					viewModel.getStore('stockLocationStore').load(function(){
						if(viewModel.getStore('stockLocationStore').getCount() == 1){
							form.findField('model.storageLocationCode').setValue(viewModel.getStore('stockLocationStore').getAt(0).get("stockLocationCode"));
							form.findField('model.storageLocationName').setValue(viewModel.getStore('stockLocationStore').getAt(0).get("stockLocationName"));
						}
					});
				}
			});
		}

		me.dealDeliveryDtlColumns();
	},

	/**
	 * @method addAfter
	 * 点击编辑按钮之后的事件
	 */
	editAfter : function() {
		var me = this;
		me.dealDeliveryDtlColumns();
	},
	
	/**
	 * @method addAfter
	 * 点击编辑按钮之后的事件
	 */
	viewAfter : function() {
		var me = this;
		me.dealDeliveryDtlColumns();
	},
	
	/**
	 * @method addAfter
	 * 处理细单字段显示隐藏
	 */
	dealDeliveryDtlColumns:function(){
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var gridPanel = me.getDetailGrid();
		var column = gridPanel.columns;
		//新建或者编辑状态下隐藏取消按钮和关闭按钮列
		if ('add' == vp.editWin.btnType ||'edit' == vp.editWin.btnType) {
			column.forEach(function(i){
				if(i.dataIndex=='cancelBtn' || i.dataIndex== 'closeBtn'){
					i.hide();
				}
			});
		} else {
			column.forEach(function(i){
				if(i.dataIndex=='cancelBtn' || i.dataIndex== 'closeBtn'){
					i.show();
				}
			});
		}
		//查看状态下隐藏已送数量、已收数量、退货量
		if ('view' == vp.editWin.btnType) {
			column.forEach(function(i){
				if(i.dataIndex=='canSentNumber' || i.dataIndex== 'receivedNumber' || i.dataIndex== 'returnNumber'){
					i.hide();
				}
			});
		} else {
			column.forEach(function(i){
				if(i.dataIndex=='canSentNumber' || i.dataIndex== 'receivedNumber' || i.dataIndex== 'returnNumber'){
					i.show();
				}
			});
		}
	},
	
	/**
	 * @method setFormValueAfter
	 * 表单初始化后触发
	 */
	setFormValueAfter: function( formPanel, viewType, json ){
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if(viewType == "view"){
			// 初始化plantStore
			viewModel.getStore('plantStore').proxy.extraParams = {
				'filter_EQ_purchasingOrgCode': json.data.purchasingOrgCode
			};
			viewModel.getStore('plantStore').load();
			// 初始化stockLocationStore
			viewModel.getStore('stockLocationStore').proxy.extraParams = {
				'filter_EQ_plantCode': json.data.plantCode
			};
			viewModel.getStore('stockLocationStore').load();
			// 初始化deliveryTypeStore
			viewModel.getStore('deliveryTypeStore').load();
		}
	},
	
	
	/**
	 * @method showSendScheduleSelectWin
	 * 显示订单排程明细
	 * @params {Ext.ux.Window} 选择窗体
	 */
	showSendScheduleSelectWin: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var detailGrid = me.getDetailGrid();

		var selectWin = new Cp.delivery.SendScheduleDetailSelectWin({
			recordTypeStore: viewModel.getStore('recordTypeStore'),
			singleSelect: false,
			gridOut: detailGrid,
			moduleId:me.getView().id,
			select: function(g, records) {
				for (var i = 0; i < records.length; i++) {
					var store = detailGrid.getStore();
					var record = records[i];
					// 可送数量= 需求数量-收货量-在途量+退货量 canSendQty
					/*var canSend = record.get('scheduleQty') + record.get('receiptQty')-
						Math.abs(record.get('onWayQty')) + record.get('returnGoodsQty');*/
					var index = store.find('orderId', record.get('sendScheduleCommonId'));
					if (index < 0) {
						var rowNo = store.getCount() + 1;
						var u = new Ext.data.Record({
							rowNo: rowNo,
							lineNumber: record.get('rowIds'),
							orderId: record.get('sendScheduleCommonId'),
							purchaseOrderCode: record.get('purchaseOrderNo'), // 把采购订单赋值给采购订单号
							erpPurchaseOrderNo: record.get('erpPurchaseOrderNo'), // 把erp采购订单赋值给采购订单号
							scheduleCode: record.get('sendScheduleNo'), // 采购订单号
							orderDetailId: record.get('purchaseOrderDetailId'),
							sendDetailId: record.get('sendScheduleDetailId'),
							buyer: record.get('createName'),
							materialCode: record.get('materialCode'),
							materialName: record.get('materialName'),
							unitCode: record.get('unitCode'),
							unitName: record.get('unitName'),
							orderNumber: record.get('sendQty'), // 订单数量
							toSentNumber: record.get('deliveryQty'), // 已送数量
							receivedNumber: record.get('receiptQty'), // 已收货量	
							returnNumber: record.get('returnGoodsQty'), // 退货量
							canSentNumber: record.get('canSendQty'), // 可送货量
							deliveryNumber: record.get('canSendQty'), // 送货量							
							lineItemTypes: record.get('lineItemTypeCode'), // 行项目类型
							storageLocationCode: record.get('stockLocal'),
							cancelFlag: '0',
							closeFlag: '0',
							dataFrom: 2, // 数据来源排程
							remark: '',
							uploadFileGroupId: '',
							overDeliveryLimit: record.get('overDeliveryLimit')
						});
						store.add(u);
						vp.editWin.setFormReadOnlyFields(detailGrid.formFieldReadyArr|| [], true);
					} else {
						Q.tips($('shoppingnoticedetail.tip.msg2') + '！'); // 同张送货单不能存在同一个排程明细行项目
					}
				}
				detailGrid.getView().refresh();
				selectWin.close();
			}
		});

		selectWin.on('show', function() {
			var form = vp.editWin.formPanel.getForm();
			var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
			var vendorCode = form.findField('model.vendorCode').getValue();
			var plantCode = form.findField('model.plantCode').getValue();
			var storageLocationCode = form.findField('model.storageLocationCode').getValue();
			selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_purchasingOrgCode = purchasingOrgCode;
			selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_vendorCode = vendorCode;
			if(plantCode != null){
				selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_plantCode = plantCode;
			}
			if (storageLocationCode != null) {
				selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_stockLocal = storageLocationCode;
			}

			var store = detailGrid.getStore();
			var count = store.getCount();

			if (count > 0) {
				var array = [];
				detailGrid.getStore().each(function(record) {
					var id = record.get('sendDetailId');
					if(id!=null&&id!=""){
						array.push(id);
					}
				})
				selectWin.gridPanel.getStore().proxy.extraParams.filter_NOTIN_sendScheduleDetailId = array;
			}

			selectWin.gridPanel.getStore().load();
		});

		selectWin.show();
		return selectWin;
	},

	/**
	 * @method showPurchaseOrderDetailSelectWin
	 * 显示可送货的采购订单明细
	 * @params {Ext.ux.Window} 选择窗体
	 */
	showPurchaseOrderDetailSelectWin: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var vm = this.getView();
		var detailGrid = me.getDetailGrid();
		
		var selectWin = Ext.create('Cp.delivery.PurcahseOrderDetailSelectWin',{
			recordTypeStore: viewModel.getStore('recordTypeStore'),
			title: $('purchaseOrder.title'),
			singleSelect: false,
			gridOut: detailGrid,
			moduleId: vm.id,
			select: function(g, records) {
				for (var i = 0; i < records.length; i++) {
					var store = detailGrid.getStore();
					var record = records[i];
					// /可送数量= 订单数量-收货量-在途量+退货量
					var canSend = record.get('vendorQty') + record.get('qtyQuit') - Math.abs(record.get('qtyArrive')) - Math.abs(record.get('qtyOnline'));

					var index = store.find('orderDetailId', record.get('purchaseOrderDetailId'));
					if (index < 0) {
						var rowNo = store.getCount() + 1;
						var u = new Ext.data.Record({
							rowNo: rowNo,
							// 送货单细单.采购订单号 --> 采购订单.SAP采购订单号
							purchaseOrderCode: record.get('purchaseOrder.purchaseOrderNo'), // 把采购订单赋值给采购订单号
						    erpPurchaseOrderNo: record.get('purchaseOrder.erpPurchaseOrderNo'),   
						      
							// 送货单细单.送货排程号 --> NULL
							scheduleCode: '',
							// 送货单细单.行号 --> 采购订单下拉框.行号
							lineNumber: record.get('rowIds'),
							orderId: record.get('purchaseOrder.purchaseOrderId'),
							// 送货单细单.数据来源 --> 采购订单细单.id
							orderDetailId: record.get('purchaseOrderDetailId'),
							sendDetailId: '',
							// 送货单细单.采购员 --> 采购订单.采购员
							buyer: record.get('purchaseOrder.createName'),
							// 送货单细单.物料编码  --> 采购订单下拉框.物料编码
							materialCode: record.get('materialCode'),
							// 送货单细单.物料名称  --> 采购订单下拉框.物料名称 
							materialName: record.get('materialName'),
							// 送货单细单.单位编码  --> 采购订单下拉框.单位编码
							unitCode: record.get('unitCode'),
							// 送货单细单.单位名称  --> 采购订单下拉框.单位名称 
							unitName: record.get('unitName'),
							// 送货单细单.订单数量  --> 采购订单下拉框.订单数量 
							orderNumber: record.get('vendorQty'),
							// 送货单细单.已送数量  --> 采购订单下拉框.送货数量
							toSentNumber: record.get('qtySend'),
							// 送货单细单.已收货量  --> 采购订单下拉框.已收货量
							receivedNumber: record.get('qtyArrive'),
							// 送货单细单.退货量  --> 采购订单下拉框.退货量
							returnNumber: record.get('qtyQuit'),
							// 送货单细单.可送数量 --> 采购订单下拉框.(订单数量-收货量-在途量+退货量)
							canSentNumber: canSend,
							// 送货单细单.送货数量 --> 采购订单下拉框.(订单数量-收货量-在途量+退货量)
							deliveryNumber: canSend,
							// 送货单细单.取消标识 --> 否
							cancelFlag: '0',
							storageLocationCode: record.get('storeLocal'),
							// 送货单细单.数据来源 --> 采购订单
							closeFlag: '0',
							dataFrom: 1,
							uploadFileGroupId: '',
							// 送货单细单.备注 --> 采购订单细单备注
							remark: '',
							lineItemTypes: record.get('lineItemTypeCode'),
							overDeliveryLimit: record.get('overDeliveryLimit')
						});
						store.add(u);
						vp.editWin.setFormReadOnlyFields(detailGrid.formFieldReadyArr|| [], true);
					} else {
						Q.tips($('shoppingnoticedetail.tip.msg2') + '！'); // 同张送货单不能存在同一个排程明细行项目
					}
				}
				detailGrid.getView().refresh();
				selectWin.close();
			}
		});

		selectWin.on('show', function() {
			var form = vp.editWin.formPanel.getForm();
			var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
			var vendorCode = form.findField('model.vendorCode').getValue();
			var plantCode = form.findField('model.plantCode').getValue();
			var storageLocationCode = form.findField('model.storageLocationCode').getValue();
		    selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_purchaseOrder_purchasingOrgCode = purchasingOrgCode;
			selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_purchaseOrder_vendorCode_OR_purchaseOrder_vendorErpCode = vendorCode;
			if(plantCode != null){
				selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_plantCode = plantCode;
			}
			if (storageLocationCode != null) {
				selectWin.gridPanel.getStore().proxy.extraParams.filter_EQ_storeLocal = storageLocationCode;
			}
            
			var store = detailGrid.getStore();
			var count = store.getCount();

			if (count > 0) {
				var array = [];
				store.each(function(record) {
					var id = record.get('orderDetailId');
					array.push(id);
				})
				selectWin.gridPanel.getStore().proxy.extraParams.filter_NOTIN_purchaseOrderDetailId = array;
			}

			selectWin.gridPanel.getStore().load();
		});

		selectWin.show();
		return selectWin;
	},


	/**
	 * @method getDetailGrid
	 * 获取编辑界面订单明细列表方法
	 * @return {Ext.grid.Panel} detailGrid 
	 */
	getDetailGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('deliveryDtls');
	},
	
	/**
	 * 关闭方法
	 */
	closeFunction: function(deliveryDtlId) {
		if (Ext.isEmpty(deliveryDtlId) && deliveryDtlId != "") {
			Q.tips($("message.close.confirm"));
			return;
		}
		
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var vm = this.getView();
		//var detailGrid = me.getDetailGrid();
		
		var gridTab = me.getDetailGrid();
		// 确定关闭选中的送货明细
		Q.confirm($("message.close.confirm"), {
			ok: function () {
				Ext.getBody().submitMask();
				Ext.Ajax.request({
					url: path_srm + "/cp/delivery/closedetail",
					params: {"id": deliveryDtlId},
					success: function (response) {
						var json = Ext.decode(response.responseText);
						if (json.success == false) {
							Q.error(json.info || $("message.cancel.failure")+"<br/><br/>"+$("message.system.error"));
							return;
						}
						//var parentId = vp.grid.getSelectionModel().getSelection()[0].id;
						//gridTab.getStore().proxy.extraParams = {"filter_EQ_delivery_deliveryId": parentId};
						//var indexUploadFlag = gridTab.getColumnModel().findColumnIndex('uploadFile4View');
						//gridTab.getStore().load({callback:function(arr_r){
							//if(indexUploadFlag > -1){// 有附件上传时调用
							//	Q.each(arr_r,function(rr,i){
							//		vp.renderUploadFile(rr.get("uploadFileGroupId"),rr,"uploadFile4View","",gridTab);// 附件渲染
							//	})
							//}
						//}});
						me.getDetailGrid().getStore().reload();
						vp.editWin.vpWin.grid.getStore().reload();
						Q.tips("<font color='blue'>"+$("message.close.success")+"</font>");
					},
					failure: function (response) {
						Q.error($("message.close.failure")+"<br/><br/>"+$("message.system.disconnect"));
					},
					callback: function () {
						Ext.getBody().unmask();
					}
				});
			}
		});
	},

	/**
	 * 取消方法
	 */
	cancelFunction: function(deliveryDtlId) {
		if (Ext.isEmpty(deliveryDtlId) && deliveryDtlId != "") {
			Q.tips($("message.cancel.confirm"));
			return;
		}
		
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var vm = this.getView();
		
		var gridTab = me.getDetailGrid();
		// 确定取消选中的送货明细
		Q.confirm($("message.cancel.confirm"), {
			ok: function () {
				vm.mask($('message.submit.wait'));
				//Ext.getBody().submitMask();
				Ext.Ajax.request({
					url: path_srm + "/cp/delivery/canceldetail",
					params: {"id": deliveryDtlId},
					success: function (response) {
						var json = Ext.decode(response.responseText);
						if (json.success == false) {
							Q.error(json.info || $("message.cancel.failure")+"<br/><br/>"+$("message.system.error"));
							return;
						}
						me.getDetailGrid().getStore().reload();
						vp.editWin.vpWin.grid.getStore().reload();
						Q.tips("<font color='blue'>"+$("message.cancel.success")+"</font>");
					},
					failure: function (response) {
						Q.error($("message.cancel.failure")+"<br/><br/>"+$("message.system.disconnect"));
					},
					callback: function () {
						//Ext.getBody().unmask();
						vm.unmask();
					}
				});
			}
		});
		
	},
	
	overrideSubmit: function(t, r, a, n, i) {
		var B = this;
		var D = "POST";
		var E = "ENCODE";
		var C;
		var jsonParamName;
		if (B.configVar.editWin.form.dataType
				&& B.configVar.editWin.form.dataType == "JSON") {
			E = "JSON";
			F = F
		} else {
			F = Ext.encode(F) == "{}" ? F : Q.parseParams(F)
		}
		if (B.configVar.editWin.form.dataRoot) {
			C = B.configVar.editWin.form.dataRoot
		}
		if (B.configVar.editWin.form.method) {
			D = B.configVar.editWin.form.method
		}
		if (B.configVar.editWin.form.jsonParamName) {
			jsonParamName = B.configVar.editWin.form.jsonParamName
		}
		B.formPanel.getForm().submit({
			waitTitle : $("message.submit.data"),
			waitMsg : $("message.submit.wait"),
			renderTo : B.id,
			url : A,
			method : D,
			dataType : E,
			dataRoot : C,
			timeout:560,
			jsonParamName:jsonParamName,
			params :F,
			success : function(I, H) {
				var K = H.result
                  , L = K.info;
                var J = typeof B.configVar.editWin != "undefined" && typeof B.configVar.editWin.submitSuccessAfter != "undefined" && B.configVar.editWin.submitSuccessAfter;
                if (Ext.isFunction(J)) {
                    J(I, H, Q.parseParams(F))
                }
                B.resetWin();
                B.fireEvent("submit");
                if (G == "audit") {
                    Q.tips($("message.confirm.success"))
                } else {
                    Q.tips($("message.save.success"))
                }
			},
			failure : function(I, H) {
				if (H && H.result) {
                    Q.error(H.result.info || $("message.submit.failure"), {
                        renderTo: B.id
                    })
                } else {
                    Q.error($("message.submit.failure") + "<br/><br/>" + $("message.system.disconnect"), {
                        renderTo: B.id
                    })
                }
			}
		});
	},
	/**
     *  附件上传前判断
     *  @params  _self 本身
     *  @params  {Object} op 相关配置项 
     *  @return {Boolean } false 窗口不弹出 | true 窗口弹出
     */
    beforeshow:function(_self,op){
        try{ 
            return false;
        }catch(e){
            console.log(e);
        }
    },
    btnbeforeshow:function(_self,op){
        try{ 
            var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid
            var selectids = grid.getSelectionModel().getSelection();
            if (selectids.length <= 0) { // 请选择+grid.moduleName
                Q.tips("请先选择要上传的记录", 'E');
                return false;
            } else if (selectids.length > 1) { // 同时只能编辑一条信息！
                Q.tips($("同时只能编辑一条信息！"), 'E');
                return false;
            } 
            return true;
        }catch(e){
            console.log(e);
        }
    },
    
	vendorErpCodeSetValueAfter:function(_self,fieldselect,parentObj,grid,parentType){
 		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var vendorCode = _self.value;
	},
	 /**
     *  请求前事件
     *  _self 本身 
     *  baseParams 请求前参数 
     *  parentObj 父类
     */
    plantCodeTriggerbaseparams:function(_self,baseParams,parentObj){
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

