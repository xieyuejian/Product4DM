/**
 * @class {Cp.quality.CensorQualityController}
 * @extend {Ext.ux.app.ViewController} 待检验质检管理控制层
 */
Ext.define('Cp.quality.CensorQualityController', {
	extend : 'Ext.srm.app.ViewController',
	alias : 'controller.censorQualityController',

	/**
	 * @method gridErpSynStateRenderer 订单同步状态渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @param {Ext.data.Model} record 当前记录行
	 * @return {String} 要呈现的HTML字符串
	 */
	gridErpSynStateRenderer: function(value, metaData, record) {
		var me = this;
		var viewModel = me.getViewModel();
		var msg = record.get('erpReturnMsg');
		metaData.tdAttr = "data-qtip='" + msg + "'";

		switch (value) {
			case 'SYNCHRONIZEDNOT':
				return "<font color='#bbbbbb'>" + $('erpSyn.nosyn') + "</font>"; // 灰色
			case 'SYNCHRONIZING':
				return "<font color='#ff8800'>" + $('erpSyn.onsyn') + "</font>"; // 蓝色
			case 'SYNSUCCESS':
				return "<font color='#444444'>" + $('erpSyn.synsuccess') + "</font>"; // 绿色
			case 'SYNFAILED':
				return "<font color='#ee4444'>" + $('erpSyn.synfail') + "</font>"; // 红色
			case 'SYNNONEED':
				return "<font color='#444444'>" + $('erpSyn.noneed') + "</font>"; // 红色
			default:
				return '';
		}
	},
	
	/**
	 * @method vpAfterRender 窗体加载后渲染
	 */
	vpAfterRender : function() {
		return "view";
	},

	/**
	 * @method vpExportHandler 列表界面导出按钮
	 * @param {Object} _self 默认事件对象本身
	 */
	vpExportHandler : function(_self) {
		var viewModel = this.getViewModel();
		var grid = viewModel.getVp().grid;
		var baseParams = grid.getStore().baseParams;
		var store = grid.getStore();
		if(store.totalCount>10000){
			Q.tips('导出数据超过10000条，请使用其他工具进行导出！');
			return false;
		}
		var url = viewModel.get('dealUrl') + '/export?jasperFile=QualityResult&reportFileType=xls&' + Ext.urlEncode(baseParams);
		window.open(url);
	},

	/**
	 * @method vpSynerpHandler 同步到ERP
	 * @param {Object} _self 默认事件对象本身
	 */
	vpSynerpHandler : function(_self) {
		var viewModel = this.getViewModel();
		var grid = viewModel.getVp().grid; // 查找出所属的父Grid
		var selectids = grid.getSelectionModel().getSelection();

		if (selectids.length <= 0) {
			Q.tips('<font color="red">' + $('message.pleaseSelect') + grid.moduleName + '！</font>');
			return;
		} else if (selectids.length > 1) {
			Q.warning($('message.onlySelect'));
			return;
		}

		var id = selectids[0].get('censorqualityId');

		// 确定同步+grid.moduleName+'?';
		Q.confirm($('message.operator.confirm').replace('{0}', $('button.synchronize')), {
			renderTo:viewModel.getVp().id,
			ok : function() {
				Ext.Ajax.request({
					url : viewModel.get('dealUrl') + '/synErp',
					params : {
						'id' : id
					},
					success : function(response) {
						var json = Ext.decode(response.responseText);
						if (false === json.success) { // 失败！未知系统异常！
							Q.tips(json.data,"E");
							return;
						}
						Q.tips('<font color="blue">' + $('message.operator.success') + '</font>');
						grid.getStore().reload();
						grid.getSelectionModel().clearSelections();
					},
					failure : function(response) { // 失败！请检查与服务器的连接是否正常，或稍候再试！
						Q.error($('message.operator.failure') + '<br/><br/>' + $('message.system.disconnect'));
					}
				});
			}
		});
	},

	/**
	 * @method qualityTabUploadHandler 本次质检上次附件
	 * @param {_self} 默认事件对象本身
	 */
	qualityTabUploadHandler : function() {
		var me = this;
		var vp = me.getViewModel().getVp();
		// var qualityForm = me.getCensorQualityFormPanel().getForm();
		var qualityForm = me.getMainFormPanel().getForm();
		var uploadFileGroupId = qualityForm.findField('model.uploadFileGroupId').getValue();
		vp.openUploadWindows(null, uploadFileGroupId, 'uploadFile4View', qualityForm, null);
	},

	/**
	 * @method qualityTabResultCodeSelect 本次质检质检结果字段选择事件
	 * @param {_self} 默认事件对象本身
	 * @param {record} 所选中的记录对象
	 */
	qualityTabResultCodeSelect : function(_self, record) {
		var me = this;
		var vp = me.getViewModel().getVp();
		var mainFormPanelForm = me.getMainFormPanel().getForm();
		var canCheckQty = mainFormPanelForm.findField('model.canCheckQty').getValue();
		var qualifiedQty = mainFormPanelForm.findField('model.qualifiedQty').getValue();
		var unqualifiedQty = mainFormPanelForm.findField('model.unqualifiedQty').getValue();
		var receiveQty = mainFormPanelForm.findField('model.receiveQty').getValue();
		var result = mainFormPanelForm.findField('model.resultCode');
		mainFormPanelForm.findField('model.resultName').setValue(record.get('text'));

		if ((qualifiedQty + unqualifiedQty + receiveQty) == canCheckQty) {
			result.setDisabled(false);
			if (Ext.isEmpty(result.getValue())) {
				result.validator = function() {
					return '质检结果必填!';
				}
			} else {
				// mainFormPanelForm.findField('model.resultCode').setValue(record.get("text"));
				result.validator = function() {
					return true;
				}
			}
			result.validate();
		} else {
			result.reset();
			result.setDisabled(true);
			result.validator = function() {
				return true;
			}
			result.validate();
		}
	},

	/**
	 * @method gridStatusRenderer 列表状态字段渲染
	 * @param {value} 当前对象的值
	 * @return 返回渲染后的值
	 */
	gridStatusRenderer : function(value) {
		var me = this;
		var viewModel = me.getViewModel();
		if (!Ext.isEmpty(value)) {
			return viewModel.data.stateObj[value].name;
		}
		return value;
	},


	/**
	 * @method erpSynRenderer 同步状态字段值渲染
	 * @param {value} 当前对象的值
	 * @param m 原始元数据
	 * @param {record} 当前记录
	 * @return 返回渲染后的值
	 */
	erpSynRenderer : function(value, m, record) {
		var msg = record.get('erpReturnMsg');
		m.tdAttr = "data-qtip='" + msg + "'";
		if (!Ext.isEmpty(value)) {
			return synStatuObj[value].name;
		}

		return value;
	},

	/**
	 * @method getEditWin 获取构造单例窗口
	 * @param {Object} cfg 实例化窗体的参数
	 */
	getEditWin : function(cfg) {
		var me = this;
		var vp = me.getViewModel().getVp();
		// 实例化对应的编辑窗口
		if (typeof cfg.editWin == 'undefined') {
			return;
		}
		var winG = Ext.create('Ext.comm.CommModelEditWin', cfg);

		winG.on('submit', function() {
			vp.grid.key = '';
			vp.grid.getStore().reload();
		});

		this.getEditWin = function() {
			return winG;
		};

		return winG;
	},

	/**
	 * @method vpCheckHandler 检验按钮
	 * @param {Object} _self 默认事件对象本身
	 */
	vpCheckHandler : function(_self) {
		var me = this;
		var viewModel = me.getViewModel();
		var grid = viewModel.getVp().grid; // 查找出所属的父Grid
		var selectids = grid.getSelectionModel().getSelection();
		if (selectids.length <= 0) { // 请选择+grid.moduleName
			Q.tips($('message.pleaseSelect') + grid.moduleName + '！', 'E');
			return;
		} else if (selectids.length > 1) { // 同时只能编辑一条信息！
			Q.warning($('message.onlySelect'));
			return;
		}

		var editWinCfg = me.getEditCfg();
		var editWin = me.getEditWin(editWinCfg);
		editWin.setFormValue(selectids[0], 'edit', null, null, editWinCfg.editWin.hiddenBtn);
		editWin.vpWin = viewModel.getVp();
		editWin.path = me.getView().path;
		viewModel.getVp().editWin1 = editWin;
		editWin.show();
	},

	/**
	 * @method getEditCfg 获取质检编辑界面的cfg
	 * @return {Object} 获取配置文件
	 */
	getEditCfg : function() {
		var me = this;
		var viewModel = me.getViewModel();

		var viewFormItems = [ {
			xtype : 'hidden',
			fieldLabel : $('censorQuality.censorqualityId'),
			name : 'model.censorqualityId'
		}, {
			QfieldLabel : '检验批号',
			fieldLabel : $('censorQuality.censorqualityNo'),
			name : 'model.censorqualityNo',
			readOnly:true
		}, {
			QfieldLabel : '供应商编码',
			fieldLabel : $('censorQuality.vendorCode'),
			name : 'model.vendorErpCode',
			readOnly:true
		}, {
			QfieldLabel : '供应商名称',
			fieldLabel : $('censorQuality.vendorName'),
			name : 'model.vendorName',
			readOnly:true
		}, {
			QfieldLabel : '物料编码',
			fieldLabel : $('censorQuality.materialCode'),
			name : 'model.materialCode',
			readOnly:true
		}, {
			QfieldLabel : '物料名称',
			fieldLabel : $('censorQuality.materialName'),
			name : 'model.materialName',
			readOnly:true
		}, {
			QfieldLabel : '单位',
			fieldLabel : $('censorQuality.unit'),
			name : 'model.unit',
			readOnly:true
		}, {
			QfieldLabel : '送检量',
			fieldLabel : $('censorQuality.censorQty'),
			name : 'model.censorQty',
			readOnly:true
		}, {
			QfieldLabel : '可检量',
			fieldLabel : $('censorQuality.canCheckQty'),
			name : 'model.canCheckQty',
			readOnly:true
		}, {
			QfieldLabel : '已质检合格量',
			fieldLabel : $('censorQuality.checkQualifiedQty'),
			name : 'model.checkQualifiedQty',
			readOnly:true
		}, {
			QfieldLabel : '已质检不合格量',
			fieldLabel : $('censorQuality.checkUnqualifiedQty'),
			name : 'model.checkUnqualifiedQty',
			readOnly:true
		}, {
			QfieldLabel : '已质检让步接收量',
			fieldLabel : $('censorQuality.checkReceiveQty'),
			name : 'model.checkReceiveQty',
			readOnly:true
		}, {
			QfieldLabel : '采购订单号',
			fieldLabel : $('censorQuality.purchaseOrderNo'),
			name : 'model.purchaseOrderNo',
			readOnly:true
		}, {
			QfieldLabel : '行号',
			fieldLabel : $('censorQuality.rowIds'),
			name : 'model.rowIds',
			readOnly:true
		}, {
			QfieldLabel : '收货单号',
			fieldLabel : $('censorQuality.shoppingNoticeNo'),
			name : 'model.receivingNoteNo',
			readOnly:true
		}, {
			QfieldLabel : '送检时间',
			fieldLabel : $('censorQuality.inspectionTime'),
			name : 'model.inspectionTime',
			anchor : '95%',
			xtype : 'datefield',
			format : 'Y-m-d H:i:s'
		},

		// hidden
		{
			xtype : 'hidden',
			QfieldLabel : '供应商Erp编码',
			fieldLabel : $('censorQuality.vendorErpCode'),
			name : 'model.vendorCode'
		}, {
			xtype : 'hidden',
			QfieldLabel : '采购组织名称',
			fieldLabel : $('censorQuality.purchasingOrgName'),
			name : 'model.purchasingOrgName'
		}, {
			xtype : 'hidden',
			QfieldLabel : '质检状态',
			fieldLabel : $('censorQuality.status'),
			name : 'model.status',
			submitValue : false,
			// xtype: 'uxcombo',
			store : viewModel.getStore('statusStore'),
			triggerAction : 'all'
		}, {
			xtype : 'hidden',
			QfieldLabel : '凭证年度',
			fieldLabel : $('censorQuality.voucherYear'),
			name : 'model.voucherYear'
		}, {
			xtype : 'hidden',
			QfieldLabel : '凭证编号',
			fieldLabel : $('censorQuality.voucherNo'),
			name : 'model.voucherNo'
		}, {
			xtype : 'hidden',
			QfieldLabel : '凭证行项目号',
			fieldLabel : $('censorQuality.voucherProNo'),
			name : 'model.voucherProNo'
		}, {
			xtype : 'hidden',
			QfieldLabel : '采购组织编码',
			fieldLabel : $('censorQuality.purchasingOrgCode'),
			name : 'model.purchasingOrgCode'
		}, {
			xtype : 'hidden',
			QfieldLabel : '工厂编码',
			fieldLabel : $('censorQuality.plantCode'),
			name : 'model.plantCode'
		}, {
			xtype : 'hidden',
			QfieldLabel : '工厂名称',
			fieldLabel : $('censorQuality.plantName'),
			name : 'model.plantName'
		}, {
			xtype : 'hidden',
			QfieldLabel : '库存地点编码',
			fieldLabel : $('censorQuality.stockCode'),
			name : 'model.stockCode'
		}, {
			xtype : 'hidden',
			QfieldLabel : '库存地点编码',
			fieldLabel : $('censorQuality.stockCode'),
			readOnly : true,
			name : 'model.stockName'
		}, {
			xtype : 'hidden',
			QfieldLabel : '同步状态',
			fieldLabel : $('censorQuality.erpSyn'),
			name : 'model.erpSyn'
		}, {
			xtype : 'hidden',
			QfieldLabel : '同步信息',
			fieldLabel : $('censorQuality.erpReturnMsg'),
			name : 'model.erpReturnMsg'
		}, {
			xtype : 'hidden',
			QfieldLabel : '送检人员id',
			fieldLabel : $('censorQuality.inspectorId'),
			name : 'model.inspectorId'
		}, {
			xtype : 'hidden',
			QfieldLabel : '送检人员名称',
			fieldLabel : $('censorQuality.inspectorName'),
			name : 'model.inspectorName'
		}, {
			xtype : 'hidden',
			QfieldLabel : '质检人员id',
			fieldLabel : $('censorQuality.qualitorId'),
			name : 'model.qualitorId'
		}, {
			xtype : 'hidden',
			QfieldLabel : '质检人员名称',
			fieldLabel : $('censorQuality.qualitorName'),
			name : 'model.qualitorName'
		}, {
			QfieldLabel : '合格量',
			fieldLabel : $('censorQuality.qualifiedQty'),
			name : 'model.qualifiedQty',
			xtype : 'numberfield',
			value : 0,
			regex : /^\d+(\.\d{0,3})?$/,
			regexText : '请输入正确的数据类型',
			allowDecimals : true,
			decimalPrecision : 3,
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'change' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.qualifiedQtyChange(_self, record);
				}
			}
		}, {
			QfieldLabel : '不合格量',
			fieldLabel : $('censorQuality.unqualifiedQty'),
			name : 'model.unqualifiedQty',
			xtype : 'numberfield',
			value : 0,
			allowDecimals : true,
			decimalPrecision : 3,
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'change' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.unqualifiedQtyChange(_self, record);
				}
			}
		}, {
			QfieldLabel : '让步接收量',
			fieldLabel : $('censorQuality.receiveQty'),
			name : 'model.receiveQty',
			xtype : 'numberfield',
			value : 0,
			allowDecimals : true,
			decimalPrecision : 3,
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'change' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.receiveQtyChange(_self, record);
				}
			}
		}, {
			QfieldLabel : '质检结果代码',
			fieldLabel : $('censorQuality.resultCode'),
			name : 'model.resultCode',
			hiddenName : 'model.resultCode',
			xtype : 'uxcombo',
			store : viewModel.getStore('resultStore'),
			maxLength : 25,
			triggerAction : 'all',
			editable : false,
			valueField : 'value',
			displayField : 'text',
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'select' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.qualityTabResultCodeSelect(_self, record);
				}
			}
		}, {
			xtype : 'hidden',
			QfieldLabel : '质检结果名称',
			fieldLabel : $('censorQuality.resultName'),
			name : 'model.resultName'
		}, {
			
			QfieldLabel : '质检时间',
			fieldLabel : $('censorQuality.qualityTime'),
			name : 'model.qualityTime',
			anchor : '95%',
			xtype : 'datefield',
			xtype : 'hidden',
			format : 'Y-m-d H:i:s'
		}, {
			xtype : 'textarea',
			QfieldLabel : '备注',
			fieldLabel : $('censorQuality.remark'),
			columnWidth : 1,
//			fieldStyle : 'background-color: #FFEDD9;',
			name : 'model.remark'
		}, /*{
			xtype : 'uploadButton',
			fieldLabel : $('porder.text'),
			name : 'uploadBtn',
			text : $('label.Annex'),
			handler : function() {
				var controller = viewModel.getVp().getController();
				controller.qualityTabUploadHandler();
			}
		}, {
			fieldLabel : $('label.AttachmentsCheck'),
			name : 'uploadFile4View',
			xtype : 'displayfield'
		}, {
			QfieldLabel : '附件',
			xtype : 'hidden',
			fieldLabel : $('censorQuality.uploadFileGroupId'),
			name : 'model.uploadFileGroupId'
		}*/{
	                name: "upload",
	                xtype: "srmpluploadbutton", 
	                text: "附件上传",
	                fileGroupIdField:"model.uploadFileGroupId",//根据自己配置附件组id存储字段
	                fileViewField:"model.uploadFile4View",//根据自己配置附件渲染字段,
	                columnWidth: 0.2
	            }, 
	            { 
	                xtype: "srmfilefield", 
	                name: "model.uploadFile4View",
	                columnWidth: 0.8, 
	                listeners:{
	                } 
	            }, {
	                QfieldLabel: "附件",
	                xtype: "srmfileidfield",
	                hidden:true,
	                fileViewField:"model.uploadFile4View",//附件渲染字段
	                name: "model.uploadFileGroupId"
	            } ];

		// 设置本次质检的表单
		viewModel.set('censorQualityFormItems.items', [ {
			QfieldLabel : '合格量',
			fieldLabel : $('censorQuality.qualifiedQty'),
			name : 'model.qualifiedQty',
			xtype : 'numberfield',
			value : 0,
			regex : /^\d+(\.\d{0,3})?$/,
			regexText : '请输入正确的数据类型',
			allowDecimals : true,
			decimalPrecision : 3,
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'change' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.qualifiedQtyChange(_self, record);
				}
			}
		}, {
			QfieldLabel : '不合格量',
			fieldLabel : $('censorQuality.unqualifiedQty'),
			name : 'model.unqualifiedQty',
			xtype : 'numberfield',
			value : 0,
			allowDecimals : true,
			decimalPrecision : 3,
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'change' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.unqualifiedQtyChange(_self, record);
				}
			}
		}, {
			QfieldLabel : '让步接收量',
			fieldLabel : $('censorQuality.receiveQty'),
			name : 'model.receiveQty',
			xtype : 'numberfield',
			value : 0,
			allowDecimals : true,
			decimalPrecision : 3,
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'change' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.receiveQtyChange(_self, record);
				}
			}
		}, {
			QfieldLabel : '质检结果代码',
			fieldLabel : $('censorQuality.resultCode'),
			name : 'model.resultCode',
			hiddenName : 'model.resultCode',
			xtype : 'uxcombo',
			store : viewModel.getStore('resultStore'),
			maxLength : 25,
			triggerAction : 'all',
			editable : false,
			valueField : 'value',
			displayField : 'text',
//			fieldStyle : 'background-color: #FFEDD9;',
			listeners : {
				'select' : function(_self, record) {
					var controller = viewModel.getVp().getController();
					controller.qualityTabResultCodeSelect(_self, record);
				}
			}
		}, {
			xtype : 'hidden',
			QfieldLabel : '质检结果名称',
			fieldLabel : $('censorQuality.resultName'),
			name : 'model.resultName'
		}, {
			xtype : 'textarea',
			QfieldLabel : '备注',
			fieldLabel : $('censorQuality.remark'),
			columnWidth : 1,
//			fieldStyle : 'background-color: #FFEDD9;',
			name : 'model.remark'
		}, {
			xtype : 'uploadButton',
			fieldLabel : $('porder.text'),
			name : 'uploadBtn',
			text : $('label.Annex'),
			handler : function() {
				var controller = viewModel.getVp().getController();
				controller.qualityTabUploadHandler();
			}
		},{
			fieldLabel : $('label.AttachmentsCheck'),
			name : 'uploadFile4View',
			xtype : 'displayfield'
		}, {
			QfieldLabel : '附件',
			xtype : 'hidden',
			fieldLabel : $('censorQuality.uploadFileGroupId'),
			name : 'model.uploadFileGroupId'
		},
			{
			xtype : 'hidden',
			QfieldLabel : 'clientCode',
			fieldLabel : $('censorQuality.clientCode'),
			name : 'model.clientCode'
		} ]);

		// viewModel.set('ew_centerTab', {
		// items: [viewModel.get('censorQualityFormItems'),
		// viewModel.get('finshQualityFormItems')]
		// });

		var parentCfg = viewModel.get('callParent');

		var editWinCfg = {
			isAudit : viewModel.get('isAudit'), // 是否需要审核右键
			dealUrl : viewModel.get('dealUrl'), // 各种操作的url地址
			moduleName : viewModel.get('moduleName'), // 模块名称
			playListMode : viewModel.get('playListMode'),
			moduleId : viewModel.get('moduleId'),
			restFul:true,
			editWin : {
				nextBillState : viewModel.get('nextBillState'), // 提交后下一步的状态
				maximized : viewModel.get('maximized'), // 是否最大化窗口，默认为否
				hiddenBtn : viewModel.get('hideEditBtn'),
				form : {
					dataRoot:"model",
					dataType:"JSON",
					items : viewFormItems, // 编辑表单字段
					columnWidth : viewModel.get('ew_columnWidth'), // 配置表单有几列
					height : viewModel.get('ew_height'),
					setFormValueAfter : 'setFormValueAfter'
				}
			// centerTab: viewModel.get('ew_centerTab')
			}
		}

		return editWinCfg;
	},

	/**
	 * @method qualifiedQtyChange 本次质检合格数量对象值改变事件
	 * @param {Object} _self 默认事件对象本身
	 * @param {Object} value 当前对象改变后的值
	 */
	qualifiedQtyChange : function(self, value) {
		var me = this;
		var vp = me.getViewModel().getVp();
		var mainFormPanelForm = me.getMainFormPanel().getForm();
		var canCheckQty = mainFormPanelForm.findField('model.canCheckQty').getValue();
		var unqualifiedQty = mainFormPanelForm.findField('model.unqualifiedQty').getValue();
		var receiveQty = mainFormPanelForm.findField('model.receiveQty').getValue();
		var result = mainFormPanelForm.findField('model.resultCode');

		if (value < 0) {
			mainFormPanelForm.findField('model.qualifiedQty').setValue(0);
			return;
		} else if ((value + unqualifiedQty + receiveQty) > canCheckQty) {
			mainFormPanelForm.findField('model.qualifiedQty').setValue(0);
			return;
		} else if ((value + unqualifiedQty + receiveQty) == canCheckQty) {
			if (Ext.isEmpty(result.getValue())) {
				result.validator = function() {
					return '质检结果必填!';
				} //
			}
			result.setDisabled(false);
			result.validate();
		} else if ((value + unqualifiedQty + receiveQty) < canCheckQty) {
			result.validator = function() {
				return true;
			} // 
			result.validate();
			result.reset();
			result.setDisabled(true);
		}

		// 把本次质检的合格量赋值到主单中
		// mainFormPanelForm.findField('model.qualifiedQty').setValue(value);
	},

	/**
	 * @method unqualifiedQtyChange 本次质检不合格数量对象值改变事件
	 * @param {Object} _self 默认事件对象本身
	 * @param {Object} value 当前对象改变后的值
	 */
	unqualifiedQtyChange : function(self, value) {
		var me = this;
		var vp = me.getViewModel().getVp();
		var mainFormPanelForm = me.getMainFormPanel().getForm();
		var canCheckQty = mainFormPanelForm.findField('model.canCheckQty').getValue();
		var qualifiedQty = mainFormPanelForm.findField('model.qualifiedQty').getValue();
		var receiveQty = mainFormPanelForm.findField('model.receiveQty').getValue();
		var result = mainFormPanelForm.findField('model.resultCode');

		if (value < 0) {
			mainFormPanelForm.findField('model.unqualifiedQty').setValue(0);
			return;
		} else if ((value + qualifiedQty + receiveQty) > canCheckQty) {
			mainFormPanelForm.findField('model.unqualifiedQty').setValue(0);
			return;
		} else if ((value + qualifiedQty + receiveQty) == canCheckQty) {
			result.validator = function() {
				return '质检结果必填!';
			}
			result.setDisabled(false);
			result.validate();
		} else if ((value + qualifiedQty + receiveQty) < canCheckQty) {
			result.validator = function() {
				return true;
			}
			result.validate();
			result.reset();
			result.setDisabled(true);
		}

		// 把本次质检的不合格量赋值到主单中
		// mainFormPanelForm.findField('model.unqualifiedQty').setValue(value);
	},

	/**
	 * @method receiveQtyChange 本次质检让步接收数量对象值改变事件
	 * @param {Object} _self 默认事件对象本身
	 * @param {Object} value 当前对象改变后的值
	 */
	receiveQtyChange : function(self, value) {
		var me = this;
		var vp = me.getViewModel().getVp();
		var mainFormPanelForm = me.getMainFormPanel().getForm();
		var canCheckQty = mainFormPanelForm.findField('model.canCheckQty').getValue();
		var qualifiedQty = mainFormPanelForm.findField('model.qualifiedQty').getValue();
		var receiveQty = mainFormPanelForm.findField('model.receiveQty').getValue();
		var result = mainFormPanelForm.findField('model.resultCode');
		var unqualifiedQty = mainFormPanelForm.findField('model.unqualifiedQty').getValue();

		if (value < 0) {
			mainFormPanelForm.findField('model.receiveQty').setValue(0);
			return;
		} else if ((value + unqualifiedQty + qualifiedQty) > canCheckQty) {
			mainFormPanelForm.findField('model.receiveQty').setValue(0);
			return;
		} else if ((value + unqualifiedQty + qualifiedQty) == canCheckQty) {
			result.validator = function() {
				return '质检结果必填!';
			}
			result.setDisabled(false);
			result.validate();
		} else if ((value + unqualifiedQty + qualifiedQty) < canCheckQty) {
			result.validator = function() {
				return true;
			}
			result.validate();
			result.reset();
			result.setDisabled(true);
		}

		// 把本次质检的让步接收量赋值到主单中
		// mainFormPanelForm.findField('model.receiveQty').setValue(value);
	},

	/**
	 * @method getMainFormPanel 获取主单form
	 * @return {Ext.form.Panel} formPanel 主单表单对象
	 */
	getMainFormPanel : function() {
		var vp = this.getViewModel().getVp();
		if (Ext.isEmpty(vp.editWin1)) {
			return vp.editWin.getCompByTabClassName('mainFormPanel');
		}
		var mainFormPanelForm = vp.editWin1.getCompByTabClassName('mainFormPanel');
		return mainFormPanelForm;
	},

	/**
	 * @method getCensorQualityFormPanel 获取质检结果tabform
	 * @return {Ext.form.Panel} formPanel 主单表单对象
	 */
	getCensorQualityFormPanel : function() {
		var vp = this.getViewModel().getVp();
		var censorQualityForm = vp.editWin1.getCompByTabClassName('censorQualityTab');
		return censorQualityForm;
	},

	/**
	 * @method setFormValueAfter 在表单值改变后触发的方法
	 */
	setFormValueAfter : function() {
		var me = this;
		var vp = me.getViewModel().getVp();
		var mainFormPanelForm = me.getMainFormPanel().getForm();
		var canCheckQty = mainFormPanelForm.findField('model.canCheckQty').getValue();
		mainFormPanelForm.findField('model.qualifiedQty').setValue(canCheckQty);
		var qualifiedQty = mainFormPanelForm.findField('model.qualifiedQty').getValue();
		var unqualifiedQty = mainFormPanelForm.findField('model.unqualifiedQty').getValue();
		var receiveQty = mainFormPanelForm.findField('model.receiveQty').getValue();

		var result = mainFormPanelForm.findField('model.resultCode');

		if ((qualifiedQty + unqualifiedQty + receiveQty) > canCheckQty) {
			mainFormPanelForm.findField('model.qualifiedQty').setValue(0);
		} else if ((qualifiedQty + unqualifiedQty + receiveQty) == canCheckQty) {
			result.validator = function() {
				return '质检结果必填!';
			} //
			result.setDisabled(false);
			result.validate();
		} else if ((qualifiedQty + unqualifiedQty + receiveQty) < canCheckQty) {
			result.validator = function() {
				return true;
			} // 
			result.validate();
			result.reset();
			result.setDisabled(true);
		}
	},
	/**检验状态渲染*/
	censorQualityRenderer:function(value){
		if(value=="TOCHECK"){
			return "待检验";
		}else if(value=="CHECKING"){
			return "检验中";
		}else if(value=="CHECKED"){
			return "检验完成";
		}else if(value=="CANCEL"){
			return "取消";
		} else{
			return "-";
		}
	
	}

});
