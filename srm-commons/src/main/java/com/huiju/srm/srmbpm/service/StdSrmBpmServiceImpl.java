package com.huiju.srm.srmbpm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.bpm.dto.AuthorizeNodeDTO;
import com.huiju.bpm.dto.MessageDTO;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.log.Level;
import com.huiju.module.log.Log;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.notify.api.NotifySenderClient;
import com.huiju.notify.dto.NotifyParam;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.portal.dto.PortalMethodType;
import com.huiju.portal.dto.PortalParameters;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.srmbpm.entity.AuditChat;
import com.huiju.srm.srmbpm.entity.Authorize;
import com.huiju.srm.srmbpm.entity.SrmBpm;

/**
 * bpm的Bean
 * 
 * @author wangmx
 *
 */
public class StdSrmBpmServiceImpl extends JpaServiceImpl<AuditChat, Long> implements StdSrmBpmService {
	@Autowired
	protected UserClient userLogic;
	@Autowired
	protected BpmServiceClient bpmServiceLogic;
	@Autowired
	protected NotifySenderClient notifySenderLogic;
	@Autowired
	protected PortalServiceClient portalServiceClient;

	/**
	 * 催审功能
	 * 
	 * @param constantsBillType 模块编码
	 * @param moduleName 模块名称
	 * @param billId 单据Id
	 * @param billNo 单据号
	 * @param createUserId 催审人Id
	 * @return
	 */
	@Override
	public String pressingforapproval(SrmBpm bpm) {
		if (StringUtils.isEmpty(bpm.getModuleCode()) || bpm.getBillId() == null) {
			return "找不到对应的单据编码或单据";
		}
		HashMap<String, List<Long>> userId = bpmServiceLogic.getAssignee(bpm.getModuleCode(), bpm.getBillId().toString());
		Boolean flag = bpmServiceLogic.isComplete(bpm.getModuleCode(), bpm.getBillId().toString());
		List<User> usersList = null;
		if (!flag) {
			usersList = this.getUserList(userId);
		}
		if (usersList != null && usersList.size() > 0 && !flag) {
			sendMessage(bpm, usersList);
		} else {
			return "该单据无当前审批人";
		}
		return null;
	}

