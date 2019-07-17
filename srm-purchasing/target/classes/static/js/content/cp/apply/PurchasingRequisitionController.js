/**
 * @class {Cp.apply.PurchasingRequisitionController} 
 * @extend {Ext.ux.app.ViewControlle}
 * 采购申请控制类
 */
Ext.define('Cp.apply.PurchasingRequisitionController', {
    extend: 'Ext.srm.app.ViewController',
    alias: 'controller.purchasingRequisitionController',

    /**
	 * @method gridStoreBeforeLoad
	 * 列表加载前事件
	 */
    gridStoreBeforeLoad: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();

        if (vp.searchFlag && undefined != vp.searchWin) {
            var params = vp.searchWin.formPanel.form.getValues();
            data = Ext.applyIf(params, vp.grid.store.proxy.extraParams);

            if (viewModel.get('playListMode') == "undeal") {
                vp.grid.store.proxy.extraParams.billFlag = 'unDeal';
            }
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
	 * @method gridDtlEdit
	 * 采购订单明细编辑事件
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
	 */
    gridDtlEdit: function(editor, content) {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();

        var field = content.field;
        var record = content.record;
        var grid = content.grid;
        var value = content.value;
		var materialCode = record.get('materialCode');

        if (field == 'plantCode' && '' != materialCode) {
            var plantCode = record.get('plantCode');
            Ext.Ajax.request({
                url: path_srment + "/cp/purchaseapply/findpurchasinggroup",
                params: {
                    'materialCode': materialCode,
                    'plantCode': plantCode
                },
                success: function(resp) {
                    var data = Ext.decode(resp.responseText);
                    if (0 < data.length) {
                        record.set('purchasingGroupCode', data[0].purchasingGroupCode);
                        record.set('purchasingGroupName', data[0].purchasingGroupName);
                    }
                }
            });
        }
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
        var field = content.field;
        var record = content.record;
        var grid = content.grid;

        //为正式物料，则根据物料名称 \基本单位，不允许编辑； 
        if ((field == 'unitCode' || field == 'materialName') && record.get('source') == '1') {
            return false;
        }

        if (field == 'plantCode') {
        	var plantStore = content.column.field.store;
        	plantStore.proxy.url = path_masterdata + '/md/material/findfiltrationplant';
        	var companyCode = vp.editWin.formPanel.getForm().findField('model.companyCode').getValue();
        	if (record.get('source') != '1') {
				plantStore.proxy.extraParams = {
					'companyCode': companyCode
				};
				
        	}else{
				//获取该物料对应工厂的数据  
				var materialCode = record.get('materialCode');
				plantStore.proxy.extraParams = {
					'companyCode': companyCode,
					'materialCode': materialCode
				};
        	}
        	
        	plantStore.load();
		}

        return vp.editWin.beforeedit();
    },

    /**
	 * @method getMaterialSelectWin
	 * 获取物料选择窗体
	 * @return {Ext.ux.Window} selectWin 
	 */
    getMaterialSelectWin: function(rowNum, vp, viewModel) {
		var grid = this.getDetailGrid();
		var vm = this.getView();
		var selectWin = Ext.create('Md.material.MaterialSelectWin', {
			singleSelect: false,
			moduleId:vm.id,
			modal:true,
			url:path_masterdata+"/md/material/findmaterialbycompany",
			select: function(g, rs) {
				Q.each(rs, function(r, i) {
					//多选时设置行号
					rowNo = rowNum + 10 * i;
					var data = {
						"materialId": r.get('materialId'),
						"materialCode": r.get('materialCode'),
						"materialName": r.get('materialName'),
						"unitCode": r.get('baseUnitCode'),
						"unitName":r.get("baseUnitName"),
						"rowNo": rowNo,
						"source": "1"
						
					};
					vp.editWin.addDetail(grid, data);
				});
				this.hide();
			}
		});
		
		// 添加申请明细，已添加到明细列表的物料不再显示在选择列表中
		// 删除申请明细行后，需要再选择列表中可以再次选择
		selectWin.gridPanel.getStore().on("load", function(store) {
			// 取到临时的store，列表中删除的记录加到临时store中
			viewModel.getStore('tempDeleteStore').each(function(v, i) {
				store.add(v);
			});
			var checkGridStore = grid.getStore();
			checkGridStore.each(function(v, i) {
				var index = store.find('materialCode', v.get('materialCode'));
				if (index > -1) {
					store.removeAt(index);
				}
			});
		});

		selectWin.gridPanel.getStore().on("beforeload", function(store, obj) {
			var form = vp.editWin.formPanel.getForm();
			var companyCode = form.findField("model.companyCode").getValue();
			store.proxy.extraParams.companyCode = companyCode;
		});

		return selectWin;
	},

    /**
	 * @method gridDtlAddFormalHandler
	 * 采购申请明细添加正式物料按钮操作方法
	 */
    gridDtlAddFormalHandler: function() {
        var me = this;
        var detailGrid = me.getDetailGrid();
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        var form = vp.editWin.formPanel.getForm();
        var company = form.findField('model.companyCode').getValue();
        var detailStore = detailGrid.store;
        var rowNo = (detailStore.getCount() + 1) * 10;

        if (Ext.isEmpty(company)) {
            Q.tips($("message.pleaseSetNoBlankInfo"), "E");
            return;
        }

        var materialWin = me.getMaterialSelectWin(rowNo, vp, viewModel);
        materialWin.show();
    },

    /**
	 * @method gridDtlAddTempHandler
	 * 采购申请明细添加临时物料按钮操作方法
	 */
    gridDtlAddTempHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var company = form.findField('model.companyCode').getValue();

		if (Ext.isEmpty(company)) {
			Q.tips($("message.pleaseSetNoBlankInfo"), "E");
			return;
		}

		var grid = me.getDetailGrid();
		var store = grid.getStore();
		var rowNo = (store.getCount() + 1) * 10;
		var data = {
			"rowNo": rowNo,
			"source": "2"
		};


		vp.editWin.addDetail(grid, data);
	},

    /**
	 * @method gridDtlDeleteHandler
	 * 采购申请明细删除按钮操作方法
	 */
    gridDtlDeleteHandler: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        var detailGrid = me.getDetailGrid();
        var sm = detailGrid.getSelectionModel();
        var selections = sm.getSelection();
        var vm = this.getView();

        if (0 == selections.length) {
            Q.tips($('message.delete.select'), 'E');
            return false;
        }
        Q.confirm($('message.delete.confirm'), {
            renderTo: vm.id,
            ok: function() {
                var store = detailGrid.getStore();
                vp.editWin.deleteDetail(detailGrid);
                store.each(function(r, i) {
                    r.set('rowNo', (i + 1) * 10);
                });
                detailGrid.getStore().commitChanges();
            }
        });
    },

    /**
	 * @method gridDtlImportHandler
	 * 列表明细界面导入按钮方法
	 */
    gridDtlImportHandler: function(_self) {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        var form = vp.editWin.formPanel.getForm();
        var company = form.findField('model.companyCode').getValue();
        var detailGrid = me.getDetailGrid();
        var store = detailGrid.store;

        if (Ext.isEmpty(company)) {
            Q.tips($("message.pleaseSetNoBlankInfo"), "E");
            return;
        }

        var urlParams = '?companyCode=' + form.findField("model.companyCode").getValue();

        var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid 
        var opt = {
            url:viewModel.get("dealUrl"), // + '/importexcel' + urlParams
            method: "/importexcel"+ urlParams,
            importSuccess:function(op, op1){//导入成功后回调
            	if(!op1.data){
            		return;
            	}
    		    Ext.each(Ext.decode(op1.data), function(r, i) {
                    var rowNo = (store.getCount() + 1) * 10;
                    var data = {};
                    if (Ext.isEmpty(r.materialCode) || r.materialCode == "") {
                        data = Ext.applyIf({
                            "rowNo": rowNo,
                            "source": "2"
                        },
                        r);
                    } else {
                        data = Ext.applyIf({
                            "rowNo": rowNo,
                            "source": "1"
                        },
                        r);
                    }

                    vp.editWin.addDetail(detailGrid, data);
                });
            }
        };//配置项
        var renderTo = vp.editWin.id;//渲染载体
        //文件工具类调用
        Ext.UxFile.fileImport(opt, renderTo); 
        
    },

    /**
	 * @method gridDtlDownloadHandler
	 * 明细下载模版按钮方法
	 */
    gridDtlDownloadHandler: function() {
    	Ext.UxFile.fileDown(this.getViewModel().get('dealUrl')+"/download","采购申请明细导入模板.xls",null);
      
    },

    /**
	 * @method getDetailGrid
	 * 获取编辑界面细列表方法
	 * @return {Ext.grid.Panel} detailGrid 
	 */
    getDetailGrid: function() {
        var vp = this.getViewModel().getVp();
        return vp.editWin.getCompByTabClassName('purchasingRequisitionDtls');
    },

    /**
	 * @method vpCheckHandler
	 * 采购申请列表界面关闭按钮方法
	 */
    vpCloseHandler: function(_self) {
        var me = this;
        var vp = me.getViewModel().getVp();
        var grid = vp.grid;
        var selections = grid.getSelectionModel().getSelection();
        if (selections.length <= 0) { //请选择+grid.moduleName
            Q.tips($('message.pleaseSelect') + grid.moduleName, 'E');
            return;
        } else if (selections.length > 1) { //同时只能编辑一条信息！
            Q.warning($('message.onlySelect'));
            return;
        }

        var data = {
            'id': selections[0].get('purchasingRequisitionId')
        };
        me.commAjax("/close", data, vp.id, {btnName : _self.text, "grid" : grid});
    },

    /**
	 * @method vpCheckHandler
	 * 采购申请列表界面取消按钮方法
	 */
    vpCancelHandler: function(_self) {
        var me = this;
        var vp = me.getViewModel().getVp();
        var grid = vp.grid;
        var selections = grid.getSelectionModel().getSelection();
        if (selections.length <= 0) { //请选择+grid.moduleName
            Q.tips($('message.pleaseSelect') + grid.moduleName, 'E');
            return;
        } else if (selections.length > 1) { //同时只能编辑一条信息！
            Q.warning($('message.onlySelect'));
            return;
        }

        var data = {
            'id': selections[0].get('purchasingRequisitionId')
        };
        me.commAjax("/cancel", data, vp.id, {btnName : _self.text, "grid" : grid});

    },


    /**
	 * @method addAfter
	 * 点击添加按钮之后的事件
	 */
    addAfter: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        var form = vp.editWin.formPanel.getForm();
        var companyStore = form.findField('model.companyCode').store;
        //如果公司只有一个，则默认选择
        if (companyStore.getCount() == 1) {
            var companyCode = companyStore.getAt(0).get('companyCode');
            var companyName = companyStore.getAt(0).get('companyName');
            form.findField('model.companyCode').setValue(companyCode);
            form.findField('model.companyName').setValue(companyName);
        }
    },

    /**
	 * @method searchWinSearch
	 * 查询窗体查询事件
	 */
    searchLoadBefore: function() {
        var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();
        vp.searchFlag = true;
    },

    /**
	 * @method vpInstanceAfter
	 * 窗体实例化之后
	 */
    vpInstanceAfter: function() {
       var me = this;
        var viewModel = me.getViewModel();
        var vp = viewModel.getVp();

        // 清除临时store
        vp.editWin.on('hide',
        function() {
            viewModel.getStore('tempDeleteStore').removeAll();
        });
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
        if (Ext.isEmpty(vp.grid.getSelectionModel().getSelection())) {
            var form = vp.editWin.formPanel.getForm();
            var purchasingRequisitionId = form.findField('model.purchasingRequisitionId').getValue();
            var model = Ext.create('Ext.data.Model', {
                'purchasingRequisitionId': purchasingRequisitionId
            });
            vp.grid.getSelectionModel().select(model);
        }

        if ('TOPASS' == name) {
            if (vp.editWin.hidden) {
                vp.flowApprove(vp,text,name);
            } else {
              vp.flowApprove(vp,text,name,vp.editWin);
            }
        } else if ('TONOPASS' == name) {
            if (vp.editWin.hidden) {
                 vp.flowApprove(vp,text,name);
            } else {
               vp.flowApprove(vp,text,name,vp.editWin);
            }
        }
    },
    /**
	 * @method dealState
	 * @param {Object} self 当前点击对象
	 * 撤销审核
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
						id : selected[0].get("purchasingRequisitionId")
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
						Q.warning(response.responseText,{renderTo:vp.id});
					}, 
					callback : function(){
						vp.unmask();
					}
				});
			}
		});
	}
	
});