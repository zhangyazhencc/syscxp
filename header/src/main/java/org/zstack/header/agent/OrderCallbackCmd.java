package org.zstack.header.agent;

import org.zstack.header.billing.OrderType;
import org.zstack.header.billing.OrderVO;

import java.sql.Timestamp;

public class OrderCallbackCmd {
    private String porductUuid;
    private OrderType type;
    private Timestamp expireDate;

    public static OrderCallbackCmd valueOf(OrderVO order){
        OrderCallbackCmd cmd = new OrderCallbackCmd();
        cmd.setPorductUuid(order.getProductUuid());
        cmd.setType(order.getType());
        cmd.setExpireDate(order.getProductEffectTimeEnd());
        return cmd;
    }

    public String getPorductUuid() {
        return porductUuid;
    }

    public void setPorductUuid(String porductUuid) {
        this.porductUuid = porductUuid;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }
}
