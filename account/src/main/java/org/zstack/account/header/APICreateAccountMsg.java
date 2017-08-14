package org.zstack.account.header;

import org.zstack.header.identity.AccountStatus;
import org.zstack.header.identity.AccountGrade;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APICreateAccountMsg extends APIMessage {
    @APIParam(maxLength = 255)
    private String name;
    @APIParam(maxLength = 255)
    private String password;
    @APIParam(maxLength = 255)
    private String email;
    @APIParam(maxLength = 255)
    private String phone;
    @APIParam(maxLength = 255)
    private String trueName;
    @APIParam(maxLength = 255, required = false)
    private String company;
    @APIParam(maxLength = 255)
    private String industry;
    @APIParam(maxLength = 255, required = false)
    private AccountGrade grade;
    @APIParam(maxLength = 255 )
    private AccountStatus status;
    @APIParam(validValues = {"SystemAdmin", "Normal","Proxy"}, required = false)
    private String type;
    @APIParam(maxLength = 2048, required = false)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCompany() {
        return company;
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

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setGrade(AccountGrade grade) {
        this.grade = grade;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
