package com.huiju.srm.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huiju.module.cache.CacheService;
import com.huiju.module.config.GlobalParameters;
import com.huiju.module.context.HuijuApplicationContext;
import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.srm.commons.entity.ExpressParamsDtlEntity;
import com.huiju.srm.commons.entity.ExpressResultEntity;
import com.huiju.srm.commons.entity.ExpressState;
import com.huiju.srm.ws.entity.WsRequestLog;
import com.huiju.srm.ws.service.WsRequestLogService;

/**
 * 快递100接口工具类
 * 
 * @author hongwl
 *
 */
public class ExpressUtils {

	private static String enterpriseKey = GlobalParameters.getString("srm.express.expressEnterpriseKey");
	private static String customer = GlobalParameters.getString("srm.express.expressCustomer");
	/**
	 * 系统参数获取访问频率，默认4H
	 */
	private static Integer expressRate = GlobalParameters.getInteger("srm.express.expressRate", 4);

	/**
	 * 实时查询请求地址
	 */
	private static String expressQueryUrl = GlobalParameters.getString("srm.express.expressQueryUrl");
	/**
	 * 单号归属快递公司请求地址
	 */
	private static String expressNoToCompanyUrl = GlobalParameters.getString("srm.express.expressNoToCompanyUrl");
	/**
	 * 单位时间
	 */
	private static Integer spanTime = GlobalParameters.getInteger("srm.express.spanTime");
	/**
	 * 允许接口调用次数最大值
	 */
	private static Integer maxInterNum = GlobalParameters.getInteger("srm.express.maxInterNum");

	private static Integer timeOut = 10000;
	/**
	 * 计数器，默认1
	 */
	private static Integer countNum = 1;
	/**
	 * 缓存service
	 */
	private static CacheService cacheService = HuijuApplicationContext.getBean("RedisCacheImpl", CacheService.class);
	/**
	 * 日志service
	 */
	private static WsRequestLogService wsRequestLogService = HuijuApplicationContext.getBean(WsRequestLogService.class);

