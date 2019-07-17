package com.huiju.srm.purchasing.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huiju.bpm.api.BpmServiceClient;
import com.huiju.bpm.support.annotation.BpmService;
import com.huiju.bpm.support.service.BpmSupport;
import com.huiju.core.sys.api.UserClient;
import com.huiju.core.sys.entity.User;
import com.huiju.interaction.api.InteractionClient;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.module.data.jpa.utils.QueryUtils;
import com.huiju.module.log.Level;
import com.huiju.module.log.LogMessage;
import com.huiju.module.log.LogType;
import com.huiju.module.log.Logs;
import com.huiju.module.util.StringUtils;
import com.huiju.notify.api.NotifySenderClient;
import com.huiju.portal.api.PortalServiceClient;
import com.huiju.portal.dto.PortalMethodType;
import com.huiju.portal.dto.PortalParameters;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.purchasing.dao.ForecastDao;
import com.huiju.srm.purchasing.dao.ForecastDtlDao;
import com.huiju.srm.purchasing.entity.Forecast;
import com.huiju.srm.purchasing.entity.ForecastDtl;
import com.huiju.srm.purchasing.entity.ForecastState;
import com.huiju.srm.srmbpm.service.SrmBpmService;
import com.huiju.srm.vendor.entity.Vendor;
import com.huiju.srm.vendor.service.VendorService;
import com.huiju.srm.ws.service.WsRequestLogService;

/**
 * 采购申请转单实现类
 * 
 * @author bairx
 *
 */
@BpmService(billTypeCode = "CGY", billNoKey = "forecastNo")
public class StdForecastServiceImpl extends BpmSupport<Forecast, Long> implements StdForecastService {
	@Autowired
	protected ForecastDao forecastDao;
	@Autowired
	protected ForecastDtlDao forecastDtlDao;
	@Autowired
	protected BpmServiceClient bpmService;
	@Autowired
	protected SrmBpmService srmBpmService;
	@Autowired
	protected NotifySenderClient notifySenderLogic;

	@Autowired
	protected UserClient userLogic;

	@Autowired
	protected VendorService vendorLogic;

	@Autowired
	protected PortalServiceClient portalDealDataLogic;

	@Autowired
	protected WsRequestLogService wsRequestLogLogic;
	@Autowired
	protected InteractionClient interactLogic;

	@Override
	public Forecast mergeForecast(Forecast object) {
		Forecast old_object = (Forecast) forecastDao.getById(object.getForecastId());
		if (old_object == null) {
			return null;
		}
		// gridDtl1明细
		HashMap<Object, Object> newForecastDtlhm = new HashMap<Object, Object>();// 新的对象明细集合
		for (ForecastDtl detail : object.getForecastDtls()) {
			// gridDtl1明细
			newForecastDtlhm.put(detail.getForecastDtlId(), detail);
		}

		for (ForecastDtl detail : old_object.getForecastDtls()) {
			// gridDtl1明细
			if (!newForecastDtlhm.containsKey(detail.getForecastDtlId())) {
				forecastDtlDao.deleteById(detail.getForecastDtlId());// 在新中不存在的老的数据删除了
			}
		}
		Forecast o = forecastDao.save(object);// 重新保存对象
		return o;
	}

	@Override
	public List<String> getForecastEvents(Long userId, String roleType, Long forecastId) {

		// 20160815---xufq--当前用户 是否有审核权限
		boolean isAuthoritiedToAuditing = bpmService.isAuthoritiedToAuditing(userId.toString(), SrmConstants.BILLTYPE_CGY,
				forecastId.toString());

		List<String> events = new ArrayList<String>();
		Forecast entity = findById(forecastId);
		if (entity == null) {
			return events;
		}
		ForecastState status = entity.getStatus();
		switch (status) {
		case NEW:
			events.add(ForecastState.TOCONFIRM.name());
			break;
		case TOCONFIRM:
			if (isAuthoritiedToAuditing) {
				events.add(ForecastState.TOPASS.name());
				events.add(ForecastState.TONOPASS.name());
			}
			break;
		case TONOPASS:
			// events.add(ForecastEvent.TOSAVE.name());
			events.add(ForecastState.TOCONFIRM.name());
			break;
		default:
			break;
		}
		return events;
	}

