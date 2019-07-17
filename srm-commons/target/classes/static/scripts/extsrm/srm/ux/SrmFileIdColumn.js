Ext.define('Ext.srm.ux.SrmFileIdColumn', {    
	extend: 'Ext.grid.column.Column', 
    alias: ['widget.srmfileidcolumn'], 
    xtype:'srmfileidcolumn',
    alternateClassName: 'Ext.srm.ux.SrmFileIdColumn',  
    hidden:true,
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
		/*	var me = this,codes = [],fileViewField = self.fileViewField,viewStore;
			if(value != r.curValue && !Ext.isEmpty(value)){  
                var fildeStore = Ext.create("Ext.ux.data.JsonStore", {
				    proxy: {
				        type: 'ajax',
				        url: path_srment + "/fs/File_getAll.action",
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
							var cols = me.getColumns();
			                Ext.Array.each(cols,function(col,i){
			                    if(col.dataIndex == fileViewField){
			                        viewStore = col.getEditor().getStore();
			                        return;
			                    }
			                }); 
							Ext.Array.each(records,function(r){ 
							   codes.push(r.data.fileCode);
		                       viewStore.add({"fileCode":r.data.fileCode,"fileName":r.data.fileName});  
					        });
			                r.set(fileViewField,null);
			                r.set(fileViewField,codes);
						} 
					}
				});
				r.curValue = value;
			} */
			return value;
		}catch(e){
    		console.log(e);
    	}
	}
 
});