package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.APIGetModifyProductPriceDiffReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/8
 */
@RestResponse(fieldsTo = {"all"})
public class APIGetModifyTunnelPriceDiffReply extends APIGetModifyProductPriceDiffReply {
    public APIGetModifyTunnelPriceDiffReply() {
    }

    public APIGetModifyTunnelPriceDiffReply(APIGetModifyProductPriceDiffReply reply) {
        super(reply);
    }
}
