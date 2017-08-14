package org.zstack.account.header.log;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.sql.Timestamp;

public class APIUpdateNoticeMsg extends APIMessage {
    @APIParam(resourceType = NoticeVO.class, nonempty = true)
    private String uuid;
    @APIParam(maxLength = 255, required = false)
    private String title;
    @APIParam(maxLength = 255, required = false)
    private String link;
    @APIParam(required = false)
    private Timestamp startTime;
    @APIParam(required = false)
    private Timestamp endTime;

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
}
