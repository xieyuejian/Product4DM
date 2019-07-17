package com.huiju.srm.srmbpm.service;

import java.util.List;

import com.huiju.bpm.support.service.BpmSupportService;
import com.huiju.srm.srmbpm.entity.Authorize;
import com.huiju.srm.srmbpm.entity.AuthorizeState;

/**
 * 授权单 远程接口 --拓展类
 * 
 */
public interface StdAuthorizeService extends BpmSupportService<Authorize, Long> {
	/**
	 * 获取右键事件
	 * 
	 * @param userId
	 * @param roleType
	 * @param projectId
	 * @return
	 */
	public List<String> getEvents(Long userId, String roleType, Long projectId);

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
	public Authorize dealStatus(Long userId, String userName, Long authorizeId, AuthorizeState status, String message);

	/**
	 * 新的操作日志记录方法
	 * 
	 * @param userId 操作人ID
	 * @param userName 操作人姓名
	 * @param billPk 日志信息的主要key
	 * @param message 日志的记录内容
	 * @param action 操作的动作
	 * @param businessNo 单据编号
	 * @param terminal 操作终端标识
	 */
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal);

	/**
	 * 删除授权单据
	 * 
	 * @param ids
	 */
	public void deleteAuthorize(List<Long> ids);
}
