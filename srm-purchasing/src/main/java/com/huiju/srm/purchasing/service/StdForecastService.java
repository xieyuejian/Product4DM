package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import com.huiju.bpm.support.service.BpmSupportService;
import com.huiju.srm.purchasing.entity.Forecast;
import com.huiju.srm.purchasing.entity.ForecastDtl;
import com.huiju.srm.purchasing.entity.ForecastState;

/**
 * 采购预测Service接口
 * 
 * @author bairx
 */
public interface StdForecastService extends BpmSupportService<Forecast, Long> {

	/**
	 * 批量删除
	 * 
	 * @param ids 单据id集合
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param message 原因
	 */
	public void removeByIds(List<Long> ids, Long userId, String userName, String message);

	/**
	 * 同步 <code>Forecast</code>进数据库.
	 * 
	 * @param object 同步Forecast对象
	 * @return 已经保存进数据库的实例对象
	 */
	public Forecast mergeForecast(Forecast object);

	/**
	 * 获取右键事件
	 * 
	 * @param userId 登录用户Id
	 * @param roleType 登录用户的角色类型
	 * @param poId 单据Id
	 * @return
	 */
	public List<String> getForecastEvents(Long userId, String roleType, Long forecastId);

	/**
	 * 动作处理
	 * 
	 * @param userId 登录用户Id
	 * @param userName 登录用户名称
	 * @param id 单据Id
	 * @param status 单据状态
	 * @param message 审核意见
	 * @return
	 */
	public Forecast dealStatus(Long userId, String userName, Long forecastId, ForecastState status, String message);

	/**
	 * 获取细单
	 * 
	 * @param searchParams 查询条件
	 * @return
	 */
	public List<ForecastDtl> findForecastDtlAll(Map<String, Object> searchParams);

	/**
	 * 记录操作日志
	 * 
	 * @param userId 用户id
	 * @param userName 用户名
	 * @param billPk 单据编码
	 * @param oldStatus 原状态
	 * @param newStatus 当前状态
	 * @param message 消息
	 */
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal);

	/**
	 * 搜索
	 * 
	 * @param clientCode
	 * @param purchasingOrgCode
	 * @param purchasingGroupCode
	 * @param materialCode
	 * @param plantCode
	 * @param forecastMainStartDate
	 * @param forecastMainEndDate
	 * @param forecastStartDate
	 * @param forecastEndDate
	 * @return
	 */
	public String purchasingApplySearch4String(String clientCode, String purchasingOrgCode, String purchasingGroupCode, String materialCode,
			String plantCode, String forecastMainStartDate, String forecastMainEndDate, String forecastStartDate, String forecastEndDate);

	/**
	 * 撤销审批
	 * 
	 */
	public String revokeAudit(Long id, Long userId, String userName);

	public Forecast dealStatus(Long userId, String userName, Long forecastId, ForecastState status, String message, Boolean isAddLog);
}
