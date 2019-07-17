/**
 * @class {Cp.delivery.DeliveryViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 送货管理配置
 */
Ext.define('Cp.delivery.DeliveryViewModel',{
	extend : 'Ext.ux.app.ViewModel',
    alias: 'viewmodel.deliveryViewModel',
    requires:[
    	'Ext.ux.form.DateTimeField',
    	'Ext.ux.button.UploadButton',
    	'Sl.masterdata.VendorSelectWin',
    	'Cp.delivery.PurcahseOrderDetailSelectWin',
    	'Cp.delivery.PurcahseOrderDetailSearchWin',
    	'Cp.delivery.SendScheduleDetailSelectWin',
    	'Cp.delivery.SendScheduleDetailSearchWin',
        "Ext.srm.form.PurchasingOrganizationComboGrid",
        "Ext.srm.form.ExpressCompanyComboGrid",
        "Ext.srm.form.VendorComboGrid",
        "Ext.srm.form.PlantComboGrid",
        "Ext.srm.form.StockLocationComboGrid",
        "Ext.srm.ux.SrmPluploadButton",
        "Ext.srm.form.SrmFileField",
        "Ext.srm.ux.SrmFileColumn"
    ],


    /**
     * @cfg {Object} stores
     * 相关store归集 
     * 
     * - **deliveryTypeStore** - 送货类型
     * - **recordTypeStore** - 行记录类型
     * - **purchasingOrgStore** - 采购组织
     * - **purchasingOrgByVendorStore** - 供应商采购组织
     * - **plantStore** - 工厂
     * - **stockLocationStore** - 库存
     * - **yesAndNoStore** - 是或者否
     * - **vendorStore** - 供应商
     *
     */
	stores: {
		//送货类型
		deliveryTypeStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/datadict/getall?groupCode=deliveryTypes',
				type: 'ajax'
			},
			fields: ['itemCode', 'itemName'],
			autoLoad: true
		}),
		//行记录类型
		recordTypeStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/datadict/getall?groupCode=recordType',
				type: 'ajax'
			},
			fields: ['itemCode', 'itemName'],
			autoLoad: true
		}),
		//采购组织
		purchasingOrgStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/purchasingorganization/getall?authFlag=true', // 未作过滤	
				type: 'ajax'
			},
			fields: ['purchasingOrgCode', 'purchasingOrgName'],
			autoLoad: true
		}),
		//供应商采购组织
		purchasingOrgByVendorStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_srm + '/sl/vendorm/getpurchasingorgall', // 获取当前登录供应商的采购组织	
				type: 'ajax',
		        actionMethods: {  
		            create : 'POST',  
		            read   : 'POST', // by default GET  
		            update : 'POST',  
		            destroy: 'POST'  
		        } 
			},
			fields: ['purchasingOrgCode', 'purchasingOrgName'],
			autoLoad: true
		}),
		//工厂
		plantStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/plantpurchaseorg/getall', // 工厂采购组织关系
				type: 'ajax'
			},
			fields: ['plantCode', 'plantName'],
			autoLoad: true
		}),

		//库存
		stockLocationStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/stocklocation/getall',
				type: 'ajax'
			},
			fields: ['stockLocationCode', 'stockLocationName'],
			autoLoad: true
		}),

		//是或者否
		yesAndNoStore: Ext.create('Ext.data.ArrayStore', {
			fields: ['text', 'value'],
			data: [
				['否', '0'],
				['是', '1']
			]
		}),
		//供应商
		vendorStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_srm + '/cp/delivery/findonevendor',
				type: 'ajax',
				actionMethods:{read:"post"}
			},
			fields: ['vendorCode', 'vendorName', 'vendorErpCode'],
			autoLoad: true
		})
	},


	data: {
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
        		statusCode:"WAIT",
        		statusName:$("state.unaccept") // 待收货
        	},{
        		statusCode:"RECEIVING",
        		statusName:$("status.receiving") //收货中
        	},{
        		statusCode:"CLOSE",
        		statusName:$("deliveryDtl.isFinish") //收货完成
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
        		statusCode:"WAIT",
        		statusName:$("state.unaccept") // 待收货
        	},{
        		statusCode:"RECEIVING",
        		statusName:$("status.receiving") //收货中
        	},{
        		statusCode:"CLOSE",
        		statusName:$("deliveryDtl.isFinish") //收货完成
        	},{
        		statusCode:"CANCEL",
        		statusName:$("state.cancel") //取消
        	}] 
        },
         /**
         * 同步状态
         * @param {} vm 配置对象
         * @return {Array}
         */
        synStatusFn:function(vm){ 
        	return [{
        		statusCode:"SYNCHRONIZEDNOT",
        		statusName:$('erpSyn.nosyn') 
        	},{
        		statusCode:"SYNCHRONIZING",
        		statusName:$('erpSyn.onsyn') 
        	},{
        		statusCode:"SYNSUCCESS",
        		statusName:$('erpSyn.synsuccess') 
        	},{
        		statusCode:"SYNFAILED",
        		statusName:$('erpSyn.synfail') 
        	},{
        		statusCode:"SYNNONEED",
        		statusName:$('erpSyn.noneed') 
        	}] 
        },
		 /**
         * @cfg {Boolean} isExtend
         * 是否用父类的getCfg 配置方法
       	 */
      	isExtend:true,
	    /**
	     * @cfg {String} dealUrl 
	     * 方法处理url
	     */
	    dealUrl: '#{path}/cp/delivery',

	    /**
	     * @cfg {String} moduleName 
	     * 模块名称
	     */
	    moduleName: $('delivery'),

	    /**
	     * @cfg {String} triggerField 
	     * 触发域（字段名）
	     */
	    triggerField: 'deliveryNo',

	    /**
	     * @cfg {boolean} editWinMaximized
	     * 是否最大化窗口，默认为否
	     */
	    maximized: true,

	    /**
	     * @cfg {Integer} editWinFormHeight
	     * 编辑表单高度
	     */
	    ew_height: 185,
	    /**
         * @cfg {String}  vp_logModuleCode 
         *  底部logTab 操作日志 请求参数值
         */
        vp_logModuleCode: "ASN",
        /**
         * @cfg {String}  vp_billTypeCode
         *  底部msgTab 审核日志 请求参数值默认单据编码 
         */
        vp_billTypeCode: "ASN",
		/**
		 * @cfg {boolean} isAudit
		 * 是否需要右键审核
		 */
		isAudit: false,

		/**
		 * @cfg {Boolean} searchWinIsShowStatus
		 * 查询窗体是否显示状态查询
		 */
		sw_isShowStatus: true,

		/**
		 * @cfg {Integer} searchWinHeight
		 * 查询窗体高度
		 */
		searchWinHeight: 500,


		/**
		 * @cfg {String} searchFormColumnWidth
		 * 查询表单每行列数
		 */
		sw_columnWidth: '0.5',


		/**
		 * @cfg {Integer} activeTab
		 * 默认展示的tab页
		 */
		vp_activeTab: 0,

		/**
		 * @cfg {String} controllerClassName
		 * 控制类类名称
		 */
		controllerClassName: 'Cp.delivery.DeliveryController',

		/**
		 * @cfg {Object} editWinCenterTab 
		 * 编辑窗体底部tab集合
		 */
		 ew_centerTab: {
	            items: ["detailGrid"/*, ["expressDtlGrid", "logisticsDtlDtlGrid"]*/]
	     }, 
		
		/**
		 * @cfg {Object} editWinFormColumnWidth 
		 * 配置编辑表单有几列
		 */
	     ew_columnWidth: .33,

		/**
		 * @cfg {Array} editFormItems 
		 * 货源清单编辑form表单
		 */
		ew_editFormItems: [
			{
				hidden:true,
				fieldLabel: $('delivery.deliveryId'),
				name: 'model.deliveryId'
			}, {
				QfieldLabel: '送货单号',
				fieldLabel: $('delivery.deliveryCode'),
				name: 'model.deliveryCode',
				value: $('dict.autogeneration'),
				readOnly: true
			}, {
				QfieldLabel: '单据状态',
				fieldLabel: $('delivery.status'),
				name: 'model.status',
				submitValue: false,
				hidden: true
			},/* {
				QfieldLabel: '采购组织编码',
				fieldLabel: $('delivery.purchasingOrgCode') + '<font color="red"> *</font>',
				name: 'model.purchasingOrgCode',
				displayField: 'purchasingOrgName',
				valueField: 'purchasingOrgCode',
				displayValue: 'purchasingOrgCode',
				/*innerTpl: true,
				bind: {
					store: '{purchasingOrgStore}'
				},
				xtype: 'uxcombo',
				allowBlank: false,
				listeners: {
					'focus': 'formPurchasingOrgCodeFocus',
					'select': 'formPurchasingOrgCodeSelect',
					'beforeselect': 'formPurchasingOrgCodeBeforeselect',
					'clear': 'formPurchasingOrgCodeClear'
				}*/
				  /*  xtype: 'purchasingOrganizationComboGrid',
				    enabletrigger:true,
				   	clearable:true,
	            	displayField:"purchasingOrgCode",
	            	selectWinCfg:{
                     autoLoad:true
                    },
	             	listeners: {
						'focus': 'formPurchasingOrgCodeFocus',
						'select': 'formPurchasingOrgCodeSelect',
						///'beforeselect': 'formPurchasingOrgCodeBeforeselect',
						'clear': 'formPurchasingOrgCodeClear'
					}
			},*/
			{
				QfieldLabel: '采购组织编码',
				fieldLabel: $('delivery.purchasingOrgCode') + '<font color="red"> *</font>',
				name: 'model.purchasingOrgCode',
				displayField: 'purchasingOrgName',
				valueField: 'purchasingOrgCode',
				displayValue: 'purchasingOrgCode',
				innerTpl: true,
				bind: {
					store: '{purchasingOrgStore}'
				},
				xtype: 'purchasingOrganizationComboGrid2',
				allowBlank: false,
				listeners: {
//					'focus': 'formPurchasingOrgCodeFocus',
					'triggerbaseparams': 'formPurchasingOrgCodeTrigger',
					'select': 'formPurchasingOrgCodeSelect',
					'beforeselect': 'formPurchasingOrgCodeBeforeselect',
					'clear': 'formPurchasingOrgCodeClear'
				}
			}, 
				{
				QfieldLabel: '采购组织名称',
				fieldLabel: $('delivery.purchasingOrgName'),
				name: 'model.purchasingOrgName',
				readOnly: true
			},  {
				QfieldLabel: '供应商ERP编码',
				fieldLabel: $('delivery.vendorCode') + '<font color="red"> *</font>',
				name: 'model.vendorErpCode',
				xtype: 'vendorComboGrid',
				allowBlank: false, 
	           	editable:true, 
	           	clearable:true,
	           	enabletrigger:true,
	           	displayField:"vendorErpCode",
	           	minChars:1,
	            	listeners: {
					trigger: 'formVendorCodeTrigger',
					clear: 'formVendorCodeClear',
					aftersetvalue: "vendorErpCodeSetValueAfter"
				}
				
			}, {
				QfieldLabel: '供应商名称',
				fieldLabel: $('delivery.vendorName'),
				name: 'model.vendorName',
				readOnly: true
			}, {
				QfieldLabel: '送货日期',
				fieldLabel: $('delivery.deliveryDate'),
				name: 'model.deliveryDate',
				xtype: 'datefield',
				formatText: '',
				format: 'Y-m-d',
				value: new Date()
			}, {
				QfieldLabel: '工厂',
				fieldLabel: $('plant.title'),
				name: 'model.plantName',
				xtype: 'plantComboGrid',
				minChars : 1,
				displayField: 'plantCode',
				valueField: 'plantName',
				displayValue: 'plantName',
				innerTpl: true,
				allowBlank: false,
				bind: {
					store: '{plantStore}'
				},
				listeners: {
					'aftercombosetvalue': 'formPlantCodeSelect',
					'beforeselect': 'formPlantCodeBeforeselect',
					"triggerbaseparams": "plantCodeTriggerbaseparams", //下拉弹出 共有事件  
					'clear': 'formPlantCodeClear'
				}
			},  {
				QfieldLabel: '工厂编码',
				fieldLabel: $('plant.code'),
				name: 'model.plantCode',
				readOnly: true,
				hidden:true
			}, {
				QfieldLabel: '库存地点',
				fieldLabel: $('label.storageLocation'),
				name: 'model.storageLocationName',
				hiddenName: 'model.storageLocationName',
				xtype: 'uxcombo',
				displayField: 'stockLocationCode',
				valueField: 'stockLocationName',
				displayValue: 'stockLocationName',
				innerTpl: true,
				bind: {
					store: '{stockLocationStore}'
				},
				listeners: {
					'select': 'formStockLocationCodeSelect',
					'clear': 'formStockLocationCodeClear'
				}
			}, 	
			{
				QfieldLabel: '库存地点编码',
				fieldLabel: $('delivery.storageLocationCode'),
				name: 'model.storageLocationCode',
				readOnly: true,
				hidden:true
			}, {
				QfieldLabel: '送货方式 1:自送;2:快递3:托运4:自提 ',
				fieldLabel: $('delivery.deliveryTypes'),
				name: 'model.deliveryTypes',
				hiddenName: 'model.deliveryTypes',
				xtype: 'uxcombo',
				allowBlank: false,
				bind: {
					store: '{deliveryTypeStore}'
				},
				valueField: 'itemCode',
				displayField: 'itemName',
				listeners: {
					'select': 'formDeliveryTypesSelect'
				}
			}, {
				QfieldLabel: '快递单号',
				fieldLabel: $('delivery.trackingNumber'),
				name: 'model.trackingNumber',
				maxLength:20,
				listeners: {
					'change': 'formTrackingNumberChange'
				}
			}, {
				QfieldLabel: '送达日期',
				fieldLabel: $('delivery.serviceDate'),
				name: 'model.serviceDate',
				xtype: 'datefield',
				formatText: '',
				format: 'Y-m-d',
				listeners: {
					'select': 'formServiceDateChange'
				}
			},
	    	{QfieldLabel: '同步状态', fieldLabel: $('delivery.synchronizeStatus'), name: 'model.synchronizeStatus', hidden: true },
	    	{QfieldLabel: '供应商ERP编码', fieldLabel: $('delivery.vendorErpCode'), name: 'model.vendorCode', hidden: true },
	    	{QfieldLabel: '制单者名称', fieldLabel: $('shoppingnotice.createUserName'), name: 'model.createUserName', hidden: true },
	    	{QfieldLabel: '创建日期', fieldLabel: $('label.createTime'), name: 'model.createTime', xtype: 'datefield', format: 'Y-m-d H:i:s', hidden: true },
	    	{QfieldLabel: '制单者ID', fieldLabel: $('shoppingnotice.createUserrId'), name: 'model.createUserId', hidden: true },
	    	{QfieldLabel: '修改日期', fieldLabel: $('label.modifyTime'), name: 'model.modifyTime', xtype: 'datefield', format: 'Y-m-d H:i:s', hidden: true },
	    	{QfieldLabel: '修改者名称', fieldLabel: $('label.modifyUserName'), name: 'model.modifyUserName', hidden: true },
	    	{QfieldLabel: '修改者ID', fieldLabel: $('shoppingnotice.modifyId'), name: 'model.modifyId', hidden: true }
		],

		/**
		 * @cfg {Array} vpListEditStateFn 
		 * 列表界面按钮控制
		 */
		vp_listEditStateFn: [{
			'edit': function(r) {
				return r.get('status') == 'NEW';
			}
		}, {
			'delete': function(r) {
				return r.get('status') == 'NEW';
			}
		}, {
			'cancle': function(r) {
				return r.get('status') == 'WAIT';
			}
		}, {
			'export': function(r) {
				return r.get('status') == 'WAIT' || r.get('status') == 'RECEIVING' || r.get('status') == 'CLOSE'
			}
		}, {
		  'view':true
		}, {
			'synerp':function(r){
				return r.get('synchronizeStatus') == 'SYNFAILED'
			}
		}],
		
		/**
		 * @cfg {Array} vpSubTab 
		 * 列表底部tab集合
		 */
		vp_subTab: ['detailGrid'/*, ["expressDtlGrid", "logisticsDtlDtlGrid"]*/, 'logTab'],
	
		
	    /**
		 * @cfg {Array} searchFormItems 
		 * 货源清单查询字段集合
		 */
		sw_searchFormItems:[
	       	{QfieldLabel:'送货单号', fieldLabel:$('delivery.deliveryCode'), name:'filter_LIKE_deliveryCode'},
	       	{QfieldLabel:'采购订单号', fieldLabel:$('deliveryDtl.purchaseOrderCode'), name:'filter_LIKE_deliveryDtls_erpPurchaseOrderNo'},
	       	{QfieldLabel:'采购组织编码', fieldLabel:$('delivery.purchasingOrgCode'), name:'filter_LIKE_purchasingOrgCode'},
	       	{QfieldLabel:'采购组织名称', fieldLabel:$('delivery.purchasingOrgName'), name:'filter_LIKE_purchasingOrgName'},
	       	{QfieldLabel:'供应商编码', fieldLabel:$('delivery.vendorCode'), name:'filter_LIKE_vendorCode_OR_LIKE_vendorErpCode'},
	       	{QfieldLabel:'供应商名称', fieldLabel:$('delivery.vendorName'), name:'filter_LIKE_vendorName'},
	       	{QfieldLabel:'排程单号', fieldLabel:$('deliveryDtl.scheduleCode'), name:'filter_LIKE_deliveryDtls_scheduleCode'},
	       	{QfieldLabel:'工厂', fieldLabel:$('plant.title'), name:'filter_LIKE_plantCode_OR_plantName'},
	       	{QfieldLabel:'送货日期', fieldLabel:$('delivery.deliveryDate'), name:'filter_GE_deliveryDate', xtype:'datefield', format:'Y-m-d'},
	       	{QfieldLabel:'至', fieldLabel: $('label.to'), name:'filter_LE_deliveryDate', xtype:'datefield', format:'Y-m-d'},
	       	{QfieldLabel:'物料编码', fieldLabel:$('deliveryDtl.materialCode'), name:'filter_LIKE_deliveryDtls_materialCode'},
	       	{QfieldLabel:'物料名称', fieldLabel:$('deliveryDtl.materialName'), name:'filter_LIKE_deliveryDtls_materialName'},
	       	{QfieldLabel:'创建时间', fieldLabel:$('label.createTime'), name:'filter_GE_createTime', xtype:'datefield', format:'Y-m-d'},
	    	{QfieldLabel:'至', fieldLabel:$('label.to'), name:'filter_LE_createTime', xtype:'datefield', format:'Y-m-d'},
	       	{QfieldLabel: '创建人名称',fieldLabel: $('label.createUserName'),name: 'filter_LIKE_createUserName'},
	       	{QfieldLabel:'库存地点', fieldLabel:$('label.storageLocation'), name:'filter_LIKE_storageLocationName'}
		],

		vp_hideListBtn : ['export'],
		
		/**
		 * @cfg {Array} addVpBtn 
		 * 在固化的按钮基础上追加按钮
		 * - **cancle** - 取消
		 * - **export** - 导出
		 * - **synerp** - 同步
		 */
		vp_addListBtn: [{
				name: 'cancle',
				index: 4,
				Qtext: '取消',
				text: $('button.cancel'),
				build: power.cancel,
				exclude: false,
				iconCls: 'icon-cancel',
				handler: 'vpCancleHandler'
			}, {
				name : 'export',
				text : "导出",
				index : 8,
				iconCls : 'icon-export',
				build :true,
				menu: {
			        items: [ {
					Qtext: "导出",
					text : $('button.Export'),
					index: 7,
					exclude: true,
					name: "export",
					build: power["export"],
					iconCls: "icon-export",
					handler: 'vpExportHandler'
				},{
	       			 name: "export",
	 	            Qtext: "导出",
	 	            text: $("button.exportExcel"),
	 	            build: power["exportexcel"],
	 	            tooltip: $("button.export.tooltip"),
	 	            iconCls: "icon-export",
	 	            handler: "exportExcel"
	 		}]
			}
			},{
				name: "synerp",
				index: 8,
				Qtext:"同步到ERP",
				text: "同步",
				build:power.synerp,
				exclude: false,
				iconCls: "icon-sync",
				handler: 'vpSynerpHandler'
			}
		],

		/**
		 * @cfg {String} playListMode
		 * normal/audit/undeal //三种列表模式
		 */
		playListMode: "normal",

		/**
		 * @cfg {Array} gridColumn 
		 * 列表对象 列属性配置项
		 */
		vp_gridColumn:[
			{Qheader: 'id', header: $('delivery.deliveryId'), dataIndex: 'deliveryId', disabled: true },
			{Qheader: '送货单号', header: $('delivery.deliveryCode'), dataIndex: 'deliveryCode', width: 140, renderer: 'rendererNo',tipable:true,width:200},//gridDeliveryCodeRenderer
			{Qheader: '状态', header: $('sample.status'), dataIndex: 'status', renderer: 'rendererStatus',exportRenderer: true},//gridStatusRenderer
			{Qheader: '采购组织编码', header: $('delivery.purchasingOrgCode'), dataIndex: 'purchasingOrgCode', width: 200 ,tipable:true},
			{Qheader: '采购组织名称', header: $('delivery.purchasingOrgName'), dataIndex: 'purchasingOrgName', width: 200,tipable:true },
			{Qheader: '供应商编码', header: $('delivery.vendorCode'), dataIndex: 'vendorCode', width: 120,disabled:true},
			{Qheader: '供应商ERP编码', header: $('vendor.code'), dataIndex: 'vendorErpCode', width: 120,tipable: true},
			{Qheader: '供应商名称', header: $('delivery.vendorName'), dataIndex: 'vendorName', width: 250,tipable:true },
			{Qheader: '工厂编码', header: $('delivery.plantCode'), dataIndex: 'plantCode', disabled: true, hidden:true },
			{Qheader: '工厂', header: $('plant.title'), dataIndex: 'plantName', width: 250,tipable:true },
			{Qheader: '库存地点编码', header: $('delivery.storageLocationCode'), dataIndex: 'storageLocationCode', width: 120, hidden:true },
			{Qheader: '库存地点', header: $('label.storageLocation'), dataIndex: 'storageLocationName', width: 200 ,tipable:true},
			{Qheader: '送货日期', header: $('delivery.deliveryDate'), dataIndex: 'deliveryDate', type: 'date', renderer: 'rendererDate',exportRenderer: true},
			{Qheader: '送货方式 1:自送;2:快递3:托运4:自提 ', header: $('delivery.deliveryTypes'), dataIndex: 'deliveryTypes', renderer: 'gridDeliveryTypesRenderer',exportRenderer: true},
			{Qheader: '快递单号', header: $('delivery.trackingNumber'), dataIndex: 'trackingNumber',tipable:true},
			{Qheader: '送达日期', header: $('delivery.serviceDate'), dataIndex: 'serviceDate', type: 'date', renderer: 'rendererDate',width:200,exportRenderer: true},
			{Qheader: '同步状态', header: $('delivery.synchronizeStatus'), dataIndex: 'synchronizeStatus', renderer: 'renderSynStatus',exportRenderer: true},
			{Qheader: '制单者名称', header: $('label.createUserName'), dataIndex: 'createUserName', width : 100 },
			{Qheader: '创建日期', header: $('label.createTime'), dataIndex: 'createTime', renderer : 'rendererDateTime',width : 140,exportRenderer: true },
			{Qheader: '制单者ID', header: $('shoppingnotice.createUserrId'), dataIndex: 'createUserId', disabled: true },
			{Qheader: '修改日期', header: $('label.modifyTime'), dataIndex: 'modifyTime', disabled: true },
			{Qheader: '修改者名称', header: $('label.modifyUserName'), dataIndex: 'modifyUserName', disabled: true },
			{Qheader: '修改者ID', header: $('label.modifyId'), dataIndex: 'modifyId', disabled: true }
		],
		
		/**
		 * @cfg 
		 * 列表grid 配置项
		 */
		vp_gridCfg: { 
			stateHeader : true,
			stateful : true,
			stateId : s_userCode + '_delivery',
			forceFit: false ,
		    ableExporter:true,
		    rn:true,
            /**
             *  导出相关样式配置项
             */
            exportConfig:{  
                tableHeaderStyle:{
                    font: {
                        fontName: 'Arial',
                        family: 'Swiss',
                        size: 11,
                        color: '#1F497D'
                    }
                }

            },
            rn:true
	    },
	    
		/**
         * @cfg {Object} gridStore
         * 列表Store配置项
         */
        vp_gridStore: {
            idProperty: "deliveryId",
            url:  "#{dealUrl}/list",
            sort: "deliveryId",
            dir: "desc" 
          
        }, 
        
		/**
		 * @cfg {Object} detailGrid 
		 * 编辑明细
		 */
		detailGrid: {
			tabTitle: $("deliveryDtl"),
			xtype: 'uxeditorgrid',
			foreignKey: "delivery_deliveryId",
			tabClassName: "deliveryDtls",
			validField: ["deliveryNumber"], //需要提交的细单字段,
			formFieldReadyArr: ['model.deliveryDate', 'model.purchasingOrgCode', 'model.vendorErpCode', 'model.plantName', 
			    				'model.storageLocationName', 'model.deliveryTypes', 'model.trackingNumber', 'model.serviceDate'], //编辑细单需要只读的字段
			forceFit: false,
			stateHeader: true,
			stateful : true,
			stateId : s_userCode + '_deliveryDtl',
			viewConfig: {
				// forceFit:true,//当行大小变化时始终填充满
				autoScroll: true
			},
			allowEmpty: false, //明细条数是否可以为空
			cm: {
				defaultSortable: false,
				defaults: {
					menuDisabled: true
				},
				columns: [
					{Qheader: 'id', header: $('deliveryDtl.deliveryDtlId'), dataIndex: 'deliveryDtlId', hidden: true },
					{Qheader: '送货管理id', header: $('deliveryDtl.deliveryId'), dataIndex: '_deliveryId', hidden: true },
					{Qheader: '数据来源采购订单明细ID', dataIndex: 'orderDetailId', hidden: true },
					{Qheader: '数据来源送货排程明细ID', dataIndex: 'sendDetailId', hidden: true },
					{Qheader: '订单ID', header: $('shoppingnoticedetail.purchaseOrderId'), dataIndex: 'orderId', hidden: true },
					{Qheader: '取消按钮', header: $('deliveryDtl.cancelBtn'), dataIndex: 'cancelBtn', hidden: false, renderer: 'gridDtlCancelBtnRenderer'},
					{Qheader: '关闭按钮', header: $('deliveryDtl.closeBtn'), dataIndex: 'closeBtn', hidden: false, renderer: 'gridDtlCloseBtnRenderer'},
					{Qheader: '采购订单号', header: $('deliveryDtl.purchaseOrderCode'), dataIndex: 'purchaseOrderCode', width: 150,hidden:true },
					{Qheader: '采购订单号', header: $('deliveryDtl.purchaseOrderCode'), dataIndex: 'erpPurchaseOrderNo', width: 150 },
					{Qheader: '行号', header: $('deliveryDtl.lineNumber'), dataIndex: 'lineNumber', width: 100 },
					{Qheader: '行项目类别', header: $('deliveryDtl.lineItemTypes'), dataIndex: 'lineItemTypes', width: 100, renderer: 'gridDtlLineItemTypesRenderer'},
					{Qheader: '物料编码', header: $('deliveryDtl.materialCode'), dataIndex: 'materialCode', width: 100 },
					{Qheader: '物料名称', header: $('deliveryDtl.materialName'), dataIndex: 'materialName', width: 150 },
					{Qheader: '单位编码', header: $('deliveryDtl.unitCode'), dataIndex: 'unitCode', width: 100 },
					{Qheader: '单位名称', header: $('deliveryDtl.unitName'), dataIndex: 'unitName', width: 100 },
					{Qheader: '订单数量', header: $('deliveryDtl.orderNumber'), dataIndex: 'orderNumber', align: 'right', width: 100, renderer: 'rendererNumber'},
					{Qheader: '已送数量', header: $('deliveryDtl.toSentNumber'), dataIndex: 'toSentNumber', hidden: true, align: 'right', width: 100, renderer: 'rendererNumber'},
					{Qheader: '点收数量', width: 100, header: $('deliveryDtl.acceptQty'), dataIndex: 'acceptQty', hidden: true, align: 'right', renderer: 'rendererNumber'},
					{Qheader: '可送数量', header: $('deliveryDtl.canSentNumber'), dataIndex: 'canSentNumber',  align: 'right', width: 100, renderer: 'rendererNumber'},
					{
						Qheader: '送货数量',
						header: $('deliveryDtl.deliveryNumber'),
						dataIndex: 'deliveryNumber',
						align: 'right',
						width: 100,
						//自定义属性
                        customAttr:{
                        	allowBlank:false,
                        	editable:true//是否显示可以编辑背景颜色
                        },
						editor: {
							xtype: 'numberfield',
							minValue : 0.00000000000001,
							decimalPrecision : 3,
							allowNegative: false
						},
						renderer: 'rendererNumber'
					}, {
						Qheader: '备注',
						header: $('deliveryDtl.remark'),
						dataIndex: 'remark',
						tipable:true,
						//自定义属性
                        customAttr:{
                        	editable:true//是否显示可以编辑背景颜色
                        },
						editor: {
							xtype: 'textfield',
							maxLength: 500,
							name: 'remark'
						}
					},
					{Qheader: '采购员', header: $('deliveryDtl.buyer'), dataIndex: 'buyer', width: 100, hidden: true },
					{
                        Qheader: '附件',
                        header: $('deliveryDtl.annexView'),
                        dataIndex: 'annex',
                        fileViewField:"annexView",
                        disabled: true
                    },
                    {
                    	header: $('deliveryDtl.annex'), 
                    	dataIndex: 'annexView', 
                    	width: 150,
                        editor: {
                            xtype: "srmfilefield",
                            name:"annexView"//根据自己配置附件组id存储字段
                        }, 
                        fileGroupIdField:"uploadFileGroupId",
                        xtype:'srmfilecolumn'
                    },
					{Qheader: '库存地点', header : $('label.storageLocation'),dataIndex : 'storageLocationCode',width : 150},
					{Qheader: '已收货量', header: $('deliveryDtl.receivedNumber'), dataIndex: 'receivedQty', align: 'right', width: 100, renderer: 'numberThreeDecimalRenderer'},
					{Qheader: '订单已收货量', header: $('deliveryDtl.orderReceivedNumber'), dataIndex: 'receivedNumber', align: 'right', width: 100, hidden:true,renderer: 'numberThreeDecimalRenderer'},
					{Qheader: '退货量', header: $('deliveryDtl.returnNumber'), dataIndex: 'returnNumber', align: 'right', width: 100, renderer: 'numberThreeDecimalRenderer'},
					{Qheader: '取消标识', header: '取消标识', dataIndex: 'cancelFlag', width: 100, renderer: 'gridDtlFlagRenderer'},
					{Qheader: '关闭标识', header: '关闭标识', dataIndex: 'closeFlag', width: 100, renderer: 'gridDtlFlagRenderer'},
					{Qheader: '排程单号', header: $('deliveryDtl.scheduleCode'), dataIndex: 'scheduleCode', width: 150 },
					{Qheader: '送货细单类型', header: $('deliveryDtl.dataFrom'), dataIndex: 'dataFrom', hidden: true },
					{Qheader: '交货过量限度', dataIndex: 'overDeliveryLimit', hidden: true }
				]
			},
			listeners: {
				'edit': 'gridDtlEdit',
				'beforeedit': 'gridDtlBeforeedit'
			},
			pageSize: 0,
			store: {
				idProperty: 'deliveryDtlId',
				url: path_srm + '/cp/delivery/finddeliverydtlall',
				sort: 'deliveryDtlId',
				autoLoad: false,
				dir: 'desc'
			},
			tbar: [{
				Qtext: '来源采购订单',
				text: '来源采购订单',
				iconCls: 'icon-add',
				handler: 'gridDtlAddSourceOrderHandler'
			}, {
				Qtext: '来源送货排程',
				text: '来源送货排程',
				iconCls: 'icon-add',
				handler: 'gridDtlAddSourceScheduleHandler'
			}, {
				Qtext: '删除',
				text: $('button.delete'),
				iconCls: 'icon-delete',
				handler: 'gridDtlDeleteHandler'
			},{
                name: "upload",
                xtype: "srmpluploadbutton",
                text: "附件上传",
                iconCls: "icon-publish",
                fileViewField:"annexView",
                fileGroupIdField:"annex",
                gridClassName:"deliveryDtls",
                listeners:{
                    "beforebrowseshow":"beforebrowseshow"
                }
            }/*, {
				Qtext: '附件上传',
				name: 'upload',
				text: $('shoppingnoticedetail.button.upload'),
				iconCls: 'icon-publish',
				handler: 'gridDtlUploadHandler'
			}*/],
			loadValueBefore: 'gridDtlLoadValueBefore'
		},
		/**
		 * @cfg {Object} expressDtlGrid 
		 * 快递信息明细
		 */
		expressDtlGrid: {
			tabTitle: $("delivery.deliveryExpressDtl"),// 快递信息
			xtype: 'uxeditorgrid',
			foreignKey: "delivery_deliveryId",
			tabClassName: "deliveryExpressDtls",
			forceFit: false,
			stateHeader: true,
			stateful : true,
			stateId : s_userCode + '_deliveryExpressDtl',
			viewConfig: {
				autoScroll: true
			},
			allowEmpty: true, //明细条数是否可以为空
			cm: {
				defaultSortable: false,
				defaults: {
					menuDisabled: true
				},
				columns: [
					{Qheader: 'id', header: $('deliveryDtl.deliveryExpressDtlId'), dataIndex: 'deliveryExpressDtlId', hidden: true },
					{Qheader: '送货管理id', header: $('deliveryDtl.deliveryId'), dataIndex: '_deliveryId', hidden: true },
					{Qheader: '快递公司编码', header: $('delivery.expressCompanyName'), dataIndex: 'expressCompanyCode', width: 100, hidden: true },
					{
						Qheader: '快递公司名称',
						header: $('delivery.expressCompanyName'),
						dataIndex: 'expressCompanyName',
						tipable:true,
						width: 150,
						editor: {
							xtype: 'expressCompanyComboGrid',
							enabletrigger:true,
                			minChars:2,
							fieldMapping:{ 
						       "expressCompanyCode":"itemCode",
						       "expressCompanyName":"itemName" 
						    }
						},
						renderer: 'gridDtlRemarkRenderer'
					}, {
						Qheader: '快递单号',
						header: $('delivery.trackingNumber'),
						dataIndex: 'expressNo',
						tipable:true,
						width: 180,
						editor: {
							xtype: 'textfield',
							name: 'expressNo'
						},
						renderer: 'gridDtlRemarkRenderer'
					},
					{Qheader: '接口调用信息', header: $('delivery.expressCompanyName'), dataIndex: 'message', hidden: true },
					{Qheader: '接口调用状态（0：成功  1：失败）', header: $('delivery.expressCompanyName'), dataIndex: 'status', hidden: true }
				]
			},
			listeners: {
				'edit': 'expressDtlEdit',
				'beforeedit': 'gridDtlBeforeedit'
			},
			pageSize: 0,
			store: {
				idProperty: 'deliveryExpressDtlId',
				url: path_srm + '/cp/delivery/finddeliveryexpressdtlall',
				sort: 'deliveryExpressDtlId',
				autoLoad: true,
				dir: 'desc'
			},
			ew_centerTab:{
				width: 400
			},
			tbar: [{
				Qtext: '添加',
				text: $("button.add"),
				iconCls: 'icon-add',
				handler: 'expressGridAddHandle'
			},{
				Qtext: '删除',
				text: $('button.delete'),
				iconCls: 'icon-delete',
				handler: 'expressGridDeleteHandle'
			}]
		},
		/**
		 * @cfg {Object} logisticsDtlDtlGrid 
		 * 物流详情
		 */
		logisticsDtlDtlGrid: {
			tabTitle: $("delivery.logisticsDtlDtl"),// 物流详情
			xtype: 'uxeditorgrid',
			foreignKey: "deliveryExpressDtl_deliveryExpressDtlId",
			tabClassName: "logisticsDtlDtls",
			rn: false,
			forceFit: false,
			viewConfig: {
				autoScroll: true
			},
			autoDtlLoad: false,
            allowEmpty: true, //明细条数是否可以为空
			cm: {
				defaultSortable: false,
				defaults: {
					menuDisabled: true
				},
				columns: [
					{Qheader: 'id', header: $('deliveryDtl.deliveryDtlId'), dataIndex: 'logisticsDtlDtlId', hidden: true },
					{Qheader: '快递信息id', header: $('deliveryDtl.deliveryId'), dataIndex: '_deliveryExpressDtlId', hidden: true },
					{Qheader: '更新时间', header: $('porder.modifyTime'), dataIndex: 'updateTime', width: 150 },
					{Qheader: '内容', header: $('label.content'), dataIndex: 'contant', width: 350 }
				]
			},
			listeners: {
				'edit': 'gridDtlEdit',
				'beforeedit': 'gridDtlBeforeedit'
			},
			pageSize: 0,
			store: {
				idProperty: 'logisticsDtlDtlId',
				url: path_srm + '/cp/delivery/findlogisticsdtldtlall',
				sort: 'updateTime',
				autoLoad: false,
				dir: 'desc'
			},
			ew_tabPanelDtl:{
				barCollapsible : true,
				width: 600,
				rn: false
			}
		}
	}
});