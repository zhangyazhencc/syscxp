package com.syscxp.account.header.account;

import com.syscxp.header.identity.AccountStatus;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;


@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, accountOnly = true)
public class APIUpdateAccountMsg extends APIMessage implements AccountMessage{

    @APIParam
    private String uuid;

    @APIParam(maxLength = 36, required = false)
    private String email ;
    @APIParam(maxLength = 32, required = false)
    private String phone;
    @APIParam(maxLength = 128, required = false)
    private String trueName;
    @APIParam(maxLength = 128, required = false)
    private String company;
    @APIParam(maxLength = 128, required = false)
    private String industry;

    @APIParam(maxLength = 32, required = false)
    private AccountGrade grade;
    @APIParam(maxLength = 36, required = false)
    private String userUuid;

    @APIParam(maxLength = 128, required = false)
    private AccountStatus status;
    @APIParam(validValues = {"Normal", "Proxy"}, required = false)
    private String type;
    @APIParam(maxLength = 255, required = false)
    private String description;




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getType() {
        return type;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIndustry() {
        return industry;
    }

    public AccountGrade getGrade() {
        return grade;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setGrade(AccountGrade grade) {
        this.grade = grade;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Updating").resource(uuid, AccountVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }


    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
