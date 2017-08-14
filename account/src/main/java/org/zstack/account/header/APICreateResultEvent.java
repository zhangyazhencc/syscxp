package org.zstack.account.header;


import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by wangwg on 2017/8/14.
 */
public class APICreateResultEvent<T> extends APIEvent {

    private T Object;

    public APICreateResultEvent(String apiId) {
        super(apiId);
    }

    public APICreateResultEvent() {
        super(null);
    }

    public T getObject() {
        return Object;
    }

    public void setObject(T object) {
        Object = object;
    }
}
