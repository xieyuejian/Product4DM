/**
 * @class {Cp.receive.ReceivingNoteViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 收货查询配置
 */
Ext.define('Cp.receive.ReceivingNoteViewModel',{
	extend:'Ext.ux.app.ViewModel',
    alias:'viewmodel.receivingNoteViewModel',
    requires:[
    	'Ext.ux.form.DateTimeField',
    	'Sl.masterdata.VendorSelectWin',
    	"Ext.srm.ux.UxFileUtils"
    ],
     config:{
    data: {	
		/**
		 * @cfg {String} dealUrl 
		 * 方法处理url
		 */
		dealUrl: path_srment + '/cp/receivingnote',
		isExtend:true,

		/**
		 * @cfg {String} moduleName 
		 * 模块名称
		 */
		moduleName: $('receivingNote.title'),

		/**
		 * @cfg {String} triggerField 
		 * 触发域（字段名）
		 */
		vp_triggerField:  'sendScheduleNo',

		/**
		 * @cfg {Array} hideVpBtn 
		 * 对固化的按钮进行隐藏操作
		 */
		vp_hideListBtn: ["delete", "add", "edit", "view"],

		/**
		 * @cfg {String} moduleCode
		 * 模块编码
		 */
		vp_billTypeCode: 'CR',

	

		/**
		 * @cfg {Array} vpListEditStateFn 
		 * 列表界面按钮控制
		 */
		vp_listEditStateFn: [{
			'chargeOff': function(r) {
				if (parseFloat(r.get('canChargeOffNum')) > 0 && r.get('receiptBillFlag') == '0' && r.get('origin') == '0') {
					return true;
				} else {
					return false;
				}
			}
		}],
		/**
		 * @cfg {Object} nextBillState - default New 
		 * 提交后下一步的状态
		 */
		nextBillState: 'New',

		/**
		 * @cfg {Integer} activeTab
		 * 默认展示的tab页
		 */
		vp_activeTab:  0,

		/**
		 * @cfg {Integer} tabHeight
		 * 底部tab高度
		 */
		vp_tabHeight:300,

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
		 * 单据状态
		 */
		initStatesStr:'',
		
		/**
		 * @cfg {String} controllerClassName
		 * 控制类类名称
		 */
		controllerClassName: 'Cp.receive.ReceivingNoteController',

		/**
		 * @cfg {Boolean} searchWinIsShowStatus
		 * 查询窗体是否显示状态查询
		 */
		sw_isShowStatus: true, 

		/**
		 * @cfg {Integer} searchWinHeight
		 * 查询窗体高度
		 */
		sw_Height: 450,

		/**
		 * @cfg {Integer} searchWinWidth
		 * 查询窗体宽度
		 */
		sw_Width: 800,

		/**
		 * @cfg {String} searchFormColumnWidth
		 * 查询表单每行列数
		 */
		sw_columnWidth: '0.5',

	

		/**
		 * @cfg {boolean} isAudit
		 * 是否需要右键审核
		 */
		isAudit: false,

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
		vp_addListBtn: [{
			Qtext: "冲销",
			text: $("button.writeoff"),
			name: "chargeOff",
			index: 0,
			build: power['chargeoff'],
			iconCls: "icon-write-off",
			handler: 'vpChargeOffHandler'
		},{
			Qtext: "模板下载",
			text: $("button.download"),
			iconCls: "icon-download",
			build: power['download'],
			index: 1,
			handler: 'vpDownloadHandler'
		},{
			Qtext: "导入",
			text: $("button.import"),
			iconCls: "icon-putin",
			build: power['import'],
			index: 2,
			handler: 'vpImportHandlde'
		}/*,{
			name: 'export',
			text: $('button.exportExcel'),
			index: 3,
			iconCls: 'icon-export',
			build: power['export'],
			handler: 'vpExportHandler'
		}*/],

	

		/**
		 * @cfg {Object} gridStore 
		 * 列表Store
		 */
		vp_gridStore:  {
			idProperty: 'receivingNoteId',
			url:  '#{dealUrl}/list',
			sort: 'receivingNoteId',
			dir: 'desc',
			remoteSort: true,
			listeners: {
				beforeload:'gridStoreBeforeLoad',
				load:'gridStoreLoad'
			}
		},

		/**
		 * @cfg {Array} gridColumn 
		 * 列表对象 列属性配置项
		 */
		vp_gridColumn: [
			{Qheader: "收货单编号", header: $("receivingnote.grnNo"), dataIndex: "receivingNoteNo", width: 150,tipable:true }, 
			{Qheader: "采购订单号", header: $("porder.string5"), dataIndex: "purchaseOrderNo", width: 160 ,hidden:true}, 
			{Qheader: "采购订单号", header: $("porder.string5"), dataIndex: "erpPurchaseOrderNo", width: 160 ,tipable:true}, 
			{Qheader: "送货单编号", header: $("delivery.deliveryCode"), dataIndex: "shoppingNoticeNo", width: 160,tipable:true}, 
			{Qheader: "供应商代码", header: $("vendor.code"), dataIndex: "vendorErpCode",tipable:true}, 
			{Qheader: "供应商名称", header: $("vendor.name"), dataIndex: "vendorName", width: 200,tipable:true }, 
			{Qheader: "物料编码", header: $("materialInfo.code"), dataIndex: "materialCode",tipable:true}, 
			{Qheader: "物料名称", header: $("materialInfo.name"), dataIndex: "materialName", width: 200 ,tipable:true}, 
			{Qheader: "收货数量", header: $("receiptBillDetail.qtyReceive"), dataIndex: "qtyReceive", align: "right", renderer: 'rendererNumber',tipable:true}, 
			{Qheader: "单位编码", header: $("shoppingnoticedetail.unitCode"), dataIndex: "unitCode"}, 
			{
				Qheader: "收退货标识1:收货2:退货",
				header: $("receivingnote.acceptReturnFlag"),
				dataIndex: "acceptReturnFlag",
				renderer: 'acceptReturnFlagRenderer',
				exportRenderer: true
			},
			{Qheader: "质检状态", header: $("censorQuality.resultCode"), dataIndex: "status",renderer: 'statusRenderer',exportRenderer: true}, 
			{Qheader: "物料凭证年度", header: $("censorQuality.materialVoucherYear"), dataIndex: "materialCertificateYear", width: 150,tipable:true }, 
			{Qheader: "物料凭证编号", header: $("receivingnote.materialcertificateCode"), dataIndex: "materialCertificateCode", width: 180,tipable:true }, 
			{Qheader: "物料凭证中的项目", header: $("receivingnote.materialcertificateItem"), dataIndex: "materialCertificateItem", width: 150,tipable:true }, 
			{Qheader: "税代码", header: $("receivingnote.taxCode"), dataIndex: "taxCode"}, 
			{Qheader: "税率", header: $("taxRate.taxRateValue"), dataIndex: "taxRate"}, 
			{Qheader: "凭证日期", header: $("receivingnote.certificateDate"), dataIndex: "certificateDate",xtype: 'datecolumn',format: 'Y-m-d',exportRenderer:true}, 
			{Qheader: "过帐日期", header: $("receivingnote.postingDate"), dataIndex: "postingDate",xtype: 'datecolumn',format: 'Y-m-d',exportRenderer:true}, 
			{Qheader: "SAP采购订单行项目", header: $("receivingnote.sappurchaseOrderItem"), dataIndex: "purchaseOrderItem"}, 
			{Qheader: "采购组织编码", header: $("purchasingOrg.code"), dataIndex: "purchasingOrgCode", width: 150 ,tipable:true}, 
			{Qheader: "采购组织名称", header: $("purchasingOrg.name"), dataIndex: "purchasingOrgName", width: 200,tipable:true }, 
			{Qheader: "工厂", header: $("plant.title"), dataIndex: "plantCode",tipable:true}, 
			{Qheader: "采购组编码", header: $("purchasingGroup.code"), dataIndex: "purchasingGroupCode", width: 150,tipable:true }, 
			{Qheader: "采购组名称", header: $("purchasingGroup.name"), dataIndex: "purchasingGroupName", width: 200,tipable:true }, 
			{Qheader: "库存地编码", header: $("receivingnote.storeLocalCode"), dataIndex: "storeLocalCode",tipable:true}, 
			{Qheader: "原物料凭证年度", header: $("receivingnote.omaterialcertificateYear"), dataIndex: "omaterialCertificateYear", width: 150 }, 
			{Qheader: "原物料凭证编号", header: $("receivingnote.omaterialcertificateCode"), dataIndex: "omaterialCertificateCode", width: 180,tipable:true }, 
			{Qheader: "原物料凭证行号", header: $("receivingnote.omaterialcertificateItem"), dataIndex: "omaterialCertificateItem", width: 150 ,tipable:true}, 

			//hidden
			{Qheader: "收货单ID", header: $("receivingnote.grnId"), dataIndex: "receivingNoteId", hidden: true }, 
			{Qheader: "sap采购订单号", header: $("porder.purchaseOrderNo"), dataIndex: "sapPurchaseOrderNo", hidden: true }, 
			{Qheader: "采购订单行ID采购订单行项目", header: $("receivingnote.sappurchaseOrderItem"), hidden: true, dataIndex: "purchaseOrderDetailId"}, 
			{Qheader: "客户端编号", header: $("receivingnote.clientCode"), dataIndex: "clientCode", hidden: true }, 
			{Qheader: "送货单细单ID", header: $("receivingnote.shoppingNoticeDetailId"), dataIndex: "shoppingNoticeDetailId", hidden: true }, 
			{Qheader: "送货明细行号", header: $("receivingnote.shoppingNoticeRowId"), dataIndex: "shoppingNoticeRowId", hidden: true }, 
			{Qheader: "供应商代码", header: $("vendor.code"), dataIndex: "vendorErpCode", hidden: true }, 
			{Qheader: "物料Id", header: $("censorQuality.materialId"), dataIndex: "materialId", hidden: true }, 
			{Qheader: "金额（未含税）", header: $("receivingnote.amountnoTax"), dataIndex: "amountnoTax", hidden: true }, 
			{Qheader: "货币码", header: $("receivingnote.currencyCode"), dataIndex: "currencyCode", hidden: true }, 
			{Qheader: "特殊库存标志(分包:O/寄售:K/标准:S;)", header: $("receivingnote.specialwhseFlag"), dataIndex: "specialwhseFlag", hidden: true }, 
			{Qheader: "金额(未含税)", header: $("receivingnote.certificateAmount"), dataIndex: "certificateAmount", align: "right", hidden: true }, 
			{Qheader: "价格", header: $("receivingnote.price"), dataIndex: "price", hidden: true }, 
			{Qheader: "含税总金额", header: $("receivingnote.totalAmountAndTax"), dataIndex: "totalAmountAndTax", hidden: true }, 
			{Qheader: "总税额", header: $("receivingnote.totalTax"), dataIndex: "totalTax", disabled: true }, 
			{Qheader: "0非限制/1质检/2冻结", header: $("receivingnote.stockType"), dataIndex: "stockType", hidden: true }, 
			{Qheader: "未对账：0，已对账：1", header: $("receivingnote.receiptBillFlag"), dataIndex: "receiptBillFlag", hidden: true }, 
			{Qheader: "PO定价单位数量", header: $("receivingnote.fixPriceQty"), dataIndex: "fixPriceQty", hidden: true }, 
			{Qheader: "PO定价单位", header: $("receivingnote.fixPriceUnitCode"), dataIndex: "fixPriceUnitCode", hidden: true }, 
			{Qheader: "可开票数量", header: $("receivingnote.invoiceQty"), dataIndex: "invoiceQty", hidden: true }, 
			{Qheader: "库存单位数量", header: $("receivingnote.stockQty"), dataIndex: "stockQty", hidden: true }, 
			{Qheader: "库存单位", header: $("receivingnote.stockUnit"), dataIndex: "stockUnit", hidden: true }, 
			{Qheader: '创建人', header: $('label.createUserName'), dataIndex: 'createUserName', width : 100,exportRenderer: true },
			{Qheader: '创建日期', header: $('label.createTime'), dataIndex: 'createTime', renderer : 'rendererDateTime',width : 140,exportRenderer: true },
			{Qheader: "修改日期", header: $("label.modifyTime"), dataIndex: "modifyTime", hidden: true,exportRenderer:true }, 
			{Qheader: "可冲销数量", header: $("receivingnote.canChargeOffNum"), dataIndex: "canChargeOffNum", hidden: true }, 
			{Qheader: "来源", header: $("label.origin"), dataIndex: "origin", hidden: true }
		],

	    /**
	     * @cfg {Integer} editWinFormHeight
	     * 编辑表单高度
	     */
	    ew_height:250,

	    /**
	     * @cfg {String} editWinFormColumnWidth
	     * 编辑表单列个数
	     */
	    ew_columnWidth:'0.33',



		/**
		 * @cfg {Array} editFormItems 
		 * 编辑form表单
		 */
		ew_editFormItems:[
			{QfieldLabel: "收货单编码", fieldLabel: $("receivingnote.grnNo"), name: "model.receivingNoteNo", readOnly: true },
			{
				QfieldLabel: "采购组织编码",
				fieldLabel: $("purchasingOrg.title") + "<font color = 'red'>*</font>",
				name: "model.purchasingOrgCode",
				hiddenName: "model.purchasingOrgCode",
				xtype: 'uxcombo',
				bind: {
					store: '{purchasingOrgStore}'
				},
				innerTpl: true,
				valueField: 'purchasingOrgCode',
				displayField: 'purchasingOrgName',
				readOnly: true
			},
			{QfieldLabel: "送货单编号", fieldLabel: $("delivery.deliveryCode"), name: "model.shoppingNoticeNo", readOnly: true },
			{QfieldLabel: "供应商编码", fieldLabel: $("vendor.code"), name: "model.vendorCode", readOnly: true },
			{QfieldLabel: "供应商名称", fieldLabel: $("vendor.name"), name: "model.vendorName", readOnly: true },
			{QfieldLabel: "采购订单号", fieldLabel: $("porder.string5"), name: "model.purchaseOrderNo", hidden: true },
			{QfieldLabel: "采购订单号", fieldLabel: $("porder.string5"), name: "model.erpPurchaseOrderNo", readOnly: true },
			{QfieldLabel: "物料编码", fieldLabel: $("materialInfo.code"), name: "model.materialCode", readOnly: true },
			{QfieldLabel: "物料名称", fieldLabel: $("materialInfo.name"), name: "model.materialName", readOnly: true },
			{QfieldLabel: "工厂", fieldLabel: $("plant.title"), name: "model.plantCode", readOnly: true },
			{QfieldLabel: "物料凭证年度", fieldLabel: $("censorQuality.materialVoucherYear"), name: "model.materialCertificateYear", readOnly: true },
			{QfieldLabel: "物料凭证编码", fieldLabel: $("receivingnote.materialcertificateCode"), name: "model.materialCertificateCode", readOnly: true },
			{QfieldLabel: "物料凭证年度", fieldLabel: $("receivingnote.omaterialcertificateYear"), name: "model.omaterialCertificateYear", readOnly: true },
			{QfieldLabel: "物料凭证编码", fieldLabel: $("receivingnote.omaterialcertificateCode"), name: "model.omaterialCertificateCode", readOnly: true },
			{QfieldLabel: "物料凭证编码", fieldLabel: $("receivingnote.omaterialcertificateItem"), name: "model.omaterialCertificateItem", readOnly: true },
			{QfieldLabel: "凭证日期", fieldLabel: $("receivingnote.certificateDate"), name: "model.certificateDate", xtype: "datefield", format: "Y-m-d", readOnly: true },
			{QfieldLabel: "收货数量", fieldLabel: $("sendscheduledetail.receiptQty"), name: "model.qtyReceive", readOnly: true },
			{
				fieldLabel: "特殊库存标识",
				hiddenName: "model.specialwhseFlag",
				xtype: "uxcombo",
				hidden: true,
				readOnly: true,
				bind: {
					store: '{specialwhseFlagStore}'
				},
				displayField: "text",
				valueField: "value"
			},
			{
				QfieldLabel: "收退货",
				fieldLabel: $("receivingnote.acceptReturnFlag"),
				hiddenName: "model.acceptReturnFlag",
				name: "model.acceptReturnFlag",
				xtype: "uxcombo",
				readOnly: true,
				bind: {
					store: '{acceptReturnStore}'
				},
				displayField: "text",
				valueField: "value"
			},
			{
				QfieldLabel: "冲销数量",
				fieldLabel: $("receivingnote.chargeOffNum") + "<font color='red'>*</font>",
				name: "model.canChargeOffNum",
				xtype: "numberfield",
				minValue: 1,
				fieldStyle: 'background-color: #FFEDD9;',
				allowBlank: false,
				listeners: {
					blur: 'formCanChargeOffNumChange'
				}
			},
			{xtype: "hidden", fieldLabel: "凭证货币金额(未含税)", name: "model.certificateAmount"},
			{fieldLabel: "收货单id", name: "model.receivingNoteId", xtype: "hidden"},
			{fieldLabel: "收货单id", name: "model.materialCertificateItem", xtype: "hidden"},
			{fieldLabel: "采购订单行ID", name: "model.purchaseOrderDetailId", xtype: "hidden"},
			{fieldLabel: "冲销数量", name: "canChargeOffNum", xtype: "hidden"},
			{
				QfieldLabel: "质检状态",
				fieldLabel: $("censorQuality.status"),
				name: "model.status",
				submitValue: false,
				xtype: 'uxcombo',
				bind: {
					store: '{statusStore}'
				},
				triggerAction: "all",
				readOnly: true
			},
			{
				QfieldLabel: "质检结果代码",
				fieldLabel: $("censorQuality.resultCode"),
				name: "model.resultCode",
				xtype: 'hidden'
			},
			{xtype: "hidden", QfieldLabel: "质检结果名称", fieldLabel: $("censorQuality.resultName"), name: "model.resultName"}
		],
          vp_gridCfg:{
					stateId : s_userCode + '_receivingNote',
					stateHeader : true,
					forceFit : false,
				     exportConfig:{  
		                xtype:"xlsx",
		                fileName:"收货查询.xlsx"
		                
		            }, 
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
		 * @cfg {Array} searchFormItems 
		 * 查询字段集合
		 */
		sw_searchFormItems: [
			{QfieldLabel: "供应商编码", fieldLabel: $("vendor.code"), name: "filter_LIKE_vendorErpCode"},
			{QfieldLabel: "供应商名称", fieldLabel: $("vendor.name"), name: "filter_LIKE_vendorName"},
			{QfieldLabel: "收获单号  ", fieldLabel: $("receivingnote.grnNo"), name: "filter_LIKE_receivingNoteNo"},
			{QfieldLabel: "送货单号", fieldLabel: $("delivery.deliveryCode"), name: "filter_LIKE_shoppingNoticeNo"},
			{QfieldLabel: "采购组织编码", fieldLabel: $("purchasingOrg.code"), name: "filter_LIKE_purchasingOrgCode"},
			{QfieldLabel: "采购组织名称", fieldLabel: $("purchasingOrg.name"), name: "filter_LIKE_purchasingOrgName"},
			{QfieldLabel: "采购组编码", fieldLabel: $("purchasingGroup.code"), name: "filter_LIKE_purchasingGroupCode"},
			{QfieldLabel: "采购组编码", fieldLabel: $("purchasingGroup.name"), name: "filter_LIKE_purchasingGroupName"},
			{QfieldLabel: "物料编码", fieldLabel: $("materialInfo.code"), name: "filter_LIKE_materialCode"},
			{QfieldLabel: "物料名称", fieldLabel: $("materialInfo.name"), name: "filter_LIKE_materialName"},
			{QfieldLabel: "物料凭证年度", fieldLabel: $("censorQuality.materialVoucherYear"), name: "filter_LIKE_materialCertificateYear"},
			{QfieldLabel: "物料凭证编号", fieldLabel: $("receivingnote.materialcertificateCode"), name: "filter_LIKE_materialCertificateCode"},
			{QfieldLabel: "凭证日期", fieldLabel: $("receivingnote.certificateDate"), name: "filter_GE_certificateDate", xtype: "datefield", format: "Y-m-d"},
			{QfieldLabel: "至", fieldLabel: $("label.to"), name: "filter_LE_certificateDate", xtype: "datefield", format: "Y-m-d"},
			{QfieldLabel: "采购单号", fieldLabel: $("porder.purchaseOrderNo"), name: "filter_LIKE_erpPurchaseOrderNo"},
			{QfieldLabel: "收退货标识",
				fieldLabel: $("receivingnote.acceptReturnFlag"),
				name: "filter_EQ_acceptReturnFlag",
				hiddenName: 'filter_EQ_acceptReturnFlag',
				xtype: "uxcombo",
				triggerAction: 'all',
				editable: false,
				allowBlank: true,
				bind: {
					store: '{acceptReturnStore}'
				},
				valueField: 'value',
				displayField: 'text'
			},
			{QfieldLabel:'创建时间', fieldLabel:$('label.createTime'), name:'filter_GE_createTime', xtype:'datefield', format:'Y-m-d'},
	    	{QfieldLabel:'至', fieldLabel:$('label.to'), name:'filter_LE_createTime', xtype:'datefield', format:'Y-m-d'},
	    	{QfieldLabel: '创建人名称',fieldLabel: $('label.createUserName'),name: 'filter_LIKE_createUserName'},
	    	{QfieldLabel: '工厂编码',fieldLabel: $('plant.code'),name: 'filter_LIKE_plantCode'}
		]
	},

	/**
	 * @cfg {Object} stores
	 * 相关store归集
	 * 
	 * - **acceptReturnStore** - 收退货标识
	 * - **purchasingOrgStore** - 采购组织
	 * - **resultStore** - 质检结果
	 * - **statusStore** - 质检状态
	 * - **specialwhseFlagStore** - 特殊库存标识
	 * 
	 */
	stores: {
		/**
		 * 收退货标识
		 */
		acceptReturnStore: Ext.create('Ext.data.ArrayStore', {
			fields: ['value', 'text'],
			data: [
				['101', $("receivingnote.acceptReturnFlag1")],
				['102', $("receivingnote.acceptReturnFlag2")]
			]
		}),

		/**
		 * 采购组织
		 */
		purchasingOrgStore: Ext.create('Ext.data.JsonStore', {
			proxy: {
				url: path_masterdata + '/md/purchasingorganization/getall',
				type: 'ajax'
			},
			autoLoad: true,
			fields: ['purchasingOrgCode', 'purchasingOrgName']
		}),

		/**
		 * 质检结果
		 */
		resultStore: Ext.create('Ext.data.ArrayStore', {
			fields: ['text', 'value'],
			data: [
				["合格", 1],
				["让步接收", 2],
				["拒绝", 3]
			]
		}),

		/**
		 * 质检状态
		 */
		statusStore: Ext.create('Ext.data.ArrayStore', {
			fields: ['value', 'text'],
			data: [
				["TOCHECK", "待检"],
				["CHECKING", "检验中"],
				["CHECKED", "检验完成"],
				["CANCEL", "取消"]
			]
		}),

		/**
		 * 特殊库存标识
		 */
		specialwhseFlagStore: Ext.create('Ext.data.SimpleStore', {
			fields: ['value', 'text'],
			data: [
				['NULL', '标准'],
				['K', '寄售'],
				['O', '分包']
			]
		})
	}
   
     }
});