/**
 * @class {Cp.quality.PurchaseOrderDetailController}
 * @extend {Ext.ux.app.ViewController} 执行中的订单控制层
 */
Ext.define("Cp.order.PurchaseOrderDetailController", {
	extend : "Ext.srm.app.ViewController",
	alias : "controller.purchaseOrderDetailController",

	/**
	 * @method vpExportHandler 列表界面导出按钮方法
	 */
	vpExportHandler : function(self) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var baseParams = vp.grid.getStore().proxy.extraParams;
		var dealUrl = viewModel.get('dealUrl');
		window.open(dealUrl + "/export?" + Ext.urlEncode(baseParams));
	},

	/**
	 * 列表双击事件
	 * @return {Boolean}
	 */
	rowdblclickFn:function(){
		return false;
	
	},
	/**
	 * @method qualityTabUploadHandler 本次质检上次附件
	 */
	qualityTabUploadHandler : function() {
		var vp = this.viewModel.getVp();
		var uploadFileGroupId = vp.editWin1.getCompByTabClassName('censorQualityTab').getForm().findField('model.uploadFileGroupId').getValue();
		vp.openUploadWindows(null, uploadFileGroupId, 'uploadFile4View', vp.editWin1.getCompByTabClassName('censorQualityTab').getForm(), null);
	},

	/**
	 * @method gridStatusRenderer 列表状态字段渲染
	 * @param {Object}
	 *            value 当前列值
	 * @return {String} 返回渲染后的值
	 */
	gridStatusRenderer : function(value) {
		if (!Ext.isEmpty(value)) {
			return billStateObj[value].name;
		}

		return value;
	},


	/**
	 * @method numberTwoDecimalRenderer 数值字段值渲染,保留2位小数
	 * @param {Object}
	 *            value 当前列值
	 * @param {Object}
	 *            metaData 当前单元格元数据
	 * @return {String} 要呈现的HTML字符串
	 */
	numberTwoDecimalRenderer : function(value, metaData) {
		if (null == value || undefined == value || '' == value) {
			return '';
		}

		metaData.attr = 'style=text-align:right';
		return Ext.util.Format.number(value, '0.00');
	},


});
