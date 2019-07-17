/**
 * @class {Cp.forecast.ForecastViewModel}
 * @extend {Ext.ux.app.ViewModel}
 * 采购预测配置
 */
Ext.define('Cp.forecast.ForecastViewModel', {
    extend: 'Ext.ux.app.ViewModel',
    alias: 'viewmodel.forecastViewModel',
    requires: ['Ext.ux.form.DateTimeField', 'Md.material.MaterialSelectWin', 'Sl.masterdata.VendorSelectWin', 'Cp.forecast.ForecastSapSelectWin', "Ext.srm.form.PurchasingOrganizationComboGrid", "Ext.srm.ux.ChatTab", "Ext.srm.form.UnitComboGrid", "Ext.srm.form.MaterialComboGrid", "Ext.srm.form.PlantComboGrid", "Ext.srm.form.VendorComboGrid", "Ext.srm.ux.UxFileUtils"],
    config: {
        /**
         * @cfg {Object} stores
         * 相关store归集
         *
         * - **plantStore** - 工厂
         * - **purchasingOrgStore** - 采购组织
         * - **purchasingGroupStore** - 采购组
         *
         */
        stores: {
            /**
             * 工厂
             */
            plantStore: Ext.create('Ext.data.JsonStore', {
                proxy: {
                    url: path_masterdata + "/md/material/findplantbymaterial",
                    type: 'ajax'
                },
                fields: ['plantCode', 'plantName'],
                autoLoad: true
            }),

            /**
             * 采购组
             */
            purchasingGroupStore: Ext.create('Ext.data.JsonStore', {
                proxy: {
                    url: path_masterdata + '/md/purchasinggroup/getallfilter',
                    type: 'ajax'
                },
                fields: ['purchasingGroupCode', 'purchasingGroupName'],
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
            		statusCode:"TOCONFIRM",
            		statusName:$("state.confirm") // 待审核
            	},{
            		statusCode:"TONOPASS",
            		statusName:$("status.tonopass") //驳回
            	},{
            		statusCode:"TOPASS",
            		statusName:$("state.release") //发布
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
            	}] 
            },
            /**
             * @cfg {Boolean} isExtend
             * 是否用父类的getCfg 配置方法
             */
            isExtend: true,
            /**
             * @cfg {String} dealUrl
             * 统一方法处理url
             */
            dealUrl: path_srm + "/cp/forecast",
            /**
             * @cfg {String} moduleName
             * 模块名称
             */
            moduleName: $('forecast'),
            /**
             * @cfg {String} triggerField
             * 触发域（字段名）
             */
            vp_triggerField: 'forecastNo',
            authorityNameArr:[ "forecast_topass", "forecast_tonopass"],
            /**
             * @cfg {String} controllerClassName
             * 控制类类名称
             */
            controllerClassName: 'Cp.forecast.ForecastController',
            /**
             * @cfg {Array} hideVpBtn
             * 对固化的按钮进行隐藏操作
             */
            //hideVpBtn: [],
            /**
             * @cfg {String} playListMode
             * normal/audit/undeal //三种列表模式
             */
            playListMode: "normal",
            /**
             * @cfg {String}  vp_logModuleCode
             *  底部logTab 操作日志 请求参数值
             */
            vp_logModuleCode: "CGY",
            /**
             * @cfg {String}  vp_billTypeCode
             *  底部msgTab 审核日志 请求参数值默认单据编码
             */
            vp_billTypeCode: "CGY",
            chatTab: {
                xtype: 'chatTab',
                /**
                 *  回复必传参数
                 *    "model.processKey" : billTypeCode,//单据编码 【固化】
                 *    "model.businessKey" : prepaymentsId,//选中的单据id【固化】
                 *    "moduleName":moduleName //模块名称,
                 *    "userId": record.get("createUserId") 记录创建者id 对应字段编码为createUserId【固化】
                 *  当有特殊情况请根据实际情况配置对应的参数映射字段
                 *     "userId": "xxxx" 记录创建者id
                 */
                paramsMapping: {
                    "userId": "createUserId"
                },
                /**
                 *  回复参数动态处理
                 * @param {Object} vm 当前 viewModel
                 * @param {Object} params 回传参数集
                 * @param {record} record 选中回复的记录
                 * @param {String} handler 操作动作 add 新建| reply 回复
                 */
                paramsMappingFn: function(vm, params, record, handler) {
                    return {};
                },
                /**
                 *  回复弹出窗口配置
                 * @param {Object} vm 当前 viewModel
                 * @param {Object} config 窗口配置参数集
                 * @param {record} record 选中回复的记录
                 * @param {String} handler 操作动作 add 新建| reply 回复
                 */
                configMappingFn: function(vm, config, record, handler) {
                    return {};
                }
            },
            /**
             * @cfg {Array} vpSubTab
             * 列表底部tab集合
             */
            vp_subTab: ['gridDtl', 'logTab', 'msgTab', "chatTab"],
            /**
             * @cfg {Object} accessControl
             * 可以实现不同角色是表单和列表的字段，是否显示/是否可编辑/是否必填等的初始化配置；
             */
            accessControl: {
                hiddenTab: { //名称为 tabClassName
                    'logTab': (s_roleTypes == 'V') ? true : false,
                    'msgTab': (s_roleTypes == 'V') ? true : false,
                    'forecastDtls': false
                }
            },
            /**
             * @cfg {Integer} editWinFormHeight
             * 编辑表单高度
             */
            ew_height: 130,
            /**
             * @cfg {String} editWinFormColumnWidth
             * 编辑表单列个数
             */
            ew_columnWidth: '0.5',
            /**
             * @cfg {boolean} editWinMaximized
             * 是否最大化窗口，默认为否
             */
            maximized: true,
            /**
             * @cfg {String} moduleCode
             * 模块编码
             */
            moduleCode: 'CGY',
            /**
             * @cfg {Integer} activeTab
             * 默认展示的tab页
             */
            vp_activeTab: 0,
            /**
             * @cfg {boolean} isAudit
             * 是否需要右键审核
             */
            isAudit: true,
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
             * @cfg {Array} gridColumn
             * 列表对象 列属性配置项
             */
            vp_gridColumn: [{
                    Qheader: '采购预测ID',
                    header: $('forecast.forecastId'),
                    dataIndex: 'forecastId',
                    disabled: true
                }, {
                    Qheader: '采购预测单号',
                    header: $('forecast.forecastNo'),
                    dataIndex: 'forecastNo',
                    renderer: 'rendererNo',
                    tipable: true
                }, {
                    Qheader: '采购组织编码',
                    header: $('forecast.purchasingOrgCode'),
                    dataIndex: 'purchasingOrgCode',
                    tipable: true
                }, {
                    Qheader: '采购组织名称',
                    header: $('forecast.purchasingOrgName'),
                    dataIndex: 'purchasingOrgName',
                    tipable: true
                }, {
                    Qheader: '采购预测时间',
                    header: $('forecast.forecastMainDate'),
                    dataIndex: 'forecastMainDate',
		            xtype: 'datecolumn',
			        format: 'Y-m-d H:i:s',
		            width: 150,
                    exportRenderer: true
                }, {
                    Qheader: '状态',
                    header: $('forecast.forecastMainState'),
                    dataIndex: 'status',
                    renderer: 'rendererStatus',
                    exportRenderer: true
                }, {
                    Qheader: '创建方式',
                    header: $('bidSure.createType'),
                    dataIndex: 'createType',
                    renderer: 'createTypeRenderer',
                    exportRenderer: true
                },
                {
                    Qheader: '创建者ID',
                    header: $('forecast.creatorId'),
                    dataIndex: 'createUserId',
                    disabled: true
                }, {
                    Qheader: '创建者名称',
                    header: $('forecast.creatorName'),
                    dataIndex: 'createUserName',
                    tipable: true
                }, {
                    Qheader: '创建时间',
                    header: $('label.createTime'),
                    dataIndex: 'createTime',
		            xtype: 'datecolumn',
			        format: 'Y-m-d H:i:s',
		            width: 150,
                    exportRenderer: true
                }, {
                    Qheader: '修改者ID',
                    header: $('label.modifyUserId'),
                    dataIndex: 'modifyUserId',
                    disabled: true
                }, {
                    Qheader: '修改者名称',
                    header: $('label.modifyUserName'),
                    dataIndex: 'modifyUserName',
                    disabled: true
                }, {
                    Qheader: '修改时间',
                    header: $('label.modifyTime'),
                    dataIndex: 'modifyTime',
                    disabled: true,
                    exportRenderer: true
                }
            ],
            /**
             * @cfg
             * 列表grid 配置项
             */
            vp_gridCfg: {
                stateHeader: true,
                stateful: false,
                forceFit: true,
                ableExporter: false,
                billNoField: "forecastNo",
                /**
                 * 是否启用导出功能 true|false
                 */
                ableExporter:true,
                rn: true //序列列隐藏
            },
            /**
             * @cfg {Object} gridStore
             * 列表Store配置项
             */
            vp_gridStore: {
                idProperty: "forecastId",
                url: "#{dealUrl}/list",
                sort: "forecastNo",
                dir: "desc",
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
                sm: true, //选中框隐藏
                rn: true //序列列隐藏
            },
            /**
             * @cfg {Array} addVpBtn
             * 在列表固化的按钮基础上追加按钮
             *
             * - **revoke** - 撤销审批
             * - **syn** - 同步
             */
            vp_addListBtn: [{
                name: 'erpImport',
                Qtext: 'ERP导入',
                text: 'ERP导入',
                build: power['erpimport'],
                index: 7,
                iconCls: 'icon-erpleading',
                handler: 'vpErpImportHandler'
            }, {
                name: 'revokeaudit',
                text: "撤销审核",
                iconCls: 'icon-cancel',
                index: 8,
                build: power['revokeaudit'],
                handler: "revokeAuditHandler"
            }],
            /**
             * @cfg {Array}  vp_listEditStateFn
             *  列表按钮控制配置
             */
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
                'toPass': function(r) {
                    return r.get('status') == 'TOCONFIRM'; //待确认
                }
            }, {
                'toNoPass': function(r) {
                    return r.get('status') == 'TOCONFIRM'; //待确
                }
            }, {
                'toConfirm': function(r) {
                    return (r.get('status') == 'NEW' || r.get('status') == 'TONOPASS');
                }
            }, {
                "prompttrial": function(r) {
                    return r.get('status') == "TOCONFIRM";
                }
            }, {
                "revokeaudit": function(r) {
                    return r.get('status') == "TOCONFIRM" && s_userid == r.get("createUserId");
                }
            }, {
                "grant": function(r) { // 授权,审核状态下才允许使用
                    return r.get('status') == "TOCONFIRM";
                }
            }, {
                "sign": function(r) { // 加签,审核状态下才允许使用
                    return r.get('status') == "TOCONFIRM";
                }
            }],
            /**
             * @cfg {Array} addVpBtn
             * 在列表固化的按钮基础上追加按钮
             *
             * - **revoke** - 撤销审批
             * - **syn** - 同步
             */
            /* vp_addListBtn: [{
			name: 'erpImport',
			Qtext: 'ERP导入',
			text: 'ERP导入',
			build: power['erpimport'],
			index: 7,
			iconCls: 'icon-erpleading',
			handler: 'vpErpImportHandler'
		},{
            name: 'revokeAudit',
            text: "撤销审核",
            iconCls: 'icon-cancel',
            index: 8,
     		build:power['revokeaudit'],
            handler: "revokeAuditHandler"
        }],*/
            /**
             * @cfg {Object}  ew_centerTab
             * 编辑窗口 明细配置项 border布局中的 region:center
             */
            ew_centerTab: {
                items: ["gridDtl"]
            },
            /**
             * @cfg {Array} editFormItems
             * 编辑form表单
             */
            ew_editFormItems: [{
                    xtype: 'hidden',
                    fieldLabel: $('forecast.forecastId'),
                    name: 'model.forecastId'
                }, {
                    QfieldLabel: '采购预测单号',
                    fieldLabel: $('forecast.forecastNo'),
                    name: 'model.forecastNo',
                    value: '自动生成',
                    readOnly: true
                }, {
                    QfieldLabel: '采购预测时间',
                    fieldLabel: $('forecast.forecastMainDate') + "<font color='red'> *</font>",
                    name: 'model.forecastMainDate',
                    anchor: '95%',
                    xtype: 'datefield',
                    format: 'Y-m-d H:i:s',
                    value: new Date(),
                    readOnly: true
                },
                {
                    QfieldLabel: "采购组织编码",
                    fieldLabel: $("purchasingOrg.code") + "<font color='red'>*</font>",
                    name: "model.purchasingOrgCode",
                    xtype: 'purchasingOrganizationComboGrid',
                    hiddenName: "model.purchasingOrgCode",
                    enabletrigger: true,
                    selectWinCfg: {
                        autoLoad: true
                    },
                    clearable: true,
                    editable: true,
                    allowBlank: false
                }, {
                    QfieldLabel: '采购组织名称',
                    fieldLabel: $('forecast.purchasingOrgName'),
                    name: 'model.purchasingOrgName',
                    readOnly: true
                }, {
                    xtype: 'hidden',
                    QfieldLabel: '状态',
                    fieldLabel: $('forecast.forecastMainState'),
                    name: 'model.forecastMainState'
                }, {
                    xtype: 'hidden',
                    QfieldLabel: '创建者ID',
                    fieldLabel: $('forecast.creatorId'),
                    name: 'model.createUserId'
                }, {
                    xtype: 'hidden',
                    QfieldLabel: '创建者名称',
                    fieldLabel: $('forecast.creatorName'),
                    name: 'model.createUserName'
                }, {
                    QfieldLabel: '创建时间',
                    xtype: 'hidden',
                    fieldLabel: $('label.createTime'),
                    name: 'model.createTime'
                }, {
                    QfieldLabel: '修改者ID',
                    xtype: 'hidden',
                    fieldLabel: $('label.modifyUserId'),
                    name: 'model.modifyUserId'
                }, {
                    QfieldLabel: '修改者名称',
                    xtype: 'hidden',
                    fieldLabel: $('label.modifyUserName'),
                    name: 'model.modifyUserName'
                }, {
                    QfieldLabel: '修改时间',
                    xtype: 'hidden',
                    fieldLabel: $('label.modifyTime'),
                    name: 'model.modifyTime'
                }, {
                    QfieldLabel: '创建方式',
                    xtype: 'hidden',
                    fieldLabel: $('bidSure.createType'),
                    value:"srm",
                    name: 'model.createType'
                }
            ],
            /**
             * @cfg {Object} gridDtl
             * 采购预测明细列表
             */
            gridDtl: {
                name: 'detailGrid',
                tabTitle: $('forecastDtl'),
                xtype: 'uxeditorgrid',
                foreignKey: 'forecast_forecastId',
                tabClassName: 'forecastDtls',
                validField: ['materialCode', 'materialName', 'forecastMainDate', 'forecastNum', 'vendorErpCode'], //需要提交的细单字段,
                formFieldReadyArr: ['model.purchasingOrgCode'],
                allowEmpty: false, //明细条数是否可以为空
                stateHeader: true,
                stateful: true,
                stateId: s_userCode + '_forecastDtl',
                cm: {
                    defaultSortable: false,
                    defaults: {
                        menuDisabled: true
                    },
                    columns: [{
                        Qheader: '采购预测细单ID',
                        header: $('forecastDtl.forecastDtlId'),
                        dataIndex: 'forecastDtlId',
                        disabled: true
                    }, {
                        Qheader: '采购预测ID',
                        header: $('forecastDtl.forecastId'),
                        dataIndex: 'forecast_forecastId',
                        disabled: true
                    }, {
                        Qheader: '物料编码',
                        header: $('forecastDtl.materialCode') + "<font color='red'> *</font>",
                        dataIndex: 'materialCode',
                        editor: {
                            xtype: "materialComboGrid",
                            enabletrigger: true,
                            minChars: 1,
                            fieldMapping: {
                                "materialCode": "materialCode",
                                "materialName": "materialName",
                                "unitName": "baseUnitCode"
                            },
                            listeners: {
                                "triggerselect": 'gridDtlMaterialCodeSelect',
                                "aftersetvalue": "gridDtlMaterialCodeSetValueAfter"
                            }
                        },
                        customAttr: {
                            allowBlank: false,
                            editable: true //是否显示可以编辑背景颜色
                        }
                    }, {
                        Qheader: '物料名称',
                        header: $('forecastDtl.materialName'),
                        dataIndex: 'materialName',
                        width: 150
                    }, {
                        Qheader: '采购预测需求时间',
                        header: $('forecastDtl.forecastMainDate'),
                        dataIndex: 'forecastMainDate',
                        width: 80,
                        customAttr: {
                            allowBlank: false,
                            editable: true //是否显示可以编辑背景颜色
                        },
                        editor: {
                            xtype: 'datefield',
                            allowBlank: false,
                            name: 'forecastMainDate',
                            minValue: new Date(),
                            format: 'Y-m-d'
                        },
						renderer: 'rendererDate'
                    }, {
                        Qheader: '预测数量',
                        header: $('forecastDtl.forecastMainState'),
                        dataIndex: 'forecastNum',
                        width: 80,
                        customAttr: {
                            allowBlank: false,
                            editable: true //是否显示可以编辑背景颜色
                        },
                        editor: {
                            xtype: 'numberfield',
                            decimalPrecision: 0,
                            maxLength: 50,
                            minValue: 0,
                            name: 'forecastNum'
                        },
						renderer: 'rendererNumber'
                    }, {
                        Qheader: '单位',
                        header: $('forecastDtl.unitName'),
                        dataIndex: 'unitName',
                        width: 80,
                        editor: {
                            xtype: "unitComboGrid",
                            enabletrigger: true,
                            minChars: 1,
                            fieldMapping: {
//                                "unitCode": "unitCode",
                                "unitName": "unitName"
                            }
                        },
                        customAttr: {
                            allowBlank: false,
                            editable: true //是否显示可以编辑背景颜色
                        }
                    }, {
                        Qheader: '工厂编码',
                        header: $('forecastDtl.plantCode'),
                        dataIndex: 'plantCode',
                        width: 80,
                        customAttr: {
                            editable: true //是否显示可以编辑背景颜色
                        },
                        editor: {
                            xtype: 'plantComboGrid',
                            maxLength: 50,
                            name: 'plantCode',
                            hiddenName: 'plantCode',
                            minChars:1,
                            fieldMapping: {
                                "plantCode": "plantCode",
                                "plantName": "plantName"
                            }
                        }
                    }, {
                        Qheader: '供应商Erp编码',
                        header: $('forecastDtl.vendorCode'),
                        dataIndex: 'vendorErpCode',
                        width: 80,
                        editor: {
                            xtype: "vendorComboGrid",
                            enabletrigger: true,
                            fieldMapping: {
                                "vendorErpCode": "vendorErpCode",
                                "vendorName": "vendorName"
                            },
	                        listeners: {
	                            triggerbeforeshow: "vendorCodeTriggerBeforeShow",
	                            triggerbaseparams: "vendorCodeTriggerBaseParams"
	                        }
                        },
                        customAttr: {
                            allowBlank: false,
                            editable: true //是否显示可以编辑背景颜色
                        }
                    }, {
                        Qheader: '供应商Erp编码',
                        header: $('forecastDtl.vendorCode'),
                        dataIndex: 'vendorCode',
                        disabled: true
                    }, {
                        Qheader: '供应商名称',
                        header: $('forecastDtl.vendorName'),
                        dataIndex: 'vendorName',
                        width: 120
                    }, {
                        Qheader: '单位',
                        header: $('forecastDtl.plantCode'),
                        dataIndex: 'plantName',
                        disabled: true
                    }]
                },
                pageSize: 0,
                store: {
                    idProperty: 'forecastDtlId',
                    url: path_srment + '/cp/forecast/findforecastdtlall',
                    sort: 'forecastDtlId',
                    autoLoad: false,
                    dir: 'desc'
                },
                listeners: {
                    'beforeedit': 'gridDtlBeforeedit'
                },
                tbar: [{
                    Qtext: '添加',
                    text: $('button.new'),
                    iconCls: 'icon-add',
                    handler: 'gridDtlAddHandler'
                }, {
                    Qtext: '删除',
                    text: $('button.delete'),
                    iconCls: 'icon-delete',
                    handler: 'gridDtlDeleteHandler'
                }, {
                    name: 'export',
                    Qtext: '模板下载',
                    text: $('button.download'),
                    iconCls: 'icon-download',
                    handler: 'gridDtlDownloadHandler'
                }, {
                    name: 'import',
                    text: $('button.import'),
                    //build: power['IntoExcel'],
                    iconCls: 'icon-putin',
                    handler: 'gridDtlImportHandler'
                }, {
                    name: 'erpSearch',
                    Qtext: 'erp查询',
                    text: 'erp查询',
                    iconCls: 'icon-add',
                    handler: 'gridDtlErpSearchHandler'
                }]
            },
            /**
             * @cfg {Array} searchFormItems
             * 查询字段集合
             */
            sw_searchFormItems: [{
                QfieldLabel: '采购预测时间',
                fieldLabel: $('forecast.forecastMainDate'),
                name: 'filter_GE_forecastMainDate',
                anchor: '90%',
                xtype: 'datefield',
                format: 'Y-m-d'
            }, {
                QfieldLabel: '到',
                fieldLabel: $('label.to'),
                name: 'filter_LE_forecastMainDate',
                anchor: '90%',
                xtype: 'datefield',
                format: 'Y-m-d'
            }, {
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
            }, {
                QfieldLabel: '采购组织编码',
                fieldLabel: $('forecast.purchasingOrgCode'),
                name: 'filter_EQ_purchasingOrgCode'
            }, {
                QfieldLabel: '采购组织名称',
                fieldLabel: $('forecast.purchasingOrgName'),
                name: 'filter_LIKE_purchasingOrgName'
            }, {
                QfieldLabel: '供应商编码',
                fieldLabel: $('forecastDtl.vendorCode'),
                name: 'filter_EQ_forecastDtls_vendorCode'
            }, {
                QfieldLabel: '供应商名称',
                fieldLabel: $('forecastDtl.vendorName'),
                name: 'filter_LIKE_forecastDtls_vendorName'
            }, {
                QfieldLabel: '物料编码',
                fieldLabel: $('forecastDtl.materialCode'),
                name: 'filter_EQ_forecastDtls_materialCode'
            }, {
                QfieldLabel: '物料名称',
                fieldLabel: $('forecastDtl.materialName'),
                name: 'filter_LIKE_forecastDtls_materialName'
            }, {
                QfieldLabel: '创建人名称',
                fieldLabel: $('label.createUserName'),
                name: 'filter_LIKE_createUserName'
            }]
        }
    }
});