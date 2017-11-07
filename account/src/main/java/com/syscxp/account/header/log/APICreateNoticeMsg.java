package com.syscxp.account.header.log;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APICreateMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.sql.Timestamp;
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, adminOnly = true)
public class APICreateNoticeMsg extends APICreateMessage {
    @APIParam(maxLength = 255, emptyString = false)
    private String title;
    @APIParam(maxLength = 255, emptyString = false)
    private String link;
    @APIParam
    private Timestamp startTime;
    @APIParam
    private Timestamp endTime;

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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateNoticeEvent) evt).getInventory().getUuid();
                }
                ntfy("Create NoticeVO")
                        .resource(uuid, NoticeVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
