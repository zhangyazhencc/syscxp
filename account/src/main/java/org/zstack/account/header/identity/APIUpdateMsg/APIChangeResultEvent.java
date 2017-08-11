package org.zstack.account.header.identity.APIUpdateMsg;

import org.zstack.header.message.APIReply;
import org.zstack.header.message.APIEvent;

/**
 *
 * Created by wangwg on 2017/8/8.
 */
public class APIChangeResultEvent extends APIEvent  {
    private boolean success;

    private String message;

    private Object object;

    public APIChangeResultEvent(String apiId) {
        super(apiId);
    }

    public APIChangeResultEvent() {
        super(null);
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
