package org.zstack.account.header.identity;

import org.zstack.header.identity.*;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;


@Action(category = AccountConstant.ACTION_CATEGORY, names = {"account"})
public class APIUpdateAccountMsg extends APIMessage implements AccountMessage{

    @APIParam(resourceType = AccountVO.class, required = true, checkAccount = true, operationTarget = true)
    private String targetUuid;

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
    @APIParam(validValues = {"SystemAdmin", "Normal", "Proxy"}, required = false)
    private String type;
    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(maxLength = 36, required = false)
    private String specialLine;
    @APIParam(maxLength = 36, required = false)
    private String internetCloud;

    @APIParam(maxLength = 255, required = false)
    private CompanyNature companyNature ;
    @APIParam(maxLength = 255, required = false)
    private String salesman;
    @APIParam(maxLength = 255, required = false)
    private String contacts;
    @APIParam(maxLength = 255, required = false)
    private String contactNumber;

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

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
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

    public CompanyNature getCompanyNature() {
        return companyNature;
    }

    public String getSalesman() {
        return salesman;
    }

    public String getContacts() {
        return contacts;
    }

    public String getContactNumber() {
        return contactNumber;
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

    public void setCompanyNature(CompanyNature companyNature) {
        this.companyNature = companyNature;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSpecialLine() {
        return specialLine;
    }

    public String getInternetCloud() {
        return internetCloud;
    }

    public void setSpecialLine(String specialLine) {
        this.specialLine = specialLine;
    }

    public void setInternetCloud(String internetCloud) {
        this.internetCloud = internetCloud;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Updating").resource(targetUuid, AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }


    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
