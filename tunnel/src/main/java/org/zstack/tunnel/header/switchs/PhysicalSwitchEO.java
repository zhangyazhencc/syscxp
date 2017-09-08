package org.zstack.tunnel.header.switchs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DCY on 2017-09-06
 */
@Entity
@Table
public class PhysicalSwitchEO extends PhysicalSwitchAO {
    @Column
    private Integer deleted;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
