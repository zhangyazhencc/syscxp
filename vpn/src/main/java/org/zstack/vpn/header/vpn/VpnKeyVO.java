package org.zstack.vpn.header.vpn;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class VpnKeyVO {

    @Id
    @Column
    private String uuid;
    @Column
    private String vpnUuid;
    @Column
    private String vpnKey;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVpnUuid() {
        return vpnUuid;
    }

    public void setVpnUuid(String vpnUuid) {
        this.vpnUuid = vpnUuid;
    }

    public String getVpnKey() {
        return vpnKey;
    }

    public void setVpnKey(String vpnKey) {
        this.vpnKey = vpnKey;
    }
}
