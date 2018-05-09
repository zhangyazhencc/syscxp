package com.syscxp.header.vpn.agent;

import java.util.Map;

public class UpdateL3VpnIPMsg extends VpnMessage {
    DhcpPools dhcpPools;

    public DhcpPools getDhcpPools() { return dhcpPools; }

    public void setDhcpPools(DhcpPools dhcpPools) { this.dhcpPools = dhcpPools; }
}
