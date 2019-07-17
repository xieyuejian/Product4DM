/**
 * @class {Cp.apply.PurchasingRequisitionCollectionTransController}
 * @extend {Ext.ux.app.ViewControlle}
 * 采购申请明细归集控制类
 */
Ext.define('Cp.apply.PurchasingRequisitionCollectionTransController', {
    extend: 'Ext.srm.app.ViewController',
    alias: 'controller.purchasingRequisitionCollectionTransController',

    /**
     * @method searchWinSearch
     * 查询窗体查询事件
     */
    searchWinSearch: function () {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        vp.searchFlag = true;
    },


    /**
     * @method gridStoreBeforeLoad
     * 列表加载前事件
     */
    gridStoreBeforeLoad: function () {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        var grid = vp.grid;
        var store = grid.store;

        store.proxy.extraParams.initStates = viewModel.get("initStates");
        if (vp.searchFlag && undefined != vp.searchWin) {
            var params = vp.searchWin.formPanel.form.getValues();
            params.initStates = vp.grid.store.proxy.extraParams.initStates;
            grid.store.proxy.extraParams = params;
            vp.searchFlag = false;
        }
    },
    /**
     * @method gridStoreLoad
     * 列表加载事件
     */
    gridStoreLoad: function () {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        if (undefined != vp.searchWin) {
            vp.searchWin.formPanel.form.reset();
        }
    },
    /**
     * @method gridStoreLoad
     * 关闭编辑
     */
    rowdblclickFn:function(){
          return false;
    },

    /**
	 * @method resetTransQuantity
	 * 根据是否编辑状态显示数量
	 */
    renderTransferQuantity: function (v, m, r) {
    	m.tdStyle = 'background-color:#ffedd9;border:1px solid #fff;';
        if (Ext.isEmpty(v)) {
            r.set("transferQuantity", 0);
            return 0;
        }
        var tGridDtl = Ext.ComponentQuery.query("*[name=transConfigGrid]")[0];
        if (!tGridDtl.isNotEdit) {
            r.set("transferQuantity", v);
            return v;
        }
        var vp = tGridDtl.outerVp;
        var mr = vp.grid.getSelectionModel().getSelection();
        var prcId = mr[0].get("purchasingRequisitionColId");
        var cacheColObj = vp.cacheTransRecords[prcId];
        if (cacheColObj == undefined || cacheColObj == null) {
            return v;
        }
        var transOrders = cacheColObj["transOrders"];
        if (transOrders == undefined || transOrders == null) {
            return v;
        }
        for (var i = 0; i < transOrders.length; i++) {
            var transObj = transOrders[i];
            if (transObj["purchasingOrgCode"] == r.data.purchasingOrgCode && transObj["vendorCode"] == r.data.vendorCode && transObj["purchaseType"] == r.data.purchaseType && transObj["price"] == r.data.price) {
                var transQ = transObj["transferQuantity"];
                transQ = (transQ == undefined || transQ == null) ? 0 : parseInt(transQ);
                r.set("transferQuantity", transQ);
                return transQ;
            }
        }
        return v;
    },

    /**
     * @cacheTransferOrder
     * 将分配的转单放入缓存
     */
    cacheTransferOrder: function (editor, e) {
        var tGridDtl = Ext.ComponentQuery.query("*[name=transConfigGrid]")[0];
        var vp = tGridDtl.outerVp;
        var r = vp.grid.getSelectionModel().getSelection();
        var transQ = e.record.data.transferQuantity;
        if (transQ < 0) {
            Q.tips("转单数量不能为负数");
            e.record.set("transferQuantity", 0);
            return;
        }
        var prcId = r[0].get("purchasingRequisitionColId");
        var cacheColObj = vp.cacheTransRecords[prcId];
        var transOrders = cacheColObj["transOrders"];
        //判断是否超限
        var totalTransQ = 0;
        var thisStore = tGridDtl.getStore();
        thisStore.each(function (record) {
            if (record.data["purchasingOrgCode"] == e.record.data.purchasingOrgCode && record.data["vendorCode"] == e.record.data.vendorCode && record.data["purchaseType"] == e.record.data.purchaseType && record.data["price"] == e.record.data.price) {} else {
                totalTransQ += parseInt(record.data["transferQuantity"]);
            }
        });
        totalTransQ += parseInt(e.record.data.transferQuantity);
        var canTransQ = r[0].get("canTransferQuantity");
        canTransQ = (canTransQ == '' || canTransQ == null) ? 0 : parseInt(canTransQ);
        //		alert(totalTransQ + "," + canTransQ);
        if (totalTransQ > canTransQ) {
            Q.tips("总转单数量不能大于可转单数量");
            e.record.set("transferQuantity", 0);
            //			return;
        }
        //放入缓存
        for (var i = 0; i < transOrders.length; i++) {
            var transObj = transOrders[i];
            if (transObj["purchasingOrgCode"] == e.record.data.purchasingOrgCode && transObj["vendorCode"] == e.record.data.vendorCode && transObj["purchaseType"] == e.record.data.purchaseType && transObj["price"] == e.record.data.price) {
                if (e.record.data.transferQuantity == '' || e.record.data.transferQuantity == null) {
                    transObj["transferQuantity"] = '0';
                } else {
                    transObj["transferQuantity"] = parseInt(e.record.data.transferQuantity);
                }
                break;
            }
        }
    },

    /**
	 * @method resetTransQuantity
	 * 重置分配数量
	 */
    resetTransQuantity: function (_self) {
        var grid = _self.findParentByType(Ext.grid.GridPanel);
        var store = grid.getStore();
        store.each(function (record) {
            record.set("transferQuantity", 0);
        });
        //缓存也置0
        var vp = grid.outerVp;
        var r = vp.grid.getSelectionModel().getSelection();
        var cacheTransRecords = vp.cacheTransRecords;
        if (cacheTransRecords != undefined) {
            var reqColId = r[0].get('purchasingRequisitionColId');
            var transOrders = cacheTransRecords[reqColId]["transOrders"];
            if (transOrders != undefined && transOrders != null) {
                Ext.each(transOrders, function (item, index) {
                    item["transferQuantity"] = 0;
                });
            }
        }
    },

    /**
     * @method saveTrans
     * 保存转单
     */
    saveTrans: function (_self, transFlag) {
        var vc = this;
        var vm = this.getViewModel();
        var cacheTransRecords = vm.getVp().cacheTransRecords;
        //去掉transferQuantity为0的
        var cacheTransList = [];
        for (var name in cacheTransRecords) {
            var transOrders = cacheTransRecords[name]["transOrders"];
            var transOrdersTmp = [];
            for (var i = 0; i < transOrders.length; i++) {
                var transOrder = transOrders[i];
                if (transOrder["transferQuantity"] == 0) {} else {
                    transOrdersTmp.push(transOrder);
                }
            }
            cacheTransRecords[name]["transOrders"] = transOrdersTmp;
            cacheTransList.push(cacheTransRecords[name]);
        }
        //转换成json格式
        var jsonStr = "";
        for (var i = 0; i < cacheTransList.length; i++) {
            var trans = cacheTransList[i];
            jsonStr += "{\"purchasingRequisitionColId\":" + trans["purchasingRequisitionColId"] + ",";
            if (!Ext.isEmpty(trans["companyCode"])) {
                jsonStr += "\"companyCode\":\"" + trans["companyCode"] + "\",";
            }
            if (!Ext.isEmpty(trans["companyCode"])) {
                jsonStr += "\"companyName\":\"" + trans["companyName"] + "\",";
            }
            jsonStr += "\"purchasingRequisitionNo\":\"" + trans["purchasingRequisitionNo"] + "\"," +
                "\"canTransferQuantity\":" + trans["canTransferQuantity"] + ",\"transOrders\":[";
            var secJsonStr = "";
            var transOrders = trans["transOrders"];
            for (var j = 0; j < transOrders.length; j++) {
                var transOrder = transOrders[j];
                secJsonStr += "{\"purchasingOrgCode\":\"" + transOrder["purchasingOrgCode"] + "\"," +
                    "\"purchasingOrgName\":\"" + transOrder["purchasingOrgName"] + "\"," +
                    "\"vendorCode\":\"" + transOrder["vendorCode"] + "\"," +
                    "\"vendorErpCode\":\"" + transOrder["vendorErpCode"] + "\"," +
                    "\"vendorName\":\"" + transOrder["vendorName"] + "\"," +
                    "\"purchaseType\":\"" + transOrder["purchaseType"] + "\"," +
                    "\"taxrateCode\":\"" + transOrder["taxrateCode"] + "\"," +
                    "\"taxrateValue\":\"" + transOrder["taxrateValue"] + "\"," +
                    "\"transferQuantity\":" + transOrder["transferQuantity"] + "," +
                    "\"price\":" + transOrder["price"] + "},"
            }
            secJsonStr = secJsonStr.substring(0, secJsonStr.length - 1) + "]},";
            jsonStr += secJsonStr;
        }
       jsonStr = "[" + jsonStr.substring(0, jsonStr.length - 1) + "]";
       //jsonStr="[{\"purchasingRequisitionColId\":236,\"companyCode\":\"1000\",\"companyName\":\"1000\",\"purchasingRequisitionNo\":\"PR201903120002\",\"canTransferQuantity\":1000,\"transOrders\":[]}]";
          Ext.Ajax.request({
            url: path_srm + "/cp/purchasingRequisitionTrans/save",
            dataType:'json',
            params:{paramsJson:jsonStr},
            success: function (result) {
                var json = Ext.decode(result.responseText);
                if (json.success) {
                    vm.getVp().cacheTransRecords = {};
                    if (transFlag == 1) {
                        vc.openTransferedWin();
                    } else {
                        Q.tips("保存成功");
                        vm.vpList.grid.store.reload();
                    }
                } else if (json.data == 2) {
                    Q.tips("总转单数量不能大于可转单数量");
                } else {
                    Q.tips("保存失败");
                }
            }
        });
    },

    /**
     * @purReqTransfer
     * 转单
     */
    openTransferedWin: function () {
        var vm = this.getViewModel();
        var win = Ext.create("Cp.apply.PurchasingRequisitionTransSelectWin", {
            singleSelect: true,
            moduleId: vm.getVp().id,
            maximized: true,
            header: false
        }, vm.getVp());
        win.show();
    },
    
    /**
     * @purReqTransfer
     * 转单
     */
    purReqTransfer: function (_self) {
        this.saveTrans(_self, 1);
    }
});