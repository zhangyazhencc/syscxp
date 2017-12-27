package com.syscxp.rest;

import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.utils.gson.JSONObjectUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Project: syscxp
 * Package: com.syscxp.rest
 * Date: 2017/12/26 15:09
 * Author: wj
 */
public class RequestData {
    String webHook;
    APIMessage apiMessage;
    RestServer.RequestInfo requestInfo;
    private String apiClassName;

    static RequestData fromJson(String jsonstr) {
        Map m = JSONObjectUtil.toObject(jsonstr, LinkedHashMap.class);

        RequestData d = new RequestData();
        d.webHook = (String) m.get("webHook");
        d.apiClassName = (String) m.get("apiClassName");
        d.requestInfo = JSONObjectUtil.rehashObject(m.get("requestInfo"), RestServer.RequestInfo.class);

        if (d.apiClassName != null) {
            try {
                Class<? extends APIMessage> clz = (Class<? extends APIMessage>) Class.forName(d.apiClassName);
                d.apiMessage = JSONObjectUtil.rehashObject(m.get("apiMessage"), clz);
            } catch (Exception e) {
                throw new CloudRuntimeException(e);
            }
        }

        return d;
    }

    String toJson() {
        if (apiMessage != null) {
            apiClassName = apiMessage.getClass().getName();
        }

        return JSONObjectUtil.toJsonString(this);
    }
}
