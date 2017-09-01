package org.zstack.header.alipay;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.math.BigDecimal;

public class APIRechargeMsg extends APISyncCallMessage {

    @APIParam(numberRange = {0,Long.MAX_VALUE})
    private BigDecimal total;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
