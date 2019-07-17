/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huiju.srm.purchasing.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class ReceivingNoteCheckDto implements Serializable {

    private static final long serialVersionUID = 248156217607779720L;

    @NotNull(message = "物料凭证年度不能为空")
    //@FormatDate(pattern = "yyyy", message = "物料凭证年度格式不正确,正确格式[yyyy]")
    @Length(max = 4, message = "物料凭证年度长度超出限制{max}")
    private String materialCertificateYear;// 物料凭证年度

    @NotNull(message = "物料凭证编号不能为空")
    @Length(max = 50, message = "物料凭证编号长度超出限制{max}")
    private String materialCertificateCode;// 物料凭证编号

    @NotNull(message = "物料凭证中的项目不能为空")
    private String materialCertificateItem;// 物料凭证中的项目

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

}
