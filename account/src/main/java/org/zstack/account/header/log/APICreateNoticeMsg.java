package org.zstack.account.header.log;

import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIParam;

import java.sql.Timestamp;

public class APICreateNoticeMsg extends APICreateMessage {
    @APIParam(maxLength = 255)
    private String title;
    @APIParam(maxLength = 255)
    private String link;
    @APIParam(nonempty = true)
    private Timestamp startTime;
    @APIParam(nonempty = true)
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
}
