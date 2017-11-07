package com.syscxp.vpn.header.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.vpn.vpn.VpnConstant;

@SuppressCredentialCheck
@Action(category = VpnConstant.ACTION_CATEGORY_VPN)
public class APIDownloadCertificateMsg extends APIMessage {
    @APIParam(resourceType = VpnVO.class)
    private String sid;
    @APIParam
    private long timestamp;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Download certificate")
                        .resource(sid, VpnVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
