package com.syscxp.core.config;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

import java.util.List;

import static java.util.Arrays.asList;

/**
 */
@AutoQuery(replyClass = APIQueryGlobalConfigReply.class, inventoryClass = GlobalConfigInventory.class)
@Action(category = "configuration", names = {"read"})
public class APIQueryGlobalConfigMsg extends APIQueryMessage {
    public static List<String> __example__() {
        return asList();
    }

}
