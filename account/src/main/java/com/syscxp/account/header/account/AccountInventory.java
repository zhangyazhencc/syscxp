package com.syscxp.account.header.account;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = AccountVO.class)
public class AccountInventory {
    private String uuid;
    private String name;
    private String emailStatus;
    private String phoneStatus;

    private String email;
    private String phone;

    private String trueName;
    private String company;
    private String industry;
    private String status;
    private String description;
    private String type;

    private Timestamp createDate;
    private Timestamp lastOpDate;

    private AccountExtraInfoInventory extraInfo;

    public static AccountInventory valueOf(AccountVO vo) {
        AccountInventory inv = new AccountInventory();
        inv.setUuid(vo.getUuid());
        inv.setName(vo.getName());
        inv.setTrueName(vo.getTrueName());
        inv.setCompany(vo.getCompany());
        inv.setDescription(vo.getDescription());
        inv.setEmail(vo.getEmail());
        inv.setPhone(vo.getPhone());
        inv.setStatus(vo.getStatus().toString());
        inv.setEmailStatus(vo.getEmailStatus().toString());
        inv.setPhoneStatus(vo.getPhoneStatus().toString());

        inv.setType(vo.getType().toString());
        inv.setIndustry(vo.getIndustry());

        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        //SystemAdmin 没有扩展信息
        if(vo.getAccountExtraInfo() != null){
            inv.setExtraInfo(AccountExtraInfoInventory.valueOf(vo.getAccountExtraInfo()));
        }


        return inv;
    }

    public static List<AccountInventory> valueOf(Collection<AccountVO> vos) {
        List<AccountInventory> lst = new ArrayList<AccountInventory>(vos.size());
        for (AccountVO vo : vos) {
            lst.add(AccountInventory.valueOf(vo));
        }
        return lst;
    }


    public String getEmailStatus() {
        return emailStatus;
    }

    public String getPhoneStatus() {
        return phoneStatus;
    }

    public void setEmailStatus(String emailStatus) {
        this.emailStatus = emailStatus;
    }

    public void setPhoneStatus(String phoneStatus) {
        this.phoneStatus = phoneStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getTrueName() {
        return trueName;
    }

    public String getCompany() {
        return company;
    }

    public String getIndustry() {
        return industry;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccountExtraInfoInventory getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(AccountExtraInfoInventory extraInfo) {
        this.extraInfo = extraInfo;
    }
}
