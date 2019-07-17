/**
 * 跳转column
 */
Ext.define('Ext.srm.ux.SrmSkipTabColumn', {    
	extend: 'Ext.grid.column.Column', 
    alias: ['widget.srmskiptabcolumn'], 
    xtype:'srmskiptabcolumn',
    alternateClassName: 'Ext.srm.ux.SrmSkipTabColumn',  
    /**
     * 
     * @param {Object} value 当前record对应的列值
     * @param {} cellValues 该列的其他信息
     * @param {Ext.data.Record} record 当前记录
     * @param {} e
     * @param {} a
     * @param {} f 
     * @param {Ext.view.Table} gridView 列表对象
     * @return {Object} 当前渲染的值
     */
    defaultRenderer: function(value, cellValues,record,e,a,f,gridView) {
    	try{
	    	var me = this,
	            cls = me.checkboxCls,
	            tip = me.tooltip,
	            skipModule = me.skipModule,
	            originDataIndex = me.originDataIndex;   
	        if(!Ext.isEmpty(value)){  
	        	if(Ext.isEmpty(skipModule) || Ext.isEmpty(originDataIndex)){
	        		console.error("not config skipModule or originDataIndex");
	        	}  
	        	//该额外参数类型为提供返回是回调处理
        	    var extra = {
        	       xtype: "extra",
	        	   originId: record.get(originDataIndex),
	        	   moduleId: gridView.up("commModelVpList").moduleId
	            }  
	        	window.skipByKey = function(extraDe){   
	        	   window.onSkipByKey(skipModule , extraDe);
	        	} 
	        	extraEn = Ext.encode(extra);
	    		return "<a onclick = window.skipByKey("+ extraEn +")>" + value + "</a>";
	    	}else{
	            return value;
	    	}   
    	}catch(e){
    		console.log(e);
    	} 
    }  
});