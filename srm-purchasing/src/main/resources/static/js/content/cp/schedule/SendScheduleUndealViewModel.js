/**
 * @class {SendScheduleUndealViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 待处理送货排程配置
 */
Ext.define('Cp.schedule.SendScheduleUndealViewModel',{
	extend:'Cp.schedule.SendScheduleViewModel',
    alias:'viewmodel.sendScheduleUnDealViewModel',
	config:{ 
    	data:{
    		playListMode:'undeal',
    		
    		moduleName: $('sendschedul.title'),
    		
    		methodName: 'UnDealList',
    		
    		/**
             * 初始化状态
             * @param {} vm 配置对象
             * @return {Array}
             */
            initStatusFn:function(vm){ 
            	return s_roleTypes.indexOf("V") >= 0 ?
	            	[{
	            		statusCode:"RELEASE",
	            		statusName:"发布" 
	            	}] 
            	:
            		[{
                		statusCode:"REFUSE",
                		statusName:"拒绝" 
                	},{
                		statusCode:"NOPASS",
                		statusName:"审核不过" 
                	},{
                		statusCode:"CONFIRM",
                		statusName:"待审核" 
                	}];
            }, 
    		
    		vp_gridCfg:  {
				stateful : true,
				stateId : s_userCode + '_sendScheduleUndeal',
//					stateHeader : s_roleTypes[0] == "V" ? false : true,
				stateHeader : true,
				forceFit : false,
				ableExporter:false,
				 /**
                   * 是否启用导出功能 true|false
                 */
                ableExporter:false,
                rn:true//序列列隐藏
			},
    		
    		sw_Height: 300,
    		
    		sw_searchFormItems:[{
				QfieldLabel:"排程单号  ",
				fieldLabel:$("sendschedul.sendScheduleNo"),
			    name:"filter_LIKE_sendScheduleNo"
			},{
				QfieldLabel:"采购订单号",
				fieldLabel:$("porder.purchaseOrderNo"),
			    name:"filter_LIKE_sendScheduleCommons_sendScheduleDetails_purchaseOrderNo"
			},{
				QfieldLabel:"采购组织编码",
				fieldLabel:$("purchasingOrg.code"),
			    name:"filter_LIKE_purchasingOrgCode"
			},{
				QfieldLabel:"采购组织名称",
				fieldLabel:$("purchasingOrg.name"),
			    name:"filter_LIKE_purchasingOrgName"
			},{
				QfieldLabel:"供应商编码",
				fieldLabel:$("vendor.code"),
			    name:"filter_LIKE_vendorErpCode"
			},{
				QfieldLabel:"供应商名称",
				fieldLabel:$("vendor.name"),
			    name:"filter_LIKE_vendorName"
			},{
				QfieldLabel:"物料编码",
				fieldLabel:$("materialInfo.code"),
			    name:"filter_LIKE_sendScheduleCommons_sendScheduleDetails_materialCode"
			},{
				QfieldLabel:"物料名称",
				fieldLabel:$("materialInfo.name"),
			    name:"filter_LIKE_sendScheduleCommons_sendScheduleDetails_materialName"
			},  {
            QfieldLabel: '创建时间',
            fieldLabel: $('label.createTime'),
            name: 'filter_GE_createTime',
            anchor: '90%',
            xtype: 'datefield',
            format: 'Y-m-d'
        }, {
            QfieldLabel: '到',
            fieldLabel: $('label.to'),
            name: 'filter_LE_createTime',
            anchor: '90%',
            xtype: 'datefield',
            format: 'Y-m-d'
        }, {
            QfieldLabel: '创建人名称',
            fieldLabel: $('label.createUserName'),
            name: 'filter_LIKE_createUserName'
        }, {
				fieldLabel:$('sample.status'),
				xtype:"hidden",
				items:[]
			},{   
		        fieldLabel:$("erpSyn.erpSynState"),
		        xtype:"hidden", 
		        items:[]
			}],
			
			vp_hideListBtn: ['add','prompttrial'],
			
			vp_addListBtn: [{
			 name:"accept",
			 index:4,
			 Qtext:"接受",
			 text:$("button.accept"),  
		     build:"#{power.accept}",    
			 exclude:true, 
		     iconCls:"icon-accept2",
		     handler: 'editBtnToAcceptFn'
		 },{
			 name:"refuse",
			 index:5,
			 Qtext:"拒绝",
			 text:$("button.refuse"),  
		     build:"#{power.refuse}",   
			 exclude:true, 
		     iconCls:"icon-refuse",
		     handler: 'editBtnToRefuseFn'
		 }/*,{
			 name:"change",
			 index:6,
			 Qtext:"强制变更",
			 text:$("button.change"),  
		     build:"#{power.change}",
		     hidden:s_roleTypes.indexOf("V")>-1,
			 exclude:false, 
		     iconCls:"icon-choose1",
		     handler: 'editBtnToChangeFn'
		 },{
			 name:"cancel",
			 index:7,
			 Qtext:"取消",
			 text:$("button.cancel"),  
		     build:"#{power.cancel}",  
			 exclude:false, 
		     iconCls:"icon-cancel",
		     handler: 'editBtnToCancelFn'
		 },{
			 name:"synerp",
			 index:8,
			 Qtext:"同步到ERP",
			 text:$("button.synerp"),  
		     build:power.synerp,   
			 exclude:false, 
		     iconCls:"icon-sync",
		     handler: 'editBtnToSyncFn'
		 }*/]
			
    	}
    }
});
