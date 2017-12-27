package com.syscxp.header.tunnel.solution;

import com.syscxp.header.query.APIQueryReply;

import java.math.BigDecimal;

public class APIGetVPNPriceReply extends APIQueryReply {
    private BigDecimal price;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
