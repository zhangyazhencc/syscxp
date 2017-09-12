package org.zstack.tunnel.header.switchs;

import org.zstack.header.vo.EO;
import org.zstack.tunnel.header.node.NodeVO;

import javax.persistence.*;

/**
 * Created by DCY on 2017-09-05
 */
@Entity
@Table
@EO(EOClazz = PhysicalSwitchEO.class)
public class PhysicalSwitchVO extends PhysicalSwitchAO {

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="nodeUuid", insertable=false, updatable=false)
    private NodeVO node;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="switchModelUuid", insertable=false, updatable=false)
    private SwitchModelVO switchModel;

    public SwitchModelVO getSwitchModel() {
        return switchModel;
    }

    public void setSwitchModel(SwitchModelVO switchModel) {
        this.switchModel = switchModel;
    }

    public NodeVO getNode() {
        return node;
    }

    public void setNode(NodeVO node) {
        this.node = node;
    }
}