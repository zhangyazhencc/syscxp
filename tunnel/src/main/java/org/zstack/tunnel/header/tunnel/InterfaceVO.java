package org.zstack.tunnel.header.tunnel;

import org.zstack.header.vo.EO;
import org.zstack.tunnel.header.switchs.SwitchPortVO;

import javax.persistence.*;

/**
 * Created by DCY on 2017-09-08
 */
@Entity
@Table
@EO(EOClazz = InterfaceEO.class)
public class InterfaceVO extends InterfaceAO{

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="switchPortUuid", insertable=false, updatable=false)
    private SwitchPortVO switchPortVO;


    public SwitchPortVO getSwitchPortVO() {
        return switchPortVO;
    }

    public void setSwitchPortVO(SwitchPortVO switchPortVO) {
        this.switchPortVO = switchPortVO;
    }


}
