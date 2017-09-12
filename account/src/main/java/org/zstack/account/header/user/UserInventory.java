package org.zstack.account.header.user;


import org.zstack.account.header.identity.RoleInventory;
import org.zstack.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = UserVO.class)
public class UserInventory {
    private String uuid;
    private String accountUuid;
    private String name;
    private String email;
    private String status;
    private String phone;
    private String emailStatus;
    private String phoneStatus;

    private String userType;
    private String trueName;
    private String department;
    private String description;
    private Timestamp createDate;
    private Timestamp lastOpDate;

    private List<RoleInventory> roles;

    public static UserInventory valueOf(UserVO vo) {
        UserInventory inv = new UserInventory();
        inv.setUuid(vo.getUuid());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setName(vo.getName());
        inv.setDepartment(vo.getDepartment());
        inv.setEmail(vo.getEmail());
        inv.setPhone(vo.getPhone());

        inv.setUserType(vo.getUserType().toString());

        inv.setPhoneStatus(vo.getPhoneStatus().toString());
        inv.setEmailStatus(vo.getEmailStatus().toString());
        inv.setStatus(vo.getStatus().toString());
        inv.setTrueName(vo.getTrueName());
        inv.setDescription(vo.getDescription());

        inv.setRoles(RoleInventory.valueOf(vo.getRoleSet()));

        inv.setCreateDate(vo.getCreateDate());
        inv.setLastOpDate(vo.getLastOpDate());

        return inv;
    }

    public static List<UserInventory> valueOf(Collection<UserVO> vos) {
        List<UserInventory> invs = new ArrayList<UserInventory>(vos.size());
        for (UserVO vo : vos) {
            invs.add(UserInventory.valueOf(vo));
        }
        return invs;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public String getPhone() {
        return phone;
    }

    public String getTrueName() {
        return trueName;
    }

    public String getDepartment() {
        return department;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<RoleInventory> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInventory> roles) {
        this.roles = roles;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
