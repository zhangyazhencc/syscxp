package com.syscxp.account.header.user;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, accountOnly = true)
public class APICreateUserMsg extends APIMessage implements AccountMessage {

    @APIParam(maxLength = 128)
    private String name;
    @APIParam(maxLength = 128)
    private String password;
    @APIParam(maxLength = 36)
    private String email;
    @APIParam(maxLength = 32)
    private String phone;
    @APIParam(maxLength = 128, required = false)
    private String trueName;

    @APIParam(maxLength = 128, required = false)
    private String department;
    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(maxLength = 255, required = false)
    private String roleUuid;

    @APIParam(maxLength = 32, required = false)
    private UserType userType;

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

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateUserEvent)evt).getInventory().getUuid();
                }
                ntfy("Creating").resource(uuid, UserVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }
}
