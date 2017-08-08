package org.zstack.tunnel.header.inventory;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.vo.NetworkTypeVO;

import java.sql.Timestamp;

@Inventory(mappingVOClass = NetworkTypeVO.class)
public class NetworkTypeInventory {

    private String uuid;
    private String name;
    private String code;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static NetworkTypeInventory valueOf(NetworkTypeVO vo){
        NetworkTypeInventory inv = new NetworkTypeInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setCode(vo.getCode());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
