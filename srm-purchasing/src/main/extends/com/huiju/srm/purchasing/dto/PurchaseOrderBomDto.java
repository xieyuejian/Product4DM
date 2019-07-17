package com.huiju.srm.purchasing.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.huiju.srm.commons.ws.utils.Key;

/**
 * 订单BOM
 * 
 * @author WANGLQ
 *
 */
public class PurchaseOrderBomDto implements Serializable {

    private static final long serialVersionUID = 334059748711537547L;;

    @Key(name = "SAP采购订单号")
    @NotBlank(message = "SAP采购订单号不能为空")
    @Length(max = 50, message = "SAP采购订单号超出限制{max}")
    private String errpPurchaseOrderNo;

    @Key(name = "SAP采购细单行项目号")
    @NotNull(message = "SAP采购细单行项目号不能为空")
    private Integer rowIds;

    @Key(name = "BOM物料号")
    @Length(max = 100, message = "BOM物料号超出限制{max}")
    private String materialCode;

    @Key(name = "BOM物料名称")
    @NotBlank(message = "BOM物料名称不能为空")
    @Length(max = 200, message = "BOM物料名称超出限制{max}")
    private String materialName;

    @Key(name = "单位")
    @NotBlank(message = "单位不能为空")
    @Length(max = 50, message = "单位超出限制{max}")
    private String unitCode;

    @Key(name = "数量")
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    public Integer getRowIds() {
        return rowIds;
    }

    public void setRowIds(Integer rowIds) {
        this.rowIds = rowIds;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getErpPurchaseOrderNo() {
        return errpPurchaseOrderNo;
    }

    public String getErrpPurchaseOrderNo() {
        return errpPurchaseOrderNo;
    }

    public void setErrpPurchaseOrderNo(String errpPurchaseOrderNo) {
        this.errpPurchaseOrderNo = errpPurchaseOrderNo;
    }

}
