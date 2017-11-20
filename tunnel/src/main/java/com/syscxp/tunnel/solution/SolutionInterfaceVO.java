package com.syscxp.tunnel.solution;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by wangwg on 2017/11/20.
 */
@Entity
@Table
public class SolutionInterfaceVO extends SolutionBaseVO{

    @Column
    private String endpointName;
    @Column
    private String portOfferingName;

    public String getEndpointUuid() {
        return endpointName;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointName = endpointUuid;
    }

    public String getSwitchPortUuid() {
        return portOfferingName;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.portOfferingName = switchPortUuid;
    }
}
