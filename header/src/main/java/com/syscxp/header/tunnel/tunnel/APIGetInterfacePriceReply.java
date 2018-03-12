package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.APIGetProductPriceReply;
import com.syscxp.header.rest.RestResponse;

/**
 * Create by DCY on 2017/11/1
 */
@RestResponse(fieldsTo = {"all"})
public class APIGetInterfacePriceReply extends APIGetProductPriceReply {
    public APIGetInterfacePriceReply() {
    }

    public APIGetInterfacePriceReply(APIGetProductPriceReply reply) {
        super(reply);
    }
}
