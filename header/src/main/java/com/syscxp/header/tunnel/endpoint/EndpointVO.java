package com.syscxp.header.tunnel.endpoint;

import com.syscxp.header.vo.EO;
import com.syscxp.header.tunnel.node.NodeVO;

import javax.persistence.*;

/**
 * Created by DCY on 2017-08-23
 */
@Entity
@Table
@EO(EOClazz = EndpointEO.class)
public class EndpointVO extends EndpointAO {

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="nodeUuid", insertable=false, updatable=false)
    private NodeVO nodeVO;

    public NodeVO getNodeVO() {
        return nodeVO;
    }

    public void setNodeVO(NodeVO nodeVO) {
        this.nodeVO = nodeVO;
    }
}
