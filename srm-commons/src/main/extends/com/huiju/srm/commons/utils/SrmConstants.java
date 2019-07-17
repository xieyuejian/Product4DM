package com.huiju.srm.commons.utils;

import com.huiju.module.config.GlobalParameters;

/**
 * srm常量
 * 
 * @author chenyx
 * 
 */
public abstract class SrmConstants {

	/** 管理员角色 */
	public final static String ROLETYPE_A = "A";
	/** 采购角色 */
	public final static String ROLETYPE_B = "B";
	/** 供应商角色 */
	public final static String ROLETYPE_V = "V";
	/** 客户角色 */
	public final static String ROLETYPE_C = "C";

	/** 采购订单 */
	public final static String BILLTYPE_CGD = "CGD";
	/** 送货单 */
	public final static String BILLTYPE_ASN = "ASN";
	/** 外贸询价单 */
	public final static String BILLTYPE_WXJ = "WXJ";
	/** 外贸报价单 */
	public final static String BILLTYPE_WBJ = "WBJ";
	/** 外贸销售客户订单 */
	public final static String BILLTYPE_WXD = "WXD";
	/** 外贸出口合同 */
	public final static String BILLTYPE_WHT = "WHT";
	/** 送检质检单 */
	public final static String BILLTYPE_ZJD = "ZJD";
	/** 入库单 */
	public final static String BILLTYPE_RKD = "RKD";
	/** 退货单 */
	public final static String BILLTYPE_THD = "THD";
	/** 绩效考核单 */
	public final static String BILLTYPE_JXD = "JXD";
	/** 样品单 */
	public final static String BILLTYPE_YPD = "YPD";
	/** 送样单 */
	public final static String BILLTYPE_SYD = "SYD";
	/** 采购预测 */
	public final static String BILLTYPE_CGY = "CGY";
	/** 质检单反馈单 第一个审核流程，新建后提交的质量问题反馈进行审核 */
	public final static String BILLTYPE_QUD = "QUD";
	/** 质检单反馈单 第二个审核流程，对品质跟踪后的质量问题反馈进行审核 */
	public final static String BILLTYPE_QPD = "QPD";
	/** 价格体系单 */
	public final static String BILLTYPE_JGD = "JGD";
	/** 供应商数据 */
	public final static String BILLTYPE_V = "V";
	/** 注册供应商数据 */
	public final static String BILLTYPE_ZSR = "ZSR";
	/** 潜在供应商数据 */
	public final static String BILLTYPE_ZSP = "ZSP";
	/** 合格供应商数据 */
	public final static String BILLTYPE_ZSQ = "ZSQ";
	/** 对账单 */
	public final static String BILLTYPE_DZD = "DZD";
	/** 预制发票 */
	public final static String BILLTYPE_YZD = "YZD";
	/** 税控发票 */
	public final static String BILLTYPE_SKD = "SKD";
	/** 收货单 */
	public final static String BILLTYPE_SHD = "SHD";
	/** 询价单 */
	public final static String BILLTYPE_XJD = "XJD";
	/** 报价单 */
	public final static String BILLTYPE_BJD = "BJD";
	/** 比价单 */
	public final static String BILLTYPE_PJD = "PJD";
	/** 询报价定价单 */
	public final static String BILLTYPE_DJD = "DJD";
	/** 招标单 */
	public final static String BILLTYPE_ZBD = "ZBD";
	/** 投标单 */
	public final static String BILLTYPE_TBD = "TBD";
	/** 评标单 */
	public final static String BILLTYPE_PBD = "PBD";
	/** 竞拍 */
	public final static String BILLTYPE_JPZ = "JPZ";
	/** 定标单 */
	public final static String BILLTYPE_DBD = "DBD";
	/** 合同条款 */
	public final static String BILLTYPE_HTT = "HTT";
	/** 合同 */
	public final static String BILLTYPE_HT = "HT";
	/** 导出配置 */
	public final static String BILLTYPE_EC = "EC";
	/** 补充协议 */
	public final static String BILLTYPE_HTB = "HTB";
	/** 货源清单 */
	public final static String BILLTYPE_SCL = "SCL";
	/** 到货通知单 */
	public final static String DHD = "DHD";
	/** 授权管理 */
	public final static String BILLTYPE_AU = "AU";
	public static final String processStartUserId = "processStartUserId";
	/**
	 * 供应商绩效考核
	 */
	public final static String BILLTYPE_PERFORMANCE = "PERFORMANCE";
	/**
	 * 测试产品管理(curd)
	 */
	public final static String BILLTYPE_TESTP = "TSP";
	/**
	 * 测试招标单管理管理(一对多再对多)
	 */
	public final static String BILLTYPE_TESTZBD = "TZB";
	/**
	 * 测试招标单管理管理(一对多)
	 */
	public final static String BILLTYPE_TESTXQ = "TXQ";
	/**
	 * 排程单
	 */
	public final static String BILLTYPE_PCD = "PCD";
	/**
	 * 排程单变更
	 */
	public final static String BILLTYPE_PCDCHANGE = "PCDCHANGE";
	/**
	 * 物料需求看板主数据
	 */
	public final static String BILLTYPE_MRB = "MRB";
	/**
	 * 技术品质
	 */
	public final static String BILLTYPE_TQ = "TQ";
	/**
	 * 整改通知单
	 */
	public final static String BILLTYPE_ZGD = "ZGD";
	/**
	 * 整改回复单
	 */
	public final static String BILLTYPE_ZGH = "ZGH";
	/**
	 * 整改确认单
	 */
	public final static String BILLTYPE_ZGQ = "ZGQ";
	/**
	 * 供应商等级变更单
	 */
	public final static String BILLTYPE_VGC = "VGC";
	/**
	 * 供应商冻结解冻
	 */
	public final static String BILLTYPE_VST = "VST";
	/**
	 * 实地评鉴模板
	 */
	public final static String BILLTYPE_FEF = "FEF";
	/**
	 * 实地评鉴
	 */
	public final static String BILLTYPE_FEU = "FEU";
	/**
	 * 配额申请
	 */
	public final static String BILLTYPE_QP = "QP";
	/**
	 * 收货单的物料凭证号
	 */
	public final static String BILLTYPE_MCC = "MCC";
	/** 出库单 */
	public final static String BILLTYPE_CKD = "CKD";
	/** 盘点单 */
	public final static String BILLTYPE_CHE = "CHE";
	/** 调拨单 */
	public final static String BILLTYPE_ALO = "ALO";
	/** 供应商全部信息变更 */
	public final static String BILLTYPE_VALL = "VALL";
	/** 供应商基本信息变更 */
	public final static String BILLTYPE_VBASE = "VBASE";
	/** 供应商物料组信息变更 */
	public final static String BILLTYPE_VMATERIAL = "VMATERIAL";
	/** 供应商公司信息变更 */
	public final static String BILLTYPE_VCOMPANY = "VCOMPANY";
	/** 供应商采购组织信息变更 */
	public final static String BILLTYPE_VPORG = "VPORG";
	/** 供应商引入 */
	public final static String BILLTYPE_VIN = "VIN";
	/** 供应商主数据 */
	public final static String BILLTYPE_VMD = "VMD";
	/** 价格主数据 */
	public final static String BILLTYPE_PMD = "PMD";
	/** 价格申请 */
	public final static String BILLTYPE_MMP = "MMP";
	/** 价格冻结解冻 */
	public final static String BILLTYPE_MFP = "MFP";
	/** 新品推荐 */
	public final static String BILLTYPE_NPR = "NPR";
	/** 采购申请 */
	public final static String BILLTYPE_PR = "PR";
	/** 竞价 */
	public final static String BILLTYPE_EBO = "EBO";

