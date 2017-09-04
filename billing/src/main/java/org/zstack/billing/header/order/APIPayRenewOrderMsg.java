package org.zstack.billing.header.order;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Deprecated
public class APIPayRenewOrderMsg extends APIMessage{


    @APIParam(nonempty = true, resourceType = OrderVO.class, checkAccount = true)
    private String orderUuid;

    @APIParam(nonempty = true,validValues = {"OK","CANCEL"})
    private String  confirm;

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
