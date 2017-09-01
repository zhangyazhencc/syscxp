package org.zstack.billing.header.sla;

import org.zstack.billing.header.balance.ProductType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.sql.Timestamp;

public class APIUpdateSLACompensateMsg extends APIMessage {
    @APIParam(nonempty = true)
    private String uuid;

    @APIParam(nonempty = true)
    private String accountUuid;

    @APIParam(nonempty = true)
    private String productUuid;

    @APIParam(nonempty = true)
    private ProductType productType;

    @APIParam(nonempty = true,required = false)
    private String productName;

    @APIParam(nonempty = true)
    private String reason;

    @APIParam(nonempty = true,required = false)
    private String description;

    @APIParam(nonempty = true)
    private Integer duration;

    @APIParam(nonempty = true)
    private Timestamp timeStart;

    @APIParam(nonempty = true)
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
