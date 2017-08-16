package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APICreateUserMsg extends APICreateMessage implements AccountMessage {

    @APIParam(maxLength = 255)
    private String name;
    @APIParam(maxLength = 255)
    private String password;
    @APIParam(maxLength = 2048, required = false)
    private String accountUuid;
    @APIParam(maxLength = 2048, required = false)
    private String email;
    @APIParam(maxLength = 2048, required = false)
    private String phone;
    @APIParam(maxLength = 2048, required = false)
    private String trueName;
    @APIParam(maxLength = 2048, required = false)
    private String department;
    @APIParam(validValues = {"Available", "Disabled"}, required = false)
    private String status;
    @APIParam(maxLength = 2048, required = false)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
