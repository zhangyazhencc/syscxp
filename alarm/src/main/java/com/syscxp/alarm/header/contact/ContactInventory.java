package com.syscxp.alarm.header.contact;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Inventory(mappingVOClass = ContactVO.class)
public class ContactInventory {
    private String uuid;
    private String name;
    private String email;
    private String mobile;
    private Timestamp createDate;
    private Timestamp lastOpDate;
    private Set<NotifyWayVO> ways;

    public static ContactInventory valueOf(ContactVO vo) {
        ContactInventory inv = new ContactInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setEmail(vo.getEmail());
        inv.setMobile(vo.getMobile());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setWays(vo.getNotifyWayVOs());
        return inv;
    }

    public static List<ContactInventory> valueOf(Collection<ContactVO> vos) {
        List<ContactInventory> lst = new ArrayList<>(vos.size());
        for (ContactVO vo : vos) {
            lst.add(ContactInventory.valueOf(vo));
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public Set<NotifyWayVO> getWays() {
        return ways;
    }

    public void setWays(Set<NotifyWayVO> ways) {
        this.ways = ways;
    }
}
