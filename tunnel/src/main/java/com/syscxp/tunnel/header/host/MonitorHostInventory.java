package com.syscxp.tunnel.header.host;

import com.syscxp.header.host.HostInventory;
import com.syscxp.header.message.GsonTransient;
import com.syscxp.header.message.NoJsonSchema;
import com.syscxp.header.rest.APINoSee;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.search.Parent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Inventory(mappingVOClass = MonitorHostVO.class, collectionValueOfMethod = "valueOf1",
        parent = {@Parent(inventoryClass = HostInventory.class, type = MonitorConstant.HOST_TYPE)})
public class MonitorHostInventory extends HostInventory {
    private String username;
    @GsonTransient
    @APINoSee
    @NoJsonSchema
    private String password;

    private Integer sshPort;

    protected MonitorHostInventory(MonitorHostVO vo) {
        super(vo);
        this.setUsername(vo.getUsername());
        this.setPassword(vo.getPassword());
        this.setSshPort(vo.getSshPort());
    }

    public static MonitorHostInventory valueOf(MonitorHostVO vo) {
        return new MonitorHostInventory(vo);
    }

    public static List<MonitorHostInventory> valueOf1(Collection<MonitorHostVO> vos) {
        List<MonitorHostInventory> invs = new ArrayList<>();
        for (MonitorHostVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

}