	@Override
	public Forecast dealStatus(Long userId, String userName, Long forecastId, ForecastState status, String message) {
		Forecast entity = findById(forecastId);
		if (entity == null) {
			return null;
		}
		switch (status) {
		case NEW:
			break;
		case TOCONFIRM:
			if (entity.getStatus().equals(ForecastState.NEW) || entity.getStatus().equals(ForecastState.TONOPASS)) {
				submitBpm(entity.getForecastId(), userId);
			} else {
				return null;
			}
			break;
		case TOPASS:
			if (entity.getStatus().equals(ForecastState.TOCONFIRM)) {

				entity = approve(entity.getForecastId(), userId, message);
				// 发送知会
				String processInstanceId = bpmService.getProcessInstanceId(SrmConstants.BILLTYPE_CGY, entity.getForecastId().toString());
				srmBpmService.bpmNotify(entity.getCreateUserId(), SrmConstants.BILLTYPE_CGY, "采购预测", entity.getForecastId().toString(),
						entity.getForecastNo().toString(), processInstanceId);

			} else {
				return null;
			}
			break;
		case TONOPASS:
			if (entity.getStatus().equals(ForecastState.TOCONFIRM)) {
				reject(entity.getForecastId(), userId, message);
			} else {
				return null;
			}
			break;
		default:
			break;
		}
		return entity;
	}

