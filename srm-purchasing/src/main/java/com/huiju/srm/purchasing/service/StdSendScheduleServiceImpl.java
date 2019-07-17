package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.FetchType;
import javax.persistence.LockModeType;

import org.springframework.beans.factory.annotation.Autowired;

import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.bpm.support.annotation.BpmService;
import com.huiju.bpm.support.enums.ApprovalState;
import com.huiju.bpm.support.service.BpmSupport;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.jpa.utils.DataUtils;
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
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.purchasing.dao.PurchaseOrderDetailDao;
import com.huiju.srm.purchasing.dao.SendScheduleCommonDao;
import com.huiju.srm.purchasing.dao.SendScheduleDetailDao;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.SendSchedule;
import com.huiju.srm.purchasing.entity.SendScheduleCommon;
import com.huiju.srm.purchasing.entity.SendScheduleDetail;
import com.huiju.srm.purchasing.entity.SendScheduleState;
import com.huiju.srm.purchasing.util.SendScheduleConstant;
import com.huiju.srm.srmbpm.service.SrmBpmService;

/**
 * 送货排程 业务
 * 
 * @author CWQ date 2016-08-04 18:25:46
 */
@BpmService(billTypeCode = "PCD", billNoKey = "sendScheduleNo")
public class StdSendScheduleServiceImpl extends BpmSupport<SendSchedule, Long> implements StdSendScheduleService {

	@Autowired(required = false)
	protected SendScheduleDetailDao sendScheduleDetailEao;
	@Autowired(required = false)
	protected PurchaseOrderDetailDao purchaseOrderDetailEao;
	@Autowired(required = false)
	protected SendScheduleCommonDao sendScheduleCommonEao;
	@Autowired(required = false)
	protected PurchaseOrderService purchaseOrderLogic;
	@Autowired(required = false)
	protected PurchaseOrderDetailService purchaseOrderDetailLogic;
	@Autowired(required = false)
	protected PortalServiceClient portalDealDataLogic;
	@Autowired(required = false)
	protected UserClient userLogic;
	@Autowired(required = false)
	protected NotifySenderClient notifySenderLogic;
	@Autowired(required = false)
	protected BpmServiceClient bpmService;
	@Autowired
	protected SrmBpmService bpmSrmLogic;
	@Autowired(required = false)
	protected GroovyScriptInvokerService groovyScriptInvokerLogic;

