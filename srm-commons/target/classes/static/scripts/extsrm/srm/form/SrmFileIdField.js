Ext.define("Ext.srm.form.SrmFileIdField", {
	extend : "Ext.form.field.Text",
	alias : "widget.srmfileidfield",
	xtype : "srmfileidfield", 
	hidden:true,
	/**
	 * 挂起方法
	 * @type Boolean true 挂起
	 */
	isSuspendFn:false,
    /**
     * @private
     * If grow=true, invoke the autoSize method when the field's value is changed.
     */
    onChange: function(newVal, oldVal) {
    	var me = this; 
        this.callParent([newVal, oldVal]); 
        this.autoSize(); 
    	me.renderFileView(newVal, oldVal);
    }, 
    /**
     * 设置附件渲染
     * @param {} newVal 新值
     * @param {} oldVal 旧值
     */
    renderFileView:function(newVal, oldVal){
    	try{
    		var me = this,isSuspendFn = me.isSuspendFn; 
    		if(isSuspendFn || Ext.isEmpty(newVal)){
    			return false;
    		}
    		if(Ext.isEmpty(me.fileViewField)){
    			 Ext.raise("fileViewField no configuration");
    			 return false;
    		}
    		var fileViewField = me.fileViewField;
    		if(me.up("form")){
	    		var form = me.up("form").getForm();
	    		var viewField = form.findField(fileViewField);
	    		if(viewField){
	    			viewField.renderFile(newVal);
	    		}else{
	    			Ext.raise(fileViewField + "no find");
	    		} 
	    	}
    	}catch(e){
    		Ext.log(e);
    	} 
    },
    /**
     * 方法挂起
     * @return {Boolean}
     */
    suspendFn:function(){
       try{
    		var me = this; 
    		me.isSuspendFn = true;
    	}catch(e){
    		Ext.raise(e);
    	} 
    },
    /**
     * 方法挂起恢复
     * @return {Boolean}
     */
    resumeFn:function(){
       try{
    		var me = this; 
    		me.isSuspendFn = false;
    	}catch(e){
    		Ext.raise(e);
    	} 
    }
	
});