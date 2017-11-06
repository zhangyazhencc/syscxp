package com.syscxp.vpn.host;

import com.syscxp.vpn.header.host.VpnHostInventory;
import org.springframework.web.util.UriComponentsBuilder;

public class VpnHostContext {
    private VpnHostInventory inventory;
    private String baseUrl;

    public VpnHostInventory getInventory() {
        return inventory;
    }

    public void setInventory(VpnHostInventory inventory) {
        this.inventory = inventory;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String buildUrl(String... path) {
        UriComponentsBuilder ub = UriComponentsBuilder.fromHttpUrl(baseUrl);
        for (String p : path) {
            ub.path(p);
        }
        return ub.build().toString();
    }
}
