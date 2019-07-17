/**
 * @class Ext.srm.form.MaterialTriggerField
 * @extends Ext.ux.form.TriggerField
 * See the example: 
 * 
 *     @example 
 *		 Ext.create('Ext.ux.form.FormPanel',{
 *        frame:true,
 *		    buttonAlign : 'center',
 *		    layout:'fit',
 *        width:400,
 *		        renderTo:Ext.getBody(),
 *		    items:[{
 *		       layout : 'form', 
 *		    	 labelAlign:'right',
 *		     	 labelWidth: 50,
 *		    	 items:[{
 *        		fieldLabel:"状态",
	 *        		//editable: true,
	 *              name:"start",
	 *              hiddenName:"start", 
	 *        		xtype:'usertrigger', 
 * 				    renderToId:id,
 *                  fieldMapping:{"model.userCode":"userCode"}
 *       		}
 *     		}]
 *		    }]
 *		 });
 *
 */ 
Ext.define('Ext.srm.form.MaterialTriggerField', {
	extend: 'Ext.srm.form.SrmTriggerField',
	alias: 'materialtrigger',
	xtype: 'materialtrigger',
	alternateClassName: ['Ext.srm.form.MaterialTriggerField'],   
    /**
     * 创建弹出窗口类名 
     */
    selectWinClassName:"Md.material.MaterialSelectWin",
    /**
     * 请求初始参数 
     */ 
    baseParams: {
        filter_IN_status: "1"//物料使用状态 
    },
    /**
     * 树请求参数 
     */
    baseParamsTree: {
        filter_IN_status: "1"//物料组使用状态
    },  
    /**
     * 字段映射配置 
     *  键为赋值对象:form取 name 属性值/grid 取 dataIndex属性
     *  值为取值对象dataIndex属性值
     *  {
     *     "target" :"origin"
     *  } 
     */
    fieldMapping:{
    	"model.materialCode":"materialCode",
    	"model.materialName":"materialName" 
    }, 
    constructor:function(conf){
       var me = this; 
       var conf = conf || {};  
       this.callParent([conf]);
    } 
});