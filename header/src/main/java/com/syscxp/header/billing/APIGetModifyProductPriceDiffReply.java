package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class APIGetModifyProductPriceDiffReply extends APIReply {

    private BigDecimal remainMoney ;//产品还剩的金额
    private BigDecimal needPayMoney;//打折后的
    private BigDecimal needPayOriginMoney ;//原价
    private BigDecimal subMoney ;

    public BigDecimal getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(BigDecimal remainMoney) {
        this.remainMoney = remainMoney;
    }

    public BigDecimal getNeedPayMoney() {
        return needPayMoney;
    }

    public void setNeedPayMoney(BigDecimal needPayMoney) {
        this.needPayMoney = needPayMoney;
    }

    public BigDecimal getNeedPayOriginMoney() {
        return needPayOriginMoney;
    }

    public void setNeedPayOriginMoney(BigDecimal needPayOriginMoney) {
        this.needPayOriginMoney = needPayOriginMoney;
    }

    public BigDecimal getSubMoney() {
        return subMoney;
    }

    public void setSubMoney(BigDecimal subMoney) {
        this.subMoney = subMoney;
    }

    public APIGetModifyProductPriceDiffReply() {
    }
    public APIGetModifyProductPriceDiffReply(APIGetModifyProductPriceDiffReply reply) {
        this.setRemainMoney(reply.getRemainMoney());
        this.setNeedPayMoney(reply.getNeedPayMoney());
        this.setNeedPayOriginMoney(reply.getNeedPayOriginMoney());
        this.setSubMoney(reply.getSubMoney());
    }
}
