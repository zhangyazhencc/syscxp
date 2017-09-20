package org.zstack.billing.header.sla;

import org.zstack.header.billing.ProductType;
import org.zstack.header.billing.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.sql.Timestamp;
@Action(category = BillingConstant.ACTION_CATEGORY_SLA, names = {"create"})
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
    private SLAReason reason;

    @APIParam(emptyString = false)
    private int duration;

    @APIParam(emptyString = false)
    private String comment;




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

    public SLAReason getReason() {
        return reason;
    }

    public void setReason(SLAReason reason) {
        this.reason = reason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