	/**
	 * 根据单号判断归属快递公司
	 * 
	 * @param expressNo 快递单号
	 */
	public static ExpressResultEntity getExpressCompanyByNo(String expressNo) {
		StringBuffer bf = new StringBuffer("");
		ExpressResultEntity deliveryExpressDtl = new ExpressResultEntity();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(expressNoToCompanyUrl);
			sb.append(expressNo);
			sb.append("&key=").append(enterpriseKey);
			URL url = new URL(sb.toString());
			URLConnection con = url.openConnection();
			con.setAllowUserInteraction(false);
			con.setConnectTimeout(timeOut);
			InputStream urlStream = url.openStream();
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				bf.append(line);
			}
			deliveryExpressDtl.setStatus("0");
			deliveryExpressDtl.setMessage("接口调用成功");
			String returnInfo = bf.toString();
			returnInfo.trim();
			reader.close();
			urlStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			bf.setLength(0);
			deliveryExpressDtl.setStatus("1");
			deliveryExpressDtl.setMessage("接口调用异常");
		}
		JSONArray objectArr = JSONObject.parseArray(bf.toString());
		if (objectArr != null && objectArr.size() > 0) {
			JSONObject object = objectArr.getJSONObject(0);// 如果返回多个快递公司，默认取第一个
			String expressCompanyCode = object.getString("comCode");
			deliveryExpressDtl.setExpressCompanyCode(expressCompanyCode);// 快递公司编码
		} else {
			deliveryExpressDtl.setMessage("接口调用成功，但快递100并未找到对应快递公司。");
		}
		return deliveryExpressDtl;
	}

	/**
	 * 实时查询快递单号
	 * 
	 * @param expressParamsDtlEntity 参数实体
	 * @return
	 */
	public static List<ExpressResultEntity> synQueryData(ExpressParamsDtlEntity expressParamsDtlEntity) {
		Integer interfaceCountNum = null;
		if (cacheService.get("srm_interfaceCountNum") != null) {
			interfaceCountNum = (Integer) cacheService.get("srm_interfaceCountNum");
		}
		if (StringUtils.isNotBlank(expressParamsDtlEntity.getNum())) {// 单号为空直接返回空
			if (interfaceCountNum == null) {// 接口第一次调用或者单位时间已过
				if (cacheService.get(expressParamsDtlEntity.getNum()) == null) {// 缓存无此单号数据则调用接口
					if (expressParamsDtlEntity.getSpanTime() != null) {
						spanTime = expressParamsDtlEntity.getSpanTime();
					}
					cacheService.put("srm_interfaceCountNum", countNum, spanTime);
					return invokeInterface(expressParamsDtlEntity);
				} else {
					return null;
				}
			} else {// 缓存有接口调用次数的数据
				if (expressParamsDtlEntity.getMaxInterNum() != null) {
					maxInterNum = expressParamsDtlEntity.getMaxInterNum();
				}
				if (maxInterNum.compareTo(interfaceCountNum) > 0) {// 允许调用接口的次数大于缓存数据的次数
					cacheService.setNotUpdateTime("srm_interfaceCountNum", interfaceCountNum + 1);// 调用接口的次数加1，并放进redis
					return invokeInterface(expressParamsDtlEntity);
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	/**
	 * @Description:
	 * @param @param expressParamsDtlEntity
	 * @param @return 参数
	 * @return List<ExpressResultEntity> 返回类型
	 */
	private static List<ExpressResultEntity> invokeInterface(ExpressParamsDtlEntity expressParamsDtlEntity) {
		StringBuilder param = new StringBuilder("{");
		param.append("\"com\":\"").append(expressParamsDtlEntity.getCom()).append("\"");
		param.append(",\"num\":\"").append(expressParamsDtlEntity.getNum()).append("\"");
		param.append(",\"phone\":\"").append(expressParamsDtlEntity.getPhone()).append("\"");
		param.append(",\"from\":\"").append(expressParamsDtlEntity.getFrom()).append("\"");
		param.append(",\"to\":\"").append(expressParamsDtlEntity.getTo()).append("\"");
		if (expressParamsDtlEntity.getResult() != null && 1 == expressParamsDtlEntity.getResult()) {
			param.append(",\"resultv2\":1");
		} else {
			param.append(",\"resultv2\":0");
		}
		param.append("}");
		Map<String, String> params = new HashMap<String, String>();
		params.put("customer", customer);
		String sign = ExpressMD5Utils.encode(param + enterpriseKey + customer);
		params.put("sign", sign);
		params.put("param", param.toString());
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createdate = sdf.format(date); // 存放缓存的时间为配置时间（H）转为秒
		cacheService.put(expressParamsDtlEntity.getNum(), createdate, expressRate * 60 * 60);
		return post(params, expressParamsDtlEntity.getNum());
	}

	/**
	 * @Description: 调用快递100接口
	 * @param @param params 参数
	 * @param @param expressNo 单号
	 * @param @return 参数
	 * @return List<ExpressResultEntity> 返回类型
	 */
	public static List<ExpressResultEntity> post(Map<String, String> params, String expressNo) {
		StringBuffer response = new StringBuffer("");
		WsRequestLog wrlog = new WsRequestLog();
		List<ExpressResultEntity> expressResultEntitys = new ArrayList<ExpressResultEntity>();
		BufferedReader reader = null;
		try {
			StringBuilder builder = new StringBuilder();
			wrlog = wsRequestLogService.createTargetErpLog(SrmConstants.SRM_EXPRESS_CODE, expressNo, DataUtils.toJson(params));
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (builder.length() > 0) {
					builder.append('&');
				}
				builder.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				builder.append('=');
				builder.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}

			byte[] bytes = builder.toString().getBytes("UTF-8");
			URL url = new URL(expressQueryUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(bytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(bytes);

			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			wsRequestLogService.addFailLog(wrlog, response.toString());
		} finally {
			try {
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject parseObject = JSONObject.parseObject(response.toString());
		if (parseObject.get("data") != null) {
			JSONArray parseArray = JSONObject.parseArray(parseObject.get("data").toString());
			if (parseArray != null && parseArray.size() > 0) {
				for (int i = 0; i < parseArray.size(); i++) {
					ExpressResultEntity expressResultEntity = new ExpressResultEntity();
					JSONObject object = parseArray.getJSONObject(i);
					Date date = new Date();
					Calendar calendar = Calendar.getInstance();
					try {
						if (object.get("time") != null) {
							date = sdf.parse(object.get("time").toString());
							calendar.setTime(date);
							expressResultEntity.setUpdateTime(calendar);// 物流信息内容
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					expressResultEntity.setContext(object.getString("context"));// 物流信息内容
					// 快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态
					expressResultEntity.setState(express100ForState(parseObject.getString("state")));
					expressResultEntity.setMessage(parseObject.getString("message"));// 接口返回消息
					expressResultEntity.setStatus(parseObject.getString("status"));// 接口返回状态
					expressResultEntitys.add(expressResultEntity);
				}
			}
		} else {// 当没有请求到物流数据时，删除缓存
			cacheService.remove(expressNo);
		}
		// 记录成功日志
		wsRequestLogService.addSuccessLog(wrlog, response.toString());
		return expressResultEntitys;
	}

	/**
	 * @Description: 快递100物流状态转换
	 * @param @param state
	 * @param @return 参数
	 * @return ExpressState 返回类型
	 */
	public static ExpressState express100ForState(String state) {
		if (StringUtils.isNotBlank(state)) {
			switch (state) {
			case "0":
				return ExpressState.ONTHEWAY;
			case "1":
				return ExpressState.COLLECTED;
			case "2":
				return ExpressState.DIFFICULT;
			case "3":
				return ExpressState.RECEIVED;
			case "4":
				return ExpressState.REFUND;
			case "5":
				return ExpressState.CITYDELIVERY;
			case "6":
				return ExpressState.TRANSFER;
			case "7":
				return ExpressState.RETURN;
			default:
				break;
			}
		}
		return null;
	}

}