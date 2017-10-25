package com.syscxp.alarm.header.contact;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Inventory(mappingVOClass = ContactGroupVO.class)
public class ContactGroupInventory {
    private String uuid;
    private String groupCode;
    private String groupName;
    private Timestamp createDate;
    private Timestamp lastOpDate;
    private Set<ContactVO> contactVOList;

    public static ContactGroupInventory valueOf(ContactGroupVO vo) {
        ContactGroupInventory inv = new ContactGroupInventory();
        inv.setUuid(vo.getUuid());
        inv.setGroupCode(vo.getGroupCode());
        inv.setGroupName(vo.getGroupName());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setContactVOList(vo.getContactVOList());
        return inv;
    }

    public static List<ContactGroupInventory> valueOf(Collection<ContactGroupVO> vos) {
        List<ContactGroupInventory> lst = new ArrayList<>(vos.size());
        for (ContactGroupVO vo : vos) {
            lst.add(ContactGroupInventory.valueOf(vo));
        }
        return lst;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public Set<ContactVO> getContactVOList() {
        return contactVOList;
    }

    public void setContactVOList(Set<ContactVO> contactVOList) {
        this.contactVOList = contactVOList;
    }
}
