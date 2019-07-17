﻿/**
	 * @class {Cp.schedule.SendScheduleController}
	 * @extend {Ext.ux.app.ViewController} 送货排程控制层
	 */
Ext.define("Cp.schedule.SendScheduleController",{
	extend:"Ext.srm.app.ViewController",
	alias:"controller.sendScheduleController",
	
	/**
	 * @method grid1AfterValidFn 编辑界面添加校验
	 */
	grid1AfterValidFn: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = me.getDetailGrid();
		var selected = grid.getSelectionModel().getSelection()[0];
		var store = grid.getStore();
		// 必须添加排程子明细 验证
	  	 var childGrid = vp.editWin.getCompByTabClassName("sendScheduleDetails"); 
	     var  tempKey =  vp.editWin.getStoreTempKey(childGrid,selected); // 明细的明细的临时sotre
																			// 的
																			// key
	   	 if((Ext.isEmpty(childGrid.storeTemp[tempKey]) || childGrid.storeTemp[tempKey].getCount() == 0) && Ext.isEmpty(selected.get('sendScheduleCommonId'))){
	   		 // 该排程明细还没有添加排程子明细
//			 Q.tips($("sendschedulecommon.message.msg1"),"E");
			 return false;
	   	 }
		var checkValue; 
		var vaildFlag = true;
		store.each(function(v,i){
			if(i == 0){
				checkValue = v.get('purchaseOrderNo');
			}else{
				if(checkValue != v.get('purchaseOrderNo')){
					vaildFlag = false;
					return false;
				}
			} 
		}); 
		
		var isMulti = vp.editWin.formPanel.form.findField("model.isMulti").getValue();
		
		if(vaildFlag){ 
	    	return true;
		}else if("2" == isMulti){
			Q.tips($("sendschedule.message.warnMsg8"),"E")
			return false;
		}
	},
	/**
	 * @method Handler 列表界面同步状态
	 */
	gridErpStateRender:function(v,m,r){
		var msg=r.get("synReturnMsg");
		m.attr="ext:qtip='<b>ERP同步信息:</b><br/>---------<br/>"+msg+"'";
		switch(v) {
		    case 0:return "<font color='#bbbbbb'>"+$("erpSyn.nosyn")+"</font>";break;// 灰色
		    case 2:return "<font color='#ff8800'>"+$("erpSyn.onsyn")+"</font>";break;// 蓝色
		    case 1:return "<font color='#444444'>"+$("erpSyn.synsuccess")+"</font>";break;// 绿色
		    case 3:return "<font color='#444444'>"+$("erpSyn.synfail")+"</font>";break;// 红色
		    default:return "";
		}
	},
	/**
	 * @method Handler 新增表格的一行
	 */
	grid2AddChildDetail:function(v,m,r){
		var grid = this.getDtlDtlGrid();
		var vp = this.getViewModel().getVp();
		vp.editWin.addChildDetail(grid,{"sendFlag":0,"deleteFlag":1});
	},

	/**
	 * @method Handler 列表界面同步erp按钮方法
	 */
	grid1AddHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var grid = me.getDetailGrid();
		var selected = grid.getSelectionModel().getSelection();
		var vp = viewModel.getVp();
    	if (!vp.editWin.formPanel.form.isValid()) {
			Q.tips($("message.pleaseSetNoBlankInfo"),"E");
			return;
		}  

    	var formField = vp.editWin.formPanel.getForm();
    	
         //  主单上所选供应商的采购排程；
    	var vendorErpCode = formField.findField('model.vendorErpCode').getValue(); 
    	//  主单上所选采购组织的采购排程；
    	var purchasingOrgCode = formField.findField('model.purchasingOrgCode').getValue(); 
    	
    	if(Ext.isEmpty(vendorErpCode)){
    		Q.tips($('sendschedule.message.warnMsg9'));
    		return;
    	}
    	
    	if(Ext.isEmpty(purchasingOrgCode)){
    		// 请选择采购组织
    		Q.tips($("purchasingOrg.pleaseSelectPorg")+"！");
    		return;
    	} 
    	
    	var selectWin = this.showPurchaseOrderSelectWin(); 
    	var selectGrid = selectWin.gridPanel; 
    	selectGrid.getStore().on('beforeload',function(store, options){  
    		store.proxy.extraParams.filter_EQ_purchaseOrder_vendorErpCode =  vendorErpCode; 
    		store.proxy.extraParams.filter_EQ_purchaseOrder_purchasingOrgCode =  purchasingOrgCode; 
    	});
    	selectWin.show(); 
    },

	/**
	 * @method Handler 编辑界面明细删除方法
	 */
	grid1DeleteHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = me.getDetailGrid();
		var selections = grid.getSelectionModel().getSelection();
        var store = grid.getStore();
        // 删除前验证
        // 如果该排程明细的送货标识为未送货，则该排程明细允许删除；
        // 如果该排程明细的送货标识为部分送货或全部送货，则该明细不允许采购更改；
    	var canDeleteFlag = true;
    	var gridPanel = vp.editWin.getCompByTabClassName("sendScheduleDetails");
    	var gridStore = vp.editWin.getCompByTabClassName("sendScheduleDetails").getStore();
    	if(gridStore){
    		gridStore.each(function(o,i){ 
         		Q.each(selections,function(oo,ii){
                	var selected = oo;
                	var  tempKey =  vp.editWin.getStoreTempKey(o,selected); // 明细的明细的临时sotre
                	if(gridPanel.storeTemp[tempKey]){  
                		gridPanel.storeTemp[tempKey].each(function(v,i){
                			if(v.get('sendFlag') != 0){
                       			 Q.tips($("sendschedule.message.warnMsg11"));// 请选择删除的信息！
                       			 canDeleteFlag = false;
                       			 return;
                   		    }
                		});
                	} 
                	if(canDeleteFlag){
                    	return;
                    }
                })
                if(canDeleteFlag){
                	return;
                }
         	})
    	}
     
    	if(canDeleteFlag){  
    		Q.confirm($("message.delete.confirm"), {
    	       ok: function(){
    		// 如果该条删除的排程明细有Id这先保存到临时的删除store中
        	// 目的为可在次选择到进行排程
    		Ext.each(selections,function(obj,ii){ 
    			if(!Ext.isEmpty(obj.get('sendScheduleCommonId'))){ 
            		var u = new Ext.data.Record({ 
            			sendScheduleCommonId:obj.get('sendScheduleCommonId'),// 采购明细
            			purchaseOrderDetailId:obj.get('purchaseOrderDetailId'),// 排程明细id
            			purchaseOrderId:obj.get('purchaseOrderId'), // 订单Id
            			buyerId:obj.get('buyerId'),// 采购员id
            			buyer:obj.get('buyerName'),// 采购员名称
            			purchaseOrderNo:obj.get('purchaseOrderNo'),// 订单号
            			erpPurchaseOrderNo:obj.get('erpPurchaseOrderNo'),// SAP采购订单号
    					rowIds:obj.get('rowIds'),// 订单明细行号
    					vendorCode:obj.get('vendorCode'),// 供应商编码
    					vendorName:obj.get('vendorName'),// 供应商名称
    					materialId:obj.get('materialId'),// 物料ID
    					materialCode:obj.get('materialCode'),// 物料编码
    					materialName:obj.get('materialName'),// 物料名称
    					unitCode:obj.get('unitCode'),// 单位编码
    					unitName:obj.get('unitName'), // 单位名称
    					vendorQty:obj.get('sendQty'),// 订单数量
    					scheduleQty:0,// 已排程
    					storeLocal:obj.get('storeLocal'),// 库存地点
    					lineItemTypeCode:obj.get('lineItemTypeCode'),// 行项目类别
    					canSendQty:obj.get('sendQty'),// 订单数量
    					plantCode:obj.get('factoryCode'),// 工厂编码
    					plantCode:obj.get('factoryName'),// 工厂名称
    					vendorTime:obj.get('vendorTime'),// 交货日期
    					qtyArrive:0,
    					qtyQuit:0,
    					qtySend:0,
    					qtyOnline:0,
    					buyerPrice:obj.get('taxPrice'),// 为税价
    					vendorErpCode:obj.get('vendorErpCode'),// 供应商erp编码
    					companyCode:obj.get('companyCode'),// 公司名称
    					schdule:obj.get('scheduledQty'),// 隐藏已排程
    					unSchdule:Ext.util.Format.number( (obj.get('vendorQty')-obj.get('scheduledQty')),'0.000')// 隐藏可排程量
                     });  
           		    // 把删除的记录暂存到临时的store中
            		viewModel.getStore("tempDeleteStore").add(u);
    			}
            });
        	vp.editWin.deleteDetail(grid);
			}
		});
    	}// confirm delete end
    },
    /**
	 * @method Handler 编辑界面细细单删除方法
	 */
	grid2DeleteHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var grid = me.getDtlDtlGrid();
		// 删除细单操作
    	var canDelete = true;
    	// 删除前验证
        var selections = grid.getSelectionModel().getSelection();
        var store = grid.getStore();
        
        // 如果该排程明细的送货标识为未送货，则该排程明细允许删除；
        // 如果该排程明细的送货标识为部分送货或全部送货，则该明细不允许采购更改；
    	Q.each(selections,function(v,i){
    		if(!Ext.isEmpty(v.get('sendFlag')) && v.get('sendFlag') != 0){
    			 Q.tips($('sendschedule.message.warnMsg11'));// 请选择删除的信息！
    			 canDelete = false;
    			 return;
    		}
    		if(vp.editWin.changeFlag == true){
    			v.set('deleteFlag',1);
			}
    	}); 
    	if(canDelete){
    		Q.confirm($("message.delete.confirm"), {
    	         ok: function(){
    	         	var totalSendQty = 0;
    	         	Q.each(selections,function(r,i){
    	         		var scheduleQty = Ext.isEmpty(r.get('scheduleQty'))? 0 :r.get('scheduleQty');
    	         		totalSendQty = totalSendQty + scheduleQty;
    	         	});
    	         	// 回执明细的数量
		    		// 找出选中的明细记录
		       	    var parentGrid = vp.editWin.getCompByTabClassName("sendScheduleCommons"); 
		       	    var selected = parentGrid.getSelectionModel().getSelection()[0];
		       	    // 所删除的子明细 的需求数量
    	         	
    	         	// 回执可送货数量
		            var canSendQtyNew = selected.get('canSendQty') + totalSendQty; 
		            selected.set('canSendQty',canSendQtyNew);
		             
		            // 回执已排程量
		            var scheduleQtyNew = selected.get('scheduleQty') - totalSendQty; 
		            selected.set('scheduleQty',scheduleQtyNew); 
    	         	
    	         	vp.editWin.deleteChildDetail(grid);
       	    
				}
			});
    	}
    
	},

    /**
	 * @method gridPricingDtlEdit 排程细细单编辑事件
	 * @param {Ext.grid.plugin.Editing}
	 *            editor 编辑对象
	 * @param {Object}
	 *            content 具有以下属性的编辑上下文
	 * @param {Ext.grid.Panel}
	 *            content.content 所编辑的表格对象
	 * @param {Ext.data.Model}
	 *            content.record 正在编辑的记录
	 * @param {String}
	 *            content.field 正在编辑的字段名称
	 * @param {Mixed}
	 *            content.value 字段当前值
	 * @param {HTMLElement}
	 *            content.row 正在编辑的行html元素
	 * @param {Ext.grid.column.Column}
	 *            content.column 正在编辑的列
	 * @param {Number}
	 *            content.rowIdx 正在编辑的行序列
	 * @param {Number}
	 *            content.colIdx 正在编辑的列序列.
	 */
    grid2DtlEdit: function(editor, e) {
    	var me = this;
    	var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
    	vp.editWin.afterEditChildDetail(e);
	   	 // 获取排程子明细dfd
	   	 var selectedChild = e.record;
	   	 var gridStore = e.grid.getStore();
	   	 var parentGrid = vp.editWin.getCompByTabClassName("sendScheduleCommons"); 
	   	 var selected = parentGrid.getSelectionModel().getSelection()[0]; 
	   	 var detailId = selected.get("purchaseOrderDetailId");
	   	 /*if(Ext.isEmpty(selected.get('schdule'))){
	   		 selected.set('schdule',selected.get('scheduleQty'));
	   		 selected.set('unSchdule',selected.get('canSendQty'));
	   	 }*/
	   	 var schduleQty = Ext.isEmpty(selected.get('scheduleQty')) ? 0 : selected.get('scheduleQty');
	   	 var unSchduleQty = Ext.isEmpty(selected.get('canSendQty')) ? 0 : selected.get('canSendQty');
	   	 if(e.field == "scheduleQty"){  
        	 // 当前填写的明细需求数量
        	 var currentTotal = selectedChild.get('scheduleQty');
        	 if(currentTotal <= 0){
        		 Q.tips($("sendschedule.message.warnMsg1"),"E"); 
        		 selectedChild.set('scheduleQty',null);
        		 return false;
        	 }
        	 // 当明细对于的收货量不为空时判断需求量不能小于收货量+在途量
        	 var receiptQty = selectedChild.get('receiptQty');
        	 var onWayQty = selectedChild.get('onWayQty');   
        	 var original  = Ext.isEmpty(e.originalValue) ? 0 : e.originalValue,
        	     setOriginal = Ext.isEmpty(e.originalValue) ? '' : e.originalValue;
             if( selectedChild.get('sendFlag') == 1
        			 && currentTotal < ((!Ext.isEmpty(receiptQty)?Number(receiptQty.toFixed(3)):0) + (!Ext.isEmpty(receiptQty)?Number(onWayQty.toFixed(3)):0))){
           	 	selectedChild.set('scheduleQty',setOriginal);
           	 	
        		 Q.tips($("sendschedule.message.warnMsg14"),"E");  
        		 return false;
        	 }
        	 
        	 //新排程量 = 原已排程量 + （新量 - 旧量）
        	 var newSendQty = schduleQty + (e.value - original);
        	 // 订单数量
        	 var sendQtyTotal =  selected.get('sendQty');
        	 
        	 var canSendQty = sendQtyTotal - newSendQty;
        	 
        	 if(canSendQty < 0){
        	 	Q.tips($("sendschedule.message.warnMsg2"),"E"); 
        		selectedChild.set('scheduleQty',setOriginal);// 细细单的需求量为上次编辑的需求量
        		return false;
        	 }else{
        	 	// 可排程量
				selected.set('canSendQty',canSendQty);
				// 已排程量
				selected.set('scheduleQty',newSendQty); 
        	 }
   	 	} 
	   	 // 非强制变更时，更新旧的需求时间和需求数量
	   	 if(vp.editWin.changeFlag != true){
	   		 selectedChild.set("oldScheduleQty",selectedChild.get("scheduleQty"));
	       	 selectedChild.set("oldScheduleTime",selectedChild.get("scheduleTime"));
		}
    },
    /**
	 * @method gridPricingDtlBeforeedit 排程细细单编辑前事件
	 * @param {Ext.grid.plugin.Editing}
	 *            editor 编辑对象
	 * @param {Object}
	 *            content 具有以下属性的编辑上下文
	 * @param {Ext.grid.Panel}
	 *            content.content 所编辑的表格对象
	 * @param {Ext.data.Model}
	 *            content.record 正在编辑的记录
	 * @param {String}
	 *            content.field 正在编辑的字段名称
	 * @param {Mixed}
	 *            content.value 字段当前值
	 * @param {HTMLElement}
	 *            content.row 正在编辑的行html元素
	 * @param {Ext.grid.column.Column}
	 *            content.column 正在编辑的列
	 * @param {Number}
	 *            content.rowIdx 正在编辑的行序列
	 * @param {Number}
	 *            content.colIdx 正在编辑的列序列.
	 * @param {Boolean}
	 *            context.cancel 将此设置为“TRUE”取消编辑或从处理程序返回false。
	 * @param {Mixed}
	 *            context.originalValue 编辑前的值
	 */
    grid2DtlBeforeedit: function(editor, e) {
    	var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
	  	  // 判断排程子明细的宗数量是否大于订单数量
	   	 // 获取排程明细grid
	   	 // 获取排程子明细
	   	 var row=e.row;
    	 var record = e.record;
    	 var gridStore = e.grid.getStore(); 
    	 var size=gridStore.getCount( ) ;
    	 // 该明细行之后有部分送货和已送货的明细行则该明细行不能编辑
    	 if(size>(1+parseFloat(row))){
    		 var flag=false;
    		 for(var i=1+parseFloat(row);i<size;i++){
    			var r = gridStore.getAt(i); 
    			if(!Ext.isEmpty(r.data.sendFlag) && (r.data.sendFlag == 2||r.data.sendFlag == 1)){
    				flag=true;
    				break;
    			}
    		 }
    		 if(flag){
    			 return false;
    		 }
    	 }
    	 // 全部送货不能修改
    	 if(!Ext.isEmpty(record.get("sendFlag")) &&( record.get("sendFlag") == 2||record.get("sendFlag") == 1)){
    		 return false;
    	 }
 		 return vp.editWin.beforeedit();
     },
     /**
		 * @method vpDownloadHandler 列表界面下载按钮方法
		 */
     grid2DtlAfterValid: function(grid,rr,fn) {   
      var vp = this.getViewModel().getVp();
  	  var store = grid.getStore(); 
	  var total = 0;  
	  store.each(function(v){
		  if(!Ext.isEmpty(v.get('scheduleQty'))){ 
    		  total +=  Number(v.get('scheduleQty').toFixed(3)); 
		  }
	  });  
	  
	  var form = vp.editWin.formPanel.getForm();
	  // 订单明细需全部排程
	  var isFull = form.findField('model.isFull').getValue();
	  
	  // 该采购订单明细的订单量需要在一张排程单上全部排完
	  // 即判断该订单明细对应的排程子明细的需求数量是否等于订单数量或可排程数量
	  if(isFull == "1" && Number(rr.get('canSendQty')!=0)){ 
		  // Q.tips('<font color = "red">'+"需求数量不为为订单数量，请重新排程！"+'</font>');
		 // XX订单XX行号数量未全部排程完，请继续填写！
		  megValid = rr.get('purchaseOrderNo')+$("sendscheduledetail.message.msg")+rr.get('rowIds')/10+$("sendscheduledetail.message.msg1");
		  Q.error(megValid,{renderTo:this.getView().id}); 
		  return false;;
	  }else{ 
    	  return true; 
	  }
  },
  	/**
	 * @method grid2DtlCellclick 细细单行点击事件
	 */
  	grid2DtlCellclick: function(grid,rowIndex,columnIndex,e ) {
	  	var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
    	var oldScheduleQty = 0;
      	if('edit' == vp.editWin.btnType){
      		//细单grid
      		var parentGrid = vp.editWin.getCompByTabClassName("sendScheduleCommons");
			var selected = parentGrid.getSelectionModel().getSelection()[0]; 
			//订单细单id
			var detailId = selected.get("purchaseOrderDetailId");
			var index = this.isContainValue(detailId);
			var gridStore = grid.getStore();
			if(index == -1){
			    // 设置可排程量初始值
//				var size=gridStore.getCount( ) ;
			    // 编辑界面取得编辑之前的总需求量
			    gridStore.each(function(v,i){
			    	if(!Ext.isEmpty(v.get('scheduleQty')) && Number(v.get('scheduleQty').toFixed(3)) > 0){ 
				        oldScheduleQty += Number(v.get('scheduleQty').toFixed(3));
				    }
			    });   
			    var item={"id":detailId,"value":oldScheduleQty};
			   	vp.editWin.oldValueArray.push(item) ;
			}
      	}
    },

	/**
	 * 判断是否已存在数据
	 */
    isContainValue:function(val){
 		var vp = this.getViewModel().getVp();
		for(var i=0;i<vp.editWin.oldValueArray.length;i++){
	 		if(vp.editWin.oldValueArray[i].id == val){
				return i;
			}
		}
		return -1;
	},

	/**
	 * @method vpImportHandler 列表界面导入按钮方法
	 */
	vpImportHandler: function(_self) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
		var vendorErpCode = form.findField('model.vendorErpCode').getValue();
		if(Ext.isEmpty(purchasingOrgCode) || Ext.isEmpty(vendorErpCode)){
			Q.tips($("message.pleaseSetNoBlankInfo"),"E");
				return;
		}
		var grid = _self.findParentByType(Ext.grid.GridPanel); //查找出所属的父Grid 
		var urlParams = "?purchasingOrgCode="+purchasingOrgCode+"&vendorErpCode="+vendorErpCode;
	    var store = grid.store;
	    var orderNo;
	    var rowNo;
	    var existParam = [];
	    for(var i=0; i<store.getCount(); i++){
		   orderNo = store.getAt(i).get("purchaseOrderNo");
		   rowNo = store.getAt(i).get("rowIds");
		   existParam.push(orderNo+";"+rowNo);
	    }
	    urlParams+="&existParam"+existParam;
		   
		
        var opt = {
            url:viewModel.get("dealUrl"), // + '/importexcel' + urlParams
            method: "/importexcel"+ urlParams,
            importSuccess:function(op, op1){//导入成功后回调
            	//var store = grid.getStore();
            	//store.add(op1.data);
            	Ext.each(op1.data,function(r,i){ 
 				   me.gridImportFormal(grid,r);
 			   	}); 
            	//store.reload();
            }
        };//配置项
        var renderTo = vp.editWin.id;//渲染载体
        //文件工具类调用
        Ext.UxFile.fileImport(opt, renderTo); 

	},

	/**
	 * @method updateOldValue
	 * @param {Object}
	 *            val 删除的数量
	 */
	updateOldValue: function(detailId,val) {
		var vp = this.getViewModel().getVp();
		var index = this.isContainValue(detailId);
		if(index != -1){
			vp.editWin.oldValueArray[index].value = val;
		}
	},
	/**
	 * 获取上一次的总数量
	 * 
	 * @method getOldValue
	 * @param {Object}
	 *            detailId id
	 */
	getOldValue: function(detailId) {
		var vp = this.getViewModel().getVp();
		var index = this.isContainValue(detailId);
		if(index != -1){
			return vp.editWin.oldValueArray[index].value;
		}
		return 0;
	},

	/**
	 * @method vpRowdblclick 列表双击事件
	 * @return {Boolean} 默认返回false
	 */
	vpRowdblclick: function() {
		return false;
	},

	/**
	 * @method setEditBtnShowHide 设置编辑按钮显隐
	 * @param {Integer}
	 *            id 单据id
	 * @param {Ext.Toolbar}
	 *            tbar 编辑界面顶部按钮对象
	 */
	setEditBtnShowHide: function(id, tbar) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var isShow = 'view' == vp.editWin.btnType;
		var tbar = vp.editWin.formPanel.getTopToolbar();
		var acceptBtn = tbar.find('name','accept')[0];
		var refuseBtn = tbar.find('name','refuse')[0];
		var sendScheduleState = vp.editWin.formPanel.form.findField('model.sendScheduleState').getValue();
		acceptBtn.hide();
		refuseBtn.hide();
		
		if(isShow && power.accept && sendScheduleState == 'RELEASE'){
			acceptBtn.show();
		}
		
		if(isShow && power.refuse && sendScheduleState == 'RELEASE'){
			refuseBtn.show();
		}
	},

	/**
	 * @method vpInstanceAfter 窗体实例化之后
	 */
	vpInstanceAfter: function() {   
			// 为明细添加选中时事件并且只注册一次
		    // 获取明细grid
			var me = this;
			var viewModel = me.getViewModel();
			var vp = viewModel.getVp();
			var editWin=vp.editWin;
			editWin.on("show",function() {
		    var me = this;
		    var viewModel = me.getViewModel();
		    var vp = viewModel.getVp();

		   var formPanel = vp.editWin.formPanel;
	    	var grid = vp.grid;
		   vp.editWin.oldValueArray = [];
    	   var form = vp.editWin.formPanel.getForm();
		    var store = viewModel.getStore('purchasingOrgStore');
		    if(store.getCount()==1){
			form.findField('model.purchasingOrgCode').setValue(store.getAt(0).get('purchasingOrgCode'));
			form.findField('model.purchasingOrgName').setValue(store.getAt(0).get('purchasingOrgName'));
		 }
	});
           var parentGrid = me.getDetailGrid();
           var gridSM = parentGrid.getSelectionModel();
           gridSM.on("selectionchange",function(sm){ 
  	    		var selected = sm.getSelection()[0];
  	    		// console.info(selected)
  	    		if(Ext.isEmpty(selected)){
  	    			return ;
  	    		} 
  	    		// 明细删除按钮是否可用
  	    		if(typeof(vp.editWin.changeFlag) == 'undefined'){
  	    		    return ;
  	    		}else if( vp.editWin.changeFlag === true && Ext.isEmpty(selected.get('sendScheduleCommonId'))){
  	    			 parentGrid.getTopToolbar().find("name","delete")[0].setDisabled(false); 
  	    		}else if( vp.editWin.changeFlag === true && !Ext.isEmpty(selected.get('sendScheduleCommonId'))){
  	    			 parentGrid.getTopToolbar().find("name","delete")[0].setDisabled(true); 
  	    		}
  	    		 
           }); 
           
			// 清除临时store
			vp.editWin.on('hide',function(){ 
				viewModel.getStore("tempDeleteStore").removeAll();
				 if(typeof(vp.editWin.changeFlag) != 'undefined'){ 
  				     delete vp.editWin.changeFlag;
				 }
			});
		
		  var me = this;
	    var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (vp.searchFlag && undefined != vp.searchWin) {
			var params = vp.searchWin.formPanel.form.getValues();
			params.initStates = vp.grid.store.proxy.extraParams.initStates;
			params.start = vp.grid.store.proxy.extraParams.start;
			params.limit = vp.grid.store.proxy.extraParams.limit;
			params.sort = vp.grid.store.proxy.extraParams.sort;
			params.dir = vp.grid.store.proxy.extraParams.dir;
			vp.grid.store.proxy.extraParams = params;
			vp.searchFlag = false;
			if(s_roleTypes.indexOf("V") > -1){
					me.get('searchFormItems')[8].items.splice(0,1);//去掉查询框的新建选项
				}
		}
	},

	/**
	 * @method companyStore 公司store加载事件
	 * @param {Ext.data.Store}
	 *            store 当前加载后的store对象
	 */
	companyStoreLoad: function(store) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (store.getCount() == 1 && !Ext.isEmpty(vp)) {
			var form = vp.editWin.formPanel.getForm();
			var field = form.findField('model.companyCode');
			field.setValue(store.getAt(0).get('companyCode'));
			field.fireEvent('select', field, store.getAt(0));
		}
	},

	/**
	 * @method plantStoreLoad 工厂store加载事件
	 * @param {Ext.data.Store}
	 *            store 当前加载后的store对象
	 */
	plantStoreLoad: function(store) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		if (store.getCount() == 1 && !Ext.isEmpty(vp)) {
			var record = me.getDetailGrid().getSelectionModel().getSelection()[0];
			var r = store.getAt(0);
			record.set('stockType', r.get('qualityCheck'));
			record.set('plantCode', r.get('plantCode'));
			record.set('plantName', r.get('plantName'));
			me.setMaterialLadderPrice(r.get('plantCode'));
		}
	},

	/**
	 * @method storLocStoreLoad 库存地点store加载事件
	 * @param {Ext.data.Store}
	 *            store 当前加载后的store对象
	 */
	storLocStoreLoad: function(store) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (store.getCount() == 1 && !Ext.isEmpty(vp)) {
			var record = me.getDetailGrid().getSelectionModel().getSelection()[0];
			var r = store.getAt(0);
			record.set('storeLocal', r.get('stockLocationCode'));
		}
	},

	

	/**
	 * @method editWinShow 编辑窗口初始化方法
	 */
	editAfter: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

		var formPanel = vp.editWin.formPanel;
		var grid = vp.grid;
		vp.editWin.oldValueArray = [];
    	var form = vp.editWin.formPanel.getForm();
		var store = viewModel.getStore('purchasingOrgStore');
		if(store.getCount()==1){
			form.findField('model.purchasingOrgCode').setValue(store.getAt(0).get('purchasingOrgCode'));
			form.findField('model.purchasingOrgName').setValue(store.getAt(0).get('purchasingOrgName'));
		}
	},

	/**
	 * @method formPurchasingOrgCodeSelect 表单采购组织选中事件
	 * @param {Ext.form.field.ComboBox}
	 *            combo 当前对象
	 * @param {Ext.data.Model}
	 *            record 所选中的记录
	 */
	formPurchasingOrgCodeSelect: function(combo, record) {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.purchasingOrgCode').setValue(record.get("purchasingOrgCode"));
		form.findField('model.purchasingOrgName').setValue(record.get("purchasingOrgName"));
		// 获取管控点
		this.getSendScheduleFlag();
  	},
     /**
	 * @method formPurchasingOrgCodeSelect 表单采购组织清除事件
	 * @param {Ext.form.field.ComboBox}
	 *            combo 当前对象
	 * @param {Ext.data.Model}
	 *            record 所选中的记录
	 */
  	formPurchasingOrgCodeClear: function(combo, record) {
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
		form.findField('model.purchasingOrgName').setValue("");
		},

	/**
	 * @method getSendScheduleFlag 获取管控点
	 */
	getSendScheduleFlag : function(){
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
		
		var param = {"model":{"purchasingOrgCode":purchasingOrgCode}};
    		
		// 获取采购组织管控点
		Ext.Ajax.request({
			url:path_srm + "/cp/sendschedule/getsendscheduleflag",
			method:"post",
			async:false,
			jsonData:param,
			success:function(result) {
				if('' != result.responseText){
					var form = vp.editWin.formPanel.getForm();
					var values = result.responseText.split(",");
					// 订单明细需全部排程
					form.findField('model.isFull').setValue(values[0]);
					// 排程允许选择多个订单
					form.findField('model.isMulti').setValue(values[1]);
				}
			},
			failure:function() {
			}
		});
	},
	
	/**
	 * @method setFormValueAfter 设置表单后事件
	 */
	setFormValueAfter: function() {
		// 设置表单后获取管控点
		this.getSendScheduleFlag();
		
		// 审核不过的编辑操作
		this.setEditBtnShowHide();  
    },

	/**
	 * @method formVendorErpCodeClear 表单供应商清空
	 */
	formVendorErpCodeClear: function() {
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
        var form = vp.editWin.formPanel.getForm();
        form.findField("model.vendorCode").reset();
        form.findField("model.vendorName").reset();  
    },

	/**
	 * @method showMaterialMasterPriceWin 显示价格主数据窗体
	 */
	showPurchaseOrderSelectWin: function() {
		var me = this;
		var viewModel = this.getViewModel();
		var vp = viewModel.getVp();
    	var globalValue;
		var grid = me.getDetailGrid();
		var vm = this.getView();
		var selectWin = Ext.create("Cp.schedule.PurchaseOrderDetailSelectWin",{
			title : $("purchaseOrder.title"),
			singleSelect : false, 
			gridOut:grid,
			renderTo:vm.id,
			select : function(g, selections) {   
                var store = grid.getStore();
                var rs = [];
                Q.each(selections, function(obj, all){
                	var index = store.find("purchaseOrderDetailId",obj.get('purchaseOrderDetailId'));
                	if(index<0){
                		var rowNo =store.getCount()+1; 
                		var data = {
                			rowNo:rowNo,
                			"purchaseOrderDetailId":obj.get('purchaseOrderDetailId'),// 订单明细id
                			purchaseOrderId:obj.get('purchaseOrder.purchaseOrderId'), // 订单Id
        					buyerId:obj.get('purchaseOrder.buyerId'),// 采购员id
        					buyerName:obj.get('purchaseOrder.buyer'),// 采购员名称
        					purchaseOrderNo:obj.get('purchaseOrder.purchaseOrderNo'),// 订单号
        					rowIds:obj.get('rowIds'),// 订单明细行号
        					vendorCode:obj.get('purchaseOrder.vendorErpCode'),// 供应商编码
        					vendorName:obj.get('purchaseOrder.vendorName'),// 供应商名称
        					materialId:obj.get('materialId'),// 物料ID
        					materialCode:obj.get('materialCode'),// 物料编码
        					materialName:obj.get('materialName'),// 物料名称
        					unitCode:obj.get('unitCode'),// 单位编码
        					unitName:obj.get('unitName'), // 单位名称
        					sendQty:obj.get('vendorQty'),// 订单数量
        					scheduleQty:obj.get('scheduledQty'),// 已排程
        					stockLocal:obj.get('storeLocal'),// 库存地点
        					lineItemTypeCode:obj.get('lineItemTypeCode'),// 行项目类别
        					canSendQty:Ext.util.Format.number( (obj.get('vendorQty')-obj.get('scheduledQty')),'0.000'),// 可排程量
        					factoryCode:obj.get('plantCode'),// 工厂编码
        					factoryName:obj.get('plantName'),// 工厂名称
        					erpPurchaseOrderNo:obj.get('purchaseOrder.erpPurchaseOrderNo'),// sap采购订单号
        				    vendorErpCode:obj.get('purchaseOrder.vendorErpCode'),// 供应商erp编码
        				    companyCode:obj.get('purchaseOrder.companyCode'),// 公司名称
        				    taxPrice:obj.get('buyerPrice'),// 为税价
        					vendorTime:obj.get('vendorTime'),// 交货日期
        					schdule:obj.get('scheduledQty'),// 隐藏已排程
        					unSchdule:Ext.util.Format.number( (obj.get('vendorQty')-obj.get('scheduledQty')),'0.000')// 隐藏可排程量
	                     }
                		
                		rs.push(data);
                	}
               }); 
               vp.editWin.addDetail(grid, rs);
			}
		},viewModel); 
		// 添加排程明细，已添加到排程明细列表的订单明细行不要再显示在选择列表中，
		// 删除订单明细行后，需要再选择列表中可以再次选择
		selectWin.gridPanel.getStore().on("load",function(store){
			 // 查看临时删除store中是否有记录有就加上去
		     // 取到临时的store
			viewModel.getStore("tempDeleteStore").each(function(v,i){
		    	 store.add(v);
		     }); 
		     var checkGridStore = grid.getStore();  
		     checkGridStore.each(function(v,i){
				 // 如果在排程明细中已经存在 则移除该记录 不然出现
				  var index = store.find('purchaseOrderDetailId',v.get('purchaseOrderDetailId'));
				  if(index > -1){
					  store.removeAt(index);
				  }
			 }); 
		});
		return selectWin;
	},

	/**
	 * @method grid1ImportHandler 排程明细导入按钮操作方法
	 */
	grid1ImportHandler: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var purchasingOrgCode = form.findField('model.purchasingOrgCode').getValue();
		var vendorCode = form.findField('model.vendorCode').getValue();
		if(Ext.isEmpty(purchasingOrgCode) || Ext.isEmpty(vendorCode)){
			Q.tips($("message.pleaseSetNoBlankInfo"),"E");
				return;
		}
		me.gridImport(grid);  
	},

	/*
	 * gridImport 导入物料 params {object} grid 编辑grid
	 */
  gridImport:function(grid){
	  	var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();

	   var form = vp.editWin.formPanel.getForm();
	   var importWin = vp.editWin.getImportWin(); 
	   importWin.show();
	   importWin.on("sumbitSuccess",function(form,action){  
		   var jsons = Ext.decode(action.result.info);  
		   if(!Ext.isEmpty(jsons) && jsons.length > 0){
			   Ext.each(jsons,function(r,i){ 
				   this.gridImportFormal(grid,r);
			   }); 
		   } 
	   });
	   importWin.on("sumbitBefore",function(params){
		   params["purchasingOrgCode"] = form.findField("model.purchasingOrgCode").getValue();
		   params["vendorCode"] = form.findField("model.vendorCode").getValue();
		   var store = grid.store;
		   var orderNo;
		   var rowNo;
		   var existParam = [];
		   for(var i=0;i<store.getCount();i++){
			   orderNo = store.getAt(i).get("purchaseOrderNo");
			   rowNo = store.getAt(i).get("rowIds");
			   existParam.push(orderNo+";"+rowNo);
		   }
		   params["existParam"] = existParam;
		   
	   });
	   importWin.on("sumbitFailure",function(form,action){
	   });
  },
  /*
	 * gridImportFormal 导入物料
	 */
  gridImportFormal:function(grid,r){
		 var vp = this.getViewModel().getVp();
		 var store = grid.getStore();
		 scheduleTimeStr = r.scheduleTime.replace(/-/g,"/");
		 var scheduleTime = new Date(scheduleTimeStr);  
		 var record = {
			  "purchaseOrderDetailId":r.purchaseOrderDetailId,
			  "purchaseOrderNo":r.purchaseOrderNo,
	 		  "rowIds":r.rowIds,
	 		  "materialCode":r.materialCode,
	 		  "materialName":r.materialName,
	 		  "unitCode":r.unitCode,
	 		  "unitName":r.unitName,
	 		  "sendQty":r.sendQty,
	 		  "vendorTime":r.sendScheduleCommon.vendorTime,
	 		  "scheduleQty":r.sendScheduleCommon.scheduleQty,
	 		  "canSendQty":r.sendScheduleCommon.canSendQty,
	 		  "factoryCode":r.plantCode,
	 		  "stockLocal":r.stockLocal,
	 		  "lineItemTypeCode":r.lineItemTypeCode,// 行项目类别
	 		  "factoryName":r.factoryName,// 工厂名称
			  "vendorCode":r.sendScheduleCommon.vendorCode,// 供应商编码
			  "vendorErpCode":r.sendScheduleCommon.vendorErpCode,// 供应商编码
			  "vendorName":r.sendScheduleCommon.vendorName,
			  "taxPrice":r.taxPrice,// 为税价
			  "companyCode":r.sendScheduleCommon.companyCode,
			  "erpPurchaseOrderNo":r.erpPurchaseOrderNo
			 
		 };  
		 vp.editWin.addDetail(grid,record); 
		 var store = grid.getStore();
		 grid.getSelectionModel().select(store.getCount()-1);
		 var parentGrid = vp.editWin.getCompByTabClassName("sendScheduleDetails"); 
		 vp.editWin.addChildDetail(parentGrid,{"scheduleTime":scheduleTime,"scheduleQty":r.scheduleQty,"sendFlag":0,
			 "deleteFlag":1,"vendorCode":r.vendorCode, "vendorName":r.vendorName, "erpPurchaseOrderNo":r.erpPurchaseOrderNo});
	  },
	/**
	 * @method grid1DownloadHandler 排程明细下载模版
	 */
	grid1DownloadHandler: function() {
		//  window.open(this.getViewModel().get('dealUrl') + "/download?template=");
		Ext.UxFile.fileDown(this.getViewModel().get('dealUrl') + "/download", "送货排程.xls", null);
    },

	/**
	 * @method editWinHide 编辑窗体隐藏事件
	 */
	editWinHide: function() {
		var vp = this.getViewModel().getVp();
		delete vp.editWin.oldValueArray;
	},


	
    /**
	 * @method gridEditScheduleQtyRenderer 细细单需求数量渲染
	 * @param {Object}
	 *            v 当前列值
	 * @param {Object}
	 *            m 当前单元格元数据
	 * @param {Ext.data.Model}
	 *            r 当前行记录
	 * @return {String} 要呈现的HTML字符串
	 */
    gridEditScheduleQtyRenderer: function(v,m,r) {
    	var me = this;
	    var viewModel = me.getViewModel();
	    var vp = viewModel.getVp();
	    if (!vp.editWin.hidden) {
			vp.editWin.setBgColor(m,r,this.dataIndex,this.rendererColor);
		}
        if(v != r.get("oldScheduleQty") || r.get("scheduleTime")!=r.get("oldScheduleTime")){
        	if(v == undefined){
        		return null;
        	}
        	return "<font color='red'>"+Ext.util.Format.number(v,'0.000')+"</font>"; 
        }
        return Ext.util.Format.number(v,'0.000'); 
    },
    /**
	 * @method gridEditSendFlagRenderer 细细单需求数量渲染
	 * @param {Object}
	 *            v 当前列值
	 * @param {Object}
	 *            m 当前单元格元数据
	 * @param {Ext.data.Model}
	 *            r 当前行记录
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
		 if(r.get("scheduleQty") != r.get("oldScheduleQty") ||r.get("scheduleTime")!=r.get("oldScheduleTime")){
			 return "<font color='red'>"+text+"</font>"
		 }else{
			 return text;
		 }
    },

	

	/**
	 * @method getDetailGrid 获取编辑界面排程明细列表方法
	 * @return {Ext.grid.Panel} detailGrid
	 */
	getDetailGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('sendScheduleCommons');
	},
	/**
	 * @method getDtlDtlGrid 获取编辑界面排程细细单列表方法
	 * @return {Ext.grid.Panel} detailGrid
	 */
	getDtlDtlGrid: function() {
		var vp = this.getViewModel().getVp();
		return vp.editWin.getCompByTabClassName('sendScheduleDetails');
	},
	/**
	 * @method editBtnToAcceptFn 接受方法
	 */
	editBtnToAcceptFn: function() {
		var vp = this.getViewModel().getVp();
		var grid = vp.grid;
		var form = vp.editWin.formPanel.getForm();
    	var selectids = grid.getSelectionModel().getSelection();
    	var id;
    	if(selectids[0]){
    		if(selectids.length != 1){// 请选择+grid.moduleName
    			Q.tips($("technicalquality.message.warnMsg4"),"E");
                return;
            }
    		id = selectids[0].get('sendScheduleId');
    	}else{
    		id = form.findField('model.sendScheduleId').getValue();
    	}
		 
		Q.confirm($("message.accept.confirm"), {
         ok: function(){
        	 Ext.getBody().mask($("pleaseWait"));
             Ext.Ajax.request({
                 url: path_srm+"/cp/sendschedule/accept",
                 params:{"id": id},
                 success: function(response){
                     var json = Ext.decode(response.responseText);
						if(false === json.success){// grid.moduleName+"删除失败！未知系统异常！
							Q.error(json.info || $("message.accept.failure")+"<br/><br/>"+$("message.system.error"));
							return;
                     }
                     Q.tips($("message.accept.success"));
                     vp.editWin.resetWin();
                     vp.editWin.hide(); 
                     vp.editWin.fireEvent("submit");
                     
                 },
					failure: function(response){// grid.moduleName+"删除失败！请检查与服务器的连接是否正常，或稍候再试！
						Q.error($("message.accept.failure")+"<br/><br/>"+$("message.system.disconnect"));
                 },
                 callback: function(){
                     Ext.getBody().unmask();
                 }
             });
         }
     });
	},
	/**
	 * @method editBtnToRefuseFn 拒绝方法
	 */
	editBtnToRefuseFn: function() {
		var vp = this.getViewModel().getVp();
	    var grid = vp.grid;
	    var formPanel = vp.editWin.formPanel.getForm();
	    var selectids = grid.getSelectionModel().getSelection();
	    var id
	    if(selectids[0]){
    		if(selectids.length != 1){// 请选择+grid.moduleName
    			Q.tips($("technicalquality.message.warnMsg4"),"E");
                return;
            }
    		id = selectids[0].get('sendScheduleId');
    	}else{
    		id = formPanel.findField('model.sendScheduleId').getValue();
    	}
        var form = new Ext.form.FormPanel({
	        labelAlign: 'right',
	        labelWidth: 10,
	        defaultType: 'textfield',
	        bodyStyle : "padding:20px 30px 0px",
	        items: [{
		        	fieldLabel: '',
		        	name:"remark",
		        	width:440,
		        	height: 150,
		        	xtype:'textarea',
		        	maxLength:500,
		        	allowblank:false
		        }]
	    });
		var win = new Ext.Window({
			title:$("contractAgreement.rejectReason")+'<font color="red">*</font>',
		    layout: 'fit',
		    width: 500,
		    autoScroll:true,
		    height: 300,
	        closeAction: 'hide',
	        resizable : false,
	        items: [form],
	        buttons: [{
	        	// 取消
	            text:$("button.cancel"),
	            margin : "20 30 20 0",
				style : "width:80px;height:34px;",
				ui:'gray-btn',
	            handler:function(){
            		win.close();
	            }
	      },{
	            text:$("message.confirm"),
	            ui:'blue-btn',
				margin : "20 30 20 0",
				style : "width:80px;height:34px;",
	            handler:function(){
	            	var remark = form.getForm().findField("remark").getValue();
	            	if (!form.isValid()) {
	            		Q.tips($("sendschedule.message.warnMsg7"),"E");
                        return false;
	        		}
//	            	if (Ext.isEmpty(remark)) {
//	            		// 请填写拒绝原因！
//	            		Q.tips($("sendschedule.message.warnMsg6"),"E");
//	                            return false;
//	            	}
	            	Ext.getBody().mask($("pleaseWait"));
	            	Ext.Ajax.request({
                        	url: path_srm+"/cp/sendschedule/refuse",
                        method : 'POST',
                        params : {
                            id : id,
                            refuseReason:remark
                        },
                        success: function(response){
                            var json = Ext.decode(response.responseText);
                        	if(false === json.success){// grid.moduleName+"删除失败！未知系统异常！
                        		Q.error(json.info || $("message.refuse.failure")+"<br/><br/>"+$("message.system.error"));
                                 return;
                             } 
                        	 win.close();
                             Q.tips($("message.refuse.success"));
                             vp.editWin.resetWin();
                             vp.editWin.hide(); 
                             vp.editWin.fireEvent("submit");
                        },
                        failure: function(response){// grid.moduleName+"删除失败！请检查与服务器的连接是否正常，或稍候再试！
                        	Q.error($("message.refuse.failure")+"<br/><br/>"+$("message.system.disconnect"));
                        },
                        callback: function(){
                             Ext.getBody().unmask();
                        }
                    });
	              }
	          }],
		      listeners:{
		        	"show":function(){
		        	}
		      }
		  });	
		  win.show(); 
	},
	/**
	 * @method searchWinSearch
	 * 送货排程查询窗体查询事件
	 */
	searchWinSearch: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		vp.searchFlag = true;
	},
	
	/**
	 * @method gridStoreLoad
	 * 送货排程列表加载事件
	 */
	vpAfterRender: function() {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (undefined != vp.searchWin) {
			vp.searchWin.formPanel.form.reset();
		}
		return 'view';
	},
	/**
	 * @method gridStoreLoad
	 * 采购组织加载事件
	 */
	purchasingOrgStoreLoad: function(store) {
		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		if (store.getCount() == 1) {
			var form = vp.editWin.formPanel.getForm();
			var field = form.findField('model.purchasingOrgCode');
			var record = store.getAt(0);
			field.setValue(record.get('purchasingOrgCode'));
			field.fireEvent('select', field, record);
			field.fireEvent('change', field, field.getValue());
		}
	},
	/**
	 * @method editBtnToChangeFn 强制变更方法
	 */
	editBtnToChangeFn: function(e) { 
		var me = this;
		var vp = me.getViewModel().getVp();
		var cfg = me.getViewModel().getCfg();
	    var grid = vp.grid;
	    var selectids = grid.getSelectionModel().getSelection();
		 if(selectids.length<=0){// 请选择+grid.moduleName
			Q.tips($("message.pleaseSelect"),"E");
         return;
		 }else if(selectids.length > 1){// 同时只能编辑一条信息！
			Q.tips($("message.onlySelect"),"E");
         return;
     }
	 var win = grid.getEditWin(cfg);
	 win.editFlag = true;
	 vp.editWin = win; // 把编辑窗口变成vp 的内置属性，提供给配置文件使用
	 // 设置临时内置属性
	 vp.editWin.changeFlag = true;
	 vp.editWin.oldValueArray = [];
	 win.setFormValue(selectids[0],'edit',null,null,null,true); 
	 win.show();
	 var tools = win.formPanel.getTopToolbar().items;  
     tools.each(function(item){  
    	 if(item.name=="save"){
			 item.hide();
		 }
     });  
     
     // 1) 允许再次添加采购订单明细，不允许删除订单明细；
     // 获取明细grid
     var parentGrid = win.getCompByTabClassName("sendScheduleCommons");
 	 parentGrid.getTopToolbar().find("name","delete")[0].setDisabled(true); 
		  
	 win.url = win.dealUrl+"/change?";   
	 
	},
 	/**
	 * @method editBtnToCancelFn 取消方法
	 */
	editBtnToCancelFn: function() {  
		var vp = this.getViewModel().getVp();
	    var grid = vp.grid;
    	var selectids = grid.getSelectionModel().getSelection();
			if(selectids.length == 0){// 请选择+grid.moduleName
				Q.tips($("message.pleaseSelect"),"E");
            return;
        }else if(selectids.length > 1){// 请选择+grid.moduleName
			    Q.tips($("technicalquality.message.warnMsg4"),"E");
            return;
        } 
        var id = selectids[0].get('sendScheduleId'); 

			// 确定取消+grid.moduleName+"?";
			Q.confirm($("message.cancel.confirm"), {
             ok: function(){
                 Ext.getBody().mask($("pleaseWait"));
                 Ext.Ajax.request({
                     url: path_srm+"/cp/sendschedule/cancel",
                     params:{"id": id},
                     success: function(response){
                        var json = Ext.decode(response.responseText);
							if(false === json.success){// grid.moduleName+"删除失败！未知系统异常！
								Q.error(json.info || $("message.cancel.failure")+"<br/><br/>"+$("message.system.error"));
                             return;
                         }
                         Q.tips($("message.cancel.success"));
                         grid.getStore().reload();  
                         grid.getSelectionModel().clearSelections();
                     },
					 failure: function(response){// grid.moduleName+"删除失败！请检查与服务器的连接是否正常，或稍候再试！
							Q.error($("message.cancel.failure")+"<br/><br/>"+$("message.system.disconnect"));
                     },
                     callback: function(){
                         Ext.getBody().unmask();
                     }
                 });
             }
         }); 
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
						id : selected[0].get("sendScheduleId")
					},
					success : function(response) {
						var json = Ext.decode(response.responseText), flag = json.success;
						if(json.data == "ok"){
							Q.tips($("message.operate.success") + "!");
							grid.getStore().reload();
						}else if(json.info == "notOperation"){// 第一个节点已审批，无法撤销审核！
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
 	 * @method dealState
 	 * @param {Object} self 当前点击对象
 	 * 状态处理方法
 	 
 	dealState: function(self) {
 		var me = this;
 		var viewModel = me.getViewModel();
 		var vp = viewModel.getVp();
 		var name = self.name;
 		var text = self.text;
 		var id = null;

		if (!vp.editWin.hidden) {
			id = vp.editWin.formPanel.form.findField('model.sendScheduleId').getValue();
		}
 		
		if ('TOPASS' == name) {
			if(vp.editWin.hidden){
				vp.dealstate(name, text, true, false, vp.id, false);
			}else{
				vp.dealstate(name, text, true, false, vp.editWin.id, true,id);
			}
		} else if ('TONOPASS' == name) {
			if(vp.editWin.hidden){
				vp.dealstate(name, text, true, true, vp.id, false);
			}else{
				vp.dealstate(name, text, true, true, vp.editWin.id, true, id);
			}
		}
 	}*/
  /**
 	 * @method dealState
 	 * @param {Object} self 当前点击对象
 	 * 供应商下拉选择事件*/
	vendorErpCodeTriggerselect:function(r, v,selected) {
		try{
		var vp = this.getViewModel().getVp();
		var form = vp.editWin.formPanel.getForm();
        form.findField('model.vendorCode').setValue(selected.get("vendorErpCode"));
		}catch(e){
		console.log(e);}
  	},
  	 /**
 	 * @method dealState
 	 * @param {Object} self 当前点击对象
 	 * 供应商加载事件*/
  	vendorErpCodeTriggerBaseParams : function(_self, baseParams, parentObj) {
		try{
			var viewModel = this.getViewModel()
			var form = viewModel.getEditWin().formPanel.getForm();
			var purchasingOrgCode = form.findField("model.purchasingOrgCode").getValue();// 采购组织
			baseParams.purchasingOrgCode = purchasingOrgCode;
			baseParams.filter_IN_certificationStatus = "QUALIFIED";
			baseParams.filter_EQ_stopFlag = 0;
			baseParams.filter_EQ_erpSynState = 1;
		} catch(e) {
            console.log(e);
        }
	},
	vendorErpCodeSetValueAfter:function(_self,fieldselect,parentObj,grid,parentType){
 		var me = this;
		var viewModel = me.getViewModel();
		var vp = viewModel.getVp();
		var form = vp.editWin.formPanel.getForm();
		var vendorCode = _self.value;
		form.findField('model.vendorCode').setValue(vendorCode);
	}

});