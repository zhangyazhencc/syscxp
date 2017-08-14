package org.zstack.account.header;


import org.zstack.header.message.APIReply;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */
public class APIListResultReply<T> extends APIReply {

    private boolean success;

    private String message;

    private List<T> list;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getList() {
        return list;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
