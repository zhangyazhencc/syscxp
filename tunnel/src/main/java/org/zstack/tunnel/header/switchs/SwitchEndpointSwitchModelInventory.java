package org.zstack.tunnel.header.switchs;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.endpoint.EndpointInventory;
import org.zstack.tunnel.header.endpoint.EndpointVO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by DCY on 2017-09-01
 */
@Inventory(mappingVOClass = SwitchVO.class)
public class SwitchEndpointSwitchModelInventory {
    private String uuid;
    private String endpointUuid;
    private EndpointInventory endpoint;
    private String code;
    private String name;
    private String switchAttributionUuid;
    private SwitchAttributionInventory switchAttribution;
    private SwitchUpperType upperType;
    private Integer enabled;
    private String description;
    private SwitchStatus status;
    private Integer isPrivate;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static SwitchEndpointSwitchModelInventory valueOf(SwitchVO vo){
        SwitchEndpointSwitchModelInventory inv = new SwitchEndpointSwitchModelInventory();

        inv.setUuid(vo.getUuid());
        inv.setEndpointUuid(vo.getEndpointUuid());
        inv.setEndpoint(EndpointInventory.valueOf(vo.getEndpoint()));
        inv.setCode(vo.getCode());
        inv.setName(vo.getName());
        inv.setSwitchAttributionUuid(vo.getSwitchAttributionUuid());
        inv.setSwitchAttribution(SwitchAttributionInventory.valueOf(vo.getSwitchAttribution()));
        inv.setUpperType(vo.getUpperType());
        inv.setEnabled(vo.getEnabled());
        inv.setDescription(vo.getDescription());
        inv.setStatus(vo.getStatus());
        inv.setIsPrivate(vo.getIsPrivate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());

        return inv;
    }

    public static List<SwitchEndpointSwitchModelInventory> valueOf(Collection<SwitchVO> vos) {
        List<SwitchEndpointSwitchModelInventory> lst = new ArrayList<SwitchEndpointSwitchModelInventory>(vos.size());
        for (SwitchVO vo : vos) {
            lst.add(SwitchEndpointSwitchModelInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEndpointUuid() {
        return endpointUuid;
    }

    public void setEndpointUuid(String endpointUuid) {
        this.endpointUuid = endpointUuid;
    }

    public EndpointInventory getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(EndpointInventory endpoint) {
        this.endpoint = endpoint;
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

    public String getSwitchAttributionUuid() {
        return switchAttributionUuid;
    }

    public void setSwitchAttributionUuid(String switchAttributionUuid) {
        this.switchAttributionUuid = switchAttributionUuid;
    }

    public SwitchAttributionInventory getSwitchAttribution() {
        return switchAttribution;
    }

    public void setSwitchAttribution(SwitchAttributionInventory switchAttribution) {
        this.switchAttribution = switchAttribution;
    }

    public SwitchUpperType getUpperType() {
        return upperType;
    }

    public void setUpperType(SwitchUpperType upperType) {
        this.upperType = upperType;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SwitchStatus getStatus() {
        return status;
    }

    public void setStatus(SwitchStatus status) {
        this.status = status;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
