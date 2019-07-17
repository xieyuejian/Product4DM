package com.huiju.srm.purchasing.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.huiju.srm.commons.ws.utils.Key;

/**
 * 双单位转换VO
 * 
 * @author WANGLQ
 *
 */
public class PurchaseOrderUnitConversionDto implements Serializable {

    private static final long serialVersionUID = 334059748711537547L;;

    @Key(name = "采购细单行项目号")
    @NotNull(message = "采购细单行项目号不能为空")
    private Integer rowIds;

    @Key(name = "转换分子1-->订单单位")
    private Integer convertMolecular;

    @Key(name = "转换分母1-->基本单位（SKU）")
    private Integer convertDenominator;

    @Key(name = "采购订单单位")
    @Length(max = 50, message = "采购订单单位超出限制{max}")
    private String orderDetailUnit;

    @Key(name = "基本单位")
    @Length(max = 50, message = "基本单位超出限制{max}")
    private String unitCode;

    @Key(name = "转换分子2--->订单单位")
    private Integer convertMolecular2;

    @Key(name = "转换分母2---->定价单位")
    private Integer convertDenominator2;

    @Key(name = "订单定价单位")
    @Length(max = 50, message = "订单定价单位超出限制{max}")
    private String pricingUnit;

    @Key(name = "订单定价单位数量")
    private Integer pricingQty;

    @Key(name = "PO QTY IN SKU")
    private Integer skuQty;

    public Integer getRowIds() {
        return rowIds;
    }

    public void setRowIds(Integer rowIds) {
        this.rowIds = rowIds;
    }

    public Integer getConvertMolecular() {
        return convertMolecular;
    }

    public void setConvertMolecular(Integer convertMolecular) {
        this.convertMolecular = convertMolecular;
    }

    public Integer getConvertDenominator() {
        return convertDenominator;
    }

    public void setConvertDenominator(Integer convertDenominator) {
        this.convertDenominator = convertDenominator;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public Integer getConvertMolecular2() {
        return convertMolecular2;
    }

    public void setConvertMolecular2(Integer convertMolecular2) {
        this.convertMolecular2 = convertMolecular2;
    }

    public Integer getConvertDenominator2() {
        return convertDenominator2;
    }

    public void setConvertDenominator2(Integer convertDenominator2) {
        this.convertDenominator2 = convertDenominator2;
    }

    public String getPricingUnit() {
        return pricingUnit;
    }

    public void setPricingUnit(String pricingUnit) {
        this.pricingUnit = pricingUnit;
    }

    public Integer getPricingQty() {
        return pricingQty;
    }

    public void setPricingQty(Integer pricingQty) {
        this.pricingQty = pricingQty;
    }

    public Integer getSkuQty() {
        return skuQty;
    }

    public void setSkuQty(Integer skuQty) {
        this.skuQty = skuQty;
    }

    public String getOrderDetailUnit() {
        return orderDetailUnit;
    }

    public void setOrderDetailUnit(String orderDetailUnit) {
        this.orderDetailUnit = orderDetailUnit;
    }

}
