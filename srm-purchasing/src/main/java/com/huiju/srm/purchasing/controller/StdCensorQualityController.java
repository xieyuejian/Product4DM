package com.huiju.srm.purchasing.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.FeignParam;
import com.huiju.module.data.common.JsonParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.data.common.Result;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.entity.CensorQualityState;
import com.huiju.srm.purchasing.service.CensorQualityService;

/**
 * 质检管理controller
 * 
 * @author bairx
 *
 */
public class StdCensorQualityController extends CloudController {
	@Autowired
	protected CensorQualityService censorQualityServiceImpl;
	@Autowired(required = false)
	protected UserAuthGroupServiceClient userAuthGroupClient;

	protected String synStatus;

	/**
	 * <pre>
	 * 获取列表 / 查询数据
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/list")
	public Page<CensorQuality> list(String initStates) {
		Page<CensorQuality> page = buildPage(CensorQuality.class);
		Map<String, Object> searchParams = buildParams();

		// 单据状态查询
		if (!"".equals(initStates) && initStates != null) {
			String value = initStates;
			String[] values = value.trim().split(",");
			CensorQualityState[] statusArray = new CensorQualityState[values.length];
			for (int i = 0; i < values.length; i++) {
				CensorQualityState status = CensorQualityState.valueOf(values[i]);
				statusArray[i] = status;
			}
			List<CensorQualityState> strlist = Arrays.asList(statusArray);
			searchParams.put("IN_status", strlist);
		}

		if (isRoleOf(com.huiju.srm.commons.utils.SrmConstants.ROLETYPE_B)) {
			searchParams.putAll(userAuthGroupClient
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), CensorQuality.class)));
		}

		if (SrmConstants.ROLETYPE_V.equals(getRoleTypes())) {
			searchParams.put("EQ_vendorErpCode_OR_EQ_vendorCode", new String[] { getErpCode(), getUserCode() });
		}

		page = censorQualityServiceImpl.findAllWithoutAssociation(page, searchParams);
		return page;
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
		CensorQuality model = censorQualityServiceImpl.findById(id);
		if (model == null) {
			return Result.error("信息不存在！");
		}
		return Result.success(model);
	}

	/**
	 * <pre>
	 * 保存表单
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/save")
	public Result save(@RequestBody JsonParam<CensorQuality> jsonParam) {
		CensorQuality model = jsonParam.getModel();
		String submitFlag = jsonParam.getSubmitFlag();
		model.setCreateUserId(getUserId());
		model.setCreateUserName(getUserName());
		model.setCreateTime(Calendar.getInstance());
		if ("save".equalsIgnoreCase(submitFlag)) {
			BigDecimal qualifiedQty = model.getQualifiedQty() == null ? BigDecimal.ZERO : model.getQualifiedQty(); // 合格量
			BigDecimal unqualifiedQty = model.getUnqualifiedQty() == null ? BigDecimal.ZERO : model.getUnqualifiedQty(); // 不合格量
			BigDecimal receiveQty = model.getReceiveQty() == null ? BigDecimal.ZERO : model.getReceiveQty(); // 让步接收量
			BigDecimal canCheckQty = model.getCanCheckQty() == null ? BigDecimal.ZERO : model.getCanCheckQty(); // 可检量
			if (canCheckQty.compareTo(qualifiedQty.add(unqualifiedQty).add(receiveQty)) == 0) {
				model.setStatus(CensorQualityState.CHECKED);
			} else {
				model.setStatus(CensorQualityState.TOCHECK);
			}
			model = censorQualityServiceImpl.save(model);
			censorQualityServiceImpl.addLog(getUserId(), getUserName(), model.getCensorqualityId(), "检验单创建", SrmConstants.PERFORM_SAVE,
					model.getCensorqualityNo(), SrmConstants.PLATFORM_WEB);
		} else if ("audit".equalsIgnoreCase(submitFlag)) {
			model.setStatus(CensorQualityState.TOCHECK);
			model = censorQualityServiceImpl.save(model);
			censorQualityServiceImpl.addLog(getUserId(), getUserName(), model.getCensorqualityId(), "检验单提交", SrmConstants.PERFORM_AUDIT,
					model.getCensorqualityNo(), SrmConstants.PLATFORM_WEB);
		}
		// 操作成功的日志信息
		return Result.success(true);

	}

	/**
	 * 修改
	 * 
	 * @return String
	 */
	@PostMapping("/update")
	public Result update(@RequestBody JsonParam<CensorQuality> jsonParam) {
		try {
			CensorQuality model = jsonParam.getModel();
			jsonParam.getSubmitFlag();
			CensorQuality pd = censorQualityServiceImpl.findById(model.getCensorqualityId());

			if (pd == null) {
				return Result.error(getText("message.notexisted"));
			}

			Object[] creator = new Object[3];
			creator[0] = getUserId();
			creator[1] = getUserCode();
			creator[2] = getUserName();

			// 赋值质检的相关时间与值
			model.setQualitorId(getUserId());
			model.setQualitorName(getUserName());
			model.setQualityTime(Calendar.getInstance());

			censorQualityServiceImpl.mergeCensorQuality(model, creator);

			/*
			 * if ("save".equalsIgnoreCase(submitFlag)) {
			 * model.setStatus(pd.getStatus()); //
			 * censorQualityLogic.mergeCensorQuality(model); } else if
			 * ("audit".equalsIgnoreCase(submitFlag)) {
			 * model.setStatus(pd.getStatus());
			 * //censorQualityServiceImpl.save(model); }
			 */
			censorQualityServiceImpl.addLog(getUserId(), getUserName(), model.getCensorqualityId(), "检验单检验", SrmConstants.PERFORM_SAVE,
					model.getCensorqualityNo(), SrmConstants.PLATFORM_WEB);
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error(e.getMessage());
		}
	}

	/**
	 * 删除
	 * 
	 * @return String
	 */
	@PostMapping("/delete")
	public String delete(List<Long> ids) {
		try {
			censorQualityServiceImpl.deleteById(ids);
			return dealJson(true);
		} catch (Exception e) {
			e.printStackTrace();
			return dealJson(false, e.toString());
		}
	}
	// --------------------------------------------Feign----------------------------------------------

	/**
	 * 查询
	 * 
	 * @return String
	 */
	@PostMapping("/find")
	public String find(@RequestBody FeignParam<CensorQuality> feignParam) {
		return censorQualityServiceImpl.findAllJson(feignParam.getParams(), feignParam.getExcludes(), feignParam.getSorts());

	}

}
