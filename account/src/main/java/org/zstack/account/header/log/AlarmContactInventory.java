package org.zstack.account.header.log;

import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AlarmContactVO.class)
public class AlarmContactInventory {
    private String uuid;

    private String name;

    private String phone;

    private String email;

    private String accountUuid;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static AlarmContactInventory valueOf(AlarmContactVO vo) {
        AlarmContactInventory inv = new AlarmContactInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setEmail(vo.getEmail());
        inv.setPhone(vo.getPhone());
        inv.setName(vo.getName());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        return inv;
    }

    public static List<AlarmContactInventory> valueOf(Collection<AlarmContactVO> vos) {
        List<AlarmContactInventory> lst = new ArrayList<AlarmContactInventory>(vos.size());
        for (AlarmContactVO vo : vos) {
            lst.add(AlarmContactInventory.valueOf(vo));
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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
