package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.alarm.header.contact.APICreateContactEvent;
import com.syscxp.alarm.header.contact.ContactVO;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.util.List;

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"update"})
public class APIAttachPolicyToResourceMsg extends APIMessage {

    @APIParam(required = false,resourceType = PolicyVO.class,checkAccount = true)
    private String policyUuid;

    @APIParam
    private String resourceUuid;

    @APIParam
    private ProductType type;

    @APIParam
    private String accountUuid;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = resourceUuid;
                }

                ntfy("attach policy to resource")
                        .resource(uuid, ResourcePolicyRefVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