	// ======================主数据管理========================
	/** 计量单位 */
	public final static String BILLTYPE_UNIT = "UNIT";
	/** 货币信息 */
	public final static String BILLTYPE_CURRENCY = "CURRENCY";
	/** 汇率 */
	public final static String BILLTYPE_EXRATE = "EXRATE";
	/** 税率 */
	public final static String BILLTYPE_TAXRATE = "TAXRATE";
	/** 付款条件 */
	public final static String BILLTYPE_PAYTERM = "PAYTERM";
	/** 付款方式 */
	public final static String BILLTYPE_PAYTYPE = "PAYTYPE";
	/** 银行信息 */
	public final static String BILLTYPE_BAINFO = "BAINFO";
	/** 合同类型 */
	public final static String BILLTYPE_CONTYPE = "CONTYPE";
	/** 贸易条件 */
	public final static String BILLTYPE_TRTERM = "TRTERM";
	/** 供应商类型 */
	public final static String BILLTYPE_VETYPE = "VETYPE";
	/** 数据分组 */
	public final static String BILLTYPE_DAGROUP = "DAGROUP";
	/** 数据字典 */
	public final static String BILLTYPE_DADICT = "DADICT";
	/** 账户组 */
	public final static String BILLTYPE_AGSET = "AGSET";
	/** 物料类型 */
	public final static String BILLTYPE_MATYPE = "MATYPE";
	/** 物料组 */
	public final static String BILLTYPE_MAGROUP = "MAGROUP";
	/** 物料信息 */
	public final static String BILLTYPE_MATERIAL = "MATERIAL";
	/** 集团 */
	public final static String BILLTYPE_GROUP = "GROUP";
	/** 公司 */
	public final static String BILLTYPE_COMPANY = "COMPANY";
	/** 工厂 */
	public final static String BILLTYPE_PLANT = "PLANT";
	/** 库存地点 */
	public final static String BILLTYPE_STLOCA = "STLOCA";
	/** 采购组 */
	public final static String BILLTYPE_PUGROUP = "PUGROUP";
	/** 采购组织 */
	public final static String BILLTYPE_PUORGAN = "PUORGAN";
	/** 公司分配采购组织 */
	public final static String BILLTYPE_CPO = "CPO";
	/** 工厂分配采购组织 */
	public final static String BILLTYPE_PPO = "PPO";

