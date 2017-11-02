package com.syscxp.tunnel.header.node;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.tunnel.manage.NodeConstant;

@Action(category = NodeConstant.ACTION_CATEGORY, names = {"read"})
public class APIListCountryNodeMsg extends APISyncCallMessage{

}

