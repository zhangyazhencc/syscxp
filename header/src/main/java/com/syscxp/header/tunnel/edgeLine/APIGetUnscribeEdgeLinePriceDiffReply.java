package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.billing.APIGetUnscribeProductPriceDiffReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2018/1/12
 */

@RestResponse(superclassFieldsTo = {"all"})
public class APIGetUnscribeEdgeLinePriceDiffReply extends APIGetUnscribeProductPriceDiffReply {

    public APIGetUnscribeEdgeLinePriceDiffReply(){}

    public APIGetUnscribeEdgeLinePriceDiffReply(APIGetUnscribeProductPriceDiffReply reply) {
        super(reply);
    }
}
