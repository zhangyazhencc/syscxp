package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.vpn.VpnAO;
import com.syscxp.header.vpn.host.VpnHostVO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@TriggerIndex
public class VpnVO extends VpnAO {

    @Column
    private String endpointUuid;
    @Column
    private String tunnelUuid;

    public VpnVO() {
    }

    public VpnVO(VpnVO other) {
        super(other);
        this.endpointUuid = other.endpointUuid;
        this.tunnelUuid = other.tunnelUuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
