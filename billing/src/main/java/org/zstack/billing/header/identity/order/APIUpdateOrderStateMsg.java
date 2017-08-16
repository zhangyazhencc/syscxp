package org.zstack.billing.header.identity.order;

import org.apache.logging.log4j.core.config.Order;
import org.zstack.billing.header.identity.OrderState;
import org.zstack.billing.header.identity.OrderVO;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APIUpdateOrderStateMsg extends APIMessage{


    @APIParam(nonempty = true, resourceType = OrderVO.class, checkAccount = true)
    private String uuid;

    @APIParam(nonempty = true)
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
