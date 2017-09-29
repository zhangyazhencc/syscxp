package org.zstack.vpn.header.vpn;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class VpnMotifyRecordVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String vpnUuid;
    @Column
    private String opAccountUuid;
    @Column
    @Enumerated(EnumType.STRING)
    private MotifyType motifyType;
    @Column
    private Timestamp createDate;

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

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

    public String getOpAccountUuid() {
        return opAccountUuid;
    }

    public void setOpAccountUuid(String opAccountUuid) {
        this.opAccountUuid = opAccountUuid;
    }

    public MotifyType getMotifyType() {
        return motifyType;
    }

    public void setMotifyType(MotifyType motifyType) {
        this.motifyType = motifyType;
    }
}
