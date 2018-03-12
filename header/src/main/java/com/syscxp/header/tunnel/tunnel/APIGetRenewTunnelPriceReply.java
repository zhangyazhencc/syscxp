package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.APIGetRenewProductPriceReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/12/11
 */
@RestResponse(fieldsTo = {"all"})
public class APIGetRenewTunnelPriceReply extends APIGetRenewProductPriceReply {
    public APIGetRenewTunnelPriceReply(){}

    public APIGetRenewTunnelPriceReply(APIGetRenewProductPriceReply reply) {
        super(reply);
    }
}
