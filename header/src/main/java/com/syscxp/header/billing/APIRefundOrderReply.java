package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

public class APIRefundOrderReply extends APIReply{

    private boolean success;

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
