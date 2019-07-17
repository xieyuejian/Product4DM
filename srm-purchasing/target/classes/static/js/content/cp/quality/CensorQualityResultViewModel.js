/**
 * @class {Cp.quality.CensorQualityResultViewModel} 
 * @extend {Cp.quality.CensorQualityViewModel}
 * 待检验质检管理配置
 */
Ext.define('Cp.quality.CensorQualityResultViewModel', {
	extend: 'Cp.quality.CensorQualityViewModel',
	alias: 'viewmodel.censorQualityViewResultModel',
	config:{ 
	 	   data:{
	 		  dealUrl:  path_srm +'/cp/censorqualityresult',
	 		  initStatesStr: "CHECKED",	
	 		  moduleName: $('censorQualityResult'),
	 		  
	 		   
		        /**
				 * @cfg 
				 * 列表grid 配置项
				 */
			vp_gridCfg: { 
				stateHeader : true,
				stateful : true,
				stateId : s_userCode + '_censorQualityResult',
				forceFit: false ,
			    ableExporter:true
			 },
 		  	vp_addListBtn: [{
				name: 'synerp',
				text: $('button.synchronize'),
				index: 7,
				iconCls: 'icon-sync',
				build: power['synerp'],
				handler: 'vpSynerpHandler'
		   	}],
			vp_listEditStateFn: [{
				"synerp":function(r){
					return r.get('erpSyn') == 'SYNFAILED';
				}
			}],
			   
			vp_gridColumn:[
				{Qheader:'检验批号',header:$('censorQuality.censorqualityNo'),dataIndex:'censorqualityNo',width:150,tipable:true},
				{Qheader:'质检状态',header:$('censorQuality.status'),dataIndex:'status',width:130,renderer:'censorQualityRenderer',exportRenderer: true},
				{Qheader:'送检时间',header:$('censorQuality.inspectionTime'),dataIndex:'inspectionTime',width:150,renderer:'rendererDateTime',tipable:true,exportRenderer:true},               
				{Qheader:'物料编码',header:$('censorQuality.materialCode'),dataIndex:'materialCode',width:130,tipable:true},
				{Qheader:'物料名称',header:$('censorQuality.materialName'),dataIndex:'materialName',width:180,tipable:true},
				{Qheader:'送检量',header:$('censorQuality.censorQty'),dataIndex:'censorQty',width:130,tipable:true},
				{Qheader:'单位',header:$('censorQuality.unit'),dataIndex:'unit',width:130},
				{Qheader:'已质检合格量',header:$('censorQuality.checkQualifiedQty'),dataIndex:'checkQualifiedQty',width:130,renderer : 'rendererNumber'},
				{Qheader:'已质检不合格量',header:$('censorQuality.checkUnqualifiedQty'),dataIndex:'checkUnqualifiedQty',width:130,renderer : 'rendererNumber'},
				{Qheader:'已质检让步接收量',header:$('censorQuality.checkReceiveQty'),dataIndex:'checkReceiveQty',width:130,renderer : 'rendererNumber'},
				{Qheader:'质检结果名称',header:$('censorQuality.resultCode'),dataIndex:'resultName',width:130,tipable:true},
				{Qheader:'收货单号',header:$('censorQuality.shoppingNoticeNo'),dataIndex:'receivingNoteNo',width:150,tipable:true},
				{Qheader:'供应商编码',header:$('censorQuality.vendorCode'),dataIndex:'vendorErpCode',width:130},
				//{Qheader:'供应商Erp编码',header:$('censorQuality.vendorErpCode'),dataIndex:'vendorErpCode',width:130},
				{Qheader:'供应商名称',header:$('censorQuality.vendorName'),dataIndex:'vendorName',width:180,tipable:true},
				{Qheader:'采购订单号',header:$('censorQuality.purchaseOrderNo'),dataIndex:'purchaseOrderNo',width:150},
				{Qheader:'行号',header:$('censorQuality.rowIds'),dataIndex:'rowIds',width:130,tipable:true},
				{Qheader:'凭证年度',header:$('censorQuality.voucherYear'),dataIndex:'voucherYear',width:130},
				{Qheader:'凭证编号',header:$('censorQuality.voucherNo'),dataIndex:'voucherNo',width:150},
				{Qheader:'凭证行项目号',header:$('censorQuality.voucherProNo'),dataIndex:'voucherProNo',width:130,tipable:true},
				{Qheader:'采购组织编码',header:$('censorQuality.purchasingOrgCode'),dataIndex:'purchasingOrgCode',width:160,tipable:true},
				{Qheader:'采购组织名称',header:$('censorQuality.purchasingOrgName'),dataIndex:'purchasingOrgName',width:180,tipable:true},
				{Qheader:'质检时间',header:$('censorQuality.qualityTime'),dataIndex:'qualityTime',width:150,renderer:'rendererDateTime',exportRenderer:true},             
				{Qheader:'质检人员名称',header:$('censorQuality.qualitorName'),dataIndex:'qualitorName'},
				{Qheader:'备注',header:$('censorQuality.remark'),dataIndex:'remark',width:130},
				{Qheader:'同步状态',header:$('censorQuality.erpSyn'),dataIndex:'erpSyn',width:130,renderer: 'gridErpSynStateRenderer',exportRenderer: true},
				{Qheader:'送检质检单ID',header:$('censorQuality.censorqualityId'),dataIndex:'censorqualityId',disabled:true},
				{Qheader:'不合格量',header:$('censorQuality.unqualifiedQty'),dataIndex:'unqualifiedQty',disabled:true},
				{Qheader:'让步接收量',header:$('censorQuality.receiveQty'),dataIndex:'receiveQty',disabled:true},
				{Qheader:'合格量',header:$('censorQuality.qualifiedQty'),dataIndex:'qualifiedQty',disabled:true},
//					{Qheader:'质检结果代码',header:$('censorQuality.resultCode'),dataIndex:'resultCode',disabled:true},
				{Qheader:'可检量',header:$('censorQuality.canCheckQty'),dataIndex:'canCheckQty',disabled:true},
				{Qheader:'工厂编码',header:$('censorQuality.plantCode'),dataIndex:'plantCode',disabled:true},
				{Qheader:'工厂名称',header:$('censorQuality.plantName'),dataIndex:'plantName',disabled:true},
				{Qheader:'库存地点编码',header:$('censorQuality.stockCode'),dataIndex:'stockCode',disabled:true},
				{Qheader:'库存地点名称',header:$('censorQuality.stockName'),dataIndex:'stockName',width:180},
				{Qheader:'附件',header:$('censorQuality.uploadFileGroupId'),dataIndex:'uploadFileGroupId',disabled:true},
				{Qheader:'同步信息',header:$('censorQuality.erpReturnMsg'),dataIndex:'erpReturnMsg',disabled:true},
				{Qheader:'送检人员id',header:$('censorQuality.inspectorId'),dataIndex:'inspectorId',disabled:true},
				{Qheader:'送检人员名称',header:$('censorQuality.inspectorName'),dataIndex:'inspectorName',disabled:true},
				{Qheader:'质检人员id',header:$('censorQuality.qualitorId'),dataIndex:'qualitorId',disabled:true},
				{Qheader:'客户端编码',header:$('censorQuality.clientCode'),dataIndex:'clientCode',disabled:true}
			],
		   
		    /**
			 * @cfg {Array} searchFormItems 
			 * 质检管理查询字段集合
			 */
			sw_searchFormItems: [
				{xtype:'hidden', fieldLabel:$('censorQuality.censorqualityId'), name : 'filter_LIKE_censorqualityId'},
				{QfieldLabel:'检验批号', fieldLabel:$('censorQuality.censorqualityNo'), name:'filter_LIKE_censorqualityNo'},
				{QfieldLabel:'收货单号', fieldLabel:$('censorQuality.shoppingNoticeNo'), name:'filter_LIKE_receivingNoteNo'},
				{QfieldLabel:'采购订单号', fieldLabel:$('censorQuality.purchaseOrderNo'), name:'filter_LIKE_purchaseOrderNo'},
				{QfieldLabel:'物料编码', fieldLabel:$('censorQuality.materialCode'), name:'filter_LIKE_materialCode'},
				{QfieldLabel:'物料名称', fieldLabel:$('censorQuality.materialName'), name:'filter_LIKE_materialName'},
				{QfieldLabel:'供应商编码', fieldLabel:$('censorQuality.vendorCode'), name:'filter_LIKE_vendorCode_OR_vendorErpCode'},
				{xtype:'hidden', QfieldLabel:'供应商Erp编码', fieldLabel:$('censorQuality.vendorErpCode'), name:'filter_LIKE_vendorErpCode'},
				{QfieldLabel:'供应商名称', fieldLabel:$('censorQuality.vendorName'), name:'filter_LIKE_vendorName'},
				{QfieldLabel:'凭证编号', fieldLabel:$('censorQuality.voucherNo'), name:'filter_LIKE_voucherNo'},
				{
		                QfieldLabel: '质检时间',
		                fieldLabel: $('censorQuality.qualityTime'),
		                name: 'filter_GE_qualityTime',
		                anchor: '90%',
		                xtype: 'datefield',
		                format: 'Y-m-d'
		            }, {
		                QfieldLabel: '到',
		                fieldLabel: $('label.to'),
		                name: 'filter_LE_qualityTime',
		                anchor: '90%',
		                xtype: 'datefield',
		                format: 'Y-m-d'
		            },  {
		                QfieldLabel: '质检人名称',
		                fieldLabel: $('censorQuality.qualitorName'),
		                name: 'filter_LIKE_qualitorName'
		            }
			],
		   sw_isShowStatus: false
 	   }
	}
});