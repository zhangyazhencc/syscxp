package com.syscxp.header.tunnel.node;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Create by DCY on 2018/5/3
 */
@Inventory(mappingVOClass = AccountNodeRefVO.class)
public class AccountNodeRefInventory {

    private String uuid;
    private String accountUuid;
    private String nodeUuid;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static AccountNodeRefInventory valueOf(AccountNodeRefVO vo){
        AccountNodeRefInventory inv = new AccountNodeRefInventory();

        inv.setUuid(vo.getUuid());
        inv.setNodeUuid(vo.getNodeUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<AccountNodeRefInventory> valueOf(Collection<AccountNodeRefVO> vos) {
        List<AccountNodeRefInventory> lst = new ArrayList<AccountNodeRefInventory>(vos.size());
        for (AccountNodeRefVO vo : vos) {
            lst.add(AccountNodeRefInventory.valueOf(vo));
        }
        return lst;
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

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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
