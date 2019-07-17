/**
 * @class {Cp.apply.PurchasingRequisitionCollectionViewModel}
 * @extend {Ext.ux.app.ViewModel}
 * 采购申请明细归集配置
 */
Ext.define('Cp.apply.PurchasingRequisitionCollectionViewModel', {
    extend: 'Ext.ux.app.ViewModel',
    alias: 'viewmodel.purchasingRequisitionCollectionViewModel',
    requires: [
        'Ext.ux.form.DateTimeField',
        'Md.material.MaterialSelectWin',
        'Sl.masterdata.VendorSelectWin',
        'Cp.apply.PurchaseingRequisitionImportWin'
    ],

    /**
     * @cfg {Object} stores
     * 相关store归集
     *
     * - **plantStore** - 工厂
     *
     */
    config:{
	    stores: {
	        /**
	         * 工厂
	         */
	        plantStore: Ext.create('Ext.data.JsonStore', {
	            proxy: {
	                url: path_masterdata + '/md/plant/getall',
	                type: 'ajax'
	            },
	            fields: ['plantCode', 'plantName'],
	            autoLoad: true
	        }),
	        /**单位*/
	        unitStore: Ext.create("Ext.data.JsonStore", {
				proxy: {
					//url: path_srment + "/bp/businessparams/Unit_getAll.action",
				    url:path_masterdata + "/md/unit/getall",
					type: 'ajax'
				},
				fields: ['unitCode', 'unitName', 'taxRateValue'],
				autoLoad: true
			})
	    },
	
	
	    data: {
	    	isExtend:true,
	        /**
	         * @cfg {Array} hideVpBtn
	         * 对固化的按钮进行隐藏操作
	         */
	    	vp_hideListBtn : ['delete', 'add', 'edit'],
	
	        /**
	         * @cfg {String} playListMode
	         * normal/audit/undeal //三种列表模式
	         */
	        playListMode: 'normal',
	      
	        /**
	         * @cfg {Array} gridColumn
	         * 列表对象 列属性配置项
	         */
	       vp_gridColumn: [{
	            Qheader: '采购申请明细归集id',
	            header: $('purchasingRequisitionCollection.purchasingRequisitionColId'),
	            dataIndex: 'purchasingRequisitionColId',
	            disabled: true,
	            tipable:true
	        }, {
	            Qheader: '采购申请单号',
	            header: $('purchasingRequisition.purchasingRequisitionNo'),
	            dataIndex: 'purchasingRequisitionNo',
	            width: 150,
	            renderer: 'gridPurchasingRequisitionNoRenderer',
	            tipable:true
	        }, {
	            Qheader: '行号',
	            header: $('purchasingRequisitionDtl.rowNo'),
	            dataIndex: 'rowNo',
	            tipable:true
	        }, {
	            Qheader: '工厂名称',
	            header: $('purchasingRequisitionDtl.plantName'),
	            dataIndex: 'plantName',
	            disabled: true,
	            tipable:true
	        }, {
	            Qheader: '工厂编码',
	            header: $('purchasingRequisitionDtl.plantCode'),
	            dataIndex: 'plantCode',
	            tipable:true
	        }, {
	            Qheader: '物料编码',
	            header: $('purchasingRequisitionDtl.materialCode'),
	            dataIndex: 'materialCode',
	            tipable:true
	        }, {
	            Qheader: '物料名称',
	            header: $('purchasingRequisitionDtl.materialName'),
	            dataIndex: 'materialName',
	            width: 200,
	            tipable:true
	        }, {
	            Qheader: '基本单位编码',
	            header: $('purchasingRequisitionDtl.unitCode'),
	            dataIndex: 'unitCode',
	            width: 120,
	            tipable:true
	        }, {
	            Qheader: '基本单位名称',
	            header: $('purchasingRequisitionDtl.unitName'),
	            dataIndex: 'unitName',
	            disabled: true,
	            tipable:true
	        }, {
	            Qheader: '需求日期',
	            header: $('purchasingRequisitionDtl.demandDate'),
	            dataIndex: 'demandDate',
	            xtype: 'datecolumn',
	            format: 'Y-m-d',
	            tipable:true,
	            exportRenderer:true
	        }, {
	            Qheader: '需求量',
	            header: $('purchasingRequisitionDtl.quantityDemanded'),
	            dataIndex: 'quantityDemanded',
	            align : 'right',
	            renderer: 'rendererNumber',
	            tipable:true
	        }, {
	            Qheader: '转移数量',
	            header: $('purchasingRequisitionCollection.transferQuantity'),
	            dataIndex: 'transferQuantity',
	            disabled: true
	        }, {
	            Qheader: '已转移数量',
	            header: $('purchasingRequisitionCollection.transferedQuantity'),
	            dataIndex: 'transferedQuantity',
	            disabled: true
	        }, {
	            Qheader: '采购组编码',
	            header: $('purchasingRequisitionDtl.purchasingGroupCode'),
	            dataIndex: 'purchasingGroupCode',
	            tipable:true
	        }, {
	            Qheader: '采购组名称',
	            header: $('purchasingRequisitionDtl.purchasingGroupName'),
	            dataIndex: 'purchasingGroupName',
	            disabled: true
	        }, {
	            Qheader: '公司编码',
	            header: $('label.companyCode'),
	            dataIndex: 'companyCode',
	            tipable:true
	        }, {
	            Qheader: '公司名称',
	            header: $('label.companyName'),
	            dataIndex: 'companyName',
	            disabled: true
	        }, {
	            Qheader: '申请人编码',
	            header: $('purchasingRequisition.applicantCode'),
	            dataIndex: 'applicantCode',
	            disabled: true
	        }, {
	            Qheader: '申请人姓名',
	            header: $('purchasingRequisition.applicantName'),
	            dataIndex: 'applicantName'
	        },  {
	            Qheader: '申请时间',
	            header: $('purchasingRequisition.applicantTime'),
	            dataIndex: 'createTime',
	            xtype: 'datecolumn',
	            format: 'Y-m-d H:i:s',
	            width: 150,
	            tipable:true,
	            exportRenderer:true
	        }, {
	            Qheader: '备注',
	            header: $('purchasingRequisitionDtl.remark'),
	            dataIndex: 'remark',
	            tipable:true
	        }, {
	            Qheader: '来源(1:srm;2:sap)',
	            header: $('purchasingRequisitionCollection.source'),
	            dataIndex: 'source',
	            renderer: 'gridSourceRenderer',
	            exportRenderer: true
	        }],
	
	        /**
	         * @cfg {Array} vpSubTab
	         * 列表底部tab集合
	         */
	        vp_SubTab: ['logTab'],
	
	        /**
	         * @cfg {String} dealUrl
	         * 方法处理url
	         */
	        dealUrl: path_srm + '/cp/purchasingrequisitioncollection',
	
	        /**
	         * @cfg {String} moduleName
	         * 模块名称
	         */
	        moduleName: $('purchasingRequisitionCollection'),
	
	        /**
	         * @cfg {String} triggerField
	         * 触发域（字段名）
	         */
	       vp_triggerField: 'purchasingRequisitionNo',
	
	        /**
	         * @cfg {Object} gridStore
	         * 列表Store
	         */
	       vp_gridStore: {
	           idProperty: 'purchasingRequisitionColId',
	           url:  '#{dealUrl}/list',
	           sort: 'purchasingRequisitionColId',
	           dir: 'desc',
	           remoteSort: true
	          /* listeners: {
	               load: function () {
	                       var controller = me.getVp().getController();
	                       controller.gridStoreLoad();
	                   },
	                   beforeload: function () {
	                       var controller = me.getVp().getController();
	                       controller.gridStoreBeforeLoad();
	                   }
	           }*/
	       },
	
	        /**
	         * @cfg {String} controllerClassName
	         * 控制类类名称
	         */
	        controllerClassName: 'Cp.apply.PurchasingRequisitionCollectionController',
	        /**
	         * @cfg {Array} editFormItems
	         * 编辑form表单
	         */
	        ew_editFormItems: [{
	            xtype: 'hidden',
	            fieldLabel: $('purchasingRequisitionDtl.purchasingRequisitionCollectionId'),
	            name: 'model.purchasingRequisitionColId'
	        }, {
	            QfieldLabel: '采购申请单号',
	            fieldLabel: $('purchasingRequisition.purchasingRequisitionNo'),
	            name: 'model.purchasingRequisitionNo'
	        }, {
	            QfieldLabel: '行号',
	            fieldLabel: $('purchasingRequisitionDtl.rowNo'),
	            name: 'model.rowNo'
	        }, {
	            QfieldLabel: '工厂名称',
	            fieldLabel: $('purchasingRequisitionDtl.plantName'),
	            name: 'model.plantName'
	        }, {
	            QfieldLabel: '工厂编码',
	            fieldLabel: $('purchasingRequisitionDtl.plantCode'),
	            name: 'model.plantCode'
	        }, {
	            QfieldLabel: '物料编码',
	            fieldLabel: $('purchasingRequisitionDtl.materialCode'),
	            name: 'model.materialCode'
	        }, {
	            QfieldLabel: '物料名称',
	            fieldLabel: $('purchasingRequisitionDtl.materialName'),
	            name: 'model.materialName'
	        }, {
	            QfieldLabel: '基本单位编码',
	            fieldLabel: $('purchasingRequisitionDtl.unitCode'),
	            name: 'model.unitCode'
	        }, {
	            QfieldLabel: '基本单位名称',
	            fieldLabel: $('purchasingRequisitionDtl.unitName'),
	            name: 'model.unitName',
	            renderer:"unitNameRender"
	        }, {
	            QfieldLabel: '采购组编码',
	            fieldLabel: $('purchasingRequisitionDtl.purchasingGroupCode'),
	            name: 'model.purchasingGroupCode'
	        }, {
	            QfieldLabel: '采购组名称',
	            fieldLabel: $('purchasingRequisitionDtl.purchasingGroupName'),
	            name: 'model.purchasingGroupName'
	        }, {
	            QfieldLabel: '需求日期',
	            fieldLabel: $('purchasingRequisitionDtl.demandDate'),
	            name: 'model.demandDate',
	            anchor: '90%',
	            xtype: 'datefield',
	            format: 'Y-m-d H:ii:s'
	        }, {
	            QfieldLabel: '需求量',
	            fieldLabel: $('purchasingRequisitionDtl.quantityDemanded'),
	            name: 'model.quantityDemanded'
	        }, {
	            QfieldLabel: '转移数量',
	            fieldLabel: $('purchasingRequisitionCollection.transferQuantity'),
	            name: 'model.canTransferQuantity'
	        }, {
	            QfieldLabel: '已转移数量',
	            fieldLabel: $('purchasingRequisitionCollection.transferedQuantity'),
	            name: 'model.transferedQuantity'
	        }, {
	            QfieldLabel: '公司编码',
	            fieldLabel: $('label.companyCode'),
	            name: 'model.companyCode'
	        }, {
	            QfieldLabel: '申请人',
	            fieldLabel: $('purchasingRequisition.applicantName'),
	            name: 'model.applicantName'
	        }, {
	            QfieldLabel: '备注',
	            fieldLabel: $('purchasingRequisition.remark'),
	            name: 'model.remark'
	        }, {
	            QfieldLabel: '来源(1:srm;2:sap)',
	            fieldLabel: $('purchasingRequisitionCollection.source'),
	            name: 'model.source'
	        }],
	
	        /**
	         * @cfg {Array} searchFormItems
	         * 查询字段集合
	         */
	        sw_searchFormItems: [{
	            xtype: 'hidden',
	            fieldLabel: $('purchasingRequisition.purchasingRequisitionCollectionId'),
	            name: 'filter_LIKE_purchasingRequisitionCollectionId'
	        }, {
	            QfieldLabel: '采购申请单号',
	            fieldLabel: $('purchasingRequisition.purchasingRequisitionNo'),
	            name: 'filter_LIKE_purchasingRequisitionNo'
	        }, {
	            QfieldLabel: '工厂编码',
	            fieldLabel: $('purchasingRequisitionDtl.plantCode'),
	            name: 'filter_LIKE_plantCode'
	        }, {
	            QfieldLabel: '物料编码',
	            fieldLabel: $('purchasingRequisitionDtl.materialCode'),
	            name: 'filter_LIKE_materialCode'
	        }, {
	            QfieldLabel: '物料名称',
	            fieldLabel: $('purchasingRequisitionDtl.materialName'),
	            name: 'filter_LIKE_materialName'
	        }, {
	            QfieldLabel: '采购组编码',
	            fieldLabel: $('purchasingRequisitionDtl.purchasingGroupCode'),
	            name: 'filter_LIKE_purchasingGroupCode'
	        }, {
	            QfieldLabel: '采购组名称',
	            fieldLabel: $('purchasingRequisitionDtl.purchasingGroupName'),
	            name: 'filter_LIKE_purchasingGroupName'
	        }, {
	            QfieldLabel: '需求日期',
	            fieldLabel: $('purchasingRequisitionDtl.demandDate'),
	            name: 'filter_GE_demandDate',
	            xtype: 'datefield',
	            format: 'Y-m-d'
	        }, {
	            QfieldLabel: '需求日期',
	            fieldLabel: $('label.to'),
	            name: 'filter_LE_demandDate',
	            xtype: 'datefield',
	            format: 'Y-m-d'
	        },	
	        {
	            QfieldLabel: '申请时间',
	            fieldLabel: $('purchasingRequisition.applicantTime'),
	            name: 'filter_GE_createTime',
	            xtype: 'datefield',
	            format: 'Y-m-d'
	        }, {
	            QfieldLabel: '申请时间',
	            fieldLabel: $('label.to'),
	            name: 'filter_LE_createTime',
	            xtype: 'datefield',
	            format: 'Y-m-d'
	        },	
	        {Qheader:'申请人',fieldLabel:$('purchasingRequisition.applicantName'),name:'filter_LIKE_applicantName'} 
	
	        ],
	        vp_gridCfg: {
	        	stateful : true,
				stateId : s_userCode + '_purchasingRequisitionCol',
				stateHeader : true,
				forceFit : false,
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
	            sm:true,//选中框隐藏
	            rn:true//序列列隐藏	
			},
	
	        /**
	         * @cfg {Array} addVpBtn
	         * 在固化的按钮基础上追加按钮
	         * - **close** - 关闭
	         *  - **cancel** - 取消
	         */
	        vp_addListBtn: [{
	            name: 'import',
	            Qtext: '导入',
	            text: $('button.erpImport'),
	            build: power['importfromsap'],
	            index: 7,
	            iconCls: 'icon-erpleading',
	            handler: 'vpImportHandler'
	        }],
	
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
	         * @cfg {boolean} editWinMaximized
	         * 是否最大化窗口，默认为否
	         */
	        maximized : true,
	
	
	        /**
	         * @cfg {String} moduleCode
	         * 模块编码
	         */
	        vp_billTypeCode: 'PRC',
	
	        /**
	         * @cfg {Integer} activeTab
	         * 默认展示的tab页
	         */
	        vp_activeTab : 0,
	
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
	        sw_Height: 300,
	
	        /**
	         * @cfg {Integer} searchWinHeight
	         * 查询窗宽度
	         */
	        sw_Width: 800,
	
	        /**
	         * @cfg {String} searchFormColumnWidth
	         * 查询表单每行列数
	         */
	        sw_columnWidth: '0.5',
	
	        /**
	         * @cfg {String} searchWinIsShowSynStatus
	         * 是否显示同步状态进行查询
	         */
	        sw_isShowStatus: true,
	
	        /**
	         * @cfg {Boolean} hideSubTab
	         * 是否隐藏底部tab
	         */
	        vp_hideSubTab: true
	
	    }
    }
});