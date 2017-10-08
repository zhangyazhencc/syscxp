package com.syscxp.account.header.log;

import com.syscxp.header.search.Inventory;

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

    private List<AlarmChannel> channel;

    private String accountName;

    private String company;

    private Timestamp lastOpDate;

    private Timestamp createDate;

    public static AlarmContactInventory valueOf(AlarmContactVO vo) {
        AlarmContactInventory inv = new AlarmContactInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountName(vo.getAccountName());
        inv.setCompany(vo.getCompany());
        inv.setEmail(vo.getEmail());
        inv.setPhone(vo.getPhone());
        inv.setName(vo.getName());
        inv.setChannel(vo.getChannel());
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

    public List<AlarmChannel> getChannel() {
        return channel;
    }

    public void setChannel(List<AlarmChannel> channel) {
        this.channel = channel;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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
