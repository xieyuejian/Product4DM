Ext.define('Ext.srm.ux.SrmPluploadButton', {
	extend : 'Ext.button.Button',
	alias : ['widget.srmpluploadbutton'],
    xtype: "srmpluploadbutton",
    ui: 'form-attach-btn', 
	uploader:null,
	extens : ["bmp", "jpeg", "gif", "png", "jpg"], 
	parentObj:null,
	/**
     * 判断是否是grid类型组件
     * @return {Boolean} true 是 false 不是
     */
    isGrid:function(){
        var me = this;
        var gridXtypes = ["grid","uxgrid","uxgridpanel","uxeditorgrid","gridpanel"];
        if(gridXtypes.indexOf(me.parentXtype) != -1){ 
        	return true;
        }else{
        	return false;
        }
    },
	/**
	 * 获取父类对象
	 */
	getParent:function(){
		try{
	    	var me = this;
	    	var parentObj = null;   
	        //如果由外部传进来则对该对象进行操作
	    	if(!me.parentObj){ 
	    	    me.parentObj = me.findParentByType(Ext.form.Panel);//父类是表单
	    		if(!me.parentObj){ 
	    	       me.parentObj = me.findParentByType(Ext.grid.Panel);//父类是列表 
	    		}
	    	}
	    	//判断操作对象类型
	    	if(me.parentObj){
	    	   me.parentXtype = me.parentObj.getXType();//获取对象类型
	    	}   
	    }catch(e){
		 	console.log(e);
	    }
	},
	constructor : function(config) {
		var me = this;   
		if(Ext.isEmpty(config.iconCls)){
			config = Ext.apply({
	            ui: 'form-attach-btn'
	        }, config); 
		}else{
			config = Ext.apply({
	            ui: 'list-tbar-btn'
	        }, config); 
		}  
	    me.callParent([config]); // 调用你模块的initComponent函数
	    me.getParent();
	    if(Ext.isEmpty(config.fileGroupIdField)){
	    	console.log("未维护对应的fileGroupIdField配置项");
	    	return false;
	    }
	    if(Ext.isEmpty(config.fileViewField)){
	    	console.log("未维护对应的fileViewField配置项,用于渲染附件信息。");
	    	return false;
	    }
		var uploadFile = config.fileGroupIdField; 
		var renderField = config.fileViewField;
		var moduleCode = "undefined" == typeof config.moduleCode ? "" : config.moduleCode;
		var billKey = "undefined" == typeof config.billKey ? "" : config.billKey;
		me.on('render', function(btn, eOpts) { 
			me.uploader = Ext.create('Ext.ux.Plupload', {
			     browseButton : me,
				 pluploadConfig : eOpts.pluploadConfig
		    });
		    me.uploader.uploader.disableBrowse(true); 
		}, me, {
			pluploadConfig : {
                url: path_srm + '/fs/file/upload',
                filters:config.filters,
                headers:{
                	"Authorization": window.localStorage.getItem("token"),
                	"clientId": "pc",
                	"target":target
                },
                multi_selection : (typeof config.multi_selection == "undefined" || Ext.isEmpty(config.multi_selection))? true : config.multi_selection,
                init: {
                   Error:function(_self,errorCode){
                   		try{ 
                   			if(errorCode && errorCode.code == -600){ //附件超出长度
                   				var fileName = errorCode.file.name;
                   				var max_file_size = _self._options.filters.max_file_size;
                   				Q.tips(fileName + $("plupload.outMaxFileSize").replace("{0}",max_file_size),"E"); 
                   			}
                   			
                   			if(errorCode && errorCode.code == -601){ //扩展名
                   				if(!Ext.isEmpty(config.errorMessage)){
                   					Q.tips(config.errorMessage,"E");
                   				}else{
	                   				var fileName = errorCode.file.name;
	                   				var a = fileName.split(".")
	                   				Q.tips(fileName + $("plupload.extension").replace("{0}",a[a.length-1]),"E"); 
                   				}
                   			} 
                   		}catch(e){
                   			console.log(e); 
                   		} 
                   },
                   FilesAdded:function(up,files){
	                   try{  
		                   me.uploadFileLength = files.length;
		                   me.uploadedlength = 0;  	
		                   //var editWin = me.findParentByType(Ext.comm.CommModelEditWin);
		                   me.Z = Ext.create("Ext.window.MessageBox");
					       me.Z.wait($("message.submit.wait"), $("message.submit.data"), {
					       		renderTo: me.parentObj.id
					       });
			           }catch(e){
                   		   console.log(e); 
                   	   } 
                   },	                   
                   QueueChanged: function (up) {
                   	   try{ // 设置每个文件对应的record
					      up.start();	
	                   }catch(e){
	               	      console.log(e); 
	               	   } 
                   },
                   BeforeUpload:function(up,files){  
                   	   try{ // 判断附件显示 
	                   	  if(!Ext.isEmpty(config.beforeHandleFn)){
	                   	 	 me.beforeHandleFn(up);
	                   	  }else{
	                   	     var parentObj = me.parentObj;
		                   	 var editWin = me.findParentByType(Ext.comm.CommModelEditWin); 
		                   	 var billTypeCode = "";
		                   	 if("undefined" != typeof editWin && editWin.vpWin){
		                   	 	billTypeCode = editWin.vpWin.billTypeCode;
		                   	 }
		                   	 if(billTypeCode){
		                   	 	billTypeCode = editWin.billTypeCode;
		                   	 }
		                   	 if(me.isGrid()){
							 	 var selected = parentObj.getSelection()[0];
								 var uploadFileGroupId = selected.get(uploadFile);
								 var params = {"fileGroupId":Ext.isEmpty(uploadFileGroupId)?"":uploadFileGroupId,"billTypeCode":billTypeCode,extraParams:config.extraParams};
								 me.fireEvent("beforeupload",me, params); 
								 up.setOption("multipart_params",params);	
		                   	 }else{
			                   	 var form = parentObj.getForm();
			                   	 var uploadFileGroupId = form.findField(uploadFile).getValue();
			                   	 var params = {
	                   	 			   "fileGroupId": Ext.isEmpty(uploadFileGroupId) ? "" : uploadFileGroupId,
	                   	 			   "billTypeCode": billTypeCode,
	                   	 			    extraParams: config.extraParams,
	                   	 			   "moduleCode": moduleCode,
	                   	 			   "billKey": billKey
			                   	 };
								 me.fireEvent("beforeupload",me, params); 
								 up.setOption("multipart_params",params);	
		                   	 }
	                   	  }
                   	   }catch(e){
               			  console.log(e); 
               		   } 
                   },
                   FileUploaded: function (up, files, response) {
                      try{
                   		  me.uploadedlength = me.uploadedlength+1;
                   		  if(me.uploadedlength == me.uploadFileLength){
                   			  me.Z.hide();
               			      if(!Ext.isEmpty(config.callbackFn)){
               			  	 	 me.callbackFn();
               			  	  }
                   		  }
	                   	  if(!Ext.isEmpty(config.handleFn)){
	                   	 	  var json = Ext.JSON.decode(response.response);
	                   	 	  me.handleFn(json);
	                   	  }else{
	                   		  var parentObj = me.parentObj;  
		                      var json = Ext.JSON.decode(response.response);
		                      if(json.success){
		                          var data = json.fileInfo;
		                      	  if(me.isGrid()){ 
								     var selected = parentObj.getSelection()[0]; 
								     if(Ext.isEmpty(selected.get(uploadFile))){
								        selected.set(uploadFile,json.fileInfo.fileGroup.fileGroupId); 
								     }
								     var code = Ext.isEmpty(selected.get(renderField)) ? [] : selected.get(renderField); 	
							         code.push(json.fileInfo.fileCode);
							         var cols = parentObj.getColumns(); 
							         Ext.Array.each(cols,function(col,i){
							    	    if(col.dataIndex == renderField){
							    		  	var store = col.getEditor().getStore();
							    		  	store.add({"fileCode":json.fileInfo.fileCode,"fileName":json.fileInfo.fileName}); 
							    		  	return;
							    	   	}
							        });
							        selected.set(renderField,null);
		                      	    selected.set(renderField,code);
			                      	 up.removeFile(files.id);
			                      	 me.fireEvent("uploadsuccess", "grid", me, data, selected);
		                      	  }else{
		                      		  var form = parentObj.getForm();
			                          var uploadFileGroupId = form.findField(uploadFile).getValue();
				                      if(Ext.isEmpty(uploadFileGroupId)){
				                      	  var upload = form.findField(uploadFile);
				                      	  upload.suspendFn();
				                          upload.setValue(json.fileInfo.fileGroup.fileGroupId);
				                      	  upload.resumeFn();
				                      }
			                      	  var renderFieldView = form.findField(renderField);  
			                          renderFieldView.addFile({"fileCode":json.fileInfo.fileCode,"fileName":json.fileInfo.fileName}); 
				                      me.fireEvent("uploadsuccess", "form", me, data);
		                          } 
		                      }else{
		                          Q.tips(json.msg||"上传附件失败");
		                      }
	                   	   } 
                   	   }catch(e){
               			  console.log(e); 
               		   } 
                   },
                   beforeShow:function(_self){
                	   var isShow = me.fireEvent("beforebrowseshow",me,_self);
                	   if(isShow === false){
                		   me.fireEvent("dealerror", me, _self);
                	   }
                   	   return isShow; 
                   }
                } 
            }
		});
		me.on('destroy', function(btn, eOpts) {
			try{ 
				if (btn.uploader) {
					btn.uploader.uploader.destroy();
				}
			}catch(e){
       			console.log(e); 
       		} 
	    });
	}
});