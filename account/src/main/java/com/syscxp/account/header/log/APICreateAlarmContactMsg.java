package com.syscxp.account.header.log;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APICreateMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.util.List;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, adminOnly = true)
public class APICreateAlarmContactMsg extends APICreateMessage {
    @APIParam(maxLength = 32, required = false)
    private String accountName;
    @APIParam(maxLength = 128, required = false)
    private String company;
    @APIParam(maxLength = 32, emptyString = false)
    private String name;
    @APIParam(maxLength = 32, required = false)
    private String phone;
    @APIParam(maxLength = 255, required = false)
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
                        .resource(uuid, AlarmContactVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
