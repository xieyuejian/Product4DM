package com.huiju.srm.purchasing.ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.purchasing.dto.PurOrderDetailCloseDto;

/**
 * 订单关闭接口
 * 
 * @author zhuang.jq
 */
@WebService(name = "purOrderDetailCloseWebService", targetNamespace = "http://www.huiju.com/purOrderDetailClose")
public interface PurOrderDetailCloseWebService {
    /**
     * 关闭订单的方法
     * 
     * @param PurOrderDetails
     *            采购订单明细
     * @return 处理结果
     */
    @WebResult(name = "result")
    @WebMethod(operationName = "updatePurOrderCloseState")
    Message updatePurOrderCloseState(
            @WebParam(name = "purOrderDetailClose") List<PurOrderDetailCloseDto> purOrderDetails);
}
