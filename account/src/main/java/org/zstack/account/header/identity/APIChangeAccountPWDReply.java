package org.zstack.account.header.identity;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.APIReply;

/**
 *
 * Created by wangwg on 2017/8/5.
 */
public class APIChangeuserPWDReply extends APIReply {
    private boolean success;

    private String message;

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
}
