package com.syscxp.header.tunnel;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by DCY on 2017-09-11
 */
// @Inventory(mappingVOClass = TunnelForAlarmVO.class)
public class TunnelDetailForAlarmInventory {

    private String tunnelUuid;
    private String endpointAVlan;
    private String endpointZVlan;
    private String endpointAIp;
    private String endpointZIp;
    private Integer bandwidth;

    public static TunnelDetailForAlarmInventory valueOf(Map<String,String> vo){
        TunnelDetailForAlarmInventory inv = new TunnelDetailForAlarmInventory();
        inv.setTunnelUuid(vo.get("tunnelUuid"));
        inv.setEndpointAIp(vo.get("endpointAIp"));
        inv.setEndpointAVlan(vo.get("endpointAVlan"));
        inv.setEndpointZIp(vo.get("endpointZIp"));
        inv.setEndpointZVlan(vo.get("endpointZVlan"));
        inv.setBandwidth(Integer.valueOf(vo.get("bandwidth")));
        return inv;
    }

    public static List<TunnelDetailForAlarmInventory> valueOf(Collection<Map<String,String>> vos) {
        List<TunnelDetailForAlarmInventory> lst = new ArrayList<TunnelDetailForAlarmInventory>(vos.size());
        for (Map<String,String> vo : vos) {
            lst.add(TunnelDetailForAlarmInventory.valueOf(vo));
        }
        return lst;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getEndpointAVlan() {
        return endpointAVlan;
    }

    public void setEndpointAVlan(String endpointAVlan) {
        this.endpointAVlan = endpointAVlan;
    }

    public String getEndpointZVlan() {
        return endpointZVlan;
    }

    public void setEndpointZVlan(String endpointZVlan) {
        this.endpointZVlan = endpointZVlan;
    }

    public String getEndpointAIp() {
        return endpointAIp;
    }

    public void setEndpointAIp(String endpointAIp) {
        this.endpointAIp = endpointAIp;
    }

    public String getEndpointZIp() {
        return endpointZIp;
    }

    public void setEndpointZIp(String endpointZIp) {
        this.endpointZIp = endpointZIp;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }
}
