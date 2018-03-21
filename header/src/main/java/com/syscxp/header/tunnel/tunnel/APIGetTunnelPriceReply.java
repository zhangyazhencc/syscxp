package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.APIGetProductPriceReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/1
 */
@RestResponse(superclassFieldsTo = {"all"})
public class APIGetTunnelPriceReply extends APIGetProductPriceReply {
    public APIGetTunnelPriceReply() {
    }

    public APIGetTunnelPriceReply(APIGetProductPriceReply reply) {
        super(reply);
    }
}
