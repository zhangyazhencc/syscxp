package com.syscxp.vpn.header.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN)
public class APIDownloadCertificateMsg extends APIVpnMessage {
    @APIParam(resourceType = VpnVO.class, checkAccount = true)
    private String uuid;
    @APIParam
    private String sid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Download certificate")
                        .resource(uuid, VpnVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
