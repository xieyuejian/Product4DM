/**
 * 导出预览窗口
 */
Ext.define("Ext.ux.window.UEditorDownWin", {
	  extend : "Ext.ux.Window",
	  alias : "ueditorDownWin",
	  xtype : "ueditorDownWin",
	  alternateClassName : [ "Ext.ux.window.UEditorDownWin" ],
	  uses : [ "Ext.ux.Q","Ext.ux.Window" ],  
	  title: '导出预览窗口',  
	  height: 460,
	  width: 700,
	  draggable:true, 
	  closeAction:'close',
	  closable:false,
	  layout: 'fit', 
	  ueditorConfig:{ 
		  toolbars: [[
	            'print', 'preview'
	        ]]
	  	 /*'fontfamily':[
           { label:'',name:'songti',val:'宋体,SimSun'},
           { label:'',name:'kaiti',val:'楷体,SimKai'},
           { label:'',name:'yahei',val:'微软雅黑,Microsoft YaHei'},
           { label:'',name:'heiti',val:'黑体,SimHei'} 
         ]*/
	  },
	  listeners:{
		  "afterRender":function(){
			    var me = this; 
		    	if (!me.ue) {  
		    		 var frameHeight = me.height - 240; 
		    		 me.ue = UE.getEditor(me.extId,Ext.apply(me.ueditorConfig, {
		                 initialFrameHeight:frameHeight, 
		                 initialFrameWidth: '100%',
		                 autoHeightEnabled:false,
		                 zIndex:200000
		             }));  
		    		 me.ue.ready(function () {
		    			 me.UEditorIsReady = true;
		    			 var content ; 
		    			 if(me.content){   
			    			 me.ue.setContent(me.content);  
		    			 } 
		    		 });  
		    	}
		    } 
	  },
	  initComponent:function(){
		 var me = this;
		  var hiddenFlag = false;
		  if(me.ueditorConfig && me.ueditorConfig.readonly){
			  hiddenFlag = true
		  }  
		  me.buttons = [{
		    	text: $("button.return"),
		        Qtext: "返回",
		        margin:'0 30 0 0',
		        style:'width:80px;height:34px',
		        ui:"gray-btn",
		        handler: function(){ 
		            UE.delEditor(me.extId);
		        	me.hide();
		        }
		    },{
		        text:  "确定",
		        ui:"blue-btn", 
		        margin:'0 30 0 0',
		        style:'width:80px;height:34px',
		        Qtext: "确定",
		        handler: function(){ 
	    		    var html = me.ue.getAllHtml();
		            var reg=new RegExp(/&nbsp;/g); 
		            html = html.replace(/&nbsp;/g, ""); 
		            console.info(html);
		            var params =  {
                         html : html 
                    };
		        	me.fireEvent("ajaxBefore",me,params);
		            Ext.Ajax.request({
                         url : me.exportUrl, 
                         method : 'POST',
                         params : params,
                         success : function(response) { 
		        	         me.fireEvent("ajaxSuccess",me,response,params);
                         },
                         failure : function(response) {
                         	 me.fireEvent("ajaxFailure",me,response,params);
                         },
                         callback : function(){
                         	 me.fireEvent("ajaxCallback",me ,params);
                         }
                     });  
		        	me.hide(); 
		        }
		  }];
		  me.callParent();     // 调用父类进行初始化传递进来的参数
	  },
	  //给当前定义的类加一个构造器 ,目的就是为了初始化信息
 	  constructor:function(conf){
	 		  var me = this;  
	 		  var conf = conf || {};
			  var extId = Ext.id();   
			  me.extId = extId;
			  Ext.apply(conf,{ 
			      html:"<div id='"+extId+"' style='width:100%;height:100%;'>" + "</div>" , 
			      renderTo : conf.moduleId,
			      constrain:true 
			  });  
	 		  me.callParent([ conf ]);     // 调用父类进行初始化传递进来的参数
 	  }
});