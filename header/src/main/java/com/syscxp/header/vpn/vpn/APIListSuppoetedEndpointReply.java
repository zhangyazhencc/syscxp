package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIListSuppoetedEndpointReply extends APIReply {
    private List<SupportedEndpointInventory> inventories;

    public List<SupportedEndpointInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SupportedEndpointInventory> inventories) {
        this.inventories = inventories;
    }

    public static class SupportedEndpointInventory {
        private String uuid;
        private String name;
        private Integer vlan;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getVlan() {
            return vlan;
        }

        public void setVlan(Integer vlan) {
            this.vlan = vlan;
        }
    }
}

