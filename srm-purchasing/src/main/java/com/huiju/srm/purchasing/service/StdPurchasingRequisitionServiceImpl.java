package com.huiju.srm.purchasing.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.bpm.support.annotation.BpmService;
import com.huiju.bpm.support.service.BpmSupport;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.data.jpa.utils.QueryUtils;
import com.huiju.module.log.Level;
import com.huiju.module.log.Log;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.module.util.StringUtils;
import com.huiju.notify.api.NotifySenderClient;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.portal.dto.PortalMethodType;
import com.huiju.portal.dto.PortalParameters;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.masterdata.api.CompanyClient;
import com.huiju.srm.masterdata.entity.Company;
import com.huiju.srm.purchasing.dao.PurchasingRequisitionDao;
import com.huiju.srm.purchasing.dao.PurchasingRequisitionDtlDao;
import com.huiju.srm.purchasing.entity.PurchasingRequisition;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionCollection;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionDtl;
import com.huiju.srm.purchasing.entity.PurchasingRequisitionState;
import com.huiju.srm.srmbpm.service.SrmBpmService;

/**
 * 采购申请转单实现类
 * 
 * @author bairx
 *
 */
@BpmService(billTypeCode = "PR", billNoKey = "purchasingRequisitionNo")
public class StdPurchasingRequisitionServiceImpl extends BpmSupport<PurchasingRequisition, Long>
		implements StdPurchasingRequisitionService {

	@Autowired
	protected PurchasingRequisitionDtlDao purchasingRequisitionDtlDao;
	@Autowired
	protected PurchasingRequisitionDao purchasingRequisitionDao;
	@Autowired
	protected PurchasingRequisitionCollectionService purchasingRequisitionCollectionServiceImpl;

	@Autowired
	protected CompanyClient companyClient;
	@Autowired(required = false)
	protected UserClient userLogic;
	@Autowired
	protected BpmServiceClient bpmClient;

	@Autowired
	protected NotifySenderClient notifySenderLogic;
	@Autowired
	protected PortalServiceClient portalDealDataLogic;
	@Autowired
	protected SrmBpmService srmBpmService;

	/**
	 * 右键操作
	 * 
	 * @param userId 审核人id
	 * @param id 审核单据id
	 * @return
	 */
	@Override
	public List<String> getPurchasingRequisitionEvents(Long userId, Long id) {
		boolean isAuthoritiedToAuditing = bpmClient.isAuthoritiedToAuditing(userId.toString(), SrmConstants.BILLTYPE_PR, id.toString());
		List<String> events = new ArrayList<String>();
		PurchasingRequisition po = purchasingRequisitionDao.getOne(id);
		if (po == null) {
			return events;
		}
		if (PurchasingRequisitionState.NEW.equals(po.getStatus()) || PurchasingRequisitionState.TONOPASS.equals(po.getStatus())) {
			events.add(PurchasingRequisitionState.TOCONFIRM.name());
		} else if (PurchasingRequisitionState.TOCONFIRM.equals(po.getStatus())) {
			if (isAuthoritiedToAuditing) {
				events.add(PurchasingRequisitionState.TONOPASS.name());
				events.add(PurchasingRequisitionState.TOPASS.name());
			}
		}
		return events;
	}

	/**
	 * 提交后事件处理
	 */
	@Override
	protected PurchasingRequisition afterBpmSubmit(PurchasingRequisition entity, Long userId, List<User> assignees,
			Map<String, Object> properties) {
		entity = super.afterBpmSubmit(entity, userId, assignees, properties);
		entity.setStatus(PurchasingRequisitionState.TOCONFIRM);
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
	protected PurchasingRequisition afterReject(PurchasingRequisition entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		entity = super.afterReject(entity, userId, createUserId, message, properties);
		entity.setStatus(PurchasingRequisitionState.TONOPASS);
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
	protected PurchasingRequisition afterComplete(PurchasingRequisition entity, Long userId, Long createUserId, String message,
			Map<String, Object> properties) {
		// 通知创建人
		entity = super.afterComplete(entity, userId, createUserId, message, properties);
		entity.setStatus(PurchasingRequisitionState.TOPASS);
		entity = save(entity);
		// 同步更新采购收集明细
		updateToPrc(entity);
		return entity;
	}

	/**
	 * 处理状态
	 * 
	 * @param userId 登录用户Id
	 * @param userName 登录用户名称
	 * @param id 单据Id
	 * @param status 单据状态
	 * @param message 审核意见
	 * @return
	 */
	@Override
	public PurchasingRequisition dealStatus(Long userId, String userName, Long materialFrozenPriceId, PurchasingRequisitionState status,
			String message) {
		PurchasingRequisition entity = purchasingRequisitionDao.getOne(materialFrozenPriceId);
		if (entity == null) {
			return null;
		}
		switch (status) {
		case NEW:
			break;
		case TOCONFIRM:
			if (entity.getStatus().equals(PurchasingRequisitionState.NEW)
					|| entity.getStatus().equals(PurchasingRequisitionState.TONOPASS)) {
				submitBpm(entity.getPurchasingRequisitionId(), userId);
			} else {
				return null;
			}
			break;
		case TOPASS:
			if (entity.getStatus().equals(PurchasingRequisitionState.TOCONFIRM)) {
				entity = approve(entity.getPurchasingRequisitionId(), userId, message);
				String processInstanceId = bpmClient.getProcessInstanceId(SrmConstants.BILLTYPE_PR,
						entity.getPurchasingRequisitionId().toString());// 流程实例id
				// 发送知会
				srmBpmService.bpmNotify(entity.getCreateUserId(), SrmConstants.BILLTYPE_PR, "采购申请",
						entity.getPurchasingRequisitionId().toString(), entity.getPurchasingRequisitionNo().toString(), processInstanceId);
			} else {
				return null;
			}
			break;
		case TONOPASS:
			if (entity.getStatus().equals(PurchasingRequisitionState.TOCONFIRM)) {
				reject(entity.getPurchasingRequisitionId(), userId, message);
			} else {
				return null;
			}
			break;
		default:
			break;
		}
		return entity;
	}

	/**
	 * 处理状态
	 * 
	 * @param userId 登录用户Id
	 * @param userName 登录用户名称
	 * @param id 单据Id
	 * @param status 单据状态
	 * @param message 审核意见
	 * @return
	 */
	@Override
	public PurchasingRequisition dealStatus(Long userId, String userName, Long materialFrozenPriceId, PurchasingRequisitionState status,
			String message, Boolean isAdd) {
		PurchasingRequisition entity = purchasingRequisitionDao.getOne(materialFrozenPriceId);
		if (entity == null) {
			return null;
		}
		switch (status) {
		case NEW:
			break;
		case TOCONFIRM:
			if (entity.getStatus().equals(PurchasingRequisitionState.NEW)
					|| entity.getStatus().equals(PurchasingRequisitionState.TONOPASS)) {
				submitBpm(entity.getPurchasingRequisitionId(), userId);
			} else {
				return null;
			}
			break;
		case TOPASS:
			if (entity.getStatus().equals(PurchasingRequisitionState.TOCONFIRM)) {
				entity = approve(entity.getPurchasingRequisitionId(), userId, message);
				String processInstanceId = bpmClient.getProcessInstanceId(SrmConstants.BILLTYPE_PR,
						entity.getPurchasingRequisitionId().toString());// 流程实例id
				// 发送知会
				srmBpmService.bpmNotify(entity.getCreateUserId(), SrmConstants.BILLTYPE_PR, "采购申请",
						entity.getPurchasingRequisitionId().toString(), entity.getPurchasingRequisitionNo().toString(), processInstanceId);

			} else {
				return null;
			}
			break;
		case TONOPASS:
			if (entity.getStatus().equals(PurchasingRequisitionState.TOCONFIRM)) {
				reject(entity.getPurchasingRequisitionId(), userId, message);
			} else {
				return null;
			}
			break;
		default:
			break;
		}
		return entity;
	}

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
		LogMessage.create(log).type(LogType.OPERATION).level(Level.INFO)// 设置日志级别
				.module(SrmConstants.BILLTYPE_PR)// 设置日志模块
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
	 * 查询明细
	 * 
	 * @param searchParams 查询条件
	 * @param orderStr 排序字段和排序方向
	 * @return 符合添加的明细集合
	 */
	@Override
	public List<PurchasingRequisitionDtl> findPurchasingRequisitionDtlAll(Map<String, Object> searchParams, String orderStr) {
		Specification<PurchasingRequisitionDtl> spec = QueryUtils.newSpecification(searchParams);
		return purchasingRequisitionDtlDao.findAll(spec);
	}

	/**
	 * 同步到采购归集明细
	 * 
	 * @param entity 已发布的采购申请单
	 */
	protected void updateToPrc(PurchasingRequisition entity) {
		List<PurchasingRequisitionDtl> prdList = entity.getPurchasingRequisitionDtls();
		for (PurchasingRequisitionDtl prd : prdList) {
			PurchasingRequisitionCollection prc = new PurchasingRequisitionCollection();
			prc.setSource("1");// 来源srm
			prc.setDemandDate(prd.getDemandDate());// 需求日期
			prc.setMaterialCode(prd.getMaterialCode());// 物料编码
			prc.setMaterialName(prd.getMaterialName());// 物料名称
			prc.setPlantCode(prd.getPlantCode());// 工厂编码，
			prc.setPlantName(prd.getPlantName());// 工厂名称
			prc.setPurchasingGroupCode(prd.getPurchasingGroupCode());// 采购组编码
			prc.setPurchasingGroupName(prd.getPurchasingGroupName());// 采购组名称
			prc.setPurchasingRequisitionNo(entity.getPurchasingRequisitionNo());// 采购申请单号
			prc.setQuantityDemanded(prd.getQuantityDemanded());// 需求数量
			prc.setRemark(prd.getRemark());// 备注
			prc.setRowNo(prd.getRowNo());// 行号
			prc.setUnitCode(prd.getUnitCode());// 单位编码‘
			prc.setUnitName(prd.getUnitName());// 单位名称
			prc.setTransferQuantity(prd.getQuantityDemanded());// 可转数量
			prc.setCanTransferQuantity(prd.getQuantityDemanded());// 可转单数量
			prc.setTransferedQuantity(BigDecimal.ZERO); // 已转数量
			prc.setCompanyCode(entity.getCompanyCode());
			prc.setCompanyName(entity.getCompanyName());
			prc.setApplicantCode(entity.getApplicantCode());
			prc.setApplicantName(entity.getApplicantName());
			prc.setCreateTime(entity.getApplicantTime());// 赋值主单的申请时间到明细归集的创建时间
			if (StringUtils.isNotBlank(prc.getMaterialCode())) {
				prc.setConfigState("0");// 分配状态
			}
			purchasingRequisitionCollectionServiceImpl.save(prc);
		}
	}

	/**
	 * 批量删除
	 * 
	 * @param ids 单据id
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param message 原因
	 */
	@Override
	public void removeByIds(List<Long> ids, Long userId, String userName, String message) {
		for (Long id : ids) {
			PurchasingRequisition entity = findById(id);
			if (entity.getStatus().equals(PurchasingRequisitionState.TONOPASS)) {
				PortalParameters pp = new PortalParameters();
				pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
				pp.setBillTypeCode(SrmConstants.BILLTYPE_PR).setBillId(entity.getPurchasingRequisitionId().toString());
				portalDealDataLogic.data4Portal(pp);
			}
			deleteById(id);
			purchasingRequisitionCollectionServiceImpl.removeByNo(entity.getPurchasingRequisitionNo(), null);
			addLog(userId, userName, entity.getPurchasingRequisitionId(), "采购申请删除,原因:" + message, SrmConstants.PERFORM_DELETE,
					entity.getPurchasingRequisitionNo(), SrmConstants.PLATFORM_WEB);

		}
	}

	/**
	 * 获取采购待处理的数据id
	 * 
	 * @param userId 用户ID
	 * @return 创建者为登录用户且审核不过的单据ID
	 */
	public List<Long> findIdByStatus(Long userId) {
		// 获取该用户该流程所有待审核单据
		List<String> idsList = bpmClient.getAllUncheckedKeys(userId.toString(), SrmConstants.BILLTYPE_PR);
		List<Long> idsLong = new ArrayList<Long>();
		for (String id : idsList) {
			idsLong.add(Long.parseLong(id));
		}
		if (0 == idsLong.size()) {
			idsLong.add(0L);
		}

		// 查找创建者为登录用户且审核不过的单据
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("EQ_status", PurchasingRequisitionState.TONOPASS);
		searchMap.put("EQ_createUserId", userId);
		Specification<PurchasingRequisition> spec = QueryUtils.newSpecification(searchMap);
		List<PurchasingRequisition> list = purchasingRequisitionDao.findAll(spec);
		for (PurchasingRequisition pr : list) {
			idsLong.add(pr.getPurchasingRequisitionId());
		}
		return idsLong;
	}

	@Override
	public String revokeAudit(Long id, Long userId, String userName) {
		try {
			PurchasingRequisition purchasingRequisition = revokeBpmSubmit(id, userId, userName);
			// 新建状态
			purchasingRequisition.setStatus(PurchasingRequisitionState.NEW);
			save(purchasingRequisition);
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "notOperation";
		}

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
		// notifySender.send("800", userId, userList, "BILL_REVOKEAUDIT", new
		// String[] {}, notifyParams);

	}

	/******************************** APP部分 ***********************************/
	/**
	 * 获取公司JSON
	 * 
	 * @param 数据权限过滤条件
	 * @param 排除的表
	 * @return 公司JSON
	 */
	public String findCompanyJson(Map<String, Object> searchMap, String[] excludes) {
		FeignParam<Company> params = new FeignParam<>();
		params.setParams(searchMap);
		List<Company> companys = companyClient.findAll(params);
		return DataUtils.toJson(companys, excludes);
	}

	/**
	 * 关闭
	 * 
	 * @param id 单据id
	 * @param userId 用户ID
	 * @param userName 用户名称
	 */
	@Override
	public void close(Long id, Long userId, String userName) {
		PurchasingRequisition model = findById(id);
		// 只有状态为发布 的采购申请才允许被关闭
		if (PurchasingRequisitionState.TOPASS.equals(model.getStatus())) {
			model.setStatus(PurchasingRequisitionState.CLOSE);
			save(model);
			purchasingRequisitionCollectionServiceImpl.removeByNo(model.getPurchasingRequisitionNo(), null);

			addLog(userId, userName, model.getPurchasingRequisitionId(), "采购申请关闭", SrmConstants.PERFORM_TOCLOSE,
					model.getPurchasingRequisitionNo(), SrmConstants.PLATFORM_WEB);
		}

	}

	/**
	 * 取消
	 * 
	 * @param id 单据id
	 * @param userId 用户ID
	 * @param userName 用户名称
	 */
	@Override
	public void cancel(Long id, Long userId, String userName) {
		PurchasingRequisition model = findById(id);
		// 只有状态为发布 的采购申请才允许被关闭

		model.setStatus(PurchasingRequisitionState.CANCEL);
		save(model);
		purchasingRequisitionCollectionServiceImpl.removeByNo(model.getPurchasingRequisitionNo(), null);

		addLog(userId, userName, model.getPurchasingRequisitionId(), "采购申请取消", SrmConstants.PERFORM_TOCANCEL,
				model.getPurchasingRequisitionNo(), SrmConstants.PLATFORM_WEB);

	}
}
