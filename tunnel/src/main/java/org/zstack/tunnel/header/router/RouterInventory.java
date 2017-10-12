package org.zstack.tunnel.header.router;

import org.zstack.header.search.Inventory;
import org.zstack.tunnel.header.node.NodeInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = RouterVO.class)
public class RouterInventory {

    private String uuid;

    private String name;

    public static RouterInventory valueOf(RouterVO vo){
        RouterInventory inv = new RouterInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        return inv;
    }

    public static List<RouterInventory> valueOf(Collection<RouterVO> vos) {
        List<RouterInventory> lst = new ArrayList<RouterInventory>(vos.size());
        for (RouterVO vo : vos) {
            lst.add(RouterInventory.valueOf(vo));
        }
        return lst;
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
}
