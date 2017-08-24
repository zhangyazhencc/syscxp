package org.zstack.account.header.log;

import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

import java.sql.Timestamp;

public class APIUpdateNoticeMsg extends APIMessage {
    @APIParam(resourceType = NoticeVO.class, emptyString = false)
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
                        .resource(uuid, NoticeVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
