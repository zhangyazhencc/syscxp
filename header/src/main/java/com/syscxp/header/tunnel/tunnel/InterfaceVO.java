package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.tunnel.switchs.SwitchPortVO;
import com.syscxp.header.vo.EO;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.vo.NoView;

import javax.persistence.*;

/**
 * Created by DCY on 2017-09-08
 */
@Entity
@Table
@EO(EOClazz = InterfaceEO.class)
public class InterfaceVO extends InterfaceAO{

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="endpointUuid", insertable=false, updatable=false)
    @NoView
    private EndpointVO endpointVO;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="switchPortUuid", insertable=false, updatable=false)
    @NoView
    private SwitchPortVO switchPortVO;

    public SwitchPortVO getSwitchPortVO() {
        return switchPortVO;
    }

    public void setSwitchPortVO(SwitchPortVO switchPortVO) {
        this.switchPortVO = switchPortVO;
    }

    public EndpointVO getEndpointVO() {
        return endpointVO;
    }

    public void setEndpointVO(EndpointVO endpointVO) {
        this.endpointVO = endpointVO;
    }
}
