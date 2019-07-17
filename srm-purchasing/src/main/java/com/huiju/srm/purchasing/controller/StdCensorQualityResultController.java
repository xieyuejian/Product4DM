package com.huiju.srm.purchasing.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.huiju.core.sys.api.UserAuthGroupServiceClient;
import com.huiju.core.sys.dto.UserAuthGroupParam;
import com.huiju.module.data.common.Page;
import com.huiju.module.mvc.controller.CloudController;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.utils.SrmSynStatus;
import com.huiju.srm.purchasing.entity.CensorQuality;
import com.huiju.srm.purchasing.entity.CensorQualityState;
import com.huiju.srm.purchasing.service.CensorQualityService;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;

/**
 * 质检结果controller
 * 
 * @author bairx
 *
 */
public class StdCensorQualityResultController extends CloudController {

	@Autowired
	protected CensorQualityService censorQualityServiceImpl;
	@Autowired
	protected UserAuthGroupServiceClient userAuthGroupClient;
	protected String synStatus;
	protected String qualityId;

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

		if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			searchParams.putAll(userAuthGroupClient
					.buildAuthFieldParamsWithBlank(new UserAuthGroupParam(getClientCode(), getUserCode(), CensorQuality.class)));
		}

		if (isRoleOf(SrmConstants.ROLETYPE_V)) {
			searchParams.put("EQ_vendorErpCode", getErpCode());
		}
		// 待处理，待审核初始化状态过滤
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
		if (!"".equals(synStatus) && synStatus != null) {
			String value = synStatus;
			String[] values = value.trim().split(",");
			SrmSynStatus[] statusArray = new SrmSynStatus[values.length];
			for (int i = 0; i < values.length; i++) {
				SrmSynStatus status = SrmSynStatus.valueOf(values[i]);
				statusArray[i] = status;
			}
			searchParams.put("IN_erpSyn", statusArray);
		}

		page = censorQualityServiceImpl.findAll(page, searchParams);
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
	public String get(Long id) {
		CensorQuality model = censorQualityServiceImpl.findById(id);
		if (model == null) {
			return dealJson(false, "信息不存在！");
		}
		return renderJson(model, new String[] {});
	}

	/**
	 * <pre>
	 * 返回编辑表单数据对象
	 * </pre>
	 * 
	 * @return
	 */
	@PostMapping("/synErp")
	public String synErp(String id) {
		try {
			CensorQuality model = censorQualityServiceImpl.findById(Long.valueOf(id));
			String result = censorQualityServiceImpl.synErp(Long.valueOf(id));
			// String result=null;
			if (!"".equals(result)) {
				return dealJson(false, result);
			}
			censorQualityServiceImpl.addLog(getUserId(), getUserName(), Long.valueOf(id), "检验单同步", SrmConstants.PERFORM_SYNC,
					model.getCensorqualityNo(), SrmConstants.PLATFORM_WEB);
			return dealJson(true);
		} catch (Exception e) {
			e.printStackTrace();
			return dealJson(false, e.getMessage());
		}
	}

	public List<ExcelExportEntity> createEntity() {
		String[] is = { "待检_0", "检验中_1", "检验完成_2", "取消_3" };
		String[] status = { "未同步_0", "同步中_1", "已同步_2", "同步失败_3" };
		List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
		// 构造对象等同于@Excel
		entity.add(new ExcelExportEntity("检验批次", "censorqualityNo"));
		ExcelExportEntity r1 = new ExcelExportEntity("状态", "status");
		r1.setReplace(is);
		entity.add(r1);
		entity.add(new ExcelExportEntity("送检时间", "inspectionTime"));
		entity.add(new ExcelExportEntity("物料编码", "materialCode", 25));
		entity.add(new ExcelExportEntity("物料名称", "materialName", 25));
		entity.add(new ExcelExportEntity("送检量", "censorqty"));
		entity.add(new ExcelExportEntity("单位", "unit"));
		entity.add(new ExcelExportEntity("合格量", "checkQualifiedQty"));
		entity.add(new ExcelExportEntity("不合格量", "checkUnqualifiedQty"));
		entity.add(new ExcelExportEntity("让步接收量", "checkReceiveQty"));
		entity.add(new ExcelExportEntity("质检结果", "resultName"));
		ExcelExportEntity r2 = new ExcelExportEntity("同步状态", "erpSyn");
		r2.setReplace(status);
		entity.add(r2);
		entity.add(new ExcelExportEntity("收货单号", "receivingnoteNo", 20));
		entity.add(new ExcelExportEntity("供应商编码", "vendorErpCode", 20));
		entity.add(new ExcelExportEntity("供应商名称", "vendorName", 20));
		entity.add(new ExcelExportEntity("采购订单号", "purchaseOrderNo", 20));
		entity.add(new ExcelExportEntity("行号", "rowIds"));
		entity.add(new ExcelExportEntity("凭证年度", "voucherYear"));
		entity.add(new ExcelExportEntity("凭证编号", "voucherNo"));
		entity.add(new ExcelExportEntity("凭证行项目号", "voucherProNo"));
		entity.add(new ExcelExportEntity("采购组织编码", "purchasingOrgCode", 20));
		entity.add(new ExcelExportEntity("采购组织名称", "purchasingOrgName", 20));
		entity.add(new ExcelExportEntity("质检时间", "qualityTime"));
		entity.add(new ExcelExportEntity("备注", "remark"));
		return entity;
	}

	@PostMapping("/getCondition")
	public Map<String, Object> getCondition(String initStates) {
		Map<String, Object> searchParams = buildParams();

		if (SrmConstants.ROLETYPE_V.equals(getRoleTypes())) {
			searchParams.put("EQ_vendorErpCode", getErpCode());
		}
		if (isRoleOf(SrmConstants.ROLETYPE_B)) {
			searchParams.putAll(
					userAuthGroupClient.buildAuthFieldParams(new UserAuthGroupParam(getClientCode(), getUserCode(), CensorQuality.class)));
		}
		// 待处理，待审核初始化状态过滤
		if (!"".equals(initStates) && initStates != null) {
			String value = initStates;
			String[] values = value.trim().split(",");
			CensorQualityState[] statusArray = new CensorQualityState[values.length];
			for (int i = 0; i < values.length; i++) {
				CensorQualityState status = CensorQualityState.valueOf(values[i].replace(" ", ""));
				statusArray[i] = status;
			}
			searchParams.put("IN_status", statusArray);
		}
		if (!"".equals(synStatus) && synStatus != null) {
			String value = synStatus;
			String[] values = value.trim().split(",");
			SrmSynStatus[] statusArray = new SrmSynStatus[values.length];
			for (int i = 0; i < values.length; i++) {
				SrmSynStatus status = SrmSynStatus.valueOf(values[i]);
				statusArray[i] = status;
			}
			searchParams.put("IN_erpSyn", statusArray);
		}
		return searchParams;
	}

	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}

	public String getSynStatus() {
		return synStatus;
	}

	public void setSynStatus(String synStatus) {
		this.synStatus = synStatus;
	}

}
