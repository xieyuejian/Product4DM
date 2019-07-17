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
package com.huiju.srm.purchasing.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huiju.module.data.common.FeignParam;
import com.huiju.module.util.StringUtils;
import com.huiju.srm.masterdata.api.MaterialClient;
import com.huiju.srm.masterdata.api.MaterialGroupClient;
import com.huiju.srm.masterdata.api.MaterialTypeClient;
import com.huiju.srm.masterdata.api.PlantClient;
import com.huiju.srm.masterdata.api.StockLocationClient;
import com.huiju.srm.masterdata.entity.Material;
import com.huiju.srm.masterdata.entity.MaterialGroup;
import com.huiju.srm.masterdata.entity.MaterialType;
import com.huiju.srm.masterdata.entity.StockLocation;

/**
 * @author wuxii@foxmail.com
 */
@Service
public class ReceivingNoteCheckHelper {
    @Autowired
    private MaterialClient materialInfoLogic;

    @Autowired
    private MaterialGroupClient materialGroupLogic;

    @Autowired
    private PlantClient plantLogic;

    @Autowired
    private MaterialTypeClient materialTypeLogic;

    @Autowired
    private StockLocationClient slLogic;

    /**
     * 检查工厂是否存在
     */
    public boolean plantExists(String plantCode) {
        if (StringUtils.isBlank(plantCode)) {
            return false;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_plantcode", plantCode);
        return plantLogic.count(params) > 0;
    }

    /**
     * 检查物料是否存在
     */
    public boolean materialInfoExsits(String materialCode) {
        if (StringUtils.isBlank(materialCode)) {
            return false;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_materialCode", materialCode);
        List<Material> list = materialInfoLogic.findAll(new FeignParam<Material>(params));
        return list.size() > 0;
    }

    /**
     * 检查物料组是否存在
     */
    public boolean materialGroupExists(String materialGroupCode) {
        if (StringUtils.isBlank(materialGroupCode)) {
            return false;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_materialgroupcode", materialGroupCode);
        return materialGroupLogic.count(params) > 0;
    }

    /**
     * 检查物料类型是否存在
     */
    public boolean materialTypeExists(String materialType) {
        if (StringUtils.isBlank(materialType)) {
            return false;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_materialTypeCode", materialType);
        List<MaterialType> list = materialTypeLogic.findAll(new FeignParam<MaterialType>(params));
        return list.size() > 0;
    }

    public MaterialGroup getMaterialGroup(String materialGroupCode) {
        if (StringUtils.isBlank(materialGroupCode)) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_materialgroupcode", materialGroupCode);
        return materialGroupLogic.findOne(params);
    }

    public MaterialType getMaterialType(String materialType) {
        if (StringUtils.isBlank(materialType)) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_materialTypeCode", materialType);
        return materialTypeLogic.findOne(params);
    }

    public Material getMaterialInfo(String materialCode) {
        if (StringUtils.isBlank(materialCode)) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_materialCode", materialCode);
        return materialInfoLogic.findOne(new FeignParam<Material>(params));
    }

    public StockLocation getStorageLocation(String plantCode, String storLocCode) {
        if (StringUtils.isBlank(storLocCode)) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("EQ_plantCode", plantCode);
        params.put("EQ_stockLocationCode", storLocCode);
        return slLogic.findOne(params);
    }
}
