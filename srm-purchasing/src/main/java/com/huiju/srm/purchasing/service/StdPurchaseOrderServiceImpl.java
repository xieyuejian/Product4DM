package com.huiju.srm.purchasing.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FetchType;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.bpm.support.annotation.BpmService;
import com.huiju.bpm.support.enums.ApprovalState;
import com.huiju.bpm.support.service.BpmSupport;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.core.sys.entity.User;
import com.huiju.interaction.api.InteractionClient;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.JobResultData;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.event.listener.TransactionEventCallback;
import com.huiju.module.event.service.TransactionEventPublisher;
import com.huiju.module.fs.FileItem;
import com.huiju.module.fs.entity.FileInfo;
import com.huiju.module.fs.logic.FileInfoService;
import com.huiju.module.groovy.service.GroovyScriptInvokerService;
import com.huiju.module.log.Level;
import com.huiju.module.log.Log;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.module.util.StringUtils;
import com.huiju.notify.api.NotifySenderClient;
import com.huiju.notify.dto.NotifyParam;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.portal.dto.PortalMethodType;
import com.huiju.portal.dto.PortalParameters;
import com.huiju.srm.commons.utils.CnMoneyFormat;
import com.huiju.srm.commons.utils.CommonUtil;
import com.huiju.srm.commons.utils.I18nUtils;
import com.huiju.srm.commons.utils.IdUtils;
import com.huiju.srm.commons.utils.PoiUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.AttachmentClient;
import com.huiju.srm.masterdata.api.CompanyClient;
import com.huiju.srm.masterdata.api.CompanyPurchaseOrgClient;
import com.huiju.srm.masterdata.api.CurrencyClient;
import com.huiju.srm.masterdata.api.DataDictClient;
import com.huiju.srm.masterdata.api.ExchangeRateClient;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.MaterialPlantClient;
import com.huiju.srm.masterdata.api.MaterialPlantViewClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.PlantPurchaseOrgClient;
import com.huiju.srm.masterdata.api.PurchasingGroupClient;
import com.huiju.srm.masterdata.api.PurchasingOrganizationClient;
import com.huiju.srm.masterdata.api.StockLocationClient;
import com.huiju.srm.masterdata.api.TaxRateClient;
import com.huiju.srm.masterdata.api.UnitClient;
import com.huiju.srm.masterdata.entity.Attachment;
import com.huiju.srm.masterdata.entity.Company;
import com.huiju.srm.masterdata.entity.CompanyPurchaseOrg;
import com.huiju.srm.masterdata.entity.Currency;
import com.huiju.srm.masterdata.entity.DataDict;
import com.huiju.srm.masterdata.entity.ExchangeRate;
import com.huiju.srm.masterdata.entity.Material;
import com.huiju.srm.masterdata.entity.MaterialPlant;
import com.huiju.srm.masterdata.entity.MaterialPlantView;
import com.huiju.srm.masterdata.entity.Plant;
import com.huiju.srm.masterdata.entity.PlantPurchaseOrg;
import com.huiju.srm.masterdata.entity.PurchasingGroup;
import com.huiju.srm.masterdata.entity.PurchasingOrganization;
import com.huiju.srm.masterdata.entity.StockLocation;
import com.huiju.srm.masterdata.entity.TaxRate;
import com.huiju.srm.masterdata.entity.Unit;
import com.huiju.srm.purchasing.dao.PurchaseOrderDetailDao;
import com.huiju.srm.purchasing.dao.SendScheduleDao;
import com.huiju.srm.purchasing.dao.SendScheduleDetailDao;
import com.huiju.srm.purchasing.entity.DeliveryDtl;
import com.huiju.srm.purchasing.entity.DeliveryState;
import com.huiju.srm.purchasing.entity.MaterialMasterPriceOrderDtlView;
import com.huiju.srm.purchasing.entity.PurchaseDualUnitConversion;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderCheckState;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderEvent;
import com.huiju.srm.purchasing.entity.PurchaseOrderFlowState;
import com.huiju.srm.purchasing.entity.PurchaseOrderPricing;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.PurchaseOrderType;
import com.huiju.srm.purchasing.entity.PurchasingRequisition;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionTrans;
import com.huiju.srm.purchasing.entity.SendSchedule;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.purchasing.entity.SendScheduleDetailVo;
import com.huiju.srm.purchasing.util.PurchaseOrderConstant;
import com.huiju.srm.sourcing.dao.MaterialLadderPriceDtlDao;
import com.huiju.srm.sourcing.dao.MaterialMasterPriceDtlDao;
import com.huiju.srm.sourcing.entity.MaterialLadderPriceDtl;
import com.huiju.srm.sourcing.entity.MaterialMasterPrice;
import com.huiju.srm.sourcing.entity.MaterialMasterPriceDtl;
import com.huiju.srm.sourcing.entity.MaterialUnitConversionDtl;
import com.huiju.srm.sourcing.entity.SourceList;
import com.huiju.srm.sourcing.entity.SourceListDtl;
import com.huiju.srm.sourcing.service.MaterialMasterPriceService;
import com.huiju.srm.sourcing.service.SourceListDtlService;
import com.huiju.srm.srmbpm.service.SrmBpmService;
import com.huiju.srm.vendor.dao.VendorFileDtlDao;
import com.huiju.srm.vendor.dao.VendorPorgDtlDao;
import com.huiju.srm.vendor.entity.Vendor;
import com.huiju.srm.vendor.entity.VendorFileDtl;
import com.huiju.srm.vendor.entity.VendorPorgDtl;
import com.huiju.srm.vendor.service.VendorService;
import com.huiju.srm.ws.entity.WsRequestLog;
import com.huiju.srm.ws.service.WsRequestLogService;

/**
 * 采购订单 业务
 * 
 * @author zhuang.jq
 */
@BpmService(billTypeCode = "CGD", billNoKey = "purchaseOrderNo")
public class StdPurchaseOrderServiceImpl extends BpmSupport<PurchaseOrder, Long> implements StdPurchaseOrderService {

	@Autowired(required = false)
	protected PurchaseOrderDetailDao purchaseOrderDetailEaoBean;
	@Autowired(required = false)
	protected PurchasingRequisitionCollectionService PurchasingRequisitionCollectionLogic;
	@Autowired(required = false)
	protected PurchasingRequisitionTransService purchasingRequisitionTransLogic;
	@Autowired(required = false)
	protected PurchasingRequisitionService purchasingRequisitionLogic;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupLogic;
	@Autowired(required = false)
	protected VendorService vendorLogic;
	@Autowired(required = false)
	protected BpmServiceClient bpmService;

	@Autowired(required = false)
	protected UserClient userLogic;

	@Autowired(required = false)
	protected GroovyScriptInvokerService groovyScriptInvokerLogic;

	@Autowired
	protected WsRequestLogService wsRequestLogLogic;

	@Autowired(required = false)
	protected NotifySenderClient notifySender;

	@Autowired(required = false)
	protected InteractionClient interactLogic;

	@Autowired
	protected SrmBpmService bpmSrmLogic;

	@Autowired(required = false)
	protected TaxRateClient taxRateLogic;

	@Autowired(required = false)
	protected MaterialMasterPriceDtlDao materialMasterPriceDtlEao;
	@Autowired(required = false)
	protected VendorPorgDtlDao vendorPorgDtlEao;
	@Autowired(required = false)
	protected VendorFileDtlDao vendorFileDtlEao;
	@Autowired(required = false)
	protected FileInfoService fileInfoLogic;
	@Autowired(required = false)
	protected CompanyClient companyLogic;
	@Autowired(required = false)
	protected AttachmentClient attachmentLogic;
	@Autowired(required = false)
	protected PurchasingGroupClient purchasingGroupLogic;
	@Autowired(required = false)
	protected PurchaseOrderDetailDao purchaseOrderDetailEao;
	@Autowired(required = false)
	protected MaterialLadderPriceDtlDao materialLadderPriceDtlEao;
	@Autowired(required = false)
	protected MaterialPlantClient materialPlantLogic;
	@Autowired(required = false)
	protected SourceListDtlService sourceListDtlLogic;
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	@Autowired(required = false)
	protected SendScheduleDetailDao sendScheduleDetailEao;
	@Autowired(required = false)
	protected SendScheduleDao sendscheduleEao;
	@Autowired(required = false)
	protected StockLocationClient stockLocationLogic;
	@Autowired(required = false)
	protected DeliveryDtlService deliveryDtlLogic;
	@Autowired(required = false)
	protected UnitClient unitLogic;
	@Autowired(required = false)
	protected MaterialClient materialLogic;
	@Autowired(required = false)
	protected PlantClient plantLogic;
	@Autowired(required = false)
	protected PlantPurchaseOrgClient plantPurchaseOrgLogic;
	@Autowired(required = false)
	protected PurchasingOrganizationClient purchasingOrganizationLogic;
	@Autowired(required = false)
	protected CompanyPurchaseOrgClient companyPurchaseOrgLogic;
	@Autowired(required = false)
	protected DataDictClient dataDictlogic;
	@Autowired(required = false)
	protected ExchangeRateClient exchangeRateLogic;
	@Autowired(required = false)
	protected PortalServiceClient portalDealDataLogic;
	@Autowired(required = false)
	protected MaterialMasterPriceService materialMasterPriceLogic;
	@Autowired(required = false)
	protected MaterialPlantViewClient materialPlantViewEao;
	@Autowired(required = false)
	protected CurrencyClient currencyLogic;
	@Autowired(required = false)
	protected MaterialMasterPriceOrderDtlViewService materialMasterPriceOrderDtlViewLogic;

	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupBean;

	@Autowired
	protected TransactionEventPublisher eventPublisher;

	/**
	 * 不需要同步 -1
	 */
	public Integer SYNC_NO_NEED = -1;
	/**
	 * 未同步 0
	 */
	public Integer SYNC_UNSYNCHRONIZED = 0;
	/**
	 * 同步完成 1
	 */
	public Integer SYNC_SYNCHRONIZED = 1;
	/**
	 * 同步中 2
	 */
	public Integer SYNC_SYNCHRONIZING = 2;
	/**
	 * 同步异常 3
	 */
	public Integer SYNC_EXCEPTION = 3;

	public String DESC_UNSYNCHRONIZED = "未同步";

	public String DESC_SYNCHRONIZING = "同步中";

	public String DESC_SYNCHRONIZED = "同步成功";

	public String DESC_EXCEPTION = "同步失败";

	public String DESC_NO_NEED = "无需同步";

	public String SAP_OK = "S";

	public String SAP_NO = "E";

	/**
	 * APP分页
	 * 
	 * @param searchParams
	 * @param specialParams
	 * @return
	 */
	@Override
	public String page4App(Map<String, Object> searchParams, Map<String, Object> specialParams) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
		try {
			String startTmp = (String) specialParams.get("start");
			int start = startTmp == null ? 0 : Integer.parseInt(startTmp);
			String limitTmp = (String) specialParams.get("limit");
			int limit = limitTmp == null ? 20 : Integer.parseInt(limitTmp);
			String sort = (String) specialParams.get("sort");
			String dir = (String) specialParams.get("dir");
			String roleType = (String) specialParams.get("roleType");
			String key = (String) specialParams.get("key");
			String isDraft = (String) specialParams.get("isDraft");
			String userId = (String) specialParams.get("userId");
			// 判断角色类型来生成查询条件
			if (key != null && roleType != null && !"".equals(key)) {
				if (SrmConstants.ROLETYPE_V.equals(roleType)) {
					searchParams.put("LIKE_purchaseOrderNo", key);

				} else if (SrmConstants.ROLETYPE_B.equals(roleType)) {
					searchParams.put("LIKE_purchaseOrderNo_OR_LIKE_vendorName_OR_LIKE_companyName_OR_LIKE_erpPurchaseOrderNo", key);

				}

			}

			if (isDraft != null && "Y".equals(isDraft)) {// 订单草稿中的查询条件只需要订单的创建者和状态为新建的条件
				searchParams.clear();
				searchParams.put("EQ_createUserId", Long.parseLong(userId));
				searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.NEW);
			} else {
				setListParams(searchParams, specialParams, roleType);
				searchParams.put("NE_purchaseOrderState", PurchaseOrderState.NEW);
				// 列表查询权限
				// 根据角色设置参数
				if (SrmConstants.ROLETYPE_V.equals(roleType)) {
					setVendorSearchParams(searchParams, specialParams);
				} else if (SrmConstants.ROLETYPE_B.equals(roleType)) {
					setBuyerParams(searchParams, specialParams);
				} else {
					setOtherParams(searchParams, specialParams);
				}
			}

