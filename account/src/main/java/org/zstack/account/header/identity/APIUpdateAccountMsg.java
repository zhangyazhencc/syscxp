package org.zstack.account.header.identity;

import org.zstack.header.identity.*;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;


@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"update"})
public class APIUpdateAccountMsg extends APIMessage implements AccountMessage{

    @APIParam(resourceType = AccountVO.class, required = true, checkAccount = true, operationTarget = true)
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
    @APIParam(maxLength = 128, required = false)
    private AccountStatus status;
    @APIParam(validValues = {"Normal", "Proxy"}, required = false)
    private String type;
    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(maxLength = 255, required = false)
    private String salesman;


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

    public String getSalesman() {
        return salesman;
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

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Updating").resource(uuid, AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }


    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
