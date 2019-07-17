/**
 * @class {Cp.apply.PurchasingRequisitionViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 采购申请配置
 */
Ext.define('Cp.apply.PurchasingRequisitionViewModel',{
	extend : 'Ext.ux.app.ViewModel',
    alias: 'viewmodel.purchasingRequisitionViewModel',
    requires:[
    	'Ext.ux.form.DateTimeField',
    	"Ext.srm.form.PlantComboGrid",
    	"Ext.srm.form.PurchasingGroupComboGrid",
    	"Ext.srm.form.CompanyComboGrid",
    	"Ext.srm.form.UnitComboGrid",
    	"Ext.srm.dictionary.CompanyCombo",
    	"Ext.srm.ux.ChatTab"
    ],
        /**
     * 相关配置项
     */
    config:{
	    /**
		 * @cfg {Object} stores
		 * 相关store归集 
		 * 
		 * - **tempDeleteStore** - 删除申请明细时暂存store
		 *
		 */
		stores: {
	
			/**
			 * 删除申请明细时暂存store
			 */
			tempDeleteStore: Ext.create("Ext.data.JsonStore", {
				fields: ['materialId', 'materialCode']
			})
		},
		
	   	data:{
	   		/**
             * 单据状态
             * @param {} vm 配置对象
             * @return {Array}
             */
            billStatusFn:function(vm){ 
            	return [{
            		statusCode:"NEW",
            		statusName:$("status.new") //新建
            	},{
            		statusCode:"TOCONFIRM",
            		statusName:$("state.confirm") // 待审核
            	},{
            		statusCode:"TONOPASS",
            		statusName:$("status.tonopass") //驳回
            	},{
            		statusCode:"TOPASS",
            		statusName:$("state.release") //发布
            	},{
            		statusCode:"CLOSE",
            		statusName:$("state.shut") //关闭
            	},{
            		statusCode:"CANCEL",
            		statusName:$("state.cancel") //取消
            	}] 
            },
            /**
             * 单据状态
             * @param {} vm 配置对象
             * @return {Array}
             */
            initStatusFn:function(vm){ 
            	return [{
            		statusCode:"NEW",
            		statusName:$("status.new") //新建
            	},{
            		statusCode:"TOCONFIRM",
            		statusName:$("state.confirm") // 待审核
            	},{
            		statusCode:"TONOPASS",
            		statusName:$("status.tonopass") //驳回
            	},{
            		statusCode:"TOPASS",
            		statusName:$("state.release") //发布
            	},{
            		statusCode:"CLOSE",
            		statusName:$("state.shut") //关闭
            	},{
            		statusCode:"CANCEL",
            		statusName:$("state.cancel") //取消
            	}] 
            },
	   		
			isAudit:true,
		   	isExtend:true,
		 	authorityNameArr:[ "purchaseapply_topass", "purchaseapply_tonopass"],
		    	/**
			 * @cfg {String} dealUrl 
			 * 方法处理url
			 */
			dealUrl: path_srm +'/cp/purchaseapply',
				/**
			 * @cfg {String} controllerClassName
			 * 控制类类名称
			 */
			controllerClassName: 'Cp.apply.PurchasingRequisitionController',
			/**
			 * @cfg {String} moduleName 
			 * 模块名称
			 */
			moduleName: $('purchasingRequisition'),	
		    /**
	         * @cfg {String}  vp_logModuleCode 
	         *  底部logTab 操作日志 请求参数值
	         */
			vp_logModuleCode:'PR',
		     /**
	          * @cfg {String}  vp_billTypeCode
	         *  底部msgTab 审核日志 请求参数值默认单据编码 
	         */
		    vp_billTypeCode: "PR",
		      /**
		       * @cfg {String} triggerField
		       * 触发域（字段名）
		       */
		    vp_triggerField: 'purchasingRequisitionNo', 
		    vp_listEditStateFn: [{
					'edit': function(r) {
						return (r.get('status') == 'NEW' || r.get('status') == 'TONOPASS'); //新建审核或不过可以编辑
					}
				}, {
					'view': true
				}, {
					'delete': function(r) {
						return (r.get('status') == 'NEW' || r.get('status') == 'TONOPASS'); //新建审核或不过可以编辑
					}
				}, {
					'close': function(r) {
						return r.get('status') == 'TOPASS'; //发布
					}
				}, {
					'cancel': function(r) {
						return r.get('status') == 'TOPASS'; //发布
					}
				}, {
		        	"grant": function(r){// 授权,审核状态下才允许使用
		        		return r.get('status') == "TOCONFIRM";
		        	}
	            }, {
		        	"revokeaudit": function(r){// 撤销审核,审核状态下才允许使用
		        		return r.get('status') == "TOCONFIRM"&& s_userid == r.get("createUserId");
		        	}
				}, {
		        	"prompttrial": function(r){
		        		return r.get('status') == "TOCONFIRM";
		        	}
		        }
		    ],
		    vp_addListBtn:[
		    {
				name: "close",
				Qtext: "关闭",
				text: $('button.close'),
				build: power['close'],
				index: 4,
				iconCls: "icon-close",
				handler: 'vpCloseHandler'
			}, {
				name: "cancel",
				Qtext: "取消",
				text: $('button.cancel'),
				build: power['cancel'],
				index: 5,
				iconCls: "icon-cancel",
				handler: 'vpCancelHandler'
			},
			{
	            name: 'revokeaudit',
	            text: "撤销审核",
	            iconCls: 'icon-cancel',
	            index: 12,
	            build:power['revokeaudit'],
	            handler: "revokeAuditHandler"
	        }],
		
		
			/**
			 * @cfg {Array} gridColumn 
			 * 列表对象 列属性配置项
			 */
			vp_gridColumn:[
				{Qheader:'采购申请编码',header:$('purchasingRequisition.purchasingRequisitionNo'),dataIndex:'purchasingRequisitionNo',width:200, renderer:'rendererNo',tipable:true}, 
				{Qheader:'单据状态',header:$('purchasingRequisition.status'),dataIndex:'status',renderer:'rendererStatus',exportRenderer: true,tipable:true}, 
				{Qheader:'公司编码',header:$('label.companyCode'),dataIndex:'companyCode',tipable:true}, 
				{Qheader:'申请人姓名',header:$('purchasingRequisition.applicantName'),dataIndex:'applicantName',tipable:true}, 
				{Qheader:'申请时间',header:$('purchasingRequisition.applicantTime'),width:150,dataIndex:'applicantTime',xtype: 'datecolumn',format: 'Y-m-d H:i:s',exportRenderer:true}, 
				{Qheader:'采购申请id',header:$('purchasingRequisition.purchasingRequisitionId'),dataIndex:'purchasingRequisitionId',disabled:true,tipable:true}, 
				{Qheader:'客户端编码',header:$('purchasingRequisition.clientCode'),dataIndex:'clientCode',disabled:true,tipable:true}, 
				{Qheader:'公司名称',header:$('label.companyName'),dataIndex:'companyName',disabled:true}, 
				{Qheader:'申请人编码',header:$('purchasingRequisition.applicantCode'),dataIndex:'applicantCode',disabled:true}, 
				{Qheader:'备注',header:$('purchasingRequisition.remark'),dataIndex:'remark',disabled:true}, 
				{Qheader:'创建人id',header:$('label.createUserId'),dataIndex:'createUserId',disabled:true}, 
				{Qheader:'创建人姓名',header:$('label.createUserName'),dataIndex:'createUserName',disabled:true}, 
				{Qheader:'创建时间',header:$('label.createTime'),dataIndex:'createTime',disabled:true}, 
				{Qheader:'修改人id',header:$('label.modifyUserId'),dataIndex:'modifyUserId',disabled:true}, 
				{Qheader:'修改人姓名',header:$('label.modifyUserName'),dataIndex:'modifyUserName',disabled:true}, 
				{Qheader:'修改人姓名',header:$('label.modifyTime'),dataIndex:'modifyTime',disabled:true}
			],
			 vp_gridStore: {
		        idProperty: 'purchasingRequisitionId',
				url: "#{dealUrl}/list",
				sort: 'purchasingRequisitionId',
				dir: 'desc',
				remoteSort: true,
				listeners: {
					load: "gridStoreLoad",
					beforeload:"gridStoreBeforeLoad"
				}
		    }, 
		    vp_gridCfg:{
    	        stateful : true,
				stateId : s_userCode + '_purchasingRequisition',
				stateHeader : true,
				forceFit : false,

    			ableExporter:true,
    			billNoField: "purchasingRequisitionNo",
                rn:true//序列列隐藏
		    },
			/**
			 * @cfg {Array} vpSubTab 
			 * 列表底部tab集合
			 */
		    vp_subTab:['gridDtl', 'logTab', 'msgTab','chatTab'],
	
			/**
			 * @cfg {Integer} editWinFormHeight
			 * 编辑表单高度
			 */
			ew_height: 160,
	
			/**
			 * @cfg {String} editWinFormColumnWidth
			 * 编辑表单列个数
			 */
			ew_columnWidth: '0.5',
	
			/**
			 * @cfg {boolean} editWinMaximized
			 * 是否最大化窗口，默认为否
			 */
			maximized : true,
	
			
	
			/**
			 * @cfg {Integer} activeTab
			 * 默认展示的tab页
			 */
			vp_activeTab: 0,
	
			
			/**
			 * @cfg {Boolean} searchWinIsShowStatus
			 * 查询窗体是否显示状态查询
			 */
			sw_isShowStatus: true,
	
			/**
			 * @cfg {String} searchFormColumnWidth
			 * 查询表单每行列数
			 */
			sw_columnWidth: '0.5',
	
			/**
			 * @cfg {Array} menuOverride 
			 * 重写右键审核方法 
			 * - **TOPASS** - 审核通过
			 * - **TONOPASS** - 审核不通过
			 */
			menuOverride: [{
				text: $('button.toPass'),
				name: 'TOPASS',
				iconCls: "icon-pass",
				hidden: true,
				handler: 'flowApprove'
			}, {
				text: $('button.toNoPass'),
				name: 'TONOPASS',
				iconCls: "icon-nopass",
				hidden: true,
				handler: 'flowApprove'
			}],
			/**
			* @cfg {Object}  ew_centerTab
			* 编辑窗口 明细配置项 border布局中的 region:center
			*/
			ew_centerTab: {
			 items: ['gridDtl'] 	
			},
			 /**
		         * @cfg {Array} editFormItems
		         * 编辑form表单配置
		         */
		    ew_editFormItems:[
				{
					QfieldLabel: '采购申请编码',
					fieldLabel: $('purchasingRequisition.purchasingRequisitionNo'),
					name: 'model.purchasingRequisitionNo',
					readOnly: true
				}, {
					QfieldLabel: '公司编码',
					fieldLabel: $('label.companyCode') + '<font color = "red">*</font>',
					name: 'model.companyCode',
					xtype: 'companyComboGrid',
					minChars:1,
					displayField : 'companyName',
					valueField : 'companyCode',
					displayValue : 'companyCode',
					innerTpl : true,
					queryField : 'companyCode',
					editable:true, 
					allowBlank: false
				}, {
					QfieldLabel: '公司名称',
					xtype: 'hidden',
					fieldLabel: $('label.companyName'),
					name: 'model.companyName'
				}, {
					QfieldLabel: '备注',
					fieldLabel: $('purchasingRequisition.remark'),
					name: 'model.remark',
					columnWidth: 0.5,
					maxLength: 1000,
					xtype: 'textarea'
				}, {
					QfieldLabel: '创建人id',
					xtype: 'hidden',
					fieldLabel: $('label.createUserId'),
					name: 'model.createUserId'
				}, {
					QfieldLabel: '创建人姓名',
					xtype: 'hidden',
					fieldLabel: $('label.createUserName'),
					name: 'model.createUserName'
				}, {
					QfieldLabel: '修改人id',
					xtype: 'hidden',
					fieldLabel: $('label.modifyUserId'),
					name: 'model.modifyUserId'
				}, {
					QfieldLabel: '修改人姓名',
					xtype: 'hidden',
					fieldLabel: $('label.modifyUserName'),
					name: 'model.modifyUserName'
				},
	
				//hidden
				{QfieldLabel: '申请人编码', fieldLabel: $('purchasingRequisition.applicantCode'), name: 'model.applicantCode', hidden: true },
				{QfieldLabel: '申请人姓名', fieldLabel: $('purchasingRequisition.applicantName'), name: 'model.applicantName', hidden: true },
				{QfieldLabel: '创建时间', fieldLabel: $('purchasingRequisition.applicantTime'), name: 'model.createTime', anchor: '90%', xtype: 'datefield', format: 'Y-m-d H:i:s', hidden: true },
				{QfieldLabel: '申请时间', fieldLabel: $('purchasingRequisition.applicantTime'), name: 'model.applicantTime', anchor: '90%', xtype: 'datefield', format: 'Y-m-d H:i:s', hidden: true },
				{QfieldLabel: '客户端编码', fieldLabel: $('purchasingRequisition.clientCode'), name: 'model.clientCode', hidden: true },
				{QfieldLabel: '单据状态', fieldLabel: $('purchasingRequisition.status'), name: 'model.status', submitValue: false, hidden: true },
				{xtype: 'hidden', fieldLabel: $('purchasingRequisition.purchasingRequisitionId'), name: 'model.purchasingRequisitionId'}
			],
			  /**
		         * @cfg {Array} searchFormItems
		         * 查询窗口字段配置项
		         */
	        sw_searchFormItems:[
		    	{QfieldLabel:'公司编码', fieldLabel:$('label.companyCode'), name : 'filter_EQ_companyCode'},
		    	{QfieldLabel:'采购申请编码', fieldLabel:$('purchasingRequisition.purchasingRequisitionNo'), name:'filter_EQ_purchasingRequisitionNo'},
		    	{QfieldLabel:'物料编码', fieldLabel:$('purchasingRequisitionDtl.materialCode'), name:'filter_EQ_purchasingRequisitionDtls_materialCode'},
		    	{QfieldLabel:'物料名称', fieldLabel:$('purchasingRequisitionDtl.materialName'), name:'filter_LIKE_purchasingRequisitionDtls_materialName'},
		    	{QfieldLabel:'申请时间', fieldLabel:$('purchasingRequisition.applicantTime'), name:'filter_GE_applicantTime', xtype:'datefield', format:'Y-m-d'},
		    	{QfieldLabel:'至', fieldLabel:$('label.to'), name:'filter_LE_applicantTime', xtype:'datefield', format:'Y-m-d'},
		    	{QfieldLabel:'需求日期', fieldLabel:$('purchasingRequisitionDtl.demandDate'), name:'filter_GE_purchasingRequisitionDtls_demandDate', xtype:'datefield', format:'Y-m-d'},
		    	{QfieldLabel:'至', fieldLabel:$('label.to'), name:'filter_LE_purchasingRequisitionDtls_demandDate', xtype:'datefield', format:'Y-m-d'},
				{Qheader:'申请人',fieldLabel:$('purchasingRequisition.applicantName'),name:'filter_LIKE_applicantName'}
	    	],
	    	
		    chatTab : {
	            xtype:'chatTab',
	            /**
	             *  回复必传参数
	             *    "model.processKey" : billTypeCode,//单据编码 【固化】
	             *    "model.businessKey" : prepaymentsId,//选中的单据id【固化】
	             *    "moduleName":moduleName //模块名称,
	             *    "userId": record.get("createUserId") 记录创建者id 对应字段编码为createUserId【固化】
	             *  当有特殊情况请根据实际情况配置对应的参数映射字段
	             *     "userId": "xxxx" 记录创建者id
	             */
	            paramsMapping:{
	               "userId" : "createId"
	            },
	            /**
	             *  回复参数动态处理
	             * @param {Object} vm 当前 viewModel
	             * @param {Object} params 回传参数集
	             * @param {record} record 选中回复的记录
	             * @param {String} handler 操作动作 add 新建| reply 回复
	             */
	            paramsMappingFn:function(vm, params, record, handler){
	
	                return {};
	            },
	            /**
	             *  回复弹出窗口配置
	             * @param {Object} vm 当前 viewModel
	             * @param {Object} config 窗口配置参数集
	             * @param {record} record 选中回复的记录
	             * @param {String} handler 操作动作 add 新建| reply 回复
	             */
	            configMappingFn:function(vm, config, record, handler){
	
	               return {};
	            }
	
	        },
			/**
			 * @cfg {Object} gridDtl 
			 * 明细列表
			 */
			gridDtl: {
				tabTitle: $('purchasingRequisitionDtl'),
				xtype: 'uxeditorgrid',
				foreignKey: 'purchasingRequisition_purchasingRequisitionId',
				tabClassName: 'purchasingRequisitionDtls',
				validField: ['rowNo', 'plantCode', 'materialName', 'unitCode', 'demandDate', 'quantityDemanded'], //需要提交的细单字段,
				formFieldReadyArr: ['model.companyCode'],
				sm: {
					singleSelect: false
				},
				stateHeader : true,
				stateful : true,
				stateId : s_userCode + '_purchasingRequisitionDtl',
				allowEmpty: false, //明细条数是否可以为空
				cm: {
					defaultSortable: false,
					defaults: {
						menuDisabled: true
					},
					columns: [
						{Qheader: '行号', header: $('purchasingRequisitionDtl.rowNo'), dataIndex: 'rowNo'},
						{Qheader: '物料编码', header: $('purchasingRequisitionDtl.materialCode'), dataIndex: 'materialCode'},
						{
							Qheader: '物料名称',
							header: $('purchasingRequisitionDtl.materialName'),
							dataIndex: 'materialName',
							width: 80,
							//自定义属性
	                        customAttr:{
	                        	allowBlank:false,
	                        	editable:true//是否显示可以编辑背景颜色
	                        },
							editor: {
								xtype: 'textfield',
								name: 'materialName'
							}
						}, {
							Qheader: '工厂名称',
							header: $('purchasingRequisitionDtl.plantName'),
							dataIndex: 'plantName',
							disabled: true
						}, {
						    Qheader: '工厂编码',
						    header: $('forecastDtl.plantCode'),
						    dataIndex: 'plantCode',
						    width: 80,
						    //自定义属性
	                        customAttr:{
	                        	allowBlank:false,
	                        	editable:true//是否显示可以编辑背景颜色
	                        },
						    editor: {
						        xtype: 'plantComboGrid',
						        name: 'plantCode',
						        hiddenName: 'plantCode',
				    		    fieldMapping:{ 
							       "plantCode":"plantCode",
							       "plantName":"plantName" 
							    },
							    minChars:1
						    }
						}, {
							Qheader: '基本单位编码',
							header: $('purchasingRequisitionDtl.unitCode'),
							dataIndex: 'unitCode',
							width: 80,
							//自定义属性
	                        customAttr:{
	                        	allowBlank:false,
	                        	editable:true//是否显示可以编辑背景颜色
	                        },
							editor: {
								xtype: 'unitComboGrid',
								name: 'unitCode',
						        hiddenName: 'unitCode',
	                			minChars:1,
				    		    fieldMapping:{ 
							       "unitCode":"unitCode",
							       "unitName":"unitName" 
							    }
							}
						}, {
							Qheader: '单位名称',
							header: $('purchasingRequisitionDtl.unitName'),
							dataIndex: 'unitName',
							disabled: true
						}, {
							Qheader: '采购组编码',
							header: $('purchasingRequisitionDtl.purchasingGroupCode'),
							dataIndex: 'purchasingGroupCode',
							width: 80,
							//自定义属性
	                        customAttr:{
	                        	allowBlank:true,
	                        	editable:true//是否显示可以编辑背景颜色
	                        },
							editor: {
						        xtype: 'purchasingGroupComboGrid',
						        name: 'purchasingGroupCode',
						        hiddenName: 'purchasingGroupCode',
						        minChars:1,
				    		    fieldMapping:{ 
							       "purchasingGroupCode":"purchasingGroupCode",
							       "purchasingGroupName":"purchasingGroupName" 
							    }
							}
						}, {
							Qheader: '需求日期',
							header: $('purchasingRequisitionDtl.demandDate'),
							dataIndex: 'demandDate',
							width: 80,
							//自定义属性
	                        customAttr:{
	                        	allowBlank:false,
	                        	editable:true//是否显示可以编辑背景颜色
	                        },
							editor: {
								xtype: 'datefield',
								name: 'demandDate',
								dateFormat: 'Y-m-d',
								minValue : new Date()
							},
							renderer: 'rendererDate'
						}, {
							Qheader: '需求量',
							header: $('purchasingRequisitionDtl.quantityDemanded'),
							dataIndex: 'quantityDemanded',
							width: 80,
							//自定义属性
	                        customAttr:{
	                        	allowBlank:false,
	                        	editable:true//是否显示可以编辑背景颜色
	                        },
							editor: {
								xtype: 'numberfield',
								decimalPrecision: 3,
								allowNegative: false,
								minValue:0,
								name: 'quantityDemanded'
							},
							renderer: 'rendererNumber'
						}, {
							Qheader: '备注',
							header: $('purchasingRequisitionDtl.remark'),
							dataIndex: 'remark',
							width: 120,
							//自定义属性
	                        customAttr:{
	                        	allowBlank:true,
	                        	editable:true//是否显示可以编辑背景颜色
	                        },
							editor: {
								xtype: 'textfield',
								name: 'remark',
								maxLength: 1500
							}
						},
						{Qheader: '采购组名称', header: $('purchasingRequisitionDtl.purchasingGroupName'), dataIndex: 'purchasingGroupName', hidden:true},
						{Qheader: '基本单位名称', header: $('purchasingRequisitionDtl.unitName'), dataIndex: 'unitName', hidden: true },
						{Qheader: '来源(1:正式;2:临时)', header: $('purchasingRequisitionDtl.source'), dataIndex: 'source', disabled: true },
						{Qheader: '采购申请明细id', header: $('purchasingRequisitionDtl.purchasingRequisitionDtlId'), dataIndex: 'purchasingRequisitionDtlId', disabled: true },
						{Qheader: '采购申请id', header: $('purchasingRequisitionDtl.purchasingRequisitionId'), dataIndex: '_purchasingRequisitionId', disabled: true }
					]
				},
				pageSize: 0,
				store: {
					idProperty: 'purchasingRequisitionDtlId',
					url: path_srment + '/cp/purchaseapply/findpurchasingrequisitiondtlall',
					sort: 'rowNo',
					autoLoad: false,
					dir: 'asc'
				},
				listeners: {
					'beforeedit': 'gridDtlBeforeedit',
					'edit': 'gridDtlEdit'
				},
				tbar: [{
					Qtext:"添加正式物料",
					text:$("button.formal"),
					iconCls:"icon-add",
					handler:'gridDtlAddFormalHandler'
				}, {
					Qtext:"添加临时物料",
					text:$("button.temporary"),
					iconCls:"icon-add",
					handler:'gridDtlAddTempHandler'
				}, {
					Qtext:"删除",
					text:$("button.delete"),
					iconCls:"icon-delete",
					handler:'gridDtlDeleteHandler'
				}, {
					name: 'export',
					Qtext: '模板下载',
					text: $('button.download'),
					iconCls: 'icon-download',
					handler: 'gridDtlDownloadHandler'
				}, {
					name: 'import',
					text: $('button.import'),
					iconCls: 'icon-putin',
					handler: 'gridDtlImportHandler'
				}]
			}
		}
    }	
});