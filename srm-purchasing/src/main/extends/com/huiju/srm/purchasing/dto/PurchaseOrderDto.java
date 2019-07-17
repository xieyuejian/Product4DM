package com.huiju.srm.purchasing.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.huiju.module.util.StringUtils;
import com.huiju.srm.commons.ws.utils.Key;

/**
 * 采购订单
 * 
 * @author CWQ
 */
public class PurchaseOrderDto implements Serializable {
    private static final long serialVersionUID = 334059748711537547L;;

    @Key(name = "采购订单类型")
    @NotNull(message = "采购订单类型不能为空")
    @Length(max = 50, message = "采购订单类型超出限制{max}")
    private String orderType;

    @Key(name = "SAP采购订单号")
    @NotBlank(message = "SAP采购订单号不能为空")
    @Length(max = 50, message = "SAP采购订单号超出限制{max}")
    private String erpPurchaseOrderNo;

    @Key(name = "采购订单日期")
    @NotBlank(message = "采购订单日期不能为空")
    //@FormatDate(pattern = "yyyy-MM-dd", message = "采购订单日期正确格式为yyyy-MM-dd")
    private String purchaseOrderTime;

    @Key(name = "SAP供应商代码")
    @NotBlank(message = "SAP供应商代码不能为空")
    @Length(max = 100, message = "SAP供应商代码超出限制{max}")
    private String vendorErpCode;

    @Key(name = "采购组织代码")
    @NotBlank(message = "采购组织代码不能为空")
    @Length(max = 50, message = "采购组织代码超出限制{max}")
    private String purchasingOrgCode;

    @Key(name = "采购组代码")
    @Length(max = 50, message = "采购组代码超出限制{max}")
    private String purchasingGroupCode;

    @Key(name = "公司编码")
    @NotBlank(message = "公司编码不能为空")
    @Length(max = 50, message = "公司编码超出限制{max}")
    private String companyCode;

    @Key(name = "撤销审批：0")
    @Length(max = 10, message = "审批状态超出限制{max}")
    private String purchaseOrderStatusCode;

    @Key(name = "采购员编码")
    //	@Length(max = 10,message = "采购员编码{max}")
    private String buyerId;

    @Key(name = "币代码")
    @Length(max = 20, message = "币代码超出限制{max}")
    private String currencyCode;

    @Key(name = "汇率")
    private Double exchangeRate;

    @Key(name = "订单备注")
    @Length(max = 500, message = "订单备注超出限制{max}")
    private String remark;

    @Key(name = "国际贸易条件")
    @Length(max = 50, message = "国际贸易条件超出限制{max}")
    private String internationlTradeTerm;

    @Key(name = "说明")
    @Length(max = 500, message = "说明超出限制{max}")
    private String internationlTradeRemark;

    @Key(name = "订单明细")
    @Size(min = 1, message = "订单明细不能为空")
    @NotNull(message = "订单明细不能为空")
    private List<PurchaseOrderDetailDto> purchaseOrderDetails;

    /**
     * 去零方法
     */
    public void removeZero() {
        this.vendorErpCode = removeZero(this.vendorErpCode);
    }

    /**
     * 去零
     * 
     * @return 返回去零后的值
     */
    protected String removeZero(String sourceData) {
        if (StringUtils.isBlank(sourceData)) {
            return sourceData;
        }

        boolean flag = true;
        while (flag) {
            flag = sourceData.startsWith("0");
            if (flag) {
                sourceData = sourceData.substring(1, sourceData.length());
            }
        }

        return sourceData;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPurchaseOrderTime() {
        return purchaseOrderTime;
    }

    public void setPurchaseOrderTime(String purchaseOrderTime) {
        this.purchaseOrderTime = purchaseOrderTime;
    }

    public String getVendorErpCode() {
        return vendorErpCode;
    }

    public void setVendorErpCode(String vendorErpCode) {
        this.vendorErpCode = vendorErpCode;
    }

    public String getPurchasingOrgCode() {
        return purchasingOrgCode;
    }

    public void setPurchasingOrgCode(String purchasingOrgCode) {
        this.purchasingOrgCode = purchasingOrgCode;
    }

    public String getPurchasingGroupCode() {
        return purchasingGroupCode;
    }

    public void setPurchasingGroupCode(String purchasingGroupCode) {
        this.purchasingGroupCode = purchasingGroupCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getPurchaseOrderStatusCode() {
        return purchaseOrderStatusCode;
    }

    public void setPurchaseOrderStatusCode(String purchaseOrderStatusCode) {
        this.purchaseOrderStatusCode = purchaseOrderStatusCode;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInternationlTradeTerm() {
        return internationlTradeTerm;
    }

    public void setInternationlTradeTerm(String internationlTradeTerm) {
        this.internationlTradeTerm = internationlTradeTerm;
    }

    public String getInternationlTradeRemark() {
        return internationlTradeRemark;
    }

    public void setInternationlTradeRemark(String internationlTradeRemark) {
        this.internationlTradeRemark = internationlTradeRemark;
    }

    public List<PurchaseOrderDetailDto> getPurchaseOrderDetails() {
        return purchaseOrderDetails;
    }

    public void setPurchaseOrderDetails(List<PurchaseOrderDetailDto> purchaseOrderDetails) {
        this.purchaseOrderDetails = purchaseOrderDetails;
    }

    public String getErpPurchaseOrderNo() {
        return erpPurchaseOrderNo;
    }

    public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
        this.erpPurchaseOrderNo = erpPurchaseOrderNo;
    }

}
