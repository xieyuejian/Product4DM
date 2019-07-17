/**
 * @class {Cp.order.PurchaseOrderViewModel}
 * @extend {Ext.ux.app.ViewModel} 采购订单配置
 */
Ext.define('Cp.order.PurchaseOrderViewModel', {
	extend : 'Ext.ux.app.ViewModel',
	alias : 'viewmodel.purchaseOrderViewModel',
	requires : [ 'Ext.ux.form.DateTimeField',
	             'Ext.ux.button.UploadButton', 
	             'Ext.menu.Menu', 
	             'Sl.masterdata.VendorSelectWin', 
	             'Cp.order.MaterialMasterPriceSelectWin', 
	             'Cp.order.InteractionEditWin', 
	             "Ext.srm.form.PurchasingOrganizationComboGrid",
	             "Ext.srm.form.PurchasingGroupComboGrid",
	             "Ext.srm.form.CompanyComboGrid",
	             "Ext.srm.form.VendorComboGrid",
	             "Ext.srm.form.CurrencyComboGrid",
    			 "Ext.srm.dictionary.DictionaryCombo",
	             "Ext.srm.ux.SrmPluploadButton",
	             "Ext.srm.form.SrmFileField",
	             "Ext.srm.ux.ChatTab",
	             "Ext.srm.ux.SrmFileColumn",
	             "Ext.srm.ux.UxFileUtils"],
 	config:{
	data : {
		isExtend:true,
		/**
		 * @cfg {String} dealUrl 方法处理url
		 */
		dealUrl : path_srm + '/cp/purchaseorder',

		/**
		 * @cfg {String} moduleName 模块名称
		 */
		moduleName : $('porder.title'),

		/**
		 * @cfg {String} triggerField 触发域（字段名）
		 */
		vp_triggerField  : 'erpPurchaseOrderNo',
		authorityNameArr:[ "purchaseorder_topass", "purchaseorder_tonopass"],

		/**
		 * @cfg {Array} hideVpBtn 对固化的按钮进行隐藏操作
		 */
		vp_hideListBtn : ["export","prompttrial","grant"],

		/**
		 * @cfg {String} moduleCode 质检单模块编码
		 */
		vp_billTypeCode: 'CGD',
		
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
		 * @cfg {Array} vpSubTab 列表底部tab集合
		 */
 		vp_subTab: [ [ 'detailGrid', 'quantityPanel', 'pricingGrid' ], "chatTab",'logTab', 'msgTab', 'interactionGrid' ],
		/**
		 * @cfg {Object} editWinCenterTab 编辑窗体底部tab集合
		 */
		ew_centerTab: {
			items : [[ 'detailGrid', 'quantityPanel','pricingGrid' ] ,"logDetailGrid", "messageDetailGrid"]
		},

		/**
		 * @cfg {Array} vpListEditStateFn 列表界面按钮控制
		 */
		vp_listEditStateFn : [{
			'view' :true
		}, {
			'edit' : function(r) {
				return r.get('purchaseOrderState') == 'NEW' || ((-1 < s_roleTypes.indexOf("B") || -1 < s_roleTypes.indexOf("A")) && (r.get('purchaseOrderFlowState') == 'NOPASS'));
			}
		}, {
			'delete' : function(r) {
				if (1 != r.get('isRevocationCheck') && (r.get('purchaseOrderState') == 'NEW' || (r.get('purchaseOrderState') == 'RELEASE' && r.get('purchaseOrderFlowState') == 'NOPASS'))) {
					return true;
				} else {
					return false;
				}
			}
		}, {
	    	"grant": function(r){// 授权,审核状态下才允许使用
		 		return r.get('purchaseOrderFlowState') == "CONFIRM";
     		}
      	}, {
			'check' : function(r) { // 订单状态为“关闭”“取消”的订单不能进行撤销审批操作
				return (/* r.get('createType') == "FromInput" && */ r.get('purchaseOrderState') == "OPEN")
						|| (/* r.get('createType') == "FromInput" && */r.get('purchaseOrderState') == "RELEASE" && (r.get("purchaseOrderFlowState") == "PASS" || r.get("purchaseOrderFlowState") == "CONFIRM"));
			}
		},{
        	"prompttrial": function(r){
        		return r.get('purchaseOrderFlowState') == "CONFIRM";
        	}
        }, {
        	"revokeaudit": function(r){// 撤销审核,审核状态下才允许使用
        		return r.get('purchaseOrderFlowState') == "CONFIRM" && s_userid == r.get("createId");
        	}
        }, {
			'synerp' : function(r) {
				// 第一次同步，或者同步失败数据
				if((r.get('erpSynState') == '0' && (r.get('purchaseOrderState') == "open")) || r.get('erpSynState') == '3'){
					return true;
				}
				return false;
			}
		}, {
			'pressingforapproval': function(r) {
				if(r.get('purchaseOrderFlowState')=="CONFIRM"){
					return true;
				}
				
				return false;
			}
		}],

		/**
		 * @cfg {Object} nextBillState - default New 提交后下一步的状态
		 */
		nextBillState : 'New',

		/**
		 * @cfg {Integer} activeTab 默认展示的tab页
		 */
		vp_activeTab : 0,

		/**
		 * @cfg {Integer} tabHeight 底部tab高度
		 */
		vp_tabHeight: 250,

		/**
		 * @cfg {boolean} editWinMaximized 是否最大化窗口，默认为否
		 */
		maximized : true,

		/**
		 * @cfg {String} playListMode normal/audit/undeal //三种列表模式
		 */
		playListMode : "normal",

		/**
		 * @cfg {String} controllerClassName 控制类类名称
		 */
		controllerClassName : 'Cp.order.PurchaseOrderController',

		/**
		 * @cfg {Boolean} searchWinIsShowStatus 查询窗体是否显示状态查询
		 */
		sw_isShowStatus : false,

		/**
		 * @cfg {Integer} searchWinHeight 查询窗体高度
		 */
		sw_Height : 400,

		/**
		 * @cfg {Integer} searchWinWidth 查询窗体宽度
		 */
		sw_Width : 800,

		/**
		 * @cfg {String} searchFormColumnWidth 查询表单每行列数
		 */
		sw_columnWidth: '0.5',

		/**
		 * @cfg {Array} menuOverride 重写右键审核方法 - **TOPASS** - 审核通过 - **TONOPASS** -
		 *      审核不过
		 */
		menuOverride : [ {
			text : $('button.toPass'),
			name : 'TOPASS',
			iconCls : "icon-pass",
			build:true,
			hidden : true,
			handler :'dealState'
		}, {
			text : $('button.toNoPass'),
			name : 'TONOPASS',
			iconCls : "icon-nopass",
			build:true,
			hidden : true,
			handler : 'dealState'
		} ],
		
		isExtend:true,

		/**
		 * @cfg {boolean} isAudit 是否需要右键审核
		 */
		isAudit : true,

		/**
		 * @cfg {boolean} singleSelect 列表是否单选 true
		 */
		singleSelect : true,

		/**
		 * @me {String} methodName 跳转前的方法 true
		 */
		methodName : 'list',

		/**
		 * @cfg {Array} addVpBtn 在固化的按钮基础上追加按钮 - **changeState** - 变更状态 -
		 *      **check** - 撤销审批 - **synErp** - 同步到erp - **export** - 导出 -
		 *      **download** - 下载 - **import** - 导入
		 */
    	vp_addListBtn : [{	
        	build : true,
			name : 'changeState',
			text : $('porder.changeState'),
			index : 4,
			iconCls : 'icon-edit',
			build : true,
			menu: {
				items : [ {
					text : $('porder.accept'),
					name : 'TOACCEPT',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('porder.hold'),
					name : 'TOHOLD',
					hidden : true,
					handler : 'toHold'
				}, {
					text : $('porder.reject'),
					name : 'TOREJECT',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('porder.confirmHold'),
					name : 'TOFIRMHOLD',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('porder.confirmReject'),
					name : 'TOFIRMREJECT',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('porder.realese'),
					name : 'TORELEASE',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('porder.open'),
					name : 'TOOPEN',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('porder.close'),
					name : 'TOCLOSE',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('porder.cancel'),
					name : 'TOCANCEL',
					hidden : true,
					handler : 'dealState'
				}, {
					text : $('button.noOperation'),
					name : 'unstate',
					hidden : true
				}, {
					text : $('porder.pleaseSelect'),
					name : 'sel',
					hidden : false
				} ]
			}
		}, {
			name : 'synerp',
			text : $('button.synerp'),
			disabled : true,
			index : 6,
			iconCls : 'icon-sync',
			build : '#{power.posterp}',
			handler : 'vpSynErpHandler'
		}, {
			name : '审核',
			text : "审核",
			index : 8,
			iconCls : 'icon-export',
			build : s_roleTypes.indexOf("V") >= 0 ? false: true,
			menu: {
		        items: [ {
					name : 'revokeaudit',
					build : '#{power.revokeaudit}',
					text : $('button.revoke'),
					index : 5,
					iconCls : 'icon-cancel',
					handler : 'vpCheckHandler'
				}, {
                    name: "prompttrial",
                    Qtext: "催审",
                    text:"催审",
                    build: '#{power.prompttrial}',
                    iconCls: "icon-edit",
                    handler: "vpPressingforapprovalHandler"
              	},{
              		name: "grant",
                   	text: $("btn.processInsertUser"),
                   	index: 13,
                   	iconCls: "icon-system-user",
                   	build: '#{power.grant}',
                   	handler: "insertUserToProcessFn"
                }]
	    	}
		}, {
			name : '导入导出',
			text : '导入导出',
			index : 11,
			iconCls : 'icon-export',
			build : s_roleTypes.indexOf("V") >= 0 ? false: true,
			menu: {
		        items: [  {
					text : $('button.download'),
					name : "download",
					iconCls : 'icon-download',
					index : 9,
					build : '#{power.download}',
					handler : 'vpDownloadHandler'
				},
				 {
					text : $('button.import'),
					iconCls : 'icon-putin',
					name : "import",
					index : 10,
					build : '#{power.batchimport}',
					handler : 'vpImportHandler'
				},{
					name : 'export',
					text : $('button.Export'),
					index : 8,
					iconCls : 'icon-export',
					build : '#{power.export}',
					handler : 'vpExportHandler'
				},{
	       			name: "export",
	 	            Qtext: "导出",
	 	            text: $("button.exportExcel"),
	 	            build: '#{power.exportexcel}',
	 	            tooltip: $("button.export.tooltip"),
	 	            iconCls: "icon-export",
	 	            handler: "exportExcel"
	 		    }]
		    }
		}],

		/**
		 * @cfg {Array} addEditBtn 在编辑界面固化的按钮基础上追加按钮 - **changeState** - 变更状态 -
		 *      **check** - 撤销审批 - **synErp** - 同步到erp - **export** - 导出 -
		 *      **download** - 下载 - **import** - 导入
		 */
		ew_addEditBtn  : [ {
			text : $('porder.accept'),
			powerEffect : false,
			build:true,
			index : 7,
			name : 'TOACCEPT',
			iconCls : "icon-accept2",
			displayType : [ "view" ],
			hidden : true,
			handler : 'dealState'
		}, {
			text : $('porder.reject'),
			powerEffect : false,
			build:true,
			index : 8,
			name : 'TOREJECT',
			iconCls : "icon-refuse",
			displayType : [ "view" ],
			hidden : true,
			handler : 'dealState'
		}, {
			text : $('porder.hold'),
			powerEffect : false,
			build:true,
			index : 9,
			name : 'TOHOLD',
			iconCls : "icon-choose1",
			displayType : [ "view" ],
			hidden : true,
			handler : 'toHold'
		}, {
			text : $('porder.confirmHold'),
			powerEffect : false,
			build:true,
			index : 10,
			name : 'TOFIRMHOLD',
			iconCls : "icon-accept2",
			displayType : [ "view" ],
			hidden : true,
			handler : 'dealState'
		}, {
			text : $('porder.confirmReject'),
			powerEffect : false,
			build:true,
			index : 11,
			name : 'TOFIRMREJECT',
			iconCls : "icon-accept2",
			displayType : [ "view" ],
			hidden : true,
			handler : 'dealState'
		}/*,{
			text : $('button.toPass'),
			powerEffect : false,
			index : 12,
			name : 'TOPASS',
			iconCls : "icon-pass",
			displayType : [ "view" ],
			hidden : true,
			handler : function(){
				alert(3)
			}//'dealState'
		}, {
			text : $('button.toNoPass'),
			powerEffect : false,
			index : 12,
			name : 'TONOPASS',
			iconCls : "icon-nopass",
			displayType : [ "view" ],
			hidden : true,
			handler : function(){
				alert(4)
			}//'dealState'
		}*/ ,
		{
            name: "grant",
            text: $("btn.processInsertUser"),
            index: 8,
            iconCls: "icon-system-user",
            build: true,
            handler: "insertUserToProcessFn"
        }
		],

		/**
		 * @cfg {Object} gridStore 列表Store
		 */
		vp_gridStore :  {
			idProperty : 'purchaseOrderId',
			url : '#{dealUrl}/list',
			sort : 'purchaseOrderId',
			dir : 'desc',
			remoteSort : true,
			listeners : {
				beforeload :"gridStoreBeforeLoad",
				load :"gridStoreLoad"
			}
		},

		/**
		 * @cfg {Array} gridColumn 列表对象 列属性配置项
		 */
		vp_gridColumn : [ {
			header : $('batchCharacter.purchaseOrderCode'),
			dataIndex : 'erpPurchaseOrderNo',
			width : 140,
			renderer : 'rendererNo',
			tipable:true
		}, {
			header : $('porder.purchaseOrderTime'),
			dataIndex : 'purchaseOrderTime',
			width : 100,
			renderer : 'rendererDate',
			exportRenderer:true
		}, {
			header : $('purchasingOrg.title'),
			dataIndex : 'purchasingOrgCode',
			width : 110,
			tipable:true
		}, {
			header : $('purchasingGroup.title'),
			dataIndex : 'purchasingGroupCode',
			tipable:true
		}, {
			header : $('vendor.code'),
			dataIndex : 'vendorErpCode',
			width : 110,
			tipable:true
		}, {
			header : $('vendor.name'),
			dataIndex : 'vendorName',
			width : 270,
			tipable:true
		}, {
			header : $('porder.purchaseOrderState'),
			dataIndex : 'purchaseOrderState',
			width : 100,
			renderer : 'gridOrderStateRenderer',
			exportRenderer: true
		}, {
			header : $('label.status'),
			dataIndex : 'purchaseOrderFlowState',
			width : 100,
			renderer : 'gridOrderFlowStateRenderer',
			exportRenderer: true
		}, {
			header : $('label.confirmStatus'),
			dataIndex : 'purchaseOrderCheckState',
			width : 100,
			renderer : 'gridOrderCheckStateRenderer',
			exportRenderer: true
		}, {
			header : $('erpSyn.erpSynState'),
			dataIndex : 'erpSynState',
			width : 100,
			renderer : 'gridErpSynStateRenderer',
			exportRenderer: true
		}, {
			header : $('label.createUserName'),
			dataIndex : 'createUserName',
			tipable:true
		}, {
			header : $('porder.orderType'),
			dataIndex : 'purchaseOrderType',
			width : 120,
			renderer : 'gridPurchaseOrderTypeRenderer',
			exportRenderer: true
		}, {
			header : $('porder.purchaseOrderType'),
			dataIndex : 'createType',
			width : 100,
			renderer : 'gridCreateTypeRenderer',
			exportRenderer: true
			
		}, {
			header : $('label.createTime'),
			dataIndex : 'createTime',
			width : 140,
			renderer : 'rendererDateTime',
			exportRenderer:true
		}, {
			header : 'SRM采购订单号',
			dataIndex : 'purchaseOrderNo',
			disabled : true
		}, {
			header : '采购组织名称',
			dataIndex : 'purchasingOrgName',
			disabled : true
		}, {
			header : '供应商编码',
			dataIndex : 'vendorCode',
			disabled : true
		}, {
			header : '公司编码',
			dataIndex : 'companyCode',
			disabled : true
		}, {
			header : '公司名称',
			dataIndex : 'companyName',
			disabled : true
		}, {
			header : '货币编码',
			dataIndex : 'currencyCode',
			disabled : true
		}, {
			header : '货币名称',
			dataIndex : 'currencyName',
			disabled : true
		}, {
			header : '货币汇率',
			dataIndex : 'currencyRate',
			disabled : true
		}, {
			header : '订单金额',
			dataIndex : 'totalAmount',
			disabled : true
		}, {
			header : '附件ID',
			dataIndex : 'uploadFileGroupId',
			disabled : true
		}, {
			header : '供应商查看标识',
			dataIndex : 'viewFlag',
			disabled : true
		}, {
			header : 'ERP返回信息',
			dataIndex : 'erpReturnMsg',
			disabled : true
		}, {
			header : '先确认后审核标识1是先确认，0是先审核',
			dataIndex : 'checkFirst',
			disabled : true
		}, {
			header : '客户端编码',
			dataIndex : 'clientCode',
			disabled : true
		}, {
			header : '备注',
			dataIndex : 'remark',
			disabled : true
		}, {
			header : '是否撤销审批',
			dataIndex : 'isRevocationCheck',
			disabled : true
		}, {
			header : '修改时间',
			dataIndex : 'modifyTime',
			disabled : true
		},{
			header : $('forecast.creatorId'),
			dataIndex : 'createId',
			width : 100,
			disabled:true
		} ],
		vp_gridCfg: {
			stateful : true,
			stateId : s_userCode + '_purchaseOrder',
			stateHeader : true,
			forceFit : false,
			ableExporter:true,
			billNoField: "purchaseOrderNo",
            rn:true//序列列隐藏

		},


		/**
		 * @cfg {Integer} editWinFormHeight 编辑表单高度
		 */
		ew_height : 150,

		/**
		 * @cfg {String} editWinFormColumnWidth 编辑表单列个数
		 */
		ew_columnWidth: '0.33',

		/**
		 * @cfg {Array} editFormItems 订单编辑form表单
		 */
		ew_editFormItems: [ {
			fieldLabel : $('batchCharacter.purchaseOrderCode'),
			name : 'model.erpPurchaseOrderNo',
			readOnly : true
		}, {
			fieldLabel : $('purchasingOrg.title') + '<font color="red">*</font>',
			name : 'model.purchasingOrgCode',
			hiddenName : 'model.purchasingOrgCode',
			editable : true,
			allowBlank : false,
			valueField : 'purchasingOrgCode',
			displayField : 'purchasingOrgName',
			displayValue : 'purchasingOrgCode',
			innerTpl : true,
			queryField : 'purchasingOrgCode',
			xtype: 'purchasingOrganizationComboGrid',
			allowBlank: false, 
          	editable:true, 
         	clearable:true,
			listeners : {
				change : 'formPurchasingOrgCodeChange',
				clear : 'formPurchasingOrgCodeClear'
			}
		}, {
			fieldLabel : $('purchasingGroup.title') + '<font color="red">*</font>',
			name : 'model.purchasingGroupCode',
			hiddenName : 'model.purchasingGroupCode',
			allowBlank : false,
			editable :true,
			forceSelection: true,
			innerTpl : true,
			xtype: 'purchasingGroupComboGrid',
			enabletrigger:false,
            clearable : true,
            minChars:1
		}, {
			fieldLabel : $('company.title'),
			name : 'model.companyCode',
			hiddenName : 'model.companyCode',
			xtype : 'companyComboGrid',
			allowBlank : false,
			editable :true,
			forceSelection: false,
			bind : {
				store:'{companyStore}'
			},
			minChars:1,
			listeners : {
				select : 'formCompanyCodeSelect',
				"triggerbaseparams": "companyCodeTriggerbaseparams", //下拉弹出 共有事件  
				clear : 'formCompanyCodeClear',
				"triggerbeforeshow" : 'formCompanyCodeBeforeexpand',
				"beforeexpand":"formCompanyCodeBeforeexpand"
			}
		}, {
			fieldLabel : $('porder.orderType'),
			name : 'model.purchaseOrderType',
			hiddenName : 'model.purchaseOrderType',
            xtype: "dictionaryCombo",
          	groupCode:"purchasingOrderType",
			allowBlank : false
		}, {
			name : 'model.purchaseOrderTime',
			fieldLabel : $('porder.purchaseOrderTime'),
			xtype : 'datefield',
			value : new Date(),
			format : 'Y-m-d',
			exportRenderer:true
		}, {
			fieldLabel : $('vendor.code') + '<font color="red">*</font>',
			name : 'model.vendorErpCode',
			editable :true,
			allowBlank : false,
			clearable : true, // 是否带清空按钮
			xtype: 'vendorComboGrid',
			enabletrigger:true,
          	minChars:1,
         	listeners : {
				trigger : 'formVendorErpCodeTrigger',
				triggerbaseparams:"vendorErpCodeTriggerBaseParams",
			    aftersetvalue:"vendorErpCodeSetValueAfter", //弹窗框选择完事件
			    aftercombosetvalue: "vendorErpCodeSetValueAfter",//下拉框选择完事件
				"triggerbeforeshow" : 'formCompanyCodeBeforeexpand',
				"beforeexpand":"formCompanyCodeBeforeexpand",
				"clear" : "formVendorErpCodeClear"
			}
		}, {
			fieldLabel : $('vendor.name'),
			name : 'model.vendorName',
			readOnly : true
		}, {
			fieldLabel : $('currency.title'),
			name : 'model.currencyCode',
			hiddenName : 'model.currencyCode',
			editable :true,
			allowBlank : false,
			clearable : true, // 是否带清空按钮
			xtype: 'currencyComboGrid',
			enabletrigger:false,
          	minChars:1,
          	listConfig:{
          		listeners : {
					select : 'currencyCodeSelect'
				}
          	}
		}, {
			name : 'model.currencyRate',
			fieldLabel : $('currency.exchangeRate'),
			allowBlank : false,
			xtype : 'numberfield',
			decimalPrecision :6,
			readOnly : true
		}, {
			fieldLabel : $('porder.totalAmount'),
			name : 'model.totalAmount',
			xtype : 'numberfield',
			decimalPrecision : 2,
			readOnly : true
		}, {
			name : 'model.remark',
			fieldLabel : $('porder.text')
		
		}, { 
        	fieldLabel : $('label.AttachmentsCheck'),
            xtype: "srmfilefield", 
            name: "uploadFile4View",
            //columnWidth: 0.8, 
            listeners:{
               "operation": "operation"
            } 
        }, {
            name: "upload",
            xtype: "srmpluploadbutton", 
            text: "附件上传",
            fileGroupIdField:"model.uploadFileGroupId",//根据自己配置附件组id存储字段
            fileViewField:"uploadFile4View"//根据自己配置附件渲染字段,
            //columnWidth: 0.2
        }, {
            QfieldLabel: "附件",
            xtype: "srmfileidfield",
            hidden:true,
            fieldLabel: $("censorQuality.uploadFileGroupId"),
            fileViewField:"uploadFile4View",//附件渲染字段
            name: "model.uploadFileGroupId"
        }, {
			fieldLabel : "订单ID",
			name : 'model.purchaseOrderId',
			xtype:"hidden"
		},{
			fieldLabel : "订单状态",
			name : 'model.purchaseOrderState',
			xtype:"hidden"
		}, {
			name : 'model.modifyTime',
			fieldLabel : '修改时间',
			xtype:"hidden"
		}, {
			name : 'model.vendorCode',
			fieldLabel : '供应商编码',
			xtype:"hidden"
		}, {
			name : 'model.purchasingGroupName',
			fieldLabel : '采购组名称',
			xtype:"hidden"
		},{
			name : 'model.purchaseOrderNo',
			fieldLabel : 'SAP采购订单号',
			xtype:"hidden"
		}, {
			name : 'model.purchasingOrgName',
			fieldLabel : '采购组名称',
			xtype:"hidden"
		}, {
			name : 'model.companyName',
			fieldLabel : '公司名称',
			xtype:"hidden"
		}, {
			name : 'model.currencyName',
			fieldLabel : '货币名称',
			xtype:"hidden"
		}, {
			name : 'model.purchaseOrderFlowState',
			fieldLabel : '审核状态',
			xtype:"hidden"
		}, {
			name : 'model.purchaseOrderCheckState',
			fieldLabel : '流程状态',
			xtype:"hidden"
		}, {
			name : 'model.erpSynState',
			fieldLabel : 'ERP同步标识,0未同步,1已同步',
			xtype:"hidden"
		}, {
			name : 'model.createType',
			fieldLabel : '创建方式',
			xtype:"hidden"
		}, {
			name : 'model.viewFlag',
			fieldLabel : '供应商查看标识',
			xtype:"hidden"
		}, {
			name : 'model.erpReturnMsg',
			fieldLabel : 'ERP返回信息',
			xtype:"hidden"
		}, {
			name : 'model.checkFirst',
			fieldLabel : '先确认后审核标识1是先确认，0是先审核',
			xtype:"hidden"
		}, {
			name : 'model.clientCode',
			fieldLabel : '客户端编码',
			xtype:"hidden"
		}, {
			name : 'model.isRevocationCheck',
			fieldLabel : '是否撤销审批',
			xtype:"hidden"
		}, {
			name : 'model.isVendorView',
			fieldLabel : '供应商可查看标识',
			xtype:"hidden"
		}, {
			name : 'model.internationlTradeRemark',
			fieldLabel : '国际贸易条件说明',
			xtype:"hidden"
		}, {
			name : 'model.taxRateCode',
			fieldLabel : '税率编码',
			xtype:"hidden"
		}, {
			name : 'taxRateCode',
			fieldLabel : '税率编码',
			submitValue : false,
			xtype:"hidden"
		}  ],

		/**
		 * @cfg {Object} detailGrid 订单编辑明细
		 */
		detailGrid : {
			region : 'center',
			allowEmpty : false,
			name : 'detailGrid',
			tabTitle : $('porder.detail'),
			xtype: 'uxeditorgrid',
			foreignKey : 'purchaseOrder_purchaseOrderId',
			tabClassName : 'purchaseOrderDetails',
			formFieldReadyArr : [ 'model.purchasingOrgCode', 'model.vendorErpCode', 'model.companyCode', 'model.purchaseOrderType', 'model.currencyCode' ], // 编辑细单需要只读的字段
			noValidation : true,
			stateHeader: true,
			stateful : true,
			stateId : s_userCode + '_purchaseOrderDtl',
			viewConfig : {
				forceFit : true
			},
			enableColumnHide : false,
			cm : {
				defaultSortable : false,
				defaults : {
					menuDisabled : false
				},
				columns : [ {
					dataIndex : "unitConversionInfo",
					disabled: true
				},{
					dataIndex : "pricingInfo",
					disabled: true
				},{
					header : $('porder.lineNo'),
					dataIndex : 'srmRowids'
				}, {
					header : $('porder.closeFlag'),
					width : 100,
					dataIndex : 'closeFlag',
					renderer : 'gridDtlCloseFlagRenderer'
				}, {
					header : $('porder.lineItemType'),
					width : 110,
					dataIndex : 'lineItemTypeCode',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{lineItemTypeStore}'
						},
						displayField : 'itemName',
						valueField : 'itemCode'
					},
					renderer : 'gridDtlLineItemTypeCodeRenderer'
				}, {
					header : $('materialInfo.code'),
					width : 100,
					dataIndex : 'materialCode'
				}, {
					header : $('materialInfo.name'),
					width : 250,
					dataIndex : 'materialName',
					editor : {
						xtype : 'textfield'
					},
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					}
				}, {
					header : $('materialMasterPriceApply.orderElementaryUnit'),
					width : 80,
					dataIndex : 'unitCode',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{unitStore}'
						},
						displayField : 'unitCode',
						valueField : 'unitCode',
						listWidth : 120,
						listeners : {
							select : 'gridDtlUnitCodeSelect'
						}
					}
				}, {
					header : $('sendscheduledetail.sendQty1'),
					width : 120,
					align : "right",
					dataIndex : 'buyerQty',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						width : 120,
						xtype : 'numberfield',
						decimalPrecision : 3,
						allowNegative : false,
						minValue : 0.00000000000001
					},
					renderer : 'rendererNumber'
				}, {
					header : $('label.deliveryDate'),
					width : 130,
					dataIndex : 'buyerTime',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'datefield',
						format : 'Y-m-d'
					},
					renderer : 'rendererDate'
				}, {
					header : $('porder.vendorTime'),
					width : 130,
					dataIndex : 'vendorTime',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'datefield',
						format : 'Y-m-d'
					},
					renderer : 'rendererDate'
				}, {
					header : $('sendscheduledetail.message.msg')+$('label.notTaxPrice'),
					width : 120,
					align : 'right',
					dataIndex : 'buyerPrice',
					renderer : 'rendererNumber'
				}, {
					Qheader:"定价单位",
					header : $('purchasingRecord.pricingunit'),
					dataIndex:"pricingUnit"
					
				},{
					Qheader : "订单行金额",
					header : $('sendscheduledetail.message.msg')+$('porder.itemAmount'),
					width : 100,
					align : 'right',
					dataIndex : 'lineItemValAmt',
					renderer : 'rendererNumber'
				}, {
					header : $('plant.title'),
					width : 150,
					dataIndex : 'plantCode',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{plantStore}'
						},
						displayField : 'plantName',
						valueField : 'plantCode',
						innerTpl : true,
						listeners : {
							focus : 'gridDtlPlantCodeFocus',
							select : 'gridDtlPlantCodeSelect'
						}
					}
				},  {
					header : $('porder.stockType'),
					width : 150,
					dataIndex : 'stockType',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{stockTypeStore}'
						},
						innerTpl : true,
						displayField : 'itemName',
						valueField : 'itemCode'
					},
					renderer : 'gridDtlStockTypeRenderer'
				}, 
				{
					header : $('label.storageLocation'),
					width : 150,
					dataIndex : 'storeLocal',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{storLocStore}'
						},
						displayField : 'stockLocationName',
						valueField : 'stockLocationCode',
						innerTpl : true,
						minListWidth : 250,
						listeners : {
							focus : 'gridDtlStoreLocalFocus'
						}
					}
				}, {
					header : $('taxRate.taxRateCode'),
					width : 100,
					dataIndex : 'taxRateCode',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{taxRateStore}'
						},
						displayField : 'taxRateCode',
						valueField : 'taxRateCode'
					}
				}, {
					header : $('porder.overDeliveryLimit'),
					width : 170,
					dataIndex : 'overDeliveryLimit',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'numberfield',
						decimalPrecision : 2,
						allowNegative : false
					},
					renderer : 'rendererNum2Fn'
				}, {
					header : $('porder.shortDeliveryLimit'),
					width : 170,
					dataIndex : 'shortDeliveryLimit',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'numberfield',
						decimalPrecision : 2,
						allowNegative : false
					},
					renderer : 'rendererNum2Fn'
				}, {
					header : $('porder.isReturn'),
					width : 150,
					dataIndex : 'isReturn',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{flagStore}'
						},
						displayField : 'display',
						valueField : 'value'
					},
					renderer : 'gridDtlIsReturnRenderer'
				}, {
					header : $('porder.isFree'),
					width : 150,
					dataIndex : 'isFree',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{flagStore}'
						},
						displayField : 'display',
						valueField : 'value'
					},
					renderer : 'gridDtlIsReturnRenderer'
				}, {
					header : $('porder.scheduelFlag'),
					width : 150,
					dataIndex : 'scheduleFlag',
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{flagStore}'
						},
						displayField : 'display',
						valueField : 'value'
					},
					renderer : 'gridDtlIsReturnRenderer'
				}, {
					header : $('label.remark'),
					width : 150,
					dataIndex : 'remark',
					tipable:true,
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor : {
						xtype : 'textfield'
					}
				}, {
					header : $('porder.deleteFlag'),
					dataIndex : 'deleteFlag',
					editor : {
						xtype : 'uxcombo',
						bind : {
							store : '{flagStore}'
						},
						displayField : 'display',
						valueField : 'value'
					},
					renderer : 'gridDtlIsReturnRenderer'
				}, {
					header : $('erpSyn.erpSynState'),
					dataIndex : 'erpSynState',
					width : 180,
					renderer : 'gridErpSynStateRenderer'
				},

				// hieedn fields
				{
					header : 'erp行号',
					width : 70,
					dataIndex : 'rowIds',
					disabled : true
				}, {
					header : '订单明细id',
					dataIndex : 'purchaseOrderDetailId',
					disabled : true
				}, {
					header : '价格_供应商',
					dataIndex : 'vendorPrice',
					hidden : true
				}, {
					header : '数量_供应商',
					dataIndex : 'vendorQty',
					hidden : true
				}, {
					header : '送货数量',
					dataIndex : 'qtySend',
					hidden : true
				}, {
					header : '在途数量',
					dataIndex : 'qtyOnline',
					hidden : true
				}, {
					header : '到货数量',
					dataIndex : 'qtyArrive',
					hidden : true
				}, {
					header : '检验数量',
					dataIndex : 'qtyCheck',
					hidden : true
				}, {
					header : '合格数量',
					dataIndex : 'qtyAccord',
					hidden : true
				}, {
					header : '不合格数量',
					dataIndex : 'qtyNaccord',
					hidden : true
				}, {
					header : '入库数量',
					dataIndex : 'qtyStore',
					hidden : true
				}, {
					header : '退货数量',
					dataIndex : 'qtyQuit',
					hidden : true
				}, {
					header : '送货(库存)地点',
					dataIndex : 'deliverystoreLocal',
					hidden : true
				}, {
					header : '已排程数量',
					dataIndex : 'scheduledQty',
					hidden : true
				}, {
					header : '行项目类别编码',
					dataIndex : 'lineItemTypeCode',
					hidden : true
				}, {
					header : '工厂名称',
					dataIndex : 'plantName',
					hidden : true
				}, {
					header : '物料组编码',
					dataIndex : 'materialGroupCode',
					hidden : true
				}, {
					header : '物料组名称',
					dataIndex : 'materialGroupName',
					hidden : true
				}, {
					header : '价格单位代码',
					dataIndex : 'priceUnitCode',
					hidden : true
				}, {
					header : '价格单位名称',
					dataIndex : 'priceUnitName',
					hidden : true
				}, {
					header : '税率编码',
					dataIndex : 'taxRateCode',
					hidden : true
				}, {
					header : '未送数量',
					dataIndex : 'unCount',
					hidden : true
				}, {
					header : 'ERP返回信息',
					dataIndex : 'erpReturnMsg',
					disabled : true
				}, {
					header : '紧急标识',
					dataIndex : 'emergencyFlag',
					hidden : true
				}, {
					header : '数据来源',
					dataIndex : 'sourceCode',
					hidden : true
				}, {
					header : '操作标识',
					dataIndex : 'operate',
					hidden : true
				}, {
					header : '单位名称',
					dataIndex : 'unitName',
					hidden : true
				}, {
					header : '已收货量',
					dataIndex : 'receiveQty',
					hidden : true
				}, {
					header : '可送数量',
					dataIndex : 'canSendQty',
					hidden : true
				}, {
					header : '价格主数据ID',
					dataIndex : 'materialMasterPriceId',
					disabled : true
				}, {
					header : '细单ID',
					dataIndex : 'materialMasterPriceDtlId',
					disabled : true
				}, {
					header : '价格ID',
					dataIndex : 'materialLadderPriceDtlId',
					disabled : true
				}, {
					header : '研发项目号',
					dataIndex : 'pdProjectNumber',
					hidden : true
				}, {
					header : '科目分配类别',
					dataIndex : 'accountAllocationTypeCode',
					hidden : true
				}, {
					header : '资产号',
					dataIndex : 'assetNumber',
					hidden : true
				}, {
					header : '成本中心',
					dataIndex : 'costCenter',
					hidden : true
				}, {
					header : '总账科目',
					dataIndex : 'generalLedgerSubject',
					hidden : true
				}, {
					header : '供应商是否可以查看',
					dataIndex : 'isVendorView',
					hidden : true
				}, {
					header : '采购申请明细归集ID',
					dataIndex : 'purchasingRequisitionColId',
					disabled : true
				} ]
			},
			listeners : {
				'edit' : 'gridDtlEdit',
				'beforeedit' : 'gridDtlBeforeedit',
				'itemclick' : 'gridDtlSelectionchange'
			},
			sm : {
				singleSelect : true
			},
			pageSize : 0,
			store : {
				idProperty : 'purchaseOrderDetailId',
				url : path_srm + '/cp/purchaseorderdetail/findall',
				sort : 'srmRowids',
				autoLoad : false,
				dir : 'asc'
			},
			tbar : [ {
				// 来源价格主数据
				text : $('porder.sourcepurchasingRecord'),
				iconCls : 'icon-add',
				handler : 'gridDtlAddPriceHandler'
			}, {
				// 手动添加明细
				text : $('porder.sourceManual'),
				iconCls : 'icon-add',
				handler : 'gridDtlAddManualHandler'
			}, {
				// 导入
				text : $('button.import'),
				iconCls : 'icon-putin',
				handler : 'gridDtlImportHandler'
			}, {
				// 模版下载
				text : $('button.download'),
				iconCls : 'icon-download',
				handler : 'gridDtlDownloadHandler'
			}, {
				// 删除
				text : $('button.delete'),
				name : 'deleteBtn',
				iconCls : 'icon-delete',
				handler : 'gridDtlDeleteHandler'
			}, {
				// 关闭
				text : $('button.close'),
				name : 'closeBtn',
				iconCls : 'icon-close',
				handler : 'gridDtlCloseOrCancelCloseHandler'
			}, {
				// 取消关闭
				text : $('button.cancel') + $('button.close'),
				name : 'cancelCloseBtn',
				iconCls : 'icon-cancel',
				handler : 'gridDtlCloseOrCancelCloseHandler'
			} ]
		},

		/**
		 * @cfg {Object} pricingGrid 订单明细价格
		 */
		pricingGrid : {
			name : 'pricingGrid',
			region : 'center',
			tabTitle : $('porderDtl.purchaseOrderPricings'),
			xtype : 'uxeditorgrid',
			foreignKey : 'purchaseOrderDetail_purchaseOrderDetailId',
			tabClassName : 'purchaseOrderPricings',
			noValidation : true,
			formFieldReadyArr : [], // 细单有值时设置主单是否只读
			sourceField : "pricingInfo",
			autoDtlLoad : false,
			enableColumnHide : false,
			stateHeader: true,
			stateful : true,
			stateId : s_userCode + '_purchaseOrderPricing',
			cm : {
				defaultSortable : false,
				defaults : {
					menuDisabled : false
				},
				columns : [ {
					header : $('porderDtl.purchaseOrderPricingTypeCode'),
					dataIndex : 'purchaseOrderPricingTypeCode',
					width : 140,
					renderer : 'pricingGridDtlTypeCodeRenderer',
					editor : {
						xtype : "uxcombo",
						bind : {
							store : '{pricingConditionTypeStore}'
						},
						valueField : "itemCode",
						displayField : "itemName"
					}
				}, {
					header : $('porderDtl.purchaseOrderPricingTypeName'),
					dataIndex : "purchaseOrderPricingTypeName",
					hidden : true
				}, {
					header : $('porder.priceInCondition'),
					dataIndex : 'pricingQty',
					align : 'right',
					width : 80,
					renderer : 'pricingGridDtlPricingQtyRenderer',
					editor : {
						xtype : 'numberfield',
						decimalPrecision : 2,
						minValue : 0,
						allowNegative : false,
						allowBlank : false
					}
				}, {
					header : $('inquiryMatDtl.priceBase'),
					dataIndex : 'priceUnit',
					align : 'right',
					renderer : 'gridEditBgColorRenderer',
					editor : {
						xtype : 'numberfield',
						allowDecimal : false,
						allowNegative : false,
						allowBlank : false,
						minValue : 0
					}
				}, {
					header : $('porderDtl.amount'),
					align : 'right',
					dataIndex : 'amount',
					renderer : 'pricingGridDtlAmountRenderer'
				},
				{
					header : $('porderDtl.purchaseOrderPricingId'),
					dataIndex : 'purchaseOrderPricingId',
					hidden : true
				}, {
					header : '采购订单编号',
					dataIndex : 'purchaseOrderDetail_purchaseOrderDetailId',
					hidden : true
				}, {
					header : '定价条件行ID',
					dataIndex : 'purchaseOrderPricingRowId',
					hidden : true
				}, {
					header : 'SAP采购细单行项目号',
					dataIndex : 'rowIds',
					hidden : true
				}, {
					header : '币种',
					dataIndex : 'curType',
					hidden : true
				}

				]
			},
			listeners : {
				'edit' : 'gridPricingDtlEdit',
				'beforeedit' : 'gridPricingDtlBeforeedit'
			},
			pageSize : 0,
			store : {
				idProperty : 'purchaseOrderPricingId',
				url : path_srm + '/cp/purchaseorder/getpurchaseorderpricing',
				sort : 'purchaseOrderPricingId',
				dir : 'desc'
			}
		},

		/**
		 * @cfg {Object} quantityPanel 订单明细单位转换
		 */
		quantityPanel : {
			name : 'quantityPanel',
			tabTitle : $('porderDtl.titleQuantity'),
			xtype : 'formpanel',
			border : false,
			foreignKey : 'purchaseOrderDetail_purchaseOrderDetailId',
			tabClassName : 'purchaseDualUnitConversions',
			saveField : [  ],
			noValidation : true,
			loadUrl : path_srm + '/cp/purchaseorder/getpurchasedualunitconversion',
			sourceField : "unitConversionInfo",
			autoDtlLoad : false,
			items : [ {
				xtype : 'textfield',
				hidden : true,
				name : 'purchaseOrderQtyId'
			}, {
				xtype : 'fieldset',
				title : $('porderDtl.titleUnitChange'),
				layout : 'form',
				style : 'padding:5px;',
				columnWidth : 1,
				defaults : {
					layout : 'column',
					border : false
				},
				items : [ {
					defaults : {
						xtype : 'textfield',
						readOnly : true
					},
					layout : 'column',
					columnWidth : .2,
					items : [ {
						xtype : 'numberfield',
						name : 'convertMolecular2', // 转换分子2
						style : {
							'text-align' : 'right'
						},
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'right'
						},
						width : '30%'
					}, {
						name : 'orderDetailUnit', // 采购订单单位
						xtype : 'textfield',
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'right'
						},
						width : '10%'
					}, {
						xtype : 'label',
						text : '<==>',
						width : '20%',
						style : {
							'text-align' : 'center',
							'padding-top' : '4px'
						}
					}, {
						xtype : 'numberfield',
						name : 'convertDenominator2', // 转换分母2
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'right'
						},
						width : '30%'
					}, {
						name : 'pricingUnit', // 定价单位
						xtype : 'textfield',
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'right'
						},
						width : '10%'
					} ]
				} ]
			}, {
				xtype : 'fieldset',
				title : $('porderDtl.titleUnitChange1'),
				layout : 'form',
				style : 'padding:5px;',
				columnWidth : 1,
				defaults : {
					layout : 'column',
					border : false
				},
				items : [ {
					defaults : {
						xtype : 'textfield',
						readOnly : true
					},
					layout : 'column',
					columnWidth : .2,
					items : [ {
						xtype : 'numberfield',
						name : 'convertMolecular', // 转换分子1
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'right'
						},
						width : '30%'
					}, {
						name : 'orderDetailUnit2', // 采购订单单位
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'center'
						},
						xtype : 'textfield',
						width : '10%'
					}, {
						xtype : 'label',
						text : '<==>',
						width : '20%',
						style : {
							'text-align' : 'center',
							'padding-top' : '4px'
						}
					}, {
						xtype : 'numberfield',
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'right'
						},
						width : '30%',
						name : 'convertDenominator' // 转换分母1
					}, {
						name : 'unitCode', // sku
						style : {
							padding : '0px',
							margin : '0px',
							'text-align' : 'center'
						},
						xtype : 'textfield',
						width : '10%'
					} ]
				} ]
			}, {
				QfieldLabel :"定价数量",//定价数量 = 订单数量*定价单位/订单单位
				fieldLabel : $('porderDtl.pricingQty'),
				readOnly : true,
				name : 'pricingQty',
				format : '0.000',
				columnWidth : 1,
				decimalPrecision : 3,
				labelWidth : 120,
				xtype : 'numberfield'
			}, {
				QfieldLabel : "SKU数量",//SKU数量 = 订单数量 * 基本单位/订单单位
				fieldLabel : $('porderDtl.posku'),
				readOnly : true,
				labelWidth : 120,
				name : 'skuQty',
				format : '0.000',
				columnWidth : 1,
				decimalPrecision : 3,
				xtype : 'numberfield'
			}, {
				fieldLabel : '<font color="red">' + $('porder.remark') + '</font>',
				xtype : 'displayfield',
				name : 'comment',
				columnWidth : 1,
				value : '<font color="red">' + $('porderDtl.message.warnMsg2') + '</font>'
			}, {
				name : 'purchaseOrderQtyId',
				xtype : 'hidden',
				fieldLabel : $('porderDtl.purchaseOrderQtyId')
			} ]
		},

		/**
		 * @cfg {Array} interactionGrid 订单交互
		 */
		interactionGrid : {
			xtype : 'uxgrid',
			name : 'interactionGrid',
			tabClassName : 'interactionGrids',
			tabTitle : $('priceQuote.interaction'),
			trackMouseOver : false,
			disableSelection : true,
			border : false,
			loadMask : true,
			stripeRows : false,
			allowEmpty : true,
			stateHeader: true,
			stateful : true,
			stateId : s_userCode + '_purchaseOrderInteraction',
			viewConfig : {
				forceFit : false
			},
			store : {
				idProperty : 'billBbsId',
				url :  path_console + '/sys/billbbs/list',
				sort : 'createTime',
				autoLoad : false,
				dir : 'asc'
			},
			cm : {
				columns : [ {
					header : $('label.createUserName'),
					width : 90,
					dataIndex : 'createUserName'
				}, {
					header : $('biddingDelBidDefJ.content'),
					width : 300,
					dataIndex : 'bbsContent',
                    tipable:true
				}, {
					header : $('label.createTime'),
					width : 150,
					dataIndex : 'createTime',
					renderer : 'rendererDateTime'
				}, {
					header : $('porder.viewUploadFile'),
					dataIndex : 'fileViewField',
					width : 500,
                    editor: {
                        xtype: "srmfilefield",
                        name:"fileViewField"
                    }, 
                    fileGroupIdField:"uploadFileGroupId",//根据自己配置附件组id存储字段
                    xtype:'srmfilecolumn'
				}, {
					dataIndex : 'billBbsId',
					hidden : true
				}, {
					dataIndex : 'createUserId',
					hidden : true
				} ]
			},
			vpShowTbar : true,
			tbar : [ {
				text : $('bbs.add'), // 新建交互信息
				hidden : !power['addinteraction'],
				iconCls : 'icon-add',
				handler : 'interactionGridAddHandler'
			}, {
				text : $('bbs.view'), // 查看交互信息
				hidden : !power['viewinteraction'],
				iconCls : 'icon-view',
				handler : 'interactionGridViewHandler'
			}, {
				text : $('button.delete'), // 查看交互信息
				hidden : !power['deleteinteraction'],
				iconCls : 'icon-delete',
				name : 'delete',
				disabled : true,
				handler : 'interactionGridDeleteHandler'
			} ]
		},
		
		/**
         * @cfg {Object} messageDetailGrid 审核日志
         */
        messageDetailGrid : {
            tabTitle : $("flowInfo.dealComment"),
            xtype : 'uxeditorgrid',
            tabClassName : "dealComments",
            validField : [], // 需要提交的细单字段,
            formFieldReadyArr : [], // 编辑细单需要只读的字段
            forceFit : true,
            stateHeader : true,
            disableSelection: true,
            autoUploadField: true,
            stateful : true,
            viewConfig : {
                forceFit : true,// 当行大小变化时始终填充满
                autoScroll : true
            },
            allowEmpty : true, // 明细条数是否可以为空
            cm : {
                defaultSortable : false,
                defaults : {
                    menuDisabled : true
                },
                columns : [{
							header : $("flowInfo.roleName"),
							dataIndex : "role.roleName",
							renderer: function(v, m, r) {
								if (Ext.isEmpty(v)) {
									v = "";
								}
								m.tdAttr = "data-qtip='" + v + "'";
								return v;
							}
						}, {
							header : $("flowInfo.userName"),
							dataIndex : "userName"
						}, {
							header : $("flowInfo.dealName"),
							dataIndex : "dealName"
						}, {
							header : $("flowInfo.dealComment"),
							dataIndex : "dealComment",
							renderer: function(v, m, r) {
								if (Ext.isEmpty(v)) {
									v = "";
								}
								m.tdAttr = "data-qtip='" + v + "'";
								return v;
							}
						}, {
							header : $("flowInfo.dealTime"),
							dataIndex : "dealTime"
						}, {
			                header: $("file.annex"),
			                name: "downloadBtn",
			                dataIndex: "uploadFileGroupId",
			                renderer: 'dealMsgRenderer'
			            }]
            },
            listeners : {},
            pageSize : 0,
            store : {
                url : path_console + "/bpm/message/list",
                sort : 'dealTime',
                autoLoad : false,
                dir : 'desc',
                baseParams: {
                    processKey: "CGD",
                    businessKey: "purchaseOrderId"
                }
            }
        },
        /**
         * @cfg {Object} logDetailGrid 操作日志
         */
        logDetailGrid : {
            tabTitle : $("logOperation.logOperation"),
            xtype : 'uxeditorgrid',
            tabClassName : "operationLog",
            forceFit : true,
            stateHeader : true,
            disableSelection: true,
            autoUploadField: true,
            stateful : true,
            viewConfig : {
                forceFit : true,// 当行大小变化时始终填充满
                autoScroll : true
            },
            allowEmpty : true, // 明细条数是否可以为空
            cm : {
                defaultSortable : false,
                defaults : {
                    menuDisabled : true
                },
                columns : [
					{header: $("logOperation.operatorName"), dataIndex: "operatorName"}, 
					{header: $("logOperation.message"), dataIndex: "message", renderer: function(v, m, r) {
								if (Ext.isEmpty(v)) {
									v = "";
								}
								m.tdAttr = "data-qtip='" + v + "'";
								return v;
							}
					}, 
					{header: $("logOperation.createTime"), dataIndex: "createTime"}]
            },
            listeners : {},
            pageSize : 20,
            store : {
				idProperty: "logId",
				sort: "createTime",
				dir: "desc",
				url: path_console + "/sys/logoperation/list",
				autoLoad: false
            }
        },

		/**
		 * @cfg {Array} searchFormItems 订单查询字段集合
		 */
		sw_searchFormItems : [ {
			fieldLabel : $('porder.purchaseOrderTime'),
			name : 'filter_GE_purchaseOrderTime',
			xtype : 'datefield',
			format : 'Y-m-d'
		}, {
			fieldLabel : $('label.to'),
			name : 'filter_LE_purchaseOrderTime',
			xtype : 'datefield',
			format : 'Y-m-d'
		}, {
			QfieldLabel:'创建时间', 
			fieldLabel:$('label.createTime'), 
			name:'filter_GE_createTime', 
			xtype:'datefield', 
			format:'Y-m-d'
		}, {
			QfieldLabel:'至', 
			fieldLabel:$('label.to'), 
			name:'filter_LE_createTime', 
			xtype:'datefield', 
			format:'Y-m-d'
		}, {
			fieldLabel : $('purchasingOrg.code'),
			name : 'filter_LIKE_purchasingOrgCode'
		}, {
			fieldLabel : $('purchasingGroup.code'),
			name : 'filter_LIKE_purchasingGroupCode'
		}, {
			fieldLabel : $('materialInfo.code'),
			name : 'filter_EQ_purchaseOrderDetails_materialCode'
		}, {
			fieldLabel : $('materialInfo.name'),
			name : 'filter_LIKE_purchaseOrderDetails_materialName'
		}, {
			fieldLabel : $('vendor.code'),
			name : 'filter_LIKE_vendorErpCode'
		}, {
			fieldLabel : $('vendor.name'),
			name : 'filter_LIKE_vendorName'
		}, {
			fieldLabel : $('label.createUserName'),
			name : 'filter_LIKE_createUserName'
		}, {
			fieldLabel : $('batchCharacter.purchaseOrderCode'),
			name : 'filter_LIKE_purchaseOrderNo_OR_erpPurchaseOrderNo'
		}, {
			fieldLabel : $('porder.purchaseOrderFlowState'),
			xtype : 'checkboxgroup',
			columns : 5,
			height : 30,
			labelAlign : "right",
			labelSeparator : '：',// 分隔符
			columnWidth : 1,
			anchor : '95%',
			items : [ {
				boxLabel : $('state.confirm'),
				name : 'purchaseOrderFlowState',
				inputValue : "CONFIRM"
			}, {
				boxLabel : $('state.pass'),
				name : 'purchaseOrderFlowState',
				inputValue : "PASS"
			}, {
				boxLabel : $('state.nopass'),
				name : 'purchaseOrderFlowState',
				inputValue : "NOPASS"
			} ]
		}, {
			fieldLabel : $('porder.purchaseOrderState'),
			xtype : 'checkboxgroup',
			columns : 5,
			height : 30,
			labelAlign : "right",
			columnWidth : 1,
			anchor : '95%',
			name : 'purchaseOrderState',
			items : [ {
				boxLabel : $('state.new'),
				name : 'purchaseOrderState',
				inputValue : 'NEW',
				width : 103
			}, {
				boxLabel : $('state.release'),
				name : 'purchaseOrderState',
				inputValue : 'RELEASE',
				width : 117
			}, {
				boxLabel : $('porder.purchaseOrderStateOpen'),
				name : 'purchaseOrderState',
				inputValue : 'OPEN',
				width : 117
			}, {
				boxLabel : $('label.close'),
				name : 'purchaseOrderState',
				inputValue : 'CLOSE',
				width : 117
			}, {
				boxLabel : $('state.cancel'),
				name : 'purchaseOrderState',
				inputValue : "CANCEL"
			} ]
		}, {
			fieldLabel : $('erpSyn.erpSynState'),
			name : 'erpSynState',
			xtype : 'checkboxgroup',
			columns : 5,
			height : 30,
			columnWidth : 1,
			labelAlign : "right",
			anchor : '95%',
			items : [ {
				boxLabel : $('erpSyn.nosyn'),
				name : 'erpSynState',
				inputValue : '0'
			}, {
				boxLabel : $('erpSyn.onsyn'),
				name : 'erpSynState',
				inputValue : '2',
				width : 117
			}, {
				boxLabel : $('erpSyn.synsuccess'),
				name : 'erpSynState',
				inputValue : '1',
				width : 117
			}, {
				boxLabel : $('erpSyn.synfail'),
				name : 'erpSynState',
				inputValue : "3"
			}, {
				boxLabel : $('erpSyn.noneed'),
				name : 'erpSynState',
				inputValue : '-1'
			} ]
		}, {
			fieldLabel : $('porder.checkState'),
			xtype : 'checkboxgroup',
			columns : 5,
			height : 30,
			labelAlign : "right",
			columnWidth : 1,
			anchor : '95%',
			items : [ {
				boxLabel : $('porder.purchaseOrderCheckStateCheck'),
				name : 'purchaseOrderCheckState',
				inputValue : "CONFIRM"
			}, {
				boxLabel : $('porder.purchaseOrderCheckStateAccept'),
				name : 'purchaseOrderCheckState',
				inputValue : 'ACCEPT',
				width : 117
			}, {
				boxLabel : $('porder.purchaseOrderCheckStateHold'),
				name : 'purchaseOrderCheckState',
				inputValue : 'HOLD',
				width : 117
			}, {
				boxLabel : $('button.reject'),
				name : 'purchaseOrderCheckState',
				inputValue : 'REJECT',
				width : 117
			}, {
				boxLabel : $('porder.purchaseOrderCheckStateFirmhold'),
				name : 'purchaseOrderCheckState',
				inputValue : "FIRMHOLD"
			}, {
				boxLabel : $('porder.purchaseOrderCheckStateFirmreject'),
				name : 'purchaseOrderCheckState',
				inputValue : "FIRMREJECT"
			} ]
		} ]
	},

	/**
	 * @cfg {Object} stores 相关store归集 - **taxRateStore** - 税率 -
	 *      **purchasingOrderTypeStore** - 采购订单类型 - **companyStore** - 公司 -
	 *      **currencyStore** - 货币 - **purchasingOrgStore** - 采购组织 -
	 *      **purchaseGroupStore** - 采购组 - **flagStore** - 公共Boolean类型 -
	 *      **lineItemTypeStore** - 行项目类别 - **stockTypeStore** - 库存类型 -
	 *      **unitStore** - 单位 - **plantStore** - 工厂,根据物料、采购组织进行查询 -
	 *      **storLocStore** - 库存地点 - **pricingConditionTypeStore** - 定价条件类型
	 */
	stores : {
		/**
		 * 税率store
		 */
		taxRateStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/taxrate/getall',
				type : 'ajax'
			},
			autoLoad : true,
			fields : [ 'taxRateCode', 'taxRateName' ]
		}),

		/**
		 * 采购订单类型
		 */
		purchasingOrderTypeStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/datadict/getall?groupCode=' + 'purchasingOrderType',
				type : 'ajax'
			},
			autoLoad : true,
			fields : [ 'itemCode', 'itemName' ]
		}),

		/**
		 * 公司
		 */
		companyStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/companypurchaseorg/getall',
				actionMethods:{read:"post"},
				type : 'ajax'
			},
			fields : [ 'companyCode', 'companyName' ],
			autoLoad : false
		}),

		/**
		 * 货币
		 */
		currencyStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/currency/getall',
				type : 'ajax'
			},
			fields : [ 'currencyName', 'currencyCode' ],
			autoLoad : true
		}),

		/**
		 * 采购组织
		 */
		purchasingOrgStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/purchasingorganization/getall',
				type : 'ajax'
			},
			autoLoad : true,
			fields : [ 'purchasingOrgCode', 'purchasingOrgName' ]
		}),

		/**
		 * 采购组
		 */
		purchaseGroupStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + "/md/purchasinggroup/getall",
				type : 'ajax'
			},
			autoLoad : true,
			fields : [ 'purchasingGroupCode', 'purchasingGroupName' ]
		}),

		/**
		 * 公共Boolean类型
		 */
		flagStore : Ext.create('Ext.data.JsonStore', {
			data : [ {
				value : 1,
				display : $('dict.yes')
			}, {
				value : 0,
				display : $('dict.no')
			} ],
			fields : [ 'value', 'display' ]
		}),

		/**
		 * 行项目类别
		 */
		lineItemTypeStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/datadict/getall?groupCode=recordType',
				type : 'ajax'
			},
			fields : [ 'itemCode', 'itemName' ],
			autoLoad : true
		}),

		/**
		 * 库存类型
		 */
		stockTypeStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/datadict/getall?groupCode=qualityCheck',
				type : 'ajax'
			},
			fields : [ 'itemCode', 'itemName' ],
			autoLoad : true
		}),

		/**
		 * 单位
		 */
		unitStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/unit/getall',
				type : 'ajax'
			},
			fields : [ 'unitCode', 'unitName' ],
			autoLoad : true
		}),

		/**
		 * 工厂store 根据物料、采购组织进行查询
		 */
		plantStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/material/findfiltrationplant',
				type : 'ajax'
			},
			fields : [ 'plantCode', 'plantName'],
			autoLoad : true
		}),

		/**
		 * 库存地点store
		 */
		storLocStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/stocklocation/getall',
				type : 'ajax'
			},
			fields : [ 'stockLocationCode', 'stockLocationName' ],
			listeners : {
				// load : 'storLocStoreLoad'
			},
			autoLoad : true
		}),
		/**
		 * 定价条件类型
		 */
		pricingConditionTypeStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_masterdata + '/md/datadict/getall?groupCode=pricingConditionType',
				type : 'ajax'
			},
			fields : [ 'itemCode', 'itemName' ],
			autoLoad : true

		}),
		/**
		 * 供应商采购组织信息
		 */
		vendorPorgDtlStore : Ext.create('Ext.data.JsonStore', {
			proxy : {
				url : path_srm + '/sl/vendorm/findvendorporgdtlall',
				type : 'ajax',
				actionMethods:{read:"post"}
			},
			fields : [ 'purchasingOrgCode', 'currencyCode' ],
			autoLoad : false
		})
	}
		
		
	
	}
	
});
