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

/**
 * @author wuxii@foxmail.com
 */
public class WsConstant {

    /**
     * 不需要同步 -1
     */
    public static final Integer SYNC_NO_NEED = -1;
    /**
     * 未同步 0
     */
    public static final Integer SYNC_UNSYNCHRONIZED = 0;
    /**
     * 同步完成 1
     */
    public static final Integer SYNC_SYNCHRONIZED = 1;
    /**
     * 同步中 2
     */
    public static final Integer SYNC_SYNCHRONIZING = 2;
    /**
     * 同步异常 3
     */
    public static final Integer SYNC_EXCEPTION = 3;

    public static final String DESC_UNSYNCHRONIZED = "未同步";

    public static final String DESC_SYNCHRONIZING = "同步中";

    public static final String DESC_SYNCHRONIZED = "同步成功";

    public static final String DESC_EXCEPTION = "同步失败";

    public static final String DESC_NO_NEED = "无需同步";

    public static final String NS_BATCH_FEATURE = "http://www.huiju.com/batch/feature";

    public static final String NS_CUSTOMER = "http://www.huiju.com/customer";

    public static final String NS_EXCHANGE_RATE = "http://www.harmony.com/rate";

    public static final String NS_FACTORY_MATERIAL = "http://www.harmony.com/material";

    public static final String NS_QUALITY_CONTROL = "http://www.harmony.com/quality/control";

    public static final String NS_RECEIVING_NOTE = "http://www.harmony.com/receiving";

    public static final String NS_PUCHASE_REQUIRED = "http://www.harmony.com/puchase/required";

    //public static final String CLIENT_CODE = "400";

    public static final String SAP_OK = "OK";

    public static final String SAP_NG = "NG";

    public static final String SAP_PC = "PC";

    private static final String KEY_SUFFIX = ".PIRMARY_KEY";

    //public static final String SRM_KEY_OF_PREINVOICE = PreInvoice.class.getName() + KEY_SUFFIX;

    // public static final String SRM_KEY_OF_PURCHASINGRECORD = PurchasingRecord.class.getName() + KEY_SUFFIX;

    //  public static final String SRM_KEY_OF_PURCHASEORDER = PurchaseOrder.class.getName() + KEY_SUFFIX;

    //  public static final String SRM_KEY_OF_SOURCELIST = SourceList.class.getName() + KEY_SUFFIX;

    //  public static final String SRM_KEY_OF_VENDOR = Vendor.class.getName() + KEY_SUFFIX;

    // public static final String SRM_KEY_OF_SMSLOG = SmsLog.class.getName() + KEY_SUFFIX;

}
