package org.zstack.account.header.identity.updatemsg;

import org.zstack.header.message.APIReply;

/**
 *
 * Created by wangwg on 2017/8/8.
 */
public class APIChangeResultReply extends APIReply {
    private boolean success;

    private String message;

    private Object object;

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
