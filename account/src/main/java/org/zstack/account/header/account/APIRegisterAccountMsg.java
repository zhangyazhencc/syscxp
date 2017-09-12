package org.zstack.account.header.account;

import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@SuppressCredentialCheck
public class APIRegisterAccountMsg extends APIMessage {
    @APIParam(maxLength = 128)
    private String name;
    @APIParam(maxLength = 128)
    private String password;
    @APIParam(maxLength = 36)
    private String email;
    @APIParam(validRegexValues = "^1[3,4,5,7,8]\\d{9}$")
    private String phone;

    @APIParam(maxLength = 32)
    private String code;

    @APIParam(maxLength = 128)
    private String company;

    @APIParam(maxLength = 128, required = false)
    private String industry;

    @APIParam(maxLength = 255, required = false)
    private String description;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany() {
        return company;
    }

    public String getIndustry() {
        return industry;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APIRegisterAccountEvent)evt).getInventory().getUuid();
                }
                ntfy("Regist ").resource(uuid, AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
