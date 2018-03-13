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

@Action(services = {AlarmConstant.ACTION_SERVICE}, category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"create"})
public class APICreatePolicyMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String name;

    private String description;

    @APIParam
    private ProductType productType;

    @APIParam
    private String  accountUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
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
                    uuid = ((APICreatePolicyEvent) evt).getInventory().getUuid();
                }

                ntfy("Create Alarm PolicyVO")
                        .resource(uuid, PolicyVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
