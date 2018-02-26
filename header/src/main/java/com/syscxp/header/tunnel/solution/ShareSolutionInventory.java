package com.syscxp.header.tunnel.solution;

import com.syscxp.header.search.Inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ShareSolutionVO.class)
public class ShareSolutionInventory {
    private String uuid;
    private String accountUuid;
    private String ownerAccountUuid;
    private String solutionUuid;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static ShareSolutionInventory valueOf(ShareSolutionVO vo) {
        ShareSolutionInventory inv = new ShareSolutionInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setOwnerAccountUuid(vo.getOwnerAccountUuid());
        inv.setSolutionUuid(vo.getSolutionUuid());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<ShareSolutionInventory> valueOf(Collection<ShareSolutionVO> vos) {
        List<ShareSolutionInventory> list = new ArrayList<>(vos.size());
        for (ShareSolutionVO vo : vos) {
            list.add(ShareSolutionInventory.valueOf(vo));
        }
        return list;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getOwnerAccountUuid() {
        return ownerAccountUuid;
    }

    public void setOwnerAccountUuid(String ownerAccountUuid) {
        this.ownerAccountUuid = ownerAccountUuid;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
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
