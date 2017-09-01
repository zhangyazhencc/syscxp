package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountGrade;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIParam;

@SuppressCredentialCheck
public class APIRegisterAccountMsg extends APICreateMessage implements AccountMessage{
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
}