	// ======================新招投标==========================
	/** 保证金处理单 */
	public final static String BILLTYPE_MOB = "MOB";
	/** 保证金缴纳单 */
	public final static String BILLTYPE_MPB = "MPB";
	/** 异常处理单 */
	public final static String BILLTYPE_EDB = "EDB";
	/** 关差单 */
	public final static String BILLTYPE_SAB = "SAB";
	/** 关差物资申请单 */
	public final static String BILLTYPE_SMB = "SMB";
	/** 招标单 */
	public final static String BILLTYPE_BID = "BID";
	/** 投标单 */
	public final static String BILLTYPE_TEND = "TEND";
	/** 供应商确认 */
	public final static String BILLTYPE_VCF = "VCF";
	/** 投标资格 */
	public final static String BILLTYPE_CON = "CON";
	/** 评标专家确认 */
	public final static String BILLTYPE_ECF = "ECF";
	/** 延标单 */
	public final static String BILLTYPE_DEB = "DEB";
	/** 废标单 */
	public final static String BILLTYPE_ABB = "ABB";
	/** 跟标单 */
	public final static String BILLTYPE_DIS = "DIS";
	/** 立项单 */
	public final static String BILLTYPE_PRO = "PRO";
	/** 评标单 */
	public final static String BILLTYPE_BED = "BED";
	/** 定标单 */
	public final static String BILLTYPE_BSD = "BSD";
	/** 评估体系 */
	public final static String BILLTYPE_EVS = "EVS";
	/** 考核指标 */
	public final static String BILLTYPE_INI = "INI";

	// =============其他业务类型状态=============
	/** 专家状态-在线/线上 */
	public final static String STATUS_ONLINE = "ONLINE";
	/** 专家状态-离线 /线下 */
	public final static String STATUS_OFFLINE = "OFFLINE";

