package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FetchType;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.core.sys.entity.User;
import com.huiju.interaction.api.InteractionClient;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.groovy.service.GroovyScriptInvokerService;
import com.huiju.module.log.Level;
import com.huiju.module.log.Log;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.module.util.StringUtils;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.portal.dto.PortalMethodType;
import com.huiju.portal.dto.PortalParameters;
import com.huiju.srm.commons.entity.ExpressParamsDtlEntity;
import com.huiju.srm.commons.entity.ExpressResultEntity;
import com.huiju.srm.commons.entity.ExpressState;
import com.huiju.srm.commons.utils.ExpressUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.masterdata.api.CompanyPurchaseOrgClient;
import com.huiju.srm.masterdata.api.PurchasingOrganizationClient;
import com.huiju.srm.purchasing.dao.DeliveryDtlDao;
import com.huiju.srm.purchasing.dto.DeliveryDtlDto;
import com.huiju.srm.purchasing.dto.DeliveryDto;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.DeliveryDtl;
import com.huiju.srm.purchasing.entity.DeliveryExpressDtl;
import com.huiju.srm.purchasing.entity.DeliveryState;
import com.huiju.srm.purchasing.entity.LogisticsDtlDtl;
import com.huiju.srm.purchasing.entity.PurchaseOrder;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.SendSchedule;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.purchasing.entity.SendScheduleState;
import com.huiju.srm.ws.entity.WsRequestLog;
import com.huiju.srm.ws.service.WsRequestLogService;

/**
 * <pre>
 * 送货管理数据表EaoBean
 * </pre>
 * 
 * @author wz
 */
public class StdDeliveryServiceImpl extends JpaServiceImpl<Delivery, Long> implements StdDeliveryService {

	@Autowired
	protected DeliveryDtlDao deliveryDtlEao;

	@Autowired
	protected PurchaseOrderService purchaseOrderEao;

	@Autowired
	protected SendScheduleService sendScheduleEao;

	@Autowired(required = false)
	protected PurchaseOrderDetailService purchaseOrderDetailEao;

	@Autowired(required = false)
	protected DeliveryExpressDtlService deliveryExpressDtlService;

	@Autowired(required = false)
	protected LogisticsDtlDtlService logisticsDtlDtlService;

	@Autowired(required = false)
	protected SendScheduleDetailService sendScheduleDetailEao;

	@Autowired(required = false)
	protected ReceivingNoteService receivingNoteService;

	@Autowired(required = false)
	protected GroovyScriptInvokerService groovyScriptInvokerLogic;

	@Autowired(required = false)
	protected UserClient userLogic;

	@Autowired(required = false)
	protected PortalServiceClient portalDealDataLogic;

	@Autowired(required = false)
	protected DeliveryService deliveryLogic;

	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthLogic;

	@Autowired
	protected WsRequestLogService wsRequestLogLogic;

	@Autowired(required = false)
	protected InteractionClient interactLogic;

	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupBean;

	@Autowired(required = false)
	protected PurchasingOrganizationClient purchasingOrganizationLogic;

	@Autowired(required = false)
	protected CompanyPurchaseOrgClient companyPurchaseOrgLogic;

	@Override
	public String findDeliveryAllPage(Page<Delivery> page, Map<String, Object> map) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		String clientCode = map.get("clientCode").toString();
		String roleType = map.get("roleType").toString();
		String billFlag = map.get("billFlag").toString();
		String userCode = map.get("userCode").toString();

		map.remove("clientCode");// 客户端
		map.remove("roleType");// 角色类型
		map.remove("userId");// 用户编码
		map.remove("billFlag");// 状态标识
		map.remove("userCode");

		// 是按照送货单状态为:（待收货，收货中，同步成功）的才可以点收
		searchParams.put("EQ_synchronizeStatus", SrmSynStatus.SYNSUCCESS);
		if (SrmConstants.ROLETYPE_V.equals(roleType)) { // 供应商
			// 供应商只能查看到自己的数据
			searchParams.put("EQ_vendorCode", userCode);
		} else if (SrmConstants.ROLETYPE_B.equals(roleType)) {
			// 只有采购才要根据资源组过滤
			Map<String, Object> userGroupParams = userAuthLogic
					.buildAuthFieldParams(new UserAuthGroupParam(clientCode, userCode, Delivery.class));
			searchParams.putAll(userGroupParams);
		}

