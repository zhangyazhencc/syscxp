package com.syscxp.portal.managementnode;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 * Created by xing5 on 2016/7/22.
 */
@GlobalPropertyDefinition
public class PortalGlobalProperty {
    @GlobalProperty(name = "ManagementNode.maxHeartbeatFailure", defaultValue = "5")
    public static int MAX_HEARTBEAT_FAILURE;
}
