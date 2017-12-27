package com.syscxp.rest;

import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.JsonSchemaBuilder;
import com.syscxp.utils.BeanUtils;
import com.syscxp.utils.gson.JSONObjectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 14:59
 * Author: wj
 */
public class ApiEventResult {
    private Object apiEvent;
    private Map<String, String> schema;
    private String apiEventClassName;

    static APIEvent fromJson(String jsonStr) {
        try {
            ApiEventResult res = JSONObjectUtil.toObject(jsonStr, ApiEventResult.class);
            Class<? extends APIEvent> clz = (Class<? extends APIEvent>) Class.forName(res.apiEventClassName);

            APIEvent evt = JSONObjectUtil.rehashObject(res.apiEvent, clz);

            List<String> paths = new ArrayList();
            paths.addAll(res.schema.keySet());
            Collections.sort(paths);

            for (String path : paths) {
                String clzName = res.schema.get(path);

                Object bean = BeanUtils.getProperty(evt, path);
                if (bean.getClass().getName().equals(clzName)) {
                    // not an inherent object
                    continue;
                }

                Class fclz = Class.forName(clzName);
                Object val = JSONObjectUtil.rehashObject(BeanUtils.getProperty(res.apiEvent, path), fclz);
                BeanUtils.setProperty(evt, path, val);
            }

            return evt;
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
    }

    static String toJson(APIEvent evt) {
        ApiEventResult res = new ApiEventResult();
        res.apiEvent = evt;
        res.apiEventClassName = evt.getClass().getName();
        res.schema = new JsonSchemaBuilder(evt).build();

        return JSONObjectUtil.toJsonString(res);
    }
}

