package com.syscxp.vpn.header.vpn;

import com.syscxp.header.identity.Action;
import com.syscxp.header.search.APIGetMessage;
import com.syscxp.vpn.vpn.VpnConstant;

@Action(category = VpnConstant.ACTION_CATEGORY_VPN, names = {"read"})
public class APIGetVpnMsg extends APIGetMessage{

}
