package com.syscxp.header.core.progress;

import com.syscxp.header.message.APISyncCallMessage;

/**
 * Created by xing5 on 2017/3/21.
 */

public class APIGetTaskProgressMsg extends APISyncCallMessage {
    private String apiId;
    private boolean all;

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public static APIGetTaskProgressMsg __example__() {
        APIGetTaskProgressMsg msg = new APIGetTaskProgressMsg();
        msg.setApiId(uuid());
        return msg;
    }
}
