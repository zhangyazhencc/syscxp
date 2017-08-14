package org.zstack.account.header;

import org.zstack.account.header.identity.UserInventory;
import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APICreateUserEvent<T> extends APIEvent {
    private T Object;

    public APICreateUserEvent(String apiId) {
        super(apiId);
    }

    public APICreateUserEvent() {
        super(null);
    }

    public T getObject() {
        return Object;
    }

    public void setObject(T object) {
        Object = object;
    }
}
