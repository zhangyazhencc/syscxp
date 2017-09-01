package org.zstack.billing.header.receipt;

import org.zstack.header.message.APIReply;

import java.math.BigDecimal;

public class APIGetValuebleReceiptReply extends APIReply{
    private BigDecimal valuebleReceipt;
    private BigDecimal consumeCash;
    private BigDecimal hadReceiptCash;
    private BigDecimal hadConsumeCreditPoint;

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
