/**
 * @class {Cp.quality.CensorQualityViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 待检验质检管理配置
 */
Ext.define('Cp.quality.CensorQualityViewModel',{
	extend : 'Ext.ux.app.ViewModel',
    alias: 'viewmodel.censorQualityViewModel',
    requires:[
    	'Ext.ux.form.DateTimeField',
    	'Ext.ux.button.UploadButton'
    ],
    
    /**
     * @cfg {Object} stores
     * 相关store归集
     * - **resultStore** - 质检结果
     * - **statusStore** - 检验结果
     */
	stores: {
		//质检结果
		resultStore: Ext.create('Ext.data.ArrayStore', {
			fields: ['text', 'value'],
			data: [
				['合格', 1],
				['让步接收', 2],
				['拒绝', 3]
			]
		}),
		//检验结果
		statusStore: Ext.create('Ext.data.ArrayStore', {
			fields: ['value', 'text'],
			data: [
				['TOCHECK', '待检'],
				['CHECKING', '检验中'],
				['CHECKED', '检验完成'],
				['CANCEL', '取消']
			]
		})
	},

	data: {
		 /**
         * @cfg {Boolean} isExtend
         * 是否用父类的getCfg 配置方法
       	 */
      	isExtend:true,
      	initStatesStr: "TOCHECK,CHECKING",	
		/**
         * @cfg {Object} gridStore
         * 列表Store配置项
         */
        vp_gridStore: {
        	idProperty: 'censorqualityId',
			url: '#{dealUrl}/list',
			sort: 'censorqualityId',
			dir: 'desc'
        }, 
        
        /**
		 * @cfg 
		 * 列表grid 配置项
		 */
		vp_gridCfg: { 
			stateHeader : s_roleTypes[0]=="V"?false:true,
			stateful : true,
			stateId : s_userCode + '_censorQuality',
			forceFit: false ,
			  /**
             * 是否启用导出功能 true|false
             */
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
          //  sm:false,//选中框隐藏
            rn:true//序列列隐藏
	    },
	
		vp_gridColumn:[
			{Qheader:'检验批号',header:$('censorQuality.censorqualityNo'),dataIndex:'censorqualityNo',width:150,tipable:true},
			{Qheader:'质检状态',header:$('censorQuality.status'),dataIndex:'status',width:130,renderer:'censorQualityRenderer',exportRenderer: true},
			{Qheader:'送检时间',header:$('censorQuality.inspectionTime'),dataIndex:'inspectionTime',width:150,renderer:'rendererDateTime',tipable:true,exportRenderer:true},               
			{Qheader:'物料编码',header:$('censorQuality.materialCode'),dataIndex:'materialCode',width:130,tipable:true},
			{Qheader:'物料名称',header:$('censorQuality.materialName'),dataIndex:'materialName',width:180,tipable:true},
			{Qheader:'送检量',header:$('censorQuality.censorQty'),dataIndex:'censorQty',width:130,tipable:true},
			{Qheader:'单位',header:$('censorQuality.unit'),dataIndex:'unit',width:130},
			{Qheader:'已质检合格量',header:$('censorQuality.checkQualifiedQty'),dataIndex:'checkQualifiedQty',width:130,renderer : 'rendererNumber'},
			{Qheader:'已质检不合格量',header:$('censorQuality.checkUnqualifiedQty'),dataIndex:'checkUnqualifiedQty',width:130,renderer : 'rendererNumber'},
			{Qheader:'已质检让步接收量',header:$('censorQuality.checkReceiveQty'),dataIndex:'checkReceiveQty',width:130,renderer : 'rendererNumber'},
			{Qheader:'质检结果名称',header:$('censorQuality.resultCode'),dataIndex:'resultName',width:130,tipable:true},
			{Qheader:'收货单号',header:$('censorQuality.shoppingNoticeNo'),dataIndex:'receivingNoteNo',width:150,tipable:true},
			{Qheader:'供应商编码',header:$('censorQuality.vendorCode'),dataIndex:'vendorCode',width:130,hidden:true},
			{Qheader:'供应商Erp编码',header:$('censorQuality.vendorCode'),dataIndex:'vendorErpCode',width:130},
			{Qheader:'供应商名称',header:$('censorQuality.vendorName'),dataIndex:'vendorName',width:180,tipable:true},
			{Qheader:'采购订单号',header:$('censorQuality.purchaseOrderNo'),dataIndex:'purchaseOrderNo',width:150},
			{Qheader:'行号',header:$('censorQuality.rowIds'),dataIndex:'rowIds',width:130,tipable:true},
			{Qheader:'凭证年度',header:$('censorQuality.voucherYear'),dataIndex:'voucherYear',width:130},
			{Qheader:'凭证编号',header:$('censorQuality.voucherNo'),dataIndex:'voucherNo',width:150},
			{Qheader:'凭证行项目号',header:$('censorQuality.voucherProNo'),dataIndex:'voucherProNo',width:130,tipable:true},
			{Qheader:'采购组织编码',header:$('censorQuality.purchasingOrgCode'),dataIndex:'purchasingOrgCode',width:160,tipable:true},
			{Qheader:'采购组织名称',header:$('censorQuality.purchasingOrgName'),dataIndex:'purchasingOrgName',width:180,tipable:true},
			{Qheader:'质检时间',header:$('censorQuality.qualityTime'),dataIndex:'qualityTime',width:150,renderer:'rendererDateTime',exportRenderer:true},             
			{Qheader:'备注',header:$('censorQuality.remark'),dataIndex:'remark',width:130},
			{Qheader:'同步状态',header:$('censorQuality.erpSyn'),dataIndex:'erpSyn',width:130,renderer: 'gridErpSynStateRenderer',exportRenderer: true},
			{
                Qheader: '创建者名称',
                header: $('forecast.creatorName'),
                dataIndex: 'createUserName',
                tipable: true
            }, {
                Qheader: '创建时间',
                header: $('label.createTime'),
                dataIndex: 'createTime',
                width:150,
                renderer:'rendererDateTime',
                exportRenderer: true
            }, 
			{Qheader:'送检质检单ID',header:$('censorQuality.censorqualityId'),dataIndex:'censorqualityId',disabled:true},
			{Qheader:'不合格量',header:$('censorQuality.unqualifiedQty'),dataIndex:'unqualifiedQty',disabled:true},
			{Qheader:'让步接收量',header:$('censorQuality.receiveQty'),dataIndex:'receiveQty',disabled:true},
			{Qheader:'合格量',header:$('censorQuality.qualifiedQty'),dataIndex:'qualifiedQty',disabled:true},
//			{Qheader:'质检结果代码',header:$('censorQuality.resultCode'),dataIndex:'resultCode',disabled:true},
			{Qheader:'可检量',header:$('censorQuality.canCheckQty'),dataIndex:'canCheckQty',disabled:true},
			{Qheader:'工厂编码',header:$('censorQuality.plantCode'),dataIndex:'plantCode',disabled:true},
			{Qheader:'工厂名称',header:$('censorQuality.plantName'),dataIndex:'plantName',disabled:true},
			{Qheader:'库存地点编码',header:$('censorQuality.stockCode'),dataIndex:'stockCode',disabled:true},
			{Qheader:'库存地点名称',header:$('censorQuality.stockName'),dataIndex:'stockName',width:180},
			{Qheader:'附件',header:$('censorQuality.uploadFileGroupId'),dataIndex:'uploadFileGroupId',disabled:true},
			{Qheader:'同步信息',header:$('censorQuality.erpReturnMsg'),dataIndex:'erpReturnMsg',disabled:true},
			{Qheader:'送检人员id',header:$('censorQuality.inspectorId'),dataIndex:'inspectorId',disabled:true},
			{Qheader:'送检人员名称',header:$('censorQuality.inspectorName'),dataIndex:'inspectorName',disabled:true},
			{Qheader:'质检人员id',header:$('censorQuality.qualitorId'),dataIndex:'qualitorId',disabled:true},
			{Qheader:'质检人员名称',header:$('censorQuality.qualitorName'),dataIndex:'qualitorName',disabled:true},
			{Qheader:'客户端编码',header:$('censorQuality.clientCode'),dataIndex:'clientCode',disabled:true}
		],

		/**
		 * @cfg {String} dealUrl 
		 * 方法处理url
		 */
		dealUrl:  path_srm +'/cp/censorquality',

		/**
		 * @cfg {String} moduleName 
		 * 模块名称
		 */
		moduleName: $('censorQuality'),

		/**
		 * @cfg {String} triggerField 
		 * 触发域（字段名）
		 */
		vp_triggerField: 'censorqualityNo',

		/**
		 * @cfg {Array} addVpBtn 
		 * 在固化的按钮基础上追加按钮
		 * - **check** - 检验
		 */
		vp_addListBtn: [{
			name: 'check',
			Qtext: '检验',
			text: '检验',
			build: power['check'],
			index: 0,
			iconCls: 'icon-add',
			handler: 'vpCheckHandler'
		}],
	    
		/**
		 * @cfg {Array} hideVpBtn 
		 * 对固化的按钮进行隐藏操作
		 * - **add** - 新建
		 * - **edit** - 编辑
		 * - **delete** - 删除
		 */
		vp_hideListBtn: ['add', 'edit', 'delete'],

		/**
		 * @cfg {String} moduleCode
		 * 质检单模块编码
		 */
		moduleCode: 'ZJD',
		 /**
         * @cfg {String}  vp_logModuleCode 
         *  底部logTab 操作日志 请求参数值
         */
        vp_logModuleCode: "ZJD",
        /**
         * @cfg {String}  vp_billTypeCode
         *  底部msgTab 审核日志 请求参数值默认单据编码 
         */
        vp_billTypeCode: "ZJD",
		/**
		 * @cfg {boolean} isAudit
		 * 是否需要右键审核
		 */
		isAudit: false,

		/**
		 * @cfg {boolean} editWinMaximized
		 * 是否最大化窗口
		 */
		maximized: true,

		vp_subTab: ['logTab'],

		/**
		 * @cfg {String} nextBillState - default New 
		 * 提交后下一步的状态
		 */
		nextBillState: 'New',
		/**
		 * @cfg {Array} hideEditBtn 
		 * 对固化的按钮进行隐藏操作
		 * - **submit** - 提交
		 */
		hideEditBtn: ['submit'],

	    /**
	     * @cfg {Object} finshQualityFormItems 
	     * 已完成质检的表单对象
	     */
	    finshQualityFormItems:{
			tabTitle:'已质检',//'平台门户',
	        xtype:'formpanel',
	        tabClassName:'finshQualitytab',
	        foreignKey:'censorqualityId',//对应主单的主键 
	        belongParent:true,
	        columnWidth:.33, 
	        items: [{
				QfieldLabel:'已质检合格量',
				fieldLabel:$('censorQuality.checkQualifiedQty'),
				readOnly:true,
				value:0,
				name:'model.checkQualifiedQty'
			},{
				QfieldLabel:'已质检不合格量',
				fieldLabel:$('censorQuality.checkUnqualifiedQty'),
				readOnly:true,
				value:0,
				name:'model.checkUnqualifiedQty'
			},{
				QfieldLabel:'已质检让步接收量',
				fieldLabel:$('censorQuality.checkReceiveQty'),
				readOnly:true,
				value:0,
				name:'model.checkReceiveQty'
			}]  
	    },

		/**
		 * @cfg {Object} censorQualityFormItems 
		 * 本次质检编辑表单对象
		 */
		censorQualityFormItems: {
			tabTitle: '本次检验',
			xtype: 'formpanel',
			tabClassName: 'censorQualityTab',
			foreignKey: 'censorqualityId', //对应主单的主键 
			belongParent: true,
			columnWidth: .33,
			items: []
		},

	    
	    /**
		 * @cfg {Array} searchFormItems 
		 * 质检管理查询字段集合
		 */
		sw_searchFormItems: [
			{xtype:'hidden', fieldLabel:$('censorQuality.censorqualityId'), name : 'filter_LIKE_censorqualityId'},
			{QfieldLabel:'检验批号', fieldLabel:$('censorQuality.censorqualityNo'), name:'filter_LIKE_censorqualityNo'},
			{QfieldLabel:'收货单号', fieldLabel:$('censorQuality.shoppingNoticeNo'), name:'filter_LIKE_receivingNoteNo'},
			{QfieldLabel:'采购订单号', fieldLabel:$('censorQuality.purchaseOrderNo'), name:'filter_LIKE_purchaseOrderNo'},
			{QfieldLabel:'物料编码', fieldLabel:$('censorQuality.materialCode'), name:'filter_LIKE_materialCode'},
			{QfieldLabel:'物料名称', fieldLabel:$('censorQuality.materialName'), name:'filter_LIKE_materialName'},
			{QfieldLabel:'供应商编码', fieldLabel:$('censorQuality.vendorCode'), name:'filter_LIKE_vendorCode_OR_vendorErpCode'},
			{xtype:'hidden', QfieldLabel:'供应商Erp编码', fieldLabel:$('censorQuality.vendorErpCode'), name:'filter_LIKE_vendorErpCode'},
			{QfieldLabel:'供应商名称', fieldLabel:$('censorQuality.vendorName'), name:'filter_LIKE_vendorName'},
			{QfieldLabel:'凭证编号', fieldLabel:$('censorQuality.voucherNo'), name:'filter_LIKE_voucherNo'},
			 {
	                QfieldLabel: '创建时间',
	                fieldLabel: $('label.createTime'),
	                name: 'filter_GE_createTime',
	                anchor: '90%',
	                xtype: 'datefield',
	                format: 'Y-m-d'
	            }, {
	                QfieldLabel: '到',
	                fieldLabel: $('label.to'),
	                name: 'filter_LE_createTime',
	                anchor: '90%',
	                xtype: 'datefield',
	                format: 'Y-m-d'
	            },  {
	                QfieldLabel: '创建人名称',
	                fieldLabel: $('label.createUserName'),
	                name: 'filter_LIKE_createUserName'
	            }
		],

		/**
		 * @cfg {String} playListMode
		 * normal/audit/undeal //三种列表模式
		 */
		playListMode: "normal",

		/**
		 * @cfg {String} controllerClassName
		 * 控制类类名称
		 */
		controllerClassName: 'Cp.quality.CensorQualityController',

		/**
		 * @cfg {Integer} editWinFormHeight
		 * 编辑表单高度
		 */
		ew_Height: 250,

		/**
		 * @cfg {String} editWinFormColumnWidth
		 * 编辑表单列个数
		 */
		ew_columnWidth: '0.33',

		/**
		 * @cfg {Array} vpListEditStateFn 
		 * 列表界面按钮控制
		 */
		vp_listEditStateFn: [{'view':true}],

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
		 * @cfg {String} searchWinIsShowSynStatus
		 * 是否显示同步状态进行查询
		 */
		sw_isShowSynStatus: true,

		/**
		 * @cfg {Object} editWinCenterTab 
		 * 编辑窗体底部tab集合
		 */
		ew_centerTab: {
			items: []
		},

		ew_editFormItems: [
 	    	{xtype: 'hidden', fieldLabel: $('censorQuality.censorqualityId'), name: 'model.censorqualityId'},
 	    	{QfieldLabel: '检验批号', fieldLabel: $('censorQuality.censorqualityNo'), name: 'model.censorqualityNo'},
 	    	{QfieldLabel: '供应商编码', fieldLabel: $('censorQuality.vendorCode'), name: 'model.vendorCode',hidden:true},
 	    	{QfieldLabel: '供应商Erp编码', fieldLabel: $('censorQuality.vendorCode'), name: 'model.vendorErpCode'},
 	    	{QfieldLabel: '供应商名称', fieldLabel: $('censorQuality.vendorName'), name: 'model.vendorName'},
 	    	{QfieldLabel: '物料编码', fieldLabel: $('censorQuality.materialCode'), name: 'model.materialCode'},
 	    	{QfieldLabel: '物料名称', fieldLabel: $('censorQuality.materialName'), name: 'model.materialName'},
 	    	{QfieldLabel: '单位', fieldLabel: $('censorQuality.unit'), name: 'model.unit'},
 	    	{QfieldLabel: '送检量', fieldLabel: $('censorQuality.censorQty'), name: 'model.censorQty'},
 	    	{QfieldLabel: '可检量', fieldLabel: $('censorQuality.canCheckQty'), name: 'model.canCheckQty'},
 	    	{QfieldLabel: '已质检合格量', fieldLabel: $('censorQuality.checkQualifiedQty'), name: 'model.checkQualifiedQty'},
 	    	{QfieldLabel: '已质检不合格量', fieldLabel: $('censorQuality.checkUnqualifiedQty'), name: 'model.checkUnqualifiedQty'},
 	    	{QfieldLabel: '已质检让步接收量', fieldLabel: $('censorQuality.checkReceiveQty'), name: 'model.checkReceiveQty'},
 	    	{
 	    		QfieldLabel: '质检结果代码',
				fieldLabel: $('censorQuality.resultCode'),
				name: 'model.resultCode',
				hiddenName: 'model.resultCode',
				xtype: 'uxcombo',
				displayField: 'text',
				valueField: 'value',
				displayValue: 'text',
				bind: {
					store: '{resultStore}'
				},
				triggerAction: 'all'
 			},
 	    	{QfieldLabel: '采购订单号', fieldLabel: $('censorQuality.purchaseOrderNo'), name: 'model.purchaseOrderNo'},
 	    	{QfieldLabel: '行号', fieldLabel: $('censorQuality.rowIds'), name: 'model.rowIds'},
 	    	{QfieldLabel: '收货单号', fieldLabel: $('censorQuality.shoppingNoticeNo'), name: 'model.receivingNoteNo'},
 	    	{QfieldLabel: '凭证年度', fieldLabel: $('censorQuality.voucherYear'), name: 'model.voucherYear'},
 	    	{QfieldLabel: '凭证编号', fieldLabel: $('censorQuality.voucherNo'), name: 'model.voucherNo'},
 	    	{QfieldLabel: '凭证行项目号', fieldLabel: $('censorQuality.voucherProNo'), name: 'model.voucherProNo'},
 	    	{QfieldLabel: '采购组织编码', fieldLabel: $('censorQuality.purchasingOrgCode'), name: 'model.purchasingOrgCode'},
 	    	
 	    	{QfieldLabel: '送检时间', fieldLabel: $('censorQuality.inspectionTime'), name: 'model.inspectionTime', anchor: '95%', xtype: 'datefield', format: 'Y-m-d H:i:s'},
 	    	{QfieldLabel: '质检时间', fieldLabel: $('censorQuality.qualityTime'), name: 'model.qualityTime', anchor: '95%', xtype: 'datefield', format: 'Y-m-d H:i:s'},
 	    	{xtype: 'textarea', QfieldLabel: '备注', fieldLabel: $('censorQuality.remark'), columnWidth: 1, name: 'model.remark'},
 	    	{ QfieldLabel: '附件', fieldLabel: $('censorQuality.uploadFileGroupId'), name: 'model.uploadFileGroupId'},
 	    	    	
 	    	{xtype: 'hidden', QfieldLabel: '采购组织名称', fieldLabel: $('censorQuality.purchasingOrgName'), name: 'model.purchasingOrgName'},
 	    	{xtype: 'hidden', QfieldLabel: '质检状态', fieldLabel: $('censorQuality.status'), name: 'model.status'},
 	    	{xtype: 'hidden', QfieldLabel: '合格量', fieldLabel: $('censorQuality.qualifiedQty'), name: 'model.qualifiedQty'},
 	    	{xtype: 'hidden', QfieldLabel: '不合格量', fieldLabel: $('censorQuality.unqualifiedQty'), name: 'model.unqualifiedQty'},
 	    	{xtype: 'hidden', QfieldLabel: '工厂编码', fieldLabel: $('censorQuality.plantCode'), name: 'model.plantCode'},
 	    	{xtype: 'hidden', QfieldLabel: '工厂名称', fieldLabel: $('censorQuality.plantName'), name: 'model.plantName'},
 	    	{xtype: 'hidden', QfieldLabel: '库存地点编码', fieldLabel: $('censorQuality.stockCode'), name: 'model.stockCode'},
 	    	{xtype: 'hidden', QfieldLabel: '库存地点编码', fieldLabel: $('censorQuality.stockName'), name: 'model.stockName'},
 	    	{xtype: 'hidden', QfieldLabel: '让步接收量', fieldLabel: $('censorQuality.receiveQty'), name: 'model.receiveQty'},
 	    	{xtype: 'hidden', QfieldLabel: '质检结果名称', fieldLabel: $('censorQuality.resultName'), name: 'model.resultName'},
 	    	{xtype: 'hidden', QfieldLabel: '同步状态', fieldLabel: $('censorQuality.erpSyn'), name: 'model.erpSyn'},
 	    	{xtype: 'hidden', QfieldLabel: '同步信息', fieldLabel: $('censorQuality.erpReturnMsg'), name: 'model.erpReturnMsg'},
 	    	{xtype: 'hidden', QfieldLabel: '送检人员id', fieldLabel: $('censorQuality.inspectorId'), name: 'model.inspectorId'},
 	    	{xtype: 'hidden', QfieldLabel: '送检人员名称', fieldLabel: $('censorQuality.inspectorName'), name: 'model.inspectorName'},
 	    	{xtype: 'hidden', QfieldLabel: '质检人员id', fieldLabel: $('censorQuality.qualitorId'), name: 'model.qualitorId'},
 	    	{xtype: 'hidden', QfieldLabel: '质检人员名称', fieldLabel: $('censorQuality.qualitorName'), name: 'model.qualitorName'},
 	    	{xtype: 'hidden', QfieldLabel: 'clientCode', fieldLabel: $('censorQuality.clientCode'), name: 'model.clientCode'}
 		]
	}
	
});