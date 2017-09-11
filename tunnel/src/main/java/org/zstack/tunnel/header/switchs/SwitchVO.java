package org.zstack.tunnel.header.switchs;

import org.zstack.header.vo.EO;
import org.zstack.tunnel.header.endpoint.EndpointVO;

import javax.persistence.*;

/**
 * Created by DCY on 2017-08-24
 */
@Entity
@Table
@EO(EOClazz = SwitchEO.class)
public class SwitchVO extends SwitchAO {

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="endpointUuid", insertable=false, updatable=false)
    private EndpointVO endpoint;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="physicalSwitchUuid", insertable=false, updatable=false)
    private PhysicalSwitchVO physicalSwitch;

    public EndpointVO getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointVO endpoint) {
        this.endpoint = endpoint;
    }

    public PhysicalSwitchVO getPhysicalSwitch() {
        return physicalSwitch;
    }

    public void setPhysicalSwitch(PhysicalSwitchVO physicalSwitch) {
        this.physicalSwitch = physicalSwitch;
    }
}
