package com.syscxp.billing.header.receipt;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;

public class APIGetValuebleReceiptReply extends APIReply{
    private BigDecimal valuebleReceipt;  //可开票金额
    private BigDecimal consumeCash;     //总消费金额
    private BigDecimal hadReceiptCash;  //已开票金额
    private BigDecimal hadConsumeCreditPoint; // 欠费（已用信用额度）

    public BigDecimal getConsumeCash() {
        return consumeCash;
    }

    public void setConsumeCash(BigDecimal consumeCash) {
        this.consumeCash = consumeCash;
    }

    public BigDecimal getHadReceiptCash() {
        return hadReceiptCash;
    }

    public void setHadReceiptCash(BigDecimal hadReceiptCash) {
        this.hadReceiptCash = hadReceiptCash;
    }

    public BigDecimal getHadConsumeCreditPoint() {
        return hadConsumeCreditPoint;
    }

    public void setHadConsumeCreditPoint(BigDecimal hadConsumeCreditPoint) {
        this.hadConsumeCreditPoint = hadConsumeCreditPoint;
    }

    public BigDecimal getValuebleReceipt() {
        return valuebleReceipt;
    }

    public void setValuebleReceipt(BigDecimal valuebleReceipt) {
        this.valuebleReceipt = valuebleReceipt;
    }
}
