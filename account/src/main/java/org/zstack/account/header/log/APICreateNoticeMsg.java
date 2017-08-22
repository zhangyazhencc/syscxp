package org.zstack.account.header.log;

import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

import java.sql.Timestamp;

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
                ntfy("新建公告", title, link, startTime, endTime)
                        .resource(uuid, NoticeVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