	/**
	 * 发送催审消息
	 * 
	 * @param constantsBillType 模块编码
	 * @param moduleName 模块名称
	 * @param billId 单据Id
	 * @param billNo 单据号
	 * @param createUserId 催审人Id
	 * @param createUserName 催审人名称
	 * @param userList 需要通知的用户集合
	 * @param nodeDresc 当前节点名称
	 */
	protected void sendMessage(SrmBpm bpm, List<User> usersList) {
		List<Long> receiverIds = new ArrayList<Long>();
		if (usersList != null && usersList.size() > 0) {
			for (User u : usersList) {
				receiverIds.add(u.getUserId());
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("constantsBillType", bpm.getModuleCode());
		params.put("moduleName", bpm.getModuleName());
		params.put("billId", bpm.getBillId());
		params.put("billNo", bpm.getBillNo());
		params.put("createUserName", bpm.getCreateUserName());
		List<Map<String, Object>> taskInfo = bpmServiceLogic.queryTaskInfos(bpm.getModuleCode(), bpm.getBillId().toString());
		String nodeDresc = "";

		if (taskInfo.size() > 0) {
			nodeDresc = taskInfo.get(0).get("taskName").toString();
		}
		NotifyParam notifyParam = new NotifyParam();
		notifyParam.setClientCode("800");
		notifyParam.setNotifyCode("BN_PRESSINGFORAPPROVAL");
		notifyParam.setExtraParams(params);
		notifyParam.setSenderCode(bpm.getCreateUserCode());
		notifyParam.setReceiverIds(receiverIds);
		notifySenderLogic.send(notifyParam);
		addLog(bpm.getModuleCode(), bpm.getCreateUserId(), bpm.getCreateUserName(), bpm.getBillId().toString(), "催审," + nodeDresc, "催审",
				SrmConstants.PLATFORM_WEB);
	}

	/**
	 * 往当前的审核节点中授权审核人
	 * 
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	@Override
	public String insertUsersToProcessPoint(Map<String, Object> params) {
		String returnCode = "";
		String billNo = "";
		String moduleName = "";
		Long userId = (Long) params.get("userId");
		String userName = params.get("userName").toString();
		if (params.get("billNo") != null) {
			billNo = params.get("billNo").toString();
		}
		if (params.get("moduleName") != null) {
			moduleName = params.get("moduleName").toString();
		}

		List<String> userIdList = new ArrayList<String>();
		List<Long> receives = new ArrayList<Long>();
		// 获取需要插入的用户的id
		String[] userIds = params.get("IN_userId").toString().split(",");
		for (int i = 0; i < userIds.length; i++) {
			userIdList.add(userIds[i]);
			receives.add(Long.valueOf(userIds[i]));
		}
		// userIdList = Arrays.asList(userIds);
		// 获取审核流的编码
		String processKey = params.get("processKey").toString();
		// 获取单据的id值
		String businessKey = params.get("dataKeyId").toString();
		// 调用流程，插入审核人员
		String dealUserId = userId == null ? "" : userId.toString();
		returnCode = bpmServiceLogic.setAssigneeUser(dealUserId, userIdList, processKey, businessKey, null);
		// 发送邮件提醒
		User u = userLogic.findById(userId);
		Map<String, Object> extraparams = new HashMap<String, Object>();
		extraparams.put("userName", u.getUserName());
		extraparams.put("billTypeName", moduleName);
		extraparams.put("billTypeCode", billNo);
		notifySenderLogic.send(new NotifyParam(u.getClientCode(), userId, receives, "COMM_GRANT", new String[] {}, extraparams));
		// 发送待办
		// 创建内门户参数
		PortalParameters pp = new PortalParameters();
		FeignParam<User> feignParams = new FeignParam<User>();
		extraparams.clear();
		extraparams.put("IN_userId", receives);
		feignParams.setParams(extraparams);
		List<User> userLists = userLogic.findAll(feignParams);
		Map<String, Object> portalParams = new HashMap<String, Object>();
		portalParams.put("1", u.getUserName());
		portalParams.put("2", billNo);
		pp.addPortalMethod(PortalMethodType.TODEAL_DELETE)
				.addPortalMethod(PortalMethodType.TODEAL_ADD, "TODEAL_GRANT_CONFIRM", portalParams, userLists)// 我的待办业务
				.setBillTypeCode(processKey).setBillId(businessKey).setBillNo(billNo).setInfo(DataUtils.toJson(new Authorize()));
		// 执行调用内门户处理方法
		portalServiceClient.data4Portal(pp);
		/*
		 * if (null == returnCode) { Contract ct =
		 * contractLogic.findById(contractId); if(ct !=null){
		 * if("xs".equals(ct.getContractType().substring(0, 2))){
		 * params.remove("processKey"); params.put("processKey", "FHT"); } } //
		 * 发送消息 sendProcessPointMessage(params, "EMPOWER"); }
		 */

		// 添加日志
		String userNames = "";
		for (String uId : userIdList) {
			User user = userLogic.findById(Long.valueOf(uId));
			if (user != null) {
				userNames = user.getUserName();
			}
		}
		String message = "进行[授权]操作,授权审核权限给[" + userNames + "]";
		addLog(processKey, userId, userName, businessKey, message, "授权", SrmConstants.PLATFORM_WEB);
		// 返回参数
		return returnCode;
	}

	/**
	 * 往当前的审核节点中加签审核角色
	 * 
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	@Override
	public String insertRolesToProcessPoint(Map<String, Object> params) {
		Long userId = (Long) params.get("userId");
		String userName = params.get("userName").toString();

		List<String> userIdList = new ArrayList<String>();
		// 获取需要插入的用户的id
		String[] userIds = params.get("IN_userId").toString().split(",");
		for (int i = 0; i < userIds.length; i++) {
			userIdList.add(userIds[i]);
		}
		// userIdList = Arrays.asList(userIds);
		// 获取审核流的编码
		String processKey = params.get("processKey").toString();
		// 获取单据的id值
		String dataKeyId = params.get("dataKeyId").toString();
		// 构造节点--节点名称--审核人--候选人id集合--候选角色id集合
		AuthorizeNodeDTO node = new AuthorizeNodeDTO("加签节点", null, userIdList, null);
		// 调用流程，插入审核人员
		String dealUserId = userId == null ? "" : userId.toString();
		boolean success = false;
		bpmServiceLogic.insertAuthorizeNodeAfter(dealUserId, processKey, dataKeyId, node);
		// 返回参数
		if (!success) {
			return "加签失败！";
		}
		// 添加日志
		String userNames = "";
		for (String uId : userIdList) {
			User user = userLogic.findById(Long.valueOf(uId));
			if (user != null) {
				userNames = user.getUserName();
			}
		}
		String message = "进行[加签]操作,加签给[" + userNames + "]";
		addLog(processKey, userId, userName, dataKeyId, message, "授权", SrmConstants.PLATFORM_WEB);
		return null;

	}

	/**
	 * 授权/加签添加操作日志
	 * 
	 * @param billKey
	 * @param userId
	 * @param userName
	 * @param billPk
	 * @param message
	 * @param action
	 */
	public void addLog(String billKey, Long userId, String userName, String billPk, String message, String action, String terminal) {
		try {
			if (StringUtils.isBlank(action)) {
				action = "数据操作日志";
			}
			// create log
			Log log = Logs.getLog();
			LogMessage.create(log)// 创建日志message
					.type(LogType.OPERATION).level(Level.INFO)// 设置日志级别
					.module(billKey)// 设置日志模块
					.key(billPk)// 日志信息的主要key
					.action(action)// 操作的动作
					.message(message)// 日志的记录内容
					.result("success")// 日志的操作结果
					.operatorName(userName)// 日志的操作人
					.operatorId(userId)// 操作人id
					.terminal(terminal) // 终端标识
					.log();// 调用记录日志
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 知会
	 * 
	 * @param createUserId 单据创建者Id
	 * @param billTypeCode 模块编码
	 * @param billTypeName 模块名称
	 * @param billId 单据id
	 * @param billNo 单据编码
	 * @param processInstanceId 流程实例id
	 */
	@Override
	public void bpmNotify(Long createUserId, String billTypeCode, String billTypeName, String billId, String billNo,
			String processInstanceId) {
		// 发送知会邮件
		if (StringUtils.isNotBlank(processInstanceId)) {
			String jsonData = bpmServiceLogic.getNoticeInfos(processInstanceId);
			List<String> noticeUserIds = new ArrayList<String>();
			JSONArray jsonArray = JSONArray.parseArray(jsonData);
			JSONObject jsonObject = new JSONObject();
			if (StringUtils.isNotBlank(jsonData)) {// 有知会节点信息才发知会邮件
				if (jsonArray != null && jsonArray.size() > 0) {
					for (Object o : jsonArray) {
						jsonObject = JSONObject.parseObject(o.toString());
						noticeUserIds.add(jsonObject.get("userId").toString());
					}
					User sendUser = userLogic.findById(createUserId);
					sendNotify(processInstanceId, sendUser, StringUtils.join(noticeUserIds, ","), billTypeCode, billTypeName, billId,
							billNo);
				}
			}
		}
	}

	/**
	 * 发送知会通知
	 * 
	 * @param sender 发件人
	 * @param receiver 接收人
	 * @param billTypeCode 模块编码
	 * @param billTypeName 模块名称
	 * @param billId 单据id
	 * @param billNo 单据号
	 * @return
	 */
	public void sendNotify(String processInstanceId, User sender, String receiver, String billTypeCode, String billTypeName, String billId,
			String billNo) {
		// 邮件提醒
		Map<String, Object> notifyParams = new HashMap<String, Object>();
		FeignParam<User> feignParams = new FeignParam<User>();
		notifyParams.put("IN_userId", receiver);
		feignParams.setParams(notifyParams);
		List<User> receivers = userLogic.findAll(feignParams);
		notifyParams.clear();
		List<Long> receiverIds = new ArrayList<Long>();
		if (receivers != null && receivers.size() > 0) {
			for (User u : receivers) {
				receiverIds.add(u.getUserId());
			}
		}
		notifyParams.put("billTypeCode", billTypeName);
		notifyParams.put("billNo", billNo);
		notifyParams.put("billId", billId);
		notifyParams.put("userName", sender.getUserName());
		NotifyParam notifyParam = new NotifyParam();
		notifyParam.setClientCode("800");
		notifyParam.setNotifyCode("BILL_NOTIFY");
		notifyParam.setExtraParams(notifyParams);
		notifyParam.setSenderCode(sender.getUserCode());
		notifyParam.setReceiverIds(receiverIds);
		notifySenderLogic.send(notifyParam);
		// 回置当前知会节点为已发邮件
		if (StringUtils.isNotBlank(processInstanceId)) {
			bpmServiceLogic.updateInfoNoticed(bpmServiceLogic.getNoticeInfos(processInstanceId));
		}
	}

	/**
	 * 获取该单据中已审核过的审核人或单据提交者
	 * 
	 * @param billTypeCode 单据编码
	 * @param billId 单据Id
	 * @param type 类型(回复或新增)
	 * @param userId 用户Id
	 * @return
	 */
	public List<User> getAuditPerson(String billTypeCode, String billId, String type, Long userId, Long operatorId) {
		boolean complete = bpmServiceLogic.isComplete(billTypeCode, billId);
		List<User> user = null;
		// 进入审核流才能确认单据审批人,才能发起审批沟通
		if (!complete) {
			Map<String, Object> map = new HashMap<String, Object>();
			if ("reply".equals(type)) {
				map.put("EQ_userId", userId);
				FeignParam<User> feignParam = new FeignParam<User>();
				feignParam.setParams(map);
				user = userLogic.findAll(feignParam);
			} else {
				List<MessageDTO> list = bpmServiceLogic.getMessages(billTypeCode, billId, true);
				List<Long> userIds = new ArrayList<Long>();
				Map<String, String> userMap = new HashMap<String, String>();
				if (!operatorId.equals(userId)) {
					userIds.add(userId);
				}
				for (MessageDTO message : list) {
					if (null != message.getUserId() && !userMap.containsKey(message.getUserId())
							&& !message.getUserId().equals(operatorId.toString())) {
						userIds.add(Long.parseLong(message.getUserId()));
						userMap.put(message.getUserId(), message.getUserId());
					}
				}
				if (userIds != null && userIds.size() > 0) {
					map.put("IN_userId", userIds);
					FeignParam<User> feignParam = new FeignParam<User>();
					feignParam.setParams(map);
					user = userLogic.findAll(feignParam);
				}
			}
		}
		return user;
	}

	/**
	 * 获取有权限审核的用户结果集
	 * 
	 * @param ids 审批流中配置的审批人
	 * @return
	 */
	protected List<User> getUserList(HashMap<String, List<Long>> ids) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("DISTINCT", true);
		if (ids.get("roleId") != null && 0 < ids.get("roleId").size()) {
			params.put("IN_roles_roleId", StringUtils.join(ids.get("roleId"), ","));
		}
		if (ids.get("userId") != null && 0 < ids.get("userId").size()) {
			params.put("IN_userId", StringUtils.join(ids.get("userId"), ","));
		}
		List<User> users = new ArrayList<User>();
		if (params.size() > 1) {
			FeignParam<User> feignParam = new FeignParam<User>();
			feignParam.setParams(params);
			users = userLogic.findAll(feignParam);
		}
		return users;

	}

	/**
	 * 新增回复审批沟通
	 * 
	 * @param auditChat 审批沟通实体
	 * @param moduleName 模块名称
	 * @return
	 */
	@Override
	public String addAuditChat(AuditChat auditChat, String moduleName) {
		// 判断是是否有审批权限，只有当前节点待审核的人才能发起审批沟通
		boolean falg = bpmServiceLogic.isAuthoritiedToAuditing(auditChat.getSenderId().toString(), auditChat.getProcessKey(),
				auditChat.getBusinessKey().toString());
		if (!falg) {
			return "只有当前节点待审核的审核人才有权限主动发起沟通信息";
		}
		auditChat = save(auditChat);
		// this.sendAuditChatNotity(auditChat, moduleName);
		return null;
	}

	/**
	 * 审批沟通新增或回复发送消息提醒
	 * 
	 * @param auditChat 审批沟通实体
	 * @param moduleName 模块名称
	 */
	protected void sendAuditChatNotity(AuditChat auditChat, String moduleName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("moduleName", moduleName);
		params.put("billId", auditChat.getBusinessKey());
		params.put("billNo", auditChat.getBillNo());
		params.put("moduleCode", auditChat.getProcessKey());
		params.put("createUserName", auditChat.getSenderName());
		List<User> userList = null;
		if (auditChat.getReceipentId() != null) {
			Map<String, Object> finUserMap = new HashMap<String, Object>();
			finUserMap.put("EQ_userId", auditChat.getReceipentId());
			FeignParam<User> feignParam = new FeignParam<User>();
			feignParam.setParams(finUserMap);
			userList = userLogic.findAll(feignParam);
			List<Long> receiverIds = new ArrayList<Long>();
			receiverIds.add(auditChat.getReceipentId());
			NotifyParam notifyParam = new NotifyParam();
			notifyParam.setClientCode("800");
			notifyParam.setNotifyCode("AUDIT_CHAT_NOTIFY");
			notifyParam.setExtraParams(params);
			notifyParam.setSenderId(auditChat.getSenderId());
			notifyParam.setReceiverIds(receiverIds);
			notifySenderLogic.send(notifyParam);
		}
	}

	/**
	 * 回复审批沟通
	 * 
	 * @param auditChat 审批沟通实体
	 * @param moduleName 模块名称
	 * @return
	 */
	@Override
	public String replyAuditChat(AuditChat auditChat, String moduleName) {
		auditChat = save(auditChat);
		// this.sendAuditChatNotity(auditChat, moduleName);
		return null;
	}

	/**
	 * ，只有该单据的提报人和该单据审核人有权限查看全部的沟通记录
	 * 
	 * @param userId 操作人Id
	 * @param billTypeCode 模块编码
	 * @param billId 单据Id
	 * @return
	 */
	@Override
	public boolean canFindData(Long userId, String billTypeCode, String billId) {
		List<MessageDTO> list = bpmServiceLogic.getAllMessages(billTypeCode, billId);
		boolean flag = false;
		// 判断是否是审核人，当操作人是之前审核过的人则可查看所有审批回复记录
		if (list != null && list.size() > 0) {
			for (MessageDTO message : list) {
				if (StringUtils.isNotBlank(message.getUserId()) && userId.toString().equals(message.getUserId())) {
					flag = true;
					return flag;
				}
				List<String> roleRelatedUserIds = message.getRoleRelatedUserId();
				List<String> configuredUserIds = message.getConfiguredUserId();
				if (roleRelatedUserIds != null && roleRelatedUserIds.size() > 0) {
					if (roleRelatedUserIds.contains(userId.toString())) {
						flag = true;
						return flag;
					}
				}
				if (configuredUserIds != null && configuredUserIds.size() > 0) {
					if (configuredUserIds.contains(userId.toString())) {
						flag = true;
						return flag;
					}
				}
			}
		}

		// 判断是否有审批权限，如果有审批权限也可查看审批回复记录
		/*
		 * boolean complete = bpmServiceLogic.isComplete(billTypeCode, billId);
		 * if (!complete) { flag =
		 * bpmServiceLogic.isAuthoritiedToAuditing(userId.toString(),
		 * billTypeCode, billId); return flag; }
		 */

		return flag;
	}
}