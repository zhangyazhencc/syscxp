package org.zstack.billing.header.receipt;

import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;
@Action(category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"create"})
public class APICreateReceiptPostAddressMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String name;

    @APIParam(emptyString = false)
    private String telephone;

    @APIParam(emptyString = false)
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateReceiptPostAddressEvent) evt).getInventory().getUuid();
                }
                ntfy("Create ReceiptPostAddressVO")
                        .resource(uuid, ReceiptPostAddressVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