		// 待处理
		if ("unDealList".equals(billFlag)) {
			searchParams.put("IN_status", new DeliveryState[] { DeliveryState.WAIT, DeliveryState.RECEIVING });
		}
		// ------------------查询处理-------------------
		for (String key : map.keySet()) {
			if (key.contains("_")) {
				String value = (String) map.get(key);
				if (key.equals("IN_status")) {// 审核状态
					dealDeliveryStatus(searchParams, value);
				} else {
					searchParams.put(key, value);// 其他条件
				}
			}
		}
		map.remove("IN_status");
		searchParams.putAll(map);
		page = deliveryLogic.findAll(page, searchParams);
		return DataUtils.toJson(page, new String[] { "deliveryDtls" });
	}

	/**
	 * 处理查询状态
	 * 
	 * @param searchParams
	 * @param initStates
	 */
	protected void dealDeliveryStatus(Map<String, Object> searchParams, String initStates) {
		// 待处理，待审核初始化状态过滤
		if (!"".equals(initStates) && initStates != null) {
			String value = initStates;
			String[] values = value.trim().split(",");
			DeliveryState[] statusArray = new DeliveryState[values.length];
			for (int i = 0; i < values.length; i++) {
				DeliveryState status = DeliveryState.valueOf(values[i].trim());
				statusArray[i] = status;
			}
			searchParams.put("IN_status", Arrays.asList(statusArray));
		}
	}

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
			String initStates = (String) specialParams.get("initStates");
			String synStatus = (String) specialParams.get("synStatus");
			String erpCode = (String) specialParams.get("erpCode");
			String clientCode = (String) specialParams.get("clientCode");
			String userCode = (String) specialParams.get("userCode");
			// 单据状态查询
			if (!"".equals(initStates) && initStates != null) {
				String value = initStates;
				String[] values = value.trim().split(",");
				DeliveryState[] statusArray = new DeliveryState[values.length];
				for (int i = 0; i < values.length; i++) {
					values[i] = values[i].replaceAll(" ", "");
					DeliveryState status = DeliveryState.valueOf(values[i]);
					statusArray[i] = status;
				}
				searchParams.put("IN_status", Arrays.asList(statusArray));
			}

			// 同步状态查询
			if (synStatus != null) {
				String param = synStatus;
				String[] params = param.trim().split(",");
				SrmSynStatus[] synStatues = new SrmSynStatus[params.length];
				for (int i = 0; i < params.length; i++) {
					params[i] = params[i].replaceAll(" ", "");
					SrmSynStatus statues = SrmSynStatus.valueOf(params[i]);
					synStatues[i] = statues;
				}
				searchParams.put("IN_synchronizeStatus", Arrays.asList(synStatues));
			}

			if (SrmConstants.ROLETYPE_V.equals(roleType)) {
				// 供应商只能查看到自己且所有状态数据
				searchParams.put("EQ_vendorErpCode", erpCode);
			} else if (SrmConstants.ROLETYPE_B.equals(roleType)) {
				searchParams.putAll(userAuthLogic.buildAuthFieldParams(new UserAuthGroupParam(clientCode, userCode, Delivery.class)));
			}
			Page<Delivery> page = new Page<Delivery>(start, limit, sort, dir);
			page = dao.findAll(page, searchParams);
			resultMap.put("data", page);

			SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
			Set<String> excludes = filter.getExcludes();
			excludes.add("deliveryDtls");
			JSON.toJSONString(resultMap, filter, SerializerFeature.WriteMapNullValue);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("errormsg", e.getMessage());
		}

		return JSON.toJSONString(resultMap);
	}

	/**
	 * 保存送货单
	 *
	 * @param delivery 送货单
	 * @return DeliVery实例类
	 */
	@Override
	public Delivery saveDelivery(Delivery delivery) {
		List<Long> podDtList = new ArrayList<Long>();
		List<Long> ssdIdList = new ArrayList<Long>();
		Map<Long, PurchaseOrderDetail> poDtlMap = new HashMap<Long, PurchaseOrderDetail>();
		Map<Long, SendScheduleDetail> ssDtlMap = new HashMap<Long, SendScheduleDetail>();

		for (DeliveryDtl detail : delivery.getDeliveryDtls()) {
			if (detail.getOrderDetailId() != null) {
				podDtList.add(detail.getOrderDetailId());
			}
			if (detail.getSendDetailId() != null) {
				ssdIdList.add(detail.getSendDetailId());
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("IN_purchaseOrderDetailId", podDtList);
		List<PurchaseOrderDetail> poDtlList = purchaseOrderDetailEao.findAll(params);
		for (PurchaseOrderDetail pod : poDtlList) {
			poDtlMap.put(pod.getPurchaseOrderDetailId(), pod);
		}
		if (ssdIdList != null && ssdIdList.size() > 0) {
			params.clear();
			params.put("IN_sendScheduleDetailId", ssdIdList);
			List<SendScheduleDetail> ssDtlList = sendScheduleDetailEao.findAll(params);
			for (SendScheduleDetail ssd : ssDtlList) {
				ssDtlMap.put(ssd.getSendScheduleDetailId(), ssd);
			}
		}

		for (DeliveryDtl detail : delivery.getDeliveryDtls()) {
			if (detail.getDataFrom() == 1) {// 数据来源订单明细
				// 更新对应订单明细记录
				this.addPOD(detail, poDtlMap);

			} else if (detail.getDataFrom() == 2) {// 数据来源排程明细
				// 更新对应排程子明细
				this.addSCD(detail, ssDtlMap, poDtlMap);

			}
		}

		Delivery newo = dao.save(delivery);
		// 消息通知

		// 添加业务跟踪
		PortalParameters pp = new PortalParameters();
		pp.addPortalMethod(PortalMethodType.ST_ADD).setRemindInfoCode(null).setBillTypeCode(SrmConstants.BILLTYPE_ASN)
				.setBillNo(newo.getDeliveryCode()).setBillId(newo.getDeliveryId().toString());
		// portalDealDataLogic.data4Portal(pp);
		return newo;
	}

	/**
	 * 保存或提交 增加送货单对应的订单明细 送货量 在途量
	 *
	 * @param deliveryDtl 送货明细
	 * 
	 * @param id 订单Id
	 */
	protected void addPOD(DeliveryDtl deliveryDtl, Map<Long, PurchaseOrderDetail> poDtlMap) {
		// 找到对应的订单明细记录
		PurchaseOrderDetail pod = poDtlMap.get(deliveryDtl.getOrderDetailId());
		if (pod == null) {
			pod = purchaseOrderDetailEao.findById(deliveryDtl.getOrderDetailId());
		}

		// 重新计算对应订单明细的在途量等相关数量信息
		recountPodQty(pod, deliveryDtl.getDeliveryNumber(), true);

	}

	/**
	 * 更新对应采购订单明细的送货量和在途量
	 * 
	 * @param old_detail 旧的送货单明细
	 * @param new_detail 新的送货单明细
	 * @param id 订单明细ID
	 */
	protected void updatePOD(DeliveryDtl old_detail, DeliveryDtl new_detail, Long id) {
		// 找到对应的订单明细记录
		PurchaseOrderDetail pod = purchaseOrderDetailEao.findById(id);

		BigDecimal oldNum = (old_detail.getDeliveryNumber() == null ? BigDecimal.ZERO : old_detail.getDeliveryNumber());
		BigDecimal newNum = (new_detail.getDeliveryNumber() == null ? BigDecimal.ZERO : new_detail.getDeliveryNumber());

		// 重新计算对应订单明细的在途量等相关数量信息
		recountPodQty(pod, newNum.subtract(oldNum), true);
	}

	/**
	 * 关闭送货单细单 重置采购订单的细单
	 *
	 * @param detail 发货单
	 */
	protected void subPODClose(DeliveryDtl detail, Long id) {
		// 找到对应的订单明细记录
		PurchaseOrderDetail pod = purchaseOrderDetailEao.findById(id);
		// 重新计算对应订单明细的在途量等相关数量信息
		recountPodQty(pod, detail.getDeliveryNumber(), false);
	}

	/**
	 * 取消送货单明细 重置货单对应的订单明细 送货量 在途量
	 *
	 * @param item 送货明细
	 * @param id 采购订单Id
	 */
	protected void subPOD(DeliveryDtl item, Long id) {
		// 找到对应的订单明细记录
		PurchaseOrderDetail pod = purchaseOrderDetailEao.findById(id);

		// 重新计算对应订单明细的在途量等相关数量信息
		recountPodQty(pod, item.getDeliveryNumber(), false);
	}

	/**
	 * 重新计算对应订单明细的在途量等相关数量信息
	 * 
	 * @param pod 采购订单明细
	 * @param deliveryNumber 送货单明细(实际送货数量)
	 * @param flag true 加，false 减
	 */
	protected void recountPodQty(PurchaseOrderDetail pod, BigDecimal deliveryNumber, boolean flag) {
		deliveryNumber = (deliveryNumber == null ? BigDecimal.ZERO : deliveryNumber);
		if (!flag) {
			deliveryNumber = BigDecimal.ZERO.subtract(deliveryNumber);
		}
		// 送货量
		BigDecimal oldQtySend = (pod.getQtySend() == null ? BigDecimal.ZERO : pod.getQtySend());
		BigDecimal newQtySend = oldQtySend.add(deliveryNumber);
		pod.setQtySend(newQtySend);

		// 在途量
		BigDecimal oldQtyOnline = (pod.getQtyOnline() == null ? BigDecimal.ZERO : pod.getQtyOnline());
		BigDecimal newQtyOnline = oldQtyOnline.add(deliveryNumber);
		pod.setQtyOnline(newQtyOnline);

		purchaseOrderDetailEao.recountCanSendQty(pod);
	}

	/**
	 * 保存或提交 增加送货单对应的送货排程 送货量 在途量
	 *
	 * @param deliveryDtl 送货明细
	 * 
	 */
	protected void addSCD(DeliveryDtl deliveryDtl, Map<Long, SendScheduleDetail> ssDtlMap, Map<Long, PurchaseOrderDetail> poDtlMap) {
		// 找到对应的排程子明细
		SendScheduleDetail ssd = ssDtlMap.get(deliveryDtl.getSendDetailId());
		if (ssd == null) {
			ssd = sendScheduleDetailEao.findById(deliveryDtl.getSendDetailId());
		}

		// 送货量
		BigDecimal oldDeliveryQty = (ssd.getDeliveryQty() == null ? BigDecimal.ZERO : ssd.getDeliveryQty());
		BigDecimal newDeliveryQty = oldDeliveryQty
				.add(deliveryDtl.getDeliveryNumber() == null ? BigDecimal.ZERO : deliveryDtl.getDeliveryNumber());
		ssd.setDeliveryQty(newDeliveryQty);

		// 在途量
		BigDecimal oldOnWayQty = (ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty());
		BigDecimal newQtyOnline = oldOnWayQty
				.add(deliveryDtl.getDeliveryNumber() == null ? BigDecimal.ZERO : deliveryDtl.getDeliveryNumber());
		ssd.setOnWayQty(newQtyOnline);

		// 可送货量
		BigDecimal oldCanSendQty = ssd.getCanSendQty();
		if (oldCanSendQty != null) {
			// 排程需求量 - 在途量 - 收货量 + 退货量
			if (ssd.getScheduleQty() != null) {
				BigDecimal newCanSendQty = ssd.getScheduleQty().subtract(ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty())
						.subtract(ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty())
						.add(ssd.getReturnGoodsQty() == null ? BigDecimal.ZERO : ssd.getReturnGoodsQty());
				ssd.setCanSendQty(newCanSendQty);
			}
		}

		// 排程送货标识更新
		if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) == 0) {
			ssd.setSendFlag(0); // 如果可送货量等于需求量则 为未送货
		} else if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) > 0 && ssd.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {
			ssd.setSendFlag(1); // 如果可送货量大于0并且小于需求量则
		} else if (ssd.getCanSendQty().compareTo(BigDecimal.ZERO) == 0 || ssd.getCanSendQty().compareTo(BigDecimal.ZERO) < 0) {
			ssd.setSendFlag(2); // 如果可送货量等于0为完全送货
		}
		SendScheduleDetail newSsd = sendScheduleDetailEao.save(ssd);

		// 更新排程状态更新
		this.updateSSStatus(newSsd.getSendScheduleNo());

		// 同步更新订单明细
		this.addPOD(deliveryDtl, poDtlMap);
	}

	/**
	 * 更新送货单
	 * 
	 * @param delivery 送货单
	 * @return DeliVery实例类
	 */
	@Override
	public Delivery updateDelivery(Delivery delivery) {
		// TODO Auto-generated method stub
		Delivery old_obj = dao.getById(delivery.getDeliveryId());
		if (old_obj == null) {
			return null;
		}
		List<Long> podDtList = new ArrayList<Long>();
		List<Long> ssdIdList = new ArrayList<Long>();
		Map<Long, PurchaseOrderDetail> poDtlMap = new HashMap<Long, PurchaseOrderDetail>();
		Map<Long, SendScheduleDetail> ssDtlMap = new HashMap<Long, SendScheduleDetail>();

		for (DeliveryDtl detail : delivery.getDeliveryDtls()) {
			if (detail.getOrderDetailId() != null) {
				podDtList.add(detail.getOrderDetailId());
			}
			if (detail.getSendDetailId() != null) {
				ssdIdList.add(detail.getSendDetailId());
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("IN_purchaseOrderDetailId", podDtList);
		List<PurchaseOrderDetail> poDtlList = purchaseOrderDetailEao.findAll(params);
		for (PurchaseOrderDetail pod : poDtlList) {
			poDtlMap.put(pod.getPurchaseOrderDetailId(), pod);
		}
		if (ssdIdList != null && ssdIdList.size() > 0) {
			params.clear();
			params.put("IN_sendScheduleDetailId", ssdIdList);
			List<SendScheduleDetail> ssDtlList = sendScheduleDetailEao.findAll(params);
			for (SendScheduleDetail ssd : ssDtlList) {
				ssDtlMap.put(ssd.getSendScheduleDetailId(), ssd);
			}
		}
		// 送货明细新的集合
		HashMap<Object, DeliveryDtl> newhm = new HashMap<Object, DeliveryDtl>();// 新的对象明细集合

		for (DeliveryDtl detail : delivery.getDeliveryDtls()) {
			newhm.put(detail.getDeliveryDtlId(), detail);

			// 如果该送货明细的是新增的则
			// 更新对应订单明细或排程子明细相关值
			if (detail.getDeliveryDtlId() == null) {
				if (detail.getDataFrom() == 1) { // 来源订单明细
					this.addPOD(detail, poDtlMap);
				} else if (detail.getDataFrom() == 2) { // 来源排程子明细
					this.addSCD(detail, ssDtlMap, poDtlMap);
				}
			}
		}

		// 删除物料明细对应供应商配额明细
		for (DeliveryDtl detail : old_obj.getDeliveryDtls()) {
			// 如果新明细中不存在旧明细则删除该条旧明细
			if (!newhm.containsKey(detail.getDeliveryDtlId())) {
				// 减对应订单明细或排程明细
				if (detail.getDataFrom() == 1) { // 来源订单明细
					this.subPOD(detail, detail.getOrderDetailId());
				} else if (detail.getDataFrom() == 2) { // 来源排程子明细
					this.subSSD(detail);
				}
				deliveryDtlEao.delete(detail); // 在新中不存在的老的数据删除了

				// 如果存在则判断新明细中的送货数量 和 旧明细中的送货数量 是否有变化
			} else if (newhm.containsKey(detail.getDeliveryDtlId())) {
				// 取得新的送货明细送货数量
				DeliveryDtl newSnd = newhm.get(detail.getDeliveryDtlId());
				BigDecimal newDeliveryQty = newSnd.getDeliveryNumber();

				// 如果有变化则更新对应的订单明细或排程子明细
				// 没有变化则不更新
				if (detail.getDeliveryNumber().compareTo(newDeliveryQty) != 0) {
					if (detail.getDataFrom() == 1) {// 来源订单明细
						this.updatePOD(detail, newSnd, newSnd.getOrderDetailId());
					} else if (detail.getDataFrom() == 2) {// 来源排程子明细
						this.updateSSD(detail, newSnd);
					}
				}
			}
		}
		// 重新保存对象
		Delivery new_obj = dao.save(delivery);

		// 消息通知

		return new_obj;
	}

	/**
	 * 更新对应送货排程的送货量和在途量
	 * 
	 * @param old_detail 旧的送货单明细
	 * @param new_detail 新的送货单明细
	 */
	protected void updateSSD(DeliveryDtl old_detail, DeliveryDtl new_detail) {
		// 找到对应的排程子明细
		SendScheduleDetail ssd = sendScheduleDetailEao.findById(new_detail.getSendDetailId());

		// 送货量
		BigDecimal oldDeliveryQty = ssd.getDeliveryQty() == null ? BigDecimal.ZERO : ssd.getDeliveryQty();
		// 先减旧的再加上新的
		BigDecimal newDeliveryQty = oldDeliveryQty
				.subtract(old_detail.getDeliveryNumber() == null ? BigDecimal.ZERO : old_detail.getDeliveryNumber())
				.add(new_detail.getDeliveryNumber() == null ? BigDecimal.ZERO : new_detail.getDeliveryNumber());
		ssd.setDeliveryQty(newDeliveryQty);

		// 在途量
		BigDecimal oldOnWayQty = ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty();
		// 先减旧的再加上新的
		BigDecimal newQtyOnline = oldOnWayQty
				.subtract(old_detail.getDeliveryNumber() == null ? BigDecimal.ZERO : old_detail.getDeliveryNumber())
				.add(new_detail.getDeliveryNumber() == null ? BigDecimal.ZERO : new_detail.getDeliveryNumber());
		ssd.setOnWayQty(newQtyOnline);

		// 可送货量
		// BigDecimal oldCanSendQty = ssd.getCanSendQty() == null ?
		// BigDecimal.ZERO : ssd.getCanSendQty();
		// 排程需求量 - 在途量 - 收货量 + 退货量
		if (ssd.getScheduleQty() != null) {
			BigDecimal newCanSendQty = ssd.getScheduleQty().subtract(ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty())
					.subtract(ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty())
					.add(ssd.getReturnGoodsQty() == null ? BigDecimal.ZERO : ssd.getReturnGoodsQty());
			ssd.setCanSendQty(newCanSendQty);
		}

		// 排程送货标识更新
		// 如果可送货量等于需求量则 为未送货
		if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) == 0) {
			ssd.setSendFlag(0);
		} else if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) > 0 && ssd.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {// 如果可送货量大于0并且小于需求量则
																																	// 为部分送货
			ssd.setSendFlag(1);
		} else if (ssd.getCanSendQty().compareTo(BigDecimal.ZERO) == 0) {// 如果可送货量等于0为完全送货
			ssd.setSendFlag(2);
		}

		SendScheduleDetail newSsd = sendScheduleDetailEao.save(ssd);

		// 更新排程状态更新
		this.updateSSStatus(newSsd.getSendScheduleNo());

		// 同步更新订单明细
		this.updatePOD(old_detail, new_detail, newSsd.getPurchaseOrderDetailId());
	}

	/**
	 * 取消送货单
	 *
	 * @param id 送货单
	 * @return 取消后的送货单
	 */
	@Override
	public Delivery cancelDelivery(Long id) {
		Delivery model = dao.getById(id);
		if (model == null) {
			return null;
		}
		model.setStatus(DeliveryState.CANCEL);

		for (DeliveryDtl detail : model.getDeliveryDtls()) {
			if (detail.getDataFrom() == 1) {
				this.subPOD(detail, detail.getOrderDetailId());
			} else if (detail.getDataFrom() == 2) {
				this.subSSD(detail);
			}
			// 设置对应的送货明细取消标志为1
			detail.setCancelFlag("1");
		}
		model = dao.save(model);

		// 结束业务跟踪
		PortalParameters pp = new PortalParameters();
		pp.addPortalMethod(PortalMethodType.ST_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_ASN)
				.setBillId(model.getDeliveryId().toString()).setCreatorId(model.getCreateUserId().toString());
		// portalDealDataLogic.data4Portal(pp);
		return model;
	}

	/**
	 * 同步sap
	 *
	 * @param object 要同步的单据
	 */
	@Override
	public Delivery syncDelivery(Delivery delivery) {

		delivery = dao.getById(delivery.getDeliveryId());
		// 获取管控点配置
		Boolean flag = isSync(delivery);
		// 接口编码
		String interfaceCode = SrmConstants.SRM_DELIVERY_CODE;

		String params = setSyncParams(delivery);

		if (flag) {
			// 设置同步中状态
			delivery.setSynchronizeStatus(SrmSynStatus.SYNCHRONIZING);
			dao.save(delivery);

			// 调用RESTFul接口并获取返回值
			String json = null;
			JSONObject jsonObj = null;
			JSONObject data = null;
			String errorCode = null;
			String errorMessgae = null;
			WsRequestLog wrlog = new WsRequestLog();
			try {
				wrlog = wsRequestLogLogic.createTargetErpLog(interfaceCode, delivery.getDeliveryCode(), params);
				json = interactLogic.invoke("syncDeliveryNew", params);
				// 响应时间
				jsonObj = (JSONObject) JSONObject.parseObject(json);
				data = jsonObj.getJSONObject("data");
				errorCode = jsonObj.getString("errcode");
				errorMessgae = jsonObj.getString("errmsg");

				// 不等于0出现错误
				if (!"0".equals(errorCode)) {
					delivery.setSynchronizeStatus(SrmSynStatus.SYNFAILED);
					String msg = StringUtils.isNotBlank(errorMessgae) && errorMessgae.length() > 2000 ? errorMessgae.substring(0, 1999)
							: errorMessgae;
					delivery.setErpReturnMsg(msg);
					wsRequestLogLogic.addErrorLog(wrlog, json);
				} else {
					JSONObject returnItem = data.getJSONObject("EtReturn").getJSONObject("item");

					if ("S".equals(returnItem.getString("Type"))) {
						// 同步成功
						delivery.setSynchronizeStatus(SrmSynStatus.SYNSUCCESS);
						wsRequestLogLogic.addSuccessLog(wrlog, json);
					} else {
						// 同步失败
						delivery.setSynchronizeStatus(SrmSynStatus.SYNFAILED);
						String msg = data.toJSONString();
						msg = StringUtils.isNotBlank(msg) && msg.length() > 2000 ? msg.substring(0, 1999) : msg;
						delivery.setErpReturnMsg(msg);
						wsRequestLogLogic.addErrorLog(wrlog, json);
					}
				}
			} catch (Exception e) {
				// 同步失败
				delivery.setSynchronizeStatus(SrmSynStatus.SYNFAILED);
				String msg = StringUtils.isNotBlank(errorMessgae) && errorMessgae.length() > 2000 ? errorMessgae.substring(0, 1999)
						: errorMessgae;
				delivery.setErpReturnMsg(msg);
				wsRequestLogLogic.addFailLog(wrlog, json);
			}
		} else {
			// 同步成功
			delivery.setSynchronizeStatus(SrmSynStatus.SYNSUCCESS);
		}

		delivery = dao.save(delivery);
		return delivery;
	}

	/**
	 * 是否可以调用同步接口
	 * 
	 * @param delivery
	 * @return
	 */
	protected Boolean isSync(Delivery delivery) {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("po", delivery);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("map", hm);
		String obj = null;

		try {
			obj = (String) groovyScriptInvokerLogic.invoke("CP0501", map); //
			// 是否启用
		} catch (Exception e) {
			e.printStackTrace();
		}

		Boolean flag = false;

		if (obj.equals("1")) {
			// 是否调用同步接口
			flag = true;
		}

		return flag;
	}

	/**
	 * 设置同步sap数据对象
	 * 
	 * @param delivery 送货数据对象
	 * @return 返回同步sap的参数对象
	 */
	protected String setSyncParams(Delivery delivery) {

		// JSON数据
		DeliveryDto vo = new DeliveryDto();
		vo.setDeliveryCode(delivery.getDeliveryCode());
		vo.setStatus(delivery.getStatus().getIndex());
		vo.setStorageLocationCode(delivery.getStorageLocationCode());
		vo.setClientCode(delivery.getClientCode());

		List<DeliveryDtlDto> deliveryDtlVOs = new ArrayList<DeliveryDtlDto>();

		HashSet<String> purchaseOrderNoList = new HashSet<String>();
		for (DeliveryDtl dtl : delivery.getDeliveryDtls()) {
			if (StringUtils.isNotBlank(dtl.getPurchaseOrderCode()) && !purchaseOrderNoList.contains(dtl.getPurchaseOrderCode())) {
				purchaseOrderNoList.add(dtl.getPurchaseOrderCode());
			}
		}
		Map<String, Object> orderMap = new HashMap<String, Object>();
		if (purchaseOrderNoList != null && purchaseOrderNoList.size() > 0) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("IN_purchaseOrderNo", StringUtils.join(purchaseOrderNoList.toArray(), ","));
			List<PurchaseOrder> orders = purchaseOrderEao.findAll(param);
			if (orders != null && orders.size() > 0) {
				for (PurchaseOrder order : orders) {
					orderMap.put(order.getPurchaseOrderNo(), order.getErpPurchaseOrderNo());
				}
			}
		}

		for (DeliveryDtl dtl : delivery.getDeliveryDtls()) {
			DeliveryDtlDto dtlVo = new DeliveryDtlDto();
			dtlVo.setDeliveryDtlId(dtl.getDeliveryDtlId());
			dtlVo.setDeliveryNumber(dtl.getDeliveryNumber());
			dtlVo.setLineNumber(dtl.getLineNumber());
			dtlVo.setDeliveryCode(delivery.getDeliveryCode());
			String erpPurhaseOrderCode = (String) orderMap.get(dtl.getPurchaseOrderCode());
			dtlVo.setPurchaseOrderCode(StringUtils.isBlank(erpPurhaseOrderCode) ? dtl.getPurchaseOrderCode() : erpPurhaseOrderCode);
			deliveryDtlVOs.add(dtlVo);
		}

		vo.setDeliveryDtls(deliveryDtlVOs);
		// params.put("json", DataUtils.toJson(vo));
		return DataUtils.toJson(vo);
	}

	/**
	 * 取消送货明细
	 *
	 * @param id 要取消的明细id
	 */
	@Override
	public void cancelDetail(Long id) {
		DeliveryDtl detail = deliveryDtlEao.getById(id);
		// 只有还没有取消的送货单明细才能取消
		if (detail.getCancelFlag().equals("0")) { // 取消标识为“否”，“0”为否

			detail.setCancelFlag("1"); // 取消标识为“是”，“1”为是

			if (detail.getDataFrom() == 1) {
				this.subPOD(detail, detail.getOrderDetailId());
				this.updateDSCancel(detail.getDelivery());

			} else if (detail.getDataFrom() == 2) {
				this.subSSD(detail);
				// 更新送货单状态
				this.updateDSCancel(detail.getDelivery());
			}
		}
		deliveryDtlEao.save(detail);
	}

	/**
	 * 关闭送货明细
	 *
	 * @param id 要关闭的明细id
	 */
	@Override

	public void closeDetail(Long id) {
		DeliveryDtl detail = deliveryDtlEao.getById(id);
		// 只有还没有关闭的送货单明细才能关闭
		if (detail.getCloseFlag().equals("0")) { // 关闭标识为“否”，“0”为否
			if (detail.getDataFrom() == 1) {

				this.subPODClose(detail, detail.getOrderDetailId());
				// 更新送货单状态
				this.updateDSClose(detail.getDelivery());

			} else if (detail.getDataFrom() == 2) {

				this.subSSDClose(detail);
				// 更新送货单状态
				this.updateDSClose(detail.getDelivery());

			}
			detail.setCloseFlag("1"); // 关闭标识为“是”，“1”为是
			// 判断送货单的所有明细都已关闭，将送货单的状态置为收货完成
			List<DeliveryDtl> details = findDeliveryDetails(detail.getDelivery().getDeliveryId());
			boolean closeAll = true;
			for (DeliveryDtl snd : details) {
				if (detail.getDeliveryDtlId().equals(snd.getDeliveryDtlId())) {
					continue;
				}
				if (!"1".equals(snd.getCloseFlag())) {
					closeAll = false;
					break;
				}
			}
			if (closeAll) {
				detail.getDelivery().setStatus(DeliveryState.CLOSE);// 置为关闭
			}
			deliveryDtlEao.save(detail);
		}
	}

	/**
	 * 取消送货单明细 重置送货排程 送货量 在途量
	 *
	 * @param item 送货明细
	 */
	protected void subSSD(DeliveryDtl item) {

		// 找到对应的排程子明细
		SendScheduleDetail ssd = sendScheduleDetailEao.findById(item.getSendDetailId());

		// 送货量
		BigDecimal oldDeliveryQty = ssd.getDeliveryQty() == null ? BigDecimal.ZERO : ssd.getDeliveryQty();
		BigDecimal newDeliveryQty = oldDeliveryQty.subtract(item.getDeliveryNumber() == null ? BigDecimal.ZERO : item.getDeliveryNumber());
		ssd.setDeliveryQty(newDeliveryQty);

		// 在途量
		BigDecimal oldOnWayQty = ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty();
		BigDecimal newQtyOnline = oldOnWayQty.subtract(item.getDeliveryNumber() == null ? BigDecimal.ZERO : item.getDeliveryNumber());
		ssd.setOnWayQty(newQtyOnline);

		// 可送货量
		// BigDecimal oldCanSendQty = ssd.getCanSendQty();
		// if(oldCanSendQty != null){
		// 排程需求量 - 在途量 - 收货量 + 退货量
		if (ssd.getScheduleQty() != null) {
			BigDecimal newCanSendQty = ssd.getScheduleQty().subtract(ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty())
					.subtract(ssd.getReceiptQty() == null ? BigDecimal.ZERO : ssd.getReceiptQty())
					.add(ssd.getReturnGoodsQty() == null ? BigDecimal.ZERO : ssd.getReturnGoodsQty());
			ssd.setCanSendQty(newCanSendQty);
		}
		// }

		// 排程送货标识更新
		if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) == 0) {
			ssd.setSendFlag(0); // 如果可送货量等于需求量则 为未送货
		} else if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) > 0 && ssd.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {// 如果可送货量大于0并且小于需求量则
			ssd.setSendFlag(1); // 为部分送货
		} else if (ssd.getCanSendQty().compareTo(BigDecimal.ZERO) == 0 || ssd.getCanSendQty().compareTo(BigDecimal.ZERO) < 0) {
			ssd.setSendFlag(2); // 完全送货
		}

		SendScheduleDetail newSsd = sendScheduleDetailEao.save(ssd);

		// 更新排程状态更新
		this.updateSSStatus(newSsd.getSendScheduleNo());

		// 同步更新订单明细
		this.subPOD(item, newSsd.getPurchaseOrderDetailId());

	}

	/**
	 * 取消送货单细单 更新排程单的状态
	 *
	 * @param sendscheduleNo 排程单号
	 */
	protected void updateSSStatus(String sendscheduleNo) {
		// 获取全部的该排程单对应的排程子明细记录
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_sendScheduleNo", sendscheduleNo);
		// Specification<SendScheduleDetail> spc =
		// QueryUtils.newSpecification(searchParams);
		List<SendScheduleDetail> ssdList = sendScheduleDetailEao.findAll(searchParams);

		if (ssdList != null && ssdList.size() > 0) {
			// 如果明细中全部为完全送货这该排程单状态设置为完成
			boolean flag = true;
			for (SendScheduleDetail ssd : ssdList) {
				if (ssd.getSendFlag() != 2) {
					break;
				}
			}
			if (flag) {
				SendSchedule ssd = sendScheduleEao.findOne(searchParams);
				ssd.setSendScheduleState(SendScheduleState.OPEN);
				sendScheduleEao.save(ssd);
			}
		}
	}

	/**
	 * 取消送货单细单 更新送货单的状态
	 *
	 * @param delivery 送货单明细
	 *
	 */
	protected void updateDSCancel(Delivery delivery) {
		Map<String, Object> search = new HashMap<String, Object>();
		search.put("EQ_delivery_deliveryId", delivery.getDeliveryId());
		search.put("EQ_cancelFlag", "0");
		List<DeliveryDtl> ddList = deliveryDtlEao.findAll(search);
		// 送货单取消标识为否的送货明细的关闭标识都为是，则更新送货主单的状态为收货完成
		if (ddList.size() > 0) {
			boolean closeFlag = true;
			for (DeliveryDtl dd : ddList) {
				if (dd.getCloseFlag().equals("0")) // 取消标识为是
				{
					closeFlag = false;
					break;
				}
			}
			if (closeFlag == true)
				delivery.setStatus(DeliveryState.CLOSE);
		}

		boolean cancelFlag = true;
		// 送货单的送货明细的取消标识都为是，则更新送货主单的状态为取消
		for (DeliveryDtl detail : delivery.getDeliveryDtls()) {
			if (detail.getCancelFlag().equals("0")) {
				cancelFlag = false;
				break;
			}
		}
		if (cancelFlag == true) {
			delivery.setStatus(DeliveryState.CANCEL);

			// 结束业务跟踪
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.ST_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_ASN)
					.setBillId(delivery.getDeliveryId().toString()).setCreatorId(delivery.getCreateUserId().toString());
			// portalDealDataLogic.data4Portal(pp);
		}
		;
	}

	/**
	 * 关闭送货单细单 重置送货排程的细单
	 *
	 * @param detail 发货单
	 */
	protected void subSSDClose(DeliveryDtl detail) {
		// 找到对应的排程子明细
		SendScheduleDetail ssd = sendScheduleDetailEao.findById(detail.getSendDetailId());

		// 送货排程在途量
		BigDecimal oldQtyOnline = ssd.getOnWayQty() == null ? BigDecimal.ZERO : ssd.getOnWayQty();
		BigDecimal newQtyOnline = oldQtyOnline.add(detail.getReceivedNumber() == null ? BigDecimal.ZERO : detail.getReceivedNumber())
				.subtract(detail.getDeliveryNumber() == null ? BigDecimal.ZERO : detail.getDeliveryNumber());
		ssd.setOnWayQty(newQtyOnline);

		// 排程送货标识更新
		// 如果可送货量等于需求量则 为未送货
		if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) == 0) {
			ssd.setSendFlag(0);
		} else if (ssd.getScheduleQty().compareTo(ssd.getCanSendQty()) > 0 && ssd.getCanSendQty().compareTo(BigDecimal.ZERO) > 0) {// 如果可送货量大于0并且小于需求量则
			// 为部分送货
			ssd.setSendFlag(1);
		} else if (ssd.getCanSendQty().compareTo(BigDecimal.ZERO) == 0) {// 如果可送货量等于0为完全送货
			ssd.setSendFlag(2);
		}

		SendScheduleDetail newSsd = sendScheduleDetailEao.save(ssd);

		// 更新排程状态更新
		this.updateSSStatus(newSsd.getSendScheduleNo());

		// 同步更新订单明细
		this.subPODClose(detail, newSsd.getPurchaseOrderDetailId());
	}

	/**
	 * 关闭送货单细单 更新送货单的状态
	 *
	 * @param delivery 送货单明细
	 *
	 */
	protected void updateDSClose(Delivery delivery) {
		Map<String, Object> search = new HashMap<String, Object>();
		search.put("EQ_delivery_deliveryId", delivery.getDeliveryId());
		search.put("EQ_cancelFlag", "0");
		List<DeliveryDtl> ddList = deliveryDtlEao.findAll(search);
		if (ddList.size() > 0) {
			boolean flag = true;
			for (DeliveryDtl detail : ddList) {
				if (detail.getCloseFlag().equals("0")) {
					flag = false;
					break;
				}
			}
			if (flag == true) {
				delivery.setStatus(DeliveryState.CLOSE);
				// 结束业务跟踪
				PortalParameters pp = new PortalParameters();
				pp.addPortalMethod(PortalMethodType.ST_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_ASN)
						.setBillId(delivery.getDeliveryId().toString()).setCreatorId(delivery.getCreateUserId().toString());
				// portalDealDataLogic.data4Portal(pp);
			}
			;
		}
	}

	/**
	 * 删除送货单
	 * 
	 * @param ids 送货单ids
	 */
	@Override
	public void deleteDelivery(List<Long> ids) {
		// TODO Auto-generated method stub
		List<Delivery> dList = dao.findAllById(ids);
		for (Delivery delivery : dList) {
			for (DeliveryDtl detail : delivery.getDeliveryDtls()) {
				if (detail.getDataFrom() == 1) { // 来源订单明细
					this.subPOD(detail, detail.getOrderDetailId());
				} else if (detail.getDataFrom() == 2) {
					this.subSSD(detail);
				}
			}
			dao.delete(delivery);

			// 结束业务跟踪
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.ST_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_ASN)
					.setBillId(delivery.getDeliveryId().toString()).setCreatorId(delivery.getCreateUserId().toString());
			// portalDealDataLogic.data4Portal(pp);
		}
	}

	/**
	 * 日程提醒 调用
	 */
	protected void data4Schedule(Set<String> deliveryNos) {
		// 获取符合条件的：采购方重要提示 、供应商重要提示 数据
		// 当前用户数据权限下可查看的送货单+送货单状态为“待收货”+ 预计到达日期=当前时间
		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.MILLISECOND, 0);

		Map<String, Object> searchParams = new HashMap<String, Object>();

		searchParams.put("EQ_delivery_serviceDate", currentDate);
		searchParams.put("IN_delivery_status", Arrays.asList(DeliveryState.WAIT, DeliveryState.RECEIVING));

		List<DeliveryDtl> details = deliveryDtlEao.findAll(searchParams);
		for (DeliveryDtl detail : details) {
			// 供应日程
			List<User> users = findUserByVendor(detail.getDelivery().getVendorCode());
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.IW_ADD, null, null, users).setBillNo(detail.getDelivery().getDeliveryCode())
					.setBillId(detail.getDelivery().getDeliveryId().toString()).setBillTypeCode(SrmConstants.BILLTYPE_ASN);
			// portalDealDataLogic.data4Portal(pp);
			deliveryNos.add(detail.getDelivery().getDeliveryCode());
		}
	}

	/**
	 * 送货单定时器方法
	 */
	public Map<String, Object> deliveryJobMethod() {
		Map<String, Object> result = new HashMap<String, Object>();
		Set<String> orderNos = new HashSet<String>();
		// 日程
		data4Schedule(orderNos);

		// 参数拼接
		List<String> list = new ArrayList<String>(orderNos);
		result.put("successfulNos", list);
		result.put("failureNos", new ArrayList<String>());

		return result;
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
		FeignParam<User> fp = new FeignParam<User>(params);
		List<User> users = userLogic.findAll(fp);
		return users;
	}

	/**
	 * 设置日程方法参数
	 * 
	 * @param detail 明细
	 * @param receiver 接受者
	 * @return 返回方法参数
	 */
	protected Map<String, Object> data4ScheduleParam(DeliveryDtl detail, Object receiver) {
		Delivery entity = detail.getDelivery();
		Integer i = 1;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put((i++).toString(), SrmConstants.BILLTYPE_ASN);// 单据编码
		params.put((i++).toString(), entity.getDeliveryId().toString());// 单据Id
		params.put((i++).toString(), entity.getDeliveryCode());// 单据号
		params.put((i++).toString(), DataUtils.toJson(entity, FetchType.EAGER));// 单据信息（整条单据转成json）
		params.put((i++).toString(), null);// 接收者ID(发给采购，不能精确到个人的，传null) // TODO
											// 暂时不传
		params.put((i++).toString(), null);// 单据权限字段键值（该单据用户资源权限过滤的字段，以及该字段的值）
		params.put((i++).toString(), entity.getServiceDate());// 日期（对应需求文档中，作为判断的日期）
		params.put("methodName", "SCHEDULE_Y");// 方法标识
		return params;
	}

	/**
	 * 记录审核的操作日志:新
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
				.module(SrmConstants.BILLTYPE_ASN)// 设置日志模块
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

	@Override
	public String findDeliveryOne(Map<String, Object> searchParams) {
		if (!searchParams.isEmpty()) {
			List<Delivery> list = new ArrayList<Delivery>();
			list = deliveryLogic.findAll(searchParams);
			if (!list.isEmpty()) {
				Delivery de = list.get(0);
				JSONObject json = new JSONObject();
				json.put("deliveryId", de.getDeliveryId().toString());

				return DataUtils.toJson(json);
			}
		}
		return "{}";
	}

	/**
	 * 送货单点收,可以分批收货，全部收完，送货单状态为已完成（只有发布状态的送货单才可以点收） 需要更新送货单的已收货数量，对应采购订单明细的已收货数量
	 * 
	 * @param deliveryJson {Delivery对象json字符串,deliveryDtls明细对象字符串}
	 * @param userId 用户id
	 * @param userName 用户名称
	 * @return
	 */
	@Override
	public String receiving(String deliveryJson, Long userId, String userName) {
		try {
			Map<Boolean, String> result = new HashMap<Boolean, String>();
			result = receivingNoteService.getDelivery(deliveryJson, userId, userName);
			if (result.containsKey(true)) {
				return "{success:true}";
			} else {
				if ("shoppingNotice.message.orderClosedCannotReceiving".equals(result.get(false))) {
					return "{success:false,errorMsg:\"该采购订单号已经关闭无法进行收货！\"}";
				} else if ("shoppingNotice.message.receivedQtyCanNotBiggerThenDeliverQty".equals(result.get(false))) {
					return "{success:false,errorMsg:\"已收货量不能大于送货数量！\"}";
				} else {
					return "{success:false,errorMsg:" + result.get(false) + "}";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{success:false,errorMsg:" + e.getMessage() + "}";
		}
	}

	/**
	 * 查找送货单明细
	 * 
	 * @param purchasingOrderId 采购订单ID
	 * @return 订单明细
	 */
	public List<DeliveryDtl> findDeliveryDetails(Long deliveryId) {
		Map<String, Object> searchParams = new LinkedHashMap<String, Object>();
		searchParams.put("EQ_delivery_deliveryId", deliveryId);
		return deliveryDtlEao.findAll(searchParams);
	}

	/**
	 * 根据快递信息明细获取物流信息
	 * 
	 * @param deliveryExpressDtlId 快递信息明细id
	 * @return
	 */
	@Override
	public List<LogisticsDtlDtl> findLogisticsDtlDtlAll(Long deliveryExpressDtlId) {
		List<LogisticsDtlDtl> LogisticsDtlDtlList = new ArrayList<LogisticsDtlDtl>();
		if (deliveryExpressDtlId != null) {
			DeliveryExpressDtl deliveryExpressDtl = deliveryExpressDtlService.findById(deliveryExpressDtlId);
			// 已签收状态无需调接口
			if (deliveryExpressDtl.getState() == null || !ExpressState.RECEIVED.equals(deliveryExpressDtl.getState())) {
				LogisticsDtlDtlList = invokeExpressMethod(LogisticsDtlDtlList, deliveryExpressDtl);
			}
		}
		return LogisticsDtlDtlList;
	}

	/**
	 * @Description:调用快递100接口（实时查询接口）
	 * @param @param LogisticsDtlDtlList
	 * @param @param deliveryExpressDtl 参数
	 * @return void 返回类型
	 */
	protected List<LogisticsDtlDtl> invokeExpressMethod(List<LogisticsDtlDtl> LogisticsDtlDtlList, DeliveryExpressDtl deliveryExpressDtl) {
		String expressCompanyCode = deliveryExpressDtl.getExpressCompanyCode();// 快递公司编码
		String expressNo = deliveryExpressDtl.getExpressNo();// 快递单号
		if (StringUtils.isNotBlank(expressCompanyCode) && StringUtils.isNotBlank(expressNo)) {
			ExpressParamsDtlEntity expressParamsDtlEntity = new ExpressParamsDtlEntity();
			expressParamsDtlEntity.setCom(expressCompanyCode);// 快递公司编码
			expressParamsDtlEntity.setNum(expressNo);// 快递单号
			List<ExpressResultEntity> synQueryData = ExpressUtils.synQueryData(expressParamsDtlEntity);// 根据快递公司编码，快递号调用快递100接口获取物流信息
			if (synQueryData != null && synQueryData.size() > 0) {
				// 请求到数据后，如果最新数据和之前请求到的数据条数不一致，根据时间，找出不同的数据，重新更新
				List<Calendar> updateTimes = new ArrayList<Calendar>();
				if (deliveryExpressDtl.getLogisticsDtlDtls() != null && deliveryExpressDtl.getLogisticsDtlDtls().size() > 0) {
					for (LogisticsDtlDtl ldd : deliveryExpressDtl.getLogisticsDtlDtls()) {
						updateTimes.add(ldd.getUpdateTime());
					}
				}
				for (ExpressResultEntity expressResultEntity : synQueryData) {
					LogisticsDtlDtl logisticsDtlDtl = new LogisticsDtlDtl();
					logisticsDtlDtl.setUpdateTime(expressResultEntity.getUpdateTime());// 更新时间
					logisticsDtlDtl.setContant(expressResultEntity.getContext());// 物流信息内容
					logisticsDtlDtl.setDeliveryExpressDtl(deliveryExpressDtl);// 设置一对多的值
					if (expressResultEntity.getUpdateTime() != null && !updateTimes.contains(expressResultEntity.getUpdateTime())) {
						LogisticsDtlDtlList.add(logisticsDtlDtl);
					}
				}
				// 更新调用接口时间
				deliveryExpressDtl.setInvokeTime(Calendar.getInstance());
				// 快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态
				deliveryExpressDtl.setState(synQueryData.get(0).getState());
				deliveryExpressDtl.setLogisticsMessage(synQueryData.get(0).getMessage());// 接口返回消息
				deliveryExpressDtl.setLogisticsStatus(synQueryData.get(0).getStatus());// 接口返回状态
				deliveryExpressDtlService.save(deliveryExpressDtl);
			}
		}
		logisticsDtlDtlService.saveAll(LogisticsDtlDtlList);
		return LogisticsDtlDtlList;
	}

}