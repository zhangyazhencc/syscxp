package com.syscxp.header.host;

import com.syscxp.header.search.Inventory;
import com.syscxp.header.search.TypeField;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Inventory(mappingVOClass = HostVO.class)
public class HostInventory implements Serializable {
    private String uuid;
    private String nodeUuid;
    private String name;
    private String code;
    private String hostIp;
    @TypeField
    private String hostType;
    private String position;
    private HostState state;
    private HostStatus status;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    protected HostInventory(HostAO vo) {
        this.setStatus(vo.getStatus());
        this.setCreateDate(vo.getCreateDate());
        this.setHostType(vo.getHostType());
        this.setLastOpDate(vo.getLastOpDate());
        this.setHostIp(vo.getHostIp());
        this.setName(vo.getName());
        this.setState(vo.getState());
        this.setUuid(vo.getUuid());
        this.setCode(vo.getCode());
        this.setNodeUuid(vo.getNodeUuid());
        this.setPosition(vo.getPosition());
    }

    public HostInventory() {
    }

    public static HostInventory valueOf(HostVO vo) {
        return new HostInventory(vo);
    }

    public static List<HostInventory> valueOf(Collection<HostVO> vos) {
        List<HostInventory> invs = new ArrayList<>(vos.size());
        for (HostVO vo : vos) {
            invs.add(HostInventory.valueOf(vo));
        }
        return invs;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostType() {
        return hostType;
    }

    public void setHostType(String hostType) {
        this.hostType = hostType;
    }

    public HostState getState() {
        return state;
    }

    public void setState(HostState state) {
        this.state = state;
    }

    public HostStatus getStatus() {
        return status;
    }

    public void setStatus(HostStatus status) {
        this.status = status;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
