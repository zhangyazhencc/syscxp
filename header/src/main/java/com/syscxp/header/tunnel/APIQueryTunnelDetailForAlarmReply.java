package com.syscxp.header.tunnel;

import com.syscxp.header.query.APIQueryReply;

import java.util.List;
import java.util.Map;

/**
 * Created by DCY on 2017-09-17
 */
public class APIQueryTunnelDetailForAlarmReply extends APIQueryReply {
    private Map<String,Map<String,String>> map;

    public Map<String, Map<String, String>> getMap() {
        return map;
    }

    public void setMap(Map<String, Map<String, String>> map) {
        this.map = map;
    }
}
