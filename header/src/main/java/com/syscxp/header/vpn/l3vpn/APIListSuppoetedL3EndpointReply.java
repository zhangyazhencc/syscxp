package com.syscxp.header.vpn.l3vpn;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIListSuppoetedL3EndpointReply extends APIReply {
    private List<SupportedEndpointInventory> inventories;

    public List<SupportedEndpointInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SupportedEndpointInventory> inventories) {
        this.inventories = inventories;
    }

    public static class SupportedEndpointInventory {
        private String l3EndpointUuid;
        private String name;
        private Integer vlan;
        private String endpointUuid;

        public String getL3EndpointUuid() {
            return l3EndpointUuid;
        }

        public void setL3EndpointUuid(String l3EndpointUuid) {
            this.l3EndpointUuid = l3EndpointUuid;
        }

        public String getEndpointUuid() {
            return endpointUuid;
        }

        public void setEndpointUuid(String endpointUuid) {
            this.endpointUuid = endpointUuid;
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

