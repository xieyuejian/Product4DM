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
 * 订单表行
 * 
 * @author WANGLQ
 *
 */
public class PurchaseOrderDetailDto implements Serializable {
    private static final long serialVersionUID = 334059748711537547L;;
    @Key(name = "SAP采购订单号")
    @Length(max = 50, message = "SAP采购订单号超出限制{max}")
    private String erpPurchaseOrderNo;

    @Key(name = "SAP采购细单行项目号")
    @NotNull(message = "SAP采购细单行项目号不能为空")
    private Integer rowIds;

    @Key(name = "科目分配类别")
    @Length(max = 50, message = "科目分配类别超出限制{max}")
    private String accountAllocationTypeCode;

    @Key(name = "行项目类别")
    @Length(max = 50, message = "行项目类别超出限制{max}")
    private String lineItemTypeCode;

    @Key(name = "紧急标识")
    private String emergencyFlag;

    @Key(name = "排程标识")
    private String scheduleFlag;

    @Key(name = "删除标识 ")
    private String deleteFlag;

    @Key(name = "物料编码")
    @Length(max = 100, message = "物料编码超出限制{max}")
    private String materialCode;

    @Key(name = "物料名称")
    @NotBlank(message = "物料名称不能为空")
    @Length(max = 200, message = "物料名称超出限制{max}")
    private String materialName;

    @Key(name = "采购数量")
    @NotNull(message = "采购数量不能为空")
    private Double buyerQty;

    @Key(name = "采购单位代码")
    @NotBlank(message = "采购单位代码不能为空")
    @Length(max = 10, message = "采购单位代码超出限制{max}")
    private String unitCode;

    @Key(name = "交货日期")
    @NotBlank(message = "交货日期不能为空")
    //@FormatDate(pattern = "yyyy-MM-dd", message = "交货日期正确格式为yyyy-MM-dd")
    private String buyerTime;

    @Key(name = "净价")
    private Double vendorPrice;

    @Key(name = "价格单位")
    @NotNull(message = "价格单位不能为空")
    private Integer priceUnit;

    @Key(name = "物料组编码")
    @NotBlank(message = "物料组编码不能为空")
    @Length(max = 200, message = "物料组编码超出限制{max}")
    private String materialGroupCode;

    @Key(name = "工厂代码")
    @NotBlank(message = "工厂代码不能为空")
    @Length(max = 100, message = "工厂代码超出限制{max}")
    private String plantCode;

    @Key(name = "库存地点")
    @Length(max = 200, message = "库存地点超出限制{max}")
    private String storeLocal;

    @Key(name = "成本中心")
    @Length(max = 100, message = "成本中心超出限制{max}")
    private String costCenter;

    @Key(name = "总账科目")
    @Length(max = 100, message = "总账科目超出限制{max}")
    private String generalLedgerSubject;

    @Key(name = "资产号")
    @Length(max = 100, message = "资产号超出限制{max}")
    private String assetNumber;

    @Key(name = "研发项目号")
    @Length(max = 100, message = "研发项目号超出限制{max}")
    private String pdProjectNumber;

    @Key(name = "退货标识")
    private String isReturn;

    @Key(name = "免费标识")
    private String isFree;

    @Key(name = "过量交货限度")
    private Double overDeliveryLimit;

    @Key(name = "交货不足限度")
    private Double shortDeliveryLimit;

    @Key(name = "关闭标识")
    private String closeLogo;

    @Key(name = "信息记录号")
    @Length(max = 50, message = "信息记录号超出限制{max}")
    private String informationRecordNo;

    @Key(name = "SRM采购订单明细ID")
    private Integer purchaseOrderDetailId;

    @Key(name = "税率编码")
    private String taxCode;

    @Key(name = "订单条件")
    private List<PurchaseOrderPricingDto> purchaseOrderPricings;

    @Key(name = "订单BOM")
    private List<PurchaseOrderBomDto> purchaseOrderBoms;

    @Key(name = "库存类型")
    private String stockType;

    @Key(name = "单位转换")
    @Size(min = 1, message = "单位转换不能为空")
    @NotNull(message = "单位转换不能为空")
    private List<PurchaseOrderUnitConversionDto> purchaseOrderUnitConversions;

    /**
     * 去零方法
     */
    public void removeZero() {
        this.informationRecordNo = removeZero(this.informationRecordNo);
        this.costCenter = removeZero(this.costCenter);
        this.generalLedgerSubject = removeZero(this.generalLedgerSubject);
        this.assetNumber = removeZero(this.assetNumber);
        this.pdProjectNumber = removeZero(this.pdProjectNumber);
        this.materialCode = removeZero(this.materialCode);
    }

