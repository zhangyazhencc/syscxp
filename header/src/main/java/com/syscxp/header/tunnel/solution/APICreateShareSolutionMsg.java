package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.tunnel.TunnelConstant;

import java.util.List;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateShareSolutionMsg extends APIMessage {
    @APIParam(maxLength = 32)
    private List<String> accountUuids;
    @APIParam(maxLength = 32, resourceType = SolutionVO.class, checkAccount = true)
    private String solutionUuid;

    public List<String> getAccountUuids() {
        return accountUuids;
    }

    public void setAccountUuids(List<String> accountUuids) {
        this.accountUuids = accountUuids;
    }

    public String getSolutionUuid() {
        return solutionUuid;
    }

    public void setSolutionUuid(String solutionUuid) {
        this.solutionUuid = solutionUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
            ntfy("Share SolutionVO")
                    .resource(solutionUuid, SolutionVO.class)
                    .messageAndEvent(that, evt).done();
            }
        };
    }
}
