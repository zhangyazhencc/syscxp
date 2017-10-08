package com.syscxp.billing.header.receipt;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ReceiptPostAddressVO.class)
public class ReceiptPostAddressInventory {

    private String uuid;

    private String accountUuid;

    private String name;

    private String telephone;

    private String address;

    private boolean isDefault;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static ReceiptPostAddressInventory valueOf(ReceiptPostAddressVO vo) {
        ReceiptPostAddressInventory inv = new ReceiptPostAddressInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setAddress(vo.getAddress());
        inv.setDefault(vo.isDefault());
        inv.setName(vo.getName());
        inv.setTelephone(vo.getTelephone());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<ReceiptPostAddressInventory> valueOf(Collection<ReceiptPostAddressVO> vos) {
        List<ReceiptPostAddressInventory> lst = new ArrayList<>(vos.size());
        for (ReceiptPostAddressVO vo : vos) {
            lst.add(ReceiptPostAddressInventory.valueOf(vo));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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
