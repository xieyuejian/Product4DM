/**
 * @class {Cp.schedule.SendScheduleViewModel} 
 * @extend {Ext.ux.app.ViewModel}
 * 送货排程
 */
Ext.define('Cp.schedule.SendScheduleViewModel',{
	extend:'Ext.ux.app.ViewModel',
    alias:'viewmodel.sendScheduleViewModel',
    requires:[
    	'Ext.ux.form.DateTimeField',
    	'Ext.ux.button.UploadButton',
    	'Ext.menu.Menu',
    	'Sl.masterdata.VendorSelectWin',
    	'Cp.schedule.PurchaseOrderDetailSelectWin',
        "Ext.srm.form.PurchasingOrganizationComboGrid",
    	"Ext.srm.form.VendorComboGrid",
    	"Ext.srm.ux.ChatTab",
    	"Ext.srm.ux.UxFileUtils"
    ],
  config:{ 
	  
    data: {	
    	/**
         * 单据状态
         * @param {} vm 配置对象
         * @return {Array}
         */
        billStatusFn:function(vm){ 
        	return [{
        		statusCode:"NEW",
        		statusName:$("status.new") //新建
        	},{
        		statusCode:"CONFIRM",
        		statusName:$("state.confirm") // 待审核
        	},{
        		statusCode:"NOPASS",
        		statusName:$("status.tonopass") //驳回
        	},{
        		statusCode:"PASS",
        		statusName:$("state.pass") //同意
        	},{
        		statusCode:"RELEASE",
        		statusName:$("state.release") //发布
        	},{
        		statusCode:"REFUSE",
        		statusName:$("state.refuse") //拒绝
        	},{
        		statusCode:"OPEN",
        		statusName:$("state.open") //执行
        	},{
        		statusCode:"CLOSE",
        		statusName:$("state.shut") //关闭
        	},{
        		statusCode:"CANCEL",
        		statusName:$("state.cancel") //取消
        	}] 
        },
        /**
         * 单据状态
         * @param {} vm 配置对象
         * @return {Array}
         */
        initStatusFn:function(vm){ 
        	return s_roleTypes.indexOf("V") >= 0 ? 
        	[{
        		statusCode:"RELEASE",
        		statusName:$("state.release") //发布
        	},{
        		statusCode:"REFUSE",
        		statusName:$("state.refuse") //拒绝
        	},{
        		statusCode:"OPEN",
        		statusName:$("state.open") //执行
        	},{
        		statusCode:"CLOSE",
        		statusName:$("state.shut") //关闭
        	},{
        		statusCode:"CANCEL",
        		statusName:$("state.cancel") //取消
        	}] : [{
        		statusCode:"NEW",
        		statusName:$("status.new") //新建
        	},{
        		statusCode:"CONFIRM",
        		statusName:$("state.confirm") // 待审核
        	},{
        		statusCode:"NOPASS",
        		statusName:$("status.tonopass") //驳回
        	},{
        		statusCode:"PASS",
        		statusName:$("state.pass") //同意
        	},{
        		statusCode:"RELEASE",
        		statusName:$("state.release") //发布
        	},{
        		statusCode:"REFUSE",
        		statusName:$("state.refuse") //拒绝
        	},{
        		statusCode:"OPEN",
        		statusName:$("state.open") //执行
        	},{
        		statusCode:"CLOSE",
        		statusName:$("state.shut") //关闭
        	},{
        		statusCode:"CANCEL",
        		statusName:$("state.cancel") //取消
        	}]
        },
		/**
		 * @cfg {String} dealUrl 
		 * 方法处理url
		 */
		dealUrl: path_srm + '/cp/sendschedule',
		
        isExtend:true,
		/**
		 * @cfg {String} moduleName 
		 * 模块名称
		 */
		moduleName: $('sendschedul.title'),

		/**
		 * @cfg {String} triggerField 
		 * 触发域（字段名）
		 */
		vp_triggerField: 'sendScheduleNo',
		
	    authorityNameArr:[ "sendschedule_topass","sendschedule_tonopass" ],

        chatTab : {
                xtype:'chatTab',
                /**
                 *  回复必传参数
                 *    "model.processKey" : billTypeCode,//单据编码 【固化】
                 *    "model.businessKey" : prepaymentsId,//选中的单据id【固化】
                 *    "moduleName":moduleName //模块名称,
                 *    "userId": record.get("createUserId") 记录创建者id 对应字段编码为createUserId【固化】
                 *  当有特殊情况请根据实际情况配置对应的参数映射字段
                 *     "userId": "xxxx" 记录创建者id
                 */
                paramsMapping:{
                   "userId" : "createUserId"
                },
                /**
                 *  回复参数动态处理
                 * @param {Object} vm 当前 viewModel
                 * @param {Object} params 回传参数集
                 * @param {record} record 选中回复的记录
                 * @param {String} handler 操作动作 add 新建| reply 回复
                 */
                paramsMappingFn:function(vm, params, record, handler){

                    return {};
                },
                /**
                 *  回复弹出窗口配置
                 * @param {Object} vm 当前 viewModel
                 * @param {Object} config 窗口配置参数集
                 * @param {record} record 选中回复的记录
                 * @param {String} handler 操作动作 add 新建| reply 回复
                 */
                configMappingFn:function(vm, config, record, handler){

                   return {};
                }

            }, 

		/**
		 * @cfg {String} moduleCode
		 * 质检单模块编码
		 */
		 vp_billTypeCode: 'PCD',

		/**
		 * @cfg {Array} vpSubTab 
		 * 列表底部tab集合
		 */
		 vp_subTab:[["grid1","grid2"],"logTab", "msgTab", "chatTab"],

		/**
		 * @cfg {Object} editWinCenterTab 
		 * 编辑窗体底部tab集合
		 */
	    ew_centerTab: {
	    	items:[['grid1', 'grid2']]
		},
		/**
		 * @cfg {Array} vpListEditStateFn 
		 * 列表界面按钮控制
		 */
		 vp_listEditStateFn: [
			{
				"edit":function(r){
					return (r.get('sendScheduleState') == 'NEW' || r.get('sendScheduleState') == "REFUSE" || r.get('sendScheduleState') == "NOPASS");// 新建\拒绝\审核不过
   				}
   			},{
				"view":true
   			},{
				"delete":function(r){
					return (r.get('sendScheduleState') == 'NEW' || r.get('sendScheduleState') == "REFUSE" || r.get('sendScheduleState') == "NOPASS");// 新建\拒绝\审核不过
   				}
   			},{
				"cancel":function(r){
   					return (r.get('sendScheduleState') == 'RELEASE' || r.get('sendScheduleState') == 'OPEN');// 执行或发布
   				}
   			},{
				"accept":function(r){
   					return r.get('sendScheduleState') == 'RELEASE';// 发布
   				}
   			},{
				"refuse":function(r){
   					return r.get('sendScheduleState') == 'RELEASE';// 发布
   				}
   			},{
				"change":function(r){
   					return (r.get('sendScheduleState') == 'OPEN');// 执行
   				}
   			},{
          		"grant": function(r){// 授权,审核状态下才允许使用
    				return (r.get('sendScheduleState') == 'CONFIRM');
    	  		}
           	},{
   	        	"prompttrial": function(r){
   	        		return (r.get('sendScheduleState') == 'CONFIRM');
   	        	}
   	        },{
				"revokeAudit":function(r){
   				 	return (r.get('sendScheduleState') == 'CONFIRM'&& s_userid == r.get("createUserId"));
   				}
   			} 				
		],

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

		/**
		 * @cfg {boolean} editWinMaximized
		 * 是否最大化窗口，默认为否
		 */
		maximized : true,

		/**
		 * @cfg {String} playListMode
		 * normal/audit/undeal //三种列表模式
		 */
		playListMode: 'normal',

		/**
		 * @cfg {String} controllerClassName
		 * 控制类类名称
		 */
		controllerClassName: 'Cp.schedule.SendScheduleController',

		/**
		 * @cfg {Boolean} searchWinIsShowStatus
		 * 查询窗体是否显示状态查询
		 */
		sw_isShowStatus: false, 

		/**
		 * @cfg {Integer} searchWinHeight
		 * 查询窗体高度
		 */
		sw_Height: 700,

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
		
	
			/**
		 * @cfg {Array} addVpBtn 
		 * 在固化的按钮基础上追加按钮
		 * - **changeState** - 变更状态
		 * - **check** - 撤销审批
		 * - **export** - 导出
		 * - **download** - 下载
		 * - **import** - 导入
		 */
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
		 },{
			 name:"change",
			 index:6,
			 Qtext:"强制变更",
			 text:$("button.change"),  
		     build:"#{power.change}",
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
            name: 'revokeAudit',
            text: "撤销审核",
            iconCls: 'icon-cancel',
            index: 9,
     		build:"#{power.revokeaudit}",
            handler: "revokeAuditHandler"
        }
			
		],

		/**
		 * @cfg {Array} addEditBtn 
		 * 在编辑界面固化的按钮基础上追加按钮
		 * - **changeState** - 变更状态
		 * - **check** - 撤销审批
		 * - **export** - 导出
		 * - **download** - 下载
		 * - **import** - 导入
		 */
	    ew_addEditBtn: [{
			text:$("button.accept"),
			index:4,
			name:'accept',
			Qtext:"接受",
			build:true,
			iconCls:"icon-accept2",
			displayType : [ "view" ],
			powerEffect : false,
			handler:'editBtnToAcceptFn'
		},{
			text:$("button.refuse"),  
		    name:"refuse",
			Qtext:"拒绝",
			index:5,
			build:true,
			iconCls:"icon-refuse",
			displayType : [ "view" ],
			powerEffect : false,
			handler:'editBtnToRefuseFn'
		}],

		/**
		 * @cfg {Object} gridStore 
		 * 列表Store
		 */
		 vp_gridStore: {
			idProperty:"sendScheduleId",
			url:"#{dealUrl}/list",
			sort:"sendScheduleNo",
		    dir:"desc",
		    remoteSort: true
		},
		vp_gridCfg:  {
			stateful : true,
			stateId : s_userCode + '_sendSchedule',
			stateHeader : true,
			forceFit : false,
			ableExporter:false,
			billNoField: "sendScheduleNo",
			 /**
               * 是否启用导出功能 true|false
             */
            ableExporter:true,
            rn:true//序列列隐藏
		},

		/**
		 * @cfg {Array} gridColumn 
		 * 列表对象 列属性配置项
		 */
		 vp_gridColumn: [  
  			{
				Qheader:"排程单号", 
				header:$("sendschedul.sendScheduleNo"),
				dataIndex:"sendScheduleNo",
				width:150,
				renderer:'rendererNo',
				tipable:true
			},
			{Qheader:"单据状态(0:新建;1:发布;2:拒绝;3:执行;4:取消;5:完成)", 
				header:$("sendschedule.billStatus"), 
				dataIndex:"sendScheduleState",
				renderer:'rendererStatus',
				exportRenderer: true
			},
			{header:$('purchasingOrg.code'),dataIndex:"purchasingOrgCode", width:120,tipable:true}, 
			{header:$('purchasingOrg.name'),dataIndex:"purchasingOrgName", width:250,tipable:true},
			{Qheader:"供应商编码", header:$("vendor.code"), dataIndex:"vendorErpCode",tipable:true},
			{Qheader:"供应商名称", header:$("vendor.name"), dataIndex:"vendorName", width:250,tipable:true},
			{Qheader:"排程日期", header:$("sendschedul.sendScheduleDate"), dataIndex:"sendScheduleDate",xtype: 'datecolumn',format: 'Y-m-d',exportRenderer:true},
			{Qheader:"创建人名称", header:$("label.createUser"), dataIndex:"createUserName",tipable:true},
			{Qheader:"创建时间", header:$("label.createTime"), dataIndex:"createTime",xtype: 'datecolumn',format: 'Y-m-d H:i:s',exportRenderer:true},
			{
				Qheader:"同步sap状态(0:未同步2:同步中1:已同步3:同步失败)", 
				header:$("erpSyn.erpSynState"), 
				dataIndex:"erpSynState",
				width:170,
				renderer: 'gridErpStateRender',
				exportRenderer: true,
				tipable:true,
				hidden:true
			},
			{Qheader:"修改时间", header:$("label.modifyTime"), dataIndex:"modifyTime",disabled:true,exportRenderer:true},
			{Qheader:"排程Id",  header:"排程Id",dataIndex:"sendScheduleId",disabled:true},
			{Qheader:"客户端编码",  header:$("bidEval.clientCode"),dataIndex:"clientCode",disabled:true},
			{Qheader:"Sap返回信息", header:$("sendschedul.synReturnMsg"), dataIndex:"erpReturnMsg",disabled:true},
			{Qheader:"拒绝原因",  header:$("contractAgreement.rejectReason"),dataIndex:"refuseReason",disabled:true},
			{Qheader:"创建人ID",  header:$("news.createUserId"),dataIndex:"createUserId",disabled:true},
			{Qheader:"修改人ID",  header:"修改人ID",dataIndex:"modifyUserId",disabled:true},
			{Qheader:"修改人名称", header:"修改人名称", dataIndex:"modifyUserName",disabled:true},
			{Qheader:"流程Key", header:"流程key", dataIndex:"procesKey",disabled:true}
	  ],

	    /**
	     * @cfg {Integer} editWinFormHeight
	     * 编辑表单高度
	     */
	    ew_height :160,

	    /**
	     * @cfg {String} editWinFormColumnWidth
	     * 编辑表单列个数
	     */
	    ew_columnWidth:'0.5',

	    

		/**
		 * @cfg {Array} editFormItems 
		 * 排程编辑form表单
		 */
		ew_editFormItems: [{
				QfieldLabel:"排程Id",
				fieldLabel:$("sendschedul.sendScheduleId"),
				name: "model.sendScheduleId",
				hidden:true
			},{
				QfieldLabel:"排程单号 ",
				fieldLabel:$("sendschedul.sendScheduleNo"),
				value: $("dict.autogeneration"),
				readOnly:true,
				name:"model.sendScheduleNo"
			},{ 
				QfieldLabel:"排程日期",
				fieldLabel:$("sendschedul.sendScheduleDate"),
				name:"model.sendScheduleDate", 
				xtype:"datefield",
				format:"Y-m-d",
				value:new Date()
			},/*{
				QfieldLabel:"采购组织编码<font color = 'red'>*</font>",
				fieldLabel:$("purchasingOrg.code")+"<font color = 'red'>*</font>",
				xtype:'uxcombo',
				name:"model.purchasingOrgCode",
			    hiddenName:"model.purchasingOrgCode",
			    innerTpl:true,
			    valueField:'purchasingOrgCode',
				displayField:"purchasingOrgName" ,  
				displayValue:'purchasingOrgCode',
			    store:me.getStore('purchasingOrgStore'),
				allowBlank:false,
			    listeners:{
			    	select: 'formPurchasingOrgCodeSelect'
			    }
			},*/
			/*{
				QfieldLabel: "采购组织编码",
				fieldLabel: $("companyPurchaseOrg.purchasingOrgCode") + "<font color = 'red'>*</font>",
				name: "model.purchasingOrgCode",
				hiddenName: 'model.purchasingOrgCode',
				xtype: "purchasingOrganizationComboGrid", 
	            allowBlank: false, 
	            editable:true, 
	            clearable:true,
	            enabletrigger:false,
	            minChars:2,
	            keypressParam:"filter_LIKE_purchasingOrgCode",
	            listeners:{
	            	"aftersetvalue": 'formPurchasingOrgCodeSelect'
			    }
			},*//*{
				QfieldLabel:"采购组织编码<font color = 'red'>*</font>",
				fieldLabel:$("purchasingOrg.code")+"<font color = 'red'>*</font>",
				xtype:'uxcombo',
				name:"model.purchasingOrgCode",
			    hiddenName:"model.purchasingOrgCode",
			    innerTpl:true,
			    valueField:'purchasingOrgCode',
				displayField:"purchasingOrgName" ,  
				displayValue:'purchasingOrgCode',
			    bind: {
				store: '{purchasingOrgStore}'
			     },
				allowBlank:false,
			    listeners:{
			    	select: 'formPurchasingOrgCodeSelect',
			    	clear:"formPurchasingOrgCodeClear"
			    }
			},{
				QfieldLabel:"采购组织名称",
				fieldLabel:$("purchasingOrg.name"),
				name:"model.purchasingOrgName",
				allowBlank:false,
				readOnly:true
			},*/
			{
                QfieldLabel: "采购组织编码",
                fieldLabel: $("purchasingOrg.code") + "<font color='red'>*</font>",
                name: "model.purchasingOrgCode",
                xtype: 'purchasingOrganizationComboGrid',
                hiddenName: "model.purchasingOrgCode",
                enabletrigger: true,
                selectWinCfg: {
                    autoLoad: true
                },
                clearable: true,
                editable: true,
                allowBlank: false,
	          	listeners : {
					aftersetvalue : 'formPurchasingOrgCodeSelect',
					aftercombosetvalue : 'formPurchasingOrgCodeSelect'
					
				}
            }, {
                QfieldLabel: '采购组织名称',
                fieldLabel: $('forecast.purchasingOrgName'),
                name: 'model.purchasingOrgName',
                readOnly: true
            },{
				fieldLabel : $('vendor.code') + '<font color="red">*</font>',
				name : 'model.vendorErpCode',
				xtype: "vendorComboGrid", 
	            allowBlank: false, 
	            editable:true, 
	            clearable:true,
	            enabletrigger:true,
	            minChars:1,
		        listeners: {
					//triggerselect: "vendorErpCodeTriggerselect",
					triggerbaseparams : "vendorErpCodeTriggerBaseParams"
					//aftersetvalue: "vendorErpCodeSetValueAfter"
				}
			},
			{QfieldLabel:"供应商名称",fieldLabel:$("vendor.name"),name:"model.vendorName",allowBlank:false,readOnly:true},
			// 隐藏字段
			{QfieldLabel:"客户端编码",name:"model.clientCode",hidden:true},
			{QfieldLabel:"单据状态 0:新建;1:发布、2:拒绝3:执行4:取消5:完成",name:"model.sendScheduleState",hidden:true},
			{QfieldLabel:"同步sap状态 0:未同步2:同步中1:已同步3:同步失败 ",name:"model.erpSynState",hidden:true},
			{QfieldLabel:"Sap返回信息",name:"model.erpReturnMsg",hidden:true},
			{QfieldLabel:"拒绝原因",name:"model.refuseReason",hidden:true},
			{QfieldLabel:"创建人ID",name:"model.createUserId",hidden:true},
			{QfieldLabel:"创建人名称",name:"model.createUserName",hidden:true},
			{QfieldLabel:"创建时间",name:"model.createTime",hidden:true,
				xtype:"datefield",
				format:"Y-m-d H:i:s"
			},
			{QfieldLabel:"修改人ID",name:"model.modifyUserId",hidden:true},
			{QfieldLabel:"修改人名称",name:"model.modifyUserName",hidden:true},
			{QfieldLabel:"修改时间",name:"model.modifyTime",hidden:true,
				xtype:"datefield",
				format:"Y-m-d H:i:s"
			},
			{QfieldLabel:"订单明细需全部排程",name:"model.isFull",hidden:true,submitValue:false},
			{QfieldLabel:"排程允许选择多个订单",name:"model.isMulti",hidden:true,submitValue:false},
			{QfieldLabel:"流程主键",name:"model.procesKey",hidden:true},
			{QfieldLabel:"供应商编码",fieldLabel:$("vendor.code"),name:"model.vendorCode",hidden:true}
		],

		/**
		 * @cfg {Object} detailGrid 
		 * 排程明细
		 */
		grid1 : {
	        region:"center",
	        height:300,
	        tabTitle:$("d_po_sendschedulecommon"),
	        xtype:'uxeditorgrid',
	        foreignKey:"sendSchedule_sendScheduleId",
	        tabClassName:"sendScheduleCommons", 
	        formFieldReadyArr:['model.vendorErpCode','model.purchasingOrgCode','model.sendScheduleDate'],// 细单有值时设置主单是否只读
	        sm:{singleSelect:true},
	        allowEmpty:false, // 添加明细条数时是否可以为空
	        stateHeader : true,
	        stateful : true,
			stateId : s_userCode + '_sendScheduleDtl',
	        viewConfig: {
	            forceFit:true,//当行大小变化时始终填充满
	            autoScroll:true
	        },
			cm:{
		       	rn:{
	                width:40
	            },
	            defaultSortable:false,
	            defaults:{ menuDisabled:true},
				columns:[
		         // /采购订单号、行号、物料编码、物料名称、单位编码、订单数量、确认交货日期、已排程量、可排程量、工厂编码、库存地点
				{Qheader:"采购细单ID", header:$("sendscheduledetail.purchaseOrderDetailId"), dataIndex:"purchaseOrderDetailId",hidden:true},
				{Qheader:"采购订单号", header:$("porder.purchaseOrderNo"),dataIndex:"purchaseOrderNo",width:150,hidden:true},
				{Qheader:"采购订单号", header:$("porder.purchaseOrderNo"),dataIndex:"erpPurchaseOrderNo",width:150},
				{Qheader:"行号", header:$("label.rowNo")+"<font color = 'red'>*</font>", dataIndex:"rowIds",width:80},
				{Qheader:"物料编码", header:$("materialInfo.code")+"<font color = 'red'>*</font>", dataIndex:"materialCode"},
				{Qheader:"物料名称", header:$("materialInfo.name")+"<font color = 'red'>*</font>", dataIndex:"materialName",width:150},
				{Qheader:"单位编码", header:$("shoppingnoticedetail.unitCode")+"<font color = 'red'>*</font>", dataIndex:"unitCode"},
				{
					Qheader:"订单数量", 
					header:$("sendscheduledetail.sendQty1")+"<font color = 'red'>*</font>", 
					dataIndex:"sendQty",
					align:'right',
					renderer: 'rendererNumber'
				},
				{Qheader :'确认交货日期', header:$("porder.vendorTime")+"<font color = 'red'>*</font>",
					dataIndex : 'vendorTime',
					width : 160,
					xtype: 'datecolumn',
					format: 'Y-m-d'
				},
				{
					Qheader:"已排程量",
					header:$("sendschedulecommon.scheduleQty")+"<font color = 'red'>*</font>",
					dataIndex:"scheduleQty",
					align:'right',
					renderer: 'rendererNumber'
				}, 
				{
			    	Qheader:"可排程量",
			    	header:$("sendscheduledetail.canSendQty")+"<font color = 'red'>*</font>", 
			    	dataIndex:"canSendQty",
			    	align:'right',
					renderer: 'rendererNumber'
			    },
				{Qheader:"工厂编码", header:$("plant.plantCode")+"<font color = 'red'>*</font>", dataIndex:"factoryCode"},
				{Qheader:"库存地点", header:$("label.storageLocation")+"<font color = 'red'>*</font>", dataIndex:"stockLocal"},
				
				// 隐藏字段
				{Qheader:"sap采购订单号", header:$("porder.erpPurchaseOrderNo"), dataIndex:"erpPurchaseOrderNo",hidden:true},
				{Qheader:"单位名称", header:$("sendscheduledetail.unitName"), dataIndex:"unitName",hidden:true}, 
				{Qheader:"工厂名称", header:$("sendscheduledetail.factoryName"), dataIndex:"factoryName",hidden:true},
				{Qheader:"行项目类别", header:"行项目类别", dataIndex:"lineItemTypeCode",hidden:true},
				{Qheader:"供应商编码", header:$("vendor.code"), dataIndex:"vendorCode",hidden:true},
				{Qheader:"供应商erp编码", header:$("vendor.vendorErpCode"),dataIndex:"vendorErpCode",hidden:true},
				{Qheader:"供应商名称", header:$("vendor.name"), dataIndex:"vendorName",width:150,hidden:true},
				{Qheader:"物料ID", header:$("censorQuality.materialId"), dataIndex:"materialId",hidden:true},
		     	{Qheader:"排程中间表ID", header:$("sendschedulecommon.sendScheduleCommonId"), dataIndex:"sendScheduleCommonId",hidden:true},
				{Qheader:"排程主表ID", header:$("sendschedulecommon.sendScheduleId"), dataIndex:"sendSchedule.sendScheduleId",hidden:true}, 
			 	{Qheader:"订单ID", header:$("porder.purchaseOrderId"), dataIndex:"purchaseOrderId",hidden:true}, 
				{Qheader:"采购员ID", header:$("shoppingnoticedetail.buyerId"), dataIndex:"buyerId",hidden:true},
				{Qheader:"采购员名称", header:$("porder.buyer"), dataIndex:"buyerName",hidden:true}, 
				{Qheader:"公司名称", header:$("company.name"),dataIndex:"companyCode",hidden:true},
				{Qheader:"隐藏已排程", header:"隐藏已排程",dataIndex:"schdule",hidden:true},
				{Qheader:"隐藏可排程", header:"隐藏可排程",dataIndex:"unSchdule",hidden:true}
			]
			},
	        listeners:{
	            "afterValid" : 'grid1AfterValidFn'
	        },	
	        pageSize:0,
	        store:{
				idProperty:"sendScheduleCommonId",
				url:path_srm+"/cp/sendschedule/findsendschedulecommonall",
				sort:"sendScheduleCommonId",
				autoLoad:false,
				dir:"desc"
	        },
	        tbar:[{
				Qtext:"添加",
	            text:$("button.add"),
	            name:'add',
	            iconCls:"icon-add",
	            handler:'grid1AddHandler'
	        },{
	            Qtext:"删除",
				text:$("button.delete"),
				name:'delete',
	            iconCls:"icon-delete",
	            handler:'grid1DeleteHandler'
	        },{
				Qtext:"导入",
				text:$("button.import"),
				name:"import",
				iconCls:"icon-putin",
				handler:'vpImportHandler'
			},{
				Qtext:"模板下载",
				text:$("button.download"),
				name:"download",
				iconCls:"icon-download",
				handler:'grid1DownloadHandler'
			}]
	    },

		/**
		 * @cfg {Object} pricingGrid 
		 * 细细单
		 */
		grid2: {
	        region:"center",
	        height:300,
	        tabTitle:$("d_po_sendscheduledetail"),
	        xtype:'uxeditorgrid',
	        foreignKey:"sendScheduleCommon_sendScheduleCommonId",
	        tabClassName:"sendScheduleDetails",
	        saveField:['sendScheduleDetailId','sendScheduleId','purchaseOrderId','purchaseOrderDetailId','buyerId','buyerName','buyerCode','sendScheduleNo','purchaseOrderNo','rowNo','vendorCode','vendorName','sendFlag','materialId','materialCode','materialName','unitCode',
	                   'rowIds','unitName','scheduleTime','scheduleQty','sendQty','deliveryQty','onWayQty','receiptQty','returnGoodsQty','canSendQty','plantCode','factoryName','stockQty','stockLocal','createTime','modifyTime','lineItemTypeCode','purchasingOrgCode','purchasingGroupCode',
	                   'sapPurchaseOrderNo','vendorErpCode','companyCode','taxPrice','oldScheduleQty','oldScheduleTime'],// 需要提交的细单字段,
	        validField:['scheduleQty','scheduleTime'],// 需要验证的字段
	        validStartIndex:1,
	        sm:{singleSelect:false},
	        stateHeader : true,
	        stateful : true,
			stateId : s_userCode + '_sendScheduleDtlDtl',
	        forceFit:true,
	        viewConfig: {
	            forceFit:true// 当行大小变化时始终填充满
	        },
			cm:{
	            defaultSortable:false,
	            defaults:{ menuDisabled:true},
				columns:[	
	         	{Qheader:"排程细单ID", header:$("sendscheduledetail.sendScheduleDetailId"), dataIndex:"sendScheduleDetailId",hidden:true},
				{Qheader:"排程中间表ID", header:$("sendschedulecommon.sendScheduleCommonId"), dataIndex:"sendScheduleCommon.sendSchedulecommonId",hidden:true},
				{Qheader:"订单ID", header:$("porder.purchaseOrderId"), dataIndex:"purchaseOrderId",hidden:true},
				{Qheader:"采购细单ID", header:$("sendscheduledetail.purchaseOrderDetailId"), dataIndex:"purchaseOrderDetailId",hidden:true},
				{Qheader:"采购员ID", header:$("shoppingnoticedetail.buyerId"), dataIndex:"buyerId",hidden:true},
				{Qheader:"采购员名称", header:$("porder.buyer"), dataIndex:"buyerName",hidden:true},
				{Qheader:"采购员编码", header:$("porder.buyerBusinessCode"), dataIndex:"buyerCode",hidden:true},
				{Qheader:"来自主单排程单号编码", header:$("sendschedul.sendScheduleNo"), dataIndex:"sendScheduleNo",hidden:true},
				{Qheader:"来自主单采购订单号编码", header:$("porder.purchaseOrderNo"), dataIndex:"purchaseOrderNo",hidden:true},
				{Qheader:"行号", header:$("label.rowNo"), dataIndex:"rowNo",hidden:true},
				{Qheader:"行号", header:$("label.rowNo"), dataIndex:"rowIds",hidden:true},
				{Qheader:"供应商编码", header:$("vendor.code"), dataIndex:"vendorCode",hidden:true},
				{Qheader:"供应商名称", header:$("vendor.name"), dataIndex:"vendorName",hidden:true},
				{Qheader:"物料ID", header:$("censorQuality.materialId"), dataIndex:"materialId",hidden:true},
				{Qheader:"物料编码", header:$("materialInfo.code"), dataIndex:"materialCode",hidden:true},
				{Qheader:"物料名称", header:$("materialInfo.name"), dataIndex:"materialName",hidden:true},
				{Qheader:"单位编码", header:$("shoppingnoticedetail.unitCode"), dataIndex:"unitCode",hidden:true},
				{Qheader:"单位名称", header:$("sendscheduledetail.unitName"), dataIndex:"unitName",hidden:true},
				{
					Qheader:"需求时间", 
					header:$("sendscheduledetail.scheduleTime"), 
					dataIndex:"scheduleTime",
					customAttr:{
						editable:true//是否显示可以编辑背景颜色 
					},
					editor:{
	                    xtype:"datetimefield",  
	                    allowBlank:false,
	                    name:"startDate",
	                    format:"Y-m-d H:i:s",
	                    filter:true
	                },
	                renderer:'rendererDateTime'
				},         
				{
					Qheader:"需求数量",
					header:$("sendscheduledetail.scheduleQty"), 
					dataIndex:"scheduleQty",
					customAttr:{
						editable:true,//是否显示可以编辑背景颜色 
						allowBlank:false
					},
					editor:{
	                    xtype:"numberfield",
	                    decimalPrecision:3,
	                    name:"scheduleQty",
	                    minValue:0.0001
	                },
	                align:'right',
	                renderer:'gridEditScheduleQtyRenderer'
				},
				{
					Qheader:"送货标识 0:未送货1:部分送货2:已送货 ", 
					header:$("sendscheduledetail.sendFlag"),
					dataIndex:"sendFlag",
					renderer:'gridEditSendFlagRenderer'
				},
				{header:"可删除标识",dataIndex:"deleteFlag",disabled:true},// 新添加的可以删除，之前的不能删除
				{Qheader:"订单数量", header:$("sendscheduledetail.sendQty"), dataIndex:"sendQty",hidden:true}, 
				{Qheader:"未税价", header:$("label.notTaxPrice"), dataIndex:"taxPrice",hidden:true},
				{Qheader:"送货量", header:$("sendscheduledetail.deliveryQty"), dataIndex:"deliveryQty",hidden:true},
				{Qheader:"在途量", header:$("sendscheduledetail.onWayQty"), dataIndex:"onWayQty",hidden:true},
				{Qheader:"收货量", header:$("sendscheduledetail.receiptQty"), dataIndex:"receiptQty",hidden:true},
				{Qheader:"退货量", header:$("shoppingnoticedetail.returnGoodsQty"), dataIndex:"returnGoodsQty",hidden:true},
				{Qheader:"可送货量", header:$("sendscheduledetail.canSendQty"), dataIndex:"canSendQty",hidden:true},
				{Qheader:"工厂编码", header:$("plant.plantCode"), dataIndex:"plantCode",hidden:true},
				{Qheader:"工厂名称", header:$("sendscheduledetail.factoryName"), dataIndex:"factoryName",hidden:true},
				{Qheader:"库存", header:$("sendscheduledetail.stockQty"), dataIndex:"stockQty",hidden:true},
				{Qheader:"库存地点", header:$("label.storageLocation"), dataIndex:"stockLocal",hidden:true}, 
				{Qheader:"行项目类别", header:"行项目类别", dataIndex:"lineItemTypeCode",hidden:true}, 
				{Qheader:"sap采购订单号", header:$("sendscheduledetail.sapPurchaseOrderNo"),dataIndex:"sapPurchaseOrderNo",hidden:true},
				{Qheader:"供应商erp编码", header:$("vendor.vendorErpCode"),dataIndex:"vendorErpCode",hidden:true},
				{Qheader:"公司名称", header:$("company.name"),dataIndex:"companyCode",hidden:true},
				{Qheader:"采购组织编码", header:$("purchasingOrg.code"),dataIndex:"purchasingOrgCode",hidden:true},
				{Qheader:"采购组织名称", header:$("purchasingOrg.name"),dataIndex:"purchasingOrgName",hidden:true},
				{Qheader:"采购组编码", header:$("purchasingGroup.code"), dataIndex:"purchasingGroupCode",hidden:true},
				{Qheader:"旧的需求数量", header:$("sendscheduledetail.scheduleQty"),dataIndex:"oldScheduleQty",hidden:true},
				{Qheader:"旧的需求时间", header:$("sendscheduledetail.scheduleTime"), dataIndex:"oldScheduleTime",hidden:true}
			]
			},
			listeners:{
				'edit': 'grid2DtlEdit',
				'beforeedit': 'grid2DtlBeforeedit',
				"afterValid": 'grid2DtlAfterValid',
				"cellclick": 'grid2DtlCellclick'
	        },			
	        pageSize:0, 
	        store:{
				idProperty:"sendScheduleDetailId",
				url:path_srm+"/cp/sendschedule/findsendscheduledetailall",
				sort:"sendScheduleDetailId",
				autoLoad:false,
				dir:"desc"
	        },
	        tbar:[{
				Qtext:"添加",
	            text:$("button.add"),
	            name:"add",
	            iconCls:"icon-add",
	            handler:'grid2AddChildDetail'
	        },{
	            Qtext:"删除",
				text:$("button.delete"),
				name:"delete",
	            iconCls:"icon-delete",
	            handler:'grid2DeleteHandler'
	        }]
	    },

		
		/**
		 * @cfg {Array} searchFormItems 
		 * 订单查询字段集合
		 */
		sw_searchFormItems : [{
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
        },{
            QfieldLabel: '创建人名称',
            fieldLabel: $('label.createUserName'),
            name: 'filter_LIKE_createUserName'
        },{
			fieldLabel:$('sample.status'),
			xtype:"checkboxgroup",
			columns:5,
			height:50,
			labelAlign:'right',
			columnWidth:1,
			items:[{
					boxLabel:$('state.new'),
					name:'sendScheduleState',
					inputValue:"NEW"
				}, {
					boxLabel:$('state.release'),
					name:'sendScheduleState',
					inputValue:"RELEASE"
				}, {
					boxLabel:$('porder.purchaseOrderStateOpen'),
					name:'sendScheduleState',
					inputValue:"OPEN"
				}, {
					boxLabel:$('state.refuse'),
					name:'sendScheduleState',
					inputValue:"REFUSE"
				}, {
					boxLabel:$('state.cancel'),
					name:'sendScheduleState',
					inputValue:"CANCEL"
				},{
					boxLabel:$('state.confirm'),
					name:'sendScheduleState',
					inputValue:"CONFIRM"
				},{
					boxLabel:$('state.nopass'),
					name:'sendScheduleState',
					inputValue:"NOPASS"
				}
				]
		},{   
	        fieldLabel:$("erpSyn.erpSynState"),
	        xtype:"checkboxgroup", 
	        columns:5,
	        labelAlign:'right',
	        columnWidth:1,
	        items:[{boxLabel:$("erpSyn.nosyn"), name: 'filter_IN_erpSynState',checked: false,inputValue:0},
	               /*{boxLabel:$("erpSyn.onsyn"), name: 'filter_IN_erpSynState',checked: false,inputValue:1},*/
	               {boxLabel:$("erpSyn.synsuccess"), name: 'filter_IN_erpSynState',checked: false,inputValue:1},
	               {boxLabel:$("erpSyn.synfail"), name: 'filter_IN_erpSynState',checked: false,inputValue:3,width:120}
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
		// 采购组织
	    purchasingOrgStore :Ext.create("Ext.data.JsonStore",{
	    	proxy:{
				url:path_masterdata + '/md/purchasingorganization/getall',
				type:'ajax'
	    	},
			fields:[{
					name:'purchasingOrgCode',
					type:'string'
				}, {
					name:'purchasingOrgName',
					type:'string'
				}],
		    listeners:{
				load:'purchasingOrgStoreLoad'
		    },
		  	autoLoad:true
		}), 
		// 库存地点
		storageLocationStore : Ext.create("Ext.data.JsonStore",{
			proxy:{
				url : path_masterdata + "/md/storagelocation/findall",
				type:'ajax'
			},
			fields : [ "locationcode", "locationname", "plantcode" ]
		}),
		/**
		 * 工厂
		 */
		plantStore : Ext.create("Ext.data.JsonStore",{
			proxy:{
				url : path_masterdata + "/md/materialinfo/getcheckplant",
				type:'ajax',
				fields : [ 'materialCode', 'plantCode', {
					name : 'codename',
					convert : function(v, r) {
						return r.plantCode;
					}
				} ],
				autoLoad : true
			}
		}),
	    /*
		 * 删除排程明细时暂存store
		 */
	    tempDeleteStore : Ext.create("Ext.data.JsonStore",{
				 fields:['sendScheduleCommonId','purchaseOrder.purchaseOrderNo','rowIds','purchaseOrder.vendorCode','purchaseOrder.vendorName','materialId',
				         'materialCode','materialName','unitCode','unitName','vendorQty','vendorTime','plantCode','plantName','storeLocal',
				         'lineItemTypeCode','qtyArrive','qtyQuit','qtySend','scheduleQty','qtyOnline','purchaseOrder.purchaseOrderId','purchaseOrder.buyer','purchaseOrder.buyerId',
				         'purchaseOrderDetailId','taxPrice','purchaseOrder.erpPurchaseOrderNo'
				         ]
		})
       
	}
  }
});