package org.zstack.header.managementnode;

import org.springframework.http.HttpMethod;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.header.rest.RestRequest;

/**
 * Created by frank on 11/14/2015.
 */
public class APIGetVersionMsg extends APISyncCallMessage {
 
    public static APIGetVersionMsg __example__() {
        APIGetVersionMsg msg = new APIGetVersionMsg();


        return msg;
    }

}
