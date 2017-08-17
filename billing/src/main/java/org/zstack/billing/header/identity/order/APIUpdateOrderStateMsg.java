package org.zstack.billing.header.identity.order;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIUpdateOrderStateMsg extends APIMessage{


    @APIParam(nonempty = true, resourceType = OrderVO.class, checkAccount = true)
    private String uuid;

    @APIParam(nonempty = true,validValues = {"CANCELED","PAID"})
    private OrderState state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }
}
