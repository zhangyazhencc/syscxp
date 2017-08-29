package org.zstack.billing.header.identity.receipt;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.math.BigDecimal;

public class APICreateReceiptMsg extends APIMessage {

    @APIParam
    private BigDecimal total;

    @APIParam(resourceType = ReceiptPostAddressVO.class, checkAccount = true)
    private String receiptAddressUuid;

    @APIParam(resourceType = ReceiptInfoVO.class, checkAccount = true)
    private String receiptInfoUuid;

    public String getReceiptAddressUuid() {
        return receiptAddressUuid;
    }

    public void setReceiptAddressUuid(String receiptAddressUuid) {
        this.receiptAddressUuid = receiptAddressUuid;
    }

    public String getReceiptInfoUuid() {
        return receiptInfoUuid;
    }

    public void setReceiptInfoUuid(String receiptInfoUuid) {
        this.receiptInfoUuid = receiptInfoUuid;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
