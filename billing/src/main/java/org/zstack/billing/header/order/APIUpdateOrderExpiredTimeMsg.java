package org.zstack.billing.header.order;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import javax.persistence.PreUpdate;
import java.sql.Timestamp;

public class APIUpdateOrderExpiredTimeMsg extends APIMessage {

    @APIParam
    private String productUuid;

    @APIParam
    private Timestamp startTime;

    @APIParam
    private Timestamp endTime;

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }
}
