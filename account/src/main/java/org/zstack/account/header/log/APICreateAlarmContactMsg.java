package org.zstack.account.header.log;

import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

public class APICreateAlarmContactMsg extends APICreateMessage {
    @APIParam(maxLength = 32)
    private String accountUuid;
    @APIParam(maxLength = 32)
    private String name;
    @APIParam(maxLength = 32)
    private String phone;
    @APIParam(maxLength = 255)
    private String email;
    @APIParam(maxLength = 32)
    private String channel;

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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAccountUuid() {

        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                if (evt.isSuccess()) {
                    ntfy(String.format("Create AlarmContact[uuid: %s]", ((APICreateAlarmContactEvent) evt).getInventory().getUuid()))
                            .resource(((APICreateAlarmContactEvent) evt).getInventory().getUuid(), AlarmContactVO.class.getSimpleName())
                            .messageAndEvent(that, evt).done();
                } else {
                    ntfy("Create AlarmContact fail")
                            .resource(null, AlarmContactVO.class.getSimpleName())
                            .messageAndEvent(that, evt).done();
                }
            }
        };
    }
}
