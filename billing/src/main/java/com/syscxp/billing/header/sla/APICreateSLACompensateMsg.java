package com.syscxp.billing.header.sla;

import com.syscxp.billing.header.receipt.APICreateReceiptEvent;
import com.syscxp.billing.header.receipt.ReceiptVO;
import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_SLA, names = {"create"}, adminOnly = true)
public class APICreateSLACompensateMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false)
    private String productUuid;

    @APIParam(emptyString = false)
    private ProductType productType;

    @APIParam(emptyString = false,required = false)
    private String productName;

    @APIParam(emptyString = false)
    private String reason;

    @APIParam(emptyString = false)
    private int duration;

    @APIParam(emptyString = false,required = false)
    private String comment;

    @APIParam(emptyString = false,required = false)
    private String description;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateSLACompensateEvent) evt).getInventory().getUuid();
                }
                ntfy("Create SLACompensateVO")
                        .resource(uuid, SLACompensateVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
