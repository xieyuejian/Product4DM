/**
 * @class {Cp.forecast.ForecastController} 
 * @extend {Ext.ux.app.ViewControlle}
 * 采购预测控制类
 */
Ext.define('Cp.forecast.ForecastController',{
	extend: 'Ext.srm.app.ViewController',	
    alias: 'controller.forecastController',

    
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
 	 * @method formPurchasingOrgCodeSelect
 	 * 表单采购组织选中事件
 	 * @param {Ext.form.field.ComboBox} combo 当前对象
 	 * @param {Ext.data.Model} record 所选中的记录
 	 */
 	formPurchasingOrgCodeSelect: function(field,record) {
	 	var me = this;
	 	var viewModel = me.getViewModel();
	 	var vp = viewModel.getVp();
 		var form = vp.editWin.formPanel.getForm();

 		form.findField('model.purchasingOrgCode').setValue(record.get('purchasingOrgCode'));
 		form.findField('model.purchasingOrgName').setValue(record.get('purchasingOrgName'));
 		viewModel.getStore('plantStore').proxy.extraParams = {
 		    'purchasingOrgCode': record.get('purchasingOrgCode')
 		};
 		viewModel.getStore('plantStore').load()
 	},


 	/**
 	 * @method vpErpImportHandler
 	 * 列表界面ERP导入按钮方法
 	 */
 	vpErpImportHandler: function(){
	 	var me = this;
	 	var viewModel = me.getViewModel();
	 	var vp = viewModel.getVp();
	 	var form = vp.editWin.formPanel.getForm();
		form.findField('model.createType').setValue('erp');
	
	 	vp.customAddBtn(me.afterFn());
 	}, 
 	
 	afterFn: function(){
 		try{
 			var me = this;
			var detailGrid = me.getDetailGrid();
	 		var add = detailGrid.getTopToolbar().items.items;
	 		add[0].hide();
	 		var exportbtn = detailGrid.getTopToolbar().find("name","export");
	 		exportbtn[0].hide();
	 		var importbtn = detailGrid.getTopToolbar().find("name","import");
	 		importbtn[0].hide();
	 		var erpsearch = detailGrid.getTopToolbar().find("name","erpSearch");
			erpsearch[0].show();
 		}catch(e){
 			console.log(e)
 		}
 		
 	},

     /*
	 * @method gridDtlMaterialCodeTrigger
 	 * 表格物料点击触发事件
 	 */
 	gridDtlMaterialCodeSetValueAfter:function(_self,fieldselect,parentObj,grid,parentType){
 		var recordTemp = grid.getSelectionModel().getSelection()[0];
        recordTemp.set('unitName', fieldselect.data.baseUnitCode);
 
 	},
 	/**
 	 * @method gridDtlMaterialCodeSelect
 	 * 表格物料下拉选择触发事件
 	 */
 	gridDtlMaterialCodeSelect:function(combo, record){
 		console.log("self");
 		var me = this;
 		var viewModel = me.getViewModel();
 		var vp = viewModel.getVp();
 		var record = vp.editWin.getCompByTabClassName("forecastDtls").getSelection()[0];
 		record.set('materialCode', record.get('materialCode'));
        record.set('materialName', record.get('materialName'));
        record.set('unitCode', record.get('baseUnitCode'));
 	},
 	
 	/**
 	 * @method gridDtlPlantCodeFocus
 	 * 采购预测明细供应商选中事件
 	 * @param {Object} combo 当前控件对象
 	 * @param {Ext.data.Model} record 选中的记录
 	 */
 	gridDtlVendorCodeSelect: function(field,value,selected){
 		var me = this;
 		var viewModel = me.getViewModel();
 		var vp = viewModel.getVp();
 		var record = vp.editWin.getCompByTabClassName("forecastDtls").getSelection()[0];
 		record.set('vendorCode', selected.get('vendorCode'));
        record.set('vendorName', selected.get('vendorName'));
 	},

 


 	/**
 	 * @method gridDtlVendorCodeTrigger
	 * @param {Ext.field.Field} field 当前field对象
 	 * 列表供应商点击触发事件
 	 */
 	gridDtlVendorCodeTrigger:function(_self){
 		var me = this;
 		var viewModel = me.getViewModel();
 		var vp = viewModel.getVp();
 		var purchasingOrgCode = vp.editWin.formPanel.getForm().findField('model.purchasingOrgCode').getValue();
 		var record = vp.editWin.getCompByTabClassName("forecastDtls").getSelection()[0];//vp.editWin.findByType('grid')[0].getSelection()[0];
 		var vm = this.getView();
 		var selectWin = new Sl.masterdata.VendorSelectWin({
 			singleSelect: true,
 		    moduleId: vm.id,
 		    modal:true,
 		    baseParams: {
 		    filter_IN_certificationStatus:'QUALIFIED',
 			purchasingOrgCode:purchasingOrgCode
 		    },
 		    baseParamsTree: {	
		    certificationStatus:'QUALIFIED'
 		    },
 		    select: function(grid, r) {
 		    	_self.setConfig({editFlag:false});
 		     	record.set('vendorCode', r.get('vendorCode'));
 		        record.set('vendorErpCode', r.get('vendorErpCode'));
 		       record.set('vendorName', r.get('vendorName'));
 		        this.hide();
 		    }
 		});
 		selectWin.show();
 	},
 	/**
 	 * @method gridDtlBeforeedit
 	 * 采购订单明细编辑前事件
 	 * @param {Ext.grid.plugin.Editing} editor 编辑对象
 	 * @param {Object} content 具有以下属性的编辑上下文
 	 *   @param {Ext.grid.Panel} content.content 所编辑的表格对象
 	 *   @param {Ext.data.Model} content.record 正在编辑的记录
 	 *   @param {String} content.field 正在编辑的字段名称
 	 *   @param {Mixed} content.value 字段当前值
 	 *   @param {HTMLElement} content.row 正在编辑的行html元素
 	 *   @param {Ext.grid.column.Column} content.column 正在编辑的列
 	 *   @param {Number} content.rowIdx 正在编辑的行序列
 	 *   @param {Number} content.colIdx 正在编辑的列序列.
 	 *   @param {Boolean} context.cancel 将此设置为“TRUE”取消编辑或从处理程序返回false。
 	 *   @param {Mixed} context.originalValue 编辑前的值
 	 */
 	gridDtlBeforeedit: function(editor, content) {
 		var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        var record = content.record;
        var field = content.field;
        var form = vp.editWin.formPanel.getForm();
		var createType = form.findField('model.createType').getValue();
        if("erp" == createType){
            return false;
        }
        
        if(content.field == 'forecastMainDate'||content.field == 'plantCode'){
            if(record.get('materialCode') == null){
                Q.warning('请选择物料编码！');
            }
        }
        
        if (field == 'plantCode') {
        	var plantStore = content.column.field.store;
        	plantStore.proxy.url = path_masterdata + '/md/material/findfiltrationplant';
        	var purchasingOrgCode = vp.editWin.formPanel.getForm().findField('model.purchasingOrgCode').getValue();
        	//获取该物料对应工厂的数据  
				var materialCode = record.get('materialCode');
				plantStore.proxy.extraParams = {
					'purchasingOrgCode': purchasingOrgCode,
					'materialCode': materialCode
				};
        	
        	plantStore.load();
		}
        
        
        // 判断是否
        return vp.editWin.beforeedit();
 	},


	/**
	 * @method gridDtlAddHandler
	 * 预测明细添加按钮操作方法
	 */
	gridDtlAddHandler: function() {
		var me = this;
		var vp = me.getViewModel().getVp();
		var grid = me.getDetailGrid(); //查找出所属的父Grid

		if(!vp.editWin.formPanel.getForm().isValid()){
			return;
		}

		vp.editWin.addDetail(grid);
	},

	/**
	 * @method gridDtlDeleteHandler
	 * 预测明细删除按钮操作方法
	 */
	gridDtlDeleteHandler: function() {
		var vp = this.getViewModel().getVp();
		var grid = this.getDetailGrid(); //查找出所属的父Grid
		vp.editWin.deleteDetail(grid);
		var store = grid.store;

		store.each(function(r) {
			r.set('round', store.indexOf(r) + 1)
		});

		store.commitChanges();
	},

	/**
	 * @method gridDtlDownloadHandler
	 * 明细下载模版按钮方法
	 */
	gridDtlDownloadHandler: function() {
		Ext.UxFile.fileDown(this.getViewModel().get('dealUrl')+"/download","采购预测明细导入模板.xls",null);
	},

	/**
	 * @method gridDtlImportHandler
	 * 列表明细界面导入按钮方法
	 */
	gridDtlImportHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = me.getDetailGrid();
		
		

        var opt = {
            url:viewModel.get("dealUrl"), // + '/importexcel' + urlParams
            method: "/importdata",
            importSuccess:function(op, op1){//导入成功后回调
            	if(!op1.data){
            		return;
            	}
    		    Ext.each(Ext.decode(op1.data), function(r, i) {
                    vp.editWin.addDetail(grid, r);
                });
            }
        };//配置项
        var renderTo = vp.editWin.id;//渲染载体
        //文件工具类调用
        Ext.UxFile.fileImport(opt, renderTo); 
		
	},


	/**
	 * @method gridDtlErpSearchHandler
	 * 列表明细界面从ERP查询按钮方法
	 */
	gridDtlErpSearchHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var grid = this.getDetailGrid(); //查找出所属的父Grid
		if (!vp.editWin.formPanel.getForm().isValid()) {
			return;
		}
		var form = vp.editWin.formPanel.getForm();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
		var purchasingOrgName = form.findField('model.purchasingOrgName').getValue();
		var plantStore =  me.getStore('plantStore');
		plantStore.proxy.extraParams = {
 		    'purchasingOrgCode': purchasingOrgCode
 		};
 		var purchasingGroupStore =  me.getStore('purchasingGroupStore');
		purchasingGroupStore.proxy.extraParams = {
 		    'purchasingOrgCode': purchasingOrgCode
 		};
 		purchasingGroupStore.load();
		var wins = Ext.create("Cp.forecast.ForecastSapSelectWin",{
			grid: grid,
			plantStore: plantStore,
			purchasingGroupStore: purchasingGroupStore,
			renderTo:vp.editWin.id
		});
		
		wins.show();

		
		wins.loadData({
			purchasingOrgCode: purchasingOrgCode,
			purchasingOrgName: purchasingOrgName
		});
	},


	/**
	 * @method submitBefore
	 * @param {Ext.form.Panel} formPanel 当前主单
	 * 提交之前事件
	 */
	submitBefore: function(formPanel) {
		var me = this;
		var detailGrid = me.getDetailGrid();
		var store = detailGrid.store;

		var flag = false;
		var date = '';
		var date1 = '';

		store.data.each(function(r) {
			store.data.each(function(r1) {
				if (r.get('rowNo') !== r1.get('rowNo')) {
					if (typeof r.get('forecastMainDate') == 'string') {
						date = r.get('forecastMainDate').substring(0, 10)
					} else {
						if(r.get('forecastMainDate') != null){
							date = r.get('forecastMainDate').getTime();
						}
					}
					if (typeof r1.get('forecastMainDate') == 'string') {
						date1 = r1.get('forecastMainDate').substring(0, 10)
					} else {
						if(r1.get('forecastMainDate') != null){
							date1 = r1.get('forecastMainDate').getTime();
						}
					}

					if (r.get('materialCode') != null && r.get('materialCode') == r1.get('materialCode') && date == date1 && r.get('plantCode') == r1.get('plantCode')) {
						flag = true;
					}
				}
			})
		})

		if (flag) {
			Q.tips('该时间数据已创建，不能再次新建', 'E');
			return false;
		}

		return true;
	},


	
	/**
	 * @method addAfter
	 * 点击添加按钮之后的事件
	 */
	addAfter: function() {
		var me = this;
		var detailGrid = me.getDetailGrid();
 		var add = detailGrid.getTopToolbar().items.items;
 		add[0].show();
		var erpsearch = detailGrid.getTopToolbar().find('name', 'erpSearch');
		erpsearch[0].hide();
		var exportbtn = detailGrid.getTopToolbar().find('name', 'export');
		exportbtn[0].show();
		var importbtn = detailGrid.getTopToolbar().find('name', 'import');
		importbtn[0].show();
	},
	
	editAfter: function(grid, r, win) {
		try{
			var me = this;
			var createType = r[0].get('createType');
			if("erp" == createType){
				me.afterFn();
			}else{
				me.addAfter()
			}
		}catch(e){
			console.log(e);
		}
	},

	/**
	 * @method getDetailGrid
	 * 获取编辑界面订单明细列表方法
	 * @return {Ext.grid.Panel} detailGrid 
	 */
	getDetailGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('forecastDtls');
	},


 	/**
 	 * @method dealState
 	 * @param {Object} self 当前点击对象
 	 * 状态处理方法
 	 */
 	flowApprove: function(self) {
 		var me = this;
 		var viewModel = me.getViewModel();
 		var vp = viewModel.getVp();
 		var name = self.name;
 		var text = self.text;

 		//由审批跟踪跳转过来的情况下
 		if(Ext.isEmpty(vp.grid.getSelectionModel().getSelection())){
 			var form = vp.editWin.formPanel.getForm();
 			var forecastId = form.findField('model.forecastId').getValue();
 			var model = Ext.create('Ext.data.Model',{ 
 			    'forecastId':forecastId
 			});
 			vp.grid.getSelectionModel().select(model);
 		}
 		
		if ('TOPASS' == name) {
			if(vp.editWin.hidden){
				vp.flowApprove(vp,text,name);
			}else{
				vp.flowApprove(vp,text,name,vp.editWin);
			}
		} else if ('TONOPASS' == name) {
			if(vp.editWin.hidden){
				vp.flowApprove(vp,text,name);
			}else{
				vp.flowApprove(vp,text,name,vp.editWin);
			}
		}
 	},
 	
 	vpAfterRender : function() {
		return 'view';
	},
	/**
	 * 撤销审批
	 */
	revokeAuditHandler:function(){
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = vp.grid;
		var selected = grid.getSelectionModel().getSelection();
		var dealUrl = viewModel.get('dealUrl');
		if (Ext.isEmpty(selected)) {
			Q.tips($('message.pleaseSelect'),"E");
			return;
		}
		Q.confirm($("message.revoke.confirm"), {
			renderTo : vp.id,
			ok : function() {
				vp.mask($("auction.toconfirmPlearWait"));
				Ext.Ajax.request({
					url : dealUrl + "/revokeaudit",
					method : 'POST',
					params : {
						id : selected[0].get("forecastId")
					},
					success : function(response) {
						var json = Ext.decode(response.responseText), flag = json.success;
						if(json.data == "ok"){
							Q.tips($("message.operate.success") + "!");
							grid.getStore().reload();
						}else if(json.data == "notOperation"){// 第一个节点已审批，无法撤销审核！
							Q.tips($("project.notRevoke"));
						}
					},
					failure : function(response) {
						vp.unmask();
						Q.warning(response.responseText,{renderTo:me.moduleId});
					}, 
					callback : function(){
						vp.unmask();
					}
				});
			}
		});
	},
	 /**
     * 供应商编码弹出前事件
     * **/
	 vendorCodeTriggerBeforeShow : function() {
		try{
			var viewModel = this.getViewModel()
			var form = viewModel.getEditWin().formPanel.getForm();
			var purchasingOrgCode = form.findField("model.purchasingOrgCode").getValue();// 采购组织
			//判断采购组织是否为空
			if(Ext.isEmpty(purchasingOrgCode)){
				Q.tips($("correctiveNotice.message.mustSelectedPurchasingOrg"),"E");
				return false;
			}
		} catch(e) {
            console.log(e);
        }
	},

 /**
     * 供应商编码弹出过滤事件
     * **/
     vendorCodeTriggerBaseParams : function(_self, baseParams, parentObj) {
		try{
			var viewModel = this.getViewModel()
			var form = viewModel.getEditWin().formPanel.getForm();
			var purchasingOrgCode = form.findField("model.purchasingOrgCode").getValue();// 采购组织
			baseParams.purchasingOrgCode = purchasingOrgCode;
			baseParams.filter_IN_certificationStatus = "QUALIFIED";
			
		} catch(e) {
            console.log(e);
        }
	},

	
	/**
	 * 创建方式渲染
	 */
	createTypeRenderer:function(v,m,r){
		try{
			var me = this;
			if("srm" == v){
				return $('createType.manual');
			}else if("erp" == v){
				return $('button.erpImport');
			}
			return v;
			
		}catch(e){
			console.log(e);
		}
	}

});
  	