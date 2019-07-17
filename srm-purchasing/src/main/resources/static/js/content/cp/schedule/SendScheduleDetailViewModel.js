/**
 * @class {Cp.schedule.SendScheduleViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 送货排程
 */
Ext.define('Cp.schedule.SendScheduleDetailViewModel',{
	extend:'Ext.ux.app.ViewModel',
    alias:'viewmodel.sendScheduleDetailViewModel',
    requires:[
    	'Ext.ux.form.DateTimeField'
    ],

    data: {	
    	
    	isExtend:true,
    	/**
		 * @cfg {String} dealUrl 
		 * 方法处理url
		 */
		dealUrl: path_srm + '/cp/sendscheduledetail',

		/**
		 * @cfg {String} moduleName 
		 * 模块名称
		 */
		moduleName: $('sendscheduledetail'),

		/**
		 * @cfg {String} triggerField 
		 * 触发域（字段名）
		 */
		vp_triggerField: '',

		/**
		 * @cfg {Array} hideVpBtn 
		 * 对固化的按钮进行隐藏操作
		 */
		vp_hideListBtn: ["delete","add","edit","view"],

		/**
		 * @cfg {String} moduleCode
		 * 质检单模块编码
		 */
		moduleCode: 'PCD',

		/**
		 * @cfg {Array} vpSubTab 
		 * 列表底部tab集合
		 */
//		vp_subTab: [],

		/**
		 * @cfg {Object} editWinCenterTab 
		 * 编辑窗体底部tab集合
		 */
		ew_centerTab: {},

		/**
		 * @cfg {Array} vpListEditStateFn 
		 * 列表界面按钮控制
		 */
		vp_listEditStateFn: [],

		/**
		 * @cfg {Array} editWinListeners 
		 * 编辑窗体事件
		 */
		//editWinListeners: {},

		/**
		 * @cfg {Object} nextBillState - default New 
		 * 提交后下一步的状态
		 */
		nextBillState: 'New',

		/**
		 * @cfg {Integer} activeTab
		 * 默认展示的tab页
		 */
		vp_activeTab: 0,

		/**
		 * @cfg {Integer} tabHeight
		 * 底部tab高度
		 */
		vp_tabHeight: 250,
		

		vp_hideSubTab : true,// 隐藏底部的tab

		/**
		 * @cfg {boolean} editWinMaximized
		 * 是否最大化窗口，默认为否
		 */
		maximized : true,
		
		/**
		 * 单据状态
		 */
		initStatesStr:'',
		
		/**
		 * @cfg {String} playListMode
		 * normal/audit/undeal //三种列表模式
		 */
		playListMode: 'normal',

		/**
		 * @cfg {String} controllerClassName
		 * 控制类类名称
		 */
		controllerClassName: 'Cp.schedule.SendScheduleDetailController',

		/**
		 * @cfg {Boolean} searchWinIsShowStatus
		 * 查询窗体是否显示状态查询
		 */
		sw_isShowStatus: false, 
		
		/**
		 * @cfg {boolean} vpRowdbclick
		 * 是否双击
		 */
		rowdblclickFn:function(){
			return false;
		},

		/**
		 * @cfg {Integer} searchWinHeight
		 * 查询窗体高度
		 */
		sw_Height: 375,

		/**
		 * @cfg {Integer} searchWinWidth
		 * 查询窗体宽度
		 */
		sw_Width: 800,

		/**
		 * @cfg {String} searchFormColumnWidth
		 * 查询表单每行列数
		 */
		sw_columnWidth:'0.5',

		/**
		 * @cfg {boolean} isAudit
		 * 是否需要右键审核
		 */
		isAudit: true,

		/**
		 * @cfg {boolean} singleSelect
		 * 列表是否单选 true
		 */
		singleSelect: true,

		/**
		 * @me {String} singleSelect
		 * 跳转前的方法 true
		 */
		methodName: 'list',
		
		
		vp_gridCfg : {
			stateful : true,
			stateId :  s_userCode + '_SendScheduleDetail',
			stateHeader :true,
			forceFit : false,
			ableExporter:true,
			  /**
             *  导出相关样式配置项
             */
            /* 
            exportConfig:{  
                tableHeaderStyle:{
                    font: {
                        fontName: 'Arial',
                        family: 'Swiss',
                        size: 11,
                        color: '#1F497D'
                    }
                }
            }, */
           // sm:true,//选中框隐藏
            rn:true//序列列隐藏
		},

		/**
		 * @cfg {Array} addEditBtn 
		 * 在编辑界面固化的按钮基础上追加按钮
		 * - **changeState** - 变更状态
		 * - **check** - 撤销审批
		 * - **synErp** - 同步到erp
		 * - **export** - 导出
		 * - **download** - 下载
		 * - **import** - 导入
		 */
		ew_addEditBtn : [],

		/**
		 * @cfg {Object} gridStore 
		 * 列表Store
		 */
		vp_gridStore : {
			idProperty:"sendScheduleDetailId",
			url:path_srm+"/cp/sendscheduledetail/list",
			sort:"sendScheduleNo",
		    dir:"desc",
		    remoteSort: true
		},
		/**
		 * @cfg {Array} gridColumn 
		 * 列表对象 列属性配置项
		 */
		vp_gridColumn : [  
			{
				Qheader:"需求时间", 
				header:$("sendscheduledetail.scheduleTime"), 
				dataIndex:"scheduleTime",
				width:150,
				xtype: 'datecolumn',
				format: 'Y-m-d H:i:s',
				exportRenderer:true
			}, 
			{
				Qheader:"送货标识 0:未送货1:部分送货2:已送货 ", 
				header:$("sendscheduledetail.sendFlag"),
				dataIndex:"sendFlag",
				renderer:'gridEditSendFlagRenderer'
			}, 
			{Qheader:"物料编码", header:$("materialInfo.code"), dataIndex:"materialCode",width:120,tipable:true},
			{Qheader:"物料名称", header:$("materialInfo.name"),width:160,  dataIndex:"materialName",width:250,tipable:true},
			{Qheader:"供应商编码", header:$("vendor.code"), dataIndex:"vendorCode",tipable:true},
			{Qheader:"供应商名称", header:$("vendor.name"), dataIndex:"vendorName",width:250,tipable:true},
			{
				Qheader:"需求数量",
				header:$("sendscheduledetail.scheduleQty"), 
				dataIndex:"scheduleQty", 
				width:120,
                align:'right', 
                renderer:'rendererNumber'
			},
			{
				Qheader:"送货量", 
				header:$("sendscheduledetail.deliveryQty"),
				dataIndex:"deliveryQty", 
				width:140,
                align:'right', 
                renderer:'rendererNumber'
			},
			{
				Qheader:"在途量", 
				header:$("sendscheduledetail.onWayQty"), 
				dataIndex:"onWayQty", 
				width:140,
                align:'right', 
                renderer:'rendererNumber'
			},
			{
				Qheader:"收货量", 
				header:$("sendscheduledetail.receiptQty"), 
				dataIndex:"receiptQty",  
                align:'right', 
                renderer:'rendererNumber'
			},
			{
				Qheader:"退货量", 
				header:$("shoppingnoticedetail.returnGoodsQty"), 
				dataIndex:"returnGoodsQty", 
                align:'right', 
                hidden:true,
                renderer:'rendererNumber'
			},
			{
				Qheader:"可送货量", 
				header:$("porder.sendableQty"),
				hidden:true,
				dataIndex:"canSendQty", 
				width:130,
                align:'right', 
                renderer:'rendererNumber'
			},
			{Qheader:"来自主单排程单号编码", header:$("sendschedul.sendScheduleNo"), dataIndex:"sendScheduleNo",width:150,tipable:true},
			{Qheader:"来自主单采购订单号编码", header:$("porder.purchaseOrderNo"), dataIndex:"purchaseOrderNo",width:150,tipable:true},
			{Qheader:"行号", header:$("label.rowNo"), dataIndex:"rowIds"},
			{
				Qheader:"订单数量", 
				header:$("sendscheduledetail.sendQty1"), 
				dataIndex:"sendQty",
				width:120,
				align:'right', 
                renderer:'rendererNumber'
			},
			{Qheader:"单位编码", header:$("shoppingnoticedetail.unitCode"), dataIndex:"unitCode"},
			{Qheader:"工厂编码", header:$("plant.plantCode"), dataIndex:"plantCode",tipable:true},
			{Qheader:"库存地点编码", header:$("delivery.storageLocationCode"), dataIndex:"stockLocal",width:120},
			{Qheader:"采购组织编码", header:$("purchasingOrg.code"),dataIndex:"purchasingOrgCode",width:120,tipable:true},
			{Qheader:"采购组织名称", header:$("purchasingOrg.name"),dataIndex:"sendScheduleCommon.sendSchedule.purchasingOrgName",width:180,tipable:true},
			{Qheader: '创建人', header: $('label.createUserName'),dataIndex:"sendScheduleCommon.sendSchedule.createUserName",width:100,tipable:true},
			{Qheader: '创建日期', header: $('label.createTime'), dataIndex:"sendScheduleCommon.sendSchedule.createTime",renderer : 'rendererDateTime',width : 140,tipable:true},
			//hidden fields
			{Qheader:"排程细单ID",dataIndex:"sendScheduleDetailId",disabled:true},
			{Qheader:"排程中间表ID",dataIndex:"sendScheduleCommonId",disabled:true},
			{Qheader:"订单明细ID",dataIndex:"purchaseOrderDetailId",disabled:true},
			{Qheader:"sap采购订单号",dataIndex:"erpPurchaseOrderNo",disabled:true},
			{Qheader:"行号",dataIndex:"rowNo",disabled:true},
			{Qheader:"订单明细行号",dataIndex:"rowIds",disabled:true},
			{Qheader:"物料ID",dataIndex:"materialId",disabled:true},
			{Qheader:"单位编码",dataIndex:"unitCode",disabled:true},
			{Qheader:"单位名称",dataIndex:"unitName",disabled:true},
			{Qheader:"行项目类别",dataIndex:"lineItemTypeCode",disabled:true},
			{Qheader:"未税价",dataIndex:"taxPrice",disabled:true}
	  ],


	    /**
	     * @cfg {Integer} editWinFormHeight
	     * 编辑表单高度
	     */
	    ew_height:200,

	    /**
	     * @cfg {String} editWinFormColumnWidth
	     * 编辑表单列个数
	     */
	    ew_columnWidth :'0.5',

	    /**
	     * @cfg {boolean} editWinMaximized
	     * 是否最大化窗口，默认为否
	     */
	    maximized : true,

		/**
		 * @cfg {Array} editFormItems 
		 * 排程编辑form表单
		 */

	    ew_editFormItems: [],

		/**
		 * @cfg {Object} detailGrid 
		 * 排程明细
		 */
		grid1 : {},

		/**
		 * @cfg {Array} searchFormItems 
		 * 订单查询字段集合
		 */
		sw_searchFormItems : [{  
		fieldLabel:$("sendscheduledetail.scheduleTime"),
        name:"filter_GE_scheduleTime",
        xtype:"datefield",
        format:"Y-m-d"                                       
    },{
    	QfieldLabel:"至",
    	fieldLabel:$("label.to"),
        name:"filter_LT_scheduleTime",
    	format:"Y-m-d",
    	xtype:"datefield"
    },{  
		fieldLabel:$("label.createTime"),
        name:"filter_GE_sendScheduleCommon_sendSchedule_createTime",
        xtype:"datefield",
        format:"Y-m-d"                                       
    },{
    	QfieldLabel:"至",
    	fieldLabel:$("label.to"),
        name:"filter_LT_sendScheduleCommon_sendSchedule_createTime",
    	format:"Y-m-d",
    	xtype:"datefield"
    },{
		QfieldLabel:"物料编码",
		fieldLabel:$("materialInfo.code"),
        name:"filter_LIKE_materialCode"
    },{
		QfieldLabel:"物料名称",
		fieldLabel:$("materialInfo.name"),
        name:"filter_LIKE_materialName"
    },{
		QfieldLabel:"供应商编码",
		fieldLabel:$("vendor.code"),
        name:"filter_LIKE_vendorCode"
    },{
		QfieldLabel:"供应商名称",
		fieldLabel:$("vendor.name"),
        name:"filter_LIKE_vendorName"
    },{
		QfieldLabel:"采购订单号",
		fieldLabel:$("porder.purchaseOrderNo"),
        name:"filter_LIKE_purchaseOrderNo"
    },{
		QfieldLabel:"行号",
		xtype:"numberfield",
		minValue:0,
		fieldLabel:$("label.rowNo"),
        name:"filter_EQ_rowIds"
    },{
		QfieldLabel:"采购组织名称",
		fieldLabel:$("purchasingOrg.code"),
        name:"filter_LIKE_purchasingOrgCode"
    },{
		QfieldLabel:"采购组织编码",
		fieldLabel:$("purchasingOrg.name"),
        name:"filter_LIKE_purchasingOrgName"
    }, {
		fieldLabel : $('delivery.storageLocationCode'),
		name : 'filter_LIKE_stockLocal'
	}, {
		fieldLabel : $('label.createUserName'),
		name : 'filter_LIKE_sendScheduleCommon_sendSchedule_createUserName'
	},{   
        fieldLabel:$("sendscheduledetail.sendFlag"),
//        anchor: "100%",
        labelAlign:'right',
        xtype:"checkboxgroup", 
        columnWidth:1,
        defaults:{
           cls:'inline_checkbox'
        },
        items:[{boxLabel: $("sendscheduledetail.sendFlag0"), name: 'filter_IN_sendFlag',checked: true,inputValue:0},//未送货
               {boxLabel: $("sendscheduledetail.sendFlag1"), name: 'filter_IN_sendFlag',checked: true,inputValue:1},//部分送货
               {boxLabel: $("sendscheduledetail.sendFlag2"), name: 'filter_IN_sendFlag',checked: false,inputValue:2}//已送货
              ]
    }]
	},

	/**
	 * @cfg {Object} stores
	 * 相关store归集
	 * 
	 * - **taxRateStore** - 税率
	 * - **purchasingOrderTypeStore** - 采购订单类型
	 * - **companyStore** - 公司
	 * - **currencyStore** - 货币
	 * - **purchasingOrgStore** - 采购组织
	 * - **purchaseGroupStore** - 采购组
	 * - **flagStore** - 公共Boolean类型 
	 * - **lineItemTypeStore** - 行项目类别
	 * - **stockTypeStore** - 库存类型
	 * - **unitStore** - 单位
	 * - **plantStore** - 工厂,根据物料、采购组织进行查询
	 * - **storLocStore** - 库存地点
	 * - **pricingConditionTypeStore** - 定价条件类型
	 */
	stores: {
		//采购组织
	    porgStore:Ext.create("Ext.data.JsonStore",{
	    	proxy:{
	    	url: path_masterdata+"/md/purchasingorganization/getAll",
	        baseParams:{},
	        type:'ajax'
	    	},
	    	fields: ['porgcode', 'porgname'],
	        autoLoad:false
	    }), 
	    /**
	     * 采购组store
	     */
	    purchaseGroupStore :Ext.create("Ext.data.JsonStore",{
	    	proxy:{
	    	url: path_masterdata+"/md/purchasinggroup/getList.action",
	        baseParams:{},
	        type:'ajax'
	    	},
	    	fields: ['pgroupcode', 'pgroupname'],
	        autoLoad:false
	    }) 
}
		
		/**
		 * @method constructor
		 * 构造函数
		 */
		/*constructor: function(config){
			var me = this;
			//可以通过这种形式先调用父类的改方法返回对应值
			me.callParent([config]);
			var initStatesStr = "";
			if(config.initStates && config.initStates.length>0){
				 initStatesStr = initStatesStr.substring(1);
			}
			me.set("initStatesStr",initStatesStr);
			me.set('hideVpBtn',["delete","add","edit","view"]);
			me.set('vpListEditStateFn', []);
			me.set('editWinListeners', {});
			//设置交互
			me.set('vpSubTab', [[]]);
			me.set('gridStore',{
				idProperty:"sendScheduleDetailId",
				url:path_srm+"/cp/sendscheduledetail/list",
				sort:"sendScheduleNo",
			    dir:"desc",
			    autoLoad:false,
			    remoteSort: true,
			    listeners:{
//			        	"beforeload":'gridStoreBeforeLoad',
//			        	"load":'gridStoreLoad'
			      }	
		    });
		},*/

	/**
	 * @method getCfg
	 * 获取公共编辑窗口配置数据
	 * @return {object} cfg  内门户维护配置类
	 */
	/*getCfg: function() {
		var me = this;
		//可以通过这种形式先调用父类的改方法返回对应值
		var parentCfg = me.callParent();
		
		//配置文件
		var cfg = {
			isAudit: me.get('isAudit'), // 是否需要审核右键
			dealUrl: me.get('dealUrl'), // 各种操作的url地址
			moduleName: me.get('moduleName'), // 模块名称
			playListMode: me.get('playListMode'), // normal/audit/undeal //三种列表模式
			controllerClassName: me.get('controllerClassName'),
			vp: {
				//addOtherBtn: me.get('addVpBtn'), // 在固化的按钮基础上追加按钮
				column: me.get('gridColumn'), // 初始化列表
				sm: {
					singleSelect: me.get('singleSelect')
				},
				hideBtn: me.get('hideVpBtn'),
				store: me.get('gridStore'), // 初始化 列表store
				subTab: me.get('vpSubTab'),
				activeTab: me.get('activeTab'), // 默认展示哪儿tab
				tabHeight: me.get('tabHeight'), // tab 的高度设置
				triggerField: me.get('triggerField'), // 点击列表的哪一列显示对应的tab列表	
				billTypeCode: me.get('moduleCode'), // 模块类型编码
				rowdblclickFn:me.get('rowdblclickFn'),//是否双击
				baseParams: {
					initStates: me.get('initStatesStr'),
					methodName: me.get('methodName')
				},
//				menuOverride: me.get('menuOverride'),
				listEditStateFn: me.get('vpListEditStateFn'),
				gridCfg : {
					stateful : true,
					stateId :  s_userCode + '_SendScheduleDetail',
					stateHeader :true,
					forceFit : false,
					ableExporter:true,
					  *//**
	                 *  导出相关样式配置项
	                 *//*
	                 
	                exportConfig:{  
	                    tableHeaderStyle:{
	                        font: {
	                            fontName: 'Arial',
	                            family: 'Swiss',
	                            size: 11,
	                            color: '#1F497D'
	                        }
	                    }
	                }, 
	               // sm:true,//选中框隐藏
	                rn:true//序列列隐藏
				},
			},
			editWin: {
				addOtherBtn: me.get('addEditBtn'), //在固化的按钮基础上追加按钮
				nextBillState: me.get('nextBillState'), // 提交后下一步的状态
				maximized: me.get('editWinMaximized'), // 是否最大化窗口，默认为否
				listeners:{
					show:function(){
						
					},
					hide:function(){
						
					}
				},
				form: {
					items: me.get('editFormItems'), // 编辑表单字段
					columnWidth: me.get('editWinFormColumnWidth'), // 配置表单有几列
					height: me.get('editWinFormHeight')
				}
				
			},
			vpInstanceAfter: function() {
				var controller = me.getVp().getController();
				controller.vpInstanceAfertFn();
			},
			searchWin: {
				title: $('button.search'),
				isShowStatus: me.get('searchWinIsShowStatus'), // 是否显示查询状态
				height: me.get('searchWinHeight'),
				width: me.get('searchWinWidth'),
				form: {
					items: me.get('searchFormItems'), // 查询表单字段
					columnWidth: me.get('searchFormColumnWidth') // 配置表单有几列
				},
        		listeners:{
        			//search:'searchFn'
        		}
			}
		};

		Ext.apply(cfg, parentCfg);
		return cfg;
	}*/
});