/**
 * @class {Cp.order.PurchaseOrderDetailViewModel}
 * @extend {Ext.ux.app.ViewModel} 执行中订单明细配置
 */
Ext.define('Cp.order.PurchaseOrderDetailViewModel', {
	extend : 'Ext.ux.app.ViewModel',
	alias : 'viewmodel.purchaseOrderDetailViewModel',
	requires : [ 'Ext.ux.form.DateTimeField' ],
    config:{
	data : {
		isExtend:true,
		/**
		 * @cfg {String} dealUrl 方法处理url
		 */
		dealUrl : path_srm + '/cp/purchaseorderdetail',
		/**
		 * @cfg {Array} gridColumn 列表对象 列属性配置项
		 */
		vp_gridColumn : [ {
			header : $('porder.purchaseOrderTime'),
			sortable : false,
			width : 100,
			dataIndex : "purchaseOrder.purchaseOrderTime",
			renderer : 'rendererDate',
			exportRenderer:true
		}, {
			header : $('batchCharacter.purchaseOrderCode'),
			dataIndex : "purchaseOrder.erpPurchaseOrderNo",
			width : 140
		}, {
			header : $('label.rowNo'),
			dataIndex : "rowIds",
			width : 80,
			align : "center"
		}, {
			header : $('vendor.code'),
			dataIndex : "purchaseOrder.vendorErpCode",
			width : 100
		}, {
			header : $('vendor.name'),
			dataIndex : "purchaseOrder.vendorName",
			width : 200
		}, {
			header : $('materialInfo.code'),
			dataIndex : "materialCode",
			width : 100
		}, {
			header : $('biddingRequireDelJ.materialName'),
			dataIndex : "materialName",
			width : 120
		}, {
			header : $('deliveryDtl.orderNumber'),
			dataIndex : "vendorQty",
			sortable : false,
			width : 90,
			align : "right",
			renderer : 'rendererNumber'
		}, {
			header : $('label.unit'),
			dataIndex : "unitCode",
			sortable : false,
			width : 50
		}, {
			header : $('sendscheduledetail.deliveryQty'),
			dataIndex : "qtySend",
			align : "right",
			width : 150,
			renderer : 'rendererNumber'
		}, {
			header : $('sendscheduledetail.onWayQty'),
			dataIndex : "qtyOnline",
			align : "right",
			width : 150,
			renderer : 'rendererNumber'
		}, {
			header : $('sendscheduledetail.receiptQty'),
			dataIndex : "qtyArrive",
			align : "right",
			width : 150,
			renderer : 'rendererNumber'
		}, {
			header : $('shoppingnoticedetail.returnGoodsQty'),
			dataIndex : "qtyQuit",
			align : "right",
			width : 150,
			renderer : 'rendererNumber'
		}, {
            header : $('deliveryDtl.canSentNumber'),
		    dataIndex : "canSendQty",
			align : "right",
			width : 150,
			sortable : false,
			renderer : 'rendererNumber'
		}, {
			header : $('porder.tempPrice'),
			dataIndex : "buyerPrice",
			sortable : false,
			width : 120,
			align : "right",
			renderer : 'numberTwoDecimalRenderer'
		}, {
			header : $('porder.tempItemAmount'),
			dataIndex : "lineItemValAmt",
			align : "right",
			sortable : false,
			width : 150,
			renderer : 'numberTwoDecimalRenderer'
		}, {
			header : $('label.deliveryDate'),
			dataIndex : "buyerTime",
			sortable : false,
			width : 150,
			renderer : 'rendererDate',
			exportRenderer:true
		}, {
			header : $("porder.vendorTime"),
			dataIndex : "vendorTime",
			width : 150,
			renderer : 'rendererDate',
			exportRenderer:true
		}, {
			header : $('porder.overDeliveryLimit'),
			dataIndex : "overDeliveryLimit",
			sortable : false,
			width : 170,
			renderer : 'numberTwoDecimalRenderer'
		}, {
			header : $('porder.shortDeliveryLimit'),
			dataIndex : "shortDeliveryLimit",
			sortable : false,
			width : 170,
			renderer : 'numberTwoDecimalRenderer'
		}, {
			header : $('label.remark'),
			dataIndex : "remark",
			sortable : false,
			tipable:true,
			width : 100
		}, {
			header : $('purchasingOrg.title'),
			fieldLabel : $('purchasingOrg.title'),
			dataIndex : "purchaseOrder.purchasingOrgCode",
			width : 150
		}, {
			header : $('purchasingGroup'),
			fieldLabel : $('purchasingGroup'),
			dataIndex : "purchaseOrder.purchasingGroupCode",
			width : 150
		}, {
			Qheader: '创建人', 
			header: $('label.createUserName'), 
			dataIndex: 'purchaseOrder.createUserName', 
			width : 100,
			exportRenderer: true 
		}, {
			header : $('label.createTime'),
			dataIndex : 'purchaseOrder.createTime',
			width : 140,
			renderer : 'rendererDateTime',
			exportRenderer:true
		} ],

		

		/**
		 * @cfg {String} moduleName 模块名称
		 */
		moduleName : $('purchaseOrderDetail.title'),

		/**
		 * @cfg {String} triggerField 触发域（字段名）
		 */
		vp_triggerField: 'purchaseOrder.purchaseOrderNo',

		/**
		 * @cfg {boolean} isAudit 是否需要右键审核
		 */
		isAudit : false,

		/**
		 * @cfg {Object} gridStore 列表Store
		 */
		vp_gridStore : {
			idProperty : 'purchaseOrderDetailId',
			url :'#{dealUrl}/list',
			sort : 'purchaseOrder.purchaseOrderId',
			autoLoad : true,
			dir : 'desc'
		},

		/**
		 * @cfg {Array} hideVpBtn 对固化的按钮进行隐藏操作 - **add** - 新建 - **edit** - 编辑 -
		 *      **delete** - 删除 - **view** - 查看
		 */
		vp_hideListBtn: [ 'add', 'edit', 'delete', 'view'],

		/**
		 * @cfg {String} moduleCode 质检单模块编码
		 */
		vp_billTypeCode : 'CGD',

		/**
		 * @cfg {String} playListMode normal/audit/undeal //三种列表模式
		 */
		playListMode : 'normal',

		/**
		 * @cfg {String} controllerClassName 控制类类名称
		 */
		controllerClassName : 'Cp.order.PurchaseOrderDetailController',

		/**
		 * @cfg {Object} nextBillState - default New 提交后下一步的状态
		 */
		nextBillState : 'New',

		/**
		 * @cfg {Integer} activeTab 默认展示的tab页
		 */
		vp_activeTab : 0,

		
		/**
		 * @cfg {Boolean} searchWinIsShowStatus 查询窗体是否显示状态查询
		 */
		sw_isShowStatus: false,

		/**
		 * @cfg {Integer} searchWinIsShowStatus 查询窗体高度
		 */
//		searchWinHeight : 400,
		sw_Height: {height:280},

		/**
		 * @cfg {Integer} searchWinIsShowStatus 查询窗体宽度
		 */
		sw_Width: 800,

		/**
		 * @cfg {String} searchWinIsShowStatus 查询窗体每行列数
		 */
		sw_columnWidth : '0.5',

		/**
		 * @cfg {Integer} editWinFormHeight 编辑表单高度
		 */
		ew_Height : 250,

		/**
		 * @cfg {String} editWinFormColumnWidth 编辑表单列个数
		 */
		ew_ColumnWidth : '0.33',

		/**
		 * @cfg {boolean} editWinMaximized 是否最大化窗口，默认为否
		 */
		maximized: true,
		/**
		 * @cfg 导出配置
		 */
        vp_gridCfg : {
					stateful : true,
					stateId : s_userCode + '_purchaseOrderDetail',
					stateHeader : true,
					forceFit : false,
					ableExporter:true,
	                /**
	                 *  导出相关样式配置项
	                 */
					exportConfig:{  
			               type:'xlsx',
			               fileName:  $('purchaseOrderDetail.title') + ".xlsx"
			        }, 
	                //sm:true,//选中框隐藏
	                rn:true//序列列隐藏
				},

		/**
		 * @cfg {Array} searchFormItems 执行中订单明细查询字段集合
		 */
		sw_searchFormItems: [ {
			fieldLabel : $('porder.purchaseOrderTime'),
			name : "filter_GE_purchaseOrder_purchaseOrderTime",
			xtype : "datefield",
			format : "Y-m-d"
		}, {
			fieldLabel : $('label.to'),
			name : "filter_LE_purchaseOrder_purchaseOrderTime",
			xtype : "datefield",
			format : "Y-m-d"
		}, {
			fieldLabel : $('label.deliveryDate'),
			name : "filter_GE_vendorTime",
			xtype : "datefield",
			format : "Y-m-d"
		}, {
			fieldLabel : $('label.to'),
			name : "filter_LE_vendorTime",
			xtype : "datefield",
			format : "Y-m-d"
		}, {
			QfieldLabel:'创建时间', 
			fieldLabel:$('label.createTime'), 
			name:'filter_GE_purchaseOrder_createTime', 
			xtype:'datefield', 
			format:'Y-m-d'
		}, {
			QfieldLabel:'至', 
			fieldLabel:$('label.to'), 
			name:'filter_LE_purchaseOrder_createTime', 
			xtype:'datefield', 
			format:'Y-m-d'
		},{
			fieldLabel : $('purchasingOrg.code'),
			name : "filter_LIKE_purchaseOrder_purchasingOrgCode"
		}, {
			fieldLabel : $('purchasingGroup.code'),
			name : "filter_LIKE_purchaseOrder_purchasingGroupCode"
		}, {
			fieldLabel : $('vendor.code'),
			name : "filter_LIKE_purchaseOrder_vendorErpCode"
		}, {
			fieldLabel : $('vendor.name'),
			name : "filter_LIKE_purchaseOrder_vendorName"
		}, {
			fieldLabel : $('materialInfo.code'),
			name : "filter_LIKE_materialCode"
		}, {
			fieldLabel : $("materialInfo.name"),
			name : "filter_LIKE_materialName"
		}, {
			fieldLabel : $('batchCharacter.purchaseOrderCode'),
			name : "filter_LIKE_purchaseOrder_erpPurchaseOrderNo"
		}, {
			fieldLabel : $('label.createUserName'),
			name : 'filter_LIKE_purchaseOrder_createUserName'
		} ]


	}
    }
});
