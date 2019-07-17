package com.huiju.srm.purchasing.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.purchasing.dto.CensorQualityDto;

/**
 * 质检接口
 * 
 * @author zhuang.jq
 */
@WebService(name = "censorQualityWebService", targetNamespace = "http://www.huiju.com/censorQuality")
public interface CensorQualityWebService {

    /**
     * 质检新增修改
     * 
     * @param censorQuality
     *            质检vo
     * @return 处理结果
     */
    @WebResult(name = "result")
    @WebMethod(operationName = "saveOrUpdateCensorQuality")
    Message saveOrUpdateCensorQuality(@WebParam(name = "censorQualityVo") CensorQualityDto censorQuality);

}
