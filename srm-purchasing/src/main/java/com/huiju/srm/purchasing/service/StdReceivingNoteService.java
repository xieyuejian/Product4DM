package com.huiju.srm.purchasing.service;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.purchasing.entity.Delivery;
import com.huiju.srm.purchasing.entity.ReceivingNote;

/**
 * 收货单远程接口类
 * 
 * @author zhuang.jq
 */
public interface StdReceivingNoteService extends JpaService<ReceivingNote, Long> {
	/**
	 * 收货单冲销
	 * 
	 * @param entity 收货单
	 * @return 收货单
	 */
	public ReceivingNote chargeOff(ReceivingNote entity);

	/**
	 * 获取送货单信息
	 * 
	 * @param params 参数
	 * @param userId 用户ID
	 * @param userName 用户名
	 * @return 结果集合
	 */
	public Map<Boolean, String> getDelivery(String params, Long userId, String userName);

	/**
	 * 送货单点收 --APP
	 * 
	 * @param model 送货单
	 * @param userId 用户ID
	 * @param userName 用户名
	 * @return 结果集合
	 */
	public Map<Boolean, String> receiving(Delivery model, Long userId, String userName);

	/**
	 * 送货单关闭
	 * 
	 * @param id 收货单ID
	 */
	public void close(Long id);

	/**
	 * 批量导入数据
	 * 
	 * @param wb excel工作簿对象
	 * @param userAuthMap 用户资源组条件
	 * @param userId 当前用户id
	 * @param userName 当前用户名称
	 * @param clientCode 客户端编码
	 * @return 返回校验消息
	 */
	public String batchImportExcel(HSSFSheet sheet, Map<String, Object> userAuthMap, Long userId, String userName, String clientCode)
			throws Exception;

	/**
	 * 增加日志
	 */
	public void addLog(Long userId, String userName, Long billPk, String message, String action, String businessNo, String terminal);
}
