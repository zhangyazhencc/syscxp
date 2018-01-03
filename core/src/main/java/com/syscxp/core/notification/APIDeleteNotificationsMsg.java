package com.syscxp.core.notification;

import org.springframework.http.HttpMethod;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.rest.RestRequest;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by xing5 on 2017/3/18.
 */
@RestRequest(
        method = HttpMethod.DELETE,
        isAction = true,
        responseClass = APIDeleteNotificationsEvent.class
)
public class APIDeleteNotificationsMsg extends APIMessage {
    @APIParam(nonempty = true)
    private List<String> uuids;

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public static APIDeleteNotificationsMsg __example__() {
        APIDeleteNotificationsMsg msg = new APIDeleteNotificationsMsg();
        msg.setUuids(asList(uuid(),uuid()));
        return msg;
    }
}
