package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.APIGetUnscribeProductPriceDiffReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/20
 */

@RestResponse(fieldsTo = {"all"})
public class APIGetUnscribeInterfacePriceDiffReply extends APIGetUnscribeProductPriceDiffReply {
    public APIGetUnscribeInterfacePriceDiffReply(){}

    public APIGetUnscribeInterfacePriceDiffReply(APIGetUnscribeProductPriceDiffReply reply) {
        super(reply);
    }
}
