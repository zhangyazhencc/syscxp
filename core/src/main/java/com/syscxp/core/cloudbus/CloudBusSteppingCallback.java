package com.syscxp.core.cloudbus;

import com.syscxp.header.core.AbstractCompletion;
import com.syscxp.header.core.AsyncBackup;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.message.NeedReplyMessage;

/**
 */
public abstract class CloudBusSteppingCallback extends AbstractCompletion {
    public CloudBusSteppingCallback(AsyncBackup one, AsyncBackup... others) {
        super(one, others);
    }

    public abstract void run(NeedReplyMessage msg, MessageReply reply);
}
