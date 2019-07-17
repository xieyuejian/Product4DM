/**
 * 转单后信息 - for Q版本(ext-2.0+) //引用本选择列表无需为权限添加资源
 *
 * @param {object}
 *            cfg {boolean} singleSelect 是否单选，缺省为多选 {object} baseParams 查询条件
 *            {function} select 单击[选择]按钮时触发的回调函数，该函数有两个参数：grid与选中的记录
 *            单选时参数：(grid, record) 多选时参数：(grid, selections)
 */
/**
 * 定价条件类型
 */
var pricingConditionTypeStore = Ext.create('Ext.data.JsonStore', {
    proxy: {
        url:path_masterdata + "/md/datadict/getall",
        type: 'ajax'
    },
    fields: ['itemCode', 'itemName'],
    autoLoad: true

});
Ext.define("Cp.apply.PurchasingRequisitionTransSelectWin", {
    extend: 'Ext.ux.Window',
    alias: 'purchasingRequisitionTransSelectWin',
    constructor: function (cfg, vp) {
            cfg = cfg || {};
            var singleSelect = false !== cfg.singleSelect;
            cfg = Ext.apply({
                forceFit: true,
                singleSelect: singleSelect,
                constrain: true,
                renderTo: cfg.moduleId
            }, cfg);
            var grid = this.createGrid(cfg, singleSelect);

            cfg = Ext.apply({
                layout: "border",
                items: [grid, {
                    name: 'transDetailGrid',
                    region: 'south', // 所在的位置
                    xtype: 'uxgrid',
                    height: 250,
                    split: true, // 允许调整大小
                    margins: '0 0 0 0',
                    hidden: true,
                    pageSize: 0,
                    checked: true,
                    stateful : true,
        			stateId : s_userCode + '_purchasingRequisitionTransDtl',
        			stateHeader:true,
                    cm: {
                        defaultSortable: false,
                        columns: [{
                            Qheader: "物料编码",
                            header: $('purchasingRequisitionDtl.materialCode'),
                            dataIndex: 'purchasingRequisitionCollection.materialCode'
                        }, {
                            Qheader: "物料名称",
                            header: $('purchasingRequisitionDtl.materialName'),
                            dataIndex: 'purchasingRequisitionCollection.materialName'
                        }, {
                            Qheader: "单位",
                            header: $('materialMasterPriceApply.elementaryUnitCode'),
                            dataIndex: 'purchasingRequisitionCollection.unitCode'
                        }, {
                            Qheader: "转单数量",
                            header: $("purchasingApply.transferNum"),
                            dataIndex: 'transferQuantity'
                        }, {
                            Qheader: "价格",
                            header: $("receivingnote.price"),
                            dataIndex: 'price',
                            renderer: function(value, metaData) {
                                if (null == value || undefined == value || '' == value) {
                                    return '';
                                }
                                return Ext.util.Format.number(value, '0.000');
                            }
                        }, {
                            Qheader: "采购类别",
                            header: $("biddingJ.purchasingCategoryName"),
                            dataIndex: 'purchaseType',
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
                            Qheader: "交货日期",
                            header: $("label.deliveryDate"), //$("label.companyCode"),
                            dataIndex: 'purchasingRequisitionCollection.demandDate',
                            renderer: function (v, m, r) {
                                if (!Ext.isEmpty(v)) {
                                    return Ext.util.Format.date(v, 'Y-m-d');
                                }
                                return v;
                            }
                        }, {
                            Qheader: "需求数量",
                            header: $("sampleDetail.requiredQty"), //$("label.companyCode"),
                            dataIndex: 'purchasingRequisitionCollection.quantityDemanded',
                            disabled: true
                        }, {
                            Qheader: "税率编码",
                            dataIndex: 'taxrateCode',
                            disabled: true
                        }, {
                            Qheader: "税率值",
                            dataIndex: 'taxrateValue',
                            disabled: true
                        }]
                    },
                    store: {
                        url: path_srm + "/cp/purchasingRequisitionTrans/getChaList",
                        autoLoad: false
                    }
                }],
                listeners: {
                    "hide": function () {
                        grid.getSelectionModel().clearSelections();
                        vp.grid.store.reload();
                    }
                }
            }, cfg);

            this.callParent([cfg]);
        },
        createGrid: function (cfg, singleSelect) {
            var win = this;
            var tBar = Ext.create("Ext.toolbar.Toolbar", {
                dock: 'top',
                style: {
                    margin: '0 0 0 0',
                    border: '0px',
                    align: 'left'
                },
                items: [{
                    text: "提交",
                    margin: "0",
                    style: "width:60px;height:34px;",
                    iconCls: "icon-submit",
                    handler: function () {
                        var detailGrid = Ext.ComponentQuery.query("*[name=transDetailGrid]")[0];
                        var delSelects = detailGrid.getSelectionModel().getSelection();
                        if (delSelects.length < 1) {
                            Q.tips($("purchasingApply.selectApplyDetail"), "E");
                            return false;
                        }
                        var selected = grid.getSelectionModel().getSelection()[0];
                        //跳转到采购订单界面
                        selected.delSelects=delSelects;
                        window.onSkipByKey("100234", selected);
                       /* var viewModel = Ext.create('Cp.order.PurchaseOrderViewModel', {
                            moduleId: 'tabContent_100234'
                        });
                        var viewModelObj = Ext.create("Ext.comm.CommModelVpList", {
                            viewModel: viewModel,
                            moduleId: 'tabContent_100234'
                        });
                        
                        //触发vpInstanceAfert方法
                        viewModelObj.fireEvent("vpInstanceAfter");
                        var vp = viewModelObj.getVp();
                        var cfg = vp.grid.cfg;
                        //获取编辑窗口
                        var win = vp.grid.getEditWin(cfg);
                        win.editFlag = true;
                        //设置编辑窗口的值
                          win.show();
                       // win.setFormValue(0, 'add', null, "save", null, true);
                        var controller = vp.getController();
                        //重新加载定价条件store
                        var pricingConditionTypeStore = viewModel.getStore('pricingConditionTypeStore');
                        var pricingType;
                        pricingConditionTypeStore.load(function(){
                        	pricingType =pricingConditionTypeStore.getCount()>0? pricingConditionTypeStore.getAt(0).data:null;
                            if(pricingType==null){
                       	 		Q.tips("初始化定价条件失败", "E");
                       	 		return;
                       		}
                            controller.setEditWinValues(win, selected, delSelects, pricingType);
                            var form = win.formPanel.getForm();
	                        //设置订单类型和货币编码为可编辑
	                        form.findField("model.purchaseOrderType").setReadOnly(false);
	                        form.findField("model.currencyCode").setReadOnly(false);
	                        win.show();
	                        //设置订单明细
	                        var delGrid = win.getCompByTabClassName("purchaseOrderDetails");
	                        var tbar = win.formPanel.getTopToolbar();
	                        var tabPanel = Ext.getCmp('tabContent_100234');
	                        Ext.each(tbar.items.items, function (btn) {
	                            btn.on('click', function () {
	                               tabPanel.closeFlag = true;
	                            })
	                        	if ('save' == btn.name || 'submit' == btn.name) {
	                                btn.on('click', function () {
	                                    //判断订单数量是否大于可转单数量
	                                    delGrid.store.each(function (r, i) {
	                                        if (r.get('sourceCode') == '3') {
	                                            detailGrid.store.each(function (record, index) {
	                                                if (record.data.purchaseRequisitionTransId == r.get('purchasingRequisitionColId')) {
	                                                	var canTransferQuantity = record.data.purchasingRequisitionCollection.canTransferQuantity;
	                                                    if (r.get("buyerQty") > canTransferQuantity) {
	                                                        var msg = $('purchasingApply.transferOrder.canTransNumLessThanOrderNum').replace('{0}', i + 1).replace('{1}', canTransferQuantity);
	                                                        Q.tips(msg, "E");
	                                                        return ;
	                                                    }
	                                                }
	                                            });
	                                        }
	                                    });
	                                });
	                            }
	                        });
	                        //订单界面关闭方法
	                        win.hide = function () {
	                            if(tabPanel.closeFlag == true){
	                            	tabPanel.close();
	                            }
	                            grid.store.reload();
	                            detailGrid.getSelectionModel().clearSelections();
	                            detailGrid.hide();
	                        }
                        });*/
                       

                    }
                }, {
                    text: $("label.return"),
                    margin: "0 20 0 0",
                    style: "width:60px;height:34px;",
                    iconCls: "icon-return",
                    handler: function () {
                        win.destroy();
                    }
                }, "->"]
            });
            var grid = this.gridPanel = new Ext.ux.grid.GridPanel({
                name: "transListGrid",
                style: {
                    padding: '0px 0px 0px 0px',
                    border: '1px'
                },
                border: false,
                sm: {
                    singleSelect: singleSelect
                },
                store: {
                    url: path_srm +"/cp/purchasingRequisitionTrans/configGroupList",
                    baseParams: cfg.baseParams || {},
                    autoLoad: true
                },
                stateful : true,
    			stateId : s_userCode + '_purchasingRequisitionTransList',
    			stateHeader:true,
                cm: {
                    defaultSortable: false,
                    columns: [{
                        Qheader: "公司编码",
                        header: $("label.companyCode"),
                        dataIndex: 'companyCode',
                        width:120
                    }, {
                        Qheader: "公司名称",
                        header: $("label.companyName"),
                        dataIndex: 'companyName',
                        width:150
                    }, {
                        Qheader: "采购组织编码",
                        header: $("purchasingOrganization.porgcode"),
                        dataIndex: 'purchasingOrgCode',
                        width:120
                    }, {
                        Qheader: "采购组织名称",
                        header: $("purchasingOrganization.porgname"),
                        dataIndex: "purchasingOrgName",
                        width:150
                    }, {
                        Qheader: "供应商编码",
                        header: $("vendor.code"),
                        dataIndex: "vendorCode",
                        width:120,
                        hidden:true
                    }, {
                        Qheader: "供应商编码",
                        header: $("vendor.code"),
                        dataIndex: "vendorErpCode",
                        width:120
                    }, {
                        Qheader: "供应商名称",
                        header: $("vendor.name"),
                        dataIndex: "vendorName",
                        width:150
                    }]
                },
                dockedItems: [tBar], //[tBar,queryBar],
                listeners: {
                    "itemclick": function (g, i) {
                        var r = grid.getSelectionModel().getSelection();
                        var newParams = {};
                        newParams["vendorCode"] = r[0].get("vendorCode");
                        newParams["purchasingOrgCode"] = r[0].get('purchasingOrgCode');
                        newParams["companyCode"] = r[0].get('companyCode');
                        var transDtlGrid = Ext.ComponentQuery.query("*[name=transDetailGrid]")[0];
                        transDtlGrid.show();
                        var gStore = transDtlGrid.getStore();
                        //默认勾选
                        gStore.on("load", function () {
                            var sm = transDtlGrid.getSelectionModel();
                            gStore.each(function (record, i) {
                                sm.select(i, true);
                            });
                        });
                        //重新加载
                        Ext.apply(gStore.proxy.extraParams, newParams);
                        gStore.load();
                    }
                }

            });
            return grid;
        }
});