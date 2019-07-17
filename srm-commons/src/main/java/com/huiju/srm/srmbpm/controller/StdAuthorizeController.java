package com.huiju.srm.srmbpm.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.core.sys.api.BillSetServiceClient;
import com.huiju.core.sys.api.UserClient;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.srmbpm.entity.Authorize;
import com.huiju.srm.srmbpm.entity.AuthorizeState;
import com.huiju.srm.srmbpm.service.AuthorizeService;

/**
 * 授权单action
 * 
 */
public class StdAuthorizeController extends CloudController {

	private static final long serialVersionUID = 1L;

	@Autowired
	private AuthorizeService authorizeLogic;
	@Autowired
	protected UserClient userLogic;
	@Autowired
	protected BillSetServiceClient billSetLogic;
	@Autowired
	protected BpmServiceClient bpmService;

	/**
	 * 获取列表数据
	 * 
	 * @return
	 */
	@RequestMapping("/list")
	public Page<Authorize> list(String dataAuth) {
		Map<String, Object> searchParams = buildParams();
		Page<Authorize> page = buildPage(Authorize.class);
		Map<String, Object> listSearchParams = new HashMap<String, Object>();
		listSearchParams.put("EQ_status", AuthorizeState.TOPASS);
		List<Authorize> authorizeList = authorizeLogic.findAll(listSearchParams);
		List<Authorize> newAuthorizeList = new ArrayList<Authorize>();
		if (authorizeList != null && authorizeList.size() > 0) {
			for (Authorize authorize : authorizeList) {
				if (Calendar.getInstance().compareTo(authorize.getExpiryTime()) > 0) {// 当前时间大于失效时间
					authorize.setStatus(AuthorizeState.CANCEL);// 设置为失效
					newAuthorizeList.add(authorize);
				}
			}
		}
		authorizeLogic.saveAll(newAuthorizeList);
		if (searchParams.containsKey("IN_status")) {
			Object obj = searchParams.get("IN_status");
			String status = obj.toString();
			String[] arrStates = status.split(",");
			List<AuthorizeState> applyStates = new ArrayList<AuthorizeState>();
			for (String s : arrStates) {
				applyStates.add(AuthorizeState.valueOf(s));
			}
			searchParams.put("IN_status", applyStates);
		}
		if ("search".equals(dataAuth)) {// 如果为查询模块 展示全部
			page = authorizeLogic.findAll(page, searchParams);
			return page;
		} else {
			// 列表可以查看我创建的或者我审核的单据
			List<String> billPks = new ArrayList<String>();
			// 我创建的
			searchParams.put("EQ_authorizePersonId", getUserId());
			List<Authorize> list = authorizeLogic.findAll(searchParams);
			if (list != null && list.size() > 0) {
				for (Authorize authorize : list) {
					billPks.add(String.valueOf(authorize.getAuthorizeId()));
				}
			}
			searchParams.remove("EQ_authorizePersonId");
			if (searchParams.containsKey("IN_status")) {
				searchParams.remove("IN_status");
			}
			// 我审核过的
			List<String> fis = bpmService.getAllRelatedKeys(getUserId().toString(), SrmConstants.BILLTYPE_AU, true);
			billPks.addAll(fis);

			if (billPks != null && billPks.size() > 0) {
				String authorizeIds = StringUtils.join(billPks, ",");
				searchParams.put("IN_authorizeId", authorizeIds);
				page = authorizeLogic.findAll(page, searchParams);
				return page;
			} else {
				return page;
			}
		}
	}

