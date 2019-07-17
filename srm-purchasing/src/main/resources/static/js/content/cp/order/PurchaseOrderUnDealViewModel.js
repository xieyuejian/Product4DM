/**
 * @class {PurchaseOrderUnDealViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 待处理采购订单配置
 */
Ext.define('Cp.order.PurchaseOrderUnDealViewModel',{
	extend:'Cp.order.PurchaseOrderViewModel',
    alias:'viewmodel.purchaseOrderUnDealViewModel',

  config:{
  	data : {
        playListMode:"undeal",
        
		vp_hideListBtn:['add','prompttrial',"export"],
		
		vp_gridCfg: {
			stateful : true,
			stateId : s_userCode + '_purchaseOrderUndeal',
			stateHeader : false,
			forceFit : false,
		    rn:true//序列列隐藏
	
		},
		// 查询窗体高度
      	sw_Height:350,	
      	
      	playListMode:'undeal',
      	
      	 /**
         * 初始化状态
         * @param {} vm 配置对象
         * @return {Array}
         */
        initStatusFn:function(vm){ 
        	return [{
            		statusCode:"RELEASE",
            		statusName:$("porder.purchaseOrderStateRelease") 
            	}];
        }, 
        
		// 执行中订单明细查询字段集合
		sw_searchFormItems:[
			{fieldLabel:$('porder.purchaseOrderTime'), name:'filter_GE_purchaseOrderTime', xtype:'datefield', format:'Y-m-d'},
			{fieldLabel:$('label.to'), name:'filter_LE_purchaseOrderTime', xtype:'datefield', format:'Y-m-d'},
			{fieldLabel:$('label.createTime'), name:'filter_GE_createTime', xtype:'datefield', format:'Y-m-d'},
			{fieldLabel:$('label.to'), name:'filter_LE_createTime', xtype:'datefield', format:'Y-m-d'},
			{fieldLabel:$('purchasingOrg.code'), name:'filter_LIKE_purchasingOrgCode'},
			{fieldLabel:$('purchasingGroup.code'), name:'filter_LIKE_purchasingGroupCode'},
			{fieldLabel:$('materialInfo.code'), name:'filter_EQ_purchaseOrderDetails_materialCode'},
			{fieldLabel:$('materialInfo.name'), name:'filter_LIKE_purchaseOrderDetails_materialName'},
			{fieldLabel:$('vendor.code'), name:'filter_LIKE_vendorErpCode'},
			{fieldLabel:$('vendor.name'), name:'filter_LIKE_vendorName'},
			{fieldLabel:$('label.createUserName'), name:'filter_LIKE_createUserName'},
			{fieldLabel:$('batchCharacter.purchaseOrderCode'), name:'filter_LIKE_erpPurchaseOrderNo'}
		],
		moduleName:$('porder.title'),
		vp_addListBtn:[{
			build : true,
			name : 'changeState',
			text : $('porder.changeState'),
			index : 4,
			iconCls : 'icon-edit',
			build : true,
			menu:{
			//new Ext.menu.Menu({
			//getByName : function(name) {
			//	var items = this.items.items;
			//	for (var i = 0; i < items.length; i++) {
			//		if (items[i].name == name) {
			//			return items[i];
			//		}
			//	}
			//},
			items : [ {
				text : $('porder.accept'),
				name : 'TOACCEPT',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('porder.hold'),
				name : 'TOHOLD',
				hidden : true,
				handler : 'toHold'
			}, {
				text : $('porder.reject'),
				name : 'TOREJECT',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('porder.confirmHold'),
				name : 'TOFIRMHOLD',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('porder.confirmReject'),
				name : 'TOFIRMREJECT',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('porder.realese'),
				name : 'TORELEASE',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('porder.open'),
				name : 'TOOPEN',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('porder.close'),
				name : 'TOCLOSE',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('porder.cancel'),
				name : 'TOCANCEL',
				hidden : true,
				handler : 'dealState'
			}, {
				text : $('button.noOperation'),
				name : 'unstate',
				hidden : true
			}, {
				text : $('porder.pleaseSelect'),
				name : 'sel',
				hidden : false
			} ]
		//})
			}	
		}/*,{
			name : 'synErp',
			text : $('button.synerp'),
			disabled : true,
			index : 6,
			iconCls : 'icon-sync',
			build : power['posterp'],
			handler : 'vpSynErpHandler'
		},
		{
			name : 'export',
			text : "导出",
			index : 8,
			iconCls : 'icon-export',
			build : power['export'],
			menu: {
		        items: [ {
					name : 'export',
					text : $('button.Export'),
					index : 8,
					iconCls : 'icon-export',
					build : power['export'],
					handler : 'vpExportHandler'
				},{
       			 name: "export",
 	            Qtext: "导出",
 	            text: $("button.exportExcel"),
 	            build: power["export"],
 	            tooltip: $("button.export.tooltip"),
 	            iconCls: "icon-export",
 	            handler: "exportExcel"
 		}]
		    }
		}, 
		{
			name : '更多',
			text : $('button.moreData'),
			index : 11,
			iconCls : 'icon-export',
			build : true,
			menu: {
		        items: [  {
					text : $('button.download'),
					name : "download",
					iconCls : 'icon-download',
					index : 9,
					build : power['download'],
					handler : 'vpDownloadHandler'
				},
				 {
					text : $('button.import'),
					iconCls : 'icon-putin',
					name : "import",
					index : 10,
					build : power['batchimport'],
					handler : 'vpImportHandler'
				}
		        ]
		    }
		}*/],
		vp_subTab: [ [ 'detailGrid', 'quantityPanel', 'pricingGrid' ], 'logTab', 'msgTab','chatTab', 'interactionGrid' ]
	}
	}
});
