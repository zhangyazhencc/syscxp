package com.syscxp.tunnel.host;

import com.syscxp.header.tunnel.host.MonitorHostInventory;
import org.springframework.web.util.UriComponentsBuilder;

public class MonitorHostContext {
    private MonitorHostInventory inventory;
    private String baseUrl;

    public MonitorHostInventory getInventory() {
        return inventory;
    }
    public void setInventory(MonitorHostInventory inventory) {
        this.inventory = inventory;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String buildUrl(String...path) {
        UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        for (String p : path) {
            ub.path(p);
        }
        return ub.build().toString();
    }
}
