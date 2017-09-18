package org.zstack.vpn.header.gateway;

import org.zstack.header.vo.ForeignKey;
import org.zstack.header.vo.ForeignKey.ReferenceOption;


import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class VpnRouteVO {

    @Id
    @Column
    private String uuid;
    @Column
    @ForeignKey(parentEntityClass = VpnGatewayVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String gatewayUuid;
    @Column
    private RouteType routeType;
    @Column
    private String nextIface;
    @Column
    private String nextIface2;
    @Column
    private String targetCidr;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getGatewayUuid() {
        return gatewayUuid;
    }

    public void setGatewayUuid(String gatewayUuid) {
        this.gatewayUuid = gatewayUuid;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public String getNextIface() {
        return nextIface;
    }

    public void setNextIface(String nextIface) {
        this.nextIface = nextIface;
    }

    public String getNextIface2() {
        return nextIface2;
    }

    public void setNextIface2(String nextIface2) {
        this.nextIface2 = nextIface2;
    }

    public String getTargetCidr() {
        return targetCidr;
    }

    public void setTargetCidr(String targetCidr) {
        this.targetCidr = targetCidr;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