			Page<PurchaseOrder> page = new Page<PurchaseOrder>(start, limit, sort, dir);
			page = dao.findAll(page, searchParams);
			resultMap.put("data", page);

		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("errormsg", e.getMessage());
			return DataUtils.toJson(resultMap);
		}
		SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
		Set<String> excludes = filter.getExcludes();
		excludes.add("purchaseOrderDetails");// 排除细单
		return JSON.toJSONString(resultMap, filter, SerializerFeature.WriteMapNullValue);

	}

	/**
	 * 设置列表查询权限
	 * 
	 * @param searchParams
	 */
	protected void setListParams(Map<String, Object> searchParams, Map<String, Object> specialParams, String roleType) {
		String purchaseOrderState = (String) specialParams.get("purchaseOrderState");
		String purchaseOrderFlowState = (String) specialParams.get("purchaseOrderFlowState");
		String purchaseOrderCheckState = (String) specialParams.get("purchaseOrderCheckState");
		// 订单状态
		if (StringUtils.isNotBlank(purchaseOrderState)) {
			String[] states = purchaseOrderState.split(",");
			int i = 0;
			PurchaseOrderState[] searcharr = new PurchaseOrderState[states.length];
			for (String state : states) {
				searcharr[i] = PurchaseOrderState.valueOf(state.trim());
				i++;
			}
			searchParams.put("IN_purchaseOrderState", Arrays.asList(searcharr));
		}

		// 流程状态
		if (StringUtils.isNotBlank(purchaseOrderFlowState)) {
			String[] states = purchaseOrderFlowState.split(",");
			int i = 0;
			PurchaseOrderFlowState[] searchflowarr = new PurchaseOrderFlowState[states.length];
			for (String state : states) {
				searchflowarr[i] = PurchaseOrderFlowState.valueOf(state.trim());
				i++;
			}
			searchParams.put("IN_purchaseOrderFlowState", Arrays.asList(searchflowarr));
		}
		// 确认状态
		if (StringUtils.isNotBlank(purchaseOrderCheckState)) {
			String[] states = purchaseOrderCheckState.split(",");
			int i = 0;
			PurchaseOrderCheckState[] searchcheckarr = new PurchaseOrderCheckState[states.length];
			for (String state : states) {
				searchcheckarr[i] = PurchaseOrderCheckState.valueOf(state.trim());
				i++;
			}
			searchParams.put("IN_purchaseOrderCheckState", Arrays.asList(searchcheckarr));
		}
	}

	/**
	 * 设置供应商查询参数
	 * 
	 * @param roleType 角色
	 * @param searchParams web查询参数
	 */
	protected void setVendorSearchParams(Map<String, Object> searchParams, Map<String, Object> specialParams) {
		String erpCode = (String) specialParams.get("erpCode");
		// 供应商角色只能查看自己的
		searchParams.put("EQ_vendorErpCode", erpCode);
		searchParams.put("NE_purchaseOrderState", PurchaseOrderState.NEW);
		if (!searchParams.containsKey("IN_purchaseOrderCheckState")) {
			searchParams.put("IN_purchaseOrderCheckState", Arrays.asList(PurchaseOrderCheckState.values()));// 所有状态
		}
		String methodName = (String) specialParams.get("methodName");
		// 待处理列表
		if ("UnDealList".equals(methodName)) {
			searchParams.put("IN_purchaseOrderCheckState", Arrays.asList(PurchaseOrderCheckState.CONFIRM));
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.RELEASE);
		} else if ("ExecList".equals(methodName)) {// 等于执行状态的列表，执行状态
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.OPEN);
		}
	}

	/**
	 * 设置采购查询参数
	 * 
	 * @param roleType 角色
	 * @param searchParams web查询参数
	 */
	protected void setBuyerParams(Map<String, Object> searchParams, Map<String, Object> specialParams) {
		String clientCode = (String) specialParams.get("clientCode");
		String userCode = (String) specialParams.get("userCode");
		String user = (String) specialParams.get("userId");
		Long userId = Long.valueOf(user);
		// 资源组查询
		searchParams.putAll(userAuthGroupLogic.buildAuthFieldParams(new UserAuthGroupParam(clientCode, userCode, PurchaseOrder.class)));
		String methodName = (String) specialParams.get("methodName");
		if ("UnDealList".equals(methodName)) {
			String idsStr = findIdByStatus(userId, true);
			searchParams.put("IN_purchaseOrderId", idsStr);

		} else if ("ExecList".equals(methodName)) {// 等于执行状态的列表，执行状态
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.OPEN);
		}
	}

	/***
	 * 设置其他角色查询权限
	 * 
	 * @param searchParams web查询参数
	 */
	protected void setOtherParams(Map<String, Object> searchParams, Map<String, Object> specialParams) {
		String methodName = (String) specialParams.get("methodName");
		String user = (String) specialParams.get("userId");
		Long userId = Long.valueOf(user);
		if ("UnDealList".equals(methodName)) {
			String idsStr = findIdByStatus(userId, false);
			searchParams.put("IN_purchaseOrderId", idsStr);
		} else if ("ExecList".equals(methodName)) {// 等于执行状态的列表，执行状态
			searchParams.put("EQ_purchaseOrderState", PurchaseOrderState.OPEN);
		}
	}

	/**
	 * 获取供应商汇率
	 * 
	 * @param vendorCode 供应商编码
	 * @param purchasingOrgCode 采购组织
	 * @param origCurrencyCode 供应商货币
	 * @return 汇率
	 */
	public String findExchangeRate(String vendorCode, String purchasingOrgCode, String origCurrencyCode) {
		// String message = getText("porder.exitsCurrencyRate");
		String message = "没有维护有效的汇率！";
		String stadCurrencyCode = "";
		Map<String, Object> params = new HashMap<String, Object>();

		if (StringUtils.isBlank(origCurrencyCode)) {
			// 查询供应商货币
			params.put("EQ_purchasingOrgCode", purchasingOrgCode);
			params.put("EQ_vendor_vendorErpCode_OR_vendor_vendorCode", vendorCode);
			List<VendorPorgDtl> vendorPorgDtls = vendorLogic.findVendorPorgDtlAll(params);

			if (0 == vendorPorgDtls.size()) {
				return "供应商[" + vendorCode + "]采购组织[" + purchasingOrgCode + "]下不存在货币!";
			} else {
				origCurrencyCode = vendorPorgDtls.get(0).getCurrencyCode();
			}
		}

		// 查询本位币
		params.clear();
		params.put("EQ_currencyFlag", 1);
		Currency currency = currencyLogic.findOne(params);
		if (null == currency) {
			return "本位币不存在!";
		} else {
			stadCurrencyCode = currency.getCurrencyCode();
		}

		// 查询汇率
		params.clear();
		params.put("EQ_origCurrencyCode", origCurrencyCode);
		params.put("EQ_stadCurrencyCode", stadCurrencyCode);
		params.put("LE_effectiveDate", Calendar.getInstance());
		params.put("GT_expiryDate", Calendar.getInstance());
		List<ExchangeRate> exchangeRates = exchangeRateLogic.findAll(new FeignParam<ExchangeRate>(params));
		if (0 == exchangeRates.size()) {
			return message;
		} else {
			ExchangeRate rate = exchangeRates.get(0);
			message = origCurrencyCode + "-" + stadCurrencyCode + "-" + rate.getExchangeRate().toString();
		}

		return message;
	}

	/**
	 * APP创建或者修改订单
	 * 
	 * @author wangxm
	 * @param orderJson 订单json
	 * @param orderJson 是否提交订单，true是，false否
	 * @param userId 当前操作人id
	 * @param userName 当前操做人名称
	 * @return 返回校验信息 key = true or false value = 消息
	 */
	// {"id":1,"list":[{"id":2,"name":"user1"},{"id":3,"name":"user2"}],"name":"group"}
	public Map<Boolean, String> saveOrUpdateOrder(String orderJson, Boolean submitFlag, Long userId, String userName, String platForm) {
		Map<Boolean, String> returnMap = new HashMap<Boolean, String>();
		String submitFlagStr = submitFlag ? "audit" : "";

		try {
			PurchaseOrder model = JSONObject.parseObject(orderJson, PurchaseOrder.class);
			StringBuffer messages = new StringBuffer();

			// 设置汇率
			String currencyRate = findExchangeRate(model.getVendorCode(), model.getPurchasingOrgCode(), model.getCurrencyCode());
			if (currencyRate.contains("-")) {
				model.setCurrencyRate(new BigDecimal(currencyRate.split("-")[2]));
			} else {
				returnMap.put(false, currencyRate);
				return returnMap;

			}
			int index = 0;
			Map<String, Object> validateSourceListMap = validateSourceList(model);
			for (PurchaseOrderDetail detail : model.getPurchaseOrderDetails()) {
				index++;
				String key = detail.getMaterialCode() + "_" + detail.getPlantCode() + "_" + model.getVendorErpCode();
				Boolean falg = validateSourceListMap.containsKey(key) && validateSourceListMap.get(key).equals("1") ? true : false;
				if (!falg) {
					messages.append(getResource("label.theRow", new String[] { (index) + "" }) + "：");
					messages.append(getResource("porder.materialNotSourceList", new String[] { detail.getMaterialCode() })).append("。");
				}
			}
			if (0 < messages.length()) {
				returnMap.put(false, messages.toString());
				return returnMap;
			}

			if (model.getPurchaseOrderId() == null) {
				String poorderNo = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_CGD);
				if (poorderNo == null) {
					returnMap.put(false, "订单单号生成失败！");
					return returnMap;
				}
				model.setCreateUserId(userId);
				model.setCreateUserName(userName);
				model.setPurchaseOrderNo(poorderNo);
				model.setErpPurchaseOrderNo(poorderNo);
				// 保存
				model = persistPo(model, submitFlagStr, platForm);
			} else {
				model.setErpSynState(0);
				model.setModifyUserId(userId);
				model.setModifyTime(Calendar.getInstance());
				model.setModifyUserName(userName);
				model.setPurchaseOrderState(PurchaseOrderState.NEW);
				model.setViewFlag(0);
				PurchaseOrder order = mergeLogic(model, submitFlagStr, userId, userName, platForm);
				if ("save".equalsIgnoreCase(submitFlagStr)) {
					addLog(userId, userName, order.getPurchaseOrderId(), "采购订单修改", SrmConstants.PERFORM_SAVE, order.getPurchaseOrderNo(),
							SrmConstants.PLATFORM_APP);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put(false, "出现异常");
			return returnMap;
		}
		returnMap.put(true, "操作成功");
		return returnMap;
	}

	/**
	 * 供应商变更订单明细
	 * 
	 * @param map 存储id和变更的时间
	 * @param userId 当前用户ID
	 * @param userName 当前用户名称
	 * @param message 处理意见
	 * @return 返回成功失败
	 */
	public Boolean updataToVariation(Map<Long, String> map, Long userId, String userName, String message, String platForm) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			for (Long id : map.keySet()) {
				PurchaseOrderDetail detail = purchaseOrderDetailEao.getById(id);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(sdf.parse(map.get(id)));
				detail.setVendorTime(calendar);
				purchaseOrderDetailEao.save(detail);
			}

			// 获取明细ID
			PurchaseOrder entity = purchaseOrderDetailEao.getById(map.keySet().iterator().next()).getPurchaseOrder();
			entity.setVendorConfirmTime(Calendar.getInstance());
			entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.HOLD);
			dao.save(entity);

			// @Message-CGD_HOLD 变更订单
			sendNotify(entity, userId, Arrays.asList(entity.getCreateUserId()), "CGD_HOLD");

			// 采购-完成跟踪
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.ST_DELETE);
			pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
					.setBillNo(entity.getPurchaseOrderNo());
			addLog(userId, userName, entity.getPurchaseOrderId(), "采购订单变更,原因:" + message, SrmConstants.PERFORM_CHANGE,
					entity.getPurchaseOrderNo(), platForm);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 获取excel导出参数
	 * 
	 * @return
	 */
	public Map<String, Object> getExportParams(Map<String, Object> params) {
		PurchaseOrder purchaseOrder = dao.getById(Long.valueOf((String) params.get("purchaseOrderId")));
		if (purchaseOrder != null) {
			params.put("currency", purchaseOrder.getCurrencyCode());// 币种
			params.put("vendorName", purchaseOrder.getVendorName());// 销方
			params.put("buyerName", purchaseOrder.getCompanyName());// 购方
			params.put("purchaseOrderNo", purchaseOrder.getErpPurchaseOrderNo());// 订单号
			params.put("totalAmount", purchaseOrder.getTotalAmount());// 订单金额
			params.put("remark", purchaseOrder.getRemark() == null ? "" : purchaseOrder.getRemark());// 订单备注
			params.put("totalAmountInChinese", CnMoneyFormat.format(purchaseOrder.getTotalAmount().doubleValue()));// 订单金额
			params.put("purchaseOrderTime", "");
			params.put("currency", purchaseOrder.getCurrencyCode());
			params.put("sysCompanyName", purchaseOrder.getCompanyName());// 公司名称
			params.put("purchaseOrderId", purchaseOrder.getPurchaseOrderId());// 采购订单主键

			// 订单日期
			if (purchaseOrder.getPurchaseOrderTime() != null) {
				params.put("purchaseOrderTime", purchaseOrder.getPurchaseOrderTime().getTime());
			}

			Map<String, Object> vendorParams = new HashMap<String, Object>();
			vendorParams.put("EQ_clientCode", purchaseOrder.getClientCode());
			vendorParams.put("EQ_vendorErpCode", purchaseOrder.getVendorErpCode());
			Vendor vendor = vendorLogic.findOne(vendorParams);

			if (vendor != null) {
				params.put("vendorErpCode", vendor.getVendorErpCode());// 卖方单位编码
				params.put("vendorName", vendor.getVendorName());// 卖方单位名称
				params.put("vendorAdd", vendor.getAddress());// 卖方单位地址
				params.put("vendorTel", vendor.getTel());// 销方电话
				params.put("vendorFax", vendor.getFax());// 销方传真

				Map<String, Object> vendorPurchasingParams = new HashMap<String, Object>();
				vendorPurchasingParams.put("EQ_vendor_clientCode", purchaseOrder.getClientCode());
				vendorPurchasingParams.put("EQ_vendor_vendorErpCode", purchaseOrder.getVendorErpCode());
				vendorPurchasingParams.put("EQ_purchasingOrgCode", purchaseOrder.getPurchasingOrgCode());
				VendorPorgDtl vendorPorg = vendorPorgDtlEao.findOne(vendorPurchasingParams);

				if (vendorPorg != null) {
					params.put("vendorContactPerson", vendorPorg.getSalesMan());// 卖方联系人
					params.put("vendorEmail", vendorPorg.getEmail());// 卖方电邮
					params.put("paymentTerms", vendorPorg.getPaymentTermsName());// 付款条件
				} else {
					params.put("vendorContactPerson", "");// 卖方联系人
					params.put("vendorEmail", "");// 卖方电邮
					params.put("paymentTerms", "");// 付款条件
				}

				// 订单状态为执行或关闭，审核通过，确认变更或接受的订单，才能导出电子签章和签名
				if ((PurchaseOrderCheckState.ACCEPT.equals(purchaseOrder.getPurchaseOrderCheckState())
						|| PurchaseOrderCheckState.FIRMHOLD.equals(purchaseOrder.getPurchaseOrderCheckState()))
						&& PurchaseOrderFlowState.PASS.equals(purchaseOrder.getPurchaseOrderFlowState())
						&& (PurchaseOrderState.OPEN.equals(purchaseOrder.getPurchaseOrderState())
								|| PurchaseOrderState.CLOSE.equals(purchaseOrder.getPurchaseOrderState()))) {
					Map<String, Object> fileDtlParams = new HashMap<String, Object>();
					fileDtlParams.put("EQ_vendor_vendorId", vendor.getVendorId());
					List<VendorFileDtl> fileDtls = vendorFileDtlEao.findAll(fileDtlParams);

					if (null != fileDtls) {
						for (VendorFileDtl vf : fileDtls) {
							// 查找供应商电子签章
							if (vf.getFileType().equals("001") && !params.containsKey("vendorStamp")) {
								Map<String, Object> stampParams = new HashMap<String, Object>();
								stampParams.put("EQ_fileGroup_fileGroupId", vf.getUploadFileGroupId());
								List<FileInfo> list = fileInfoLogic.findAll(stampParams);
								if (null != list && 0 < list.size()) {
									try {
										FileItem fileItem = fileInfoLogic.download(list.get(0).getFileInfoId());
										if (fileItem != null && fileItem.getFile().exists()) {
											params.put("vendorStamp", fileItem.getFile());
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}

								// 查找供应商电子签名
							} else if (vf.getFileType().equals("002") && !params.containsKey("vendorSignature")) {
								Map<String, Object> stampParams = new HashMap<String, Object>();
								stampParams.put("EQ_fileGroup_fileGroupId", vf.getUploadFileGroupId());
								List<FileInfo> list = fileInfoLogic.findAll(stampParams);
								if (null != list && 0 < list.size()) {
									try {
										FileItem fileItem = fileInfoLogic.download(list.get(0).getFileInfoId());
										if (fileItem != null && fileItem.getFile().exists()) {
											params.put("vendorSignature", fileItem.getFile());
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}

							}
						}
					}
				}
			} else {
				params.put("vendorErpCode", "");// 销方单位编码
				params.put("vendorName", "");// 销方单位名称
				params.put("vendorAdd", "");// 销方单位地址
				params.put("vendorContactPerson", "");// 销方法定代理人
				params.put("vendorTel", "");// 销方电话
				params.put("vendorFax", "");// 销方传真
				params.put("vendorEmail", "");// 销方邮编
			}

			User user = userLogic.findById(purchaseOrder.getCreateUserId());
			params.put("buyerEmail", user.getEmail());// 购方电邮
			params.put("buyerContactPerson", purchaseOrder.getCreateUserName());// 经办人

			Map<String, Object> companyParams = new HashMap<String, Object>();
			companyParams.put("EQ_clientCode", purchaseOrder.getClientCode());
			companyParams.put("EQ_companyCode", purchaseOrder.getCompanyCode());
			Company company = companyLogic.findOne(companyParams);

			Map<String, Object> purchaseGroupParams = new HashMap<String, Object>();
			purchaseGroupParams.put("EQ_clientCode", purchaseOrder.getClientCode());
			purchaseGroupParams.put("EQ_purchasingGroupCode", purchaseOrder.getPurchasingGroupCode());
			PurchasingGroup purchasingGroup = purchasingGroupLogic.findOne(purchaseGroupParams);
			if (purchasingGroup != null) {
				params.put("buyerTel", purchasingGroup.getTel());// 购方电话
				params.put("buyerFax", purchasingGroup.getFax());// 购方传真
			} else {
				params.put("buyerTel", "");// 购方电话
				params.put("buyerFax", "");// 购方传真
			}

			if (company != null) {
				params.put("buyerName", company.getCompanyName());// 购方单位名称
				params.put("buyerAdd", company.getAddress());// 购方单位地址
				// data.put("buyerTel", company.getTel());// 购方电话
				// data.put("buyerFax", company.getFax());// 购方传真
				params.put("vatNo", company.getCertificateNo());// 购方税号
				params.put("accountNo", company.getDepositBank() + " " + company.getBankAccount());// 购方帐号

				// 订单状态为执行或关闭，审核通过，确认变更或接受的订单，才能导出电子签章和签名
				if ((PurchaseOrderCheckState.ACCEPT.equals(purchaseOrder.getPurchaseOrderCheckState())
						|| PurchaseOrderCheckState.FIRMHOLD.equals(purchaseOrder.getPurchaseOrderCheckState()))
						&& PurchaseOrderFlowState.PASS.equals(purchaseOrder.getPurchaseOrderFlowState())
						&& (PurchaseOrderState.OPEN.equals(purchaseOrder.getPurchaseOrderState())
								|| PurchaseOrderState.CLOSE.equals(purchaseOrder.getPurchaseOrderState()))) {
					// 查找公司电子签章
					Map<String, Object> stampParams = new HashMap<String, Object>();
					stampParams.put("EQ_company_companyId", company.getCompanyId());
					FeignParam<Attachment> feignParam = new FeignParam<Attachment>();
					feignParam.setParams(stampParams);
					List<Attachment> attachments = attachmentLogic.findAll(feignParam);

					if (null != attachments) {
						for (Attachment att : attachments) {
							// 查找公司电子签章
							if (att.getFileType().equals("A001") && !params.containsKey("companyStamp")) {
								stampParams.clear();
								stampParams.put("EQ_fileGroup_fileGroupId", att.getAttachmentGroupId());
								List<FileInfo> list = fileInfoLogic.findAll(stampParams);
								if (null != list && 0 < list.size()) {
									try {
										FileItem fileItem = fileInfoLogic.download(list.get(0).getFileInfoId());
										if (fileItem != null && fileItem.getFile().exists()) {
											params.put("companyStamp", fileItem.getFile());
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}

								// 查找公司电子签名
							} else if (att.getFileType().equals("A002") && !params.containsKey("companySignature")) {
								stampParams.clear();
								stampParams.put("EQ_fileGroup_fileGroupId", att.getAttachmentGroupId());
								List<FileInfo> list = fileInfoLogic.findAll(stampParams);
								if (null != list && 0 < list.size()) {
									try {
										FileItem fileItem = fileInfoLogic.download(list.get(0).getFileInfoId());
										if (fileItem != null && fileItem.getFile().exists()) {
											params.put("companySignature", fileItem.getFile());
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			} else {
				params.put("sysCompanyName", "");// 公司名称
				params.put("buyerName", "");// 购方单位名称
				params.put("buyerAdd", "");// 购方单位地址
				params.put("buyerContactPerson", "");// 购方联系人
				// data.put("buyerTel", "");// 购方电话
				// data.put("buyerFax", "");// 购方传真
				params.put("buyerEmail", "");// 购方电邮
				params.put("vatNo", "");// 购方税号
				params.put("accountNo", "");// 购方帐号
			}
		}

		return params;
	}

	/**
	 * 获取采购订单管控点
	 * 
	 * @param entity 实体对象
	 * @param code 管控点Code
	 * @return
	 * @throws Exception
	 */
	public String getPurchaseOrderControl(Object entity, String code) {
		try {
			Map<String, Object> poMap = new HashMap<String, Object>();
			poMap.put("po", entity);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("map", poMap);
			String value;
			value = (String) groovyScriptInvokerLogic.invoke(code, params);

			if (StringUtils.isBlank(value) || !value.equals(PurchaseOrderConstant.YES)) {
				return PurchaseOrderConstant.GROOVY_NO;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PurchaseOrderConstant.GROOVY_YES;
	}

	/**
	 * 根据采购组织、物料、工厂、供应商、行类型、货币 获取阶梯价格
	 * 
	 * @return
	 */
	public MaterialLadderPriceDtl findMaterialLadderPrice(Map<String, Object> params) {
		MaterialLadderPriceDtl ladderPriceDtl = null;
		List<MaterialMasterPriceDtl> masterPriceDtls = materialMasterPriceDtlEao.findAll(params);

		// 工厂为空nul的查询
		if (0 == masterPriceDtls.size()) {
			if (params.containsKey("EQ_materialMasterPrice_plantCode")) {
				params.remove("EQ_materialMasterPrice_plantCode");
			}
			params.put("IS_materialMasterPrice_plantCode", "null");
			masterPriceDtls = materialMasterPriceDtlEao.findAll(params);
			params.remove("IS_materialMasterPrice_plantCode");
		}

		// 存在对应的价格住数据
		if (0 < masterPriceDtls.size()) {
			MaterialMasterPriceDtl priceDtl = masterPriceDtls.get(0);
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("LE_startNum", params.get("LE_materialLadderPriceDtls_startNum"));
			searchParams.put("EQ_materialMasterPriceDtl_materialMasterPriceDtlId", priceDtl.getMaterialMasterPriceDtlId());
			List<MaterialLadderPriceDtl> dtls = materialLadderPriceDtlEao.findAll(searchParams, "startNum,desc");
			if (0 < dtls.size()) {
				ladderPriceDtl = dtls.get(0);
			}
		}

		if (null != ladderPriceDtl) {
			ladderPriceDtl.getMaterialMasterPriceDtl().getMaterialLadderPriceDtls().size();
			ladderPriceDtl.getMaterialMasterPriceDtl().getMaterialUnitConversionDtls().size();
		}

		return ladderPriceDtl;
	}

	/**
	 * 根据采购组织、资源组、工厂物料视图、物料查询工厂
	 * 
	 * @param params
	 * @return
	 */
	public String findPlantAll(Map<String, Object> params) {

		StringBuffer sql = new StringBuffer(" SELECT PLANT.PLANTCODE,PLANT.PLANTNAME,MMd.qualityCheck, ");
		sql.append(
				" MATERIAL.MATERIALID, plant.plantid FROM D_OMD_PLANTPURCHASEORG omd, B_MMD_MATERIALPLANT mmd, B_MMD_MATERIAL material, D_OMD_PLANT plant ")
				.append(" WHERE MATERIAL.MATERIALID = MMD.MATERIALID and PLANT.PLANTID = MMD.PLANTID ")
				.append(" and OMD.PURCHASINGORGCODE = '").append(params.get("EQ_purchasingOrgCode")).append("' ")
				.append(" and material.MATERIALCODE = '").append(params.get("EQ_materialCode")).append("' ")
				.append(" and omd.plantcode = mmd.plantcode"); // modified by
																// linshp

		if (params.containsKey("IN_plantCode")) {
			sql.append(" and PLANT.PLANTCODE in (").append(params.get("IN_plantCode")).append(") ");
		}

		if (params.containsKey("EQ_plantCode")) {
			sql.append(" and PLANT.PLANTCODE = '").append(params.get("EQ_plantCode")).append("' ");
		}

		sql.append(
				" GROUP BY MATERIAL.MATERIALID, plant.plantid, MATERIAL.MATERIALCODE, PLANT.PLANTCODE, MMd.qualityCheck,PLANT.PLANTNAME ");

		List<Object[]> list = dao.executeSqlQueryArray(sql.toString(), params);
		List<Map<String, Object>> jsonList = new ArrayList<Map<String, Object>>();
		for (Object[] obj : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("plantCode", obj[0].toString());
			map.put("plantName", obj[1].toString());
			map.put("qualityCheck", obj[2] == null ? "A" : obj[2].toString());
			jsonList.add(map);
		}
		return DataUtils.toJson(jsonList);
	}

	/**
	 * 查找订单明细
	 * 
	 * @param purchasingOrderId 采购订单ID
	 * @return 订单明细
	 */
	public List<PurchaseOrderDetail> findPurchaseOrderDetails(Long purchasingOrderId) {
		Map<String, Object> searchParams = new LinkedHashMap<String, Object>();
		searchParams.put("EQ_purchaseOrder_purchaseOrderId", purchasingOrderId);
		return purchaseOrderDetailEaoBean.findAll(searchParams);
	}

	/**
	 * 关闭订单明细
	 * 
	 * @param purchaseOrderDetailId 细单ID
	 * @param operate 关闭、取消关闭
	 * @param closeFlag 1、0
	 * @return 订单的状态
	 * @throws Exception
	 */
	public PurchaseOrderState closePurchaseOrderDetail(Long purchaseOrderDetailId, String operate, int closeFlag, Long userid,
			String userName) {
		PurchaseOrderDetail pod = purchaseOrderDetailEaoBean.getById(purchaseOrderDetailId);
		if (pod == null) {
			return null;
		}
		// 如果订单明细已创建送货单,其送货单状态不是取消或者关闭状态 不可以关闭
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_orderDetailId", pod.getPurchaseOrderDetailId());
		List<DeliveryDtl> deliveryDtls = deliveryDtlLogic.findAll(searchParams);
		boolean flag = true;
		if (deliveryDtls != null && deliveryDtls.size() > 0) {
			for (DeliveryDtl deliveryDtl : deliveryDtls) {
				if (!DeliveryState.CANCEL.equals(deliveryDtl.getDelivery().getStatus())
						&& !DeliveryState.CLOSE.equals(deliveryDtl.getDelivery().getStatus())) {
					flag = false;
				}
			}
		}
		if (!flag) {
			return null;
		}
		pod.setOperate(operate);
		pod.setCloseFlag(closeFlag);
		if (PurchaseOrderConstant.YES.equals(String.valueOf(closeFlag))) {// 关闭明细
			// 判断订单的所有明细都已关闭，将订单的状态置为关闭
			List<PurchaseOrderDetail> details = findPurchaseOrderDetails(pod.getPurchaseOrder().getPurchaseOrderId());
			boolean closeAll = true;
			for (PurchaseOrderDetail detail : details) {
				if (pod.getPurchaseOrderDetailId().equals(detail.getPurchaseOrderDetailId())) {
					continue;
				}
				if (detail.getDeleteFlag() == 0 && detail.getCloseFlag() == 0) {
					closeAll = false;
					break;
				}
			}
			if (closeAll) {
				pod.getPurchaseOrder().setPurchaseOrderState(PurchaseOrderState.CLOSE);// 置为关闭
			}

			// 增加操作日志
			addLog(userid, userName, pod.getPurchaseOrder().getPurchaseOrderId(), "采购订单明细" + pod.getSrmRowids() + "行关闭",
					SrmConstants.PERFORM_TOCANCEL, pod.getPurchaseOrder().getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);

		} else {
			pod.getPurchaseOrder().setPurchaseOrderState(PurchaseOrderState.OPEN);// 置为执行
			// 增加操作日志
			addLog(userid, userName, pod.getPurchaseOrder().getPurchaseOrderId(), "采购订单明细" + pod.getSrmRowids() + "行取消关闭",
					SrmConstants.PERFORM_TOCANCEL, pod.getPurchaseOrder().getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		}

		purchaseOrderDetailEaoBean.save(pod);

		// 同步接口，先设置成为未
		PurchaseOrder entity = pod.getPurchaseOrder();
		entity.setErpSynState(0);
		// dao.save(entity);

		// 同步到sap
		Boolean syncFlag = doSync(pod.getPurchaseOrder().getPurchaseOrderId(), null, "");
		// 同步失败且关闭标识为关闭，则将关闭标识回置为关闭
		if (!syncFlag && closeFlag == 1) {
			pod.setCloseFlag(0);
		} else if (!syncFlag && closeFlag == 0) {// 同步失败且关闭标识为取消关闭，则将关闭标识回置为取消关闭
			pod.setCloseFlag(1);
		}
		purchaseOrderDetailEaoBean.save(pod);
		PortalParameters pp = new PortalParameters();
		// 完成提醒
		pp.addPortalMethod(PortalMethodType.IW_DELETE);
		// 完成日程
		pp.addPortalMethod(PortalMethodType.SCHEDULE_DELETE);
		// 调用内门户
		pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
				.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity)); // FetchType.EAGER
		portalDealDataLogic.data4Portal(pp);
		return pod.getPurchaseOrder().getPurchaseOrderState();
	}

	/**
	 * 获取APP 审核权限
	 * 
	 * @param s_userId 用户ID
	 * @param s_roleType 角色类型
	 * @param id 主键
	 * @return 权限
	 */
	public List<String> getPurchaseOrderEvents(Long userId, String roleType, Long id) {
		List<String> events = getEventList(userId, roleType, id);
		return events;
	}

	/**
	 * 获取权限集合
	 * 
	 * @param s_userId 当前用户ID
	 * @param roleType 角色
	 * @param id 单据ID
	 * @return
	 */
	protected List<String> getEventList(Long s_userId, String roleType, Long id) {
		List<String> events = new ArrayList<String>();
		PurchaseOrder po = dao.getById(id);
		if (po == null) {
			return events;
		}

		boolean isAuthoritiedToAuditing = bpmService.isAuthoritiedToAuditing(s_userId.toString(), SrmConstants.BILLTYPE_CGD, id.toString());

		PurchaseOrderState state = po.getPurchaseOrderState();
		PurchaseOrderCheckState check = po.getPurchaseOrderCheckState();
		PurchaseOrderFlowState flow = po.getPurchaseOrderFlowState();

		if (null == po.getCheckFirst()) {
			return events;
		}

		// 先确认后审批
		if (1 == po.getCheckFirst()) {
			if (state.equals(PurchaseOrderState.NEW) && null == check && null == flow) {
				events.add(PurchaseOrderEvent.TORELEASE.name());
			} else if (state.equals(PurchaseOrderState.RELEASE)) {

				if (!roleType.contains(SrmConstants.ROLETYPE_V) && (null == flow || !flow.equals(PurchaseOrderFlowState.CONFIRM))) {
					events.add(PurchaseOrderEvent.TOCANCEL.name());
				}

				if (PurchaseOrderCheckState.CONFIRM.equals(check)) {
					if (!roleType.contains(SrmConstants.ROLETYPE_B)) {
						events.add(PurchaseOrderEvent.TOACCEPT.name());
						events.add(PurchaseOrderEvent.TOREJECT.name());
						events.add(PurchaseOrderEvent.TOHOLD.name());
					}
				} else if (!roleType.contains(SrmConstants.ROLETYPE_V)) {
					if (PurchaseOrderCheckState.HOLD.equals(check)) {
						events.add(PurchaseOrderEvent.TOFIRMHOLD.name());
					} else if (PurchaseOrderCheckState.REJECT.equals(check)) {
						events.add(PurchaseOrderEvent.TOFIRMREJECT.name());
					} else if (PurchaseOrderCheckState.ACCEPT.equals(check) || PurchaseOrderCheckState.FIRMHOLD.equals(check)) {
						if (isAuthoritiedToAuditing) {
							events.add(PurchaseOrderEvent.TOPASS.name());
							events.add(PurchaseOrderEvent.TONOPASS.name());
						}
					} else if (PurchaseOrderFlowState.NOPASS.equals(flow)) {
						events.add(PurchaseOrderEvent.TOCONFIRM.name());
					}
				}
			} else if (PurchaseOrderState.OPEN.equals(state)) {
				if (!roleType.contains(SrmConstants.ROLETYPE_V)) {
					events.add(PurchaseOrderEvent.TOCLOSE.name());
					events.add(PurchaseOrderEvent.TOCANCEL.name());
				}
			}

		} else {
			if (PurchaseOrderFlowState.NOPASS.equals(flow)) {
				events.add(PurchaseOrderEvent.TOCONFIRM.name());
			}

			// 发布
			if (PurchaseOrderState.NEW.equals(state)) {
				events.add(PurchaseOrderEvent.TORELEASE.name());
			} else if (PurchaseOrderState.RELEASE.equals(state)) {
				if (!roleType.contains(SrmConstants.ROLETYPE_V) && (null == flow || !flow.equals(PurchaseOrderFlowState.CONFIRM))) {
					events.add(PurchaseOrderEvent.TOCANCEL.name());
				}

				if (PurchaseOrderFlowState.PASS.equals(flow) && PurchaseOrderCheckState.CONFIRM.equals(check)) {
					if (!roleType.contains(SrmConstants.ROLETYPE_B)) {
						events.add(PurchaseOrderEvent.TOACCEPT.name());
						events.add(PurchaseOrderEvent.TOREJECT.name());
						events.add(PurchaseOrderEvent.TOHOLD.name());
					}
				} else if (PurchaseOrderFlowState.CONFIRM.equals(flow)) {
					if (isAuthoritiedToAuditing) {
						events.add(PurchaseOrderEvent.TOPASS.name());
						events.add(PurchaseOrderEvent.TONOPASS.name());
					}
				} else if (PurchaseOrderCheckState.HOLD.equals(check)) {
					events.add(PurchaseOrderEvent.TOFIRMHOLD.name());
				} else if (PurchaseOrderCheckState.REJECT.equals(check)) {
					events.add(PurchaseOrderEvent.TOFIRMREJECT.name());
				}
			} else if (PurchaseOrderState.OPEN.equals(state)) {
				if (!roleType.contains(SrmConstants.ROLETYPE_V)) {
					events.add(PurchaseOrderEvent.TOCLOSE.name());
					events.add(PurchaseOrderEvent.TOCANCEL.name());
				}
			}
		}

		return events;

	}

	/**
	 * 获取右键事件
	 * 
	 * @param userId 用户ID
	 * @param s_roleType 角色类型
	 * @param id 主键
	 * @return 右键可操作方法列表
	 */
	public String getPurchaseOrderEvents(Long userId, String roleType, Long id, List<String> authorities, String btnStateFlag) {
		List<String> events = getEventList(userId, roleType, id);
		StringBuilder sb = new StringBuilder("[");

		// 事件对应的权限
		String[] events4Authoritie = { "purchaseorder_toclose", "purchaseorder_toconfirm", "purchaseorder_topass", "purchaseorder_tonopass",
				"purchaseorder_toaccept", "purchaseorder_toreject", "purchaseorder_tohold", "purchaseorder_tofirmhold",
				"purchaseorder_tofirmreject", "purchaseorder_tocancel", "purchaseorder_torelease" };

		// 如果是供右键使用，则按钮相关菜单不显示
		if (!PurchaseOrderConstant.YES.equals(btnStateFlag)) {
			events4Authoritie = new String[] { "purchaseorder_toconfirm", "purchaseorder_topass", "purchaseorder_tonopass" };
		}

		// 从授权中找出所拥有的事件权限
		StringBuffer authSb = new StringBuffer();
		for (String auth : events4Authoritie) {
			if (authorities.contains(auth)) {
				if (authSb.length() > 0)
					authSb.append(",");
				authSb.append("'" + auth + "'");
			}
		}
		String eventAuth = authSb.toString(); // 拥有的事件权限
		for (String event : events) {
			if (event != null) {
				if (eventAuth.indexOf("'purchaseorder_" + event.toLowerCase() + "'") > -1 || event.indexOf("#") > -1) {
					sb.append("'" + event + "',");
				}
			}
		}
		if (sb.length() > 1) {
			sb.append("'@'");
		}
		sb.append("]");

		return sb.toString();
	}

	/**
	 * 撤销审批
	 * 
	 * @param id 单据ID
	 * @param userId 当前用户ID
	 * @param userName 当前用户名称
	 * @return 返回校验消息
	 * @throws Exception
	 */
	public String revocationCheck(Long id, Long userId, String userName) {
		PurchaseOrder model = dao.getById(id);
		// 查找是否已送货
		Map<String, Object> mapd = new LinkedHashMap<String, Object>();
		mapd.put("EQ_purchaseOrderCode", model.getErpPurchaseOrderNo());
		mapd.put("NE_deliveryNumber", 0);
		Long count = deliveryDtlLogic.count(mapd);
		if (count.intValue() > 0) {
			return "porder.revocationCheckShoppingNoticeDetail";
		}

		model.setPurchaseOrderCheckState(null);
		model.setPurchaseOrderFlowState(null);
		model.setPurchaseOrderState(PurchaseOrderState.NEW);
		model.setViewFlag(0);
		model.setErpSynState(0);
		model.setIsVendorView(0);
		// model.setIsRevocationCheck(1);
		dao.save(model);

		// 撤销审批是否需要调用
		bpmService.forceTerminate(SrmConstants.BILLTYPE_CGD, id.toString());

		PortalParameters pp = new PortalParameters();
		// List<Map<String, Object>> mapLists = new ArrayList<Map<String,
		// Object>>();
		// 完成待办
		// mapLists.add(finish4ToDealParam(model, null));
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
		// 完成追踪
		// mapLists.add(finish4ServiceTraceParam(model));
		pp.addPortalMethod(PortalMethodType.ST_DELETE);
		// 完成提示
		// mapLists.add(finish4ImportantWarnParam(model.getPurchaseOrderId().toString()));
		pp.addPortalMethod(PortalMethodType.IW_DELETE);
		// 调用内门户接口
		// data4PortalParam(mapLists);
		// 增加日志
		addLog(model.getCreateUserId(), model.getCreateUserName(), model.getPurchaseOrderId(), "采购订单撤销审批", SrmConstants.PERFORM_REVOKE,
				model.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(model.getPurchaseOrderId().toString())
				.setBillNo(model.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(model));// FetchType.EAGER
		portalDealDataLogic.data4Portal(pp);
		return "";
	}

	/**
	 * 订单状态变更及审核
	 * 
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param purchaseOrderId 主单ID
	 * @param status 要变更的状态或审核结果
	 * @param message 其他需要保存的消息，如审核意见等
	 * @return 变更后的订单
	 * @throws Exception
	 */
	public PurchaseOrder dealPurchaseOrder(Long userId, String userName, Long purchaseOrderId, String status, String message,
			String platForm, String... ids) {
		PurchaseOrder entity = dao.getById(purchaseOrderId);
		if (entity == null) {
			return null;
		}

		// 设置流程类型
		if (PurchaseOrderEvent.TORELEASE.name().equals(status) && null == entity.getCheckFirst()) {
			String checkFirst = this.getPurchaseOrderControl(entity, PurchaseOrderConstant.GROOVY_CHECKFIRST);
			if (StringUtils.isNotBlank(checkFirst)) {
				entity.setCheckFirst(Integer.parseInt(checkFirst));
			}
		}

		String vendorAcceptStr = this.getPurchaseOrderControl(entity, PurchaseOrderConstant.GROOVY_ACCEPT);

		// 1 先确认后审核
		boolean flag = entity.getCheckFirst() == 1; // 订单确认标识：1先确认后审核,0是先审核后确认

		// 供应商默认接受标识
		boolean vendorAccept = PurchaseOrderConstant.GROOVY_YES.equals(vendorAcceptStr) ? true : false;

		// 发布操作
		if (PurchaseOrderEvent.TORELEASE.name().equals(status)) {
			toRelease(entity, userId, userName, flag, vendorAccept);
			// 提交审核操作
		} else if (PurchaseOrderEvent.TOCONFIRM.name().equals(status)) {
			toConfirm(entity, userId);
			// 审核不过
		} else if (PurchaseOrderEvent.TONOPASS.name().equals(status)) {
			toNoPass(entity, userId, message, flag, vendorAccept);
			// 审核通过
		} else if (PurchaseOrderEvent.TOPASS.name().equals(status)) {
			toPass(entity, userId, message, flag, vendorAccept);
			// 确认变更
		} else if (PurchaseOrderEvent.TOFIRMHOLD.name().equals(status)) {
			toFirmHold(entity, userId, userName, flag);
			if (!flag && entity.getCreateType().equals(PurchaseOrderType.FromInput)) {
				doSync(purchaseOrderId, null, status);
			}
			// 供应商接受操作
		} else if (PurchaseOrderEvent.TOACCEPT.name().equals(status)) {
			toAccept(entity, userId, userName, flag);
			if (!flag && entity.getCreateType().equals(PurchaseOrderType.FromInput)) {
				doSync(purchaseOrderId, null, status);
			}
			// 供应商拒绝操作
		} else if (PurchaseOrderEvent.TOREJECT.name().equals(status)) {
			toReject(entity, userId, userName, message);
			// 取消操作
		} else if (PurchaseOrderEvent.TOCANCEL.name().equals(status)) {
			toCancel(entity, userId, userName);
			doSync(purchaseOrderId, null, status);
			// 关闭操作
		} else if (PurchaseOrderEvent.TOCLOSE.name().equals(status)) {
			toClose(entity, userId, userName);
			doSync(purchaseOrderId, null, status);
			// 确认拒绝操作
		} else if (PurchaseOrderEvent.TOFIRMREJECT.name().equals(status)) {
			toFirmReject(entity, userId, userName);
			if (!flag && entity.getCreateType().equals(PurchaseOrderType.FromInput)) {
				doSync(purchaseOrderId, null, status);
			}
		}

		// 存在错误消息
		if (StringUtils.isNotBlank(entity.getReturnValue())) {
			return entity;
		}

		// 更新订单
		entity.setModifyUserId(userId);
		entity.setModifyUserName(userName);
		entity.setModifyTime(Calendar.getInstance());
		entity = dao.save(entity);

		// 保存日志
		PurchaseOrderEvent event = PurchaseOrderEvent.valueOf(status);
		String eventDesc = event.getEventDesc();
		if (eventDesc.equals("发布订单")) {
			addLog(userId, userName, entity.getPurchaseOrderId(), "采购订单发布", SrmConstants.PERFORM_AUDIT, entity.getPurchaseOrderNo(),
					platForm);
		} else if (eventDesc.equals("接受订单")) {
			addLog(userId, userName, entity.getPurchaseOrderId(), "采购订单接受", SrmConstants.PERFORM_ACCEPT, entity.getPurchaseOrderNo(),
					SrmConstants.PLATFORM_WEB);
		} else if (eventDesc.equals("拒绝订单")) {
			addLog(userId, userName, entity.getPurchaseOrderId(), "采购订单拒绝，原因:" + message, SrmConstants.PERFORM_REFUSE,
					entity.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		} else if (eventDesc.equals("订单完成")) {
			addLog(userId, userName, entity.getPurchaseOrderId(), "采购订单完成，原因:" + message, SrmConstants.PERFORM_TOCOMPLETE,
					entity.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		} else if (eventDesc.equals("取消订单")) {
			addLog(userId, userName, entity.getPurchaseOrderId(), "采购订单取消，原因:" + message, SrmConstants.PERFORM_TOCANCEL,
					entity.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		} else if (eventDesc.equals("确认订单变更")) {
			addLog(userId, userName, entity.getPurchaseOrderId(), StringUtils.isBlank(message) ? "采购订单确认变更" : "采购订单确认变更,原因:" + message,
					SrmConstants.PERFORM_TOPASS, entity.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		} else if (eventDesc.equals("确认订单拒绝")) {
			addLog(userId, userName, entity.getPurchaseOrderId(), StringUtils.isBlank(message) ? "采购订单确认订单拒绝" : "采购订单确认订单拒绝,原因:" + message,
					SrmConstants.PERFORM_TOPASS, entity.getPurchaseOrderNo(), SrmConstants.PLATFORM_WEB);
		}
		return entity;
	}

	/**
	 * 确认拒绝
	 * 
	 * @param entity
	 * @param userId
	 * @param userName
	 */
	protected void toFirmReject(PurchaseOrder entity, Long userId, String userName) {
		entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.FIRMREJECT);
		entity.setPurchaseOrderState(PurchaseOrderState.CANCEL);

		if (entity.getPurchaseOrderDetails() != null) {
			for (PurchaseOrderDetail purchaseOrderDetail : entity.getPurchaseOrderDetails()) {
				// 细单删除标识设为是
				purchaseOrderDetail.setDeleteFlag(1);
				// 如果来源于采购申请，则更新采购申请明细的已创建订单量、可转单量
				if (purchaseOrderDetail.getSourceCode().equals(3L)) {
					PurchasingRequisitionTrans prt = purchasingRequisitionTransLogic
							.findById(purchaseOrderDetail.getPurchasingRequisitionColId());
					PurchasingRequisitionCollection prc = prt.getPurchasingRequisitionCollection();

					BigDecimal oldTransferQuantity = prt.getTransferQuantity();
					// 回置可转单数量
					prt.setTransferQuantity(oldTransferQuantity.add(purchaseOrderDetail.getBuyerQty()));
					if (prt.getTransferQuantity().compareTo(prt.getAssignedQuantity()) == 0) {
						// 修改为未转单
						prt.setIsTransfered("0");
					}
					// 更新转单数据
					purchasingRequisitionTransLogic.save(prt);
					// 设置采购申请为未使用
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("EQ_isUsed", "1");
					params.put("EQ_purchasingRequisitionNo", prc.getPurchasingRequisitionNo());
					PurchasingRequisition pr = purchasingRequisitionLogic.findOne(params);
					// 转单已分配数量 = 可转单数量，设置采购申请使用状态为未使用
					if (pr != null && prt.getTransferQuantity().compareTo(prt.getAssignedQuantity()) == 0) {
						pr.setIsUsed("0");
						purchasingRequisitionLogic.save(pr);
					}

					// 修改采购申请明细的已创建订单量、可转单量
					BigDecimal oldTransQty = prc.getTransferedQuantity();// 原已转单数量
					prc.setTransferedQuantity(oldTransQty.subtract(purchaseOrderDetail.getBuyerQty()));// 更新已转单数量
					// 更新可转单数量
					if (prc.getCanTransferQuantity().add(purchaseOrderDetail.getBuyerQty()).compareTo(prc.getQuantityDemanded()) > 0) {
						prc.setCanTransferQuantity(prc.getQuantityDemanded());
					} else {
						prc.setCanTransferQuantity(prc.getCanTransferQuantity().add(purchaseOrderDetail.getBuyerQty()));
					}
					PurchasingRequisitionCollectionLogic.save(prc);
				}
			}
		}

		// @Message-CGD_FIRMREJECT 确认拒绝提醒供应商
		List<User> users = findUserByVendor(entity.getVendorErpCode());

		// 发送消息
		sendNotify(entity, userId, DataUtils.fetchAsList(users, "userId", Long.class), "CGD_FIRMREJECT");
		PortalParameters pp = new PortalParameters();
		// 完成我的待办
		// Map<String, Object> params = finish4ToDealParam(entity, null);
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
		// data4PortalParam(params);
		pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
				.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity)); // FetchType.EAGER
		portalDealDataLogic.data4Portal(pp);
	}

	/**
	 * 根据供应商编码获取用户信息
	 * 
	 * @param vendorErpCode
	 * @return
	 */
	protected List<User> findUserByVendor(String vendorErpCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_erpCode", vendorErpCode);
		List<User> users = userLogic.findAll(new FeignParam<User>(params));
		return users;
	}

	/**
	 * 获取中文资源
	 * 
	 * @return
	 */
	protected String getResource(String key) {
		Map<String, String> resourceMap = new HashMap<String, String>();
		resourceMap.put("porder.purchaseOrderCheckStateCheck", "待确认");
		resourceMap.put("porder.purchaseOrderCheckStateAccept", "接受");
		resourceMap.put("porder.purchaseOrderCheckStateHold", "变更");
		resourceMap.put("porder.purchaseOrderCheckStateFirmhold", "确认变更");
		resourceMap.put("porder.purchaseOrderCheckStateReject", "拒绝");
		resourceMap.put("porder.purchaseOrderCheckStateFirmreject", "确认拒绝");
		resourceMap.put("porder.purchaseOrderStateNew", "新建");
		resourceMap.put("porder.purchaseOrderStateRelease", "发布");
		resourceMap.put("porder.purchaseOrderStateOpen", "执行");
		resourceMap.put("porder.purchaseOrderStateClose", "关闭");
		resourceMap.put("porder.purchaseOrderStateCancel", "取消");
		resourceMap.put("state.confirm", "待审核");
		resourceMap.put("state.nopass", "审核不过");
		resourceMap.put("state.pass", "审核通过");

		return resourceMap.get(key);
	}

	/**
	 * 确认变更
	 * 
	 * @param entity
	 * @param userId
	 * @param userName
	 * @param flag
	 * @return
	 */
	protected void toRelease(PurchaseOrder entity, Long userId, String userName, boolean flag, boolean vendorAccept) {
		entity.setPurchaseOrderState(PurchaseOrderState.RELEASE);

		// 先确认后审核
		if (flag) {
			// 1先确认后审核,直接取 提交时的日期
			entity.setOrderReleaseTime(Calendar.getInstance());
			if (vendorAccept) {
				entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.ACCEPT);
				entity.setVendorConfirmTime(Calendar.getInstance());
			} else {
				entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.CONFIRM);
			}
		}

		// 先确认后审批 且供应商默认接受
		if (flag && vendorAccept && !PurchaseOrderFlowState.PASS.equals(entity.getPurchaseOrderFlowState())) {
			toConfirm(entity, userId);

			// 先审批后确认
		} else if (!flag) {

			if (!PurchaseOrderFlowState.PASS.equals(entity.getPurchaseOrderFlowState())) {
				toConfirm(entity, userId);
			}

			if (PurchaseOrderFlowState.PASS.equals(entity.getPurchaseOrderFlowState())) {
				if (vendorAccept) {
					entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.ACCEPT);
					entity.setVendorConfirmTime(Calendar.getInstance());
				} else {
					entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.CONFIRM);
				}
			}
		}

		// 默认接受且审核通过
		if (PurchaseOrderCheckState.ACCEPT.equals(entity.getPurchaseOrderCheckState())
				&& PurchaseOrderFlowState.PASS.equals(entity.getPurchaseOrderFlowState())) {
			entity.setPurchaseOrderState(PurchaseOrderState.OPEN);
		}

		sendPublishMessage(entity, userId);

	}

	/**
	 * 发布信息给供应商
	 * 
	 * @param entity 采购订单
	 * @param userId 操作者
	 */
	protected void sendPublishMessage(PurchaseOrder entity, Long userId) {
		// @Message-CGD_PUBLISH 待确认状态--发送提醒供应商
		if (PurchaseOrderCheckState.CONFIRM.equals(entity.getPurchaseOrderCheckState())
				&& !PurchaseOrderFlowState.CONFIRM.equals(entity.getPurchaseOrderFlowState())) {
			String notifyCode = "CGD_PUBLISH";
			List<User> users = findUserByVendor(entity.getVendorErpCode());

			// 发送消息
			sendNotify(entity, userId, DataUtils.fetchAsList(users, "userId", Long.class), notifyCode);

			PortalParameters pp = new PortalParameters();
			User user = userLogic.findById(userId);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("billNo", entity.getErpPurchaseOrderNo());
			params.put("userName", user.getUserName());
			pp.addPortalMethod(PortalMethodType.ST_ADD).setRemindInfoCode(notifyCode)
					.setRemindParamsMap(extraparams(entity, userId, users, notifyCode)).setReceiverCode(entity.getVendorErpCode())
					.setCreatorId(entity.getCreateUserId().toString());
			// 供应商待办通知
			pp.addPortalMethod(PortalMethodType.TODEAL_ADD, "TODEAL_CGD_PUBLISH", params, users);
			pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
					.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity)); // ,
																									// FetchType.EAGER
			portalDealDataLogic.data4Portal(pp);
		}

	}

	/**
	 * 确认变更
	 * 
	 * @param entity
	 * @param userId
	 * @param userName
	 * @param flag
	 * @return
	 */
	protected void toFirmHold(PurchaseOrder entity, Long userId, String userName, boolean flag) {
		entity.setViewFlag(1);// 置为"已查看"
		entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.FIRMHOLD);

		// @Message-CGD_FIRMHOLD 确认变更提醒供应商
		List<User> users = findUserByVendor(entity.getVendorErpCode());

		// 发送消息
		sendNotify(entity, userId, DataUtils.fetchAsList(users, "userId", Long.class), "CGD_FIRMHOLD");

		PortalParameters pp = new PortalParameters();

		// 完成我的待办
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
		pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
				.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity, FetchType.EAGER));
		portalDealDataLogic.data4Portal(pp);

		// 先确认后审批
		if (flag) {
			toConfirm(entity, userId);
		}

		// 审核通过
		if (entity.getPurchaseOrderFlowState().equals(PurchaseOrderFlowState.PASS)) {
			// 置为执行
			entity.setPurchaseOrderState(PurchaseOrderState.OPEN);
		}

	}

	/**
	 * 供应商拒绝操作
	 * 
	 * @param entity
	 * @param userId
	 * @param userName
	 * @return
	 */
	protected void toReject(PurchaseOrder entity, Long userId, String userName, String message) {
		entity.setViewFlag(1);// 置为"已查看"
		entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.REJECT);
		entity.setVendorConfirmTime(Calendar.getInstance());

		eventPublisher.afterCommit(this, entity, new TransactionEventCallback<PurchaseOrder>() {

			@Override
			public void handle(PurchaseOrder entity) {
				// @Message-CGD_REJECT 拒绝订单
				String notifyCode = "CGD_REJECT";
				// 发送消息
				sendNotify(entity, userId, Arrays.asList(entity.getCreateUserId()), notifyCode);

				PortalParameters pp = new PortalParameters();

				// 采购-完成跟踪
				// mapLists.add(finish4ServiceTraceParam(entity));
				pp.addPortalMethod(PortalMethodType.ST_DELETE);
				User user = userLogic.findById(userId);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("1", entity.getErpPurchaseOrderNo());
				params.put("0", user.getUserName());
				// 采购-变更待办
				List<User> users = new ArrayList<User>();
				users.add(userLogic.findById(entity.getCreateUserId()));
				// mapLists.addAll(data4ToDealParam(entity, notifyCode, users,
				// userId));
				pp.addPortalMethod(PortalMethodType.TODEAL_ADD, notifyCode, params, users);
				// 采购-业务通知
				pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
						.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity));// FetchType.EAGER
				portalDealDataLogic.data4Portal(pp);
			}
		});

	}

	/**
	 * 供应商同意操作
	 * 
	 * @param entity
	 * @param userId
	 * @param userName
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	protected void toAccept(PurchaseOrder entity, Long userId, String userName, boolean flag) {
		entity.setViewFlag(1);// 置为"已查看"
		entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.ACCEPT);
		entity.setVendorConfirmTime(Calendar.getInstance());

		PortalParameters pp = new PortalParameters();
		// 采购-完成跟踪
		pp.addPortalMethod(PortalMethodType.ST_DELETE);
		// 完成待办通知
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
		pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
				.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity));// FetchType.EAGER
		portalDealDataLogic.data4Portal(pp);

		// 先确认后审批
		if (flag) {
			toConfirm(entity, userId);
		}

		// 审核通过
		if (entity.getPurchaseOrderFlowState() != null && entity.getPurchaseOrderFlowState().equals(PurchaseOrderFlowState.PASS)) {
			// 置为执行
			entity.setPurchaseOrderState(PurchaseOrderState.OPEN);
		}

		// @Message-CGD_ACCEPT 接受订单
		// 发送消息
		sendNotify(entity, userId, Arrays.asList(entity.getCreateUserId()), "CGD_ACCEPT");

	}

	/**
	 * 关闭操作
	 * 
	 * @param entity
	 * @param userId
	 * @param userName
	 * @return
	 */
	protected Boolean toClose(PurchaseOrder entity, Long userId, String userName) {
		StringBuffer materialCodes = new StringBuffer("");
		// 校验是否满足关闭条件
		if (entity.getPurchaseOrderDetails() != null) {
			for (PurchaseOrderDetail pod : entity.getPurchaseOrderDetails()) {
				if (BigDecimal.ZERO.compareTo(pod.getQtySend()) < 0) {
					materialCodes.append(pod.getRowIds() + " ");
				}
			}
		}

		if (materialCodes.length() > 0) {
			entity.setReturnValue("purchaseOrder.message.doNotClose" + "_" + materialCodes.toString());
			return false;
		}
		// 校验通过后设置状态
		entity.setPurchaseOrderState(PurchaseOrderState.CLOSE);
		if (entity.getPurchaseOrderDetails() != null) {
			for (PurchaseOrderDetail pod : entity.getPurchaseOrderDetails()) {
				pod.setCloseFlag(1);
			}
		}

		save(entity);
		eventPublisher.afterCommit(this, entity, new TransactionEventCallback<PurchaseOrder>() {

			@Override
			public void handle(PurchaseOrder entity) {
				PortalParameters pp = new PortalParameters();

				// 完成提醒
				pp.addPortalMethod(PortalMethodType.IW_DELETE);
				// 完成日程
				pp.addPortalMethod(PortalMethodType.SCHEDULE_DELETE);
				// 调用内门户
				pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
						.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity, FetchType.EAGER));
				portalDealDataLogic.data4Portal(pp);
			}
		});

		return true;
	}

	/**
	 * 取消操作
	 * 
	 * @param entity
	 * @param userId
	 * @param userName
	 * @return
	 */
	protected boolean toCancel(PurchaseOrder entity, Long userId, String userName) {
		StringBuffer materialCodeSb = new StringBuffer("");
		StringBuffer materialCodeSb2 = new StringBuffer("");
		for (PurchaseOrderDetail pod : entity.getPurchaseOrderDetails()) {
			if (BigDecimal.ZERO.compareTo(pod.getQtySend()) < 0) {
				materialCodeSb.append(pod.getRowIds() + " ");
			}
			if (BigDecimal.ZERO.compareTo(pod.getQtyArrive()) < 0) {
				materialCodeSb2.append(pod.getRowIds() + " ");
			}
		}
		if (materialCodeSb.length() > 0) {
			entity.setReturnValue("purchaseOrder.message.doNotCancel" + "_" + materialCodeSb.toString());
			return false;
		}
		if (materialCodeSb2.length() > 0) {
			entity.setReturnValue("purchaseOrder.message2.doNotCancel" + "_" + materialCodeSb2.toString());
			return false;
		}
		entity.setPurchaseOrderState(PurchaseOrderState.CANCEL);
		for (PurchaseOrderDetail pod : entity.getPurchaseOrderDetails()) {
			pod.setDeleteFlag(1);
			// 如果来源于采购申请，则更新采购申请明细的已创建订单量、可转单量
			if (pod.getSourceCode().equals(3L)) {
				PurchasingRequisitionTrans prt = purchasingRequisitionTransLogic.findById(pod.getPurchasingRequisitionColId());
				PurchasingRequisitionCollection prc = prt.getPurchasingRequisitionCollection();

				BigDecimal oldTransferQuantity = prt.getTransferQuantity();
				// 回置可转单数量
				prt.setTransferQuantity(oldTransferQuantity.add(pod.getBuyerQty()));
				if (prt.getTransferQuantity().compareTo(prt.getAssignedQuantity()) == 0) {
					// 修改为未转单
					prt.setIsTransfered("0");
				}
				// 更新转单数据
				purchasingRequisitionTransLogic.save(prt);
				// 设置采购申请为未使用
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("EQ_isUsed", "1");
				params.put("EQ_purchasingRequisitionNo", prc.getPurchasingRequisitionNo());
				PurchasingRequisition pr = purchasingRequisitionLogic.findOne(params);
				// 转单已分配数量 = 可转单数量，设置采购申请使用状态为未使用
				if (pr != null && prt.getTransferQuantity().compareTo(prt.getAssignedQuantity()) == 0) {
					pr.setIsUsed("0");
					purchasingRequisitionLogic.save(pr);
				}

				// 修改采购申请明细的已创建订单量、可转单量
				BigDecimal oldTransQty = prc.getTransferedQuantity();// 原已转单数量
				prc.setTransferedQuantity(oldTransQty.subtract(pod.getBuyerQty()));// 更新已转单数量
				// 更新可转单数量
				if (prc.getCanTransferQuantity().add(pod.getBuyerQty()).compareTo(prc.getQuantityDemanded()) > 0) {
					prc.setCanTransferQuantity(prc.getQuantityDemanded());
				} else {
					prc.setCanTransferQuantity(prc.getCanTransferQuantity().add(pod.getBuyerQty()));
				}
				PurchasingRequisitionCollectionLogic.save(prc);
			}
		}

		entity = dao.save(entity);
		if (StringUtils.isNotEmpty(entity.getErpPurchaseOrderNo())
				&& (!entity.getErpPurchaseOrderNo().startsWith(SrmConstants.BILLTYPE_CGD))) {
			entity.setErpSynState(0);// 同步前先置状态为未同步
		}

		eventPublisher.afterCommit(this, entity, new TransactionEventCallback<PurchaseOrder>() {

			@Override
			public void handle(PurchaseOrder entity) {
				sendCancelMessage(entity, userId);
			}
		});

		return true;
	}

	/**
	 * 发送取消的消息
	 * 
	 * @param entity 实体
	 * @param userId 操作人
	 */
	public void sendCancelMessage(PurchaseOrder entity, Long userId) {
		// @Message-CGD_CANCEL 取消订单--发送提醒供应商、创建者
		List<User> users = findUserByVendor(entity.getVendorErpCode());
		users.add(userLogic.findById(entity.getCreateUserId()));// 创建者

		// 发送消息
		sendNotify(entity, userId, DataUtils.fetchAsList(users, "userId", Long.class), "CGD_CANCEL");

		PortalParameters pp = new PortalParameters();
		// List<Map<String, Object>> mapLists = new ArrayList<Map<String,
		// Object>>();
		// 采购-完成跟踪
		// mapLists.add(finish4ServiceTraceParam(entity));
		pp.addPortalMethod(PortalMethodType.ST_DELETE);
		// 完成待办通知
		// mapLists.add(finish4ToDealParam(entity, null));
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
		// 调用内门户接口
		// data4PortalParam(mapLists);
		pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
				.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity)); // FetchType.EAGER
		portalDealDataLogic.data4Portal(pp);
	}

	/**
	 * 处理提交审核
	 * 
	 * @param entity
	 * @return
	 */
	protected void toConfirm(PurchaseOrder entity, Long userId) {
		try {
			if (null == entity.getPurchaseOrderFlowState() || PurchaseOrderFlowState.NOPASS.equals(entity.getPurchaseOrderFlowState())) {
				HashMap<String, Object> map = CommonUtil.toMap(entity, new String[] { "purchaseOrderDetails" });

				super.submitBpm(entity.getPurchaseOrderId(), userId, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提交审核成功事件
	 * 
	 * @param entity
	 * @param userId
	 * @param assignees
	 * @param properties
	 * @return
	 */
	@Override
	protected PurchaseOrder afterBpmSubmit(PurchaseOrder entity, Long userId, List<User> assignees, Map<String, Object> properties) {
		entity = super.afterBpmSubmit(entity, userId, assignees, properties);
		entity.setPurchaseOrderFlowState(PurchaseOrderFlowState.CONFIRM);
		entity = save(entity);
		return entity;
	}

	/**
	 * 工作流完成事件
	 * 
	 * @param entity
	 * @param userId
	 * @param createUserId
	 * @param message
	 * @param properties
	 * @return
	 */
	@Override
	protected PurchaseOrder afterComplete(PurchaseOrder entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		entity = super.afterComplete(entity, userId, createUserId, message, properties);
		entity.setPurchaseOrderFlowState(PurchaseOrderFlowState.PASS);

		// 1 先确认后审核
		boolean flag = entity.getCheckFirst() == 1; // 订单确认标识：1先确认后审核,0是先审核后确认

		String vendorAcceptStr = this.getPurchaseOrderControl(entity, PurchaseOrderConstant.GROOVY_ACCEPT);
		// 供应商默认接受标识
		boolean vendorAccept = PurchaseOrderConstant.GROOVY_YES.equals(vendorAcceptStr) ? true : false;

		// 先审核后确认
		if (!flag) {
			// 先审核后确认，订单发布时间直接取审核通过的时间
			entity.setOrderReleaseTime(Calendar.getInstance());
			if (vendorAccept) {
				entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.ACCEPT);
				entity.setVendorConfirmTime(Calendar.getInstance());
				entity.setPurchaseOrderState(PurchaseOrderState.OPEN);
			} else {
				entity.setPurchaseOrderCheckState(PurchaseOrderCheckState.CONFIRM);
			}
		} else {
			entity.setPurchaseOrderState(PurchaseOrderState.OPEN);
		}

		entity = save(entity);

		return entity;
	}

	/**
	 * 审核完成后的所有操作事务已提交后触发，例如修改状态后要同步SAP等，可以在这里实现。
	 * 
	 * @param entity 实体对象
	 * @return
	 */
	@Override
	protected PurchaseOrder afterCompleteCommit(PurchaseOrder entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		doSync(entity.getPurchaseOrderId(), null, PurchaseOrderEvent.TOPASS.name());
		entity = dao.getById(entity.getPurchaseOrderId());
		return entity;
	};

	/**
	 * 审核不过事件
	 */
	@Override
	protected PurchaseOrder afterReject(PurchaseOrder entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		super.afterReject(entity, userId, createUserId, message, properties);
		// 审核不过相关处理逻辑
		entity.setPurchaseOrderFlowState(PurchaseOrderFlowState.NOPASS);
		entity = dao.save(entity);

		return entity;
	}

	@Override
	protected PurchaseOrder notifyAfterReject(PurchaseOrder entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		// TODO Auto-generated method stub
		super.notifyAfterReject(entity, userId, createUserId, message, properties);

		// 1 先确认后审核
		boolean flag = entity.getCheckFirst() == 1; // 订单确认标识：1先确认后审核,0是先审核后确认

		String vendorAcceptStr = this.getPurchaseOrderControl(entity, PurchaseOrderConstant.GROOVY_ACCEPT);
		// 供应商默认接受标识
		boolean vendorAccept = PurchaseOrderConstant.GROOVY_YES.equals(vendorAcceptStr) ? true : false;

		List<User> users = new ArrayList<User>();
		// 先确认后审核且需要供应商确认,也需要提醒供应商
		if (flag && !vendorAccept) {
			users = findUserByVendor(entity.getVendorErpCode());
		}
		User createUser = userLogic.findById(entity.getCreateUserId());
		// 创建者
		users.add(createUser);

		// @Message-CGD_NOPASS 审核不过-发送消息提醒创建者
		String notifyCode = "CGD_NOPASS";

		User user = userLogic.findById(userId);
		sendNotify(entity, userId, DataUtils.fetchAsList(users, "userId", Long.class), notifyCode, message);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("0", entity.getVendorName());
		params.put("1", entity.getPurchaseOrderNo());
		params.put("2", user.getUserName());

		PortalParameters pp = new PortalParameters();
		// 采购-流程结束跟踪
		// mapLists.add(finish4ApprovalTraceParam(entity));
		pp.addPortalMethod(PortalMethodType.AT_DELETE);
		// 采购-待办审核不过
		users.clear();
		users.add(createUser);
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
		pp.addPortalMethod(PortalMethodType.TODEAL_ADD, notifyCode, params, createUser);
		// 采购-业务通知
		pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
				.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity));// FetchType.EAGER
		portalDealDataLogic.data4Portal(pp);
		return entity;
	}

	/**
	 * 处理待办事项（重写）
	 */
	@Override
	protected void customToDeal(PurchaseOrder entity, ApprovalState approvalState, Long userId, String... message) {
		super.customToDeal(entity, approvalState, userId, message);
		entity = dao.getById(entity.getPurchaseOrderId());
		sendPublishMessage(entity, userId);

	}

	/**
	 * 处理审核不通过
	 * 
	 * @param entity
	 * @param message
	 * @return
	 */
	protected void toNoPass(PurchaseOrder entity, Long userId, String message, boolean flag, boolean vendorAccept) {
		if (PurchaseOrderFlowState.CONFIRM.equals(entity.getPurchaseOrderFlowState())) {
			HashMap<String, Object> map = CommonUtil.toMap(entity, new String[] { "purchaseOrderDetails" });
			map.put("pass", false);
			entity = super.reject(entity.getPurchaseOrderId(), userId, message);

		}
	}

	/**
	 * 处理审核通过
	 * 
	 * @param entity
	 * @param message
	 * @return
	 */
	protected void toPass(PurchaseOrder entity, Long userId, String message, boolean flag, boolean vendorAccept) {
		try {
			HashMap<String, Object> map = CommonUtil.toMap(entity, new String[] { "purchaseOrderDetails" });
			entity = super.approve(entity.getPurchaseOrderId(), userId, message, map);
			String processInstanceId = bpmService.getProcessInstanceId(SrmConstants.BILLTYPE_CGD, entity.getPurchaseOrderId().toString());

			if ((PurchaseOrderCheckState.ACCEPT.equals(entity.getPurchaseOrderCheckState())
					|| PurchaseOrderCheckState.FIRMHOLD.equals(entity.getPurchaseOrderCheckState()))
					&& PurchaseOrderFlowState.PASS.equals(entity.getPurchaseOrderFlowState())) {
				entity.setPurchaseOrderState(PurchaseOrderState.OPEN);
			}

			entity = dao.save(entity);

			// 发送知会
			bpmSrmLogic.bpmNotify(entity.getCreateUserId(), SrmConstants.BILLTYPE_CGD, "采购订单", entity.getPurchaseOrderId().toString(),
					entity.getPurchaseOrderNo().toString(), processInstanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 供应商变更订单
	 * 
	 * @param userId 用户ID
	 * @param userName 用户名
	 * @param entity 要变更的订单信息
	 * @return 变更后的订单实体
	 */
	public PurchaseOrder toHold(Long userId, String userName, PurchaseOrder entity) {
		PurchaseOrder model = dao.getById(entity.getPurchaseOrderId());
		if (model == null) {
			return null;
		}

		if (!model.getPurchaseOrderState().equals(PurchaseOrderState.RELEASE)
				&& !model.getPurchaseOrderCheckState().equals(PurchaseOrderCheckState.CONFIRM)
				&& !model.getPurchaseOrderCheckState().equals(PurchaseOrderCheckState.REJECT)) {
			return null;
		}

		Map<Long, Calendar> vendorTimeMap = new HashMap<Long, Calendar>();
		for (PurchaseOrderDetail detail : entity.getPurchaseOrderDetails()) {
			vendorTimeMap.put(detail.getPurchaseOrderDetailId(), detail.getVendorTime());
		}

		for (PurchaseOrderDetail pod : model.getPurchaseOrderDetails()) {
			if (vendorTimeMap.containsKey(pod.getPurchaseOrderDetailId())) {
				pod.setVendorTime(vendorTimeMap.get(pod.getPurchaseOrderDetailId()));
			}
			pod.setPurchaseOrder(model);
		}

		model.setVendorConfirmTime(Calendar.getInstance());
		model.setPurchaseOrderCheckState(PurchaseOrderCheckState.HOLD);
		model = dao.save(model);

		eventPublisher.afterCommit(this, model, new TransactionEventCallback<PurchaseOrder>() {

			@Override
			public void handle(PurchaseOrder model) {
				// TODO Auto-generated method stub
				// @Message-CGD_HOLD 变更订单
				String notifyCode = "CGD_HOLD";
				sendNotify(model, userId, Arrays.asList(model.getCreateUserId()), notifyCode);

				PortalParameters pp = new PortalParameters();
				User user = userLogic.findById(userId);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("billNo", entity.getErpPurchaseOrderNo());
				params.put("userName", user.getUserName());
				pp.addPortalMethod(PortalMethodType.AT_DELETE);
				// 采购-新增待办
				List<User> users = new ArrayList<User>();
				users.add(userLogic.findById(model.getCreateUserId()));
				pp.addPortalMethod(PortalMethodType.TODEAL_ADD, "TODEAL_CGD_HOLD", params, users);
				// 采购-业务通知
				pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
						.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity));// FetchType.EAGER
				portalDealDataLogic.data4Portal(pp);
			}
		});

		return model;
	}

	/**
	 * 发送消息通知
	 * 
	 * @param entity 实体
	 * @param sendUserId 发送者ID
	 * @param receive 接收人
	 * @param messageCode 消息编码
	 * @param message 用户输入的操作意见
	 */
	protected void sendNotify(PurchaseOrder entity, Long sendUserId, List<Long> receivers, String messageCode, String... message) {
		entity = dao.getById(entity.getPurchaseOrderId());
		// --------------------新增app消息提醒需要的参数map
		// start------------------20161109-------------
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String modifyTime = formatter.format(Calendar.getInstance().getTime());
		User sendUser = userLogic.findById(sendUserId);

		Map<String, Object> extraparams = new HashMap<String, Object>();
		extraparams.put("billNo", entity.getErpPurchaseOrderNo());
		extraparams.put("billId", entity.getPurchaseOrderId().toString());
		extraparams.put("vendorCode", entity.getVendorErpCode());
		extraparams.put("vendorName", entity.getVendorName());
		extraparams.put("userName", sendUser.getUserName());
		extraparams.put("userCode", sendUser.getUserCode());

		extraparams.put("first", "");
		extraparams.put("keyword1", entity.getErpPurchaseOrderNo());
		extraparams.put("keyword2", DateUtils.format(Calendar.getInstance().getTime(), "yyyy年MM月dd日 HH:ss"));
		extraparams.put("keyword3", "");
		extraparams.put("remark", "");

		// --------------------新增app消息提醒需要的参数map
		// end------------------20161109-------------
		if (messageCode.equals("CGD_HOLD") || messageCode.equals("CGD_FIRMHOLD") || messageCode.equals("CGD_REJECT")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderCheckState().desc()));
			extraparams.put("keyword3", modifyTime);
		} else if (messageCode.equals("CGD_FIRMREJECT") || messageCode.equals("CGD_ACCEPT")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderCheckState().desc()));
			extraparams.put("keyword3", modifyTime);
		} else if (messageCode.equals("CGD_PUBLISH") || messageCode.equals("CGD_CANCEL")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderState().desc()));
			extraparams.put("keyword3", modifyTime);
		} else if (messageCode.equals("CGD_CONFIRM")) {
			extraparams.put("keyword2", modifyTime);
		} else if (messageCode.equals("CGD_NOPASS") || messageCode.equals("CGD_PASS")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderFlowState().desc()));
			extraparams.put("remark", message[0]);
		}

		notifySender
				.send(new NotifyParam(entity.getClientCode(), sendUser.getUserId(), receivers, messageCode, new String[] {}, extraparams));
	}

	/**
	 * 获取采购待处理的数据id
	 * 
	 * @return
	 */
	public String findIdByStatus(Long userId, Boolean isBuyer) {
		List<PurchaseOrderCheckState> checkStates = Arrays.asList(PurchaseOrderCheckState.HOLD, PurchaseOrderCheckState.REJECT);
		List<PurchaseOrderFlowState> flowStates = Arrays.asList(PurchaseOrderFlowState.NOPASS, PurchaseOrderFlowState.CONFIRM);
		List<Long> list = new ArrayList<Long>();

		List<Long> idsLong = new ArrayList<Long>();
		List<String> idsStr = bpmService.getAllUncheckedKeys(userId.toString(), SrmConstants.BILLTYPE_CGD);
		for (String id : idsStr) {
			idsLong.add(Long.parseLong(id));
		}

		if (0 == idsLong.size()) {
			idsLong.add(0L);
		}

		List<List<Long>> idsList = divisionList(idsLong);

		if (isBuyer) {
			// flowStates = Arrays.asList(PurchaseOrderFlowState.NOPASS);
			List<PurchaseOrderFlowState> flowStates2 = Arrays.asList(PurchaseOrderFlowState.CONFIRM);
			// a) 创建者为登录用户+订单状态为“发布”+审核状态为“审核不过”
			// b) 创建者为登录用户+订单状态为“发布”+审核状态为“空”+确认状态为“变更”
			// c) 创建者为登录用户+订单状态为“发布”+审核状态为“空”+确认状态为“拒绝”
			// d) 创建者为登录用户+订单状态为“发布”+审核状态为“审核通过”+确认状态为“变更”
			// e) 创建者为登录用户+订单状态为“发布”+审核状态为“审核通过”+确认状态为“拒绝”
			String query = " select po.purchaseOrderId from PurchaseOrder po"
					+ " where po.purchaseOrderState = ?1 and po.createUserId = ?2 and ((po.purchaseOrderFlowState = ?3) or "
					+ " ((po.purchaseOrderFlowState is null and po.purchaseOrderCheckState in ?4)) or "
					+ " (po.purchaseOrderFlowState = ?5 and po.purchaseOrderCheckState in ?6))";
			List<Long> createList = dao.executeQuery(query, Long.class, PurchaseOrderState.RELEASE, userId, PurchaseOrderFlowState.NOPASS,
					checkStates, PurchaseOrderFlowState.PASS, checkStates);
			list.addAll(createList);
			// f) 审核状态为“待审核”+当前用户应该审核的单据（登录用户角色属于当前单据审核节点角色）
			query = "select po.purchaseOrderId from PurchaseOrder po where po.purchaseOrderFlowState in ?1 or po.purchaseOrderId in ?2 ";
			for (List<Long> temp : idsList) {
				temp = dao.executeQuery(query, Long.class, flowStates2, temp);
				list.addAll(temp);
			}

		} else {
			String query = "select po.purchaseOrderId from PurchaseOrder po where po.purchaseOrderState = ?1"
					+ " and ((po.purchaseOrderCheckState in ?2) " + " or (po.purchaseOrderFlowState in ?3 or po.purchaseOrderId in ?4))";

			for (List<Long> temp : idsList) {
				temp = dao.executeQuery(query, Long.class, PurchaseOrderState.RELEASE, checkStates, flowStates, temp);
				list.addAll(temp);
			}
		}

		String returnStr = "";
		for (Long id : list) {
			returnStr += "," + id;
		}

		if (StringUtils.isNotBlank(returnStr)) {
			returnStr = returnStr.substring(1);
		} else {
			returnStr = "0";
		}

		return returnStr;
	}

	/**
	 * 分割集合
	 * 
	 * @return 返回分割的集合
	 */
	protected List<List<Long>> divisionList(List<Long> ids) {
		List<List<Long>> idsList = new ArrayList<List<Long>>();
		int size = ids.size();

		if (size <= 1000) {
			idsList.add(ids);
		} else {
			int count = 0;
			while (true) {
				if (size < 1000) {
					idsList.add(ids.subList(count * 1000, ids.size() - 1));
					break;
				} else {
					size = size - 1000;
					idsList.add(ids.subList(count * 1000, 999 + count * 1000));
					count++;
				}
			}
		}

		return idsList;
	}

	/**
	 * 同步到ERP
	 * 
	 * @param purchaseOrderId 采购订单ID
	 * @param list 交货明细行，仅供排程使用，其他情况传null
	 * @param status 采购订单事件，确认变更，确认拒绝，其他
	 * @return 同步结果
	 * @throws Exception
	 */
	public Boolean doSync(Long purchaseOrderId, List<SendScheduleDetail> list, String status) {
		PurchaseOrder entity = dao.getById(purchaseOrderId);
		if (entity == null) {
			return false;
		}
		// 同步接口
		String checkFirst = this.getPurchaseOrderControl(entity, PurchaseOrderConstant.GROOVY_SYNC);

		// 接口设置为不需要同步，而且是走到执行状态的订单才设置成已同步
		if (PurchaseOrderConstant.GROOVY_NO.equals(checkFirst)) {
			entity.setErpSynState(1);
			entity = dao.save(entity);
			updateDetailSyncState(entity);
			return true;
			// 同步操作
		} else {
			// 同步到sap
			return syncToSap(list, entity);
		}
	}

	/**
	 * 同步到SAP
	 * 
	 * @param sendScheduleDetails
	 * @param entity
	 */
	protected Boolean syncToSap(List<SendScheduleDetail> sendScheduleDetails, PurchaseOrder entity) {

		// 接口编码
		String interfaceCode = SrmConstants.SRM_PO_CODE;
		Calendar.getInstance();
		IdUtils.genNextId();
		String json = null;
		JSONObject jsonMap = null;
		JSONObject dataMap = null;
		JSONObject returnMap = null;
		String errorCode = null;
		// Map<String, String> params = new HashMap<String, String>();
		String params = "";
		WsRequestLog wrlog = new WsRequestLog();
		try {
			// 设置排程明细
			setSynsScheduleDetails(sendScheduleDetails, entity);

			// 设置同步中状态
			entity.setErpSynState(SYNC_SYNCHRONIZING);
			dao.save(entity);
			updateDetailSyncState(entity);

			String orderNo = entity.getErpPurchaseOrderNo();

			if (entity.getErpPurchaseOrderNo().contains("CGD")) {
				entity.setErpPurchaseOrderNo("");
			}
			params = DataUtils.toJson(entity, FetchType.EAGER);
			entity.setErpPurchaseOrderNo(orderNo);
			wrlog = wsRequestLogLogic.createTargetErpLog(interfaceCode, entity.getPurchaseOrderNo(), params);

			try {
				// 调用RESTFul接口并获取返回值
				json = interactLogic.invoke("AddUpdateOrder", params);
				Calendar.getInstance();
				jsonMap = JSONObject.parseObject(json);
				dataMap = jsonMap.getJSONObject("data");
				if (dataMap.getJSONObject("EtReturn") == null) {
					returnMap = dataMap;

				} else {
					returnMap = dataMap.getJSONObject("EtReturn").getJSONObject("item");

				}
				errorCode = returnMap.getString("Type");
			} catch (Exception e) {
				wsRequestLogLogic.addFailLog(wrlog, json);
			}
			// 同步失败
			if (!"S".equals(errorCode)) {
				String message = StringUtils.isBlank(returnMap.getString("Message")) ? returnMap.toString()
						: returnMap.getString("Message");

				entity.setErpSynState(SYNC_EXCEPTION);
				entity.setErpReturnMsg(message.length() > 1000 ? message.substring(0, 999) : message);
				entity = dao.save(entity);
				updateDetailSyncState(entity);

				if (null != sendScheduleDetails) {
					for (SendScheduleDetail sendDetail : sendScheduleDetails) {
						sendDetail = sendScheduleDetailEao.getById(sendDetail.getSendScheduleDetailId());
						SendSchedule sendschedule = sendscheduleEao
								.getById(sendDetail.getSendScheduleCommon().getSendSchedule().getSendScheduleId());
						sendschedule.setErpSynState(SYNC_EXCEPTION);
						sendscheduleEao.save(sendschedule);
					}
				}
				wsRequestLogLogic.addErrorLog(wrlog, json);
				return false;
			}

			// 默认同步失败的设置
			String message = returnMap.getString("Message");
			Integer status = SYNC_EXCEPTION;// 默认同步失败
			boolean isSysnc = false;

			// 同步成功
			if (SAP_OK.equals(returnMap.getString("Type"))) {
				orderNo = returnMap.getString("MessageV1");
				status = SYNC_SYNCHRONIZED;
				isSysnc = true;
				wsRequestLogLogic.addSuccessLog(wrlog, json);
			} else {
				wsRequestLogLogic.addErrorLog(wrlog, json);
			}

			// 设置排程同步状态
			if (null != sendScheduleDetails) {
				for (SendScheduleDetail sendDetail : sendScheduleDetails) {
					sendDetail = sendScheduleDetailEao.getById(sendDetail.getSendScheduleDetailId());
					SendSchedule sendschedule = sendscheduleEao
							.getById(sendDetail.getSendScheduleCommon().getSendSchedule().getSendScheduleId());
					sendschedule.setErpSynState(status);
					sendscheduleEao.save(sendschedule);
				}
			}

			entity.setErpPurchaseOrderNo(orderNo);
			entity.setErpReturnMsg(message);
			entity.setErpSynState(status);
			entity = dao.save(entity);
			updateDetailSyncState(entity);

			return isSysnc;
		} catch (Exception e) {
			e.printStackTrace();
			entity.setErpSynState(SYNC_EXCEPTION);
			entity = dao.save(entity);
			updateDetailSyncState(entity);
			wsRequestLogLogic.addFailLog(wrlog, json);
			return false;
		}
	}

	/**
	 * 设置送货排程同步数据
	 * 
	 * @param sendScheduleDetails 送货排程
	 * @param entity 订单
	 */
	protected void setSynsScheduleDetails(List<SendScheduleDetail> sendScheduleDetails, PurchaseOrder entity) {
		// 设置排程
		if (null != sendScheduleDetails) {
			for (PurchaseOrderDetail detail : entity.getPurchaseOrderDetails()) {
				for (SendScheduleDetail scDetail : sendScheduleDetails) {
					if (scDetail.getRowIds().equals(detail.getSrmRowids())) {
						if (null == detail.getSendScheduleDetails()) {
							detail.setSendScheduleDetails(new ArrayList<SendScheduleDetailVo>());
						}

						SendScheduleDetailVo vo = new SendScheduleDetailVo();
						BeanUtils.copyProperties(scDetail, vo);
						detail.getSendScheduleDetails().add(vo);
					}
				}
			}
		}
	}

	/**
	 * save 订单
	 * 
	 * @throws Exception
	 */
	public PurchaseOrder persistPo(PurchaseOrder model, String submitFlag, String platForm) {
		// 流程顺序
		String checkFirst = this.getPurchaseOrderControl(model, PurchaseOrderConstant.GROOVY_CHECKFIRST);
		model.setCheckFirst(Integer.parseInt(checkFirst));

		// 如果单号为空
		if (StringUtils.isBlank(model.getPurchaseOrderNo())) {
			String poorderNo = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_CGD);
			model.setPurchaseOrderNo(poorderNo);
			model.setErpPurchaseOrderNo(poorderNo);
		}
		model.setErpSynState(0);
		model.setPurchaseOrderState(PurchaseOrderState.NEW);
		model.setViewFlag(0);// 供应商未查看
		model.setIsVendorView(0);
		model.setPurchaseOrderTime(Calendar.getInstance());
		model.setCreateType(PurchaseOrderType.FromInput);
		model.setIsRevocationCheck(0);

		for (PurchaseOrderDetail pod : model.getPurchaseOrderDetails()) {

			pod.setPurchaseOrder(model);
			pod = setBaseInfo(pod, model);

			// 如果来源于采购申请，则更新采购申请明细的已创建订单量、可转单量
			if (pod.getSourceCode().equals(3L)) {
				resetPrInfo(pod);
			}
		}
		model = dao.save(model);
		// 提交待审
		if ("audit".equals(submitFlag)) {
			dealPurchaseOrder(model.getCreateUserId(), model.getCreateUserName(), model.getPurchaseOrderId(),
					PurchaseOrderEvent.TORELEASE.name(), model.getClientCode(), platForm);
			addLog(model.getCreateUserId(), model.getCreateUserName(), model.getPurchaseOrderId(), "采购订单提交", SrmConstants.PERFORM_AUDIT,
					model.getPurchaseOrderNo(), platForm);
		} else {
			addLog(model.getCreateUserId(), model.getCreateUserName(), model.getPurchaseOrderId(), "采购订单创建", SrmConstants.PERFORM_SAVE,
					model.getPurchaseOrderNo(), platForm);
		}
		return model;
	}

	/**
	 * 校验货源清单
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> validateSourceList(PurchaseOrder model) {
		Map<String, Object> map = new HashMap<String, Object>();
		HashSet<String> set = new HashSet<String>();
		List<PurchaseOrderDetail> purchaseOrderDetails = model.getPurchaseOrderDetails();
		if (purchaseOrderDetails != null && purchaseOrderDetails.size() > 0) {
			for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
				String key = purchaseOrderDetail.getMaterialCode() + "_" + purchaseOrderDetail.getPlantCode() + "_"
						+ model.getVendorErpCode();
				// 管控点设置不校验的物料直接跳过下一步
				if (map.containsKey(key)) {
					continue;
				}
				String f = getPurchaseOrderControl(purchaseOrderDetail, PurchaseOrderConstant.GROOVY_SOURCELIST);
				if (PurchaseOrderConstant.GROOVY_NO.equals(f)) {
					map.put(key, "1");
				}
				if (StringUtils.isNotBlank(purchaseOrderDetail.getMaterialCode())) {
					set.add(purchaseOrderDetail.getMaterialCode());
				}
			}
		}
		Map<String, Object> matirialPlantMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		if (set.size() > 0) {// 来源价格主数据
			params.put("IN_materialCode", StringUtils.join(set.toArray(), ","));
			// params.put("EQ_plantCode", detail.getPlantCode());
			List<MaterialPlant> mpList = materialPlantLogic.findAll(new FeignParam<MaterialPlant>(params));
			if (mpList != null && mpList.size() > 0) {
				for (MaterialPlant mp : mpList) {
					matirialPlantMap.put(mp.getMaterialCode() + "_" + mp.getPlantCode(), mp);
				}
			}
		}

		// 获取货源清单
		Map<String, Object> sourceListDtlMap = new HashMap<String, Object>();
		params.clear();
		params.put("EQ_vendorCode", model.getVendorErpCode());
		params.put("LE_effectiveDate", Calendar.getInstance());
		params.put("GT_failureDate", Calendar.getInstance());
		List<SourceListDtl> sdList = sourceListDtlLogic.findAll(params);
		if (sdList != null && sdList.size() > 0) {
			for (SourceListDtl sd : sdList) {
				SourceList sl = sd.getSourceList();
				String key = sl.getMaterialCode() + "_" + sl.getPlantCode() + "_" + sd.getVendorCode();
				if (!sourceListDtlMap.containsKey(key)) {
					sourceListDtlMap.put(key, 1);
				}
			}
		}

		// 需要校验货源清单
		if (purchaseOrderDetails != null && purchaseOrderDetails.size() > 0) {
			for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails) {
				String key = purchaseOrderDetail.getMaterialCode() + "_" + purchaseOrderDetail.getPlantCode();
				String _key = purchaseOrderDetail.getMaterialCode() + "_" + purchaseOrderDetail.getPlantCode() + "_"
						+ model.getVendorErpCode();
				if (map.containsKey(_key)) {
					continue;
				}

				MaterialPlant mp = (MaterialPlant) matirialPlantMap.get(key);
				if (mp == null || mp.getSourceList() == null || (!mp.getSourceList().equals("X") && !mp.getSourceList().equals("1"))) {
					map.put(_key, "1");
				} else if (!sourceListDtlMap.containsKey(_key)) {
					map.put(_key, "0");
				} else {
					map.put(_key, "1");
				}
			}
		}
		return map;
	}

	/**
	 * 校验货源清单
	 * 
	 * @param mpMap
	 * 
	 * @return
	 * @throws Exception
	 */
	protected Boolean validateSourceList(PurchaseOrderDetail detail, PurchaseOrder model, Map<String, MaterialPlant> mpMap) {
		String flag = getPurchaseOrderControl(detail, PurchaseOrderConstant.GROOVY_SOURCELIST);
		Map<String, Object> params = new HashMap<String, Object>();
		String key = detail.getMaterialCode() + "_" + detail.getPlantCode();
		MaterialPlant mp = mpMap.get(key);
		// 需要校验货源清单
		if (PurchaseOrderConstant.GROOVY_NO.equals(flag)
				|| (null == mp || mp.getSourceList() == null || (!mp.getSourceList().equals("X") && !mp.getSourceList().equals("1")))) {
			return true;
		}

		params.clear();
		params.put("EQ_sourceList_plantCode", detail.getPlantCode());
		params.put("EQ_sourceList_materialCode", detail.getMaterialCode());
		params.put("EQ_vendorCode", model.getVendorErpCode());
		params.put("LE_effectiveDate", Calendar.getInstance());
		params.put("GT_failureDate", Calendar.getInstance());

		Long count = sourceListDtlLogic.count(params);

		// XX物料未维护货源清单
		// porder.materialNotSourceList={0}物料未维护货源清单
		if (0 == count) {
			return false;
		}

		return true;
	}

	/**
	 * 设置价格主数据质检标识（库存类型）
	 * 
	 * @author linshp
	 */
	public void setMMQualityCheck(List<MaterialMasterPriceOrderDtlView> viewList) {

		if (0L != viewList.size()) {
			Map<String, Object> searchParams = new HashMap<String, Object>();
			for (MaterialMasterPriceOrderDtlView view : viewList) {
				searchParams.clear();
				searchParams.put("EQ_plantCode", view.getPlantCode());
				searchParams.put("EQ_materialCode", view.getMaterialCode());
				MaterialPlant materialPlant = materialPlantLogic.findOne(new FeignParam<MaterialPlant>(searchParams));
				if (materialPlant != null) {
					view.setQualityCheck(materialPlant.getQualityCheck());
				}
			}
		}

	}

	/**
	 * 设置价格主数据库存地点(导入)
	 */
	public void setMMStockLocation(List<MaterialMasterPriceOrderDtlView> viewList) {

		if (0L != viewList.size()) {
			Map<String, Object> searchParams = new HashMap<String, Object>();
			String plantCodes = "";
			for (MaterialMasterPriceOrderDtlView view : viewList) {
				plantCodes += "," + view.getPlantCode();
			}

			if (StringUtils.isNotBlank(plantCodes)) {
				searchParams.clear();
				searchParams.put("IN_plantCode", plantCodes);
				List<StockLocation> list = stockLocationLogic.findAll(new FeignParam<StockLocation>(searchParams));
				Map<String, String> stockCodeMap = new HashMap<String, String>();
				for (StockLocation stockLocation : list) {
					if (!stockCodeMap.containsKey(stockLocation.getPlantCode())) {
						stockCodeMap.put(stockLocation.getPlantCode(), stockLocation.getStockLocationCode());
					}
				}

				for (MaterialMasterPriceOrderDtlView view : viewList) {
					String code = stockCodeMap.get(view.getPlantCode());
					code = StringUtils.isNotBlank(code) ? code : "";
					view.setStockLocationCode(code);
				}
			}
		}

	}

	/**
	 * 设置价格主数据库存地点(来源价格主数据创建明细)
	 */
	@Override
	public List<MaterialMasterPriceOrderDtlView> setMMStockLocation2(List<MaterialMasterPriceOrderDtlView> vlist) {

		if (vlist != null && vlist.size() > 0) {
			Map<String, Object> searchParams = new HashMap<String, Object>();
			String materialCodes = "";
			for (MaterialMasterPriceOrderDtlView view : vlist) {
				materialCodes += "," + view.getMaterialCode();
			}
			if (StringUtils.isNotBlank(materialCodes)) {
				searchParams.clear();
				searchParams.put("IN_materialCode", materialCodes);
				List<MaterialPlant> list = materialPlantLogic.findAll(new FeignParam<MaterialPlant>(searchParams));
				Map<String, String> stockCodeMap = new HashMap<String, String>();
				for (MaterialPlant materialPlant : list) {
					if (!stockCodeMap.containsKey(materialPlant.getMaterialCode() + "_" + materialPlant.getPlantCode())) {
						stockCodeMap.put(materialPlant.getMaterialCode() + "_" + materialPlant.getPlantCode(),
								materialPlant.getStorlocCode() + "," + materialPlant.getQualityCheck());
					}
				}

				for (MaterialMasterPriceOrderDtlView view : vlist) {
					String code = stockCodeMap.get(view.getMaterialCode() + "_" + view.getPlantCode());
					code = StringUtils.isNotBlank(code) ? code : "";
					String[] codes = code.split(",");
					view.setStockLocationCode(codes[0]);
					view.setQualityCheck(codes[1]);
				}
			}
		}
		return vlist;
	}

	/**
	 * 批量导入数据
	 * 
	 * @param wb excel工作簿对象
	 * @param webPrams 前台传入的web条件
	 * @param userId 当前用户id
	 * @param userName 当前用户名称
	 * @param clientCode 客户端编码
	 * @return 返回校验消息
	 */
	public String batchImportExcel(List<PurchaseOrder> orders, List<PurchaseOrderDetail> orderDetials,
			Map<String, PurchaseOrder> orderNoPOrgMap, Map<String, Object> webPrams, Long userId, String userName, String clientCode) {
		// String message = getBatchExcelData(wb, orders, orderDetials);//
		// 获取excel数据
		String message = validateMainExcelData(orders, orderDetials, orderNoPOrgMap);
		if (StringUtils.isNotBlank(message)) {
			return message;
		}

		// 设置采购订单主单
		bulidPurchaseOrder(orders, orderDetials, userId, userName, clientCode);

		return message;
	}

	/**
	 * 设置采购订单主单字段
	 * 
	 * @param orders 主单集合
	 * @param details 订单明细集合
	 * @param userId 当前用户ID
	 * @param userName 当前用户名称
	 * @throws Exception
	 */
	protected void bulidPurchaseOrder(List<PurchaseOrder> orders, List<PurchaseOrderDetail> details, Long userId, String userName,
			String clientCode) {
		Map<String, List<PurchaseOrderDetail>> detailMap = new HashMap<String, List<PurchaseOrderDetail>>();
		for (PurchaseOrderDetail detail : details) {
			String key = detail.getPurchaseOrder().getPurchaseOrderNo();
			List<PurchaseOrderDetail> tempDetails = new ArrayList<PurchaseOrderDetail>();

			detail.setCreateUserId(userId);
			detail.setCreateUserName(userName);
			detail.setCreateTime(Calendar.getInstance());

			if (detailMap.containsKey(key)) {
				tempDetails = detailMap.get(key);
				tempDetails.add(detail);
				detailMap.put(key, tempDetails);
			} else {
				tempDetails.add(detail);
				detailMap.put(key, tempDetails);
			}
		}

		for (PurchaseOrder order : orders) {
			order.setCreateUserId(userId);
			order.setCreateUserName(userName);
			order.setCreateTime(Calendar.getInstance());
			order.setClientCode(clientCode);
			order.setCreateType(PurchaseOrderType.FromBatchLimit);

			if (detailMap.containsKey(order.getPurchaseOrderNo())) {
				order.setPurchaseOrderDetails(detailMap.get(order.getPurchaseOrderNo()));
				order.setPurchaseOrderNo(null);
				setSrmRowId(order);// 设置行号
				countTotalAmout(order);// 计算总金额
				order = persistPo(order, "audit", SrmConstants.PLATFORM_WEB);

				// 记录日志
				// addLog(userId, userName, order.getPurchaseOrderId(), "导入成功",
				// "BATCHIMPORT");
				addLog(userId, userName, order.getPurchaseOrderId(), "采购订单导入", SrmConstants.PERFORM_AUDIT, order.getPurchaseOrderNo(),
						SrmConstants.PLATFORM_WEB);
				System.out.println("import purchaseOrder end");

			}
		}
	}

	/**
	 * 计算总金额
	 * 
	 * @param order
	 */
	protected void countTotalAmout(PurchaseOrder order) {
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (PurchaseOrderDetail detail : order.getPurchaseOrderDetails()) {
			BigDecimal lineItemValAmt = detail.getLineItemValAmt();
			totalAmount = totalAmount.add(lineItemValAmt);
		}

		order.setTotalAmount(totalAmount);
	}

	/**
	 * 设置采购订单明细srm行号
	 */
	protected void setSrmRowId(PurchaseOrder order) {
		int i = 1;
		for (PurchaseOrderDetail detail : order.getPurchaseOrderDetails()) {
			detail.setSrmRowids(i * 10);
			i++;
		}
	}

	/**
	 * 校验主单数据
	 * 
	 * @param orders
	 * @return
	 * @throws Exception
	 */
	protected String validateMainExcelData(List<PurchaseOrder> orders, List<PurchaseOrderDetail> orderDetails,
			Map<String, PurchaseOrder> orderNoPOrgMap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder messages = new StringBuilder();
		String eachMessage = "";

		if (0 == orders.size()) {
			return getText("porder.exsitBaseInfo");// "不存在基本信息";
		}

		if (0 == orderDetails.size()) {
			return getText("porder.exsitDetailInfo");// "不存在明细信息";
		}

		int index = 1;
		// 批导数据-所有采购组织编码
		List<String> purchasingOrgCodes = new ArrayList<String>();
		// 批导数据-所有采购组编码
		List<String> purchasingGroupCodes = new ArrayList<String>();
		// 批导数据-所有公司编码
		List<String> companyCodes = new ArrayList<String>();
		// 批导数据-所有供应商ERP编码
		List<String> vendorErpCodes = new ArrayList<String>();

		for (PurchaseOrder order : orders) {
			if (!purchasingOrgCodes.contains(order.getPurchasingOrgCode())) {
				purchasingOrgCodes.add(order.getPurchasingOrgCode());
			}
			if (!purchasingGroupCodes.contains(order.getPurchasingGroupCode())) {
				purchasingGroupCodes.add(order.getPurchasingGroupCode());
			}
			if (!companyCodes.contains(order.getCompanyCode())) {
				companyCodes.add(order.getCompanyCode());
			}
			if (!vendorErpCodes.contains(order.getVendorErpCode())) {
				vendorErpCodes.add(order.getVendorErpCode());
			}

			// 关联单号
			if (StringUtils.isBlank(order.getPurchaseOrderNo())) {
				// porder.batchImport.relationNo.notNull = 基本信息工作簿中第X行中的关联单号不能为空
				eachMessage += getResource("porder.batchImport.relationNo.notNull", index + "") + "\n";
			}

			// 采购组织编码
			if (StringUtils.isBlank(order.getPurchasingOrgCode())) {
				// porder.batchImport.purchasingOrgCode.notNull =
				// 基本信息工作簿中第X行中的采购组织编码不能为空
				eachMessage += getResource("porder.batchImport.purchasingOrgCode.notNull", index + "") + "\n";
			}

			// 采购组
			if (StringUtils.isBlank(order.getPurchasingGroupCode())) {
				// porder.batchImport.purchasingGroupCode.notNull =
				// 基本信息工作簿中第X行中的采购组编码不能为空
				eachMessage += getResource("porder.batchImport.purchasingGroupCode.notNull", index + "") + "\n";
			}

			// 公司编码
			if (StringUtils.isBlank(order.getCompanyCode())) {
				// porder.batchImport.companyCode.notNull =
				// 基本信息工作簿中第X行中的公司编码不能为空
				eachMessage += getResource("porder.batchImport.companyCode.notNull", index + "") + "\n";
			}

			// 供应商编码
			if (StringUtils.isBlank(order.getVendorCode())) {
				// porder.batchImport.vendorCode.notNull =
				// 基本信息工作簿中第X行中的供应商编码不能为空
				eachMessage += getResource("porder.batchImport.vendorCode.notNull", index + "") + "\n";
			}

			// 订单日期
			if (null == order.getPurchaseOrderTime()) {
				// porder.batchImport.purchaseOrderTime.notNull =
				// 基本信息工作簿中第X行中的订单日期不能为空
				eachMessage += getResource("porder.batchImport.purchaseOrderTime.notNull", index + "") + "\n";
			} else {
				eachMessage = validateDate(order.getPurchaseOrderTime(), index, "purchaseOrderTime");
			}

			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}

		if (0 < messages.length()) {
			return messages.toString();
		}

		// 获取采购组织基础数据
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("IN_purchasingOrgCode", StringUtils.join(purchasingOrgCodes, ","));
		List<PurchasingOrganization> pOrgList = purchasingOrganizationLogic.findAll(new FeignParam<PurchasingOrganization>(params));
		Map<String, String> pOrgMap = new HashMap<String, String>();
		for (PurchasingOrganization pOrg : pOrgList) {
			pOrgMap.put(pOrg.getPurchasingOrgCode(), pOrg.getPurchasingOrgName());
		}

		// 获取采购组
		params.clear();
		params.put("IN_purchasingGroupCode", StringUtils.join(purchasingGroupCodes, ","));
		List<PurchasingGroup> pGroupList = purchasingGroupLogic.findAll(new FeignParam<PurchasingGroup>(params));
		Map<String, String> pGroupMap = new HashMap<String, String>();
		for (PurchasingGroup pGroup : pGroupList) {
			pGroupMap.put(pGroup.getPurchasingGroupCode(), pGroup.getPurchasingGroupName());
		}

		// 获取公司
		params.clear();
		params.put("IN_companyCode", StringUtils.join(companyCodes, ","));
		List<Company> companyList = companyLogic.findAll(new FeignParam<Company>(params));
		Map<String, String> companyMap = new HashMap<String, String>();
		for (Company company : companyList) {
			companyMap.put(company.getCompanyCode(), company.getCompanyName());
		}

		// 获取供应商
		params.clear();
		params.put("IN_vendorErpCode", StringUtils.join(vendorErpCodes, ","));
		List<Vendor> vendorList = vendorLogic.findAll(params);
		Map<String, Vendor> vendorMap = new HashMap<String, Vendor>();
		for (Vendor vendor : vendorList) {
			vendorMap.put(vendor.getVendorErpCode(), vendor);
		}

		index = 1;

		for (PurchaseOrder order : orders) {
			// 采购组织校验
			if (!pOrgMap.containsKey(order.getPurchasingOrgCode())) {
				// porder.batchImport.pOrgCode = 基本信息工作簿中第{0}行的采购组织编码不存在
				eachMessage += getResource("porder.batchImport.pOrgCode", index + "") + "\n";
			} else {
				order.setPurchasingOrgName(pOrgMap.get(order.getPurchasingOrgCode()));
			}

			// 采购组校验
			if (!pGroupMap.containsKey(order.getPurchasingGroupCode())) {
				// porder.batchImport.groupCode = 基本信息工作簿中第{0}行的采购组编码不存在
				eachMessage += getResource("porder.batchImport.groupCode", index + "") + "\n";
			} else {
				order.setPurchasingGroupName(pGroupMap.get(order.getPurchasingGroupCode()));
			}

			// 公司校验
			if (!companyMap.containsKey(order.getCompanyCode())) {
				// porder.batchImport.companyCode = 基本信息工作簿中第{0}行的公司编码不存在
				eachMessage += getResource("porder.batchImport.companyCode", index + "") + "\n";
			} else {
				order.setCompanyName(companyMap.get(order.getCompanyCode()));
			}

			// 供应商校验
			if (!vendorMap.containsKey(order.getVendorCode())) {
				// porder.batchImport.vendorCode = 基本信息工作簿中第{0}行的供应商编码不存在
				eachMessage += getResource("porder.batchImport.vendorCode", index + "") + "\n";
			} else {
				Vendor vendor = vendorMap.get(order.getVendorErpCode());
				order.setVendorName(vendor.getVendorName());
				order.setVendorErpCode(vendor.getVendorErpCode());
				order.setVendorCode(vendor.getVendorCode());
			}

			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}

		if (StringUtils.isNotBlank(messages.toString())) {
			return messages.toString();
		}

		// 采购组织公司视图
		params.clear();
		params.put("IN_companyCode", StringUtils.join(companyCodes, ","));
		params.put("IN_purchasingOrgCode", StringUtils.join(purchasingOrgCodes, ","));
		List<CompanyPurchaseOrg> comPOrgList = companyPurchaseOrgLogic.findAll(new FeignParam<CompanyPurchaseOrg>(params));
		Map<String, CompanyPurchaseOrg> comPOrgMap = new HashMap<String, CompanyPurchaseOrg>();
		for (CompanyPurchaseOrg comPOrg : comPOrgList) {
			String key = comPOrg.getPurchasingOrgCode() + "_" + comPOrg.getCompanyCode();
			comPOrgMap.put(key, comPOrg);
		}

		// 采购组织供应商关系
		params.clear();
		params.put("IN_vendor_vendorErpCode", StringUtils.join(vendorErpCodes, ","));
		params.put("IN_purchasingOrgCode", StringUtils.join(purchasingOrgCodes, ","));
		List<VendorPorgDtl> VendorPorgDtlList = vendorPorgDtlEao.findAll(params);
		Map<String, VendorPorgDtl> VendorPorgMap = new HashMap<String, VendorPorgDtl>();
		for (VendorPorgDtl vendorPorgDtl : VendorPorgDtlList) {
			String key = vendorPorgDtl.getPurchasingOrgCode() + "_" + vendorPorgDtl.getVendor().getVendorErpCode();
			VendorPorgMap.put(key, vendorPorgDtl);
		}

		// 汇率
		params.clear();
		params.put("LE_effectiveDate", sdf.format(new Date()));
		List<ExchangeRate> exchangeRates = exchangeRateLogic.findAll(new FeignParam<ExchangeRate>(params));
		Map<String, BigDecimal> exchangeRateMap = new HashMap<String, BigDecimal>();
		for (ExchangeRate rate : exchangeRates) {
			String key = rate.getStadCurrencyCode() + "_" + rate.getOrigCurrencyCode();
			exchangeRateMap.put(key, rate.getExchangeRate());
		}

		index = 1;
		for (PurchaseOrder order : orders) {
			// 采购组织公司关系校验
			String key = order.getPurchasingOrgCode() + "_" + order.getCompanyCode();
			if (!comPOrgMap.containsKey(key)) {
				// porder.batchImport.comPOrg = 基本信息工作簿中第{0}行的公司未分配对应的采购组织
				eachMessage += getResource("porder.batchImport.comPOrg", index + "") + "\n";
			}

			// 采购组织供应商校验
			key = order.getPurchasingOrgCode() + "_" + order.getVendorCode();
			if (!VendorPorgMap.containsKey(key)) {
				// porder.batchImport.vendorPorg = 基本信息工作簿中第{0}行的供应商未分配对应的采购组织
				eachMessage += getResource("porder.batchImport.vendorPorg", index + "") + "\n";
			} else {
				VendorPorgDtl porgDtl = VendorPorgMap.get(key);
				key = "CNY" + "_" + porgDtl.getCurrencyCode();

				if (!exchangeRateMap.containsKey(key)) {
					// porder.batchImport.exchangeRate.noExsit =
					// 基本信息工作簿中第{0}行没有维护有效的货币汇率
					eachMessage += getResource("porder.batchImport.exchangeRate.noExsit", index + "") + "\n";
				} else {
					order.setCurrencyRate(exchangeRateMap.get(key));
				}

				order.setCurrencyCode(porgDtl.getCurrencyCode());
				order.setCurrencyName(porgDtl.getCurrencyName());
			}

			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}

		if (StringUtils.isNotBlank(messages.toString())) {
			return messages.toString();
		}

		// 细单数据校验
		eachMessage = validateDetailExcelData(orders, orderDetails, purchasingOrgCodes, orderNoPOrgMap);
		messages.append(eachMessage);

		return messages.toString();
	}

	/**
	 * 
	 * @param orders
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	protected String validateDetailExcelData(List<PurchaseOrder> orders, List<PurchaseOrderDetail> orderDetails,
			List<String> purchasingOrgCodes, Map<String, PurchaseOrder> orderNoPOrgMap) {
		StringBuilder messages = new StringBuilder();
		String eachMessage = "";

		List<String> materialCodes = new ArrayList<String>();
		List<String> plantCodes = new ArrayList<String>();
		List<String> storeLocalCodes = new ArrayList<String>();
		int index = 1;

		for (PurchaseOrderDetail detail : orderDetails) {
			if (!materialCodes.contains(detail.getMaterialCode())) {
				materialCodes.add(detail.getMaterialCode());
			}

			if (!plantCodes.contains(detail.getPlantCode())) {
				plantCodes.add(detail.getPlantCode());
			}

			if (!storeLocalCodes.contains(detail.getStoreLocal())) {
				storeLocalCodes.add(detail.getStoreLocal());
			}

			// 关联单号
			if (null != detail.getPurchaseOrder() && StringUtils.isBlank(detail.getPurchaseOrder().getPurchaseOrderNo())) {
				// porderDetail.batchImport.relationNo.notNull =
				// 明细信息工作簿中第{0}行的关联单号不能为空
				eachMessage += getResource("porderDetail.batchImport.relationNo.notNull", index + "") + "\n";
			}

			// 行项目类别
			if (StringUtils.isBlank(detail.getLineItemTypeCode())) {
				// porderDetail.batchImport.lineItemTypeCode.notNull =
				// 明细信息工作簿中第{0}行的行项目类别不能为空
				eachMessage += getResource("porderDetail.batchImport.lineItemTypeCode.notNull", index + "") + "\n";
			}

			// 物料编码
			if (StringUtils.isBlank(detail.getMaterialCode())) {
				// porderDetail.batchImport.materialCode.notNull =
				// 明细信息工作簿中第{0}行的物料编码不能为空
				eachMessage += getResource("porderDetail.batchImport.materialCode.notNull", index + "") + "\n";
			}

			// 订单单位
			if (StringUtils.isBlank(detail.getUnitCode())) {
				// porderDetail.batchImport.unitCode.notNull =
				// 明细信息工作簿中第{0}行的订单单位不能为空
				eachMessage += getResource("porderDetail.batchImport.unitCode.notNull", index + "") + "\n";
			}

			// 数量
			if (null == detail.getBuyerQty()) {
				// porderDetail.batchImport.buyerQty.notNull =
				// 明细信息工作簿中第{0}行的数量不能为空
				eachMessage += getResource("porderDetail.batchImport.buyerQty.notNull", index + "") + "\n";
			}

			// 工厂编码
			if (StringUtils.isBlank(detail.getPlantCode())) {
				// porderDetail.batchImport.plantCode.notNull =
				// 明细信息工作簿中第{0}行的工厂编码不能为空
				eachMessage += getResource("porderDetail.batchImport.plantCode.notNull", index + "") + "\n";
			}

			// 库存地点编码
			if (StringUtils.isBlank(detail.getStoreLocal())) {
				// porderDetail.batchImport.storeLocal.notNull =
				// 明细信息工作簿中第{0}行的库存地点编码不能为空
				eachMessage += getResource("porderDetail.batchImport.storeLocal.notNull", index + "") + "\n";
			}

			Date currentDdate = new Date();
			currentDdate.setMinutes(0);
			currentDdate.setHours(0);
			currentDdate.setSeconds(0);

			// 交货日期
			if (null == detail.getBuyerTime()) {
				// porderDetail.batchImport.buyerTime.notNull =
				// 明细信息工作簿中第{0}行的交货日期不能为空
				eachMessage += getResource("porderDetail.batchImport.buyerTime.notNull", index + "") + "\n";
			} else if (detail.getBuyerTime().before(currentDdate)) {
				// porderDetail.batchImport.buyerTime.currentDdate =
				// 明细信息工作簿中第{0}行的交货日期不能小于当前日期
				eachMessage += getResource("porderDetail.batchImport.buyerTime.currentDdate", index + "") + "\n";
			} else {
				eachMessage = validateDate(detail.getBuyerTime(), index, "buyerTime");
			}

			// 确认交货日期
			if (null == detail.getVendorTime()) {
				// porderDetail.batchImport.vendorTime.notNull =
				// 明细信息工作簿中第{0}行的确认交货日期不能为空
				eachMessage += getResource("porderDetail.batchImport.vendorTime.notNull", index + "") + "\n";
			} else if (null != detail.getBuyerTime() && detail.getBuyerTime().after(detail.getVendorTime())) {
				// porderDetail.batchImport.vendorTime.buyerTime =
				// 明细信息工作簿中第{0}行的确认交货日期不能小于交货日期
				eachMessage += getResource("porderDetail.batchImport.vendorTime.buyerTime", index + "") + "\n";
			} else {
				eachMessage = validateDate(detail.getVendorTime(), index, "vendorTime");
			}

			// 是否免费
			if (null == detail.getIsFree()) {
				// porderDetail.batchImport.isFree.notNull =
				// 明细信息工作簿中第{0}行的是否免费不能为空
				eachMessage += getResource("porderDetail.batchImport.isFree.notNull", index + "") + "\n";
			}

			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}

		if (StringUtils.isNotBlank(messages.toString())) {
			return messages.toString();
		}

		Map<String, Object> params = new HashMap<String, Object>();

		// 获取所有物料
		params.put("IN_materialCode", StringUtils.join(materialCodes, ","));
		List<Material> materials = materialLogic.findAll(new FeignParam<Material>(params));
		Map<String, String> materialMap = new HashMap<String, String>();
		for (Material material : materials) {
			materialMap.put(material.getMaterialCode(), material.getMaterialName());
		}

		// 工厂编码
		params.clear();
		params.put("IN_plantCode", StringUtils.join(plantCodes, ","));
		List<Plant> plants = plantLogic.findAll(new FeignParam<Plant>(params));
		Map<String, String> plantMap = new HashMap<String, String>();
		for (Plant plant : plants) {
			plantMap.put(plant.getPlantCode(), plant.getPlantName());
		}

		// 获取库存
		params.clear();
		params.put("IN_stockLocationCode", StringUtils.join(storeLocalCodes, ","));
		params.put("IN_plantCode", StringUtils.join(plantCodes, ","));
		List<StockLocation> locations = stockLocationLogic.findAll(new FeignParam<StockLocation>(params));
		Map<String, StockLocation> locationMap = new HashMap<String, StockLocation>();
		Map<String, StockLocation> plantLocationMap = new HashMap<String, StockLocation>();
		for (StockLocation location : locations) {
			String key = location.getPlantCode() + "_" + location.getStockLocationCode();
			locationMap.put(location.getStockLocationCode(), location);
			plantLocationMap.put(key, location);
		}

		// 获取所有单位编码
		Map<String, Unit> unitMap = new HashMap<String, Unit>();
		params.clear();
		params.put("EQ_status", "1");
		List<Unit> units = unitLogic.findAll(new FeignParam<Unit>(params));
		for (Unit unit : units) {
			unitMap.put(unit.getUnitCode(), unit);
		}

		index = 1;

		for (PurchaseOrderDetail detail : orderDetails) {
			// 校验物料是否存在数据库
			if (!materialMap.containsKey(detail.getMaterialCode())) {
				// porder.batchImport.material = 明细信息工作簿中第{0}行的物料编码不存在
				eachMessage += getResource("porder.batchImport.material", index + "") + "\n";
			} else {
				detail.setMaterialName(materialMap.get(detail.getMaterialCode()));
			}

			// 校验订单单位是否存在数据库
			if (!unitMap.containsKey(detail.getUnitCode())) {
				// porder.batchImport.unit.noExsit = 明细信息工作簿中第{0}行的单位编码不存在
				eachMessage += getResource("porder.batchImport.unit.noExsit", index + "") + "\n";
			} else {
				Unit unit = unitMap.get(detail.getUnitCode());
				detail.setUnitName(unit.getUnitName());
			}

			// 校验工厂是否存在数据库
			if (!plantMap.containsKey(detail.getPlantCode())) {
				// porder.batchImport.plant = 明细信息工作簿中第{0}行的工厂不存在
				eachMessage += getResource("porder.batchImport.plant", index + "") + "\n";
			} else {
				detail.setPlantName(plantMap.get(detail.getPlantCode()));
			}

			// 校验库存是否存在数据库
			if (!locationMap.containsKey(detail.getStoreLocal())) {
				// porder.batchImport.location = 明细信息工作簿中第{0}行的库存地点编码不存在
				eachMessage += getResource("porder.batchImport.location", index + "") + "\n";
			}

			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}

		if (StringUtils.isNotBlank(messages.toString())) {
			return messages.toString();
		}

		// 工厂和采购组织关系
		params.clear();
		params.put("IN_plantCode", StringUtils.join(plantCodes, ","));
		params.put("IN_purchasingOrgCode", StringUtils.join(purchasingOrgCodes, ","));
		List<PlantPurchaseOrg> plantPOrgList = plantPurchaseOrgLogic.findAll(new FeignParam<PlantPurchaseOrg>(params));
		Map<String, PlantPurchaseOrg> plantPOrgMap = new HashMap<String, PlantPurchaseOrg>();
		for (PlantPurchaseOrg plantPOrg : plantPOrgList) {
			String key = plantPOrg.getPurchasingOrgCode() + "_" + plantPOrg.getPlantCode();
			plantPOrgMap.put(key, plantPOrg);
		}

		// 获取物料工厂视图
		params.clear();
		params.put("IN_materialCode", StringUtils.join(materialCodes, ","));
		params.put("IN_plantCode", StringUtils.join(plantCodes, ","));
		List<MaterialPlant> materialPlants = materialPlantLogic.findAll(new FeignParam<MaterialPlant>(params));
		Map<String, MaterialPlant> mpMap = new HashMap<String, MaterialPlant>();
		for (MaterialPlant mp : materialPlants) {
			String key = mp.getMaterialCode() + "_" + mp.getPlantCode();
			mpMap.put(key, mp);
		}

		index = 1;

		for (PurchaseOrderDetail detail : orderDetails) {
			String key = detail.getPurchaseOrder().getPurchasingOrgCode() + "_" + detail.getPlantCode();
			if (!plantPOrgMap.containsKey(key)) {
				// porder.batchImport.plantPOrg = 明细信息工作簿中第{0}行的工厂与采购组织不存在从属关系
				eachMessage += getResource("porder.batchImport.plantPOrg", index + "") + "\n";
			}

			// 校验工厂和库存是否存在关系
			key = detail.getPlantCode() + "_" + detail.getStoreLocal();
			if (!plantLocationMap.containsKey(key)) {
				// porder.batchImport.plantLocal = 明细信息工作簿中第{0}行中的库存与工厂不存在从属关系
				eachMessage += getResource("porder.batchImport.plantLocal", index + "") + "\n";
			}

			// 设置质检标识
			key = detail.getMaterialCode() + "_" + detail.getPlantCode();
			if (mpMap.containsKey(key)) {
				MaterialPlant mp = mpMap.get(key);
				if (StringUtils.isNotBlank(mp.getQualityCheck())) {
					detail.setStockType(mp.getQualityCheck());
				} else {
					// porder.batchImport.qualityCheck.isNull =
					// 明细信息工作簿中第{0}行中的物料与工厂的质检标识为空
					eachMessage += getResource("porder.batchImport.qualityCheck.isNull", index + "") + "\n";
				}
			}

			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}

		if (StringUtils.isNotBlank(messages.toString())) {
			return messages.toString();
		}

		// 价格主数据校验
		validateMaterialMasterPrice(orders, orderDetails, messages, unitMap);

		index = 1;
		// 货源清单
		for (PurchaseOrderDetail detail : orderDetails) {
			PurchaseOrder model = orderNoPOrgMap.get(detail.getPurchaseOrder().getPurchaseOrderNo());
			if (model != null) {
				Boolean falg = this.validateSourceList(detail, model, mpMap);
				if (!falg) {
					// porder.batchImport.materialNotSourceList.noExsit =
					// 明细信息工作簿中第{0}行未维护货源清单
					messages.append(getResource("porder.batchImport.materialNotSourceList.noExsit", index + "") + "\n");
				}
			}
			index++;
		}

		return messages.toString();
	}

	/**
	 * 校验价格主数据
	 * 
	 * @param orderDetails 订单明细
	 * @param messages 校验消息
	 * @param unitMap 单位map
	 */
	protected void validateMaterialMasterPrice(List<PurchaseOrder> orders, List<PurchaseOrderDetail> orderDetails, StringBuilder messages,
			Map<String, Unit> unitMap) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_groupCode", "pricingConditionType");
		params.put("EQ_status", 1);
		List<DataDict> dataDicts = dataDictlogic.findAll(new FeignParam<DataDict>(params));

		// 获取价格主数据
		Map<String, Object> materialMasterPriceDtlParams = new HashMap<String, Object>();
		Map<String, MaterialMasterPriceDtl> materialMasterPriceDtlMap = new HashMap<String, MaterialMasterPriceDtl>();
		for (PurchaseOrder order : orders) {

			materialMasterPriceDtlParams.clear();
			materialMasterPriceDtlParams.put("EQ_materialMasterPrice_purchasingOrgCode", order.getPurchasingOrgCode());
			materialMasterPriceDtlParams.put("EQ_materialMasterPrice_vendorErpCode", order.getVendorErpCode());
			materialMasterPriceDtlParams.put("EQ_currencyCode", order.getCurrencyCode());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			materialMasterPriceDtlParams.put("LE_effectiveDate", sdf.format(order.getPurchaseOrderTime().getTime()));
			materialMasterPriceDtlParams.put("GE_expirationDate", sdf.format(order.getPurchaseOrderTime().getTime()));
			List<MaterialMasterPriceDtl> materialMasterPriceDtls = materialMasterPriceDtlEao.findAll(materialMasterPriceDtlParams);

			for (MaterialMasterPriceDtl materialMasterPriceDtl : materialMasterPriceDtls) {
				MaterialMasterPrice materialMasterPrice = materialMasterPriceDtl.getMaterialMasterPrice();
				String key = materialMasterPrice.getPurchasingOrgCode() + "_" + materialMasterPrice.getPlantCode() + "_"
						+ materialMasterPrice.getMaterialCode() + materialMasterPrice.getVendorErpCode() + "_"
						+ materialMasterPrice.getRecordType() + "_" + materialMasterPriceDtl.getCurrencyCode();
				if (!materialMasterPriceDtlMap.containsKey(key)) {
					materialMasterPriceDtlMap.put(key, materialMasterPriceDtl);
				}

			}
		}

		String eachMessage = "";
		int index = 1;
		// 价格主数据校验
		for (PurchaseOrderDetail detail : orderDetails) {
			MaterialLadderPriceDtl priceDtl = null;
			// 根据采购组织、物料、工厂、供应商、行类型、货币 获取阶梯价格
			String key = detail.getPurchaseOrder().getPurchasingOrgCode() + "_" + detail.getPlantCode() + "_" + detail.getMaterialCode()
					+ detail.getPurchaseOrder().getVendorCode() + "_" + detail.getLineItemTypeCode() + "_"
					+ detail.getPurchaseOrder().getCurrencyCode();
			if (materialMasterPriceDtlMap.containsKey(key)) {
				MaterialMasterPriceDtl materialMasterPriceDtl = materialMasterPriceDtlMap.get(key);

				List<MaterialLadderPriceDtl> materialLadderPriceDtls = materialMasterPriceDtl.getMaterialLadderPriceDtls();
				// 获取阶梯报价
				if (materialLadderPriceDtls != null && materialLadderPriceDtls.size() > 0) {
					for (MaterialLadderPriceDtl materialLadderPriceDtl : materialLadderPriceDtls) {
						if (detail.getBuyerQty().compareTo(materialLadderPriceDtl.getStartNum()) >= 0
								&& detail.getBuyerQty().compareTo(materialLadderPriceDtl.getEndNum()) < 0) {
							priceDtl = materialLadderPriceDtl;
						}
					}
				}

			}

			if (null == priceDtl) {
				// porder.batchImport.priceMaterial = 明细信息工作簿中第{0}行不存在价格主数据
				eachMessage += getResource("porder.batchImport.priceMaterial", new String[] { index + "" }) + "\n";
			} else {
				MaterialUnitConversionDtl unitDtl = priceDtl.getMaterialMasterPriceDtl().getMaterialUnitConversionDtls().get(0);
				if (!detail.getUnitCode().equals(unitDtl.getOrderElementaryUnitCode())) {
					// porder.batchImport.unit.distinct =
					// 明细信息工作簿中第{0}行的单位编码和价格主数据的单位编码不一致
					eachMessage += getResource("porder.batchImport.unit.distinct", new String[] { index + "" }) + "\n";
				} else {
					bulidPurchaseOrderDetail(detail, priceDtl, dataDicts.get(0), unitMap);
				}

			}

			messages.append(eachMessage);
			eachMessage = "";
			index++;
		}
	}

	/**
	 * 设置订单明细
	 * 
	 * @param detail
	 * @param priceDtl
	 */
	protected void bulidPurchaseOrderDetail(PurchaseOrderDetail detail, MaterialLadderPriceDtl priceDtl, DataDict dataDict,
			Map<String, Unit> unitMap) {
		detail.setOverDeliveryLimit(priceDtl.getMaterialMasterPriceDtl().getExcessDeliveryLimit());
		detail.setShortDeliveryLimit(priceDtl.getMaterialMasterPriceDtl().getExcessDeliveryLimit());
		detail.setMaterialMasterPriceId(priceDtl.getMaterialMasterPriceDtl().getMaterialMasterPrice().getMaterialMasterPriceId());
		detail.setMaterialMasterPriceDtlId(priceDtl.getMaterialMasterPriceDtl().getMaterialMasterPriceDtlId());
		detail.setMaterialLadderPriceDtlId(priceDtl.getMaterialLadderPriceDtlId());
		detail.setTaxRateCode(priceDtl.getMaterialMasterPriceDtl().getTaxRateCode());
		detail.setSourceCode(2L);
		detail.setEmergencyFlag(0);
		detail.setCloseFlag(0);
		detail.setDeleteFlag(0);
		detail.setIsReturn(0);

		// 双单位转换
		PurchaseDualUnitConversion conversion = bulidPurchaseDualUnitConversion(detail, priceDtl);

		if (unitMap.containsKey(detail.getUnitCode())) {
			Unit unit = unitMap.get(detail.getUnitCode());
			detail.setUnitName(unit.getUnitName());
		}

		// 定价条件
		PurchaseOrderPricing orderPricing = bulidPurchaseOrderPricing(detail, priceDtl, dataDict, conversion);

		// 计算行金额和价格
		countItemAmount(detail, orderPricing, conversion);
	}

	/**
	 * 计算行金额和价格
	 * 
	 * @param detail
	 * @param orderPricing
	 * @param conversion
	 */
	protected void countItemAmount(PurchaseOrderDetail detail, PurchaseOrderPricing orderPricing, PurchaseDualUnitConversion conversion) {
		Integer isFree = 0;
		BigDecimal pricingAmount = orderPricing.getAmount();
		BigDecimal priceUnit = new BigDecimal(orderPricing.getPriceUnit());
		String lineItemTypeCode = detail.getLineItemTypeCode();
		// 计算价格
		BigDecimal buyerPrice = pricingAmount.divide(conversion.getPricingQty(), 9, RoundingMode.HALF_UP).multiply(priceUnit);
		detail.setBuyerPrice(buyerPrice);
		detail.setLineItemValAmt(pricingAmount);

		// 如果行金额为0，则免费标识置为是
		if (!"2".equals(lineItemTypeCode) && !"0".equals(lineItemTypeCode)) {
			isFree = 1;
		}
		detail.setIsFree(isFree);
	}

	/**
	 * 定价条件
	 * 
	 * @param detail
	 * @param priceDtl
	 * @param dataDict
	 * @param conversion
	 */
	protected PurchaseOrderPricing bulidPurchaseOrderPricing(PurchaseOrderDetail detail, MaterialLadderPriceDtl priceDtl, DataDict dataDict,
			PurchaseDualUnitConversion conversion) {
		List<PurchaseOrderPricing> orderPricings = new ArrayList<PurchaseOrderPricing>();
		PurchaseOrderPricing orderPricing = new PurchaseOrderPricing();
		orderPricing.setPurchaseOrderPricingTypeCode(dataDict.getItemCode());
		orderPricing.setPurchaseOrderPricingTypeName(dataDict.getItemName());
		orderPricing.setPriceUnit(priceDtl.getMaterialMasterPriceDtl().getPriceUnit().longValue());
		orderPricing.setPurchaseOrderPricingRowId(1);
		orderPricing.setPricingQty(priceDtl.getNonTaxPrice());
		countPricingAmount(detail, orderPricing, conversion);// 计算定价条件金额
		orderPricing.setPurchaseOrderDetail(detail);
		orderPricings.add(orderPricing);
		detail.setPurchaseOrderPricings(orderPricings);

		return orderPricing;
	}

	/**
	 * 双单位转换
	 * 
	 * @param detail
	 * @param priceDtl
	 * @return
	 */
	protected PurchaseDualUnitConversion bulidPurchaseDualUnitConversion(PurchaseOrderDetail detail, MaterialLadderPriceDtl priceDtl) {
		MaterialUnitConversionDtl unitDtl = priceDtl.getMaterialMasterPriceDtl().getMaterialUnitConversionDtls().get(0);
		List<PurchaseDualUnitConversion> conversions = new ArrayList<PurchaseDualUnitConversion>();
		PurchaseDualUnitConversion conversion = new PurchaseDualUnitConversion();
		conversion.setOrderDetailUnit(unitDtl.getOrderPricingUnitCode());
		conversion.setOrderDetailUnit2(unitDtl.getOrderElementaryUnitCode());
		conversion.setPricingUnit(unitDtl.getPricingUnitCode());
		conversion.setUnitCode(unitDtl.getElementaryUnitCode());

		conversion.setConvertMolecular2(unitDtl.getOrderPricingUnit());
		conversion.setConvertMolecular(unitDtl.getOrderElementaryUnit());
		conversion.setConvertDenominator2(unitDtl.getPricingUnit());
		conversion.setConvertDenominator(unitDtl.getElementaryUnit());
		conversion.setPurchaseOrderDetail(detail);
		countQuantity(detail.getBuyerQty(), conversion);// 根据订单单位和定价单位、SKU的转换关系，计算定价单位数量和SKU数量
		conversions.add(conversion);

		detail.setUnitCode(unitDtl.getOrderElementaryUnitCode());

		detail.setPurchaseDualUnitConversions(conversions);
		return conversion;
	}

	/**
	 * 计算定价条件金额
	 * 
	 * @param detail
	 * @param orderPricing
	 * @param conversion
	 */
	protected void countPricingAmount(PurchaseOrderDetail detail, PurchaseOrderPricing orderPricing,
			PurchaseDualUnitConversion conversion) {
		String recordType = detail.getLineItemTypeCode();

		// 获取当前行定价
		BigDecimal pricingPrice = orderPricing.getPricingQty();
		// 获取当前行价格单位
		BigDecimal priceUnit = new BigDecimal(orderPricing.getPriceUnit());
		// 获取定价单位数量
		BigDecimal pricingQty = conversion.getPricingQty();
		BigDecimal amount = BigDecimal.ZERO;

		if (recordType.equals("2")) {
			orderPricing.setPricingQty(amount);
		} else {
			amount = pricingQty.multiply(pricingPrice).divide(priceUnit, 9, RoundingMode.HALF_UP);
		}

		orderPricing.setAmount(amount);
	}

	/**
	 * 根据订单单位和定价单位、SKU的转换关系，计算定价单位数量和SKU数量
	 * 
	 * @param detail
	 * @param conversion
	 */
	protected void countQuantity(BigDecimal qty, PurchaseDualUnitConversion conversion) {

		// 订单单位（定价关系）
		BigDecimal convertMolecular2 = conversion.getConvertMolecular2();
		// 定价单位
		BigDecimal convertDenominator2 = conversion.getConvertDenominator2();
		// sku
		BigDecimal convertDenominator = conversion.getConvertDenominator();
		// 订单单位（基本单位关系）
		BigDecimal convertMolecular = conversion.getConvertMolecular();
		// 订单定价数量 = 订单数量*定价单位/订单单位（qty*convertDenominator2/convertMolecular2）
		BigDecimal pricingQty = convertDenominator2.divide(convertMolecular2, 9, RoundingMode.HALF_UP).multiply(qty);
		// SKU数量 = 订单数量 * 基本单位/订单单位（qty*convertDenominator/convertMolecular）
		BigDecimal skuQty = convertDenominator.divide(convertMolecular, 9, RoundingMode.HALF_UP).multiply(qty);
		conversion.setPricingQty(pricingQty);
		conversion.setSkuQty(skuQty);
	}

	/**
	 * 校验时间数据
	 * 
	 * @param buyerTime
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected String validateDate(Calendar buyerTime, int i, String key) {
		if (null != buyerTime && 10000 <= buyerTime.getTime().getYear()) {
			// porderDetail.batchImport.buyerTime.format =
			// 明细信息工作簿中第{0}行的交货日期格式不正确
			return getResource("porderDetail.batchImport." + key + ".format", i + "") + "\n";
		}

		return "";
	}

	/**
	 * 导入明细数据
	 * 
	 * @param wb excel工作簿对象
	 * @param dtlVos 用于装载excel数据
	 * @return 返回消息
	 */
	public Map<Boolean, Object> importExcel(List<MaterialMasterPriceOrderDtlView> dtlVos, Map<String, Object> userAuthMap,
			Map<String, Object> webPrams, Integer srmRowIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_groupCode", "pricingConditionType");
		params.put("EQ_status", 1);
		List<DataDict> dataDicts = dataDictlogic.findAll(new FeignParam<DataDict>(params));
		// 校验数据
		String messgae = validateExcelData(dtlVos, userAuthMap, webPrams);

		Map<Boolean, Object> returnMap = new HashMap<Boolean, Object>();
		List<PurchaseOrderDetail> result = new ArrayList<PurchaseOrderDetail>();
		if (StringUtils.isBlank(messgae)) {
			for (MaterialMasterPriceOrderDtlView vo : dtlVos) {
				srmRowIds = srmRowIds + 10;
				Calendar buyerTime = Calendar.getInstance();
				if (vo.getBuyerTime() == null) {
					buyerTime.add(Calendar.DAY_OF_MONTH, vo.getPlannedDays().intValue());
				} else {
					buyerTime = vo.getBuyerTime();
				}
				int isFree = "2".equals(vo.getRecordType()) ? 0 : 1;
				int jitFlag = 0;

				if (vo.getJitFlag() != null && "1".equals(vo.getJitFlag())) {
					jitFlag = 1;
				}

				BigDecimal buyerQty = vo.getBuyerQty() == null ? BigDecimal.ZERO : vo.getBuyerQty();

				// 拼装临时字段数据
				PurchaseOrderPricing purchaseOrderPricing = new PurchaseOrderPricing();
				PurchaseDualUnitConversion purchaseDualUnitConversion = new PurchaseDualUnitConversion();
				List<PurchaseOrderPricing> purchaseOrderPricings = new ArrayList<PurchaseOrderPricing>();
				List<PurchaseDualUnitConversion> purchaseDualUnitConversions = new ArrayList<PurchaseDualUnitConversion>();

				purchaseDualUnitConversion.setOrderDetailUnit(vo.getOrderPricingUnitCode());
				purchaseDualUnitConversion.setPricingUnit(vo.getPricingUnitCode());
				purchaseDualUnitConversion.setUnitCode(vo.getElementaryUnitCode());
				purchaseDualUnitConversion.setOrderDetailUnit2(vo.getOrderElementaryUnitCode());

				purchaseDualUnitConversion.setConvertMolecular2(vo.getOrderPricingUnit());
				purchaseDualUnitConversion.setConvertMolecular(vo.getOrderElementaryUnit());

				purchaseDualUnitConversion.setConvertDenominator2(vo.getPricingUnit());
				purchaseDualUnitConversion.setConvertDenominator(vo.getElementaryUnit());

				countQuantity(buyerQty, purchaseDualUnitConversion);

				purchaseDualUnitConversions.add(purchaseDualUnitConversion);

				// 订价单位数量=订价单位/订单单位*订单数量
				BigDecimal pricingQty = purchaseDualUnitConversion.getPricingQty();

				String recordType = vo.getRecordType();
				// 获取当前行定价
				BigDecimal pricingPrice = vo.getNonTaxPrice();
				// 获取当前行价格单位
				BigDecimal priceUnit = vo.getPriceUnit();
				BigDecimal amount = BigDecimal.ZERO;
				if ("2".equals(recordType)) {
					pricingPrice = BigDecimal.ZERO;
				}
				amount = pricingQty.multiply(pricingPrice).divide(priceUnit, 9, RoundingMode.HALF_UP);
				// 计算行金额
				BigDecimal pricingAmount = amount;
				BigDecimal lineItemValAmt = pricingAmount;
				// 计算价格
				BigDecimal buyerPrice = pricingAmount.divide(pricingQty, 9, RoundingMode.HALF_UP).multiply(priceUnit);
				// 如果行金额为0，则免费标识置为是
				if (!"2".equals(recordType) && pricingAmount.compareTo(BigDecimal.ZERO) == 0) {
					isFree = 1;
				}

				// 属于寄售数据
				if ("2".equals(recordType)) {
					vo.setNonTaxPrice(BigDecimal.ZERO);
				}
				if (dataDicts != null && dataDicts.size() > 0) {
					purchaseOrderPricing.setPurchaseOrderPricingTypeCode(dataDicts.get(0).getItemCode());
					purchaseOrderPricing.setPurchaseOrderPricingTypeName(dataDicts.get(0).getItemName());
				} else {
					purchaseOrderPricing.setPurchaseOrderPricingTypeCode("PB00");
					purchaseOrderPricing.setPurchaseOrderPricingTypeName("总价格");
				}

				purchaseOrderPricing.setPriceUnit(vo.getPriceUnit().longValue());
				purchaseOrderPricing.setPurchaseOrderPricingRowId(1);
				purchaseOrderPricing.setPricingQty(vo.getNonTaxPrice());
				purchaseOrderPricing.setAmount(amount);
				purchaseOrderPricings.add(purchaseOrderPricing);

				PurchaseOrderDetail orderDetail = new PurchaseOrderDetail();
				orderDetail.setUnitConversionInfo(DataUtils.toJson(purchaseDualUnitConversions));
				orderDetail.setPricingInfo(DataUtils.toJson(purchaseOrderPricings));
				orderDetail.setLineItemTypeCode(recordType);
				orderDetail.setSrmRowids(srmRowIds);
				orderDetail.setMaterialCode(vo.getMaterialCode());
				orderDetail.setMaterialName(vo.getMaterialName());
				orderDetail.setUnitCode(vo.getOrderElementaryUnitCode());
				// orderDetail.setUnitName(unitName);
				orderDetail.setPricingUnit(vo.getPricingUnitCode());
				orderDetail.setPlantCode(vo.getPlantCode());
				orderDetail.setPlantName(vo.getPlantName());
				orderDetail.setOverDeliveryLimit(vo.getExcessDeliveryLimit());
				orderDetail.setShortDeliveryLimit(vo.getDeliveryLimit());
				orderDetail.setMaterialMasterPriceId(vo.getMaterialMasterPriceId());
				orderDetail.setMaterialMasterPriceDtlId(vo.getMaterialMasterPriceDtlId());
				orderDetail.setMaterialLadderPriceDtlId(vo.getMaterialLadderPriceDtlId());
				orderDetail.setTaxRateCode(vo.getTaxRateCode());
				orderDetail.setBuyerTime(buyerTime);
				orderDetail.setVendorTime(buyerTime);
				orderDetail.setStockType("null".equals(vo.getItemCode()) ? "" : vo.getItemCode());
				orderDetail.setStoreLocal("null".equals(vo.getStockLocationCode()) ? "" : vo.getStockLocationCode());
				orderDetail.setSourceCode(2L);
				orderDetail.setScheduleFlag(jitFlag);
				orderDetail.setEmergencyFlag(0);
				orderDetail.setCloseFlag(0);
				orderDetail.setDeleteFlag(0);
				orderDetail.setIsReturn(0);
				orderDetail.setIsFree(isFree);
				orderDetail.setBuyerQty(buyerQty);
				orderDetail.setBuyerPrice(buyerPrice);
				orderDetail.setLineItemValAmt(lineItemValAmt);
				result.add(orderDetail);

			}
			returnMap.put(true, result);
		} else {
			returnMap.put(false, messgae);
		}

		return returnMap;
	}

	/**
	 * 获取国际化资源
	 * 
	 * @param key
	 * @return
	 */
	protected String getText(String key) {
		return I18nUtils.getText(key);
	}

	/**
	 * 替换国家化资源中的下标
	 * 
	 * @param key 资源主键
	 * @param params 要替换的元素
	 * @return
	 */
	protected String getResource(String key, String... params) {
		String value = getText(key);
		if (StringUtils.isNotBlank(value) && null != params && 0 < params.length) {
			int i = 0;
			for (String param : params) {
				String str = "{" + i++ + "}";
				value = value.replace(str, param);
			}
		}

		return value;
	}

	/**
	 * 校验数据
	 * 
	 * @param dtlVos
	 * @param searchParams
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected String validateExcelData(List<MaterialMasterPriceOrderDtlView> dtlVos, Map<String, Object> userAuthMap,
			Map<String, Object> webPrams) {
		String eachMessage = "";
		StringBuilder messages = new StringBuilder();
		int index = 2;

		List<String> materialCodes = new ArrayList<String>();
		List<String> plantCodes = new ArrayList<String>();

		Map<String, Object> materialMasterPriceParams = new HashMap<String, Object>();
		materialMasterPriceParams.putAll(userAuthMap);
		materialMasterPriceParams.put("EQ_purchasingOrgCode", webPrams.get("EQ_materialMasterPrice_purchasingOrgCode"));
		materialMasterPriceParams.put("EQ_vendorErpCode", webPrams.get("EQ_materialMasterPrice_vendorErpCode"));
		// Specification<MaterialMasterPrice> spec =
		// QueryUtils.newSpecification(materialMasterPriceParams);
		Map<String, Object> materialMasterPriceMap = new HashMap<String, Object>();
		String json = materialMasterPriceLogic.findAllJson(materialMasterPriceParams, new String[] {});
		List<MaterialMasterPrice> materialMasterPrices = JSONArray.parseArray(json, MaterialMasterPrice.class);
		if (materialMasterPrices != null && materialMasterPrices.size() > 0) {
			for (MaterialMasterPrice materialMasterPrice : materialMasterPrices) {
				String key = materialMasterPrice.getPurchasingOrgCode() + "_" + materialMasterPrice.getPlantCode() + "_"
						+ materialMasterPrice.getMaterialCode() + materialMasterPrice.getVendorErpCode();
				if (!materialMasterPriceMap.containsKey(key)) {
					materialMasterPriceMap.put(key, materialMasterPrice);
				}
			}
		}

		for (MaterialMasterPriceOrderDtlView vo : dtlVos) {

			if (StringUtils.isNotBlank(vo.getMaterialCode()) && !materialCodes.contains(vo.getMaterialCode())) {
				materialCodes.add(vo.getMaterialCode());
			}

			if (StringUtils.isNotBlank(vo.getPlantCode()) && !plantCodes.contains(vo.getPlantCode())) {
				plantCodes.add(vo.getPlantCode());
			}

			// 行项目类别
			if (StringUtils.isBlank(vo.getRecordType())) {
				eachMessage += getText("porder.lineItemType") + "," + "\n";
			}

			// 工厂
			if (StringUtils.isBlank(vo.getPlantCode())) {
				eachMessage += getText("plant.title") + "," + "\n";
			}
			// 数量
			if (null == vo.getBuyerQty()) {
				eachMessage += getText("label.number") + "," + "\n";
			}
			// 是否退货
			if (null == vo.getIsReturn()) {
				eachMessage += getText("label.number") + "," + "\n";
			}
			// 交货日期
			if (null == vo.getBuyerTime()) {
				eachMessage += getText("label.deliveryDate") + "," + "\n";
			}

			// 比较时间
			Date date = vo.getBuyerTime().getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date currentDdate = new Date();
			try {
				date = sdf.parse(sdf.format(date));
				currentDdate = sdf.parse(sdf.format(currentDdate));
				System.out.println("currentDdate=" + currentDdate);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

			long days = (date.getTime() - currentDdate.getTime()) / (1000 * 3600 * 24);
			if (currentDdate.getYear() > date.getYear() || date.getYear() > 9999) {
				eachMessage += getResource("label.deliveryDate.msgYear", currentDdate.getYear() + 1900 + "") + "," + "\n";
			} else if (0 > date.getMonth() || 11 < date.getMonth()) {
				eachMessage += getText("label.deliveryDate.msgMonth") + "," + "\n";
			} else if (days < 0) {
				eachMessage += getText("label.deliveryDate.currentDdate") + "," + "\n";
			}

			if (StringUtils.isNotEmpty(eachMessage)) {
				messages.append(getResource("label.theRow", new String[] { (index++) + "" }) + "：");
				messages.append(eachMessage);
				eachMessage = "";
			}
		}

		if (0 < messages.length()) {
			return messages.toString();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Material> materialMap = new HashMap<String, Material>();
		// 获取所有物料
		if (0 < materialCodes.size()) {
			params.put("IN_materialCode", StringUtils.join(materialCodes, ","));
			List<Material> materials = materialLogic.findAll(new FeignParam<Material>(params));
			for (Material material : materials) {
				materialMap.put(material.getMaterialCode(), material);
			}
		}

		// 获取所有单位编码
		Map<String, Unit> unitMap = new HashMap<String, Unit>();
		List<Unit> units = unitLogic.findAll(new FeignParam<Unit>());
		for (Unit unit : units) {
			unitMap.put(unit.getUnitCode(), unit);
		}

		Map<String, Plant> plantMap = new HashMap<String, Plant>();
		// 工厂编码
		if (0 < plantCodes.size()) {
			params.clear();
			params.put("IN_plantCode", StringUtils.join(plantCodes, ","));
			List<Plant> plants = plantLogic.findAll(new FeignParam<Plant>(params));
			for (Plant plant : plants) {
				plantMap.put(plant.getPlantCode(), plant);
			}
		}

		// 用户授权工厂编码
		Map<String, Plant> userPlantMap = new HashMap<String, Plant>();
		List<Plant> userPlants = new ArrayList<Plant>();
		if (userAuthMap.isEmpty()) {
			userPlants = plantLogic.findallwillfully();
		} else {
			userPlants = plantLogic.findAll(new FeignParam<Plant>(userAuthMap));
		}
		for (Plant plant : userPlants) {
			userPlantMap.put(plant.getPlantCode(), plant);
		}

		// 获取所有物料工厂视图
		Map<String, MaterialPlantView> materialPlantMap = new HashMap<String, MaterialPlantView>();
		if (0 < materialCodes.size()) {
			params.put("IN_materialCode", StringUtils.join(materialCodes, ","));
			List<MaterialPlantView> materialPlants = materialPlantViewEao.findAll(new FeignParam<MaterialPlantView>(params));
			for (MaterialPlantView materialPlant : materialPlants) {
				materialPlantMap.put(materialPlant.getMaterialCode() + "_" + materialPlant.getPlantCode(), materialPlant);
			}
		}

		// 获取采购组织工厂视图
		params.clear();
		params.put("EQ_purchasingOrgCode", webPrams.get("EQ_materialMasterPrice_purchasingOrgCode"));
		Map<String, PlantPurchaseOrg> plantPurchaseOrgMap = new HashMap<String, PlantPurchaseOrg>();
		List<PlantPurchaseOrg> plantPurchaseOrgs = plantPurchaseOrgLogic.findAll(new FeignParam<PlantPurchaseOrg>(params));
		for (PlantPurchaseOrg org : plantPurchaseOrgs) {
			plantPurchaseOrgMap.put(org.getPlantCode(), org);
		}
		index = 2;
		for (MaterialMasterPriceOrderDtlView vo : dtlVos) {
			// 不存在工厂
			if (!plantMap.containsKey(vo.getPlantCode())) {
				eachMessage += getText("porder.notFindData") + getText("plant.title") + "，" + "\n";
			}
			// 无物料号
			if (StringUtils.isBlank(vo.getMaterialCode())) {
				eachMessage += getText("porder.notFindData");
			}

			// 有物料号
			if (StringUtils.isNotBlank(vo.getMaterialCode())) {
				if (materialMap.containsKey(vo.getMaterialCode())) {
					Material material = materialMap.get(vo.getMaterialCode());
					vo.setMaterialName(material.getMaterialName());
					vo.setJitFlag(material.getJitFlag());
				} else {
					eachMessage += getText("porder.notFindData") + getText("materialInfo.code") + "，" + "\n";
				}

				// 无物料号判断单位
			} else if (StringUtils.isBlank(vo.getUnitCode())) {
				eachMessage += getText("label.unit") + getText("vendor.canNotBeEmpty") + "，" + "\n";
				// 无物料号判断单位
			} else if (!unitMap.containsKey(vo.getUnitCode())) {
				eachMessage += getText("porder.notFindData") + getText("label.unit") + "，" + "\n";
			}
			if (!plantPurchaseOrgMap.containsKey(vo.getPlantCode())) {
				// 采购组织工厂视图中不存在工厂
				eachMessage += getText("porder.nonSupportNullPlantPurchaseOrg") + "，";
			} else if (!userPlantMap.containsKey(vo.getPlantCode())
					|| !materialPlantMap.containsKey(vo.getMaterialCode() + "_" + vo.getPlantCode())) {
				// 物料工厂视图中不存在工厂
				eachMessage += getResource("porder.notFindPlantMaterial", new String[] { vo.getMaterialCode(), vo.getPlantCode() }) + "，"
						+ "\n";
			} else if (StringUtils.isNotBlank(vo.getMaterialCode())) {
				// 查询是否存在价格数据中
				eachMessage = validateMaterialLadderPriceDtl2(materialMasterPriceMap, webPrams, vo);
			}
			// start---------Modified by linshp
			if (StringUtils.isNotEmpty(eachMessage)) {
				messages.append(getResource("label.theRow", new String[] { (index) + "" }) + "：");
				messages.append(eachMessage);
				eachMessage = "";
			}
			index++; // ---------end
		}

		return messages.toString();
	}

	/**
	 * 校验价格主数据
	 * 
	 * @param userAuthMap 资源组
	 * @param webPrams 查询参数
	 * @param params
	 * @param vo
	 * @return
	 */
	protected String validateMaterialLadderPriceDtl2(Map<String, Object> materialMasterPriceMap, Map<String, Object> webPrams,
			MaterialMasterPriceOrderDtlView vo) {
		String eachMessage = "";
		MaterialLadderPriceDtl priceDtl = null;
		// 根据采购组织、物料、工厂、供应商、行类型、货币 获取阶梯价格
		String key = webPrams.get("EQ_materialMasterPrice_purchasingOrgCode") + "_" + vo.getPlantCode() + "_" + vo.getMaterialCode()
				+ webPrams.get("EQ_materialMasterPrice_vendorErpCode");
		if (!materialMasterPriceMap.containsKey(key)) {
			eachMessage += getResource("porder.notFindDataByMaterial", new String[] { vo.getMaterialCode() }) + "，";
			return eachMessage;
		}
		MaterialMasterPrice materialMasterPrice = (MaterialMasterPrice) materialMasterPriceMap.get(key);
		if (!materialMasterPrice.getRecordType().equals(vo.getRecordType())) {
			eachMessage += getResource("porder.notFindDataByMaterial", new String[] { vo.getMaterialCode() }) + "，";
			return eachMessage;
		}
		List<MaterialMasterPriceDtl> materialMasterPriceDtls = materialMasterPrice.getMaterialMasterPriceDtls();
		if (materialMasterPriceDtls != null && materialMasterPriceDtls.size() > 0) {
			for (MaterialMasterPriceDtl materialMasterPriceDtl : materialMasterPriceDtls) {
				Calendar calendar = vo.getBuyerTime();
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				vo.setBuyerTime(calendar);

				// 判断有效时间
				if (materialMasterPriceDtl.getEffectiveDate().before(vo.getBuyerTime())
						&& vo.getBuyerTime().before(materialMasterPriceDtl.getExpirationDate())) {
					List<MaterialLadderPriceDtl> materialLadderPriceDtls = materialMasterPriceDtl.getMaterialLadderPriceDtls();
					// 获取阶梯报价
					if (materialLadderPriceDtls != null && materialLadderPriceDtls.size() > 0) {
						for (MaterialLadderPriceDtl materialLadderPriceDtl : materialLadderPriceDtls) {
							if (vo.getBuyerQty().compareTo(materialLadderPriceDtl.getStartNum()) >= 0
									&& vo.getBuyerQty().compareTo(materialLadderPriceDtl.getEndNum()) < 0) {
								priceDtl = materialLadderPriceDtl;
							}
						}
					}
				}
			}
		}

		if (null == priceDtl) {
			eachMessage += getResource("porder.notFindDataByMaterial", new String[] { vo.getMaterialCode() }) + "，";
		} else {
			vo.setPriceUnit(priceDtl.getMaterialMasterPriceDtl().getPriceUnit());
			vo.setMaterialMasterPriceId(priceDtl.getMaterialMasterPriceDtl().getMaterialMasterPrice().getMaterialMasterPriceId());
			vo.setMaterialMasterPriceOrderDtlViewId(priceDtl.getMaterialMasterPriceDtl().getMaterialMasterPriceDtlId());
			vo.setExcessDeliveryLimit(priceDtl.getMaterialMasterPriceDtl().getExcessDeliveryLimit());
			vo.setDeliveryLimit(priceDtl.getMaterialMasterPriceDtl().getDeliveryLimit());
			vo.setPlannedDays(BigDecimal.ZERO);// 计划天数
			vo.setTaxRateCode(priceDtl.getMaterialMasterPriceDtl().getTaxRateCode());
			vo.setNonTaxPrice(priceDtl.getNonTaxPrice());

			MaterialUnitConversionDtl unitDtl = priceDtl.getMaterialMasterPriceDtl().getMaterialUnitConversionDtls().get(0);
			vo.setMaterialUnitConversionDtlId(unitDtl.getMaterialUnitConversionDtlId());
			vo.setElementaryUnit(unitDtl.getElementaryUnit());
			vo.setOrderElementaryUnit(unitDtl.getOrderElementaryUnit());
			vo.setOrderPricingUnit(unitDtl.getOrderPricingUnit());
			vo.setPricingUnit(unitDtl.getPricingUnit());
			vo.setElementaryUnitCode(unitDtl.getElementaryUnitCode());
			vo.setOrderElementaryUnitCode(unitDtl.getOrderElementaryUnitCode());
			vo.setOrderPricingUnitCode(unitDtl.getOrderPricingUnitCode());
			vo.setPricingUnitCode(unitDtl.getPricingUnitCode());
		}

		return eachMessage;
	}

	/**
	 * 设置下拉
	 * 
	 * @param wb excel对象
	 */
	public void setExcelCombox(HSSFWorkbook wb) {
		HSSFSheet mainSheet = wb.getSheetAt(0);
		HSSFSheet detailSheet = wb.getSheetAt(1);
		Map<String, Object> params = new HashMap<String, Object>();

		// 订单类型
		params.put("EQ_groupCode", "purchasingOrderType");
		List<DataDict> orderTypes = dataDictlogic.findAll(new FeignParam<DataDict>(params));
		if (orderTypes != null && orderTypes.size() > 0) {
			String[] arrCombox = new String[orderTypes.size()];
			for (int i = 0; i < orderTypes.size(); i++) {
				arrCombox[i] = "[" + orderTypes.get(i).getItemCode() + "]" + orderTypes.get(i).getItemName();
			}
			PoiUtils.setHSSFValidation(mainSheet, arrCombox, 1, 5000, 6, 6);
		}

		// 行项目类别
		params.clear();
		params.put("EQ_groupCode", "recordType");
		List<DataDict> reocrdTypes = dataDictlogic.findAll(new FeignParam<DataDict>(params));
		if (reocrdTypes != null && reocrdTypes.size() > 0) {
			String[] arrCombox = new String[reocrdTypes.size()];
			for (int i = 0; i < reocrdTypes.size(); i++) {
				arrCombox[i] = "[" + reocrdTypes.get(i).getItemCode() + "]" + reocrdTypes.get(i).getItemName();
			}
			PoiUtils.setHSSFValidation(detailSheet, arrCombox, 1, 5000, 1, 1);
		}

		// 获取所有单位编码
		/*
		 * params.clear(); params.put("EQ_status", "1"); List<Unit> units =
		 * unitLogic.findAll(params); if (units != null && units.size() > 0) {
		 * String[] arrCombox = new String[units.size()]; for (int i = 0; i <
		 * units.size(); i++) { arrCombox[i] = "[" + units.get(i).getUnitCode()
		 * + "]" + units.get(i).getUnitName(); }
		 * PoiUtils.setHSSFValidation(detailSheet, arrCombox, 1, 5000, 3, 3); }
		 */
		// 获取所有单位编码
		params.clear();
		params.put("EQ_status", "1");
		List<Unit> units = unitLogic.findAll(new FeignParam<Unit>(params));
		if (units != null && units.size() > 0) {
			String[] arrCombox = new String[units.size()];
			for (int i = 0; i < units.size(); i++) {
				arrCombox[i] = "[" + units.get(i).getUnitCode() + "]" + units.get(i).getUnitName();
			}
			// PoiUtils.setHSSFValidation(detailSheet, arrCombox, 1, 5000, 3,
			// 3);

			String hiddenSheet = "category1Hidden";
			int cellNum = 0;
			int endRow = 5000; // 结束行
			HSSFSheet category1Hidden = wb.createSheet(hiddenSheet); // 创建隐藏域
			for (int i = 0, length = arrCombox.length; i < length; i++) { // 循环赋值（为了防止下拉框的行数与隐藏域的行数相对应来获取>=选中行数的数组，将隐藏域加到结束行之后）
				category1Hidden.createRow(endRow + i).createCell(cellNum).setCellValue(arrCombox[i]);
			}
			Name category1Name = wb.createName();
			category1Name.setNameName(hiddenSheet);
			category1Name.setRefersToFormula(hiddenSheet + "!$A$1:$A$" + (arrCombox.length + endRow)); // A1:A代表隐藏域创建第?列createCell(?)时。以A1列开始A行数据获取下拉数组

			DVConstraint constraint = DVConstraint.createFormulaListConstraint(hiddenSheet);
			// 加载下拉列表内容
			// DVConstraint constraint =
			// DVConstraint.createExplicitListConstraint(arrCombox);
			// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
			CellRangeAddressList regions = new CellRangeAddressList(1, 5000, 3, 3);
			// 数据有效性对象
			HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
			detailSheet.addValidationData(data_validation_list);
			wb.setSheetHidden(wb.getSheetIndex(hiddenSheet), true);
		}

		// 税率编码
		List<TaxRate> taxRates = taxRateLogic.findAll(new FeignParam<TaxRate>());
		if (taxRates != null && taxRates.size() > 0) {
			String[] arrCombox = new String[taxRates.size()];
			for (int i = 0; i < taxRates.size(); i++) {
				arrCombox[i] = "[" + taxRates.get(i).getTaxRateCode() + "]" + taxRates.get(i).getTaxRateName();
			}
			PoiUtils.setHSSFValidation(detailSheet, arrCombox, 1, 5000, 7, 7);
		}
	}

	/**
	 * 记录单据操作日志
	 * 
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param billPk 主键
	 * @param message 日志信息
	 */

	public void addLog(Long userId, String userName, Long billPk, String message, String action) {

		try {
			if (StringUtils.isBlank(action)) {
				action = "数据操作日志";
			}
			// create log
			// Log log = Logs.getLog();
			// LogMessage.create(log)// 创建日志message
			// .type(LogType.OPERATION).level(Level.INFO)// 设置日志级别
			// .module(SrmConstants.BILLTYPE_CGD)// 设置日志模块
			// .key(billPk)// 日志信息的主要key
			// .action(action)// 操作的动作
			// .message(message)// 日志的记录内容
			// .result("success")// 日志的操作结果
			// .operatorName(userName)// 日志的操作人
			// .operatorId(userId)// 操作人id
			// .log();// 调用记录日志
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除订单
	 * 
	 * @param ids 要删除的ID
	 * @param 当前用户ID
	 * @param 当前用户名称
	 */
	public void removePo(List<Long> ids, Long userId, String userName, String message) {
		for (Long id : ids) {
			PurchaseOrder order = dao.getById(id);
			if (null != order && (PurchaseOrderState.NEW == order.getPurchaseOrderState()
					|| (PurchaseOrderState.RELEASE == order.getPurchaseOrderState()
							&& PurchaseOrderFlowState.NOPASS == order.getPurchaseOrderFlowState()))) {

				for (PurchaseOrderDetail pod : order.getPurchaseOrderDetails()) {
					// 如果来源于采购申请，则更新采购申请明细的已创建订单量、可转单量
					if (pod.getSourceCode().equals(3L)) {
						PurchasingRequisitionTrans prt = purchasingRequisitionTransLogic.findById(pod.getPurchasingRequisitionColId());
						PurchasingRequisitionCollection prc = prt.getPurchasingRequisitionCollection();

						BigDecimal oldTransferQuantity = prt.getTransferQuantity();
						// 回置可转单数量
						prt.setTransferQuantity(oldTransferQuantity.add(pod.getBuyerQty()));
						if (prt.getTransferQuantity().compareTo(prt.getAssignedQuantity()) == 0) {
							// 修改为未转单
							prt.setIsTransfered("0");
						}
						// 更新转单数据
						purchasingRequisitionTransLogic.save(prt);
						// 设置采购申请为未使用
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("EQ_isUsed", "1");
						params.put("EQ_purchasingRequisitionNo", prc.getPurchasingRequisitionNo());
						PurchasingRequisition pr = purchasingRequisitionLogic.findOne(params);
						// 转单已分配数量 = 可转单数量，设置采购申请使用状态为未使用
						if (pr != null && prt.getTransferQuantity().compareTo(prt.getAssignedQuantity()) == 0) {
							pr.setIsUsed("0");
							purchasingRequisitionLogic.save(pr);
						}

						// 修改采购申请明细的已创建订单量、可转单量
						BigDecimal oldTransQty = prc.getTransferedQuantity();// 原已转单数量
						prc.setTransferedQuantity(oldTransQty.subtract(pod.getBuyerQty()));// 更新已转单数量
						// 更新可转单数量
						if (prc.getCanTransferQuantity().add(pod.getBuyerQty()).compareTo(prc.getQuantityDemanded()) > 0) {
							prc.setCanTransferQuantity(prc.getQuantityDemanded());
						} else {
							prc.setCanTransferQuantity(prc.getCanTransferQuantity().add(pod.getBuyerQty()));
						}
						PurchasingRequisitionCollectionLogic.save(prc);
					}

				}

				// 准备删除的订单，内门户用
				PurchaseOrder oldOrder = new PurchaseOrder();
				BeanUtils.copyProperties(order, oldOrder);

				dao.delete(order);

				PortalParameters pp = new PortalParameters();
				pp.addPortalMethod(PortalMethodType.IW_DELETE);
				// 先完成跟踪
				pp.addPortalMethod(PortalMethodType.AT_DELETE);
				// 完成待办通知
				pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
				// 调用内门户
				pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(order.getPurchaseOrderId().toString())
						.setBillNo(order.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(order)); // FetchType.EAGER
				portalDealDataLogic.data4Portal(pp);
			}
		}
	}

	/**
	 * 判断采购订单供应商是否默认接受
	 * 
	 * @param entity 实体
	 * @param procesKey 流程key
	 * @throws Exception
	 */
	protected boolean isVendorDefaultAccept(PurchaseOrder entity) {
		try {
			// 采购订单供应商默认接受
			String code = PurchaseOrderConstant.GROOVY_ACCEPT;
			String value = this.getPurchaseOrderControl(entity, code);

			if (!value.equals(PurchaseOrderConstant.GROOVY_YES)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 采购订单定时器方法
	 */
	public List<JobResultData> purchaseOrderJobMethod() {
		List<JobResultData> resultList = new ArrayList<JobResultData>();
		Set<String> orderNos = new HashSet<String>();
		// 日程
		data4Schedule(orderNos);
		// 重要提醒
		data4ImportantWarn(orderNos);
		// 参数拼接
		for (String orderNo : orderNos) {
			JobResultData jsd = new JobResultData();
			jsd.setBillNo(orderNo);
			jsd.setSuccess(true);
			resultList.add(jsd);
		}

		return resultList;
	}

	protected void updateDetailSyncState(PurchaseOrder order) {
		List<PurchaseOrderDetail> details = order.getPurchaseOrderDetails();
		if (details != null && details.size() > 0) {
			for (PurchaseOrderDetail detail : details) {
				detail.setErpSynState(order.getErpSynState());
				purchaseOrderDetailEao.save(detail);
			}
		}
	}

	// -----------------------------------------------------订单内门户方法
	// end---------------------------------------------------
	/**
	 * 内门户信息传递参数
	 * 
	 * @param entity 实体
	 * @param sendUserId 发送者ID
	 * @param receive 接收人
	 * @param messageCode 消息编码
	 * @param message 用户输入的操作意见
	 */
	protected Map<String, Object> extraparams(PurchaseOrder entity, Long sendUserId, Object receive, String messageCode,
			String... message) {
		entity = dao.getById(entity.getPurchaseOrderId());
		// --------------------新增app消息提醒需要的参数map
		// start------------------20161109-------------
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String modifyTime = formatter.format(Calendar.getInstance().getTime());
		User sendUser = userLogic.findById(sendUserId);

		Map<String, Object> extraparams = new HashMap<String, Object>();
		extraparams.put("billNo", entity.getErpPurchaseOrderNo());
		extraparams.put("billId", entity.getPurchaseOrderId().toString());
		extraparams.put("vendorCode", entity.getVendorErpCode());
		extraparams.put("vendorName", entity.getVendorName());
		extraparams.put("userName", sendUser.getUserName());
		extraparams.put("userCode", sendUser.getUserCode());

		extraparams.put("first", "");
		extraparams.put("keyword1", entity.getErpPurchaseOrderNo());
		extraparams.put("keyword2", DateUtils.format(Calendar.getInstance().getTime(), "yyyy年MM月dd日 HH:ss"));
		extraparams.put("keyword3", "");
		extraparams.put("remark", "");

		// --------------------新增app消息提醒需要的参数map
		// end------------------20161109-------------
		if (messageCode.equals("CGD_HOLD") || messageCode.equals("CGD_FIRMHOLD") || messageCode.equals("CGD_REJECT")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderCheckState().desc()));
			extraparams.put("keyword3", modifyTime);
		} else if (messageCode.equals("CGD_FIRMREJECT") || messageCode.equals("CGD_ACCEPT")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderCheckState().desc()));
			extraparams.put("keyword3", modifyTime);
		} else if (messageCode.equals("CGD_PUBLISH") || messageCode.equals("CGD_CANCEL")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderState().desc()));
			extraparams.put("keyword3", modifyTime);
		} else if (messageCode.equals("CGD_CONFIRM")) {
			extraparams.put("keyword2", modifyTime);
		} else if (messageCode.equals("CGD_NOPASS") || messageCode.equals("CGD_PASS")) {
			extraparams.put("keyword2", getResource(entity.getPurchaseOrderFlowState().desc()));
			extraparams.put("remark", message[0]);
		}
		return extraparams;
	}

	/**
	 * 记录审核的操作日志:新
	 * 
	 * @param userId 操作人ID
	 * @param userName 操作人姓名
	 * @param billPk 审核的单据类型
	 * @param oldStatus 旧的状态
	 * @param newStatus 新的状态
	 */
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal) {
		if (StringUtils.isBlank(action)) {
			action = "数据操作日志";
		}
		if (StringUtils.isNotBlank(terminal) && !terminal.equals(SrmConstants.PLATFORM_WEB)) {
			message = StringUtils.isBlank(message) ? "" : ",原因：" + message;
			message = terminal + message;
		}

		// create log
		Log log = Logs.getLog();
		LogMessage.create(log).type(LogType.OPERATION).level(Level.INFO)//
				// 设置日志级别
				.module(SrmConstants.BILLTYPE_CGD)// 设置日志模块
				.key(billPk)// 日志信息的主要key
				.action(action)// 操作的动作
				.message(message)// 日志的记录内容
				.result("success")// 日志的操作结果
				.operatorName(userName)// 日志的操作人
				.operatorId(userId)// 操作人id
				.businessNo(businessNo) // 单据编号
				.terminal(terminal) // 终端标识
				.log();// 调用记录日志
	}

	/**
	 * 撤销审核发送通知邮件
	 * 
	 * @param userId
	 * @param userList
	 * @param billTypeName
	 * @param billNo
	 * @param billId
	 */
	public void revokeAuditSendMail(Long userId, List<User> userList, String billTypeName, String billNo, String billId) {
		User sender = userLogic.findById(userId);

		// 邮件提醒
		Map<String, Object> notifyParams = new HashMap<String, Object>();
		notifyParams.put("billTypeName", billTypeName);
		notifyParams.put("billNo", billNo);
		notifyParams.put("billId", billId);
		notifyParams.put("userName", sender.getUserName());
		notifySender.send(new NotifyParam("800", userId, DataUtils.fetchAsList(userList, "userId", Long.class), "BILL_REVOKEAUDIT",
				new String[] {}, notifyParams));

	}

	/**
	 * 日程提醒 调用
	 */
	protected void data4Schedule(Set<String> orderNos) {
		// 获取符合条件的：采购方日程提醒 、供应商日程提醒 数据
		// 当前用户数据权限下可查看的订单明细+订单的状态为执行+订单明细的关闭标识为否+订单明细的确认交货日期为当天+可送货量>0
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.MILLISECOND, 0);

		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_purchaseOrder_purchaseOrderState", PurchaseOrderState.OPEN);
		searchParams.put("NE_closeFlag", 1);
		searchParams.put("GT_canSendQty", 0);
		searchParams.put("EQ_vendorTime", currentDate);
		List<PurchaseOrderDetail> details = purchaseOrderDetailEao.findAll(searchParams);
		for (PurchaseOrderDetail detail : details) {
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.SCHEDULE_ADD).setWarnDate(detail.getVendorTime());
			// 供应日程

			pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(detail.getPurchaseOrder().getPurchaseOrderId().toString())
					.setBillNo(detail.getPurchaseOrder().getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(detail)); // ,
																														// FetchType.EAGER
			portalDealDataLogic.data4Portal(pp);
			orderNos.add(detail.getPurchaseOrder().getErpPurchaseOrderNo());
		}
	}

	/**
	 * 重要提示 调用
	 */
	protected void data4ImportantWarn(Set<String> orderNos) {
		// 获取符合条件的：采购方重要提示 、供应商重要提示 数据
		// 当前用户数据权限下可查看的采购订单+订单状态为“执行”+订单明细关闭状态为否+订单明细的可送货量大于0 + 当前时间大于交货日期
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MINUTE, 0);

		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_purchaseOrder_purchaseOrderState", PurchaseOrderState.OPEN);
		searchParams.put("NE_closeFlag", 1);
		searchParams.put("GT_canSendQty", 0);
		searchParams.put("LT_vendorTime", currentDate);

		int start = 0;
		int limit = 100;
		Page<PurchaseOrderDetail> page = new Page<PurchaseOrderDetail>(start, limit);
		page = purchaseOrderDetailEao.findAll(page, searchParams);

		List<PurchaseOrderDetail> details = page.getRecords();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (PurchaseOrderDetail detail : details) {
			PurchaseOrder entity = detail.getPurchaseOrder();
			PortalParameters pp = new PortalParameters();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("0", entity.getVendorName());
			params.put("1", entity.getPurchaseOrderNo());
			params.put("2", detail.getMaterialCode());
			params.put("3", sdf.format(detail.getVendorTime().getTime()));
			params.put("4", detail.getCanSendQty().toString());
			// 采购重要提示
			pp.addPortalMethod(PortalMethodType.IW_ADD, "CGD_BUYEROVERDUE", params, null);
			// 供应商重要提示
			List<User> users = findUserByVendor(detail.getPurchaseOrder().getVendorErpCode());
			pp.addPortalMethod(PortalMethodType.IW_ADD, "CGD_BUYEROVERDUE", params, users);
			pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(entity.getPurchaseOrderId().toString())
					.setBillNo(entity.getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(entity)); // FetchType.EAGER
			portalDealDataLogic.data4Portal(pp);

		}
		// }

	}

	/**
	 * 日程提醒 调用
	 */
	protected void data4Schedule(PurchaseOrder order) {
		// 获取符合条件的：采购方日程提醒 、供应商日程提醒 数据
		// 当前用户数据权限下可查看的订单明细+订单的状态为执行+订单明细的关闭标识为否+订单明细的确认交货日期为当天+可送货量>0
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.MILLISECOND, 0);

		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_purchaseOrder_purchaseOrderId", order.getPurchaseOrderId());
		searchParams.put("EQ_purchaseOrder_purchaseOrderState", PurchaseOrderState.OPEN);
		searchParams.put("NE_closeFlag", 1);
		searchParams.put("GT_canSendQty", 0);
		searchParams.put("EQ_vendorTime", currentDate);
		List<PurchaseOrderDetail> details = purchaseOrderDetailEao.findAll(searchParams);
		for (PurchaseOrderDetail detail : details) {
			// 供应商日程
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.SCHEDULE_ADD).setWarnDate(detail.getVendorTime());
			pp.setBillTypeCode(SrmConstants.BILLTYPE_CGD).setBillId(detail.getPurchaseOrder().getPurchaseOrderId().toString())
					.setBillNo(detail.getPurchaseOrder().getErpPurchaseOrderNo()).setInfo(DataUtils.toJson(detail)); // ,
																														// FetchType.EAGER
			portalDealDataLogic.data4Portal(pp);
		}
	}

	@Override
	public PurchaseOrder mergeLogic(PurchaseOrder model, String submitFlag, Long userId, String userName, String platForm) {
		PurchaseOrder oldOrder = dao.getById(model.getPurchaseOrderId());
		model.setErpSynState(oldOrder.getErpSynState());
		model.setCreateTime(oldOrder.getCreateTime());
		model.setCreateUserId(oldOrder.getCreateUserId());
		model.setCreateUserName(oldOrder.getCreateUserName());

		Map<Long, PurchaseOrderDetail> oldIds = new HashMap<Long, PurchaseOrderDetail>();
		List<Long> ids = new ArrayList<Long>();

		// 流程顺序
		String checkFirst = this.getPurchaseOrderControl(model, PurchaseOrderConstant.GROOVY_CHECKFIRST);
		model.setCheckFirst(Integer.parseInt(checkFirst));

		for (PurchaseOrderDetail pod : model.getPurchaseOrderDetails()) {
			ids.add(pod.getPurchaseOrderDetailId());
			pod.setPurchaseOrder(model);

			pod = setBaseInfo(pod, model);

			// 如果来源于采购申请转单，则更新采购申请明细的已创建订单量、可转单量
			if (pod.getSourceCode().equals(3L)) {
				resetPrInfo(pod);
			}
		}

		for (PurchaseOrderDetail oldPod : oldOrder.getPurchaseOrderDetails()) {
			if (!ids.contains(oldPod.getPurchaseOrderDetailId())) {
				oldIds.put(oldPod.getPurchaseOrderDetailId(), oldPod);
			}
		}
		// 被删除的行项目数量回置采购申请
		if (oldIds != null && oldIds.size() > 0) {
			for (PurchaseOrderDetail pod : oldIds.values()) {
				this.resetPurchaseApply(pod);
			}
		}

		model = dao.save(model);

		// 提交待审
		if ("audit".equals(submitFlag)) {
			dealPurchaseOrder(model.getModifyUserId(), model.getModifyUserName(), model.getPurchaseOrderId(),
					PurchaseOrderEvent.TORELEASE.name(), model.getClientCode(), platForm);

		}
		data4Schedule(model);
		return model;
	}

	/**
	 * 订单创建修改，重置采购申请转单、采购申请相关信息
	 * 
	 * @param pod 采购订单明细
	 */
	protected void resetPrInfo(PurchaseOrderDetail pod) {
		PurchasingRequisitionTrans prt = purchasingRequisitionTransLogic.findById(pod.getPurchasingRequisitionColId());
		PurchasingRequisitionCollection prc = prt.getPurchasingRequisitionCollection();

		BigDecimal oldTransferQuantity = prt.getTransferQuantity();
		BigDecimal diffValue = pod.getVendorQty();
		if (pod.getPurchaseOrderDetailId() != null) {
			PurchaseOrderDetail oldPod = purchaseOrderDetailEaoBean.getById(pod.getPurchaseOrderDetailId());
			diffValue = pod.getVendorQty().subtract(oldPod.getVendorQty());
		}

		// 回置可转单量
		if (oldTransferQuantity.subtract(diffValue).compareTo(BigDecimal.ZERO) < 1) {
			prt.setTransferQuantity(BigDecimal.ZERO);
			// 修改为已转单
			prt.setIsTransfered("1");
		} else {
			prt.setTransferQuantity(oldTransferQuantity.subtract(diffValue));
		}

		// 设置采购申请为已使用
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("IS_isUsed", "NULL");
		params.put("EQ_purchasingRequisitionNo", prc.getPurchasingRequisitionNo());
		PurchasingRequisition pr = purchasingRequisitionLogic.findOne(params);
		if (pr != null) {
			pr.setIsUsed("1");
			purchasingRequisitionLogic.save(pr);
		}

		purchasingRequisitionTransLogic.save(prt);
		// 修改采购申请明细的已创建订单量、可转单量
		BigDecimal oldTransQty = prc.getTransferedQuantity() == null ? BigDecimal.ZERO : prc.getTransferedQuantity();// 原已转单数量
		prc.setTransferedQuantity(oldTransQty.add(diffValue));// 更新已转单数量
		// 更新可转单数量
		prc.setCanTransferQuantity(prc.getQuantityDemanded().subtract(prc.getTransferedQuantity()));
		PurchasingRequisitionCollectionLogic.save(prc);

	}

	/**
	 * 设置订单明细基本信息
	 * 
	 * @param pod 订单明细
	 * @param model 订单主单
	 * @return 订单明细
	 */
	protected PurchaseOrderDetail setBaseInfo(PurchaseOrderDetail pod, PurchaseOrder model) {
		pod.setUnScheduledQty(pod.getVendorQty());
		pod.setDeleteFlag(0);
		pod.setVendorTime(pod.getBuyerTime());
		pod.setCloseFlag(0);
		pod.setOperate("关闭");
		pod.setVendorPrice(pod.getBuyerPrice());
		pod.setVendorQty(pod.getBuyerQty());
		if (1 == pod.getIsReturn()) {
			// 退货采购订单
			pod.setCanSendQty(BigDecimal.ZERO);
			pod.setIsAchieveLimit("Y");
		} else {
			pod.setCanSendQty(pod.getVendorQty());
			pod.setIsAchieveLimit("N");
		}
		pod.setVendorTime(pod.getBuyerTime());
		pod.setModifyTime(Calendar.getInstance());
		pod.setUnScheduledQty(pod.getVendorQty());

		if (null == pod.getRowIds()) {
			pod.setRowIds(pod.getSrmRowids());
		}
		// 计算行金额
		if (null == pod.getQtyAccord()) {
			pod.setQtyAccord(BigDecimal.ZERO);
		}
		if (null == pod.getQtyArrive()) {
			pod.setQtyArrive(BigDecimal.ZERO);
		}
		if (null == pod.getQtyCheck()) {
			pod.setQtyCheck(BigDecimal.ZERO);
		}
		if (null == pod.getQtyNaccord()) {
			pod.setQtyNaccord(BigDecimal.ZERO);
		}
		if (null == pod.getQtyOnline()) {
			pod.setQtyOnline(BigDecimal.ZERO);
		}
		if (null == pod.getQtyQuit()) {
			pod.setQtyQuit(BigDecimal.ZERO);
		}
		if (null == pod.getQtySend()) {
			pod.setQtySend(BigDecimal.ZERO);
		}
		if (null == pod.getQtyStore()) {
			pod.setQtyStore(BigDecimal.ZERO);
		}

		if (StringUtils.isNotBlank(pod.getPricingInfo())) {
			List<PurchaseOrderPricing> parseArray = JSONArray.parseArray(pod.getPricingInfo(), PurchaseOrderPricing.class);
			if (parseArray != null) {
				pod.setPurchaseOrderPricings(parseArray);
				for (PurchaseOrderPricing pop : pod.getPurchaseOrderPricings()) {// 定价条件
					pop.setCurType(model.getCurrencyCode());
					pop.setRowIds(pod.getRowIds().longValue());
					pop.setPurchaseOrderDetail(pod);
				}
			}
		}
		if (StringUtils.isNotBlank(pod.getUnitConversionInfo())) {
			List<PurchaseDualUnitConversion> parseArray = JSONArray.parseArray(pod.getUnitConversionInfo(),
					PurchaseDualUnitConversion.class);
			if (parseArray != null) {
				pod.setPurchaseDualUnitConversions(parseArray);
				for (PurchaseDualUnitConversion pdc : pod.getPurchaseDualUnitConversions()) {
					pdc.setPurchaseOrderDetail(pod);
				}
			}
		}

		return pod;
	}

	/**
	 * 回置采购申请转单数据
	 * 
	 * @param pod
	 */
	protected void resetPurchaseApply(PurchaseOrderDetail pod) {
		// 如果来源于采购申请转单，则更新采购申请明细的已创建订单量、可转单量
		if (pod.getSourceCode().equals(3L)) {
			PurchasingRequisitionTrans prt = purchasingRequisitionTransLogic.findById(pod.getPurchasingRequisitionColId());
			PurchasingRequisitionCollection prc = prt.getPurchasingRequisitionCollection();

			BigDecimal oldTransferQuantity = prt.getTransferQuantity();

			// 回置可转单量
			prt.setTransferQuantity(oldTransferQuantity.add(pod.getBuyerQty()));

			purchasingRequisitionTransLogic.save(prt);
			// 修改采购申请明细的已创建订单量、可转单量
			BigDecimal oldTransQty = prc.getTransferedQuantity() == null ? BigDecimal.ZERO : prc.getTransferedQuantity();// 原已转单数量
			if (oldTransQty.subtract(pod.getBuyerQty()).compareTo(BigDecimal.ZERO) < 0) {
				prc.setTransferedQuantity(BigDecimal.ZERO);// 更新已转单数量
			} else {
				prc.setTransferedQuantity(oldTransQty.subtract(pod.getBuyerQty()));// 更新已转单数量
			}
			// 更新可转单数量
			prc.setCanTransferQuantity(prc.getQuantityDemanded().subtract(prc.getTransferedQuantity()));
			PurchasingRequisitionCollectionLogic.save(prc);
			if (prc.getCanTransferQuantity().compareTo(prc.getQuantityDemanded()) == 0) {
				// 设置采购申请为未使用
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("EQ_purchasingRequisitionNo", prc.getPurchasingRequisitionNo());
				PurchasingRequisition pr = purchasingRequisitionLogic.findOne(params);
				if (pr != null) {
					pr.setIsUsed(null);
					purchasingRequisitionLogic.save(pr);
				}
			}
		}
	}

	@Override
	public String findMaterialMasterPricePage(Page<MaterialMasterPriceOrderDtlView> page, Map<String, Object> searchParams) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		Calendar z = Calendar.getInstance();
		searchParams.put("LE_effectiveDate", sdf.format(c.getTime()));
		z.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
		searchParams.put("GT_expirationDate", sdf.format(z.getTime()));
		searchParams.put("DISTINCT", true);
		page = materialMasterPriceOrderDtlViewLogic.findAll(page, searchParams);
		return DataUtils.toJson(page, SerializerFeature.DisableCircularReferenceDetect);
	}

	@Override
	public String findPlantByPurchasingOrgCompany(String clientCode, String userCode, Map<String, Object> map) {
		Map<String, Object> searchParams = new HashMap<String, Object>();

		Map<String, Object> plantSearchMap = new HashMap<String, Object>();
		if (map.containsKey("EQ_companyCode")) {
			plantSearchMap.put("EQ_companyCode", map.get("EQ_companyCode"));
		}
		plantSearchMap.remove("EQ_companyCode");
		List<String> plantCodes = new ArrayList<String>();
		List<Plant> plantlist = plantLogic.findAll(new FeignParam<Plant>(plantSearchMap));
		if (plantlist != null && plantlist.size() > 0) {
			for (Plant plant : plantlist) {
				plantCodes.add(plant.getPlantCode());
			}
			searchParams.put("IN_plantCode", StringUtils.join(plantCodes, ","));// 公司下工厂
		}
		if (map.containsKey("EQ_purchasingOrgCode")) {
			searchParams.put("EQ_purchasingOrgCode", map.get("EQ_purchasingOrgCode"));// 采购组织下工厂
		}
		searchParams.putAll(userAuthGroupBean.buildAuthFieldParams(new UserAuthGroupParam(clientCode, userCode, PlantPurchaseOrg.class)));
		return plantPurchaseOrgLogic.findAllJson(new FeignParam<PlantPurchaseOrg>(searchParams));
		// return plantPurchaseOrgLogic.findAllJson(searchParams, new String[] {
		// "plantPurchaseOrgId", "clientCode", "purchasingOrgCode",
		// "purchasingOrgName", "createUserId", "createUserName", "createTime",
		// "modifyUserId", "modifyUserName", "modifyTime" });
	}

	@Override
	public String findMaterialMasterPriceJson(Map<String, Object> searchParams) {
		return materialMasterPriceLogic.findAllJson(searchParams, new String[] {});
	}

	@Override
	public String getOrderJson(Map<String, Object> searchParams) {
		PurchaseOrder order = dao.findOne(searchParams);
		return DataUtils.toJson(order, FetchType.EAGER);
	}

	@Override
	public Map<String, Object> validateApply(PurchaseOrder model) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (PurchaseOrderDetail pod : model.getPurchaseOrderDetails()) {
			// 如果来源于采购申请，则更新采购申请明细的已创建订单量、可转单量
			if (pod.getSourceCode().equals(3L)) {
				PurchasingRequisitionTrans prt = purchasingRequisitionTransLogic.findById(pod.getPurchasingRequisitionColId());
				PurchasingRequisitionCollection prc = prt.getPurchasingRequisitionCollection();
				BigDecimal canTransferQuantity = prc.getCanTransferQuantity();
				BigDecimal diffValue = pod.getBuyerQty().subtract(canTransferQuantity);
				if (pod.getBuyerQty().compareTo(canTransferQuantity) > 0) {
					map.put(pod.getSrmRowids().toString(), pod.getMaterialCode() + "_" + diffValue);
				}
			}
		}
		return map;
	}

}