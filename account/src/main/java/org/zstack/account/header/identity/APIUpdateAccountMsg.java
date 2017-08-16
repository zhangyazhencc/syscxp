package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;


@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APIUpdateAccountMsg extends APIMessage {
    @APIParam(resourceType = AccountVO.class, required = true, checkAccount = true, operationTarget = true)
    private String uuid;

    @APIParam(resourceType = AccountVO.class, required = true, checkAccount = true, operationTarget = true)
    private String targetUuid;

    @APIParam(maxLength = 255, required = false)
    private String password;
    @APIParam(maxLength = 255, required = false)
    private String email ;
    @APIParam(maxLength = 2048, required = false)
    private String phone;
    @APIParam(maxLength = 2048, required = false)
    private String trueName;
    @APIParam(maxLength = 2048, required = false)
    private String company;
    @APIParam(maxLength = 2048, required = false)
    private String department;
    @APIParam(maxLength = 2048, required = false)
    private String industry;
    @APIParam(maxLength = 2048, required = false)
    private String grade;
    @APIParam(maxLength = 2048, required = false)
    private String status;
    @APIParam(maxLength = 2048, required = false)
    private String description;
    @APIParam(maxLength = 2048, required = false)
    private String type;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public String getDepartment() {
        return department;
    }

    public String getIndustry() {
        return industry;
    }

    public String getGrade() {
        return grade;
    }

    public String getStatus() {
        return status;
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

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setStatus(String status) {
        this.status = status;
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


}
