package com.syscxp.billing.header.receipt;

import com.syscxp.billing.header.balance.APICreateAccountDiscountEvent;
import com.syscxp.header.billing.AccountDiscountVO;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.ProductPriceUnitVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.math.BigDecimal;
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RECEIPT, names = {"create"})
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


    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateReceiptEvent) evt).getInventory().getUuid();
                }
                ntfy("Create ReceiptVO")
                        .resource(uuid, ReceiptVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
