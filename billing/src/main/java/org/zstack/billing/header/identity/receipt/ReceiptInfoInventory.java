package org.zstack.billing.header.identity.receipt;


import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = ReceiptInfoVO.class)
public class ReceiptInfoInventory {
    private String uuid;

    private String accountUuid;

    private ReceiptType type;

    private String title;

    private String bankName;

    private String bankAccountNumber;

    private String telephone;

    private String identifyNumber;

    private String address;

    private boolean isDefault;

    private Timestamp createDate;

    private Timestamp lastOpDate;

    public static ReceiptInfoInventory valueOf(ReceiptInfoVO vo) {
        ReceiptInfoInventory inv = new ReceiptInfoInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setAddress(vo.getAddress());
        inv.setDefault(vo.isDefault());
        inv.setTelephone(vo.getTelephone());
        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setBankAccountNumber(vo.getBankAccountNumber());
        inv.setBankName(vo.getBankName());
        inv.setIdentifyNumber(vo.getIdentifyNumber());
        inv.setTitle(vo.getTitle());
        inv.setType(vo.getType());

        return inv;
    }

    public static List<ReceiptInfoInventory> valueOf(Collection<ReceiptInfoVO> vos) {
        List<ReceiptInfoInventory> lst = new ArrayList<>(vos.size());
        for (ReceiptInfoVO vo : vos) {
            lst.add(ReceiptInfoInventory.valueOf(vo));
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

    public ReceiptType getType() {
        return type;
    }

    public void setType(ReceiptType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
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
