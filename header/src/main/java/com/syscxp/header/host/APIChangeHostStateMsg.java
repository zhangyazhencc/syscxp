package com.syscxp.header.host;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {HostConstant.TUNNEL_ACTION_SERVICE, HostConstant.VPN_ACTION_SERVICE}, category = HostConstant.ACTION_CATEGORY, adminOnly = true)
public class APIChangeHostStateMsg extends APIMessage implements HostMessage {
    /**
     * @desc host uuid
     */
    @APIParam(resourceType = HostVO.class)
    private String uuid;
    /**
     * @desc - enable: enable host
     * - disable: disable host
     * - maintain: putting host in to Maintenance
     * <p>
     * see state in :ref:`HostInventory` for details
     * @choices - enable
     * - disable
     * - maintain
     */
    @APIParam(validValues = {"enable", "disable", "maintain"})
    private String stateEvent;

    public APIChangeHostStateMsg() {
    }

    public APIChangeHostStateMsg(String uuid, String event) {
        this.uuid = uuid;
        this.stateEvent = event;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStateEvent() {
        return stateEvent;
    }

    public void setStateEvent(String stateEvent) {
        this.stateEvent = stateEvent;
    }

    @Override
    public String getHostUuid() {
        return getUuid();
    }

    public static APIChangeHostStateMsg __example__() {
        APIChangeHostStateMsg msg = new APIChangeHostStateMsg();
        msg.setUuid(uuid());
        msg.setStateEvent(HostStateEvent.enable.toString());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String content;
                if (evt.isSuccess()) {
                    content = String.format("Changed the state to %s", ((APIChangeHostStateEvent) evt).getInventory().getState());
                } else {
                    content = "Changed the state failed";
                }
                ntfy(content).resource(uuid, HostVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