	@Override
	public Forecast dealStatus(Long userId, String userName, Long forecastId, ForecastState status, String message, Boolean isAddLog) {
		Forecast entity = findById(forecastId);
		if (entity == null) {
			return null;
		}
		switch (status) {
		case NEW:
			break;
		case TOCONFIRM:
			if (entity.getStatus().equals(ForecastState.NEW) || entity.getStatus().equals(ForecastState.TONOPASS)) {
				// toConfirm(entity, userId);
				submitBpm(entity.getForecastId(), userId);
				// // 记录日志
				addLog(userId, userName, entity.getForecastId(), "采购预测提交审核", SrmConstants.PERFORM_TOCONFIRM, entity.getForecastNo(),
						SrmConstants.PLATFORM_WEB);

			} else {
				return null;
			}
			break;
		case TOPASS:
			if (entity.getStatus().equals(ForecastState.TOCONFIRM)) {
				// 发送知会
				entity = approve(entity.getForecastId(), userId, message);
				String processInstanceId = bpmService.getProcessInstanceId(SrmConstants.BILLTYPE_CGY, entity.getForecastId().toString());
				srmBpmService.bpmNotify(entity.getCreateUserId(), SrmConstants.BILLTYPE_CGY, "采购预测", entity.getForecastId().toString(),
						entity.getForecastNo().toString(), processInstanceId);

			} else {
				return null;
			}
			break;
		case TONOPASS:
			if (entity.getStatus().equals(ForecastState.TOCONFIRM)) {
				reject(entity.getForecastId(), userId, message);
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
	 * 记录审核的操作日志
	 * 
	 * @param userId 操作人ID
	 * @param userName 操作人姓名
	 * @param billPk 审核的单据类型
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

		// create log
		com.huiju.module.log.Log log = Logs.getLog();
		LogMessage.create(log).type(LogType.OPERATION).level(Level.INFO)// 设置日志级别
				.module(SrmConstants.BILLTYPE_CGY)// 设置日志模块 .key(billPk)//
													// 日志信息的主要key
				.action(action)// 操作的动作 .message(message)// 日志的记录内容
								// .result("success")// 日志的操作结果
				.operatorName(userName)// 日志的操作人 .operatorId(userId)// 操作人id
				.businessNo(businessNo);// 单据编号 .terminal(terminal) //终端标识
										// .log();// 调用记录日志

	}

	/**
	 * 提交后事件处理
	 */
	@Override
	protected Forecast afterBpmSubmit(Forecast entity, Long userId, List<User> assignees, Map<String, Object> properties) {
		entity = super.afterBpmSubmit(entity, userId, assignees, properties);
		entity.setStatus(ForecastState.TOCONFIRM);
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
	protected Forecast afterReject(Forecast entity, Long userId, Long createUserId, String message, Map<String, Object> properties) {
		entity = super.afterReject(entity, userId, createUserId, message, properties);
		entity.setStatus(ForecastState.TONOPASS);
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
	protected Forecast afterComplete(Forecast entity, Long userId, Long createUserId, String message, Map<String, Object> properties) {
		// 通知创建人
		entity = super.afterComplete(entity, userId, createUserId, message, properties);
		entity.setStatus(ForecastState.TOPASS);
		entity = save(entity);
		return entity;
	}

	@Override
	public String purchasingApplySearch4String(String clientCode, String purchasingOrgCode, String purchasingGroupCode, String materialCode,
			String plantCode, String forecastMainStartDate, String forecastMainEndDate, String forecastStartDate, String forecastEndDate) {
		JSONObject paramObj = new JSONObject();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			paramObj.put("purchasingGroupCode", purchasingGroupCode);
			paramObj.put("materialCode", materialCode);
			paramObj.put("plantCode", plantCode);
			paramObj.put("forecastMainStartDate", forecastMainStartDate);
			paramObj.put("forecastMainEndDate", forecastMainEndDate);
			paramObj.put("forecastStartDate", forecastStartDate);
			paramObj.put("forecastEndDate", forecastEndDate);

			// 开始同步
			Map<String, String> param = new HashMap<String, String>();
			// 业务场景编码
			param.put("scenarioCode", "getPurchaseApplyNew");
			// JSON数据
			param.put("json", paramObj.toJSONString());
			// 调用RESTFul接口并获取返回值
			// String json = HttpClientUtils.post(interfaceURL, param);
			String json = interactLogic.invoke("getPurchaseApplyNew", paramObj.toJSONString());
			JSONObject jsonMap = JSONObject.parseObject(json);
			JSONObject dataMap = jsonMap.getJSONObject("data");
			JSONObject etReturn = dataMap == null ? null : dataMap.getJSONObject("EtReturn");
			JSONObject returnMap = etReturn == null ? null : etReturn.getJSONObject("item");

			if (!"S".equals(returnMap.getString("Type"))) {
				return "[]";
			}

			try {
				JSONArray itemArr = dataMap.getJSONArray("ItTosrm");
				Iterator<Object> it = itemArr.iterator();
				while (it.hasNext()) {
					JSONObject temp = (JSONObject) it.next();
					JSONObject dataItem = temp.getJSONObject("item");
					Map<String, Object> map = setDataMap(dataItem, purchasingOrgCode);
					if (null != map.get("vendorErpCode")
							&& org.apache.commons.lang3.StringUtils.isNotBlank(map.get("vendorErpCode").toString())) {
						list.add(map);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				JSONObject dataItem = dataMap.getJSONObject("ItTosrm").getJSONObject("item");
				list.add(setDataMap(dataItem, purchasingOrgCode));
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return DataUtils.toJson(list);
	}

	/**
	 * 设置sap返回的数据
	 * 
	 * @param dataItem 数据对象
	 * @param purchasingOrgCode 采购组织
	 * @return 返回map
	 */
	protected Map<String, Object> setDataMap(JSONObject dataItem, String purchasingOrgCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("purchasingOrgCode", purchasingOrgCode);
		map.put("purchasingOrgName", dataItem.getString(""));
		map.put("plantCode", dataItem.getString("Werks"));
		map.put("vendorErpCode", removeZero(dataItem.getString("Banfn")));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("EQ_vendorErpCode", map.get("vendorErpCode"));
		Vendor vendor = vendorLogic.findOne(params);
		if (null != vendor) {
			map.put("vendorCode", vendor.getVendorCode());
			map.put("vendorName", vendor.getVendorName());
		}
		map.put("vendorName", dataItem.getString("Fname1"));
		map.put("materialCode", removeZero(dataItem.getString("Matnr")));
		map.put("materialName", dataItem.getString("Txz01"));
		map.put("unitName", dataItem.getString("Meins"));
		if (StringUtils.isNotEmpty(dataItem.getString("Lfdat"))) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse(dataItem.getString("Lfdat"));
				Calendar forecastMainDate = Calendar.getInstance();
				forecastMainDate.setTime(date);
				map.put("forecastMainDate", forecastMainDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map.put("forecastMainDate", dataItem.getString("Lfdat"));
		map.put("forecastNum", dataItem.getString("Menge"));
		return map;
	}

	/**
	 * 去零
	 * 
	 * @return 返回去零后的值
	 */
	protected String removeZero(String sourceData) {
		if (StringUtils.isBlank(sourceData)) {
			return sourceData;
		}

		boolean flag = true;
		while (flag) {
			flag = sourceData.startsWith("0");
			if (flag) {
				sourceData = sourceData.substring(1, sourceData.length());
			}
		}

		return sourceData;
	}

	@Override
	public List<ForecastDtl> findForecastDtlAll(Map<String, Object> searchParams) {
		Specification<ForecastDtl> spec = QueryUtils.newSpecification(searchParams);
		return forecastDtlDao.findAll(spec);
	}

	@Override
	public String revokeAudit(Long id, Long userId, String userName) {
		try {
			Forecast forecast = revokeBpmSubmit(id, userId, userName);
			// 新建状态
			forecast.setStatus(ForecastState.NEW);
			save(forecast);
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "notOperation";
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
			Forecast entity = findById(id);
			PortalParameters pp = new PortalParameters();
			pp.addPortalMethod(PortalMethodType.TODEAL_DELETE);
			pp.setBillTypeCode(SrmConstants.BILLTYPE_CGY).setBillId(entity.getForecastId().toString());
			portalDealDataLogic.data4Portal(pp);
			deleteById(id);
			addLog(userId, userName, entity.getForecastId(), "采购申请删除,原因:" + message, SrmConstants.PERFORM_DELETE, entity.getForecastNo(),
					SrmConstants.PLATFORM_WEB);

		}
	}

}
