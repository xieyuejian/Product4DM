package com.huiju.srm.ws.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.huiju.module.data.jpa.service.JpaServiceImpl;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.utils.IdUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.ws.entity.WsLog;
import com.huiju.srm.ws.entity.WsLogDtl;

/**
 * @author ZJQ
 */
@Service
public class WsLogServiceImpl extends JpaServiceImpl<WsLog, Long> implements WsLogService {

	@Autowired
	private PlatformTransactionManager transactionManager;

	protected final Map<String, String> interfaceMaps = new HashMap<String, String>() {
		private static final long serialVersionUID = -4468284336043585303L;

		{
			put("SRM_MATERIAL", "物料主数据");
			put("SRM_DELIVERY", "送货信息");
			put("SRM_PURCHASINGORDER", "采购订单");
			put("SRM_RECEIVING", "收退货信息");
			put("SRM_QUALITY", "质检信息");
			put("SRM_PREINVOICE", "预制发票");
			put("SRM_STOCK", "库存信息");
			put("SRM_SUBCONSIGNMENT", "寄售分包库存");

			// 入站接口编码
			put("SRM_001", "公司主数据");
			put("SRM_002", "汇率主数据");
			put("SRM_003", "集团主数据");
			put("SRM_004", "物料主数据");
			put("SRM_005", "订单接口");
			put("SRM_006", "订单关闭接口");
			put("SRM_007", "收退货接口");
			put("SRM_008", "质检接口");
			put("SRM_009", "寄售库存");
		}
	};

	protected final int SUCCESS_CODE = 1;
	protected final int FAIL_CODE = 2;
	protected final int ERROR_CODE = 0;
	protected final String TARGER_SYSTEM = "SRM";

	@Override
	protected Class<WsLog> getEntityClass() {
		return WsLog.class;
	}


	@Override
	public WsLog findWsLog(Long id) {
		WsLog entity = findById(id);
		if (entity == null) {
			return null;
		}
		List<WsLogDtl> dtls = entity.getWsLogDtls();
		if (dtls != null && dtls.size() > 0) {
			for (WsLogDtl dtl : dtls) {
				entity.setRequestContent(StringUtils.isBlank(dtl.getRequestContent()) ? null
						: dtl.getRequestContent().replaceAll("\\s*((2[0-3])|([0-1]?\\d)):[0-5]?\\d(:[0-5]?\\d)", "[HH:mm:ss]"));
				entity.setResponseContent(StringUtils.isBlank(dtl.getResponseContent()) ? null
						: dtl.getResponseContent().replaceAll("\\s*((2[0-3])|([0-1]?\\d)):[0-5]?\\d(:[0-5]?\\d)", "[HH:mm:ss]"));
			}
		}
		return entity;
	}

	
	
	//----------------------------------------add by cwq on 2019-04-25 16:01:49 ---------------------------------------------------------
	/**
	 * 创建log对象
	 * 
	 * @param interactCode
	 *            接口编码
	 * @param requestContent
	 *            请求保温
	 * @return 返回日志对象
	 */
	public WsLog createSourceErpLog(String interfaceCode, String requestContent) {
		WsLog log = createLog(interfaceCode, requestContent);
		log.setSourceSystem(SrmConstants.SOURCE_SYSTEM);
		return log;
	}

	/**
	 * 创建log对象
	 * 
	 * @param interfaceCode
	 * @param requestContent
	 * @return
	 */
	private WsLog createLog(String interfaceCode, String requestContent) {
		WsLog log = new WsLog();
		log.setRequestId(IdUtils.genNextId());
		log.setLogId(IdUtils.genNextId());
		log.setInterfaceCode(interfaceCode);
		log.setInterfaceName(interfaceMaps.get(interfaceCode));
		log.setRequestTime(Calendar.getInstance());
		log.setTargetSystem(TARGER_SYSTEM);
		WsLogDtl logDtl = new WsLogDtl();
		logDtl.setLogDtlId(IdUtils.genNextId());
		logDtl.setRequestContent(requestContent);
		logDtl.setWsLog(log);
		List<WsLogDtl> logDtls = new ArrayList<WsLogDtl>();
		logDtls.add(logDtl);
		log.setWsLogDtls(logDtls);
		return log;
	}

	/**
	 * 错误日志
	 * 
	 * @param log
	 *            日志对象
	 */
	public void addErrorLog(WsLog log, String responseContent) {
		log.setSuccessflag(ERROR_CODE);
		addLog(log, responseContent);
	}

	/**
	 * 成功日志
	 * 
	 * @param log
	 *            志对象
	 */
	public void addSuccessLog(WsLog log, String responseContent) {
		log.setSuccessflag(SUCCESS_CODE);
		addLog(log, responseContent);
	}

	/**
	 * 失败日志，异常日志
	 * 
	 * @param log
	 *            日志对象
	 */
	public void addFailLog(WsLog log, String responseContent) {
		log.setSuccessflag(FAIL_CODE);
		addLog(log, responseContent);
	}
	
	/**
	 * 统一插入数据入口
	 * 
	 * @param log
	 *            日志对象
	 * @param responseContent
	 *            响应保温
	 */
	private void addLog(WsLog log, String responseContent) {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		log.getWsLogDtls().get(0).setResponseContent(responseContent);
		log.setResponseTime(Calendar.getInstance());

		try {
			dao.save(log);
			transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				transactionManager.rollback(status);
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
		}
	}

	//----------------------------------------add by cwq on 2019-04-25 16:01:49 ---------------------------------------------------------
}
