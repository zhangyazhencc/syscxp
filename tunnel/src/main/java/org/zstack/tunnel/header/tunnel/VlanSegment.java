package org.zstack.tunnel.header.tunnel;

/**
 * Created by DCY on 2017-09-11
 */
public class VlanSegment {
    private Integer startVlan;
    private Integer endVlan;

    public Integer getStartVlan() {
        return startVlan;
    }

    public void setStartVlan(Integer startVlan) {
        this.startVlan = startVlan;
    }

    public Integer getEndVlan() {
        return endVlan;
    }

    public void setEndVlan(Integer endVlan) {
        this.endVlan = endVlan;
    }
}
