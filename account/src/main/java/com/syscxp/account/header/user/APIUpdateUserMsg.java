package com.syscxp.account.header.user;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.header.identity.AccountStatus;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by frank on 7/10/2015.
 */
@SuppressUserCredentialCheck
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_USER)
public class APIUpdateUserMsg extends APIMessage implements AccountMessage {

    @APIParam(resourceType = UserVO.class, checkAccount = true, operationTarget = true, required = false)
    private String uuid;

    @APIParam(maxLength = 128, required = false)
    private String name;
    @APIParam(maxLength = 36, required = false)
    private String email;
    @APIParam(maxLength = 32, required = false)
    private String phone;
    @APIParam(maxLength = 128, required = false)
    private String trueName;
    @APIParam(maxLength = 128, required = false)
    private String department;
    @APIParam(maxLength = 36, required = false)
    private AccountStatus status;
    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(maxLength = 255, required = false)
    private String roleUuid;

    @APIParam(maxLength = 32, required = false)
    private UserType userType;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public AccountStatus getStatus() {
        return status;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public void setStatus(AccountStatus status) {
        this.status = status;
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
                ntfy("UpdateUser").resource(uuid, UserVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
