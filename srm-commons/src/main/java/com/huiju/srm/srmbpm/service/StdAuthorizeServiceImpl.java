package com.huiju.srm.srmbpm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.FetchType;

import org.springframework.beans.factory.annotation.Autowired;

import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.bpm.support.annotation.BpmService;
import com.huiju.bpm.support.service.BpmSupport;
import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.module.config.GlobalParameters;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.log.Level;
import com.huiju.module.log.Log;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.module.util.StringUtils;
import com.huiju.notify.api.NotifySenderClient;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.srm.commons.utils.CommonUtil;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.srmbpm.entity.Authorize;
import com.huiju.srm.srmbpm.entity.AuthorizeState;

/**
 * 授权单逻辑实现类
 * 
 * @author hongwl
 *
 * @date 2019年4月16日
 */
@BpmService(billTypeCode = "AU", billNoKey = "authorizeNo")
public class StdAuthorizeServiceImpl extends BpmSupport<Authorize, Long> implements StdAuthorizeService {

	@Autowired
	protected BpmServiceClient bpmServiceClient;
	protected final static String billPK = "AU";
	@Autowired
	protected UserClient userLogic;
	@Autowired
	protected PortalServiceClient portalDealDataLogic; 
	@Autowired
	protected NotifySenderClient notifySenderLogic;
	@Autowired
	protected UserAuthGroupServiceClient userAuthGroupLogic;
	@Autowired
	protected SrmBpmService srmBpmService;

	/**
	 * 获取右键事件
	 * 
	 * @param userId
	 * @param roleType
	 * @param projectId
	 * @return
	 */
	@Override
	public List<String> getEvents(Long userId, String roleType, Long authorizeId) {
		// 当前用户 是否有审核权限
		boolean isAuthoritiedToAuditing = bpmServiceClient.isAuthoritiedToAuditing(userId.toString(), billPK, authorizeId.toString());
		List<String> events = new ArrayList<String>();
		Authorize entity = findById(authorizeId);
		if (entity == null) {
			return events;
		}
		AuthorizeState status = entity.getStatus();
		if (AuthorizeState.NEW.equals(status) || AuthorizeState.TONOPASS.equals(status)) {
			events.add(AuthorizeState.TOCONFIRM.name());
		} else if (AuthorizeState.TOCONFIRM.equals(status) && isAuthoritiedToAuditing) {
			events.add(AuthorizeState.TOPASS.name());
			events.add(AuthorizeState.TONOPASS.name());
		}

		/*
		 * switch (status) { case NEW:
		 * events.add(AuthorizeState.toconfirm.name()); break; case toconfirm:
		 * if (isAuthoritiedToAuditing) {
		 * events.add(AuthorizeState.topass.name());
		 * events.add(AuthorizeState.tonopass.name()); } break; case tonopass:
		 * events.add(AuthorizeState.toconfirm.name()); break; default: break; }
		 */

		return events;
	}