	// =============操作日志动作标识符=============
	/** 单据保存 */
	public final static String PERFORM_SAVE = "save";
	/** 单据编辑 */
	public final static String PERFORM_EDIT = "edit";
	/** 单据提交 */
	public final static String PERFORM_AUDIT = "audit";
	/** 单据删除 */
	public final static String PERFORM_DELETE = "delete";
	/** 单据提交审核 */
	public final static String PERFORM_TOCONFIRM = "toConfirm";
	/** 单据审核通过 */
	public final static String PERFORM_TOPASS = "toPass";
	/** 单据审核不过 */
	public final static String PERFORM_TONOPASS = "toNoPass";
	/** 接受 */
	public final static String PERFORM_ACCEPT = "accept";
	/** 拒绝 */
	public final static String PERFORM_REFUSE = "refuse";
	/** 关闭 */
	public final static String PERFORM_TOCLOSE = "toClose";
	/** 取消 */
	public final static String PERFORM_TOCANCEL = "toCancel";
	/** 关闭 */
	public final static String PERFORM_CLOSE = "close";
	/** 取消 */
	public final static String PERFORM_CANCEL = "cancel";
	/** 复制 */
	public final static String PERFORM_COPY = "copy";
	/** 发布 */
	public final static String PERFORM_PUBLISH = "publish";
	/** 发布 */
	public final static String PERFORM_TORELEASE = "toRelease";
	/** 议价 */
	public final static String PERFORM_BARGAINING = "bargaining";
	/** 执行 */
	public final static String PERFORM_TOOPEN = "toOpen";
	/** 完成 */
	public final static String PERFORM_TOCOMPLETE = "toComplete";
	/** 评分 */
	public final static String PERFORM_TOSCORE = "toScore";
	/** 整改完成 */
	public final static String PERFORM_TOCORRCOMP = "toCorrComp";
	/** 撤销整改完成 */
	public final static String PERFORM_TOCORRING = "toCorrIng";
	/** 确认整改到位 */
	public final static String PERFORM_TOAFFIVALI = "toAffiVali";
	/** 确认整改不到位 */
	public final static String PERFORM_TOAFFIINVALI = "toAffiInVali";
	/** 同步 */
	public final static String PERFORM_SYNC = "sync";
	/** 冻结 */
	public final static String PERFORM_FREEZE = "freeze";
	/** 启用 */
	public final static String PERFORM_ENABLE = "enable";
	/** 收货 */
	public final static String PERFORM_DELIED = "deliEd";
	/** 撤销 */
	public final static String PERFORM_REVOKE = "revoke";
	/** 撤销 */
	public final static String PERFORM_CONFIG = "config";
	/** 配置 */
	public final static String PERFORM_CONFIGSAVE = "configSave";
	/** 变更 */
	public final static String PERFORM_CHANGE = "change";
	/** 截止 */
	public final static String PERFORM_TOEND = "toend";
	/** 开标 */
	public final static String PERFORM_START = "start";
	/** 延标 */
	public final static String PERFORM_DELAY = "delay";
	/** 废标 */
	public final static String PERFORM_ABOLISH = "abolish";
	/** 发票验证 */
	public final static String PERFORM_CHECK = "check";
	/** 发票验冲销 */
	public final static String PERFORM_WRITEOFF = "writeOff";
	/** 导入 */
	public final static String PERFORM_IMPORT = "import";
	/** 点收 */
	public final static String PERFORM_COLLECTPOINTS = "collectpoints";
	/** 评分提交 */
	public final static String PERFORM_SCOREAUDIT = "scoreAudit";
	// ==========操作日志终端标识符==================
	/** web端 */
	public final static String PLATFORM_WEB = "WEB";
	/** app端 */
	public final static String PLATFORM_APP = "APP";
	/** wx端 */
	public final static String PLATFORM_WX = "WX";

	public static final String FILE_OPEN = "open";// 附件开放下载

