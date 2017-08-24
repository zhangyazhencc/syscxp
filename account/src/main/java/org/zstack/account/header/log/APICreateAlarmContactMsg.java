package org.zstack.account.header.log;

import org.zstack.header.message.APICreateMessage;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

import java.util.List;

public class APICreateAlarmContactMsg extends APICreateMessage {
    @APIParam(maxLength = 32, emptyString = false)
    private String accountName;
    @APIParam(maxLength = 128, emptyString = false)
    private String company;
    @APIParam(maxLength = 32, emptyString = false)
    private String name;
    @APIParam(maxLength = 32, emptyString = false)
    private String phone;
    @APIParam(maxLength = 255, emptyString = false)
    private String email;
    @APIParam(maxLength = 32, nonempty = true)
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateAlarmContactEvent) evt).getInventory().getUuid();
                }

                ntfy("Create AlarmContactVO")
                        .resource(uuid, AlarmContactVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
