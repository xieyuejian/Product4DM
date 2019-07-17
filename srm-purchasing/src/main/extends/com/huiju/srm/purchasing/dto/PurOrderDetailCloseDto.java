package com.huiju.srm.purchasing.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.huiju.srm.commons.ws.utils.Key;

/**
 * 订单明细关闭接口
 * 
 * @author WANGLQ
 *
 */
public class PurOrderDetailCloseDto implements Serializable {
    private static final long serialVersionUID = 4109172323208510330L;

    @Key(name = "SAP采购订单号")
    @Length(max = 50, message = "SAP采购订单号超出限制{max}")
    private String erpPurchaseOrderNo;

    @Key(name = "SAP采购细单行项目号")
    @NotNull(message = "SAP采购细单行项目号不能为空")
    private Integer rowIds;

    @NotNull(message = "关闭状态不能为空")
    private String closeFlag;

    public String getErpPurchaseOrderNo() {
        return erpPurchaseOrderNo;
    }

    public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
        this.erpPurchaseOrderNo = erpPurchaseOrderNo;
    }

    public Integer getRowIds() {
        return rowIds;
    }

    public void setRowIds(Integer rowIds) {
        this.rowIds = rowIds;
    }

    public String getCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(String closeFlag) {
        this.closeFlag = closeFlag;
    }

}
