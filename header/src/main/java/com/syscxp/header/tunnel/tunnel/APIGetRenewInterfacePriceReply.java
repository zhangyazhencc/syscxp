package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.APIGetRenewProductPriceReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/20
 */
@RestResponse(superclassFieldsTo = {"all"})
public class APIGetRenewInterfacePriceReply extends APIGetRenewProductPriceReply {
    public APIGetRenewInterfacePriceReply(){}

    public APIGetRenewInterfacePriceReply(APIGetRenewProductPriceReply reply) {
        super(reply);
    }
}
