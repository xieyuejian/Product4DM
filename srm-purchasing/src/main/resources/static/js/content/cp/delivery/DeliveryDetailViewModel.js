/**
 * @class {Cp.delivery.DeliveryDetailViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 送货看板配置
 */
Ext.define('Cp.delivery.DeliveryDetailViewModel', {
	extend: 'Ext.ux.app.ViewModel',
	alias: 'viewmodel.deliveryDetailViewModel',
	requires: [
		'Ext.ux.form.DateTimeField',
		'Ext.ux.button.UploadButton'
	],

	/**
	 * @cfg {Object} stores
	 * 相关store归集 
	 * 
	 * - **deliveryTypeStore** - 送货类型
	 * - **recordTypeStore** - 行记录类型
	 * - **yesAndNoStore** - 是或者否
	 *
	 */
	stores: {
		//送货类型
		deliveryTypeStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/datadict/getall?groupCode=deliveryTypes',
				actionMethods:{read:"post"},
				type: 'ajax'
			},
			fields: ['itemCode', 'itemName'],
			autoLoad: true
		}),
		//行记录类型
		recordTypeStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/datadict/getall?groupCode=recordType',
				actionMethods:{read:"post"},
				type: 'ajax'
			},
			fields: ['itemCode', 'itemName'],
			autoLoad: true
		}),

		//是或者否
		yesAndNoStore: Ext.create('Ext.data.ArrayStore', {
			fields: ['text', 'value'],
			data: [
				['否', '0'],
				['是', '1']
			]
		})
	},

	data: {
		 /**
         * @cfg {Boolean} isExtend
         * 是否用父类的getCfg 配置方法
       	 */
      	isExtend:true,

		/**
		 * @cfg {String} dealUrl 
		 * 方法处理url
		 */
		dealUrl: '#{path}/cp/deliverydetail',

		/**
		 * @cfg {String} moduleName 
		 * 模块名称
		 */
		moduleName: $('shoppingnoticedetail'), 

		/**
		 * @cfg {Integer} editWinFormHeight
		 * 编辑表单高度
		 */
		ew_height: 240,

		/**
		 * @cfg {Object} gridStore 
		 * 列表Store
		 */
		vp_gridStore: {
			idProperty: 'deliveryDtlId',
			url: '#{dealUrl}/list',
			//sort: 'delivery.deliveryId',
			//dir: 'asc',
			autoLoad: false
		},

		/**
		 * @cfg {Array} addVpBtn 
		 * 在固化的按钮基础上追加按钮
		 * - **cancle** - 取消
		 * - **export** - 导出
		 * - **synerp** - 同步
		 */
		vp_addListBtn: [/*{
			Qtext: '导出',
			text: $('button.exportExcel'),
			index: 6,
			name: 'exportSingle',
			build: power['export'],
			iconCls: 'icon-export',
			handler: 'vpExportHandler'
		}*/],
		/**
		 * @cfg {String} moduleCode
		 * 模块编码
		 */
		moduleCode: 'SHD',
		/**
         * @cfg {String}  vp_logModuleCode 
         *  底部logTab 操作日志 请求参数值
         */
        vp_logModuleCode: "SHD",
        /**
         * @cfg {String}  vp_billTypeCode
         *  底部msgTab 审核日志 请求参数值默认单据编码 
         */
        vp_billTypeCode: "SHD",
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
		sw_height: 370,

		/**
		 * @cfg {String} searchFormColumnWidth
		 * 查询表单每行列数
		 */
		sw_columnWidth: '0.5',

		/**
		 * @cfg {String} controllerClassName
		 * 控制类类名称
		 */
		controllerClassName: 'Cp.delivery.DeliveryDetailController',

		/**
		 * @cfg {Array} editFormItems 
		 * 货源清单编辑form表单
		 */
		ew_editFormItems: [],

		/**
		 * @cfg {Array} vpListEditStateFn 
		 * 列表界面按钮控制
		 */
		vp_listEditStateFn: [],

		/**
		 * @cfg {Array} searchFormItems 
		 * 货源清单查询字段集合
		 */
		sw_searchFormItems: [
			{QfieldLabel: '送货单号  ', fieldLabel: $('delivery.deliveryCode'), name: 'filter_LIKE_delivery_deliveryCode'},
			{QfieldLabel: '采购组织编码', fieldLabel: $('purchasingOrg.code'), name: 'filter_LIKE_delivery_purchasingOrgCode'},
			{QfieldLabel: '采购组织名称', fieldLabel: $('purchasingOrg.name'), name: 'filter_LIKE_delivery_purchasingOrgName'},
			{QfieldLabel: '工厂', fieldLabel: $('plant.title'), name: 'filter_LIKE_delivery_plantName'},
			{QfieldLabel: '送货日期', fieldLabel: $('delivery.deliveryDate'), name: 'filter_GE_delivery_deliveryDate', xtype: 'datefield', format: 'Y-m-d'},
			{QfieldLabel: '至', fieldLabel: $('label.to'), name: 'filter_LT_delivery_deliveryDate', xtype: 'datefield', format: 'Y-m-d'},
			{QfieldLabel: '送达日期', fieldLabel: $('delivery.serviceDate'), name: 'filter_GE_delivery_serviceDate', xtype: 'datefield', format: 'Y-m-d'},
			{QfieldLabel: '至', fieldLabel: $('label.to'), name: 'filter_LT_delivery_serviceDate', xtype: 'datefield', format: 'Y-m-d'},
			{QfieldLabel:'创建时间', fieldLabel:$('label.createTime'), name:'filter_GE_delivery_createTime', xtype:'datefield', format:'Y-m-d'},
	    	{QfieldLabel:'至', fieldLabel:$('label.to'), name:'filter_LE_delivery_createTime', xtype:'datefield', format:'Y-m-d'},
			{QfieldLabel: '库存地点', fieldLabel: $('label.storageLocation'), name: 'filter_LIKE_delivery_storageLocationName'},
			{QfieldLabel: '采购订单号', fieldLabel: $('porder.purchaseOrderNo'), name: 'filter_LIKE_purchaseOrderCode'},
			{QfieldLabel: '排程单号  ', fieldLabel: $('sendschedul.sendScheduleNo'), name: 'filter_LIKE_sendScheduleNo'},
			{QfieldLabel: '物料编码', fieldLabel: $('materialInfo.code'), name: 'filter_LIKE_materialCode'},
			{QfieldLabel: '物料名称', fieldLabel: $('materialInfo.name'), name: 'filter_LIKE_materialName'},
			{QfieldLabel: '创建人名称',fieldLabel: $('label.createUserName'),name: 'filter_LIKE_delivery_createUserName'},
			{
				QfieldLabel: '行项目类别',
				fieldLabel: $('deliveryDtl.lineItemTypes'),
				anchor: '100%',
				xtype: 'checkboxgroup',
				items: [
					{boxLabel: '标准', name: 'lineItemTypes', checked: false, inputValue: 0 }, 
					{boxLabel: '寄售', name: 'lineItemTypes', checked: false, inputValue: 2 },
					{boxLabel: '分包', name: 'lineItenTypes', checked: false, inputValue: 3 }
				]
			}, {
				QfieldLabel: '送货单状态',
				fieldLabel: '送货单状态',
				anchor: '100%',
				xtype: 'checkboxgroup',
				items: [
					{boxLabel: '待收货', name: 'initStates', checked: true, inputValue: 'WAIT'},
					{boxLabel: '收货中', name: 'initStates', checked: false, inputValue: 'RECEIVING'},
					{boxLabel: '收货完成', name: 'initStates', checked: false, inputValue: 'CLOSE'}
				]
			}, {
				QfieldLabel: '关闭标识',
				fieldLabel: '关闭标识',
				anchor: '100%',
				xtype: 'checkboxgroup',
				items: [
					{boxLabel: '否', name: 'closeFlag', checked: true, inputValue: 0 },
					{boxLabel: '是', name: 'closeFlag', checked: false, inputValue: 1 }
				]
			}
		],

		/**
		 * @cfg {Array} gridColumn 
		 * 列表对象 列属性配置项
		 */
		vp_gridColumn: [
			{Qheader: '送货单明细ID', header: $('shoppingnoticedetail.shoppingNoticeDetailId'), dataIndex: 'delivery.deliveryDtlId', width: 120, disabled: true },
			{Qheader: '送货单编码', header: $('shoppingNotice.shoppingNoticeNo'), dataIndex: 'delivery.deliveryCode', width: 120, disabled: true,tipable:true },
			{Qheader: '送货单状态(待收货、部分收货、收货完成)', header: $('sendscheduledetail.sendFlag'), dataIndex: 'delivery.status', width: 120, renderer: 'rendererState',exportRenderer: true},//gridStatusRenderer
			{Qheader: '送货明细的采购订单号', header: $('porder.purchaseOrderNo'), dataIndex: 'purchaseOrderCode', width: 150,hidden:true },
			{Qheader: '送货明细的采购订单号', header: $('porder.purchaseOrderNo'), dataIndex: 'erpPurchaseOrderNo', width: 150,tipable:true },
			{Qheader: '送货明细的行号', header: $('label.rowNo'), dataIndex: 'lineNumber', width: 120,tipable:true },
			{Qheader: '送货明细的物料编码', header: $('materialInfo.code'), dataIndex: 'materialCode', width: 120,tipable:true },
			{Qheader: '送货明细的物料名称', header: $('materialInfo.name'), dataIndex: 'materialName', width: 150,tipable:true },
			{Qheader: '送货明细的单位编码', header: $('deliveryDtl.unitCode'), dataIndex: 'unitCode', width: 80,tipable:true },
			{Qheader: '送货明细的送货数量', header: $('sendscheduledetail.deliveryQty'), dataIndex: 'deliveryNumber', align: 'right', renderer: 'rendererNumber'},//numberThreeDecimalRenderer
			{Qheader: '送货明细的收货数量', header: $('sendscheduledetail.receiptQty'), dataIndex: 'receivedQty', align: 'right', renderer: 'rendererNumber'},//numberThreeDecimalRenderer
			{Qheader: '送货单的供应商编码', header: $('vendor.code'), dataIndex: 'delivery.vendorErpCode',tipable:true},
			{Qheader: '送货单的供应商名称', header: $('vendor.name'), dataIndex: 'delivery.vendorName', width: 150 ,tipable:true},
			{Qheader: '送货单的送货单号', header: $('delivery.deliveryCode'), dataIndex: 'delivery.deliveryCode', width: 150, renderer: 'rendererNo',tipable:true},//gridDeliveryCodeRenderer
			{Qheader: '送货单的采购组织编码', header: $('purchasingOrg.code'), dataIndex: 'delivery.purchasingOrgCode',tipable:true},
			{Qheader: '送货单的采购组织名称', header: $('delivery.purchasingOrgName'), dataIndex: 'delivery.purchasingOrgName', width: 150,tipable:true },
			{Qheader: '送货单的工厂，显示工厂名称', header: $('plant.title'), dataIndex: 'delivery.plantName', width: 200,tipable:true },
			{Qheader: '送货单的库存地点，显示库存地点名称', header: $('label.storageLocation'), dataIndex: 'delivery.storageLocationName', width: 130 },
			{Qheader: '送货单的送货日期，显示格式yyyy-mm-dd', header: $('delivery.deliveryDate'), dataIndex: 'delivery.deliveryDate',  renderer: 'rendererDate',exportRenderer:true}, //gridDateRenderer
			{Qheader: '送货单的送货方式： 1:自送;2:快递3:托运4:自提 ', header: $('delivery.deliveryTypes'), dataIndex: 'delivery.deliveryTypes', renderer: 'gridDeliveryTypesRenderer',exportRenderer: true},
			{Qheader: '送货单的送达日期，显示格式yyyy-mm-dd', header: $('delivery.serviceDate'), dataIndex: 'delivery.serviceDate', renderer: 'rendererDate',exportRenderer:true},//gridDateRenderer
			{Qheader: '送货明细的排程单号', header: $('sendschedul.sendScheduleNo'), dataIndex: 'scheduleCode', width: 150},
			{Qheader: '送货明细的行项目类别', header: $('deliveryDtl.lineItemTypes'), dataIndex: 'lineItemTypes', renderer: 'gridDtlLineItemTypesRenderer'},
			{Qheader: '送货明细的关闭标识', header: '关闭标识', dataIndex: 'closeFlag', renderer: 'gridDtlFlagRenderer',exportRenderer: true},
			{Qheader: '创建人', header: $('label.createUserName'), dataIndex: 'delivery.createUserName', width : 100,exportRenderer: true },
			{Qheader: '创建日期', header: $('label.createTime'), dataIndex: 'delivery.createTime', renderer : 'rendererDateTime',width : 140,exportRenderer: true }
		],

		/**
		 * @cfg 
		 * 列表grid 配置项
		 */
		vp_gridCfg: { 
			stateHeader : true,
			stateful : true,
			stateId : s_userCode + '_deliveryDetail',
			forceFit: false,
			 ableExporter:true,
             /**
              *  导出相关样式配置项
              */
             /* 
             exportConfig:{  
                 tableHeaderStyle:{
                     font: {
                         fontName: 'Arial',
                         family: 'Swiss',
                         size: 11,
                         color: '#1F497D'
                     }
                 }
             }, */
            // sm:true,//选中框隐藏
             rn:true//序列列隐藏
	    },
	    
		/**
		 * @cfg {Object} detailGrid 
		 * 编辑明细
		 */
		detailGrid: {},

		/**
		 * @cfg {Object} hideSubTab 
		 * 隐藏的tab列表项
		 */
		vp_hideSubTab: true,

		/**
		 * @cfg {Array} hideVpBtn 
		 * 对固化的按钮进行隐藏操作
		 */
		vp_hideListBtn: ['view'],

		/**
		 * @cfg {String} playListMode
		 * normal/audit/undeal //三种列表模式
		 */
		playListMode: "normal"
	}
});