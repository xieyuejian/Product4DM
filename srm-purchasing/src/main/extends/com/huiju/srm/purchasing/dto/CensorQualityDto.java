package com.huiju.srm.purchasing.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.huiju.srm.commons.ws.utils.Key;

/**
 * @author CWQ
 */
public class CensorQualityDto implements Serializable {
    private static final long serialVersionUID = 4187781031802723452L;

    /** 物料凭证年度 */
    @Key(name = "materialCertificateYear")
    @NotNull(message = "物料凭证年度不能为空")
    @Length(max = 50, message = "物料凭证年度超出限制{max}")
    protected String materialCertificateYear;
    /** 物料凭证编号 */
    @Key(name = "materialCertificateCode")
    @NotNull(message = "物料凭证编号不能为空")
    @Length(max = 50, message = "物料凭证编号超出限制{max}")
    protected String materialCertificateCode;
    /** 物料凭证中的项目 */
    @Key(name = "materialCertificateItem")
    @NotNull(message = "物料凭证中的项目不能为空")
    @Length(max = 50, message = "物料凭证中的项目超出限制{max}")
    protected String materialCertificateItem;

    /** 合格量 */
    @Key(name = "合格量")
    protected BigDecimal qualifiedQty;

    /** 不合格量 */
    @Key(name = "不合格量")
    protected BigDecimal unqualifiedQty;

    /** 让步接收量 */
    @Key(name = "让步接收量")
    protected BigDecimal receiveQty;

    /** 质检结果代码 */
    @Key(name = "质检结果代码")
    @NotNull(message = "质检结果代码不能为空")
    @Length(max = 10, message = "收货单号超出限制{max}")
    protected String resultCode;

    /** 质检人员名称 */
    @Key(name = "质检人员编码")
    @NotNull(message = "质检人员编码不能为空")
    @Length(max = 50, message = "质检人员编码超出限制{max}")
    protected String userCode;

    public String getMaterialCertificateYear() {
        return materialCertificateYear;
    }

    public void setMaterialCertificateYear(String materialCertificateYear) {
        this.materialCertificateYear = materialCertificateYear;
    }

    public String getMaterialCertificateCode() {
        return materialCertificateCode;
    }

    public void setMaterialCertificateCode(String materialCertificateCode) {
        this.materialCertificateCode = materialCertificateCode;
    }

    public String getMaterialCertificateItem() {
        return materialCertificateItem;
    }

    public void setMaterialCertificateItem(String materialCertificateItem) {
        this.materialCertificateItem = materialCertificateItem;
    }

    public BigDecimal getQualifiedQty() {
        return qualifiedQty;
    }

    public void setQualifiedQty(BigDecimal qualifiedQty) {
        this.qualifiedQty = qualifiedQty;
    }

    public BigDecimal getUnqualifiedQty() {
        return unqualifiedQty;
    }

    public void setUnqualifiedQty(BigDecimal unqualifiedQty) {
        this.unqualifiedQty = unqualifiedQty;
    }

    public BigDecimal getReceiveQty() {
        return receiveQty;
    }

    public void setReceiveQty(BigDecimal receiveQty) {
        this.receiveQty = receiveQty;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

}
