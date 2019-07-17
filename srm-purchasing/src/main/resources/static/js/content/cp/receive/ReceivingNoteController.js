﻿
/**
 * @class {Cp.receive.ReceivingNoteController} 
 * @extend {Ext.ux.app.ViewController}
 * 收货查询控制层
 */
Ext.define('Cp.receive.ReceivingNoteController', {
	extend: 'Ext.srm.app.ViewController',
	alias: 'controller.receivingNoteController',

	/**
	 * @method gridStoreBeforeLoad
	 * 列表加载前事件
	 */
	vpInstanceAfter: function() {
		
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
	vpAfterRender: function() {
		
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
	searchLoadBefore: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		vp.searchFlag = true;
	},

	
	/**
	 * @method qualityFlagRenderer 质检状态渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前记录行
	 * @return {String} 要呈现的HTML字符串
	 */
	
	qualityFlagRenderer: function(value, metaData, record) {
		var me = this;
		var viewModel = me.getViewModel();
		
		if('0' == value){
			return '未质检';
		}else if('1' == value){
			return '待检验';
		}else if('2' == value){
			return '质检完成';
		}
		return '---';
	},
	
	/**
	 * @method qualityFlagRenderer 质检状态渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前记录行
	 * @return {String} 要呈现的HTML字符串
	 */
	statusRenderer: function(value, metaData, record) {
		switch (value) {
		case "TOCHECK":
			return "待检验";
			break;
		case "CHECKING":
			return "检验中";
			break;
		case "CHECKED":
			return "检验完成";
			break;
		case "CANCEL":
			return "取消";
			break;

		default:
			return "";
			break;
		}
	},
	
	
	/**
	 * @method dateRenderer
	 * 收退货标识渲染
	 * @param {Object} value 当前对象的值
	 * @return {String} 要呈现的HTML字符串
	 */
	acceptReturnFlagRenderer: function(value) {
		var me = this;
		var viewModel = me.getViewModel();
		var acceptReturnStore = viewModel.getStore('acceptReturnStore');
		var index = acceptReturnStore.find('value',value);
		if(-1 == index){
			return '';
		}
		var record = acceptReturnStore.getAt(index);
		return record.get('text');
	},

	/**
	 * @method vpChargeOffHandler
	 * 列表界面冲销按钮方法
	 */
	vpChargeOffHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = vp.grid;
		var record = grid.getSelectionModel().getSelection()[0];
		
		if (!record) {
			Q.tips($('message.pleaseSelect'), 'E');
		} else {
			var receivingNoteId = record.get('receivingNoteId');
			var dataMethod = 'get';
			var afterfn = function() {};
			var submitMehtod = 'chargeoff';
			var hideBtn = ['submit'];
			var editFlag = 'edit';

			/** 
			 * Function customAddRecordBtn  自定义添加新建选择的记录按钮时提供方法
			 * @param {int} recordId 查看的单据id 
			 * @param {String} urlNew  后台加载数据方法名
			 * @param {Function} afterfn 后续操作 editwin 当前新建窗口  formPanel 编辑窗口表单
			 * @param {String} submitUrl  后台提交数据方法名
			 * @param {array} hiddenBtnName 需要隐藏的按钮
			 * @param {String} editFlag 保存编辑标识
			 */
			vp.customAddEditBtn(receivingNoteId, dataMethod, afterfn, submitMehtod, hideBtn, editFlag);
		}
	},

	/**
	 * @method vpDownloadHandler
	 * 列表界面下载按钮方法
	 */
	vpDownloadHandler: function() {
		//window.open(this.getViewModel().get('dealUrl') + '/download?templateFile=ReceivingNote.xls&fileName=收货导入模板');
		var me = this;
		var viewModel = me.getViewModel();
		var url = viewModel.get('dealUrl') + '/download?templateFile=ReceivingNote.xls&fileName=收货导入模板';
		//window.open(url);
		Ext.UxFile.fileDown(url,"收货导入模板.xls",null);
	},

	/**
	 * @method vpDownloadHandler
	 * 列表界面上传按钮方法
	 */
	vpImportHandlde: function(_self) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		
		var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid 
		var opt = {
            url:viewModel.get("dealUrl"), 
            method: "/importexcel",
            importSuccess:function(op, op1){//导入成功后回调
            	vp.grid.getStore().reload();
            }
        };//配置项
        var renderTo = grid.id;//渲染载体
        //文件工具类调用
        Ext.UxFile.fileImport(opt, renderTo); 
        
	},

	/**
	 * @method vpExportHandler
	 * 列表界面导出按钮方法
	 */
	vpExportHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var store = vp.grid.store;
		var baseParams = store.proxy.extraParams;

		window.open(viewModel.get('dealUrl') + '/export?jasperFile=ReceivingNote&isDownLoad=true&reportFileType=xls&' + Ext.urlEncode(baseParams));
	},
	/**
	 * @method setFormValueAfter
	 * 表单初始化后触发
	 */
	setFormValueAfter: function( formPanel, viewType, json ){
		// 初始化可冲销数量
		var form = formPanel.getForm();
		form.findField("canChargeOffNum").setValue(json.data.canChargeOffNum);
	},
	
	/**
	 * @method formCanChargeOffNumChange
	 * @param {Object} self 当前点击对象
	 * @param {Object} value 当前对象的值
	 * 表单冲销数量改变事件
	 */
	formCanChargeOffNumChange: function(self, value) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var form = vp.editWin.formPanel.getForm();
		var canChargeOffNum = form.findField('canChargeOffNum').getValue();
		if (!Ext.isEmpty(self.value)) {
			if (parseFloat(self.value) > parseFloat(canChargeOffNum)) {
				Q.tips($('receivingnote.message.chargeNumTooBig'), 'E');
				self.setValue('');
			}
		}
	},

	/**
	 * @method editWinShow
	 * 编辑窗口初始化方法
	 */
	editAfter: function() {
		
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var tbar = vp.editWin.formPanel.getTopToolbar().find('name', 'submit')[0];
		tbar.hide();
	},
	viewAfter: function() {
	
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var tbar = vp.editWin.formPanel.getTopToolbar().find('name', 'submit')[0];
		tbar.hide();
	}


});