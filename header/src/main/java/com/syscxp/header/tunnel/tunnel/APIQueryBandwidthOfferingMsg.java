package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Create by DCY on 2017/10/30
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryBandwidthOfferingReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryBandwidthOfferingReply.class, inventoryClass = BandwidthOfferingVO.class)
public class APIQueryBandwidthOfferingMsg extends APIQueryMessage {
}
