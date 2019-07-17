/**
 *@class {Ext.srm.app.ViewController}
 *@extend {Ext.ux.app.ViewController}
 *srm公共控制层
 */
Ext.define('Ext.srm.app.ViewController', {
    extend: 'Ext.ux.app.ViewController', 
    alias: 'controller.srmViewController',   
    requires:["Ext.srm.ux.UxFileUtils","Ext.srm.dictionary.YNCombo","Ext.srm.dictionary.TFCombo"],
    /**
     * 公共提交方法
     * @param {String} dealUrl 处理方法url
     * @param {Object} [params] 处理参数
     * @param {String} renderToId 渲染位置
     * @param {Object} otherOpt 其他配置项
     *     {
     *        btnName: , 按钮名称用于国际化 eg.$("message."+otherOpt.btnName+".failure")
     *        grid:  提交的gridPanel
     *     }
     * @param {Object} [fnObj] 回调处理方法配置项 
     *     {
     *        successFn: Fn, 
     *        failureFn:Fn, 
     *        callbackFn:Fn  
     *     }
     * @param {Object} [options]  request 配置 
     */
    commAjax: function (dealUrl,params,renderToId,otherOpt,fnObj,options) { 
    	var me = this;
    	var viewModel = me.getViewModel(); 
    	var params = params || {};
    	var otherOpt = otherOpt || {}; 
        var fnObj = fnObj || {};
        var options = options || {};
        Ext.applyIf(fnObj,{
            'successFn':function(){},
           	'failureFn':function(){},
           	'callbackFn':function(){}
        }); 
        Ext.applyIf(options,{
            url: viewModel.get('dealUrl') + dealUrl,
            params: Ext.encode(params) == '{}'? params:Q.parseParams(params),
            success: function(response){
                var json = Ext.decode(response.responseText); 
                if(Ext.isFunction(fnObj['successFn'])){
                	fnObj['successFn'](response);
                }
				if(false === json.success){//grid.moduleName+"删除失败！未知系统异常！
					Q.error(json.info ||$("message.comm.failure").replace("{1}",otherOpt.btnName)+"<br/><br/>"+$("message.system.error"), { renderTo: renderToId });
                    return;
                } 
                if(otherOpt.grid){
                	otherOpt.grid.getStore().reload();
                } 
                Q.tips($("message.comm.success").replace("{1}",otherOpt.btnName));  
            },
			failure: function(response){//grid.moduleName+"删除失败！请检查与服务器的连接是否正常，或稍候再试！
				if(Ext.isFunction(fnObj['failureFn'])){
                	fnObj['failureFn'](response);
                }
				Q.error($("message.comm.failure").replace("{1}",otherOpt.btnName)+"<br/><br/>"+$("message.system.disconnect"), { renderTo:renderToId });
            },
            callback: function(){  
                Ext.getCmp(renderToId).unmask();
                if(Ext.isFunction(fnObj['callbackFn'])){
                	fnObj['callbackFn']();
                }
            }
        }); 
        //确认xx选中的记录吗?
	    Q.confirmMsg($("message.comm.confirm").replace('{1}',otherOpt.btnName), {
			renderTo:renderToId, 
			emptyText:$("confirmMsg.emptyText").replace('{1}',otherOpt.btnName),
            ok: function(btn,message){ 
		        Ext.getCmp(renderToId).mask($('message.ajax.request'));//"数据处理中........"
		        options.params['message'] = message;
			    Ext.Ajax.request(options); 
            }
	    }); 
    },
    /**
     * 列表单据状态渲染方法
     * @param {String} v 单据状态值
     */ 
    rendererStatus:function (v) {
    	var me = this;
    	try{
	    	var viewModel = me.getViewModel();    
	        if (!Ext.isEmpty(v) && !Ext.isEmpty(viewModel.get("billStateObj")[v])) {
	    	    return me.rendererCodeColor(v,viewModel.get("billStateObj")[v].name);
	        }
	        return v;
    	}catch(e){
 			if(!Ext.isIE){
 				console.error(e);
 			}   	
     	}
    },  
    
    /**
     * 列表日期类型字段渲染方法 
     * @return format "Y-m-d H:i:s"
     */ 
    rendererDateTime:Ext.util.Format.dateRenderer("Y-m-d H:i:s"),
     /**
     * 列表日期类型字段渲染方法 
     * @return format "Y-m-d"
     */ 
    rendererDate:Ext.util.Format.dateRenderer("Y-m-d"),
    /**
     * 列表数值类型字段渲染方法 
     * @return format  "0,000.000"
     */ 
    rendererNumber: function (value, m, r) {//日期加背景处理记得看本质 
        return Ext.util.Format.number(value, "0,000.000");
    },
    /**
	 * 数字类型渲染保留两位小数
	 * */
    rendererNum2Fn:function(v, m, r) {
		return Ext.util.Format.number(v, '0.00');
	},
	/**
	 * 数字类型渲染保留四位小数
	 * */
    rendererNum4Fn:function(v, m, r) {
		return Ext.util.Format.number(v, '0.0000');
	},
    /**
     * 同步状态渲染处理 
     * @return {String} 处理后渲染字符
     */
    renderSynStatus: function(value, metaData, record, rowIndex, colIndex, store, view, self){
    	try{
	    	var me = this; 
	    	return me.renderSynColor(value);
    	}catch(e){
    	   if(!Ext.isIE){
 				console.error(e);
 			}  
    	}
    }, 
    /**
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
    renderSynColor : function(code,name){    
    	var me = this;
    	return me.rendererCodeColor(code,name); 
	}, 
	/**
	 * 悬浮提示
	 * @param {String} value 当前值
	 * @param {Object} meta 配置
	 * @param {Object} record 记录
	 */
	rendererDataqtip:function(value, meta, record){
		try{  
			if(!Ext.isEmpty(value)){
	    		meta.tdAttr = "data-qtip='" + (value+"").replace(/\'/g,"&apos;") + "'";
			}
		}catch(e){
    		if(!Ext.isIE){
    			console.log(e);
    		}
    	}
    },
    /**
     * xtype:'uxcombo' 渲染处理
     */
    rendererUxCombo: function(value, metaData, record, rowIndex, colIndex, store, view, self){
    	try{  
	    	var valueField,displayField,editor;  
	    	  editor = self.getEditor(); 
			if(editor){  
	    		var opt = self.getEditor();
	    		if(opt.valueField){
	    	    	valueField = opt.valueField;
	    		} 
	    		if(opt.displayField){
	    	    	displayField = opt.displayField;
	    		}  
			}else if(self.config.editor){  
	    		var opt = self.config.editor;
	    		if(opt.valueField){
	    	    	valueField = opt.valueField;
	    		} 
	    		if(opt.displayField){
	    	    	displayField = opt.displayField;
	    		}  
			}  
	    	if(displayField && valueField){  
	    		var store = null;
	    		if(editor){
	    			store = editor.store; 
	    		}
	    		if(Ext.isEmpty(store)){ 
	    			var storeId = self.config.editor.bind.store.substr(self.config.editor.bind.store.indexOf("{") + 1,self.config.editor.bind.store.indexOf("}") - 1);
	    		    store = view.ownerCt.up("commModelEditWin").getViewModel().getStore(storeId);
	    		}
	    		var record = store.findRecord(valueField,value);
	    		if(record){
	    			return record.get(displayField);
	    		}  
	    	}else{
	    		if(!Ext.isIE){
	    			console.info("displayField or valueField not configure");
	    		}
	    	} 
	    	return value;
    	}catch(e){
    		if(!Ext.isIE){
    			console.log(e);
    		}
    	}
    },
    /**
     *  列表通过value 渲染 name
     */
    renderByStoreName: function(value, metaData, record, rowIndex, colIndex, store, view, self){ 
    	try{  
	    	var valueField,displayField;
	    	var customAttr = self.customAttr || {}; 
	    	Ext.applyIf(customAttr,{
	    		displayField:"itemName",
	    		valueField:"itemCode"
	    	});
	    	displayField = customAttr.displayField;
	    	valueField = customAttr.valueField; 
	    	if(displayField && valueField){  
	    		if(customAttr.storeId){  
					var store = view.ownerCt.getViewModel().getStore(customAttr.storeId);
					if(!store){
						store = Ext.getStore(customAttr.storeId);
					}
					if(store){
						var record = store.findRecord(valueField,value);
						if(record){
							return record.get(displayField);
						}
					} 
				}
	    	}else{
	    		if(!Ext.isIE){
	    			console.info("displayField or valueField not configure");
	    		}
	    	}  
	    	return value;
    	}catch(e){
    		if(!Ext.isIE){
    			console.log(e);
    		}
    	}
    },
    /**
	 * @method rendererNo
	 * 单号渲染
	 * @param {Object} value 当前列值
	 * @param {Object} metaData 当前单元格元数据
	 * @return {String} 要呈现的HTML字符串
	 */
    rendererNo:function(value, metaData) {
		try{  
			metaData.attr = "style='cursor:pointer;'"; // 给当前td添加样式
			if(!Ext.isEmpty(value) && "null" != value){ 
			   return "<u style='color:blue'>" + value + "</u>"; 
			}else{
			   return "";
			}
		}catch(e){
    		if(!Ext.isIE){
    			console.log(e);
    		}
    	}
	},
	/**
	 * @method rendererCodeColor
	 * 编码渲染颜色公共方法 
	 * @param code 编码
	 * @param name 显示名称 不必传现  不传默认渲染 code
	 * @return {string} 
	 *  SYNCHRONIZEDNOT("未同步"),//0
	 *  SYNCHRONIZING("同步中"),//1
	 *  SYNSUCCESS("已同步"),//2
	 *  SYNFAILED("同步失败"),//3
	 *  SYNNONEED("不同步");//4 
	 */
	rendererCodeColor: function(code,name){    
		 try{ 
			if(['SYNCHRONIZEDNOT','CANCEL'].indexOf(code) != -1 || code == 0){
				return "<font color='#bbbbbb'>" + (name || $('erpSyn.nosyn')) + "</font>";// 灰色
			}else if(['SYNCHRONIZING','TOCONFIRM'].indexOf(code) != -1 || code == 1){
				return "<font color='#ff8800'>" + (name || $('erpSyn.onsyn')) + "</font>";// 蓝色
			}else if(code == 'SYNSUCCESS' || code == 2){
				return "<font color='#444444'>" + (name || $('erpSyn.synsuccess')) + "</font>";// 绿色
			}else if(code == 'SYNFAILED' || code == 3){
				return "<font color='#EF5F5F'>" + (name || $('erpSyn.synfail')) + "</font>";// 红色
			}else if(code == 'SYNNONEED' || code == 4){
				return "<font color='#444444'>" + (name || $('erpSyn.noneed')) + "</font>";// 红色
			}else if(["TONOPASS"].indexOf(code) != -1){
			    return "<font color='#ee4444'>" + ( name || code ) + "</font>"; 
			}else{
				return "<font color='#444444'>" + ( name || code ) + "</font>"; 
			} 
		}catch(e){
    		console.log(e);
    	}
	}, 
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
			     fileName: grid.cfg.moduleName+'.xlsx'
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
     * 列表渲染后方法
     * @return {String}
     */
    vpAfterRender: function(){
    	return "view";
    },
    /**
     * 导出数据前处理
     * @param {} a
     */
    onBeforeDocumentSave : function(a) {
		a.mask($("data.export.message"));
	},
	/**
     * 导出数据后处理
     * @param {} a
     */
	onDocumentSave : function(a) {
		a.unmask();
	}
});
