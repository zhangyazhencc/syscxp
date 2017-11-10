package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AliUserVO.class)
public class AliUserInventory {
    private String uuid;
    private String accountUuid;
    private String aliAccountUuid;
    private String aliAccessKeyID;
    private String aliAccessKeySecret;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static AliUserInventory valueOf(AliUserVO vo){
        AliUserInventory inv = new AliUserInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setAliAccountUuid(vo.getAliAccountUuid());
        inv.setAliAccessKeyID(vo.getAliAccessKeyID());
        inv.setAliAccessKeySecret(vo.getAliAccessKeySecret());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<AliUserInventory> valueOf(Collection<AliUserVO> vos) {
        List<AliUserInventory> lst = new ArrayList<AliUserInventory>(vos.size());
        for (AliUserVO vo : vos) {
            lst.add(AliUserInventory.valueOf(vo));
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

    public String getAliAccountUuid() {
        return aliAccountUuid;
    }

    public void setAliAccountUuid(String aliAccountUuid) {
        this.aliAccountUuid = aliAccountUuid;
    }

    public String getAliAccessKeyID() {
        return aliAccessKeyID;
    }

    public void setAliAccessKeyID(String aliAccessKeyID) {
        this.aliAccessKeyID = aliAccessKeyID;
    }

    public String getAliAccessKeySecret() {
        return aliAccessKeySecret;
    }

    public void setAliAccessKeySecret(String aliAccessKeySecret) {
        this.aliAccessKeySecret = aliAccessKeySecret;
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
