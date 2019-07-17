Ext.define('Ext.srm.ux.SrmFileColumn', {    
	extend: 'Ext.grid.column.Column', 
    alias: ['widget.srmfilecolumn'], 
    xtype:'srmfilecolumn',
    alternateClassName: 'Ext.srm.ux.SrmFileColumn',
	urlParams:{ 
		"Authorization": window.localStorage.getItem("token"),
		"clientId": "pc",
        "target":target
	},
   /**
    * 附件默认渲染
    * @param {} value
    * @param {} meta
    * @param {} r
    * @param {} rowIndex
    * @param {} colIndex
    * @param {} store
    * @param {} view
    * @param {} self
    * @return {}
    */
	renderer:function(value, meta, r, rowIndex, colIndex, store, view, self){
		try{   
			var me = this;
			var tagField = self.getEditor();
			var fieldStore = tagField.getStore(),
				valueField = tagField.valueField,
				displayField = tagField.displayField;   
			var data = fieldStore.data.autoSource.items;
			self.fileView(r,self);
			if(!Ext.isEmpty(value)){
				var downLoadUrl = path_srm + "/fs/file/download?";
				var name = "";
				var tipStr = "";
				Ext.Array.each(value,function(code,i){ 
					Ext.Array.each(data,function(item,index){
						if(item.get(valueField) == code){
							var url = self.buildUrl(downLoadUrl + "fileCode=" + code);
							var linkStr = '<a style="cursor:pointer;color:blue" href="' + url + '">【'+item.get(displayField)+"】</a></br> ";
							name = name + linkStr;
						}
					}) 
				}); 
				return name;//'<div style=" word-wrap: break-word; word-break: normal;white-space:pre-wrap;">'+name+'</div>';
			}else{
				return $("label.NoAttachments");
			}
		}catch(e){
    		console.log(e);
    	}
	},
	fileView:function(r, self){
		try{    
			var codes = [],fileViewField = self.dataIndex,viewStore,value = r.get(self.fileGroupIdField),curValueField = "curValue" + fileViewField;
			if(value != r[curValueField] && !Ext.isEmpty(value)){  
                var fildeStore = Ext.create("Ext.ux.data.JsonStore", {
				    proxy: {
				        type: 'ajax',
				        url: path_srm + "/fs/file/getall",
				        reader: {
				            type: 'json',
				            rootProperty: 'rows'
				        }
				    },
				    fields: ['fileId', 'fileName', 'fileCode']
				})
				fildeStore.load({
					params:{ 
					   "fileGroupId":value
					},
					callback:function(records){
						if(records && records.length > 0){  
			                var viewStore = self.getEditor().getStore(); 
							Ext.Array.each(records,function(r){ 
							   codes.push(r.data.fileCode);
		                       viewStore.add({"fileCode":r.data.fileCode,"fileName":r.data.fileName});  
					        });
			                r.set(fileViewField,null);
			                r.set(fileViewField,codes);
						} 
					}
				}); 
				r[curValueField] = value;
			} 
			return codes;
		}catch(e){
    		console.log(e);
    	}
	}, 
    /**
     * 构建参数url
     * @param {} url
     * @param {} params
     * @return {}
     */
    buildUrl : function(url) {
        var me= this,query = '',urlParams = me.urlParams; 
        if(urlParams){ 
	        Ext.Object.each(urlParams, function(key, val) {
	            query += (query ? '&' : '') + encodeURIComponent(key) + '=' + encodeURIComponent(val);
	        }); 
        }
        if (query) {
            url += (url.indexOf('?') > 0 ? '&' : '?') + query;
        }  
        return url;
    }
	
 
});