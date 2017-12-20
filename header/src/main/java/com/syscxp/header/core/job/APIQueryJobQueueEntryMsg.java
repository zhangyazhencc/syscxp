package com.syscxp.header.core.job;

import com.syscxp.core.job.JobQueueEntryInventory;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/12/13
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryJobQueueEntryReply.class, inventoryClass = JobQueueEntryInventory.class)
public class APIQueryJobQueueEntryMsg  extends APIQueryMessage {
}
