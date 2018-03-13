package com.syscxp.alarm.header.contact;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.tunnel.TunnelVO;

import java.util.List;

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"update"})
public class APIUpdateContactMsg extends APIMessage {

    @APIParam(emptyString = false, resourceType = ContactVO.class)
    private String uuid;

    @APIParam(required = false)
    private String name;

    @APIParam(required = false)
    private String email;

    @APIParam(required = false)
    private String mobile;

    @APIParam(required = false)
    private String mobileCaptcha;

    @APIParam(required = false)
    private String emailCaptcha;

    @APIParam(required = false)
    private List<String> ways;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<String> getWays() {
        return ways;
    }

    public void setWays(List<String> ways) {
        this.ways = ways;
    }

    public String getMobileCaptcha() {
        return mobileCaptcha;
    }

    public void setMobileCaptcha(String mobileCaptcha) {
        this.mobileCaptcha = mobileCaptcha;
    }

    public String getEmailCaptcha() {
        return emailCaptcha;
    }

    public void setEmailCaptcha(String emailCaptcha) {
        this.emailCaptcha = emailCaptcha;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update ContactVO")
                        .resource(uuid, ContactVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
