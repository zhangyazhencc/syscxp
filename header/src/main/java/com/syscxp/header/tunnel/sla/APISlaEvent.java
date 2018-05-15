package com.syscxp.header.tunnel.sla;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.tunnel.monitor.NettoolRecordInventory;

import java.util.Map;


public class APISlaEvent extends APIEvent {
    public APISlaEvent() {
    }

    public APISlaEvent(String apiId){super(apiId);}

    private Map map;

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
}