    /**
     * 去零
     * 
     * @return 返回去零后的值
     */
    private String removeZero(String sourceData) {
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

    public String getErpPurchaseOrderNo() {
        return erpPurchaseOrderNo;
    }

    public void setErpPurchaseOrderNo(String erpPurchaseOrderNo) {
        this.erpPurchaseOrderNo = erpPurchaseOrderNo;
    }

    public Integer getRowIds() {
        return rowIds;
    }

    public List<PurchaseOrderPricingDto> getPurchaseOrderPricings() {
        return purchaseOrderPricings;
    }

    public void setPurchaseOrderPricings(List<PurchaseOrderPricingDto> purchaseOrderPricings) {
        this.purchaseOrderPricings = purchaseOrderPricings;
    }

    public void setPurchaseOrderBoms(List<PurchaseOrderBomDto> purchaseOrderBoms) {
        this.purchaseOrderBoms = purchaseOrderBoms;
    }

    public List<PurchaseOrderBomDto> getPurchaseOrderBoms() {
        return purchaseOrderBoms;
    }

    public List<PurchaseOrderUnitConversionDto> getPurchaseOrderUnitConversions() {
        return purchaseOrderUnitConversions;
    }

    public void setPurchaseOrderUnitConversions(List<PurchaseOrderUnitConversionDto> purchaseOrderUnitConversions) {
        this.purchaseOrderUnitConversions = purchaseOrderUnitConversions;
    }

    public void setRowIds(Integer rowIds) {
        this.rowIds = rowIds;
    }

    public String getAccountAllocationTypeCode() {
        return accountAllocationTypeCode;
    }

    public void setAccountAllocationTypeCode(String accountAllocationTypeCode) {
        this.accountAllocationTypeCode = accountAllocationTypeCode;
    }

    public String getLineItemTypeCode() {
        return lineItemTypeCode;
    }

    public void setLineItemTypeCode(String lineItemTypeCode) {
        this.lineItemTypeCode = lineItemTypeCode;
    }

    public String getEmergencyFlag() {
        return emergencyFlag;
    }

    public void setEmergencyFlag(String emergencyFlag) {
        this.emergencyFlag = emergencyFlag;
    }

    public String getScheduleFlag() {
        return scheduleFlag;
    }

    public void setScheduleFlag(String scheduleFlag) {
        this.scheduleFlag = scheduleFlag;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
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

    public Double getBuyerQty() {
        return buyerQty;
    }

    public void setBuyerQty(Double buyerQty) {
        this.buyerQty = buyerQty;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getBuyerTime() {
        return buyerTime;
    }

    public void setBuyerTime(String buyerTime) {
        this.buyerTime = buyerTime;
    }

    public Double getVendorPrice() {
        return vendorPrice;
    }

    public void setVendorPrice(Double vendorPrice) {
        this.vendorPrice = vendorPrice;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getMaterialGroupCode() {
        return materialGroupCode;
    }

    public void setMaterialGroupCode(String materialGroupCode) {
        this.materialGroupCode = materialGroupCode;
    }

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    public String getStoreLocal() {
        return storeLocal;
    }

    public void setStoreLocal(String storeLocal) {
        this.storeLocal = storeLocal;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getGeneralLedgerSubject() {
        return generalLedgerSubject;
    }

    public void setGeneralLedgerSubject(String generalLedgerSubject) {
        this.generalLedgerSubject = generalLedgerSubject;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getPdProjectNumber() {
        return pdProjectNumber;
    }

    public void setPdProjectNumber(String pdProjectNumber) {
        this.pdProjectNumber = pdProjectNumber;
    }

    public String getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(String isReturn) {
        this.isReturn = isReturn;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public Double getOverDeliveryLimit() {
        return overDeliveryLimit;
    }

    public void setOverDeliveryLimit(Double overDeliveryLimit) {
        this.overDeliveryLimit = overDeliveryLimit;
    }

    public Double getShortDeliveryLimit() {
        return shortDeliveryLimit;
    }

    public void setShortDeliveryLimit(Double shortDeliveryLimit) {
        this.shortDeliveryLimit = shortDeliveryLimit;
    }

    public String getCloseLogo() {
        return closeLogo;
    }

    public void setCloseLogo(String closeLogo) {
        this.closeLogo = closeLogo;
    }

    public String getInformationRecordNo() {
        return informationRecordNo;
    }

    public void setInformationRecordNo(String informationRecordNo) {
        this.informationRecordNo = informationRecordNo;
    }

    public Integer getPurchaseOrderDetailId() {
        return purchaseOrderDetailId;
    }

    public void setPurchaseOrderDetailId(Integer purchaseOrderDetailId) {
        this.purchaseOrderDetailId = purchaseOrderDetailId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }

}
