/**
 * @class {Cp.receive.ShoppingNoticeReceivinViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 送货点收配置
 */
Ext.define('Cp.receive.ShoppingNoticeReceivinViewModel',{
	extend:'Ext.ux.app.ViewModel',
    alias:'viewmodel.shoppingNoticeReceivinViewModel',
    requires:[
    	'Ext.ux.form.DateTimeField',
    	'Sl.masterdata.VendorSelectWin'
    ],
   config:{
    	/**
	 * @cfg {Object} stores
	 * 相关store归集
	 * 
	 * - **deliveryTypeStore** - 送货类型
	 * - **stockLocationStore** - 库存地点
	 * 
	 */
	stores: {
		/**
		 * 送货类型
		 */
		deliveryTypeStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/datadict/getall?groupCode=deliveryTypes',
				type: 'ajax'
			},
			fields: ['itemCode', 'itemName'],
			autoLoad: true
		}),

		/**
		 * 库存地点
		 */
		stockLocationStore: Ext.create('Ext.data.JsonStore',{
			proxy:{
			url: path_masterdata + '/md/stocklocation/getall',
			type:'ajax'
			},
			fields: ['stockLocationCode', 'stockLocationName'],
			autoLoad: true
		})
	},
  data: {	
  		restFul: true,
		isAudit: false,
	   	isExtend: true,
	   	
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
		 * @cfg {String} dealUrl 
		 * 方法处理url
		 */
		dealUrl: '#{path}/cp/shoppingnoticereceiving',

		/**
		 * @cfg {String} moduleName 
		 * 模块名称
		 */
		moduleName: $('delivery'),

		/**
		 * @cfg {String} triggerField 
		 * 触发域（字段名）
		 */
		vp_triggerField: 'deliveryCode',

		/**
		 * @cfg {String} logModuleCode 
		 * 日志code
		 */
	    vp_logModuleCode:'SHD',

		/**
		 * @cfg {Array} hideVpBtn 
		 * 对固化的按钮进行隐藏操作
		 */
		vp_hideListBtn: ['add'],

		/**
		 * @cfg {String} moduleCode
		 * 模块编码
		 */
		vp_billTypeCode: 'CR',

		/**
		 * @cfg {Array} vpSubTab 
		 * 列表底部tab集合
//		 */ 
		vp_subTab:['detailGrid','logTab'],
  

      
		/**
		 * @cfg {Object} editWinCenterTab 
		 * 编辑窗体底部tab集合
		 */
 	   	ew_centerTab:  {
	 		items: ['detailGrid']
    	},
 
	
		/**
		 * @cfg {Object} nextBillState - default New 
		 * 提交后下一步的状态
		 */
		nextBillState: 'New',

		/**
		 * @cfg {Integer} activeTab
		 * 默认展示的tab页
		 */
		vp_activeTab: 0,

		/**
		 * @cfg {Integer} tabHeight
		 * 底部tab高度
		 */
		vp_tabHeight: 300,

		/**
		 * @cfg {boolean} editWinMaximized
		 * 是否最大化窗口，默认为否
		 */
		maximized : true,
		/**
		 * @cfg {String} playListMode
		 * normal/audit/undeal //三种列表模式
		 */
		playListMode: 'normal',
		

		/**
		 * @cfg {String} controllerClassName
		 * 控制类类名称
		 */
		controllerClassName: 'Cp.receive.ShoppingNoticeReceivinController',

		/**
		 * @cfg {Boolean} searchWinIsShowStatus
		 * 查询窗体是否显示状态查询
		 */
		sw_isShowStatus: false, 

		/**
		 * @cfg {Integer} searchWinHeight
		 * 查询窗体高度
		 */
		sw_Height: 330,

		/**
		 * @cfg {Integer} searchWinWidth
		 * 查询窗体宽度
		 */
		sw_Width: 800,

		/**
		 * @cfg {String} searchFormColumnWidth
		 * 查询表单每行列数
		 */
		sw_columnWidth:'0.5',


		/**
		 * @cfg {boolean} singleSelect
		 * 列表是否单选 true
		 */
		singleSelect: true,

		/**
		 * @me {String} methodName
		 * 跳转前的方法 true
		 */
		methodName: 'list',
		
		/**
		 * @cfg {Array} addVpBtn 
		 * 在固化的按钮基础上追加按钮
		 * - **release** - 点收
		 */
		vp_addListBtn : [{
			 name:'release',
			 index:3,
			 Qtext:'点收',
			 text:$('shoppingnotice.release'),  
		     build:power.release,    
		     iconCls:'icon-accept',
		     handler: 'vpReleaseHandler'
		},{
			name: 'close',
			index: 4,
			Qtext: '关闭',
			text: $('button.close'),
			build: power.close,
			iconCls: 'icon-close',
			handler: 'vpCloseHandler'
		}],
		/**
		 * @cfg 
		 * 列表grid 配置项
		 */
		vp_gridCfg: { 
			stateHeader : true,
			stateful : true,
			stateId : s_userCode + '_ShoppingNoticeRe',
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
		 * 列表Store
		 */
	    vp_gridStore: {
			idProperty:'deliveryId',
			url:'#{dealUrl}/list',
			sort:'deliveryId',
			dir:'desc',
			remoteSort: true,
			/*baseParams: {
				initStates:me.get('initStates')
			},*/
			listeners: {
					beforeload:"gridStoreBeforeLoad",
					load:"gridStoreLoad"
		    }
	    },
		/**
		 * @cfg {Array} gridColumn 
		 * 列表对象 列属性配置项
		 */
		vp_gridColumn: [
			{Qheader:'送货单号',header:$('delivery.deliveryCode'), dataIndex:'deliveryCode', width: 150, renderer: 'rendererNo',tipable:true}, 
			{Qheader:'单据状态',header:$('delivery.status'),dataIndex:'status',renderer: 'rendererStatus',exportRenderer: true},
			{Qheader:'采购组织编码',header:$('delivery.purchasingOrgCode'),dataIndex:'purchasingOrgCode',width:120,tipable:true},
			{Qheader:'采购组织名称',header:$('delivery.purchasingOrgName'),dataIndex:'purchasingOrgName',width:200,tipable:true},
			{Qheader:'供应商编码',header:$('delivery.vendorCode'),dataIndex:'vendorErpCode',tipable:true},
			{Qheader:'供应商名称',header:$('delivery.vendorName'),dataIndex:'vendorName',width:250,tipable:true},
			{Qheader:'工厂名称',header:$('delivery.plantName'),dataIndex:'plantName',width:250,tipable:true},
			{Qheader:'库存地点名称',header:$('delivery.storageLocationName'),dataIndex:'storageLocationName',width:120,tipable:true},
			{Qheader:'送货日期',header:$('delivery.deliveryDate'),dataIndex:'deliveryDate',xtype: 'datecolumn',format: 'Y-m-d',width:120,exportRenderer:true},
			{Qheader:'送达日期',header:$('delivery.serviceDate'),dataIndex:'serviceDate',xtype: 'datecolumn',format: 'Y-m-d',exportRenderer:true},
			//hidden
			{Qheader:'id',header:$('delivery.deliveryId'),dataIndex:'deliveryId',disabled:true},
			{Qheader:'工厂编码',header:$('delivery.plantCode'),dataIndex:'plantCode', disabled: true},
			{Qheader:'库存地点编码',header:$('delivery.storageLocationCode'),dataIndex:'storageLocationCode',width:120,disabled:true},
			{Qheader:'送货方式 1:自送;2:快递3:托运4:自提 ',header:$('delivery.deliveryTypes'),dataIndex:'deliveryTypes', disabled:true},
			{Qheader:'快递单号',header:$('delivery.trackingNumber'),dataIndex:'trackingNumber',disabled:true},
			{Qheader:'同步状态',header:$('delivery.synchronizeStatus'),dataIndex:'synchronizeStatus', disabled:true},
			{Qheader: '创建人', header: $('label.createUserName'), dataIndex: 'createUserName', width : 100,exportRenderer: true },
			{Qheader: '创建日期', header: $('label.createTime'), dataIndex: 'createTime', renderer : 'rendererDateTime',width : 140,exportRenderer: true },
			{Qheader:'制单者ID', header:$('shoppingnotice.createUserrId'), dataIndex:'createUserId',disabled:true},
			{Qheader:'修改日期', header:$('label.modifyTime'), dataIndex:'modifyTime',disabled:true},
			{Qheader:'修改者名称', header:$('label.modifyUserName'), dataIndex:'modifyUserName',disabled:true},
			{Qheader:'修改者ID', header:$('shoppingnotice.modifyId'), dataIndex:'modifyId',disabled:true}
		],

	    /**
	     * @cfg {Integer} editWinFormHeight
	     * 编辑表单高度
	     */
	    ew_height:165,

	    /**
	     * @cfg {String} editWinFormColumnWidth
	     * 编辑表单列个数
	     */
	    ew_columnWidth:'0.33',

	    /**
	     * @cfg {boolean} editWinMaximized
	     * 是否最大化窗口，默认为否
	     */
	    maximized: true,

		/**
		 * @cfg {Array} editFormItems 
		 * 编辑form表单
		 */
		ew_editFormItems :  [
			{
				xtype: 'hidden',
				fieldLabel: $('delivery.deliveryId'),
				name: 'model.deliveryId'
			}, {
				xtype: 'hidden',
				fieldLabel: $('delivery.clientCode'),
				name: 'model.clientCode'
			}, {
				QfieldLabel: '送货单号',
				fieldLabel: $('delivery.deliveryCode'),
				name: 'model.deliveryCode',
				value: $('dict.autogeneration'),
				readOnly: true
			}, {
				QfieldLabel: '采购组织编码',
				fieldLabel: $('delivery.purchasingOrgCode') + '<font color="red"> *</font>',
				name: 'model.purchasingOrgCode',
				readOnly: true

			}, {
				QfieldLabel: '采购组织名称',
				fieldLabel: $('delivery.purchasingOrgName'),
				name: 'model.purchasingOrgName',
				readOnly: true
			}, {
				QfieldLabel: '供应商编码',
				fieldLabel: $('delivery.vendorCode') + '<font color="red"> *</font>',
				name: 'model.vendorErpCode',
				readOnly: true
			}, {
				QfieldLabel: '供应商名称',
				fieldLabel: $('delivery.vendorName'),
				name: 'model.vendorName',
				readOnly: true
			}, {
				QfieldLabel: '送货日期',
				fieldLabel: $('delivery.deliveryDate') + '<font color="red"> *</font>',
				name: 'model.deliveryDate',
				xtype: 'datefield',
				format: 'Y-m-d',
				hidden: true
			}, {
				QfieldLabel: '收货人',
				fieldLabel: '收货人',
				name: 'model.consignee',
				value:s_userName,
				hidden: true
			}, {
				QfieldLabel: '收货日期',
				fieldLabel: $('shoppingnotice.receiptDate') + '<font color="red"> *</font>',
				name: 'model.receivingTime',
				xtype: 'datefield',
				format: 'Y-m-d',
				hidden: true
			}, {
				QfieldLabel: '单据状态',
				fieldLabel: $('delivery.status'),
				name: 'model.status',
				hidden: true
			}, {
				QfieldLabel: '工厂编码',
				fieldLabel: $('delivery.plantCode') + '<font color="red"> *</font>',
				name: 'model.plantCode',
				readOnly: true
			}, {
				QfieldLabel: '工厂名称',
				fieldLabel: $('delivery.plantName'),
				name: 'model.plantName',
				hidden: true
			}, {
				QfieldLabel: '库存地点编码',
				fieldLabel: $('delivery.storageLocationCode'),
				name: 'model.storageLocationCode',
				xtype: 'uxcombo',
				bind : {
						store : '{stockLocationStore}'
					}, 
				innerTpl: true,
				valueField: 'stockLocationCode',
				displayField: 'stockLocationName',
				displayValue: 'stockLocationCode',
				listeners: {
					'select': 'gridDtlStorageLocationCodeSelect',
					 'clear':'gridDtlStorageLocationCodeClear'
				}
			}, {
				QfieldLabel: '库存地点名称',
				fieldLabel: $('delivery.storageLocationName'),
				name: 'model.storageLocationName',
				readOnly: true
			}, {
				QfieldLabel: '送货方式 1:自送;2:快递3:托运4:自提 ',
				fieldLabel: $('delivery.deliveryTypes') + '<font color="red"> *</font>',
				name: 'model.deliveryTypes',
				hiddenName: 'model.deliveryTypes',
				hidden: true
			}, {
				QfieldLabel: '快递单号',
				fieldLabel: $('delivery.trackingNumber'),
				name: 'model.trackingNumber',
				hidden: true
			}, {
				QfieldLabel: '送达日期',
				fieldLabel: $('delivery.serviceDate'),
				name: 'model.serviceDate',
				xtype: 'datefield',
				format: 'Y-m-d',
				hidden: true
			}, {
				QfieldLabel: '同步状态',
				fieldLabel: $('delivery.synchronizeStatus'),
				name: 'model.synchronizeStatus',
				hidden: true
			}, {
				QfieldLabel: '制单者名称',
				fieldLabel: $('shoppingnotice.createUserName'),
				name: 'model.createUserName',
				hidden: true
			}, {
				QfieldLabel: '创建日期',
				fieldLabel: $('label.createTime'),
				name: 'model.createTime',
				xtype: 'datefield',
				format: 'Y-m-d H:i:s',
				hidden: true
			}, {
				QfieldLabel: '制单者ID',
				fieldLabel: $('shoppingnotice.createUserrId'),
				name: 'model.createUserId',
				hidden: true
			}, {
				QfieldLabel: '修改日期',
				fieldLabel: $('label.modifyTime'),
				name: 'model.modifyTime',
				xtype: 'datefield',
				format: 'Y-m-d H:i:s',
				hidden: true
			}, 
			{QfieldLabel: '修改者名称', fieldLabel: $('label.modifyUserName'), name: 'model.modifyUserName', hidden: true },
			{QfieldLabel: '修改者ID', fieldLabel: $('shoppingnotice.modifyId'), name: 'model.modifyId', hidden: true } ],
		
		

		/**
		 * @cfg {Object} detailGrid 
		 * 编辑明细
		 */
		detailGrid: {
			tabTitle:$('deliveryDtl'),
			xtype:'uxeditorgrid',
			foreignKey:'delivery_deliveryId',
			tabClassName:'deliveryDtls',
			validField:['acceptQty'],//需要提交的细单字段,
			formFieldReadyArr:[],
			viewConfig: {
				autoScroll:true
			},
			stateHeader : true,
			stateful : true,
			stateId : s_userCode + '_shoppingNoticeRecvDtl',
			forceFit:false,
			allowEmpty:false, //明细条数是否可以为空
			cm:{
				defaultSortable:false,
				defaults:{ menuDisabled:true},
				columns:[
					{Qheader: '采购订单号', header: $('deliveryDtl.purchaseOrderCode'), dataIndex: 'purchaseOrderCode', width: 150 },
					{Qheader: '采购订单号', header: $('deliveryDtl.purchaseOrderCode'), dataIndex: 'erpPurchaseOrderNo', width: 150,hidden:true },
			{Qheader: '行号', header: $('deliveryDtl.lineNumber'), dataIndex: 'lineNumber'},
			{Qheader: '采购员', header: $('deliveryDtl.buyer'), dataIndex: 'buyer', xtype:'hidden'},
			{Qheader: '物料编码', header: $('deliveryDtl.materialCode'), dataIndex: 'materialCode'},
			{Qheader: '物料名称', header: $('deliveryDtl.materialName'), dataIndex: 'materialName', width: 150 },
			{Qheader: '单位编码', header: $('deliveryDtl.unitCode'), dataIndex: 'unitCode'},
			{Qheader: '订单数量', header: $('deliveryDtl.orderNumber'), dataIndex: 'orderNumber', align: 'right', renderer: 'rendererNumber'},
			{Qheader: '已收货量', header: $('deliveryDtl.receivedNumber'), dataIndex: 'receivedQty', align: 'right', renderer: 'rendererNumber'},
			{Qheader: '退货量', header: $('deliveryDtl.returnNumber'), dataIndex: 'returnNumber', align: 'right', renderer: 'rendererNumber', xtype:'hidden'},
			{Qheader: '可送数量', header: $('deliveryDtl.canSentNumber'), dataIndex: 'canSentNumber', align: 'right', renderer: 'rendererNumber', xtype:'hidden'},
			{Qheader: '送货数量', header: $('deliveryDtl.deliveryNumber'), dataIndex: 'deliveryNumber', align: 'right', renderer: 'rendererNumber'},
			{
				Qheader: '库存地点编码',
				header: $('delivery.storageLocationCode'),
				dataIndex: 'storageLocationCode',
				width: 120,
				//自定义属性
                customAttr:{
                	editable:true//是否显示可以编辑背景颜色
                },
				editor: {
					xtype: 'uxcombo',
					bind : {
						store : '{stockLocationStore}'
					},
					innerTpl: true,
					valueField: 'stockLocationCode',
					displayField: 'stockLocationCode'
				}
			},
			{
				Qheader: '点收数量',
				header: $('shoppingnoticedetail.acceptQty'),
				dataIndex: 'acceptQty',
				//自定义属性
                customAttr:{
                	allowBlank: false,
                	editable:true//是否显示可以编辑背景颜色
                },
				editor: {
					decimalPrecision: 3,
					blankText: '不能为空',
					allowNegative: false
				},
				align: 'right',
				renderer: 'rendererNumber'
			},
			{Qheader: 'id', header: $('deliveryDtl.deliveryDtlId'), dataIndex: 'deliveryDtlId', disabled: true },
			{Qheader: '送货管理id', header: $('deliveryDtl.deliveryId'), dataIndex: 'delivery_deliveryId', disabled: true },
			{Qheader: '数据来源明细ID', headRer: $('censorQuality.purchaseOrderDetailId'), dataIndex: 'orderDetailId', disabled: true },
			{Qheader: '数据来源明细ID', headRer: $('censorQuality.purchaseOrderDetailId'), dataIndex: 'sendDetailId', disabled: true },
			{Qheader: '取消按钮', header: $('deliveryDtl.cancelBtn'), dataIndex: 'cancelBtn', disabled: true },
			{Qheader: '取消标识', header: '取消标识', dataIndex: 'cancelFlag', disabled: true },
			{Qheader: '关闭按钮', header: $('deliveryDtl.closeBtn'), dataIndex: 'closeBtn', disabled: true },
			{Qheader: '关闭标识', header: '关闭标识', dataIndex: 'closeFlag', disabled: true },
			{Qheader: '行项目类别', header: $('deliveryDtl.lineItemTypes'), dataIndex: 'lineItemTypes', disabled: true },
			{Qheader: '单位名称', header: $('deliveryDtl.unitName'), dataIndex: 'unitName', disabled: true },
			{Qheader: '已送数量', header: $('deliveryDtl.toSentNumber'), dataIndex: 'toSentNumber', align: 'right', disabled: true },
			{Qheader: '库存地点名称', header: $('delivery.storageLocationName'), dataIndex: 'storageLocationName', disabled: true },
			{Qheader: '备注', header: $('deliveryDtl.remark'), dataIndex: 'remark', disabled: true },
			{Qheader: '排程单号', header: $('deliveryDtl.scheduleCode'), dataIndex: 'scheduleCode', width: 120, disabled: true },
			{Qheader: '附件', header: $('deliveryDtl.annex'), dataIndex: 'annex', width: 150, disabled: true },
			{Qheader: '送货细单类型', header: $('deliveryDtl.dataFrom'), dataIndex: 'dataFrom', disabled: true, disabled: true }]
			},
			store:{
			idProperty: 'deliveryDtlId',
			url:'#{dealUrl}/finddeliverydtlall',
			sort: 'deliveryDtlId',
			autoLoad: true,
			dir: 'desc'
		},
			listeners:{
				'edit':'gridDtlEdit',
				'beforeedit':'gridDtlBeforeedit'
			
			},
			pageSize:0,
		   //窗口关闭当前组件值被重置后调用 参数o为当前组件
			hideWinAndResetAfter:function(o){
			}
		},

		/**
		 * @cfg {Array} searchFormItems 
		 * 查询字段集合
		 */
		sw_searchFormItems: [
			{
				QfieldLabel:'送货单号',
				fieldLabel:$('delivery.deliveryCode'),
				name:'filter_LIKE_deliveryCode'
			},{
				QfieldLabel:'工厂名称',
				fieldLabel:$('delivery.plantName'),
				name:'filter_LIKE_plantName'
			},{
				QfieldLabel:'采购组织编码',
				fieldLabel:$('delivery.purchasingOrgCode'),
				name:'filter_LIKE_purchasingOrgCode'
			},{
				QfieldLabel:'采购组织名称',
				fieldLabel:$('delivery.purchasingOrgName'),
				name:'filter_LIKE_purchasingOrgName'
			},{
				QfieldLabel:'供应商编码',
				fieldLabel:$('delivery.vendorCode'),
				name:'filter_LIKE_vendorErpCode'
			},{
				QfieldLabel:'供应商名称',
				fieldLabel:$('delivery.vendorName'),
				name:'filter_LIKE_vendorName'
			},{
				QfieldLabel:'送货日期',
				fieldLabel:$('delivery.deliveryDate'),
				name:'filter_GE_deliveryDate',
				xtype:'datefield',
				format:'Y-m-d'
			},{
				QfieldLabel:'至',
				fieldLabel: $('label.to'),
				name:'filter_LT_deliveryDate',
				xtype:'datefield',
				format:'Y-m-d'
			},{
				QfieldLabel:'收货日期',
				fieldLabel:$('delivery.receivingDate'),
				name:'filter_GE_serviceDate',
				xtype:'datefield',
				format:'Y-m-d'
			},{
				QfieldLabel:'至',
				fieldLabel: $('label.to'),
				name:'filter_LT_serviceDate',
				xtype:'datefield',
				format:'Y-m-d'
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
				fieldLabel : $('label.createUserName'),
				name : 'filter_LIKE_createName'
			}]
		}
	}
});