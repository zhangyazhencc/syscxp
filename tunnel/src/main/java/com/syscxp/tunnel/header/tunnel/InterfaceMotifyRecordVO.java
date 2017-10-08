package com.syscxp.tunnel.header.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Create by DCY on 2017/9/30
 */
@Entity
@Table
public class InterfaceMotifyRecordVO {
    @Id
    @Column
    private String uuid;
    @Column
    private String interfaceUuid;
    @Column
    private String opAccountUuid;
    @Column
    @Enumerated(EnumType.STRING)
    private MotifyType motifyType;
    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInterfaceUuid() {
        return interfaceUuid;
    }

    public void setInterfaceUuid(String interfaceUuid) {
        this.interfaceUuid = interfaceUuid;
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
