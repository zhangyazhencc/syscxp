package org.zstack.sms.header;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zxhread on 17/8/14.
 */
public class SmsInventory {
    private long id;

    private String accountUuid;

    private String phone;

    private String appId;

    private String templateId;

    private String data;

    private String statusCode;

    private String statusMsg;

    private String msgEntrance;

    private Timestamp createDate;

    public static SmsInventory valueOf(SmsVO vo) {
        SmsInventory inv = new SmsInventory();
        inv.setId(vo.getId());
        inv.setAccountUuid(vo.getAccountUuid());
        inv.setPhone(vo.getPhone());
        inv.setAppId(vo.getAppId());
        inv.setTemplateId(vo.getTemplateId());
        inv.setData(vo.getData());
        inv.setStatusCode(vo.getStatusCode());
        inv.setStatusMsg(vo.getStatusMsg());
        inv.setMsgEntrance(vo.getMsgEntrance());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<SmsInventory> valueOf(Collection<SmsVO> vos) {
        List<SmsInventory> invs = new ArrayList<SmsInventory>(vos.size());
        for (SmsVO vo : vos) {
            invs.add(SmsInventory.valueOf(vo));
        }
        return invs;
    }

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

    public String getMsgEntrance() {
        return msgEntrance;
    }

    public void setMsgEntrance(String msgEntrance) {
        this.msgEntrance = msgEntrance;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
