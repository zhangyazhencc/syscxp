package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by frank on 7/10/2015.
 */
@Action(category = AccountConstant.ACTION_CATEGORY, names = {"user"})
public class APIUpdateUserMsg extends APIMessage implements AccountMessage{

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
    private String PolicyUuid;

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

    public String getPolicyUuid() {
        return PolicyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        PolicyUuid = policyUuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Updating").resource(uuid, UserVO.class.getSimpleName())
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
        return this.getAccountUuid();
    }
}
