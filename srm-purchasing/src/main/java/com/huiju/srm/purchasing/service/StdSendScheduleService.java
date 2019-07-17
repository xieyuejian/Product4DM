package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import com.huiju.bpm.support.service.BpmSupportService;
import com.huiju.srm.purchasing.entity.PurchaseOrderDetail;
import com.huiju.srm.purchasing.entity.SendSchedule;
import com.huiju.srm.purchasing.entity.SendScheduleCommon;

/**
 * 送货排程 远程接口
 * 
 * @author zhuang.jq
 */
public interface StdSendScheduleService extends BpmSupportService<SendSchedule, Long> {
    /**
     * 获取采购订单管控点
     * 
     * @param entity
     *            实体对象
     * @param code
     *            管控点Code
     * @return 是否同步
     * @throws Exception
     */
    public String getSendScheduleControl(SendSchedule entity, String code);

    /**
     * 获取权限
     * 
     * @param userId
     *            当前用户
     * @param roleType
     *            橘色
     * @param id
     *            单据id
     * @return 权限集合
     */
    public List<String> getSendScheduleEvents(Long userId, String roleType, Long id);

    /**
     * 处理流程化
     * 
     * @param sendScheduleId
     *            排程id
     * @param status
     *            状态
     * @param message
     *            内容
     * @return 排程
     * @throws Exception
     */
    public SendSchedule dealSendSchedule(Long sendScheduleId, String status, String message, Long userId);

    /**
     * 供应商接受送货排程
     * 
     * @param object
     *            送货排程
     * @return 保存后的排程 (non-Javadoc)
     * @see com.harmony.srm.scm.logic.BaseSendScheduleRemote#acceptSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
     */
    public SendSchedule acceptSendSchedule(SendSchedule object, Long sendId);

    /**
     * 保存排程
     * 
     * @param object排程
     * @throws Exception
     * @return排程 (non-Javadoc)
     * @see com.harmony.srm.scm.logic.BaseSendScheduleRemote#saveSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
     */
    public SendSchedule saveSendSchedule(SendSchedule object, String submitFlag, Long userId, String userName);

    /**
     * 同步sap
     * 
     * @param model送货排程
     */
    public Boolean doSync(SendSchedule model);

    /**
     * 获得明细列表
     * 
     * @param searchParam
     *            查询载体
     * @return 排程中间类
     */
    public List<SendScheduleCommon> findSendScheduleCommon(Map<String, Object> searchParam);

    /**
     * 删除排程
     * 
     * @param object排程
     * @return排程 (non-Javadoc)
     * @see com.harmony.srm.scm.logic.BaseSendScheduleRemote#deleteSendSchedules(java.util.List)
     */
    public void deleteSendSchedules(List<Long> ids, String userId, String userName);

    /**
     * 强制变更排程
     * 
     * @param object
     *            排程
     * @throws Exception
     * @return排程 (non-Javadoc)
     * @see com.harmony.srm.scm.logic.BaseSendScheduleRemote#changeSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
     */
    public SendSchedule changeSendSchedule(SendSchedule entity, Long userId, String userName);

    /**
     *
     * 修改排程
     * 
     * @param object
     *            排程
     * @throws Exception
     * @return排程 (non-Javadoc)
     * @see com.harmony.srm.scm.logic.BaseSendScheduleRemote#mergeSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
     */
    public SendSchedule mergeSendSchedule(SendSchedule entity, String submitFlag, String userId, String userName);

    /**
     * 取消排程
     * 
     * @param object排程
     * @return排程 (non-Javadoc)
     * @see com.harmony.srm.scm.logic.BaseSendScheduleRemote#cancelSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
     */
    public SendSchedule cancelSendSchedule(SendSchedule entity);

    /**
     * 供应商拒绝送货排程
     * 
     * @param object
     *            送货排程
     * @return 保存后的排程 (non-Javadoc)
     * @see com.harmony.srm.scm.logic.BaseSendScheduleRemote#refuseSendSchedule(com.harmony.srm.scm.entity.SendSchedule)
     */
    public SendSchedule refuseSendSchedule(SendSchedule entity, Long sendId);

    /**
     * 获取待处理送货排程的数据id
     * 
     * @param userId
     *            用户ID
     * @return ID集合
     */
    public List<Long> findIdByStatus(Long userId);

    /**
     * 获取订单明细数据
     * 
     * @param orderId
     *            订单ID
     * @return 订单明细ID
     */
    public List<PurchaseOrderDetail> findOrderDtlAll(Long orderId);

    /**
     * 记录审核的操作日志
     * 
     * @param userId
     *            操作人ID
     * @param userName
     *            操作人姓名
     * @param billPk
     *            审核的单据类型
     * @param message
     *            消息
     * @param action
     *            动作
     * 
     */
    public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo,
            String terminal);

    /**
     * 撤销审批
     * 
     * @param id
     *            单据Id
     * @param userId
     *            操作人Id
     * @param userName
     *            操作人名称
     * @return
     */
    public String revokeAudit(Long id, Long userId, String userName);
}
