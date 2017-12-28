package com.syscxp.account.header.account;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AccountApiSecurityVO.class)
public class AccountApiSecurityInventory {
    private String uuid;
    private String accountUuid;
    private String secretId;
    private String secretKey;
    private String allowIp;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static AccountApiSecurityInventory valueOf(AccountApiSecurityVO vo) {
        AccountApiSecurityInventory inv = new AccountApiSecurityInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setAllowIp(vo.getAllowIp());
        inv.setSecretKey(vo.getSecretKey());
        inv.setSecretId(vo.getSecretId());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }


    public static List<AccountApiSecurityInventory> valueOf(Collection<AccountApiSecurityVO> vos) {
        List<AccountApiSecurityInventory> invs = new ArrayList<AccountApiSecurityInventory>(vos.size());
        for (AccountApiSecurityVO vo : vos) {
            invs.add(AccountApiSecurityInventory.valueOf(vo));
        }
        return invs;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getSecretId() {
        return secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getAllowIp() {
        return allowIp;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAllowIp(String allowIp) {
        this.allowIp = allowIp;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
