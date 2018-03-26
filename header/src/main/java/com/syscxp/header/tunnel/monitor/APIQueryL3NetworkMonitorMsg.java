package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.identity.Action;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: 3层网络监控查询.
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryL3NetworkMonitorReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = MonitorConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQuerySpeedRecordsReply.class, inventoryClass = L3NetworkMonitorInventory.class)
public class APIQueryL3NetworkMonitorMsg extends APIQueryMessage {
}
