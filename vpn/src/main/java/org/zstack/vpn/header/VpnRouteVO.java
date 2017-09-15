package org.zstack.vpn.header;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class VpnRouteVO {

    @Id
    @Column
    private String uuid;
    @Column
    private String gatewayUuid;
    @Column
    private RouteType routeType;
    @Column
    private String nextIfaceUuid;
    @Column
    private String nextIfaceName;
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

    public String getNextIfaceUuid() {
        return nextIfaceUuid;
    }

    public void setNextIfaceUuid(String nextIfaceUuid) {
        this.nextIfaceUuid = nextIfaceUuid;
    }

    public String getNextIfaceName() {
        return nextIfaceName;
    }

    public void setNextIfaceName(String nextIfaceName) {
        this.nextIfaceName = nextIfaceName;
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