	/**
	 * 保存
	 * 
	 * @return
	 */
	@RequestMapping("/save")
	public Result save(@RequestBody JsonParam<Authorize> param) {
		Authorize model = param.getModel();
		String submitFlag = param.getSubmitFlag();
		Authorize authorize = checkBill(model);// 校验单据唯一性
		if (authorize != null) {
			Calendar effectiveTime2 = authorize.getEffectiveTime();
			Calendar expiryTime2 = authorize.getExpiryTime();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
			String returnMessage = "您已将此类型单据在" + formatter.format(effectiveTime2.getTime()) + "–" + formatter.format(expiryTime2.getTime())
					+ "期间授权给" + authorize.getAuthorizeToPersonName() + "请悉知";
			return Result.error(returnMessage);
		}
		Calendar expiryTime = model.getExpiryTime();
		expiryTime.set(Calendar.HOUR_OF_DAY, 23);
		expiryTime.set(Calendar.MINUTE, 59);
		expiryTime.set(Calendar.SECOND, 59);
		model.setExpiryTime(expiryTime);
		// 授权单编号
		model.setAuthorizeNo(billSetLogic.createNextRunningNum(SrmConstants.BILLTYPE_AU));
		// 单据类型id数组
		model.setBillId(StringUtils.join(model.getBillIds(), ","));
		// 授权人id
		model.setAuthorizePersonId(getUserId());
		// 授权人姓名
		model.setAuthorizePersonName(getUserName());
		model.setStatus(AuthorizeState.NEW);
		model.setCreateTime(Calendar.getInstance());
		model.setCreateUserId(getUserId());
		model.setCreateUserName(getUserName());
		Authorize autho = authorizeLogic.save(model);
		if ("audit".equalsIgnoreCase(submitFlag)) {
			authorizeLogic.dealStatus(getUserId(), getUserName(), autho.getAuthorizeId(), AuthorizeState.TOCONFIRM, null);
		} else {
			// 添加新建日志
			authorizeLogic.addLog(getUserId(), getUserName(), autho.getAuthorizeId(), "授权单保存", SrmConstants.PERFORM_SAVE,
					model.getAuthorizeNo(), SrmConstants.PLATFORM_WEB);
		}
		return Result.success();
	}

