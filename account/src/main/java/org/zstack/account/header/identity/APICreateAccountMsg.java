package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountIndustry;
import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountGrade;
import org.zstack.header.identity.CompanyNature;
import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APICreateAccountMsg  extends APICreateMessage implements AccountMessage{
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
    @APIParam(maxLength = 128)
    private String company;

    @APIParam(maxLength = 128, required = false)
    private AccountIndustry industry;
    @APIParam(maxLength = 32, required = false)
    private AccountGrade grade;
    @APIParam(maxLength = 128, required = false)
    private AccountStatus status;
    @APIParam(validValues = {"SystemAdmin", "Normal", "Proxy"}, required = false)
    private String type;
    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(maxLength = 255, required = false)
    private CompanyNature companyNature;
    @APIParam(maxLength = 255, required = false)
    private String salesman;
    @APIParam(maxLength = 255, required = false)
    private String contacts;
    @APIParam(maxLength = 255, required = false)
    private String contactNumber;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

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

    public String getTrueName() {
        return trueName;
    }

    public String getCompany() {
        return company;
    }

    public AccountIndustry getIndustry() {
        return industry;
    }

    public AccountGrade getGrade() {
        return grade;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
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

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setIndustry(AccountIndustry industry) {
        this.industry = industry;
    }

    public void setGrade(AccountGrade grade) {
        this.grade = grade;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
