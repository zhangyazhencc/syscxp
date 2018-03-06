package com.syscxp.core.notification;

import com.syscxp.header.identity.Action;
import org.springframework.http.HttpMethod;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by xing5 on 2017/3/18.
 */
@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryNotificationReply.class
)
@Action(services = {"account"}, category = "account", names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryNotificationReply.class, inventoryClass = NotificationInventory.class)
public class APIQueryNotificationMsg extends APIQueryMessage {
    public static List<String> __example__() {
        return asList();
    }
}
