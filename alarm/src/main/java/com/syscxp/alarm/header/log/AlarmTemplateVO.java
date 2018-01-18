package com.syscxp.alarm.header.log;

import com.syscxp.alarm.header.BaseVO;
import com.syscxp.header.billing.ProductType;

import javax.persistence.*;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-17.
 * @Description: 告警模板.
 */
@Entity
@Table
public class AlarmTemplateVO extends BaseVO {
    @Column
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column
    private String monitorTargetUuid;

    @Column
    private String template;

    @Column
    @Enumerated(EnumType.STRING)
    private AlarmStatus status;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getMonitorTargetUuid() {
        return monitorTargetUuid;
    }

    public void setMonitorTargetUuid(String monitorTargetUuid) {
        this.monitorTargetUuid = monitorTargetUuid;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public AlarmStatus getStatus() {
        return status;
    }

    public void setStatus(AlarmStatus status) {
        this.status = status;
    }
}
