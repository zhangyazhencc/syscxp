package org.zstack.vpn.header;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class VpnGatewayVO {

    @Id
    @Column
    private String uuid;

    @Column
    private String name;

    @Column
    private String description;


}
