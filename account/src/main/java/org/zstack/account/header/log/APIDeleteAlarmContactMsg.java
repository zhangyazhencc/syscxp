package org.zstack.account.header.log;

import org.zstack.account.log.NoticeConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIDeleteMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
@Action(category = NoticeConstant.ACTION_CATEGORY, names = {"alarm_contact"}, accountOnly = true)
public class APIDeleteAlarmContactMsg extends APIDeleteMessage {
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
                ntfy("Delete AlarmContactVO")
                        .resource(uuid, AlarmContactVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
