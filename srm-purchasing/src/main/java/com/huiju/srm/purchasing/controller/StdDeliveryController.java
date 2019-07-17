package com.huiju.srm.purchasing.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.core.sys.entity.User;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.groovy.service.GroovyScriptInvokerService;
import com.huiju.module.license.annotation.Certificate;
import com.huiju.module.license.annotation.Certificate.RequiredType;
import com.huiju.srm.commons.entity.ExpressResultEntity;
import com.huiju.srm.commons.utils.CloudReportController;
import com.huiju.srm.commons.utils.ExpressUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.masterdata.api.DataDictClient;
import com.huiju.srm.masterdata.entity.DataDict;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.DeliveryDtl;
import com.huiju.srm.purchasing.entity.DeliveryExpressDtl;
import com.huiju.srm.purchasing.entity.DeliveryState;
import com.huiju.srm.purchasing.entity.LogisticsDtlDtl;
import com.huiju.srm.purchasing.entity.PurchaseOrderCheckState;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.PurchaseOrderFlowState;
import com.huiju.srm.purchasing.entity.PurchaseOrderState;
import com.huiju.srm.purchasing.entity.SendScheduleSelect;
import com.huiju.srm.purchasing.service.DeliveryDtlService;
import com.huiju.srm.purchasing.service.DeliveryExpressDtlService;
import com.huiju.srm.purchasing.service.DeliveryService;
import com.huiju.srm.purchasing.service.LogisticsDtlDtlService;
import com.huiju.srm.purchasing.service.PurchaseOrderDetailService;
import com.huiju.srm.purchasing.service.SendScheduleDetailService;
import com.huiju.srm.purchasing.service.SendScheduleSelectService;
import com.huiju.srm.vendor.entity.Vendor;
import com.huiju.srm.vendor.service.VendorService;

/**
 * <pre>
 * 送货管理数据表Action
 * </pre>
 * 
 * @author wz
 */
@Certificate(value = { "CP_delivery" }, requiredType = RequiredType.ONE)
public class StdDeliveryController extends CloudReportController {

	@Autowired(required = false)
	protected DeliveryService deliveryLogic;
	@Autowired(required = false)
	protected DeliveryDtlService deliveryDtlLogic;
	@Autowired(required = false)
	protected DeliveryExpressDtlService deliveryExpressDtlLogic;
	@Autowired(required = false)
	protected LogisticsDtlDtlService logisticsDtlDtlLogic;
	@Autowired(required = false)
	protected BillSetServiceClient billSetLogic;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthLogic; // 资源过滤远程接口注入
	@Autowired(required = false)
	protected UserClient userLogic;
	@Autowired(required = false)
	protected SendScheduleDetailService sendScheduleDetailLogic;
	@Autowired(required = false)
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired(required = false)
	protected SendScheduleSelectService sendScheduleSelectLogic;
	@Autowired(required = false)
	protected VendorService vendorLogic;
	@Autowired(required = false)
	protected GroovyScriptInvokerService groovyScriptInvokerLogic;
	@Autowired(required = false)
	protected DataDictClient dataDictClient;

	protected String className;

	protected String submitData;

