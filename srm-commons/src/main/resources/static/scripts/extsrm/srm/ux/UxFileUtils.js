/**
 * @class {Ext.srm.ux.UxFileUtils}
 * @extend {Ext.ux.file.UxFileUtils} 文件处理工具类
 */
Ext.define('Ext.srm.ux.UxFileUtils', {  
	 extend:"Ext.ux.file.UxFileUtils",
     alternateClassName: ['Ext.UxFile'],
	 singleton: true,   
	 /**
	  * 附件下载
	  * @param {} url 请求url
	  * @param {} fileName 下载文件名
	  * @param {} options 其他配置项 如headers等
	  */
     fileDown: function(url, fileName, options) {
     	try{
     		var me = this,options = options || {};
     		if(Ext.isEmpty(url)){
     			console.log("url 没有配置"); 
     		}
     		if(Ext.isEmpty(fileName)){ 
     			console.log("fileName 没有配置");
     		}
 			options = Ext.apply({
 				headers:{
					"Authorization": window.localStorage.getItem("token"),
					"clientId": "pc",
                	"target":target
 				}
 			},options);
 			options.url = url;
 			options.fileName = fileName; 
 			options.http_method = "POST";
     		me.fileDownByXhr(options);
     	}catch(e){
     		console.log(e);
     	} 
     },
    /** 
     * 附件导入
     * @param {String} url 请求url
     * @param {Object} options 配置 
     */
    fileImport: function(options, renderTo) {
    	try{
    		var me = this; 
	        if(!options.url){
	        	console.info("url 必需维护 ");
	        	return false;
	        } 
	        options = options || {};
	        //默认处理配置
	        options = Ext.apply({ 
	        	headers:{
		        	"Authorization":window.localStorage.getItem("token"),
		        	"clientId":"pc",
                	"target":target
	        	}
	        },options);   
	        options.renderTo = renderTo;
	        var importWin = me.getImportWin(options, renderTo);
	        importWin.show(); 
    	}catch(e){
    		console.log(e);
    	} 
    }
});