/**
 * @class {Cp.delivery.DeliveryDetailController} 
 * @extend {Ext.ux.app.ViewController}
 * 送货看板控制类
 */
Ext.define('Cp.delivery.DeliveryDetailController', {
	extend: 'Ext.srm.app.ViewController',
	alias: 'viewmodel.deliveryDetailController',

	/**
	 * @method vpExportHandler
	 * 列表界面导出按钮方法
	 */
	vpExportHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = vp.grid;
		var baseParams = grid.store.baseParams;
		window.open(viewModel.get('dealUrl') + "/exportexcel?" + Ext.urlEncode(baseParams));
	},

	/**
	 * @method gridStatusRenderer
	 * 送货管理状态渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridStatusRenderer: function(value, metaData, record) {
		// 浏览器语言
		var currentLangFlag = (currentLang == 'en' || currentLang == 'en-US');

		var state = '';
		if (!Ext.isEmpty(value)) {
			if (currentLangFlag == 'en' || currentLangFlag == 'en-US') {
				state = billStateObj[value].code;
			}
			state = billStateObj[value].name;
		}

		return state;
	},

	/**
	 * @method gridDtlDeliveryNumberRenderer
	 * 送货数量渲染,保留3位小数
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDtlDeliveryNumberRenderer: function(value, metaData, record) {
		var me = this;
		return me.numberThreeDecimalRenderer(value, metaData);
	},

	/**
	 * @method gridDeliveryCodeRenderer
	 * 单号需要渲染下划线和蓝色字体
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDeliveryCodeRenderer: function(value, metaData, record) {
		metaData.attr = 'style="cursor:pointer;"'; // 给当前td添加样式
		return '<u style="color:blue">' + value + '</u>';
	},

	/**
	 * @method gridDateRenderer
	 * 日期字段值渲染 Y-m-d
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
	gridDateRenderer: function(value, metaDatam, record) {
		if (!Ext.isEmpty(value)) {
			return Ext.util.Format.date(value, 'Y-m-d');
		}

		return '';
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
	 * @method vpInstanceAfert
	 * 窗体加载之后事件
	 */
	vpInstanceAfert: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var searchBtn = vp.grid.getTopToolbar().query('*[name=search]')[0];
		searchBtn.handler();
	},
	/**
	 * @method rowdbclickFn
	 * 表格双击事件
	 */
	rowdbclickFn: function(){
		return false;
	}
});