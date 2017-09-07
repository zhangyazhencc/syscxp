package org.zstack.tunnel.header.switchs;

import org.zstack.header.vo.EO;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by DCY on 2017-09-05
 */
@Entity
@Table
@EO(EOClazz = SwitchAttributionEO.class)
public class SwitchAttributionVO extends SwitchAttributionAO{

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="switchModelUuid", insertable=false, updatable=false)
    private SwitchModelVO switchModel;

    public SwitchModelVO getSwitchModel() {
        return switchModel;
    }

    public void setSwitchModel(SwitchModelVO switchModel) {
        this.switchModel = switchModel;
    }
}
