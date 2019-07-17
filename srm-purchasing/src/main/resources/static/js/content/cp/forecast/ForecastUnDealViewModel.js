/**
 * @class {Cp.forecast.ForecastUnDealViewModel} 
 * @extend {Cp.forecast.ForecastViewModel}
 * 待处理采购预测配置
 */
Ext.define('Cp.forecast.ForecastUnDealViewModel',{
	extend : 'Cp.forecast.ForecastViewModel',
    alias: 'viewmodel.forecastUnDealViewModel',
    config:{ 
    	data:{
    		playListMode:'undeal',
			vp_hideListBtn: ['add','prompttrial'],
			 vp_addListBtn: [{
			name: 'erpImport',
			Qtext: 'ERP导入',
			text: 'ERP导入',
			build: power['erpimport'],
			index: 7,
			iconCls: 'icon-erpleading',
			handler: 'vpErpImportHandler'
		}]
			
    	}
    }
});