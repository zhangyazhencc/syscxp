package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.APIGetUnscribeProductPriceDiffReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/20
 */
@RestResponse(superclassFieldsTo = {"all"})
public class APIGetUnscribeTunnelPriceDiffReply extends APIGetUnscribeProductPriceDiffReply {
    public APIGetUnscribeTunnelPriceDiffReply(){}

    public APIGetUnscribeTunnelPriceDiffReply(APIGetUnscribeProductPriceDiffReply reply) {
        super(reply);
    }
}
