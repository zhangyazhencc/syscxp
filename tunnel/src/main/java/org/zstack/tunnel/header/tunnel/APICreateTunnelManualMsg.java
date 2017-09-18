package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.List;

/**
 * Created by DCY on 2017-09-15
 */
public class APICreateTunnelManualMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false,resourceType = NetWorkVO.class, checkAccount = true)
    private String netWorkUuid;
    @APIParam(emptyString = false,maxLength = 128)
    private String name;
    @APIParam(emptyString = false)
    private Integer bandwidth;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceAUuid;
    @APIParam(emptyString = false)
    private Integer aVlan;
    @APIParam(required = false)
    private Integer enableQinqA;
    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceZUuid;
    @APIParam(emptyString = false)
    private Integer zVlan;
    @APIParam(required = false)
    private Integer enableQinqZ;
    @APIParam(emptyString = false)
    private Integer months;
    @APIParam(required = false)
    private String description;
    @APIParam(required = false)
    private List<VlanSegment> vlanSegmentA;
    @APIParam(required = false)
    private List<VlanSegment> vlanSegmentZ;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

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

    public Integer getaVlan() {
        return aVlan;
    }

    public void setaVlan(Integer aVlan) {
        this.aVlan = aVlan;
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

    public Integer getzVlan() {
        return zVlan;
    }

    public void setzVlan(Integer zVlan) {
        this.zVlan = zVlan;
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
