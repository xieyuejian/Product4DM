/**
 * 扩展ViewController 控制类
 * @class {Ext.ux.app.ViewControlle}
 * @extend {Ext.app.ViewControlle}
 */
 console.log("asddddddddddddddddddddddddd");
Ext.define("Ext.ux.app.ViewController", {
    extend: "Ext.app.ViewController",
    alias: "viewmodel.uxviewcontroller",

    /*
     * @method renderSynColor
     * 同步状态渲染颜色公共方法 
     * @param code 编码
     * @param name 显示名称 不必传现  不传默认渲染 code
     * @return {string} 
     *  SYNCHRONIZEDNOT("未同步"),//0
     *  SYNCHRONIZING("同步中"),//1
     *  SYNSUCCESS("已同步"),//2
     *  SYNFAILED("同步失败"),//3
     *  SYNNONEED("不同步");//4 
     */
    renderSynColor: function(code, name) {
        if (code == 'SYNCHRONIZEDNOT' || code == 0) {
            return "<font color='#bbbbbb'>" + (name || $('erpSyn.nosyn')) + "</font>"; // 灰色
        } else if (code == 'SYNCHRONIZING' || code == 1) {
            return "<font color='#ff8800'>" + (name || $('erpSyn.onsyn')) + "</font>"; // 蓝色
        } else if (code == 'SYNSUCCESS' || code == 2) {
            return "<font color='#444444'>" + (name || $('erpSyn.synsuccess')) + "</font>"; // 绿色
        } else if (code == 'SYNFAILED' || code == 3) {
            return "<font color='#EF5F5F'>" + (name || $('erpSyn.synfail')) + "</font>"; // 红色
        } else if (code == 'SYNNONEED' || code == 4) {
            return "<font color='#444444'>" + (name || $('erpSyn.noneed')) + "</font>"; // 红色
        } else {
            return code || name;
        }
    },
    /**
     * 点击新增按钮前方法
     * 
     * @param {Ext.ux.gridPanel} grid 列表
     */
    addBefore: function(grid) {},
    /**
     * 点击新增按钮后方法
     * 
     * @param {Ext.ux.gridPanel} grid 列表
     * @param {Ext.ux.window} win 编辑窗口
     */
    addAfter: function(grid, win) {},
    /**
     * 点击编辑按钮前方法
     * 
     * @param {Ext.ux.gridPanel} grid 列表
     * @param {Object} selectids 选中记录集
     */
    editBefore: function(grid, selectids) {},
    /**
     * 点击编辑按钮后方法
     * 
     * @param {Ext.ux.gridPanel} grid 列表
     * @param {Object} selectids 选中记录集
     * @param {Ext.ux.window} win 编辑窗口
     */
    editAfter: function(grid, selectids, win) {},
    /**
     * 点击查看按钮前方法
     * 
     * @param {Ext.ux.gridPanel} grid 列表
     * @param {Object} selectids 选中记录集
     * @param {Ext.ux.window} win 编辑窗口
     */
    viewBefore: function(grid, selectids, win) {},
    /**
     * 点击查看按钮后方法
     * 
     * @param {Ext.ux.gridPanel} grid 列表
     * @param {Object} selectids 选中记录集
     * @param {Ext.ux.window} win 编辑窗口
     */
    viewAfter: function(grid, selectids, win) {},
    /**
     * 表单设值后方法
     * 
     * @param {Ext.ux.formPanel} formPanel 编辑表单
     * @param {String} viewType 操作类型 如查询 编辑 新建
     * @param {String} json 返回的操作
     * @param {Object} jsonObj 其他配置
     */
    setFormValueAfter: function(formPanel, viewType, json, jsonObj) {},
    /**
     * 数据提交前方法
     * 
     * @param {Ext.ux.formPanel} formPanel 编辑表单
     * @param {String} btnType 操作类型 如查询 编辑 新建 
     */
    submitBefore: function(formPanel, btnType) {},
    /**
     * 数据提交验证后方法
     * 
     * @param {Object} params 拼装后的参数
     * @param {Ext.ux.formPanel} formPanel 编辑表单
     */
    submitAfter: function(params, formPanel) {},
    /**
     * 重写提交方法
     * @param {String} url 提交的url
     * @param {Object} params 拼装好的参数
     * @param {Object} formPanel 表单对象
     * @param {Object} win 编辑窗口对象 
     * @param {Object} op 其他参数
     *   {
     *     submitType:xx 提交类型
     *   }
     */
    overrideSubmit: function(url, params, formPanel, win, op) {},
    /**
     * 重写提交方法
     * 
     * @param {Ext.ux.formPanel} formPanel 编辑表单
     * @param {Object} action  
     */
    submitSuccessAfter: function(form, action) {},
    /**
     * 查询提交前方法
     * 
     * @param {Ext.ux.data.Store} store 列表数据源
     * @param {Object} extraParams 请求参数  
     * @param {Object} data 请求参数  
     */
    searchLoadBefore: function(store, extraParams, data) {
        return true;
    },
    /**
     * 报表查询提交前方法
     * 
     * @param {Object} cfg 报表配置
     * @param {Object} data 请求参数   
     */
    searchReportSubmitBefore: function(cfg, data) {},
    /**
     * 初始化 列表后触发方法
     */
    vpInstanceAfter: function() {},
    /**
     * 列表渲染后方法
     * @param {Object} editWin 编辑窗口
     */
    vpAfterRender: function(editWin) {},
    /**
     * 列表数据点击处理方法
     * @param {Object} grid 列表
     * @param {Object} record 选中的记录
     */
    vpClickAfter: function(grid, record) {},
    /**
     * 列表双击处理方法
     */
    rowdblclickFn: function() {},
	/**
	 * 导出按钮处理方法
	 * @param {Button} _self
	 */
    exportExcel:function(_self){
    	try{ 
	    	var me = this; 
		    var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid 
		    var exportConfig = grid.getConfig('exportConfig') || {};  
		    exportConfig = Ext.apply({
	             type: 'xlsx', 
			     fileName: 'file.xlsx'
	        },exportConfig);
	    	me.doExport(grid,exportConfig);
    	}catch(e){
    		console.log(e);
    	}
    },
    /**
     * 导出
     * @param {Ext.grid.gridPanel} grid 导出的列表
     * @param {Objecrt} config
     */
    doExport: function(grid,config){   
        try{ 
	    	var me = this;
	    	var viewModel = me.getViewModel();
	        grid.saveDocumentAs(config);
    	}catch(e){
    		console.log(e);
    	}
    },
    /**
     * 导出数据前处理
     * @param {} a
     */
    onBeforeDocumentSave : function(a) {
		a.mask('Data export processing。。。');
	},
	/**
     * 导出数据后处理
     * @param {} a
     */
	onDocumentSave : function(a) {
		a.unmask();
	},
	/**
	 * 提单者=催审功能
	 * @param {} billId 单据ID
	 * @param {} billNo 单据号
	 * @param {} createUserId 创建人Id
	 * @param {} constantsBillType 模块编码
	 * @param {} moduleName 模块名称
	 * @param {} viewId 遮罩
	 */
	vpPressingforapprovalHandler:function(_self){
		try{
			var me = this;
			var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid 
			var viewModel = me.getViewModel();
			var vm = me.getView();
			
	        var idProperty = grid.getStore().idProperty;
	        var billNoField = grid.getConfig('billNoField') || "";
	        var selectids = grid.getSelectionModel().getSelection();
	        if (selectids.length <= 0) { //请选择+grid.moduleName
	            Q.tips($("message.pleaseSelect") + grid.moduleName + $("message.record"), "E");
	            return;
	        }
			var moduleCode = vm.billTypeCode;
			var moduleName = viewModel.get("moduleName");
		
			var params = {
				pos : []
			};
			Ext.Array.each(selectids,function(r,index){
				var object = {};
				object["billId"] = r.get(idProperty);
				object["billNo"] = r.get(billNoField);
				object["moduleCode"] = moduleCode;
				object["moduleName"] = moduleName;
				params.pos.push(object);
			});
			Q.confirm($('message.pressingforapproval'), {
				renderTo : vm.id,
				ok : function() {
					var myMsg = Ext.create("Ext.window.MessageBox");
	                    myMsg.wait($("message.submit.wait"), $("message.submit.data"), {
	                        renderTo: vm.id
	                    });
					Ext.Ajax.request({
						url: path_srment+ "/bpm/bpm/bpm/pressingforapproval",
						method: 'POST',
						params : Ext.encode(params) == '{}' ? params : Q.parseParams(params),// 如查params为空对像时不用Q.parseParams转换
						success: function(response) {
							var json = Ext.decode(response.responseText);
							if (false === json.success) {
								Q.tips(json.info || $('message.pressingforapproval.failure'), 'E');
								return;
							} else if (true === json.success) {
								Q.tips($('message.pressingforapproval.success'));
							}
							grid.getStore().reload();
						},
						failure: function(resp, opt) {
							Q.tips($('message.operator.failure'), 'E');
						},
	                    callback: function() {
	                        myMsg.hide(); //关闭遮罩 
	                    }
					});
				}
			});
		}catch(e){
    		console.log(e);
    	}
		
	},
	// 授权方法
	insertUserToProcessFn : function() {
		try{
			var me = this;
			var viewModel = me.getViewModel();
			var vp = viewModel.getVp();
			var editWin = viewModel.getEditWin();
			var grid = vp.grid;
			var idProperty = grid.getStore().idProperty;
			
			var billId = "";
			var selected = grid.getSelectionModel().getSelection();
	        if (Ext.isEmpty(selected)) {
	            billId = editWin.formPanel.getForm().findField("model." + idProperty).getValue();
	        }else{
	        	billId = selected[0].get(idProperty);
	        }
	        
	        var selectWin = Ext.create("Ext.comm.ProcessUserSelectWin", {
	            singleSelect : true,
	            moduleId : me.getView().id,
	            processKey : vp.billTypeCode,// 流程编码
	            baseParams : {
	                "filter_IN_roles_authorities_authorityName" : viewModel.get('authorityNameArr')
	            },
	            url : "insertUsersToProcessPoint",// 提交的方法名
	            type : "Grant",// 授权
	            dataKeyId : billId
	        });
	        selectWin.show();
		}catch(e){
    		console.log(e);
    	}
		
    },
    // 加签方法
    insertRoleToProcessNextPoint: function(flag) {
    	try{
			var me = this;
			var viewModel = me.getViewModel();
			var vp = viewModel.getVp();
			var grid = vp.grid;
			var idProperty = grid.getStore().idProperty;
			var editWin = viewModel.getEditWin();
			var billId = "";
			var selected = grid.getSelectionModel().getSelection();
	        if (Ext.isEmpty(selected)) {
	            billId = editWin.formPanel.getForm().findField("model." + idProperty).getValue();
	        }else{
	        	billId = selected[0].get(idProperty);
	        }
	        var selectWin = Ext.create("Ext.comm.ProcessUserSelectWin", {
	            singleSelect : true,
	            moduleId : me.getView().id,
	            processKey : vp.billTypeCode,// 流程编码
	            baseParams : {
	                "filter_IN_roles_authorities_authorityName" : viewModel.get('authorityNameArr')
	            },
	            url : "insertRoleToProcessNextPoint",// 提交的方法名
	            type : "Sign",// 加签
	            dataKeyId : billId
	        });
	        selectWin.show();
		}catch(e){
    		console.log(e);
    	}
        
    } 

});