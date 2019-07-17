﻿
/**
 * @class {Cp.receive.ShoppingNoticeReceivinController} 
 * @extend {Ext.ux.app.ViewController}
 * 送货点收控制层
 */
Ext.define('Cp.receive.ShoppingNoticeReceivinController', {
	extend: 'Ext.srm.app.ViewController',
	alias: 'controller.shoppingNoticeReceivinController',

	/**
	 * @method gridStoreBeforeLoad
	 * 列表加载前事件
	 */
	gridStoreBeforeLoad: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var store = vp.grid.store;
		
		store.proxy.extraParams.initStates = viewModel.get('initStatesStr');
		if (vp.searchFlag && undefined != vp.searchWin) {
			var params = vp.searchWin.formPanel.form.getValues();
			params.initStates = store.proxy.extraParams.initStates;
			store.proxy.extraParams = params;
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
	 * @method vpCloseHandler
	 * 列表界面关闭按钮方法
	 */
	vpCloseHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = vp.grid;
		var selectids = grid.getSelectionModel().getSelection();

		if (selectids.length != 1) { //请选择+grid.moduleName
			Q.tips($('message.onlySelect'), 'E');
			return;
		}

		var id = selectids[0].get('deliveryId');

		//确定删除+grid.moduleName+'?';
		Q.confirm($('message.operator.confirm').replace('{0}', $('button.close')), {
			ok: function() {
				Ext.getBody().mask($('pleaseWait'));
				Ext.Ajax.request({
					url: viewModel.get('dealUrl') + '/close',
					params: {
						'id': id
					},
					success: function(response) {
						var json = Ext.decode(response.responseText);
						if (false === json.success) { //grid.moduleName+'删除失败！未知系统异常！
							Q.error(json.info || $('message.operator.failure') + '<br/><br/>' + $('message.system.error'));
							return;
						}
						Q.tips($('message.operator.success'));
						grid.getStore().reload();
						grid.getSelectionModel().clearSelections();
					},
					failure: function(response) { //grid.moduleName+'删除失败！请检查与服务器的连接是否正常，或稍候再试！
						Q.error($('message.operator.failure') + '<br/><br/>' + $('message.system.disconnect'));
					},
					callback: function() {
						Ext.getBody().unmask();
					}
				});
			}
		});
	},

	/**
	 * @method vpReleaseHandler
	 * 列表界面点收按钮方法
	 */
	vpReleaseHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = vp.grid;
		var selectids = grid.getSelectionModel().getSelection();

		if (selectids.length <= 0) {
			//Q.tips('<font color='red'>请选择'+grid.moduleName+'</font>');
			Q.tips($('message.pleaseSelect') + grid.moduleName, 'E');
			return;
		} else if (selectids.length > 1) {
			//Q.warning('同时只能查看一条信息！');
			Q.warning($('message.onlySelect'));
			return;
		}

		var record = selectids[0];
		var stockLocationStore = viewModel.getStore('stockLocationStore');
		var plantCode = record.get('plantCode');

		stockLocationStore.proxy.extraParams = {
			'filter_EQ_plantCode': plantCode
		};
		stockLocationStore.load();
		vp.customEditBtn(record.get('deliveryId'), 'get', null, 'receiving', 'save');
	},

	/**
	 * @method getDetailGrid
	 * 获取编辑界面明细列表方法
	 * @return {Ext.grid.Panel} detailGrid 
	 */
	getDetailGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('deliveryDtls');
	},




	/**
	 * @method gridDtlEdit
	 * 明细编辑事件
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

		if (content.field == 'acceptQty') {
			var me = this;
			var viewModel = me.getViewModel();
			var vp = viewModel.getVp();
			var grid = vp.getVp().grid;
			var record = content.record;
			var qtyAccept = parseFloat(record.get('acceptQty'));
			var qtyDelivery = parseFloat(record.get('deliveryNumber'));
			var qtyReceived = parseFloat(record.get('receivedQty'));
			var isFinish = qtyDelivery - qtyReceived;

			if (qtyAccept > (qtyDelivery - qtyReceived)) {
				Q.tips($('shoppingnoticedetail.message.acceptQtyCanNotGreaterThenReceivedQty'), 'E');
				record.set('acceptQty', content.originalValue);
				grid.getView().refresh();
			}
		}
	},


	/**
	 * @method gridDtlBeforeedit
	 * 明细编辑前事件
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
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		// 判断是否
		return vp.editWin.beforeedit();
	},

	/**
	 * @method gridDtlStorageLocationCodeSelect
	 * 明细库存选中事件
	 * @param {Object} combo 当前控件对象
	 * @param {Ext.data.Model} record 选中的记录
	 */
	gridDtlStorageLocationCodeSelect: function(combo, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var detailGrid = me.getDetailGrid();
		var detailStore = detailGrid.getStore();

		form.findField('model.storageLocationName').setValue(record.get('stockLocationName'));
		detailStore.each(function(v, i) {
			v.set('storageLocationCode', record.get('stockLocationCode'));
			v.set('storageLocationName', record.get('stockLocationName'));
		});
	},
	/**
	 * @method gridDtlStorageLocationCodeSelect
	 * 明细库存清除事件
	 * @param {Object} combo 当前控件对象
	 * @param {Ext.data.Model} record 选中的记录
	 */
      gridDtlStorageLocationCodeClear: function(combo, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var detailGrid = me.getDetailGrid();
		var detailStore = detailGrid.getStore();
        form.findField('model.storageLocationName').setValue("");
		detailStore.each(function(v, i) {
			v.set('storageLocationCode', "");
		});
	},
	/**
	 * @method loadValueAfter
	 * 明细加载值之后事件
	 * @param {Ext.grid.Panel} grid 当前控件对象
	 * @param {Ext.data.JsonStore} store 选中的记录
	 */
	loadValueAfter: function() {
		var me = this;
		var vp = me.getViewModel().getVp();
		var selected = vp.grid.getSelectionModel().getSelection()[0];
		//获取库存地点编码
		var storageLocationCode = selected.get('storageLocationCode');
		var store = me.getDetailGrid().getStore();
		//设置送货明细的库存地点编码
		store.each(function(v, i) {
			v.set('storageLocationCode', storageLocationCode);
		});
	},

	/**
	 * @method submitBefore
	 * 提交之前的事件
	 * @return {Boolean} 返回true或者false
	 */
	submitBefore: function() {
		var me = this;
		var vp = me.getViewModel().getVp();
		var parentGrid = me.getDetailGrid();
		var store = parentGrid.getStore();
		var count = 0;
		for (var i = 0; i < store.getCount(); i++) {
			var record = store.getAt(i);
			count += record.get('acceptQty')
		}
		if(count==0){
			var msg = $('shoppingnoticedetail.acceptQtyCountMustBiggerZero');
			Q.tips(msg, 'E');
			return false;
		}
		return true;
	}

});