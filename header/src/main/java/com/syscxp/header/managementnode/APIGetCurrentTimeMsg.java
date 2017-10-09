package com.syscxp.header.managementnode;

import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Created by Mei Lei <meilei007@gmail.com> on 11/1/16.
 */
@RestRequest(
        path = "/management-nodes/actions",
        isAction = true,
        method = HttpMethod.PUT,
        responseClass = APIGetCurrentTimeReply.class
)
public class APIGetCurrentTimeMsg extends APISyncCallMessage {
 
    public static APIGetCurrentTimeMsg __example__() {
        APIGetCurrentTimeMsg msg = new APIGetCurrentTimeMsg();


        return msg;
    }

}
