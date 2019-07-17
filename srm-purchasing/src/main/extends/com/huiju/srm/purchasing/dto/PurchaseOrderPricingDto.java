package com.huiju.srm.purchasing.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.huiju.srm.commons.ws.utils.Key;

/**
 * 订单条件
 * 
 * @author WANGLQ
 *
 */
public class PurchaseOrderPricingDto implements Serializable {

    private static final long serialVersionUID = 334059748711537547L;;

    @Key(name = "SAP采购细单行项目号")
    @NotNull(message = "SAP采购细单行项目号不能为空")
    private Integer rowIds;

    @Key(name = "条件表行号")
    @NotNull(message = "条件表行号不能为空")
    private Integer purchaseOrderPricingRowId;

    @Key(name = "条件类型")
    @Length(max = 50, message = "条件类型超出限制{max}")
    private String purchaseOrderPricingTypeCode;

    @Key(name = "条件类型描述")
    @Length(max = 50, message = "条件类型描述超出限制{max}")
    private String purchaseOrderPricingTypeName;

    @Key(name = "单价")
    private Double pricingQty;

    @Key(name = "价格单位")
    private Integer priceUnit;

    @Key(name = "金额")
    private Double amount;

    @Key(name = "币种")
    @Length(max = 50, message = "币种超出限制{max}")
    private String curType;

    @Key(name = "SRM采购订单明细ID")
    private Integer purchaseOrderDetailId;

    public Integer getRowIds() {
        return rowIds;
    }

    public void setRowIds(Integer rowIds) {
        this.rowIds = rowIds;
    }

    public Integer getPurchaseOrderPricingRowId() {
        return purchaseOrderPricingRowId;
    }

    public void setPurchaseOrderPricingRowId(Integer purchaseOrderPricingRowId) {
        this.purchaseOrderPricingRowId = purchaseOrderPricingRowId;
    }

    public String getPurchaseOrderPricingTypeCode() {
        return purchaseOrderPricingTypeCode;
    }

    public void setPurchaseOrderPricingTypeCode(String purchaseOrderPricingTypeCode) {
        this.purchaseOrderPricingTypeCode = purchaseOrderPricingTypeCode;
    }

    public String getPurchaseOrderPricingTypeName() {
        return purchaseOrderPricingTypeName;
    }

    public void setPurchaseOrderPricingTypeName(String purchaseOrderPricingTypeName) {
        this.purchaseOrderPricingTypeName = purchaseOrderPricingTypeName;
    }

    public Double getPricingQty() {
        return pricingQty;
    }

    public void setPricingQty(Double pricingQty) {
        this.pricingQty = pricingQty;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurType() {
        return curType;
    }

    public void setCurType(String curType) {
        this.curType = curType;
    }

    public Integer getPurchaseOrderDetailId() {
        return purchaseOrderDetailId;
    }

    public void setPurchaseOrderDetailId(Integer purchaseOrderDetailId) {
        this.purchaseOrderDetailId = purchaseOrderDetailId;
    }
}
