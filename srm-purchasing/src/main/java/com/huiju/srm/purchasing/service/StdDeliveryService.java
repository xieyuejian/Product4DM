package com.huiju.srm.purchasing.service;

import java.util.List;
import java.util.Map;

import com.huiju.module.data.common.Page;
import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.LogisticsDtlDtl;

/**
 * <pre>
 * 送货管理数据表Remote
 * </pre>
 * 
 * @author wz
 */
public interface StdDeliveryService extends JpaService<Delivery, Long> {

	public String findDeliveryAllPage(Page<Delivery> page, Map<String, Object> map);

	/**
	 * APP分页
	 * 
	 * @param searchParams
	 * @param specialParams
	 * @return
	 */
	public String page4App(Map<String, Object> searchParams, Map<String, Object> specialParams);

	/**
	 * 取消送货单
	 * 
	 * @param id 送货单
	 * @return 取消后的送货单
	 */
	public Delivery cancelDelivery(Long id);

	/**
	 * 同步sap --APP
	 * 
	 * @param object 要同步的单据
	 */
	public Delivery syncDelivery(Delivery object);

	/**
	 * 取消送货明细
	 * 
	 * @param id 要取消的明细id
	 */
	public void cancelDetail(Long id);

	/**
	 * 关闭送货明细
	 * 
	 * @param id 要关闭的明细id
	 */
	public void closeDetail(Long id);

	/**
	 * 保存送货单 --APP
	 * 
	 * @param delivery 送货单
	 * @return DeliVery实例类
	 */
	public Delivery saveDelivery(Delivery delivery);

	/**
	 * 更新送货单 -- APP
	 * 
	 * @param delivery 送货单
	 * @return DeliVery实例类
	 */
	public Delivery updateDelivery(Delivery delivery);

	/**
	 * 删除送货单
	 * 
	 * @param ids 送货单ids
	 */
	public void deleteDelivery(List<Long> ids);

	/**
	 * 送货单定时器方法
	 */
	public Map<String, Object> deliveryJobMethod();

	/**
	 * 增加日志
	 */
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal);

	/**
	 * 送货单查看,扫描收货查看 --APP
	 * 
	 * @param searchParams(送货单查看-->searchParams传EQ_deliveryId;扫描收货查看
	 *            -->searchParams传EQ_deliveryCode)
	 * @return
	 */
	public String findDeliveryOne(Map<String, Object> searchParams);

	/**
	 * 送货单点收,可以分批收货，全部收完，送货单状态为已完成（只有发布状态的送货单才可以点收） 需要更新送货单的已收货数量，对应采购订单明细的已收货数量
	 * 
	 * @param deliveryJson {Delivery对象json字符串,deliveryDtls明细对象字符串}
	 * @param userId 用户id
	 * @param userName 用户名称
	 * @return
	 */
	public String receiving(String deliveryJson, Long userId, String userName);

	/**
	 * 根据快递信息明细获取物流信息
	 * 
	 * @param deliveryExpressDtlId 快递信息明细id
	 * @return
	 */
	public List<LogisticsDtlDtl> findLogisticsDtlDtlAll(Long deliveryExpressDtlId);

}
