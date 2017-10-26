package com.syscxp.header.host;

import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import static com.syscxp.header.message.APIDeleteMessage.DeletionMode.Permissive;

public class APIDeleteHostMsg extends APIDeleteMessage implements HostMessage {
    /**
     * @desc host uuid
     */
    @APIParam
    private String uuid;

    public APIDeleteHostMsg() {
    }

    public APIDeleteHostMsg(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getHostUuid() {
        return getUuid();
    }

    public static APIDeleteHostMsg __example__() {
        APIDeleteHostMsg msg = new APIDeleteHostMsg();
        msg.setUuid(uuid());
        msg.setDeletionMode(Permissive);
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Deleted").resource(uuid, HostVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