	/**
	 * 校验数据唯一性
	 * 
	 * @param model
	 * @return
	 */
	public Authorize checkBill(Authorize model) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Object> map3 = new HashMap<String, Object>();
		Calendar effectiveTime = model.getEffectiveTime();
		Calendar expiryTime = model.getExpiryTime();
		// 校验单据唯一性
		// 查询出当前授权人之前是否创建过相同时间期间的申请
		map.put("LE_effectiveTime", effectiveTime);
		map.put("GE_expiryTime", effectiveTime);
		Long userId = getUserId();
		map.put("EQ_authorizePersonId", userId);
		// 状态不是 关闭和审核不过的
		List<AuthorizeState> status = new ArrayList<AuthorizeState>();
		status.add(AuthorizeState.NEW);
		status.add(AuthorizeState.TOPASS);
		status.add(AuthorizeState.TOCONFIRM);
		map.put("IN_status", status);
		if (!"系统自动生成".equals(model.getAuthorizeNo())) {
			map.put("NE_authorizeNo", model.getAuthorizeNo());
		}
		List<Authorize> list1 = authorizeLogic.findAll(map);
		map2.put("LE_effectiveTime", expiryTime);
		map2.put("GE_expiryTime", expiryTime);
		map2.put("EQ_authorizePersonId", userId);
		map2.put("IN_status", status);
		if (!"系统自动生成".equals(model.getAuthorizeNo())) {
			map2.put("NE_authorizeNo", model.getAuthorizeNo());
		}
		List<Authorize> list2 = authorizeLogic.findAll(map2);
		map3.put("GE_effectiveTime", effectiveTime);
		map3.put("LE_expiryTime", expiryTime);
		map3.put("EQ_authorizePersonId", userId);
		map3.put("IN_status", status);
		if (!"系统自动生成".equals(model.getAuthorizeNo())) {
			map3.put("NE_authorizeNo", model.getAuthorizeNo());
		}
		List<Authorize> list3 = authorizeLogic.findAll(map3);
		String[] arr2 = model.getBillIds();
		if (list1.size() > 0) {// 如果存在 查询单据类型是否存在相同的
			if (list1.size() != 0) {
				for (Authorize authorize : list1) {
					String billId = authorize.getBillId();
					for (String newBillId : arr2) {
						if (billId.contains(newBillId)) {// 有相同的数据类型，进行提示
							return authorize;
						}
					}
				}
			}

		}
		if (list2.size() > 0) {
			for (Authorize authorize : list2) {
				String billId = authorize.getBillId();
				for (String newBillId : arr2) {
					if (billId.contains(newBillId)) {// 有相同的数据类型，进行提示
						return authorize;
					}
				}
			}
		}
		if (list3.size() > 0) {
			for (Authorize authorize : list3) {
				String billId = authorize.getBillId();
				for (String newBillId : arr2) {
					if (billId.contains(newBillId)) {// 有相同的数据类型，进行提示
						return authorize;
					}
				}
			}
		}
		return null;

	}

	/**
	 * 编辑
	 * 
	 * @return
	 */
	@RequestMapping("/get")
	public Result get(Long id) {
		Authorize model = authorizeLogic.findById(id);
		if (StringUtils.isNotBlank(model.getBillId()))
			model.setBillIds(model.getBillId().split(","));

		return Result.success(model);
	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestParam List<Long> ids) {
		authorizeLogic.deleteAuthorize(ids);
		return Result.success();
	}

	/**
	 * 修改
	 * 
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody JsonParam<Authorize> param) {
		Authorize model = param.getModel();
		String submitFlag = param.getSubmitFlag();
		Authorize authorize = checkBill(model);// 校验单据唯一性
		if (authorize != null) {
			Calendar effectiveTime2 = authorize.getEffectiveTime();
			Calendar expiryTime2 = authorize.getExpiryTime();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
			String returnMessage = "您已将此类型单据在" + formatter.format(effectiveTime2.getTime()) + "–" + formatter.format(expiryTime2.getTime())
					+ "期间授权给" + authorize.getAuthorizeToPersonName() + "请悉知";
			return Result.error(returnMessage);
		}
		// 申请状态
		model.setBillId(StringUtils.join(model.getBillIds(), ","));
		model.setModifyTime(Calendar.getInstance());
		model.setModifyUserId(getUserId());
		model.setModifyUserName(getUserName());
		// this.authorizeLogic.updateAuthorize(model, submitFlag);
		Authorize autho = authorizeLogic.save(model);
		if ("audit".equalsIgnoreCase(submitFlag)) {
			authorizeLogic.dealStatus(getUserId(), getUserName(), autho.getAuthorizeId(), AuthorizeState.TOCONFIRM, null);
		} else {
			// 添加修改日志
			authorizeLogic.addLog(getUserId(), getUserName(), autho.getAuthorizeId(), "授权单更新", SrmConstants.PERFORM_EDIT,
					model.getAuthorizeNo(), SrmConstants.PLATFORM_WEB);
		}
		return Result.success();
	}

	/**
	 * 提前结束授权
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/authorizestop")
	public Result authorizeStop(Long id) {
		Authorize model = authorizeLogic.findById(id);
		model.setStatus(AuthorizeState.CANCEL);
		this.authorizeLogic.save(model);
		// 添加操作日志
		authorizeLogic.addLog(getUserId(), getUserName(), model.getAuthorizeId(), "授权单截止", SrmConstants.PERFORM_TOEND,
				model.getAuthorizeNo(), SrmConstants.PLATFORM_WEB);
		return Result.success();
	}

	/**
	 * 获取审核流程事件
	 * 
	 * @return
	 */
	@RequestMapping("/getevents")
	public String getEvents(Long id) {
		Long s_userId = getUserId();
		String s_roleType = getRoleTypes();
		String s_authorities = "|" + getUserPermissions() + "|";
		String[] events4Authorities = { "authorize_toconfirm", "authorize_topass", "authorize_tonopass" };
		StringBuffer buf = new StringBuffer();
		for (String auth : events4Authorities) {
			// if (s_authorities.indexOf("|" + auth + "|") > -1) {
			if (buf.length() > 0)
				buf.append(",");
			buf.append("'" + auth + "'");
			// }
		}
		String eventAuth = buf.toString();
		List<String> events = authorizeLogic.getEvents(s_userId, s_roleType, id);
		buf = new StringBuffer("[");
		for (String event : events) {
			if (event != null) {
				System.out.println("'authorize_" + event.toLowerCase() + "'");
				if (eventAuth.indexOf("'authorize_" + event.toLowerCase() + "'") > -1
						|| eventAuth.indexOf("'authorize_undeal_" + event.toLowerCase() + "'") > -1 || event.indexOf("#") > -1) {
					buf.append("'" + event.toUpperCase() + "',");
				}
			}
		}
		if (buf.length() > 1) {
			buf.append("'@'");
		}
		buf.append("]");
		return renderJson(buf.toString());
	}

	/**
	 * 处理流程状态
	 * 
	 * @return
	 */
	@RequestMapping("/dealstatus")
	public Result dealStatus(@RequestParam String billState, @RequestParam String message, @RequestParam Long id) {
		Long userId = getUserId();
		String userName = getUserName();
		authorizeLogic.dealStatus(userId, userName, id, AuthorizeState.valueOf(billState), message);
		return Result.success();
	}

}
