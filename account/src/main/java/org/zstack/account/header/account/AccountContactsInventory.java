package org.zstack.account.header.account;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AccountContactsVO.class)

public class AccountContactsInventory {
    private String uuid;
    private String accountUuid;
    private String name;
    private String email;
    private String phone;
    private String noticeWay;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;
    
    public static AccountContactsInventory valueOf(AccountContactsVO vo) {
        AccountContactsInventory inv = new AccountContactsInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setEmail(vo.getEmail());
        inv.setPhone(vo.getPhone());
        inv.setNoticeWay(vo.getNoticeWay().toString());
        inv.setDescription(vo.getDescription());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<AccountContactsInventory> valueOf(Collection<AccountContactsVO> vos) {
        List<AccountContactsInventory> lst = new ArrayList<AccountContactsInventory>(vos.size());
        for (AccountContactsVO vo : vos) {
            lst.add(AccountContactsInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getNoticeWay() {
        return noticeWay;
    }

    public String getDescription() {
        return description;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNoticeWay(String noticeWay) {
        this.noticeWay = noticeWay;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
