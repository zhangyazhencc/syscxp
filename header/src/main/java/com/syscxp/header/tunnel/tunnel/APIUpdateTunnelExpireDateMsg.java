package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;

/**
 * Create by DCY on 2017/10/10
 */
@InnerCredentialCheck
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateTunnelExpireDateMsg extends APIUpdateExpireDateMsg {
}
