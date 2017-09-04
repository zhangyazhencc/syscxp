package org.zstack.billing.header.sla;

import org.zstack.billing.header.balance.ProductType;
import org.zstack.billing.manage.BillingConstant;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.sql.Timestamp;

@Action(category = BillingConstant.ACTION_CATEGORY, names = {"sla"})
public class APIUpdateSLACompensateMsg extends APIMessage {
    @APIParam(emptyString =false)
    private String uuid;

    @APIParam(emptyString =false)
    private String accountUuid;

    @APIParam(emptyString =false)
    private String productUuid;

    @APIParam(emptyString =false)
    private ProductType productType;

    @APIParam(emptyString =false,required = false)
    private String productName;

    @APIParam(emptyString =false)
    private String reason;

    @APIParam(emptyString =false,required = false)
    private String description;

    @APIParam()
    private Integer duration;

    @APIParam()
    private Timestamp timeStart;

    @APIParam()
    private Timestamp timeEnd;

    @APIParam(required = false)
    private SLAState state;

    public SLAState getState() {
        return state;
    }

    public void setState(SLAState state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }
}
