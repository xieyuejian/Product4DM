/**
 * @class {Bpm.authorize.AuthorizeViewModel}
 * @extend {Ext.ux.app.ViewModel} 授权单配置
 */
Ext.define("Bpm.authorize.AuthorizeViewModel", {
	extend : 'Ext.ux.app.ViewModel',
	alias : 'viewmodel.authorize',
	requires : ['Ext.srm.window.UserSelectWin'],
	stores : {
		billTypeStore : Ext.create("Ext.data.Store", {
			fields : ["itemName", "itemCode"],
			autoLoad : true,
			proxy : {
				type : "ajax",
				actionMethods : {
					read : "POST"
				},
				url : path_masterdata
						+ '/md/datadict/getall?groupCode='
						+ 'authorizeBillType',
				reader : {
					type : "json",
					root : "records"
				}
			}
		})
	},

		data : {
			/**
			 * @cfg {Boolean} isExtend 是否用父类的getCfg 配置方法
			 */
			isExtend : true,
			/**
			 * @cfg {Boolean} isAudit 是否有审核右键
			 */
			isAudit : true,
			/**
			 * @cfg {String} dealUrl 统一方法处理url
			 */
			dealUrl : path_srm + '/sys/authorize',
			/**
			 * @cfg {String} moduleName 模块名称
			 */
			moduleName : $("Authorize"), // 授权单信息
			/**
			 * @cfg {String} vp_triggerField 触发域（字段名）
			 */
			vp_triggerField : 'authorizeNo',
			/**
			 * @cfg {String} controllerClassName 模块控制类名
			 */
			controllerClassName : "Bpm.authorize.AuthorizeController",
			/**
			 * @cfg {String} playListMode 模块列表展示形式
			 */
			playListMode : "normal", // normal/audit/undeal/panel
			// //三种列表模式
			/**
			 * @cfg {Integer} searchWinHeight 查询窗体高度
			 */
			editWinHeight : 450,
			/**
			 * @cfg {Integer} searchWinHeight 查询窗体高度
			 */
			sw_columnWidth : 1,

			// vp_hideSubTab : true,
			/**
			 * @cfg {Array} vpSubTab 列表底部tab集合
			 */
			vp_subTab : ['logTab', 'msgTab'],
			/**
			 * @cfg {Integer} searchWinWidth 查询窗体宽度
			 */
			editWinWidth : 800,

			ew_columnWidth : 0.5,// 编辑表单每行列数
			vp_hideListBtn : [],
			/**
			 * @cfg {String} vp_logModuleCode 底部logTab 操作日志 请求参数值
			 */
			vp_logModuleCode : "AU",
			/**
			 * @cfg {String} vp_billTypeCode 底部msgTab 审核日志 请求参数值默认单据编码
			 */
			vp_billTypeCode : "AU",
			vp_addListBtn : [{// 提前结束授权
				name : 'stop',
				text : $('btn.authorizeStop'),
				index : 4,
				iconCls : "icon-delete",
				build : power['stop'],
				handler : 'authorizeStopHandler'
			}],
			vp_gridColumn : [{
						Qheader : "id",
						header : $("authorize.authorizeId"),
						dataIndex : "authorizeId",
						disabled : true
					}, {
						Qheader : "授权编号",
						header : $("authorize.authorizeNo"),
						dataIndex : "authorizeNo",
						width : 150,
						renderer : 'rendererNo'
					}, {
						Qheader : "申请状态",
						header : $("authorize.status"),
						dataIndex : "status",
						width : 100,
						renderer : 'rendererStatus'
					}, {
						Qheader : "单据类型",
						header : $("billtype"),
						dataIndex : "billTypes"
					}, {
						Qheader : "单据类型id",
						header : $("authorize.billId"),
						dataIndex : "billIds",
						disabled : true
					}, {
						Qheader : "授权人id",
						header : $("authorize.authorizePersonId"),
						dataIndex : "authorizePersonId",
						disabled : true
					}, {
						Qheader : "授权人",
						header : $("authorize.authorizePersonName"),
						width : 130,
						dataIndex : "authorizePersonName"
					}, {
						Qheader : "被授权人id",
						header : $("authorize.authorizeToPersonId"),
						dataIndex : "authorizeToPersonId",
						disabled : true
					}, {
						Qheader : "被授权人名字",
						header : $("authorize.authorizeToPersonName"),
						width : 130,
						dataIndex : "authorizeToPersonName"
					}, {
						Qheader : "生效时间",
						header : $("sourceListDtl.effectiveDate"),
						dataIndex : "effectiveTime",
						width : 120,
						renderer : 'rendererDate'
					}, {
						Qheader : "失效时间",
						header : $("sourceListDtl.failureDate"),
						dataIndex : "expiryTime",
						width : 120,
						renderer : 'rendererDate'
					}, {
						Qheader : "授权理由",
						header : $("authorize.authorityReason"),
						width : 244,
						dataIndex : "authorityReason",
						renderer : 'textRenderer'
					}],
			/**
			 * @cfg {Object} gridStore 列表Store配置项
			 */
			vp_gridStore : {
				idProperty : "authorizeId",
				url : "#{dealUrl}/list",
				sort : "authorizeNo",
				dir : "desc"
			},
			vp_gridCfg: {
				stateful : true,
				stateId : s_userCode + '_authorize',
				stateHeader : true,
				forceFit : false,
				ableExporter:true,
				billNoField: "authorizeNo"
			},
			authorityNameArr:[ "authorize_topass", "authorize_tonopass"],
			/**
			 * @cfg {Array} vp_listEditStateFn 列表按钮控制配置
			 */
			vp_listEditStateFn : [{
				"edit" : function(r) {
					return r.get('status') == 'NEW'
							|| r.get('status') == 'TONOPASS';// 新建或审核不过可编辑
				}
			}, {
				"view" : true
			}, {
				"delete" : function(r) {
					return r.get('status') == 'NEW'
							|| r.get('status') == "TONOPASS";// 审核不过可删除
				}
			}, {
				"stop" : function(r) {
					return r.get('status') == 'TOPASS';// 审核通过状态可提前结束授权
				}
			}, {
				"grant" : function(r) {
					return r.get('status') == 'TOCONFIRM';
				}
			}],
			ew_height : 200,

			/**
			 * @cfg {Array} editFormItems 编辑窗口 form表单配置项
			 */
			ew_editFormItems : [{
						xtype : "hidden",
						fieldLabel : $("authorize.authorizeId"),
						name : "model.authorizeId"
					}, {
						QfieldLabel : "授权单号",
						fieldLabel : $("authorize.authorizeNo")
								+ "<font color='red'> *</font>",
						value : $("contract.message.automatic.generated"),
						name : "model.authorizeNo",
						readOnly : true
					}, {
						QfieldLabel : "单据类型Id",
						fieldLabel : $("billtype")
								+ "<font color='red'> *</font>",
						name : "model.billIds",
						xtype : 'uxcombo',
						bind : {
							store : '{billTypeStore}'
						},
						// editable：设置false，阻止直接在表单项的文本框中输入字符，这时表单项只能通过从选择器中来选择值，用于combobox
						editable : false,
						// allowBlank的意思是是否允许为空，这个属性帮你验证必输项的。会在表单提交时，自动帮你验证，该文本框是否输入了内容，如果没有输入则会标红提示。这个是Ext底层帮你封装好的方法，省去了你自己写js代码验证的麻烦。
						// 设置为flase，则代表
						// 不允许为空，也就是说该文本框输入时不能为空。反过来true则代表允许为空，那么Ext底层就去验证是否必输了。
						allowBlank : false,
						innerTpl : false,
						// mode : "remote",
						// 用all表示把下拉框列表框的列表值全部显示出来
						triggerAction : "all",
						// valueField 对应的显示的字段
						valueField : 'itemCode',
						// displayField , 隐藏的字段
						displayField : 'itemName',
						// 列宽
						columnWidth : 1,
						multiSelect : true,
						// listeners表示监听一个鼠标或者键盘事件。
						listeners : {
							select : 'getBillTypeName'

						}
					},{
						QfieldLabel : "单据类型Id",
						fieldLabel : $("billtype"),
						name : "model.billId",
						xtype : 'hidden'
					}, {
						QfieldLabel : "单据类型",
						fieldLabel : $("billtype")
								+ "<font color='red'> *</font>",
						name : "model.billTypes",
						xtype : 'hidden'
					}, {
						QfieldLabel : "授权人",
						fieldLabel : $("authorize.authorizePersonName")
								+ "<font color='red'> *</font>",
						name : "model.authorizePersonName",
						value : s_userName,
						readOnly : true
					}, {
						QfieldLabel : "授权人id",
						fieldLabel : $("authorize.authorizePersonId"),
						name : "model.authorizePersonId",
						xtype : 'hidden'
					}, {
						QfieldLabel : "被授权人",
						fieldLabel : $("authorize.authorizeToPersonName")
								+ "<font color='red'> *</font>",
						name : "model.authorizeToPersonName",
						xtype : 'uxtrigger',
						allowBlank : false,
						listeners : {
							trigger : 'formManagerUserTrigger',
							clear : 'formManagerUserClear'
						}

					}, {
						QfieldLabel : "被授权人id",
						fieldLabel : $("authorize.authorizeToPersonId"),
						name : "model.authorizeToPersonId",
						xtype : 'hidden'
					}, {
						QfieldLabel : "生效日期",
						fieldLabel : $("sourceListDtl.effectiveDate")
								+ "<font color='red'> *</font>",
						name : "model.effectiveTime",
						xtype : "datefield",
						format : "Y-m-d",
						allowBlank : false,
						editable : false

					}, {
						QfieldLabel : "失效日期",
						fieldLabel : $("sourceListDtl.failureDate")
								+ "<font color='red'> *</font>",
						name : "model.expiryTime",
						xtype : "datefield",
						format : "Y-m-d",
						allowBlank : false,
						editable : false
					}, {
						QfieldLabel : "授权理由",
						fieldLabel : $("authorize.authorityReason"),
						name : "model.authorityReason",
						xtype : "textarea",
						// 列宽
						columnWidth : 1,
						maxLength : 150
					}],
			/**
			 * @cfg {Array} searchFormItems 查询字段集合
			 */
			sw_searchFormItems : [{
						QfieldLabel : "授权单号",
						fieldLabel : $("authorize.authorizeNo"),
						name : "filter_LIKE_authorizeNo",
						// 列宽
						columnWidth : 0.5
					}, {
						QfieldLabel : "授权人",
						fieldLabel : $("authorize.authorizePersonName"),
						name : "filter_LIKE_authorizePersonName",
						// 列宽
						columnWidth : 0.5

					}, {
						QfieldLabel : "被授权人",
						fieldLabel : $("authorize.authorizeToPersonName"),
						name : "filter_LIKE_authorizeToPersonName",
						// 列宽
						columnWidth : 0.5

					}, {
						QfieldLabel : "单据类型Id",
						fieldLabel : $("billtype"),
						name : "filter_LIKE_billId",
						xtype : 'uxcombo',
						bind : {
							store : '{billTypeStore}'
						},
						// editable：设置false，阻止直接在表单项的文本框中输入字符，这时表单项只能通过从选择器中来选择值，用于combobox
						editable : false,
						innerTpl : false,
						// mode : "remote",
						// 用all表示把下拉框列表框的列表值全部显示出来
						triggerAction : "all",
						// valueField 对应的显示的字段
						valueField : 'itemCode',
						// displayField , 隐藏的字段
						displayField : 'itemName',
						// 列宽
						columnWidth : 1,
						multiSelect : true,
						// listeners表示监听一个鼠标或者键盘事件。
						listeners : {
							select : 'getBillTypeName'

						}
					}, {
						fieldLabel : $('authorize.effectiveTime'),
						name : 'filter_EQ_effectiveTime',
						xtype : 'datefield',
						format : 'Y-m-d',
						// 列宽
						columnWidth : 0.5
					}, {
						fieldLabel : $('authorize.expiryTime'),
						name : 'filter_EQ_expiryTime',
						xtype : 'datefield',
						format : 'Y-m-d',
						// 列宽
						columnWidth : 0.5
					}, {
						fieldLabel : $('porder.purchaseOrderState'),
						xtype : 'checkboxgroup',
						columns : 5,
						height : 30,
						labelAlign : "right",
						columnWidth : 1,
						anchor : '95%',
						items : [{
									boxLabel : $('state.new'),
									name : 'filter_IN_status',
									inputValue : 'NEW',
									width : 103
								}, {
									boxLabel : $('state.confirm'),
									name : 'filter_IN_status',
									inputValue : 'TOCONFIRM',
									width : 117
								}, {
									boxLabel : $('state.nopass'),
									name : 'filter_IN_status',
									inputValue : 'TONOPASS',
									width : 117
								}, {
									boxLabel : $('state.pass'),
									name : 'filter_IN_status',
									inputValue : 'TOPASS',
									width : 117
								}, {
									boxLabel : $('authorize.stateStop'),
									name : 'filter_IN_status',
									inputValue : "CANCEL"
								}]
					}]
		}

	
});
