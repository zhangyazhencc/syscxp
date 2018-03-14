package com.syscxp.header.managementnode;

import org.springframework.http.HttpMethod;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;

import java.util.List;

import static java.util.Arrays.asList;

/**
 */
@AutoQuery(replyClass = APIQueryManagementNodeReply.class, inventoryClass = ManagementNodeInventory.class)
public class APIQueryManagementNodeMsg extends APIQueryMessage {

    public static List<String> __example__() {
        return asList("uuid=" + uuid());
    }
}
