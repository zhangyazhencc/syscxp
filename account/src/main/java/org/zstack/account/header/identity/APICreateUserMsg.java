package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY,names = {"user"}, accountOnly = true)
public class APICreateUserMsg extends APIMessage implements AccountMessage {

    @APIParam(maxLength = 128)
    private String name;
    @APIParam(maxLength = 128)
    private String password;
    @APIParam(maxLength = 36)
    private String email;
    @APIParam(maxLength = 32)
    private String phone;
    @APIParam(maxLength = 128)
    private String trueName;
    @APIParam(maxLength = 128, required = false)
    private String department;
    @APIParam(maxLength = 255, required = false)
    private String description;
    @APIParam(maxLength = 255, required = false)
    private String policyUuid;

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

//    public String getStatus() {
//        return status;
//    }

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

//    public void setStatus(String status) {
//        this.status = status;
//    }

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
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
                ntfy("Creating").resource(uuid, UserVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
