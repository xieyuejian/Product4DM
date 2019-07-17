package com.huiju.srm.ws.handler;

import java.io.Serializable;

/**
 * 请求报文本地线程缓存
 * 
 * @author ZJQ
 */
public class SoapMessageMgr implements Serializable {

    private static final long serialVersionUID = -7000774052597620033L;

    private String soapMessageStr = "";

    /** 为了同现程调用 */
    static ThreadLocal<SoapMessageMgr> localMgr = new ThreadLocal<SoapMessageMgr>();

    public SoapMessageMgr() {
    }

    public static SoapMessageMgr getCurrentInstance() {
        SoapMessageMgr mgr = localMgr.get();
        if (mgr == null) {
            mgr = new SoapMessageMgr();
            localMgr.set(mgr);
        }
        return mgr;
    }

    public static void removeInstance() {
        SoapMessageMgr mgr = localMgr.get();
        if (mgr == null) {
            return;
        }
        localMgr.remove();
    }

    public String getSoapMessageStr() {
        return soapMessageStr;
    }

    public void setSoapMessageStr(String soapMessageStr) {
        this.soapMessageStr = soapMessageStr;
    }

}
