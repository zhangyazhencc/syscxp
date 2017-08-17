package org.zstack.sms.header;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by zxhread on 17/8/14.
 */
@Entity
@Table
public class SmsVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String accountUuid;

    @Column
    private String userUuid;

    @Column
    private String ip;

    @Column
    private String phone;

    @Column
    private String appId;

    @Column
    private String templateId;

    @Column
    private String data;

    @Column
    private String statusCode;

    @Column
    private String statusMsg;

    @Column
    private String dateCreated;

    @Column
    private String smsMessagesId;

    @Column
    private String msgEntrance;

    @Column
    private Date createDay;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp lastOpDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getSmsMessagesId() {
        return smsMessagesId;
    }

    public void setSmsMessagesId(String smsMessagesId) {
        this.smsMessagesId = smsMessagesId;
    }

    public String getMsgEntrance() {
        return msgEntrance;
    }

    public void setMsgEntrance(String msgEntrance) {
        this.msgEntrance = msgEntrance;
    }

    public Date getCreateDay() {
        return createDay;
    }

    public void setCreateDay(Date createDay) {
        this.createDay = createDay;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }
}
