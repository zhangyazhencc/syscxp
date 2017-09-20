package org.zstack.vpn.header.host;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ZoneVO.class)
public class ZoneInventory {
    private String uuid;
    private String name;
    private String description;
    private String province;
    private String nodeUuid;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static ZoneInventory valueOf(ZoneVO vo){
        ZoneInventory inv = new ZoneInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setDescription(vo.getDescription());
        inv.setProvince(vo.getProvince());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<ZoneInventory> valueOf(Collection<ZoneVO> vos){
        List<ZoneInventory> invs = new ArrayList<ZoneInventory>(vos.size());
        for (ZoneVO vo:vos){
            invs.add(ZoneInventory.valueOf(vo));
        }
        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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
