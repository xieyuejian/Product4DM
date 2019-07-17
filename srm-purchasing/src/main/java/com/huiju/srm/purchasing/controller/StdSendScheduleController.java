package com.huiju.srm.purchasing.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.data.jpa.utils.QueryUtils;
import com.huiju.module.fs.util.FileUploadUtils;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.commons.utils.PoiUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.CurrencyClient;
import com.huiju.srm.masterdata.api.DataDictClient;
import com.huiju.srm.masterdata.api.TaxRateClient;
import com.huiju.srm.masterdata.api.UnitClient;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderCheckState;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderFlowState;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.SendSchedule;
import com.huiju.srm.purchasing.entity.SendScheduleCommon;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.purchasing.entity.SendScheduleState;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.PurchaseOrderService;
import com.huiju.srm.purchasing.service.SendScheduleDetailService;
import com.huiju.srm.purchasing.service.SendScheduleService;
import com.huiju.srm.purchasing.util.SendScheduleConstant;

/**
 * 送货排程 Controller
 * 
 * @author zhuang.jq
 */
@Certificate(value = { "CP_sendschedule" }, requiredType = RequiredType.ONE)
public class StdSendScheduleController extends CloudController {

	@Autowired(required = false)
	protected SendScheduleService sendScheduleLogic;
	@Autowired(required = false)
	protected SendScheduleDetailService sendScheduleDetailLogic;
	@Autowired(required = false)
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired(required = false)
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupLogic;
	@Autowired(required = false)
	protected DataDictClient dataDictLogic;
	@Autowired(required = false)
	protected CurrencyClient currencyLogic;
	@Autowired(required = false)
	protected TaxRateClient taxRateLogic;
	@Autowired(required = false)
	protected UnitClient unitLogic;
	// @Autowired
	// protected GroovyScriptInvokerService groovyScriptInvokerLogic;
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	// @Autowired
	// protected BpmServiceService bpmServiceLogic;

	/**
	 * 根据采购组织获取排程标识，用于页面上的判断
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/getsendscheduleflag")
	public String getSendScheduleFlag(@RequestBody JsonParam<SendSchedule> param) throws Exception {
		SendSchedule model = param.getModel();
		/** 订单明细需全部排程 **/
		String full = sendScheduleLogic.getSendScheduleControl(model, SendScheduleConstant.GROOVY_FULLSCHEDULE);
		/** 排程允许选择多个订单 **/
		String multi = sendScheduleLogic.getSendScheduleControl(model, SendScheduleConstant.GROOVY_MULTIORDER);

