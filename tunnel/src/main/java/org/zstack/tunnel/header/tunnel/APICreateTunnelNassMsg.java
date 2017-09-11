package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.List;

/**
 * Created by DCY on 2017-09-11
 */
public class APICreateTunnelNassMsg extends APIMessage {

    @APIParam(resourceType = NetWorkVO.class, checkAccount = true)
    private String netWorkUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false)
    private Integer bandwidth;
    @APIParam(resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(required = false)
    private Integer enableQinqA;
    @APIParam(resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
    @APIParam(required = false)
    private Integer enableQinqZ;
    @APIParam(emptyString = false)
    private Integer months;
    @APIParam(emptyString = false)
    private Integer isExclusiveA;
    @APIParam(emptyString = false)
    private Integer isExclusiveZ;
    @APIParam(required = false)
    private String description;
    @APIParam(required = false)
    private List<VlanSegment> vlanSegmentA;
    @APIParam(required = false)
    private List<VlanSegment> vlanSegmentZ;

    public String getNetWorkUuid() {
        return netWorkUuid;
    }

    public void setNetWorkUuid(String netWorkUuid) {
        this.netWorkUuid = netWorkUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public Integer getEnableQinqA() {
        return enableQinqA;
    }

    public void setEnableQinqA(Integer enableQinqA) {
        this.enableQinqA = enableQinqA;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
    }

    public Integer getEnableQinqZ() {
        return enableQinqZ;
    }

    public void setEnableQinqZ(Integer enableQinqZ) {
        this.enableQinqZ = enableQinqZ;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getIsExclusiveA() {
        return isExclusiveA;
    }

    public void setIsExclusiveA(Integer isExclusiveA) {
        this.isExclusiveA = isExclusiveA;
    }

    public Integer getIsExclusiveZ() {
        return isExclusiveZ;
    }

    public void setIsExclusiveZ(Integer isExclusiveZ) {
        this.isExclusiveZ = isExclusiveZ;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<VlanSegment> getVlanSegmentA() {
        return vlanSegmentA;
    }

    public void setVlanSegmentA(List<VlanSegment> vlanSegmentA) {
        this.vlanSegmentA = vlanSegmentA;
    }

    public List<VlanSegment> getVlanSegmentZ() {
        return vlanSegmentZ;
    }

    public void setVlanSegmentZ(List<VlanSegment> vlanSegmentZ) {
        this.vlanSegmentZ = vlanSegmentZ;
    }
}
