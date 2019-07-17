package com.huiju.srm.ws.service;

import java.util.Calendar;

import com.huiju.module.data.jpa.service.JpaService;
import com.huiju.srm.ws.entity.WsLog;

//import org.apache.commons.lang.StringEscapeUtils;


/**
 * @author ZJQ
 */
public interface WsLogService extends JpaService<WsLog,Long> {
 
    public WsLog findWsLog(Long id);
    
    
    
    /**
	 * 创建log对象
	 * 
	 * @param interactCode 接口编码 
	 * @param requestContent 请求保温
	 * @return 返回日志对象
	 */
	public WsLog createSourceErpLog(String interfaceCode, String requestContent);

	/**
	 * 错误日志
	 * 
	 * @param log 日志对象
	 */
	public void addErrorLog(WsLog log, String responseContent);

	/**
	 *  成功日志
	 * 
	 * @param log 日志对象
	 */
	public void addSuccessLog(WsLog log, String responseContent);
	

	/**
	 * 失败日志，异常日志
	 * 
	 * @param log 日志对象
	 */
	public void addFailLog(WsLog log, String responseContent);
	
}
