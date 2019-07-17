Ext.define("Ext.srm.form.SrmFileField", {
	extend : "Ext.ux.form.UxFileField",
	alias : "widget.srmfilefield",
	xtype : "srmfilefield",  
	sumbitValue:false,
	urlParams:{ 
		"Authorization": window.localStorage.getItem("token"),
		"clientId": "pc",
		"target": target
	},
    urlConfig:{
        deleteUrl: path_srment + "/fs/file/delete",
        downloadUrl: path_srment + "/fs/file/download",
        getAllUrl: path_srment + "/fs/file/getall"
    }, 
    listeners:{
    	"operation":function(_self,operations, operationsInfo){
    		try{  
	       		 var commModelEditWin = _self.up("commModelEditWin");
	       		 if(commModelEditWin){
	       		    operations["delete"] = !("view" === commModelEditWin.btnType); 
	       		 }
    		}catch(e){
    			console.log(e);
    		}
    	} 
    }  
});