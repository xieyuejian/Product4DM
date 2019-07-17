/**
 * @class {Cp.apply.PurchasingRequisitionCollectionController}
 * @extend {Ext.ux.app.ViewControlle}
 * 采购申请明细归集控制类
 */
Ext.define('Cp.apply.PurchasingRequisitionCollectionController', {
    extend: 'Ext.srm.app.ViewController',
    alias: 'controller.purchasingRequisitionCollectionController',
    /**
     * @method searchWinSearch
     * 查询窗体查询事件
     */
    searchWinSearch: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        vp.searchFlag = true;
    },
    /**
     * @method gridStoreBeforeLoad
     * 列表加载前事件
     */
    gridStoreBeforeLoad: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        var grid = vp.grid;
        var store = grid.store;
        store.proxy.extraParams.initStates = viewModel.get("initStatesStr");
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
    gridStoreLoad: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        if (undefined != vp.searchWin) {
            vp.searchWin.formPanel.form.reset();
        }
    },
    /**
     * @method gridPurchasingRequisitionNoRenderer
     * 单号渲染
     * @param {Object} value 当前列值
     * @param {Object} metaData 当前单元格元数据
     * @return {String} 要呈现的HTML字符串
     */
    gridPurchasingRequisitionNoRenderer: function(value, metaData) {
        metaData.attr = "style='cursor:pointer;'"; // 给当前td添加样式
        return "<u style='color:blue'>" + value + "</u>";
    },
    /**
     * @method gridSourceRenderer
     * 来源渲染
     * @param {Object} value 当前列值
     * @param {Object} metaData 当前单元格元数据
     * @return {String} 要呈现的HTML字符串
     */
    gridSourceRenderer: function(value, metaData) {
        if (value == '1') {
            return 'srm';
        } else {
            return 'sap';
        }
    },
    /**
     * @method vpImportHandler
     * erp导入采购申请
     */
    vpImportHandler: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var importWin = Ext.create('Cp.apply.PurchaseingRequisitionImportWin', {
            moduleId: viewModel.get("moduleId"),
            viewModel: viewModel
        });
        importWin.show();
    },
    /**单位名称渲染*/
    unitNameRender: function(value, metaData) {
        var me = this;
        var viewModel = me.getViewModel();
        var unitStore = viewModel.getStore('unitStore');
        unitStore.each(function(record) {
            if (record.get("unitCode") == value) {
                return record.get("unitName");
            }
        });
    }
});