
package com.huiju.srm.purchasing.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huiju.module.data.jpa.utils.DataUtils;
import com.huiju.srm.commons.utils.SrmConstants;
import com.huiju.srm.commons.ws.utils.Message;
import com.huiju.srm.commons.ws.utils.MessageContent;
import com.huiju.srm.commons.ws.utils.ServiceSupport;
import com.huiju.srm.purchasing.dto.ReceivingNoteCheckDto;
import com.huiju.srm.purchasing.entity.ReceivingNote;
import com.huiju.srm.purchasing.service.ReceivingNoteService;
import com.huiju.srm.ws.entity.WsLog;
import com.huiju.srm.ws.handler.SoapMessageMgr;
import com.huiju.srm.ws.service.WsLogService;

/**
 * @author zhuang.jq
 */
@Component // 由Spring管理
@WebService(serviceName = "receivingNoteCheckWebService", targetNamespace = "http://www.huiju.com/receivingNote", endpointInterface = "com.huiju.srm.purchasing.ws.ReceivingNoteCheckWebService")
public class ReceivingNoteCheckWebServiceImpl extends ServiceSupport implements ReceivingNoteCheckWebService {

    @Autowired
    protected ReceivingNoteService receivingNoteLogic;

    @Autowired
    protected WsLogService wsLogLogic;

    // 请求报文本地线程缓存
    private SoapMessageMgr soapMessageMgr;

    @Override
    public Message receivingNoteCheck(List<ReceivingNoteCheckDto> orders) {
        // 请求参数
        String requestContent = StringUtils.isBlank(soapMessageMgr.getSoapMessageStr()) ? DataUtils.toJson(orders)
                : soapMessageMgr.getSoapMessageStr();
        List<ReceivingNote> list = new ArrayList<ReceivingNote>();
        MessageContent content = createContent();
        WsLog log = wsLogLogic.createSourceErpLog(SrmConstants.SRM_RECEIVINGNOTESERVICE_CODE, requestContent);
        try {
            if (!isValid(orders, content)) {
                wsLogLogic.addErrorLog(log, error(content).toString());
                return error(content);
            }
            for (ReceivingNoteCheckDto noteVo : orders) {
                Map<String, Object> maps = new HashMap<String, Object>();
                maps.put("EQ_materialCertificateYear", noteVo.getMaterialCertificateYear());
                maps.put("EQ_materialCertificateItem", noteVo.getMaterialCertificateItem());
                maps.put("EQ_materialCertificateCode", noteVo.getMaterialCertificateCode());
                ReceivingNote rn = receivingNoteLogic.findOne(maps);
                list.add(rn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            wsLogLogic.addFailLog(log, failed(e, content).toString());
            return failed(e, content);
        }
        wsLogLogic.addSuccessLog(log, DataUtils.toJson(list));
        return success(DataUtils.toJson(list));
    }

}