	/**
	 * 获取列表 / 查询数据
	 * 
	 * @return
	 */
	@PostMapping(value = "/list")
	public Page<Delivery> list() {
		Page<Delivery> page = buildPage(Delivery.class);
		Map<String, Object> searchParams = buildParams();
		String initStates = getInitStates();

		// 单据状态查询
		if (!"".equals(initStates) && initStates != null) {
			String value = initStates;
			String[] values = value.trim().split(",");
			List<DeliveryState> statusArray = new ArrayList<DeliveryState>();
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].replaceAll(" ", "");
				DeliveryState status = DeliveryState.valueOf(values[i]);
				statusArray.add(status);
			}
			searchParams.put("IN_status", statusArray);
		}

		String synStatus = getAttribute("synStatus");
		// 同步状态查询
		if (synStatus != null) {
			String[] params = synStatus.trim().split(",");
			List<SrmSynStatus> synStatues = new ArrayList<SrmSynStatus>();
			for (int i = 0; i < params.length; i++) {
				params[i] = params[i].replaceAll(" ", "");
				SrmSynStatus statues = SrmSynStatus.valueOf(params[i]);
				synStatues.add(statues);
			}
			searchParams.put("IN_synchronizeStatus", synStatues);
		}

		if (isRoleOf(SrmConstants.ROLETYPE_V)) { // 供应商
			// 供应商只能查看到自己且所有状态数据

			searchParams.put("EQ_vendorErpCode", getErpCode());

			// 只有采购才要根据资源组过滤
		} else if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			searchParams.putAll(
					userAuthLogic.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), Delivery.class)));
		}

		return deliveryLogic.findAllWithoutAssociation(page, searchParams);
	}

	/**
	 * <pre>
	 * 返回编辑表单数据对象
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/get")
	public Result get(Long id) {
		Delivery delivery = deliveryLogic.findById(id);
		if (delivery == null) {
			return Result.error("信息不存在！");
		}
		return Result.success(DataUtils.toJson(delivery, "deliveryDtls"));
	}

	/**
	 * <pre>
	 * 保存表单
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/save")
	public Result save(@RequestBody JsonParam<Delivery> param) {
		Delivery model = param.getModel();
		String submitFlag = param.getSubmitFlag();

		model.setDeliveryCode(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_ASN));
		model.setClientCode(getClientCode());
		model.setCreateUserId(getUserId());
		model.setCreateUserName(getUserName());
		model.setCreateTime(Calendar.getInstance());
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setModifyTime(Calendar.getInstance());
		for (DeliveryDtl deliveryDtl : model.getDeliveryDtls()) {
			deliveryDtl.setDelivery(model);
		}
		if (model.getDeliveryExpressDtls() != null && model.getDeliveryExpressDtls().size() > 0) {
			for (DeliveryExpressDtl deliveryExpressDtl : model.getDeliveryExpressDtls()) {
				deliveryExpressDtl.setDelivery(model);
			}
		}
		if ("save".equalsIgnoreCase(submitFlag)) {
			model.setStatus(DeliveryState.NEW);
			model.setSynchronizeStatus(SrmSynStatus.SYNCHRONIZEDNOT);
			model = deliveryLogic.saveDelivery(model);
			deliveryLogic.addLog(getUserId(), getUserName(), model.getDeliveryId(), "送货单创建", SrmConstants.PERFORM_SAVE,
					model.getDeliveryCode(), SrmConstants.PLATFORM_WEB);

		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			model.setStatus(DeliveryState.WAIT);
			model = deliveryLogic.saveDelivery(model);
			deliveryLogic.addLog(getUserId(), getUserName(), model.getDeliveryId(), "送货单提交", SrmConstants.PERFORM_AUDIT,
					model.getDeliveryCode(), SrmConstants.PLATFORM_WEB);
		}

		// 同步sap
		if (DeliveryState.WAIT.equals(model.getStatus())) {
			deliveryLogic.syncDelivery(model);
		}
		return Result.success();

	}

	/**
	 * 修改
	 * 
	 * @return String
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody JsonParam<Delivery> param) {
		Delivery model = param.getModel();
		String submitFlag = param.getSubmitFlag();

		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		model.setModifyTime(Calendar.getInstance());
		model.setClientCode(getClientCode());
		Delivery delivery = deliveryLogic.findById(model.getDeliveryId());
		if (delivery == null) {
			return Result.error(getText("message.notexisted"));
		}
		setDeliveryOneToMaryValue(model);
		if ("save".equalsIgnoreCase(submitFlag)) {
			model.setStatus(DeliveryState.NEW);
			deliveryLogic.addLog(getUserId(), getUserName(), model.getDeliveryId(), "送货单修改", SrmConstants.PERFORM_EDIT,
					model.getDeliveryCode(), SrmConstants.PLATFORM_WEB);

		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			model.setStatus(DeliveryState.WAIT);
			deliveryLogic.addLog(getUserId(), getUserName(), model.getDeliveryId(), "送货单提交", SrmConstants.PERFORM_AUDIT,
					model.getDeliveryCode(), SrmConstants.PLATFORM_WEB);
		}
		model.setDeliveryExpressDtls(new ArrayList<DeliveryExpressDtl>());
		model = deliveryLogic.updateDelivery(model);

		// 同步sap
		if (DeliveryState.WAIT.equals(model.getStatus())) {
			deliveryLogic.syncDelivery(model);
		}

		return Result.success();
	}

	/**
	 * 删除
	 * 
	 * @return Result
	 */
	@PostMapping("/delete")
	public Result delete(@RequestParam List<Long> ids, @RequestParam String message) {
		deliveryLogic.deleteDelivery(ids);
		if (ids != null && ids.size() > 0) {
			for (Long id : ids) {
				deliveryLogic.addLog(getUserId(), getUserName(), id, "送货单删除,原因:" + message, SrmConstants.PERFORM_DELETE, "",
						SrmConstants.PLATFORM_WEB);
			}

		}
		return Result.success();
	}

	/**
	 * 查询送货管理明细表明细
	 */
	@PostMapping("/finddeliverydtlall")
	public String findDeliveryDtlAll() {
		Map<String, Object> searchParams = buildParams();
		List<DeliveryDtl> list = deliveryDtlLogic.findAll(searchParams);
		return DataUtils.toJson(list, "delivery.deliveryDtls");
	}

	/**
	 * 查询快递明细表明细
	 */
	@PostMapping("/finddeliveryexpressdtlall")
	public String findDeliveryExpressDtlAll() {
		Map<String, Object> searchParams = buildParams();
		List<DeliveryExpressDtl> list = deliveryExpressDtlLogic.findAll(searchParams);
		return DataUtils.toJson(list);
	}

	/**
	 * 查询物流详情明细明细
	 */
	@PostMapping("/findlogisticsdtldtlall")
	public String findLogisticsDtlDtlAll() {
		Map<String, Object> searchParams = buildParams();
		List<LogisticsDtlDtl> list = new ArrayList<LogisticsDtlDtl>();
		if (searchParams.get("EQ_deliveryExpressDtl_deliveryExpressDtlId") != null) {
			Long deliveryExpressDtlId = Long.valueOf(searchParams.get("EQ_deliveryExpressDtl_deliveryExpressDtlId").toString());
			deliveryLogic.findLogisticsDtlDtlAll(deliveryExpressDtlId);
		}
		list = logisticsDtlDtlLogic.findAll(searchParams, "updateTime,DESC");
		return DataUtils.toJson(list);
	}

	/**
	 * 根据单号判断归属快递公司
	 */
	@PostMapping("/getexpresscompanybyno")
	public String getExpressCompanyByNo(@RequestParam String expressNo) {
		ExpressResultEntity deliveryExpressDtl = ExpressUtils.getExpressCompanyByNo(expressNo);
		if (StringUtils.isNotBlank(deliveryExpressDtl.getExpressCompanyCode())) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("EQ_groupCode", "expressCompany");
			params.put("EQ_itemCode", deliveryExpressDtl.getExpressCompanyCode());
			List<DataDict> dataDicts = dataDictClient.findAll(new FeignParam<DataDict>(params));
			if (dataDicts != null && dataDicts.size() > 0) {
				DataDict dd = dataDicts.get(0);
				deliveryExpressDtl.setExpressCompanyName(dd.getItemName());// 快递公司名称
			} else {
				deliveryExpressDtl.setMessage("接口调用成功，但数据字典并未找到对应快递公司。");
			}
		}
		return DataUtils.toJson(deliveryExpressDtl);
	}

	/**
	 * 设置招标单一对多的值
	 */
	protected void setDeliveryOneToMaryValue(Delivery delivery) {
		if (delivery.getDeliveryDtls() != null) {
			for (DeliveryDtl item : delivery.getDeliveryDtls()) {
				item.setDelivery(delivery);
			}
		}

		if (delivery.getDeliveryExpressDtls() != null && delivery.getDeliveryExpressDtls().size() > 0) {
			// 查询Map
			Map<String, Object> searchParams = new HashMap<String, Object>();
			// 快递公司明细id集
			List<Long> ids = new ArrayList<Long>();
			// 快递公司明细对应物流信息map
			Map<Long, List<LogisticsDtlDtl>> dtldtlMap = new HashMap<Long, List<LogisticsDtlDtl>>();
			for (DeliveryExpressDtl deliveryExpressDtl : delivery.getDeliveryExpressDtls()) {
				// 找出单据对应的所有快递公司明细
				ids.add(deliveryExpressDtl.getDeliveryExpressDtlId());
			}
			searchParams.put("IN_deliveryExpressDtl_deliveryExpressDtlId", ids);
			List<LogisticsDtlDtl> dtldtls = logisticsDtlDtlLogic.findAll(searchParams);
			if (dtldtls != null && dtldtls.size() > 0) {
				for (LogisticsDtlDtl ldd : dtldtls) {
					if (dtldtlMap.containsKey(ldd.getDeliveryExpressDtl().getDeliveryExpressDtlId())) {// 如果物流信息对应的快递公司有数据，则放进去
						dtldtlMap.get(ldd.getDeliveryExpressDtl().getDeliveryExpressDtlId()).add(ldd);
					} else {// 如果物流信息对应的快递公司没数据，则创建新的list方便后面数据存
						List<LogisticsDtlDtl> dtldtlList = new ArrayList<LogisticsDtlDtl>();
						dtldtlMap.put(ldd.getDeliveryExpressDtl().getDeliveryExpressDtlId(), dtldtlList);
					}
				}
			}
			for (DeliveryExpressDtl deliveryExpressDtl : delivery.getDeliveryExpressDtls()) {
				if (dtldtlMap.containsKey(deliveryExpressDtl.getDeliveryExpressDtlId())) {
					deliveryExpressDtl.setLogisticsDtlDtls(dtldtlMap.get(deliveryExpressDtl.getDeliveryExpressDtlId()));
				}
				deliveryExpressDtl.setDelivery(delivery);
			}
		}

	}

	/**
	 * 取消送货单
	 *
	 * @return string
	 */
	@PostMapping("/cancel")
	public Result cancel(@RequestParam Long id) {
		Map<String, Object> search = new HashMap<String, Object>();
		search.put("EQ_delivery_deliveryId", id);
		List<DeliveryDtl> deliveryDtls = deliveryDtlLogic.findAll(search);
		if (deliveryDtls != null) {
			for (DeliveryDtl detail : deliveryDtls) {
				if (BigDecimal.ZERO.compareTo(detail.getReceivedNumber()) < 0) {
					return Result.error("已存在收货，不能取消！");
				}
			}
		}
		Delivery model = deliveryLogic.cancelDelivery(id);
		deliveryLogic.addLog(getUserId(), getUserName(), model.getDeliveryId(), "送货单取消", SrmConstants.PERFORM_TOCANCEL,
				model.getDeliveryCode(), SrmConstants.PLATFORM_WEB);
		deliveryLogic.syncDelivery(model);
		return Result.success();
	}

	@PostMapping("/findonevendor")
	public List<Vendor> findOneVendor() {
		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			Map<String, Object> searchParams = new HashMap<String, Object>();
			searchParams.put("EQ_vendorErpCode", getErpCode());
			return vendorLogic.findAllWithoutAssociation(searchParams);
		}
		return null;
	}

	/**
	 * 关闭送货单
	 *
	 * @return string
	 */
	@PostMapping("/close")
	public Result close() {
		return Result.success();
	}

	/**
	 * 获取采购员列表/查询数据（全部）
	 *
	 * @return string
	 */
	@PostMapping("/getuserlist")
	public List<User> getUserList() {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_role_roleType", "B");
		return userLogic.findAllByParams(searchParams);
	}

	/**
	 * 同步到erp
	 *
	 * @return String
	 */
	@PostMapping("/syncerp")
	public Result syncErp(Long id) {
		Delivery model = deliveryLogic.findById(id);
		Delivery delivery = deliveryLogic.syncDelivery(model);
		if (delivery.getSynchronizeStatus() == SrmSynStatus.SYNSUCCESS) {
			deliveryLogic.addLog(getUserId(), getUserName(), model.getDeliveryId(), "送货单同步", SrmConstants.PERFORM_SYNC,
					model.getDeliveryCode(), SrmConstants.PLATFORM_WEB);
			return Result.success();
		} else {
			return Result.error(delivery.getErpReturnMsg());
		}
	}

	/**
	 * 取消送货明细
	 *
	 * @return string
	 */
	@PostMapping("/canceldetail")
	public Result cancelDetail(Long id) {
		deliveryLogic.cancelDetail(id);
		return Result.success();
	}

	/**
	 * 关闭送货明细
	 *
	 * @return string
	 */
	@PostMapping("/closedetail")
	public Result closeDetail(Long id) {
		deliveryLogic.closeDetail(id);
		return Result.success();
	}

	/**
	 * 采购订单弹出框
	 * 
	 * @return
	 */
	@PostMapping("/getpoddetail")
	public String getPODDetail() {
		Page<PurchaseOrderDetail> page = buildPage(PurchaseOrderDetail.class);
		Map<String, Object> searchParams = buildParams();

		// 过滤条件
		// 采购组织编码+供应商编码+工厂编码+订单状态为执行+
		// 订单审核状态为审核通过+订单确认状态为接受/确认变更+排程标识为否+
		// 订单明细关闭标识为否+订单明细删除标识为否+订单明细可送货量大于0；
		// 如果主单上库存地点填写了，需要增加库存地点过滤条件
		searchParams.put("EQ_purchaseOrder_purchaseOrderState", PurchaseOrderState.OPEN);
		searchParams.put("EQ_purchaseOrder_purchaseOrderFlowState", PurchaseOrderFlowState.PASS);
		searchParams.put("IN_purchaseOrder_purchaseOrderCheckState",
				Arrays.asList(PurchaseOrderCheckState.ACCEPT, PurchaseOrderCheckState.FIRMHOLD));
		searchParams.put("EQ_scheduleFlag", 0);
		searchParams.put("EQ_closeFlag", 0);
		searchParams.put("EQ_deleteFlag", 0);
		searchParams.put("GT_canSendQty", BigDecimal.ZERO);
		searchParams.put("EQ_erpSynState", 1);// 增加ERP同步标识 已同步过滤条件
		searchParams.put("EQ_isAchieveLimit", "N");

		page = purchaseOrderDetailLogic.findAll(page, searchParams);

		return DataUtils.toJson(page, "purchaseDualUnitConversions", "purchaseOrderPricings");
	}

	/**
	 * 送货排程弹出框
	 * 
	 * @return
	 */
	@PostMapping("/getssdetail")
	public String getSSDetail() {
		Page<SendScheduleSelect> page = buildPage(SendScheduleSelect.class);
		Map<String, Object> searchParams = buildParams();

		// 查询供应商ERP编码
		if (searchParams.containsKey("EQ_vendorCode")) {
			String vendorCode = (String) searchParams.get("EQ_vendorCode");
			Map<String, Object> vendorMap = new HashMap<String, Object>();
			vendorMap.put("EQ_vendorCode", vendorCode);
			Vendor v = vendorLogic.findOne(vendorMap);
			if (v != null && v.getVendorErpCode() != null) {
				searchParams.remove("EQ_vendorCode");
				searchParams.put("EQ_vendorCode_OR_vendorCode", new String[] { vendorCode, v.getVendorErpCode() });
			}
		}

		// 过滤条件
		// 采购组织编码+供应商编码+工厂编码+订单状态为执行+订单审核状态为审核通过+
		// 订单确认状态为接受/确认变更+排程标识为是+订单明细关闭标识为否+
		// 订单明细删除标识为否+排程明细可送货量>0的送货排程明细数据；
		// 如果主单上库存地点填写了，需要增加库存地点过滤条件；

		// 默认按需求时间升序排列，再按物料编码升序排列
		page.asc("scheduleTime");
		page.asc("materialCode");
		page = sendScheduleSelectLogic.findAll(page, searchParams);

		return DataUtils.toJson(page);
	}

	@Override
	public Map<String, Object> buildCondition() {
		Map<String, Object> searchParams = buildParams();
		searchParams.put("id", Long.valueOf(request.getParameter("id")).longValue());
		return searchParams;
	}

	protected String jasperFile() {
		Long id = Long.valueOf(request.getParameter("id"));
		Map<String, Object> hm = new HashMap<String, Object>();
		if (id == null) {
			return NONE;
		}
		Delivery model = deliveryLogic.findById(id);
		if (model == null) {
			return null;
		}
		hm.put("po", model);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("map", hm);
		String value = null;
		try {
			value = (String) groovyScriptInvokerLogic.invoke("CP0502", map);
			// 送货单导出模板类型配置
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 脚本返回值为1时，生成一维码，返回值为2时，生成二维码，默认生成一维码
		if (value.equals("1")) {
			return "deliveryOne";
		} else if (value.equals("2")) {
			return "deliveryTwo";
		} else {
			return "deliveryOne";
		}
	}

}