	/**
	 * 获取送货排程管控点
	 * 
	 * @param entity 实体对象
	 * @param code 管控点Code
	 * @return 是否同步标识
	 * @throws Exception
	 */
	public String getSendScheduleControl(SendSchedule entity, String code) {
		try {
			Map<String, Object> poMap = new HashMap<String, Object>();
			poMap.put("po", entity);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("map", poMap);
			String value;

			value = (String) groovyScriptInvokerLogic.invoke(code, params);

			if (StringUtils.isBlank(value) || !value.equals(SendScheduleConstant.GROOVY_YES)) {
				return SendScheduleConstant.GROOVY_NO;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SendScheduleConstant.GROOVY_YES;

	}

	/**
	 * 获取权限
	 * 
	 * @param userId 用户ID
	 * @param roleType 角色类型
	 * @param forecastId 采购预测ID
	 * @return 事件集合
	 */
	@SuppressWarnings("incomplete-switch")
	public List<String> getSendScheduleEvents(Long userId, String roleType, Long id) {
		List<String> events = new ArrayList<String>();
		SendSchedule entity = dao.getById(id);
		// if(userId.equals(entity.getCreateUserId())){
		boolean isAuthoritiedToAuditing = bpmService.isAuthoritiedToAuditing(userId.toString(), SrmConstants.BILLTYPE_PCD, id.toString());
		SendScheduleState state = entity.getSendScheduleState();
		switch (state) {
		case NEW:
			events.add("TO" + SendScheduleState.CONFIRM.name());
			break;
		case NOPASS:
			events.add("TO" + SendScheduleState.CONFIRM.name());
			break;
		case CONFIRM:
			if (isAuthoritiedToAuditing) {
				events.add("TO" + SendScheduleState.PASS.name());
				events.add("TO" + SendScheduleState.NOPASS.name());
			}
			break;
		}
		// }
		return events;
	}

	/**
	 * 处理流程化
	 * 
	 * @param sendScheduleId 排程ID
	 * @param status 状态
	 * @param message 内容
	 * @return 排程
	 * @throws Exception
	 */
	public SendSchedule dealSendSchedule(Long sendScheduleId, String status, String message, Long userId) {
		SendSchedule entity = dao.getById(sendScheduleId);

		// 提交审核
		if (status.contains(SendScheduleState.CONFIRM.getName())) {
			// toComfirm(userId, entity);
			submitBpm(sendScheduleId, userId);
			// 审核不过
		} else if (status.contains(SendScheduleState.NOPASS.getName())) {
			// toNoPass(userId, entity, message);
			reject(sendScheduleId, userId, message);
			// 审核通过
		} else if (status.contains(SendScheduleState.PASS.getName())) {
			// toPass(userId, entity, message);
			approve(sendScheduleId, userId, message);
		}

		return entity;
	}

	/**
	 * 发送消息通知
	 * 
	 * @param entity 实体
	 * @param sendUserId 发送者ID
	 * @param receive 接收人
	 * @param messageCode 消息编码
	 */
	protected void sendNotify(SendSchedule entity, Long sendUserId, Long receiverId, String messageCode) {
		User sendUser = userLogic.findById(sendUserId);
		Map<String, Object> extraparams = new HashMap<String, Object>();
		extraparams.put("billNo", entity.getSendScheduleNo());
		extraparams.put("billId", entity.getSendScheduleId().toString());
		extraparams.put("billTypeName", "订单");
		extraparams.put("vendorCode", entity.getVendorCode());
		extraparams.put("vendorName", entity.getVendorName());
		extraparams.put("userName", sendUser.getUserName());
		extraparams.put("userName", sendUser.getUserCode());
		notifySenderLogic
				.send(new NotifyParam(entity.getClientCode(), sendUser.getUserId(), receiverId, messageCode, new String[] {}, extraparams));
	}

	/**
	 * 发送消息通知
	 * 
	 * @param entity 实体
	 * @param sendUserId 发送者ID
	 * @param receive 接收人
	 * @param messageCode 消息编码
	 */
	protected void sendNotify(SendSchedule entity, Long sendUserId, List<User> receivers, String messageCode) {
		User sendUser = userLogic.findById(sendUserId);
		Map<String, Object> extraparams = new HashMap<String, Object>();
		extraparams.put("billNo", entity.getSendScheduleNo());
		extraparams.put("billId", entity.getSendScheduleId().toString());
		extraparams.put("billTypeName", "订单");
		extraparams.put("vendorCode", entity.getVendorCode());
		extraparams.put("vendorName", entity.getVendorName());
		extraparams.put("userName", sendUser.getUserName());
		extraparams.put("userName", sendUser.getUserCode());
		notifySenderLogic.send(new NotifyParam(entity.getClientCode(), sendUser.getUserId(),
				DataUtils.fetchAsList(receivers, "userId", Long.class), messageCode, new String[] {}, extraparams));
	}

	/**
	 * 判断供应商是否需要确认
	 * 
	 * @param entity 实体
	 * @param procesKey 流程key
	 * @throws Exception
	 */
	protected boolean isVendorConfirm(SendSchedule entity) {
		// 审核完成
		if (SendScheduleState.RELEASE.equals(entity.getSendScheduleState())) {
			String code = SendScheduleConstant.GROOVY_VCONFIRM;
			// 变更排程单
			if (entity.getProcesKey().equals(SrmConstants.BILLTYPE_PCDCHANGE)) {
				code = SendScheduleConstant.GROOVY_CHANGEVCONFIRM;
			}

			String value = this.getSendScheduleControl(entity, code);
			// 供应商不进行确认
			if (!value.equals(SendScheduleConstant.GROOVY_YES)) {
				entity.setSendScheduleState(SendScheduleState.OPEN);
				entity = dao.save(entity);
				// 调用同步接口
				doSync(entity);
				return false;
			}
		}
		return true;
	}

	/**
	 * 供应商接受送货排程
	 * 
	 * @param entity 送货排程
	 * @return 保存后的排程 (non-Javadoc)
	 * @throws Exception
	 * @see com.harmony.srm.scm.logic.BaseSendScheduleService#acceptSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
	 */
	@Override
	public SendSchedule acceptSendSchedule(SendSchedule entity, Long sendId) {
		entity = dao.save(entity);
		PortalParameters pp = new PortalParameters();
		// @Message-PCD_ACCEPT 接受订单
		sendNotify(entity, sendId, entity.getCreateUserId(), "PCD_ACCEPT");

		// 流程结束跟踪
		// finish4ServiceTrace(entity);
		pp.addPortalMethod(PortalMethodType.ST_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
				.setBillId(entity.getSendScheduleId().toString()).setCreatorId(entity.getCreateUserId().toString());
		portalDealDataLogic.data4Portal(pp);
		entity = dao.getById(entity.getSendScheduleId());
		entity.getSendScheduleCommons().size();
		for (SendScheduleCommon comm : entity.getSendScheduleCommons()) {
			comm.getSendScheduleDetails().size();
		}
		// 结束我的代办
		// finish4ToDeal(entity, null);
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
				.setBillId(entity.getSendScheduleId().toString());
		portalDealDataLogic.data4Portal(pp);
		// 同步sap
		doSync(entity);
		return entity;
	}

	/**
	 * 提交后事件处理
	 */
	@Override
	protected SendSchedule afterBpmSubmit(SendSchedule entity, Long userId, List<User> assignees, Map<String, Object> properties) {
		entity = super.afterBpmSubmit(entity, userId, assignees, properties);
		entity.setSendScheduleState(SendScheduleState.CONFIRM);
		entity = save(entity);
		return entity;
	}

	/**
	 * 驳回后执行，用于修改单据状态等操作。<br>
	 * <b>注意：</b>驳回不会触发afterComplete事件。<br>
	 * <b>注意：</b>在此方法中修改entity属性，会直接反映到数据库且影响后续流程。
	 * 
	 * @param entity 实体
	 * @param userId 操作用户
	 * @param createUserId 单据的创建人ID，注意可能为空
	 * @param message 审核意见
	 * @param properties 额外属性
	 * @return
	 */
	@Override
	protected SendSchedule afterReject(SendSchedule entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		entity = super.afterReject(entity, userId, createUserId, message, properties);
		entity.setSendScheduleState(SendScheduleState.NOPASS);
		entity = save(entity);
		return entity;
	}

	/**
	 * 流程成功结束后执行，用于修改单据状态等操作。<br>
	 * <b>注意：</b>驳回不会触发此事件。<br>
	 * <b>注意：</b>在此方法中修改entity属性，会直接反映到数据库且影响后续流程。
	 * 
	 * @param entity 实体
	 * @param userId 操作用户
	 * @param message 最后一次审核意见
	 * @param properties 额外属性
	 * @return
	 */
	@Override
	protected SendSchedule afterComplete(SendSchedule entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		// 通知创建人
		entity = super.afterComplete(entity, userId, createUserId, message, properties);
		entity.setSendScheduleState(SendScheduleState.RELEASE);
		entity = save(entity);
		PortalParameters pp = new PortalParameters();
		// 排程单发布--发送提醒供应商
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_clientCode", entity.getClientCode());
		params.put("EQ_erpCode", entity.getVendorErpCode());
		FeignParam<User> feignParam = new FeignParam<User>(params);
		List<User> users = userLogic.findAll(feignParam);

		sendNotify(entity, entity.getCreateUserId(), users, "PCD_TORELEASE");

		// 判断供应商是否需要确认
		boolean isNeedVendorConfirm = isVendorConfirm(entity);

		if (isNeedVendorConfirm) {
			// 需要供应商确认-调用内门户接口确认跟踪
			// vendorConfirmServiceTrace(entity);
			pp.addPortalMethod(PortalMethodType.ST_ADD).setRemindInfoCode(null).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
					.setBillId(entity.getSendScheduleId().toString()).setBillNo(entity.getSendScheduleNo())
					.setCreatorId(entity.getCreateUserId().toString()).setInfo(DataUtils.toJson(entity, FetchType.EAGER))
					.setReceiverCode(entity.getVendorCode());
			portalDealDataLogic.data4Portal(pp);
			// 需要供应商确认-调用我的代办接口
			// String notifyCode = "TODEAL_PCD_PUBLISH";
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("1", entity.getCreateUserName());// 提交审核人
			paramsMap.put("2", entity.getSendScheduleNo());// 送货排程单号
			// data4ToDeal(entity, notifyCode, paramsMap, users);
			pp.addPortalMethod(PortalMethodType.TODEAL_ADD, "TODEAL_PCD_PUBLISH", paramsMap, users)// 我的待办业务
					.setHandlerId(String.valueOf(userId))// 当前的操作人
					.setInfo(DataUtils.toJson(entity, FetchType.EAGER)).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
					.setBillId(entity.getSendScheduleId().toString()).setBillNo(entity.getSendScheduleNo())
					.setSpoorerId(String.valueOf(userId));
			portalDealDataLogic.data4Portal(pp);

		} else {
			// 无须供方确认，设置状态为执行
			entity.setSendScheduleState(SendScheduleState.OPEN);
			// 结束我的代办
			// finish4ToDeal(entity, null);
			pp.addPortalMethod(PortalMethodType.TODEAL_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
					.setBillId(entity.getSendScheduleId().toString());
			portalDealDataLogic.data4Portal(pp);
		}
		return entity;
	}

	/**
	 * 自定义通知供应商待办消息
	 */
	@Override
	protected void customToDeal(SendSchedule entity, ApprovalState approvalState, Long userId, String... message) {
		super.customToDeal(entity, approvalState, userId);
		if (entity != null && SendScheduleState.RELEASE.equals(entity.getSendScheduleState())) {
			// 判断供应商是否需要确认
			boolean isNeedVendorConfirm = isVendorConfirm(entity);

			PortalParameters pp = new PortalParameters();
			if (isNeedVendorConfirm) {
				// 排程单发布--发送提醒供应商
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("EQ_clientCode", entity.getClientCode());
				params.put("EQ_erpCode", entity.getVendorErpCode());
				List<User> users = userLogic.findAllByParams(params);
				// 需要供应商确认-调用内门户接口确认跟踪
				// vendorConfirmServiceTrace(entity);
				pp.addPortalMethod(PortalMethodType.ST_ADD).setRemindInfoCode(null).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
						.setBillId(entity.getSendScheduleId().toString()).setBillNo(entity.getSendScheduleNo())
						.setCreatorId(entity.getCreateUserId().toString()).setInfo(DataUtils.toJson(entity, FetchType.EAGER))
						.setReceiverCode(entity.getVendorCode());
				portalDealDataLogic.data4Portal(pp);
				// 需要供应商确认-调用我的代办接口
				// String notifyCode = "TODEAL_PCD_PUBLISH";
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("1", entity.getCreateUserName());// 提交审核人
				paramsMap.put("2", entity.getSendScheduleNo());// 送货排程单号
				// data4ToDeal(entity, notifyCode, paramsMap, users);
				pp.addPortalMethod(PortalMethodType.TODEAL_ADD, "TODEAL_PCD_PUBLISH", paramsMap, users)// 我的待办业务
						.setHandlerId(String.valueOf(userId))// 当前的操作人
						.setInfo(DataUtils.toJson(entity, FetchType.EAGER)).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
						.setBillId(entity.getSendScheduleId().toString()).setBillNo(entity.getSendScheduleNo())
						.setSpoorerId(String.valueOf(userId));
				portalDealDataLogic.data4Portal(pp);

			} else {
				// 无须供方确认，设置状态为执行
				entity.setSendScheduleState(SendScheduleState.OPEN);
				// 结束我的代办
				// finish4ToDeal(entity, null);
				pp.addPortalMethod(PortalMethodType.TODEAL_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
						.setBillId(entity.getSendScheduleId().toString());
				portalDealDataLogic.data4Portal(pp);
			}
			String procesKey = SrmConstants.BILLTYPE_PCD;
			String processInstanceId = bpmService.getProcessInstanceId(procesKey, entity.getSendScheduleId().toString());// 流程实例id
			// 发送知会
			bpmSrmLogic.bpmNotify(entity.getCreateUserId(), SrmConstants.BILLTYPE_PCD, "送货排程", entity.getSendScheduleId().toString(),
					entity.getSendScheduleNo().toString(), processInstanceId);
		}
	}

	/**
	 * 同步sap
	 * 
	 * @param model送货排程
	 * @throws Exception
	 */
	@Override
	public Boolean doSync(SendSchedule model) {
		boolean result = true;
		String value = this.getSendScheduleControl(model, SendScheduleConstant.GROOVY_SYNC);

		// 不需要同步接口
		if (SendScheduleConstant.GROOVY_NO.equals(value)) {
			model.setErpSynState(1);
			dao.save(model);
			return true;
		}

		// 设置同步状态为同步中
		model.setErpSynState(2);
		dao.save(model);
		Map<Long, Long> idMap = new HashMap<Long, Long>();
		Map<Long, List<SendScheduleDetail>> listMap = new HashMap<Long, List<SendScheduleDetail>>();
		for (SendScheduleCommon ssc : model.getSendScheduleCommons()) {
			// 多条排程明细对应一条采购明细
			SendScheduleDetail ssd = ssc.getSendScheduleDetails().get(0);
			PurchaseOrderDetail pod = purchaseOrderDetailLogic.findById(ssd.getPurchaseOrderDetailId());
			// 多条订单明细对应一条订单
			idMap.put(pod.getPurchaseOrderDetailId(), pod.getPurchaseOrder().getPurchaseOrderId());
			listMap.put(pod.getPurchaseOrderDetailId(), ssc.getSendScheduleDetails());
		}
		for (Long id : idMap.keySet()) {
			// 同步采购订单
			result = purchaseOrderLogic.doSync(idMap.get(id), listMap.get(id), "");
		}
		if (result) {
			// 设置同步状态为同步成功
			model.setErpSynState(1);
			model = dao.save(model);
		} else {
			// 设置同步状态为同步失败
			model.setErpSynState(3);
			model = dao.save(model);
		}
		return result;
	}

	/**
	 * 获得明细列表
	 * 
	 * @param searchParam 查询载体
	 * @return 排程中间类集合
	 */
	public List<SendScheduleCommon> findSendScheduleCommon(Map<String, Object> searchParam) {
		return sendScheduleCommonEao.findAll(searchParam);
	}

	/**
	 * 取消排程
	 * 
	 * @param object排程
	 * @throws Exception
	 * @return排程 (non-Javadoc)
	 * @see com.harmony.srm.scm.logic.BaseSendScheduleService#cancelSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
	 */
	public SendSchedule cancelSendSchedule(SendSchedule entity) {
		entity = dao.getById(entity.getSendScheduleId());
		entity.setSendScheduleState(SendScheduleState.CANCEL);

		// 需要将该排程单中的所有订单明细的已排程量置为0
		List<SendScheduleCommon> sendScheduleCommons = entity.getSendScheduleCommons();

		for (SendScheduleCommon itemd : sendScheduleCommons) {
			PurchaseOrderDetail orderDetail = purchaseOrderDetailEao.getById(itemd.getPurchaseOrderDetailId());
			List<SendScheduleDetail> scheduleDetails = itemd.getSendScheduleDetails();
			// BigDecimal sendQty = new BigDecimal(0);
			BigDecimal schduleQty = new BigDecimal(0);
			for (SendScheduleDetail sd : scheduleDetails) {
				// sendQty = sendQty.add(sd.getSendQty());
				schduleQty = schduleQty.add(sd.getScheduleQty());
			}

			orderDetail.setScheduledQty(orderDetail.getScheduledQty().subtract(schduleQty));
			orderDetail.setUnScheduledQty(orderDetail.getUnScheduledQty().add(schduleQty));
			purchaseOrderDetailEao.save(orderDetail);
		}

		entity = dao.save(entity);// 重新保存对象

		// @Message-PCD_CANCEL 排程取消--发送提醒供应商
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_clientCode", entity.getClientCode());
		params.put("EQ_erpCode", entity.getVendorErpCode());
		List<User> users = userLogic.findAllByParams(params);
		sendNotify(entity, entity.getCreateUserId(), users, "PCD_CANCEL");

		entity = dao.getById(entity.getSendScheduleId());
		entity.getSendScheduleCommons().size();
		for (SendScheduleCommon comm : entity.getSendScheduleCommons()) {
			comm.getSendScheduleDetails().size();
		}
		doSync(entity);
		return entity;
	}

	/**
	 *
	 * 修改排程
	 * 
	 * @param object 排程
	 * @throws Exception
	 * @return排程 (non-Javadoc)
	 * @see com.harmony.srm.scm.logic.BaseSendScheduleService#saveSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
	 */
	@Override
	public SendSchedule saveSendSchedule(SendSchedule object, String submitFlag, Long userId, String userName) {
		// 修改后 每条明细对应的需求量
		Map<Long, BigDecimal> idMap = new HashMap<Long, BigDecimal>();

		for (SendScheduleCommon detail : object.getSendScheduleCommons()) {
			// 该条排程明细对应的新的需求量
			BigDecimal newSendScheduleQty = BigDecimal.ZERO;
			if (detail.getSendScheduleCommonId() == null) {
				for (SendScheduleDetail detailV : detail.getSendScheduleDetails()) {
					newSendScheduleQty = newSendScheduleQty.add(detailV.getScheduleQty());
				}
			} else {

				SendScheduleCommon scheduleCommon = sendScheduleCommonEao.findById(detail.getSendScheduleCommonId(), LockModeType.NONE);
				// 原排程明细量
				Map<Long, BigDecimal> oldDetailMap = new HashMap<Long, BigDecimal>();
				if (scheduleCommon != null && scheduleCommon.getSendScheduleDetails() != null
						&& scheduleCommon.getSendScheduleDetails().size() > 0) {
					for (SendScheduleDetail dtl : scheduleCommon.getSendScheduleDetails()) {
						oldDetailMap.put(dtl.getSendScheduleDetailId(), dtl.getScheduleQty());
					}
				}
				for (SendScheduleDetail detailV : detail.getSendScheduleDetails()) {
					// 合计新的需求量
					if (detailV.getScheduleQty() != null) {
						if (detailV.getSendScheduleDetailId() != null) {
							BigDecimal oldSsd = oldDetailMap.get(detailV.getSendScheduleDetailId());
							if (oldSsd != null) {
								BigDecimal diffValue = detailV.getScheduleQty().subtract(oldSsd);
								newSendScheduleQty = newSendScheduleQty.add(diffValue);
							}
							oldDetailMap.remove(detailV.getSendScheduleDetailId());
						} else {
							newSendScheduleQty = newSendScheduleQty.add(detailV.getScheduleQty());
						}
					}
				}
				if (!oldDetailMap.isEmpty()) {
					for (Long key : oldDetailMap.keySet()) {
						newSendScheduleQty = newSendScheduleQty.subtract(oldDetailMap.get(key));
					}
				}
			}

			// 满足如果业务为排程明细可以添加相同的订单明细
			if (idMap.containsKey(detail.getPurchaseOrderDetailId())) {
				BigDecimal old = idMap.get(detail.getPurchaseOrderDetailId());
				if (old != null) {
					old.add(newSendScheduleQty);
				}
			} else {
				idMap.put(detail.getPurchaseOrderDetailId(), newSendScheduleQty);
			}
		}

		// 修改对应订单明细的已排程量
		if (idMap != null && idMap.size() > 0) {
			for (Long podId : idMap.keySet()) {
				if (idMap.get(podId) != null) {
					PurchaseOrderDetail pod = purchaseOrderDetailLogic.findById(podId);
					// 重新已排程明细的已排程量 = 需求量
					if (pod.getVendorQty() != null) {
						// 已排程量
						BigDecimal scheduledQty = pod.getScheduledQty() == null ? BigDecimal.ZERO : pod.getScheduledQty();
						pod.setScheduledQty(scheduledQty.add(idMap.get(podId)));
						// 可排程量
						pod.setUnScheduledQty(pod.getVendorQty().subtract(pod.getScheduledQty()));
						purchaseOrderDetailEao.save(pod);
					}
				}
			}
		}

		object = dao.save(object);// 保存对象

		if ("save".equalsIgnoreCase(submitFlag)) {
			object.setSendScheduleState(SendScheduleState.NEW);
			// 增加操作日志
			addLog(userId, userName, object.getSendScheduleId(), "送货排程创建", SrmConstants.PERFORM_SAVE, object.getSendScheduleNo(),
					SrmConstants.PLATFORM_WEB);

		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			this.dealSendSchedule(object.getSendScheduleId(), "TOCONFIRM", "", userId);
			addLog(userId, userName, object.getSendScheduleId(), "送货排程提交", SrmConstants.PERFORM_AUDIT, object.getSendScheduleNo(),
					SrmConstants.PLATFORM_WEB);

		}

		return object;
	}

	/**
	 * 强制变更排程
	 * 
	 * @param entity 排程
	 * @throws Exception
	 * @return排程 (non-Javadoc)
	 * @see com.harmony.srm.scm.logic.BaseSendScheduleService#changeSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
	 */
	public SendSchedule changeSendSchedule(SendSchedule entity, Long userId, String userName) {
		SendSchedule newo = this.saveSendSchedule(entity, "audit", userId, userName);

		// @Message-PCD_CHANGE 变更排程
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_clientCode", entity.getClientCode());
		params.put("EQ_erpCode", entity.getVendorErpCode());
		List<User> users = userLogic.findAllByParams(params);
		sendNotify(entity, entity.getCreateUserId(), users, "PCD_CHANGE");

		// 同步到sap
		doSync(newo);
		return newo;
	}

	/**
	 * 删除排程
	 * 
	 * @param entity排程
	 * @return排程 (non-Javadoc)
	 * @see com.harmony.srm.scm.logic.BaseSendScheduleService#deleteSendSchedules(java.util.List)
	 */
	@Override
	public void deleteSendSchedules(List<Long> ids, String userId, String userName) {
		for (Long id : ids) {
			SendSchedule sendSchedule = dao.getById(id);
			for (SendScheduleCommon ssr : sendSchedule.getSendScheduleCommons()) {
				for (SendScheduleDetail ssd : ssr.getSendScheduleDetails()) {
					PurchaseOrderDetail orderDetail = purchaseOrderDetailEao.getById(ssd.getPurchaseOrderDetailId());
					// 更新采购订单明细的已排程数量
					orderDetail.setScheduledQty(orderDetail.getScheduledQty().subtract(ssd.getScheduleQty()));
					if (orderDetail.getUnScheduledQty() != null) {
						orderDetail.setUnScheduledQty(orderDetail.getUnScheduledQty().add(ssd.getScheduleQty()));
					} else {
						orderDetail.setUnScheduledQty(ssd.getSendQty().subtract(ssd.getScheduleQty()));
					}
					purchaseOrderDetailEao.save(orderDetail);
					addLog(Long.parseLong(userId), userName, sendSchedule.getSendScheduleId(),
							"送货排程明细" + orderDetail.getSrmRowids() + "行删除", SrmConstants.PERFORM_DELETE, sendSchedule.getSendScheduleNo(),
							SrmConstants.PLATFORM_WEB);
				}
			}
			if (sendSchedule.getSendScheduleState().equals(SendScheduleState.NOPASS)) {
				// finish4ToDeal(sendSchedule, null);
				PortalParameters pp = new PortalParameters();
				pp.addPortalMethod(PortalMethodType.TODEAL_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
						.setBillId(sendSchedule.getSendScheduleId().toString());
				portalDealDataLogic.data4Portal(pp);
			}
			dao.deleteById(id);
		}
	}

	/**
	 * 供应商拒绝送货排程
	 * 
	 * @param object 送货排程
	 * @return 保存后的排程 (non-Javadoc)
	 * @see com.harmony.srm.scm.logic.BaseSendScheduleService#refuseSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
	 */
	@Override
	public SendSchedule refuseSendSchedule(SendSchedule entity, Long sendId) {
		entity = dao.save(entity);
		PortalParameters pp = new PortalParameters();
		// @Message-PCD_REFUSE 拒绝订单
		sendNotify(entity, sendId, entity.getCreateUserId(), "PCD_REFUSE");

		// 流程结束跟踪
		// finish4ServiceTrace(entity);
		pp.addPortalMethod(PortalMethodType.ST_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
				.setBillId(entity.getSendScheduleId().toString()).setCreatorId(entity.getCreateUserId().toString());
		portalDealDataLogic.data4Portal(pp);

		// 结束我的代办
		// finish4ToDeal(entity, null);
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
				.setBillId(entity.getSendScheduleId().toString());
		portalDealDataLogic.data4Portal(pp);
		// 调用我的代办接口
		// String notifyCode = "TODEAL_PCD_REFUSE";
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("1", entity.getVendorName());// 供应商
		paramsMap.put("2", entity.getSendScheduleNo());// 送货排程单号

		// data4ToDeal(entity, notifyCode, paramsMap, entity.getCreateUserId());
		User user = userLogic.findById(entity.getCreateUserId());
		pp.addPortalMethod(PortalMethodType.TODEAL_ADD, "TODEAL_PCD_REFUSE", paramsMap, user.getUserCode())// 我的待办业务
				.setInfo(DataUtils.toJson(entity, FetchType.EAGER)).setBillTypeCode(SrmConstants.BILLTYPE_PCD)
				.setBillId(entity.getSendScheduleId().toString()).setBillNo(entity.getSendScheduleNo());
		// 执行调用内门户处理方法
		portalDealDataLogic.data4Portal(pp);

		return entity;
	}

	/**
	 * 获取待处理送货排程的数据ID
	 * 
	 * @return 排程ID集合
	 */
	public List<Long> findIdByStatus(Long userId) {
		// 获取该用户该流程所有待审核单据
		List<String> idsList = bpmService.getAllUncheckedKeys(userId.toString(), SrmConstants.BILLTYPE_PCD);
		List<Long> idsLong = new ArrayList<Long>();
		for (String id : idsList) {
			idsLong.add(Long.parseLong(id));
		}
		if (0 == idsLong.size()) {
			idsLong.add(0L);
		}

		// 查找创建者为登录用户且审核不过的单据
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("IN_sendScheduleState", Arrays.asList(SendScheduleState.NOPASS, SendScheduleState.REFUSE));
		searchMap.put("EQ_createUserId", userId);
		List<SendSchedule> list = dao.findAll(searchMap);
		for (SendSchedule ss : list) {
			idsLong.add(ss.getSendScheduleId());
		}
		return idsLong;
	}

	/**
	 * 查询订单明细
	 * 
	 * @return 订单明细集合
	 */
	public List<PurchaseOrderDetail> findOrderDtlAll(Long orderId) {
		List<PurchaseOrderDetail> detailList = new ArrayList<PurchaseOrderDetail>();
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("EQ_purchaseOrder_purchaseOrderId", orderId);
		List<PurchaseOrderDetail> list = purchaseOrderDetailEao.findAll(searchMap);
		for (PurchaseOrderDetail pod : list) {
			detailList.add(pod);
		}
		return detailList;
	}

	/**
	 * 记录审核的操作日志
	 * 
	 * @param userId 操作人ID
	 * @param userName 操作人姓名
	 * @param billPk 审核的单据类型
	 * @param message 消息
	 * @param action 动作
	 * 
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
				.module(SrmConstants.BILLTYPE_PCD)// 设置日志模块
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
	 * 撤销审批
	 * 
	 * @param id 单据Id
	 * @param userId 操作人Id
	 * @param userName 操作人名称
	 * @return
	 */
	@Override
	public String revokeAudit(Long id, Long userId, String userName) {
		try {
			SendSchedule entity = revokeBpmSubmit(id, userId, userName);
			entity.setSendScheduleState(SendScheduleState.NEW);// 新建状态
			save(entity);
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "notOperation";
		}
	}

	@Override
	public SendSchedule mergeSendSchedule(SendSchedule entity, String submitFlag, String userId, String userName) {
		SendSchedule old_object = dao.getById(entity.getSendScheduleId());
		if (old_object == null) {
			return null;
		}

		Map<Long, Long> idMap = new HashMap<Long, Long>();
		// 修改后 每条明细对应的需求量
		Map<Long, BigDecimal> scheduleQtyMap = new HashMap<Long, BigDecimal>();
		// 修改前 每条明细对应的需求量
		Map<Long, BigDecimal> oldScheduleQtyMap = new HashMap<Long, BigDecimal>();
		// 物料明细新的集合
		HashMap<Object, Object> newhm = new HashMap<Object, Object>();// 新的对象明细集合
		// 物料明细对应的配额明细新的集合
		HashMap<Object, Object> newhmv = new HashMap<Object, Object>();// 新的对象明细集合
		for (SendScheduleCommon detail : entity.getSendScheduleCommons()) {
			// 该条排程明细对应的新的需求量
			Double newSendScheduleQty = 0D;
			// 该条排程明细对应的旧的需求量
			Double oldSendScheduleQty = 0D;
			newhm.put(detail.getSendScheduleCommonId(), detail);
			for (SendScheduleDetail detailV : detail.getSendScheduleDetails()) {
				// 合计新的需求量
				if (detailV.getScheduleQty() != null) {
					newSendScheduleQty += detailV.getScheduleQty().doubleValue();
				}
				newhmv.put(detailV.getSendScheduleDetailId(), detailV);
				if (detailV.getSendScheduleDetailId() != null) {
					idMap.put(detail.getPurchaseOrderDetailId(), detailV.getSendScheduleDetailId());
				}
			}
			// 获取原排程明细
			if (detail.getSendScheduleCommonId() != null) {
				SendScheduleCommon ssc = sendScheduleCommonEao.getById(detail.getSendScheduleCommonId());
				for (SendScheduleDetail oldSsd : ssc.getSendScheduleDetails()) {
					// 计算排程明细的原需求量
					oldSendScheduleQty += oldSsd.getScheduleQty().doubleValue();
					oldScheduleQtyMap.put(detail.getPurchaseOrderDetailId(), new BigDecimal(oldSendScheduleQty));
				}
			} else {
				oldScheduleQtyMap.put(detail.getPurchaseOrderDetailId(), BigDecimal.ZERO);
			}

			// 满足如果业务为排程明细可以添加相同的订单明细
			Long orderDetailId = detail.getPurchaseOrderDetailId();
			if (scheduleQtyMap.containsKey(orderDetailId)) {
				BigDecimal old = scheduleQtyMap.get(orderDetailId);
				if (old != null) {
					BigDecimal newQty = old.add(new BigDecimal(newSendScheduleQty));
					scheduleQtyMap.put(orderDetailId, newQty);
				}
			} else {
				scheduleQtyMap.put(orderDetailId, new BigDecimal(newSendScheduleQty));
			}
		}

		// 删除物料明细对应供应商配额明细
		for (SendScheduleCommon detail : old_object.getSendScheduleCommons()) {
			// 如果删除同时回置已排程数量
			if (!newhm.containsKey(detail.getSendScheduleCommonId())) {
				sendScheduleCommonEao.deleteById(detail.getSendScheduleCommonId());// 在新中不存在的老的数据删除了
				// 该条排程明细对应的新的需求量
				Double newSendScheduleQty = 0D;
				for (SendScheduleDetail detailV : detail.getSendScheduleDetails()) {
					// 要删除的需求量
					newSendScheduleQty += detailV.getScheduleQty().doubleValue();
				}
				PurchaseOrderDetail pod = purchaseOrderDetailEao.getById(detail.getPurchaseOrderDetailId());
				// 重新赋值已排程明细的已排程量 = 需求量
				// 已排程量
				pod.setScheduledQty(BigDecimal.ZERO);
				// 可排程量
				pod.setUnScheduledQty(pod.getVendorQty());
				purchaseOrderDetailEao.save(pod);

			} else {//
				for (SendScheduleDetail detailV : detail.getSendScheduleDetails()) {
					if (!newhmv.containsKey(detailV.getSendScheduleDetailId())) {
						sendScheduleDetailEao.deleteById(detailV.getSendScheduleDetailId());
					}
				}
			}
		}
		// 修改对应订单明细的已排程量
		if (scheduleQtyMap != null && scheduleQtyMap.size() > 0) {
			for (Long podId : scheduleQtyMap.keySet()) {
				if (podId != null) {
					// 订单明细
					PurchaseOrderDetail pod = purchaseOrderDetailEao.getById(podId);
					// 可排程量=订单明细原可排程量+（原排程明细的已排程量-变更后已排程量（需求量））
					BigDecimal num = oldScheduleQtyMap.get(podId).subtract(scheduleQtyMap.get(podId));
					// 可排程量
					BigDecimal unScheduledQty = pod.getUnScheduledQty();
					// 可排程量为空 时，计算可排程量 = 订单数量 - 已排程量
					if (unScheduledQty == null) {
						unScheduledQty = pod.getVendorQty().subtract(pod.getScheduledQty());
					}
					pod.setUnScheduledQty(unScheduledQty.add(num));
					// 已排程量=变更后的需求量
					pod.setScheduledQty(pod.getScheduledQty().subtract(num));
					purchaseOrderDetailEao.save(pod);
				}
			}
		}

		entity = dao.save(entity);// 重新保存对象

		if ("save".equalsIgnoreCase(submitFlag)) {
			entity.setSendScheduleState(SendScheduleState.NEW);
			addLog(Long.parseLong(userId), userName, entity.getSendScheduleId(), "送货排程修改", SrmConstants.PERFORM_EDIT,
					entity.getSendScheduleNo(), SrmConstants.PLATFORM_WEB);

		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			this.dealSendSchedule(entity.getSendScheduleId(), "TOCONFIRM", "", entity.getCreateUserId());
			addLog(Long.parseLong(userId), userName, entity.getSendScheduleId(), "送货排程提交", SrmConstants.PERFORM_AUDIT,
					entity.getSendScheduleNo(), SrmConstants.PLATFORM_WEB);
		}
		return entity;
	}

}