	/**
	 * 处理事件
	 * 
	 * @param userId
	 * @param userName
	 * @param projectId
	 * @param status
	 * @param message
	 * @return
	 */
	@Override
	public Authorize dealStatus(Long userId, String userName, Long authorizeId, AuthorizeState status, String message) {
		Authorize entity = findById(authorizeId);
		if (entity == null) {
			return null;
		}
		String oldStatus = entity.getStatus().getStateDesc();
		switch (status) {
		case NEW:
			break;
		case TOCONFIRM:
			if (entity.getStatus().equals(AuthorizeState.NEW) || entity.getStatus().equals(AuthorizeState.TONOPASS)) {
				// toConfirm(entity, userId);
				submitBpm(entity.getAuthorizeId(), userId);
				// 记录日志
				/*
				 * addLog(userId, userName, authorizeId, "授权单提交",
				 * SrmConstants.PERFORM_AUDIT, entity.getAuthorizeNo(),
				 * SrmConstants.PLATFORM_WEB);
				 */
			} else {
				return null;
			}
			break;
		case TOPASS:
			if (entity.getStatus().equals(AuthorizeState.TOCONFIRM)) {
				// toPass(entity, userId, message);
				approve(entity.getAuthorizeId(), userId, message);
			} else {
				return null;
			}
			break;
		case TONOPASS:
			if (entity.getStatus().equals(AuthorizeState.TOCONFIRM)) {
				// toNoPass(entity, userId, message);
				reject(entity.getAuthorizeId(), userId, message);
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
	 * 提交审核后执行，用于修改单据状态等操作，默认执行发送消息通知以及待办事项操作。必须在此方法中编写单据状态变化逻辑。<br>
	 * <b>注意：</b>如果仅需要增加其他操作而不改变消息通知及待办事项操作，可在重写的方法中，第一行位置使用<b>super.afterBpmSubmit(entity,
	 * userId, assignees, properties)</b>而不必重写整个方法。<br>
	 * <b>注意：</b>在此方法中修改entity属性，会直接反映到数据库且影响后续流程。
	 * 
	 * @param entity 实体
	 * @param userId 操作用户ID
	 * @param assignees 审核用户列表
	 * @param properties 提交到工作流的额外属性
	 * @return
	 */
	protected Authorize afterBpmSubmit(Authorize entity, Long userId, List<User> assignees, Map<String, Object> properties) {
		entity = super.afterBpmSubmit(entity, userId, assignees, properties);
		entity.setStatus(AuthorizeState.TOCONFIRM);
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
	protected Authorize afterReject(Authorize entity, Long userId, Long createUserId, String message, Map<String, Object> properties) {
		entity = super.afterReject(entity, userId, createUserId, message, properties);
		entity.setStatus(AuthorizeState.TONOPASS);
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
	protected Authorize afterComplete(Authorize entity, Long userId, Long createUserId, String message, Map<String, Object> properties) {
		// 通知创建人
		entity = super.afterComplete(entity, userId, createUserId, message, properties);
		entity.setStatus(AuthorizeState.TOPASS);
		entity = save(entity);
		return entity;
	}

	/**
	 * 审核不过
	 * 
	 * @param entity
	 * @param userId
	 * @param message
	 */
	private void toNoPass(Authorize entity, Long userId, String message) {
		String notifyCode = "AU_TONOPASS";
		User sender = userLogic.findById(userId);
		AuthorizeState oldStatus = entity.getStatus();
		bpmServiceClient.toNoPass(userId.toString(), billPK, entity.getAuthorizeId().toString(), CommonUtil.toMap(entity, new String[] {}),
				message);
		entity.setStatus(AuthorizeState.TONOPASS);

		// 强制结束流程并清除流程信息，用于处理流程定义错误等异常导致流程无法继续流转的情况
		// BpmServiceClient.forceTerminate(billPK,
		// entity.getAuthorizeId().toString());// modify by xfwen 20180806

		// 审核不过 提醒
		sendNotify(entity, userId, entity.getAuthorizePersonId(), "AU_TONOPASS");

		// 待办审核不过
		List<Map<String, Object>> mapLists = new ArrayList<Map<String, Object>>();
		List<User> users = new ArrayList<User>();
		User createUser = userLogic.findById(entity.getAuthorizePersonId());
		users.add(createUser);
		mapLists.addAll(data4ToDealParam(entity, notifyCode, users, userId));
		// 调用内门户接口
		data4PortalParam(mapLists);

		// 记录日志
		/*
		 * addLog(userId, sender.getUserName(), entity.getAuthorizeId(),
		 * "授权单审核不过", SrmConstants.PERFORM_TONOPASS, entity.getAuthorizeNo(),
		 * SrmConstants.PLATFORM_WEB);
		 */

	}

	/**
	 * 我的待办 TD_Y
	 */
	private List<Map<String, Object>> data4ToDealParam(Authorize entity, String notifyCode, List<User> users, Long userId) {
		List<Map<String, Object>> mapLists = new ArrayList<Map<String, Object>>();
		// 先完成待办
		mapLists.add(finish4ToDealParam(entity, null));

		Map<String, Object> notityParams = setData4ToDealParams(entity, notifyCode, userId);

		Integer i = 1;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put((i++).toString(), SrmConstants.BILLTYPE_AU);// 单据编码
		params.put((i++).toString(), entity.getAuthorizeId().toString());// 单据Id
		params.put((i++).toString(), entity.getAuthorizeNo());// 单据号
		params.put((i++).toString(), notifyCode);// 消息模版Code
		params.put((i++).toString(), notityParams);// 消息模版params
		params.put((i++).toString(), users);// 接收人
		params.put((i++).toString(), DataUtils.toJson(entity, FetchType.EAGER));// 单据信息
																				// (json字符串)
		// params.put("methodName", Constant.TD_Y);// 方法标识
		mapLists.add(params);
		return mapLists;
	}

	/**
	 * 完成我的待办
	 * 
	 * @param entity
	 * @return
	 */
	private Map<String, Object> finish4ToDealParam(Authorize entity, Long receiverId) {
		Integer i = 1;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put((i++).toString(), SrmConstants.BILLTYPE_AU);// 单据编码
		params.put((i++).toString(), entity.getAuthorizeId().toString());// 单据Id
		params.put((i++).toString(), receiverId);// 接收者
		// params.put("methodName", Constant.TD_N);// 方法标识
		return params;
	}

	/**
	 * 设置待办参数
	 * 
	 * @return
	 */
	private Map<String, Object> setData4ToDealParams(Authorize entity, String notifyCode, Long userId) {
		Map<String, Object> notityParams = new HashMap<String, Object>();
		Integer i = 0;

		User user = null;
		if (null != userId) {
			user = userLogic.findById(userId);
		}

		if ("AU_TONOPASS".equals(notifyCode)) {
			notityParams.put((i++).toString(), entity.getAuthorizeNo());
		} else if ("AU_CONFIRM".equals(notifyCode)) {
			notityParams.put((i++).toString(), entity.getAuthorizePersonName());
			notityParams.put((i++).toString(), entity.getAuthorizeNo());
		}
		return notityParams;
	}

	/**
	 * 内门户接口调用
	 * 
	 * @param methodNames方法标识名称
	 * @param entity 实体对象
	 */
	public void data4PortalParam(Map<String, Object> map) {
		Map<String, Map<String, Object>> maps = new HashMap<String, Map<String, Object>>();
		String methodName = map.get("methodName").toString();
		maps.put(methodName, map);
		// portalDealDataLogic.data4Portal(maps);
	}

	/**
	 * 内门户接口调用
	 * 
	 * @param methodNames方法标识名称
	 * @param entity 实体对象
	 */
	public void data4PortalParam(List<Map<String, Object>> mapsList) {
		/*
		 * Map<String, Map<String, Object>> maps = new HashMap<String,
		 * Map<String, Object>>(); for (Map<String, Object> map : mapsList) {
		 * String methodName = map.get("methodName").toString();
		 * maps.put(methodName, map); }
		 */

		// portalDealDataLogic.data4Portal(maps);
	}

	/**
	 * 发送消息通知
	 * 
	 * @param clientCode
	 * @param send
	 * @param receive
	 * @param messageCode
	 */
	private void sendNotify(Authorize entity, Long sendUserId, Object receive, String messageCode, String... message) {
		User sendUser = userLogic.findById(sendUserId);
		String url = GlobalParameters.getString("application.srmDomain");
		Map<String, String> extraparams = new HashMap<String, String>();
		extraparams.put("billNo", entity.getAuthorizeNo());
		extraparams.put("billId", entity.getAuthorizeId().toString());
		extraparams.put("userName", sendUser.getUserName());
		extraparams.put("userCode", sendUser.getUserCode());
		extraparams.put("url", url);
		// notifySenderLogic.send(client.getClientCode(), sendUser, receive,
		// messageCode, new String[] {}, extraparams);
	}

	/**
	 * 处理审核通过
	 * 
	 * @param entity
	 * @param message
	 * @return
	 */
	private void toPass(Authorize entity, Long userId, String message) {
		User sender = userLogic.findById(userId);
		AuthorizeState oldStatus = entity.getStatus();
		String processInstanceId = bpmServiceClient.getProcessInstanceId(SrmConstants.BILLTYPE_AU, entity.getAuthorizeId().toString());// 流程实例id
		boolean result = bpmServiceClient.toPass(userId.toString(), billPK, entity.getAuthorizeId().toString(),
				CommonUtil.toMap(entity, new String[] {}), message);
		if (result && bpmServiceClient.isComplete(billPK, entity.getAuthorizeId().toString())) {
			// 不存在下一级审核，直接改为审核通过
			entity.setStatus(AuthorizeState.TOPASS);
			// 完成授权
			/*
			 * List<Map<String, Object>> mapLists = new ArrayList<Map<String,
			 * Object>>(); mapLists.add(finish4ToDealParam(entity, null));
			 * data4PortalParam(mapLists); sendNotify(entity, userId,
			 * entity.getAuthorizePersonId(), "AU_TOPASS");
			 */
		} else {
			// 存在下一级审核，仍为待审核状态
			// 查询下一级审核人
			// 通知下一级审核人
			// ...
			try {
				/*
				 * List<User> users = findAssignee(entity); // 待办提交审核
				 * List<Map<String, Object>> mapLists = new
				 * ArrayList<Map<String, Object>>();
				 * mapLists.addAll(data4ToDealParam(entity, "AU_CONFIRM", users,
				 * userId)); // 调用内门户接口 data4PortalParam(mapLists);
				 * sendNotify(entity, userId, users, "AU_CONFIRM");
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		entity = save(entity);

		srmBpmService.bpmNotify(entity.getCreateUserId(), SrmConstants.BILLTYPE_AU, "模块授权", entity.getAuthorizeId().toString(),
				entity.getAuthorizeNo(), processInstanceId);

		// 记录日志
		addLog(userId, sender.getUserName(), entity.getAuthorizeId(), "授权单审核通过", SrmConstants.PERFORM_TOPASS, entity.getAuthorizeNo(),
				SrmConstants.PLATFORM_WEB);
	}

	/**
	 * 处理提交审核
	 * 
	 * @param entity
	 * @return
	 */

	/**
	 * 记录操作日志
	 * 
	 * @param userId 用户id
	 * @param userName 用户名
	 * @param billPk 单据编码
	 * @param oldStatus 原状态
	 * @param newStatus 当前状态
	 */
	@Override
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal) {
		if (StringUtils.isBlank(action)) {
			action = "数据操作日志";
		}
		if (StringUtils.isNotBlank(terminal) && !terminal.equals(SrmConstants.PLATFORM_WEB)) {
			message = StringUtils.isBlank(message) ? "" : ",原因：" + message;
			message = terminal + message;
		}

		Log log = Logs.getLog();
		LogMessage.create(log).type(LogType.OPERATION).level(Level.INFO)// 设置日志级别
				.module(billPK)// 设置日志模块如：SrmConstants.BILLTYPE_ZBD
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
	 * 删除授权单据
	 * 
	 * @param ids
	 */
	@Override
	public void deleteAuthorize(List<Long> ids) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("IN_authorizeId", ids);
		List<Authorize> dList = findAll(searchParams);
		for (Authorize authorize : dList) {
			// 删除之前的提醒信息
			// portalDealDataLogic.finish4ToDeal(billPK,
			// authorize.getAuthorizeId().toString(), null);
			delete(authorize);
		}

	}
}