	// ==========入站接口编码==================
	/** 公司主数据接口 */
	public final static String SRM_COMPANYSERVICE_CODE = "SRM_001";
	public final static String SRM_COMPANYSERVICE_NAME = "公司主数据";
	/** 汇率主数据接口 */
	public final static String SRM_EXCHANGERATESERVICE_CODE = "SRM_002";
	public final static String SRM_EXCHANGERATESERVICE_NAME = "汇率主数据";
	/** 集团主数据接口 */
	public final static String SRM_GROUPSERVICE_CODE = "SRM_003";
	public final static String SRM_GROUPSERVICE_NAME = "集团主数据";
	/** 物料主数据接口 */
	public final static String SRM_MATERIALSERVICEBEANSERVICE_CODE = "SRM_004";
	public final static String SRM_MATERIALSERVICEBEANSERVICE_NAME = "物料主数据";
	/** 订单接口 */
	public final static String SRM_PURCHASEORDERSERVICE_CODE = "SRM_005";
	public final static String SRM_PURCHASEORDERSERVICE_NAME = "订单接口";
	/** 订单关闭接口 */
	public final static String SRM_PURORDERDETAILCLOSESERVICE_CODE = "SRM_006";
	public final static String SRM_PURORDERDETAILCLOSESERVICE_NAME = "订单关闭接口";
	/** 收退货接口接口 */
	public final static String SRM_RECEIVINGNOTESERVICE_CODE = "SRM_007";
	public final static String SRM_RECEIVINGNOTESERVICE_NAME = "收退货接口";
	/** 质检接口 */
	public final static String SRM_CENSORQUALITYSERVICE_CODE = "SRM_008";
	public final static String SRM_CENSORQUALITYSERVICE_NAME = "质检接口";
	/** 寄售库存接口 */
	public final static String SRM_SUBCONSIGNMENTSTOCKSERVICE_CODE = "SRM_009";
	public final static String SRM_SUBCONSIGNMENTSTOCKSERVICE_NAME = "寄售库存";

	// ==========出站接口编码==================
	/** sap url系统参数 */
	public final static String SAP_URL_SYS_KEY = "interfaceURL";

	/** 供应商同步接口 */
	public final static String SRM_VENDOR_CODE = "SRM_101";
	public final static String SRM_VENDOR_NAME = "供应商同步";
	/** 价格主数据同步接口 */
	public final static String SRM_MATERIALMASTERPRICE_CODE = "SRM_102";
	public final static String SRM_MATERIALMASTERPRICE_NAME = "价格主数据同步";
	/** 价格主数据同步接口 */
	public final static String SRM_MATERIALMASTERPRICEFREZZ_CODE = "SRM_103";
	public final static String SRM_MATERIALMASTERPRICEFREZZ_NAME = "价格主数据冻结";
	/** 货源清单同步接口 */
	public final static String SRM_SOURCELIST_CODE = "SRM_104";
	public final static String SRM_SOURCELIST_NAME = "货源清单同步";
	/** 采购申请同步接口 */
	public final static String SRM_PR_CODE = "SRM_105";
	public final static String SRM_PR_NAME = "采购申请同步";
	/** 采购订单同步接口 */
	public final static String SRM_PO_CODE = "SRM_106";
	public final static String SRM_PO_NAME = "采购订单同步";
	/** 送货信息同步接口 */
	public final static String SRM_DELIVERY_CODE = "SRM_107";
	public final static String SRM_DELIVERY_NAME = "送货信息同步";
	/** 收货信息同步接口 */
	public final static String SRM_RECEVING_CODE = "SRM_108";
	public final static String SRM_RECEVING_NAME = "收货信息同步";
	/** 质检信息同步接口 */
	public final static String SRM_QUALITY_CODE = "SRM_109";
	public final static String SRM_QUALITY_NAME = "质检信息同步";
	/** 预制发票同步接口 */
	public final static String SRM_NEWINVOICE_CODE = "SRM_110";
	public final static String SRM_NEWINVOICE_NAME = "预制发票同步";
	/** 预制发票冲销 */
	public final static String SRM_CANCELINVOICE_CODE = "SRM_111";
	public final static String SRM_CANCELINVOICE_NAME = "预制发票冲销";
	/** 快递接口 */
	public final static String SRM_EXPRESS_CODE = "SRM_112";
	public final static String SRM_EXPRESS_NAME = "快递接口";

