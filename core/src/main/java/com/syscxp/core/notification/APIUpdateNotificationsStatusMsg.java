package com.syscxp.core.notification;

import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by xing5 on 2017/3/18.
 */

public class APIUpdateNotificationsStatusMsg extends APIMessage {
    @APIParam(nonempty = true)
    private List<String> uuids;
    @APIParam(validValues = {"Unread", "Read"})
    private String status;

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static APIUpdateNotificationsStatusMsg __example__() {
        APIUpdateNotificationsStatusMsg msg = new APIUpdateNotificationsStatusMsg();
        msg.setUuids(asList(uuid(),uuid()));
        msg.setStatus("Read");
        return msg;
    }
}
