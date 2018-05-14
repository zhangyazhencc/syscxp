package com.syscxp.header.tunnel.tunnel;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by DCY on 2018/5/14
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class VsiTePathVO {
    @Id
    @Column
    private String uuid;

    @Column
    private String tunnelUuid;

    @Column
    private String name;

    @Column
    private String source;

    @Column
    private String destination;

    @Column
    private String direction;

    @Column
    private String tnlPolicyName;

    @Column
    private String tnlPolicydestination;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "vsiTePathUuid", insertable = false, updatable = false)
    private List<ExplicitPathVO> explicitPathVOS = new ArrayList<ExplicitPathVO>();

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

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTnlPolicyName() {
        return tnlPolicyName;
    }

    public void setTnlPolicyName(String tnlPolicyName) {
        this.tnlPolicyName = tnlPolicyName;
    }

    public String getTnlPolicydestination() {
        return tnlPolicydestination;
    }

    public void setTnlPolicydestination(String tnlPolicydestination) {
        this.tnlPolicydestination = tnlPolicydestination;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public List<ExplicitPathVO> getExplicitPathVOS() {
        return explicitPathVOS;
    }

    public void setExplicitPathVOS(List<ExplicitPathVO> explicitPathVOS) {
        this.explicitPathVOS = explicitPathVOS;
    }
}
