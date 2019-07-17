/**
 * @class {Cp.apply.PurchasingRequisitionUnDealViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 待处理采购申请配置
 */
Ext.define('Cp.apply.PurchasingRequisitionUnDealViewModel',{
	extend : 'Cp.apply.PurchasingRequisitionViewModel',
    alias: 'viewmodel.purchasingRequisitionUnDealViewModel',
	config:{ 
    	data:{
    		playListMode:'undeal',
			vp_hideListBtn: ['add','prompttrial'],
			sw_Height: 300,
			sw_Width: 700,
			sw_isShowStatus: false,
			sw_isShowSynStatus: false,
			vp_addListBtn: [{
				name: "close",
				Qtext: "关闭",
				text: $('button.close'),
				build: power['close'],
				index: 4,
				iconCls: "icon-close",
				handler: 'vpCloseHandler'
			}, {
				name: "cancel",
				Qtext: "取消",
				text: $('button.cancel'),
				build: power['cancel'],
				index: 5,
				iconCls: "icon-cancel",
				handler: 'vpCancelHandler'
			}]
			
    	}
    }
});