package com.syscxp.header.tunnel.network;

import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.tunnel.tunnel.TunnelMonitorState;
import com.syscxp.header.tunnel.tunnel.TunnelState;
import com.syscxp.header.tunnel.tunnel.TunnelStatus;
import com.syscxp.header.tunnel.tunnel.TunnelType;

import javax.persistence.*;
import java.sql.Timestamp;


@MappedSuperclass
public class L3NetworkAO {

    @Id
    @Column
    private String uuid;

    @Column
    private String accountUuid;

    @Column
    private String ownerAccountUuid;

}