		String result = full + "," + multi;
		return result;
	}

	/**
	 * 获取权限
	 */
	@PostMapping(value = "/getevents")
	public String getEvents(Long id) {

		List<String> s_authorities = getUserPermissions();
		// 事件对应的权限
		String[] events4Authoritie = { "sendschedule_toconfirm", "sendschedule_topass", "sendschedule_tonopass" };
		// 从授权中找出所拥有的事件权限
		StringBuffer authSb = new StringBuffer();
		for (String auth : events4Authoritie) {
			if (s_authorities.contains(auth)) {
				if (authSb.length() > 0)
					authSb.append(",");
				authSb.append("'" + auth + "'");
			}
		}

		String eventAuth = authSb.toString(); // 拥有的事件权限
		List<String> ev = sendScheduleLogic.getSendScheduleEvents(getUserId(), getRoleTypes(), id);

		StringBuffer sb = new StringBuffer("[");
		for (String event : ev) {
			if (event != null) {
				if (eventAuth.indexOf("'sendschedule_" + event.toLowerCase() + "'") > -1 || event.indexOf("#") > -1) {
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
	 * 处理流程状态
	 * 
	 * @throws Exception
	 */
	@PostMapping(value = "/dealstatus")
	public Result dealStatus(Long id, String message) throws Exception {
		String billState = getBillState();
		sendScheduleLogic.dealSendSchedule(id, billState, message, getUserId());
		return Result.success();
	}

	/**
	 * 设置列表查询参数
	 */
	protected void setSearchParams(Map<String, Object> searchParams) {
		String sendScheduleState = getAttribute("sendScheduleState");
		String initStates = getInitStates();
		String billFlag = getBillFlag();

		// 待处理，待审核初始化状态过滤
		if (StringUtils.isNotBlank(initStates)) {
			String value = initStates;
			String[] values = value.trim().split(",");
			List<SendScheduleState> statusArray = new ArrayList<SendScheduleState>();
			for (int i = 0; i < values.length; i++) {
				SendScheduleState status = SendScheduleState.valueOf(values[i]);
				statusArray.add(status);
			}
			searchParams.put("IN_sendScheduleState", statusArray);
		}

		// 状态过滤
		if (StringUtils.isNotBlank(sendScheduleState)) {
			String value = sendScheduleState;
			String[] values = value.replaceAll("\\s*", "").split(",");
			List<SendScheduleState> statusArray = new ArrayList<SendScheduleState>();
			for (int i = 0; i < values.length; i++) {
				SendScheduleState status = SendScheduleState.valueOf(values[i]);
				statusArray.add(status);
			}
			searchParams.put("IN_sendScheduleState", statusArray);
		}

		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			// 供应商只能查看到自己且状态不为新建的数据
			searchParams.put("EQ_vendorErpCode", getErpCode());
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			// 资源组查询
			searchParams.putAll(userAuthGroupLogic
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), SendSchedule.class)));
			if (null != billFlag && billFlag.equals("undeal")) {
				List<Long> idList = sendScheduleLogic.findIdByStatus(getUserId());
				if (idList.size() > 0) {// billPks为要审核的单据id
					searchParams.put("IN_sendScheduleId", idList);
				}
			}
		}
	}

	/**
	 * 加载列表数据
	 * 
	 * @return
	 */
	@PostMapping(value = "/list")
	public Page<SendSchedule> list() {
		Page<SendSchedule> page = buildPage(SendSchedule.class);
		Map<String, Object> searchParams = buildParams();
		setSearchParams(searchParams);
		searchParams.put("DISTINCT", true);
		return sendScheduleLogic.findAllWithoutAssociation(page, searchParams);
	}

	/**
	 * 新建排程
	 * 
	 * @return [description]
	 */
	@PostMapping(value = "/save")
	public Result save(@RequestBody JsonParam<SendSchedule> param) {
		SendSchedule model = param.getModel();
		String submitFlag = param.getSubmitFlag();
		model.setCreateTime(Calendar.getInstance());
		model.setCreateUserId(getUserId());
		model.setCreateUserName(getUserName());
		model.setClientCode(getClientCode());
		model.setErpSynState(0);
		model.setProcesKey(SrmConstants.BILLTYPE_PCD);

		String sendScheduleNo = billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_PCD);
		if (sendScheduleNo == null) {
			return Result.error("");
		}
		model.setSendScheduleNo(sendScheduleNo);

		for (SendScheduleCommon item : model.getSendScheduleCommons()) {
			this.saveHandle(item, model);
			item.setSendSchedule(model);
		}
		model = sendScheduleLogic.saveSendSchedule(model, submitFlag, getUserId(), getUserName());
		return Result.success();
	}

	/**
	 * 更新排程
	 * 
	 * @return [description]
	 */
	@PostMapping(value = "/update")
	public Result update(@RequestBody JsonParam<SendSchedule> param) {
		SendSchedule model = param.getModel();
		String submitFlag = param.getSubmitFlag();
		SendSchedule send = sendScheduleLogic.findById(model.getSendScheduleId());
		model.setModifyTime(Calendar.getInstance());
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setCreateUserId(send.getCreateUserId());
		model.setCreateUserName(send.getCreateUserName());
		model.setCreateTime(send.getCreateTime());
		for (SendScheduleCommon item : model.getSendScheduleCommons()) {
			this.saveHandle(item, model);
			item.setSendSchedule(model);
		}
		model = sendScheduleLogic.saveSendSchedule(model, submitFlag, getUserId(), getUserName());
		return Result.success();
	}

	/**
	 * 变更排程
	 * 
	 */
	@PostMapping(value = "/change")
	public Result change(@RequestBody JsonParam<SendSchedule> param) {
		SendSchedule model = param.getModel();
		SendSchedule SendSchedule = sendScheduleLogic.findById(model.getSendScheduleId());
		model.setModifyTime(Calendar.getInstance());
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setCreateUserId(SendSchedule.getCreateUserId());
		model.setCreateUserName(SendSchedule.getCreateUserName());
		model.setCreateTime(SendSchedule.getCreateTime());
		model.setProcesKey(SrmConstants.BILLTYPE_PCDCHANGE);

		for (SendScheduleCommon ssr : model.getSendScheduleCommons()) {
			this.saveHandle(ssr, model);
			ssr.setSendSchedule(model);
		}
		model.setSendScheduleState(SendScheduleState.NEW);
		model = sendScheduleLogic.changeSendSchedule(model, getUserId(), getUserName());
		sendScheduleLogic.addLog(getUserId(), getUserName(), model.getSendScheduleId(), "送货排程变更", SrmConstants.PERFORM_CHANGE,
				model.getSendScheduleNo(), SrmConstants.PLATFORM_WEB);
		return Result.success();
	}

	/**
	 * 供应商同意排程
	 * 
	 */
	@PostMapping(value = "/accept")
	public Result accept(Long id) {
		SendSchedule model = sendScheduleLogic.findById(id);
		if (model == null) {
			return Result.error(getText("message.notexisted"));
		}
		model.setSendScheduleState(SendScheduleState.OPEN);
		model = sendScheduleLogic.acceptSendSchedule(model, getUserId());
		// 记录状态
		sendScheduleLogic.addLog(getUserId(), getUserName(), model.getSendScheduleId(), "送货排程接受", SrmConstants.PERFORM_ACCEPT,
				model.getSendScheduleNo(), SrmConstants.PLATFORM_WEB);

		return Result.success();
	}

	/**
	 * 供应商拒绝排程
	 * 
	 */
	@PostMapping(value = "/refuse")
	public Result refuse(Long id, String message) {

		SendSchedule model = sendScheduleLogic.findById(id);
		if (model == null) {
			return Result.error(getText("message.notexisted"));
		}
		String refuseReason = getAttribute("refuseReason");
		model.setSendScheduleState(SendScheduleState.REFUSE);
		model.setRefuseReason(refuseReason);
		model = sendScheduleLogic.refuseSendSchedule(model, getUserId());

		refuseReason = StringUtils.isBlank(refuseReason) ? "送货排程拒绝" : "送货排程拒绝，原因:" + refuseReason;
		sendScheduleLogic.addLog(getUserId(), getUserName(), model.getSendScheduleId(), refuseReason, SrmConstants.PERFORM_REFUSE,
				model.getSendScheduleNo(), SrmConstants.PLATFORM_WEB);
		return Result.success();

	}

	/**
	 * 取消送货排程
	 * 
	 */
	@PostMapping(value = "/cancel")
	public Result cancel(Long id, String message) {
		SendSchedule model = sendScheduleLogic.findById(id);
		if (model == null) {
			return Result.error(getText("message.notexisted"));
		}
		HashMap<String, Object> seachParams = new HashMap<String, Object>();
		seachParams.put("EQ_sendScheduleNo", model.getSendScheduleNo());
		List<SendScheduleDetail> sendScheduleDetails = sendScheduleDetailLogic.findAll(seachParams);

		for (SendScheduleDetail itemd : sendScheduleDetails) {
			if (itemd.getSendFlag() != 0) {
				return Result.error(getText("sendSchedule.message.warnMsg12"));
			}
		}

		model = sendScheduleLogic.cancelSendSchedule(model);

		message = StringUtils.isBlank(message) ? "送货排程取消" : "送货排程取消，原因:" + message;
		sendScheduleLogic.addLog(getUserId(), getUserName(), model.getSendScheduleId(), message, SrmConstants.PERFORM_TOCANCEL,
				model.getSendScheduleNo(), SrmConstants.PLATFORM_WEB);
		return Result.success();
	}

	/**
	 * 删除排程
	 */
	@PostMapping(value = "/delete")
	public Result delete(@RequestParam List<Long> ids, @RequestParam String message) {
		sendScheduleLogic.deleteSendSchedules(ids, getUserId().toString(), getUserName());
		if (ids != null && ids.size() > 0) {
			for (Long id : ids) {
				message = StringUtils.isBlank(message) ? "送货排程删除" : "送货排程删除，原因:" + message;
				sendScheduleLogic.addLog(getUserId(), getUserName(), id, message, SrmConstants.PERFORM_DELETE, "",
						SrmConstants.PLATFORM_WEB);
			}
		}
		return Result.success();
	}

	/**
	 * 加载需要编辑的数据
	 * 
	 * @return [description]
	 */
	@PostMapping(value = "/get")
	public Result get(Long id) {
		SendSchedule model = sendScheduleLogic.findById(id);
		if (model == null) {
			return Result.error(getText("message.notexisted"));
		}
		return Result.success(DataUtils.toJson(model, "sendScheduleCommons"));
	}

	/**
	 * 获得明细列表
	 * 
	 * @return [description]
	 */
	@PostMapping(value = "/findsendschedulecommonall")
	public String findSendScheduleCommonAll() {
		Map<String, Object> searchParam = buildParams();
		List<SendScheduleCommon> sendSchedulecommons = sendScheduleLogic.findSendScheduleCommon(searchParam);
		if (sendSchedulecommons != null && sendSchedulecommons.size() > 0) {
			for (SendScheduleCommon item : sendSchedulecommons) {
				PurchaseOrderDetail pod = purchaseOrderDetailLogic.findById(item.getPurchaseOrderDetailId());
				if (pod != null) {
					item.setBuyerName(pod.getPurchaseOrder().getCreateUserName());// 采购员名称
					item.setBuyerId(pod.getPurchaseOrder().getCreateUserId()); // 采购员id
					if (pod.getScheduledQty() == null) {
						item.setScheduleQty(BigDecimal.ZERO); // 已排程
					} else {
						item.setScheduleQty(pod.getScheduledQty());
					}
					if (pod.getVendorQty() != null) {
						if (pod.getScheduledQty() == null) {
							item.setCanSendQty(pod.getVendorQty()); // 可送货量
						} else {
							item.setCanSendQty(pod.getVendorQty().subtract(pod.getScheduledQty())); // 可送货量
						}
					}
					item.setPurchaseOrderNo(pod.getPurchaseOrder().getPurchaseOrderNo());// 订单号
					item.setErpPurchaseOrderNo(pod.getPurchaseOrder().getErpPurchaseOrderNo());// sap订单号
					item.setPurchaseOrderId(pod.getPurchaseOrder().getPurchaseOrderId());// 订单Id
					if (pod.getRowIds() != null) {
						item.setRowIds(pod.getRowIds().intValue());// 订单明细行号
					}
					item.setVendorCode(pod.getPurchaseOrder().getVendorCode());// 供应商编码
					item.setVendorErpCode(pod.getPurchaseOrder().getVendorErpCode());// 供应商erp编码
					item.setVendorName(pod.getPurchaseOrder().getVendorName());// 供应商名称
					item.setMaterialCode(pod.getMaterialCode());// 物料编码
					item.setMaterialName(pod.getMaterialName());// 物料名称
					item.setUnitCode(pod.getUnitCode());// 单位编码
					item.setUnitName(pod.getUnitName()); // 单位名称
					item.setSendQty(pod.getVendorQty());// 订单数量
					item.setFactoryCode(pod.getPlantCode()); // 工厂编码
					item.setLineItemTypeCode(pod.getLineItemTypeCode());// 类型
					item.setStockLocal(pod.getStoreLocal()); // 库存地点
					item.setVendorTime(pod.getVendorTime());// 确认交货时间
				}
			}
		}
		return DataUtils.toJson(sendSchedulecommons, "sendSchedule.sendScheduleCommons");
	}

	/**
	 * 找到对应排程单的子排程明细
	 * 
	 * @return [description]
	 */
	@PostMapping(value = "/findsendscheduledetailall")
	public String findSendScheduleDetailAll() {
		Map<String, Object> searchParam = buildParams();
		List<SendScheduleDetail> sendScheduleDetails = sendScheduleDetailLogic.findAll(searchParam);
		return DataUtils.toJson(sendScheduleDetails,
				new String[] { "sendScheduleCommon.sendScheduleDetails", "sendScheduleCommon.sendSchedule" });
	}

	/**
	 * 同步到SAP
	 */
	@PostMapping(value = "/synerp")
	public Result synErp(Long id) {
		SendSchedule model = sendScheduleLogic.findById(id);
		// 同步到erp
		sendScheduleLogic.doSync(model);
		// 增加操作日志
		sendScheduleLogic.addLog(getUserId(), getUserName(), model.getSendScheduleId(), "送货排程同步", SrmConstants.PERFORM_SYNC,
				model.getSendScheduleNo(), SrmConstants.PLATFORM_WEB);
		return Result.success();
	}

	/**
	 * 处理保存排程子明细值的设置
	 * 
	 * @param item排程明细
	 * @param schedule排程实体对象
	 */
	@PostMapping(value = "/savehandle")
	protected Result saveHandle(SendScheduleCommon item, SendSchedule schedule) {
		if (item.getSendScheduleDetails() != null) {
			for (SendScheduleDetail detail : item.getSendScheduleDetails()) {
				if (detail.getSendScheduleDetailId() == null) {
					detail.setPurchaseOrderDetailId(item.getPurchaseOrderDetailId());// 设置采购订单明细ID
					detail.setSendScheduleNo(schedule.getSendScheduleNo());// 排程单号
					detail.setPurchasingOrgCode(schedule.getPurchasingOrgCode());// 采购组织编码
					detail.setPurchasingOrgName(schedule.getPurchasingOrgName());
					detail.setErpPurchaseOrderNo(item.getErpPurchaseOrderNo());// sap采购订单号
					detail.setPlantCode(item.getFactoryCode());// 工厂编码
					detail.setFactoryName(item.getFactoryName());// 工厂名称
					detail.setMaterialCode(item.getMaterialCode());// 物料编码
					detail.setMaterialId(item.getMaterialId());// 物料Id
					detail.setMaterialName(item.getMaterialName());// 物料名称
					detail.setSendQty(item.getSendQty() == null ? BigDecimal.ZERO : item.getSendQty());
					detail.setPurchaseOrderNo(item.getPurchaseOrderNo());
					detail.setRowIds(item.getRowIds()); // 行号
					detail.setSendFlag(0);
					detail.setUnitCode(item.getUnitCode());// 单位编码
					detail.setUnitName(item.getUnitName());// 单位名称
					detail.setVendorCode(item.getVendorCode());// 供应商编码
					detail.setVendorName(item.getVendorName());// 供应商名称
					detail.setVendorCode(item.getVendorErpCode());// 供应商erp编码
					detail.setStockLocal(item.getStockLocal());// 库存地点
					detail.setLineItemTypeCode(item.getLineItemTypeCode());// 行项目类型
					detail.setDeliveryQty(item.getDeliveryQty() == null ? BigDecimal.ZERO : item.getDeliveryQty());
					detail.setOnWayQty(item.getOnWayQty() == null ? BigDecimal.ZERO : item.getOnWayQty());// 在途量
					detail.setReceiptQty(item.getReceiptQty() == null ? BigDecimal.ZERO : item.getReceiptQty());
					detail.setReturnGoodsQty(item.getReturnGoodsQty() == null ? BigDecimal.ZERO : item.getReturnGoodsQty());// 退货量
					detail.setCanSendQty(detail.getScheduleQty() == null ? BigDecimal.ZERO : detail.getScheduleQty()); // 可送货量

					detail.setTaxPrice(item.getTaxPrice());// 未税价
					detail.setSendScheduleCommon(item);
				} else {
					// 可送货量 等于 需求量 - 在途量 - 收获量 + 退货量
					// 获取需求量
					BigDecimal scheduleQty = detail.getScheduleQty() == null ? BigDecimal.ZERO : detail.getScheduleQty();
					BigDecimal canSendQty = scheduleQty.subtract(detail.getOnWayQty() == null ? BigDecimal.ZERO : detail.getOnWayQty())
							.subtract(detail.getReceiptQty() == null ? BigDecimal.ZERO : detail.getReceiptQty())
							.add(detail.getReturnGoodsQty() == null ? BigDecimal.ZERO : detail.getReturnGoodsQty());
					detail.setCanSendQty(canSendQty); // 可送货量

					if (scheduleQty.compareTo(detail.getCanSendQty()) == 0) {
						detail.setSendFlag(0);
					} else if (scheduleQty.compareTo(detail.getCanSendQty()) > 0 && detail.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {// 如果可送货量大于0并且小于需求量则为部分送货
						detail.setSendFlag(1);
					} else if (scheduleQty.compareTo(BigDecimal.ZERO) == 0) {// 如果可送货量等于0为完全送货
						detail.setSendFlag(2);
					}

					detail.setSendScheduleCommon(item);
				}
			}
		} else {
			if (item.getSendScheduleCommonId() != null) {
				Map<String, Object> searchParams = new HashMap<String, Object>();
				searchParams.put("EQ_stdSendScheduleCommon_stdSendScheduleCommonId", item.getSendScheduleCommonId());
				List<SendScheduleDetail> ssds = sendScheduleDetailLogic.findAll(searchParams);
				if (ssds.size() > 0) {
					item.setSendScheduleDetails(ssds);
				}
			}
		}
		return Result.success();
	}

	/**
	 * 
	 * 获取可选择的采购订单明细
	 * 
	 * @return
	 */
	@PostMapping(value = "/getpurchaseorderdetail")
	public String getPurchaseOrderDetail() {
		// 主单上所选供应商的采购订单； 
		// 主单上所选采购组的采购订单； 
		// 订单明细的排程标识为是； 
		// 订单明细的关闭标识为否； 
		// 订单明细的删除标识为否； 
		// 订单明细的可排程量不为0；
		Page<PurchaseOrderDetail> page = buildPage(PurchaseOrderDetail.class);
		Map<String, Object> searchParams = buildParams();// buildParams();
		// 已同步
		searchParams.put("EQ_purchaseOrder_erpSynState", 1);
		// 未删除
		searchParams.put("EQ_deleteFlag", 0);
		// 未关闭
		searchParams.put("EQ_closeFlag", 0);
		// 执行
		searchParams.put("EQ_purchaseOrder_purchaseOrderState", PurchaseOrderState.OPEN);
		// 审核通过
		searchParams.put("EQ_purchaseOrder_purchaseOrderFlowState", PurchaseOrderFlowState.PASS);
		// 接受或同意变更
		searchParams.put("IN_purchaseOrder_purchaseOrderCheckState",
				Arrays.asList(PurchaseOrderCheckState.ACCEPT, PurchaseOrderCheckState.FIRMHOLD));

		// 过滤掉订单不是退货的
		searchParams.put("EQ_isReturn", 0);
		// 是排程
		searchParams.put("EQ_scheduleFlag", 1);
		// 可排程量大于0
		searchParams.put("GT_unScheduledQty", 0);
		searchParams.put("EQ_isAchieveLimit", "N");
		// 先按确认交货时间升序，再按物料编码升序
		Sort sort = QueryUtils.buildSort("vendorTime,asc", "materialCode,asc");
		page.setSort(sort);
		page = purchaseOrderDetailLogic.findAll(page, searchParams);
		return DataUtils.toJson(page, "purchaseOrder.purchaseOrderDetails");
	}

	/**
	 * 下载模板
	 */
	@PostMapping(value = "/download")
	public Result downLoad() {
		OutputStream out = null;
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		try {
			Map<Integer, Object> rowMap = new HashMap<Integer, Object>();
			// 列头
			String[] nameTitles = new String[] { "采购订单号*", "行号*", "需求日期*", "需求量*" };
			rowMap.put(0, nameTitles);

			// 设置无边框单元格样式
			HSSFCellStyle cellStyle = wb.createCellStyle();
			PoiUtils.setCellStyle(cellStyle, HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.VERTICAL_CENTER, true, HSSFCellStyle.BORDER_NONE,
					HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE, HSSFCellStyle.BORDER_NONE);
			// 设置字体
			HSSFFont font = wb.createFont();
			PoiUtils.setFont(font, HSSFFont.BOLDWEIGHT_NORMAL, "宋体", (short) 200, HSSFColor.BLUE.index);
			cellStyle.setFont(font);
			Map<Integer, HSSFCellStyle> hcsMap = new HashMap<Integer, HSSFCellStyle>();
			hcsMap.put(0, cellStyle);

			List<Object> valueList = new ArrayList<Object>();
			valueList.add("");
			valueList.add("");
			valueList.add("");
			valueList.add("");
			rowMap.put(1, valueList);

			// 设置单元格值
			PoiUtils.setCellValue(wb, sheet, rowMap, hcsMap);
			for (int i = 0; i < 4; i++) {
				sheet.setColumnWidth(i, 5000);
			}

			response.reset();
			String fileName = "送货排程.xls";// f.getName();
			fileName = fileName.replaceAll(" ", "");
			fileName = new String(fileName.getBytes("utf-8"), "iso8859-1");
			response.setContentType("application/x-msdownload");
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			// response.setHeader("Content-Length", wb.getBytes().length + "");

			out = response.getOutputStream();
			wb.write(out);
			wb.close();
			out.close();
		} catch (IOException e) {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (wb != null) {
				try {
					wb.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return Result.error("");
			// e.printStackTrace();
		}
		return Result.success();
	}

	/**
	 * 导入数据
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	@PostMapping(value = "/importexcel")
	public Result importExcel(HttpServletRequest request) {
		FileInputStream inputStream = null;
		try {
			Map<String, File> files = FileUploadUtils.getUploadedFiles(request);
			String key = files.keySet().iterator().next();
			File excelFile = files.get(key);

			inputStream = new FileInputStream(excelFile);

			HSSFSheet sheet = new HSSFWorkbook(inputStream).getSheetAt(0);
			if (0 == sheet.getLastRowNum()) {
				return Result.error(getText("message.theDateIsNull"));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 查询条件
			Map<String, Object> searchParams = new HashMap<String, Object>();

			List<HSSFCell> hfList = new ArrayList<HSSFCell>();
			List<Long> idList = new ArrayList<Long>();
			Map<Integer, List<String>> mapList = new HashMap<Integer, List<String>>();
			// 验证结果
			Map<Integer, String> mapSb = new HashMap<Integer, String>();
			// 需要验证必填项
			Map<Integer, String> validMap = new HashMap<Integer, String>();
			if (sheet.getRow(0) != null) {
				HSSFRow row = sheet.getRow(0);
				for (int i = 0; i < 4; i++) {
					HSSFCell hf = row.getCell(i);
					if (hf.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						String Str = hf.getStringCellValue().trim();
						if (Str != null && Str.contains("*")) {
							validMap.put(i, Str.substring(0, Str.indexOf("*")));
						}
					}
				}
			}

			for (int numRows = 1; numRows <= sheet.getLastRowNum(); numRows++) {
				List<String> sList = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				hfList.clear();
				if (null != sheet.getRow(numRows)) {
					HSSFRow row = sheet.getRow(numRows);
					for (int i = 0; i < 4; i++) {
						hfList.add(row.getCell(i));
					}

					// 导入验证
					importValid(searchParams, hfList, numRows, sdf, sb, sList, validMap, idList);

					mapList.put(numRows, sList);
					if (sb.length() > 1) {
						mapSb.put(numRows, sb.toString());
					}

				}
			}

			StringBuffer sbFinal = new StringBuffer();
			if (mapSb != null && mapSb.size() > 0) {
				for (Integer s : mapSb.keySet()) {
					sbFinal.append(mapSb.get(s));
				}
				return Result.error(sbFinal.toString()); // renderHtml("{success:false,info:'"
															// + + "'}");
			} else {
				if (mapList != null && mapList.size() > 0) {
					List<SendScheduleDetail> modelList = new ArrayList<SendScheduleDetail>();
					importData(searchParams, modelList, mapList, sdf, idList);
					return Result.success(modelList); // renderHtml("{success:true,info:'"
														// +
														// DataUtils.toJson(modelList)
														// + "'}");
				}
			}
			// renderHtml("{success:true}");

			if (null != inputStream) {
				try {
					excelFile = null;
					inputStream.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
		return Result.success();
	}

	/**
	 * 导入校验方法
	 * 
	 * @param searchParams 创建的查询载体
	 * @param hfList 读取的单元格
	 * @param numRows 第几行
	 * @param sdf 日期转换
	 * @param sb 校验结果
	 * @param sList 一行的数据集
	 * @param validMap 必填字段
	 * @throws Exception
	 */
	protected void importValid(Map<String, Object> searchParams, List<HSSFCell> hfList, int numRows, SimpleDateFormat sdf, StringBuilder sb,
			List<String> sList, Map<Integer, String> validMap, List<Long> idList) throws Exception {
		String purchasingOrgCode = getAttribute("purchasingOrgCode");
		String vendorErpCode = getAttribute("vendorErpCode");
		String existParam = getAttribute("existParam");

		int index = 0;
		for (HSSFCell hc : hfList) {
			String Str = null;
			if (hc != null) {
				if (hc.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					Str = hc.getStringCellValue().trim();
					if (Str != null && Str.indexOf(".") != -1) {
						Str = Str.substring(0, Str.indexOf("."));
					}
				} else if (hc.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					if (HSSFDateUtil.isCellDateFormatted(hc)) {// 判断是否是日期类型
						Date d = hc.getDateCellValue();
						if (d != null) {
							Str = String.valueOf(sdf.format(d));
						}
					} else {
						NumberFormat nf = NumberFormat.getInstance();
						nf.setGroupingUsed(false);// true时的格式：1,234,567,890
						Double acno = hc.getNumericCellValue();
						if (acno != null) {
							Str = String.valueOf(acno);
						}
					}
				}
			}
			if (validMap.get(index) != null) {
				if (hc == null || Str == null || StringUtils.isBlank(Str)) {
					sb.append("第" + numRows + "行：" + validMap.get(index) + "不能为空!\\n");
				}
			}
			sList.add(Str);
			index++;
		}
		// 验证
		searchParams.clear();
		searchParams.put("EQ_erpPurchaseOrderNo", sList.get(0));
		searchParams.put("EQ_purchasingOrgCode", purchasingOrgCode);
		searchParams.put("EQ_vendorErpCode", vendorErpCode);
		PurchaseOrder order = purchaseOrderLogic.findOne(searchParams);
		// idList.add(1621l);
		if (order != null) {
			List<PurchaseOrderDetail> list = sendScheduleLogic.findOrderDtlAll(order.getPurchaseOrderId());
			SendSchedule model = new SendSchedule();
			model.setPurchasingOrgCode(purchasingOrgCode);
			String isMulti = sendScheduleLogic.getSendScheduleControl(model, SendScheduleConstant.GROOVY_MULTIORDER);
			boolean isExist = false;
			for (PurchaseOrderDetail detail : list) {
				String errorMsg = "导入的采购订单明细不符合条件！";
				if (detail.getRowIds() == Integer.parseInt(sList.get(1))) {
					isExist = true;
					idList.add(detail.getPurchaseOrderDetailId());
					if (StringUtils.isBlank(order.getPurchasingOrgCode()) || !purchasingOrgCode.equals(order.getPurchasingOrgCode())) {
						sb.append("第" + numRows + "行：采购订单不属于" + purchasingOrgCode + "采购组织和" + vendorErpCode + "供应商!\\n");
					}
					if (order.getPurchaseOrderState() != PurchaseOrderState.OPEN) {
						sb.append(errorMsg);
					} else if (order.getPurchaseOrderCheckState() != PurchaseOrderCheckState.ACCEPT
							&& order.getPurchaseOrderCheckState() != PurchaseOrderCheckState.FIRMHOLD) {
						sb.append(errorMsg);
					} else if (order.getPurchaseOrderFlowState() != PurchaseOrderFlowState.PASS) {
						sb.append(errorMsg);
					} else if (detail.getScheduleFlag() == 0) {
						sb.append(errorMsg);
					} else if (detail.getCloseFlag() == 1) {
						sb.append(errorMsg);
					} else if (detail.getDeleteFlag() == 1) {
						sb.append(errorMsg);
					} else if (detail.getUnScheduledQty().compareTo(new BigDecimal(sList.get(3))) < 0) {
						sb.append(errorMsg);
					}
					// 判断采购组织是否允许有多个订单
					if (StringUtils.isNotBlank(existParam) && isMulti.equals("2") || numRows > 1 && isMulti.equals("2")) {
						sb.append("采购组织【" + purchasingOrgCode + "】不允许导入不同订单的排程明细！\\n");
					}
					// 需求数量
					BigDecimal scheduleQty = new BigDecimal(sList.get(3));
					if (detail.getCanSendQty().subtract(scheduleQty).compareTo(BigDecimal.ZERO) < 0) {
						sb.append("第" + numRows + "行采购订单号：" + sList.get(0) + "的排程量不能大于可排程量！\\n");
					}
					if (StringUtils.isNotBlank(existParam)) {
						String[] existParams = existParam.split(",");
						String param[] = new String[2];
						// 验证排程明细列表是否已存在相同的采购订单号+行号
						for (int i = 0; i < existParams.length; i++) {
							param = existParams[i].split(";");
							if (param[0].equals(sList.get(0)) && param[1].equals(sList.get(1))) {
								sb.append("第" + numRows + "行采购订单号：" + sList.get(0) + "已存在列表中，不能再导入！\\n");
							}
						}
					}
				}

			}
			if (!isExist) {
				sb.append("第" + numRows + "行采购订单不存在！");
			}
		} else {
			sb.append("第" + numRows + "行采购订单不属于" + purchasingOrgCode + "采购组织和" + vendorErpCode + "供应商!\\n");
		}
	}

	/**
	 * 导入数据处理
	 * 
	 * @param searchParams 查询载体
	 * @param mmpaList 导入的价格申请单据集
	 * @param mapList 导入的数据集
	 * @param sdf 日期格式化
	 * @throws ParseException
	 */
	protected void importData(Map<String, Object> searchParams, List<SendScheduleDetail> modelList, Map<Integer, List<String>> mapList,
			SimpleDateFormat sdf, List<Long> idList) throws ParseException {

		for (Integer index : mapList.keySet()) {
			PurchaseOrderDetail detail = purchaseOrderDetailLogic.findById(idList.get(index - 1));
			List<String> dataList = mapList.get(index);
			SendScheduleDetail po = new SendScheduleDetail();
			for (int i = 0; i < dataList.size(); i++) {
				SendScheduleCommon sendScheduleCommon = new SendScheduleCommon();
				// 采购订单明细id
				po.setPurchaseOrderDetailId(detail.getPurchaseOrderDetailId());
				// 采购订单号
				po.setPurchaseOrderNo(dataList.get(0));
				// 行号
				po.setRowIds(Integer.parseInt(dataList.get(1)));
				// 物料编码
				po.setMaterialCode(detail.getMaterialCode());
				// 物料名称
				po.setMaterialName(detail.getMaterialName());
				// 单位
				po.setUnitCode(detail.getUnitCode());
				// 单位名称
				po.setUnitName(detail.getUnitName());
				// 订单数量
				po.setSendQty(detail.getVendorQty());
				// 已排程量
				po.setScheduleQty(new BigDecimal(dataList.get(3)));
				// 未排程量
				po.setCanSendQty(new BigDecimal(dataList.get(3)));
				// 工厂编码
				po.setPlantCode(detail.getPlantCode());
				// 工厂名称
				po.setFactoryName(detail.getPlantName());
				// 库存地点编码
				po.setStockLocal(detail.getStoreLocal());
				// 需求时间
				Calendar cal = new GregorianCalendar();
				cal.setTime(sdf.parse(dataList.get(2)));
				po.setScheduleTime(cal);
				// 确认交货日期
				sendScheduleCommon.setVendorTime(detail.getVendorTime());
				BigDecimal scheduledQty = detail.getScheduledQty() == null ? BigDecimal.ZERO : detail.getScheduledQty();
				sendScheduleCommon.setScheduleQty(scheduledQty.add(new BigDecimal(dataList.get(3))));
				sendScheduleCommon.setCanSendQty(detail.getUnScheduledQty().subtract(new BigDecimal(dataList.get(3))));
				sendScheduleCommon.setVendorCode(detail.getPurchaseOrder().getVendorCode());
				sendScheduleCommon.setVendorErpCode(detail.getPurchaseOrder().getVendorErpCode());
				sendScheduleCommon.setVendorName(detail.getPurchaseOrder().getVendorName());
				sendScheduleCommon.setErpPurchaseOrderNo(detail.getPurchaseOrder().getErpPurchaseOrderNo());
				sendScheduleCommon.setPurchaseOrderNo(detail.getPurchaseOrder().getPurchaseOrderNo());
				po.setSendScheduleCommon(sendScheduleCommon);
				// 未税价
				po.setTaxPrice(detail.getBuyerPrice());
				// 行项目类别
				po.setLineItemTypeCode(detail.getLineItemTypeCode());
				// 供应商编码
				po.setVendorCode(detail.getPurchaseOrder().getVendorCode());
				// 供应商名称
				po.setVendorName(detail.getPurchaseOrder().getVendorName());
				// 公司编码
				sendScheduleCommon.setCompanyCode(detail.getPurchaseOrder().getCompanyCode());
				// SAP采购订单号
				po.setErpPurchaseOrderNo(detail.getPurchaseOrder().getErpPurchaseOrderNo());
			}
			modelList.add(po);
		}
	}

	/**
	 * 撤销审批
	 * 
	 * @return
	 */
	@PostMapping(value = "/revokeaudit")
	public Result revokeAudit(Long id) {
		String result = sendScheduleLogic.revokeAudit(id, getUserId(), getUserName());
		return Result.success(result);
	}

	/**
	 * 日志状态国际化
	 * 
	 * @param stateName
	 * @return
	 */
	protected String loggerStr(String stateName) {
		String newStateName = "";
		newStateName = "$(\"state." + stateName.toLowerCase() + "\")";
		return newStateName;
	}

}
