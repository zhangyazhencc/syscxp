package com.syscxp.core.job;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;

/**
 * Create by DCY on 2017/12/13
 */

@Action(services = {"job"}, category = "job", names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryJobQueueEntryReply.class, inventoryClass = JobQueueEntryInventory.class)
public class APIQueryJobQueueEntryMsg  extends APIQueryMessage {
}
