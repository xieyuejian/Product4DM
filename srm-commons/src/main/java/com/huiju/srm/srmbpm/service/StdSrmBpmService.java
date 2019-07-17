package com.huiju.srm.srmbpm.service;

import java.util.List;
import java.util.Map;

import com.huiju.core.sys.entity.User;
import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.srmbpm.entity.AuditChat;
import com.huiju.srm.srmbpm.entity.SrmBpm;

/**
 * SrmBpm接口
 * 
 * @author wangmx
 *
 */
public interface StdSrmBpmService extends JpaService<AuditChat, Long> {
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
	public String pressingforapproval(SrmBpm bpm);

	/**
	 * 往当前的审核节点中授权审核人
	 * 
	 * @param params 参数集合
	 * @return
	 */
	public String insertUsersToProcessPoint(Map<String, Object> params);

	/**
	 * 往当前的审核节点中加签审核人
	 * 
	 * @param params 参数集合
	 * @return
	 */
	public String insertRolesToProcessPoint(Map<String, Object> params);

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
	public void bpmNotify(Long createUserId, String billTypeCode, String billTypeName, String billId, String billNo,
			String processInstanceId);

	/**
	 * 获取该单据中已审核过的审核人或单据提交者
	 * 
	 * @param billTypeCode 单据编码
	 * @param billId 单据Id
	 * @param type 类型(回复或新增)
	 * @param userId 用户Ids
	 * @param operatorId 当前操作人Id
	 * @return
	 */
	public List<User> getAuditPerson(String billTypeCode, String billId, String type, Long userId, Long operatorId);

	/**
	 * 新增回复审批沟通
	 * 
	 * @param auditChat 审批沟通实体
	 * @param moduleName 模块名称
	 * @return
	 */
	public String addAuditChat(AuditChat auditChat, String moduleName);

	/**
	 * 回复审批沟通
	 * 
	 * @param auditChat 审批沟通实体
	 * @param moduleName 模块名称
	 * @return
	 */
	public String replyAuditChat(AuditChat auditChat, String moduleName);

	/**
	 * ，只有该单据的提报人和该单据审核人有权限查看全部的沟通记录
	 * 
	 * @param userId 操作人Id
	 * @param billTypeCode 模块编码
	 * @param billId 单据Id
	 * @return
	 */
	public boolean canFindData(Long userId, String billTypeCode, String billId);
}
