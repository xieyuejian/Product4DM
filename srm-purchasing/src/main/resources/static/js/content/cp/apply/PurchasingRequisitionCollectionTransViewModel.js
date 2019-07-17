Ext.override(Ext.comm.CommModelVpList, {
    createTabPanel: function (cfg) {
            var win = this;
            var subPanel = win.createFormPanel(cfg);
            //所有文本框都设成只读
            var fld = subPanel.getForm().getFields(); //findByType(Ext.form.Field); 
            // subPanel.setFormType("view");
            Ext.each(fld.items, function (item) {
                item.allowBlank = true;
                item.suspendEvent("change");
                if (item.getXType() != "displayfield") {
                    item.setReadOnly(true);
                }
            });
            //获取按钮
            var btus = subPanel.query("button");
            Ext.each(btus, function (btn) {
                btn.setDisabled(true);
            });

            //---------------------------subsubGird end--------------------------------------------------
            //日志和审核意见
            var logGrid = Ext.create("Ext.comm.ViewLogItemOrmessage", {
                grid: win.grid,
                logModuleCode: cfg.vp.logModuleCode || cfg.vp.billTypeCode, //日志类型与写入日志时的编码一至
                billTypeCode: cfg.vp.billTypeCode
            });
            //所有的子标签
            var subTabTemp = [];
            if (typeof cfg.vp.subTab != "undefined") {
                var arrTab = [];
                //提取编辑窗口的tab配置参数 columns 和 store 实例化列表的tab
                var hiddenTab = !Ext.isEmpty(win.configVar.accessControl) ? win.configVar.accessControl.hiddenTab : {};
                Q.each(cfg.vp.subTab, function (o, i) {
                    if (o instanceof Array) {
                        var arrchild = [];
                        if (o.length > 0) {
                            Q.each(o, function (oo, ii) {
                                if (oo.xtype == "formpanel" || oo.xtype == "uxform" || oo.xtype == "form") {
                                    var formPanel = win.dealDetailForm(oo);
                                    if(Ext.isEmpty(win.comps[formPanel.tabClassName])){  
		    						    win.comps[formPanel.tabClassName] = [];
		    						    win.comps[formPanel.tabClassName].push(formPanel); 
		    						}else{ 
		    						    win.comps[formPanel.tabClassName].push(formPanel); 
		    						} 

                                    if (!hiddenTab[formPanel.tabClassName]) {
                                        arrchild.push(formPanel);
                                    }
                                } else {
                                    var childcolumns = [];
                                    Q.each(oo.cm.columns, function (ooo, iii) {
                                        var column = {};
                                        Ext.apply(column, ooo);
                                        childcolumns.push(column);
                                    })
                                    if (ii == 0) {
                                        var tabTitle = oo.tabTitle;
                                        grid = win.dealDetailGrid(oo, childcolumns, true);
                                        grid.tabTitle = tabTitle;
                                    } else {
                                        grid = win.dealDetailGrid(oo, childcolumns, false);
                                    }
                                    if (!hiddenTab[grid.tabClassName]) {
                                        arrchild.push(grid);
                                    }
                                }
                            })
                            var tabOrDetailTabs = win.createTabsDtlFn(arrchild); //处理明细和明细的明细布局后返回
                            arrTab.push(tabOrDetailTabs);
                        }
                    } else {
                        if (typeof o != 'string') {
                            if (o.xtype == "formpanel" || o.xtype == "uxform") {
                                var formPanel = win.dealDetailForm(o);
                                if(Ext.isEmpty(win.comps[formPanel.tabClassName])){  
		    					    win.comps[formPanel.tabClassName] = [];
		    					    win.comps[formPanel.tabClassName].push(formPanel); 
		    				    }else{  
		    					    win.comps[formPanel.tabClassName].push(formPanel); 
		    				    } 
                                if (!hiddenTab[formPanel.tabClassName]) {
                                    arrTab.push(formPanel);
                                }
                            } else {
	                            if (typeof o.xtype != "undefined" && (o.xtype == "uxeditorgrid" || o.xtype == "editorgrid" || o.xtype == "uxgrid" || o.xtype == "grid")) {
	                                var newcolumns = [];
	                                Q.each(o.cm.columns, function (oo, ii) {
	                                    var column = {};
	                                    Ext.apply(column, oo);
	                                    newcolumns.push(column);
	                                });
	                                var gridCfg = win.dealDetailGrid(o, newcolumns, true);
	                                if(o.name == 'transConfigGrid'){
	                                	grid = Ext.create("Ext.ux.grid.EditorGridPanel", gridCfg);
	                                }else{
	                                	grid = Ext.create("Ext.ux.grid.GridPanel", gridCfg);
	                                }
	                                if(Ext.isEmpty(win.comps[grid.tabClassName])){  
			    					    win.comps[grid.tabClassName] = [];
			    					    win.comps[grid.tabClassName].push(grid); 
			    				    }else{  
			    					    win.comps[grid.tabClassName].push(grid); 
			    				    }
	
	                                if (!hiddenTab[grid.tabClassName]) {
	                                    arrTab.push(grid);
	                                }
	                            } else {
	                                arrTab.push(o);
	                            }
	                        }
                        }
                    }
                });
                //如果配置项没有传 默认tab(表单、日志、审核意见)时默认都显示，如果有传奇中一个，就按传入的显示
                if (cfg.vp.subTab.indexOf("subTab") > -1 && cfg.vp.subTab.indexOf("logTab") == -1 && cfg.vp.subTab.indexOf("msgTab") == -1 && cfg.vp.subTab.indexOf("processPic") == -1) {
                    subTabTemp = [subPanel].concat(arrTab);
                } else if (cfg.vp.subTab.indexOf("subTab") > -1) {
                    subTabTemp = [subPanel].concat(arrTab);
                } else {
                    subTabTemp = arrTab;
                }
            }
            //如果配置项没有传 默认tab(表单、日志、审核意见)时默认都显示，如果有传奇中一个，就按传入的显示
            if (cfg.vp.subTab.length == 0) {
                subTabTemp.push(logGrid.logItem);
                subTabTemp.push(logGrid.msgItem);
                // subTabTemp.push(logGrid.processPic);
            } else {
                if (!hiddenTab['logTab']) {
                    if (cfg.vp.subTab.indexOf("logTab") > -1) {
                        subTabTemp.push(logGrid.logItem)
                    }
                }
                if (!hiddenTab['msgTab']) {
                    if (cfg.vp.subTab.indexOf("msgTab") > -1) {
                        subTabTemp.push(logGrid.msgItem)
                    }
                }
                if (!hiddenTab['processTab']) {
                    if (cfg.vp.subTab.indexOf("processTab") > -1) {
                        //subTabTemp.push(logGrid.processPic)
                    }
                }
            }
            logGrid.logItem.tabClassName = "logTab";
            logGrid.msgItem.tabClassName = "msgTab";
            //logGrid.processPic.tabClassName ="processTab";
            subPanel.tabClassName = "subTab";

            win.comps["logTab"] = logGrid.logItem;
            win.comps["msgTab"] = logGrid.msgItem;
            //win.comps["processTab"] = logGrid.processPic;
            win.comps["subTab"] = subPanel;

            if (!Ext.isEmpty(subTabTemp) && subTabTemp.length > 0) {
                //创建分栏tabpanel
                var tabPanel = getSplitTab({
                    tabHeight: cfg.vp.tabHeight || 200,
                    gridPanel: win.grid,
                    container: "sp_viewport",
                    column: cfg.vp.triggerField,
                    activeTab: cfg.vp.activeTab || 0
                }, [subTabTemp]);
                //win.getAllChild(tabPanel);
                return tabPanel;
            } else {
                return [];
            }
        },
        //---------------------------detailGird start--------------------------------------------------
        dealDetailGrid: function (o, newcolumns, onActivateFlag) {
            var vp = this;
            vp.cacheTransRecords = {};
            if(o.name == 'transConfigGrid'){
            	o.gridType = 'uxeditorgrid';
            }else{
            	o.gridType = 'uxgrid';
            }
            var grid = {
                border: false,
                title: o.tabTitle,
                region: o.region || "center",
                height: o.height || 300,
                xtype: o.gridType,
                sm: o.sm || {
                        singleSelect: true
                    },
                    rendererColor: false,
                foreignKey: o.foreignKey,
                tabClassName: o.tabClassName,
                isGroupGrid: o.isGroupGrid,
                cm: {
                    rn: o.cm.rn || true,
                    defaultSortable: o.cm.defaultSortable || true,
                    defaults: o.cm.defaults || {
                            menuDisabled: true
                        },
                        columns: newcolumns
                },
                viewConfig: o.viewConfig || {
                        forceFit: true
                    },
                    pageSize: o.pageSize || 0,
                store: o.store,
                listeners: o.listeners,
                outerVp: vp,
                isNotEdit: o.isNotEdit,
                name: o.name,
                onActivate: function (key, panel) {
                    //if(!onActivateFlag)return; //细单的细单不用添加 点击事件。
                    var thisGrid = this;
                    if (Ext.isEmpty(key)) {
                        return;
                    } //没改变选中时，直接返回
                    var r = vp.grid.getSelectionModel().getSelection();
                    var store = this.getStore();
                    var idProperty = store.idProperty;
                    //					store.setBaseParam("filter_EQ_"+o.foreignKey,r.id);

                    //判断是否编辑过列表
                    if (Ext.isEmpty(vp.cacheTransRecords) || Ext.isEmpty(r[0].get('purchasingRequisitionColId')) || Ext.isEmpty(vp.cacheTransRecords[r[0].get('purchasingRequisitionColId')])) {
                        thisGrid.isNotEdit = false;
                        //其他模块的细单用到
                        if (!Ext.isEmpty(o.foreignKey)) {
                            store.setBaseParam("filter_EQ_" + o.foreignKey,
                                r[0].get(o.foreignKey.substring(o.foreignKey.indexOf("_") + 1)));
                        }
                    } else {
                        thisGrid.isNotEdit = true;
                    }
                    //缓存采购申请转单数据
                    var cacheColObj = {};
                    cacheColObj["purchasingRequisitionColId"] = r[0].get('purchasingRequisitionColId');
                    if (!Ext.isEmpty(r[0].get("companyCode"))) {
                        cacheColObj["companyCode"] = r[0].get("companyCode");
                    }
                    if (!Ext.isEmpty(r[0].get("companyName"))) {
                        cacheColObj["companyName"] = r[0].get("companyName");
                    }
                    cacheColObj["purchasingRequisitionNo"] = r[0].get("purchasingRequisitionNo");
                    cacheColObj["canTransferQuantity"] =
                        Ext.isEmpty(r[0].get("canTransferQuantity")) ? 0 : r[0].get("canTransferQuantity");
                    var cacheTransArr = [];

                    var newParams = {};
                    if(r[0].get("materialCode")!=null){
                    newParams["materialCode1"] = r[0].get("materialCode");
                    }
                    if(r[0].get("purchasingRequisitionColId")!=null){
                    newParams["purchasingRequisitionColId1"] = r[0].get('purchasingRequisitionColId');
                    }
                    delete store.proxy.extraParams["materialCode1"];
                    Ext.apply(store.proxy.extraParams, newParams);

                    var uploadFlag = false;
                    Q.each(o.cm.columns, function (oo, ii) {
                        if (oo.dataIndex == "uploadFile4View") {
                            uploadFlag = true
                        }
                    })
                    store.load({
                        params: {
                            start: 0,
                            limit: grid.pageSize == 0 ? 10000 : grid.pageSize
                        },
                        callback: function (arr_r) {
                            if (uploadFlag) { //有附件上传时调用
                                Q.each(arr_r, function (rr, i) {
                                    //console.info("细单附件");
                                    vp.renderUploadFile(rr.get("uploadFileGroupId"), rr, "uploadFile4View", "", grid); //附件渲染
                                });
                            }
                            //缓存查出来的数据
                            if (!Ext.isEmpty(vp.cacheTransRecords) && !Ext.isEmpty(r[0].get('purchasingRequisitionColId')) && !Ext.isEmpty(vp.cacheTransRecords[r[0].get('purchasingRequisitionColId')])) {
                                return;
                            }
                            Q.each(arr_r, function (rr, i) {
                                var transObj = {};
                                transObj["purchasingOrgCode"] = rr.get("purchasingOrgCode");
                                transObj["purchasingOrgName"] = rr.get("purchasingOrgName");
                                transObj["vendorCode"] = rr.get("vendorCode");
                                transObj["vendorErpCode"] = rr.get("vendorErpCode");
                                transObj["vendorName"] = rr.get("vendorName");
                                transObj["purchaseType"] = rr.get("purchaseType");
                                transObj["taxrateCode"] = rr.get("taxrateCode");
                                transObj["taxrateValue"] = rr.get("taxrateValue");
                                transObj["transferQuantity"] =
                                    Ext.isEmpty(rr.get("transferQuantity")) ? 0 : parseInt(rr.get("transferQuantity"));
                                transObj["price"] = rr.get("price");
                                cacheTransArr.push(transObj);
                            });
                            cacheColObj["transOrders"] = cacheTransArr;
                            if (!Ext.isEmpty(vp.cacheTransRecords) && !Ext.isEmpty(r[0].get('purchasingRequisitionColId'))) {
                                vp.cacheTransRecords[r[0].get('purchasingRequisitionColId')] = cacheColObj;
                            }
                        }
                    });
                }
            };
            if (!Ext.isEmpty(o) && o.isGroupGrid === true) {
                grid = Ext.apply(grid, {
                    view: o.view
                })
            }
            if (o.vpShowTbar) {
                grid.tbar = o.tbar;
            }
            return grid;
        }
});
//---------------------------override end--------------------------------------------------


