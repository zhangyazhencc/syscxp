package com.syscxp.header.message;

import com.syscxp.header.rest.APINoSee;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class NeedReplyMessage extends Message {
    /**
     * @desc in millisecond. Any reply/event received after timeout will be dropped
     * @optional
     */
    @APINoSee
    protected long timeout = -1;

    public NeedReplyMessage() {
        super();
    }

    public NeedReplyMessage(long timeout) {
        super();
    }

    public String toErrorString() {
        return String.format("Message[name: %s, id: %s] timeout after %s seconds",
                this.getClass().getName(),
                this.getId(),
                TimeUnit.MILLISECONDS.toSeconds(getTimeout()));
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
