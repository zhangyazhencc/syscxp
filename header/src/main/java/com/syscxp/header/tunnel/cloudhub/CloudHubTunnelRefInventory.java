package com.syscxp.header.tunnel.cloudhub;


import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = CloudHubTunnelRefVO.class)
public class CloudHubTunnelRefInventory {

    private Long id;
    private String cloudHubUuid;
    private String tunnelUuid;
    private Timestamp createDate;


    public static CloudHubTunnelRefInventory valueOf(CloudHubTunnelRefVO vo) {
        CloudHubTunnelRefInventory inv = new CloudHubTunnelRefInventory();
        inv.setId(vo.getId());
        inv.setCloudHubUuid(vo.getCloudHubUuid());
        inv.setTunnelUuid(vo.getTunnelUuid());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<CloudHubTunnelRefInventory> valueOf(Collection<CloudHubTunnelRefVO> vos) {
        List<CloudHubTunnelRefInventory> lst = new ArrayList<CloudHubTunnelRefInventory>(vos.size());
        for (CloudHubTunnelRefVO vo : vos) {
            lst.add(CloudHubTunnelRefInventory.valueOf(vo));
        }
        return lst;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCloudHubUuid() {
        return cloudHubUuid;
    }

    public void setCloudHubUuid(String cloudHubUuid) {
        this.cloudHubUuid = cloudHubUuid;
    }

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
