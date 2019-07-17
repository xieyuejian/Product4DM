Ext.define("Ext.ux.window.UEditorWindow", {
	  extend : "Ext.ux.Window",
	  alias : "ueditorWindow",
	  xtype : "ueditorWindow",
	  alternateClassName : [ "Ext.ux.window.UEditorWindow" ],
	  uses : [ "Ext.ux.Q","Ext.ux.Window" ],  
	  title: '文本编辑器窗口',  
	  height: 460,
	  width: 700,
	  draggable:true, 
	  closeAction:'close',
	  closable:false,
	  layout: 'fit', 
	  ueditorConfig:{
		  //工具栏上的所有的功能按钮和下拉框，可以在new编辑器的实例时选择自己需要的重新定义
	         toolbars: [[
	            'undo', 'redo', '|',
	            'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch',  'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'selectall', 'cleardoc', '|',
	            'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
	            'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
	            'directionalityltr', 'directionalityrtl', 'indent', '|',
	            'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|', 
	            'horizontal', 'date', 'time',  '|',
	            'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', '|',
	            'print', 'preview' 
	        ]],
	        'fontfamily':[
	           { label:'',name:'songti',val:'宋体,SimSun'},
	           { label:'',name:'kaiti',val:'楷体,SimKai'},
	           { label:'',name:'yahei',val:'微软雅黑,Microsoft YaHei'},
	           { label:'',name:'heiti',val:'黑体,SimHei'} 
	        ]
		  
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
		                 scaleEnabled:true,
		                 zIndex:200000
		             }));  
		    		 me.ue.ready(function () {
		    			 me.UEditorIsReady = true;
		    			 var content ;
		    			 if(me.contentField){
		    				 content = me.record.get(me.contentField); 
		    		     }else if(me.formField){
		    		    	 content = me.formField.getValue().replace("&gt;",">");
		    		     }else{
		    		    	 content = me.contentValue;
		    		     } 
		    			 if(content){
			    			 me.ue.setContent(content); 
		        	         var content = me.ue.getContent(); 
		                  	 var contentTxt = me.ue.getContentTxt();  
			    			 if(me.contentTxtField && contentTxt){
					            me.record.set(me.contentTxtField,contentTxt); 
		    		    	 }else if(me.formField){
		    		    		me.formField.setValue(content);
		    		    	 }
		    			 } 
		    		 });  
		    	}
		    },
		    editorsure:function(content,contentTxt){
		    	var me = this; 
		    	if(Ext.isFunction(me.editorsure)){
		    		me.editorsure();
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
		    	text: "获取html",
		        Qtext: "获取html",
		        margin:'0 30 0 0',
		        style:'width:80px;height:34px',
		        ui:"gray-btn",
		        hidden:true,
		        handler: function(){  
		            //console.info(me.ue.getAllHtml());
		            var html = me.ue.getAllHtml();
		            Ext.Ajax.request({
                         url : path_srment + "/contract/Contract_exportPdf.action",
                         method : 'POST',
                         params : {
                             html :"dfdf" 
                         },
                         success : function(response) {
                              console.info(response);
                         },
                         failure : function(response) {
                         	Q.warning("<font color='red'>" + response.responseText + "</font>",{renderTo:me.moduleId});
                         },
                         callback : function(){
                         }
                     }); 
		        }
		    },{
		        text:  "确定",
		        ui:"blue-btn", 
		        margin:'0 30 0 0',
		        style:'width:80px;height:34px',
		        hidden:hiddenFlag,
		        Qtext: "确定",
		        handler: function(){
		        	var content = me.ue.getContent(); 
		        	var contentTxt = me.ue.getContentTxt(); 
    		    	if(me.contentField){
			            me.record.set(me.contentField,content); 
    		    	}else if(me.formField){
    		    		me.formField.setValue(content);
    		    	}
    		    	if(me.contentTxtField){
			            me.record.set(me.contentTxtField,contentTxt); 
    		    	}  
		        	me.fireEvent("editorsure", content,contentTxt);
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
	  			  autoScroll:true,
			      constrain:true 
			  });  
	 		  me.callParent([ conf ]);     // 调用父类进行初始化传递进来的参数
 	  }
});