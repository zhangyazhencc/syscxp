package com.syscxp.header.tunnel.aliEdgeRouter;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIListAliRegionMsg extends APISyncCallMessage {
}
