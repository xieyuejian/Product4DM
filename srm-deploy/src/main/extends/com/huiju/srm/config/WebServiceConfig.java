package com.huiju.srm.config;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.huiju.srm.purchasing.ws.CensorQualityWebService;
import com.huiju.srm.purchasing.ws.PurOrderDetailCloseWebService;
import com.huiju.srm.purchasing.ws.PurchaseOrderWebService;
import com.huiju.srm.purchasing.ws.ReceivingNoteWebService;
import com.huiju.srm.report.ws.SubConsignmentStockWebService;

/**
 * 发布服务配置
 * 
 * @author caiwq
 *
 */
@Configuration
public class WebServiceConfig {
    @Autowired
    private Bus bus;
    @Autowired
    private PurchaseOrderWebService purchaseOrderWebService;
    @Autowired
    private PurOrderDetailCloseWebService purOrderDetailCloseWebService;
    @Autowired
    private ReceivingNoteWebService receivingNoteWebService;
    @Autowired
    private CensorQualityWebService censorQualityWebService;
    @Autowired
    private SubConsignmentStockWebService subConsignmentStockWebService;
    

    @Bean
    public Endpoint purhcaseOrderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, purchaseOrderWebService);
        endpoint.publish("/purchaseOrder");
        return endpoint;
    }

    @Bean
    public Endpoint purchaseOrderCloseEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, purOrderDetailCloseWebService);
        endpoint.publish("/purOrderDetailClose");
        return endpoint;
    }

    @Bean
    public Endpoint materialEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, receivingNoteWebService);
        endpoint.publish("/receivingNote");
        return endpoint;
    }

    @Bean
    public Endpoint censorQualityEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, censorQualityWebService);
        endpoint.publish("/censorQuality");
        return endpoint;
    }
    
    @Bean
    public Endpoint subConsignmentStockEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, subConsignmentStockWebService);
        endpoint.publish("/subConsignmentStock");
        return endpoint;
    }
    /*
     * 可以发布多个ws
     * 
     * @Bean public Endpoint xxxxxEndpoint() { EndpointImpl endpoint = new
     * EndpointImpl(bus, materialWebService); endpoint.publish("/material");
     * return endpoint; }
     */
}