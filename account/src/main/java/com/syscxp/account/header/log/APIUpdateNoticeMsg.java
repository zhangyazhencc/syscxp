package com.syscxp.account.header.log;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.sql.Timestamp;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT,adminOnly = true)
public class APIUpdateNoticeMsg extends APIMessage {
    @APIParam(resourceType = NoticeVO.class,checkAccount = true, emptyString = false)
    private String uuid;
    @APIParam(maxLength = 255, required = false)
    private String title;
    @APIParam(maxLength = 255, required = false)
    private String link;
    @APIParam(required = false)
    private Timestamp startTime;
    @APIParam(required = false)
    private Timestamp endTime;
    @APIParam(required = false)
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update NoticeVO")
                        .resource(uuid, NoticeVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
