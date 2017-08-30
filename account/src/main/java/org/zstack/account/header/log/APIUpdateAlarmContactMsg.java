package org.zstack.account.header.log;

import org.zstack.account.log.NoticeConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

import java.util.List;
@Action(category = NoticeConstant.ACTION_CATEGORY, names = {"alarm_contact"}, accountOnly = true)
public class APIUpdateAlarmContactMsg extends APICreateMessage {
    @APIParam(resourceType = AlarmContactVO.class, emptyString = false)
    private String uuid;
    @APIParam(maxLength = 32, required = false)
    private String name;
    @APIParam(maxLength = 32, required = false)
    private String phone;
    @APIParam(maxLength = 255, required = false)
    private String email;
    @APIParam(maxLength = 32, required = false)
    private List<AlarmChannel> channel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AlarmChannel> getChannel() {
        return channel;
    }

    public void setChannel(List<AlarmChannel> channel) {
        this.channel = channel;
    }

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
                ntfy("Update AlarmContactVO")
                        .resource(uuid, AlarmContactVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
