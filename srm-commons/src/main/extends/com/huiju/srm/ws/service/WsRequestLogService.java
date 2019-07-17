package com.huiju.srm.ws.service;

import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.ws.entity.WsRequestLog;

/**
 * @author ZJQ
 */
public interface WsRequestLogService extends JpaService<WsRequestLog,Long> { 

    public WsRequestLog findWsRequestLog(Long id);
    
    
	/**
	 * 创建log对象
	 * 
	 * @param interactCode
	 *            接口编码
	 * @param businessCodes 业务单据编码
	 * @param requestContent
	 *            请求报文
	 * @return 返回日志对象
	 */
	public WsRequestLog createTargetErpLog(String interfaceCode,String businessCodes, String requestContent);

	/**
	 * 错误信息
	 * 
	 * @param log
	 *            日志对象
	 */
	public void addErrorLog(WsRequestLog log, String responseContent);

	/**
	 * 异常日志
	 * 
	 * @param log
	 *            志对象
	 */
	public void addSuccessLog(WsRequestLog log, String responseContent);

	/**
	 * 失败日志，异常信息
	 * 
	 * @param log
	 *            日志对象
	 */
	public void addFailLog(WsRequestLog log, String responseContent);



}