/**
 * @class {Cp.apply.PurchasingRequisitionCollectionTransViewModel}
 * @extend {Ext.ux.app.ViewModel}
 * 采购申请明细归集配置
 */
Ext.define('Cp.apply.PurchasingRequisitionCollectionTransViewModel', {
    extend: 'Ext.ux.app.ViewModel',
    alias: 'viewmodel.purchasingRequisitionCollectionTransViewModel',
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
		 * 公共Boolean类型
		 */
		configStateStore: Ext.create('Ext.data.JsonStore', {
			data: [{
				value: 1,
				display: $('label.assigned')
			}, {
				value: 0,
				display: $('label.notAssigned')
			}],
			fields: ['value', 'display']
		})
    },

    data: {
    	isExtend:true,
        /**
         * @cfg {Array} hideVpBtn
         * 对固化的按钮进行隐藏操作
         */
    	vp_hideListBtn: ['delete', 'add', 'edit', 'view'],
        /**
         * @cfg {String} playListMode
         * normal/audit/undeal //三种列表模式
         */
        playListMode: "normal",

        /**
         * @cfg {Array} gridColumn
         * 列表对象 列属性配置项
         */
       vp_gridColumn: [{
            Qheader: '采购申请明细归集id',
            header: $('purchasingRequisitionCollection.purchasingRequisitionColId'),
            dataIndex: 'purchasingRequisitionColId',
            disabled: true
        }, {
            Qheader: '采购申请单号',
            header: $('purchasingRequisition.purchasingRequisitionNo'),
            dataIndex: 'purchasingRequisitionNo',
            width: 150,
            renderer: 'rendererNo'
        }, {
            Qheader: '行号',
            header: $('purchasingRequisitionDtl.rowNo'),
            dataIndex: 'rowNo'
        }, {
            Qheader: '工厂名称',
            header: $('purchasingRequisitionDtl.plantName'),
            dataIndex: 'plantName',
            disabled: true
        }, {
            Qheader: '工厂编码',
            header: $('purchasingRequisitionDtl.plantCode'),
            dataIndex: 'plantCode'
        }, {
            Qheader: '公司编码',
            header: $('label.companyCode'),
            dataIndex: 'companyCode'
        }, {
            Qheader: '公司名称',
            header: $('label.companyName'),
            dataIndex: 'companyName',
            disabled: true
        }, {
            Qheader: '物料编码',
            header: $('purchasingRequisitionDtl.materialCode'),
            dataIndex: 'materialCode'
        }, {
            Qheader: '物料名称',
            header: $('purchasingRequisitionDtl.materialName'),
            dataIndex: 'materialName',
            width: 200
        }, {
            Qheader: '基本单位编码',
            header: $('purchasingRequisitionDtl.unitCode'),
            dataIndex: 'unitCode',
            width: 120
        }, {
            Qheader: '基本单位名称',
            header: $('purchasingRequisitionDtl.unitName'),
            dataIndex: 'unitName',
            disabled: true
        }, {
            Qheader: '需求日期',
            header: $('purchasingRequisitionDtl.demandDate'),
            dataIndex: 'demandDate',
            xtype: 'datecolumn',
	        format: 'Y-m-d',
            exportRenderer:true
        }, {
            Qheader: '可转单量',
            header: $("purchasingApply.canTransferNum"), //$('purchasingRequisitionDtl.canTransferQuantity'),
            dataIndex: 'canTransferQuantity',
            align : 'right',
	        renderer: 'rendererNumber'
        }, {
            Qheader: '需求量',
            header: $('purchasingRequisitionDtl.quantityDemanded'),
            dataIndex: 'quantityDemanded',
            disabled: true
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
            dataIndex: 'purchasingGroupCode'
        }, {
            Qheader: '采购组名称',
            header: $('purchasingRequisitionDtl.purchasingGroupName'),
            dataIndex: 'purchasingGroupName',
            disabled: true
        }, {
            Qheader: '申请人编码',
            header: $('purchasingRequisition.applicantCode'),
            dataIndex: 'applicantCode',
            disabled: true
        }, {
            Qheader: '申请人姓名',
            header: $('purchasingRequisition.applicantName'),
            dataIndex: 'applicantName'//,
            //disabled: true
        },  {
            Qheader: '申请时间',
            header: $('purchasingRequisition.applicantTime'),
            dataIndex: 'createTime',
            xtype: 'datecolumn',
	        format: 'Y-m-d H:i:s',
            width: 150,
            exportRenderer:true
        }, {
            Qheader: '备注',
            header: $('purchasingRequisitionDtl.remark'),
            dataIndex: 'remark',
            tipable:true,
            disabled: true
        }, {
            Qheader: '来源(1:srm;2:sap)',
            header: $('purchasingRequisitionCollection.source'),
            dataIndex: 'source',
            disabled: true,
            renderer: function (v) {
                if (v == '1') {
                    return 'srm';
                } else {
                    return 'sap';
                }
            }
        }, {
            Qheader: '分配状态',
            header: $("purchasingApply.configState"),
            dataIndex: 'configState',
            renderer: function (v) {
                if (v == '0' || v == null || v == '') {
                    return "<font color='green'>"+$("label.notAssigned")+"</font>";
                } else {
                    return "<font color='red'>"+$("label.assigned")+"</font>";
                }
            },
            exportRenderer: true
        }],
        vp_gridStore:{
            idProperty: 'purchasingRequisitionColId',
            url:  '#{dealUrl}/list',
            sort: 'purchasingRequisitionColId',
            dir: 'desc'
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
         * @cfg {Array} vpSubTab
         * 列表底部tab集合
         */
        vp_subTab: ['gridDtl'],

        /**
         * @cfg {String} dealUrl
         * 方法处理url
         */
        dealUrl: path_srm + '/cp/purchasingRequisitionTrans',

        /**
         * @cfg {String} moduleName
         * 模块名称
         */
        moduleName: $('purchasingRequisitionTrans'),

        /**
         * @cfg {String} triggerField
         * 触发域（字段名）
         */
        vp_triggerField: 'purchasingRequisitionNo',

    

        /**
         * @cfg {String} controllerClassName
         * 控制类类名称
         */
        controllerClassName: 'Cp.apply.PurchasingRequisitionCollectionTransController',

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
            name: 'filter_EQ_purchasingRequisitionNo'
        }, {
            QfieldLabel: '分配状态',
            fieldLabel: $('purchasingApply.configState'),
            name: 'filter_EQ_configState',
            hiddenName: 'filter_EQ_configState',
			xtype: 'uxcombo',
			editable: false,
			bind:{
				store:'{configStateStore}'
			},
			valueField: 'value',
			displayField: 'display'
        }, {
            QfieldLabel: '物料编码',
            fieldLabel: $('purchasingRequisitionDtl.materialCode'),
            name: 'filter_EQ_materialCode'
        }, {
            QfieldLabel: '物料名称',
            fieldLabel: $('purchasingRequisitionDtl.materialName'),
            name: 'filter_LIKE_materialName'
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

        /**
         * @cfg {Array} addVpBtn
         * 在固化的按钮基础上追加按钮
         * - **close** - 关闭
         *  - **cancel** - 取消
         */
        vp_addListBtn: [{
            name: 'transfer',
            Qtext: '转单',
            text: '转单',
            build: power['transfer'],
            index: 1,
            iconCls: 'icon-order',
            handler: 'purReqTransfer'
        }, {
            name: 'saveTrans',
            Qtext: '保存',
            text: '保存',
            build: power['save'],
            index: 2,
            iconCls: 'icon-save',
            handler: 'saveTrans'
        }],
      vp_gridCfg : {
        	stateful : true,
			stateId : s_userCode + '_purchasingRequisitionTrans',
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
        vp_billTypeCode:'PRC',


        /**
         * @cfg {Integer} activeTab
         * 默认展示的tab页
         */
        vp_activeTab: 0,

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
        sw_Height: 290,

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
        sw_isShowSynStatus: true,

        /**
         * @cfg {Boolean} hideSubTab
         * 是否隐藏底部tab
         */
        vp_hideSubTab: false,

        /**
         * 分配明细
         */
        gridDtl: {
            name: "transConfigGrid",
            xtype: 'uxeditorgrid',
            tabTitle: "分配明细",
            foreignKey: 'purchasingRequisitionTran_purchaseRequisitionTransId',
            tabClassName:" purchasingRequisitionCollection",
            store: {
                url: path_srm + '/cp/purchasingRequisitionTrans/configList',
                autoLoad: false
            },
            cm: {
                defaultSortable: false,
                defaults: {
                    menuDisabled: true
                },
                columns: [{
                    header: $("purchasingOrganization.porgname"),
                    dataIndex: 'purchasingOrgName',
                    width: 150
                }, {
                    header: $("purchasingOrganization.porgname"),
                    dataIndex: 'purchasingOrgCode',
                    disabled: true
                }, {
                    header: $("vendor.code"),
                    dataIndex: 'vendorCode',
                    width: 150,
                    disabled: true
                }, {
                    header: $("vendor.code"),
                    dataIndex: 'vendorErpCode',
                    width: 150
                }, {
                    header: $("vendor.name"),
                    dataIndex: 'vendorName',
                    width: 200
                }, {
                    header: $("biddingJ.purchasingCategoryName"),
                    dataIndex: 'purchaseType',
                    width: 100,
                    renderer: function (v, m, r) {
                        if (v == '0') {
                            return '标准';
                        } else if (v == '2') {
                            return '寄售';
                        } else {
                            return '分包';
                        }
                    }
                }, {
                    header: $("purchasingApply.transferNum"),
                    dataIndex: 'transferQuantity',
                    width: 100,
                    editor: {
                        xtype: "numberfield"
                    },
	            	renderer: 'renderTransferQuantity'
                }, {
                    header: $("receivingnote.price"),
                    dataIndex: 'price',
                    width: 100,
                    align : 'right',
                    renderer:'rendererNum2Fn'
                }, {
                    header: '税率编码',
                    dataIndex: 'taxrateCode',
                    hidden: true
                }, {
                    header: '税率值',
                    dataIndex: 'taxrateValue',
                    hidden: true
                }]
            },
         
            tbar: [{
                text: "重置",
                iconCls:'icon-reset',
                handler: "resetTransQuantity"
            }],
            listeners: {
                beforeedit: function (editor, e) {
                    this.isNotEdit = false;
                },
                edit: "cacheTransferOrder"
            }
        }
    }
}
  
});