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
    private String publicKey;
    private String privateKey;
    private String allowIp;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    public static AccountApiSecurityInventory valueOf(AccountApiSecurityVO vo) {
        AccountApiSecurityInventory inv = new AccountApiSecurityInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setAllowIp(vo.getAllowIp());
        inv.setPrivateKey(vo.getPrivateKey());
        inv.setPublicKey(vo.getPublicKey());
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

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
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

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
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
