package com.huiju.srm.srmbpm.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.srmbpm.entity.AuditChat;
import com.huiju.srm.srmbpm.entity.SrmBpm;
import com.huiju.srm.srmbpm.service.SrmBpmService;

/**
 * 工作流Action
 * 
 * @author wangmx
 */

public class StdSrmBpmController extends CloudController {

	@Autowired
	protected SrmBpmService srmBpmService;
	@Autowired
	protected UserClient userLogic;

	/**
	 * 催审
	 * 
	 * @param pos
	 * @return
	 */
	@PostMapping("/pressingforapproval")
	public Result pressingforapproval(@RequestBody List<SrmBpm> pos) {
		for (SrmBpm bpm : pos) {
			bpm.setCreateUserId(getUserId());
			bpm.setCreateUserName(getUserName());
			bpm.setCreateUserCode(getUserCode());
			String errMesage = srmBpmService.pressingforapproval(bpm);
			if (StringUtils.isNotEmpty(errMesage)) {
				return Result.error(errMesage);
			}
		}
		return Result.success();
	}

	/**
	 * 审核流程授权加签界面弹出框过滤用户
	 * 
	 * @return
	 */
	@RequestMapping("/getuser4page")
	public Page<User> getUser4Page() {
		Page<User> page = buildPage(User.class);
		FeignParam<User> feignParam = new FeignParam<User>();
		Map<String, Object> searchParams = buildParams();
		searchParams.put("EQ_roles_roleType", SrmConstants.ROLETYPE_B);// 采购角色
		searchParams.put("DISTINCT", true);
		feignParam.setParams(searchParams);
		feignParam.setPage(page);
		page = userLogic.findAllPage(feignParam);
		page.asc("userCode");
		return page;
	}

	/**
	 * 往当前的审核节点中插入审核人
	 * 
	 * @return
	 */
	@RequestMapping("/insertuserstoprocesspoint")
	public Result insertUsersToProcessPoint() {
		Map<String, Object> map = buildParams();
		map.put("userId", getUserId());
		map.put("userName", getUserName());
		srmBpmService.insertUsersToProcessPoint(map);
		return Result.success();
	}

	/**
	 * 往当前审核节点后中加签审核角色
	 * 
	 * @return
	 */
	@RequestMapping("/insertroletoprocessnextpoint")
	public String insertRoleToProcessNextPoint() {
		Map<String, Object> map = buildParams();
		map.put("userId", getUserId());
		map.put("userName", getUserName());
		srmBpmService.insertRolesToProcessPoint(map);
		return dealJson(true);
	}

	/**
	 * 获取该单据中已审核过的审核人或单据提交者（不包括自己）
	 * 
	 * @return
	 */
	@RequestMapping("/getauditperson")
	public String getAuditPerson(String billTypeCode, String billId, String type, Long userId) {
		List<User> user = srmBpmService.getAuditPerson(billTypeCode, billId, type, userId, getUserId());
		return renderJson(user);
	}

	/**
	 * 获取工作流审批沟通
	 * 
	 * @return 审批沟通
	 */
	@RequestMapping("/getauditchat")
	public String getAuditChat() {
		Map<String, Object> searchParams = buildParams();
		boolean flag = false;
		// 只有该单据的提报人和该单据审核人有权限查看全部的沟通记录
		if (searchParams.get("IN_processKey") != null && searchParams.get("EQ_businessKey") != null) {
			flag = srmBpmService.canFindData(getUserId(), searchParams.get("IN_processKey").toString(),
					searchParams.get("EQ_businessKey").toString());
		}
		String createUserId = request.getParameter("createUserId");
		List<AuditChat> ctds = new ArrayList<AuditChat>();
		// 只有该单据的提报人和该单据审核人有权限查看全部的沟通记录。
		if (flag || (StringUtils.isNotBlank(createUserId) && getUserId().equals(Long.valueOf(createUserId)))) {
			ctds = srmBpmService.findAll(searchParams);
		}
		return renderJson(ctds);
	}

	/**
	 * <pre>
	 * 保存表单
	 * </pre>
	 * 
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody JsonParam<AuditChat> jsonParam) {
		AuditChat model = jsonParam.getModel();
		model.setCreateTime(Calendar.getInstance());
		model.setSenderId(getUserId());
		model.setSenderName(getUserName());

		if (model.getSenderId() != null) {
			if (model.getSenderId().longValue() == model.getReceipentId().longValue()) {
				return Result.error("不能给自己发送消息!");
			}
		}

		String errorMessage = srmBpmService.addAuditChat(model, model.getModuleName());
		if (StringUtils.isNoneEmpty(errorMessage)) {
			return Result.error(errorMessage);
		}
		return Result.success();
	}

	/**
	 * 回复
	 * 
	 * @return
	 */
	@RequestMapping("/reply")
	public Result reply(@RequestBody JsonParam<AuditChat> jsonParam) {
		AuditChat model = jsonParam.getModel();
		model.setCreateTime(Calendar.getInstance());
		model.setSenderId(getUserId());
		model.setSenderName(getUserName());
		if (model.getSenderId().longValue() == model.getReceipentId().longValue()) {
			return Result.error("不能给自己发送消息!");
		}
		srmBpmService.replyAuditChat(model, model.getModuleName());
		return Result.success();
	}

	/**
	 * 删除审批沟通
	 * 
	 * @return String
	 */
	@RequestMapping("/delete")
	public Result delete(Long chatId) {
		AuditChat auditChat = srmBpmService.findById(chatId);
		if (auditChat.getSenderId().longValue() == getUserId()) {
			srmBpmService.deleteById(chatId);
			return Result.success();
		} else {
			return Result.error("只能删除自己发送的消息！");
		}
	}

}
