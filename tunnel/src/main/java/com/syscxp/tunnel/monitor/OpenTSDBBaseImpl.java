package com.syscxp.tunnel.monitor;

import com.alibaba.fastjson.JSON;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.header.Component;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.monitor.OpenTSDBCommands;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */

public class OpenTSDBBaseImpl implements OpenTSDBBase, Component{

    @Autowired
    private RESTFacade restf;

    /***
     * 获取OpenTSDB服务url
     * @param method
     * @return
     */
    private String getOpenTSDBUrl(String method) {
        String url = CoreGlobalProperty.OPENTSDB_SERVER_URL + method;

        return url;
    }

    public List<OpenTSDBCommands.QueryResult> httpCall(String condition) {
        String url = getOpenTSDBUrl(OpenTSDBCommands.restMethod.OPEN_TSDB_QUERY);
        String resp = "";
        try {
            resp = restf.getRESTTemplate().postForObject(url, condition, String.class);
        } catch (Exception e) {
            resp = "";
        }

        List<OpenTSDBCommands.QueryResult> results = new ArrayList<>();
        if (!StringUtils.isEmpty(resp))
            return JSON.parseArray(resp, OpenTSDBCommands.QueryResult.class);
        else
            return null;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }
}
