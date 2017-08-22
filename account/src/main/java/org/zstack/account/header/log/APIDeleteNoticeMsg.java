package org.zstack.account.header.log;

import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

public class APIDeleteNoticeMsg extends APIDeleteMessage{
    @APIParam()
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("删除公告").resource(uuid, NoticeVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
