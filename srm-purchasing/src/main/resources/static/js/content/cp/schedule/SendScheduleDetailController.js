/**
 * @class {Cp.schedule.SendScheduleController}
 * @extend {Ext.ux.app.ViewController}
 * 送货排程控制层
 */
Ext.define("Cp.schedule.SendScheduleDetailController",{
	extend:"Ext.srm.app.ViewController",
	alias:"controller.sendScheduleDetailController",

	/**
	 * 临时数据缓存，用于保存旧的排程量
	 * @param oldScheduleQty
	 */
	searchFlag : false,
	
	/**
	 * @cfg {boolean} rowdblclickFn 是否双击
	 */
	rowdblclickFn : function() {
		return false;
	},
	
	/**
	 * @method vpInstanceAfert
	 * 窗体实例化之后
	 */
	vpInstanceAfertFn: function() {   
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		vp.grid.addListener("afterrender",function(){
			var searchBtn = vp.grid.getTopToolbar().query('*[name=search]')[0];
			searchBtn.handler();
			viewModel.getSearchWin().center();
		})
	},
	/**
     * @method gridEditBgColorRenderer
     * 送货标识渲染
     * @param {Object} v 当前列值
     * @param {Object} m 当前单元格元数据
     * @param {Ext.data.Model} r 当前行记录
     * @return {String} 要呈现的HTML字符串
     */
    gridEditSendFlagRenderer: function(v,m,r) {
		var text;
		if(v == 0 || Ext.isEmpty(v)){
			text = $("sendscheduledetail.sendFlag0"); 
		}else if(v == 1){
			text = $("sendscheduledetail.sendFlag1"); 
		}else if(v == 2){
			text = $("sendscheduledetail.sendFlag2"); 
		}
			return text;
    },

	/**
	 * @method getDetailGrid
	 * 获取编辑界面排程明细列表方法
	 * @return {Ext.grid.Panel} detailGrid 
	 */
	getDetailGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('sendScheduleCommons');
	},
	/**
	 * @method getDetailGrid
	 * 获取编辑界面排程细细单列表方法
	 * @return {Ext.grid.Panel} detailGrid 
	 */
	getDtlDtlGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('sendScheduleDetails');
	},
	/**
	 * @method getDetailGrid
	 * 获取编辑界面排程细细单列表方法
	 */
	/*exportExcel: function() {
		var vp = this.getViewModel().getVp();
		var baseParams = vp.grid.getStore().proxy.extraParams;
    	window.open(path_srm + "/cp/sendschedule/SendScheduleDetail_exprot.action?" + Ext.urlEncode(baseParams));
	},*/
	/**
	 * @method searchFn
	 * 查询方法
	 */
	searchFn: function() {
		this.searchFlag = true;
	},
	/**
	 * @method gridBeforeLoad
	 * store加载前方法
	 */
	gridStoreBeforeLoad: function(store) {
		var vp = this.getViewModel().getVp();
		store.proxy.extraParams.initStates = this.getViewModel().get("initStatesStr");
		if(this.searchFlag && undefined != vp.searchWin){
			var params = vp.searchWin.formPanel.form.getValues();
			params.initStates = vp.grid.store.proxy.extraParams.initStates;
			vp.grid.store.proxy.extraParams = params;
			this.searchFlag = false;
		}
	},
	/**
	 * @method gridBeforeLoad
	 * store加载前方法
	 */
	gridStoreLoad: function(store) {
		var vp = this.getViewModel().getVp();
		if(undefined != vp.searchWin){
			vp.searchWin.formPanel.form.reset();
		}
	},
	/**
	 * @method vpRowdblclick 列表双击事件
	 */
	vpRowdblclick : function() {
		return false;
	}
});