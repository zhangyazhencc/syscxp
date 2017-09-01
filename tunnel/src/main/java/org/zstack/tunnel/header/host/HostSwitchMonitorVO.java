package org.zstack.tunnel.header.host;

import org.zstack.header.vo.EO;
import org.zstack.tunnel.header.switchs.SwitchPortVO;

import javax.persistence.*;

/**
 * Created by DCY on 2017-08-30
 */
@Entity
@Table
@EO(EOClazz = HostSwitchMonitorEO.class)
public class HostSwitchMonitorVO extends HostSwitchMonitorAO{

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="hostUuid", insertable=false, updatable=false)
    private HostVO host;

    @OneToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="switchPortUuid", insertable=false, updatable=false)
    private SwitchPortVO switchPort;

    public HostVO getHost() {
        return host;
    }

    public void setHost(HostVO host) {
        this.host = host;
    }

    public SwitchPortVO getSwitchPort() {
        return switchPort;
    }

    public void setSwitchPort(SwitchPortVO switchPort) {
        this.switchPort = switchPort;
    }
}
