package com.syscxp.header.vpn.vpn;

import com.syscxp.header.search.TriggerIndex;
import com.syscxp.header.vpn.VpnAO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@TriggerIndex
public class VpnVO extends VpnAO {
    public VpnVO() {
    }

    public VpnVO(VpnVO other) {
        super(other);
    }

}
