package com.huiju.srm.ws.entity;

import java.io.Serializable;

/**
 * 接口日志分析统计
 * 
 * @author ZJQ
 */
public class WsLogVo implements Serializable {

    private static final long serialVersionUID = -5880439667289753888L;

    String interfaceName;
    String interfaceCode;
    String requestDate;
    String requestCount;
    Integer successFlag;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(String interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(String requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(Integer successFlag) {
        this.successFlag = successFlag;
    }

}
