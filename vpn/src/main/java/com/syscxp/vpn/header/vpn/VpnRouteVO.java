package com.syscxp.vpn.header.vpn;

import com.syscxp.core.db.converter.ListAttributeConverter;
import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table
public class VpnRouteVO {

    @Id
    @Column
    private String uuid;
    @Column
    @ForeignKey(parentEntityClass = VpnVO.class, parentKey = "uuid", onDeleteAction = ReferenceOption.CASCADE)
    private String vpnUuid;
    @Column
    private RouteType routeType;
    @Column
    @Convert(converter = ListAttributeConverter.class)
    private List<String> nextInterface;
    @Column
    private String targetCidr;
    @Column
    private Timestamp lastOpDate;
    @Column
    private Timestamp createDate;

    @PreUpdate
    private void preUpdate() {
        lastOpDate = null;
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

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public List<String> getNextInterface() {
        return nextInterface;
    }

    public void setNextInterface(List<String> nextInterface) {
        this.nextInterface = nextInterface;
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