	public final static String TAEGET_SYSTEM = "ERP";
	public final static String SOURCE_SYSTEM = "ERP";

	/** 单据表单类型 */
	public final static String BILLFLAG_UNDEAL = "undeal";// 待处理单据列表
	public final static String BILLFLAG_EXEC = "exec";// 执行单据列表

	/*** 价格主数据冻结标识 */
	public final static String ISFROZEN_A001 = "A001";
	/** 未冻结 */
	public final static String ISFROZEN_A002 = "A002";

	/** 公共脚本开启与否 ***/
	public final static String STRING_ZERO = "0";
	public final static String STRING_ONE = "1";
	public final static String STRING_TWO = "2";

	/** 接口返回成功，失败，警告 */
	public final static String SUCCESS = "S";
	public final static String ERROR = "E";
	public final static String WARN = "W";

	/*** 队列同步价格主数据 */
	public final static String MSG_PRICE_KEY = "msg_price_key";

	/** 发布订单同步队列 */
	public final static String BILLTYPE_CGD_SYNC_SEND = "CGD_SYNC_SEND";
	 
	
	/** 系统参数参量 */ 
	/** 供应商预警天数 */
	public final static String SYSPARAMS_VENDOR_WARNDAYS = "srm.vendor.warndays";
	/** 招投标预警天数 */
	public final static String SYSPARAMS_BIDWARNDAYS = "srm.bid.warndays";
	/** 供应商注册协议公司名称 */
	public final static String SYSPARAMS_VENDORREADING_COMPANYNAME = "srm.vendorreading.companyname"; 
	/** 快递100--实时查询customer */
	public final static String SYSPARAMS_DELIVERY_EXPRESSCUSTOMER = "srm.delivery.expressCustomer";
	/** 快递100--客户授权Key */
	public final static String SYSPARAMS_DELIVERY_EXPRESSENTERPRISEKEY = "srm.delivery.expressEnterpriseKey";
	/**快递100--单号归属快递公司请求地址 */
	public final static String SYSPARAMS_DELIVERY_EXPRESSNOTOCOMPANYURL = "srm.delivery.expressNoToCompanyUrl";
	/**快递100--实时查询请求地址 */
	public final static String SYSPARAMS_DELIVERY_EXPRESSQUERYURL = "srm.delivery.expressQueryUrl";
	/**访问快递100接口频率，以小时为基本单位，如：4，则表示同一条数据前后访问间隔为4小时*/
	public final static String SYSPARAMS_DELIVERY_EXPRESSRATE = "srm.delivery.expressRate";
	/**外门户上传图片(loge\banner)上传地址*/
	public final static String SYSPARAMS_HOMEMANAGE_FILEDIR = "homemanage.file.dir";
	/**登录页面的地址*/
	public final static String SYSPARAMS_LOGIN_URL = "login.url";
	/**登录页面的地址*/
	public final static String SYSPARAMS_SRMDOMAIN = "application.srmDomain";
	/**合同失效提醒天数*/
	public final static String SYSPARAMS_CONTRACT_EXPIRYDATE = "srm.contract.expirydate";
	/**价格预警天数*/
	public final static String SYSPARAMS_MATERIALMASTERPRICE_WARNDAYS = "srm.materialmasterprice.warndays";
	/**合同预警天数*/
	public final static String SYSPARAMS_CONTRACT_WARNDAYS = "srm.contract.warndays";
	/**附件上传地址*/
	public final static String SYSPARAMS_FS_SERVERDIR = "application.fs.server.dir";
	/**服务端是否校验文件类型*/
	public final static String SYSPARAMS_FS_CHECKFILETYPE = "application.fs.checkFileType";
	/**	应用默认的文件存储类型*/
	public final static String SYSPARAMS_FS_STORAGETYPE = "application.fs.storageType";
	/**	招投标公开招标外门户附件下载地址*/
	public final static String SYSPARAMS_BID_FILEDOWNURL = "srm.bid.filedownurl";
	
}
